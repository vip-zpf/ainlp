package com.zpf.ainlp.service.impl;

import com.zpf.ainlp.domain.*;
import com.zpf.ainlp.service.NlpService;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
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

            List<SentenceNer> sentenceNerList = new LinkedList<>();
            List<CoreLabel> coreLabels = sentence.get(CoreAnnotations.TokensAnnotation.class);
            for (int i = 0; i < coreLabels.size(); i++) {
                CoreLabel token = coreLabels.get(i);
                if (token == null) continue;

                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                if ("O".equals(ne)) continue;

                SentenceNer sentenceNer = new SentenceNer();
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                sentenceNer.setWord(word);
                sentenceNer.setTag(ne);
                sentenceNer.setOffset(sentenceStr.indexOf(word));
                sentenceNer.setLen(word.length());
                sentenceNerList.add(sentenceNer);
                sentenceStr = sentenceStr.replaceFirst(word,String.join("", Collections.nCopies(word.length(), "*")));
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

    private Properties getSplitProperties() {
        Properties nlpChineseProperties = stanfordCoreNLP.getProperties();
        Properties properties = new Properties();
        String key = "ssplit.boundaryTokenRegex";
        String defaultValue = "[.。]|[!?！？,;，]+";
        properties.setProperty(key, nlpChineseProperties.getProperty(key, defaultValue));
        return properties;
    }

}
