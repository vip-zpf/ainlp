package com.zpf.ainlp.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SentenceNer {
    @ApiModelProperty(value = "中文单词", position = 1, example = "史密斯")
    private String word;
    @ApiModelProperty(value = "字符起始坐标（坐标从0开始）", position = 2, example = "0")
    private Integer offset;
    @ApiModelProperty(value = "词长", position = 3, example = "3")
    private Integer len;
    @ApiModelProperty(value = "词-实体类别", position = 4, example = "PERSON")
    private String tag;
}
