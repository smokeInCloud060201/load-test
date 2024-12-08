package com.learning.project.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJsonString(Object object) {
        String result = null;
        try {
            result = objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            System.out.println("Can't convert object to json string");
        }

        return result;
    }

}
