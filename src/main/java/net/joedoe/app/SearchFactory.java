package net.joedoe.app;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BytesRef;

public class SearchFactory {
    enum Type {
        ANALYZER, TERM, PREFIX, BOOL, PHRASE, FUZZY, WILDCARD
    }

    public static TopDocs search(Searcher searcher, Type type, String field, String text1, String text2, boolean sort) throws Exception {
        Query query;
        Term term;
        switch (type) {
            case ANALYZER:
                query = new QueryParser(field, new StandardAnalyzer()).parse(text1);
                break;
            case TERM:
                term = new Term(field, text1);
                query = new TermQuery(term);
                break;
            case PREFIX:
                term = new Term(field, text1);
                query = new PrefixQuery(term);
                break;
            case BOOL:
                TermQuery query1 = new TermQuery(new Term(field, text1));
                TermQuery query2 = new TermQuery(new Term(field, text2));
                query = new BooleanQuery.Builder().add(query1, BooleanClause.Occur.MUST)
                        .add(query2, BooleanClause.Occur.MUST_NOT).build();
                break;
            case PHRASE:
                query = new PhraseQuery(1, field, new BytesRef(text1), new BytesRef(text2));
                break;
            case FUZZY:
                term = new Term(field, text1);
                query = new FuzzyQuery(term);
                break;
            case WILDCARD:
            default:
                term = new Term(field, "*" + text1 + "*");
                query = new WildcardQuery(term);
        }
        return sort ? searcher.searchAndSort(query, field) : searcher.search(query);
    }
}
