package com.lueing.oh.tushare.feign;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Need token")
public class TushareFeignTest {
    @Test
    public void testBasic() {
        TushareFeign tushare = Feign.builder()
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .target(TushareFeign.class, "http://api.tushare.pro");

        TushareFeign.StockBasicResponse response = tushare.stockBasic(TushareFeign.StockBasicRequest.builder()
                .apiName("stock_basic")
                .fields("ts_code,symbol,name,area,industry,list_date")
                .token("********")
                .params(TushareFeign.StockBasicParams.builder()
                        .listStatus("L")
                        .build())
                .build());
    }

}
