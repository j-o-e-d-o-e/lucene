package net.joedoe.app;

import net.joedoe.app.Info.Lang;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

import static net.joedoe.app.QueryFactory.QueryType;

public class Main {
    // directory paths
    private static final Lang language = Lang.JAVA;
    // query params
    private static final QueryType QUERY_TYPE = QueryType.TERM;
    private static final String field = Info.CONTENTS;
    private static final String text1 = "hash";
    private static final String text2 = "maps";
    private static final boolean sort = false; // false for Info.CONTENTS

    public static void main(String[] args) {
//        System.out.println(language.getDataDir());
        try {
//            createIndex();
            searchIndex();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private static void createIndex() throws IOException {
        Indexer indexer = new Indexer(language.getIndexDir(), new StandardAnalyzer());
        long startTime = System.currentTimeMillis();
        int numIndexed = indexer.indexDocuments(language.getDataDir());
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed + " files indexed. Time: " + (endTime - startTime) + " ms");
    }

    private static void searchIndex() throws IOException, ParseException {
        Searcher searcher = new Searcher(language.getIndexDir());
        Query query = QueryFactory.getQuery(QUERY_TYPE, field, text1, text2);
        long startTime = System.currentTimeMillis();
        TopDocs docs = sort && (field.equals(Info.FILE_NAME) || field.equals(Info.FILE_PATH)) ?
                searcher.searchAndSort(query, field) : searcher.search(query);
        long endTime = System.currentTimeMillis();
        printSearchResults(docs, endTime - startTime, searcher);
    }

    private static void printSearchResults(TopDocs docs, long time, Searcher searcher) throws IOException {
        System.out.println("Search terms: " + text1 +  ", " + text2);
        System.out.println("Query type: " + QUERY_TYPE);
        System.out.println(docs.totalHits + " document(s) found. Time: " + time + " ms");
        System.out.println("Files:");
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.print(doc.get(Info.FILE_NAME));
            if (!sort)
                System.out.println(" (" + scoreDoc.score + ")");
            else
                System.out.println();
        }
    }
}
