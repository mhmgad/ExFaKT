from elasticsearch import Elasticsearch, helpers
import codecs
import sys
import xml.etree.ElementTree as ET
from nltk import tokenize as tok

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
    with codecs.open(input_file,'r','utf-8') as f:
            
    #xml_doc = ET.parse(input_file)
    #root_node=xml_doc.getroot()
        docs=f.read().strip().split('</doc>')
        #print 'last doc is (',docs[-1],')'
        print 'to insert', len(docs)
    docs_index=[]
    for doc in docs:
        doc_count+=1
        doc=doc.strip()
        if len(doc)==0:
            continue
        first_line=doc.find('\n')
        header=doc[0:first_line]
        #print (header+'</doc>').encode('utf-8')
        xml_object=ET.fromstring((header+'</doc>').encode('utf-8'))
        #root=xml_object.getroot()
        title= xml_object.attrib['title']
        url= xml_object.attrib['url']
        did= xml_object.attrib['id']
        body=doc[first_line+1:]

        print doc_count,'Doc:', did

        sentences=tok.sent_tokenize(body)

        sent_count=0
        for sentence in sentences:
            sent_count+=1
            entry = {
                    "_index" : "wiki_sent",
                    "_type" : "text",
                    "doc_id" : did,
                    "_id" : str(did)+'_'+str(sent_count),
                    "title" : title,
                    "url" : url,
                    "sent":sentence
                    }
            docs_index.append(entry)

            if len(docs_index) > 0 and len(docs_index) % 100000 == 0:
                print "Loading entries", len(docs_index)
                helpers.bulk(es_i, docs_index)
                docs_index = []

        if len(docs_index) > 0: #last bulk
            helpers.bulk(es_i,docs_index)
            print 'inserted ', len(docs_index)
            docs_index = []
