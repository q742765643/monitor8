package com.piesat.skywalking.schedule.service.snmp;

import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.om.protocol.snmp.SNMPConstants;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import com.piesat.skywalking.util.IdUtils;
import com.piesat.util.IndexNameUtil;
import lombok.SneakyThrows;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7InsertRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

@Service
public class SNMPService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    private ConcurrentHashMap<String,ArrayList<String>> lastCpu=new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,List<TableEvent>> lastProcess=new ConcurrentHashMap<>();

    @SneakyThrows
    public void getSystemInfo(String hostComputer, String port, String version, Date date){
        BulkRequest request = new BulkRequest();
        SNMPSessionUtil snmp=new SNMPSessionUtil(hostComputer,port,"public", version);
        Map<String,Object> basicInfo=this.getBasicInfo(snmp);
        if(basicInfo==null){
            return;
        }
        basicInfo.put("ip",hostComputer);
        basicInfo.put("port",port);
        basicInfo.put("version",version);
        basicInfo.put("@timestamp",date);
        List<Map<String,Object>> esList = new CopyOnWriteArrayList<Map<String,Object>>();
        final CountDownLatch latch = new CountDownLatch(7);
        new Thread(()->{
            this.cpuMap(snmp,basicInfo,esList);
            latch.countDown();
        }).start();
        new Thread(()->{
            this.memoryMap(snmp,basicInfo,esList);
            latch.countDown();
        }).start();
        new Thread(()->{
            this.networkMap(snmp,basicInfo,esList);
            latch.countDown();
        }).start();
        new Thread(()->{
            this.diskMap(snmp,basicInfo,esList);
            latch.countDown();
        }).start();
        new Thread(()->{
            this.processMap(snmp,basicInfo,esList);
            latch.countDown();
        }).start();
        new Thread(()->{
            this.loadMap(snmp,basicInfo,esList);
            latch.countDown();
        }).start();
        new Thread(()->{
            this.diskioMap(snmp,basicInfo,esList);
            latch.countDown();
        }).start();
        latch.await();
        String indexName= IndexNameUtil.getIndexName(IndexNameConstant.METRICBEAT,date);
        for(Map<String,Object> source:esList){
            IndexRequest indexRequest = new ElasticSearch7InsertRequest(indexName, IdUtils.fastUUID()).source(source);
            request.add(indexRequest);
            //elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.24-000001",IdUtils.fastUUID(),source);
        }
        elasticSearch7Client.synchronousBulk(request);
    }

    @SneakyThrows
    public  Map<String,Object>  getBasicInfo(SNMPSessionUtil snmp){
        Map<String,Object> basicInfo=new HashMap<>();
        String[] sysDesc = {SNMPConstants.SYSDESC};
        ArrayList<String> sysDescs=snmp.getSnmpGet(PDU.GET,sysDesc);
        String[] sysName = {SNMPConstants.SYSNAME};
        ArrayList<String> sysNames=snmp.getSnmpGet(PDU.GET,sysName);
        String[] sscpuNum = {SNMPConstants.SSCPUNUM};
        ArrayList<String> sscpuNums=snmp.snmpWalk2(sscpuNum);
        String[] sysMemory = {SNMPConstants.MEMTOTALREAL};
        ArrayList<String> sysMemorys = snmp.getSnmpGet(PDU.GET,sysMemory);
        if(sysMemorys.size()==0||"noSuchObject".equals(sysMemorys.get(0))){
            return null;
        }
        BigDecimal memoryTotal=new BigDecimal(sysMemorys.get(0)).multiply(new BigDecimal(1024));
        basicInfo.put("desc",sysDescs.get(0));
        basicInfo.put("hostname",sysNames.get(0));
        long cores=sscpuNums.size();
        basicInfo.put("cores",cores);
        basicInfo.put("memoryTotal",memoryTotal);

        return basicInfo;
    }

    public Map<String,Object> metricbeatMap(String type,Map<String,Object> basicInfo) throws Exception {
        Map<String,Object> source=new HashMap<>();
        String hostname= (String) basicInfo.get("hostname");
        String ip= (String) basicInfo.get("ip");
        String desc= (String) basicInfo.get("desc");
        String version= (String) basicInfo.get("version");
        Map<String,Object> agent=new HashMap<>();
        source.put("@timestamp",basicInfo.get("@timestamp"));
        agent.put("ephemeral_id","5bc43524-b053-11ea-80ae-"+hostname);
        agent.put("hostname",hostname);
        agent.put("id","5fedf84c-b053-11ea-8288-"+hostname);
        agent.put("name",ip);
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
        host.put("hostname",hostname);
        host.put("id","");
        host.put("ip",ip);
        host.put("mac","");
        host.put("name",ip);
        Map<String,Object> os=new HashMap<>();
        os.put("codename","");
        os.put("family","");
        os.put("kernel","");
        os.put("name",desc);
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
    public void cpuMap(SNMPSessionUtil snmp,Map<String,Object> basicInfo,List<Map<String,Object>> esList) {
        Map<String,Object> source=this.metricbeatMap("cpu",basicInfo);
        String[] sysCpu = {
                SNMPConstants.SSCPURAWIDLE,
                SNMPConstants.SSCPURAWWAIT,
                SNMPConstants.SSCPURAWNICE,
                SNMPConstants.SSCPURAWSOFTIRQ,
                SNMPConstants.SSCPURAWSTEAL,
                SNMPConstants.SSCPURAWSYSTEM,
                SNMPConstants.SSCPURAWUSER,
        };
        ArrayList<String> sysCpusPre=null;
        String ip= (String) basicInfo.get("ip");
        if(null==lastCpu.get(ip)){
            sysCpusPre=snmp.getSnmpGet(PDU.GET,sysCpu);
            lastCpu.put(ip,sysCpusPre);
            return;
        }else {
            sysCpusPre=lastCpu.get(ip);
        }
        ArrayList<String> sysCpus=snmp.getSnmpGet(PDU.GET,sysCpu);
        lastCpu.put(ip,sysCpus);
        long cores= (long) basicInfo.get("cores");
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
        esList.add(source);
    }

    @SneakyThrows
    public void memoryMap(SNMPSessionUtil snmp,Map<String,Object> basicInfo,List<Map<String,Object>> esList) {
        Map<String,Object> source=this.metricbeatMap("memory",basicInfo);
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
        ArrayList<String> sysMemorys = snmp.getSnmpGet(PDU.GET,sysMemory);
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
        esList.add(source);

    }

    @SneakyThrows
    public void diskMap(SNMPSessionUtil snmp,Map<String,Object> basicInfo,List<Map<String,Object>> esList) {
        String[] sysDisk = {"1.3.6.1.2.1.25.2.3.1.2",  //type 存储单元类型
                "1.3.6.1.2.1.25.2.3.1.3",  //descr
                "1.3.6.1.2.1.25.2.3.1.4",  //unit 存储单元大小
                "1.3.6.1.2.1.25.2.3.1.5",  //size 总存储单元数
                "1.3.6.1.2.1.25.2.3.1.6"}; //used 使用存储单元数;
        List<TableEvent> tableEvents = snmp.snmpWalk(sysDisk);
        String DISK_OID = "1.3.6.1.2.1.25.2.1.4";
        long fsstatUse=0;
        long fsstatTotal=0;
        long fsstatCount=0;
        for (TableEvent event : tableEvents) {
            Map<String,Object> source=this.metricbeatMap("filesystem",basicInfo);
            VariableBinding[] values = event.getColumns();
            if(values == null ||!DISK_OID.equals(values[0].getVariable().toString()))
                continue;
            String diskName=values[1].getVariable().toString();
            if(diskName.indexOf("kubernetes")!=-1||diskName.indexOf("docker")!=-1){
                continue;
            }
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
            esList.add(source);
        }
        Map<String,Object> sourceFsstat=this.metricbeatMap("fsstat",basicInfo);
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
        esList.add(sourceFsstat);
    }

    @SneakyThrows
    public void networkMap(SNMPSessionUtil snmp,Map<String,Object> basicInfo,List<Map<String,Object>> esList) {
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
        List<TableEvent> tableEvents = snmp.snmpWalk(sysNetwork);
        for (TableEvent event : tableEvents) {
            Map<String,Object> source=this.metricbeatMap("network",basicInfo);
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
            esList.add(source);
        }
    }

    @SneakyThrows
    public void processMap(SNMPSessionUtil snmp,Map<String,Object> basicInfo,List<Map<String,Object>> esList) {
        String[] sysProcess = {
                "1.3.6.1.2.1.25.4.2.1.1",  //index
                "1.3.6.1.2.1.25.4.2.1.2",  //name
                "1.3.6.1.2.1.25.4.2.1.4",  //run path
                "1.3.6.1.2.1.25.4.2.1.5",  //parameters
                "1.3.6.1.2.1.25.4.2.1.6",  //type
                "1.3.6.1.2.1.25.5.1.1.1",  //cpu
                "1.3.6.1.2.1.25.5.1.1.2" //memory

        };
        String ip= (String) basicInfo.get("ip");
        List<TableEvent> tableEventsPre=new ArrayList<>();
        if(null==lastProcess.get(ip)){
            tableEventsPre = snmp.snmpWalk(sysProcess);
            lastProcess.put(ip,tableEventsPre);
            return;
        }else {
            tableEventsPre=lastProcess.get(ip);
        }

        Map<String,Long> differenceMap=new HashMap<>();
        Map<String,Long> tableEventsPreMap=new HashMap<>();
        long cores= (long) basicInfo.get("cores");
        BigDecimal memoryTotal= (BigDecimal) basicInfo.get("memoryTotal");
        for(TableEvent event :tableEventsPre){
            VariableBinding[] values = event.getColumns();
            long cpu = new BigDecimal(values[5].getVariable().toString()).longValue();
            String  id = values[0].getVariable().toString();
            tableEventsPreMap.put(id,cpu);
        }
        List<TableEvent> tableEvents = snmp.snmpWalk(sysProcess);
        lastProcess.put(ip,tableEvents);
        long totalCpuTime=0;
        for (TableEvent event : tableEvents) {
            VariableBinding[] values = event.getColumns();
            String  id = values[0].getVariable().toString();
            long oldCpu=0;
            if(null!=tableEventsPreMap.get(id)){
                oldCpu=tableEventsPreMap.get(id);
            }
            long cpu = new BigDecimal(values[5].getVariable().toString()).longValue();
            long difference=cpu-oldCpu;
            differenceMap.put(id,difference);
            totalCpuTime+=difference;
        }
        Map<String,String> exits=new HashMap<>();
        for (TableEvent event : tableEvents) {
            Map<String,Object> source=this.metricbeatMap("process",basicInfo);
            VariableBinding[] values = event.getColumns();
            String cpu = values[5].getVariable().toString();
            String runPath = values[2].getVariable().toString();
            String parameters=values[3].getVariable().toString();
            String name=values[1].getVariable().toString();
            String  id = values[0].getVariable().toString();
            String type=values[4].getVariable().toString();
            if(!"4".equals(type)){
                continue;
            }
            if(runPath==null|| runPath.length()<2){
                continue;
            }
            if(parameters==null){
                parameters="";
            }
            String[] args={runPath,parameters};
            BigDecimal mem = new BigDecimal(values[6].getVariable().toString()).multiply(new BigDecimal(1024));
            if(mem.longValue()==0&&differenceMap.get(id)==0){
               continue;
            }
            Map<String,Object> processSource=new HashMap<>();
            processSource.put("pid",new BigDecimal(id).longValue());
            processSource.put("args",parameters);
            processSource.put("name",name);
            processSource.put("working_directory",args);
            source.put("process",processSource);
            Map<String,Object> system=new HashMap<>();
            Map<String,Object> process=new HashMap<>();
            process.put("cmdline",args);
            BigDecimal normPct=new BigDecimal(0);
            if(totalCpuTime!=0){
               normPct=new BigDecimal(differenceMap.get(id)).divide(new BigDecimal(totalCpuTime),4,RoundingMode.HALF_UP);
            }

            Map<String,Object> cpuEs=new HashMap<>();
            Map<String,Object> cpuEsTotal=new HashMap<>();
            Map<String,Object> cpuEsTotalNorm=new HashMap<>();
            cpuEsTotalNorm.put("pct",normPct.floatValue());
            cpuEsTotal.put("norm",cpuEsTotalNorm);
            cpuEsTotal.put("pct",normPct.floatValue()*cores);
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
            esList.add(source);

        }



    }

    @SneakyThrows
    public void loadMap(SNMPSessionUtil snmp,Map<String,Object> basicInfo,List<Map<String,Object>> esList) {
        Map<String,Object> source=this.metricbeatMap("load",basicInfo);
        String[] sysLoad = {
                ".1.3.6.1.4.1.2021.10.1.3.1",  //load1
                ".1.3.6.1.4.1.2021.10.1.3.2", //load5
                ".1.3.6.1.4.1.2021.10.1.3.3" //load15
        };
        String[] sscpuNum = {SNMPConstants.SSCPUNUM};
        ArrayList<String> sscpuNums=snmp.snmpWalk2(sscpuNum);
        long cores=sscpuNums.size();
        ArrayList<String> ssLoad=snmp.getSnmpGet(PDU.GET,sysLoad);
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
        esList.add(source);

    }
    @SneakyThrows
    public void diskioMap(SNMPSessionUtil snmp,Map<String,Object> basicInfo,List<Map<String,Object>> esList) {
        String[] sysDiskio = {
                ".1.3.6.1.4.1.2021.13.15.1.1.2", //name
                ".1.3.6.1.4.1.2021.13.15.1.1.5",  //read
                ".1.3.6.1.4.1.2021.13.15.1.1.6",  //write
                ".1.3.6.1.4.1.2021.13.15.1.1.12",  //readbytes
                ".1.3.6.1.4.1.2021.13.15.1.1.13"  //writebytes
        };
        List<TableEvent> tableEvents = snmp.snmpWalk(sysDiskio);
        for (TableEvent event : tableEvents) {
            Map<String,Object> source=this.metricbeatMap("diskio",basicInfo);
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
            esList.add(source);


        }
    }
}
