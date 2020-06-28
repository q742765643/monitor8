package com.piesat.skywalking.om.protocol.snmp;

import com.piesat.common.utils.IdUtils;
import lombok.SneakyThrows;
import org.apache.skywalking.oap.server.core.storage.type.StorageDataComplexObject;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class SNMPTestService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    private ArrayList<String> sysCpusPre=new ArrayList<>();
    private Map<Long,Long> lastCputime=new HashMap<>();
    private Map<Long,BigDecimal> lastCpuPct=new HashMap<>();

    private Date date;
    @SneakyThrows
    public List<Map<String,Object>> getSystemInfo(String hostComputer, String port, String version){
        date=new Date();
        List<Map<String,Object>> mapList=new ArrayList<>();
        SNMPSessionUtil snmpSessionUtil=new SNMPSessionUtil(hostComputer,port,"public", version);

        Map<String,Object> sourceCpu=this.metricbeatMap(hostComputer,port,"cpu",snmpSessionUtil);
        long totalCpuTime=this.cpuMap(snmpSessionUtil,sourceCpu);
        Map<String,Object> sourceProcess=this.metricbeatMap(hostComputer,port,"process",snmpSessionUtil);
        this.processMap(snmpSessionUtil,sourceProcess,totalCpuTime);
        elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.19-000001",IdUtils.fastUUID(),sourceCpu);

        Map<String,Object> sourceMemory=this.metricbeatMap(hostComputer,port,"memory",snmpSessionUtil);
        this.memoryMap(snmpSessionUtil,sourceMemory);
        elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.19-000001",IdUtils.fastUUID(),sourceMemory);

        Map<String,Object> sourceDisk=this.metricbeatMap(hostComputer,port,"filesystem",snmpSessionUtil);
        this.diskMap(hostComputer,port,snmpSessionUtil,sourceDisk);

        Map<String,Object> sourceNetwork=this.metricbeatMap(hostComputer,port,"network",snmpSessionUtil);
        this.networkMap(snmpSessionUtil,sourceNetwork);

        Map<String,Object> sourceLoad=this.metricbeatMap(hostComputer,port,"load",snmpSessionUtil);
        this.loadMap(snmpSessionUtil,sourceLoad);
        elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.19-000001",IdUtils.fastUUID(),sourceLoad);

        Map<String,Object> sourceDiskio=this.metricbeatMap(hostComputer,port,"diskio",snmpSessionUtil);
        this.diskioMap(snmpSessionUtil,sourceDiskio);
        return mapList;
    }
    public Map<String,Object> metricbeatMap(String hostComputer,String version,String type,SNMPSessionUtil snmpSessionUtil) throws Exception {
        Map<String,Object> source=new HashMap<>();

        String[] sysDesc = {SNMPConstants.SYSDESC};
        ArrayList<String> sysDescs=snmpSessionUtil.getSnmpGet(PDU.GET,sysDesc);

        String[] sysUptime = {SNMPConstants.SYSUPTIME};
        ArrayList<String> sysUptimes=snmpSessionUtil.getSnmpGet(PDU.GET,sysUptime);

        String[] sysName = {SNMPConstants.SYSNAME};
        ArrayList<String> sysNames=snmpSessionUtil.getSnmpGet(PDU.GET,sysName);
        source.put("@timestamp",date);

        Map<String,Object> agent=new HashMap<>();
        agent.put("ephemeral_id","5bc43524-b053-11ea-80ae-"+sysNames.get(0));
        agent.put("hostname",sysNames.get(0));
        agent.put("id","5fedf84c-b053-11ea-8288-"+sysNames.get(0));
        agent.put("name",hostComputer);
        agent.put("type","snmpbeat");
        agent.put("version",version);
        source.put("agent",agent);

        Map<String,Object> cloud=new HashMap<>();
        cloud.put("availability_zone","");
        Map<String,Object> instance=new HashMap<>();
        instance.put("id","");
        instance.put("name","");
        cloud.put("instance",instance);
        Map<String,Object> machine=new HashMap<>();
        machine.put("type","");
        cloud.put("machine",machine);
        cloud.put("provider","");
        source.put("cloud",cloud);

        Map<String,Object> ecs=new HashMap<>();
        ecs.put("version","1.5.0");
        source.put("ecs",ecs);

        Map<String,Object> event=new HashMap<>();
        event.put("dataset","system."+type);
        event.put("duration","0.2");
        event.put("module","system");
        source.put("event",event);

        Map<String,Object> host=new HashMap<>();
        host.put("architecture","");
        host.put("containerized","");
        host.put("hostname",sysNames.get(0));
        host.put("id","");
        host.put("ip",hostComputer);
        host.put("mac","");
        host.put("name",hostComputer);
        Map<String,Object> os=new HashMap<>();
        os.put("codename","");
        os.put("family","");
        os.put("kernel","");
        os.put("name",sysDescs.get(0));
        os.put("platform","");
        os.put("version","");
        host.put("os",os);
        source.put("host",host);

        Map<String,Object> metricset=new HashMap<>();
        metricset.put("name",type);
        if(type.equals("filesystem")){
            metricset.put("period",60000l);
        }else{
            metricset.put("period",10000l);
        }
        source.put("metricset",metricset);

        Map<String,Object> service=new HashMap<>();
        service.put("type","system");
        source.put("service",service);
        return source;
    }

    @SneakyThrows
    public long cpuMap(SNMPSessionUtil snmpSessionUtil,Map<String,Object> source) {
        String[] sscpuNum = {SNMPConstants.SSCPUNUM};
        ArrayList<String> sscpuNums=snmpSessionUtil.snmpWalk2(sscpuNum);
        String[] sysCpu = {
                SNMPConstants.SSCPURAWIDLE,
                SNMPConstants.SSCPURAWWAIT,
                SNMPConstants.SSCPURAWNICE,
                SNMPConstants.SSCPURAWSOFTIRQ,
                SNMPConstants.SSCPURAWSTEAL,
                SNMPConstants.SSCPURAWSYSTEM,
                SNMPConstants.SSCPURAWUSER,
        };
        ArrayList<String> sysCpus=snmpSessionUtil.getSnmpGet(PDU.GET,sysCpu);
        if(sysCpusPre.size()==0){
            sysCpusPre=sysCpus;
            return 0;
        }
        long cores=sscpuNums.size();
        BigDecimal idlePre= new BigDecimal(sysCpusPre.get(0));
        BigDecimal idle= new BigDecimal(sysCpus.get(0));
        BigDecimal waitPre= new BigDecimal(sysCpusPre.get(1));
        BigDecimal wait= new BigDecimal(sysCpus.get(1));
        BigDecimal nicePre= new BigDecimal(sysCpusPre.get(2));
        BigDecimal nice= new BigDecimal(sysCpus.get(2));
        BigDecimal softPre= new BigDecimal(sysCpusPre.get(3));
        BigDecimal soft= new BigDecimal(sysCpus.get(3));
        BigDecimal stealPre= new BigDecimal(sysCpusPre.get(4));
        BigDecimal steal= new BigDecimal(sysCpus.get(4));
        BigDecimal systemPre= new BigDecimal(sysCpusPre.get(5));
        BigDecimal system= new BigDecimal(sysCpus.get(5));
        BigDecimal userPre= new BigDecimal(sysCpusPre.get(6));
        BigDecimal user= new BigDecimal(sysCpus.get(6));
        BigDecimal totalTime=idle.add(wait).add(nice).add(soft).add(steal).add(system).add(user)
                .subtract(idlePre).subtract(waitPre).subtract(nicePre).subtract(softPre)
                .subtract(stealPre).subtract(systemPre).subtract(userPre);
        float idlep=(idle.subtract(idlePre)).divide(totalTime,4, RoundingMode.HALF_UP).floatValue();
        float waitp=(wait.subtract(waitPre)).divide(totalTime,4, RoundingMode.HALF_UP).floatValue();
        float nicep=(nice.subtract(nicePre)).divide(totalTime,4, RoundingMode.HALF_UP).floatValue();
        float softp=(soft.subtract(softPre)).divide(totalTime,4, RoundingMode.HALF_UP).floatValue();
        float stealp=(steal.subtract(stealPre)).divide(totalTime,4, RoundingMode.HALF_UP).floatValue();
        float systemp=(system.subtract(systemPre)).divide(totalTime,4, RoundingMode.HALF_UP).floatValue();
        float userp=(user.subtract(userPre)).divide(totalTime,4, RoundingMode.HALF_UP).floatValue();
        float total=1-idlep;
        Map<String,Object> systemM=new HashMap<>();
        Map<String,Object> cpu=new HashMap<>();
        cpu.put("cores",cores);
        Map<String,Object> idleEs=new HashMap<>();
        Map<String,Object> idleNorm=new HashMap<>();
        idleNorm.put("pct",(idlep));
        idleEs.put("norm",idleNorm);
        idleEs.put("pct",idlep*cores);
        cpu.put("idle",idleEs);

        Map<String,Object> iowaitEs=new HashMap<>();
        Map<String,Object> iowaitNorm=new HashMap<>();
        iowaitNorm.put("pct",waitp);
        iowaitEs.put("norm",iowaitNorm);
        iowaitEs.put("pct",waitp*cores);
        cpu.put("iowait",iowaitEs);

        Map<String,Object> irqEs=new HashMap<>();
        Map<String,Object> irqNorm=new HashMap<>();
        irqNorm.put("pct",0f);
        irqEs.put("norm",irqNorm);
        irqEs.put("pct",0f);
        cpu.put("irq",irqEs);

        Map<String,Object> niceEs=new HashMap<>();
        Map<String,Object> niceNorm=new HashMap<>();
        niceNorm.put("pct",nicep);
        niceEs.put("norm",niceNorm);
        niceEs.put("pct",nicep*cores);
        cpu.put("nice",niceEs);

        Map<String,Object> softirqEs=new HashMap<>();
        Map<String,Object> softirqNorm=new HashMap<>();
        softirqNorm.put("pct",softp);
        softirqEs.put("norm",softirqNorm);
        softirqEs.put("pct",softp*cores);
        cpu.put("softirq",softirqEs);

        Map<String,Object> stealEs=new HashMap<>();
        Map<String,Object> stealNorm=new HashMap<>();
        stealNorm.put("pct",stealp);
        stealEs.put("norm",stealNorm);
        stealEs.put("pct",stealp*cores);
        cpu.put("steal",stealEs);

        Map<String,Object> systemEs=new HashMap<>();
        Map<String,Object> systemNorm=new HashMap<>();
        systemNorm.put("pct",systemp);
        systemEs.put("norm",systemNorm);
        systemEs.put("pct",systemp*cores);
        cpu.put("system",systemEs);


        Map<String,Object> userEs=new HashMap<>();
        Map<String,Object> userNorm=new HashMap<>();
        userNorm.put("pct",userp);
        userEs.put("norm",userNorm);
        userEs.put("pct",userp*cores);
        cpu.put("user",userEs);

        Map<String,Object> totalEs=new HashMap<>();
        Map<String,Object> totalNorm=new HashMap<>();
        totalNorm.put("pct",total);
        totalEs.put("norm",totalNorm);
        totalEs.put("pct",total*cores);
        cpu.put("total",totalEs);

        systemM.put("cpu",cpu);
        source.put("system",systemM);
        sysCpusPre=sysCpus;
        return totalTime.longValue();
    }

    @SneakyThrows
    public void memoryMap(SNMPSessionUtil snmpSessionUtil,Map<String,Object> source) {
        String[] sysMemory = {
                SNMPConstants.MEMTOTALSWAP,
                SNMPConstants.MEMAVAILSWAP,
                SNMPConstants.MEMTOTALREAL,
                SNMPConstants.MEMAVAILREAL,
                SNMPConstants.MEMTOTALFREE,
                SNMPConstants.HRMEMORYSIZE,
                SNMPConstants.MEMSHARED,
                SNMPConstants.MEMBUFFER,
                SNMPConstants.MEMCACHED
        };
        ArrayList<String> sysMemorys = snmpSessionUtil.getSnmpGet(PDU.GET,sysMemory);
        BigDecimal memTotalSwap = new BigDecimal(sysMemorys.get(0)).multiply(new BigDecimal(1024));
        BigDecimal memAvailSwap = new BigDecimal(sysMemorys.get(1)).multiply(new BigDecimal(1024));
        BigDecimal memTotalReal = new BigDecimal(sysMemorys.get(2)).multiply(new BigDecimal(1024));
        BigDecimal memAvailReal = new BigDecimal(sysMemorys.get(3)).multiply(new BigDecimal(1024));
       // BigDecimal memTotalFree = new BigDecimal(sysMemorys.get(4)).multiply(new BigDecimal(1024));
        //BigDecimal hrMemorySize = new BigDecimal(sysMemorys.get(5)).multiply(new BigDecimal(1024));
        BigDecimal memShared = new BigDecimal(sysMemorys.get(6)).multiply(new BigDecimal(1024));
        BigDecimal memBuffer = new BigDecimal(sysMemorys.get(7)).multiply(new BigDecimal(1024));
        BigDecimal memCached = new BigDecimal(sysMemorys.get(8)).multiply(new BigDecimal(1024));
        BigDecimal actualFree = memAvailReal.add(memBuffer).add(memCached);
        BigDecimal actualUsedBytes = memTotalReal.subtract(actualFree);
        BigDecimal actualUsedPct = actualUsedBytes.divide(memTotalReal,4,RoundingMode.HALF_UP);
        Map<String,Object> system=new HashMap<>();
        Map<String,Object> memory=new HashMap<>();
        Map<String,Object> actual=new HashMap<>();
        actual.put("free",actualFree.longValue());
        Map<String,Object> actualUsed=new HashMap<>();
        actualUsed.put("bytes",actualUsedBytes.longValue());
        actualUsed.put("pct",actualUsedPct.floatValue());
        actual.put("used",actualUsed);
        memory.put("actual",actual);
        memory.put("free",memAvailReal.longValue());

        Map<String,Object> hugepages=new HashMap<>();
        hugepages.put("default_size",0l);
        hugepages.put("free",0l);
        hugepages.put("reserved",0l);
        hugepages.put("surplus",0l);
        Map<String,Object> hugepagesSwap=new HashMap<>();
        Map<String,Object> hugepagesSwapOut=new HashMap<>();
        hugepagesSwapOut.put("fallback",0l);
        hugepagesSwapOut.put("pages",0l);
        hugepagesSwap.put("out",hugepagesSwapOut);
        hugepages.put("swap",hugepagesSwap);
        hugepages.put("total",0l);
        Map<String,Object> hugepagesUsed=new HashMap<>();
        hugepagesUsed.put("bytes",0l);
        hugepagesUsed.put("pct",0l);
        hugepages.put("used",hugepagesUsed);
        memory.put("hugepages",hugepages);

        Map<String,Object> pageStats=new HashMap<>();
        Map<String,Object> pgfree=new HashMap<>();
        pgfree.put("pages",0l);
        pageStats.put("pgfree",pgfree);
        Map<String,Object> pgscanDirect=new HashMap<>();
        pgscanDirect.put("pages",0l);
        pageStats.put("pgscan_direct",pgscanDirect);
        Map<String,Object> pgscanKswapd=new HashMap<>();
        pgscanKswapd.put("pages",0l);
        pageStats.put("pgscan_kswapd",pgscanKswapd);
        Map<String,Object> pgstealDirect=new HashMap<>();
        pgstealDirect.put("pages",0l);
        pageStats.put("pgsteal_direct",pgstealDirect);
        Map<String,Object> pgstealKswapd=new HashMap<>();
        pgstealKswapd.put("pages",0l);
        pageStats.put("pgsteal_kswapd",pgstealKswapd);
        memory.put("page_stats",pageStats);

        BigDecimal swapUsedBytes=memTotalSwap.subtract(memAvailSwap);
        BigDecimal swapUsedPct=new BigDecimal(0);
        if(memTotalSwap.longValue()>0){
            swapUsedPct=swapUsedBytes.divide(memTotalSwap,4,RoundingMode.HALF_UP);
        }
        Map<String,Object> swap=new HashMap<>();
        swap.put("free",memAvailSwap.longValue());
        Map<String,Object> swapIn=new HashMap<>();
        swapIn.put("pages",0l);
        swap.put("in",swapIn);
        Map<String,Object> swapOut=new HashMap<>();
        swapOut.put("pages",0l);
        swap.put("out",swapOut);
        Map<String,Object> readahead=new HashMap<>();
        readahead.put("cached",0l);
        readahead.put("pages",0l);
        swap.put("readahead",readahead);
        swap.put("total",memTotalSwap.longValue());
        Map<String,Object> swapUsed=new HashMap<>();
        swapUsed.put("bytes",swapUsedBytes.longValue());
        swapUsed.put("pct",swapUsedPct.floatValue());
        swap.put("used",swapUsed);
        memory.put("swap",swap);
        memory.put("total",memTotalReal.longValue());
        Map<String,Object> totalUsed=new HashMap<>();
        totalUsed.put("bytes",memTotalReal.subtract(memAvailReal).longValue());
        totalUsed.put("pct",(memTotalReal.subtract(memAvailReal)).divide(memTotalReal,4,RoundingMode.HALF_UP).floatValue());
        memory.put("used",totalUsed);
        system.put("memory",memory);
        source.put("system",system);

    }

    @SneakyThrows
    public void diskMap(String hostComputer,String port,SNMPSessionUtil snmpSessionUtil,Map<String,Object> source) {
        String[] sysDisk = {"1.3.6.1.2.1.25.2.3.1.2",  //type 存储单元类型
                "1.3.6.1.2.1.25.2.3.1.3",  //descr
                "1.3.6.1.2.1.25.2.3.1.4",  //unit 存储单元大小
                "1.3.6.1.2.1.25.2.3.1.5",  //size 总存储单元数
                "1.3.6.1.2.1.25.2.3.1.6"}; //used 使用存储单元数;
        List<TableEvent> tableEvents = snmpSessionUtil.snmpWalk(sysDisk);
        String DISK_OID = "1.3.6.1.2.1.25.2.1.4";
        long fsstatUse=0;
        long fsstatTotal=0;
        long fsstatCount=0;
        for (TableEvent event : tableEvents) {
            VariableBinding[] values = event.getColumns();
            if(values == null ||!DISK_OID.equals(values[0].getVariable().toString()))
                continue;
            String diskName=values[1].getVariable().toString();
            BigDecimal unit = new BigDecimal(values[2].getVariable().toString());//unit 存储单元大小
            BigDecimal totalSize = new BigDecimal(values[3].getVariable().toString()).multiply(unit);//size 总存储单元数
            BigDecimal usedSize = new BigDecimal(values[4].getVariable().toString()).multiply(unit);//use
            BigDecimal usePct=usedSize.divide(totalSize,4,RoundingMode.HALF_UP);
            fsstatUse+=usedSize.longValue();
            fsstatTotal+=totalSize.longValue();
            fsstatCount+=1;
            Map<String,Object> system=new HashMap<>();
            Map<String,Object> filesystem=new HashMap<>();
            filesystem.put("available",totalSize.longValue());
            filesystem.put("device_name",diskName);
            filesystem.put("files",999l);
            filesystem.put("free",totalSize.subtract(usedSize).longValue());
            filesystem.put("free_files",999l);
            filesystem.put("mount_point",diskName);
            filesystem.put("total",totalSize.longValue());
            filesystem.put("type","xfs");
            Map<String,Object> used=new HashMap<>();
            used.put("bytes",usedSize.longValue());
            used.put("pct",usePct.floatValue());
            filesystem.put("used",used);
            system.put("filesystem",filesystem);
            source.put("system",system);
            //XContentBuilder xContentBuilderDisk=this.map2builder(source);
            elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.19-000001",IdUtils.fastUUID(),source);

        }
        Map<String,Object> sourceFsstat=this.metricbeatMap(hostComputer,port,"fsstat",snmpSessionUtil);
        Map<String,Object> system=new HashMap<>();
        Map<String,Object> fsstat=new HashMap<>();
        fsstat.put("count",fsstatCount);
        Map<String,Object> totalSize=new HashMap<>();
        totalSize.put("free",fsstatTotal-fsstatUse);
        totalSize.put("used",fsstatUse);
        totalSize.put("total",fsstatTotal);
        fsstat.put("total_size",totalSize);
        system.put("fsstat",fsstat);
        sourceFsstat.put("system",system);
        elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.19-000001",IdUtils.fastUUID(),sourceFsstat);

    }
    @SneakyThrows
    public void networkMap(SNMPSessionUtil snmpSessionUtil,Map<String,Object> source) {
        String[] sysNetwork = {
                "1.3.6.1.2.1.2.2.1.2",  //descr
                "1.3.6.1.2.1.2.2.1.10", //inOctets
                "1.3.6.1.2.1.2.2.1.16", //outOctets
                "1.3.6.1.2.1.2.2.1.14", //inError 错误
                "1.3.6.1.2.1.2.2.1.20", //outError
                "1.3.6.1.2.1.2.2.1.13", //inDiscard 丢弃包
                "1.3.6.1.2.1.2.2.1.19", //outDiscard
                "1.3.6.1.2.1.2.2.1.11", //inUcastPkts
                "1.3.6.1.2.1.2.2.1.17" //outUcastPkts
               };
        List<TableEvent> tableEvents = snmpSessionUtil.snmpWalk(sysNetwork);
        for (TableEvent event : tableEvents) {
            VariableBinding[] values = event.getColumns();
            String descr = values[0].getVariable().toString();
            BigDecimal inOctets=new BigDecimal(values[1].getVariable().toString());
            BigDecimal outOctets=new BigDecimal(values[2].getVariable().toString());
            BigDecimal inError=new BigDecimal(values[3].getVariable().toString());
            BigDecimal outError=new BigDecimal(values[4].getVariable().toString());
            BigDecimal inDiscard=new BigDecimal(values[5].getVariable().toString());
            BigDecimal outDiscard=new BigDecimal(values[6].getVariable().toString());
            BigDecimal inUcastPkts=new BigDecimal(values[7].getVariable().toString());
            BigDecimal outUcastPkts=new BigDecimal(values[8].getVariable().toString());
            Map<String,Object> system=new HashMap<>();
            Map<String,Object> network=new HashMap<>();
            Map<String,Object> in=new HashMap<>();
            in.put("bytes",inOctets.longValue());
            in.put("dropped",inDiscard.longValue());
            in.put("errors",inError.longValue());
            in.put("packets",inUcastPkts.longValue());
            network.put("in",in);

            network.put("name",descr);
            Map<String,Object> out=new HashMap<>();
            out.put("bytes",outOctets.longValue());
            out.put("dropped",outDiscard.longValue());
            out.put("errors",outError.longValue());
            out.put("packets",outUcastPkts.longValue());
            network.put("out",out);
            system.put("network",network);
            source.put("system",system);
            elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.19-000001",IdUtils.fastUUID(),source);
        }
    }
    @SneakyThrows
    public void processMap(SNMPSessionUtil snmpSessionUtil,Map<String,Object> source,long totalCpuTime) {
        Map<Long,Long> nowCpuTime=new HashMap<>();
        Map<Long,BigDecimal> nowCpuPct=new HashMap<>();

        /*String[] sysProcess1 = {
                "1.3.6.1.2.1.25.4.2.1.4",  //run path
                "1.3.6.1.2.1.25.5.1.1.1" //cpu
        };*/
        String[] sysProcess = {
                "1.3.6.1.2.1.25.4.2.1.1",  //index
                "1.3.6.1.2.1.25.4.2.1.2",  //name
                "1.3.6.1.2.1.25.4.2.1.4",  //run path
                "1.3.6.1.2.1.25.4.2.1.5",  //parameters
                "1.3.6.1.2.1.25.4.2.1.6",  //type
                "1.3.6.1.2.1.25.5.1.1.1",  //cpu
                "1.3.6.1.2.1.25.5.1.1.2" //memory

        };
        List<TableEvent> tableEvents = snmpSessionUtil.snmpWalk(sysProcess);
        long totalNowCpuTime=0;
        if(lastCputime.size()==0){
            long pretotalCpuTime=0;
            for (TableEvent event : tableEvents) {
                VariableBinding[] values = event.getColumns();
                long  id = new BigDecimal(values[0].getVariable().toString()).longValue();
                String cpu = values[5].getVariable().toString();
                pretotalCpuTime+=new BigDecimal(cpu).longValue();
                lastCputime.put(id,new BigDecimal(cpu).longValue());
            }
            lastCputime.put(-1l,pretotalCpuTime);
            return;
        }else{
            for (TableEvent event : tableEvents) {
                VariableBinding[] values = event.getColumns();
                long  id = new BigDecimal(values[0].getVariable().toString()).longValue();
                String cpu = values[5].getVariable().toString();
                totalNowCpuTime+=new BigDecimal(cpu).longValue();
                nowCpuTime.put(id,new BigDecimal(cpu).longValue());
            }
            nowCpuTime.put(-1l,totalNowCpuTime);
        }
        long cpuTime=totalNowCpuTime-lastCputime.get(-1l);
        String[] sscpuNum = {SNMPConstants.SSCPUNUM};
        ArrayList<String> sscpuNums=snmpSessionUtil.snmpWalk2(sscpuNum);
        String[] sysMemory = {
                SNMPConstants.MEMTOTALREAL
        };
        ArrayList<String> sysMemorys = snmpSessionUtil.getSnmpGet(PDU.GET,sysMemory);
        BigDecimal memoryTotal=new BigDecimal(sysMemorys.get(0)).multiply(new BigDecimal(1024));
        if(cpuTime>totalCpuTime){
            totalCpuTime=cpuTime;
        }
        long cores=sscpuNums.size();
        for (TableEvent event : tableEvents) {
            VariableBinding[] values = event.getColumns();
            String cpu = values[5].getVariable().toString();
            String runPath = values[2].getVariable().toString();
            String parameters=values[3].getVariable().toString();
            String name=values[1].getVariable().toString();
            long  id = new BigDecimal(values[0].getVariable().toString()).longValue();
            nowCpuTime.put(id,new BigDecimal(cpu).longValue());
            if(null==nowCpuTime.get(id)||null==lastCputime.get(id)){
                continue;
            }
            if(runPath.length()<2){
                continue;
            }
            long cha=(new BigDecimal(cpu).longValue()-lastCputime.get(id))/cores;
            if(cha>0){
                System.out.println(name);
            }
            if(runPath==null){
                continue;
            }
            String[] args={runPath,parameters};
            BigDecimal mem = new BigDecimal(values[6].getVariable().toString()).multiply(new BigDecimal(1024));
            Map<String,Object> processSource=new HashMap<>();
            processSource.put("pid",id);
            processSource.put("args",parameters);
            processSource.put("name",name);
            processSource.put("working_directory",args);
            source.put("process",processSource);
            Map<String,Object> system=new HashMap<>();
            Map<String,Object> process=new HashMap<>();
            process.put("cmdline",args);
            BigDecimal normPct=new BigDecimal(0);
            if(cpuTime==0&&null!=lastCpuPct.get(id)){
                normPct=lastCpuPct.get(id);
            }
            if(cpuTime!=0){
                normPct=new BigDecimal(cha).divide(new BigDecimal(totalCpuTime),4,RoundingMode.HALF_UP);
            }
            nowCpuPct.put(id,normPct);
            Map<String,Object> cpuEs=new HashMap<>();
            Map<String,Object> cpuEsTotal=new HashMap<>();
            Map<String,Object> cpuEsTotalNorm=new HashMap<>();
            cpuEsTotalNorm.put("pct",normPct.floatValue());
            cpuEsTotal.put("norm",cpuEsTotalNorm);
            cpuEsTotal.put("pct",normPct.multiply(new BigDecimal(cores)).floatValue());
            cpuEsTotal.put("value",new BigDecimal(cpu).longValue());
            cpuEs.put("total",cpuEsTotal);
            process.put("cpu",cpuEs);
            Map<String,Object> memory=new HashMap<>();
            Map<String,Object> rss=new HashMap<>();
            rss.put("bytes",mem.longValue());
            rss.put("pct",mem.divide(memoryTotal,4,RoundingMode.HALF_UP).floatValue());
            memory.put("rss",rss);
            process.put("memory",memory);
            system.put("process",process);
            source.put("system",system);
            elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.19-000001",IdUtils.fastUUID(),source);

        }
        lastCputime.clear();
        lastCputime.putAll(nowCpuTime);
        lastCpuPct.clear();
        lastCpuPct.putAll(nowCpuPct);


    }

    @SneakyThrows
    public void loadMap(SNMPSessionUtil snmpSessionUtil,Map<String,Object> source) {
        String[] sysLoad = {
                ".1.3.6.1.4.1.2021.10.1.3.1",  //load1
                ".1.3.6.1.4.1.2021.10.1.3.2", //load5
                ".1.3.6.1.4.1.2021.10.1.3.3" //load15
        };
        String[] sscpuNum = {SNMPConstants.SSCPUNUM};
        ArrayList<String> sscpuNums=snmpSessionUtil.snmpWalk2(sscpuNum);
        long cores=sscpuNums.size();
        ArrayList<String> ssLoad=snmpSessionUtil.getSnmpGet(PDU.GET,sysLoad);
        BigDecimal load1Norm=new BigDecimal(ssLoad.get(0));
        BigDecimal load5Norm=new BigDecimal(ssLoad.get(1));
        BigDecimal load15Norm=new BigDecimal(ssLoad.get(2));
        BigDecimal load1=load1Norm.multiply(new BigDecimal(cores));
        BigDecimal load5=load5Norm.multiply(new BigDecimal(cores));
        BigDecimal load15=load15Norm.multiply(new BigDecimal(cores));
        Map<String,Object> system=new HashMap<>();
        Map<String,Object> load=new HashMap<>();
        load.put("cores",cores);
        load.put("1",load1);
        load.put("5",load5);
        load.put("15",load15);
        Map<String,Object> norm=new HashMap<>();
        norm.put("1",load1Norm);
        load.put("5",load5Norm);
        load.put("15",load15Norm);
        load.put("norm",norm);
        system.put("load",load);
        source.put("system",system);

    }
    @SneakyThrows
    public void diskioMap(SNMPSessionUtil snmpSessionUtil,Map<String,Object> source) {
        String[] sysDiskio = {
                ".1.3.6.1.4.1.2021.13.15.1.1.2", //name
                ".1.3.6.1.4.1.2021.13.15.1.1.5",  //read
                ".1.3.6.1.4.1.2021.13.15.1.1.6",  //write
                ".1.3.6.1.4.1.2021.13.15.1.1.12",  //readbytes
                ".1.3.6.1.4.1.2021.13.15.1.1.13"  //writebytes
        };
        List<TableEvent> tableEvents = snmpSessionUtil.snmpWalk(sysDiskio);
        for (TableEvent event : tableEvents) {
            VariableBinding[] values = event.getColumns();
            String name = values[0].getVariable().toString();
            BigDecimal readCount =new BigDecimal(values[1].getVariable().toString());
            BigDecimal writeCount =new BigDecimal(values[2].getVariable().toString());
            BigDecimal readBytes =new BigDecimal(values[3].getVariable().toString());
            BigDecimal writeBytes =new BigDecimal(values[4].getVariable().toString());
            Map<String,Object> system=new HashMap<>();
            Map<String,Object> diskio=new HashMap<>();
            diskio.put("name",name);
            Map<String,Object> read=new HashMap<>();
            read.put("count",readCount.longValue());
            read.put("bytes",readBytes.longValue());
            read.put("time",999l);
            diskio.put("read",read);
            Map<String,Object> write=new HashMap<>();
            write.put("count",writeCount.longValue());
            write.put("bytes",writeBytes.longValue());
            write.put("time",999l);
            diskio.put("write",write);
            system.put("diskio",diskio);
            source.put("system",system);
            elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.19-000001",IdUtils.fastUUID(),source);


        }
    }

}
