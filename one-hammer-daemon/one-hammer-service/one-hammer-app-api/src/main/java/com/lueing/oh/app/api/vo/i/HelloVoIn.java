package com.lueing.oh.app.api.vo.i;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class HelloVoIn {
    private String content;
}
