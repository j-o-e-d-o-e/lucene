package net.joedoe.app;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BytesRef;

public class SearchFactory {
    enum Type {
        normal, term, prefix, bool, phrase, fuzzy, wildcard
    }

    public static TopDocs search(Searcher searcher,Type type,  String field, String text1, String text2, boolean sort) throws Exception {
        Query query;
        Term term;
        switch (type) {
            case normal:
                query = new QueryParser(field, new StandardAnalyzer()).parse(text1);
                break;
            case term:
                term = new Term(field, text1);
                query = new TermQuery(term);
                break;
            case prefix:
                term = new Term(field, text1);
                query = new PrefixQuery(term);
                break;
            case bool:
                TermQuery query1 = new TermQuery(new Term(field, text1));
                TermQuery query2 = new TermQuery(new Term(field, text2));
                query = new BooleanQuery.Builder().add(query1, BooleanClause.Occur.MUST)
                        .add(query2, BooleanClause.Occur.MUST).build();
                break;
            case phrase:
                query = new PhraseQuery(1, field, new BytesRef(text1), new BytesRef(text2));
                break;
            case fuzzy:
                term = new Term(field, text1);
                query = new FuzzyQuery(term);
                break;
            case wildcard:
            default:
                term = new Term(field, text1);
                query = new WildcardQuery(term);
        }
        return sort ? searcher.searchAndSort(query, field) : searcher.search(query);
    }
}
