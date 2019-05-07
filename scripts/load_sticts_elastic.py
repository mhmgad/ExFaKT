from elasticsearch import Elasticsearch, helpers
import codecs
import sys
from nltk import tokenize as tok
import json
import gzip

input_files = sys.argv[1:]

print 'arguments: ',len(input_files)

es_i = Elasticsearch([{'host': 'localhost', 'port': 9200}])

#docs_index=[]
count=0
doc_count=0
for input_file in input_files:
    count+=1
    print '(',count,')file: ', input_file
    docs=[]
    #with codecs.open(input_file,'r','utf-8') as f:
    with gzip.open(input_file,'r') as f:
            
    
        docs_index=[]
        for doc in f:
            doc_count+=1
            

            doc=doc.strip()
            if len(doc)==0:
                continue
            
            u_str = doc.decode('utf-8')
            # u_str = doc
            json_obj=  json.loads(u_str)

            title= json_obj['Title']
            url= json_obj['Link']
            did= 'unknown'
            if 'guid' in json_obj:
                did= json_obj['guid']
            body=''
            if 'Text' in json_obj:
                body=json_obj['Text']
            
            full_doc_entities=[]
            if 'aida' in json_obj:
                tmp=json_obj['aida']
                if 'llEntities' in tmp:
                    full_doc_entities=tmp['allEntities']

            print doc_count,'Doc:', did
            if body==None or body=='':
                print 'doc skipped'
                continue
            # sentences=tok.sent_tokenize(body)

        
            entry = {
                    "_index" : "stics",
                    "_type" : "text",
                    "doc_id" : did,
                    "_id" : str(doc_count),
                    "title" : title,
                    "url" : url,
                    "text": body,
                    "full_doc_entities":full_doc_entities
                    }
            docs_index.append(entry)

            if len(docs_index) > 0 and len(docs_index) % 100000 == 0:
                print "Loading entries", len(docs_index)
                helpers.bulk(es_i, docs_index,request_timeout=3000)
                docs_index = []

        if len(docs_index) > 0: #last bulk
            helpers.bulk(es_i,docs_index,request_timeout=3000)
            print 'inserted ', len(docs_index)
            docs_index = []