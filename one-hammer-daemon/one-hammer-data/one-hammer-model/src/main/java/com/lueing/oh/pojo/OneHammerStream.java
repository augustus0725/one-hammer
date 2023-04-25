package com.lueing.oh.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
public class OneHammerStream {
    private String name;
    private String description;
    private String template;
    private List<String> command;
    private List<String> args;
    private List<Pair> env;
    private Resources resources;
    private String deploymentId;

    @Setter
    @Getter
    @NoArgsConstructor
    @SuperBuilder
    public static class Resources {
        private Limits limits;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @SuperBuilder
    public static class Limits {
        private String cpu;
        private String memory;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @SuperBuilder
    public static class Pair {
        private String name;
        private String value;
    }
}
