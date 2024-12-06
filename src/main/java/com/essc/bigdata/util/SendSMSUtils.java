package com.essc.bigdata.util;

import com.essc.bigdata.config.ConfigReader;
import com.essc.bigdata.constants.Constants;
import com.essc.bigdata.entity.HttpClientResult;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpHead;
import org.slf4j.Logger;

import javax.xml.ws.spi.http.HttpHandler;
import java.util.HashMap;

/**
 * 发送短信接口工具类
 */
public class SendSMSUtils {

    /**
     * @param content
     * @return
     */
    public static String SendMS2(String content, Logger logger, String receiverParams) {
        HashMap<String, String> headerMap = new HashMap<>();
        HashMap<String, String> paramsMap = new HashMap<>();
//        Map<String, Object> paramsMap = new HashMap<>();
        // 请求头
        headerMap.put(Constants.MESSAGE_APPID, ConfigReader.getString(Constants.MESSAGE_APPID));
        headerMap.put(Constants.MESSAGE_SIGNINFO, ConfigReader.getString(Constants.MESSAGE_SIGNINFO));
        headerMap.put(Constants.MESSAGE_CONTENT_TYPE, ConfigReader.getString(Constants.MESSAGE_CONTENT_TYPE));
//        headerMap.put("Content-Type","text/html;charset=UTF-8");
        // 请求参数
        paramsMap.put(Constants.MESSAGE_CODE, ConfigReader.getString(Constants.MESSAGE_CODE));
        paramsMap.put(Constants.MESSAGE_CONTENT_PARAMS, content);
        paramsMap.put(Constants.MESSAGE_TITLE_PARAMS, ConfigReader.getString(Constants.MESSAGE_TITLE_PARAMS));
        paramsMap.put(Constants.MESSAGE_RECEIVER_PARAMS, receiverParams);
        paramsMap.put(Constants.MESSAGE_SUB_TYPE, ConfigReader.getString(Constants.MESSAGE_SUB_TYPE));

        try {
            logger.info("requestURL: " + ConfigReader.getString(Constants.MESSAGE_REQUEST_URL));
            logger.info("headerMap: " + headerMap);
            logger.info("paramsMap: " + paramsMap);
//            return HttpUtil.createPost(ConfigReader.getString(Constants.MESSAGE_REQUEST_URL)).addHeaders(headerMap).form(paramsMap).execute().body();
            return HttpClientUtils.doPostNew(
                    ConfigReader.getString(Constants.MESSAGE_REQUEST_URL),
                    headerMap,
                    paramsMap
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




//    public static boolean sendV2Sms(String content) {
//        boolean flag = true;
//        //执行发送短信
//        try {
//
//            OkHttpClient client = new OkHttpClient().newBuilder().build();
//            MediaType mediaType = MediaType.parse("application/json");
//            JSONObject obj = new JSONObject();
//            obj.put(Constants.MESSAGE_CODE, ConfigReader.getString(Constants.MESSAGE_CODE));
//            obj.put(Constants.MESSAGE_CONTENT_PARAMS, content);
//            obj.put(Constants.MESSAGE_TITLE_PARAMS, ConfigReader.getString(Constants.MESSAGE_TITLE_PARAMS));
//            obj.put(Constants.MESSAGE_RECEIVER_PARAMS,ConfigReader.getString(Constants.MESSAGE_RECEIVER_PARAMS));
//
//            RequestBody body = RequestBody.create(mediaType, obj.toJSONString());
//            Request request = new Request.Builder()
//                    .url(ConfigReader.getString(Constants.MESSAGE_REQUEST_URL))
//                    .method("POST", body)
//                    .addHeader("appId", ConfigReader.getString(Constants.MESSAGE_APPID))
//                    .addHeader("signInfo", "")
//                    .addHeader("Content-Type", "application/json").build();
//            Response response = client.newCall(request).execute();
//        }catch (Exception e){
//            e.printStackTrace();
//            flag = false;
//        }
//        return flag;
//    }

}
