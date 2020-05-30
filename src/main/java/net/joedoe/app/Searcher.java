package net.joedoe.app;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Searcher {
    private IndexSearcher searcher;

    public Searcher(String pathname) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(new File(pathname).getAbsolutePath()));
        IndexReader indexReader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(indexReader);
    }

    public TopDocs search(Query query) throws IOException {
        return searcher.search(query, Info.MAX_SEARCH);
    }

    public TopDocs searchAndSort(Query query, String field) throws IOException {
        SortField sortField = new SortField(field, SortField.Type.STRING_VAL, false);
        Sort sort = new Sort(sortField);
        return searcher.search(query, Info.MAX_SEARCH, sort);
    }

    public Document getDocument(ScoreDoc scoreDoc) throws IOException {
        return searcher.doc(scoreDoc.doc);
    }
}
