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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

@Service
public class SNMPLinuxService extends SNMPService{
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @SneakyThrows
    @Override
    public void getSystemInfo(String hostComputer, String port, String version, Date date,SNMPSessionUtil snmp){
        BulkRequest request = new BulkRequest();
        Map<String,Object> basicInfo=this.getBasicInfo(snmp);
        basicInfo.put("ip",hostComputer);
        basicInfo.put("port",port);
        basicInfo.put("version",version);
        basicInfo.put("@timestamp",date);
        List<Map<String,Object>> esList = new ArrayList<>();
        this.cpuMap(snmp,basicInfo,esList);
        this.memoryMap(snmp,basicInfo,esList);
        this.networkMap(snmp,basicInfo,esList);
        this.diskMap(snmp,basicInfo,esList);
        this.processMap(snmp,basicInfo,esList);
        this.loadMap(snmp,basicInfo,esList);
        this.diskioMap(snmp,basicInfo,esList);
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
        Map<String,Object> basicInfo= null;
        basicInfo = new HashMap<>();
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
                SNMPConstants.SSCPUIDLE
        };
        ArrayList<String> sysCpus=snmp.getSnmpGet(PDU.GET,sysCpu);
        float idle=new BigDecimal(sysCpus.get(0)).divide(new BigDecimal(100)).floatValue();
        float total=1-idle;

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
        //BigDecimal actualFree = memAvailReal.add(memBuffer).add(memCached);
        //BigDecimal actualUsedBytes = memTotalReal.subtract(actualFree);
        //BigDecimal actualUsedPct = actualUsedBytes.divide(memTotalReal,4,RoundingMode.HALF_UP);
        Map<String,Object> system=new HashMap<>();
        Map<String,Object> memory=new HashMap<>();
        Map<String,Object> actual=new HashMap<>();
        actual.put("free",memAvailReal.longValue());
        Map<String,Object> actualUsed=new HashMap<>();
        actualUsed.put("bytes",memTotalReal.subtract(memAvailReal).longValue());
        actualUsed.put("pct",(memTotalReal.subtract(memAvailReal)).divide(memTotalReal,4,RoundingMode.HALF_UP).floatValue());
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
