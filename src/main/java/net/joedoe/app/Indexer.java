package net.joedoe.app;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class Indexer extends IndexWriter {

    public Indexer(String pathname, Analyzer analyzer) throws IOException {
        super(FSDirectory.open(Paths.get(new File(pathname).getAbsolutePath())),
                new IndexWriterConfig(analyzer).setOpenMode(IndexWriterConfig.OpenMode.CREATE)
        );
    }

    public int indexDocuments(String pathname) throws IOException {
        File[] files = new File(pathname).listFiles();
        assert files != null;
        for (File file : files) {
            if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead()) {
                System.out.println("Indexing: " + file.getName());
                Document doc = new Document();
                doc.add(new TextField(Info.CONTENTS, new FileReader(file)));
                doc.add(new StringField(Info.FILE_NAME, file.getName(), Field.Store.YES));
                doc.add(new SortedDocValuesField(Info.FILE_NAME, new BytesRef(file.getName())));
                doc.add(new StringField(Info.FILE_PATH, file.getPath(), Field.Store.YES));
                doc.add(new SortedDocValuesField(Info.FILE_PATH, new BytesRef(file.getPath())));
                addDocument(doc);
            }
        }
        return numDocs();
    }
}
