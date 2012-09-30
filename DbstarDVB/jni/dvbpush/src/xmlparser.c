#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <libxml/xmlmemory.h>
#include <libxml/parser.h>

#include "common.h"
#include "xmlparser.h"
#include "sqlite.h"
#include "mid_push.h"
#include "multicast.h"
#include "softdmx.h"

//static int content_in_column = 0;

int xmlparser_init(void)
{
	return 0;
}

int xmlparser_uninit(void)
{
	return 0;
}

static int column_xml_sqlite_insert(DBSTAR_COLUMN_2_S *p_column)
{
	if(NULL==p_column){
		DEBUG("can not insert NULL to column db\n");
		return -1;
	}
	DEBUG("column.id=%s\n", p_column->id);
	DEBUG("column.name=%s\n", p_column->name);
	DEBUG("column.parent_id=%s\n", p_column->parent_id);
	
	char sqlite_cmd[256+128];
	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "INSERT INTO column(id,name,parent_id) VALUES('%s','%s','%s');", \
		p_column->id, p_column->name, p_column->parent_id);
	sqlite_execute(sqlite_cmd);
	DEBUG("insert a content info of group.xml into table 'column'\n");
	return 0;
}

static int product_xml_sqlite_insert(DBSTAR_PRODUCT_S *p_product)
{
	if(NULL==p_product){
		DEBUG("can not insert NULL to column db\n");
		return -1;
	}
	
	DEBUG("p_product.id=%s\n", p_product->id);
	DEBUG("p_product.name=%s\n", p_product->contentname);
	DEBUG("p_product.column_id=%s\n", p_product->column_id);
	DEBUG("p_product.path=%s\n", p_product->xmlpath);
	
	char sqlite_cmd[256+128];
	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "INSERT INTO content(id,contentname,column_id,path) VALUES('%s','%s','%s','%s');", \
		p_product->id, p_product->contentname, p_product->column_id, p_product->xmlpath);
	sqlite_execute(sqlite_cmd);
	DEBUG("insert a content info of ProductTag.xml into table 'content'\n");
	
	return 0;
}

static int brand_xml_sqlite_insert(DBSTAR_BRAND_S *p_brand)
{
	if(NULL==p_brand){
		DEBUG("can not insert NULL to column db\n");
		return -1;
	}
	DEBUG("pathid=%s\n", p_brand->pathid);
	DEBUG("cname=%s\n", p_brand->cname);
	DEBUG("totalsize=%lld\n", p_brand->totalsize);
	
	char sqlite_cmd[256+128];
	char total_xmlpath[256];
	snprintf(total_xmlpath, sizeof(total_xmlpath), "%s/%s", RELAY_DIR, p_brand->pathid);
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "INSERT INTO brand(id,cname,regist_dir,totalsize) VALUES('%s','%s','%s',%lld);", \
		p_brand->pathid, p_brand->cname, total_xmlpath, p_brand->totalsize);
	sqlite_execute(sqlite_cmd);
	DEBUG("%s\n", sqlite_cmd);
	
	return 0;
}

static int preproduct_xml_sqlite_insert(DBSTAR_PREPRODUCT_S *p_preproduct)
{
	if(NULL==p_preproduct){
		DEBUG("can not insert NULL to column db\n");
		return -1;
	}
	DEBUG("id=%s\n", p_preproduct->id);
	DEBUG("preentry=%s\n", p_preproduct->preentry);
	DEBUG("prename=%s\n", p_preproduct->prename);
	DEBUG("column_id=%s\n", p_preproduct->column_id);
	DEBUG("xmlpath=%s\n", p_preproduct->xmlpath);
	
	char sqlite_cmd[256+128];
	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));
	
	snprintf(sqlite_cmd, sizeof(sqlite_cmd), "INSERT INTO preproduct(id,preentry,prename,xmlpath,column_id) VALUES('%s','%s','%s','%s','%s');", \
		p_preproduct->id, p_preproduct->preentry, p_preproduct->prename, p_preproduct->xmlpath, p_preproduct->column_id);
	sqlite_execute(sqlite_cmd);
	
	/*
	没有长度，没法注册push下载进度监控
	*/
	
	DEBUG("updata totalsize of brand.xml into table 'brand'\n");
	return 0;
}

/*
功能：	解析xml结点中的属性
输入：	cur		——待解析的xml结点
		xmlroute——表明从xml的跟结点到当前结点之间的路由
		ptr		——预备用来保存解析出来的属性的结构体指针
*/
static void parseProperty(xmlNodePtr cur, const char *xmlroute, void *ptr)
{
	if(NULL==cur || NULL==xmlroute){
		DEBUG("some arguments are invalide\n");
		return;
	}
	
	;//DEBUG("----------- property start -----------\n");
	xmlChar *szAttr = NULL;
	xmlAttrPtr attrPtr = cur->properties;
	int break_flag = 0;
	while(NULL!=attrPtr){
		szAttr = xmlGetProp(cur, attrPtr->name);
		if(NULL!=szAttr)
		{
			;//DEBUG("property of %s, %s: %s\n", xmlroute, attrPtr->name, szAttr);

// allpid.xml
			if(0==strcmp("allbrand^brand", xmlroute)){
				if(0==xmlStrcmp(BAD_CAST"id", attrPtr->name)){
					strcpy((char *)ptr, (char *)szAttr);
					break_flag = 1;
				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strcmp("allbrand^brand^pid", xmlroute)){
				if(0==xmlStrcmp(BAD_CAST"id", attrPtr->name)){
					strcpy((char *)ptr, (char *)szAttr);
					break_flag = 1;
				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
// column.xml
			else if(0==strcmp("grouptags^brand^columntag", xmlroute)){
				if(0==xmlStrcmp(BAD_CAST"id", attrPtr->name))
					strncpy(((DBSTAR_COLUMN_2_S *)ptr)->id, (char *)szAttr, sizeof(((DBSTAR_COLUMN_2_S *)ptr)->id)-1);
				else if(0==xmlStrcmp(BAD_CAST"columnname", attrPtr->name))
					strncpy(((DBSTAR_COLUMN_2_S *)ptr)->name, (char *)szAttr, sizeof(((DBSTAR_COLUMN_2_S *)ptr)->name)-1);
				else if(0==xmlStrcmp(BAD_CAST"parent", attrPtr->name)){
					strncpy(((DBSTAR_COLUMN_2_S *)ptr)->parent_id, (char *)szAttr, sizeof(((DBSTAR_COLUMN_2_S *)ptr)->parent_id)-1);
					if(0==strlen(((DBSTAR_COLUMN_2_S *)ptr)->parent_id))
					strncpy(((DBSTAR_COLUMN_2_S *)ptr)->parent_id, "-1", sizeof(((DBSTAR_COLUMN_2_S *)ptr)->parent_id)-1);
				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
// ProductTag.xml
			else if(0==strcmp("producttag^product^contents^content", xmlroute)){
				if(0==xmlStrcmp(BAD_CAST"id", attrPtr->name)){
					strncpy(((DBSTAR_PRODUCT_S *)ptr)->id, (char *)szAttr, sizeof(((DBSTAR_PRODUCT_S *)ptr)->id)-1);
				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strcmp("producttag^product^contents^content^column", xmlroute)){
				if(0==xmlStrcmp(BAD_CAST"id", attrPtr->name)){
					strncpy(((DBSTAR_PRODUCT_S *)ptr)->column_id, (char *)szAttr, sizeof(((DBSTAR_PRODUCT_S *)ptr)->column_id)-1);
				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
// brand_001.xml
			else if(0==strcmp("pushvod^AOC^program", xmlroute)){
				if(0==xmlStrcmp(BAD_CAST"pathid", attrPtr->name))
					strncpy(((DBSTAR_BRAND_S *)ptr)->pathid, (char *)szAttr, sizeof(((DBSTAR_BRAND_S *)ptr)->pathid)-1);
				else if(0==xmlStrcmp(BAD_CAST"cname", attrPtr->name))
					strncpy(((DBSTAR_BRAND_S *)ptr)->cname, (char *)szAttr, sizeof(((DBSTAR_BRAND_S *)ptr)->cname)-1);
				else if(0==xmlStrcmp(BAD_CAST"totalsize", attrPtr->name)){
					sscanf((char *)szAttr,"%lld", &(((DBSTAR_BRAND_S *)ptr)->totalsize));
					//((DBSTAR_BRAND_S *)ptr)->totalsize = atoi((char *)szAttr);//strtol((char *)szAttr, NULL, 10);
					DEBUG("str(%s) translate as (%lld)\n", (char *)szAttr,((DBSTAR_BRAND_S *)ptr)->totalsize);
				}
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
// PreProductTag.xml
			else if(0==strcmp("producttag^preentry", xmlroute)){
				
				//DEBUG("--------- pointer: %p\n", ptr);
				if(0==xmlStrcmp(BAD_CAST"date", attrPtr->name))
					strncpy(((DBSTAR_PREPRODUCT_S *)ptr)->preentry, (char *)szAttr, sizeof(((DBSTAR_PREPRODUCT_S *)ptr)->preentry)-1);
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strcmp("producttag^preentry^contents^content", xmlroute)){
				if(0==xmlStrcmp(BAD_CAST"id", attrPtr->name))
					strncpy(((DBSTAR_PREPRODUCT_S *)ptr)->id, (char *)szAttr, sizeof(((DBSTAR_PREPRODUCT_S *)ptr)->id)-1);
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			else if(0==strcmp("producttag^preentry^contents^content^column", xmlroute)){
				if(0==xmlStrcmp(BAD_CAST"id", attrPtr->name))
					strncpy(((DBSTAR_PREPRODUCT_S *)ptr)->column_id, (char *)szAttr, sizeof(((DBSTAR_PREPRODUCT_S *)ptr)->column_id)-1);
//				else
//					DEBUG("can NOT process such property '%s' of xml route '%s'\n", attrPtr->name, xmlroute);
			}
			
//			else
//				DEBUG("can NOT process such xml route '%s'\n", xmlroute);
//			
			xmlFree(szAttr);
		}
		attrPtr = attrPtr->next;
	}
	//DEBUG("----------- property end -----------\n\n");
	
	return;
}


static int allpid_sqlite_insert(int pid)
{
	char sqlite_cmd[256+128];
	
	sprintf(sqlite_cmd,"SELECT id FROM allpid WHERE id=%d;",pid);
	DEBUG("sqlite cmd str: %s\n", sqlite_cmd);

	int ret_sqlexec = sqlite_read(sqlite_cmd, NULL, NULL);
	if(ret_sqlexec<=0){
		snprintf(sqlite_cmd, sizeof(sqlite_cmd), "INSERT INTO allpid(id) VALUES(%d);", pid);
		sqlite_execute(sqlite_cmd);
		DEBUG("insert pid(%d) info of allpid.xml into table 'allpid'\n", pid);
		
		//int fid = alloc_filter((unsigned short)pid, 1);
		//DEBUG("set demux filter in xmlparser directly, pid=%d, fid=%d\n", (int)pid, fid);
	}
	else
		DEBUG("pid(%d) is already exist\n", pid);
	
	return 0;
}

static int pid_unregist_sqlite_callback(char **result, int row, int column, void *receiver)
{
	DEBUG("sqlite callback, row=%d, column=%d, receiver addr: %p\n", row, column, receiver);
	if(row<1){
		DEBUG("no record in table, return\n");
		return 0;
	}
	
	int i = 0;
	int pid = -1;
	for(i=1;i<row+1;i++)
	{
		pid = atoi(result[i*column]);
		//TC_free_filter(pid);
		DEBUG("free filter by pid %d\n", pid);
	}
	
	return 0;
}
static int unregist_allpid(void)
{
	char sqlite_cmd[256+128];
	int (*sqlite_callback)(char **, int, int, void *) = pid_unregist_sqlite_callback;

	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"SELECT id FROM allpid;");
	return sqlite_read(sqlite_cmd, NULL, sqlite_callback);
}

/*xmlroute仍存在重复的可能性*/
static void parseNode (xmlDocPtr doc, xmlNodePtr cur, char *xmlroute, void *ptr)
{
	if(NULL==doc || NULL==cur || NULL==xmlroute){
		DEBUG("some arguments are invalide\n");
		return;
	}
	
	xmlChar *szKey = NULL;
	char new_xmlroute[256];
	memset(new_xmlroute, 0, sizeof(new_xmlroute));
	
	cur = cur->xmlChildrenNode;
	while (cur != NULL) {
		if(XML_TEXT_NODE==cur->type){
			cur = cur->next;
			continue;
		}
		// else if(XML_ELEMENT_NODE==cur->type)...

		snprintf(new_xmlroute, sizeof(new_xmlroute), "%s^%s", xmlroute, cur->name);
		//DEBUG("cur->name: %s, XML absolute route: %s\n", cur->name, new_xmlroute);

// allpid.xml		
		if(0==strcmp(new_xmlroute, "allbrand^brand")){
#if 0	// do not check the brand id currently
			char brand_id[32];
			memset(brand_id, 0, sizeof(brand_id));
			parseProperty(cur, new_xmlroute, (void *)brand_id);
			if(0==strcmp(brand_id, SERVICE_ID)){
				DEBUG("detect valid brand_id/service_id: %s\n", brand_id);
				parseNode(doc, cur, new_xmlroute, ptr);
				break;
			}
			else
				DEBUG("this brand_id/service_id is invalid: %s\n", brand_id);
#else
			parseNode(doc, cur, new_xmlroute, ptr);
#endif
		}
		else if(0==strcmp(new_xmlroute, "allbrand^brand^pid")){
			char pid_str[32];
			memset(pid_str, 0, sizeof(pid_str));
			parseProperty(cur, new_xmlroute, (void *)pid_str);
			
			long pid = strtol(pid_str, NULL, 0); 
			/*
			下面的判断纯粹是配合测试环境使用的，测试环境下只有0x66是有效pid，其他pid都是打酱油的。
			因此，只解析出来，但不入库。
			*/
//			if(0x66L==pid)
//			{
				allpid_sqlite_insert((int)pid);
//			}
//			else
//				DEBUG("this pid is only a test case but has no using: %d\n", (int)(pid));
		}
// column.xml
		else if(0==strcmp(new_xmlroute, "grouptags^brand")){
			parseNode(doc, cur, new_xmlroute, ptr);
		}
		else if(0==strcmp(new_xmlroute, "grouptags^brand^columntag")){
			DBSTAR_COLUMN_2_S column_s;
			memset(&column_s, 0, sizeof(column_s));
			parseProperty(cur, new_xmlroute, (void *)&column_s);
			column_xml_sqlite_insert(&column_s);
		}
// ProductTag.xml
		else if(0==strcmp(new_xmlroute, "producttag^product")){
			parseNode(doc, cur, new_xmlroute, ptr);
		}
		else if(0==strcmp(new_xmlroute, "producttag^product^contents")){
			parseNode(doc, cur, new_xmlroute, ptr);
		}
		else if(0==strcmp(new_xmlroute, "producttag^product^contents^content")){
			DBSTAR_PRODUCT_S product_s;
			memset(&product_s, 0, sizeof(product_s));
			parseProperty(cur, new_xmlroute, (void *)&product_s);
			
			parseNode(doc, cur, new_xmlroute, &product_s);
		}
		else if(0==strcmp(new_xmlroute, "producttag^product^contents^content^column")){
			parseProperty(cur, new_xmlroute, ptr);
		}
		else if(0==strncmp(new_xmlroute, "producttag^product^contents^content^", strlen("producttag^product^contents^content^"))){
			szKey = xmlNodeGetContent(cur);
			DBSTAR_PRODUCT_S *p = (DBSTAR_PRODUCT_S *)ptr;
			if(0==strcmp(new_xmlroute+strlen("producttag^product^contents^content^"), "contentname"))
				strncpy(p->contentname, (char *)szKey, sizeof(p->contentname)-1);
			else if(0==strcmp(new_xmlroute+strlen("producttag^product^contents^content^"), "xmlpath")){
				int xmlpath_size = sizeof(p->xmlpath);
				//push_data_root_dir_get(p->xmlpath, xmlpath_size-1);
				/*
				路径videos1/pushvod的来源需要考证一下
				*/
				snprintf(p->xmlpath+strlen(p->xmlpath), xmlpath_size-strlen(p->xmlpath),"%s/", RELAY_DIR);
				strncpy(p->xmlpath + strlen(p->xmlpath), (char *)szKey, xmlpath_size-1);
				product_xml_sqlite_insert((DBSTAR_PRODUCT_S *)ptr);
			}
//			else
//				DEBUG("can NOT process such element '%s' in xml route '%s'\n", cur->name, xmlroute);
			xmlFree(szKey);
		}
// brand_001.xml
		else if(0==strcmp(new_xmlroute, "pushvod^AOC")){
			parseNode(doc, cur, new_xmlroute, ptr);
		}
		else if(0==strcmp(new_xmlroute, "pushvod^AOC^program")){
			DBSTAR_BRAND_S brand_s;
			memset(&brand_s, 0, sizeof(brand_s));
			parseProperty(cur, new_xmlroute, (void *)&brand_s);
			brand_xml_sqlite_insert(&brand_s);
		}
// PreProductTag.xml
		else if(0==strcmp(new_xmlroute, "producttag^preentry")){
			//DEBUG("--------- pointer: %p\n", ptr);
			parseProperty(cur, new_xmlroute, ptr);
			
			parseNode(doc, cur, new_xmlroute, ptr);
		}
		else if(0==strcmp(new_xmlroute, "producttag^preentry^contents")){
			parseNode(doc, cur, new_xmlroute, ptr);
		}
		else if(0==strcmp(new_xmlroute, "producttag^preentry^contents^content")){
			parseProperty(cur, new_xmlroute, ptr);
			
			parseNode(doc, cur, new_xmlroute, ptr);
		}
		else if(0==strcmp(new_xmlroute, "producttag^preentry^contents^content^column")){
			parseProperty(cur, new_xmlroute, ptr);
		}
		else if(0==strcmp(new_xmlroute, "producttag^preentry^contents^content^prename")){
			szKey = xmlNodeGetContent(cur);
			DBSTAR_PREPRODUCT_S *p = (DBSTAR_PREPRODUCT_S *)ptr;
			strncpy(p->prename, (char *)szKey, sizeof(p->prename)-1);
			xmlFree(szKey);
		}
		else if(0==strcmp(new_xmlroute, "producttag^preentry^contents^content^xmlpath")){
			szKey = xmlNodeGetContent(cur);
			DBSTAR_PREPRODUCT_S *p = (DBSTAR_PREPRODUCT_S *)ptr;
			snprintf(p->xmlpath, sizeof(p->xmlpath), "%s/%s", RELAY_DIR, (char *)szKey);
			xmlFree(szKey);
			
			preproduct_xml_sqlite_insert((DBSTAR_PREPRODUCT_S *)ptr);
		}
		
//		else
//			DEBUG("can NOT process such element '%s' in xml route '%s'\n", cur->name, xmlroute);
		
		cur = cur->next;
	}
	return;
}

int parseDoc(char *docname)
{
	xmlDocPtr doc;
	xmlNodePtr cur;

	if(NULL==docname){
		DEBUG("CAUTION: name of xml file is NULL\n");
		return -1;
	}
	DEBUG("parse xml file: %s\n", docname);
	
	doc = xmlParseFile(docname);
	if (doc == NULL ) {
		ERROROUT("Document not parsed successfully.\n");
		return -1;
	}
	
	int ret = 0;
	//int (*sqlite_callback)(char **,int,int,void *) = sqlite_read_version_callback;
	char old_version[32];
	char sqlite_cmd[256];
	memset(old_version, 0, sizeof(old_version));
	memset(sqlite_cmd, 0, sizeof(sqlite_cmd));

	cur = xmlDocGetRootElement(doc);
	if (cur == NULL) {
		ERROROUT("empty document\n");
		ret = -1;
	}
	else{
// allpid.xml
		if(0==xmlStrcmp(cur->name, BAD_CAST"allbrand")){
			unregist_allpid();
			sqlite_table_clear("allpid");
			parseNode(doc, cur, "allbrand", NULL);
		}
// column.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"grouptags")){
			sqlite_table_clear("column");
			parseNode(doc, cur, "grouptags", NULL);
		}
// ProductTag.xml
		else if(strstr(docname, "/ProductTag.xml") && 0==xmlStrcmp(cur->name, BAD_CAST"producttag")){
			parseNode(doc, cur, "producttag", NULL);
		}
// brand_0001.xml
		else if(0==xmlStrcmp(cur->name, BAD_CAST"pushvod")){
			sqlite_table_clear("brand");
			parseNode(doc, cur, "pushvod", NULL);
			push_monitor_reset();
		}
// PreProductTag.xml
		else if(strstr(docname, "/PreProductTag.xml") && 0==xmlStrcmp(cur->name, BAD_CAST"producttag")){
			sqlite_table_clear("preproduct");
			DBSTAR_PREPRODUCT_S preproduct_s;
			memset(&preproduct_s, 0, sizeof(&preproduct_s));
			parseNode(doc, cur, "producttag", (void *)&preproduct_s);
		}
		
		
		else{
			ERROROUT("xml file has wrong root node with '%s'\n", cur->name);
			ret = -1;
		}
	}
	
	xmlFreeDoc(doc);
	return ret;
}
