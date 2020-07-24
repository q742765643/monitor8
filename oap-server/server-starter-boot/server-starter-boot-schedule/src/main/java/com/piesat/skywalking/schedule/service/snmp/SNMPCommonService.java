package com.piesat.skywalking.schedule.service.snmp;

import com.piesat.skywalking.om.protocol.snmp.SNMPConstants;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import com.piesat.skywalking.util.IdUtils;
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
public class SNMPCommonService extends SNMPService{
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
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
        final CountDownLatch latch = new CountDownLatch(6);
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
      /*  new Thread(()->{
            this.loadMap(snmp,basicInfo,esList);
            latch.countDown();
        }).start();*/
        new Thread(()->{
            this.diskioMap(snmp,basicInfo,esList);
            latch.countDown();
        }).start();
        latch.await();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String indexName="metricbeat-7.7.0-"+format.format(date)+"-000001";
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
        basicInfo.put("desc",sysDescs.get(0));
        basicInfo.put("hostname",sysNames.get(0));
        long cores=sscpuNums.size();
        basicInfo.put("cores",cores);
        return basicInfo;
    }
    @SneakyThrows
    public void cpuMap(SNMPSessionUtil snmp,Map<String,Object> basicInfo,List<Map<String,Object>> esList) {
        Map<String,Object> source=this.metricbeatMap("cpu",basicInfo);
        String[] sysCpu = {
               "1.3.6.1.2.1.25.3.3.1.2"
        };
        List<TableEvent> list=snmp.snmpWalk(sysCpu);
        int percentage = 0;
        for (TableEvent event : list) {
            VariableBinding[] values = event.getColumns();
            percentage += Integer.parseInt(values[0].getVariable().toString());
        }
        float total=new BigDecimal(percentage).divide(new BigDecimal(100)).divide(new BigDecimal(list.size()),4,RoundingMode.HALF_UP).floatValue();

        long cores= (long) basicInfo.get("cores");

        Map<String,Object> systemM=new HashMap<>();
        Map<String,Object> cpu=new HashMap<>();
        cpu.put("cores",0f);
        Map<String,Object> idleEs=new HashMap<>();
        Map<String,Object> idleNorm=new HashMap<>();
        idleNorm.put("pct",0f);
        idleEs.put("norm",idleNorm);
        idleEs.put("pct",0f);
        cpu.put("idle",idleEs);

        Map<String,Object> iowaitEs=new HashMap<>();
        Map<String,Object> iowaitNorm=new HashMap<>();
        iowaitNorm.put("pct",0f);
        iowaitEs.put("norm",iowaitNorm);
        iowaitEs.put("pct",0f);
        cpu.put("iowait",iowaitEs);

        Map<String,Object> irqEs=new HashMap<>();
        Map<String,Object> irqNorm=new HashMap<>();
        irqNorm.put("pct",0f);
        irqEs.put("norm",irqNorm);
        irqEs.put("pct",0f);
        cpu.put("irq",irqEs);

        Map<String,Object> niceEs=new HashMap<>();
        Map<String,Object> niceNorm=new HashMap<>();
        niceNorm.put("pct",0f);
        niceEs.put("norm",niceNorm);
        niceEs.put("pct",0f);
        cpu.put("nice",niceEs);

        Map<String,Object> softirqEs=new HashMap<>();
        Map<String,Object> softirqNorm=new HashMap<>();
        softirqNorm.put("pct",0f);
        softirqEs.put("norm",softirqNorm);
        softirqEs.put("pct",0f);
        cpu.put("softirq",softirqEs);

        Map<String,Object> stealEs=new HashMap<>();
        Map<String,Object> stealNorm=new HashMap<>();
        stealNorm.put("pct",0f);
        stealEs.put("norm",stealNorm);
        stealEs.put("pct",0f);
        cpu.put("steal",stealEs);

        Map<String,Object> systemEs=new HashMap<>();
        Map<String,Object> systemNorm=new HashMap<>();
        systemNorm.put("pct",0f);
        systemEs.put("norm",systemNorm);
        systemEs.put("pct",0f);
        cpu.put("system",systemEs);


        Map<String,Object> userEs=new HashMap<>();
        Map<String,Object> userNorm=new HashMap<>();
        userNorm.put("pct",0f);
        userEs.put("norm",userNorm);
        userEs.put("pct",0f);
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
        String[] sysMemory =  {"1.3.6.1.2.1.25.2.3.1.2",  //type 存储单元类型
                "1.3.6.1.2.1.25.2.3.1.3",  //descr
                "1.3.6.1.2.1.25.2.3.1.4",  //unit 存储单元大小
                "1.3.6.1.2.1.25.2.3.1.5",  //size 总存储单元数
                "1.3.6.1.2.1.25.2.3.1.6"}; //used 使用存储单元数;
        String PHYSICAL_MEMORY_OID = "1.3.6.1.2.1.25.2.1.2";//物理存储
        String VIRTUAL_MEMORY_OID = "1.3.6.1.2.1.25.2.1.3"; //虚拟存储

        List<TableEvent> list=snmp.snmpWalk(sysMemory);
        BigDecimal memTotalSwap=new BigDecimal(0);
        //BigDecimal memAvailSwap=new BigDecimal(0);
        BigDecimal memUseSwap=new BigDecimal(0);
        BigDecimal memTotalReal=new BigDecimal(0);
        //BigDecimal memAvailReal=new BigDecimal(0);
        BigDecimal memUseReal=new BigDecimal(0);
        for(TableEvent event : list){
            VariableBinding[] values = event.getColumns();
            if(values == null) continue;
            int unit = Integer.parseInt(values[2].getVariable().toString());//unit 存储单元大小
            int totalSize = Integer.parseInt(values[3].getVariable().toString());//size 总存储单元数
            int usedSize = Integer.parseInt(values[4].getVariable().toString());//used  使用存储单元数
            String oid = values[0].getVariable().toString();
            if (PHYSICAL_MEMORY_OID.equals(oid)){
                memTotalReal=new BigDecimal(totalSize).multiply(new BigDecimal(unit));
                memUseReal=new BigDecimal(usedSize).multiply(new BigDecimal(unit));
            }else if (VIRTUAL_MEMORY_OID.equals(oid)&&unit!=0&&totalSize!=0) {
                memTotalSwap=new BigDecimal(totalSize).multiply(new BigDecimal(unit));
                memUseSwap=new BigDecimal(usedSize).multiply(new BigDecimal(unit));
            }
        }
        Map<String,Object> system=new HashMap<>();
        Map<String,Object> memory=new HashMap<>();
        Map<String,Object> actual=new HashMap<>();
        actual.put("free",memTotalReal.subtract(memUseReal).longValue());
        Map<String,Object> actualUsed=new HashMap<>();
        actualUsed.put("bytes",memUseReal.longValue());
        actualUsed.put("pct",memUseReal.divide(memTotalReal,4,RoundingMode.HALF_UP).floatValue());
        actual.put("used",actualUsed);
        memory.put("actual",actual);
        memory.put("free",memTotalReal.subtract(memUseReal).longValue());

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

        BigDecimal swapUsedPct=new BigDecimal(0);
        if(memTotalSwap.longValue()>0){
            swapUsedPct=memUseSwap.divide(memTotalSwap,4,RoundingMode.HALF_UP);
        }
        Map<String,Object> swap=new HashMap<>();
        swap.put("free",memTotalSwap.subtract(memUseSwap).longValue());
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
        swapUsed.put("bytes",memUseSwap.longValue());
        swapUsed.put("pct",swapUsedPct.floatValue());
        swap.put("used",swapUsed);
        memory.put("swap",swap);
        memory.put("total",memTotalReal.longValue());
        Map<String,Object> totalUsed=new HashMap<>();
        totalUsed.put("bytes",memUseReal.longValue());
        totalUsed.put("pct",memUseReal.divide(memTotalReal,4,RoundingMode.HALF_UP).floatValue());
        memory.put("used",totalUsed);
        system.put("memory",memory);
        source.put("system",system);
        esList.add(source);

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
        List<TableEvent> tableEvents = snmp.snmpWalk(sysProcess);

        for (TableEvent event : tableEvents) {
            Map<String,Object> source=this.metricbeatMap("process",basicInfo);
            VariableBinding[] values = event.getColumns();
            String  id = values[0].getVariable().toString();
            if(id==null){
                continue;
            }
            String runPath = values[2].getVariable().toString();
            String parameters=values[3].getVariable().toString();
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
            String cpu = values[5].getVariable().toString();

            String name=values[1].getVariable().toString();



            String[] args={runPath,parameters};
            BigDecimal mem = new BigDecimal(values[6].getVariable().toString()).multiply(new BigDecimal(1024));
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

            Map<String,Object> cpuEs=new HashMap<>();
            Map<String,Object> cpuEsTotal=new HashMap<>();
            Map<String,Object> cpuEsTotalNorm=new HashMap<>();
            cpuEsTotalNorm.put("pct",normPct.floatValue());
            cpuEsTotal.put("norm",cpuEsTotalNorm);
            cpuEsTotal.put("pct",0f);
            cpuEsTotal.put("value",new BigDecimal(cpu).longValue());
            cpuEs.put("total",cpuEsTotal);
            process.put("cpu",cpuEs);
            Map<String,Object> memory=new HashMap<>();
            Map<String,Object> rss=new HashMap<>();
            rss.put("bytes",mem.longValue());
            rss.put("pct",0f);
            memory.put("rss",rss);
            process.put("memory",memory);
            system.put("process",process);
            source.put("system",system);
            esList.add(source);

        }



    }
}
