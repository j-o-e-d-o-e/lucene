package net.joedoe;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InMemoryLuceneIndex {
    private Directory memoryIndex;
    private Analyzer analyzer;

    public InMemoryLuceneIndex(Directory memoryIndex, Analyzer analyzer) {
        this.memoryIndex = memoryIndex;
        this.analyzer = analyzer;
    }

    public void indexDocument(String title, String body) {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        try {
            IndexWriter writer = new IndexWriter(memoryIndex, indexWriterConfig);
            Document document = new Document();
            document.add(new TextField("title", title, Field.Store.YES));
            document.add(new TextField("body", body, Field.Store.YES));
            document.add(new SortedDocValuesField("title", new BytesRef(title)));
            writer.addDocument(document);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Document> searchIndex(String inField, String queryString) {
        try {
            IndexSearcher searcher = getIndexSearcher();
            Query query = new QueryParser(inField, analyzer).parse(queryString);
            TopDocs topDocs = searcher.search(query, 10);
            return getDocuments(searcher, topDocs);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Document> searchIndex(Query query) {
        try {
            IndexSearcher searcher = getIndexSearcher();
            TopDocs topDocs = searcher.search(query, 10);
            return getDocuments(searcher, topDocs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Document> searchIndex(Query query, Sort sort) {
        try {
            IndexSearcher searcher = getIndexSearcher();
            TopDocs topDocs = searcher.search(query, 10, sort);
            return getDocuments(searcher, topDocs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private IndexSearcher getIndexSearcher() throws IOException {
        IndexReader indexReader = DirectoryReader.open(memoryIndex);
        return new IndexSearcher(indexReader);
    }

    private List<Document> getDocuments(IndexSearcher searcher, TopDocs topDocs) throws IOException {
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }
        return documents;
    }

    public void deleteDocument(Term term) {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        try {
            IndexWriter writer = new IndexWriter(memoryIndex, indexWriterConfig);
            writer.deleteDocuments(term);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
