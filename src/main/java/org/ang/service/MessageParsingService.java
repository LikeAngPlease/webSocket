package org.ang.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.ang.handler.BusinessWebSocketHandler;
import org.springframework.stereotype.Component;

import java.util.List;


public class MessageParsingService {

    public static void messageParsing(String str, BusinessWebSocketHandler businessWebSocketHandler) {
        if ("1".equals(str)) {
            //心跳数据直接Return
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(str);
        String type = jsonObject.getString("type");
        String message = jsonObject.getString("message");
        List ids = JSONArray.parseArray(jsonObject.getString("send"));
        if ("0".equals(type) && ids != null && ids.size() > 0) {
            businessWebSocketHandler.sendToTextWebSocketFrame(message, ids);
        }

    }
}
