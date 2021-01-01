package com.zpf.ainlp.configuration;

import com.zpf.ainlp.service.impl.NlpServiceImpl;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.Properties;

@Configuration
public class NlpConfig {

    @Bean
    public StanfordCoreNLP stanfordCoreNLP(){
        Properties properties = new Properties();
        InputStream is = NlpServiceImpl.class.getClassLoader().getResourceAsStream("nlpChinese.properties");
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            properties.load(bufferedReader);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StanfordCoreNLP corenlp = new StanfordCoreNLP(properties);
        return corenlp;
    }

    /*@Bean
    public StanfordCoreNLP stanfordCoreSplitNLP(){
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP corenlp = new StanfordCoreNLP(props);
        return corenlp;
    }*/
}
