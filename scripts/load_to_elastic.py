from elasticsearch import Elasticsearch, helpers
import codecs
import sys
import xml.etree.ElementTree as ET

input_files = sys.argv[1:]

print 'arguments: ',len(input_files)

es_i = Elasticsearch([{'host': 'localhost', 'port': 9200}])

#docs_index=[]
count=0
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
		
		entry = {
    			"_index" : "wiki",
        		"_type" : "text",
        		"_id" : did,
        		"title" : title,
        		"url" : url,
        		"text": body
    			}
		#print entry

		docs_index.append(entry)
			
		if len(docs_index) > 0 and len(docs_index) % 100000 == 0:
	       		print "Loading entries", len(docs_index)
			helpers.bulk(es_i, docs_index)
			docs_index = []
			#print doc['title']
	
	if len(docs_index) > 0: #last bulk
    		helpers.bulk(es_i,docs_index)
		print 'inserted ', len(docs_index)
		docs_index = []
	


'''
for line in codecs.open(input, 'rt'):
    line = line[:-1]
    delim = line.find('\t')
    url = line[:delim]
    url = url[1:-1]
    label = line[delim+1:]
    label = label[1:-1]
    lang = ""
    if len(label) >= 3 and label[-3] == '@':
        lang = label[-2:]
        label = label[:-3]
    elif len(label) >= 4 and label[-4] == '@':
        lang = label[-3:]
        label = label[:-4]
    if len(label) > 2 and label[0] == '"' and label[-1] == '"':
        label = label[1:-1]

    #Construct a JSON object
    entry = {
            "_index" : "kb",
                "_type" : "label",
                "_id" : count,
                "label" : label,
                "uri" : url,
                "lang": lang
            }
    labels.append(entry)

    if count > 0 and count % 1000000 == 0:
        print("Loading entries", count)
        helpers.bulk(es_i, labels)
        labels = []
    count += 1

if len(labels) > 0:
    helpers.bulk(es_i,labels)
'''
