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
public class SNMPService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    private ArrayList<String> sysCpusPre=new ArrayList<>();
    private Map<Long,Long> mapProcessCpu=new HashMap<>();
    private Map<Long,BigDecimal> lastProcessCpu=new HashMap<>();

    private Date date;
    @SneakyThrows
    public List<Map<String,Object>> getSystemInfo(String hostComputer, String port, String version){
        date=new Date();
        hostComputer="10.1.100.8";
        port="161";
        version="2";
        List<Map<String,Object>> mapList=new ArrayList<>();
        SNMPSessionUtil snmpSessionUtil=new SNMPSessionUtil(hostComputer,port,"public", version);

        Map<String,Object> sourceCpu=this.metricbeatMap(hostComputer,port,"cpu",snmpSessionUtil);
        long totalCpuTime=this.cpuMap(snmpSessionUtil,sourceCpu);
        Map<String,Object> sourceProcess=this.metricbeatMap(hostComputer,port,"process",snmpSessionUtil);
        this.processMap(snmpSessionUtil,sourceProcess,totalCpuTime);
        XContentBuilder xContentBuilderCpu=this.map2builder(sourceCpu);
        elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.19-000001",IdUtils.fastUUID(),xContentBuilderCpu);

        Map<String,Object> sourceMemory=this.metricbeatMap(hostComputer,port,"memory",snmpSessionUtil);
        this.memoryMap(snmpSessionUtil,sourceMemory);
        XContentBuilder xContentBuilderMemory=this.map2builder(sourceMemory);
        elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.19-000001",IdUtils.fastUUID(),xContentBuilderMemory);

        Map<String,Object> sourceDisk=this.metricbeatMap(hostComputer,port,"filesystem",snmpSessionUtil);
        this.diskMap(snmpSessionUtil,sourceDisk);

        Map<String,Object> sourceNetwork=this.metricbeatMap(hostComputer,port,"network",snmpSessionUtil);
        this.networkMap(snmpSessionUtil,sourceNetwork);


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
        source.put("agent.ephemeral_id","5bc43524-b053-11ea-80ae-"+sysNames.get(0));
        source.put("agent.hostname",sysNames.get(0));
        source.put("agent.id","5fedf84c-b053-11ea-8288-"+sysNames.get(0));
        source.put("agent.name",hostComputer);
        source.put("agent.type","snmpbeat");
        source.put("agent.version",version);
        source.put("cloud.availability_zone","");
        source.put("cloud.instance.id","");
        source.put("cloud.instance.name","");
        source.put("cloud.machine.type","");
        source.put("cloud.provider","");
        source.put("ecs.version","1.5.0");
        source.put("event.dataset","system."+type);
        source.put("event.duration","0.2");
        source.put("event.module","system");
        source.put("host.architecture","");
        source.put("host.containerized","");
        source.put("host.hostname",sysNames.get(0));
        source.put("host.id","");
        source.put("host.ip",hostComputer);
        source.put("host.mac","");
        source.put("host.name",hostComputer);
        source.put("host.os.codename","");
        source.put("host.os.family","");
        source.put("host.os.kernel","");
        source.put("host.os.name",sysDescs.get(0));
        source.put("host.os.platform","");
        source.put("host.os.version","");
        source.put("metricset.name",type);
        if(type.equals("filesystem")){
            source.put("metricset.period",60000l);
        }else{
            source.put("metricset.period",10000l);
        }
        source.put("service.type","system");
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
        source.put("system.cpu.cores",cores);
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
        source.put("system.cpu.idle.norm.pct",(idlep));
        source.put("system.cpu.idle.pct",idlep*cores);
        source.put("system.cpu.iowait.norm.pct",waitp);
        source.put("system.cpu.iowait.pct",waitp*cores);
        source.put("system.cpu.irq.norm.pct",0f);
        source.put("system.cpu.irq.pct",0f);
        source.put("system.cpu.nice.norm.pct",nicep);
        source.put("system.cpu.nice.pct",nicep*cores);
        source.put("system.cpu.softirq.norm.pct",softp);
        source.put("system.cpu.softirq.pct",softp*cores);
        source.put("system.cpu.steal.norm.pct",stealp);
        source.put("system.cpu.steal.pct",stealp*cores);
        source.put("system.cpu.system.norm.pct",systemp);
        source.put("system.cpu.system.pct",systemp*cores);
        source.put("system.cpu.user.norm.pct",userp);
        source.put("system.cpu.user.pct",userp*cores);
        source.put("system.cpu.total.norm.pct",total);
        source.put("system.cpu.total.pct",total*cores);
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
        source.put("system.memory.actual.free",actualFree.longValue());
        source.put("system.memory.actual.used.bytes",actualUsedBytes.longValue());
        source.put("system.memory.actual.used.pct",actualUsedPct.floatValue());
        source.put("system.memory.free",memAvailReal.longValue());
        source.put("system.memory.hugepages.default_size",0l);
        source.put("system.memory.hugepages.free",0l);
        source.put("system.memory.hugepages.reserved",0l);
        source.put("system.memory.hugepages.surplus",0l);
        source.put("system.memory.hugepages.swap.out.fallback",0l);
        source.put("system.memory.hugepages.swap.out.pages",0l);
        source.put("system.memory.hugepages.total",0l);
        source.put("system.memory.hugepages.used.bytes",0l);
        source.put("system.memory.hugepages.used.pct",0l);
        source.put("system.memory.page_stats.pgfree.pages",0l);
        source.put("system.memory.page_stats.pgscan_direct.pages",0l);
        source.put("system.memory.page_stats.pgscan_kswapd.pages",0l);
        source.put("system.memory.page_stats.pgsteal_direct.pages",0l);
        source.put("system.memory.page_stats.pgsteal_kswapd.pages",0l);
        BigDecimal swapUsedBytes=memTotalSwap.subtract(memAvailSwap);
        BigDecimal swapUsedPct=new BigDecimal(0);
        if(memTotalSwap.longValue()>0){
            swapUsedPct=swapUsedBytes.divide(memTotalSwap,4,RoundingMode.HALF_UP);
        }
        source.put("system.memory.swap.free",memAvailSwap.longValue());
        source.put("system.memory.swap.in.pages",0l);
        source.put("system.memory.swap.out.pages",0l);
        source.put("system.memory.swap.readahead.cached",0l);
        source.put("system.memory.swap.readahead.pages",0l);
        source.put("system.memory.swap.total",memTotalSwap.longValue());

        source.put("system.memory.swap.used.bytes",swapUsedBytes.longValue());
        source.put("system.memory.swap.used.pct",swapUsedPct.floatValue());
        source.put("system.memory.total",memTotalReal.longValue());
        source.put("system.memory.used.bytes",memTotalReal.subtract(memAvailReal).longValue());
        source.put("system.memory.used.pct",(memTotalReal.subtract(memAvailReal)).divide(memTotalReal,4,RoundingMode.HALF_UP).floatValue());

    }

    @SneakyThrows
    public void diskMap(SNMPSessionUtil snmpSessionUtil,Map<String,Object> source) {
        String[] sysDisk = {"1.3.6.1.2.1.25.2.3.1.2",  //type 存储单元类型
                "1.3.6.1.2.1.25.2.3.1.3",  //descr
                "1.3.6.1.2.1.25.2.3.1.4",  //unit 存储单元大小
                "1.3.6.1.2.1.25.2.3.1.5",  //size 总存储单元数
                "1.3.6.1.2.1.25.2.3.1.6"}; //used 使用存储单元数;
        List<TableEvent> tableEvents = snmpSessionUtil.snmpWalk(sysDisk);
        String DISK_OID = "1.3.6.1.2.1.25.2.1.4";
        for (TableEvent event : tableEvents) {
            VariableBinding[] values = event.getColumns();
            if(values == null ||!DISK_OID.equals(values[0].getVariable().toString()))
                continue;
            String diskName=values[1].getVariable().toString();
            BigDecimal unit = new BigDecimal(values[2].getVariable().toString());//unit 存储单元大小
            BigDecimal totalSize = new BigDecimal(values[3].getVariable().toString()).multiply(unit);//size 总存储单元数
            BigDecimal usedSize = new BigDecimal(values[4].getVariable().toString()).multiply(unit);//use
            BigDecimal usePct=usedSize.divide(totalSize,4,RoundingMode.HALF_UP);
            source.put("system.filesystem.available",totalSize.longValue());
            source.put("system.filesystem.device_name",diskName);
            source.put("system.filesystem.files",999l);
            source.put("system.filesystem.free",totalSize.subtract(usedSize).longValue());
            source.put("system.filesystem.free_files",999l);
            source.put("system.filesystem.mount_point",diskName);
            source.put("system.filesystem.total",totalSize.longValue());
            source.put("system.filesystem.type","xfs");
            source.put("system.filesystem.used.bytes",usedSize.longValue());
            source.put("system.filesystem.used.pct",usePct.floatValue());
            XContentBuilder xContentBuilderDisk=this.map2builder(source);
            elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.19-000001",IdUtils.fastUUID(),xContentBuilderDisk);

        }
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
            source.put("system.network.in.bytes",inOctets.longValue());
            source.put("system.network.in.dropped",inDiscard.longValue());
            source.put("system.network.in.errors",inError.longValue());
            source.put("system.network.in.packets",inUcastPkts.longValue());
            source.put("system.network.name",descr);
            source.put("system.network.out.bytes",outOctets.longValue());
            source.put("system.network.out.dropped",outDiscard.longValue());
            source.put("system.network.out.errors",outError.longValue());
            source.put("system.network.out.packets",outUcastPkts.longValue());
            XContentBuilder xContentBuilderNetwork=this.map2builder(source);
            elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.19-000001",IdUtils.fastUUID(),xContentBuilderNetwork);
        }
    }
    @SneakyThrows
    public void processMap(SNMPSessionUtil snmpSessionUtil,Map<String,Object> source,long totalCpuTime) {
        Map<Long,Long> map=new HashMap<>();
        Map<Long,BigDecimal> processCpuPct=new HashMap<>();

        /*String[] sysProcess1 = {
                "1.3.6.1.2.1.25.4.2.1.4",  //run path
                "1.3.6.1.2.1.25.5.1.1.1" //cpu
        };*/
        String[] sysProcess2 = {
                "1.3.6.1.2.1.25.4.2.1.1",  //index
                "1.3.6.1.2.1.25.4.2.1.2",  //name
                "1.3.6.1.2.1.25.4.2.1.4",  //run path
                "1.3.6.1.2.1.25.4.2.1.5",  //parameters
                "1.3.6.1.2.1.25.4.2.1.6",  //type
                "1.3.6.1.2.1.25.5.1.1.1",  //cpu
                "1.3.6.1.2.1.25.5.1.1.2" //memory

        };
        List<TableEvent> tableEvents2 = snmpSessionUtil.snmpWalk(sysProcess2);
        long totalNowCpuTime=0;
        if(mapProcessCpu.size()==0){
            long pretotalCpuTime=0;
            for (TableEvent event : tableEvents2) {
                VariableBinding[] values = event.getColumns();
                long  id = new BigDecimal(values[0].getVariable().toString()).longValue();
                String cpu = values[5].getVariable().toString();
                pretotalCpuTime+=new BigDecimal(cpu).longValue();
                mapProcessCpu.put(id,new BigDecimal(cpu).longValue());
            }
            mapProcessCpu.put(-1l,pretotalCpuTime);
            return;
        }else{
            for (TableEvent event : tableEvents2) {
                VariableBinding[] values = event.getColumns();
                long  id = new BigDecimal(values[0].getVariable().toString()).longValue();
                String cpu = values[5].getVariable().toString();
                totalNowCpuTime+=new BigDecimal(cpu).longValue();
                map.put(id,new BigDecimal(cpu).longValue());
            }
            map.put(-1l,totalNowCpuTime);
        }
        long cpuTime=totalNowCpuTime-mapProcessCpu.get(-1l);
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
        for (TableEvent event : tableEvents2) {
            VariableBinding[] values = event.getColumns();
            String cpu = values[5].getVariable().toString();
            String runPath = values[2].getVariable().toString();
            String parameters=values[3].getVariable().toString();
            String name=values[1].getVariable().toString();
            long  id = new BigDecimal(values[0].getVariable().toString()).longValue();
            map.put(id,new BigDecimal(cpu).longValue());
            if(null==map.get(id)||null==mapProcessCpu.get(id)){
                continue;
            }
            if(runPath.length()<2){
                continue;
            }
            long cha=(new BigDecimal(cpu).longValue()-mapProcessCpu.get(id))/cores;
            if(cha>0){
                System.out.println(name);
            }
            if(runPath==null){
                continue;
            }
            String[] args={runPath,parameters};
            BigDecimal mem = new BigDecimal(values[6].getVariable().toString()).multiply(new BigDecimal(1024));
            source.put("process.pid",id);
            source.put("process.args",parameters);
            source.put("process.name",name);
            source.put("process.working_directory",args);
            source.put("system.process.cmdline",args);
            BigDecimal normPct=new BigDecimal(0);
            if(cpuTime==0&&null!=lastProcessCpu.get(id)){
                normPct=lastProcessCpu.get(id);
            }
            if(cpuTime!=0){
                normPct=new BigDecimal(cha).divide(new BigDecimal(totalCpuTime),4,RoundingMode.HALF_UP);
            }
            processCpuPct.put(id,normPct);
            source.put("system.process.cpu.total.norm.pct",normPct.floatValue());
            source.put("system.process.cpu.total.pct",normPct.multiply(new BigDecimal(cores)).floatValue());
            source.put("system.process.cpu.total.value",new BigDecimal(cpu).longValue());
            source.put("system.process.memory.rss.bytes",mem.longValue());
            source.put("system.process.memory.rss.pct",mem.divide(memoryTotal,4,RoundingMode.HALF_UP).floatValue());
            XContentBuilder xContentBuilderNetwork=this.map2builder(source);
            elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.19-000001",IdUtils.fastUUID(),xContentBuilderNetwork);

        }
        mapProcessCpu.clear();
        mapProcessCpu.putAll(map);
        lastProcessCpu.clear();
        lastProcessCpu.putAll(processCpuPct);


    }
    protected XContentBuilder map2builder(Map<String, Object> objectMap) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
        for (String key : objectMap.keySet()) {
            Object value = objectMap.get(key);
            if (value instanceof StorageDataComplexObject) {
                builder.field(key, ((StorageDataComplexObject) value).toStorageData());
            } else {
                builder.field(key, value);
            }
        }
        builder.endObject();

        return builder;
    }
}
