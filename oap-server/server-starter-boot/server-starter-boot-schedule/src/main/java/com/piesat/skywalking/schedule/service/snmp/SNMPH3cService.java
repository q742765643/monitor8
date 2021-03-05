package com.piesat.skywalking.schedule.service.snmp;

import com.alibaba.fastjson.JSON;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.om.protocol.snmp.SNMPConstants;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import com.piesat.skywalking.util.IdUtils;
import com.piesat.util.IndexNameUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7InsertRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.snmp4j.PDU;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class SNMPH3cService extends SNMPService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    @SneakyThrows
    @Override
    public void getSystemInfo(String hostComputer, String port, String version, Date date, SNMPSessionUtil snmp) {
        BulkRequest request = new BulkRequest();
        Map<String, Object> basicInfo = this.getBasicInfo(snmp);
        basicInfo.put("ip", hostComputer);
        basicInfo.put("port", port);
        basicInfo.put("version", version);
        basicInfo.put("@timestamp", date);
        List<Map<String, Object>> esList = new ArrayList<Map<String, Object>>();
        this.cpuMap(snmp, basicInfo, esList);
        this.memoryMap(snmp, basicInfo, esList);
        this.temperatureMap(snmp,basicInfo,esList);
        String indexName = IndexNameUtil.getIndexName(IndexNameConstant.METRICBEAT, date);
        for (Map<String, Object> source : esList) {
            IndexRequest indexRequest = new ElasticSearch7InsertRequest(indexName, IdUtils.fastUUID()).source(source);
            request.add(indexRequest);
            //elasticSearch7Client.forceInsert("metricbeat-7.7.0-2020.06.24-000001",IdUtils.fastUUID(),source);
        }
        elasticSearch7Client.synchronousBulk(request);
    }

    @SneakyThrows
    public Map<String, Object> getBasicInfo(SNMPSessionUtil snmp) {
        Map<String, Object> basicInfo = null;
        basicInfo = new HashMap<>();
        String[] sysDesc = {SNMPConstants.SYSDESC};
        ArrayList<String> sysDescs = snmp.getSnmpGet(PDU.GET, sysDesc);
        String[] sysName = {SNMPConstants.SYSNAME};
        ArrayList<String> sysNames = snmp.getSnmpGet(PDU.GET, sysName);
        basicInfo.put("desc", sysDescs.get(0));
        basicInfo.put("hostname", sysNames.get(0));
        return basicInfo;
    }

    @SneakyThrows
    public void cpuMap(SNMPSessionUtil snmp, Map<String, Object> basicInfo, List<Map<String, Object>> esList) {
        Map<String, Object> source = this.metricbeatMap("cpu", basicInfo);
        String[] h3cCpu = {".1.3.6.1.4.1.25506.2.6.1.1.1.1.6.14"};
        ArrayList<String> cpuList = snmp.getSnmpGet(PDU.GET, h3cCpu);
        log.info("cpu使用率{}", JSON.toJSONString(cpuList));
        //五分钟平均利用率
        float total = new BigDecimal(cpuList.get(0)).divide(new BigDecimal(100)).floatValue();
        Map<String, Object> systemM = new HashMap<>();
        Map<String, Object> cpu = new HashMap<>();
        cpu.put("cores", 0f);
        Map<String, Object> idleEs = new HashMap<>();
        Map<String, Object> idleNorm = new HashMap<>();
        idleNorm.put("pct", 0f);
        idleEs.put("norm", idleNorm);
        idleEs.put("pct", 0f);
        cpu.put("idle", idleEs);

        Map<String, Object> iowaitEs = new HashMap<>();
        Map<String, Object> iowaitNorm = new HashMap<>();
        iowaitNorm.put("pct", 0f);
        iowaitEs.put("norm", iowaitNorm);
        iowaitEs.put("pct", 0f);
        cpu.put("iowait", iowaitEs);

        Map<String, Object> irqEs = new HashMap<>();
        Map<String, Object> irqNorm = new HashMap<>();
        irqNorm.put("pct", 0f);
        irqEs.put("norm", irqNorm);
        irqEs.put("pct", 0f);
        cpu.put("irq", irqEs);

        Map<String, Object> niceEs = new HashMap<>();
        Map<String, Object> niceNorm = new HashMap<>();
        niceNorm.put("pct", 0f);
        niceEs.put("norm", niceNorm);
        niceEs.put("pct", 0f);
        cpu.put("nice", niceEs);

        Map<String, Object> softirqEs = new HashMap<>();
        Map<String, Object> softirqNorm = new HashMap<>();
        softirqNorm.put("pct", 0f);
        softirqEs.put("norm", softirqNorm);
        softirqEs.put("pct", 0f);
        cpu.put("softirq", softirqEs);

        Map<String, Object> stealEs = new HashMap<>();
        Map<String, Object> stealNorm = new HashMap<>();
        stealNorm.put("pct", 0f);
        stealEs.put("norm", stealNorm);
        stealEs.put("pct", 0f);
        cpu.put("steal", stealEs);

        Map<String, Object> systemEs = new HashMap<>();
        Map<String, Object> systemNorm = new HashMap<>();
        systemNorm.put("pct", 0f);
        systemEs.put("norm", systemNorm);
        systemEs.put("pct", 0f);
        cpu.put("system", systemEs);


        Map<String, Object> userEs = new HashMap<>();
        Map<String, Object> userNorm = new HashMap<>();
        userNorm.put("pct", 0f);
        userEs.put("norm", userNorm);
        userEs.put("pct", 0f);
        cpu.put("user", userEs);

        Map<String, Object> totalEs = new HashMap<>();
        Map<String, Object> totalNorm = new HashMap<>();
        totalNorm.put("pct", total);
        totalEs.put("norm", totalNorm);
        totalEs.put("pct", total);
        cpu.put("total", totalEs);

        systemM.put("cpu", cpu);
        source.put("system", systemM);
        esList.add(source);
    }

    @SneakyThrows
    public void memoryMap(SNMPSessionUtil snmp, Map<String, Object> basicInfo, List<Map<String, Object>> esList) {
        Map<String, Object> source = this.metricbeatMap("memory", basicInfo);
        String[] h3cMem = {".1.3.6.1.4.1.25506.2.6.1.1.1.1.8.14"};//内存使用率
        ArrayList<String> memUsed = snmp.getSnmpGet(PDU.GET, h3cMem);
        log.info("内存使用率{}", JSON.toJSONString(memUsed));
        //String[] h3cMemBytes = {".1.3.6.1.4.1.25506.2.6.1.1.1.1.10.192"};//总内存
        //ArrayList<String> memTotal = snmp.getSnmpGet(PDU.GET, h3cMemBytes);
        BigDecimal usedRate = new BigDecimal(memUsed.get(0)).divide(new BigDecimal(100));
        //BigDecimal total = new BigDecimal(memTotal.get(0));
        BigDecimal total = new BigDecimal(0);
        BigDecimal used = total.multiply(usedRate);//MB/1024/1024
        BigDecimal free = total.subtract(used); //MB/1024/1024

        Map<String, Object> system = new HashMap<>();
        Map<String, Object> memory = new HashMap<>();
        Map<String, Object> actual = new HashMap<>();
        actual.put("free", free.longValue());
        Map<String, Object> actualUsed = new HashMap<>();
        actualUsed.put("bytes", used.longValue());
        actualUsed.put("pct", usedRate.floatValue());
        actual.put("used", actualUsed);
        memory.put("actual", actual);
        memory.put("free", free.longValue());

        Map<String, Object> hugepages = new HashMap<>();
        hugepages.put("default_size", 0l);
        hugepages.put("free", 0l);
        hugepages.put("reserved", 0l);
        hugepages.put("surplus", 0l);
        Map<String, Object> hugepagesSwap = new HashMap<>();
        Map<String, Object> hugepagesSwapOut = new HashMap<>();
        hugepagesSwapOut.put("fallback", 0l);
        hugepagesSwapOut.put("pages", 0l);
        hugepagesSwap.put("out", hugepagesSwapOut);
        hugepages.put("swap", hugepagesSwap);
        hugepages.put("total", 0l);
        Map<String, Object> hugepagesUsed = new HashMap<>();
        hugepagesUsed.put("bytes", 0l);
        hugepagesUsed.put("pct", 0l);
        hugepages.put("used", hugepagesUsed);
        memory.put("hugepages", hugepages);

        Map<String, Object> pageStats = new HashMap<>();
        Map<String, Object> pgfree = new HashMap<>();
        pgfree.put("pages", 0l);
        pageStats.put("pgfree", pgfree);
        Map<String, Object> pgscanDirect = new HashMap<>();
        pgscanDirect.put("pages", 0l);
        pageStats.put("pgscan_direct", pgscanDirect);
        Map<String, Object> pgscanKswapd = new HashMap<>();
        pgscanKswapd.put("pages", 0l);
        pageStats.put("pgscan_kswapd", pgscanKswapd);
        Map<String, Object> pgstealDirect = new HashMap<>();
        pgstealDirect.put("pages", 0l);
        pageStats.put("pgsteal_direct", pgstealDirect);
        Map<String, Object> pgstealKswapd = new HashMap<>();
        pgstealKswapd.put("pages", 0l);
        pageStats.put("pgsteal_kswapd", pgstealKswapd);
        memory.put("page_stats", pageStats);

        Map<String, Object> swap = new HashMap<>();
        swap.put("free", 0l);
        Map<String, Object> swapIn = new HashMap<>();
        swapIn.put("pages", 0l);
        swap.put("in", swapIn);
        Map<String, Object> swapOut = new HashMap<>();
        swapOut.put("pages", 0l);
        swap.put("out", swapOut);
        Map<String, Object> readahead = new HashMap<>();
        readahead.put("cached", 0l);
        readahead.put("pages", 0l);
        swap.put("readahead", readahead);
        swap.put("total", 0l);
        Map<String, Object> swapUsed = new HashMap<>();
        swapUsed.put("bytes", 0l);
        swapUsed.put("pct", 0l);
        swap.put("used", swapUsed);
        memory.put("swap", swap);
        memory.put("total", total.longValue());
        Map<String, Object> totalUsed = new HashMap<>();
        totalUsed.put("bytes", used.longValue());
        totalUsed.put("pct", usedRate.floatValue());
        memory.put("used", totalUsed);
        system.put("memory", memory);
        source.put("system", system);
        esList.add(source);

    }
/*
    @SneakyThrows
    public void cpuMap(SNMPSessionUtil snmp, Map<String, Object> basicInfo, List<Map<String, Object>> esList) {
        Map<String, Object> source = this.metricbeatMap("cpu", basicInfo);
        String[] h3cCpu = {".1.3.6.1.4.1.25506.2.6.1.1.1.1.6.192"};
        ArrayList<String> cpuList = snmp.getSnmpGet(PDU.GET, h3cCpu); //五分钟平均利用率
        float total = new BigDecimal(cpuList.get(0)).divide(new BigDecimal(100)).floatValue();
        Map<String, Object> systemM = new HashMap<>();
        Map<String, Object> cpu = new HashMap<>();
        cpu.put("cores", 0f);
        Map<String, Object> idleEs = new HashMap<>();
        Map<String, Object> idleNorm = new HashMap<>();
        idleNorm.put("pct", 0f);
        idleEs.put("norm", idleNorm);
        idleEs.put("pct", 0f);
        cpu.put("idle", idleEs);

        Map<String, Object> iowaitEs = new HashMap<>();
        Map<String, Object> iowaitNorm = new HashMap<>();
        iowaitNorm.put("pct", 0f);
        iowaitEs.put("norm", iowaitNorm);
        iowaitEs.put("pct", 0f);
        cpu.put("iowait", iowaitEs);

        Map<String, Object> irqEs = new HashMap<>();
        Map<String, Object> irqNorm = new HashMap<>();
        irqNorm.put("pct", 0f);
        irqEs.put("norm", irqNorm);
        irqEs.put("pct", 0f);
        cpu.put("irq", irqEs);

        Map<String, Object> niceEs = new HashMap<>();
        Map<String, Object> niceNorm = new HashMap<>();
        niceNorm.put("pct", 0f);
        niceEs.put("norm", niceNorm);
        niceEs.put("pct", 0f);
        cpu.put("nice", niceEs);

        Map<String, Object> softirqEs = new HashMap<>();
        Map<String, Object> softirqNorm = new HashMap<>();
        softirqNorm.put("pct", 0f);
        softirqEs.put("norm", softirqNorm);
        softirqEs.put("pct", 0f);
        cpu.put("softirq", softirqEs);

        Map<String, Object> stealEs = new HashMap<>();
        Map<String, Object> stealNorm = new HashMap<>();
        stealNorm.put("pct", 0f);
        stealEs.put("norm", stealNorm);
        stealEs.put("pct", 0f);
        cpu.put("steal", stealEs);

        Map<String, Object> systemEs = new HashMap<>();
        Map<String, Object> systemNorm = new HashMap<>();
        systemNorm.put("pct", 0f);
        systemEs.put("norm", systemNorm);
        systemEs.put("pct", 0f);
        cpu.put("system", systemEs);


        Map<String, Object> userEs = new HashMap<>();
        Map<String, Object> userNorm = new HashMap<>();
        userNorm.put("pct", 0f);
        userEs.put("norm", userNorm);
        userEs.put("pct", 0f);
        cpu.put("user", userEs);

        Map<String, Object> totalEs = new HashMap<>();
        Map<String, Object> totalNorm = new HashMap<>();
        totalNorm.put("pct", total);
        totalEs.put("norm", totalNorm);
        totalEs.put("pct", total);
        cpu.put("total", totalEs);

        systemM.put("cpu", cpu);
        source.put("system", systemM);
        esList.add(source);
    }
*/

   /* @SneakyThrows

    public void memoryMap(SNMPSessionUtil snmp, Map<String, Object> basicInfo, List<Map<String, Object>> esList) {
        Map<String, Object> source = this.metricbeatMap("memory", basicInfo);
        String[] h3cMem = {".1.3.6.1.4.1.25506.2.6.1.1.1.1.8.192"};//内存使用率
        ArrayList<String> memUsed = snmp.getSnmpGet(PDU.GET, h3cMem);
        String[] h3cMemBytes = {".1.3.6.1.4.1.25506.2.6.1.1.1.1.10.192"};//总内存
        ArrayList<String> memTotal = snmp.getSnmpGet(PDU.GET, h3cMemBytes);
        BigDecimal usedRate = new BigDecimal(memUsed.get(0)).divide(new BigDecimal(100));
        BigDecimal total = new BigDecimal(memTotal.get(0));
        BigDecimal used = total.multiply(usedRate);//MB/1024/1024
        BigDecimal free = total.subtract(used); //MB/1024/1024

        Map<String, Object> system = new HashMap<>();
        Map<String, Object> memory = new HashMap<>();
        Map<String, Object> actual = new HashMap<>();
        actual.put("free", free.longValue());
        Map<String, Object> actualUsed = new HashMap<>();
        actualUsed.put("bytes", used.longValue());
        actualUsed.put("pct", usedRate.floatValue());
        actual.put("used", actualUsed);
        memory.put("actual", actual);
        memory.put("free", free.longValue());

        Map<String, Object> hugepages = new HashMap<>();
        hugepages.put("default_size", 0l);
        hugepages.put("free", 0l);
        hugepages.put("reserved", 0l);
        hugepages.put("surplus", 0l);
        Map<String, Object> hugepagesSwap = new HashMap<>();
        Map<String, Object> hugepagesSwapOut = new HashMap<>();
        hugepagesSwapOut.put("fallback", 0l);
        hugepagesSwapOut.put("pages", 0l);
        hugepagesSwap.put("out", hugepagesSwapOut);
        hugepages.put("swap", hugepagesSwap);
        hugepages.put("total", 0l);
        Map<String, Object> hugepagesUsed = new HashMap<>();
        hugepagesUsed.put("bytes", 0l);
        hugepagesUsed.put("pct", 0l);
        hugepages.put("used", hugepagesUsed);
        memory.put("hugepages", hugepages);

        Map<String, Object> pageStats = new HashMap<>();
        Map<String, Object> pgfree = new HashMap<>();
        pgfree.put("pages", 0l);
        pageStats.put("pgfree", pgfree);
        Map<String, Object> pgscanDirect = new HashMap<>();
        pgscanDirect.put("pages", 0l);
        pageStats.put("pgscan_direct", pgscanDirect);
        Map<String, Object> pgscanKswapd = new HashMap<>();
        pgscanKswapd.put("pages", 0l);
        pageStats.put("pgscan_kswapd", pgscanKswapd);
        Map<String, Object> pgstealDirect = new HashMap<>();
        pgstealDirect.put("pages", 0l);
        pageStats.put("pgsteal_direct", pgstealDirect);
        Map<String, Object> pgstealKswapd = new HashMap<>();
        pgstealKswapd.put("pages", 0l);
        pageStats.put("pgsteal_kswapd", pgstealKswapd);
        memory.put("page_stats", pageStats);

        Map<String, Object> swap = new HashMap<>();
        swap.put("free", 0l);
        Map<String, Object> swapIn = new HashMap<>();
        swapIn.put("pages", 0l);
        swap.put("in", swapIn);
        Map<String, Object> swapOut = new HashMap<>();
        swapOut.put("pages", 0l);
        swap.put("out", swapOut);
        Map<String, Object> readahead = new HashMap<>();
        readahead.put("cached", 0l);
        readahead.put("pages", 0l);
        swap.put("readahead", readahead);
        swap.put("total", 0l);
        Map<String, Object> swapUsed = new HashMap<>();
        swapUsed.put("bytes", 0l);
        swapUsed.put("pct", 0l);
        swap.put("used", swapUsed);
        memory.put("swap", swap);
        memory.put("total", total.longValue());
        Map<String, Object> totalUsed = new HashMap<>();
        totalUsed.put("bytes", used.longValue());
        totalUsed.put("pct", usedRate.floatValue());
        memory.put("used", totalUsed);
        system.put("memory", memory);
        source.put("system", system);
        esList.add(source);

    }
*/
   @SneakyThrows
   public void temperatureMap(SNMPSessionUtil snmp, Map<String, Object> basicInfo, List<Map<String, Object>> esList) {
       try {
           Map<String, Object> source = this.metricbeatMap("temperature", basicInfo);
           String[] oid = {
                   ".1.3.6.1.4.1.25506.2.6.1.1.1.1.12.212", //name
           };
           ArrayList<String> list = snmp.getSnmpGet(PDU.GET, oid);
           source.put("temperature", new BigDecimal(String.valueOf(list.get(0))).longValue());
           esList.add(source);
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}
