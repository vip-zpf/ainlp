package com.zpf.ainlp.service.impl;

import com.zpf.ainlp.domain.*;
import com.zpf.ainlp.pinyin.PinYinUtil;
import com.zpf.ainlp.service.NlpService;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Override
    public NlpNerPinyinResVo doNerAndPinyin(NlpNerPinyinReqVo reqVo) {
        Annotation annotation = new Annotation(reqVo.getText());
        stanfordCoreNLP.annotate(annotation);
        List<NlpSentencePinyin> nlpSentences = parserNerAndPinyinOutput(annotation);
        return new NlpNerPinyinResVo(nlpSentences);
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
            List<CoreLabel> coreLabels = sentence.get(CoreAnnotations.TokensAnnotation.class);
            for (int i = 0; i < coreLabels.size(); i++) {
                CoreLabel token = coreLabels.get(i);
                if (token == null) continue;

                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                if (StringUtils.isEmpty(word)) continue;
                if ("O".equals(ne)) {
                    sentenceStr = sentenceStr.replaceFirst(word, String.join("", Collections.nCopies(word.length(), "*")));
                    continue;
                }
                if ("TIME".equals(ne) || "NUMBER".equals(ne) || "MONEY".equals(ne)) {
                    Pattern pattern = Pattern.compile("([0-9]+)");
                    Matcher matcher = pattern.matcher(word);
                    if (!matcher.find()) continue;
                }
                if ("PERCENT".equals(ne)) {
                    Pattern pattern = Pattern.compile("([0-9.]+)[ ]*%");
                    Matcher matcher = pattern.matcher(word);
                    if (!matcher.find()) continue;
                    word = matcher.group(0);
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
            if (nlpSentence != null) {
                nerSentenceList.add(nlpSentence);
            }
        }
        return nerSentenceList;
    }


    private List<NlpSentencePinyin> parserNerAndPinyinOutput(Annotation annotation) {
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        List<NlpSentencePinyin> nerSentenceList = new LinkedList<>();
        for (CoreMap sentence : sentences) {
            NlpSentencePinyin nlpSentence = new NlpSentencePinyin();
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
                //以下类型无须做任何标记
                if ("O".equals(ne) || "COUNTRY".equals(ne) || "ORDINAL".equals(ne) || "STATE_OR_PROVINCE".equals(ne) || "MISC".equals(ne)) {
                    sentenceStr = sentenceStr.replaceFirst(word, String.join("", Collections.nCopies(word.length(), "*")));
                    continue;
                }

                if (!"NUMBER".equals(ne) && !"DATE".equals(ne) && !"TIME".equals(ne) && !"PERCENT".equals(ne)) {
                    String wordDuoYin = getWordDuoYin(word, ne);
                    if (StringUtils.isNoneEmpty(wordDuoYin)) {
                        SentencePinYin pinYin = new SentencePinYin();
                        pinYin.setWord(word);
                        pinYin.setPinyin(wordDuoYin);
                        pinYin.setOffset(sentenceStr.indexOf(word));
                        pinYin.setLen(word.length());
                        sentencePinYinList.add(pinYin);
                    }
                }

                //以下类型不需要返回实体识别结果
                if ("PERSON".equals(ne) || "CITY".equals(ne)) {
                    sentenceStr = sentenceStr.replaceFirst(word, String.join("", Collections.nCopies(word.length(), "*")));
                    continue;
                }
                if ("TIME".equals(ne) || "NUMBER".equals(ne) || "MONEY".equals(ne)) {
                    Pattern pattern = Pattern.compile("([0-9]+)");
                    Matcher matcher = pattern.matcher(word);
                    if (!matcher.find()) continue;
                }
                if ("PERCENT".equals(ne)) {
                    Pattern pattern = Pattern.compile("([0-9.]+)[ ]*%");
                    Matcher matcher = pattern.matcher(word);
                    if (!matcher.find()) continue;
                    word = matcher.group(0);
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

    private String getWordDuoYin(String word, String ne) {
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
        if ("PERSON".equals(ne)) {
            char surname = word.substring(0, 1).toCharArray()[0];
            String[] surnamePinyinArr = PinYinUtil.toHanyuPinyinStringArray(surname);
            if (surnamePinyinArr == null || surnamePinyinArr.length < 1) return pinyinStr;
            if (pinyinStr.indexOf(",") == -1) pinyinStr = pinyinStr + ",";
            pinyinStr = pinyinStr.substring(pinyinStr.indexOf(","));
            pinyinStr = surnamePinyinArr[0] + pinyinStr;
        }
        return pinyinStr;
    }

}
