#ifndef __XMLPARSER_H__
#define __XMLPARSER_H__

int xmlparser_init(void);
int xmlparser_uninit(void);
int parse_xml(char *xml_uri, PUSH_XML_FLAG_E xml_flag);
char *push_dir_get();

#endif
