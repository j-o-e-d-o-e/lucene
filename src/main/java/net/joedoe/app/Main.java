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
    private static final Lang language = Lang.PYTHON;
    // query params
    private static final QueryType QUERY_TYPE = QueryType.WILDCARD;
    private static final String field = Info.FILE_NAME;
    private static final String text1 = "string";
    private static final String text2 = "warning";
    private static final boolean sort = true; // false for Info.CONTENTS

    public static void main(String[] args) {
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
        long startTime = System.currentTimeMillis();
        Query query = QueryFactory.getQuery(QUERY_TYPE, field, text1, text2);
        TopDocs docs = sort && (field.equals(Info.FILE_NAME) || field.equals(Info.FILE_PATH)) ?
                searcher.searchAndSort(query, field) : searcher.search(query);
        long endTime = System.currentTimeMillis();
        System.out.println(docs.totalHits + " documents found. Time: " + (endTime - startTime) + " ms");
        printTopDocs(docs, searcher);
    }

    private static void printTopDocs(TopDocs docs, Searcher searcher) throws IOException {
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            if (!sort)
                System.out.print("Score: " + scoreDoc.score + " ");
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("File: " + doc.get(Info.FILE_NAME));
        }
    }
}
