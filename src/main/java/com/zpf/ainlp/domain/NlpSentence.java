package com.zpf.ainlp.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class NlpSentence {
    @ApiModelProperty(value = "中文单句", position = 1, example = "史密斯出生于加利福尼亚")
    private String sentence;

    @ApiModelProperty(value = "单句实体识别结果List", position = 2)
    private List<SentenceNer> sentenceNerList;

    @ApiModelProperty(value = "多音字识别结果List", position = 2)
    private List<SentencePinYin> sentencePinYinList;
}
