package com.lueing.oh.app.api.vo.o;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author yesido0725@gmail.com
 * @date 2022/5/30 17:06
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class HelloVoOut {
    private String content;
}
