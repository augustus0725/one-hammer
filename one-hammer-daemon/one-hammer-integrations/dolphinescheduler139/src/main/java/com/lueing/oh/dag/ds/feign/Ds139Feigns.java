package com.lueing.oh.dag.ds.feign;

import feign.Feign;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;

public class Ds139Feigns {
    public static Ds139Feign create(String host) {
        return Feign.builder()
                .encoder(new FormEncoder())
                .decoder(new GsonDecoder())
                .target(Ds139Feign.class, host);
    }
}
