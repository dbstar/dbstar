��һ��sha_verify_origine.c���÷�
�޸�һ������ļ��ĵ���������
sha_verify("f16ref-ota-eng.root.zip",buf,117906729);
�ѱ���buf�����117906729���������е�ZIP���Ĵ�С
���̺�
��gcc -o sha sha_verify.c���룬����sha��ִ���ļ�
��f16ref-ota-eng.root.zip��������ͬ��Ŀ¼�£�ִ��./sha�Ϳ����ڵ�ǰĿ¼����sha.binУ���ļ�


�ڶ���sha_verify.c���÷�
��gcc -o sha sha_verify.c���룬����sha��ִ���ļ���
ִ�У�ʾ����./sha g18ref-ota-20140915.zip���ɵõ�У��ֵsha.bin��
Ȼ��ִ��cat sha.bin g18ref-ota-20140915.zip > upgrade_20140915.zip�õ�������������ļ���
