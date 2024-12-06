package com.essc.bigdata.bean;


import java.math.BigDecimal;

/**
 * @Description flume监控指标信息
 * @Author liuquanjun
 * @Date 2024/1/23
 */
public class FlumeInfo {
    // flume监控的采集类型 flume_type: [client | kafka_server | hdfs_server]
    private String flume_type;
    // ip:port
    private String ip;
    // ip:port/metrics 返回的 json 结果串
    private String json_report;
    // A|B 中心
    private String center_name;
    // source运行终止时间
    private Long source_stoptime;
    // channel运行终止时间
    private Long channel_stoptime;
    // sink运行终止时间
    private Long sink_stoptime;
    // channel空间使用率
    private BigDecimal channel_fill_percentage;
    // 目前为止source已经接收到的事件总数量
    private Long event_received_count;
    // 成功写入channel且提交的事件总次数
    private Long event_put_sucess_count;
    // sink成功写出到存储的事件总数量
    private Long event_drain_sucess_count;
    // 统计日期
    private String par_dt;
    // 统计所属小时
    private Integer par_hour;



}
