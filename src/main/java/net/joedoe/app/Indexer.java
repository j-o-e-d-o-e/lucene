package net.joedoe.app;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class Indexer {
    private IndexWriter writer;

    public Indexer(String pathname, Analyzer analyzer) throws IOException {
        Directory indexDir = FSDirectory.open(Paths.get(new File(pathname).getAbsolutePath()));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        writer = new IndexWriter(indexDir, config);
    }

    public int indexDocuments(String pathname) throws IOException {
        File[] files = new File(pathname).listFiles();
        assert files != null;
        for (File file : files) {
            if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead()) {
                System.out.println("Indexing: " + file.getName());
                Document document = new Document();
                document.add(new TextField(Info.CONTENTS, new FileReader(file)));
                document.add(new StringField(Info.FILE_NAME, file.getName(), Field.Store.YES));
                document.add(new StringField(Info.FILE_PATH, file.getPath(), Field.Store.YES));
                writer.addDocument(document);
            }
        }
        return writer.numDocs();
    }

    @SuppressWarnings("unused")
    public void deleteDocument(Term term) throws IOException {
        writer.deleteDocuments(term);
    }

    public void close() throws IOException {
        writer.close();
    }
}
