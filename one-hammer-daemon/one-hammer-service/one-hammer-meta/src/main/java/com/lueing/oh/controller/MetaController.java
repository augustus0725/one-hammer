package com.lueing.oh.controller;

import com.lueing.oh.commons.annotation.Loggable;
import com.lueing.oh.commons.exception.BusinessException;
import com.lueing.oh.commons.standard.RestResponse;
import com.lueing.oh.meta.api.HammerMetaException;
import com.lueing.oh.meta.api.Meta;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/meta")
@RequiredArgsConstructor(onConstructor__ = {@Autowired})
public class MetaController {
    private final Meta meta;
    @GetMapping("")
    @Loggable
    public RestResponse<Map<String, Integer>> meta(@RequestParam String identify, @RequestParam String table) {
        try {
            return RestResponse.ok(meta.meta(identify, table));
        } catch (HammerMetaException e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
