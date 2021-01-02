package com.zpf.ainlp.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.zpf.ainlp.bean.ResultObject;
import com.zpf.ainlp.domain.*;
import com.zpf.ainlp.service.NlpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiSort(1)
@Api(tags = "自然语言识别（nlp）")
@RestController
@RequestMapping("nlp")
public class nlpController {

    @Autowired
    private NlpService nlpService;

    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "中文句子分割")
    @PostMapping("split")
    public ResultObject<NlpSplitResVo> split(@RequestBody NlpSplitReqVo reqVo) {
        NlpSplitResVo res = nlpService.doSplit(reqVo);
        return new ResultObject(res);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "命名实体识别")
    @PostMapping("ner")
    public ResultObject<NlpNerResVo> ner(@RequestBody NlpNerReqVo reqVo) {
        NlpNerResVo res = nlpService.doNer(reqVo);
        return new ResultObject(res);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "命名实体多音字识别")
    @PostMapping("nerpinyin")
    public ResultObject<NlpNerPinyinResVo> nerPinyin(@RequestBody NlpNerPinyinReqVo reqVo) {
        NlpNerPinyinResVo res = nlpService.doNerAndPinyin(reqVo);
        return new ResultObject(res);
    }

}
