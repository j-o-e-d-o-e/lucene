package net.joedoe;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

public class LuceneFileSearchIntegrationTest {
    private final static String indexPath = "index";
    private final static String dataPath = "data/file1.txt";
    private LuceneFileSearch luceneFileSearch;

    @Before
    public void setup() throws IOException {
        Directory directory = FSDirectory.open(Paths.get(indexPath));
        luceneFileSearch = new LuceneFileSearch(directory, new StandardAnalyzer());
    }

    @Test
    public void givenSearchQueryWhenFetchedFileNameThenCorrect() throws IOException, URISyntaxException {
        luceneFileSearch.addFileToIndex(dataPath);

        List<Document> docs = luceneFileSearch.searchFiles("contents", "consectetur");

        Assert.assertEquals("file1.txt", docs.get(0).get("filename"));
    }
}
