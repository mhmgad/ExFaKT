#find $1/*/wiki_* -type f -exec python load_to_elastic.py {} + 


find $1/*/wiki_* -type f -exec python load_sentences_elastic.py {} + 

