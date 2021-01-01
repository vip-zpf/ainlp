package com.zpf.ainlp.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NlpSplitResVo {
    @ApiModelProperty(value = "中文句子List", example = "[\"史密斯出生于加利福尼亚\"]")
    private List<String> sentenceList;
}
