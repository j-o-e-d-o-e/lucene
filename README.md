# baeldung

## Search types

### ANALYZER
- Analyzers are used to split the text into chunks, and then filter out stop words, like ‘a', ‘am', ‘is'
- e.g. field=Info.CONTENTS, text1=array

### TERM
- A Term is a basic search unit, TermQuery is the simplest of all queries
- e.g. field=Info.FILE_NAME, text1=junit.txt

### PREFIX
- To search a document with a “starts with” word
- e.g. field=Info.FILE_NAME, text1=swi

### BOOL
- to execute complex searches, combining two or more different types of queries
- e.g. field=Info.FILE_NAME, text1=hash, text2=sets

### PHRASE
- To search a sequence of texts in a document
- e.g. field=Info.CONTENTS, text1=array, text2=lists

### FUZZY
- To search for something similar, but not necessarily identical
- e.g. field=Info.FILE_NAME, text1=bndings.txt

### WILDCARD
- Wildcards “*” or “?” can be used
- e.g. field=Info.FILE_NAME, text1=sub

## g/re/p
- **g**lobally search a **r**egular **e**xpression and **p**rint
- `grep '<search_term>' <file_name>`
    - Quoting the search string (single or double quote) is good practice
- search flags
    - case-insensitive, e.g. `grep -i 'linux' input.txt`
    - whole-word, e.g. `grep -w 'is' input.txt`

### Materials

- [Tutorial website](https://www.baeldung.com/lucene)
- [grep overview](https://www.baeldung.com/linux/common-text-search)