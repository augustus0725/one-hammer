package com.lueing.oh.app.controller;

import lombok.RequiredArgsConstructor;
import com.lueing.oh.app.api.Hello;
import com.lueing.oh.app.api.vo.i.HelloVoIn;
import com.lueing.oh.app.api.vo.o.HelloVoOut;
import com.lueing.oh.commons.annotation.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yesido0725@gmail.com
 * @date 2022/5/30 17:06
 */
@RestController
@RequiredArgsConstructor(onConstructor__ = {@Autowired})
public class HelloController {
    private final Hello hello;

    @PostMapping({"/hello"})
    @Loggable
    public HelloVoOut hello(@RequestBody HelloVoIn hvi) {
        return hello.say(hvi);
    }
}
