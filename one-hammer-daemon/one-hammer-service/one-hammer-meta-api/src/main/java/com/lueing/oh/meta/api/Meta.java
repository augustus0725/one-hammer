package com.lueing.oh.meta.api;

import java.util.Map;

public interface Meta {
    Map<String, Integer> meta(String sourceId, String table) throws HammerMetaException;
}
