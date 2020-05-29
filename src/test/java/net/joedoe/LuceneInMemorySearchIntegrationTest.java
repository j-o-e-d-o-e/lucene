package net.joedoe;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class LuceneInMemorySearchIntegrationTest {
    private InMemoryLuceneIndex inMemoryLuceneIndex;

    @Before
    public void setup(){
        inMemoryLuceneIndex = new InMemoryLuceneIndex(new RAMDirectory(), new StandardAnalyzer());
    }

    @Test
    public void givenSearchQueryWhenFetchedDocumentThenCorrect() {
        inMemoryLuceneIndex.indexDocument("Hello world", "Some hello world");

        Document doc = inMemoryLuceneIndex.searchIndex("body", "world").get(0);

        Assert.assertEquals("Hello world", doc.get("title"));
        Assert.assertEquals("Some hello world", doc.get("body"));
    }

    @Test
    public void givenTermQueryWhenFetchedDocumentThenCorrect() {
        inMemoryLuceneIndex.indexDocument("activity", "Dogs are running");
        inMemoryLuceneIndex.indexDocument("activity", "Cats are running");
        inMemoryLuceneIndex.indexDocument("activity", "Ducks are swimming");

        Term term = new Term("body", "running");
        Query query = new TermQuery(term);

        List<Document> documents = inMemoryLuceneIndex.searchIndex(query);
        Assert.assertEquals(2, documents.size());
    }

    @Test
    public void givenPrefixQueryWhenFetchedDocumentThenCorrect() {
        inMemoryLuceneIndex.indexDocument("article", "Lucene introduction");
        inMemoryLuceneIndex.indexDocument("article", "Introduction to Lucene");
        inMemoryLuceneIndex.indexDocument("article", "Another article");

        Term term = new Term("body", "intro");
        Query query = new PrefixQuery(term);

        List<Document> documents = inMemoryLuceneIndex.searchIndex(query);
        Assert.assertEquals(2, documents.size());
    }

    @Test
    public void givenBooleanQueryWhenFetchedDocumentThenCorrect() {
        inMemoryLuceneIndex.indexDocument("Destination", "Las Vegas singapore car");
        inMemoryLuceneIndex.indexDocument("Commutes in singapore", "Bus Car Bikes");

        Term term1 = new Term("body", "singapore");
        Term term2 = new Term("body", "car");

        TermQuery query1 = new TermQuery(term1);
        TermQuery query2 = new TermQuery(term2);

        BooleanQuery booleanQuery = new BooleanQuery.Builder().add(query1, BooleanClause.Occur.MUST_NOT)
                .add(query2, BooleanClause.Occur.MUST).build();

        List<Document> documents = inMemoryLuceneIndex.searchIndex(booleanQuery);
        Assert.assertEquals(1, documents.size());
        Assert.assertEquals("Commutes in singapore", documents.get(0).get("title"));
    }

    @Test
    public void givenPhraseQueryWhenFetchedDocumentThenCorrect() {
        inMemoryLuceneIndex.indexDocument("quotes", "A rose by any other name would smell as sweet.");

        // slop is the distance in the number of words, between the terms to be matched
        // TODO: NOT WORKING CORRECTLY
        Query query = new PhraseQuery(1, "body", new BytesRef("smell"), new BytesRef("sweet"));
        List<Document> documents = inMemoryLuceneIndex.searchIndex(query);

        Assert.assertEquals(1, documents.size());
    }

    @Test
    public void givenFuzzyQueryWhenFetchedDocumentThenCorrect() {
        inMemoryLuceneIndex.indexDocument("article", "Halloween Festival");
        inMemoryLuceneIndex.indexDocument("decoration", "Decorations for Halloween");

        Term term = new Term("body", "hallowen");
        Query query = new FuzzyQuery(term);

        List<Document> documents = inMemoryLuceneIndex.searchIndex(query);
        Assert.assertEquals(2, documents.size());
    }

    @Test
    public void givenWildCardQueryWhenFetchedDocumentThenCorrect() {
        inMemoryLuceneIndex.indexDocument("article", "Lucene introduction");
        inMemoryLuceneIndex.indexDocument("article", "Introducing Lucene with Spring");

        Term term = new Term("body", "intro*");
        Query query = new WildcardQuery(term);

        List<Document> documents = inMemoryLuceneIndex.searchIndex(query);
        Assert.assertEquals(2, documents.size());
    }

    @Test
    public void givenSortFieldWhenSortedThenCorrect() {
        inMemoryLuceneIndex.indexDocument("Ganges", "River in India");
        inMemoryLuceneIndex.indexDocument("Mekong", "This river flows in south Asia");
        inMemoryLuceneIndex.indexDocument("Amazon", "Rain forest river");
        inMemoryLuceneIndex.indexDocument("Rhine", "Belongs to Europe");
        inMemoryLuceneIndex.indexDocument("Nile", "Longest River");

        Term term = new Term("body", "river");
        Query query = new WildcardQuery(term);

        SortField sortField = new SortField("title", SortField.Type.STRING_VAL, false);
        Sort sortByTitle = new Sort(sortField);

        List<Document> documents = inMemoryLuceneIndex.searchIndex(query, sortByTitle);
        Assert.assertEquals(4, documents.size());
        Assert.assertEquals("Amazon", documents.get(0).getField("title").stringValue());
    }

    @Test
    public void whenDocumentDeletedThenCorrect() {
        inMemoryLuceneIndex.indexDocument("Ganges", "River in India");
        inMemoryLuceneIndex.indexDocument("Mekong", "This river flows in south Asia");

        Term term = new Term("title", "ganges");
        inMemoryLuceneIndex.deleteDocument(term);

        Term term1 = new Term("body", "river");
        Query query = new TermQuery(term1);

        List<Document> documents = inMemoryLuceneIndex.searchIndex(query);
        Assert.assertEquals(1, documents.size());
    }
}
