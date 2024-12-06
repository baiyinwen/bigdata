package com.essc.bigdata.constants;

public class Constants {
    /**
     * 短信告警相关常量
     */
    public final static String MESSAGE_CODE = "messageCode";
    public final static String MESSAGE_SUB_TYPE = "messageSubType";
    public final static String MESSAGE_TITLE_PARAMS = "titleParams";
    public final static String MESSAGE_APPID = "appID";
    public final static String MESSAGE_SIGNINFO = "signInfo";
    public final static String MESSAGE_REQUEST_URL = "requestURL";
    public final static String MESSAGE_CONTENT_PARAMS = "contentParams";
    public final static String MESSAGE_RECEIVER_PARAMS = "receiverParams";
    public final static String MESSAGE_CONTENT_TYPE = "Content-Type";

    /**
     * flume配置文件相关参数
     */
    public final static String FLUME_CLIENT_SOURCE = "client_source";
    public final static String FLUME_CLIENT_CHANNEL = "client_channel";
    public final static String FLUME_CLIENT_SINK = "client_sink";
    public final static String FLUME_HDFS_SOURCE = "hdfs_source";
    public final static String FLUME_HDFS_CHANNEL = "hdfs_channel";
    public final static String FLUME_HDFS_SINK = "hdfs_sink";
    public final static String FLUME_KAFKA_SOURCE = "kafka_source";
    public final static String FLUME_KAFKA_CHANNEL = "kafka_channel";
    public final static String FLUME_KAFKA_SINK = "kafka_sink";

    /**
     * flume监控相关参数
     */
    public final static String FLUME_MONITOR_JOB_NAME = "jobName";
    public final static String FLUME_MONITOR_SCHEDULE_TIME = "scheduleTime";
    public final static String FLUME_MONITOR_IP1 = "monitorIp1";
    public final static String FLUME_MONITOR_IP2 = "monitorIp2";
    public final static String FLUME_MONITOR_IP3 = "monitorIp3";
    // channel已填入的百分比监控阈值
    public final static String FLUME_CHANNEL_FILL_PERCENTAGE_MONITOR = "ChannelFillPercentageMonitor";
    // source|channel|sink停止时的毫秒值时间，为0表示一直在运行
    public final static String FLUME_STOPTIME = "StopTime";

    // source:
    // 接收到事件批次的总数量
    public final static String FLUME_SOURCE_APPEND_BATCH_RECEIVED_COUNT = "AppendBatchReceivedCount";
    // 成功提交到channel的批次的总数量
    public final static String FLUME_SOURCE_APPEND_BATCH_ACCEPTED_COUNT = "AppendBatchAcceptedCount";
    // source端成功收到的event数量
    public final static String FLUME_SOURCE_EVENT_RECEIVED_COUNT = "EventReceivedCount";
    // 成功写出到channel的事件总数量
    public final static String FLUME_SOURCE_EVENT_ACCEPTED_COUNT = "EventAcceptedCount";

    // channel:
    // channel已填入的百分比
    public final static String FLUME_CHANNEL_FILL_PERCENTAGE = "ChannelFillPercentage";
    // Source尝试写入Channe的事件总次数
    public final static String FLUME_CHANNEL_EVENT_PUT_ATTEMPT_COUNT = "EventPutAttemptCount";
    // 成功写入channel且提交的事件总次数
    public final static String FLUME_CHANNEL_EVENT_PUT_SUCCESS_COUNT = "EventPutSuccessCount";
    // sink尝试从channel拉取事件的总次数
    public final static String FLUME_CHANNEL_EVENT_TAKE_ATTEMPT_COUNT = "EventTakeAttemptCount";
    // sink成功从channel读取事件的总数量
    public final static String FLUME_CHANNEL_EVENT_TAKE_SUCCESS_COUNT = "EventTakeSuccessCount";

    // sink:
    // sink尝试写出到存储的事件总数量
    public final static String FLUME_SINK_EVENT_DRAIN_ATTEMPT_COUNT = "EventDrainAttemptCount";
    // sink成功写出到存储的事件总数量
    public final static String FLUME_SINK_EVENT_DRAIN_SUCCESS_COUNT = "EventDrainSuccessCount";
    // 批量处理event的个数为0的数量-表示source写入数据的速度比sink处理数据的速度慢
    public final static String FLUME_SINK_BATCH_EMPTY_COUNT = "BatchEmptyCount";
    // 批量处理event的个数小于批处理大小的数量
    public final static String FLUME_SINK_BATCH_UNDERFLOW_COUNT = "BatchUnderflowCount";
    // 批量处理event的个数等于批处理大小的数量
    public final static String FLUME_SINK_BATCH_COMPLETE_COUNT = "BatchCompleteCount";

    /**
     * 告警信息
     */
    public final static String EXCEPTION_MSG_1 = "exception1";
    public final static String EXCEPTION_MSG_2 = "exception2";
    public final static String EXCEPTION_MSG_3 = "exception3";
    public final static String EXCEPTION_MSG_4 = "exception4";
    public final static String EXCEPTION_MSG_5 = "exception5";
    public final static String EXCEPTION_MSG_6 = "exception6";
    public final static String EXCEPTION_MSG_7 = "exception7";
    public final static String EXCEPTION_MSG_8 = "exception8";
    public final static String FLUME_TABLE = "tableName";

    // Gbase配置
    public final static String GBASE_JDBC_DRIVER = "gbase.jdbc.driver";
    public final static String GBASE_JDBC_POOL_SIZE = "gbase.jdbc.pool.size";
    public final static String GBASE_JDBC_URL = "gbase.jdbc.url";
    public final static String GBASE_JDBC_USER = "gbase.jdbc.user";
    public final static String GBASE_JDBC_PASSWORD = "gbase.jdbc.pwd";

    /*TBase参数*/
    public final static String TBASE_JDBC_DRIVER = "org.postgresql.Driver";//TBase数据库驱动
    public final static String TBASE_JDBC_URL = "tbase.jdbc.url";
    public final static String TBASE_JDBC_USER = "tbase.jdbc.user";
    public final static String TBASE_JDBC_PD = "tbase.jdbc.pwd";


}
