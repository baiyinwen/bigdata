package com.essc.bigdata;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.essc.bigdata.config.ConfigReader;
import com.essc.bigdata.constants.Constants;
import com.essc.bigdata.entity.HttpClientResult;
import com.essc.bigdata.util.HttpClientUtils;
import com.essc.bigdata.util.SendSMSUtils;
import com.essc.bigdata.util.TBaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

/**
 * @author liu.yb
 * 从配置表中读取flume监控的采集类型[flume_type],监听ip及端口[ip],source配置,channel配置,sink配置等相关信息
 */
public class FlumeMonitorApplication {
    private static final Logger logger = LoggerFactory.getLogger(FlumeMonitorApplication.class);
    private static TBaseUtils tBaseUtils = TBaseUtils.getInstance();
    private static Set<String> keys; //每条数据对应的字段名
    private static Collection<Object> values;

    public static void main(String[] args) {
        if (args.length != 2) {
            logger.error("传入需要监控的中心名称：[A|B]");
            logger.error("传入运行所属时间：YYYYMMDDHHmm");
            System.exit(1);
        }
        String centerName = args[0];
        String date = args[1];
        logger.info("传入的参数 centerName 为: " + centerName + "---date为: " + date);
        try {
            flumeMonitorTask(centerName, date);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            System.exit(-1);
        }
    }

    private static void flumeMonitorTask(String centerName, String date) {
        String selectSql = "select * from flume_monitor_config where center_name = '" + centerName + "'";
        final ArrayList<HashMap<String, String>> hashMaps = new ArrayList<>();
        tBaseUtils.executeQuery(selectSql, null, new TBaseUtils.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                if (rs != null){
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    while (rs.next()) {
                        HashMap<String, String> flumeConfig = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i);
                            String value = rs.getString(i);
                            flumeConfig.put(columnName, value);
                        }
                        hashMaps.add(flumeConfig);
                    }
                } else {
                    throw new Exception("配置表配置有问题，请检查配置表配置<flume_type> is client|kafka_server|hdfs_server");
                }
            }
        });

        // 确保能够重跑，删除本次运行的结果数据
        String flumeInfoDelSql = "delete from flume_monitor_info where par_dt = '" + date.substring(0, 8) + "' and par_hour = '"
                + date.substring(8, 10) + "'   and par_min = '" + date.substring(10, 12) + "' and center_name = '" + centerName + "'";
        logger.info("执行Delete: " + flumeInfoDelSql);
        tBaseUtils.executeQuery(flumeInfoDelSql, null, null);

        for (HashMap<String, String> hashMap : hashMaps) {
            String[] ipArray = hashMap.get("ip").split(",");
            String flume_type = hashMap.get("flume_type");
            StringBuilder exceptionMsg = new StringBuilder();
            for (String ip : ipArray) {
                //监控指标数据分析
                logger.info("本次监控flume类型及IP: " + flume_type + ", " + ip);
                metricsAnalysis(ip, flume_type, centerName, exceptionMsg, hashMap, date);
            }
            //存在告警信息即触发告警 **每种监控类型输出一条短信**
            if (exceptionMsg.length() > 0) {
                exceptionMsg.insert(0, "flume类型：" + flume_type + "|");
                String content = SendSMSUtils.SendMS2(exceptionMsg.substring(0,65), logger, hashMap.get("author"));
                logger.info("http响应结果: {}", content);
                logger.info("本次监控报错内容: " + exceptionMsg);
            }
        }

        logger.info("flume监控运行完成...");
    }

    private static void metricsAnalysis(String ip, String flume_type, String centerName, StringBuilder exceptionMsg, HashMap<String, String> flumeConfig, String date) {
        long sourceStopTime = 0L;
        long channelStopTime = 0L;
        long sinkStopTime = 0L;
        ArrayList<String> channelPercentages = new ArrayList<>();
        long eventReceivedCounts = 0L;
        long eventAcceptedCounts = 0L;
        long appendBatchAcceptedCount = 0L;
        long appendBatchReceivedCount = 0L;
        long eventPutAttemptCount = 0L;
        long eventPutSuccessCount = 0L;
        long eventTakeAttemptCount = 0L;
        long eventTakeSuccessCount = 0L;
        long eventDrainAttemptCount = 0L;
        long eventDrainSuccessCount = 0L;
        // 获取[source|channel|sink]对应配置文件的名称
        String flume_source_type = flumeConfig.get("source");
        String flume_channel_type = flumeConfig.get("channel");
        String flume_sink_type = flumeConfig.get("sink");

        try {
            HttpClientResult httpClientResult = HttpClientUtils.doGet("http://" + ip + "/metrics");
            String jsonReporting = httpClientResult.getContent();
            logger.info("ip:{} ,flume 运行指标数据：{}", ip, jsonReporting);
            if (!jsonReporting.isEmpty()) {
                JSONObject jsonObject = JSON.parseObject(jsonReporting);

                // source指标
                assert flume_source_type != null;
                String[] sources = flume_source_type.split(",");
                for (String source : sources) {
                    JSONObject sourceJson = jsonObject.getJSONObject(source);
                    // source|channel|sink停止时的毫秒值时间，为0表示一直在运行
                    long sourceStopTime1 = sourceJson.getLongValue(Constants.FLUME_STOPTIME);
                    logger.info(source + "<停止时的毫秒值时间>:" + sourceStopTime1);
                    sourceStopTime += sourceStopTime1;
                    // source端成功收到的event数量
                    long sourceEventReceivedCounts = sourceJson.getLongValue(Constants.FLUME_SOURCE_EVENT_RECEIVED_COUNT);
                    logger.info(source + "<source端成功收到的event数量>:" + sourceEventReceivedCounts);
                    eventReceivedCounts += sourceEventReceivedCounts;
                    // source端成功写出到channel的事件总数量
                    long sourceEventAcceptedCounts = sourceJson.getLongValue(Constants.FLUME_SOURCE_EVENT_ACCEPTED_COUNT);
                    logger.info(source + "<source端成功写出到channel的事件总数量>:" + sourceEventAcceptedCounts);
                    eventAcceptedCounts += sourceEventAcceptedCounts;
                    // 接收到事件批次的总数量
                    long sourceAppendBatchReceivedCount = sourceJson.getLongValue(Constants.FLUME_SOURCE_APPEND_BATCH_RECEIVED_COUNT);
                    logger.info(source + "<接收到事件批次的总数量>:" + sourceAppendBatchReceivedCount);
                    appendBatchReceivedCount += sourceAppendBatchReceivedCount;
                    // 成功提交到channel的批次的总数量
                    long sourceAppendBatchAcceptedCount = sourceJson.getLongValue(Constants.FLUME_SOURCE_APPEND_BATCH_ACCEPTED_COUNT);
                    logger.info(source + "<成功提交到channel的批次的总数量>:" + sourceAppendBatchAcceptedCount);
                    appendBatchAcceptedCount += sourceAppendBatchAcceptedCount;
                }

                // channel指标
                String[] channels = flume_channel_type.split(",");
                for (String channel : channels) {
                    JSONObject channelJson = jsonObject.getJSONObject(channel);
                    // channel已填入的百分比
                    String channelFillPercentage = channelJson.getString(Constants.FLUME_CHANNEL_FILL_PERCENTAGE);
                    logger.info(channel + "<channel已填入的百分比>:" + channelFillPercentage);
                    channelPercentages.add(channelFillPercentage);
                    // source|channel|sink停止时的毫秒值时间，为0表示一直在运行
                    long channelStopTime1 = channelJson.getLongValue(Constants.FLUME_STOPTIME);
                    logger.info(channel + "<channel端停止时的毫秒值时间>:" + channelStopTime1);
                    channelStopTime += channelStopTime1;
                    // Source尝试写入Channe的事件总次数
                    long channelEventPutAttemptCount = channelJson.getLongValue(Constants.FLUME_CHANNEL_EVENT_PUT_ATTEMPT_COUNT);
                    logger.info(channel + "<Source尝试写入Channe的事件总次数>:" + channelEventPutAttemptCount);
                    eventPutAttemptCount += channelEventPutAttemptCount;
                    // 成功写入channel且提交的事件总次数
                    long channelEventPutSuccessCount = channelJson.getLongValue(Constants.FLUME_CHANNEL_EVENT_PUT_SUCCESS_COUNT);
                    logger.info(channel + "<成功写入channel且提交的事件总次数>:" + channelEventPutSuccessCount);
                    eventPutSuccessCount += channelEventPutSuccessCount;
                    // sink尝试从channel拉取事件的总次数
                    long channelEventTakeAttemptCount = channelJson.getLongValue(Constants.FLUME_CHANNEL_EVENT_TAKE_ATTEMPT_COUNT);
                    logger.info(channel + "<sink尝试从channel拉取事件的总次数>:" + channelEventTakeAttemptCount);
                    eventTakeAttemptCount += channelEventTakeAttemptCount;
                    // sink成功从channel读取事件的总数量
                    long channelEventTakeSuccessCount = channelJson.getLongValue(Constants.FLUME_CHANNEL_EVENT_TAKE_SUCCESS_COUNT);
                    logger.info(channel + "<sink成功从channel读取事件的总数量>:" + channelEventTakeSuccessCount);
                    eventTakeSuccessCount += channelEventTakeSuccessCount;
                }

                // sink指标
                String[] sinks = flume_sink_type.split(",");
                for (String sink : sinks) {
                    JSONObject sinkJson = jsonObject.getJSONObject(sink);
                    // source|channel|sink停止时的毫秒值时间，为0表示一直在运行
                    long sinkStopTime1 = sinkJson.getLongValue(Constants.FLUME_STOPTIME);
                    logger.info(sink + "<sink端停止时的毫秒值时间>:" + sinkStopTime1);
                    sinkStopTime += sinkStopTime1;
                    // sink尝试写出到存储的事件总数量
                    long sinkEventDrainAttemptCount = sinkJson.getLongValue(Constants.FLUME_SINK_EVENT_DRAIN_ATTEMPT_COUNT);
                    logger.info(sink + "<sink尝试写出到存储的事件总数量>:" + sinkEventDrainAttemptCount);
                    eventDrainAttemptCount += sinkEventDrainAttemptCount;
                    // sink成功写出到存储的事件总数量
                    long sinkEventDrainSuccessCount = sinkJson.getLongValue(Constants.FLUME_SINK_EVENT_DRAIN_SUCCESS_COUNT);
                    logger.info(sink + "<sink成功写出到存储的事件总数量>:" + sinkEventDrainSuccessCount);
                    eventDrainSuccessCount += sinkEventDrainSuccessCount;
                }

                //汇总告警信息
                //1. 监控指标1：source|channel|sink stopTime不为0
                logger.info("source运行终止时间: " + sourceStopTime);
                logger.info("channel运行终止时间: " + channelStopTime);
                logger.info("sink运行终止时间: " + sinkStopTime);
                if (sourceStopTime != 0) {
                    setIPType(exceptionMsg, ip);
                    exceptionMsg.append("flume进程<source>").append(ConfigReader.getString(Constants.EXCEPTION_MSG_8)).append("|");
                }
                if (channelStopTime != 0) {
                    setIPType(exceptionMsg, ip);
                    exceptionMsg.append("flume进程<channel>").append(ConfigReader.getString(Constants.EXCEPTION_MSG_8)).append("|");
                }
                if (sinkStopTime != 0) {
                    setIPType(exceptionMsg, ip);
                    exceptionMsg.append("flume进程<sink>").append(ConfigReader.getString(Constants.EXCEPTION_MSG_8)).append("|");
                }
                //2. 监控指标2：channel空间使用率是否超过90%
                BigDecimal channelFps = new BigDecimal(ConfigReader.getString(Constants.FLUME_CHANNEL_FILL_PERCENTAGE_MONITOR));
                logger.info("channel空间使用率配置阈值: " + channelFps);
                for (String channelPercentage : channelPercentages) {
                    BigDecimal channelFillPercentage = new BigDecimal(channelPercentage);
                    logger.info("channel空间使用率: " + channelFillPercentage);
                    if (channelFps.compareTo(channelFillPercentage) < 0) {
                        setIPType(exceptionMsg, ip);
                        exceptionMsg.append(ConfigReader.getString(Constants.EXCEPTION_MSG_2)).append("|");
                    }
                }
                //3. 监控指标3：目前为止source已经接收到的事件总数量
                //4. 监控指标4：成功写入channel且提交的事件总次数
                //4. 监控指标5：sink成功写出到存储的事件总数量
                logger.info("目前为止source已经接收到的事件总数量: " + eventReceivedCounts);
                logger.info("成功写入channel且提交的事件总次数: " + eventPutSuccessCount);
                logger.info("sink成功写出到存储的事件总数量: " + eventDrainSuccessCount);
                // Client端数据源由于使用了自定义的sls数据源，flume监控不到此指标，计数不计入监控比较
                if (!flume_type.equalsIgnoreCase("CLIENT")) {
                    if (eventReceivedCounts == 0) {
                        setIPType(exceptionMsg, ip);
                        exceptionMsg.append(ConfigReader.getString(Constants.EXCEPTION_MSG_3)).append("|");
                    }
                }
                if (eventPutSuccessCount == 0) {
                    setIPType(exceptionMsg, ip);
                    exceptionMsg.append(ConfigReader.getString(Constants.EXCEPTION_MSG_6)).append("|");
                }
                if (eventDrainSuccessCount == 0) {
                    setIPType(exceptionMsg, ip);
                    exceptionMsg.append(ConfigReader.getString(Constants.EXCEPTION_MSG_7)).append("|");
                }

                // 写入监控指标结果表的信息
                JSONObject flumeInfoMap = new JSONObject();
                flumeInfoMap.put("flume_type", flume_type);
                flumeInfoMap.put("ip", ip);
                flumeInfoMap.put("json_report", jsonReporting);
                flumeInfoMap.put("center_name", centerName);
                flumeInfoMap.put("source_stoptime", sourceStopTime);
                flumeInfoMap.put("channel_stoptime", channelStopTime);
                flumeInfoMap.put("sink_stoptime", sinkStopTime);
                flumeInfoMap.put("channel_fill_percentage", formatToTwoDecimalPlaces(channelPercentages.get(0)));
                flumeInfoMap.put("event_received_count", eventReceivedCounts);
                flumeInfoMap.put("event_put_success_count", eventPutSuccessCount);
                flumeInfoMap.put("event_drain_success_count", eventDrainSuccessCount);
                flumeInfoMap.put("par_dt", date.substring(0, 8));
                flumeInfoMap.put("par_hour", date.substring(8, 10));
                flumeInfoMap.put("par_min", date.substring(10, 12));

                // 写入tbase监控结果
                executeInsertSql(ConfigReader.getString(Constants.FLUME_TABLE), flumeInfoMap);

            } else {
                logger.error("IP:" + ip + ", " + " flume进程未监控到...");
                setIPType(exceptionMsg, ip);
                exceptionMsg.append(ConfigReader.getString(Constants.EXCEPTION_MSG_1)).append("|");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            setIPType(exceptionMsg, ip);
            exceptionMsg.append(ConfigReader.getString(Constants.EXCEPTION_MSG_4)).append("|");
        }
    }

    // 标识所属IP信息
    private static void setIPType(StringBuilder exceptionMsg, String ip) {
        if (!exceptionMsg.toString().contains(ip)) {
            exceptionMsg.append("IP:" + ip + "|");
        }
    }

    // 构建INSERT SQL语句并执行
    private static void executeInsertSql(String tableName, JSONObject dataJsonObj) {
        keys = dataJsonObj.keySet();
        values = dataJsonObj.values();
        // 替换values中的单引号为空字符串
        List<String> sanitizedValues = new ArrayList<>();
        for (Object value : values) {
            sanitizedValues.add(value.toString().replaceAll("'", ""));
        }
        String insertSql = "insert into " + tableName
                + " (" + StringUtils.join(keys, ",") + ") " +
                "values" +
                "('" + StringUtils.join(sanitizedValues, "','") + "')";

        logger.info("执行Insert: " + insertSql);
        tBaseUtils.executeUpdateSql(insertSql);
    }

    // 截取两位小数
    private static String formatToTwoDecimalPlaces(String input) {
        try {
            BigDecimal number = new BigDecimal(input);
            return number.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        } catch (NumberFormatException e) {
            return String.valueOf(0);
        }
    }
}
