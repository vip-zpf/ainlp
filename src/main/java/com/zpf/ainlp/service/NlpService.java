package com.zpf.ainlp.service;

import com.zpf.ainlp.domain.NlpNerReqVo;
import com.zpf.ainlp.domain.NlpNerResVo;
import com.zpf.ainlp.domain.NlpSplitReqVo;
import com.zpf.ainlp.domain.NlpSplitResVo;

import java.util.List;

public interface NlpService {
    NlpSplitResVo doSplit(NlpSplitReqVo reqVo);

    NlpNerResVo doNer(NlpNerReqVo reqVo);
}
