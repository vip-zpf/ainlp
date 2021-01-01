package com.zpf.ainlp.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NlpNerReqVo {
    @ApiModelProperty(value = "中文段落", required = true, example = "史密斯出生于加利福尼亚。2017年夏天，他去了法国巴黎。他的航班于2017年7月10日下午3点起飞。第一次吃了一些蜗牛后，乔说:太好吃了!他寄了一张明信片给他的妹妹简·史密斯，他打了他的女儿汤姆。听了乔的旅行后，简决定有一天去法国。")
    private String text;
}
