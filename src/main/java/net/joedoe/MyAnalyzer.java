package net.joedoe;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyAnalyzer {

    public List<String> analyze(String field, String text, Analyzer analyzer) throws IOException {
        TokenStream tokenStream = analyzer.tokenStream(field, text);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();

        List<String> result = new ArrayList<>();
        while (tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }

    public static class MyCustomAnalyzer extends Analyzer {
        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            final StandardTokenizer src = new StandardTokenizer();
            TokenStream result = new StandardFilter(src);
            result = new LowerCaseFilter(result);
            result = new StopFilter(result, StandardAnalyzer.STOP_WORDS_SET);
            result = new PorterStemFilter(result);
            result = new CapitalizationFilter(result);
            return new TokenStreamComponents(src, result);
        }
    }
}