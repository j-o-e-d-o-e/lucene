package net.joedoe;

import net.joedoe.MyAnalyzer.MyCustomAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;

public class LuceneAnalyzerIntegrationTest {
    private static final String SAMPLE_TEXT = "This is baeldung.com Lucene Analyzers test";
    private static final String FIELD_NAME = "sampleName";
    private MyAnalyzer analyzer;

    @Before
    public void setup() {
        analyzer = new MyAnalyzer();
    }

    @Test
    public void whenUseStandardAnalyzerThenAnalyzed() throws IOException {
        List<String> result = analyzer.analyze(FIELD_NAME, SAMPLE_TEXT, new StandardAnalyzer());

        assertThat(result, contains("baeldung.com", "lucene", "analyzers", "test"));
    }

    @Test
    public void whenUseStopAnalyzerThenAnalyzed() throws IOException {
        List<String> result = analyzer.analyze(FIELD_NAME, SAMPLE_TEXT, new StopAnalyzer());

        assertThat(result, contains("baeldung", "com", "lucene", "analyzers", "test"));
    }

    @Test
    public void whenUseSimpleAnalyzerThenAnalyzed() throws IOException {
        List<String> result = analyzer.analyze(FIELD_NAME, SAMPLE_TEXT, new SimpleAnalyzer());

        assertThat(result, contains("this", "is", "baeldung", "com", "lucene", "analyzers", "test"));
    }

    @Test
    public void whenUseWhiteSpaceAnalyzerThenAnalyzed() throws IOException {
        List<String> result = analyzer.analyze(FIELD_NAME, SAMPLE_TEXT, new WhitespaceAnalyzer());

        assertThat(result, contains("This", "is", "baeldung.com", "Lucene", "Analyzers", "test"));
    }

    @Test
    public void whenUseKeywordAnalyzerThenAnalyzed() throws IOException {
        List<String> result = analyzer.analyze(FIELD_NAME, SAMPLE_TEXT, new KeywordAnalyzer());

        assertThat(result, contains("This is baeldung.com Lucene Analyzers test"));
    }

    @Test
    public void whenUseEnglishAnalyzerThenAnalyzed() throws IOException {
        List<String> result = analyzer.analyze(FIELD_NAME, SAMPLE_TEXT, new EnglishAnalyzer());

        assertThat(result, contains("baeldung.com", "lucen", "analyz", "test"));
    }

    @Test
    public void whenUseCustomAnalyzerBuilderThenAnalyzed() throws IOException {
        Analyzer analyzer = CustomAnalyzer.builder()
                .withTokenizer("standard")
                .addTokenFilter("lowercase")
                .addTokenFilter("stop")
                .addTokenFilter("porterstem")
                .addTokenFilter("capitalization")
                .build();
        List<String> result = this.analyzer.analyze(FIELD_NAME, SAMPLE_TEXT, analyzer);

        assertThat(result, contains("Baeldung.com", "Lucen", "Analyz", "Test"));
    }

    @Test
    public void whenUseCustomAnalyzerThenAnalyzed() throws IOException {
        List<String> result = analyzer.analyze(FIELD_NAME, SAMPLE_TEXT, new MyCustomAnalyzer());

        assertThat(result, contains("Baeldung.com", "Lucen", "Analyz", "Test"));
    }

    // ================= usage example

    @Test
    public void givenTermQueryWhenUseCustomAnalyzerThenCorrect() {
        InMemoryLuceneIndex luceneIndex = new InMemoryLuceneIndex(new RAMDirectory(), new MyCustomAnalyzer());
        luceneIndex.indexDocument("introduction", "introduction to lucene");
        luceneIndex.indexDocument("analyzers", "guide to lucene analyzers");
        Query query = new TermQuery(new Term("body", "Introduct"));

        List<Document> documents = luceneIndex.searchIndex(query);
        assertEquals(1, documents.size());
    }

    @Test
    public void givenTermQueryWhenUsePerFieldAnalyzerWrapperThenCorrect() {
        Map<String, Analyzer> analyzerMap = new HashMap<>();
        analyzerMap.put("title", new MyCustomAnalyzer());
        analyzerMap.put("body", new EnglishAnalyzer());

        PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);
        InMemoryLuceneIndex luceneIndex = new InMemoryLuceneIndex(new RAMDirectory(), wrapper);
        luceneIndex.indexDocument("introduction", "introduction to lucene");
        luceneIndex.indexDocument("analyzers", "guide to lucene analyzers");

        Query query = new TermQuery(new Term("body", "introduct"));
        List<Document> documents = luceneIndex.searchIndex(query);
        assertEquals(1, documents.size());

        query = new TermQuery(new Term("title", "Introduct"));
        documents = luceneIndex.searchIndex(query);
        assertEquals(1, documents.size());
    }
}
