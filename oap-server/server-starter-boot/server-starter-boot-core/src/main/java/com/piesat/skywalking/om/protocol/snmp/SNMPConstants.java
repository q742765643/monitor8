package com.piesat.skywalking.om.protocol.snmp;

public class SNMPConstants {
    //获取系统基本信息
    public static final String SYSDESC = ".1.3.6.1.2.1.1.1.0";
    //监控时间
    public static final String SYSUPTIME = ".1.3.6.1.2.1.1.3.0";
    //主机名
    public static final String SYSNAME = ".1.3.6.1.2.1.1.5.0";

    //cpu system 百分比
    public static final String SSCPUSYSTEM = ".1.3.6.1.4.1.2021.11.10.0";
    //cpu user 百分比
    public static final String SSCPUUSER = ".1.3.6.1.4.1.2021.11.9.0";
    //cpu idle 百分比
    public static final String SSCPUIDLE = ".1.3.6.1.4.1.2021.11.11.0";


    //cpu rawuser 百分比
    public static final String SSCPURAWUSER = ".1.3.6.1.4.1.2021.11.50.0";
    //cpu rawnice 百分比
    public static final String SSCPURAWNICE = ".1.3.6.1.4.1.2021.11.51.0";
    //cpu rawsystem 百分比
    public static final String SSCPURAWSYSTEM = ".1.3.6.1.4.1.2021.11.52.0";
    //cpu rawidle 百分比
    public static final String SSCPURAWIDLE = ".1.3.6.1.4.1.2021.11.53.0";
    //cpu rawiowait 百分比
    public static final String SSCPURAWWAIT = ".1.3.6.1.4.1.2021.11.54.0";
    //cpu rawsoftirq 百分比
    public static final String SSCPURAWSOFTIRQ = ".1.3.6.1.4.1.2021.11.61.0";
    //cpu rawsteal 百分比
    public static final String SSCPURAWSTEAL = ".1.3.6.1.4.1.2021.11.64.0";
    //cpu num
    public static final String SSCPUNUM = ".1.3.6.1.2.1.25.3.3.1.2";

    //交换区总内存
    public static final String MEMTOTALSWAP = ".1.3.6.1.4.1.2021.4.3.0";
    //交换区可用内存
    public static final String MEMAVAILSWAP = ".1.3.6.1.4.1.2021.4.4.0";
    //实际总物理内存
    public static final String MEMTOTALREAL = ".1.3.6.1.4.1.2021.4.5.0";
    //实际可用物理内存
    public static final String MEMAVAILREAL = ".1.3.6.1.4.1.2021.4.6.0";
    //交换区总页数
    //public static final String MEMTOTALSWAPTXT = ".1.3.6.1.4.1.2021.4.7.0";
    //交换区可用页数
    //public static final String MEMAVAILSWAPTXT = ".1.3.6.1.4.1.2021.4.8.0";
    //总页数
    //public static final String MEMTOTALREALTXT = ".1.3.6.1.4.1.2021.4.9.0";
    //可用页数
    //public static final String MEMAVAILREALTXT = ".1.3.6.1.4.1.2021.4.10.0";
    //可用内存
    public static final String MEMTOTALFREE = ".1.3.6.1.4.1.2021.4.11.0";
    //内存大小
    public static final String HRMEMORYSIZE = ".1.3.6.1.2.1.25.2.2.0";

    public static final String MEMSHARED = ".1.3.6.1.4.1.2021.4.13.0";
    //内存buffer
    public static final String MEMBUFFER = ".1.3.6.1.4.1.2021.4.14.0";
    //内存缓存
    public static final String MEMCACHED = ".1.3.6.1.4.1.2021.4.15.0";

    //磁盘安装路径
    public static final String DSKPATH = "1.3.6.1.4.1.2021.9.1.2";
    //分区设备的路径
    public static final String DSKDEVICE = "1.3.6.1.4.1.2021.9.1.3";
    //磁盘/分区的总大小（千字节）
    public static final String DSKTOTAL = "1.3.6.1.4.1.2021.9.1.6";
    //磁盘上的可用空间
    public static final String DSKAVAIL = "1.3.6.1.4.1.2021.9.1.7";
    //磁盘上的已用空间
    public static final String DSKUSED = "1.3.6.1.4.1.2021.9.1.8";
    //磁盘上已使用空间的百分比
    public static final String DSKPERCENT = "1.3.6.1.4.1.2021.9.1.9";
    //磁盘上使用的inode百分比
    public static final String DSKPERCENTNODE = "1.3.6.1.4.1.2021.9.1.10";
}
