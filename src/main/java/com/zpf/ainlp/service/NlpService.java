package com.zpf.ainlp.service;

import com.zpf.ainlp.domain.*;

import java.util.List;

public interface NlpService {
    NlpSplitResVo doSplit(NlpSplitReqVo reqVo);

    NlpNerResVo doNer(NlpNerReqVo reqVo);

    NlpNerPinyinResVo doNerAndPinyin(NlpNerPinyinReqVo reqVo);
}
