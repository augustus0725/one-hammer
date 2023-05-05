package com.lueing.oh.tushare.feign;

import com.google.gson.annotations.SerializedName;
import feign.RequestLine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

public interface TushareFeign {
    @RequestLine("POST /")
    StockBasicResponse stockBasic(StockBasicRequest request);

    @Setter
    @Getter
    @NoArgsConstructor
    @SuperBuilder
    class StockBasicDataResponse {
        private List<String> fields;
        private List<List<String>> items;
    }
/*
    // 返回字段说明

    名称	类型	默认显示	描述
    ts_code	str	Y	TS代码
    symbol	str	Y	股票代码
    name	str	Y	股票名称
    area	str	Y	地域
    industry	str	Y	所属行业
    fullname	str	N	股票全称
    enname	str	N	英文全称
    cnspell	str	N	拼音缩写
    market	str	Y	市场类型（主板/创业板/科创板/CDR）
    exchange	str	N	交易所代码
    curr_type	str	N	交易货币
    list_status	str	N	上市状态 L上市 D退市 P暂停上市
    list_date	str	Y	上市日期
    delist_date	str	N	退市日期
    is_hs	str	N	是否沪深港通标的，N否 H沪股通 S深股通
*/

    @Setter
    @Getter
    @NoArgsConstructor
    @SuperBuilder
    class StockBasicResponse {
        @SerializedName("request_id")
        private String requestId;
        private int code;
        private String msg;

        private StockBasicDataResponse data;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @SuperBuilder
    class StockBasicRequest {
        @SerializedName("api_name")
        private String apiName;
        private String token;
        private StockBasicParams params;
        private String fields;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @SuperBuilder
    class StockBasicParams {
        @SerializedName("is_hs")
        private String isHs;     /* 非必选 是否沪深港通标的，N否 H沪股通 S深股通 */
        @SerializedName("list_status")
        private String listStatus; /* 非必选 是否沪深港通标的，N否 H沪股通 S深股通 */
        private String exchange; /* 非必选 交易所 SSE上交所 SZSE深交所 BSE北交所 */
        @SerializedName("ts_code")
        private String tsCode; /* 非必选 TS股票代码 */
        private String market; /* 非必选 市场类别 （主板/创业板/科创板/CDR/北交所） */
        private Long limit; /* 非必选 */
        private Long offset; /* 非必选 */
        private String name; /* 非必选 */
    }
}
