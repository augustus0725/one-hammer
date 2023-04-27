package com.lueing.oh.meta;

import com.lueing.oh.meta.api.Meta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor__ = {@Autowired})
@Slf4j
public class MetaImpl implements Meta {
    @Override
    public Map<String, Integer> meta(String identify, String table) {
        return null;
    }
}
