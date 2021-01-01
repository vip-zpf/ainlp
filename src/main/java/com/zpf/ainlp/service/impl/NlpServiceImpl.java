package com.zpf.ainlp.service.impl;

import com.zpf.ainlp.domain.*;
import com.zpf.ainlp.service.NlpService;
import com.zpf.ainlp.utils.PinYinUtil;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

@Service
public class NlpServiceImpl implements NlpService {

    @Autowired
    private StanfordCoreNLP stanfordCoreNLP;

    @Override
    public NlpSplitResVo doSplit(NlpSplitReqVo reqVo) {
        Properties splitProperties = getSplitProperties();
        List<String> result = new LinkedList<>();
        Document doc = new Document(reqVo.getText());
        for (Sentence sent : doc.sentences(splitProperties)) {
            result.add(sent.text());
        }
        return new NlpSplitResVo(result);
    }

    @Override
    public NlpNerResVo doNer(NlpNerReqVo reqVo) {
        Annotation annotation = new Annotation(reqVo.getText());
        stanfordCoreNLP.annotate(annotation);
        List<NlpSentence> nlpSentences = parserNerOutput(annotation);
        return new NlpNerResVo(nlpSentences);
    }

    private List<NlpSentence> parserNerOutput(Annotation annotation) {
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        List<NlpSentence> nerSentenceList = new LinkedList<>();
        for (CoreMap sentence : sentences) {
            NlpSentence nlpSentence = new NlpSentence();
            String sentenceStr = sentence.toString();
            nlpSentence.setSentence(sentenceStr);

            //实体识别
            List<SentenceNer> sentenceNerList = new LinkedList<>();
            //多音字识别
            List<SentencePinYin> sentencePinYinList = new LinkedList<>();
            List<CoreLabel> coreLabels = sentence.get(CoreAnnotations.TokensAnnotation.class);
            for (int i = 0; i < coreLabels.size(); i++) {
                CoreLabel token = coreLabels.get(i);
                if (token == null) continue;

                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                if (StringUtils.isEmpty(word)) continue;
                String wordDuoYin = getWordDuoYin(word);
                if (StringUtils.isNoneEmpty(wordDuoYin)) {
                    SentencePinYin pinYin = new SentencePinYin();
                    pinYin.setWord(word);
                    pinYin.setPinyin(wordDuoYin);
                    pinYin.setOffset(sentenceStr.indexOf(word));
                    pinYin.setLen(word.length());
                    sentencePinYinList.add(pinYin);
                }

                if ("O".equals(ne)) {
                    sentenceStr = sentenceStr.replaceFirst(word, String.join("", Collections.nCopies(word.length(), "*")));
                    continue;
                }
                SentenceNer sentenceNer = new SentenceNer();
                sentenceNer.setWord(word);
                sentenceNer.setTag(ne);
                sentenceNer.setOffset(sentenceStr.indexOf(word));
                sentenceNer.setLen(word.length());
                sentenceNerList.add(sentenceNer);
                sentenceStr = sentenceStr.replaceFirst(word, String.join("", Collections.nCopies(word.length(), "*")));
            }
            if (!CollectionUtils.isEmpty(sentenceNerList)) {
                nlpSentence.setSentenceNerList(sentenceNerList);
            }
            if (!CollectionUtils.isEmpty(sentencePinYinList)) {
                nlpSentence.setSentencePinYinList(sentencePinYinList);
            }
            if (nlpSentence != null) {
                nerSentenceList.add(nlpSentence);
            }
        }
        return nerSentenceList;
    }

    private Properties getSplitProperties() {
        Properties nlpChineseProperties = stanfordCoreNLP.getProperties();
        Properties properties = new Properties();
        String key = "ssplit.boundaryTokenRegex";
        String defaultValue = "[.。]|[!?！？,;，]+";
        properties.setProperty(key, nlpChineseProperties.getProperty(key, defaultValue));
        return properties;
    }

    private String getWordDuoYin(String word) {
        String pinyinStr = null;
        if (!PinYinUtil.checkDuoYin(word)) {
            return pinyinStr;
        }
        try {
            pinyinStr = PinyinHelper.toHanYuPinyinString(word, new HanyuPinyinOutputFormat(), "", true);
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
        if (StringUtils.isEmpty(pinyinStr)) {
            return pinyinStr;
        }
        pinyinStr = PinYinUtil.getPinYinStr(pinyinStr);
        return pinyinStr;
    }

}
