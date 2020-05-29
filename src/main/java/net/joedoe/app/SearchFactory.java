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
                // Analyzers are used to split the text into chunks, and then filter out stop words, like ‘a', ‘am', ‘is'
                // e.g. field=Info.CONTENTS, text1=array
                query = new QueryParser(field, new StandardAnalyzer()).parse(text1);
                break;
            case TERM:
                // A Term is a basic search unit, TermQuery is the simplest of all queries
                // e.g. field=Info.FILE_NAME, text1=junit.txt
                term = new Term(field, text1);
                query = new TermQuery(term);
                break;
            case PREFIX:
                // To search a document with a “starts with” word
                // e.g. field=Info.FILE_NAME, text1=swi
                term = new Term(field, text1);
                query = new PrefixQuery(term);
                break;
            case BOOL:
                // to execute complex searches, combining two or more different types of queries
                // e.g. field=Info.FILE_NAME, text1=hash, text2=sets
                WildcardQuery query1 = new WildcardQuery(new Term(field, "*" + text1 + "*"));
                WildcardQuery query2 = new WildcardQuery(new Term(field, "*" + text2 + "*"));
                query = new BooleanQuery.Builder().add(query1, BooleanClause.Occur.MUST)
                        .add(query2, BooleanClause.Occur.MUST_NOT).build();
                break;
            case PHRASE:
                // To search a sequence of texts in a document
                // e.g. field=Info.CONTENTS, text1=array, text2=lists
                query = new PhraseQuery(1, field, new BytesRef(text1), new BytesRef(text2));
                break;
            case FUZZY:
                // To search for something similar, but not necessarily identical
                // e.g. field=Info.FILE_NAME, text1=bndings.txt
                term = new Term(field, text1);
                query = new FuzzyQuery(term);
                break;
            case WILDCARD:
            default:
                // Wildcards “*” or “?” can be used
                // e.g. field=Info.FILE_NAME, text1=sub
                term = new Term(field, "*" + text1 + "*");
                query = new WildcardQuery(term);
        }
        // To sort the search results documents based on certain fields
        return sort ? searcher.searchAndSort(query, field) : searcher.search(query);
    }
}
