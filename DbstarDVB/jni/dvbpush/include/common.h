//ȫ��ʹ�õ�ͷ�ļ����������������ļ���������

#ifndef __COMMON_H__
#define __COMMON_H__
#include <errno.h>

extern int debug_level_get(void);

// �����tuner�źŰ汾������˺ꣻ����������汾
//#define TUNER_INPUT

#define DVBPUSH_DEBUG_ANDROID 1
#if DVBPUSH_DEBUG_ANDROID
#include <android/log.h>
#define LOG_TAG "dvbpush"

#define PRINTF(x...) do{__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, x);} while(0)

#define DEBUG(x...) do { \
	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "[%s:%d] ", __FUNCTION__, __LINE__); \
	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, x); \
} while(0)

#define ERROROUT(x...) do { \
	__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "[%s:%s:%d] ", __FILE__, __FUNCTION__, __LINE__); \
	if (errno != 0) \
		__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "[err note: %s]",strerror(errno)); \
	__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, x); \
} while(0)

#else

#define PRINTF(x...) do{printf(x);} while(0)

#define DEBUG(x...) do{printf("[%s:%s:%d] ", __FILE__, __FUNCTION__, __LINE__);printf(x);}while(0)

#define ERROROUT(ERRSTR...) \
			do{printf("[%s:%s:%d] ", __FILE__, __FUNCTION__, __LINE__);\
			if(errno!=0) printf("[err note: %s]",strerror(errno));\
			printf(ERRSTR);}while(0)
#endif

#define MIN_LOCAL(a,b) ((a)>(b)?(b):(a))

typedef enum{
	BOOL_FALSE = 0,
	BOOL_TRUE
}BOOL_E;

/*
 �ж˰汾�ţ����Ǵ�loader��ȡ�ģ����Ǹ��ݹ�����Ҫ����ģ�ÿ�η����汾��Ҫ�ֹ�����
*/
#define HARDWARE_VERSION	"6.1"
#define LOADER_VERSION		"1.2.2"


// �����DRM��֤�汾������˺�
//#define DRM_TEST

// ������Ŀ
//#define SMARTLIFE_LC

// ������Ŀ����ý�����
//#define MEDIASHARING_LC

// ������Ŀ���ļ����
//#define FILEBROWSER_LC

// ������Ŀ���ҵ�Ӧ��
//#define MYAPP_LC

// ������Ŀ�������
//#define WEBBROWSER_LC

/*
��������ʹ�õ����á�fifo�ļ���
*/
#define	WORKING_DATA_DIR		"/data/dbstar"
#define	MSG_FIFO_ROOT_DIR		WORKING_DATA_DIR"/msg_fifo"
#define PUSH_CONF_SEED			"/system/etc/dbstar/push.conf"
#define PUSH_CONF_WORKING		WORKING_DATA_DIR"/push.conf"
#define INITIALIZE_XML_URI		"pushroot/initialize/Initialize.xml"
#define MOTHERDISC_XML_URI		"ContentDelivery.xml"

/*
�������й����в��������ݣ����������ص�ƬԴ����Ӧ�����ݿ�
*/
#define PUSH_STORAGE_HD		"/storage/external_storage/sda1"	// ���ص�Ӳ�̵�Ŀ¼
#define PUSH_STORAGE_FLASH	WORKING_DATA_DIR			// ���ص�flash��Ŀ¼
#define UDISK_MOUNT_PREFIX	"/storage/external_storage/sdb"		// U�̹���·��ǰ׺��ֻ����drm���ԣ�ǰ��������Ӳ�̣����U�̣�������������豸

#define DB_PROTOTYPE		"/system/etc/dbstar/Dbstar.db"	// ϵͳ���õ����ݿ�ԭ�ͣ��ڴ˻����������������ҵ��ʹ��
#define DB_MAIN_URI			WORKING_DATA_DIR"/Dbstar.db"	// �����洢�ն˻�����Ϣ�������ݿ⣬�Լ�flash�洢СƬ����Ϣ
#define DB_SUB_NAME				"Dbstar.db"		// �����洢���ص�Ӳ���еĽ�Ŀ��Ϣ�����ݿ�Ҳ�洢��Ӳ����
#define SMARTHOME_DATABASE		WORKING_DATA_DIR"/Smarthome.db"

// �״ο���Launcher��Ҫ���������ʼ������ʼ����Ϻ�Launcherд��˱���ļ���Ŀǰ���ݽ�һ���ַ���1��
#define NETWORK_INIT_FLAG		"/data/data/com.dbstar/files/flag"
#define DEVICE_NUM_CHANGED_FLAG	"/cache/recovery/last_log"

#define	SHOW_FLASH_COLUMNTYPE			(12)
#define ROOT_CHANNEL		(400)	// 0x190
#ifdef TUNER_INPUT
	#define MULTI_BUF_SIZE	(524288)	/* (524288)=(512*1024) */
#else
	#define MULTI_BUF_SIZE	(16171008)	/* (16171008)=(12*1024*1316) */
#endif
//#define MULTI_BUF_SIZE		(86245376)	/* (86245376)=(64*1024*1316) ~ 79M */
//#define MULTI_BUF_SIZE		(43122688)	/* (43122688)=(32*1024*1316) ~ 41M */

#define SERVICEID_FILL		"0"
#define XML_ROOT_ELEMENT	"RootElement"
#define EXTENSION_STR_FILL	"Extension"
#define OBJID_PAUSE			"_|_"
#define OBJ_SERVICE			"Service"
#define OBJ_PRODUCT			"Product"
#define OBJ_PUBLICATIONSSET	"PublicationsSet"
#define OBJ_PUBLICATION		"Publication"
#define OBJ_MFILE			"MFile"
#define OBJ_COLUMN			"Column"
#define OBJ_GUIDELIST		"GuideList"
#define OBJ_PRODUCTDESC		"ProductDesc"
#define OBJ_MESSAGE			"Message"
#define OBJ_PREVIEW			"Preview"

#define GLB_NAME_SERVICEID			"serviceID"
#define GLB_NAME_PUSHDIR			"PushDir"		// ��ֵ��DbstarLauncher.apk����Ӳ���Ƿ���أ���libpush����ǰ���г�ʼ��
#define GLB_NAME_COLUMNRES			"ColumnRes"
#define GLB_NAME_PREVIEWPATH		"PreviewPath"
#define GLB_NAME_CURLANGUAGE		"CurLanguage"
#define GLB_NAME_DATASOURCE			"PushSource"
#define GLB_NAME_PRODUCTSN			"ProductSN"
#define GLB_NAME_DEVICEMODEL		"DeviceModel"
#define GLB_NAME_HARDWARE_VERSION	"HardwareVersion"
#define GLB_NAME_SOFTWARE_VERSION	"SoftwareVersion"
#define GLB_NAME_LOADER_VERSION		"LoaderVersion"
#define GLB_NAME_DBDATASERVERIP		"DBDataServerIP"
#define GLB_NAME_DBDATASERVERPORT	"DBDataServerPort"
#define GLB_NAME_REBOOT_TIMESTAMP	"RebootStamp"
#define GLB_NAME_TUNERARGS			"TunerArgs"
#define GLB_NAME_TUNERARGS_DFT		"TunerArgsDefault"
#define GLB_NAME_STORAGE_ID			"storage_id"			// ��¼�������ݴ洢��ʲô�豸�ϣ������ж��Ƿ����Ӳ�̻���Ӳ��

#define INITIALIZE_MIDPATH	"pushroot/initialize"
#define DBSTAR_PREVIEWPATH	"/mnt/sda1/dbstar/PreView"
#define COLUMNRES_DIR			"ColumnRes"
#define LOCALCOLUMNICON_DIR		"LocalColumnIcon"
#define STORAGE_HD_MARK_FILE		".hd_mark"		// �ļ��м�¼һ���������ʾһ��Ӳ�̣�������������Ӳ��

#define LOCAL_COLUMNICON_ORIGIN_DIR	"/system/etc/dbstar/ColumnRes/LocalColumnIcon"

#define CURLANGUAGE_DFT				"cho"
#define DEVICEMODEL_DFT				"02"
#define DBDATASERVERIP_DFT			"239.1.7.5"
#define DBDATASERVERPORT_DFT		"4321"
#define TUNERARGS_DFT				"12620\t43200\t11300\t0\t0"

#define STORAGE_ID_FLASH		"flash"
#define STORAGE_ID_HD_DFT		"hd"	// �洢�豸ΪӲ�̣������޷�������ʶ��ֵsn�������������Ӳ�̵�sn

// ��Ӳ������ʱ��ÿ����������С��������Ϊ32G�����ڼ����Ƿ���Ҫ��������
#define DOWNLOAD_ONCE_MIN			(34359738368LL)

#define STORAGE_FLASH_SIZE			(1073741824LL - 50000000LL)

typedef enum{
	NAVIGATIONTYPE_NOCOLUMN = 0,
	NAVIGATIONTYPE_COLUMN
}NAVIGATIONTYPE_E;

/*
Ĭ�ϵĳ�ʼ���ļ�uri�������push��·����uri��������Initialize.xml��Channel.xml��·��
�������й����п��ܻᱻ����
*/
typedef enum{
	PUSH_XML_FLAG_UNDEFINED	= -1,
	PUBLICATION_DIR			= 0,
	
	INITIALIZE_XML			= 100,
	COLUMN_XML				= 101,
	GUIDELIST_XML			= 102,
	COMMANDS_XML			= 104,
	MESSAGE_XML				= 105,
	PRODUCTDESC_XML			= 106,
	SERVICE_XML				= 107,
	SPRODUCT_XML			= 108,
	
// defined myself
	PUBLICATION_XML			= 10000,
	
	
	PUSH_XML_FLAG_MAXLINE
}PUSH_XML_FLAG_E;

/*
��ý��Ҷ����Ŀ���ڸ�ý���ڲ��ķ��࣬������ʾ����Ŀλ�ã���Ҫ���⴦��������ж�����200����203
*/
typedef enum{
	COLUMN_TYPE_MOVIE = 1,
	COLUMN_TYPE_TVSERIAL = 2,
	COLUMN_TYPE_BOOK = 3,
	COLUMN_TYPE_MAGAZINE = 4,
	COLUMN_TYPE_NEWSPAPER = 5,
	COLUMN_TYPE_MUSIC = 6,
	COLUMN_TYPE_CARTOON = 7,
	COLUMN_TYPE_FLASH = 8,
	COLUMN_TYPE_APP = 9,
	COLUMN_TYPE_WEBSITE = 10,
	
	COLUMN_TYPE_MYCENTER = 101,
	COLUMN_TYPE_SETTINGS = 102,
	COLUMN_TYPE_SMARTPOWER = 103,
	COLUMN_TYPE_SMARTHOME = 104,
	
	COLUMN_TYPE_LEAF_BOOK = 200,
	COLUMN_TYPE_LEAF_MAGAZINE = 201,
	COLUMN_TYPE_LEAF_NEWSPAPER = 202,
}COLUMN_TYPE_E;

/*
	���ֵܽڵ��У�ֻ��Ҫ�����ڵ��ڲ���Ϣ����Ϻ���Ҫ����ɨ�������ֵܽڵ㣬����process_overΪ1��
	�������������д����ж��������Ϸ�ʱ�ݹ�parseNode�����ڲ����ڲ�������Ϻ������ڵ������
	1���Ѿ��ҵ��Ϸ��ķ�֧��ʣ�µķ�֧�����������ǰ�˳�
	2����ҵ���߼����жϲ���Ҫ���������磬ServiceID��ƥ�䣬���߰汾���
	3��������������ǰ�˳�
*/
typedef enum{
	XML_EXIT_NORMALLY = 0,
	XML_EXIT_MOVEUP = 1,
	XML_EXIT_UNNECESSARY = 2,
	XML_EXIT_ERROR = 3,
}XML_EXIT_E;


/*
	����֧�ֶ�ҵ������Ҫ���Ƿ�ע����յ��ն˶��ԣ���Ҫ��¼ҵ���״̬��
	0������ҵ����Ч
	1������ҵ�����ն���Ҫ֧�ֵ�ҵ��֮һ�������ǵ�ǰ��Ч��ҵ��ֻ���ڶ�ҵ��֧��ʱ������Ҫ��ֵ��
	2������ҵ�����ն˵ĵ�ǰ��Чҵ��
*/
typedef enum{
	SERVICE_STATUS_INVALID	= 0,
	SERVICE_STATUS_VALID	= 1,
	SERVICE_STATUS_EFFECT	= 2
}SERVICE_STATUS_E;


/*
	����Ͷ�ݵ��еĽ�Ŀ����
*/
typedef enum{
	RECEIVETYPE_SEQUENCE	= 0,
	RECEIVETYPE_PUBLICATION	= 1,
	RECEIVETYPE_SPRODUCT	= 2,
	RECEIVETYPE_COLUMN		= 4,
	RECEIVETYPE_PREVIEW		= 8,
//	RECEIVETYPE_ALL			= 100
}RECEIVETYPE_E;

/*
	����Ͷ�ݵ��еĲ�ͬ��Ŀ���͵Ľ���˳��
*/
typedef enum{
	RECV_SEQUENCE_ALL	= 0,	// ����˳��ȫ������
	RECV_SEQUENCE_1		= 1,	// ˳��1
	RECV_SEQUENCE_2		= 2,	// ˳��2
	
	RECV_SEQUENCE_TAIL	= 10000	// ��β��˳�򿿺�
}RECV_SEQUENCE_E;

/*
	����״̬
*/
typedef enum{
	RECEIVESTATUS_REJECT		= -2,
	RECEIVESTATUS_FAILED		= -1,
	RECEIVESTATUS_WAITING		= 0,
	RECEIVESTATUS_FINISH		= 1,
	RECEIVESTATUS_HISTORY		= 2,
	RECEIVESTATUS_REJECT_TMP	= 3
}RECEIVESTATUS_E;

/*
 dvbpush��ʼ������������
*/
typedef enum{
	RELY_CONDITION_NET = 1,
	RELY_CONDITION_HD = 2,
	RELY_CONDITION_UPGRADE = 4,
	
	RELY_CONDITION_EXIT = 128
}RELY_CONDITION_E;


/*
��Ʒ�Ʒ���Ӫ����
*/
typedef enum{
	PRODUCTTYPE_INVALID = 0,
	PRODUCTTYPE_VOD = 1,				// �㲥��Ʒ
	PRODUCTTYPE_WHOLESALE_BY_MON = 2,	// ���²�Ʒ
	PRODUCTTYPE_PACKAGE = 3,			// ר���Ʒ
	PRODUCTTYPE_SPECIAL = 4				// �����Ʒ
}PRODUCTTYPE_E;


/*
��Ʒ����
*/
typedef enum{
	PRODUCTFLAG_INVALID = 0,
	PRODUCTFLAG_NORMAL = 1,		// ��ͨ��Ʒ����Ʒ��
	PRODUCTFLAG_PREVIEW = 2,	// СƬ��Ʒ
	PRODUCTFLAG_SPRODUCT = 3	// �����Ʒ
}PRODUCTFLAG_E;

/*
��Ʒ����
*/
typedef enum{
	PUBLICATIONTYPE_RM = 1,
	PUBLICATIONTYPE_VA = 2,
	PUBLICATIONTYPE_APP = 3
}PUBLICATIONTYPE_E;

/*
��ý�����
*/
typedef enum{
	RMCATEGORY_BOOKS = 1,
	RMCATEGORY_MAGAZINE = 2,
	RMCATEGORY_NEWSPAPER = 3
}RMCATEGORY_E;

/*
���ز���pushʱʹ�ã����hytd.ts������������������¹رմ˺ꡣ
*/
//#define PUSH_LOCAL_TEST

typedef struct{
	char	Name[64];
	char	Value[512];
	char	Param[256];
}DBSTAR_GLOBAL_S;

typedef struct{
	char	productID[64];
	char	productName[64];
	char	serviceID[64];
}DBSTAR_PRODUCT_SERVICE_S;

typedef struct{
	char	PushFlag[64];
	char	ServiceID[64];
	char	XMLName[64];
	char	Version[64];
	char	StandardVersion[64];
	char	URI[256];
	char	ID[64];
}DBSTAR_XMLINFO_S;

typedef struct{
	char	ServiceID[64];
	char	pid[64];
	char	pidtype[64];
	char	multiURI[64];
}DBSTAR_CHANNEL_S;

typedef struct{
	char	ServiceID[64];
	char	ObjectName[128];
	char	EntityID[64];
	char	StrLang[32];
	char	StrName[64];
	char	StrValue[8192];
	char	Extension[64];	// "Extension" or ""
}DBSTAR_RESSTR_S;

typedef struct{
	char	ServiceID[64];
	char	ObjectName[64];
	char	EntityID[64];
	char	SubTitleID[64];
	char	SubTitleName[64];
	char	SubTitleLanguage[64];
	char	SubTitleURI[256];
}DBSTAR_RESSUBTITLE_S;

typedef struct{
	char	ServiceID[64];
	char	ObjectName[64];
	char	EntityID[64];
	char	TrailerID[64];
	char	TrailerName[64];
	char	TrailerURI[256];
}DBSTAR_RESTRAILER_S;

typedef struct{
	char	ServiceID[64];
	char	ObjectName[64];
	char	EntityID[64];
	char	PosterID[64];
	char	PosterName[256];
	char	PosterURI[512];
}DBSTAR_RESPOSTER_S;

typedef struct{
	char	ServiceID[64];
	char	ObjectName[64];
	char	EntityID[64];
	char	Name[64];
	char	Type[64];
}DBSTAR_RESEXTENSION_S;

typedef struct{
	char	ServiceID[64];
	char	ObjectName[256];
	char	EntityID[64];
	char	FileID[64];
	char	FileName[64];
	char	FileURI[256];
}DBSTAR_RESEXTENSIONFILE_S;

typedef struct{
	char	ServiceID[64];
	char	RegionCode[64];
	char	OnlineTime[32];
	char	OfflineTime[32];
	char	Status[32];
}DBSTAR_SERVICE_S;

typedef struct{
	char	ServiceID[64];
	char	ProductID[64];
	char	ProductType[64];
	char	Flag[64];
	char	OnlineDate[64];
	char	OfflineDate[64];
	char	IsReserved[64];
	char	Price[64];
	char	CurrencyType[64];
	char	DRMFile[256];
}DBSTAR_PRODUCT_S;

typedef struct{
	char	type[64];
	char	uri[256];
}COLUMNICON_S;

typedef struct{
	char	ServiceID[64];
	char	ColumnID[64];
	char	ParentID[64];
	char	Path[256];
	char	ColumnType[256];
	char	ColumnIcon_losefocus[256];
	char	ColumnIcon_getfocus[256];
	char	ColumnIcon_onclick[256];
}DBSTAR_COLUMN_S;

typedef struct{
	char	ServiceID[64];
	char	DateValue[64];
	char	GuideListID[64];
	char	productID[64];
	char	PublicationID[64];
}DBSTAR_GUIDELIST_S;

typedef struct{
	char	ServiceID[64];
	char	ReceiveType[64];
	char	rootPath[256];
	char	ProductDescID[128];
	char	productID[64];
	char	SetID[64];
	char	ID[64];
	char	TotalSize[64];
	char	URI[256];
	char	DescURI[384];
	char	PushStartTime[64];
	char	PushEndTime[64];
	char	Columns[512];	// it's better to use malloc and relloc
	char	ReceiveStatus[32];
	char	version[64];
}DBSTAR_PRODUCTDESC_S;

typedef struct{
	char	ServiceID[64];
	char	SetID[64];
	char	PublicationType[64];
	char	IsReserved[64];
	char	Visible[64];
	char	Title[128];
	char	Starring[1024];
	char	Scenario[8192];
	char	Classification[64];
	char	Period[64];
	char	CollectionNumber[64];
	char	Review[64];
}DBSTAR_PUBLICATIONSSET_S;

typedef struct{
	char	serviceID[64];
	char	navigationType[64];
}DBSTAR_NAVIGATION_S;

typedef struct{
	char	ServiceID[64];
	char	columnID[64];
	char	EntityID[64];
}DBSTAR_COLUMNENTITY_S;

typedef struct{
	char	ServiceID[64];
	char	PublicationID[64];
	char	PublicationName[512];	// ����⣬ֻ��ת����Ĭ������cho��PublicationName�����������õ�DBSTAR_MULTIPLELANGUAGEINFORM_S�е�Title��������ƨ������
	char	PublicationType[64];
	char	IsReserved[32];
	char	Visible[32];
	char	DRMFile[256];
	char	FileID[64];
	char	FileSize[64];
	char	FileURI[256];
	char	FileType[64];
	char	Duration[32];
	char	Resolution[32];
	char	BitRate[32];
	char	FileFormat[32];
	char	CodeFormat[32];
	char	SetID[64];
	char	SetName[512];
	char	SetDesc[1024];
	char	SetPosterID[64];
	char	SetPosterName[512];
	char	SetPosterURI[512];
	char	RMCategory[32];		// RMCategory��������Publication�⣬ֻ�������жϡ���ֽ������
}DBSTAR_PUBLICATION_S;

/*
 ��ԱPublicationType��IsReserved��Visibleֻ��Ϊ�˽��ṹ��Publication�е���Ϣ͸����PublicationsSet
*/
typedef struct{
	char	ServiceID[64];
	char	PublicationID[64];
	char	infolang[64];
	char	PublicationDesc[8192];
	char	Keywords[1024];
	char	ImageDefinition[32];
	char	Area[512];
	char	Language[64];
	char	Episode[32];
	char	AspectRatio[32];
	char	AudioChannel[32];
	char	Director[512];
	char	Actor[1024];
	char	Audience[512];
	char	Model[32];
}DBSTAR_MULTIPLELANGUAGEINFOVA_S;

typedef struct{
	char	ServiceID[64];
	char	PublicationID[64];
	char	infolang[64];
	char	PublishID[64];
	char	RMCategory[32];
	char	Author[512];
	char	Publisher[512];
	char	Issue[64];
	char	Keywords[512];
	char	Description[1024];
	char	PublishDate[64];
	char	PublishWeek[32];
	char	PublishPlace[256];
	char	CopyrightInfo[256];
	char	TotalEdition[64];
	char	Data[64];
	char	Format[64];
	char	TotalIssue[64];
	char	Recommendation[1024];
	char	Words[32];			// ��������ͼ�顱ʱ����
	char	Title[256];			// Publication.xml��û������ֶΣ��������ڵ�DbstarLauncherȴ��Title��ȡֵ��������ʱ��dvbpush��ͨ���ݣ���SetName������Title��
	
	// SetInfo�ڵ���ʱ�洢��DBSTAR_MULTIPLELANGUAGEINFORM_S���ȴ�����Ϻ�Ҫ�ȿ���ΪDBSTAR_PUBLICATION_S��Ȼ��ͨ��DBSTAR_PUBLICATION_S�洢�����ݿ�֮Publication����
	char	SetID[64];
	char	SetName[512];
	char	SetDesc[1024];
	char	SetPosterID[64];
	char	SetPosterName[512];
	char	SetPosterURI[512];
}DBSTAR_MULTIPLELANGUAGEINFORM_S;

typedef struct{
	char	ServiceID[64];
	char	PublicationID[64];
	char	infolang[64];
	char	*PublicationDesc;
	char	Keywords[256];
	char	Category[64];
	char	Released[64];
	char	AppVersion[64];
	char	Language[64];
	char	Developer[64];
	char	Rated[64];
}DBSTAR_MULTIPLELANGUAGEINFOAPP_S;


typedef struct{
	char	ServiceID[64];
	char	MessageID[64];
	char	type[64];
	char	displayForm[64];
	char	StartTime[64];
	char	EndTime[64];
	char	Interval[64];
}DBSTAR_MESSAGE_S;

typedef struct{
	char	ServiceID[64];
	char	PublicationID[64];
	char	PreviewID[64];
	char	PreviewType[64];
	char	PreviewSize[64];
	char	ShowTime[64];
	char	PreviewURI[256];
	char	PreviewFormat[64];
	char	Duration[64];
	char	Resolution[64];
	char	BitRate[64];
	char	CodeFormat[64];
}DBSTAR_PREVIEW_S;


typedef struct{
	char	ServiceID[64];
	char	SType[64];
	char	Name[64];
	char	URI[256];
}DBSTAR_SPRODUCT_S;



typedef enum{
	DBSTAR_CMD_OP_UNDEFINED = 0,
	DBSTAR_CMD_OP_DELETE = 1,
	DBSTAR_CMD_OP_UPDATE = 2,
	DBSTAR_CMD_OP_CANCELRESERVATION = 3,
	DBSTAR_CMD_OP_RESERVE = 4,
	DBSTAR_CMD_OP_FORCEDISPLAY = 5,
	DBSTAR_CMD_OP_FORCEHIDE = 6
}DBSTAR_CMD_OP_TYPE_E;

typedef enum{
	DBSTAR_CMD_OBJ_UNDEFINED = 0,
	DBSTAR_CMD_OBJ_PUBLICATION = 1,
	DBSTAR_CMD_OBJ_PRODUCT = 2,
	DBSTAR_CMD_OBJ_PREVIEW = 3
}DBSTAR_CMD_OBJ_TYPE_E;

typedef enum{
	DBSTAR_CMD_OBJ_FILE_UNDEFINED = 0,
	DBSTAR_CMD_OBJ_FILE_DESCRIPTION = 1,
	DBSTAR_CMD_OBJ_FILE_SUBTITLE = 2,
	DBSTAR_CMD_OBJ_FILE_POSTER = 3
}DBSTAR_CMD_OBJ_FILE_TYPE_E;

typedef struct{
	char						ID[64];
	DBSTAR_CMD_OBJ_FILE_TYPE_E	fileType;
}DBSTAR_CMD_OBJ_S;

typedef struct{
	DBSTAR_CMD_OP_TYPE_E	type;
	DBSTAR_CMD_OBJ_TYPE_E	objectType;
	DBSTAR_CMD_OBJ_S		object;			
}DBSTAR_CMD_OPERATION_S;


////////////////////////////////////////////////////////////////////////////////
// for dmx
#define FILTER_BUF_SIZE (4096+184)

#ifdef TUNER_INPUT
	#define MAX_CHAN_FILTER 32
	#define DMX_FILTER_SIZE 16
#else
	#define MAX_CHAN_FILTER 16
	#define DMX_FILTER_SIZE 8  //16
#endif

typedef void (*dataCb) (int fid, const unsigned char *data, int len, void *user_data);

#define HIGH_PRIORITY_FILTER_NUM 2

typedef enum {
        CHAN_STAGE_START,
        CHAN_STAGE_HEADER,
        CHAN_STAGE_PTS,
        CHAN_STAGE_PTR,
        CHAN_STAGE_DATA_SEC,
        CHAN_STAGE_DATA_PES,
        CHAN_STAGE_END
} ChannelStage_t;


typedef struct LoaderInfo LoaderInfo_t;
/*struct LoaderInfo {
    unsigned int stb_id_h;  //64bit
    unsigned int stb_id_l;
    unsigned char software_version[4]; //32bit
    unsigned char hardware_version[4]; //32bit
    unsigned int img_len;          //32bit
    int fid;                       //32bit
    unsigned short oui;            //16bit
    unsigned short model_type;     //16bit
    unsigned short user_group_id;  //16bit
    unsigned char  download_type;  //8bit
    unsigned char  file_type;      //8bit
	char guodian_serialnum[24];
};*/
struct LoaderInfo {
    char oui[3];
    char hardware_version[16];
    char software_version[16];
    char model_type[3];
    char user_group_id[3];
    char stbid[32];
    char file_type[3];
    char download_type[3];
    unsigned int img_len;          //32bit
    int fid;                      //32bit
};

/*
������صĶ���
*/
//#define UPGRADEFILE_ALL "/tmp/upgrade.zip"
#define UPGRADEFILE_IMG "/cache/upgrade.zip"
#define COMMAND_FILE  "/cache/command0"
#define COMMAND_FILE1  "/cache/command1"
#define LOADER_PACKAGE_SIZE		(4084)

#define UPGRADE_PARA_STRUCT "/cache/recovery/last_log"
#define TC_OUI "03"
#define TC_MODEL_TYPE "02"
#define TC_HARDWARE_VERSION0 0
#define TC_HARDWARE_VERSION1 0
#define TC_HARDWARE_VERSION2 6
#define TC_HARDWARE_VERSION3 1
 
typedef struct Channel Channel_t;
struct Channel {
        int              bytes;
        int              fid;
        int              offset;
        int              sec_len;
        void             *userdata;
        dataCb           hdle;
        unsigned short   pid;
        unsigned char    buf[FILTER_BUF_SIZE];
        unsigned char    used;
        unsigned char    value[DMX_FILTER_SIZE+2];
        unsigned char    maskandmode[DMX_FILTER_SIZE+2];
        unsigned char    maskandnotmode[DMX_FILTER_SIZE+2];
        unsigned char    neq;
        ChannelStage_t   stage;
        unsigned char    samepidnum;
        unsigned char	 cc;
};

struct Filterp {
        unsigned char filter[DMX_FILTER_SIZE];
        unsigned char mask[DMX_FILTER_SIZE];
        unsigned char mode[DMX_FILTER_SIZE];
};
typedef struct Filterp Filter_param;


#define PUSH_PID_NUM	16
typedef struct push_pid{
	int pid;
	char pid_type[32];
	int fresh_flag;	// -1: useless pid need free; 0: has alloc already; 1: new pid need alloc
}PUSH_PID;

////////////////////////////////////////////////////////////////////////////////


int appoint_str2int(char *str, unsigned int str_len, unsigned int start_position, unsigned int appoint_len, int base);
unsigned int randint();
int dir_exist_ensure(char *dir);

void print_timestamp(int show_s_ms, int show_str);
int dir_ensure(char *dir);
int phony_div(unsigned int div_father, unsigned int div_son);
void ms_sleep(unsigned int ms);
char *strrstr_s(const char *str_dad, char *str_son, char signchr);
char *time_serial();
int ipv4_simple_check(const char *ip_addr);
int distill_file(char *path, char *file, unsigned int file_size, char *filefmt, char *preferential_file);
int strtailcmp(const char *str_dad, char *str_tail, int case_cmp);
int igmp_simple_check(const char *igmp_addr, char *igmp_ip, int *igmp_port);
int signed_char_clear(char *str_dad, unsigned int str_dad_len, char sign_c, int flag);
int fcopy_c(char *from_file, char *to_file);
int files_copy(char *from_dir, char *to_dir);
int remove_force(const char *from_fun, const char *uri);
char *hms_stamp();
long long dir_size(const char *uri);
int dir_stat_ensure(const char *uri);
int disk_usable_check(char *disk_dir, unsigned long long *tt_size, unsigned long long *free_size);

#endif

