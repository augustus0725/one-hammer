package com.lueing.oh.dag.ds.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;

public class Ds139Escape {
    public static String escape(String value) {
        return escape0(escape0(value));
    }

    private static final Gson gson = new GsonBuilder().create();

    public static String escape0(String payload) {
        Payload content = new Payload(payload);
        String escapeValue = gson.toJson(content);
        return escapeValue.substring("{\"message\":".length() + 1, escapeValue.length() - 2);
    }

    @Data
    public static class Payload {
        public Payload(String message) {
            this.message = message;
        }
        private String message;
    }
}
