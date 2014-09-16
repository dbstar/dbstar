第一版sha_verify_origine.c的用法
修改一下这个文件的倒数第三行
sha_verify("f16ref-ota-eng.root.zip",buf,117906729);
把变量buf后面的117906729换成你手中的ZIP包的大小
存盘后
用gcc -o sha sha_verify.c编译，生成sha可执行文件
把f16ref-ota-eng.root.zip拷贝到相同的目录下，执行./sha就可以在当前目录生成sha.bin校验文件


第二版sha_verify.c的用法
用gcc -o sha sha_verify.c编译，生成sha可执行文件；
执行（示例）./sha g18ref-ota-20140915.zip即可得到校验值sha.bin；
然后执行cat sha.bin g18ref-ota-20140915.zip > upgrade_20140915.zip得到待打包的升级文件。
