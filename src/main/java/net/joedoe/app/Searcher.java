package net.joedoe.app;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Searcher extends IndexSearcher {

    public Searcher(String pathname) throws IOException {
        super(DirectoryReader.open(
                FSDirectory.open(
                        Paths.get(new File(pathname).getAbsolutePath())))
        );
    }

    public TopDocs search(Query query) throws IOException {
        return search(query, Info.MAX_SEARCH);
    }

    public TopDocs searchAndSort(Query query, String field) throws IOException {
        SortField sortField = new SortField(field, SortField.Type.STRING_VAL, false);
        Sort sort = new Sort(SortField.FIELD_SCORE, sortField);
        return search(query, Info.MAX_SEARCH, sort);
    }
}
