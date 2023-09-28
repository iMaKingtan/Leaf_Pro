package com.generate.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @DESCRIPTION: Json转化工具类,避免在不受spring管理的bean中无法使用的弊端
 * @CREATED by: iMaKingtan
 * @DATE: 2022/10/30 21:38
 */
public class JsonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    static {
        // 指定日期格式
        JSON_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 配置遇到不识别的字段自动忽略
        JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,false);
    }
    /**
     * @param o
     * @return
     * @throws JsonProcessingException
     * @apiNote 对象转json
     */
    public static String objectToJson(Object o) {
        if (o == null) {
            LOGGER.info("Jackson:object is null");
            return null;
        }
        String res = null;
        try {
            res = JSON_MAPPER.writeValueAsString(o);
            return res;
        } catch (JsonProcessingException e) {
            LOGGER.error("json转换出错:{}",o);
        }
        return null;
    }


    /**
     * @param json
     * @param className
     * @param <T>
     * @return
     * @throws IOException
     * @apiNote json转对象
     */
    public static <T> T jsonToObject(String json, Class<T> className){
        if (json == null || json.length() < 1) {
            return null;
        }
        T res = null;
        try {
            res = JSON_MAPPER.readValue(json, className);
        } catch (JsonProcessingException e) {
            LOGGER.error("json convert to:{} fails",className);
        }
        return res;
    }


    /**
     * @param content
     * @param className
     * @param <T>
     * @return
     * @throws IOException
     * @apiNote byte[]转对象
     */
    public static <T> T utf8BytesToBean(byte[] content, Class<T> className) throws IOException {
        if (null == content) {
            return null;
        }
        return JSON_MAPPER.readValue(new String(content, "utf-8"), className);
    }

    /**
     * @param json
     * @param typeReference
     * @param <T>
     * @return
     * @throws IOException
     * @apiNote json转集合类型
     */
    public static <T> T jsonToObject(String json, TypeReference<T> typeReference) throws IOException {
        if (json == null || json.length() < 1) {
            return null;
        }
        return (T) JSON_MAPPER.readValue(json, typeReference);
    }

    /**
     * @param json
     * @param node
     * @return
     * @throws IOException
     * @apiNote 获取json中注定的字段值
     */
    public static String filter(String json, String node) throws IOException {
        JsonNode jsonNode = JSON_MAPPER.readTree(json);
        return jsonNode.get(node).toString();
    }
}
