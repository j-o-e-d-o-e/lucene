package net.joedoe.app;

import net.joedoe.app.Info.Lang;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

import static net.joedoe.app.SearchFactory.Type;
import static net.joedoe.app.SearchFactory.search;

public class Main {
    // directory paths
    static final Lang language = Lang.JAVA;
    // search params
    static final Type type = Type.ANALYZER;
    static final String field = Info.FILE_NAME;
    static final String text1 = "array";
    static final String text2 = "map";
    static final boolean sort = true;

    public static void main(String[] args) {
        try {
//            createIndex();
            searchIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private static void createIndex() throws IOException {
        Indexer indexer = new Indexer(language.getIndexDir(), new StandardAnalyzer());
        long startTime = System.currentTimeMillis();
        int numIndexed = indexer.indexDocuments(language.dataDir);
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed + " files indexed. Time: " + (endTime - startTime) + " ms");
    }

    @SuppressWarnings("SameParameterValue")
    private static void searchIndex() throws Exception {
        Searcher searcher = new Searcher(language.getIndexDir());
        long startTime = System.currentTimeMillis();
        TopDocs docs = search(searcher, type, field, text1, text2, sort);
        long endTime = System.currentTimeMillis();
        printSearchResults(startTime, docs, endTime, searcher);
    }

    private static void printSearchResults(long startTime, TopDocs docs, long endTime, Searcher searcher) throws IOException {
        System.out.println(docs.totalHits + " documents found. Time: " + (endTime - startTime) + " ms");
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            System.out.print("Score: " + scoreDoc.score + " ");
            System.out.println("File: " + doc.get(Info.FILE_NAME));
        }
    }
}
