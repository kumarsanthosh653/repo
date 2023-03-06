package com.ozonetel.occ.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

/**
 *
 * @author pavanj
 */
public class JsonUtil<T> {

    public String convertToJson(T obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public T convertFromJson(String jsonString, Class<T> serializeClass) {
        try {
            return (T) mapper.readValue(jsonString, serializeClass);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    private static final ObjectMapper mapper = new ObjectMapper();
    private static Logger logger = Logger.getLogger(JsonUtil.class);
}
