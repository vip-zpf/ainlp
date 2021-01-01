package com.zpf.ainlp.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NlpNerResVo {
    @ApiModelProperty(value = "中文段落拆分List")
    List<NlpSentence> nerSentenceList;
}
