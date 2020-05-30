package net.joedoe.app;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BytesRef;

public class QueryFactory {
    enum QueryType {
        ANALYZER, TERM, PREFIX, BOOL, PHRASE, FUZZY, WILDCARD
    }

    public static Query getQuery(QueryType queryType, String field, String text1, String text2) throws ParseException {
        Query query;
        switch (queryType) {
            case ANALYZER:
                query = new QueryParser(field, new StandardAnalyzer()).parse(text1);
                break;
            case TERM:
                query = new TermQuery(new Term(field, text1));
                break;
            case PREFIX:
                query = new PrefixQuery(new Term(field, text1));
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
                query = new FuzzyQuery(new Term(field, text1));
                break;
            case WILDCARD:
            default:
                query = new WildcardQuery(new Term(field, "*" + text1 + "*"));
        }
        return query;
    }
}
