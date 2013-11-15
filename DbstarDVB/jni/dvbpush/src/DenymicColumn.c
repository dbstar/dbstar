
ALTER TABLE Column ADD COLUMN Visible INTEGER DEFAULT 1;

// 1,找到所有非一级栏目的叶子节点，并且这些叶子节点下面是无节目
SELECT ColumnID FROM Column WHERE (ColumnType='1' OR ColumnType='2') AND ParentID!='-1' AND ColumnID NOT IN(SELECT ParentID FROM Column) AND ColumnID NOT IN (SELECT DISTINCT ColumnID FROM Publication where ColumnID!='-1');
SELECT ColumnID FROM Column WHERE (ColumnType='1' OR ColumnType='2') AND ColumnID NOT IN(SELECT ParentID FROM Column) AND ColumnID NOT IN (SELECT DISTINCT ColumnID FROM Publication where ColumnID!='-1');
UPDATE Column SET Visible=0 WHERE ColumnID IN (SELECT ColumnID FROM Column WHERE (ColumnType='1' OR ColumnType='2') AND ColumnID NOT IN(SELECT ParentID FROM Column) AND ColumnID NOT IN (SELECT DISTINCT ColumnID FROM Publication where ColumnID!='-1'));

// 2,找到这些非一级栏目的空载叶子节点的父节点
SELECT DISTINCT ParentID FROM Column WHERE ColumnID IN (SELECT ColumnID FROM Column WHERE (ColumnType='1' OR ColumnType='2') AND ParentID!='-1' AND ColumnID NOT IN(SELECT ParentID FROM Column) AND ColumnID NOT IN (SELECT DISTINCT ColumnID FROM Publication where ColumnID!='-1'));

// 3,获取Publication表中ColumnID不等于-1的ColumnID个数
select count(ColumnID) FROM Publication where ColumnID!='-1';
	获取Publication表中ColumnID不定于-1的不重复的ColumnID的个数
select count(distinct ColumnID) FROM Publication where ColumnID!='-1';
	统计父目录ID以及对应的子目录个数
select ParentID,count(*) from Column group by ParentID;
	
// 找到所有非一级栏目的父节点
select DISTINCT ParentID from Column where ParentID!='-1' and (ColumnType='1' or ColumnType='2') group by ParentID;

CREATE TABLE Column(
ServiceID   NVARCHAR(64) DEFAULT '0',
ColumnID       NVARCHAR(64) DEFAULT '',
ParentID        NVARCHAR(64) DEFAULT '',
Path        NVARCHAR(256) DEFAULT '',
ColumnType     NVARCHAR(256) DEFAULT '',
ColumnIcon_losefocus   NVARCHAR(256) DEFAULT '',
ColumnIcon_getfocus        NVARCHAR(256) DEFAULT '',
ColumnIcon_onclick     NVARCHAR(256) DEFAULT '',
ColumnIcon_spare       NVARCHAR(256) DEFAULT '',
SequenceNum        INTEGER DEFAULT 100,
URI NVARCHAR(256) DEFAULT '',
TimeStamp NOT NULL DEFAULT (datetime('now','localtime')), 
Visible INTEGER DEFAULT 1,
PRIMARY KEY (ServiceID,ColumnID));

replace into Column() values();


static int column_visible_proc_cb(char **result, int row, int column, void *receiver, unsigned int receiver_size)
{
	DEBUG("sqlite callback, row=%d, column=%d\n", row, column);
	if(row<1){
		DEBUG("no record in table Column, return\n");
		return 0;
	}
	
// ColumnID,ParentID,Visible
	int m = 0, i = 0, j = 0;
	char *gene_cmd = (char *)receiver;
	
	for(m=0;m<16;m++){	// 最多支持16级目录扫描
		for(i=1;i<row+1;i++){
			if(atoi(result[i*column+2])<=0){
				for(j=1;j<row+1;j++){
					if( (i!=j) && (0==strcmp(result[j*column+1],result[i*column+0])) && aoti(result[j*column+2])>0 ){
						snprintf(result[i*column+2],sizeof(result[i*column+2]),"1");
						break;
					}
				}
			}
		}
	}
	
	PRINTF("addr of receiver: %p, addr of gene_cmd: %p\n",receiver,gene_cmd);
	snprintf(gene_cmd,receiver_size,"UPDATE Column SET Visible=1 WHERE");
	for(i=1;i<row+1;i++){
		if(atoi(result[i*column+2])>0){
			if(strlen(gene_cmd)>strlen("UPDATE Column SET Visible=1 WHERE"))
				snprintf(gene_cmd+strlen(gene_cmd),receiver_size-strlen(gene_cmd)," OR");
			snprintf(gene_cmd+strlen(gene_cmd),receiver_size-strlen(gene_cmd)," ColumnID='%s'",result[i*column+0]);
		}
	}
	snprintf(gene_cmd+strlen(gene_cmd),receiver_size-strlen(gene_cmd),";");
	
	return 0;
}

/*
在更新下发的Column时，除了一级栏目（ParentID为-1），总是置Visible为0。
*/
int column_visible_proc()
{
	char sqlite_cmd[512];
	char sqlite_cmd_secondary[8192];
	int ret = -1;
	int (*sqlite_callback)(char **, int, int, void *, unsigned int) = column_visible_proc_cb;
	
	// 将ColumnID非-1的叶子且其下有Publication的节点Visible置为1
	snprintf(sqlite_cmd,sizeof(sqlite_cmd),"UPDATE Column SET Visible=1 WHERE ColumnID IN (SELECT ColumnID FROM Column WHERE ColumnID NOT IN(SELECT DISTINCT ParentID FROM Column) AND ColumnID IN (SELECT DISTINCT ColumnID FROM Publication where ColumnID!='-1'));");
	sqlite_execute(sqlite_cmd);
	
	snprintf(sqlite_cmd,size(sqlite_cmd),"SELECT ColumnID,ParentID,Visible FROM Column WHERE (ColumnType='%d' OR ColumnType='%d') AND ParentID!='-1';",COLUMNTYPE_SINGLEPROG,COLUMNTYPE_MULTIPROG);
	
	PRINTF("addr of sqlite_cmd_secondary: %p\n",sqlite_cmd_secondary);
	ret = sqlite_read(sqlite_cmd, sqlite_cmd_secondary, sizeof(sqlite_cmd_secondary), sqlite_callback);
	PRINTF("ret: %d\n", ret);
	if(ret>0){
		PRINTF("sqlite_cmd_secondary: %s\n", sqlite_cmd_secondary);
		sqlite_execute(sqlite_cmd_secondary);
		
		return 0;
	}
	else
		return -1;
}

