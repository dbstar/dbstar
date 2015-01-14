[    1.902526@0] logger: created 256K log 'log_radio'
[    1.907260@0] logger: created 256K log 'log_system'
[    1.912021@0] vout_init_module
[    1.914966@0] start init vout module 
[    1.918783@0] create  vout attribute ok 
[    1.922779@0] ge2d_init
[    1.925090@0] ge2d_dev major:249
[    1.929158@0] ge2d start monitor
[    1.931733@0] osd_init
[    1.931739@1] ge2d workqueue monitor start
[    1.938264@0] [osd0] 0x84100000-0x850fffff
[    1.942495@0] Frame buffer memory assigned at phy:0x84100000, vir:0xe1000000, size=16384K
[    1.950446@0] ---------------clear framebuffer0 memory  
[    1.967575@0] [osd1] 0x85100000-0x851fffff
[    1.967623@0] Frame buffer memory assigned at phy:0x85100000, vir:0xe0200000, size=1024K
[    1.974134@0] init fbdev bpp is :24
[    1.991558@0] osd probe ok  
[    1.998294@0] amlvideo-000: V4L2 device registered as video10
[    1.999091@1]  set pinmux c08f48d4
[    2.001783@1]  set pinmux c08f48dc
[    2.038260@1] UART_ttyS0:(irq = 122)
[    2.068255@1] UART_ttyS3:(irq = 125)
[    2.068410@1] dwc_otg: version 2.94a 05-DEC-2012
[    2.570217@0] USB (0) use clock source: XTAL input
[    2.638231@1] hdmitx: ddc: cmd 0x10000002
[    2.638259@1] HDMITX: no HDCP key available
[    2.640757@1] hdmitx: ddc: cmd 0x10000002
[    2.644754@1] HDMITX: no HDCP key available
[    2.771047@0] Core Release: 2.94a
[    2.771068@0] Setting default values for core params
[    3.173289@0] Using Buffer DMA mode
[    3.173311@0] OTG VER PARAM: 1, OTG VER FLAG: 1
[    3.175646@0] Working on port type = OTG
[    3.179559@0] Current port type: SLAVE
[    3.183334@0] dwc_otg lm0: DWC OTG Controller
[    3.187713@0] dwc_otg lm0: new USB bus registered, assigned bus number 1
[    3.194348@0] dwc_otg lm0: irq 62, io mem 0x00000000
[    3.200119@0] hub 1-0:1.0: USB hub found
[    3.203172@0] hub 1-0:1.0: 1 port detected
[    3.207512@0] Dedicated Tx FIFOs mode
[    3.211049@0] using timer detect id change, df80a800
[    3.318240@1] HOST mode
[    3.415778@0] Core Release: 2.94a
[    3.415800@0] Setting default values for core params
[    3.518180@1] Using Buffer DMA mode
[    3.518206@1] OTG VER PARAM: 1, OTG VER FLAG: 1
[    3.571287@1] Init: Port Power? op_state=1
[    3.571309@1] Init: Power Port (0)
[    3.573120@1] set usb port power on (board gpio 25)!
[    3.818058@0] Using Buffer DMA mode
[    3.818079@0] OTG VER PARAM: 1, OTG VER FLAG: 1
[    3.820423@0] Working on port type = HOST
[    3.824454@0] dwc_otg lm1: DWC OTG Controller
[    3.828793@0] dwc_otg lm1: new USB bus registered, assigned bus number 2
[    3.835449@0] dwc_otg lm1: irq 63, io mem 0x00000000
[    3.843450@0] Init: Port Power? op_state=1
[    3.844456@0] Init: Power Port (0)
[    3.848661@0] hub 2-0:1.0: USB hub found
[    3.851758@0] hub 2-0:1.0: 1 port detected
[    3.856193@0] Amlogic nand flash Kernel driver, Version K1.06.018 (c) 2010 Amlogic Inc.
[    3.863846@0] ####Version of Uboot must be newer than U1.06.011!!!!! 
[    3.870301@0] 2
[    3.871974@0] SPI BOOT, m3_nand_probe continue i 0
[    3.876748@0] chip->controller=c0a6b964
[    3.880602@0] checking ChiprevD :0
[    3.883951@0] aml_nand_probe checked chiprev:0
[    3.888401@0] init bus_cycle=17, bus_timing=10, start_cycle=10, end_cycle=10,system=5.0ns
[    3.896965@0] No NAND device found.
[    3.900266@0] NAND device id: ad d7 94 91 60 44 
[    3.904611@0] aml_chip->hynix_new_nand_type =: 4 
[    3.909311@0] NAND device: Manufacturer ID: 0xad, Chip ID: 0xad (Hynix C revision 20nm NAND 4GiB H27UBG8T2C)
[    3.919235@0] #####aml_nand_init, with RB pins and chip->chip_delay:20
[    3.925619@0] bus_cycle=4, bus_timing=5, start_cycle=5, end_cycle=6,system=5.0ns
[    3.933007@0] oob size is not enough for selected bch mode: NAND_BCH60_1K_MODE force bch to mode: NAND_BCH40_1K_MODE
[    3.943507@0] aml_chip->oob_fill_cnt =64,aml_chip->oob_size =640,bch_bytes =70
[    3.950736@0] multi plane error for selected plane mode: NAND_TWO_PLANE_MODE force plane to : NAND_SINGLE_PLANE_MODE
[    3.961218@0]  oob layout use nand base oob layout oobsize = 16,oobmul =1,mtd->oobsize =640,aml_chip->oob_size =640
[    3.972805@0] aml_nand_get_read_default_value_hynix 913 get default reg value at blk:0, page:7
[    3.978264@1] Indeed it is in host mode hprt0 = 00021501
[    3.985523@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb0):    value:0x3c, for chip[0]
[    3.994459@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb1):    value:0x36, for chip[0]
[    4.003398@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb2):    value:0x5c, for chip[0]
[    4.012338@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb3):    value:0xa2, for chip[0]
[    4.021281@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb4):    value:0x40, for chip[0]
[    4.030219@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb5):    value:0x39, for chip[0]
[    4.039171@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb6):    value:0x50, for chip[0]
[    4.048091@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb7):    value:0x90, for chip[0]
[    4.057055@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb0):    value:0x3a, for chip[0]
[    4.065992@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb1):    value:0x39, for chip[0]
[    4.074923@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb2):    value:0x55, for chip[0]
[    4.083861@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb3):    value:0x9b, for chip[0]
[    4.092802@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb4):    value:0x3e, for chip[0]
[    4.101742@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb5):    value:0x3c, for chip[0]
[    4.110683@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb6):    value:0x49, for chip[0]
[    4.119625@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb7):    value:0x89, for chip[0]
[    4.128564@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb0):    value:0x38, for chip[0]
[    4.137495@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb1):    value:0x38, for chip[0]
[    4.146444@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb2):    value:0x52, for chip[0]
[    4.155384@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb3):    value:0x9d, for chip[0]
[    4.158243@1] usb 1-1: new high speed USB device number 2 using dwc_otg
[    4.170970@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb4):    value:0x3c, for chip[0]
[    4.170977@1] Indeed it is in host mode hprt0 = 00001101
[    4.185164@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb5):    value:0x3b, for chip[0]
[    4.194098@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb6):    value:0x46, for chip[0]
[    4.203038@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb7):    value:0x8b, for chip[0]
[    4.211978@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb0):    value:0x34, for chip[0]
[    4.220920@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb1):    value:0x36, for chip[0]
[    4.229859@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb2):    value:0x4f, for chip[0]
[    4.238805@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb3):    value:0x9a, for chip[0]
[    4.247731@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb4):    value:0x38, for chip[0]
[    4.256680@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb5):    value:0x39, for chip[0]
[    4.265623@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb6):    value:0x43, for chip[0]
[    4.274562@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb7):    value:0x88, for chip[0]
[    4.283502@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb0):    value:0x2d, for chip[0]
[    4.292442@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb1):    value:0x34, for chip[0]
[    4.301382@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb2):    value:0x4b, for chip[0]
[    4.310339@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb3):    value:0x96, for chip[0]
[    4.319266@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb4):    value:0x31, for chip[0]
[    4.328195@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb5):    value:0x37, for chip[0]
[    4.337144@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb6):    value:0x3f, for chip[0]
[    4.346085@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb7):    value:0x84, for chip[0]
[    4.355025@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb0):    value:0x23, for chip[0]
[    4.363989@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb1):    value:0x32, for chip[0]
[    4.372907@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb2):    value:0x47, for chip[0]
[    4.381847@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb3):    value:0x93, for chip[0]
[    4.390817@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb4):    value:0x27, for chip[0]
[    4.399757@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb5):    value:0x35, for chip[0]
[    4.408679@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb6):    value:0x3b, for chip[0]
[    4.417617@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb7):    value:0x81, for chip[0]
[    4.426578@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb0):    value:0x19, for chip[0]
[    4.435499@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb1):    value:0x25, for chip[0]
[    4.444449@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb2):    value:0x3b, for chip[0]
[    4.453380@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb3):    value:0x83, for chip[0]
[    4.462332@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb4):    value:0x1d, for chip[0]
[    4.471262@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb5):    value:0x28, for chip[0]
[    4.480210@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb6):    value:0x2f, for chip[0]
[    4.489163@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb7):    value:0x71, for chip[0]
[    4.498200@1] scsi0 : usb-storage 1-1:1.0
[    4.503051@1] Indeed it is in host mode hprt0 = 00021501
[    4.505334@0] aml nand env valid addr: 418000 
[    4.537617@0] nand env: nand_env_probe. 
[    4.538302@0] nand key: nand_key_probe. 
[    4.539812@0] key start_blk=2040,end_blk=2047,aml_nand_key_init:651
[    4.555721@0] aml nand key valid addr: ff000000 
[    4.555746@0] aml nand key valid addr: ff200000 
[    4.559321@0] aml nand key valid addr: ff400000 
[    4.563890@0] aml nand key valid addr: ff600000 
[    4.568537@0] i=0,register --- nand_key
[    4.572463@0] Creating 8 MTD partitions on "C revision 20nm NAND 4GiB H27UBG8T2C":
[    4.579891@0] 0x000000c00000-0x000001400000 : "logo"
[    4.586119@0] 0x000001400000-0x000001c00000 : "aml_logo"
[    4.591211@0] 0x000001c00000-0x000002400000 : "recovery"
[    4.596427@0] 0x000002400000-0x000008c00000 : "boot"
[    4.601458@0] 0x000008c00000-0x000048c00000 : "system"
[    4.606558@0] 0x000048c00000-0x000068c00000 : "cache"
[    4.611623@0] 0x000068c00000-0x000078c00000 : "backup"
[    4.616657@0] 0x000078c00000-0x0000ff000000 : "data"
[    4.621989@0] init_aml_nftl start
[    4.623876@0] mtd->name: system
[    4.626998@0] nftl version 140415a
[    4.630459@0] nftl part attr 0
[    4.633472@0] nftl start:512,64
[    4.637745@0] first
[    4.688267@1] usb 2-1: new high speed USB device number 2 using dwc_otg
[    4.689511@1] Indeed it is in host mode hprt0 = 00001101
[    4.782524@0] average_erase_count:0
[    4.782553@0] second 139,448
[    4.831231@0] current used block :373
[    4.831254@0] current_block1:373
[    4.852304@0] free block cnt = 373
[    4.852324@0] new current block is 372
[    4.854122@0] prio_garbage_collect cannot find PHY_MAPPING_PAGE ffffffff !!
[    4.890839@1] hub 2-1:1.0: USB hub found
[    4.891163@1] hub 2-1:1.0: 4 ports detected
[    5.030752@0] prio_garbage_collect_no_last_page put_phy_block_to_free_list!
[    5.361597@0] recover_current_block_mapping : fill the current block, from page 255
[    5.363609@0] nftl ok!
[    5.366292@0] aml_nftl_blk->mbd.tr.name =system
[    5.371220@1] aml_nftl_init_bounce_buf, use cache here
[    5.375994@0]  system: unknown partition table
[    5.380320@0] _nftl_init_bounce_buf already init 1000
[    5.385049@0] aml_nftl_add_mtd ok
[    5.388389@0] mtd->name: cache
[    5.391390@0] nftl version 140415a
[    5.394794@0] nftl part attr 0
[    5.397840@0] nftl start:256,32
[    5.401474@0] first
[    5.477448@0] average_erase_count:0
[    5.477472@0] second 6,224
[    5.480070@0] current used block :250
[    5.481633@0] current_block1:250
[    5.500485@1] scsi 0:0:0:0: Direct-Access     Initio   INIC-3609        0213 PQ: 0 ANSI: 6
[    5.504398@1] sd 0:0:0:0: [sda] 976773167 512-byte logical blocks: (500 GB/465 GiB)
[    5.511413@0] free block cnt = 250
[    5.514203@1] sd 0:0:0:0: [sda] Write Protect is off
[    5.514219@0] new current block is 249
[    5.514574@0] prio_garbage_collect cannot find PHY_MAPPING_PAGE ffffffff !!
[    5.530633@1] sd 0:0:0:0: [sda] Write cache: disabled, read cache: enabled, supports DPO and FUA
[    5.562013@1]  sda: sda1
[    5.569816@1] sd 0:0:0:0: [sda] Attached SCSI disk
[    5.693202@0] prio_garbage_collect_no_last_page put_phy_block_to_free_list!
[    6.019214@0] recover_current_block_mapping : fill the current block, from page 255
[    6.021232@0] nftl ok!
[    6.023935@0] aml_nftl_blk->mbd.tr.name =cache
[    6.028687@1] aml_nftl_init_bounce_buf, use cache here
[    6.033548@0]  cache: unknown partition table
[    6.037758@0] _nftl_init_bounce_buf already init 1000
[    6.042518@0] aml_nftl_add_mtd ok
[    6.045797@0] mtd->name: backup
[    6.048936@0] nftl version 140415a
[    6.052322@0] nftl part attr 0
[    6.055362@0] nftl start:128,16
[    6.058739@0] first
[    6.097836@0] average_erase_count:0
[    6.097858@0] second 1,112
[    6.098737@0] all block full!!
[    6.101411@0] free block cnt = 127
[    6.104794@0] new current block is 126
[    6.108537@0] nftl ok!
[    6.111231@0] aml_nftl_blk->mbd.tr.name =backup
[    6.116104@1] aml_nftl_init_bounce_buf, use cache here
[    6.120946@0]  backup: unknown partition table
[    6.125221@0] _nftl_init_bounce_buf already init 1000
[    6.130001@0] aml_nftl_add_mtd ok
[    6.133266@0] mtd->name: data
[    6.136221@0] nftl version 140415a
[    6.139715@0] nftl part attr 0
[    6.142761@0] nftl start:1074,134
[    6.147844@0] first
[    6.460146@0] average_erase_count:0
[    6.460195@0] second 21,940
[    6.468053@0] current used block :1053
[    6.468075@0] current_block1:1053
[    6.552594@0] free block cnt = 1053
[    6.552616@0] new current block is 1052
[    6.554594@0] prio_garbage_collect cannot find PHY_MAPPING_PAGE ffffffff !!
[    6.885110@0] prio_garbage_collect_no_last_page put_phy_block_to_free_list!
[    7.058856@0] recover_current_block_mapping : fill the current block, from page 255
[    7.060874@0] nftl ok!
[    7.063643@0] aml_nftl_blk->mbd.tr.name =data
[    7.068427@1] aml_nftl_init_bounce_buf, use cache here
[    7.073102@0]  data: unknown partition table
[    7.077250@0] _nftl_init_bounce_buf already init 1000
[    7.081996@0] aml_nftl_add_mtd ok
[    7.085264@0] init_aml_nftl end
[    7.088399@0] ethernetinit(dbg[c08dc64c]=1)
[    7.092578@0] ethernet base addr is f3610000
[    7.096806@0] set_phy_mode() phy_Identifier: 0x0
[    7.101556@0] ethernet: MII PHY 0007c0f1h found at address 1, status 0x7829 advertising 01e1.
[    7.110003@0] find phy phy_Identifier=7c0f1
[    7.114077@0] write mac add to:dfb16fc8: 84 26 90 00 00 02 |.&....|
[    7.120746@0] eth0: mixed no checksumming and other settings.
[    7.126341@0] ethernet_driver probe!
[    7.129638@0] ****** aml_eth_pinmux_setup() ******
[    7.134391@0] ****** aml_eth_clock_enable() ******
[    7.139354@0] Amlogic A/V streaming port init
[    7.146717@1] amvideocap_register_memory 8e100000 6266880
[    7.149078@1] amvideocap_init
[    7.152246@1] amvideocap_init,0
[    7.155090@1] regist mpeg12 codec profile
[    7.159112@1] regist mpeg4 codec profile
[    7.162856@1] amvdec_vc1 module init
[    7.166512@1] regist vc1 codec profile
[    7.170180@1] amvdec_avs module init
[    7.173807@1] amvdec_h264 module init
[    7.177447@1] regist h264 codec profile
[    7.181344@1] regist mjpeg codec profile
[    7.185077@1] amvdec_real module init
[    7.188858@1] regist real codec profile
[    7.193520@1] request_fiq:152: fiq=35
[    7.196188@1] request_fiq:186: end
[    7.200230@1] SARADC Driver init.
[    7.203158@1] Remote Driver
[    7.205989@1] input: aml_keypad as /devices/platform/meson-remote/input/input0
[    7.213570@1] meson_remote_pinmux_setup()
[    7.216848@1] Remote platform_data g_remote_base=f3100480
[    7.222270@1] Remote date_valye======0,status == 8915f00
[    7.227553@1] remote config major:244
[    7.231742@1] physical address:0x9f162000
[    7.235345@1] ADC Keypad Driver init.
[    7.239062@1] Meson KeyInput init
[    7.242242@1] Key 116 registed.
[    7.245500@1] input: key_input as /devices/platform/meson-keyinput.0/input/input1
[    7.253177@1] Meson KeyInput register RTC interrupt
[    7.257494@1] Meson KeyInput major=243
[    7.262308@1]  spi_nor_probe 586
[    7.264587@1] SPI BOOT  : spi_nor_probe 591 
[    7.268908@1] spi_nor apollospi:0: mx25l3205d (4096 Kbytes)
[    7.274395@1] Creating 2 MTD partitions on "apollospi:0":
[    7.279824@1] 0x000000000000-0x000000060000 : "bootloader"
[    7.286514@1] 0x000000068000-0x000000070000 : "ubootenv"
[    7.292265@1] Memory Card media Major: 253
[    7.294761@1] card max_req_size is 128K 
[    7.299442@1] card creat process sucessful
[    7.302641@1] 
[    7.302644@1] SD/MMC initialization started......
[    7.948247@1] mmc data3 pull high
[    7.948471@0] sd_mmc_info->card_type=0
[    7.949649@0] begin SDIO check ......
[    7.975869@0] sdio_timeout_int_times = 0; timeout = 498
[    7.998469@0] sdio_timeout_int_times = 0; timeout = 497
[    7.998496@0] SEND OP timeout @1
[    8.001260@0] mmc data3 pull high
[    8.004752@0] begin SD&SDHC check ......
[    8.050801@0] sdio_timeout_int_times = 0; timeout = 498
[    8.050831@0] SEND IF timeout @2
[    8.076353@0] sdio_timeout_int_times = 0; timeout = 498
[    8.076381@0] begin MMC check ......
[    8.120797@0] sdio_timeout_int_times = 0; timeout = 498
[    8.120823@0] No any SD/MMC card detected!
[    8.124458@0] #SD_MMC_ERROR_DRIVER_FAILURE error occured in sd_voltage_validation()
[    8.132114@0] [card_force_init] unit_state 3
[    8.136511@0] [dsp]DSP start addr 0xc4000000
[    8.140641@0] [dsp]register dsp to char divece(232)
[    8.150475@0] DSP pcmenc stream buffer to [0x9e401000-0x9e601000]
[    8.151541@0] amlogic audio dsp pcmenc device init!
[    8.157287@0] amlogic audio spdif interface device init!
[    8.162923@0] using rtc device, aml_rtc, for alarms
[    8.166410@0] aml_rtc aml_rtc: rtc core: registered aml_rtc as rtc0
[    8.173665@0] gpio dev major number:240
[    8.177546@0] create gpio device success
[    8.180758@0] vdin_drv_init: major 238
[    8.184843@1] vdin0 mem_start = 0x87200000, mem_size = 0x2000000
[    8.190388@1] vdin.0 cnavas initial table:
[    8.194417@1]        128: 0x87200000-0x87a29000  3840x2228 (8356 KB)
[    8.200262@1]        129: 0x87a29000-0x88252000  3840x2228 (8356 KB)
[    8.206047@1]        130: 0x88252000-0x88a7b000  3840x2228 (8356 KB)
[    8.211888@1] vdin_drv_probe: driver initialized ok
[    8.216849@1] amvdec_656in module: init.
[    8.220661@1] amvdec_656in_init_module:major 237
[    8.225336@1] kobject (df13ae10): tried to init an initialized object, something is seriously wrong.
[    8.234400@1] [<c0043b44>] (unwind_backtrace+0x0/0xf8) from [<c0256840>] (kobject_init+0x78/0x94)
[    8.243234@1] [<c0256840>] (kobject_init+0x78/0x94) from [<c0298098>] (device_initialize+0x28/0x6c)
[    8.252260@1] [<c0298098>] (device_initialize+0x28/0x6c) from [<c029c59c>] (platform_device_register+0x10/0x1c)
[    8.262329@1] [<c029c59c>] (platform_device_register+0x10/0x1c) from [<c00213bc>] (amvdec_656in_init_module+0xac/0x140)
[    8.273098@1] [<c00213bc>] (amvdec_656in_init_module+0xac/0x140) from [<c003770c>] (do_one_initcall+0xfc/0x164)
[    8.283190@1] [<c003770c>] (do_one_initcall+0xfc/0x164) from [<c0008988>] (kernel_init+0x98/0x13c)
[    8.292097@1] [<c0008988>] (kernel_init+0x98/0x13c) from [<c003e0c0>] (kernel_thread_exit+0x0/0x8)
[    8.301718@1] amvdec_656in probe ok.
[    8.304699@1] efuse===========================================
[    8.310897@1] efuse: device efuse created
[    8.314471@1] efuse--------------------------------------------
[    8.320364@1] keys===========================================
[    8.326004@1] keys_devno=eb00000
[    8.329714@1] efuse: device aml_keys created
[    8.333475@1] amlkeys=0
[    8.335990@1] platform_driver_register--aml_keys_driver--------------------
[    8.358252@1] 6amlogic audio data interface device init!
[    8.358291@1] aml_dvb_init 
[    8.360856@1] dvb_io_setup start
[    8.364033@1] DVB: registering new adapter (amlogic-dvb)
[    8.373341@1] DVB: async fifo 0 buf size 524288, flush size 262144
[    8.376584@1] DVB: async fifo 1 buf size 524288, flush size 262144
[    8.383333@1] [aml_fe..]aml_fe_probe ok.
[    8.385739@1] Smartcard: cannot get resource "smc0_reset"
[    8.391465@1] SMC CLK SOURCE - 200000KHz
[    8.395073@1] [***smc***] smartcard->state: 1
[    8.401118@0] aml_hw_crypto initialization.
[    8.404241@0] usbcore: registered new interface driver snd-usb-audio
[    8.411018@0] enter dummy_codec_audio_probe
[    8.414940@0] aml-pcm 0:playback preallocate_dma_buffer: area=ffd80000, addr=9e8c0000, size=131072
[    8.424010@0] init controls
[    8.425553@0] iec958 0: preallocate dma buffer start=ffd00000, size=80000
[    8.433070@0] aml-pcm 1:capture preallocate_dma_buffer: area=ffce0000, addr=9eb40000, size=65536
[    8.441104@0] asoc: dummy_codec <-> aml-dai0 mapping ok
[    8.447623@0] dummy codec control ALSA component registered!
[    8.452069@0] ALSA device list:
[    8.455053@0]   #0: AML-DUMMY-CODEC
[    8.458653@0] <--GT msg--><1> /proc/gt82x_dbg created
[    8.463821@0] GACT probability NOT on
[    8.467210@0] Mirror/redirect action on
[    8.471039@0] u32 classifier
[    8.473886@0]     Actions configured
[    8.477448@0] Netfilter messages via NETLINK v0.30.
[    8.482359@0] nf_conntrack version 0.5.0 (13252 buckets, 53008 max)
[    8.489266@0] ctnetlink v0.93: registering with nfnetlink.
[    8.494067@0] NF_TPROXY: Transparent proxy support initialized, version 4.1.0
[    8.501159@0] NF_TPROXY: Copyright (c) 2006-2007 BalaBit IT Ltd.
[    8.507779@0] xt_time: kernel timezone is -0000
[    8.511838@0] ip_tables: (C) 2000-2006 Netfilter Core Team
[    8.517244@0] arp_tables: (C) 2002 David S. Miller
[    8.521968@0] TCP cubic registered
[    8.526349@0] NET: Registered protocol family 10
[    8.530657@0] Mobile IPv6
[    8.532516@0] ip6_tables: (C) 2000-2006 Netfilter Core Team
[    8.538139@0] IPv6 over IPv4 tunneling driver
[    8.543644@0] NET: Registered protocol family 17
[    8.547003@0] NET: Registered protocol family 15
[    8.551734@0] Bridge firewalling registered
[    8.555770@0] NET: Registered protocol family 35
[    8.560656@0] VFP support v0.3: implementor 41 architecture 3 part 30 variant 9 rev 4
[    8.568156@0] DDR low power is enable.
[    8.571978@0] enter meson_pm_probe!
[    8.575361@0] meson_pm_probe done !
[    8.579428@0] ------------[ cut here ]------------
[    8.583620@0] WARNING: at /home/mx/openlinux-jbmr1/common/kernel/smp.c:459 smp_call_function_many+0xc8/0x280()
[    8.593581@0] Modules linked in:
[    8.596813@0] [<c0043b44>] (unwind_backtrace+0x0/0xf8) from [<c0061f38>] (warn_slowpath_common+0x4c/0x64)
[    8.606349@0] [<c0061f38>] (warn_slowpath_common+0x4c/0x64) from [<c0061f6c>] (warn_slowpath_null+0x1c/0x24)
[    8.616156@0] [<c0061f6c>] (warn_slowpath_null+0x1c/0x24) from [<c008f6bc>] (smp_call_function_many+0xc8/0x280)
[    8.626225@0] [<c008f6bc>] (smp_call_function_many+0xc8/0x280) from [<c008f8a8>] (smp_call_function+0x34/0x68)
[    8.636209@0] [<c008f8a8>] (smp_call_function+0x34/0x68) from [<c0049c2c>] (clk_set_rate_a9+0x9c/0xdc)
[    8.645494@0] [<c0049c2c>] (clk_set_rate_a9+0x9c/0xdc) from [<c0049f28>] (meson_clk_set_rate+0x130/0x168)
[    8.655041@0] [<c0049f28>] (meson_clk_set_rate+0x130/0x168) from [<c0049ff4>] (clk_set_rate+0x94/0xbc)
[    8.664331@0] [<c0049ff4>] (clk_set_rate+0x94/0xbc) from [<c0052d7c>] (meson_cpufreq_target_locked.isra.2+0x14c/0x224)
[    8.675006@0] [<c0052d7c>] (meson_cpufreq_target_locked.isra.2+0x14c/0x224) from [<c0052e78>] (meson_cpufreq_target+0x24/0x3c)
[    8.686381@0] [<c0052e78>] (meson_cpufreq_target+0x24/0x3c) from [<c0388a34>] (__cpufreq_driver_target+0x50/0x64)
[    8.696621@0] [<c0388a34>] (__cpufreq_driver_target+0x50/0x64) from [<c038b1e0>] (cpufreq_governor_performance+0x28/0x30)
[    8.707555@0] [<c038b1e0>] (cpufreq_governor_performance+0x28/0x30) from [<c0388b1c>] (__cpufreq_governor+0xb0/0x138)
[    8.718144@0] [<c0388b1c>] (__cpufreq_governor+0xb0/0x138) from [<c038997c>] (__cpufreq_set_policy+0x128/0x168)
[    8.728212@0] [<c038997c>] (__cpufreq_set_policy+0x128/0x168) from [<c0389c1c>] (cpufreq_add_dev_interface+0x260/0x2c8)
[    8.738974@0] [<c0389c1c>] (cpufreq_add_dev_interface+0x260/0x2c8) from [<c038a140>] (cpufreq_add_dev+0x4bc/0x634)
[    8.749308@0] [<c038a140>] (cpufreq_add_dev+0x4bc/0x634) from [<c02996bc>] (sysdev_driver_register+0xb0/0x12c)
[    8.759290@0] [<c02996bc>] (sysdev_driver_register+0xb0/0x12c) from [<c0388fe8>] (cpufreq_register_driver+0x70/0x17c)
[    8.769880@0] [<c0388fe8>] (cpufreq_register_driver+0x70/0x17c) from [<c029bf3c>] (platform_drv_probe+0x18/0x1c)
[    8.780034@0] [<c029bf3c>] (platform_drv_probe+0x18/0x1c) from [<c029ad30>] (driver_probe_device+0x90/0x1ac)
[    8.789841@0] [<c029ad30>] (driver_probe_device+0x90/0x1ac) from [<c029aed8>] (__driver_attach+0x8c/0x90)
[    8.799389@0] [<c029aed8>] (__driver_attach+0x8c/0x90) from [<c0299f50>] (bus_for_each_dev+0x5c/0x88)
[    8.808590@0] [<c0299f50>] (bus_for_each_dev+0x5c/0x88) from [<c029a714>] (bus_add_driver+0x17c/0x244)
[    8.817876@0] [<c029a714>] (bus_add_driver+0x17c/0x244) from [<c029b384>] (driver_register+0x78/0x13c)
[    8.827165@0] [<c029b384>] (driver_register+0x78/0x13c) from [<c029c2ec>] (platform_driver_probe+0x18/0x9c)
[    8.836893@0] [<c029c2ec>] (platform_driver_probe+0x18/0x9c) from [<c003770c>] (do_one_initcall+0xfc/0x164)
[    8.846611@0] [<c003770c>] (do_one_initcall+0xfc/0x164) from [<c0008988>] (kernel_init+0x98/0x13c)
[    8.855552@0] [<c0008988>] (kernel_init+0x98/0x13c) from [<c003e0c0>] (kernel_thread_exit+0x0/0x8)
[    8.864505@0] ---[ end trace 7729bc1b54a826e1 ]---
[    8.869258@0] ------------[ cut here ]------------
[    8.874037@0] WARNING: at /home/mx/openlinux-jbmr1/common/kernel/smp.c:320 smp_call_function_single+0x150/0x1c0()
[    8.884273@0] Modules linked in:
[    8.887492@0] [<c0043b44>] (unwind_backtrace+0x0/0xf8) from [<c0061f38>] (warn_slowpath_common+0x4c/0x64)
[    8.897039@0] [<c0061f38>] (warn_slowpath_common+0x4c/0x64) from [<c0061f6c>] (warn_slowpath_null+0x1c/0x24)
[    8.906847@0] [<c0061f6c>] (warn_slowpath_null+0x1c/0x24) from [<c008f584>] (smp_call_function_single+0x150/0x1c0)
[    8.917178@0] [<c008f584>] (smp_call_function_single+0x150/0x1c0) from [<c008f8a8>] (smp_call_function+0x34/0x68)
[    8.927420@0] [<c008f8a8>] (smp_call_function+0x34/0x68) from [<c0049c2c>] (clk_set_rate_a9+0x9c/0xdc)
[    8.936707@0] [<c0049c2c>] (clk_set_rate_a9+0x9c/0xdc) from [<c0049f28>] (meson_clk_set_rate+0x130/0x168)
[    8.946254@0] [<c0049f28>] (meson_clk_set_rate+0x130/0x168) from [<c0049ff4>] (clk_set_rate+0x94/0xbc)
[    8.955543@0] [<c0049ff4>] (clk_set_rate+0x94/0xbc) from [<c0052d7c>] (meson_cpufreq_target_locked.isra.2+0x14c/0x224)
[    8.966218@0] [<c0052d7c>] (meson_cpufreq_target_locked.isra.2+0x14c/0x224) from [<c0052e78>] (meson_cpufreq_target+0x24/0x3c)
[    8.977590@0] [<c0052e78>] (meson_cpufreq_target+0x24/0x3c) from [<c0388a34>] (__cpufreq_driver_target+0x50/0x64)
[    8.987832@0] [<c0388a34>] (__cpufreq_driver_target+0x50/0x64) from [<c038b1e0>] (cpufreq_governor_performance+0x28/0x30)
[    8.998768@0] [<c038b1e0>] (cpufreq_governor_performance+0x28/0x30) from [<c0388b1c>] (__cpufreq_governor+0xb0/0x138)
[    9.009358@0] [<c0388b1c>] (__cpufreq_governor+0xb0/0x138) from [<c038997c>] (__cpufreq_set_policy+0x128/0x168)
[    9.019425@0] [<c038997c>] (__cpufreq_set_policy+0x128/0x168) from [<c0389c1c>] (cpufreq_add_dev_interface+0x260/0x2c8)
[    9.030188@0] [<c0389c1c>] (cpufreq_add_dev_interface+0x260/0x2c8) from [<c038a140>] (cpufreq_add_dev+0x4bc/0x634)
[    9.040519@0] [<c038a140>] (cpufreq_add_dev+0x4bc/0x634) from [<c02996bc>] (sysdev_driver_register+0xb0/0x12c)
[    9.050501@0] [<c02996bc>] (sysdev_driver_register+0xb0/0x12c) from [<c0388fe8>] (cpufreq_register_driver+0x70/0x17c)
[    9.061092@0] [<c0388fe8>] (cpufreq_register_driver+0x70/0x17c) from [<c029bf3c>] (platform_drv_probe+0x18/0x1c)
[    9.071247@0] [<c029bf3c>] (platform_drv_probe+0x18/0x1c) from [<c029ad30>] (driver_probe_device+0x90/0x1ac)
[    9.081054@0] [<c029ad30>] (driver_probe_device+0x90/0x1ac) from [<c029aed8>] (__driver_attach+0x8c/0x90)
[    9.090602@0] [<c029aed8>] (__driver_attach+0x8c/0x90) from [<c0299f50>] (bus_for_each_dev+0x5c/0x88)
[    9.099803@0] [<c0299f50>] (bus_for_each_dev+0x5c/0x88) from [<c029a714>] (bus_add_driver+0x17c/0x244)
[    9.109091@0] [<c029a714>] (bus_add_driver+0x17c/0x244) from [<c029b384>] (driver_register+0x78/0x13c)
[    9.118379@0] [<c029b384>] (driver_register+0x78/0x13c) from [<c029c2ec>] (platform_driver_probe+0x18/0x9c)
[    9.128102@0] [<c029c2ec>] (platform_driver_probe+0x18/0x9c) from [<c003770c>] (do_one_initcall+0xfc/0x164)
[    9.137822@0] [<c003770c>] (do_one_initcall+0xfc/0x164) from [<c0008988>] (kernel_init+0x98/0x13c)
[    9.146762@0] [<c0008988>] (kernel_init+0x98/0x13c) from [<c003e0c0>] (kernel_thread_exit+0x0/0x8)
[    9.155698@0] ---[ end trace 7729bc1b54a826e2 ]---
[    9.168131@0] android_usb gadget: Mass Storage Function, version: 2009/09/11
[    9.169562@0] android_usb gadget: Number of LUNs=2
[    9.174314@0]  lun0: LUN: removable file: (no medium)
[    9.179358@0]  lun1: LUN: removable file: (no medium)
[    9.185143@0] android_usb gadget: android_usb ready
[    9.189781@0] aml_rtc aml_rtc: setting system clock to 1970-01-02 00:24:54 UTC (87894)
[    9.197219@0] ------------[ cut here ]------------
[    9.201975@0] WARNING: at /home/mx/openlinux-jbmr1/common/fs/proc/generic.c:586 proc_register+0xec/0x1b4()
[    9.211589@0] proc_dir_entry '/proc/gt82x_dbg' already registered
[    9.217624@0] Modules linked in:
[    9.220858@0] [<c0043b44>] (unwind_backtrace+0x0/0xf8) from [<c0061f38>] (warn_slowpath_common+0x4c/0x64)
[    9.230402@0] [<c0061f38>] (warn_slowpath_common+0x4c/0x64) from [<c0061fe4>] (warn_slowpath_fmt+0x30/0x40)
[    9.240124@0] [<c0061fe4>] (warn_slowpath_fmt+0x30/0x40) from [<c0119ca8>] (proc_register+0xec/0x1b4)
[    9.249343@0] [<c0119ca8>] (proc_register+0xec/0x1b4) from [<c011a068>] (create_proc_entry+0x68/0xb4)
[    9.258551@0] [<c011a068>] (create_proc_entry+0x68/0xb4) from [<c062f6f8>] (goodix_ts_init+0x58/0xdc)
[    9.267722@0] [<c062f6f8>] (goodix_ts_init+0x58/0xdc) from [<c003770c>] (do_one_initcall+0xfc/0x164)
[    9.276843@0] [<c003770c>] (do_one_initcall+0xfc/0x164) from [<c0008988>] (kernel_init+0x98/0x13c)
[    9.285783@0] [<c0008988>] (kernel_init+0x98/0x13c) from [<c003e0c0>] (kernel_thread_exit+0x0/0x8)
[    9.294714@0] ---[ end trace 7729bc1b54a826e4 ]---
[    9.299502@0] <--GT msg--><1> /proc/gt82x_dbg created
[    9.304539@0] Error: Driver 'Goodix-TS' is already registered, aborting...
[    9.311401@0] CEC init
[    9.313756@1] CEC: CEC task process
[    9.317243@0_[    9.338237@0] Changing baud from 0 to 115200
[    9.398284@0] Freeing init memory: 188K
[    9.402158@1] init (1): /proc/1/oom_adj is deprecated, please use /proc/1/oom_score_adj instead.
[    9.411981@1] UMP: UMP device driver  loaded
[    9.418785@0] usb 2-1.2: new high speed USB device number 3 using dwc_otg
[    9.434509@1] mail version=-1
[    9.434553@1] Mali pp1 MMU register mapped at e00ec000...
[    9.437229@1] Mali pp2 MMU register mapped at e00ee000...
[    9.505725@1] mali_meson_poweron: Interrupt received.
[    9.568752@1] mail version=1
[    9.631870@1] mali_meson_poweron: Interrupt received.
[    9.694983@1] mail version=1
[    9.698068@1] Mali: Mali device driver loaded
[    9.698746@1] boot_timer_set: <start!>,boot_timer_state=1
[   10.702757@1] init: hdmi hpd_status is :49
[   10.702790@1] tvmode set to 720p
[   10.704510@1] don't set the same mode as current.
[   10.709344@1] init: ===== resolution=720p
[   10.713100@1] init: ===== cvbsmode=480cvbs
[   10.717179@1] init: ===== hdmimode=720posd0=>x:0 ,y:0,w:1280,h:720
[   10.724917@0]  osd1=> x:0,y:0,w:18,h:18 
[   10.741920@0] init: load_565rle_image_mbx result is: 0
[   10.791748@0] osd0 free scale ENABLE
[   10.791780@0] vf_reg_provider:osd
[   10.879807@0] EXT4-fs (system): INFO: recovery required on readonly filesystem
[   10.881393@0] EXT4-fs (system): write access will be enabled during recovery
[   10.888245@1] <FIQ>:vf_ext_light_unreg_provide
[   10.888250@1] 0
[   11.264727@0] EXT4-fs (system): recovery complete
[   11.270043@0] EXT4-fs (system): mounted filesystem with ordered data mode. Opts: noauto_da_alloc
[   11.273246@0] init: Before e2fsck_main...
[   11.286545@0] init: After e2fsck_main...
[   11.295147@0] EXT4-fs (data): mounted filesystem with ordered data mode. Opts: noauto_da_alloc
[   11.309426@0] EXT4-fs (cache): mounted filesystem with ordered data mode. Opts: noauto_da_alloc
[   11.312703@0] name=nand_key nand_key
[   11.316113@0] read:addr:0xff000000,phy_blk_addr:2040,phy_page_addr:0,aml_nand_get_key:129
[   11.371562@0] init: cannot find '/sbin/sec_test', disabling 'sec_test'
[   11.377229@0] init: cannot find '/system/etc/install-recovery.sh', disabling 'flash_recovery'
[   11.385151@0] init: cannot find '/system/bin/smbd', disabling 'smbd'
[   11.387933@0] init: cannot find '/system/bin/pppoe_wrapper', disabling 'pppoe_wrapper'
[   11.397183@0] init: cannot find '/system/bin/xcmid-amlogic', disabling 'xcmidware'
[   11.415818@0] DSP pcmenc stream buffer to [0x9e401000-0x9e601000]
[   11.426746@0] init: property 'ro.usb.vendor.string' doesn't exist while expanding '${ro.usb.vendor.string}'
[   11.430998@0] init: cannot expand '${ro.usb.vendor.string}' while writing to '/sys/class/android_usb/android0/f_mass_storage/vendor_string'
[   11.444217@0] init: property 'ro.usb.product.string' doesn't exist while expanding '${ro.usb.product.string}'
[   11.453598@0] init: cannot expand '${ro.usb.product.string}' while writing to '/sys/class/android_usb/android0/f_mass_storage/product_string'
[   11.466636@0] vfm_map_store:rm default
[   11.469799@0] vfm_map_store:add default decoder ppmgr deinterlace amvideo
[   11.478439@0] android_usb: already disabled
[   11.480882@0] android_usb: already disabled
[   11.488526@0] warning: `adbd' uses 32-bit capabilities (legacy support in use)
[   11.492424@0] adb_open
[   11.494321@0] mtp_bind_config
[   11.497307@0] adb_bind_config
root@android:/ # [   11.658674@0] tvmode set to 720p
[   11.658687@0] 
[   11.658734@0] don't set the same mode as current.
[   11.862691@0] osd0 free scale ENABLE
[   11.862759@0] vf_reg_provider:osd
[   11.875350@0] osd1 free scale ENABLE
[   11.888262@1] <FIQ>:vf_ext_light_unreg_provide
[   11.888267@1] 0
[   11.920851@0] buf=0
[   11.920855@0] 
[   11.920908@0] IEC958_mode_raw=0
[   13.619080@1] osd[0] set scale, h_scale: DISABLE, v_scale: DISABLE
[   13.619711@1] osd[0].scaledata: 0 0 0 0
[   13.623542@1] osd[0].pandata: 0 1279 0 719
[   14.927309@1] adb_release
[   14.927380@1] dwc_otg_pcd_pullup, is_on 0
[   14.928306@0] WARN::dwc_otg_handle_mode_mismatch_intr:154: Mode Mismatch Interrupt: currently in Host mode
[   14.928313@0] 
[   14.928393@1] WARN::ep_dequeue:412: bogus device state
[   14.928397@1] 
[   14.937753@1] adb_open
[   14.937773@1] mtp_bind_config
[   14.937808@1] adb_bind_config
[   31.308279@0] pcd_ep0_timer_timeout 1
[   31.308346@0] WARN::dwc_otg_handle_mode_mismatch_intr:154: Mode Mismatch Interrupt: currently in Host mode
[   31.308352@0] 
[   33.432765@0] 333000
[   38.809249@1] request_suspend_state: wakeup (3->0) at 38541031001 (1970-01-02 00:25:24.104271001 UTC)
[   41.869085@0] trun off vdac

root@android:/ # 
root@android:/ # 
root@android:/ # 
root@android:/ # logcat -c;logcat
--------- beginning of /dev/log/system
I/SystemServer( 3802): NetworkManagement Service
--------- beginning of /dev/log/main
I/PackageManager( 3802): No secure containers on sdcard
W/MountService( 3802): Duplicate state transition (removed -> removed) for /storage/external_storage/sdcard1
D/MountService( 3802): addVolumeLocked() StorageVolume [mStorageId=196609 mPath=/storage/external_storage/sda1 mDescriptionId=17040648 mPrimary=false mRemovable=true mEmulated=false mMtpReserveSpace=0 mAllowMassStorage=false mMaxFileSize=0 mOwner=null]
D/MountService( 3802): volume state changed for /storage/external_storage/sda1 (removed -> unmounted)
I/SystemServer( 3802): Text Service Manager Service
W/TextServicesManagerService( 3802): no available spell checker services found
I/SystemServer( 3802): NetworkStats Service
W/PackageManager( 3802): Unknown permission android.permission.ACCESS_DOWNLOAD_MANAGER in package com.android.defcontainer
W/PackageManager( 3802): Unknown permission android.permission.ACCESS_ALL_DOWNLOADS in package com.android.defcontainer
I/SystemServer( 3802): NetworkPolicy Service
W/PackageManager( 3802): Unknown permission com.google.android.googleapps.permission.GOOGLE_AUTH in package com.android.settings
W/PackageManager( 3802): Unknown permission android.permission.ACCESS_DOWNLOAD_MANAGER in package com.android.settings
W/PackageManager( 3802): Unknown permission com.android.launcher.permission.READ_SETTINGS in package com.android.settings
W/PackageManager( 3802): Unknown permission com.android.launcher.permission.WRITE_SETTINGS in package com.android.settings
I/SystemServer( 3802): Wi-Fi P2pService
W/PackageManager( 3802): Unknown permission com.android.launcher.permission.READ_SETTINGS in package com.dbstar.settings
W/PackageManager( 3802): Unknown permission com.android.launcher.permission.WRITE_SETTINGS in package com.dbstar.settings
W/PackageManager( 3802): Unknown permission com.android.launcher.permission.READ_SETTINGS in package com.settings.ottsettings
W/PackageManager( 3802): Unknown permission com.android.launcher.permission.WRITE_SETTINGS in package com.settings.ottsettings
W/PackageManager( 3802): Unknown permission android.webkit.permission.PLUGIN in package com.adobe.flashplayer
W/PackageManager( 3802): Unknown permission android.permission.ACCESS_DOWNLOAD_MANAGER in package com.guozi.appstore
W/PackageManager( 3802): Unknown permission com.android.launcher.permission.INSTALL_SHORTCUT in package tv.icntv.ott
W/PackageManager( 3802): Unknown permission com.android.launcher.permission.UNINSTALL_SHORTCUT in package tv.icntv.ott
W/PackageManager( 3802): Unknown permission android.permission.RAISED_THREAD_PRIORITY in package tv.icntv.ott
W/PackageManager( 3802): Not granting permission android.permission.INJECT_EVENTS to package tv.icntv.ott (protectionLevel=2 flags=0x98be45)
W/PackageManager( 3802): Unknown permission android.permission.WRITE_OWNER_DATA in package com.dbstar.DbstarDVB
D/dalvikvm( 3802): GC_CONCURRENT freed 357K, 41% free 5200K/8716K, paused 2ms+2ms, total 27ms
I/SystemServer( 3802): Wi-Fi Service
E/CommandListener( 2589): Failed to open /proc/sys/net/ipv6/conf/wlan0/disable_ipv6: No such file or directory
E/WifiStateMachine( 3802): Failed to disable IPv6: java.lang.IllegalStateException: command '1 interface ipv6 wlan0 disable' failed with '400 1 Failed to change IPv6 state (No such file or directory)'
I/SystemServer( 3802): Connectivity Service
D/ConnectivityService( 3802): ConnectivityService starting up
I/pppoe   ( 3802): ==>android_net_pppoe_initPppoeNative
E/pppoe   ( 3802): android_net_pppoe_initPppoeNative exited with success
I/pppoe   ( 3802): ==>android_net_pppoe_initPppoeNative
E/pppoe   ( 3802): android_net_pppoe_initPppoeNative exited with success
V/ConnectivityService( 3802): mNetworkPreference9
D/ConnectivityService( 3802): *******netType=wifi,1,1,1,-1,true
I/ConnectivityService( 3802): NetworkAttributes naString: wifi,1,1,1,-1,true  type: 1
D/ConnectivityService( 3802): *******netType=mobile,0,0,0,-1,true
I/ConnectivityService( 3802): NetworkAttributes naString: mobile,0,0,0,-1,true  type: 0
D/ConnectivityService( 3802): *******netType=wifi_p2p,13,1,0,-1,true
I/ConnectivityService( 3802): NetworkAttributes naString: wifi_p2p,13,1,0,-1,true  type: 13
D/ConnectivityService( 3802): *******netType=ethernet,9,9,1,-1,true
I/ConnectivityService( 3802): NetworkAttributes naString: ethernet,9,9,1,-1,true  type: 9
D/ConnectivityService( 3802): *******netType=pppoe,14,14,1,-1,true
I/ConnectivityService( 3802): NetworkAttributes naString: pppoe,14,14,1,-1,true  type: 14
E/ConnectivityService( 3802): Ignoring protectedNetwork 10
E/ConnectivityService( 3802): Ignoring protectedNetwork 11
E/ConnectivityService( 3802): Ignoring protectedNetwork 12
I/PppoeStateTracker( 3802): Starts ...
V/PppoeStateTracker( 3802): setTeardownRequested(false)
I/PppoeStateTracker( 3802): Successed
D/ConnectivityService( 3802): *******targetNetworkType=14
I/PppoeStateTracker( 3802): Starts ...
V/PppoeStateTracker( 3802): setTeardownRequested(false)
I/PppoeStateTracker( 3802): Successed
I/PppoeService( 3802): Pppoe dev enabled 0
I/PppoeService( 3802): setPppoeState from 0 to 0
I/PppoeService( 3802): Trigger the pppoe monitor
I/PppoeStateTracker( 3802): start monitoring
I/PppoeMonitor( 3802): go poll events
I/PppoeStateTracker( 3802): start to monitor the pppoe devices
I/PppoeManager( 3802): Init Pppoe Manager
I/PppoeService( 3802): setPppoeState from 0 to 2
I/PppoeService( 3802): @@@@@@NO CONFIG. set default
I/PppoeStateTracker( 3802): >>>reconnect
I/PppoeStateTracker( 3802): >>>resetInterface
I/PppoeService( 3802): setPppoeState from 2 to 2
I/PppoeStateTracker( 3802): >>>resetInterface
I/PppoeStateTracker( 3802): pppoeConfigured: true
I/PppoeStateTracker( 3802): pppoeConfigured: true
I/PppoeStateTracker( 3802): IfName:ppp0
I/PppoeStateTracker( 3802): IP:0.0.0.0
D/NetUtils( 3802): android_net_utils_resetConnections in env=0x620bbf18 clazz=0xf3400001 iface=ppp0 mask=0x3
I/PppoeStateTracker( 3802): Mask:255.255.255.0
I/PppoeStateTracker( 3802): DNS:0.0.0.0
I/PppoeStateTracker( 3802): reset device ppp0
I/PppoeStateTracker( 3802): IfName:ppp0
I/PppoeStateTracker( 3802): IP:0.0.0.0
I/PppoeStateTracker( 3802): Mask:255.255.255.0
I/PppoeStateTracker( 3802): DNS:0.0.0.0
I/PppoeStateTracker( 3802): Force the connection disconnected before configuration
V/PppoeStateTracker( 3802): configureInterfaceStatic: ifname:ppp0
D/CommandListener( 2589): Setting iface cfg
V/PppoeStateTracker( 3802): IP configuration failed: java.lang.IllegalStateException: command '2 interface setcfg ppp0 0.0.0.0 24' failed with '400 2 Failed to set address (No such device)'
D/NetUtils( 3802): android_net_utils_resetConnections in env=0x620bbf18 clazz=0xf7a00001 iface=ppp0 mask=0x3
D/PppoeStateTracker( 3802): PST.setPppoeState()false ==> false
I/PppoeStateTracker( 3802): start to monitor the pppoe devices
I/PppoeStateTracker( 3802): >>>resetInterface
I/PppoeStateTracker( 3802): pppoeConfigured: true
I/PppoeStateTracker( 3802): IfName:ppp0
I/PppoeStateTracker( 3802): IP:0.0.0.0
I/PppoeStateTracker( 3802): Mask:255.255.255.0
I/PppoeStateTracker( 3802): DNS:0.0.0.0
I/PppoeStateTracker( 3802): reset device ppp0
D/NetUtils( 3802): android_net_utils_resetConnections in env=0x61e0d988 clazz=0x1fa00001 iface=ppp0 mask=0x3
I/PppoeStateTracker( 3802): reset device ppp0
I/PppoeStateTracker( 3802): Force the connection disconnected before configuration
V/PppoeStateTracker( 3802): configureInterfaceStatic: ifname:ppp0
D/CommandListener( 2589): Setting iface cfg
D/PppoeStateTracker( 3802): PST.setPppoeState()false ==> false
V/PppoeStateTracker( 3802): IP configuration failed: java.lang.IllegalStateException: c[   49.251517@0] netdev_open
[   49.252452@0] Ethernet reset
[   49.255316@0] NET MDA descpter start addr=df905000
[   49.260382@0] phy_reset!
[   49.262606@0] set_phy_mode() phy_Identifier: 0x7c0f1
[   49.267964@0] --1--write mac add to:dfb16fc8: 84 26 90 00 00 02 |.&....|
[   49.274246@0] unkown current key-name,key_read_show:1286
[   49.279545@0] ret = -22
[   49.279548@0] print_buff=
[   49.284563@0] --2--write mac add to:dfb16fc8: 84 26 90 00 00 02 |.&....|
[   49.291246@0] write mac add to:dfb16fc8: 84 26 90 00 00 02 |.&....|
[   49.297497@0] Current DMA mode=0, set mode=621c100
[   49.302321@0] ether leave promiscuous mode
[   49.306350@0] ether leave all muticast mode
[   49.310527@0] changed the Multicast,mcount=1
[   49.314774@0] add mac address:33:33:00:00:00:01,bit=1
[   49.319819@0] set hash low=2,high=0
[   49.323276@0] changed the filter setting to :4
[   49.328273@1] changed the Multicast,mcount=1
[   49.331959@1] add mac address:33:33:00:00:00:01,bit=1
[   49.338725@1] changed the Multicast,mcount=2
[   49.341262@1] add mac address:33:33:00:00:00:01,bit=1
[   49.346279@1] add mac address:01:00:5e:00:00:01,bit=32
[   49.351414@1] set hash low=2,high=1
[   49.354868@1] changed the filter setting to :4
[   49.360611@1] ADDRCONF(NETDEV_UP): eth0: link is not ready
ommand '3 interface setcfg ppp0 0.0.0.0 24' failed with '400 3 Failed to set address (No such device)'
D/NetUtils( 3802): android_net_utils_resetConnections in env=0x620bbf18 clazz=0xfc700001 iface=ppp0 mask=0x3
I/PppoeStateTracker( 3802): >>>reconnect
I/PppoeService( 3802): setPppoeState from 2 to 2
I/PppoeStateTracker( 3802): >>>resetInterface
I/PppoeStateTracker( 3802): pppoeConfigured: true
I/PppoeStateTracker( 3802): IfName:ppp0
I/PppoeStateTracker( 3802): IP:0.0.0.0
I/PppoeStateTracker( 3802): Mask:255.255.255.0
I/PppoeStateTracker( 3802): DNS:0.0.0.0
I/PppoeStateTracker( 3802): reset device ppp0
V/PppoeStateTracker( 3802): configureInterfaceStatic: ifname:ppp0
D/CommandListener( 2589): Setting iface cfg
V/PppoeStateTracker( 3802): IP configuration failed: java.lang.IllegalStateException: command '4 interface setcfg ppp0 0.0.0.0 24' failed with '400 4 Failed to set address (No such device)'
I/PppoeStateTracker( 3802): Force the connection disconnected before configuration
D/PppoeStateTracker( 3802): PST.setPppoeState()false ==> false
V/PppoeStateTracker( 3802): configureInterfaceStatic: ifname:ppp0
[   49.464238@1] acc_open
[   49.465984@1] acc_release
D/CommandListener( 2589): Setting iface cfg
V/PppoeStateTracker( 3802): IP configuration failed: java.lang.IllegalStateException: command '5 interface setcfg ppp0 0.0.0.0 24' failed with '400 5 Failed to set address (No such device)'
I/ethernet( 3802): ==>android_net_ethernet_initEthernetNative
E/ethernet( 3802): android_net_ethernet_initEthernetNative exited with success
I/ethernet( 3802): User ask for device name on 0, list:6319AD90, total:1
I/ethernet( 3802): Found :eth0
I/PppoeStateTracker( 3802): Force the connection disconnected before configuration
D/PppoeStateTracker( 3802): PST.setPppoeState()false ==> false
I/EthernetStateTracker( 3802): Starts...
I/EthernetStateTracker( 3802): Success
V/EthernetService( 3802): Ethernet dev enabled 2
I/EthernetService( 3802): total found 1 net devices
I/EthernetService( 3802):  device 0 name eth0
I/EthernetService( 3802): setEthState from 0 to 2
V/EthernetService( 3802): Set ethernet mode [Ljava.lang.String;@40f99750 -> dhcp
D/NetUtils( 3802): android_net_utils_resetConnections in env=0x620bbf18 clazz=0xb200001 iface=eth0 mask=0x3
I/EthernetService( 3802): $$ EthernetService uninited,disable setEthState() call resetInterface()
I/EthernetService( 3802): $$ resetInterface() will be called in reconnect()
V/EthernetService( 3802): Trigger the ethernet monitor
V/EthernetStateTracker( 3802): start polling
D/ConnectivityService( 3802): *******targetNetworkType=9
I/EthernetStateTracker( 3802): start to monitor the Ethernet devices
I/EthernetManager( 3802): Init Ethernet Manager
I/EthernetStateTracker( 3802): $$ DISABLE startMonitoring call resetInterface()
I/EthernetService( 3802): setEthState from 2 to 2
I/EthernetStateTracker( 3802): $$reconnect call resetInterface()
I/EthernetStateTracker( 3802): reset device eth0
I/EthernetStateTracker( 3802): Force the connection disconnected before configuration
D/EthernetStateTracker( 3802): setEthState state=false->false event=4
I/ethernet( 3802): event: NEWLINK(16), flags=0X1043
I/ethernet( 3802): flags: UP BC RUNNING MC
I/ethernet( 3802): poll state :eth0:17:
I/pppoe   ( 3802): For NETLINK bug, RTM_NEWLINK ==> RTM_DELLINK
I/pppoe   ( 3802): Event(DELLINK) NOT from PPP interface, ignore it
V/EthernetStateTracker( 3802): report new state DISCONNECTED on dev eth0 current=eth0
V/EthernetStateTracker( 3802): update network state tracker
V/PppoeMonitor( 3802): net.pppoe.running not FOUND
V/PppoeMonitor( 3802): !net.pppoe.running. Discard event
I/PppoeMonitor( 3802): go poll events
I/pppoe   ( 3802): For NETLINK bug, RTM_NEWLINK ==> RTM_DELLINK
I/pppoe   ( 3802): Event(DELLINK) NOT from PPP interface, ignore it
D/ethernet( 3802): eth0: flags = 0x1003, IFF_LOWER_DOWN
V/PppoeMonitor( 3802): net.pppoe.running not FOUND
V/PppoeMonitor( 3802): !net.pppoe.running. Discard event
I/PppoeMonitor( 3802): go poll events
D/EthernetService( 3802): EthernetNative.isEthDeviceAdded(eth0) return false
D/ConnectivityService( 3802): *******targetNetworkType=1
I/EthernetStateTracker( 3802): Old status stackConnected=false HWConnected=false
I/EthernetStateTracker( 3802): [EVENT: ether is removed]
D/EthernetStateTracker( 3802): setEthState state=false->false event=4
I/EthernetStateTracker( 3802): New status, stackConnected=false HWConnected=false
I/ethernet( 3802): event: NEWLINK(16), flags=0X1003
I/ethernet( 3802): flags: UP BC MC
I/ethernet( 3802): poll state :eth0:17:
V/EthernetStateTracker( 3802): report new state DISCONNECTED on dev eth0 current=eth0
V/EthernetStateTracker( 3802): update network state tracker
I/EthernetStateTracker( 3802): Old status stackConnected=false HWConnected=false
I/EthernetStateTracker( 3802): [EVENT: ether is removed]
D/EthernetStateTracker( 3802): setEthState state=false->false event=4
D/ConnectivityService( 3802): *******targetNetworkType=13
I/EthernetStateTracker( 3802): New status, stackConnected=false HWConnected=false
D/ConnectivityService( 3802): *******targetNetworkType=0
E/MobileDataStateTracker( 3802): default: Ignoring feature request because could not acquire PhoneService
E/MobileDataStateTracker( 3802): default: Could not enable APN type "default"
I/WifiService( 3802): WifiService starting up with Wi-Fi disabled
D/WifiWatchdogStateMachine( 3802): Disabling poor network avoidance for wi-fi only device
I/SystemServer( 3802): Network Service Discovery Service
D/NsdService( 3802): Network service discovery enabled true
I/SystemServer( 3802): Throttle Service
I/SystemServer( 3802): UpdateLock Service
I/SystemServer( 3802): Notification Manager
I/SystemServer( 3802): Device Storage Monitor
I/SystemServer( 3802): Location Manager
I/SystemServer( 3802): Country Detector
I/SystemServer( 3802): Search Service
I/SystemServer( 3802): DropBox Service
I/SystemServer( 3802): Wallpaper Service
W/WallpaperService( 3802): no current wallpaper -- first boot?
I/SystemServer( 3802): Audio Service
D/dalvikvm( 3802): GC_CONCURRENT freed 320K, 39% free 5374K/8716K, paused 2ms+2ms, total 37ms
I/SystemServer( 3802): Dock Observer
W/DockObserver( 3802): This kernel does not have dock station support
I/SystemServer( 3802): Wired Accessory Manager
W/WiredAccessoryManager( 3802): This kernel does not have wired headset support
W/WiredAccessoryManager( 3802): This kernel does not have usb audio support
I/SystemServer( 3802): USB Service
I/SystemServer( 3802): Serial Service
I/SystemServer( 3802): Twilight Service
I/SystemServer( 3802): UI Mode Manager Service
I/SystemServer( 3802): Backup Service
V/BackupManagerService( 3802): No ancestral data
I/BackupManagerService( 3802): Backup enabled => false
I/SystemServer( 3802): AppWidget Service
I/SystemServer( 3802): Recognition Service
I/SystemServer( 3802): DiskStats Service
I/SystemServer( 3802): SamplingProfiler Service
D/dalvikvm( 3802): JIT started for system_server
I/SystemServer( 3802): NetworkTimeUpdateService
I/SystemServer( 3802): CommonTimeManagementService
I/SystemServer( 3802): CertBlacklister
I/SystemServer( 3802): Dreams Service
E/SQLiteLog( 3802): (1) no such table: locksettings
I/LockSettingsService( 3802): Migrated lock settings to new location
I/Zygote  ( 3802): Process: zygote socket opened
I/ActivityManager( 3802): Sending system update to ComponentInfo{com.android.providers.media/com.android.providers.media.MediaUpgradeReceiver} for user 0
I/InputReader( 3802): InputReader::setTvOutStatus 0
I/ActivityManager( 3802): Start proc android.process.media for broadcast com.android.providers.media/.MediaUpgradeReceiver: pid=3981 uid=10009 gids={50009, 1015, 1023, 1024, 1028}
I/WindowManagerService ( 3802): TvOut Intent receiver, tvout status=false
W/InputMethodManagerService( 3802): Illegal subtype state: old subtype = null, new subtype = android.view.inputmethod.InputMethodSubtype@6c61b14
I/ActivityManager( 3802): Launching preboot mode app: ProcessRecord{41191d58 3981:android.process.media/u0a10009}
E/Trace   ( 3981): error opening trace file: No such file or directory (2)
I/ActivityManager( 3802): Writing new set of last done pre-boot receivers...
I/ActivityManager( 3802): Removing system update proc: ProcessRecord{41191d58 3981:android.process.media/u0a10009}
I/ActivityManager( 3802): Killing proc 3981:android.process.media/u0a10009: system update done
I/ActivityManager( 3802): System now ready
I/SystemServer( 3802): Making services ready
I/ActivityManager( 3802): Start proc com.android.systemui for service com.android.systemui/.SystemUIService: pid=3995 uid=10020 gids={50020, 1028, 1015, 3002, 3001}
D/NetworkManagementService( 3802): enabling bandwidth control
I/Vold    ( 2587): /dev/block/vold/8:1 being considered for partition  in volume sda1 at /storage/external_storage/sda1 i=0 type=3
D/Vold    ( 2587): Volume sda1 state changing 1 (Idle-Unmounted) -> 3 (Checking)
D/MountService( 3802): volume state changed for /storage/external_storage/sda1 (unmounted -> checking)
D/MountService( 3802): sendStorageIntent Intent { act=android.intent.action.MEDIA_CHECKING dat=file:///storage/external_storage/sda1 (has extras) } to UserHandle{-1}
I//system/bin/fsck.exfat( 2587): exfatfsck 0.9.5
E/Trace   ( 3995): error opening trace file: No such file or directory (2)
D/SystemUIService( 3995): loading: class com.android.systemui.statusbar.phone.PhoneStatusBar
D/SystemUIService( 3995): running: com.android.systemui.statusbar.phone.PhoneStatusBar@40f490a0
I/StatusBarManagerService( 3802): registerStatusBar bar=com.android.internal.statusbar.IStatusBar$Stub$Proxy@4138e350
D/dalvikvm( 3802): GC_CONCURRENT freed 237K, 37% free 5526K/8716K, paused 3ms+6ms, total 57ms
I/keystore( 2596): uid: 1000 action: e -> 7 state: 3 -> 3 retry: 4
D/dalvikvm( 3995): GC_CONCURRENT freed 110K, 4% free 4474K/4624K, paused 3ms+4ms, total 34ms
D/dalvikvm( 3995): WAIT_FOR_CONCURRENT_GC blocked 2ms
I/keystore( 2596): uid: 1000 action: e -> 7 state: 3 -> 3 retry: 4
E/BluetoothAdapter( 3995): Bluetooth binder is null
E/BluetoothAdapter( 3995): Bluetooth binder is null
E/BluetoothAdapter( 3995): Bluetooth binder is null
I/logwrapper( 2587): /system/bin/fsck.exfat terminated by exit(2)
E/Vold    ( 2587): Filesystem check failed (not a EXFAT filesystem)
W/Vold    ( 2587): /dev/block/vold/8:1 does not contain a exFAT filesystem
D/dalvikvm( 3995): GC_CONCURRENT freed 120K, 4% free 4859K/5016K, paused 1ms+2ms, total 38ms
E/BluetoothAdapter( 3995): Bluetooth binder is null
I//system/bin/fsck_msdos( 2587): ** /dev/block/vold/8:1
I/ActivityManager( 3802): Config changes=200 {1.0 ?mcc?mnc zh_CN ldltr sw720dp w1280dp h720dp 160dpi xlrg long land television -touch -keyb/v/h dpad/v s.4}
W/RecognitionManagerService( 3802): no available voice recognition services found for user 0
W/ContextImpl( 3802): Calling a method in the system process without a qualif[   50.325620@0] FAT-fs (sda1): bogus number of reserved sectors
ied user: android[   50.332084@0] FAT-fs (sda1): Can't find a valid FAT filesystem
.app.ContextImpl.sendBroadcast:1067 com.android.server.usb.UsbSettingsManager.deviceAttached:629 com.android.server.usb.UsbHostManager.usbDeviceAdded:156 com.android.server.usb.UsbHostManager.monitorUsbHostBus:-2 com.android.server.usb.UsbHostManager.access$000:38 
E/UsbSettingsManager( 3802): interfaces0num8
W/ContextImpl( 3802): Calling a method in the system process without a qualified user: android.app.ContextImpl.sendBroadcast:1067 com.android.server.usb.UsbSettingsManager.deviceAttached:629 com.android.server.usb.UsbHostManager.usbDeviceAdded:156 com.android.server.usb.UsbHostManager.monitorUsbHostBus:-2 com.android.server.usb.UsbHostManager.access$000:38 
E/UsbSettingsManager( 3802): interfaces0num9
W/ContextImpl( 3802): Calling a method in the system process without a qualified user: android.app.ContextImpl.sendBroadcast:1067 com.android.server.usb.UsbSettingsManager.deviceAttached:629 com.android.server.usb.UsbHostManager.usbDeviceAdded:156 com.android.server.usb.UsbHostManager.monitorUsbHostBus:-2 com.android.server.usb.UsbHostManager.access$000:38 
W/AppWidgetServiceImpl( 3802): Failed to read state: java.io.FileNotFoundException: /data/system/users/0/appwidgets.xml: open failed: ENOENT (No such file or directory)
E/UsbSettingsManager( 3802): interfaces0num9
E/UsbSettingsManager( 3802): interfaces1num9
W/ContextImpl( 3802): Calling a method in the system process without a qualified user: android.app.ContextImpl.sendBroadcast:1067 com.android.server.usb.UsbSettingsManager.deviceAttached:629 com.android.server.usb.UsbHostManager.usbDeviceAdded:156 com.android.server.usb.UsbHostManager.monitorUsbHostBus:-2 com.android.server.usb.UsbHostManager.access$000:38 
E/UsbSettingsManager( 3802): interfaces0num9
W/ContextImpl( 3802): Calling a method in the system process without a qualified user: android.app.ContextImpl.sendBroadcast:1067 com.android.server.usb.UsbSettingsManager.deviceAttached:629 com.android.server.usb.UsbHostManager.usbDeviceAdded:156 com.android.server.usb.UsbHostManager.monitorUsbHostBus:-2 com.android.server.usb.UsbHostManager.access$000:38 
E/UsbSettingsManager( 3802): interfaces0num255
I//system/bin/fsck_msdos( 2587): Unknown file system version: 3a.38
I/logwrapper( 2587): /system/bin/fsck_msdos terminated by exit(2)
E/Vold    ( 2587): Filesystem check failed (not a FAT filesystem)
W/Vold    ( 2587): /dev/block/vold/8:1 does not contain a FAT filesystem
W/Vold    ( 2587): Skipping fs checks
W/InputMethodManagerService( 3802): Ignoring setImeWindowStatus of uid 1000 token: null
I//system/bin/mount.exfat( 2587): FUSE exfat 0.9.5
I/logwrapper( 2587): /system/bin/mount.exfat terminated by exit(1)
E/Vold    ( 2587): /dev/block/vold/8:1 appears to be a read only filesystem - retrying mount(exfat) RO
I//system/bin/mount.exfat( 2587): FUSE exfat 0.9.5
I/ActivityManager( 3802): Start proc com.amlogic.inputmethod.remote for service com.amlogic.inputmethod.remote/.RemoteIME: pid=4061 uid=10002 gids={50002, 1028}
I/logwrapper( 2587): /system/bin/mount.exfat terminated by exit(1)
D/Vold    ( 2587): /dev/block/vold/8:1 failed to mount via exFAT (I/O error). Trying VFAT...
I/LocationBlacklist( 3802): whitelist: []
E/ThrottleService( 3802): Error reading data file
I/LocationBlacklist( 3802): blacklist: []
D/PhoneStatusBar( 3995): disable: < expand icons alerts ticker system_info back home recent clock search >
W/ServiceWatcher( 3802): com.google.android.location not found
I/CommonTimeManagementService( 3802): No common time service detected on this platform.  Common time services will be unavailable.
I/InputReader( 3802): Reconfiguring input devices.  changes=0x00000020
I/InputReader( 3802): Reconfiguring input devices.  changes=0x00000010
I/ActivityManager( 3802): START u0 {act=android.intent.action.MAIN cat=[android.intent.category.HOME] flg=0x10000000 cmp=com.android.settings/.CryptKeeper} from pid 0
I/ActivityManager( 3802): start package name is com.android.settings, class name is com.android.settings.CryptKeeper, error code is 0
E/Trace   ( 4061): error opening trace file: No such file or directory (2)
D/ActivityManager( 3802): startActivityLocked Luncher start
I/ActivityManager( 3802): Start proc com.android.settings for activity com.android.settings/.CryptKeeper: pid=4080 uid=1000 gids={41000, 1015, 1028, 3003, 3002, 3001, 2001}
W/ServiceWatcher( 3802): com.google.android.location not found
W/ServiceWatcher( 3802): com.google.android.location not found
D/Vold    ( 2587): /dev/block/vold/8:1 failed to mount via VFAT (Invalid argument). Trying NTFS...
W/LocationManagerService( 3802): no network location provider found
I/ActivityManager( 3802): Start proc android.process.media for broadcast com.android.providers.media/.MtpReceiver: pid=4093 uid=10009 gids={50009, 1015, 1023, 1024, 1028}
E/ActivityThread( 3995): Failed to find provider info for com.android.contacts
W/ThrottleService( 3802): unable to find stats for iface rmnet0
I/ActivityManager( 3802): Start proc com.android.location.fused for service com.android.location.fused/.FusedLocationService: pid=4109 uid=10010 gids={50010, 1028}
E/Trace   ( 4080): error opening trace file: No such file or directory (2)
E/Trace   ( 4093): error opening trace file: No such file or directory (2)
W/ServiceWatcher( 3802): com.google.android.location not found
E/Trace   ( 4109): error opening trace file: No such file or directory (2)
E/LocationManagerService( 3802): no geocoder provider found
D/dalvikvm( 3995): GC_CONCURRENT freed 62K, 2% free 5230K/5332K, paused 3ms+4ms, total 94ms
D/dalvikvm( 3802): GC_CONCURRENT freed 316K, 35% free 5717K/8716K, paused 4ms+6ms, total 197ms
E/BluetoothAdapter( 3995): Bluetooth binder is null
W/MountService( 3802): getVolumeState(/storage/sdcard0): Unknown volume
W/ThrottleService( 3802): unable to find stats for iface rmnet0
I/ActivityManager( 3802): START u0 {act=android.intent.action.MAIN cat=[android.intent.category.HOME] flg=0x10000000 cmp=com.android.provision/.DefaultActivity} from pid 0
D/ActivityManager( 3802): startActivityLocked Luncher start
I/ActivityManager( 3802): start package name is com.android.provision, class name is com.android.provision.DefaultActivity, error code is 0
D/SystemUIService( 3995): loading: class com.android.systemui.power.PowerUI
D/SystemUIService( 3995): running: com.android.systemui.power.P[   50.893756@1] 333000
owerUI@40f3fae8
I/ActivityManager( 3802): Start proc com.android.provision for activity com.android.provision/.DefaultActivity: pid=4143 uid=10018 gids={50018, 1028}
D/SystemUIService( 3995): loading: class com.android.systemui.media.RingtonePlayer
D/SystemUIService( 3995): running: com.android.systemui.media.RingtonePlayer@40f39ef0
W/MountService( 3802): getVolumeState(/storage/sdcard0): Unknown volume
V/MediaProvider( 4093): onCreate attaching external storage!
V/MediaProvider( 4093): /storage/emulated/0 volume ID: -1
I/FusedLocation( 4109): engine started (com.android.location.fused)
V/MediaProvider( 4093): /storage/emulated/0 temp volume ID: -1
D/MediaProvider( 4093): genVirtualFsId 50530
V/WallpaperService( 3802): saveSettingsLocked,userId:0
E/Trace   ( 4143): error opening trace file: No such file or directory (2)
W/BackupManagerService( 3802): dataChanged but no participant pkg='com.android.providers.settings' uid=10018
I/MediaProvider( 4093): Upgrading media database from version 0 to 511, which will destroy all old data
I/ActivityManager( 3802): START u0 {act=android.intent.action.MAIN cat=[android.intent.category.HOME] flg=0x10000000 cmp=com.dbstar/.app.DbstarOTTActivity} from pid 0
D/ActivityManager( 3802): startActivityLocked Luncher start
I/ActivityManager( 3802): start package name is com.dbstar, class name is com.dbstar.app.DbstarOTTActivity, error code is 0
I/ActivityManager( 3802): Start proc com.dbstar for activity com.dbstar/.app.DbstarOTTActivity: pid=4172 uid=1000 gids={41000, 1015, 1028, 3003, 3002, 3001, 2001}
[   51.172768@1] 333000
V/WindowManager( 3802): /system/etc/game_dimension_list.txt
E/Trace   ( 4172): error opening trace file: No such file or directory (2)
D/libEGL  ( 3995): loaded /system/lib/egl/libEGL_mali.so
D/libEGL  ( 3995): loaded /system/lib/egl/libGLESv1_CM_mali.so
D/libEGL  ( 3995): loaded /system/lib/egl/libGLESv2_mali.so
D/DVBDataProvider( 4172): initialize in GDDVBDataProvider, dbFile [null]
D/GDDataModel( 4172): APPVersion.GUODIAN is false, do not init GDSmartHomeProvider
D/GDDBProvider( 4172): getDeviceGlobalReadableDatabase: /data/dbstar/Dbstar.db
D/GDDBProvider( 4172): open dbFile = /data/dbstar/Dbstar.db
D/GDDBProvider( 4172): deviceGlobalQuery: table = Global
D/GDDBProvider( 4172): deviceGlobalQuery: db.query(...)
W/BufferQueue( 2591): freeAllBuffersLocked called but mQueue is not empty
D/OpenGLRenderer( 3995): Enabling debug mode 0
D/GDDataModel( 4172): queryDeviceGlobal: query property[CurLanguage] has value[cho]
D/GDDBProvider( 4172): getDeviceGlobalReadableDatabase: /data/dbstar/Dbstar.db
D/GDDBProvider( 4172): open dbFile = /data/dbstar/Dbstar.db
D/GDDBProvider( 4172): deviceGlobalQuery: table = Global
D/GDDBProvider( 4172): deviceGlobalQuery: db.query(...)
D/GDDataModel( 4172): queryDeviceGlobal: query property[CurLanguage] has value[cho]
D/GDDBProvider( 4172): getDeviceGlobalReadableDatabase: /data/dbstar/Dbstar.db
D/GDDBProvider( 4172): open dbFile = /data/dbstar/Dbst[   51.318646@0] duplex
[   51.320759@0] 100m
[   51.323371@0] ADDRCONF(NETDEV_CHANGE): eth0: link becomes ready
[   51.323465@0] changed the Multicast,mcount=3
[   51.323476@0] add mac address:33:33:00:00:00:01,bit=1
[   51.323493@0] add mac address:01:00:5e:00:00:01,bit=32
[   51.323500@0] add mac address:33:33:ff:00:00:02,bit=53
[   51.323505@0] set hash low=2,high=200001
[   51.323509@0] changed the filter setting to :4
ar.db
D/GDDBProvider( 4172): deviceGlobalQuery: table = Global
D/GDDBProvider( 4172): deviceGlobalQuery: db.query(...)
I/pppoe   ( 3802): Event(NEWLINK) NOT from PPP interface, ignore it
V/PppoeMonitor( 3802): net.pppoe.running not FOUND
V/PppoeMonitor( 3802): !net.pppoe.running. Discard event
I/PppoeMonitor( 3802): go poll events
I/ethernet( 3802): event: NEWLINK(16), flags=0X11043
I/ethernet( 3802): flags: UP BC RUNNING MC LINK_UP
I/ethernet( 3802): poll state :eth0:16:
D/ethernet( 3802): eth0: flags = 0x11043, IFF_LOWER_UP
D/ethernet( 3802): eth0: flags [   51.405377@0] Ethernet Driver ioctl (8b1b)
[   51.405395@0] Ethernet Driver unknow ioctl (8b1b) 
= 0x11043, IFF_LOWER_UP
D/GDDataModel( 4172): queryDeviceGlobal: query property[PushDir] has value[null]
D/MultipleLanguageInfoProvider( 4172): -------mConfigure.getStorageDir() = null
I/EthernetStateTracker( 3802): report interface is up for eth0
I/EthernetStateTracker( 3802): Old status stackConnected=false HWConnected=false
I/EthernetStateTracker( 3802): [EVENT: Ether is up]
D/EthernetService( 3802): EthernetNative.isEthDeviceAdded(eth0) return true
D/EthernetStateTracker( 3802): setEthState state=false->false event=5
I/EthernetStateTracker( 3802): New status, stackConnected=false HWConnected=true
D/EthernetService( 3802): EthernetNative.isEthDeviceAdded(eth0) return true
I/EthernetStateTracker( 3802): trigger dhcp for device eth0
D/EthernetStateTracker( 3802): DhcpHandler: DHCP request started
D/EthernetStateTracker( 3802): setEthState state=false->false event=0
I/dhcpcd  ( 4190): dhcpcd: /system/bin/dhcpcd -BK -f /system/etc/dhcpcd/dhcpcd.conf -f /system/etc/dhcpcd/dhcpcd.conf -h android-6d881a01e1dc7b5 eth0
D/StatusBar.NetworkController( 3995): updateEth event=4
E/dhcpcd  ( 4190): disable closefrom(), else no log output
D/dhcpcd  ( 4190): read global config/options from file /system/etc/dhcpcd/dhcpcd.conf
I/dhcpcd  ( 4190): ARP | GATEWAY | IPV4LL | DAEMONISE | LINK | IPV6RS | 
D/dhcpcd  ( 4190): Add options from command line
I/dhcpcd  ( 4190): ARP | GATEWAY | IPV4LL | HOSTNAME | IPV6RS | 
I/dhcpcd  ( 4190): version 5.5.6 starting
I/dhcpcd  ( 4190): ARP | GATEWAY | IPV4LL | HOSTNAME | IPV6RS | 
I/dhcpcd  ( 4190): ARP | GATEWAY | IPV4LL | HOSTNAME | WAITIP | IPV6RS | 
D/dhcpcd  ( 4190): read config/options for eth0 from /system/etc/dhcpcd/dhcpcd.conf
W/ContextImpl( 4172): Calling a method in the system process without a qualified user: android.app.ContextImpl.bindService:1441 android.content.ContextWrapper.bindService:473 com.dbstar.app.GDBaseActivity.onCreate:209 com.dbstar.app.DbstarOTTActivity.onCreate:153 android.app.Activity.performCreate:5131 
I/dhcpcd  ( 4190): ARP | GATEWAY | IPV4LL | HOSTNAME | WAITIP | IPV6RS | 
D/dhcpcd  ( 4190): eth0: using hwaddr 84:26:90:00:00:02
D/dhcpcd  ( 4190): eth0: executing `/system/etc/dhcpcd/dhcpcd-run-hooks', reason PREINIT
E/ActivityThread( 3995): Failed to find provider info for com.android.contacts
D/dalvikvm( 4172): GC_FOR_ALLOC freed 94K, 4% free 4176K/4312K, paused 56ms, total 56ms
I/dalvikvm-heap( 4172): Grow heap (frag case) to 7.629MB for 3686416-byte allocation
D/dalvikvm( 4172): GC_CONCURRENT freed 6K, 2% free 7770K/7916K, paused 2ms+2ms, total 45ms
D/dalvikvm( 3995): GC_FOR_ALLOC freed 69K, 3% free 5298K/5408K, paused 70ms, total 83ms
I/dhcpcd  ( 4190): eth0: sending IPv6 Router Solicitation
E/dhcpcd  ( 4190): eth0: sendmsg: m
I/dhcpcd  ( 4190): offer is null, start_discover
I/dhcpcd  ( 4190): eth0: broadcasting for a lease
D/dhcpcd  ( 4190): eth0: sending DISCOVER (xid 0xd139ae83), next in 3.44 seconds
I/dalvikvm-heap( 3995): Grow heap (frag case) to 14.584MB for 9830416-byte allocation
D/MediaProvider( 4093): Adjusting external storage paths to: /storage/emulated/0
I/Vold    ( 2587): Volume::mount mounted partitions: 0x80000000
D/Vold    ( 2587): Volume sda1 state changing 3 (Checking) -> 4 (Mounted)
D/MountService( 3802): volume state changed for /storage/external_storage/sda1 (checking -> mounted)
E/VoldConnector( 3802): NDC Command {3 volume mount /storage/external_storage/sda1} took too long (1796ms)
W/MountService( 3802): Duplicate state transition (mounted -> mounted) for /storage/emulated/0
D/dalvikvm( 3995): GC_CONCURRENT freed 2K, 1% free 14897K/15012K, paused 2ms+3ms, total 68ms
D/MountService( 3802): sendStorageIntent Intent { act=android.intent.action.MEDIA_MOUNTED dat=file:///storage/external_storage/sda1 (has extras) } to UserHandle{-1}
V/MediaProvider( 4093): setExtVolumeAvailable /storage/emulated/0, -1
E/SQLiteLog( 4093): (1) no such table: ext_volumes
I/EthernetManager( 4172): Init Ethernet Manager
D/DbstarUtil( 4172): getLocalMacAddress84:26:90:00:00:02
I/EthernetService( 3802): setEthState from 2 to 2
V/MediaProvider( 4093):  i [1] -1
D/readQueryPosterFromSDCard( 4172): ∂¡»° ß∞‹
W/System.err( 4172): java.io.FileNotFoundException: /data/dbstar/posterData1.txt: open failed: ENOENT (No such file or directory)
W/System.err( 4172):    at libcore.io.IoBridge.open(IoBridge.java:416)
W/System.err( 4172):    at java.io.FileInputStream.<init>(FileInputStream.java:78)
W/System.err( 4172):    at java.io.FileInputStream.<init>(FileInputStream.java:105)
W/System.err( 4172):    at com.dbstar.util.DbstarUtil.readQueryPosterFromSDcard(DbstarUtil.java:312)
W/System.err( 4172):    at com.dbstar.http.SimpleWorkPool$ReadSDCardData.connect(SimpleWorkPool.java:140)
W/System.err( 4172):    at com.dbstar.http.SimpleWorkPool$ReadSDCardData.execute(SimpleWorkPool.java:135)
W/System.err( 4172):    at com.dbstar.http.SimpleWorkPool$WorkRunable.run(SimpleWorkPool.java:73)
W/System.err( 4172):    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1080)
W/System.err( 4172):    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:573)
W/System.err( 4172):    at java.lang.Thread.run(Thread.java:856)
W/System.err( 4172): Caused by: libcore.io.ErrnoException: open failed: ENOENT (No such file or directory)
W/System.err( 4172):    at libcore.io.Posix.open(Native Method)
W/System.err( 4172):    at libcore.io.BlockGuardOs.open(BlockGuardOs.java:110)
W/System.err( 4172):    at libcore.io.IoBridge.open(IoBridge.java:400)
W/System.err( 4172):    ... 9 more
D/readQueryPosterFromSDCard( 4172): ∂¡»° ß∞‹
W/System.err( 4172): java.io.FileNotFoundException: /data/dbstar/queryRecommand1.txt: open failed: ENOENT (No such file or directory)
W/System.err( 4172):    at libcore.io.IoBridge.open(IoBridge.java:416)
W/System.err( 4172):    at java.io.FileInputStream.<init>(FileInputStream.java:78)
W/System.err( 4172):    at java.io.FileInputStream.<init>(FileInputStream.java:105)
W/System.err( 4172):    at com.dbstar.util.DbstarUtil.readQueryPosterFromSDcard(DbstarUtil.java:312)
W/System.err( 4172):    at com.dbstar.http.SimpleWorkPool$ReadSDCardData.connect(SimpleWorkPool.java:140)
W/System.err( 4172):    at com.dbstar.http.SimpleWorkPool$ReadSDCardData.execute(SimpleWorkPool.java:135)
W/System.err( 4172):    at com.dbstar.http.SimpleWorkPool$WorkRunable.run(SimpleWorkPool.java:73)
W/System.err( 4172):    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1080)
W/System.err( 4172):    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:573)
W/System.err( 4172):    at java.lang.Thread.run(Thread.java:856)
W/System.err( 4172): Caused by: libcore.io.ErrnoException: open failed: ENOENT (No such file or directory)
W/System.err( 4172):    at libcore.io.Posix.open(Native Method)
W/System.err( 4172):    at libcore.io.BlockGuardOs.open(BlockGuardOs.java:110)
W/System.err( 4172):    at libcore.io.IoBridge.open(IoBridge.java:400)
W/System.err( 4172):    ... 9 more
I/dhcpcd  ( 4190): eth0: offered 192.168.1.170 from 192.168.1.1
D/dhcpcd  ( 4190): eth0: sending REQUEST (xid 0xd139ae83), next in 4.35 seconds
V/MediaProvider( 4093): Attached volume: /storage/emulated/0
D/fetchDiskInfo()( 4172):  result = backup
D/fetchDiskInfo()( 4172): cache
D/fetchDiskInfo()( 4172): data
D/fetchDiskInfo()( 4172): loop0
D/fetchDiskInfo()( 4172): loop1
D/fetchDiskInfo()( 4172): loop2
D/fetchDiskInfo()( 4172): loop3
D/fetchDiskInfo()( 4172): loop4
D/fetchDiskInfo()( 4172): loop5
D/fetchDiskInfo()( 4172): loop6
D/fetchDiskInfo()( 4172): loop7
D/fetchDiskInfo()( 4172): mtdblock0
D/fetchDiskInfo()( 4172): mtdblock1
D/fetchDiskInfo()( 4172): mtdblock2
D/fetchDiskInfo()( 4172): mtdblock3
D/fetchDiskInfo()( 4172): mtdblock4
D/fetchDiskInfo()( 4172): mtdblock5
D/fetchDiskInfo()( 4172): mtdblock6
D/fetchDiskInfo()( 4172): mtdblock7
D/fetchDiskInfo()( 4172): mtdblock8
D/fetchDiskInfo()( 4172): mtdblock9
D/fetchDiskInfo()( 4172): sda
D/fetchDiskInfo()( 4172): sda1
D/fetchDiskInfo()( 4172): system
D/fetchDiskInfo()( 4172): vold
D/fetchDiskInfo()( 4172): D/DbstarOTTActivity( 4172):  sda1 is exists = true
D/DbstarOTTActivity( 4172):  before wait time = 87937190
D/DbstarOTTActivity( 4172):  file.canWrite() = true
D/DbstarOTTActivity( 4172):  after wait time = 87937190
D/DbstarOTTActivity( 4172): fileName = .hd_mark
[   52.550376@0] changed the Multicast,mcount=3 = true
[   52.553294@0] add mac address:33:33:00:00:00:01,bit=1
[   52.558319@0] add mac address:01:00:5e:00:00:01,bit=32
[   52.563475@0] add mac address:33:33:ff:00:00:02,bit=53

D/DbstarOTTActivity( 4172): fileName = booklib_bk
D/DbstarOTTActivity( 4172): fileName = ColumnRes
D/DbstarOTTActivity( 4172): fileName = Dbstar.db
D/Dbstar[   52.583988@0] WRITE [GPIOAO_10] 0 
OTTActivity( 4172): fileName = Dbstar.db_test
D/DbstarOTTActivity( 4172): fileName = pushroot
D/DbstarOTTActivity( 4172): fileList.size() = 6
W/ContextImpl( 4172): Calling a method in the system process without a qualified user: android.app.ContextImpl.startService:1387 android.content.ContextWrapper.startService:450 com.dbstar.app.DbstarOTTActivity.onCreate:191 android.app.Activity.performCreate:5131 android.app.Instrumentation.callActivityOnCreate:1090 
W/ContextImpl( 4172): Calling a method in the system process without a qualified user: android.app.ContextImpl.sendBroadcast:1067 android.content.ContextWrapper.sendBroadcast:338 com.dbstar.app.DbstarOTTActivity.onCreate:193 android.app.Activity.performCreate:5131 android.app.Instrumentation.callActivityOnCreate:1090 
D/DbstarOTTActivity( 4172):  timerTag =false
D/GDDataProviderService( 4172): GDDataProviderService onCreate
D/GDDataProviderService( 4172): APPVersion.GUODIAN is false
D/ethernet( 3802): eth0: flags = 0x11043, IFF_LOWER_UP
I/EthernetManager( 4172): Init Ethernet Manager
D/EthernetService( 3802): EthernetNative.isEthDeviceAdded(eth0) return true
D/dalvikvm( 3995): GC_FOR_ALLOC freed <1K, 1% free 14897K/15012K, paused 20ms, total 20ms
I/dalvikvm-heap( 3995): Grow heap (frag case) to 24.300MB for 10188816-byte allocation
D/dalvikvm( 3995): GC_CONCURRENT freed 0K, 1% free 24847K/24964K, paused 4ms+2ms, total 27ms
D/dalvikvm( 3995): WAIT_FOR_CONCURRENT_GC blocked 16ms
I/dhcpcd  ( 4190): eth0: acknowledged 192.168.1.170 from 192.168.1.1
W/BufferQueue( 2591): freeAllBuffersLocked called but mQueue is not empty
I/dhcpcd  ( 4190): eth0: checking for 192.168.1.170
D/dhcpcd  ( 4190): eth0: sending ARP probe (1 of 3), next in 1.10 seconds
I/ActivityManager( 3802): Start proc com.android.music for broadcast com.android.music/.MediaButtonIntentReceiver: pid=4208 uid=10012 gids={50012, 3003, 1015, 1028}
D/NetUtils( 3802): android_net_utils_resetConnections in env=0x40089fd0 clazz=0x68d00001 iface=eth0 mask=0x3
I/dalvikvm( 4208): Turning on JNI app bug workarounds for target SDK version 9...
I/EthernetStateTracker( 3802): reset device eth0
E/Trace   ( 4208): error opening trace file: No such file or directory (2)
D/dalvikvm( 2592): GC_EXPLICIT freed 35K, 2% free 4105K/4180K, paused 3ms+6ms, total 107ms
I/AudioService( 3802):   Remote Control   registerMediaButtonIntent() for PendingIntent{4137eb80: PendingIntentRecord{4137eae0 com.android.[   52.800249@0] Ethernet Driver ioctl (8b1b)
[   52.803080@0] Ethernet Driver unknow ioctl (8b1b) 
music broadcastIntent}}
E/BluetoothAdapter( 4080): Bluetooth binder is null
D/MediaScannerReceiver( 4093): onReceive policy 0 systemscan false
D/MediaScannerReceiver( 4093): action: android.intent.action.MEDIA_MOUNTED path: /storage/external_storage/sda1
D/MediaScannerReceiver( 4093): /storage/external_storage/sda1 system scan skipped
D/dalvikvm( 2592): GC_EXPLICIT freed <1K, 2% free 4105K/4180K, paused 1ms+11ms, total 65ms
D/dalvikvm( 2592): GC_EXPLICIT freed <1K, 2% free 4106K/4180K, paused 17ms+5ms, total 87ms
I/ActivityManager( 3802): Start proc com.settings.ottsettings for broadcast com.settings.ottsettings/com.settings.service.OTTSettingsReceiver: pid=4224 uid=1000 gids={41000, 1015, 1028, 3003, 3002, 3001, 2001}
E/Trace   ( 4224): error opening trace file: No such file or directory (2)
D/OTTSettingsModeService( 4224): videoMode = 720p mDefaultFrequency = 
W/ContextImpl( 4224): Calling a method in the system process without a qualified user: android.app.ContextImpl.sendBroadcast:1067 android.content.ContextWrapper.sendBroadcast:338 android.content.ContextWrapper.sendBroadcast:338 com.settings.service.OTTSettingsReceiver.recoveryVideoMode:213 com.settings.service.OTTSettingsReceiver.onReceive:84 
I/EthernetStateTracker( 3802): Force the connection disconnected before configuration
D/EthernetStateTracker( 3802): setEthState state=false->false event=4
I/EthernetStateTracker( 3802): DhcpHandler: DHCP request failed: DHCP result was failed
D/ethernet( 3802): eth0: flags = 0x11043, IFF_LOWER_UP
D/NetworkController( 4172): =========== hw  ====== 1
D/EthernetService( 3802): EthernetNative.isEthDeviceAdded(eth0) return true
I/EthernetStateTracker( 3802): trigger dhcp for device eth0
I/EthernetService( 3802): $$UpdateEthDevInfo() call resetInterface()
I/ActivityManager( 3802): Start proc com.dbstar.settings for broadcast com.dbstar.settings/.display.OutputSettingsBroadcastReceiver: pid=4240 uid=1000 gids={41000, 1015, 1028, 3003, 3002, 3001, 2001}
D/GDDataProviderService( 4172): Hdmi: IN
D/PeripheralController( 4172): setAudioOutputOff
D/PeripheralController( 4172): setPowerLedOff
D/GDDataProviderService( 4172): Worker Thread [4247] Priority [0]
D/GDDataProviderService( 4172): Worker Thread [4247] Priority [-1]
D/GDDataProviderService( 4172): @@@ 1 Thread [4247]-- Begin Run
D/GDSystemConfigure( 4172): config file property[PushDir]:[/storage/external_storage/sda1]
D/GDSystemConfigure( 4172): defaultValueInit(): mIconRootDir is nothing
D/GDSystemConfigure( 4172): defaultValueInit(): mDbstarDatabase is nothing
D/GDSystemConfigure( 4172): defaultValueInit(): mDefaultStorageDisk[/storage/external_storage/sda1], mIconRootDir[/data/dbstar/ColumnRes], mDbstarDatabase[/data/dbstar/Dbstar.db]
D/GDSystemConfigure( 4172): configureStorage(): disk[/storage/external_storage/sda1] is ready
D/GDDataProviderService( 4172): disk(/storage/external_storage/sda1) is ready, use it as PushDir
D/GDDataProviderService( 4172): Worker Thread [4248] Priority [0]
D/GDDataProviderService( 4172): Worker Thread [4248] Priority [-1]
D/GDDataProviderService( 4172): @@@ 1 Thread [4248]-- Begin Run
I/Choreographer( 3995): Skipped 83 frames!  The application may be doing too much work on its main thread.
E/Trace   ( 4240): error opening trace file: No such file or directory (2)
D/StatusBar.NetworkController( 3995): updateEth event=5
D/StatusBar.NetworkController( 3995): updateEth event=0
D/StatusBar.NetworkController( 3995): updateEth event=4
D/onReceive( 4240): new mode is: 720p
D/onReceive( 4240): old mode is: 720p
D/OutputSettingsBroadcastReceiver( 4240): Action:dbstar.intent.action.OUTPUTMODE_CHANGEcomplete
D/onReceive( 4224): new mode is: 720p
D/onReceive( 4224): old mode is: 720p
D/OutputSettingsBroadcastReceiver( 4224): Action:dbstar.intent.action.OUTPUTMODE_CHANGEcomplete
D/GDDataProviderService( 4172): file[/storage/external_storage/sda1/Dbstar.db] is exist already
D/GDDataProviderService( 4172): dir[/storage/external_storage/sda1/ColumnRes/LocalColumnIcon/] is exist already
D/FileOperation( 4172): readFile(/data/dbstar/channel_file)=
D/GDDataProviderService( 4172): channel =
D/GDDataProviderService( 4172): network is connected false
D/DVBDataProvider( 4172): initialize in GDDVBDataProvider, dbFile [/storage/external_storage/sda1/Dbstar.db]
D/DVBDataProvider( 4172): initialize in GDDVBDataProvider, mDbFile[/storage/external_storage/sda1/Dbstar.db]
D/GDDataModel( 4172): APPVersion.GUODIAN is false, do not init GDSmartHomeProvider
D/GDDBProvider( 4172): getDeviceGlobalReadableDatabase: /data/dbstar/Dbstar.db
D/GDDBProvider( 4172): open dbFile = /data/dbstar/Dbstar.db
D/GDDBProvider( 4172): deviceGlobalQuery: table = Global
D/GDDBProvider( 4172): deviceGlobalQuery: db.query(...)
D/GDDataModel( 4172): updateDeviceGlobal: want set property[PushDir] value[/storage/external_storage/sda1]
D/GDDataModel( 4172): updateDeviceGlobal, insert to Global[PushDir] value=/storage/external_storage/sda1
D/GDDBProvider( 4172): getDeviceGlobalWriteableDatabase: /data/dbstar/Dbstar.db
D/GDDBProvider( 4172): open dbFile = /data/dbstar/Dbstar.db
D/GDDBProvider( 4172):  insert
D/GDDBProvider( 4172):  insert at id=8
W/ContextImpl( 4172): Calling a method in the system process without a qualified user: android.app.ContextImpl.startService:1387 android.content.ContextWrapper.startService:450 com.dbstar.service.client.GDDBStarClient.start:73 com.dbstar.service.GDDataProviderService.onCreate:393 android.app.ActivityThread.handleCreateService:2529 
I/Choreographer( 4172): Skipped 40 frames!  The application may be doing too much work on its main thread.
I/ActivityManager( 3802): Start proc com.dbstar.DbstarDVB for service com.dbstar.DbstarDVB/.DbstarService: pid=4255 uid=1000 gids={41000, 1015, 1028, 3003, 3002, 3001, 2001}
W/ContextImpl( 4172): Calling a method in the system process without a qualified user: android.app.ContextImpl.bindService:1441 android.content.ContextWrapper.bindService:473 com.dbstar.service.client.GDDBStarClient.start:74 com.dbstar.service.GDDataProviderService.onCreate:393 android.app.ActivityThread.handleCreateService:2529 
E/Trace   ( 4255): error opening trace file: No such file or directory (2)
D/EthernetStateTracker( 3802): DhcpHandler: DHCP request started
I/EthernetStateTracker( 3802): Old status stackConnected=false HWConnected=false
D/EthernetStateTracker( 3802): setEthState state=false->false event=0
I/EthernetStateTracker( 3802): [EVENT_INTERFACE_CONFIGURATION_FAILED]
I/EthernetStateTracker( 3802): New status, stackConnected=false HWConnected=false
D/EthernetStateTracker( 3802): setEthState state=false->false event=2
D/StatusBar.NetworkController( 3995): updateEth event=0
D/StatusBar.NetworkController( 3995): updateEth event=2
I/dhcpcd  ( 4269): dhcpcd: /system/bin/dhcpcd -BK -f /system/etc/dhcpcd/dhcpcd.conf -f /system/etc/dhcpcd/dhcpcd.conf -h android-6d881a01e1dc7b5 eth0
E/dhcpcd  ( 4269): disable closefrom(), else no log output
D/dhcpcd  ( 4269): read global config/options from file /system/etc/dhcpcd/dhcpcd.conf
I/dhcpcd  ( 4269): ARP | GATEWAY | IPV4LL | DAEMONISE | LINK | IPV6RS | 
D/dhcpcd  ( 4269): Add options from command line
I/dhcpcd  ( 4269): ARP | GATEWAY | IPV4LL | HOSTNAME | IPV6RS | 
I/dhcpcd  ( 4269): version 5.5.6 starting
I/dhcpcd  ( 4269): ARP | GATEWAY | IPV4LL | HOSTNAME | IPV6RS | 
I/dhcpcd  ( 4269): ARP | GATEWAY | IPV4LL | HOSTNAME | WAITIP | IPV6RS | 
D/dhcpcd  ( 4269): read config/options for eth0 from /system/etc/dhcpcd/dhcpcd.conf
I/dhcpcd  ( 4269): ARP | GATEWAY | IPV4LL | HOSTNAME | WAITIP | IPV6RS | 
D/dhcpcd  ( 4269): eth0: using hwaddr 84:26:90:00:00:02
D/dhcpcd  ( 4269): eth0: executing `/system/etc/dhcpcd/dhcpcd-run-hooks', reason PREINIT
D/dalvikvm( 4172): GC_FOR_ALLOC freed 246K, 4% free 8193K/8480K, paused 24ms, total 26ms
I/dalvikvm-heap( 4172): Grow heap (frag case) to 10.258MB for 2329616-byte allocation
I/dhcpcd  ( 4269): eth0: sending IPv6 Router Solicitation
E/dhcpcd  ( 4269): eth0: sendmsg: m
I/dhcpcd  ( 4269): offer is null, start_discover
I/dhcpcd  ( 4269): eth0: broadcasting for a lease
D/dhcpcd  ( 4269): eth0: sending DISCOVER (xid 0xcc162977), next in 4.97 seconds
D/dalvikvm( 4172): GC_FOR_ALLOC freed 4K, 3% free 10463K/10756K, paused 35ms, total 35ms
I/Dbstar_JNI( 4255): OnLoad() OK
D/DbstarService( 4255): ----- onCreate ----
D/DbstarService( 4255): startObserving()
D/DbstarService( 4255): onBind
I/DbstarService( 4255): startId 1: Intent { cmp=com.dbstar.DbstarDVB/.DbstarService }
D/dalvikvm( 4172): GC_CONCURRENT freed 3K, 3% free 10460K/10756K, paused 3ms+2ms, total 30ms
D/libEGL  ( 4172): loaded /system/lib/egl/libEGL_mali.so
D/libEGL  ( 4172): loaded /system/lib/egl/libGLESv1_CM_mali.so
D/libEGL  ( 4172): loaded /system/lib/egl/libGLESv2_mali.so
I/dhcpcd  ( 4269): eth0: offered 192.168.1.170 from 192.168.1.1
D/dhcpcd  ( 4269): eth0: sending REQUEST (xid 0xcc162977), next in 4.60 seconds
W/BufferQueue( 2591): freeAllBuffersLocked called but mQueue is not empty
D/OpenGLRenderer( 4172): Enabling debug mode 0
D/dalvikvm( 4172): GC_FOR_ALLOC freed <1K, 3% free 10463K/10756K, paused 12ms, total 12ms
I/dalvikvm-heap( 4172): Grow heap (frag case) to 12.475MB for 2329616-byte allocation
D/dalvikvm( 4172): GC_CONCURRENT freed <1K, 3% free 12738K/13032K, paused 2ms+2ms, total 17ms
D/N[   53.625076@1] init: ubootenv.var.firstboot=(null)
etworkController( 4172): ============== ethernet event ===========4
D/NetworkController( 4172): ========= ethernet EVENT_HW_DISCONNECTED ===========1
D/NetworkController( 4172): ========= ethernet EVENT_HW_DISCONNECTED =======1
D/ethernet( 3802): eth0: flags = 0x11043, IFF_LOWER_UP
D/GDBaseActivity( 4172): onServiceStart
D/NetworkController( 4172): ============== ethernet event ===========0
D/NetworkController( 4172): ============== ethernet EVENT_DHCP_START ===========
D/NetworkController( 4172): ============== ethernet event ===========2
D/NetworkController( 4172): ============== ethernet EVENT_INTERFACE_CONFIGURATION_FAILED ===========
D/GDDBStarClient( 4172): +++++++++++GDDBStarClient onServiceConnected+++++++++
D/DbstarService( 4255): initDvbpush()
I/Dbstar_JNI( 4255): dvbpushStart()
D/dvbpush ( 4255): [dvbpush_register_notify:1733] 
D/dvbpush ( 4255): dvbpush_register_notify
D/dvbpush ( 4255): [dvbpush_init:130] 
D/dvbpush ( 4255): dvbpush init...>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.......
D/dvbpush ( 4255): [main_thread:32] 
D/dvbpush ( 4255): main thread start...
D/GDDBStarClient( 4172): +++++++++++startDvbpush+++++++++++
D/EthernetService( 3802): EthernetNative.isEthDeviceAdded(eth0) return true
W/ContextImpl( 4172): Calling a method in the system process without a qualified user: android.app.ContextImpl.sendStickyBroadcast:1202 android.content.ContextWrapper.sendStickyBroadcast:383 com.dbstar.service.client.GDDBStarClient.notifyDbstartServiceStarted:65 com.dbsta[   53.759468@0] SMC CLK SOURCE - 200000KHz
[   53.765510@0] ATR from INT
r.service.client.GDDBStarClient.access$200:21 com.dbstar.service.client.GDDBStarClient$1.onServiceConnected:47 
I/dhcpcd  ( 4269): eth0: acknowledged 192.168.1.170 from 192.168.1.1
D/dalvikvm( 3802): GC_CONCURRENT freed 332K, 34% free 5770K/8716K, paused 3ms+3ms, total 51ms
I/dhcpcd  ( 4269): eth0: checking for 192.168.1.170
D/dhcpcd  ( 4269): eth0: sending ARP probe (1 of 3), next in 1.75 seconds
I/SurfaceFlinger( 2591): Boot is finished (41437 ms)
D/GDDataProviderService( 4172): onReceive System msg android.intent.action.BOOT_COMPLETED
D/SystemUtils( 4172): BOOT_COMPLETED - set video axis
I/ActivityManager( 3802): Displayed com.dbstar/.app.DbstarOTTActivity: +2s574ms (total +3s277ms)
D/SystemUtils( 4172):  write osd black ok!
D/SystemUtils( 4172):  write osd black ok!
D/SystemUtils( 4172): +++++++++++ getSystemStatus()=
E/BluetoothAdapter( 3802): Bluetooth binder is null
E/BluetoothAdapter( 3802): Bluetooth binder is null
V/MediaPlayerService( 2594): decode(16, 0, 3994)
V/MediaPlayerService( 2594): player type = 3
V/MediaPlayerService( 2594):  create StagefrightPlayer
D/dvbpush ( 4255): [push_decoder_buf_init:1141] 
D/dvbpush ( 4255): malloc for push decoder buffer 1502*40000 success
D/dvbpush ( 4255): [main_thread:53] 
D/dvbpush ( 4255): this is a network box, build at Dec 30 2014 18:27:58
V/WiredAccessoryManager( 3802): init()
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff8103)
V/MP3Extractor( 2594): subsequent header is 15009000
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/ActivityManager( 3802): Config changes=1400 {1.0 ?mcc?mnc zh_CN ldltr sw720dp w1280dp h720dp 160dpi xlrg long land television -touch -keyb/v/h dpad/v s.5}
D/MediaScannerReceiver( 4093): onReceive policy 0 systemscan false
V/AwesomePlayer( 2594): createExtractor mime:application/ogg,confidence=0.20, am_mime:,am_confidence=0.00
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/AwesomePlayer( 2594): track of type 'audio/vorbis' does not pu[   53.958923@1] init: sys_prop: permission denied uid:1003  name:service.bootanim.exit
blish bitrate
V/AwesomePlayer( 2594): mBitrate = -1 bits/sec
V/AwesomePlayer( 2594): setDataSource_l extractor->countTracks()=1
V/AwesomePlayer( 2594): setDataSource_l _mime=audio/vorbis
I/AwesomePlayer( 2594): awesomeplayer MEDIA_INFO_AMLOGIC_NO_VIDEO audio[1] video[0]
V/AudioCache( 2594): notify(0x41addd38, 200, 8003, 0)
V/AudioCache( 2594): ignored
V/MediaPlayerService( 2594): prepare
V/MediaPlayerService( 2594): wait for prepare
D/OMXCodec( 2594): current node OMX.google.vorbis.decoder use MS
V/AudioCache( 2594): notify(0x41addd38, 5, 0, 0)
V/AudioCache( 2594): ignored
V/AudioCache( 2594): notify(0x41addd38, 1, 0, 0)
V/AudioCache( 2594): prepared
V/AudioCache( 2594): wait - success
V/MediaPlayerService( 2594): start
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): open(44100, 1, 0x0, 1, 4)
V/MediaPlayerService( 2594): wait for playback complete
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41a5a008, 2048)
V/AudioCache( 2594): memcpy(0x43625000, 0x41a5a008, 2048)
V/AudioCache( 2594): write(0x41a5a008, 742)
V/AudioCache( 2594): memcpy(0x43625800, 0x41a5a008, 742)
V/AwesomePlayer( 2594): MEDIA_PLAYBACK_COMPLETE
V/AudioCache( 2594): notify(0x41addd38, 2, 0, 0)
V/AudioCache( 2594): playback complete
V/AudioCache( 2594): wait - success
V/MediaPlayerService( 2594): return memory @ 0x43625000, sampleRate=44100, channelCount = 1, format = 1
I/RecoverySystem( 3802): Deleted: /cache/recovery/log
V/MediaPlayerService( 2594): decode(16, 0, 5194)
V/MediaPlayerService( 2594): player type = 3
V/MediaPlayerService( 2594):  create StagefrightPlayer
V/MP3Extractor( 2594): found possible 1st frame at 98 (header = 0xffffe203)
V/MP3Extractor( 2594): subsequent header is 000010d2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
V/AwesomePlayer( 2594): createExtractor mime:application/ogg,confidence=0.20, am_mime:,am_confidence=0.00
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/AwesomePlayer( 2594): track of type 'audio/vorbis' does not publish bitrate
V/AwesomePlayer( 2594): mBitrate = -1 bits/sec
V/AwesomePlayer( 2594): setDataSource_l extractor->countTracks()=1
V/AwesomePlayer( 2594): setDataSource_l _mime=audio/vorbis
I/AwesomePlayer( 2594): awesomeplayer MEDIA_INFO_AMLOGIC_NO_VIDEO audio[1] video[0]
V/AudioCache( 2594): notify(0x42dbe878, 200, 8003, 0)
V/AudioCache( 2594): ignored
V/MediaPlayerService( 2594): prepare
V/MediaPlayerService( 2594): wait for prepare
D/OMXCodec( 2594): current node OMX.google.vorbis.decoder use MS
V/AudioCache( 2594): notify(0x42dbe878, 5, 0, 0)
V/AudioCache( 2594): ignored
V/AudioCache( 2594): notify(0x42dbe878, 1, 0, 0)
V/AudioCache( 2594): prepared
V/AudioCache( 2594): wait - success
V/MediaPlayerService( 2594): start
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_[   54.269202@1] init: cannot find '/system/bin/preinstall.sh', disabling 'preinstall'
[   54.276780@1] boot_timer_set: <stop!>,boot_timer_state = 1
[   54.281765@1] disable boot timer2
TRACE:onQueueFilled 358 !
I/Sof[   54.288291@0] get atr len:16 data: 3b 6c 00 00 4e 54 49 43 32 8d dc 28 4a 03 00 00 
tVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): open(44100, 2, 0x0, 1, 4)
V/MediaPlayerService( 2594): wait for playback complete
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43725000, 0x42dbd008, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43726000, 0x42dbd008, 4096)
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43727000, 0x42dbd008, 4096)
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43728000, 0x42dbd008, 4096)
V/AudioCache( 2594): write(0x42dbd008, 1496)
V/AudioCache( 2594): memcpy(0x43729000, 0x42dbd008, 1496)
D/dalvikvm( 3802): GC_CONCURRENT freed 101K, 30% free 6180K/8716K, paused 22ms+4ms, total 91ms
D/dalvikvm( 3802): WAIT_FOR_CONCURRENT_GC blocked 69ms
D/dalvikvm( 3802): WAIT_FOR_CONCURRENT_GC blocked 12ms
D/dalvikvm( 3802): WAIT_FOR_CONCURRENT_GC blocked 68ms
V/AwesomePlayer( 2594): MEDIA_PLAYBACK_COMPLETE
V/AudioCache( 2594): notify(0x42dbe878, 2, 0, 0)
V/AudioCache( 2594): playback complete
V/AudioCache( 2594): wait - success
V/MediaPlayerService( 2594): return memory @ 0x43725000, sampleRate=44100, channelCount = 2, format = 1
D/dalvikvm( 3802): WAIT_FOR_CONCURRENT_GC blocked 13ms
D/dalvikvm( 3802): WAIT_FOR_CONCURRENT_GC blocked 13ms
D/dalvikvm( 3802): WAIT_FOR_CONCURRENT_GC blocked 69ms
V/MediaProvider( 4093): insertInternal: content://media/none/media_scanner, initValues=path=/system/media volume=internal
V/MediaPlayerService( 2594): decode(16, 0, 7392)
V/MediaPlayerService( 2594): player type = 3
V/MediaPlayerService( 2594):  create StagefrightPlayer
V/MP3Extractor( 2594): found possible 1st frame at 98 (header = 0xffffe203)
V/MP3Extractor( 2594): subsequent header is 000010d2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
V/AwesomePlayer( 2594): createExtractor mime:application/ogg,confidence=0.20, am_mime:,am_confidence=0.00
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/AwesomePlayer( 2594): track of type 'audio/vorbis' does not publish bitrate
V/AwesomePlayer( 2594): mBitrate = -1 bits/sec
V/AwesomePlayer( 2594): setDataSource_l extractor->countTracks()=1
V/AwesomePlayer( 2594): setDataSource_l _mime=audio/vorbis
I/AwesomePlayer( 2594): awesomeplayer MEDIA_INFO_AMLOGIC_NO_VIDEO audio[1] video[0]
V/AudioCache( 2594): notify(0x42dbe878, 200, 8003, 0)
V/AudioCache( 2594): ignored
V/MediaPlayerService( 2594): prepare
V/MediaPlayerService( 2594): wait for prepare
D/OMXCodec( 2594): current node OMX.google.vorbis.decoder use MS
V/AudioCache( 2594): notify(0x42dbe878, 5, 0, 0)
V/AudioCache( 2594): ignored
V/AudioCache( 2594): notify(0x42dbe878, 1, 0, 0)
V/AudioCache( 2594): prepared
V/AudioCache( 2594): wait - success
V/MediaPlayerService( 2594): start
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): open(44100, 2, 0x0, 1, 4)
I/ActivityManager( 3802): Start proc com.dbstar.multiple.media.shelf for broadcast com.dbstar.multiple.media.shelf/.share.BootReceiver: pid=4299 uid=1000 gids={41000, 1015, 1028, 3003, 3002, 3001, 2001}
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/MediaPlayerService( 2594): wait for playback complete
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad8010, 4096)
V/AudioCache( 2594): memcpy(0x43825000, 0x41ad8010, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad8010, 4096)
V/AudioCache( 2594): memcpy(0x43826000, 0x41ad8010, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad8010, 4096)
V/AudioCache( 2594): memcpy(0x43827000, 0x41ad8010, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad8010, 4096)
V/AudioCache( 2594): memcpy(0x43828000, 0x41ad8010, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad8010, 4096)
V/AudioCache( 2594): memcpy(0x43829000, 0x41ad8010, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad8010, 4096)
V/AudioCache( 2594): memcpy(0x4382a000, 0x41ad8010, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad8010, 4096)
V/AudioCache( 2594): memcpy(0x4382b000, 0x41ad8010, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad8010, 4096)
V/AudioCache( 2594): memcpy(0x4382c000, 0x41ad8010, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad8010, 4096)
V/AudioCache( 2594): memcpy(0x4382d000, 0x41ad8010, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad8010, 4096)
V/AudioCache( 2594): memcpy(0x4382e000, 0x41ad8010, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad8010, 4096)
V/AudioCache( 2594): memcpy(0x4382f000, 0x41ad8010, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad8010, 4096)
V/AudioCache( 2594): memcpy(0x43830000, 0x41ad8010, 4096)
V/AudioCache( 2594): write(0x41ad8010, 136)
V/AudioCache( 2594): memcpy(0x43831000, 0x41ad8010, 136)
V/AwesomePlayer( 2594): MEDIA_PLAYBACK_COMPLETE
V/AudioCache( 2594): notify(0x42dbe878, 2, 0, 0)
V/AudioCache( 2594): playback complete
V/AudioCache( 2594): wait - success
V/MediaPlayerService( 2594): return memory @ 0x43825000, sampleRate=44100, channelCount = 2, format = 1
E/Trace   ( 4299): error opening trace file: No such file or directory (2)
V/MediaPlayerService( 2594): decode(16, 0, 6193)
V/MediaPlayerService( 2594): player type = 3
W/Searchables( 3802): No global search activity found
V/MediaPlayerService( 2594):  create StagefrightPlayer
V/MP3Extractor( 2594): found possible 1st frame at 98 (header = 0xffffe203)
V/MP3Extractor( 2594): subsequent header is 000010d2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 4088 (header = 0xffff6118)
V/MP3Extractor( 2594): subsequent header is 096287ae
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 4437 (header = 0xfffe9624)
V/MP3Extractor( 2594): subsequent header is 8ed2c380
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
V/AwesomePlayer( 2594): createExtractor mime:application/ogg,confidence=0.20, am_mime:,am_confidence=0.00
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
E/ActivityThread( 3802): Failed to find provider info for downloads
E/BootReceiver( 3802): Can't remove old update packages
E/BootReceiver( 3802): java.lang.IllegalArgumentException: Unknown URL content://downloads/my_downloads
E/BootReceiver( 3802):  at android.content.ContentResolver.delete(ContentResolver.java:954)
E/BootReceiver( 3802):  at android.provider.Downloads.removeAllDownloadsByPackage(Downloads.java:800)
E/BootReceiver( 3802):  at com.android.server.BootReceiver.removeOldUpdatePackages(BootReceiver.java:93)
E/BootReceiver( 3802):  at com.android.server.BootReceiver.access$100(BootReceiver.java:42)
E/BootReceiver( 3802):  at com.android.server.BootReceiver$1.run(BootReceiver.java:82)
I/RM BootReceiver( 4299): onReceive ----------
W/ContextImpl( 4299): Calling a method in the system process without a qualified user: android.app.ContextImpl.startService:1387 android.content.ContextWrapper.startService:450 android.content.ContextWrapper.startService:450 com.dbstar.multiple.media.shelf.share.BootReceiver.onReceive:14 android.app.ActivityThread.handleReceiver:2376 
I/GLog    ( 4299): RMShare[ main: ShareService.java:60 onCreate ] - shareservice onCreate
I/EthernetManager( 4299): Init Ethernet Manager
V/AwesomePlayer( 2594): track of type 'audio/vorbis' does not publish bitrate
V/AwesomePlayer( 2594): mBitrate = -1 bits/sec
V/AwesomePlayer( 2594): setDataSource_l extractor->countTracks()=1
V/AwesomePlayer( 2594): setDataSource_l _mime=audio/vorbis
I/AwesomePlayer( 2594): awesomeplayer MEDIA_INFO_AMLOGIC_NO_VIDEO audio[1] video[0]
V/AudioCache( 2594): notify(0x42dbe878, 200, 8003, 0)
V/AudioCache( 2594): ignored
V/MediaPlayerService( 2594): prepare
V/MediaPlayerService( 2594): wait for prepare
D/OMXCodec( 2594): current node OMX.google.vorbis.decoder use MS
V/AudioCache( 2594): notify(0x42dbe878, 5, 0, 0)
V/AudioCache( 2594): ignored
V/AudioCache( 2594): notify(0x42dbe878, 1, 0, 0)
V/AudioCache( 2594): prepared
V/AudioCache( 2594): wait - success
V/MediaPlayerService( 2594): start
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): open(44100, 2, 0x0, 1, 4)
V/MediaPlayerService( 2594): wait for playback complete
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad7810, 4096)
V/AudioCache( 2594): memcpy(0x43925000, 0x41ad7810, 4096)
V/AudioCache( 2594): write(0x41ad7810, 4096)
V/AudioCache( 2594): memcpy(0x43926000, 0x41ad7810, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad7810, 4096)
V/AudioCache( 2594): memcpy(0x43927000, 0x41ad7810, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad7810, 4096)
V/AudioCache( 2594): memcpy(0x43928000, 0x41ad7810, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad7810, 4096)
V/AudioCache( 2594): memcpy(0x43929000, 0x41ad7810, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad7810, 4096)
V/AudioCache( 2594): memcpy(0x4392a000, 0x41ad7810, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x41ad7810, 4096)
V/AudioCache( 2594): memcpy(0x4392b000, 0x41ad7810, 4096)
V/AudioCache( 2594): write(0x41ad7810, 1108)
V/AudioCache( 2594): [   55.352090@1] SMC CLK SOURCE - 200000KHz
[   55.358425@1] ATR from INT
memcpy(0x4392c000, 0x41ad7810, 1108)
V/AwesomePlayer( 2594): MEDIA_PLAYBACK_COMPLETE
V/AudioCache( 2594): notify(0x42dbe878, 2, 0, 0)
V/AudioCache( 2594): playback complete
V/AudioCache( 2594): wait - success
V/MediaPlayerService( 2594): return memory @ 0x43925000, sampleRate=44100, channelCount = 2, format = 1
V/MediaPlayerService( 2594): decode(16, 0, 7972)
V/MediaPlayerService( 2594): player type = 3
V/MediaPlayerService( 2594):  create StagefrightPlayer
V/MP3Extractor( 2594): found possible 1st frame at 98 (header = 0xffffe203)
V/MP3Extractor( 2594): subsequent header is 000010d2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 6599 (header = 0xfff799d5)
V/MP3Extractor( 2594): subsequent header is f6485516
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
V/AwesomePlayer( 2594): createExtractor mime:application/ogg,confidence=0.20, am_mime:,am_confidence=0.00
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/AwesomePlayer( 2594): track of type 'audio/vorbis' does not publish bitrate
V/AwesomePlayer( 2594): mBitrate = -1 bits/sec
V/AwesomePlayer( 2594): setDataSource_l extractor->countTracks()=1
V/AwesomePlayer( 2594): setDataSource_l _mime=audio/vorbis
I/AwesomePlayer( 2594): awesomeplayer MEDIA_INFO_AMLOGIC_NO_VIDEO audio[1] video[0]
V/AudioCache( 2594): notify(0x42dbe878, 200, 8003, 0)
V/AudioCache( 2594): ignored
V/MediaPlayerService( 2594): prepare
V/MediaPlayerService( 2594): wait for prepare
D/OMXCodec( 2594): current node OMX.google.vorbis.decoder use MS
V/AudioCache( 2594): notify(0x42dbe878, 5, 0, 0)
V/AudioCache( 2594): ignored
V/AudioCache( 2594): notify(0x42dbe878, 1, 0, 0)
V/AudioCache( 2594): prepared
V/AudioCache( 2594): wait - success
V/MediaPlayerService( 2594): start
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): open(44100, 2, 0x0, 1, 4)
V/MediaPlayerService( 2594): wait for playback complete
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a25000, 0x42dbd008, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a26000, 0x42dbd008, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a27000, 0x42dbd008, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a28000, 0x42dbd008, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a29000, 0x42dbd008, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a2a000, 0x42dbd008, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a2b000, 0x42dbd008, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a2c000, 0x42dbd008, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a2d000, 0x42dbd008, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a2e000, 0x42dbd008, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a2f000, 0x42dbd008, 4096)
I/SoftVorbis( 2594): OGG_TRACE:onQueueFilled 358 !
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a30000, 0x42dbd008, 4096)
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a31000, 0x42dbd008, 4096)
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a32000, 0x42dbd008, 4096)
V/AudioCache( 2594): write(0x42dbd008, 4096)
V/AudioCache( 2594): memcpy(0x43a33000, 0x42dbd008, 4096)
V/AudioCache( 2594): write(0x42dbd008, 2768)
V/AudioCache( 2594): memcpy(0x43a34000, 0x42dbd008, 2768)
I/MediaProvider( 4093): Upgrading media database from version 0 to 511, which will destroy all old data
V/AwesomePlayer( 2594): MEDIA_PLAYBACK_COMPLETE
V/AudioCache( 2594): notify(0x42dbe878, 2, 0, 0)
V/AudioCache( 2594): playback complete
V/AudioCache( 2594): wait - success
V/MediaPlayerService( 2594): return memory @ 0x43a25000, sampleRate=44100, channelCount = 2, format = 1
D/dalvikvm( 4093): [   55.878256@1] get atr len:16 data: 3b 6c 00 00 4e 54 49 43 32 8d dc 28 4a 03 00 00 
GC_CONCURRENT freed 204K, 6% free 4278K/4524K, paused 4ms+4ms, total 31ms
D/dvbpush ( 4255): [db_init:922] 
D/dvbpush ( 4255): db_init(/data/dbstar/Dbstar.db)
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "Global" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "Initialize" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "Channel" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "Service" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "TRIGGER_DELETE_Service" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "ResStr" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "ResPoster" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "ResTrailer" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "ResSubTitle" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "Column" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "TRIGGER_DELETE_Column" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "Product" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "TRIGGER_DELETE_Product" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "PublicationsSet" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "TRIGGER_DELETE_PublicationsSet" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "SetInfo" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "Publication" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "TRIGGER_DELETE_Publication" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "MultipleLanguageInfoVA" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "MultipleLanguageInfoRM" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "MultipleLanguageInfoApp" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "Message" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "TRIGGER_DELETE_Message" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "GuideList" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "TRIGGER_DELETE_GuideList" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "ProductDesc" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "TRIGGER_DELETE_ProductDesc" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "Preview" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "SProduct" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "SCEntitleInfo" is exist, OK
D/dvbpush ( 4255): [createDatabase:319] 
D/dvbpush ( 4255): shot tables finished, ret=0
D/dvbpush ( 4255): [db_init:929] 
D/dvbpush ( 4255): open database success
D/dvbpush ( 4255): [sqlite_read_db:1139] 
D/dvbpush ( 4255): [/data/dbstar/Dbstar.db] sqlite read: SELECT Value FROM Global WHERE Name='PushDir';
D/dvbpush ( 4255): [push_dir_init:3000] 
D/dvbpush ( 4255): read PushDir: /storage/external_storage/sda1
D/dvbpush ( 4255): [push_dir_init:3003] 
D/dvbpush ( 4255): use hd /storage/external_storage/sda1 as storage
D/dvbpush ( 4255): [disk_usable_check:1003] 
D/dvbpush ( 4255): /storage/external_storage/sda1(1): TOTAL_SIZE(500104687616 B) FREE_SIZE(485763018752 B)
D/dvbpush ( 4255): [push_dir_init:3017] 
D/dvbpush ( 4255): hd /storage/external_storage/sda1 is ready, 500104687616
D/dvbpush ( 4255): [sqlite_read_db:1139] 
D/dvbpush ( 4255): [/data/dbstar/Dbstar.db] sqlite read: SELECT Value from Global where Name='storage_id';
D/dvbpush ( 4255): [sqlite_read_db:1163] 
D/dvbpush ( 4255): no row, l_row=0, l_column=0
D/dvbpush ( 4255): [storage_id_init:3128] 
D/dvbpush ( 4255): can not read s_previous_storage_id
D/MediaProvider( 4093): Adjusting external storage paths to: /storage/emulated/0
D/dvbpush ( 4255): [storage_id_read:2765] 
D/dvbpush ( 4255): read [459838224] from /storage/external_storage/sda1/.hd_mark
D/dvbpush ( 4255): [storage_init:2915] 
D/dvbpush ( 4255): s_previous_storage_id[], cur_storage_id[459838224]
D/dvbpush ( 4255): [storage_init:2920] 
D/dvbpush ( 4255): storage has changed, init it
D/dvbpush ( 4255): [storage_hd_db_init:2856] 
D/dvbpush ( 4255): database /storage/external_storage/sda1/Dbstar.db is exist already
D/dvbpush ( 4255): [storage_init:2924] 
D/dvbpush ( 4255): work with db in sda1 start
E/dvbpush ( 4255): [packages/dbstar/DbstarDVB/jni/dvbpush/src/porting.c:motherdisc_check:2644] 
E/dvbpush ( 4255): [err note: No such file or directory]
E/dvbpush ( 4255): can not stat(/storage/external_storage/sda1/pushroot/ContentDelivery.xml)
D/dvbpush ( 4255): [motherdisc_check:2645] 
D/dvbpush ( 4255): this is not a mother disc
D/dvbpush ( 4255): [sqlite_execute:975] 
D/dvbpush ( 4255): waiting_cnt=0, DELETE FROM ProductDesc;
D/dvbpush ( 4255): [sqlite_execute:991] 
D/dvbpush ( 4255): DELETE FROM ProductDesc;
E/SQLiteLog( 4093): (1) no such table: files
I/PowerManagerService( 3802): Boot animation finished.
D/dvbpush ( 4255): [sqlite_execute:975] 
D/dvbpush ( 4255): waiting_cnt=0, DELETE FROM GuideList;
D/dvbpush ( 4255): [sqlite_execute:991] 
D/dvbpush ( 4255): DELETE FROM GuideList;
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 97 (header = 0xffff3203)
V/MP3Extractor( 2594): subsequent header is 00000100
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/dvbpush ( 4255): [sqlite_execute:975] 
D/dvbpush ( 4255): waiting_cnt=0, DELETE FROM Initialize;
V/MP3Extractor( 2594): found possible 1st frame at 5273 (header = 0xfffe22e4)
V/MP3Extractor( 2594): subsequent header is 77aa0266
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7207 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is e186dcd1
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7303 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is a94ee762
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7508 (header = 0xfffc9ae4)
V/MP3Extractor( 2594): subsequent header is 65e0bca3
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7613 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is 05870942
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8299 (header = 0xfffcea72)
V/MP3Extractor( 2594): subsequent header is 451e42da
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8607 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is 3115c7a9
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8704 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is 183e586e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8809 (header = 0xfffc9ae4)
V/MP3Extractor( 2594): subsequent header is 082c6298
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8913 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is 845e213d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9111 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is 0e52feff
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9215 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is f6f3a4bc
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9620 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is c48e7121
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9724 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is c0ffdfdd
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/dvbpush ( 4255): [sqlite_execute:991] 
D/dvbpush ( 4255): DELETE FROM Initialize;
V/MP3Extractor( 2594): found possible 1st frame at 10306 (header = 0xfff6a800)
V/MP3Extractor( 2594): subsequent header is bd51f211
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10635 (header = 0xffe6dac7)
V/MP3Extractor( 2594): subsequent header is 530004cb
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 13283 (header = 0xffe2136b)
V/MP3Extractor( 2594): subsequent header is 96d84946
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 13499 (header = 0xfffcea72)
V/MP3Extractor( 2594): subsequent header is 02294764
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 13704 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is a9e51e4d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 13908 (header = 0xfffc9ae4)
V/MP3Extractor( 2594): subsequent header is 849159a1
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14011 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is d81b4f0e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14312 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is 98568ee7
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14517 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is cb8e36d9
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14618 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is 10964eb6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14718 (header = 0xfffc9ae4)
V/MP3Extractor( 2594): subsequent header is e88e7b17
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14820 (header = 0xfffc9ae4)
V/MP3Extractor( 2594): subsequent header is 694d287d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 15019 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is 5a5dd5a7
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 15121 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is a3f56ab5
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 15316 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is ec26c32d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 15422 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is 83a65e5c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 15524 (header = 0xfffc1ae5)
V/MP3Extractor( 2594): subsequent header is a7ac36d9
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16042 (header = 0xfffc9ae5)
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
D/dvbpush ( 4255): [printf_fserrno:96] 
D/dvbpush ( 4255): /storage/external_storage/sda1/pushroot/pushinfo ENOENT
D/dvbpush ( 4255): [push_clear] remove_force(/storage/external_storage/sda1/pushroot/pushinfo) success
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 97 (header = 0xffff9103)
V/MP3Extractor( 2594): subsequent header is 00000442
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff1103)
V/MP3Extractor( 2594): subsequent header is 38303333
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 3375 (header = 0xfff68204)
V/MP3Extractor( 2594): subsequent header is 76196bff
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 3744 (header = 0xfff3884d)
V/MP3Extractor( 2594): subsequent header is 7f99c04d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 4224 (header = 0xfffbd47e)
V/MP3Extractor( 2594): subsequent header is 3d5db11d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 5352 (header = 0xfffe1200)
V/MP3Extractor( 2594): subsequent header is e3d2020d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7873 (header = 0xffffc72b)
V/MP3Extractor( 2594): subsequent header is 1a162cfe
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10008 (header = 0xfffcd2c2)
V/MP3Extractor( 2594): subsequent header is 39056d61
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 11045 (header = 0xffe6d73c)
V/MP3Extractor( 2594): subsequent header is c53f7800
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 18436 (header = 0xffe7aaf6)
V/MP3Extractor( 2594): subsequent header is 075bc6ca
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 19118 (header = 0xffe762d3)
V/MP3Extractor( 2594): subsequent header is 3e13b6a9
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 97 (header = 0xffff3203)
V/MP3Extractor( 2594): subsequent header is 01000001
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 3894 (header = 0xffe5277c)
V/MP3Extractor( 2594): subsequent header is 1f31e310
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7569 (header = 0xfff77ada)
V/MP3Extractor( 2594): subsequent header is 91919a97
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 11262 (header = 0xfffc5a64)
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 4103 (header = 0xffff50ff)
V/MP3Extractor( 2594): subsequent header is b62ffe5c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 4115 (header = 0xffff10ff)
V/MP3Extractor( 2594): subsequent header is d77aeeb5
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 4180 (header = 0xfffebbdf)
V/MP3Extractor( 2594): subsequent header is 9fde01b0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 5159 (header = 0xfff6e63e)
V/MP3Extractor( 2594): subsequent header is 28e3dff0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 6004 (header = 0xfffdbb80)
V/MP3Extractor( 2594): subsequent header is 01fc8116
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8283 (header = 0xffff34ff)
V/MP3Extractor( 2594): subsequent header is 93f10848
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8296 (header = 0xffff5bff)
V/MP3Extractor( 2594): subsequent header is 8fe3fcec
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 12592 (header = 0xffff746d)
V/MP3Extractor( 2594): subsequent header is 7346febd
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 12600 (header = 0xffff8401)
V/MP3Extractor( 2594): subsequent header is 063eacd3
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 12839 (header = 0xfffd8ad6)
V/MP3Extractor( 2594): subsequent header is 00000000
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "GuideList" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "TRIGGER_DELETE_GuideList" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "ProductDesc" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "TRIGGER_DELETE_ProductDesc" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "Preview" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "SProduct" is exist, OK
D/dvbpush ( 4255): [createTable:387] 
D/dvbpush ( 4255): tabel "SCEntitleInfo" is exist, OK
D/dvbpush ( 4255): [createDatabase:319] 
D/dvbpush ( 4255): shot tables finished, ret=0
D/dvbpush ( 4255): [db_init:929] 
D/dvbpush ( 4255): open database success
D/dvbpush ( 4255): [localcolumn_init:1534] 
D/dvbpush ( 4255): init local column, such as 'Settings' or 'My Center
D/dvbpush ( 4255): sqlite_transaction_begin >>
D/dvbpush ( 4255): [sqlite_transaction_read:1404] 
D/dvbpush ( 4255): no row, l_row=0, l_column=0
D/dvbpush ( 4255): [check_record_in_trans:1502] 
D/dvbpush ( 4255): Column has NO ColumnID=L9906
D/dvbpush ( 4255): sqlite_transaction_end 2 << rollback
D/dvbpush ( 4255): rollback transaction
D/dvbpush ( 4255): sqlite_transaction_end 0
D/dvbpush ( 4255): [global_info_init:2107] 
D/dvbpush ( 4255): init table 'Global', set default records with 0
D/dvbpush ( 4255): sqlite_transaction_begin >>
D/dvbpush ( 4255): no global default setting need save
D/dvbpush ( 4255): sqlite_transaction_end 2 << rollback
D/dvbpush ( 4255): rollback transaction
D/dvbpush ( 4255): sqlite_transaction_end 0
D/dvbpush ( 4255): [sqlite_read:1065] 
D/dvbpush ( 4255): waiting_cnt=0
D/dvbpush ( 4255): [sqlite_read:1093] 
D/dvbpush ( 4255): no row, l_row=0, l_column=0
D/dvbpush ( 4255): has no ColumnType 12
D/dvbpush ( 4255): [push_conf_init:201] 
D/dvbpush ( 4255): setting item: INITFILE, value: pushroot/initialize/Initialize.xml
D/dvbpush ( 4255): [push_conf_init:214] 
D/dvbpush ( 4255): read INITFILE as pushroot/initialize/Initialize.xml from push.conf
D/dvbpush ( 4255): [push_conf_init:201] 
D/dvbpush ( 4255): setting item: DATA_DIR, value: /storage/external_storage/sda1
D/dvbpush ( 4255): [push_conf_init:201] 
D/dvbpush ( 4255): setting item: LOG_DIR, value: /data/dbstar
E/dvbpush ( 4255): [packages/dbstar/DbstarDVB/jni/dvbpush/src/common.c:dir_size:830] 
E/dvbpush ( 4255): [err note: No such file or directory]
E/dvbpush ( 4255): can not stat(/data/dbstar/libpush)
D/dvbpush ( 4255): [push_conf_init:206] 
D/dvbpush ( 4255): size of /data/dbstar/libpush is -1
D/dvbpush ( 4255): [push_conf_init:222] 
D/dvbpush ( 4255): check libpush logdir finish
D/dvbpush ( 4255): [sqlite_read_db:1139] 
D/dvbpush ( 4255): [/data/dbstar/Dbstar.db] sqlite read: SELECT Value FROM Global WHERE Name='CurLanguage';
V/MediaProvider( 4093): Inserted /system
D/MediaProvider( 4093): no find storage /system/mediain getStorageId
D/dvbpush ( 4255): [cur_language_init:2575] 
D/dvbpush ( 4255): read Language: cho
D/dvbpush ( 4255): [sqlite_read_db:1139] 
D/dvbpush ( 4255): [/data/dbstar/Dbstar.db] sqlite read: SELECT Value FROM Global WHERE Name='RebootStamp';
D/dvbpush ( 4255): [sqlite_read_db:1163] 
D/dvbpush ( 4255): no row, l_row=0, l_column=0
D/dvbpush ( 4255): [reboot_timestamp_init:3051] 
D/dvbpush ( 4255): read no s_reboot_timestamp_str from db
D/dvbpush ( 4255): [sqlite_read:1065] 
D/dvbpush ( 4255): waiting_cnt=0
D/dvbpush ( 4255): [str_read_cb:904] 
D/dvbpush ( 4255): NULL value
D/dvbpush ( 4255): [push_end_early_hour_init:3083] 
D/dvbpush ( 4255): read no s_onehour_before_pushend from db
D/dvbpush ( 4255): [sqlite_read_db:1139] 
D/dvbpush ( 4255): [/data/dbstar/Dbstar.db] sqlite read: SELECT Value FROM Global WHERE Name='serviceID';
W/BackupManagerService( 3802): dataChanged but no participant pkg='com.android.providers.settings' uid=10009
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 25775 (header = 0xffff164f)
V/MP3Extractor( 2594): subsequent header is 320f0e6f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 26069 (header = 0xffff6afb)
V/MP3Extractor( 2594): subsequent header is 3800f88b
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 26747 (header = 0xfff37007)
V/MP3Extractor( 2594): subsequent header is 01f0ff07
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29097 (header = 0xffe319f6)
V/MP3Extractor( 2594): subsequent header is 9277fa7c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29540 (header = 0xffff78ff)
V/MP3Extractor( 2594): subsequent header is 20e12300
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29543 (header = 0xfffd877f)
V/MP3Extractor( 2594): subsequent header is 50968983
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29943 (header = 0xffff5453)
V/MP3Extractor( 2594): subsequent header is 0b38383e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 30006 (header = 0xfffab578)
V/MP3Extractor( 2594): subsequent header is 08fffa24
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 30583 (header = 0xfffa247c)
V/MP3Extractor( 2594): subsequent header is 605cde8f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 31563 (header = 0xffff58ce)
V/MP3Extractor( 2594): subsequent header is 6030006e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 32920 (header = 0xffe5e5e5)
V/MP3Extractor( 2594): subsequent header is ddac73f9
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 34082 (header = 0xffff46ff)
V/MP3Extractor( 2594): subsequent header is 00b81c00
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 34089 (header = 0xffff234c)
V/MP3Extractor( 2594): subsequent header is ce6e1d00
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 34101 (header = 0xffff50d8)
V/MP3Extractor( 2594): subsequent header is 638ce17c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 36130 (header = 0xffe5975f)
V/MP3Extractor( 2594): subsequent header is 77e3029d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 36944 (header = 0xffe23218)
V/MP3Extractor( 2594): subsequent header is cfe30900
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 37936 (header = 0xfff526b7)
V/MP3Extractor( 2594): subsequent header is c3128e63
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 38352 (header = 0xffff1855)
V/MP3Extractor( 2594): subsequent header is efb79b3e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
W/ContextImpl( 4255): Calling a method in the system process without a qualified user: android.app.ContextImpl.sendBroadcast:1067 android.content.ContextWrapper.sendBroadcast:338 com.dbstar.DbstarDVB.DbstarService.postNotifyMessage:154 dalvik.system.NativeStart.run:-2 <bottom of call stack> 
V/MP3Extractor( 2594): subsequent header is e0de9909
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 58963 (header = 0xfffd5831)
V/MP3Extractor( 2594): subsequent header is 6c4943f5
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 61935 (header = 0xffe258b7)
V/MP3Extractor( 2594): subsequent header is 0c68dea0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 66129 (header = 0xfffa8000)
V/MP3Extractor( 2594): subsequent header is ecca8f20
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 66825 (header = 0xfff2d49d)
V/MP3Extractor( 2594): subsequent header is 146a6853
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 68178 (header = 0xfffa502c)
V/MP3Extractor( 2594): subsequent header is 431a007e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 69923 (header = 0xfff6d48d)
V/MP3Extractor( 2594): subsequent header is e92bc675
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 70360 (header = 0xfff38bb4)
V/MP3Extractor( 2594): subsequent header is 0b3eacef
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 72329 (header = 0xffff4371)
V/MP3Extractor( 2594): subsequent header is 71da2a27
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 72521 (header = 0xfffde5ce)
V/MP3Extractor( 2594): subsequent header is a3fc6d2e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 72922 (header = 0xfffc170f)
V/MP3Extractor( 2594): subsequent header is 3a8aea9c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 75876 (header = 0xfff39716)
V/MP3Extractor( 2594): subsequent header is 594af173
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 79368 (header = 0xfff77406)
V/MP3Extractor( 2594): subsequent header is 261f55c1
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 84050 (header = 0xfffc847e)
V/MP3Extractor( 2594): subsequent header is 7444111c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 86677 (header = 0xffe47afb)
V/MP3Extractor( 2594): subsequent header is 791efecf
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 90213 (header = 0xffffd8d0)
V/MP3Extractor( 2594): subsequent header is 8b862769
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 91034 (header = 0xfffa1313)
V/MP3Extractor( 2594): subsequent header is 1e4cfa66
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 91744 (header = 0xfff279ad)
V/MP3Extractor( 2594): subsequent header is d7b5dda5
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10466 (header = 0xfff7979b)
V/MP3Extractor( 2594): subsequent header is 4ec5fd3c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 11405 (header = 0xffffdabf)
V/MP3Extractor( 2594): subsequent header is 60bac1d4
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16803 (header = 0xffffc0ff)
V/MP3Extractor( 2594): subsequent header is b67f1fe5
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16806 (header = 0xffff13[   58.582252@1] WRITE [GPIOD_1] 1 
ff)
V/MP3Extractor( 2594): subsequent header is 2142753d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16812 (header = 0xffff26ff)
V/MP3Extractor( 2594): subsequent header is 4e9c5c91
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16815 (header = 0xffff23ff)
V/MP3Extractor( 2594): subsequent header is a5ea0b59
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16818 (header = 0xffff38ff)
V/MP3Extractor( 2594): subsequent header is 4c66454e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16821 (header = 0xffff74f9)
V/MP3Extractor( 2594): subsequent header is b3bd4aad
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 21126 (header = 0xffff28ff)
V/MP3Extractor( 2594): subsequent header is 3536bcfc
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 21261 (header = 0xffe73081)
V/MP3Extractor( 2594): subsequent header is 71f135e9
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 23331 (header = 0xfffc887f)
V/MP3Extractor( 2594): subsequent header is 4c086bb8
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 23919 (header = 0xfff6a88c)
V/MP3Extractor( 2594): subsequent header is 4ce648b0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 24082 (header = 0xfffb999e)
V/MP3Extractor( 2594): subsequent header is da4d0ca5
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 25337 (header = 0xffff38ff)
V/MP3Extractor( 2594): subsequent header is f4ef52b5
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 25340 (header = 0xffff56ff)
V/MP3Extractor( 2594): subsequent header is f1b29a76
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 28679 (header = 0xfff2e325)
V/MP3Extractor( 2594): subsequent header is 8b6d9dba
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29586 (header = 0xfffea48a)
V/MP3Extractor( 2594): subsequent header is abb7b2d4
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29627 (header = 0xffff2bff)
V/MP3Extractor( 2594): subsequent header is 4a7ff9db
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29633 (header = 0xffff18ff)
V/MP3Extractor( 2594): subsequent header is f5c915d1
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29636 (header = 0xffff39ff)
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 98 (header = 0xffffc903)
V/MP3Extractor( 2594): subsequent header is 4068c82a
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff8103)
V/MP3Extractor( 2594): subsequent header is 1c84c22c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 98 (header = 0xffffc903)
V/MP3Extractor( 2594): subsequent header is c82a0040
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 5022 (header = 0xffe483f7)
V/MP3Extractor( 2594): subsequent header is cdab7711
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 5780 (header = 0xfffe2bd7)
V/MP3Extractor( 2594): subsequent header is 602237b1
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7440 (header = 0xfffe30b6)
V/MP3Extractor( 2594): subsequent header is 1ee26925
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 100 (header = 0xffff9103)
V/MP3Extractor( 2594): subsequent header is 21851452
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10690 (header = 0xfff2dbc4)
V/MP3Extractor( 2594): subsequent header is 1f5f0c85
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 11180 (header = 0xfff55a72)
V/MP3Extractor( 2594): subsequent header is 974df276
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 13085 (header = 0xfff3c56b)
V/MP3Extractor( 2594): subsequent header is ffd2cf03
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 13509 (header = 0xfff5d1ee)
V/MP3Extractor( 2594): subsequent header is 10bc824b
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16313 (header = 0xffe663b8)
V/MP3Extractor( 2594): subsequent header is 98c994e7
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16409 (header = 0xfffc82e9)
V/MP3Extractor( 2594): subsequent header is 0314182f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 18973 (header = 0xfffe6b7c)
V/MP3Extractor( 2594): subsequent header is 37ac847a
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): subsequent header is 4ec00a00
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10466 (header = 0xfff73429)
V/MP3Extractor( 2594): subsequent header is b33c76da
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff8103)
V/MP3Extractor( 2594): subsequent header is 1c84c22c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 3706 (header = 0xfffe658c)
V/MP3Extractor( 2594): subsequent header is f6aa1214
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 5986 (header = 0xffe6c0ac)
V/MP3Extractor( 2594): subsequent header is 7de00000
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
D/DRMLIB  ( 4255): ######################get STBID pwPlatformID=0x0, pdwUniqueIDpdwUniqueID=0x0 
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 6470 (header = 0xffe75403)
V/MP3Extractor( 2594): subsequent header is 002003c1
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9175 (header = 0xfff6666a)
V/MP3Extractor( 2594): subsequent header is 00ce4b40
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9228 (header = 0xfff54792)
V/MP3Extractor( 2594): subsequent header is f2df8a66
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10977 (header = 0xfffb23f3)
V/MP3Extractor( 2594): subsequent header is 2e655ebb
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 6350 (header = 0xfff465c7)
V/MP3Extractor( 2594): subsequent header is 9cd6e6ab
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7599 (header = 0xffe793a1)
V/MP3Extractor( 2594): subsequent header is 20fabeb4
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8843 (header = 0xfffb5377)
V/MP3Extractor( 2594): subsequent header is 5181bf14
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10039 (header = 0xfffd75f1)
V/MP3Extractor( 2594): subsequent header is 7be1bdad
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10215 (header = 0xfffe189a)
V/MP3Extractor( 2594): subsequent header is ea3f5980
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 11383 (header = 0xfff72800)
V/MP3Extractor( 2594): subsequent header is 50555511
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 100 (header = 0xffff9103)
V/MP3Extractor( 2594): subsequent header is 85145288
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 5854 (header = 0xffe7cb7e)
V/MP3Extractor( 2594): subsequent header is b639eed3
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8547 (header = 0xffffd9ce)
V/MP3Extractor( 2594): subsequent header is fac8fba2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 12543 (header = 0xfffc6544)
V/MP3Extractor( 2594): subsequent header is fe351a18
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 12963 (header = 0xffe653fd)
V/MP3Extractor( 2594): subsequent header is 1ddecb29
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 13374 (header = 0xfffaa767)
V/MP3Extractor( 2594): subsequent header is 91969a4f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14824 (header = 0xffe6a7ee)
V/MP3Extractor( 2594): subsequent header is 60254562
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16357 (header = 0xffff750c)
V/MP3Extractor( 2594): subsequent header is fc983e77
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 17354 (header = 0xfff367ff)
V/MP3Extractor( 2594): subsequent header is e0b05558
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 17755 (header = 0xffff974c)
V/MP3Extractor( 2594): subsequent header is b8d3ca52
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 22204 (header = 0xfff3e24d)
V/MP3Extractor( 2594): subsequent header is ff3df0f0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 25407 (header = 0xfff6c977)
V/MP3Extractor( 2594): subsequent header is bd412900
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 28245 (header = 0xfff73559)
V/MP3Extractor( 2594): subsequent header is 936c8079
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 28755 (header = 0xfffed360)
V/MP3Extractor( 2594): subsequent header is fe978179
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29578 (header = 0xffff6be8)
V/MP3Extractor( 2594): subsequent header is 77e65033
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 31571 (header = 0xfff76718)
V/MP3Extractor( 2594): subsequent header is f505be4c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 32320 (header = 0xffe5196a)
V/MP3Extractor( 2594): subsequent header is a680ce86
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 32496 (header = 0xfffebb9b)
V/MP3Extractor( 2594): subsequent header is c9d4639f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10497 (header = 0xfff22ba6)
V/MP3Extractor( 2594): subsequent header is 74d71509
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 11126 (header = 0xffff5900)
V/MP3Extractor( 2594): subsequent header is 00603b5e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 13749 (header = 0xfff226d2)
V/MP3Extractor( 2594): subsequent header is 3d18bc04
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16188 (header = 0xfff2ab2a)
V/MP3Extractor( 2594): subsequent header is 9fabb81f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 17843 (header = 0xfff4d8af)
V/MP3Extractor( 2594): subsequent header is e61c60a5
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 20037 (header = 0xfffae8f6)
V/MP3Extractor( 2594): subsequent header is cfcadc2b
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 20427 (header = 0xffe7c306)
V/MP3Extractor( 2594): subsequent header is 5ff6454e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 21315 (header = 0xfffcc73f)
V/MP3Extractor( 2594): subsequent header is fd970520
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 22972 (header = 0xfff3660f)
V/MP3Extractor( 2594): subsequent header is 78e9c35a
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 23107 (header = 0xffe6d1e4)
V/MP3Extractor( 2594): subsequent header is e352ce69
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 26763 (header = 0xfffa4601)
V/MP3Extractor( 2594): subsequent header is cb61c038
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 28082 (header = 0xffffd6cf)
V/MP3Extractor( 2594): subsequent header is 773c804f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 28952 (header = 0xfff591c3)
V/MP3Extractor( 2594): subsequent header is f9edb313
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 30390 (header = 0xfffc9265)
V/MP3Extractor( 2594): subsequent header is 9dd2a65f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 30797 (header = 0xffe6e6ad)
V/MP3Extractor( 2594): subsequent header is 0000a0f9
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 31700 (header = 0xfff6a5b8)
V/MP3Extractor( 2594): subsequent header is e787e3cb
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 31721 (header = 0xfff3e269)
V/MP3Extractor( 2594): subsequent header is 0000c1d0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 31916 (header = 0xfffe9299)
V/MP3Extractor( 2594): subsequent header is 19e85226
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 31943 (header = 0xfffa67d9)
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): inserting directory /system/media/audio/notifications
V/MediaProvider( 4093): Returning cached entry for /system/media/audio
D/MediaProvider( 4093): no find storage /system/media/audio/notificationsin getStorageId
E/MetadataRetrieverClient( 2594): failed to extract an album art
V/MediaProvider( 4093): Inserted /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/pixiedust.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_ringtone=false is_alarm=false is_podcast=false bucket_display_name=notifications composer=null title=Pixie Dust mime_type=application/ogg date_added=87942 _display_name=pixiedust.ogg _size=16905 _data=/system/media/audio/notifications/pixiedust.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=6 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1729 is_notification=true title_key=   °•       ?       ?       ?       ?               ?       1       ¶Ã       °§        returned: 7
W/BackupManagerService( 3802): dataChanged but no participant pkg='com.android.providers.settings' uid=10009
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff1103)
V/MP3Extractor( 2594): subsequent header is 20492032
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 4528 (header = 0xfffe9250)
V/MP3Extractor( 2594): subsequent header is eb577326
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9509 (header = 0xffff7b4c)
V/MP3Extractor( 2594): subsequent header is 08a70d99
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10615 (header = 0xfffe39b2)
V/MP3Extractor( 2594): subsequent header is 3d4e6027
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 12792 (header = 0xfff3cb9a)
V/MP3Extractor( 2594): subsequent header is ee0ff8c0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff8103)
V/MP3Extractor( 2594): subsequent header is 39e81c84
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 4265 (header = 0xfff59039)
V/MP3Extractor( 2594): subsequent header is de8956b2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7534 (header = 0xfffcda0a)
V/MP3Extractor( 2594): subsequent header is d8818904
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8679 (header = 0xfffac454)
V/MP3Extractor( 2594): subsequent header is 3400ac80
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9944 (header = 0xffe56166)
V/MP3Extractor( 2594): subsequent header is ccc8fbd1
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 17389 (header = 0xffe66398)
V/MP3Extractor( 2594): subsequent header is 1c0080e4
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 18250 (header = 0xfffd20a1)
W/ContextImpl( 4255): Calling a method in the system process without a qualified user: android.app.ContextImpl.sendBroadcast:1067 android.content.ContextWrapper.sendBroadcast:338 com.dbstar.DbstarDVB.DbstarService.postNotifyMessage:154 dalvik.system.NativeStart.run:-2 <bottom of call stack> 
V/MP3Extractor( 2594): subsequent header is 3376c7c7
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16580 (header = 0xfff3452f)
V/MP3Extractor( 2594): subsequent header is 9c8f5f0a
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 26795 (header = 0xfff73450)
V/MP3Extractor( 2594): subsequent header is 87b42baf
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 28496 (header = 0xffe64000)
V/MP3Extractor( 2594): subsequent header is 1ec4bb36
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 30892 (header = 0xfffd657f)
V/MP3Extractor( 2594): subsequent header is 4577d700
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 31891 (header = 0xfff619ee)
V/MP3Extractor( 2594): subsequent header is 1f6a123c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 39555 (header = 0xfff4b1f5)
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff1103)
V/MP3Extractor( 2594): subsequent header is 20492032
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 6656 (header = 0xfffb4265)
V/MP3Extractor( 2594): subsequent header is 00420015
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 11193 (header = 0xfffb669e)
V/MP3Extractor( 2594): subsequent header is 55d269fd
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 13620 (header = 0xffe58675)
V/MP3Extractor( 2594): subsequent header is c11d10da
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 17882 (header = 0xfffb8591)
V/MP3Extractor( 2594): subsequent header is 495c3ea9
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 19884 (header = 0xfff55204)
V/MP3Extractor( 2594): subsequent header is d9aabea9
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 19953 (header = 0xffe68bb4)
V/MP3Extractor( 2594): subsequent header is b62fb747
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 21110 (header = 0xffe5e758)
V/MP3Extractor( 2594): subsequent header is 99400bec
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 22329 (header = 0xfff7c183)
V/MP3Extractor( 2594): subsequent header is a75e4b14
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 23181 (header = 0xffe597da)
V/MP3Extractor( 2594): subsequent header is a3076808
V/PppoeMonitor( 3802): net.pppoe.running not FOUND
V/PppoeMonitor( 3802): !net.pppoe.running. Discard event
I/PppoeMonitor( 3802): go poll events
D/EthernetStateTracker( 3802): DhcpHandler: DHCP request succeeded: addr: 192.168.1.170/24 mRoutes: 0.0.0.0/0 -> 192.168.1.1 |  dns: 202.106.0.20, dhcpServer: 192.168.1.1 leaseDuration: 259200
I/EthernetStateTracker( 3802): Old status stackConnected=false HWConnected=false
I/EthernetStateTracker( 3802): [EVENT_INTERFACE_CONFIGURATION_SUCCEEDED]
D/EthernetService( 3802): EthernetNative.isEthDeviceAdded(eth0) return true
I/EthernetStateTracker( 3802): Ether is added
D/EthernetStateTracker( 3802): setEthState state=false->true event=1
D/EthernetStateTracker( 3802): ***isConnected: false
D/EthernetStateTracker( 3802): ***isConnected: true
I/EthernetStateTracker( 3802): New status, stackConnected=true HWConnected=true
D/StatusBar.NetworkController( 3995): updateEth event=1
I/EthernetManager( 3995): Init Ethernet Manager
D/EthernetService( 3802): EthernetNative.isEthDeviceAdded(eth0) return true
D/ConnectivityService( 3802): ConnectivityChange for ethernet: CONNECTED/CONNECTED
V/ConnectivityService( 3802): handleConnect: ActiveDefaultNetwork is -1
D/ConnectivityService( 3802): handleConnectivityChange: address are the same reset per doReset linkProperty[9]: resetMask=0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 22790 (header = 0xfff34b7e)
V/MP3Extractor( 2594): subsequent header is bcf64096
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 24766 (header = 0xfffaa32e)
V/MP3Extractor( 2594): subsequent header is 89e59b01
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 97 (header = 0xffff3203)
V/MP3Extractor( 2594): subsequent header is 5468656d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 4811 (header = 0xffe69658)
V/MP3Extractor( 2594): subsequent header is f6b1494e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 5884 (header = 0xffe7e906)
V/MP3Extractor( 2594): subsequent header is 83470b51
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8758 (header = 0xfff3d11b)
V/MP3Extractor( 2594): subsequent header is b5e8f0a8
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16877 (header = 0xfff74b8c)
V/MP3Extractor( 2594): subsequent header is 2746d8e1
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 28492 (header = 0xfff5689a)
V/MP3Extractor( 2594): subsequent header is 497aca4c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 34592 (header = 0xfffbba34)
V/MP3Extractor( 2594): subsequent header is 711c860a
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 35842 (header = 0xffe49bab)
V/MP3Extractor( 2594): subsequent header is 9ece00b9
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 37147 (header = 0xfffa8b1f)
V/MP3Extractor( 2594): subsequent header is 8851e7f3
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9871 (header = 0xfff2a33c)
V/MP3Extractor( 2594): subsequent header is 4a9926e3
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10666 (header = 0xfff37b77)
V/MP3Extractor( 2594): subsequent header is 6eb392d1
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 15235 (header = 0xffe2788c)
V/MP3Extractor( 2594): subsequent header is ceb9e6bd
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16550 (header = 0xfffa7860)
V/MP3Extractor( 2594): subsequent header is cff3c6d7
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16666 (header = 0xfffed14d)
V/MP3Extractor( 2594): subsequent header is c723100f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 19110 (header = 0xfff683eb)
V/MP3Extractor( 2594): subsequent header is 7fe2c3bc
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29983 (header = 0xfffc8bfe)
V/MP3Extractor( 2594): subsequent header is 662ed3d7
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff8103)
V/MP3Extractor( 2594): subsequent header is 84453968
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10059 (header = 0xffe796c0)
V/MP3Extractor( 2594): subsequent header is 4c538ee9
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 15950 (header = 0xfffe4430)
V/MP3Extractor( 2594): subsequent header is c51dee8a
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 26251 (header = 0xffe27079)
V/MP3Extractor( 2594): subsequent header is 9fa4e423
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 39215 (header = 0xfffeaafa)
V/MP3Extractor( 2594): subsequent header is 8d661702
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 45082 (header = 0xfffbba82)
V/MP3Extractor( 2594): subsequent header is 631360c3
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 49833 (header = 0xffff473f)
V/MP3Extractor( 2594): subsequent header is 6987959b
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 50685 (header = 0xfffee38b)
V/MP3Extractor( 2594): subsequent header is e474b333
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 59760 (header = 0xfffb5902)
V/MP3Extractor( 2594): subsequent header is 34029e24
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 61929 (header = 0xfffe5927)
V/MP3Extractor( 2594): subsequent header is 5a407a1e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 67212 (header = 0xfffa92f8)
V/MP3Extractor( 2594): found possible 1st frame at 103618 (header = 0xffe6304c)
V/MP3Extractor( 2594): subsequent header is 06e71808
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 106461 (header = 0xfff66be7)
V/MP3Extractor( 2594): subsequent header is 0bf3ff10
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 110974 (header = 0xfff341ef)
V/MP3Extractor( 2594): subsequent header is 4b73f2e7
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 95 (header = 0xffffc903)
V/MP3Extractor( 2594): subsequent header is 4217e79c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 4636 (header = 0xffff9bde)
V/MP3Extractor( 2594): subsequent header is 1a115e58
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 5939 (header = 0xfff5c77a)
V/MP3Extractor( 2594): subsequent header is ff79a3ff
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7375 (header = 0xfff516a2)
V/MP3Extractor( 2594): subsequent header is a87c96f4
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9741 (header = 0xfffe2941)
V/MP3Extractor( 2594): subsequent header is 78006ce3
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 21774 (header = 0xfffb6374)
V/MP3Extractor( 2594): subsequent header is 9557c4a2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 24677 (header = 0xffe4441d)
V/MP3Extractor( 2594): subsequent header is a723ad10
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff1103)
V/MP3Extractor( 2594): subsequent header is 20492032
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 6885 (header = 0xfff323f3)
V/MP3Extractor( 2594): subsequent header is 2afaa9e4
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7361 (header = 0xfff5c56a)
V/MP3Extractor( 2594): subsequent header is 17f307d5
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16537 (header = 0xfffbe2d9)
V/MP3Extractor( 2594): subsequent header is 0146649d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 20628 (header = 0xfffc3320)
V/MP3Extractor( 2594): subsequent header is d812244a
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 21012 (header = 0xffff9433)
V/MP3Extractor( 2594): subsequent header is 8e787b25
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 4463 (header = 0xfffa9a02)
V/MP3Extractor( 2594): subsequent header is b989e759
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 4956 (header = 0xffe2b702)
V/MP3Extractor( 2594): subsequent header is cff0f15e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7326 (header = 0xffff46a2)
V/MP3Extractor( 2594): subsequent header is 2500b0e3
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7939 (header = 0xffffc4fa)
V/MP3Extractor( 2594): subsequent header is fcc4e4b1
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10280 (header = 0xfff6593c)
V/MP3Extractor( 2594): subsequent header is 82990410
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10491 (header = 0xfffb11ac)
V/MP3Extractor( 2594): subsequent header is 3f590000
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 11726 (header = 0xfff2b99f)
V/MP3Extractor( 2594): subsequent header is 21826086
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 13170 (header = 0xfff45900)
V/MP3Extractor( 2594): subsequent header is 05001ee8
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 13488 (header = 0xfff41a30)
V/MP3Extractor( 2594): subsequent header is 054e5df3
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14502 (header = 0xfff21713)
V/MP3Extractor( 2594): subsequent header is 207f6e87
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16207 (header = 0xffff9709)
V/MP3Extractor( 2594): subsequent header is 686b13d8
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 20652 (header = 0xffe39a84)
V/MP3Extractor( 2594): subsequent header is d9103c00
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 21822 (header = 0xfffb619b)
V/MP3Extractor( 2594): subsequent header is efa50000
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 22635 (header = 0xfff6608c)
V/MP3Extractor( 2594): subsequent header is c5469e7f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 23496 (header = 0xfff7471b)
V/MP3Extractor( 2594): subsequent header is 7f728b51
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 24427 (header = 0xffe48002)
V/MP3Extractor( 2594): subsequent header is 00989f0f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 26803 (header = 0xfffad7d1)
V/MP3Extractor( 2594): subsequent header is be0200c6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 26935 (header = 0xffe2d0c6)
V/MP3Extractor( 2594): subsequent header is 8ca74bcc
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 63746 (header = 0xffff21ff)
V/MP3Extractor( 2594): subsequent header is 0c2941a6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 63749 (header = 0xffff22ff)
V/MP3Extractor( 2594): subsequent header is 89104419
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 63752 (header = 0xffff32ff)
V/MP3Extractor( 2594): subsequent header is 0ab80023
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 63755 (header = 0xffff2860)
V/MP3Extractor( 2594): subsequent header is d6588cc5
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 68005 (header = 0xffe2343a)
V/MP3Extractor( 2594): subsequent header is 7d942952
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 81042 (header = 0xffff18ff)
V/MP3Extractor( 2594): subsequent header is 04523195
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 81045 (header = 0xffff34ff)
V/MP3Extractor( 2594): subsequent header is 40002e03
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 81048 (header = 0xffff29ff)
V/MP3Extractor( 2594): subsequent header is 03c21172
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 81054 (header = 0xffff2aff)
V/MP3Extractor( 2594): subsequent header is 65c113f2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 81057 (header = 0xffffe8ff)
V/MP3Extractor( 2594): subsequent header is 02c40c09
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 81060 (header = 0xffffcb5e)
V/MP3Extractor( 2594): subsequent header is 5d54964b
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 94605 (header = 0xffff195b)
V/MP3Extractor( 2594): subsequent header is fa84f5a8
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 103488 (header = 0xffff3aff)
V/MP3Extractor( 2594): subsequent header is 04404a01
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 103491 (header = 0xffff25ff)
V/MP3Extractor( 2594): subsequent header is 2a03505e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 103494 (header = 0xffff295e)
V/MP3Extractor( 2594): subsequent header is 5b24001d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 107718 (header = 0xffff12ff)
V/MP3Extractor( 2594): subsequent header is e409ddb7
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 112322 (header = 0xffff51ff)
V/MP3Extractor( 2594): subsequent header is 13484242
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 112331 (header = 0xffff3bff)
V/MP3Extractor( 2594): subsequent header is 4c420082
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 112334 (header = 0xffff3bff)
V/MP3Extractor( 2594): subsequent header is 82c0e531
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 112337 (header = 0xffff54ff)
W/BackupManagerService( 3802): dataChanged but no participant pkg='com.android.providers.settings' uid=10009
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 31049 (header = 0xfffae2db)
V/MP3Extractor( 2594): subsequent header is 02dea122
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 35796 (header = 0xffe667fa)
V/MP3Extractor( 2594): subsequent header is 7f6ddfe4
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 36588 (header = 0xfffc3907)
V/MP3Extractor( 2594): subsequent header is affe7e0b
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 38060 (header = 0xffe5c1ba)
V/MP3Extractor( 2594): subsequent header is 7935fd45
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 42819 (header = 0xfffe747f)
V/MP3Extractor( 2594): subsequent header is e500007d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 48029 (header = 0xfff543b7)
V/MP3Extractor( 2594): subsequent header is 147d034c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 49085 (header = 0xfffca150)
V/MP3Extractor( 2594): subsequent header is 4a2efd93
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 49701 (header = 0xfffc74e2)
V/MP3Extractor( 2594): subsequent header is 8006e040
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 50588 (header = 0xffe75b82)
V/MP3Extractor( 2594): subsequent header is c9c0d22c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 51201 (header = 0xffe7c1dc)
V/MP3Extractor( 2594): subsequent header is 300000ec
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 51335 (header = 0xffff779f)
V/MP3Extractor( 2594): subsequent header is bffc5700
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 52522 (header = 0xffff131f)
V/MP3Extractor( 2594): subsequent header is ff0080ea
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 56815 (header = 0xfffb5502)
V/MP3Extractor( 2594): subsequent header is 00d8fa70
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 57694 (header = 0xffe5e96a)
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 58583 (header = 0xfff51160)
V/MP3Extractor( 2594): subsequent header is e15d391c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 59622 (header = 0xffe393ba)
V/MP3Extractor( 2594): subsequent header is 00000000
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
D/dhcpcd  ( 4269): eth0: sending ARP announce (2 of 2)
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 97 (header = 0xffff3203)
V/MP3Extractor( 2594): subsequent header is 79204a61
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 22267 (header = 0xfff2814a)
V/MP3Extractor( 2594): subsequent header is 70a428e2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff1103)
V/MP3Extractor( 2594): subsequent header is 20492032
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9752 (header = 0xfff2a884)
V/MP3Extractor( 2594): subsequent header is ddc086e8
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 11895 (header = 0xfff7e8a5)
V/MP3Extractor( 2594): subsequent header is 883f0198
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14377 (header = 0xfff5b15f)
V/MP3Extractor( 2594): subsequent header is b57b7eb7
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14958 (header = 0xfffeb649)
V/MP3Extractor( 2594): subsequent header is ca28110b
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 17640 (header = 0xfff670ff)
V/MP3Extractor( 2594): subsequent header is 96400d0d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 19061 (header = 0xfff576d1)
V/MP3Extractor( 2594): subsequent header is 36a9de77
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 19543 (header = 0xfff21018)
V/MP3Extractor( 2594): subsequent header is 2723ceed
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 97 (header = 0xffff3203)
V/MP3Extractor( 2594): subsequent header is 61746564
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14129 (header = 0xffe572be)
V/MP3Extractor( 2594): subsequent header is 2bbc75ce
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 22066 (header = 0xfffee798)
V/MP3Extractor( 2594): subsequent header is 53bfaeb7
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 22942 (header = 0xfffceb03)
V/MP3Extractor( 2594): subsequent header is fd5aa472
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 28248 (header = 0xfffb693c)
V/MP3Extractor( 2594): subsequent header is a07fcfed
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29818 (header = 0xfffa913c)
V/MP3Extractor( 2594): subsequent header is e7382edc
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 33587 (header = 0xfffc56e4)
V/MP3Extractor( 2594): subsequent header is 5c514f36
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 7247 (header = 0xffff338a)
V/MP3Extractor( 2594): subsequent header is 61034b21
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9605 (header = 0xfff2627c)
V/MP3Extractor( 2594): subsequent header is 83d1ff16
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10750 (header = 0xffff238a)
V/MP3Extractor( 2594): subsequent header is 057f8026
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 17058 (header = 0xffffb89a)
V/MP3Extractor( 2594): subsequent header is 8add2a0e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 28940 (header = 0xfff3da1f)
V/MP3Extractor( 2594): subsequent header is 0a8b0180
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29276 (header = 0xffe3e475)
V/MP3Extractor( 2594): subsequent header is 00001e5a
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29368 (header = 0xffffbb7e)
V/MP3Extractor( 2594): subsequent header is 8bd9a693
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 30125 (header = 0xffe7e8ab)
V/MP3Extractor( 2594): subsequent header is ce3f81a6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 36509 (header = 0xffff31d3)
V/MP3Extractor( 2594): subsequent header is b92ae6aa
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 44961 (header = 0xfffadb3c)
V/MP3Extractor( 2594): subsequent header is 7ddec9f2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 47517 (header = 0xfff3c33c)
V/MP3Extractor( 2594): subsequent header is 9d7575cf
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 47837 (header = 0xfffd5acf)
V/MP3Extractor( 2594): subsequent header is afe9d19d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 48384 (header = 0xfff7c7f2)
V/MP3Extractor( 2594): subsequent header is 59e737a6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 48721 (header = 0xfff7c7f8)
V/MP3Extractor( 2594): subsequent header is 95767735
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 49595 (header = 0xfffe9437)
V/MP3Extractor( 2594): subsequent header is 27b89272
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 50129 (header = 0xffe7e84f)
V/MP3Extractor( 2594): subsequent header is efeb8319
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 51137 (header = 0xffe7e24f)
V/MP3Extractor( 2594): subsequent header is 375633d7
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 57394 (header = 0xfff3d59c)
V/MP3Extractor( 2594): subsequent header is 886a3bbd
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 63666 (header = 0xffe249d7)
V/MP3Extractor( 2594): subsequent header is 7c53d39e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 68116 (header = 0xfff535bf)
V/MP3Extractor( 2594): subsequent header is de8a0b57
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 68465 (header = 0xfff3989e)
V/MP3Extractor( 2594): subsequent header is b85720b7
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 68634 (header = 0xfffb35be)
V/MP3Extractor( 2594): subsequent header is 9afd7f7f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 68998 (header = 0xfff431fd)
V/MP3Extractor( 2594): subsequent header is a7ee2f4a
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 69443 (header = 0xfff259ef)
V/MP3Extractor( 2594): subsequent header is 8b7a2677
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 69656 (header = 0xfffe943b)
V/MP3Extractor( 2594): subsequent header is 310d7a52
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 71011 (header = 0xffe7e277)
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 71612 (header = 0xfff66494)
V/MP3Extractor( 2594): subsequent header is 14dbe08e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 71751 (header = 0xffe7ea6f)
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 72002 (header = 0xffe7ea4f)
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff1103)
V/MP3Extractor( 2594): subsequent header is 20492032
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 3775 (header = 0xfff29b1c)
V/MP3Extractor( 2594): subsequent header is ede3eeff
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9133 (header = 0xfff77bd5)
V/MP3Extractor( 2594): subsequent header is 4d4b150a
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9150 (header = 0xfff2877f)
V/MP3Extractor( 2594): subsequent header is e37dab47
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 13961 (header = 0xfffae98c)
V/MP3Extractor( 2594): subsequent header is 3faf3c5e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14373 (header = 0xfff66067)
V/MP3Extractor( 2594): subsequent header is eb01334f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 15351 (header = 0xffe3b8e1)
V/MP3Extractor( 2594): subsequent header is bc5125e6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16160 (header = 0xfffc2032)
V/MP3Extractor( 2594): subsequent header is ed4c0cb3
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 16701 (header = 0xffe5a884)
V/MP3Extractor( 2594): subsequent header is 79579cb6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 20942 (header = 0xffe6210d)
V/MP3Extractor( 2594): subsequent header is 63eed216
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 22802 (header = 0xffe786dd)
V/MP3Extractor( 2594): subsequent header is 453e59e6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 35235 (header = 0xfff7c8bc)
V/MP3Extractor( 2594): subsequent header is d708c306
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 37734 (header = 0xfff68321)
V/MP3Extractor( 2594): subsequent header is 1b4e0000
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 39374 (header = 0xfff797bd)
V/MP3Extractor( 2594): subsequent header is 2b804b6a
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 39560 (header = 0xfffd19d0)
V/MP3Extractor( 2594): subsequent header is 8d51cd2b
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 43378 (header = 0xfffad24f)
V/MP3Extractor( 2594): subsequent header is 7e238d11
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 48553 (header = 0xfff33212)
V/MP3Extractor( 2594): subsequent header is b8169d75
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 50413 (header = 0xfffd6686)
V/MP3Extractor( 2594): subsequent header is 666201cc
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 50917 (header = 0xfffbb2d2)
V/MP3Extractor( 2594): subsequent header is 2cd76995
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 52183 (header = 0xfff6cb44)
V/MP3Extractor( 2594): subsequent header is 4fc9497d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 54520 (header = 0xfffd38d8)
V/MP3Extractor( 2594): subsequent header is 35088087
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 58661 (header = 0xffe2836f)
V/MP3Extractor( 2594): subsequent header is ed9a75bb
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 62407 (header = 0xffe2b71b)
V/MP3Extractor( 2594): subsequent header is 29c61db0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 62537 (header = 0xfffa77de)
V/MP3Extractor( 2594): subsequent header is 648eaecb
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 66896 (header = 0xffe511d9)
V/MP3Extractor( 2594): subsequent header is f09d0af3
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 75009 (header = 0xffe553df)
V/MP3Extractor( 2594): subsequent header is 87b2876f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 76188 (header = 0xfff75502)
V/MP3Extractor( 2594): subsequent header is 207d93c0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 79662 (header = 0xfff34bbd)
V/MP3Extractor( 2594): subsequent header is 7334e631
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 81293 (header = 0xfffc9106)
V/MP3Extractor( 2594): subsequent header is 2fddb35c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 86040 (header = 0xfffc96e0)
V/MP3Extractor( 2594): subsequent header is 3a33ecfe
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 95106 (header = 0xffe2623e)
V/MP3Extractor( 2594): subsequent header is c5071ea6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 105405 (header = 0xfff75a5c)
V/MP3Extractor( 2594): subsequent header is 44c05639
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 108955 (header = 0xfffbe2df)
V/MP3Extractor( 2594): subsequent header is 8a34bb69
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 111297 (header = 0xfff5340e)
V/MP3Extractor( 2594): subsequent header is 971f0a76
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 112383 (header = 0xfffc1108)
V/MP3Extractor( 2594): subsequent header is e2026b5f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 114066 (header = 0xfff5e91b)
V/MP3Extractor( 2594): subsequent header is 14ae1e00
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 114409 (header = 0xfff3e16e)
V/MP3Extractor( 2594): subsequent header is d28be5a2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 114964 (header = 0xffe23219)
V/MP3Extractor( 2594): subsequent header is afaaa62b
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 126694 (header = 0xfff23b40)
V/MP3Extractor( 2594): subsequent header is 67a69598
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 132977 (header = 0xffe7851a)
V/MP3Extractor( 2594): subsequent header is 4ea9b3b8
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 133550 (header = 0xffe7150b)
V/MP3Extractor( 2594): subsequent header is 35056013
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 144981 (header = 0xfff33ada)
V/MP3Extractor( 2594): subsequent header is f6e3db09
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 154266 (header = 0xfffab380)
V/MP3Extractor( 2594): subsequent header is 8507dd95
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
D/dalvikvm( 4093): GC_EXPLICIT freed 249K, 7% free 4554K/4864K, paused 2ms+4ms, total 37ms
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff1103)
V/MP3Extractor( 2594): subsequent header is 20492032
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 5781 (header = 0xffe4dbaf)
V/MP3Extractor( 2594): subsequent header is 5de0a0e9
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 21642 (header = 0xfffe3a1b)
V/MP3Extractor( 2594): subsequent header is 41c18d33
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 27060 (header = 0xfff4206c)
V/MP3Extractor( 2594): subsequent header is 6254f1cf
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 27686 (header = 0xfff32240)
V/MP3Extractor( 2594): subsequent header is 902794cb
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 32451 (header = 0xffe59036)
V/MP3Extractor( 2594): subsequent header is 0e800980
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 33345 (header = 0xfff63453)
V/MP3Extractor( 2594): subsequent header is 26864d7e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
I/InputReader( 3802): Reconfiguring input devices.  changes=0x00000010
I/InputReader( 3802): Reconfiguring input devices.  changes=0x00000010
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff1103)
V/MP3Extractor( 2594): subsequent header is 20492032
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
W/Searchables( 3802): No global search activity found
V/MP3Extractor( 2594): found possible 1st frame at 3404 (header = 0xfffeb923)
V/MP3Extractor( 2594): subsequent header is 4599c8f5
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 6539 (header = 0xffff3739)
V/MP3Extractor( 2594): subsequent header is 42b74cd9
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 11217 (header = 0xffffe626)
V/MP3Extractor( 2594): subsequent header is b9030077
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 11218 (header = 0xffe626cd)
V/MP3Extractor( 2594): subsequent header is 0ed01074
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 15576 (header = 0xfff6189d)
V/MP3Extractor( 2594): subsequent header is 1fd556bf
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 17192 (header = 0xfffd40f9)
V/MP3Extractor( 2594): subsequent header is b1a7b7ca
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 17665 (header = 0xffe6239c)
V/MP3Extractor( 2594): subsequent header is 0e5e9c1f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 21199 (header = 0xfffe210d)
V/MP3Extractor( 2594): subsequent header is f58eef1f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29027 (header = 0xffff3674)
V/MP3Extractor( 2594): subsequent header is 939d9995
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 34083 (header = 0xffe635ef)
V/MP3Extractor( 2594): subsequent header is 92d00169
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 35676 (header = 0xfffbd818)
V/MP3Extractor( 2594): subsequent header is b60bc01c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 38605 (header = 0xfffec4cc)
V/MP3Extractor( 2594): subsequent header is f55548e7
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 45079 (header = 0xfffe373a)
V/MP3Extractor( 2594): subsequent header is 5cbc2fd0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
W/Searchables( 3802): No global search activity found
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 97 (header = 0xffff3203)
V/MP3Extractor( 2594): subsequent header is 41776179
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8388 (header = 0xfff416fa)
V/MP3Extractor( 2594): subsequent header is b3c70ccb
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9499 (header = 0xffe7b058)
V/MP3Extractor( 2594): subsequent header is 55a1f009
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 10832 (header = 0xfff48059)
V/MP3Extractor( 2594): subsequent header is ef01c0a5
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 15170 (header = 0xffe54700)
V/MP3Extractor( 2594): subsequent header is 0598d4c0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 20551 (header = 0xfffd2800)
V/MP3Extractor( 2594): subsequent header is 7b2691f0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 23449 (header = 0xfffda2c1)
V/MP3Extractor( 2594): subsequent header is 047c57b1
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 25644 (header = 0xfffce7be)
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 8264 (header = 0xfffe8a2d)
V/MP3Extractor( 2594): subsequent header is 7c96c7f1
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8384 (header = 0xffe56a03)
V/MP3Extractor( 2594): subsequent header is 9ce0b2e2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14422 (header = 0xfffb3141)
V/MP3Extractor( 2594): subsequent header is 4297e157
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 20774 (header = 0xffe345f0)
V/MP3Extractor( 2594): subsequent header is c8ad7c0b
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 22096 (header = 0xffe35bf0)
V/MP3Extractor( 2594): subsequent header is 69090067
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 34325 (header = 0xfffe86b7)
V/MP3Extractor( 2594): subsequent header is 4a00b6b8
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 43399 (header = 0xfffc7b00)
V/MP3Extractor( 2594): subsequent header is 20bd2684
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 55168 (header = 0xfff54201)
V/MP3Extractor( 2594): subsequent header is 396337f6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 56743 (header = 0xffe27a76)
V/MP3Extractor( 2594): subsequent header is 2e4505ac
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 59577 (header = 0xfff4ea3e)
V/MP3Extractor( 2594): subsequent header is fa019efa
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 70229 (header = 0xfff72400)
V/MP3Extractor( 2594): subsequent header is c0f6e280
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 74505 (header = 0xffe222a8)
V/MP3Extractor( 2594): subsequent header is f632c668
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 78717 (header = 0xfffa9475)
V/MP3Extractor( 2594): subsequent header is c113ab04
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 85743 (header = 0xfff6a52b)
V/MP3Extractor( 2594): subsequent header is 4176321c
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 93371 (header = 0xfff2e06d)
V/MP3Extractor( 2594): subsequent header is bb27d7ab
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 95525 (header = 0xfff6c330)
V/MP3Extractor( 2594): subsequent header is 7100d8c4
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 97588 (header = 0xfffb30ce)
V/MP3Extractor( 2594): subsequent header is 04008003
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 110719 (header = 0xffe6e556)
V/MP3Extractor( 2594): subsequent header is c826267f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 119168 (header = 0xfff5829f)
V/MP3Extractor( 2594): subsequent header is 18a4744e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 127410 (header = 0xffe5827e)
V/MP3Extractor( 2594): subsequent header is 202fbd58
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 135186 (header = 0xfffa86dc)
V/MP3Extractor( 2594): subsequent header is 4b1b29fc
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 141434 (header = 0xffffe25c)
V/MP3Extractor( 2594): subsequent header is 7bd314f9
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 145467 (header = 0xffe3c8db)
V/MP3Extractor( 2594): subsequent header is c689b551
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 145708 (header = 0xfff35a7a)
V/MP3Extractor( 2594): subsequent header is 92be6513
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 151126 (header = 0xffe27077)
V/MP3Extractor( 2594): subsequent header is ffd9f15f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 153202 (header = 0xfff68aab)
V/MP3Extractor( 2594): subsequent header is 3169503f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 154850 (header = 0xfffd636a)
V/MP3Extractor( 2594): subsequent header is c2993ad6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 158379 (header = 0xffe3b612)
V/MP3Extractor( 2594): subsequent header is a5ac026d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 175118 (header = 0xffe25ad9)
V/MP3Extractor( 2594): subsequent header is fc2802a0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 183331 (header = 0xfffdc3da)
V/MP3Extractor( 2594): subsequent header is 8c9d2c00
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 184842 (header = 0xfffa67ff)
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
I/dhcpcd  ( 4269): eth0: sending IPv6 Router Solicitation
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 97 (header = 0xffff3203)
V/MP3Extractor( 2594): subsequent header is 6c61726d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 5152 (header = 0xffff69a0)
V/MP3Extractor( 2594): subsequent header is c0dd0100
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 5898 (header = 0xffe6d1df)
V/MP3Extractor( 2594): subsequent header is 320350bb
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7383 (header = 0xfffb67e6)
V/MP3Extractor( 2594): subsequent header is d602f83e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 19837 (header = 0xfff4931d)
V/MP3Extractor( 2594): subsequent header is b7b7404b
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 26185 (header = 0xfffd2bbb)
V/MP3Extractor( 2594): subsequent header is c91725aa
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 5001 (header = 0xfff2a2ff)
V/MP3Extractor( 2594): subsequent header is 4edf4526
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 6784 (header = 0xfffe7b00)
V/MP3Extractor( 2594): subsequent header is d795d1c6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 11838 (header = 0xfff3b105)
V/MP3Extractor( 2594): subsequent header is 0de01c00
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14651 (header = 0xfffcb467)
V/MP3Extractor( 2594): subsequent header is 747bb4d2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 97 (header = 0xffff3203)
V/MP3Extractor( 2594): subsequent header is 00000041
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 11373 (header = 0xffff1002)
V/MP3Extractor( 2594): subsequent header is 03303ddd
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 14881 (header = 0xfff25a59)
V/MP3Extractor( 2594): subsequent header is 0000fd02
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 19978 (header = 0xfffa7bf9)
V/MP3Extractor( 2594): subsequent header is 0ac880cd
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 20371 (header = 0xfffae1e4)
V/MP3Extractor( 2594): subsequent header is 1941756a
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 23578 (header = 0xfff57830)
V/MP3Extractor( 2594): subsequent header is 82d760f6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 4137 (header = 0xfff6a169)
V/MP3Extractor( 2594): subsequent header is a2356800
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 6375 (header = 0xffff6af8)
V/MP3Extractor( 2594): subsequent header is 6d008a08
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8781 (header = 0xfffbcb6f)
V/MP3Extractor( 2594): subsequent header is 915f5b4d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 8811 (header = 0xfffec36d)
V/MP3Extractor( 2594): subsequent header is e7e6fe80
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 12462 (header = 0xffe79601)
V/MP3Extractor( 2594): subsequent header is efce3e99
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 13284 (header = 0xffe34505)
V/MP3Extractor( 2594): subsequent header is 00444d1b
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 21443 (header = 0xfffd9893)
V/MP3Extractor( 2594): subsequent header is fc7fbc94
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 25756 (header = 0xfffc497d)
V/MP3Extractor( 2594): subsequent header is 4f000000
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 27420 (header = 0xffe23300)
V/MP3Extractor( 2594): subsequent header is feffa05d
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 27440 (header = 0xfffe65e2)
V/MP3Extractor( 2594): subsequent header is 80e1dfbe
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 29036 (header = 0xfff43892)
V/MP3Extractor( 2594): subsequent header is 74d591bf
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 35901 (header = 0xffe7246a)
V/MP3Extractor( 2594): subsequent header is 48329009
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 44067 (header = 0xfff2cace)
V/MP3Extractor( 2594): subsequent header is ee577755
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 48158 (header = 0xfffb95d1)
V/MP3Extractor( 2594): subsequent header is 44c113b8
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 48903 (header = 0xfffd8705)
V/MP3Extractor( 2594): subsequent header is 3d2423e0
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 53430 (header = 0xfffd7704)
V/MP3Extractor( 2594): subsequent header is 0705a038
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 55543 (header = 0xfff4cb34)
V/MP3Extractor( 2594): subsequent header is 6de38d15
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 57916 (header = 0xfff780ed)
V/MP3Extractor( 2594): subsequent header is 2fa60e00
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 61712 (header = 0xffe6c1fe)
V/MP3Extractor( 2594): subsequent header is 5341433e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 67130 (header = 0xfff581f1)
V/MP3Extractor( 2594): subsequent header is db8eb8ed
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 68822 (header = 0xfff73149)
V/MP3Extractor( 2594): subsequent header is fb19eaf4
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 71031 (header = 0xffffd165)
V/MP3Extractor( 2594): subsequent header is 00314091
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 71760 (header = 0xfffc5965)
V/MP3Extractor( 2594): subsequent header is 7b29859e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 72051 (header = 0xffffa7cb)
V/MP3Extractor( 2594): subsequent header is e4aa60de
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 73168 (header = 0xfffd6b94)
V/MP3Extractor( 2594): subsequent header is 7083f7ac
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 73594 (header = 0xfffe33ca)
V/MP3Extractor( 2594): subsequent header is c9951ad6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 74004 (header = 0xffff34d9)
V/MP3Extractor( 2594): subsequent header is c5d5afeb
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 75474 (header = 0xffffaab2)
V/MP3Extractor( 2594): subsequent header is aa719c24
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 76267 (header = 0xfffe5565)
V/MP3Extractor( 2594): subsequent header is 00076c36
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 77860 (header = 0xfffe53d4)
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff8103)
V/MP3Extractor( 2594): subsequent header is 15009000
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff8103)
V/MP3Extractor( 2594): subsequent header is 15009000
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 98 (header = 0xffffe203)
V/MP3Extractor( 2594): subsequent header is 000010d2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 4088 (header = 0xffff6118)
V/MP3Extractor( 2594): subsequent header is 096287ae
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 4437 (header = 0xfffe9624)
V/MP3Extractor( 2594): subsequent header is 8ed2c380
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 98 (header = 0xffffe203)
V/MP3Extractor( 2594): subsequent header is 000010d2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 6599 (header = 0xfff799d5)
V/MP3Extractor( 2594): subsequent header is f6485516
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st fram[   67.207616@1] init: no such service 'wififix'
e at 98 (header = 0xffffe203)
V/MP3Extractor( 2594): subsequent header is 000010d2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 98 (header = 0xffffe203)
V/MP3Extractor( 2594): subsequent header is 000010d2
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff1103)
V/MP3Extractor( 2594): subsequent header is 20492032
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff8103)
V/MP3Extractor( 2594): subsequent header is 15009000
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff1103)
V/MP3Extractor( 2594): subsequent header is 20492032
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff1103)
V/MP3Extractor( 2594): subsequent header is 20492032
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff1103)
V/MP3Extractor( 2594): subsequent header is 20492032
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 4334 (header = 0xffe625dd)
V/MP3Extractor( 2594): subsequent header is a8fec59e
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 4341 (header = 0xffff5bf5)
V/MP3Extractor( 2594): subsequent header is be6ac2bb
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
V/MP3Extractor( 2594): found possible 1st frame at 4908 (header = 0xfff48801)
V/MP3Extractor( 2594): subsequent header is 392df1a6
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 6777 (header = 0xfffe5700)
V/MP3Extractor( 2594): subsequent header is 2288c5c8
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 7347 (header = 0xfffe3b34)
V/MP3Extractor( 2594): subsequent header is 1781176f
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
V/MP3Extractor( 2594): found possible 1st frame at 9123 (header = 0xfff4e291)
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
D/Tethering( 3802): MasterInitialState.processMessage what=3
D/GDDataProviderService( 4172): ConnectivityManager Action: android.net.conn.CONNECTIVITY_CHANGE
I/GLog    ( 4299): RMShare[ main: ShareService.java:90 initServerThread ] - initServerThread +++++++++++++++++++++++++++++
D/GDDataProviderService( 4172): wifi disconnected
D/GDDataProviderService( 4172): ++++++++++++ network disconnected +++++++++++
D/GDDataProviderService( 4172): ethernet is disconnected already!
I/GLog    ( 4299): RMShare[ main: ShareService.java:93 initServerThread ] - isNetworkConnected = true
I/GLog    ( 4299): RMShare[ main: ShareService.java:95 initServerThread ] - getBestIpAddress = 192.168.1.170
I/GLog    ( 4299): RMShare[ main: ShareService.java:97 initServerThread ] - mServerThread = null
I/GLog    ( 4299): RM ServerThread[ main: ServerThread.java:48 <init> ] - new ServerThread ip = 192.168.1.170
I/GLog    ( 4299): RM ServerThread[ Thread-55: ServerThread.java:62 send ] - Waiting for connections.
V/MediaProvider( 4093): insertInternal: content://media/internal/file, initValues=title=audio mime_type=null date_modified=87818 _size=0 format=12289 _data=/system/media/audio is_drm=false
D/MediaProvider( 4093): insert path/system/media/audio
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media
D/MediaProvider( 4093): no find storage /system/media/audioin getStorageId
V/MediaProvider( 4093): insertFile: values=bucket_id=1664432563 media_type=0 title=audio storage_id=65537 mime_type=null date_added=87946 date_modified=87818 parent=2 _size=4096 format=12289 _data=/system/media/audio bucket_display_name=media is_drm=false returned: 10
V/MediaProvider( 4093): insertInternal: content://media/internal/file, initValues=title=alarms mime_type=null date_modified=87818 _size=0 format=12289 _data=/system/media/audio/alarms is_drm=false
D/MediaProvider( 4093): insert path/system/media/audio/alarms
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio
D/MediaProvider( 4093): no find storage /system/media/audio/alarmsin getStorageId
V/MediaProvider( 4093): insertFile: values=bucket_id=-43825030 media_type=0 title=alarms storage_id=65537 mime_type=null date_added=87946 date_modified=87818 parent=10 _size=4096 format=12289 _data=/system/media/audio/alarms bucket_display_name=audio is_drm=false returned: 11
V/MediaProvider( 4093): insertInternal[   67.883335@0] rtl8192cu driver version=v4.0.2_9000.20130911
[   67.886737@0] build time: Dec 29 2014 19:10:59
[   67.891278@0] 
[   67.891280@0] usb_endpoint_descriptor(0):
[   67.896777@0] bLength=7
[   67.899184@0] bDescriptorType=5
[   67.902312@0] bEndpointAddress=81
[   67.905587@0] wMaxPacketSize=200
[   67.908876@0] bInterval=0
[   67.911404@0] RT_usb_endpoint_is_bulk_in = 1
[   67.915677@0] 
[   67.915680@0] usb_endpoint_descriptor(1):
[   67.921336@0] bLength=7
[   67.923728@0] bDescriptorType=5
[   67.926875@0] bEndpointAddress=2
[   67.930075@0] wMaxPacketSize=200
[   67.933301@0] bInterval=0
[   67.935880@0] RT_usb_endpoint_is_bulk_out = 2
[   67.940371@0] 
[   67.940374@0] usb_endpoint_descriptor(2):
[   67.945894@0] bLength=7
[   67.948304@0] bDescriptorType=5
[   67.951438@0] bEndpointAddress=3
[   67.954628@0] wMaxPacketSize=200
[   67.957876@0] bInterval=0
[   67.960481@0] RT_usb_endpoint_is_bulk_out = 3
[   67.964806@0] 
[   67.964809@0] usb_endpoint_descriptor(3):
[   67.970460@0] bLength=7
[   67.972856@0] bDescriptorType=5
[   67.976006@0] bEndpointAddress=84
[   67.979295@0] wMaxPacketSize=40
[   67.982435@0] bInterval=1
[   67.985009@0] RT_usb_endpoint_is_int_in = 4, Interval = 1
[   67.990419@0] nr_endpoint=4, in_num=2, out_num=2
[   67.990423@0] 
[   67.996667@0] USB_SPEED_HIGH
[   67.999727@0] CHIP TYPE: RTL8188C_8192C
[   68.003383@0] register rtw_netdev_ops to netdev_ops
[   68.008478@0] Chip Version ID: VERSION_NORMAL_TSMC_CHIP_92C.
[   68.013850@0] RF_Type is 2!!
[   68.017100@0] EEPROM type is E-FUSE
[   68.020208@0] ====> ReadAdapterInfo8192C
[   68.024228@0] Boot from EFUSE, Autoload OK !
: content://media/internal/file, initValues=title=notifications mime_type=null date_modified=87818 _size=0 format=12289 _data=/system/media/audio/notifications is_drm=false
D/MediaProvider( 4093): insert path/system/media/audio/notifications
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio
D/MediaProvider( 4093): no find storage /system/media/audio/notificationsin getStorageId
V/MediaProvider( 4093): insertFile: values=bucket_id=-43825030 media_type=0 title=notifications storage_id=65537 mime_type=null date_added=87946 date_modified=87818 parent=10 _size=4096 format=12289 _data=/system/media/audio/notifications bucket_display_name=audio is_drm=false returned: 12
V/MediaProvider( 4093): insertInternal: content://media/internal/file, initValues=title=ringtones mime_type=null date_modified=87818 _size=0 format=12289 _data=/system/media/audio/ringtones is_drm=false
D/MediaProvider( 4093): insert path/system/media/audio/ringtones
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio
D/MediaProvider( 4093): no find storage /system/media/audio/ringtonesin getStorageId
V/MediaProvider( 4093): insertFile: values=bucket_id=-43825030 media_type=0 title=ringtones storage_id=65537 mime_type=null date_added=87946 date_modified=87818 parent=10 _size=4096 format=12289 _data=/system/media/audio/ringtones bucket_display_name=audio is_drm=false ret[   68.162014@0] EEPROMVID = 0x0bda
[   68.162862@0] EEPROMPID = 0x8178
[   68.166102@0] EEPROMCustomerID : 0x00
[   68.169756@0] EEPROMSubCustomerID: 0x00
[   68.173581@0] RT_CustomerID: 0x00
[   68.176844@0] _ReadMACAddress MAC Address from EFUSE = ac:a2:13:0b:05:79
[   68.183562@0] EEPROMRegulatory = 0x0
[   68.187092@0] _ReadBoardType(0)
[   68.190241@0] BT Coexistance = disable
[   68.193940@0] mlmepriv.ChannelPlan = 0x08
[   68.197958@0] _ReadPSSetting...bHWPwrPindetect(0)-bHWPowerdown(0) ,bSupportRemoteWakeup(0)
[   68.206209@0] ### PS params=>  power_mgnt(1),usbss_enable(0) ###
[   68.212215@0] ### AntDivCfg(0)
[   68.215203@0] readAdapterInfo_8192CU(): REPLACEMENT = 1
[   68.220441@0] <==== ReadAdapterInfo8192C in 200 ms
[   68.225202@0] rtw_wdev_alloc(padapter=e2dd8000)
[   68.230941@0] rtw_register_early_suspend
[   68.233631@0] rtw_macaddr_cfg MAC Address  = ac:a2:13:0b:05:79
[   68.239696@0] bDriverStopped:1, bSurpriseRemoved:0, bup:0, hw_init_completed:0
[   68.246717@0] register rtw_netdev_ops to netdev_ops
[   68.251524@0] register rtw_netdev_if2_ops to netdev_ops
[   68.256755@0] rtw_wdev_alloc(padapter=e402e000)
[   68.262046@0] Chip Version ID: VERSION_NORMAL_TSMC_CHIP_92C.
[   68.266881@0] RF_Type is 2!!
urned: 13
V/MediaProvider( 4093): insertInternal: content://media/internal/file, initValues=tit[   68.278200@0] _rtw_drv_register_netdev, MAC Address (if1) = ac:a2:13:0b:05:79
le=ui mime_type=null date_modified=87818 _size=0 format=12289 _d[   68.293114@0] _rtw_drv_register_netdev, MAC Address (if2) = ae:a2:13:0b:05:79
[   68.298916@0] usbcore: registered new interface driver rtl8192cu
ata=/system/media/audio/ui is_drm=false
D/MediaProvider( 4093): insert path/system/media/audio/ui
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio
D/MediaProvider( 4093): no find storage /system/media/audio/uiin getStorageId
V/MediaProvider( 4093): insertFile: values=bucket_id=-43825030 media_type=0 title=ui storage_id=65537 mime_type=null date_added=87946 date_modified=87818 parent=10 _size=4096 format=12289 _data=/system/media/audio/ui bucket_display_name=audio is_drm=false returned: 14
V/MediaProvider( 4093): insertInternal: content://media/internal/file, initValues=title=screensaver mime_type=null date_modified=87819 _size=0 format=12289 _data=/system/media/screensaver is_drm=false
D/MediaProvider( 4093): insert path/system/media/screensaver
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media
D/MediaProvider( 4093): no find storage /system/media/screensaverin getStorageId
V/MediaProvider( 4093): insertFile: values=bucket_id=1664432563 media_type=0 title=screensaver storage_id=65537 mime_type=null date_added=87946 date_modified=87819 parent=2 _size=4096 format=12289 _data=/system/media/screensaver bucket_display_name=media is_drm=false returned: 15
V/MediaProvider( 4093): insertInternal: content://media/internal/file, initValues=title=images mime_type=null date_modified=87820 _size=0 format=12289 _data=/system/media/screensaver/images is_drm=false
D/MediaProvider( 4093): insert path/system/media/screensaver/images
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/screensaver
D/MediaProvider( 4093): no find storage /system/media/screensaver/imagesin getStorageId
V/MediaProvider( 4093): insertFile: values=bucket_id=-1780305075 media_type=0 title=images storage_id=65537 mime_type=null date_added=87946 date_modified=87820 parent=15 _size=4096 format=12289 _data=/system/media/screensaver/images bucket_display_name=screensaver is_drm=false returned: 16
V/MediaProvider( 4093): insertInternal: content://media/internal/images/media, initValues=orientation=0 title=dlna height=720 mime_type=image/jpeg date_modified=1217592000 width=1280 _size=1286989 datetaken=1376587479000 _data=/system/media/screensaver/images/dlna.jpg is_drm=false
D/MediaProvider( 4093): insert path/system/media/screensaver/images/dlna.jpg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/screensaver/images
D/MediaProvider( 4093): no find storage /system/media/screensaver/images/dlna.jpgin getStorageId
V/MediaProvider( 4093): insertFile: values=bucket_id=-2003316870 orientation=0 media_type=1 storage_id=65537 date_modified=1217592000 width=1280 parent=16 format=14337 bucket_display_name=images is_drm=false title=dlna height=720 mime_type=image/jpeg _display_name=dlna.jpg date_added=87947 _size=1286989 datetaken=1376587479000 _data=/system/media/screensaver/images/dlna.jpg returned: 17
V/MediaProvider( 4093): insertInternal: content://media/internal/images/media, initValues=orientation=0 title=miracast height=720 mime_type=image/jpeg date_modified=1217592000 width=1280 _size=1352849 datetaken=1376587690000 _data=/system/media/screensaver/images/miracast.jpg is_drm=false
D/MediaProvider( 4093): insert path/system/media/screensaver/images/miracast.jpg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/screensaver/images
D/MediaProvider( 4093): no find storage /system/media/screensaver/images/miracast.jpgin getStorageId
V/MediaProvider( 4093): insertFile: values=bucket_id=-2003316870 orientation=0 media_type=1 storage_id=65537 date_modified=1217592000 width=1280 parent=16 format=14337 bucket_display_name=images is_drm=false title=miracast height=720 mime_type=image/jpeg _display_name=miracast.jpg date_added=87947 _size=1352849 datetaken=1376587690000 _data=/system/media/screensaver/images/miracast.jpg returned: 18
V/MediaProvider( 4093): insertInternal: content://media/internal/images/media, initValues=orientation=0 title=phone_remote height=720 mime_type=image/jpeg date_modified=1217592000 width=1280 _size=1265224 datetaken=1376587580000 _data=/system/media/screensaver/images/phone_remote.jpg is_drm=false
D/MediaProvider( 4093): insert path/system/media/screensaver/images/phone_remote.jpg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/screensaver/images
D/MediaProvider( 4093): no find storage /system/media/screensaver/images/phone_remote.jpgin getStorageId
V/MediaProvider( 4093): insertFile: values=bucket_id=-2003316870 orientation=0 media_type=1 storage_id=65537 date_modified=1217592000 width=1280 parent=16 format=14337 bucket_display_name=images is_drm=false title=phone_remote height=720 mime_type=image/jpeg _display_name=phone_remote.jpg date_added=87947 _size=1265224 datetaken=1376587580000 _data=/system/media/screensaver/images/phone_remote.jpg returned: 19
V/MediaProvider( 4093): insertInternal: content://media/internal/file, initValues=title=bootanimation mime_type=application/zip date_modified=1217592000 _size=8028630 format=12288 _data=/system/media/bootanimation.zip is_drm=false
D/MediaProvider( 4093): insert path/system/media/bootanimation.zip
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media
D/MediaProvider( 4093): no find storage /system/media/bootanimation.zipin getStorageId
V/MediaProvider( 4093): insertFile: values=bucket_id=1664432563 media_type=0 title=bootanimation storage_id=65537 mime_type=application/zip date_added=87947 date_modified=1217592000 parent=2 _size=8028630 format=12288 _data=/system/media/bootanimation.zip bucket_display_name=media is_drm=false returned: 20
D/dalvikvm( 4093): GC_CONCURRENT freed 384K, 9% free 4561K/4992K, paused 3ms+1ms, total 21ms
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=alarms track=0 is_ringtone=false is_alarm=true is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Piezo Alarm duration=2112 is_notification=false mime_type=application/ogg _size=16130 artist=<unknown> _data=/system/media/audio/alarms/Alarm_Beep_01.ogg
D/MediaProvider( 4093): insert path/system/media/audio/alarms/Alarm_Beep_01.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/alarms
D/MediaProvider( 4093): no find storage /system/media/audio/alarms/Alarm_Beep_01.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=true is_ringtone=false is_podcast=false bucket_display_name=alarms composer=null title=Piezo Alarm mime_type=application/ogg date_added=87947 _display_name=Alarm_Beep_01.ogg _size=16130 _data=/system/media/audio/alarms/Alarm_Beep_01.ogg bucket_id=-183121033 date_modified=1217592000 track=0 parent=11 format=47362 artist_id=1 is_music=false is_drm=false album_id=1 duration=2112 is_notification=false title_key=        °•       ?       ?       ?       -               ?       °Ï       ?       3       ?        returned: 21
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=alarms track=0 is_ringtone=false is_alarm=true is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=BeeBeep Alarm duration=251 is_notification=false mime_type=application/ogg _size=5898 artist=<unknown> _data=/system/media/audio/alarms/Alarm_Beep_02.ogg
D/MediaProvider( 4093): insert path/system/media/audio/alarms/Alarm_Beep_02.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/alarms
D/MediaProvider( 4093): no find storage /system/media/audio/alarms/Alarm_Beep_02.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=true is_ringtone=false is_podcast=false bucket_display_name=alarms composer=null title=BeeBeep Alarm mime_type=application/ogg date_added=87947 _display_name=Alarm_Beep_02.ogg _size=5898 _data=/system/media/audio/alarms/Alarm_Beep_02.ogg bucket_id=-183121033 date_modified=1217592000 track=0 parent=11 format=47362 artist_id=1 is_music=false is_drm=false album_id=1 duration=251 is_notification=false title_key=        ?       ?       ?       ?       ?       ?       °•               ?       °Ï       ?       3       ?        returned: 22
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=alarms track=0 is_ringtone=false is_alarm=true is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Beep-Beep-Beep Alarm duration=2372 is_notification=false mime_type=application/ogg _size=21153 artist=<unknown> _data=/system/media/audio/alarms/Alarm_Beep_03.ogg
D/MediaProvider( 4093): insert path/system/media/audio/alarms/Alarm_Beep_03.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/alarms
D/MediaProvider( 4093): no find storage /system/media/audio/alarms/Alarm_Beep_03.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=true is_ringtone=false is_podcast=false bucket_display_name=alarms composer=null title=Beep-Beep-Beep Alarm mime_type=application/ogg date_added=87947 _display_name=Alarm_Beep_03.ogg _size=21153 _data=/system/media/audio/alarms/Alarm_Beep_03.ogg bucket_id=-183121033 date_modified=1217592000 track=0 parent=11 format=47362 artist_id=1 is_music=false is_drm=false album_id=1 duration=2372 is_notification=false title_key=       ?       ?       ?       °•               ?       ?       ?       °•               ?       ?       ?    returned: 23
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=alarms track=0 is_ringtone=false is_alarm=true is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Buzzer Alarm duration=1103 is_notification=false mime_type=application/ogg _size=11368 artist=<unknown> _data=/system/media/audio/alarms/Alarm_Buzzer.ogg
D/MediaProvider( 4093): insert path/system/media/audio/alarms/Alarm_Buzzer.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/alarms
D/MediaProvider( 4093): no find storage /system/media/audio/alarms/Alarm_Buzzer.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=true is_ringtone=false is_podcast=false bucket_display_name=alarms composer=null title=Buzzer Alarm mime_type=application/ogg date_added=87947 _display_name=Alarm_Buzzer.ogg _size=11368 _data=/system/media/audio/alarms/Alarm_Buzzer.ogg bucket_id=-183121033 date_modified=1217592000 track=0 parent=11 format=47362 artist_id=1 is_music=false is_drm=false album_id=1 duration=1103 is_notification=false title_key= ?       1       ?       ?       ?       3               ?       °Ï       ?       3       ?        returned: 24
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=alarms track=0 is_ringtone=false is_alarm=true is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Rooster Alarm duration=1673 is_notification=false mime_type=application/ogg _size=11160 artist=<unknown> _data=/system/media/audio/alarms/Alarm_Rooster_02.ogg
D/MediaProvider( 4093): insert path/system/media/audio/alarms/Alarm_Rooster_02.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/alarms
D/MediaProvider( 4093): no find storage /system/media/audio/alarms/Alarm_Rooster_02.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=true is_ringtone=false is_podcast=false bucket_display_name=alarms composer=null title=Rooster Alarm mime_type=application/ogg date_added=87947 _display_name=Alarm_Rooster_02.ogg _size=11160 _data=/system/media/audio/alarms/Alarm_Rooster_02.ogg bucket_id=-183121033 date_modified=1217592000 track=0 parent=11 format=47362 artist_id=1 is_music=false is_drm=false album_id=1 duration=1673 is_notification=false title_key=        3       -       -       ¶Ã       °§       ?       3               ?       °Ï       ?       3       ?    returned: 25
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=alarms track=0 is_ringtone=false is_alarm=true is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Barium duration=1951 is_notification=false mime_type=application/ogg _size=51499 artist=<unknown> _data=/system/media/audio/alarms/Barium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/alarms/Barium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/alarms
D/MediaProvider( 4093): no find storage /system/media/audio/alarms/Barium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=true is_ringtone=false is_podcast=false bucket_display_name=alarms composer=null title=Barium mime_type=application/ogg date_added=87947 _display_name=Barium.ogg _size=51499 _data=/system/media/audio/alarms/Barium.ogg bucket_id=-183121033 date_modified=1217592000 track=0 parent=11 format=47362 artist_id=1 is_music=false is_drm=false album_id=1 duration=1951 is_notification=false title_key=   ?       ?       3       ?       1       ?        returned: 26
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=alarms track=0 is_ringtone=false is_alarm=true is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Carbon duration=3200 is_notification=false mime_type=application/ogg _size=43364 artist=<unknown> _data=/system/media/audio/alarms/Carbon.ogg
D/MediaProvider( 4093): insert path/system/media/audio/alarms/Carbon.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/alarms
D/MediaProvider( 4093): no find storage /system/media/audio/alarms/Carbon.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=true is_ringtone=false is_podcast=false bucket_display_name=alarms composer=null title=Carbon mime_type=application/ogg date_added=87947 _display_name=Carbon.ogg _size=43364 _data=/system/media/audio/alarms/Carbon.ogg bucket_id=-183121033 date_modified=1217592000 track=0 parent=11 format=47362 artist_id=1 is_music=false is_drm=false album_id=1 duration=3200 is_notification=false title_key=   ?       ?       3       ?       -       ?        returned: 27
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=alarms track=0 is_ringtone=false is_alarm=true is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Fermium duration=7000 is_notification=false mime_type=application/ogg _size=117227 artist=<unknown> _data=/system/media/audio/alarms/Fermium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/alarms/Fermium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/alarms
D/MediaProvider( 4093): no find storage /system/media/audio/alarms/Fermium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=true is_ringtone=false is_podcast=false bucket_display_name=alarms composer=null title=Fermium mime_type=application/ogg date_added=87947 _display_name=Fermium.ogg _size=117227 _data=/system/media/audio/alarms/Fermium.ogg bucket_id=-183121033 date_modified=1217592000 track=0 parent=11 format=47362 artist_id=1 is_music=false is_drm=false album_id=1 duration=7000 is_notification=false title_key=       ?       ?       3       ?       ?       1       ?        returned: 28
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=alarms track=0 is_ringtone=false is_alarm=true is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Hassium duration=8833 is_notification=false mime_type=application/ogg _size=147068 artist=<unknown> _data=/system/media/audio/alarms/Hassium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/alarms/Hassium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/alarms
D/MediaProvider( 4093): no find storage /system/media/audio/alarms/Hassium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=true is_ringtone=false is_podcast=false bucket_display_name=alarms composer=null title=Hassium mime_type=application/ogg date_added=87947 _display_name=Hassium.ogg _size=147068 _data=/system/media/audio/alarms/Hassium.ogg bucket_id=-183121033 date_modified=1217592000 track=0 parent=11 format=47362 artist_id=1 is_music=false is_drm=false album_id=1 duration=8833 is_notification=false title_key=       ?       ?       ¶Ã       ¶Ã       ?       1       ?        returned: 29
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=alarms track=0 is_ringtone=false is_alarm=true is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Neptunium duration=5950 is_notification=false mime_type=application/ogg _size=97614 artist=<unknown> _data=/system/media/audio/alarms/Neptunium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/alarms/Neptunium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/alarms
D/MediaProvider( 4093): no find storage /system/media/audio/alarms/Neptunium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=true is_ringtone=false is_podcast=false bucket_display_name=alarms composer=null title=Neptunium mime_type=application/ogg date_added=87947 _display_name=Neptunium.ogg _size=97614 _data=/system/media/audio/alarms/Neptunium.ogg bucket_id=-183121033 date_modified=1217592000 track=0 parent=11 format=47362 artist_id=1 is_music=false is_drm=false album_id=1 duration=5950 is_notification=false title_key=  ?       ?       °•       °§       1       ?       ?       1       ?        returned: 30
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=alarms track=0 is_ringtone=false is_alarm=true is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Nobelium duration=20400 is_notification=false mime_type=application/ogg _size=176792 artist=<unknown> _data=/system/media/audio/alarms/Nobelium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/alarms/Nobelium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/alarms
D/MediaProvider( 4093): no find storage /system/media/audio/alarms/Nobelium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=true is_ringtone=false is_podcast=false bucket_display_name=alarms composer=null title=Nobelium mime_type=application/ogg date_added=87947 _display_name=Nobelium.ogg _size=176792 _data=/system/media/audio/alarms/Nobelium.ogg bucket_id=-183121033 date_modified=1217592000 track=0 parent=11 format=47362 artist_id=1 is_music=false is_drm=false album_id=1 duration=20400 is_notification=false title_key=   ?       -       ?       ?       °Ï       ?       1       ?        returned: 31
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=alarms track=0 is_ringtone=false is_alarm=true is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Plutonium duration=4963 is_notification=false mime_type=application/ogg _size=103252 artist=<unknown> _data=/system/media/audio/alarms/Plutonium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/alarms/Plutonium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/alarms
D/MediaProvider( 4093): no find storage /system/media/audio/alarms/Plutonium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=true is_ringtone=false is_podcast=false bucket_display_name=alarms composer=null title=Plutonium mime_type=application/ogg date_added=87947 _display_name=Plutonium.ogg _size=103252 _data=/system/media/audio/alarms/Plutonium.ogg bucket_id=-183121033 date_modified=1217592000 track=0 parent=11 format=47362 artist_id=1 is_music=false is_drm=false album_id=1 duration=4963 is_notification=false title_key= °•       °Ï       1       °§       -       ?       ?       1       ?        returned: 32
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=alarms track=0 is_ringtone=false is_alarm=true is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Scandium duration=3938 is_notification=false mime_type=application/ogg _size=47812 artist=<unknown> _data=/system/media/audio/alarms/Scandium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/alarms/Scandium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/alarms
D/MediaProvider( 4093): no find storage /system/media/audio/alarms/Scandium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=true is_ringtone=false is_podcast=false bucket_display_name=alarms composer=null title=Scandium mime_type=application/ogg date_added=87947 _display_name=Scandium.ogg _size=47812 _data=/system/media/audio/alarms/Scandium.ogg bucket_id=-183121033 date_modified=1217592000 track=0 parent=11 format=47362 artist_id=1 is_music=false is_drm=false album_id=1 duration=3938 is_notification=false title_key=     ¶Ã       ?       ?       ?       ?       ?       1       ?        returned: 33
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Adara duration=600 is_notification=true mime_type=application/ogg _size=10414 artist=<unknown> _data=/system/media/audio/notifications/Adara.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Adara.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notification[   70.340868@1] cfg80211_rtw_change_iface(p2p0) call netdev_if2_open
[   70.346835@1] +871x_drv - if2_open, bup=0
[   70.350896@1] +871x_drv - drv_open, bup=0
s
D/MediaProvider( 4093): no fi[   70.359134@0]  ===> FirmwareDownload91C() fw:Rtl819XFwImageArray_TSMC
[   70.364076@0] FirmwareDownload92C accquire FW from embedded image
[   70.370141@0] fw_ver=v88, fw_subver=2, sig=0x88c0
nd storage /system/media/audio/notifications/Adara.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Adara mime_type=applic[   70.399994@0] fw download ok!
[   70.402763@0] Set RF Chip ID to RF_6052 and RF type to 2.
ation/ogg date_added=87947 _display_name=Adara.ogg _size=10414 _data=/system/media/audio/notifications/Adara.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=600 is_notification=true title_key=       ?       ?       ?    returned: 34
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=2 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Aldebaran duration=604 is_notification=true mime_type=application/ogg _size=7593 artist=pdx _data=/system/media/audio/notifications/Aldebaran.ogg
D/dalvikvm( 4093): GC_CONCURRENT freed 399K, 9% free 4552K/4996K, paused 3ms+1ms, total 28ms
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Aldebaran.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Aldebaran.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Aldebaran mime_type=application/ogg date_added=87947 _display_name=Aldebaran.ogg _size=7593 _data=/system/media/audio/notifications/Aldebaran.ogg bucket_id=-1608219629 date_modified=1217592000 track=2 parent=12 format=47362 artist_id=2 is_music=false is_drm=false album_id=2 duration=604 is_notification=true title_key=     ?       °Ï       ?       ?       ?       ?       3       ?       ?        returned: 35
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=2 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Altair duration=735 is_notification=true mime_type=application/ogg _size=7021 artist=pdx _data=/system/media/audio/notifications/Altair.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Altair.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Altair.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Altair mime_type=application/ogg date_added=87947 _display_name=Altair.ogg _size=7021 _data=/system/media/audio/notifications/Altair.ogg bucket_id=-1608219629 date_modified=1217592000 track=2 parent=12 format=47362 artist_id=2 is_music=false is_drm=false album_id=2 duration=735 is_notification=true title_key=      ?       °Ï       °§       ?       ?       3        returned: 36
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=2 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Antares duration=908 is_notification=true mime_type=application/ogg _size=10375 artist=pdx _data=/system/media/audio/notifications/Antares.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Antares.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Antares.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Antares mime_type=application/ogg date_added=87947 _display_name=Antares.ogg _size=10375 _data=/system/media/audio/notifications/Antares.ogg bucket_id=-1608219629 date_modified=1217592000 track=2 parent=12 format=47362 artist_id=2 is_music=false is_drm=false album_id=2 duration=908 is_notification=true title_key=  ?       ?       °§       ?       3       ?       ¶Ã        returned: 37
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Antimony duration=1689 is_notification=true mime_type=appl[   70.814467@0] IQK:Start!!!
ication/ogg _size=27849 artist=<unknown> _data=/system/media/audio/notifications/Antimony.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Antimony.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093)[   70.837849@0] Path A IQK Success!!
: Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find [   70.849598@0] Path B IQK Success!!
storage /system/media/audio/notifications/Antimony.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=6553[   70.868727@0] Path A IQK Success!!
7 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=n[   70.878384@0] Path B IQK Success!!
ull title=Antimony mime_type=application/ogg date_added=87947 _display_name=Antimony.ogg _size=2[   70.891091@0] IQK: final_candidate is 0
[   70.893753@0] IQK: RegE94=101 RegE9C=3f8 RegEA4=fe RegEAC=3fa RegEB4=103 RegEBC=c RegEC4=f8 RegECC=3
[   70.893773@0]  Path A IQ Calibration Success !
7849 _data=/system/media/audio/n[   70.911487@0] Path B IQ Calibration Success !
otifications/Antimony.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1689 is_notification=true title_key=     ?       ?       °§       ?       ?       -       ?       ®¢        returned: 38
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Arcturus duration=1000 is_notification=true mime_type=application/ogg _size=7933 artist=Unknown _data=/system/media/audio/notifications/Arcturus.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Arcturus.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Arcturus.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Arcturus mime_type=application/ogg date_added=87947 _display_name=Arcturus.ogg _size=7933 _data=/system/media/audio/notifications/Arcturus.ogg bucket_id=-1608219629 date_modifi[   71.035782@0] pdmpriv->TxPowerTrackControl = 1
ed=1217592000 track=0 parent=12 format=47362 artist_id=3 is_music=false is_drm=false album_id=2 d[   71.046908@0] rtl8192cu_hal_init in 690ms
uration=1000 is_notification=true title_key=    ?       3       ?       °§       1       3       1       ¶Ã        returned: 39
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false c[   71.079166@0] MAC Address = ac:a2:13:0b:05:79
[   71.083614@0] rtw_cfg80211_init_wiphy:rf_type=2
[   71.087102@0] +871x_drv - if2_open, bup=0
[   71.091188@0]  rtw_hal_inirp_init HalFunc.inirp_init is NULL!!!
[   71.097338@0] rtw_cfg80211_init_wiphy:rf_type=2
[   71.101550@0] -871x_drv - if2_open, bup=1
[   71.105539@0] -871x_drv - drv_open, bup=1
[   71.109575@0] -871x_drv - if2_open, bup=1
[   71.113538@0] cfg80211_rtw_change_iface(p2p0) old_iftype=6, new_iftype=2
[   71.121536@1] hw_var_set_opmode()-4150 mode = 2
[   71.121967@0] +871x_drv - if2_open, bup=1
[   71.121989@0] -871x_drv - if2_open, bup=1
[   71.122659@0] cfg80211_rtw_set_power_mgmt(p2p0) enabled:1, timeout:-1
[   71.123710@0] ADDRCONF(NETDEV_UP): p2p0: link is not ready
ompilation=0 is_drm=false composer=null title=Argon duration=1200 is_notification=true mime_type=application/ogg _size=15851 artist=<unknown> _data=/system/m[   71.162490@1] cfg80211_rtw_flush_pmksa(p2p0)

D/MediaProvider( 4093): insert path/system/media/audio/notifications/Argon.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Argon.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null [   71.200123@0] cfg80211_rtw_change_station(p2p0)
[   71.203798@0] cfg80211_rtw_change_station(p2p0)
[   71.208255@0] cfg80211_rtw_change_station(p2p0)
media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Argon mime_type=application/ogg date_added=87947 _display_name=Argon.ogg _size=15851 _data=/system/media/audio/notifications/Argon.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1200 is_notification=true title_key=       ?       3       ?    returned: 40
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Beat Box Android duration=2013 is_notification=true mime_type=application/ogg _size=34601 artist=<unknown> _data=/system/media/audio/notifications/Beat_Box_Android.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Beat_Box_Android.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Beat_Box_Android.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Beat Box Android mime_type=application/ogg date_added=87947 _display_name=Beat_Box_Android.ogg _size=34601 _data=/system/media/audio/notifications/Beat_Box_Android.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=2013 is_notification=true title_key=      ?       ?       ?       °§               ?       -       ?               ?       ?    returned: 41
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Bellatrix duration=1200 is_notification=true mime_type=application/ogg _size=15714 artist=<unknown> _data=/system/media/audio/notifications/Bellatrix.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Bellatrix.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Bellatrix.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Bellatrix mime_type=application/ogg date_added=87947 _display_name=Bellatrix.ogg _size=15714 _data=/system/media/audio/notifications/Bellatrix.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1200 is_notification=true title_key=   ?       ?       °Ï       °Ï       ?       °§       3       ?       ?        returned: 42
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Beryllium duration=1400 is_notification=true mime_type=application/ogg _size=19381 artist=<unknown> _data=/system/media/audio/notifications/Beryllium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Beryllium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Beryllium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Beryllium mime_type=application/ogg date_added=87947 _display_name=Beryllium.ogg _size=19381 _data=/system/media/audio/notifications/Beryllium.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1400 is_notification=true title_key=   ?       ?       3       ®¢       °Ï       °Ï       ?       1       ?        returned: 43
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=2 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Betelgeuse duration=1067 is_notification=true mime_type=application/ogg _size=16108 artist=pdx _data=/system/media/audio/notifications/Betelgeuse.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Betelgeuse.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Betelgeuse.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Betelgeuse mime_type=application/ogg date_added=87947 _display_name=Betelgeuse.ogg _size=16108 _data=/system/media/audio/notifications/Betelgeuse.ogg bucket_id=-1608219629 date_modified=1217592000 track=2 parent=12 format=47362 artist_id=2 is_music=false is_drm=false album_id=2 duration=1067 is_notification=true title_key=        ?       ?       °§       ?       °Ï       ?       ?       1       ¶Ã       ?        returned: 44
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Caffeinated Rattlesnake duration=1920 is_notification=true mime_type=application/ogg _size=19121 artist=Dr. MAD _data=/system/media/audio/notifications/CaffeineSnake.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/CaffeineSnake.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/CaffeineSnake.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Caffeinated Rattlesnake mime_type=application/ogg date_added=87947 _display_name=CaffeineSnake.ogg _size=19121 _data=/system/media/audio/notifications/CaffeineSnake.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=4 is_music=false is_drm=false album_id=2 duration=1920 is_notification=true title_key=     ?       ?       ?       ?       ?       ?       ?       ?       °§       ?       ?    returned: 45
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Canopus duration=705 is_notification=true mime_type=application/ogg _size=11948 artist=<unknown> _data=/system/media/audio/notifications/Canopus.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Canopus.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Canopus.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Canopus mime_type=application/ogg date_added=87947 _display_name=Canopus.ogg _size=11948 _data=/system/media/audio/notifications/Canopus.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=705 is_notification=true title_key=  ?       ?       ?       -       °•       [   71.956480@1] cfg80211_rtw_change_iface(wlan0) call netdev_open
[   71.961250@1] +871x_drv - drv_open, bup=1
[   71.965140@1] -871x_drv - drv_open, bup=1
[   71.969282@1] cfg80211_rtw_change_iface(wlan0) old_iftype=6, new_iftype=2
[   71.976481@1] +871x_drv - drv_open, bup=1
[   71.979973@1] -871x_drv - drv_open, bup=1
[   71.983905@1] hw_var_set_opmode()-4234 mode = 2
[   71.988950@1] cfg80211_rtw_set_power_mgmt(wlan0) enabled:1, timeout:-1
1       ¶Ã        returned: 46
V/MediaProv[   71.997701@1] ADDRCONF(NETDEV_UP): wlan0: link is not ready
ider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=2 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Capella duration=1379 is_notification=true mime_type=application/ogg _size=13162 artist=pdx _data=/system/media/[   72.038714@1] cfg80211_rtw_flush_pmksa(wlan0)
[   72.041462@1] cfg80211_rtw_change_station(wlan0)
[   72.045636@1] cfg80211_rtw_change_station(wlan0)
[   72.050328@1] cfg80211_rtw_change_station(wlan0)
audio/notifications/Capella.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Capella.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Capella.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Capella mime_type=application/ogg date_added=87947 _display_name=Capella.ogg _size=13162 _data=/system/media/audio/notifications/Capella.ogg bucket_id=-1608219629 date_modified=1217592000 track=2 parent=12 format=47362 artist_id=2 is_music=false is_drm=false album_id=2 duration=1379 is_notification=true title_key= ?       ?       °•       ?       °Ï       °Ï       ?        returned: 47
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=2 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Castor duration=1568 is_notification=true mime_type=application/ogg _size=14648 artist=pdx _data=/system/media/audio/notifications/Castor.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Castor.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Castor.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Castor mime_type=application/ogg date_added=87947 _display_name=Castor.ogg _size=14648 _data=/system/media/audio/notifications/Castor.ogg bucket_id=-1608219629 date_modified=1217592000 track=2 parent=12 format=47362 artist_id=2 is_music=false is_drm=false album_id=2 duration=1568 is_notification=true title_key=    ?       ?       ¶Ã       °§       -       3        returned: 48
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Ceti Alpha duration=2876 is_notification=true mime_type=application/ogg _size=26158 art[   72.279319@0] cfg80211_rtw_change_station(p2p0)
ist=<unknown> _data=/system/media/audio/notifications/CetiAlpha.ogg
D/dalvikvm( 4093): GC_CONCU[   72.290910@1] rtw_android_priv_cmd: Android private cmd "BTCOEXSCAN-STOP" on wlan0
[   72.300086@1] rtw_android_priv_cmd: Android private cmd "RXFILTER-STOP" on wlan0
RRENT freed 393K, 9% free 4551K/[   72.308850@0] rtw_android_priv_cmd: Android private cmd "RXFILTER-ADD 3" on wlan0
[   72.316596@0] rtw_android_priv_cmd: Android private cmd "RXFILTER-START" on wlan0
[   72.323872@0] rtw_android_priv_cmd: Android private cmd "RXFILTER-STOP" on wlan0
[   72.330802@0] rtw_android_priv_cmd: Android private cmd "RXFILTER-REMOVE 2" on wlan0
[   72.338428@0] rtw_android_priv_cmd: Android private cmd "RXFILTER-START" on wlan0
[   72.348007@0] rtw_android_priv_cmd: Android private cmd "SETBAND 0" on wlan0
[   72.353157@0] rtw_set_band(wlan0) band:0
[   72.357810@0] rtw_android_priv_cmd: Android private cmd "SCAN-ACTIVE" on wlan0
[   72.364509@0] cfg80211_rtw_scan(wlan0)
[   72.368344@1] rtw_cfg80211_set_probe_req_wpsp2pie(wlan0) listen channel - country:XX, class:81, ch:6
[   72.377136@1] rtw_android_priv_cmd: Android private cmd "SCAN-PASSIVE" on wlan0
[   72.377431@0] no packet in tx packet buffer (0)
4996K, paused 3ms+2ms, total 21ms
D/MediaProvider( 4093): insert path/system/media/audio/notifications/CetiAlpha.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/CetiAlpha.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Ceti Alpha mime_type=application/ogg date_added=87947 _display_name=CetiAlpha.ogg _size=26158 _data=/system/media/audio/notifications/CetiAlpha.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=2876 is_notification=true title_key=  ?       ?       °§       ?               ?       °Ï       °•       ?       ?        returned: 49
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Cobalt duration=1600 is_notification=true mime_type=application/ogg _size=18121 artist=<unknown> _data=/system/media/audio/notifications/Cobalt.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Cobalt.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Cobalt.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Cobalt mime_type=application/ogg date_added=87947 _display_name=Cobalt.ogg _size=18121 _data=/system/media/audio/notifications/Cobalt.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1600 is_notification=true title_key=    ?       -       ?       ?       °Ï       °§        returned: 50
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Cricket duration=892 is_notification=true mime_type=application/ogg _size=10936 artist=Dave Sparks _data=/system/media/audio/notifications/Cricket.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Cricket.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Cricket.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Cricket mime_type=application/ogg date_added=87947 _display_name=Cricket.ogg _size=10936 _data=/system/media/audio/notifications/Cricket.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=5 is_music=false is_drm=false album_id=2 duration=892 is_notification=true title_key=  ?       3       ?       ?       £§       ?       °§        returned: 51
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Dear Deer duration=1920 is_notification=true mime_type=application/ogg _size=18659 artist=Dr. MAD _data=/system/media/audio/notifications/DearDeer.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/DearDeer.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/DearDeer.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Dear Deer mime_type=application/ogg date_added=87947 _display_name=DearDeer.ogg _size=18659 _data=/system/media/audio/notifications/DearDeer.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=4 is_music=false is_drm=false album_id=2 duration=1920 is_notification=true title_key=     ?       ?       ?       3               ?       ?       ?       3        returned: 52
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=2 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Deneb duration=1666 is_notification=true mime_type=application/ogg _size=14416 artist=pdx _data=/system/media/audio/notifications/Deneb.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Deneb.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Deneb.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Deneb mime_type=application/ogg date_added=87947 _display_name=Deneb.ogg _size=14416 _data=/system/media/audio/notifications/Deneb.ogg bucket_id=-1608219629 date_modified=1217592000 track=2 parent=12 format=47362 artist_id=2 is_music=false is_drm=false album_id=2 duration=1666 is_notification=true title_key=       ?       ?       ?       ?       ?        returned: 53
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Doink duration=361 is_notification=true mime_type=application/ogg _size=8911 artist=Dave Sparks _data=/system/media/audio/notifications/Doink.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Doink.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Doink.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Doink mime_type=application/ogg date_added=87947 _display_name=Doink.ogg _size=8911 _data=/system/media/audio/notifications/Doink.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=5 is_music=false is_drm=false album_id=2 duration=361 is_notification=true title_key= ?       -       ?       ?       £§        returned: 54
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false[   73.055814@1] cfg80211_rtw_change_station(wlan0)
 compilation=0 is_drm=false composer=null title=Don't Panic duration=1920 is_notification=true mime_type=application/ogg _size=16985 artist=Dr. MAD _data=/system/media/audio/notifications/DontPanic.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/DontPanic.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/DontPanic.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Don't Panic mime_type=application/ogg date_added=87947 _display_name=DontPanic.ogg _size=16985 _data=/system/media/audio/notifications/DontPanic.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=4 is_music=false is_drm=false album_id=2 duration=1920 is_notification=true title_key= ?       -       ?       °§               °•       ?       ?       ?       ?        returned: 55
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Drip duration=1240 is_notification=true mime_type=application/ogg _size=13244 artist=Dave Sparks _data=/system/media/audio/notifications/Drip.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Drip.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Drip.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Drip mime_type=application/ogg date_added=87947 _display_name=Drip.ogg _size=13244 _data=/system/media/audio/notifications/Drip.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=5 is_music=false is_drm=false album_id=2 duration=1240 is_notification=true title_key=  ?       3       ?       °•        returned: 56
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=2 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Electra duration=2091 is_notification=true mime_type=application/ogg _size=15199 artist=pdx _data=/system/media/audio/notifications/Electra.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Electra.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Electra.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Electra mime_type=application/ogg date_added=87947 _display_name=Electra.ogg _size=15199 _data=/system/media/audio/notifications/Electra.ogg bucket_id=-1608219629 date_modified=1217592000 track=2 parent=12 format=47362 artist_id=2 is_music=false is_drm=false album_id=2 duration=2091 is_notification=true title_key= ?       °Ï       ?       ?       °§       3       ?        returned: 57
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Missed It duration=705 is_notification=true mime_type=application/ogg _size=11950 artist=<unknown> _data=/system/media/audio/notifications/F1_MissedCall.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/F1_MissedCall.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/F1_MissedCall.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Missed It mime_type=application/ogg date_added=87947 _display_name=F1_MissedCall.ogg _size=11950 _data=/system/media/audio/notifications/F1_MissedCall.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=705 is_notification=true title_key=    ?       ?       ¶Ã       ¶Ã       ?       ?               ?       °§        returned: 58
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Captain's Log duration=1306 is_notification=true mime_type=application/ogg _size=20983 artist=<unknown> _data=/system/media/audio/notifications/F1_New_MMS.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/F1_New_MMS.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/F1_New_MMS.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Captain's Log mime_type=application/ogg date_added=87947 _display_name=F1_New_MMS.ogg _size=20983 _data=/system/media/audio/notifications/F1_New_MMS.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1306 is_notification=true title_key=     ?       ?       °•       °§       ?       ?       ?       ¶Ã               °Ï       -       ?        returned: 59
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Look At Me duration=653 is_notification=true mime_type=application/ogg _size=11941 artist=<unknown> _data=/system/media/audio/notifications/F1_New_SMS.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/F1_New_SMS.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/F1_New_SMS.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Look At Me mime_type=application/ogg date_added=87947 _display_name=F1_New_SMS.ogg _size=11941 _data=/system/media/audio/notifications/F1_New_SMS.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=653 is_notification=true title_key= °Ï       -       -       £§               ?       °§               ?       ?        returned: 60
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Fluorine duration=1600 is_notification=true mime_type=application/ogg _size=18674 artist=<unknown> _data=/system/media/audio/notifications/Fluorine.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Fluorine.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Fluor[   73.781793@0] survey done event(3) band:0 for wlan0
ine.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Fluorine mime_type=application/ogg date_added=87947 _display_name=Fluorine.ogg _size=18674 _data=/system/media/audio/notifications/Fluorine.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1600 is_notification=true title_key=      ?       °Ï       1       -       3       ?       ?       ?        returned: 61
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=3 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Fomalhaut duration=2300 is_notification=true mime_type=application/ogg _size=22003 artist=pdx _data=/system/media/audio/notifications/Fomalhaut.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Fomalhaut.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Fomalhaut.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Fomalhaut mime_type=application/ogg date_added=87947 _display_name=Fomalhaut.ogg _size=22003 _data=/system/media/audio/notifications/Fomalhaut.ogg bucket_id=-1608219629 date_modified=1217592000 track=3 parent=12 format=47362 artist_id=2 is_music=false is_drm=false album_id=2 duration=2300 is_notification=true title_key=   ?       -       ?       ?       °Ï       ?       ?       1       °§        returned: 62
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Gallium duration=800 is_notification=true mime_type=application/ogg _size=12214 artist=<unknown> _data=/system/media/audio/notifications/Gallium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Gallium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Gallium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Gallium mime_type=application/ogg date_added=87947 _display_name=Gallium.ogg _size=12214 _data=/system/media/audio/notifications/Gallium.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=800 is_notification=true title_key=  ?       ?       °Ï       °Ï       ?       1       ?        returned: 63
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Heaven duration=2013 is_notification=true mime_type=application/ogg _size=38517 artist=<unknown> _data=/system/media/audio/notifications/Heaven.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Heaven.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Heaven.oggin getStorageId
D/dalvikvm( 4093): GC_CONCURRENT freed 416K, 10% free 4552K/5012K, paused 3ms+1ms, total 21ms
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Heaven mime_type=application/ogg date_added=87947 _display_name=Heaven.ogg _size=38517 _data=/system/media/audio/notifications/Heaven.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=2013 is_notification=true title_key=    ?       ?       ?       ?       ?       ?        returned: 64
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Helium duration=300 is_notification=true mime_type=application/ogg _size=7445 artist=<unknown> _data=/system/media/audio/notifications/Helium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Helium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Helium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Helium mime_type=application/ogg date_added=87947 _display_name=Helium.ogg _size=7445 _data=/system/media/audio/notifications/Helium.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=300 is_notification=true title_key=      ?       ?       °Ï       ?       1       ?        returned: 65
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Highwire duration=1920 is_notification=true mime_type=application/ogg _size=18442 artist=Dr. MAD _data=/system/media/audio/notifications/Highwire.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Highwire.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Highwire.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Highwire mime_type=application/ogg date_added=87947 _display_name=Highwire.ogg _size=18442 _data=/system/media/audio/notifications/Highwire.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=4 is_music=false is_drm=false album_id=2 duration=1920 is_notification=true title_key=      ?       ?       ?       ?       ?       ?       3       ?        returned: 66
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Hojus duration=1200 is_notification=true mime_type=application/ogg _size=13986 artist=<unknown> _data=/system/media/audio/notifications/Hojus.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Hojus.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Hojus.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Hojus mime_type=application/ogg date_added=87947 _display_name=Hojus.ogg _size=13986 _data=/system/media/audio/notifications/Hojus.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1200 is_notification=true title_key=       ?       -       °Í       1       ¶Ã        returned: 67
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Iridium duration=3300 is_notification=true mime_type=application/ogg _size=58227 artist=<unknown> _data=/system/media/audio/notifications/Iridium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Iridium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Iridium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Iridium mime_type=application/ogg date_added=87947 _display_name=Iridium.ogg _size=58227 _data=/system/media/audio/notifications/Iridium.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=3300 is_notification=true title_key= ?       3       ?       ?       ?       1       ?        returned: 68
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Krypton duration=667 is_notification=true mime_type=application/ogg _size=11566 artist=<unknown> _data=/system/media/audio/notifications/Krypton.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Krypton.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Krypton.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Krypton mime_type=application/ogg date_added=87947 _display_name=Krypton.ogg _size=11566 _data=/system/media/audio/notifications/Krypton.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=667 is_notification=true title_key=  £§       3       ®¢       °•       °§       -       ?        returned: 69
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Kzurb Sonar duration=1920 is_notification=true mime_type=application/ogg _size=15619 artist=Dr. MAD _data=/system/media/audio/notifications/KzurbSonar.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/KzurbSonar.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/KzurbSonar.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Kzurb Sonar mime_type=application/ogg date_added=87947 _display_name=KzurbSonar.ogg _size=15619 _data=/system/media/audio/notifications/KzurbSonar.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=4 is_music=false is_drm=false album_id=2 duration=1920 is_notification=true title_key=       £§       ?       1       3       ?               ¶Ã       -       ?       ?       3        returned: 70
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Lalande duration=1600 is_notification=true mime_type=application/ogg _size=18138 artist=<unknown> _data=/system/media/audio/notifications/Lalande.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Lalande.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Lalande.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Lalande mime_type=application/ogg date_added=87947 _display_name=Lalande.ogg _size=18138 _data=/system/media/audio/notifications/Lalande.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1600 is_notification=true title_key= °Ï       ?       °Ï       ?       ?       ?       ?        returned: 71
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Merope duration=1099 is_notification=true mime_type=application/ogg _size=18854 artist=<unknown> _data=/system/media/audio/notifications/Merope.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Merope.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Merope.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Merope mime_type=application/ogg date_added=87947 _display_name=Merope.ogg _size=18854 _data=/system/media/audio/notifications/Merope.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1099 is_notification=true title_key=    ?       ?       3       -       °•       ?        returned: 72
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Mira duration=1200 is_notification=true mime_type=application/ogg _size=17711 artist=<unknown> _data=/system/media/audio/notifications/Mira.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Mira.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Mira.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Mira mime_type=application/ogg date_added=87947 _display_name=Mira.ogg _size=17711 _data=/system/media/audio/notifications/Mira.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1200 is_notification=true title_key=  ?       ?       3       ?        returned: 73
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=On The Hunt duration=1920 is_notification=true mime_type=application/ogg _size=20052 artist=Dr. MAD _data=/system/media/audio/notifications/OnTheHunt.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/OnTheHunt.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/OnTheHunt.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=On The Hunt mime_type=application/ogg date_added=87947 _display_name=OnTheHunt.ogg _size=20052 _data=/system/media/audio/notifications/OnTheHunt.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=4 is_music=false is_drm=false album_id=2 duration=1920 is_notification=true title_key= -       ?               °§       ?       ?               ?       1       ?       °§        returned: 74
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Palladium duration=1594 is_notification=true mime_type=application/ogg _size=14645 artist=<unknown> _data=/system/media/audio/notifications/Palladium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Palladium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Palladium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Palladium mime_type=application/ogg date_added=87947 _display_name=Palladium.ogg _size=14645 _data=/system/media/audio/notifications/Palladium.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1594 is_notification=true title_key=   °•       ?       °Ï       °Ï       ?       ?       ?       1       ?        returned: 75
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Plastic Pipe duration=395 is_notification=true mime_type=application/ogg _size=6652 artist=<unknown> _data=/system/media/audio/notifications/Plastic_Pipe.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Plastic_Pipe.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Plastic_Pipe.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Plastic Pipe mime_type=application/ogg date_added=87947 _display_name=Plastic_Pipe.ogg _size=6652 _data=/system/media/audio/notifications/Plastic_Pipe.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=395 is_notification=true title_key=    °•       °Ï       ?       ¶Ã       °§       ?       ?               °•       ?       °•       ?        returned: 76
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=2 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Polaris duration=2358 is_notification=true mime_type=application/ogg _size=20567 artist=pdx _data=/system/media/audio/notifications/Polaris.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Polaris.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Polaris.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Polaris mime_type=application/ogg date_added=87947 _display_name=Polaris.ogg _size=20567 _data=/system/media/audio/notifications/Polaris.ogg bucket_id=-1608219629 date_modified=1217592000 track=2 parent=12 format=47362 artist_id=2 is_music=false is_drm=false album_id=2 duration=2358 is_notification=true title_key= °•       -       °Ï       ?       3       ?       ¶Ã        returned: 77
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=2 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Pollux duration=2589 is_notification=true mime_type=application/ogg _size=23397 artist=pdx _data=/system/media/audio/notifications/Pollux.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Pollux.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Pollux.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Pollux mime_type=application/ogg date_added=87947 _display_name=Pollux.ogg _size=23397 _data=/system/media/audio/notifications/Pollux.ogg bucket_id=-1608219629 date_modified=1217592000 track=2 parent=12 format=47362 artist_id=2 is_music=false is_drm=false album_id=2 duration=2589 is_notification=true title_key=    °•       -       °Ï       °Ï       1       ?        returned: 78
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=2 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Procyon duration=2625 is_notification=true mime_type=application/ogg _size=22380 artist=pdx _data=/system/media/audio/notifications/Procyon.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Procyon.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Procyon.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Procyon mime_type=application/ogg date_added=87947 _display_name=Procyon.ogg _size=22380 _data=/system/media/audio/notifications/Procyon.ogg bucket_id=-1608219629 date_modified=1217592000 track=2 parent=12 format=47362 artist_id=2 is_music=false is_drm=false album_id=2 duration=2625 is_notification=true title_key= °•       3       -       ?       ®¢       -       ?        returned: 79
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Proxima duration=1000 is_notification=true mime_type=application/ogg _size=13244 artist=<unknown> _data=/system/media/audio/notifications/Proxima.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Proxima.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Proxima.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Proxima mime_type=application/ogg date_added=87947 _display_name=Proxima.ogg _size=13244 _[   75.993889@0] ==>rtw_ps_processor .fw_state(8)
[   75.995773@0] ==>ips_enter cnts:1
[   75.999069@0] ===> rtw_ips_pwr_down...................
[   76.004722@0] ====> rtw_ips_dev_unload...
data=/system/media/audio/notifications/Proxima.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1000 is_notification=true title_key=    °•       3       -       ?       ?       ?       ?        returned: 80
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notific[   76.046380@0] usb_read_port_cancel
[   76.047882@0] usb_read_port_complete()-1284: RX Warning! bDriverStopped(0) OR bSurpriseRemoved(0) bReadPortCancel(1)
[   76.058452@0] usb_read_port_complete()-1284: RX Warning! bDriverStopped(0) OR bSurpriseRemoved(0) bReadPortCancel(1)
[   76.068986@0] usb_read_port_complete()-1284: RX Warning! bDriverStopped(0) OR bSurpriseRemoved(0) bReadPortCancel(1)
[   76.079474@0] usb_read_port_complete()-1284: RX Warning! bDriverStopped(0) OR bSurpriseRemoved(0) bReadPortCancel(1)
[   76.089872@0] usb_write_port_cancel 
[   76.093540@0] ==> rtl8192cu_hal_deinit 
[   76.097241@0] bkeepfwalive(0)
[   76.100162@0] card disble without HWSM...........
ations track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is[   76.114883@0] <=== rtw_ips_pwr_down..................... in 110ms
_drm=false composer=null title=Radon duration=1800 is_notification=true mime_type=application/ogg _size=25356 artist=<unknown> _data=/system/media/audio/notifications/Radon.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Radon.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Radon.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Radon mime_type=application/ogg date_added=87947 _display_name=Radon.ogg _size=25356 _data=/system/media/audio/notifications/Radon.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1800 is_notification=true title_key=       3       ?       ?       -       ?        returned: 81
D/dalvikvm( 4093): GC_CONCURRENT freed 468K, 11% free 4558K/5072K, paused 3ms+2ms, total 23ms
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Rubidium duration=1800 is_notification=true mime_type=application/ogg _size=21191 artist=<unknown> _data=/system/media/audio/notifications/Rubidium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Rubidium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Rubidium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Rubidium mime_type=application/ogg date_added=87947 _display_name=Rubidium.ogg _size=21191 _data=/system/media/audio/notifications/Rubidium.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1800 is_notification=true title_key=      3       1       ?       ?       ?       ?       1       ?        returned: 82
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Selenium duration=1000 is_notification=true mime_type=application/ogg _size=10893 artist=<unknown> _data=/system/media/audio/notifications/Selenium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Selenium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Selenium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Selenium mime_type=application/ogg date_added=87947 _display_name=Selenium.ogg _size=10893 _data=/system/media/audio/notifications/Selenium.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1000 is_notification=true title_key=      ¶Ã       ?       °Ï       ?       ?       ?       1       ?        returned: 83
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Shaula duration=1371 is_notification=true mime_type=application/ogg _size=24298 artist=<unknown> _data=/system/media/audio/notifications/Shaula.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Shaula.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Shaula.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Shaula mime_type=application/ogg date_added=87947 _display_name=Shaula.ogg _size=24298 _data=/system/media/audio/notifications/Shaula.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1371 is_notification=true title_key=    ¶Ã       ?       ?       1       °Ï       ?        returned: 84
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Sirrah duration=1306 is_notification=true mime_type=application/ogg _size=20976 artist=<unknown> _data=/system/media/audio/notifications/Sirrah.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Sirrah.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Sirrah.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Sirrah mime_type=application/ogg date_added=87947 _display_name=Sirrah.ogg _size=20976 _data=/system/media/audio/notifications/Sirrah.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1306 is_notification=true title_key=    ¶Ã       ?       3       3       ?       ?        returned: 85
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Space Seed duration=2876 is_notification=true mime_type=application/ogg _size=26180 artist=Dave Sparks _data=/system/media/audio/notifications/SpaceSeed.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/SpaceSeed.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/SpaceSeed.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Space Seed mime_type=application/ogg date_added=87947 _display_name=SpaceSeed.ogg _size=26180 _data=/system/media/audio/notifications/SpaceSeed.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=5 is_music=false is_drm=false album_id=2 duration=2876 is_notification=true title_key=  ¶Ã       °•       ?       ?       ?               ¶Ã       ?       ?       ?        returned: 86
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Spica duration=900 is_notification=true mime_type=application/ogg _size=9069 artist=<unknown> _data=/system/media/audio/notifications/Spica.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Spica.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Spica.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Spica mime_type=application/ogg date_added=87947 _display_name=Spica.ogg _size=9069 _data=/system/media/audio/notifications/Spica.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=900 is_notification=true title_key= ¶Ã       °•       ?       ?       ?        returned: 87
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Strontium duration=3000 is_notification=true mime_type=application/ogg _size=39203 artist=<unknown> _data=/system/media/audio/notifications/Strontium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Strontium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Strontium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Strontium mime_type=application/ogg date_added=87947 _display_name=Strontium.ogg _size=39203 _data=/system/media/audio/notifications/Strontium.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=3000 is_notification=true title_key=   ¶Ã       °§       3       -       ?       °§       ?       1       ?        returned: 88
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Ta Da duration=3293 is_notification=true mime_type=application/ogg _size=43522 artist=<unknown> _data=/system/media/audio/notifications/TaDa.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/TaDa.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/TaDa.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Ta Da mime_type=application/ogg date_added=87947 _display_name=TaDa.ogg _size=43522 _data=/system/media/audio/notifications/TaDa.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=3293 is_notification=true title_key= °§       ?               ?       ?        returned: 89
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Tejat duration=1200 is_notification=true mime_type=application/ogg _size=15866 artist=<unknown> _data=/system/media/audio/notifications/Tejat.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Tejat.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Tejat.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Tejat mime_type=application/ogg date_added=87947 _display_name=Tejat.ogg _size=15866 _data=/system/media/audio/notifications/Tejat.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1200 is_notification=true title_key=       °§       ?       °Í       ?       °§        returned: 90
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Thallium duration=1375 is_notification=true mime_type=application/ogg _size=18235 artist=<unknown> _data=/system/media/audio/notifications/Thallium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Thallium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Thallium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Thallium mime_type=application/ogg date_added=87947 _display_name=Thallium.ogg _size=18235 _data=/system/media/audio/notifications/Thallium.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1375 is_notification=true title_key=      °§       ?       ?       °Ï       °Ï       ?       1       ?        returned: 91
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Tinkerbell duration=1099 is_notification=true mime_type=application/ogg _size=18858 artist=<unknown> _data=/system/media/audio/notifications/Tinkerbell.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Tinkerbell.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Tinkerbell.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Tinkerbell mime_type=application/ogg date_added=87947 _display_name=Tinkerbell.ogg _size=18858 _data=/system/media/audio/notifications/Tinkerbell.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1099 is_notification=true title_key=        °§       ?       ?       £§       ?       3       ?       ?       °Ï       °Ï        returned: 92
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Upsilon duration=1600 is_notification=true mime_type=application/ogg _size=24040 artist=<unknown> _data=/system/media/audio/notifications/Upsilon.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Upsilon.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Upsilon.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Upsilon mime_type=application/ogg date_added=87947 _display_name=Upsilon.ogg _size=24040 _data=/system/media/audio/notifications/Upsilon.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1600 is_notification=true title_key= 1       °•       ¶Ã       ?       °Ï       -       ?        returned: 93
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Vega duration=4383 is_notification=true mime_type=application/ogg _size=29310 artist=Unknown _data=/system/media/audio/notifications/Vega.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Vega.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Vega.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Vega mime_type=application/ogg date_added=87947 _display_name=Vega.ogg _size=29310 _data=/system/media/audio/notifications/Vega.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=3 is_music=false is_drm=false album_id=2 duration=4383 is_notification=true title_key=  ?       ?       ?       ?        returned: 94
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Voila duration=1920 is_notification=true mime_type=application/ogg _size=16487 artist=Dr. MAD _data=/system/media/audio/notifications/Voila.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Voila.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Voila.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Voila mime_type=application/ogg date_added=87947 _display_name=Voila.ogg _size=16487 _data=/system/media/audio/notifications/Voila.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=4 is_music=false is_drm=false album_id=2 duration=1920 is_notification=true title_key=       ?       -       ?       °Ï       ?        returned: 95
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Xenon duration=2000 is_notification=true mime_type=application/ogg _size=27860 artist=<unknown> _data=/system/media/audio/notifications/Xenon.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Xenon.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Xenon.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Xenon mime_type=application/ogg date_added=87947 _display_name=Xenon.ogg _size=27860 _data=/system/media/audio/notifications/Xenon.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=2000 is_notification=true title_key=       ?       ?       ?       -       ?        returned: 96
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Zirconium duration=1200 is_notification=true mime_type=application/ogg _size=12697 artist=<unknown> _data=/system/media/audio/notifications/Zirconium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/Zirconium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/Zirconium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Zirconium mime_type=application/ogg date_added=87947 _display_name=Zirconium.ogg _size=12697 _data=/system/media/audio/notifications/Zirconium.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1200 is_notification=true title_key=   ?       ?       3       ?       -       ?       ?       1       ?        returned: 97
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Moonbeam duration=1874 is_notification=true mime_type=application/ogg _size=16243 artist=<unknown> _data=/system/media/audio/notifications/moonbeam.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/moonbeam.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/moonbeam.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Moonbeam mime_type=application/ogg date_added=87947 _display_name=moonbeam.ogg _size=16243 _data=/system/media/audio/notifications/moonbeam.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=1874 is_notification=true title_key=      ?       -       -       ?       ?       ?       ?       ?        returned: 98
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Pizzicato duration=2003 is_notification=true mime_type=application/ogg _size=18884 artist=<unknown> _data=/system/media/audio/notifications/pizzicato.ogg
D/dalvikvm( 4093): GC_CONCURRENT freed 472K, 11% free 4557K/5076K, paused 3ms+2ms, total 24ms
D/MediaProvider( 4093): insert path/system/media/audio/notifications/pizzicato.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/pizzicato.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Pizzicato mime_type=application/ogg date_added=87947 _display_name=pizzicato.ogg _size=18884 _data=/system/media/audio/notifications/pizzicato.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=2003 is_notification=true title_key=   °•       ?       ?       ?       ?       ?       ?       °§       -        returned: 99
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=3 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Regulus duration=3278 is_notification=true mime_type=application/ogg _size=27852 artist=pdx _data=/system/media/audio/notifications/regulus.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/regulus.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/regulus.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Regulus mime_type=application/ogg date_added=87947 _display_name=regulus.ogg _size=27852 _data=/system/media/audio/notifications/regulus.ogg bucket_id=-1608219629 date_modified=1217592000 track=3 parent=12 format=47362 artist_id=2 is_music=false is_drm=false album_id=2 duration=3278 is_notification=true title_key= 3       ?       ?       1       °Ï       1       ¶Ã        returned: 100
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=2 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Sirius duration=4300 is_notification=true mime_type=application/ogg _size=26612 artist=pdx _data=/system/media/audio/notifications/sirius.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/sirius.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/sirius.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Sirius mime_type=application/ogg date_added=87947 _display_name=sirius.ogg _size=26612 _data=/system/media/audio/notifications/sirius.ogg bucket_id=-1608219629 date_modified=1217592000 track=2 parent=12 format=47362 artist_id=2 is_music=false is_drm=false album_id=2 duration=4300 is_notification=true title_key=    ¶Ã       ?       3       ?       1       ¶Ã        returned: 101
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=notifications track=0 is_ringtone=false is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Tweeters duration=788 is_notification=true mime_type=application/ogg _size=10421 artist=<unknown> _data=/system/media/audio/notifications/tweeters.ogg
D/MediaProvider( 4093): insert path/system/media/audio/notifications/tweeters.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/notifications
D/MediaProvider( 4093): no find storage /system/media/audio/notifications/tweeters.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=notifications composer=null title=Tweeters mime_type=application/ogg date_added=87947 _display_name=tweeters.ogg _size=10421 _data=/system/media/audio/notifications/tweeters.ogg bucket_id=-1608219629 date_modified=1217592000 track=0 parent=12 format=47362 artist_id=1 is_music=false is_drm=false album_id=2 duration=788 is_notification=true title_key=       °§       ?       ?       ?       °§       ?       3       ¶Ã        returned: 102
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Andromeda duration=3000 is_notification=false mime_type=application/ogg _size=24973 artist=<unknown> _data=/system/media/audio/ringtones/Andromeda.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Andromeda.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Andromeda.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Andromeda mime_type=application/ogg date_added=87947 _display_name=Andromeda.ogg _size=24973 _data=/system/media/audio/ringtones/Andromeda.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=1 is_music=false is_drm=false album_id=3 duration=3000 is_notification=false title_key=     ?       ?       ?       3       -       ?       ?       ?       ?        returned: 103
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=2 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Aquila duration=3000 is_notification=false mime_type=application/ogg _size=21509 artist=pdx _data=/system/media/audio/ringtones/Aquila.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Aquila.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Aquila.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Aquila mime_type=application/ogg date_added=87947 _display_name=Aquila.ogg _size=21509 _data=/system/media/audio/ringtones/Aquila.ogg bucket_id=293500348 date_modified=1217592000 track=2 parent=13 format=47362 artist_id=2 is_music=false is_drm=false album_id=3 duration=3000 is_notification=false title_key=      ?       °¿       1       ?       °Ï       ?        returned: 104
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Argo Navis duration=13333 is_notification=false mime_type=application/ogg _size=89534 artist=Ali Spagnola _data=/system/media/audio/ringtones/ArgoNavis.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/ArgoNavis.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/ArgoNavis.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Argo Navis mime_type=application/ogg date_added=87947 _display_name=ArgoNavis.ogg _size=89534 _data=/system/media/audio/ringtones/ArgoNavis.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=6 is_music=false is_drm=false album_id=3 duration=13333 is_notification=false title_key=   ?       3       ?       -               ?       ?       ?       ?       ¶Ã        returned: 105
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=2 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Boˆtes duration=3000 is_notification=false mime_type=application/ogg _size=26310 artist=pdx _data=/system/media/audio/ringtones/BOOTES.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/BOOTES.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/BOOTES.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Boˆtes mime_type=application/ogg date_added=87947 _display_name=BOOTES.ogg _size=26310 _data=/system/media/audio/ringtones/BOOTES.ogg bucket_id=293500348 date_modified=1217592000 track=2 parent=13 format=47362 artist_id=2 is_music=false is_drm=false album_id=3 duration=3000 is_notification=false title_key=      ?       -       E
                                                        ?       ¶Ã        returned: 106
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Backroad duration=3609 is_notification=false mime_type=application/ogg _size=36049 artist=Dr. Mad _data=/system/media/audio/ringtones/Backroad.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Backroad.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Backroad.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Backroad mime_type=application/ogg date_added=87947 _display_name=Backroad.ogg _size=36049 _data=/system/media/audio/ringtones/Backroad.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=3609 is_notification=false title_key=        ?       ?       ?       £§       3       -       ?       ?        returned: 107
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Beat Plucker duration=4000 is_notification=false mime_type=application/ogg _size=28433 artist=Dr. MAD _data=/system/media/audio/ringtones/BeatPlucker.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/BeatPlucker.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/BeatPlucker.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Beat Plucker mime_type=application/ogg date_added=87947 _display_name=BeatPlucker.ogg _size=28433 _data=/system/media/audio/ringtones/BeatPlucker.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=      ?       ?       ?       °§               °•       °Ï       1       ?       £§       ?       3        returned: 108
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Bentley Dubs duration=4444 is_notification=false mime_type=application/ogg _size=30759 artist=Dr. MAD _data=/system/media/audio/ringtones/BentleyDubs.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/BentleyDubs.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/BentleyDubs.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Bentley Dubs mime_type=application/ogg date_added=87947 _display_name=BentleyDubs.ogg _size=30759 _data=/system/media/audio/ringtones/BentleyDubs.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4444 is_notification=false title_key=      ?       ?       ?       °§       °Ï       ?       ®¢               ?       1       ?       ¶Ã        returned: 109
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Big Easy duration=4800 is_notification=false mime_type=application/ogg _size=46673 artist=Dr. Mad _data=/system/media/audio/ringtones/Big_Easy.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Big_Easy.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Big_Easy.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Big Easy mime_type=application/ogg date_added=87947 _display_name=Big_Easy.ogg _size=46673 _data=/system/media/audio/ringtones/Big_Easy.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4800 is_notification=false title_key=        ?       ?       ?               ?       ?       ¶Ã       ®¢        returned: 110
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Bird Loop duration=5854 is_notification=false mime_type=application/ogg _size=41828 artist=Dr. MAD _data=/system/media/audio/ringtones/BirdLoop.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/BirdLoop.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/BirdLoop.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Bird Loop mime_type=application/ogg date_added=87947 _display_name=BirdLoop.ogg _size=41828 _data=/system/media/audio/ringtones/BirdLoop.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=5854 is_notification=false title_key=       ?       ?       3       ?               °Ï       -       -       °•        returned: 111
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Bollywood duration=4211 is_notification=false mime_type=application/ogg _size=39174 artist=Dr. Mad _data=/system/media/audio/ringtones/Bollywood.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Bollywood.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Bollywood.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Bollywood mime_type=application/ogg date_added=87947 _display_name=Bollywood.ogg _size=39174 _data=/system/media/audio/ringtones/Bollywood.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4211 is_notification=false title_key=     ?       -       °Ï       °Ï       ®¢       ?       -       -       ?        returned: 112
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Bus' a Move duration=4898 is_notification=false mime_type=application/ogg _size=48968 artist=Dr. Mad _data=/system/media/audio/ringtones/BussaMove.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/BussaMove.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/BussaMove.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Bus' a Move mime_type=application/ogg date_added=87947 _display_name=BussaMove.ogg _size=48968 _data=/system/media/audio/ringtones/BussaMove.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4898 is_notification=false title_key=   ?       1       ¶Ã               ?               ?       -       ?       ?        returned: 113
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=2 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Cassiopeia duration=4000 is_notification=false mime_type=application/ogg _size=31941 artist=pdx _data=/system/media/audio/ringtones/CASSIOPEIA.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/CASSIOPEIA.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/CASSIOPEIA.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Cassiopeia mime_type=application/ogg date_added=87947 _display_name=CASSIOPEIA.ogg _size=31941 _data=/system/media/audio/ringtones/CASSIOPEIA.ogg bucket_id=293500348 date_modified=1217592000 track=2 parent=13 format=47362 artist_id=2 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=  ?       ?       ¶Ã       ¶Ã       ?       -       °•       ?       ?       ?        returned: 114
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Cairo duration=4174 is_notification=false mime_type=application/ogg _size=37672 artist=Dr. Mad _data=/system/media/audio/ringtones/Cairo.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Cairo.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Cairo.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Cairo mime_type=application/ogg date_added=87947 _display_name=Cairo.ogg _size=37672 _data=/system/media/audio/ringtones/Cairo.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4174 is_notification=false title_key= ?       ?       ?       3       -        returned: 115
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Calypso Steel duration=4000 is_notification=false mime_type=application/ogg _size=38875 artist=Dr. Mad _data=/system/media/audio/ringtones/Calypso_Steel.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Calypso_Steel.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Calypso_Steel.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Calypso Steel mime_type=application/ogg date_added=87947 _display_name=Calypso_Steel.ogg _size=38875 _data=/system/media/audio/ringtones/Calypso_Steel.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key= ?       ?       °Ï       ®¢       °•       ¶Ã       -               ¶Ã       °§       ?       ?       °Ï        returned: 116
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Canis Major duration=3498 is_notification=false mime_type=application/ogg _size=31133 artist=<unknown> _data=/system/media/audio/ringtones/CanisMajor.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/CanisMajor.ogg
D/MediaProvider( 4093): happend modified time not equal
D/dalvikvm( 4093): GC_CONCURRENT freed 480K, 11% free 4560K/5084K, paused 2ms+3ms, total 23ms
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/CanisMajor.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Canis Major mime_type=application/ogg date_added=87947 _display_name=CanisMajor.ogg _size=31133 _data=/system/media/audio/ringtones/CanisMajor.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=1 is_music=false is_drm=false album_id=3 duration=3498 is_notification=false title_key= ?       ?       ?       ?       ¶Ã               ?       ?       °Í       -       3        returned: 117
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Caribbean Ice duration=4000 is_notification=false mime_type=application/ogg _size=30615 artist=Dr. MAD _data=/system/media/audio/ringtones/CaribbeanIce.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/CaribbeanIce.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/CaribbeanIce.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Caribbean Ice mime_type=application/ogg date_added=87947 _display_name=CaribbeanIce.ogg _size=30615 _data=/system/media/audio/ringtones/CaribbeanIce.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=   ?       ?       3       ?       ?       ?       ?       ?       ?               ?       ?       ?        returned: 118
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Carina duration=3001 is_notification=false mime_type=application/ogg _size=15462 artist=Dr. Mad _data=/system/media/audio/ringtones/Carina.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Carina.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Carina.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Carina mime_type=application/ogg date_added=87947 _display_name=Carina.ogg _size=15462 _data=/system/media/audio/ringtones/Carina.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=3001 is_notification=false title_key=      ?       ?       3       ?       ?       ?        returned: 119
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=2 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Centaurus duration=4000 is_notification=false mime_type=application/ogg _size=27681 artist=pdx _data=/system/media/audio/ringtones/Centaurus.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Centaurus.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Centaurus.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Centaurus mime_type=application/ogg date_added=87947 _display_name=Centaurus.ogg _size=27681 _data=/system/media/audio/ringtones/Centaurus.ogg bucket_id=293500348 date_modified=1217592000 track=2 parent=13 format=47362 artist_id=2 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=     ?       ?       ?       °§       ?       1       3       1       ¶Ã        returned: 120
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Champagne Edition duration=4364 is_notification=false mime_type=application/ogg _size=39921 artist=Dr. Mad _data=/system/media/audio/ringtones/Champagne_Edition.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Champagne_Edition.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Champagne_Edition.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Champagne Edition mime_type=application/ogg date_added=87947 _display_name=Champagne_Edition.ogg _size=39921 _data=/system/media/audio/ringtones/Champagne_Edition.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4364 is_notification=false title_key=     ?       ?       ?       ?       °•       ?       ?       ?       ?               ?       ?    returned: 121
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Club Cubano duration=4000 is_notification=false mime_type=application/ogg _size=37179 artist=Dr. Mad _data=/system/media/audio/ringtones/Club_Cubano.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Club_Cubano.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Club_Cubano.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Club Cubano mime_type=application/ogg date_added=87947 _display_name=Club_Cubano.ogg _size=37179 _data=/system/media/audio/ringtones/Club_Cubano.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=       ?       °Ï       1       ?               ?       1       ?       ?       ?       -        returned: 122
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Crayon Rock duration=3556 is_notification=false mime_type=application/ogg _size=37952 artist=Dr. Mad _data=/system/media/audio/ringtones/CrayonRock.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/CrayonRock.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/CrayonRock.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Crayon Rock mime_type=application/ogg date_added=87947 _display_name=CrayonRock.ogg _size=37952 _data=/system/media/audio/ringtones/CrayonRock.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=3556 is_notification=false title_key= ?       3       ?       ®¢       -       ?               3       -       ?       £§        returned: 123
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Crazy Dream duration=31448 is_notification=false mime_type=application/ogg _size=206809 artist=Dr. MAD _data=/system/media/audio/ringtones/CrazyDream.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/CrazyDream.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/CrazyDream.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Crazy Dream mime_type=application/ogg date_added=87947 _display_name=CrazyDream.ogg _size=206809 _data=/system/media/audio/ringtones/CrazyDream.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=31448 is_notification=false title_key=       ?       3       ?       ?       ®¢               ?       3       ?       ?       ?        returned: 124
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Curve Ball Blend duration=4000 is_notification=false mime_type=application/ogg _size=30925 artist=Dr. MAD _data=/system/media/audio/ringtones/CurveBall.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/CurveBall.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/CurveBall.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Curve Ball Blend mime_type=application/ogg date_added=87947 _display_name=CurveBall.ogg _size=30925 _data=/system/media/audio/ringtones/CurveBall.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=      ?       1       3       ?       ?               ?       ?       °Ï       °Ï               ?       °Ï       ?    returned: 125
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=2 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Cygnus duration=4000 is_notification=false mime_type=application/ogg _size=29844 artist=pdx _data=/system/media/audio/ringtones/Cygnus.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Cygnus.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Cygnus.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Cygnus mime_type=application/ogg date_added=87947 _display_name=Cygnus.ogg _size=29844 _data=/system/media/audio/ringtones/Cygnus.ogg bucket_id=293500348 date_modified=1217592000 track=2 parent=13 format=47362 artist_id=2 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=      ?       ®¢       ?       ?       1       ¶Ã        returned: 126
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Dancin' Fool duration=5760 is_notification=false mime_type=application/ogg _size=61434 artist=Dr. Mad _data=/system/media/audio/ringtones/DancinFool.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/DancinFool.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/DancinFool.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Dancin' Fool mime_type=application/ogg date_added=87947 _display_name=DancinFool.ogg _size=61434 _data=/system/media/audio/ringtones/DancinFool.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=5760 is_notification=false title_key=        ?       ?       ?       ?       ?       ?               ?       -       -       °Ï        returned: 127
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Ding duration=1501 is_notification=false mime_type=application/ogg _size=15476 artist=Dr. Mad _data=/system/media/audio/ringtones/Ding.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Ding.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Ding.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Ding mime_type=application/ogg date_added=87947 _display_name=Ding.ogg _size=15476 _data=/system/media/audio/ringtones/Ding.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=1501 is_notification=false title_key=    ?       ?       ?       ?        returned: 128
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Don' Mess Wiv It duration=5161 is_notification=false mime_type=application/ogg _size=50545 artist=Dr. Mad _data=/system/media/audio/ringtones/DonMessWivIt.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/DonMessWivIt.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/DonMessWivIt.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Don' Mess Wiv It mime_type=application/ogg date_added=87947 _display_name=DonMessWivIt.ogg _size=50545 _data=/system/media/audio/ringtones/DonMessWivIt.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=5161 is_notification=false title_key=        ?       -       ?               ?       ?       ¶Ã       ¶Ã               ?       ?       ?            returned: 129
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=2 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Draco duration=4000 is_notification=false mime_type=application/ogg _size=31819 artist=pdx _data=/system/media/audio/ringtones/Draco.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Draco.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Draco.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Draco mime_type=application/ogg date_added=87947 _display_name=Draco.ogg _size=31819 _data=/system/media/audio/ringtones/Draco.ogg bucket_id=293500348 date_modified=1217592000 track=2 parent=13 format=47362 artist_id=2 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key= ?       3       ?       ?       -        returned: 130
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Dream Theme duration=26667 is_notification=false mime_type=application/ogg _size=175423 artist=Dr. MAD _data=/system/media/audio/ringtones/DreamTheme.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/DreamTheme.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/DreamTheme.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Dream Theme mime_type=application/ogg date_added=87947 _display_name=DreamTheme.ogg _size=175423 _data=/system/media/audio/ringtones/DreamTheme.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=26667 is_notification=false title_key=       ?       3       ?       ?       ?               °§       ?       ?       ?       ?        returned: 131
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Eastern Sky duration=5333 is_notification=false mime_type=application/ogg _size=50578 artist=Dr. Mad _data=/system/media/audio/ringtones/Eastern_Sky.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Eastern_Sky.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Eastern_Sky.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Eastern Sky mime_type=application/ogg date_added=87947 _display_name=Eastern_Sky.ogg _size=50578 _data=/system/media/audio/ringtones/Eastern_Sky.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=5333 is_notification=false title_key=       ?       ?       ¶Ã       °§       ?       3       ?               ¶Ã       £§       ®¢        returned: 132
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Enter the Nexus duration=6621 is_notification=false mime_type=application/ogg _size=61699 artist=Dr. Mad _data=/system/media/audio/ringtones/Enter_the_Nexus.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Enter_the_Nexus.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Enter_the_Nexus.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Enter the Nexus mime_type=application/ogg date_added=87947 _display_name=Enter_the_Nexus.ogg _size=61699 _data=/system/media/audio/ringtones/Enter_the_Nexus.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=6621 is_notification=false title_key=   ?       ?       °§       ?       3               °§       ?       ?               ?       ?       ?    returned: 133
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Eridani duration=5000 is_notification=false mime_type=application/ogg _size=36585 artist=Dr. MAD _data=/system/media/audio/ringtones/Eridani.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Eridani.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Eridani.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Eridani mime_type=application/ogg date_added=87947 _display_name=Eridani.ogg _size=36585 _data=/system/media/audio/ringtones/Eridani.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=5000 is_notification=false title_key=   ?       3       ?       ?       ?       ?       ?        returned: 134
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Ether Shake duration=4000 is_notification=false mime_type=application/ogg _size=31563 artist=Dr. MAD _data=/system/media/audio/ringtones/EtherShake.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/EtherShake.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/EtherShake.oggin getStorageId
D/dalvikvm( 4093): GC_CONCURRENT freed 486K, 11% free 4554K/5088K, paused 3ms+2ms, total 24ms
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Ether Shake mime_type=application/ogg date_added=87947 _display_name=EtherShake.ogg _size=31563 _data=/system/media/audio/ringtones/EtherShake.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key= ?       °§       ?       ?       3               ¶Ã       ?       ?       £§       ?        returned: 135
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Free Flight duration=12632 is_notification=false mime_type=application/ogg _size=113055 artist=Dave Sparks _data=/system/media/audio/ringtones/FreeFlight.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/FreeFlight.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/FreeFlight.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Free Flight mime_type=application/ogg date_added=87947 _display_name=FreeFlight.ogg _size=113055 _data=/system/media/audio/ringtones/FreeFlight.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=5 is_music=false is_drm=false album_id=3 duration=12632 is_notification=false title_key=       ?       3       ?       ?               ?       °Ï       ?       ?       ?       °§        returned: 136
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Friendly Ghost duration=5217 is_notification=false mime_type=application/ogg _size=46425 artist=Dr. MAD _data=/system/media/audio/ringtones/FriendlyGhost.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/FriendlyGhost.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/FriendlyGhost.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Friendly Ghost mime_type=application/ogg date_added=87947 _display_name=FriendlyGhost.ogg _size=46425 _data=/system/media/audio/ringtones/FriendlyGhost.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=5217 is_notification=false title_key=        ?       3       ?       ?       ?       ?       °Ï       ®¢               ?       ?       -       ¶Ã    returned: 137
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Funk Y'all duration=4103 is_notification=false mime_type=application/ogg _size=39738 artist=Dr. Mad _data=/system/media/audio/ringtones/Funk_Yall.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Funk_Yall.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Funk_Yall.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Funk Y'all mime_type=application/ogg date_added=87947 _display_name=Funk_Yall.ogg _size=39738 _data=/system/media/audio/ringtones/Funk_Yall.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4103 is_notification=false title_key=    ?       1       ?       £§               ®¢       ?       °Ï       °Ï        returned: 138
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Game Over Guitar duration=6000 is_notification=false mime_type=application/ogg _size=49978 artist=Dr. MAD _data=/system/media/audio/ringtones/GameOverGuitar.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/GameOverGuitar.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/GameOverGuitar.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Game Over Guitar mime_type=application/ogg date_added=87947 _display_name=GameOverGuitar.ogg _size=49978 _data=/system/media/audio/ringtones/GameOverGuitar.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=6000 is_notification=false title_key=    ?       ?       ?       ?               -       ?       ?       3               ?       1       ?    returned: 139
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Gimme Mo' Town duration=4000 is_notification=false mime_type=application/ogg _size=38688 artist=Dr. Mad _data=/system/media/audio/ringtones/Gimme_Mo_Town.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Gimme_Mo_Town.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Gimme_Mo_Town.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Gimme Mo' Town mime_type=application/ogg date_added=87947 _display_name=Gimme_Mo_Town.ogg _size=38688 _data=/system/media/audio/ringtones/Gimme_Mo_Town.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=        ?       ?       ?       ?       ?               ?       -               °§       -       ?       ?    returned: 140
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Girtab duration=3450 is_notification=false mime_type=application/ogg _size=44376 artist=<unknown> _data=/system/media/audio/ringtones/Girtab.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Girtab.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Girtab.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Girtab mime_type=application/ogg date_added=87947 _display_name=Girtab.ogg _size=44376 _data=/system/media/audio/ringtones/Girtab.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=1 is_music=false is_drm=false album_id=3 duration=3450 is_notification=false title_key=      ?       ?       3       °§       ?       ?        returned: 141
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Glacial Groove duration=5095 is_notification=false mime_type=application/ogg _size=54923 artist=Dr. Mad _data=/system/media/audio/ringtones/Glacial_Groove.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Glacial_Groove.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Glacial_Groove.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Glacial Groove mime_type=application/ogg date_added=87947 _display_name=Glacial_Groove.ogg _size=54923 _data=/system/media/audio/ringtones/Glacial_Groove.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=5095 is_notification=false title_key=      ?       °Ï       ?       ?       ?       ?       °Ï               ?       3       -       -       ?    returned: 142
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Growl duration=5783 is_notification=false mime_type=application/ogg _size=41094 artist=Dr. MAD _data=/system/media/audio/ringtones/Growl.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Growl.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Growl.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Growl mime_type=application/ogg date_added=87947 _display_name=Growl.ogg _size=41094 _data=/system/media/audio/ringtones/Growl.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=5783 is_notification=false title_key= ?       3       -       ?       °Ï        returned: 143
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Halfway Home duration=7111 is_notification=false mime_type=application/ogg _size=68588 artist=Dr. Mad _data=/system/media/audio/ringtones/HalfwayHome.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/HalfwayHome.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/HalfwayHome.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Halfway Home mime_type=application/ogg date_added=87947 _display_name=HalfwayHome.ogg _size=68588 _data=/system/media/audio/ringtones/HalfwayHome.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=7111 is_notification=false title_key=      ?       ?       °Ï       ?       ?       ?       ®¢               ?       -       ?       ?        returned: 144
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Hydra duration=5000 is_notification=false mime_type=application/ogg _size=22924 artist=<unknown> _data=/system/media/audio/ringtones/Hydra.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Hydra.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Hydra.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Hydra mime_type=application/ogg date_added=87947 _display_name=Hydra.ogg _size=22924 _data=/system/media/audio/ringtones/Hydra.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=1 is_music=false is_drm=false album_id=3 duration=5000 is_notification=false title_key= ?       ®¢       ?       3       ?        returned: 145
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Insert Coin duration=2000 is_notification=false mime_type=application/ogg _size=15146 artist=Dr. MAD _data=/system/media/audio/ringtones/InsertCoin.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/InsertCoin.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/InsertCoin.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Insert Coin mime_type=application/ogg date_added=87947 _display_name=InsertCoin.ogg _size=15146 _data=/system/media/audio/ringtones/InsertCoin.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=2000 is_notification=false title_key= ?       ?       ¶Ã       ?       3       °§               ?       -       ?       ?        returned: 146
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Loopy Lounge duration=5095 is_notification=false mime_type=application/ogg _size=38307 artist=Dr. MAD _data=/system/media/audio/ringtones/LoopyLounge.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/LoopyLounge.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/LoopyLounge.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Loopy Lounge mime_type=application/ogg date_added=87947 _display_name=LoopyLounge.ogg _size=38307 _data=/system/media/audio/ringtones/LoopyLounge.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=5095 is_notification=false title_key=      °Ï       -       -       °•       ®¢               °Ï       -       1       ?       ?       ?        returned: 147
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Love Flute duration=4800 is_notification=false mime_type=application/ogg _size=34627 artist=Dr. MAD _data=/system/media/audio/ringtones/LoveFlute.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/LoveFlute.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/LoveFlute.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Love Flute mime_type=application/ogg date_added=87947 _display_name=LoveFlute.ogg _size=34627 _data=/system/media/audio/ringtones/LoveFlute.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4800 is_notification=false title_key=    °Ï       -       ?       ?               ?       °Ï       1       °§       ?        returned: 148
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=2 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Lyra duration=5676 is_notification=false mime_type=application/ogg _size=42540 artist=pdx _data=/system/media/audio/ringtones/Lyra.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Lyra.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Lyra.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Lyra mime_type=application/ogg date_added=87947 _display_name=Lyra.ogg _size=42540 _data=/system/media/audio/ringtones/Lyra.ogg bucket_id=293500348 date_modified=1217592000 track=2 parent=13 format=47362 artist_id=2 is_music=false is_drm=false album_id=3 duration=5676 is_notification=false title_key=    °Ï       ®¢       3       ?        returned: 149
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Machina duration=5125 is_notification=false mime_type=application/ogg _size=38077 artist=Ali Spagnola _data=/system/media/audio/ringtones/Machina.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Machina.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Machina.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Machina mime_type=application/ogg date_added=87947 _display_name=Machina.ogg _size=38077 _data=/system/media/audio/ringtones/Machina.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=6 is_music=false is_drm=false album_id=3 duration=5125 is_notification=false title_key=   ?       ?       ?       ?       ?       ?       ?        returned: 150
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Medieval Jaunt duration=3840 is_notification=false mime_type=application/ogg _size=28124 artist=Dr. MAD _data=/system/media/audio/ringtones/MidEvilJaunt.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/MidEvilJaunt.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/MidEvilJaunt.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Medieval Jaunt mime_type=application/ogg date_added=87947 _display_name=MidEvilJaunt.ogg _size=28124 _data=/system/media/audio/ringtones/MidEvilJaunt.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=3840 is_notification=false title_key=  ?       ?       ?       ?       ?       ?       ?       °Ï               °Í       ?       1       ?       °§    returned: 151
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Mildly Alarming duration=4000 is_notification=false mime_type=application/ogg _size=34864 artist=Dr. MAD _data=/system/media/audio/ringtones/MildlyAlarming.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/MildlyAlarming.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/MildlyAlarming.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Mildly Alarming mime_type=application/ogg date_added=87947 _display_name=MildlyAlarming.ogg _size=34864 _data=/system/media/audio/ringtones/MildlyAlarming.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=     ?       ?       °Ï       ?       °Ï       ®¢               ?       °Ï       ?       3       ?       ?    returned: 152
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Nairobi duration=4000 is_notification=false mime_type=application/ogg _size=41355 artist=Dr. Mad _data=/system/media/audio/ringtones/Nairobi.ogg
D/dalvikvm( 4093): GC_CONCURRENT freed 470K, 11% free 4551K/5088K, paused 3ms+2ms, total 25ms
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Nairobi.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Nairobi.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Nairobi mime_type=application/ogg date_added=87947 _display_name=Nairobi.ogg _size=41355 _data=/system/media/audio/ringtones/Nairobi.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=   ?       ?       ?       3       -       ?       ?        returned: 153
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Nassau duration=4364 is_notification=false mime_type=application/ogg _size=44104 artist=Dr. Mad _data=/system/media/audio/ringtones/Nassau.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Nassau.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Nassau.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Nassau mime_type=application/ogg date_added=87947 _display_name=Nassau.ogg _size=44104 _data=/system/media/audio/ringtones/Nassau.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4364 is_notification=false title_key=      ?       ?       ¶Ã       ¶Ã       ?       1        returned: 154
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=New Player duration=2000 is_notification=false mime_type=application/ogg _size=15563 artist=Dr. MAD _data=/system/media/audio/ringtones/NewPlayer.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/NewPlayer.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/NewPlayer.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=New Player mime_type=application/ogg date_added=87947 _display_name=NewPlayer.ogg _size=15563 _data=/system/media/audio/ringtones/NewPlayer.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=2000 is_notification=false title_key=    ?       ?       ?               °•       °Ï       ?       ®¢       ?       3        returned: 155
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=No Limits duration=3582 is_notification=false mime_type=application/ogg _size=32933 artist=Dr. Mad _data=/system/media/audio/ringtones/No_Limits.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/No_Limits.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/No_Limits.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=No Limits mime_type=application/ogg date_added=87947 _display_name=No_Limits.ogg _size=32933 _data=/system/media/audio/ringtones/No_Limits.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=3582 is_notification=false title_key=     ?       -               °Ï       ?       ?       ?       °§       ¶Ã        returned: 156
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Noisey One duration=4000 is_notification=false mime_type=application/ogg _size=36287 artist=Dr. MAD _data=/system/media/audio/ringtones/Noises1.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Noises1.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Noises1.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Noisey One mime_type=application/ogg date_added=87947 _display_name=Noises1.ogg _size=36287 _data=/system/media/audio/ringtones/Noises1.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=        ?       -       ?       ¶Ã       ?       ®¢               -       ?       ?        returned: 157
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Terrible Twos duration=4000 is_notification=false mime_type=application/ogg _size=39025 artist=Dr. MAD _data=/system/media/audio/ringtones/Noises2.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Noises2.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Noises2.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Terrible Twos mime_type=application/ogg date_added=87947 _display_name=Noises2.ogg _size=39025 _data=/system/media/audio/ringtones/Noises2.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=     °§       ?       3       3       ?       ?       °Ï       ?               °§       ?       -       ¶Ã        returned: 158
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Thriller Three duration=3967 is_notification=false mime_type=application/ogg _size=26662 artist=Dr. MAD _data=/system/media/audio/ringtones/Noises3.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Noises3.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Noises3.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Thriller Three mime_type=application/ogg date_added=87947 _display_name=Noises3.ogg _size=26662 _data=/system/media/audio/ringtones/Noises3.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=3967 is_notification=false title_key=    °§       ?       3       ?       °Ï       °Ï       ?       3               °§       ?       3       ?       ?        returned: 159
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Organ Dub duration=4364 is_notification=false mime_type=application/ogg _size=32640 artist=Dr. MAD _data=/system/media/audio/ringtones/OrganDub.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/OrganDub.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/OrganDub.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Organ Dub mime_type=application/ogg date_added=87947 _display_name=OrganDub.ogg _size=32640 _data=/system/media/audio/ringtones/OrganDub.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4364 is_notification=false title_key=       -       3       ?       ?       ?               ?       1       ?        returned: 160
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=2 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Orion duration=6205 is_notification=false mime_type=application/ogg _size=54456 artist=pdx _data=/system/media/audio/ringtones/Orion.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Orion.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Orion.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Orion mime_type=application/ogg date_added=87947 _display_name=Orion.ogg _size=54456 _data=/system/media/audio/ringtones/Orion.ogg bucket_id=293500348 date_modified=1217592000 track=2 parent=13 format=47362 artist_id=2 is_music=false is_drm=false album_id=3 duration=6205 is_notification=false title_key= -       3       ?       -       ?        returned: 161
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Paradise Island duration=4138 is_notification=false mime_type=application/ogg _size=39199 artist=Dr. Mad _data=/system/media/audio/ringtones/Paradise_Island.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Paradise_Island.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Paradise_Island.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Paradise Island mime_type=application/ogg date_added=87947 _display_name=Paradise_Island.ogg _size=39199 _data=/system/media/audio/ringtones/Paradise_Island.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4138 is_notification=false title_key=   °•       ?       3       ?       ?       ?       ¶Ã       ?               ?       ¶Ã       °Ï       ?    returned: 162
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=2 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Pegasus duration=9620 is_notification=false mime_type=application/ogg _size=86731 artist=pdx _data=/system/media/audio/ringtones/Pegasus.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Pegasus.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Pegasus.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Pegasus mime_type=application/ogg date_added=87947 _display_name=Pegasus.ogg _size=86731 _data=/system/media/audio/ringtones/Pegasus.ogg bucket_id=293500348 date_modified=1217592000 track=2 parent=13 format=47362 artist_id=2 is_music=false is_drm=false album_id=3 duration=9620 is_notification=false title_key=   °•       ?       ?       ?       ¶Ã       1       ¶Ã        returned: 163
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Perseus duration=10170 is_notification=false mime_type=application/ogg _size=236343 artist=<unknown> _data=/system/media/audio/ringtones/Perseus.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Perseus.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Perseus.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Perseus mime_type=application/ogg date_added=87947 _display_name=Perseus.ogg _size=236343 _data=/system/media/audio/ringtones/Perseus.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=1 is_music=false is_drm=false album_id=3 duration=10170 is_notification=false title_key= °•       ?       3       ¶Ã       ?       1       ¶Ã        returned: 164
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Playa duration=5854 is_notification=false mime_type=application/ogg _size=56616 artist=Dr. Mad _data=/system/media/audio/ringtones/Playa.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Playa.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Playa.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Playa mime_type=application/ogg date_added=87947 _display_name=Playa.ogg _size=56616 _data=/system/media/audio/ringtones/Playa.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=5854 is_notification=false title_key= °•       °Ï       ?       ®¢       ?        returned: 165
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Pyxis duration=3709 is_notification=false mime_type=application/ogg _size=16653 artist=pdx _data=/system/media/audio/ringtones/Pyxis.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Pyxis.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Pyxis.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Pyxis mime_type=application/ogg date_added=87947 _display_name=Pyxis.ogg _size=16653 _data=/system/media/audio/ringtones/Pyxis.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=2 is_music=false is_drm=false album_id=3 duration=3709 is_notification=false title_key= °•       ®¢       ?       ?       ¶Ã        returned: 166
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Revelation duration=5944 is_notification=false mime_type=application/ogg _size=52536 artist=Dr. Mad _data=/system/media/audio/ringtones/Revelation.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Revelation.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Revelation.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Revelation mime_type=application/ogg date_added=87947 _display_name=Revelation.ogg _size=52536 _data=/system/media/audio/ringtones/Revelation.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=5944 is_notification=false title_key=  3       ?       ?       ?       °Ï       ?       °§       ?       -       ?        returned: 167
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=2 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Rigel duration=3434 is_notification=false mime_type=application/ogg _size=32129 artist=pdx _data=/system/media/audio/ringtones/Rigel.ogg
D/dalvikvm( 4093): GC_CONCURRENT freed 388K, 11% free 4551K/5088K, paused 2ms+2ms, total 21ms
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Rigel.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Rigel.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Rigel mime_type=application/ogg date_added=87947 _display_name=Rigel.ogg _size=32129 _data=/system/media/audio/ringtones/Rigel.ogg bucket_id=293500348 date_modified=1217592000 track=2 parent=13 format=47362 artist_id=2 is_music=false is_drm=false album_id=3 duration=3434 is_notification=false title_key= 3       ?       ?       ?       °Ï        returned: 168
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Bell Phone duration=5094 is_notification=false mime_type=application/ogg _size=59024 artist=<unknown> _data=/system/media/audio/ringtones/Ring_Classic_02.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Ring_Classic_02.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Ring_Classic_02.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Bell Phone mime_type=application/ogg date_added=87947 _display_name=Ring_Classic_02.ogg _size=59024 _data=/system/media/audio/ringtones/Ring_Classic_02.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=1 is_music=false is_drm=false album_id=3 duration=5094 is_notification=false title_key=        ?       ?       °Ï       °Ï               °•       ?       -       ?       ?        returned: 169
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Digital Phone duration=3709 is_notification=false mime_type=application/ogg _size=21007 artist=<unknown> _data=/system/media/audio/ringtones/Ring_Digital_02.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Ring_Digital_02.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Ring_Digital_02.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Digital Phone mime_type=application/ogg date_added=87947 _display_name=Ring_Digital_02.ogg _size=21007 _data=/system/media/audio/ringtones/Ring_Digital_02.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=1 is_music=false is_drm=false album_id=3 duration=3709 is_notification=false title_key=     ?       ?       ?       ?       °§       ?       °Ï               °•       ?       -       ?       ?    returned: 170
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Chimey Phone duration=4153 is_notification=false mime_type=application/ogg _size=52809 artist=<unknown> _data=/system/media/audio/ringtones/Ring_Synth_02.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Ring_Synth_02.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Ring_Synth_02.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Chimey Phone mime_type=application/ogg date_added=87947 _display_name=Ring_Synth_02.ogg _size=52809 _data=/system/media/audio/ringtones/Ring_Synth_02.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=1 is_music=false is_drm=false album_id=3 duration=4153 is_notification=false title_key=  ?       ?       ?       ?       ?       ®¢               °•       ?       -       ?       ?        returned: 171
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Road Trip duration=5486 is_notification=false mime_type=application/ogg _size=49108 artist=Dr. Mad _data=/system/media/audio/ringtones/Road_Trip.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Road_Trip.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Road_Trip.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Road Trip mime_type=application/ogg date_added=87947 _display_name=Road_Trip.ogg _size=49108 _data=/system/media/audio/ringtones/Road_Trip.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=5486 is_notification=false title_key=     3       -       ?       ?               °§       3       ?       °•        returned: 172
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Romancing The Tone duration=4571 is_notification=false mime_type=application/ogg _size=31641 artist=Dr. MAD _data=/system/media/audio/ringtones/RomancingTheTone.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/RomancingTheTone.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/RomancingTheTone.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Romancing The Tone mime_type=application/ogg date_added=87947 _display_name=RomancingTheTone.ogg _size=31641 _data=/system/media/audio/ringtones/RomancingTheTone.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4571 is_notification=false title_key=      3       -       ?       ?       ?       ?       ?       ?       ?               °§       ?    returned: 173
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Safari duration=4000 is_notification=false mime_type=application/ogg _size=42220 artist=Dr. Mad _data=/system/media/audio/ringtones/Safari.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Safari.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Safari.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Safari mime_type=application/ogg date_added=87947 _display_name=Safari.ogg _size=42220 _data=/system/media/audio/ringtones/Safari.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=      ¶Ã       ?       ?       ?       3       ?        returned: 174
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Savannah duration=4000 is_notification=false mime_type=application/ogg _size=39138 artist=Dr. Mad _data=/system/media/audio/ringtones/Savannah.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Savannah.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Savannah.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Savannah mime_type=application/ogg date_added=87947 _display_name=Savannah.ogg _size=39138 _data=/system/media/audio/ringtones/Savannah.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=        ¶Ã       ?       ?       ?       ?       ?       ?       ?        returned: 175
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Scarabaeus duration=9231 is_notification=false mime_type=application/ogg _size=108968 artist=Ali Spagnola _data=/system/media/audio/ringtones/Scarabaeus.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Scarabaeus.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Scarabaeus.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Scarabaeus mime_type=application/ogg date_added=87947 _display_name=Scarabaeus.ogg _size=108968 _data=/system/media/audio/ringtones/Scarabaeus.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=6 is_music=false is_drm=false album_id=3 duration=9231 is_notification=false title_key= ¶Ã       ?       ?       3       ?       ?       ?       ?       1       ¶Ã        returned: 176
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Sceptrum duration=22528 is_notification=false mime_type=application/ogg _size=294019 artist=Ali Spagnola _data=/system/media/audio/ringtones/Sceptrum.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Sceptrum.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Sceptrum.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Sceptrum mime_type=application/ogg date_added=87947 _display_name=Sceptrum.ogg _size=294019 _data=/system/media/audio/ringtones/Sceptrum.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=6 is_music=false is_drm=false album_id=3 duration=22528 is_notification=false title_key=      ¶Ã       ?       ?       °•       °§       3       1       ?        returned: 177
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Seville duration=4800 is_notification=false mime_type=application/ogg _size=44422 artist=Dr. Mad _data=/system/media/audio/ringtones/Seville.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Seville.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Seville.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Seville mime_type=application/ogg date_added=87947 _display_name=Seville.ogg _size=44422 _data=/system/media/audio/ringtones/Seville.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4800 is_notification=false title_key=   ¶Ã       ?       ?       ?       °Ï       °Ï       ?        returned: 178
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=She's All That duration=4174 is_notification=false mime_type=application/ogg _size=39413 artist=Dr. Mad _data=/system/media/audio/ringtones/Shes_All_That.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Shes_All_That.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Shes_All_That.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=She's All That mime_type=application/ogg date_added=87947 _display_name=Shes_All_That.ogg _size=39413 _data=/system/media/audio/ringtones/Shes_All_That.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4174 is_notification=false title_key=        ¶Ã       ?       ?       ¶Ã               ?       °Ï       °Ï               °§       ?       ?       °§    returned: 179
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Silky Way duration=4174 is_notification=false mime_type=application/ogg _size=44186 artist=Dr. Mad _data=/system/media/audio/ringtones/SilkyWay.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/SilkyWay.ogg
D/MediaProvider[   87.390509@1] cfg80211_rtw_scan(wlan0)
[   87.393052@1] _rtw_pwr_wakeup call ips_leave....
[   87.397487@1] ==>ips_leave cnts:1
[   87.400778@1] ===>  rtw_ips_pwr_up..............
[   87.405297@1] ===> ips_netdrv_open.........
( 4093): happend modified time n[   87.414300@0]  ===> FirmwareDownload91C() fw:Rtl819XFwImageArray_TSMC
[   87.418694@0] FirmwareDownload92C accquire FW from embedded image
[   87.424739@0] fw_ver=v88, fw_subver=2, sig=0x88c0
ot equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/SilkyWay.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_ala[   87.454667@0] fw download ok!
[   87.457375@0] Set RF Chip ID to RF_6052 and RF type to 2.
rm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Silky Way mime_type=application/ogg date_added=87947 _display_name=SilkyWay.ogg _size=44186 _data=/system/media/audio/ringtones/SilkyWay.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4174 is_notification=false title_key=   ¶Ã       ?       °Ï       £§       ®¢               ?       ?    returned: 180
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Sitar Versus Sitar duration=4000 is_notification=false mime_type=application/ogg _size=28898 artist=Dr. MAD _data=/system/media/audio/ringtones/SitarVsSitar.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/SitarVsSitar.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/SitarVsSitar.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Sitar Versus Sitar mime_type=application/ogg date_added=87947 _display_name=SitarVsSitar.ogg _size=28898 _data=/system/media/audio/ringtones/SitarVsSitar.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=      ¶Ã       ?       °§       ?       3               ?       ?       3       ¶Ã       1       ¶Ã            returned: 181
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Solarium duration=7895 is_notification=false mime_type=application/ogg _size=60201 artist=Ali Spagnola _data=/system/media/audio/ringtones/Solarium.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Solarium.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Solarium.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Solarium mime_type=application/ogg date_added=87947 _display_name=Solarium.ogg _size=60201 _data=/system/media/audio/ringtones/Solarium.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=6 is_music=false is_drm=false album_id=3 duration=7895 is_notification=false title_key=        ¶Ã       -       °Ï       ?       3       ?       1       ?        returned: 182
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Springy Jalopy duration=4000 is_notification=false mime_type=application/ogg _size=26144 artist=Dr. MAD _data=/system/media/audio/ringtones/SpringyJalopy.ogg
D/dalvikvm( 4093): GC_CONCURRENT freed 396K, 11% free 4557K/5088K, paused 2ms+2ms, total 22ms
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/SpringyJalopy.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/SpringyJalopy.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Springy Jalopy mime_type=application/ogg date_added=87947 _display_name=SpringyJalopy.ogg _size=26144 _data=/system/media/audio/ringtones/SpringyJalopy.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=        ¶Ã       °•       3       ?       ?       ?       ®¢               °Í       ?       °Ï       -       °•    returned: 183
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_al[   87.873893@0] IQK:Start!!!
arm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Steppin' Out duration=4000 is_notification=false mime_type=application/ogg _size=37847 artist=Dr. Mad _data=/system/media/audio/ringtones/Steppin_Out.ogg
D/MediaProvide[   87.897830@0] Path A IQK Success!!
r( 4093): insert path/system/media/audio/ringtones/Steppin_Out.ogg
D/MediaProvider( 4093): happe[   87.910021@0] Path B IQK Success!!
nd modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/med[   87.929024@0] Path A IQK Success!!
ia/audio/ringtones/Steppin_Out.oggin getStorageId
V/MediaProvid[   87.938530@0] Path B IQK Success!!
er( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_r[   87.948663@0] IQK: final_candidate is 0
[   87.951465@0] IQK: RegE94=101 RegE9C=3f8 RegEA4=fe RegEAC=3fa RegEB4=103 RegEBC=b RegEC4=f8 RegECC=2
[   87.951484@0]  Path A IQ Calibration Success !
ingtone=true is_podcast=false bu[   87.969287@0] Path B IQ Calibration Success !
cket_display_name=ringtones composer=null title=Steppin' Out mime_type=application/ogg date_added=87947 _display_name=Steppin_Out.ogg _size=37847 _data=/system/media/audio/ringtones/Steppin_Out.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=       ¶Ã       °§       ?       °•       °•       ?       ?               -       1       °§        returned: 184
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Terminated duration=5333 is_notification=false mime_type=application/ogg _size=36620 artist=Dr. MAD _data=/system/media/audio/ringtones/Terminated.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Terminated.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Terminated.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Terminated mime_type=application/ogg date_added=87[   88.095704@0] pdmpriv->TxPowerTrackControl = 1
947 _display_name=Terminated.ogg _size=36620 _data=/system/media/audio/ringtones/Terminated.ogg [   88.108533@0] rtl8192cu_hal_init in 700ms
bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=5333 is_notification=false title_key=        °§       ?       3       ?       ?       ?       ?       °§       ?       ?        returned: 185
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=[   88.139615@0] <===  rtw_ips_pwr_up.............. in 740ms
[   88.144980@0] ==> ips_leave.....LED(0x00028000)...
[   88.150354@0] no packet in tx packet buffer (0)
null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Testudo duration=7636 is_notification=false mime_type=application/ogg _size=72078 artist=Ali Spagnola _data=/system/media/audio/ringtones/Testudo.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Testudo.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Testudo.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Testudo mime_type=application/ogg date_added=87947 _display_name=Testudo.ogg _size=72078 _data=/system/media/audio/ringtones/Testudo.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=6 is_music=false is_drm=false album_id=3 duration=7636 is_notification=false title_key=   °§       ?       ¶Ã       °§       1       ?       -        returned: 186
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Themos duration=21179 is_notification=false mime_type=application/ogg _size=175478 artist=<unknown> _data=/system/media/audio/ringtones/Themos.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Themos.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Themos.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Themos mime_type=application/ogg date_added=87948 _display_name=Themos.ogg _size=175478 _data=/system/media/audio/ringtones/Themos.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=1 is_music=false is_drm=false album_id=3 duration=21179 is_notification=false title_key=    °§       ?       ?       ?       -       ¶Ã        returned: 187
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Third Eye duration=4000 is_notification=false mime_type=application/ogg _size=36539 artist=Dr. Mad _data=/system/media/audio/ringtones/Third_Eye.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Third_Eye.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Third_Eye.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Third Eye mime_type=application/ogg date_added=87948 _display_name=Third_Eye.ogg _size=36539 _data=/system/media/audio/ringtones/Third_Eye.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=     °§       ?       ?       3       ?               ?       ®¢       ?        returned: 188
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Thunderfoot duration=4800 is_notification=false mime_type=application/ogg _size=46049 artist=Dr. Mad _data=/system/media/audio/ringtones/Thunderfoot.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Thunderfoot.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Thunderfoot.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Thunderfoot mime_type=application/ogg date_added=87948 _display_name=Thunderfoot.ogg _size=46049 _data=/system/media/audio/ringtones/Thunderfoot.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4800 is_notification=false title_key=       °§       ?       1       ?       ?       ?       3       ?       -       -       °§        returned: 189
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Twirl Away duration=4000 is_notification=false mime_type=application/ogg _size=26298 artist=Dr. MAD _data=/system/media/audio/ringtones/TwirlAway.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/TwirlAway.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/TwirlAway.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Twirl Away mime_type=application/ogg date_added=87948 _display_name=TwirlAway.ogg _size=26298 _data=/system/media/audio/ringtones/TwirlAway.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=    °§       ?       ?       3       °Ï               ?       ?       ?       ®¢        returned: 190
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=UrsaMinor duration=12800 is_notification=false mime_type=application/ogg _size=185005 artist=<unknown> _data=/system/media/audio/ringtones/UrsaMinor.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/UrsaMinor.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/UrsaMinor.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=UrsaMinor mime_type=application/ogg date_added=87948 _display_name=UrsaMinor.ogg _size=185005 _data=/system/media/audio/ringtones/UrsaMinor.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=1 is_music=false is_drm=false album_id=3 duration=12800 is_notification=false title_key=   1       3       ¶Ã       ?       ?       ?       ?       -       3        returned: 191
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Very Alarmed duration=4000 is_notification=false mime_type=application/ogg _size=28691 artist=Dr. MAD _data=/system/media/audio/ringtones/VeryAlarmed.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/VeryAlarmed.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/VeryAlarmed.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Very Alarmed mime_type=application/ogg date_added=87948 _display_name=VeryAlarmed.ogg _size=28691 _data=/system/media/audio/ringtones/VeryAlarmed.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4000 is_notification=false title_key=      ?       ?       3       ®¢               ?       °Ï       ?       3       ?       ?       ?        returned: 192
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Vespa duration=6857 is_notification=false mime_type=application/ogg _size=18043 artist=Ali Spagnola _data=/system/media/audio/ringtones/Vespa.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Vespa.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Vespa.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Vespa mime_type=application/ogg date_added=87948 _display_name=Vespa.ogg _size=18043 _data=/system/media/audio/ringtones/Vespa.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=6 is_music=false is_drm=false album_id=3 duration=6857 is_notification=false title_key= ?       ?       ¶Ã       °•       ?        returned: 193
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=World duration=4174 is_notification=false mime_type=application/ogg _size=31136 artist=Dr. MAD _data=/system/media/audio/ringtones/World.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/World.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/World.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=World mime_type=application/ogg date_added=87948 _display_name=World.ogg _size=31136 _data=/system/media/audio/ringtones/World.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=4 is_music=false is_drm=false album_id=3 duration=4174 is_notification=false title_key= ?       -       3       °Ï       ?        returned: 194
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ringtones track=0 is_ringtone=true is_alarm=false is_music=false is_podcast=false compilation=0 is_drm=false composer=null title=Zeta duration=6600 is_notification=false mime_type=application/ogg _size=77919 artist=<unknown> _data=/system/media/audio/ringtones/Zeta.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ringtones/Zeta.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ringtones
D/MediaProvider( 4093): no find storage /system/media/audio/ringtones/Zeta.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=true is_podcast=false bucket_display_name=ringtones composer=null title=Zeta mime_type=application/ogg date_added=87948 _display_name=Zeta.ogg _size=77919 _data=/system/media/audio/ringtones/Zeta.ogg bucket_id=293500348 date_modified=1217592000 track=0 parent=13 format=47362 artist_id=1 is_music=false is_drm=false album_id=3 duration=6600 is_notification=false title_key=    ?       ?       °§       ?        returned: 195
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ui track=0 is_ringtone=false is_alarm=false is_music=true is_podcast=false compilation=0 is_drm=false composer=null title=Dock duration=500 is_notification=false mime_type=application/ogg _size=6019 artist=<unknown> _data=/system/media/audio/ui/Dock.ogg
V/MediaPlayerService( 2594): Create new media retriever from pid 4093
D/MediaProvider( 4093): insert path/system/media/audio/ui/Dock.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ui
D/MediaProvider( 4093): no find storage /system/media/audio/ui/Dock.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=ui composer=null title=Dock mime_type=application/ogg date_added=87948 _display_name=Dock.ogg _size=6019 _data=/system/media/audio/ui/Dock.ogg bucket_id=78638153 date_modified=1217592000 track=0 parent=14 format=47362 artist_id=1 is_music=true is_drm=false album_id=4 duration=500 is_notification=false title_key=     ?       -       ?       £§        returned: 196
V/MP3Extractor( 2594): found possible 1st frame at 96 (header = 0xffff8103)
V/MP3Extractor( 2594): subsequent header is 15009000
V/MP3Extractor( 2594): no dice, no valid sequence of frames found.
D/WVMExtractorImpl( 2594): IsWidevineMedia 0
I/AsfExtractor( 2594): SniffAsf 3479
I/DDPExtractor( 2594): not ddp_dolby file
I/OggExtractor( 2594): OGG_SEEK: seekPacketAlign 395  seek to file_start offset=-1
I/OggExtractor( 2594):  
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ui track=0 is_ringtone=false is_alarm=false is_music=true is_podcast=false compilation=0 is_drm=false composer=null title=Effect_Tick duration=32 is_notification=false mime_type=application/ogg _size=3994 artisedia/audio/ui/KeypressDelete.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=ui composer=null title=KeypressDelete mime_type=application/ogg date_added=87948 _display_name=KeypressDelete.ogg _size=6193 _data=/system/media/audio/ui/KeypressDelete.ogg bucket_id=78638153 date_modified=1217592000 track=0 parent=14 format=47362 artist_id=7 is_music=true is_drm=false album_id=4 duration=169 is_notification=false title_key=       £§       ?       ®¢       °•       3       ?       ¶Ã       ¶Ã       ?       ?       °Ï       ?       °§       ?        returned: 198
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ui track=0 is_ringtone=false is_alarm=false is_music=true is_podcast=false compilation=0 is_drm=false composer=null title=KeypressReturn duration=364 is_notification=false mime_type=application/ogg _size=7972 artist=Copyright 2009 Android Open Source Project _data=/system/media/audio/ui/KeypressReturn.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ui/KeypressReturn.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ui
D/MediaProvider( 4093): no find storage /system/media/audio/ui/KeypressReturn.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_a[   89.570496@0] survey done event(3) band:0 for wlan0
larm=false is_ringtone=false is_podcast=false bucket_display_name=ui composer=null title=KeypressReturn mime_type=application/ogg date_added=87948 _display_name=KeypressReturn.ogg _size=7972 _data=/system/media/audio/ui/KeypressReturn.ogg bucket_id=78638153 date_modified=1217592000 track=0 parent=14 format=47362 artist_id=7 is_music=true is_drm=false album_id=4 duration=364 is_notification=false title_key= £§       ?       ®¢       °•       3       ?       ¶Ã       ¶Ã    returned: 199
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ui track=0 is_ringtone=false is_alarm=false is_music=true is_podcast=false compilation=0 is_drm=false composer=null title=KeypressSpacebar duration=279 is_notification=false mime_type=application/ogg _size=7392 artist=Copyright 2009 Android Open Source Project _data=/system/media/audio/ui/KeypressSpacebar.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ui/KeypressSpacebar.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ui
D/MediaProvider( 4093): no find storage /system/media/audio/ui/KeypressSpacebar.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null media_type=2 storage_id=65537 is_alarm=false is_ringtone=false is_podcast=false bucket_display_name=ui composer=null title=KeypressSpacebar mime_type=application/ogg date_added=87948 _display_name=KeypressSpacebar.ogg _size=7392 _data=/system/media/audio/ui/KeypressSpacebar.ogg bucket_id=78638153 date_modified=1217592000 track=0 parent=14 format=47362 artist_id=7 is_music=true is_drm=false album_id=4 duration=279 is_notification=false title_key= £§       ?       ®¢       °•       3       ?       ¶Ã       ¶Ã       ¶Ã       °•       ?       ?       ?       ?       ?    returned: 200
V/MediaProvider( 4093): insertInternal: content://media/internal/audio/media, initValues=album_artist=null genre=null date_modified=1217592000 album=ui track=0 is_ringtone=false is_alarm=false is_music=true is_podcast=false compilation=0 is_drm=false composer=null title=KeypressStandard duration=101 is_notification=false mime_type=application/ogg _size=5194 artist=Copyright 2009 Android Open Source Project _data=/system/media/audio/ui/KeypressStandard.ogg
D/MediaProvider( 4093): insert path/system/media/audio/ui/KeypressStandard.ogg
D/MediaProvider( 4093): happend modified time not equal
V/MediaProvider( 4093): Returning cached entry for /system/media/audio/ui
D/MediaProvider( 4093): no find storage /system/media/audio/ui/KeypressStandard.oggin getStorageId
V/MediaProvider( 4093): insertFile: values=album_artist=null medi^C

130|root@android:/ # 
130|root@android:/ # 
130|root@android:/ # 
130|root@android:/ # 
130|root@android:/ # 
130|root@android:/ # 
130|root@android:/ # 
130|root@android:/ # 
130|root@android:/ # netc[   92.138294@0] ==>rtw_ps_processor .fw_state(8)
[   92.138325@0] ==>ips_enter cnts:2
[   92.140401@0] ===> rtw_ips_pwr_down...................
[   92.148453@1] ====> rtw_ips_dev_unload...
[   92.173206@0] usb_read_port_cancel
[   92.173266@0] usb_read_port_complete()-1284: RX Warning! bDriverStopped(0) OR bSurpriseRemoved(0) bReadPortCancel(1)
[   92.181530@0] usb_read_port_complete()-1284: RX Warning! bDriverStopped(0) OR bSurpriseRemoved(0) bReadPortCancel(1)
[   92.192029@0] usb_read_port_complete()-1284: RX Warning! bDriverStopped(0) OR bSurpriseRemoved(0) bReadPortCancel(1)
[   92.202572@0] usb_read_port_complete()-1284: RX Warning! bDriverStopped(0) OR bSurpriseRemoved(0) bReadPortCancel(1)
[   92.213052@0] usb_write_port_cancel 
[   92.216620@0] ==> rtl8192cu_hal_deinit 
[   92.220411@0] bkeepfwalive(0)
[   92.223312@0] card disble without HWSM...........
[   92.233060@0] <=== rtw_ips_pwr_down..................... in 90ms
fg
lo       UP                                   127.0.0.1/8   0x00000049 00:00:00:00:00:00
eth0     UP                               192.168.1.170/24  0x00001043 84:26:90:00:00:02
sit0     DOWN                                   0.0.0.0/0   0x00000080 00:00:00:00:00:00
ip6tnl0  DOWN                                   0.0.0.0/0   0x00000080 00:00:00:00:00:00
wlan0    UP                                     0.0.0.0/0   0x00001003 ac:a2:13:0b:05:79
p2p0     UP                                     0.0.0.0/0   0x00001003 ae:a2:13:0b:05:79
root@android:/ # 
root@android:/ # 
root@android:/ # 
root@android:/ # reboot
[   95.192483@0] cacel delay work and reset watch dog
[   95.192605@0] m3_nand_reboot_notifier 1242 
[   95.195920@0] aml_nftl_reboot_notifier :system 0
[   95.200703@0] aml_nftl_reboot_notifier :cache 0
[   95.205011@0] aml_nftl_reboot_notifier :backup 0
[   95.209832@0] aml_nftl_reboot_notifier :data 0
[   95.214041@0] aml_keys_notify_reboot:1597rtw_dev_shutdown
[   95.223399@0] m3_nand_shutdown 1367 chip->options:60a01
[   95.224758@0] Disabling non-boot CPUs ...
[   95.229524@1] IRQ61 no longer affine to CPU1
[   95.229725@1] Disable timerD
[   95.230020@0] CPU1: shutdown
[   95.240660@0] Restarting system.
[   95.24E I3000000032940xf100110203:77500EEEE I400000004294_M6_BL1_3431>2534313
TE : 77172
wait pll-0x03 target is 0204 now it is 0x00000203

DDR clock is 516MHz with Low Power & 1T mode

DDR training :
DX0DLLCR:40000000
DX0DQTR:ffffffff
DX0DQSTR:3db05001
DX1DLLCR:40000000
DX1DQTR:ffffffff
DX1DQSTR:3db05001
DX2DLLCR:40000000
DX2DQTR:ffffffff
DX2DQSTR:3db05001
DX3DLLCR:40000000
DX3DQTR:ffffffff
DX3DQSTR:3db05001
Stage 00 Result 00000000
Stage 01 Result 00000000
Stage 02 Result 00000000
Stage 03 Result 00000000

HHH
Boot From SPI
0x12345678
Boot from internal device 1st SPI RESERVED

System Started



U-boot(m6_mbx_v1@0e184153) (Dec 24 2014 - 16:51:00)

aml_rtc_init
aml rtc init first time!
Clear HDMI KSV RAM
DRAM:  1 GiB
relocation Offset is: 105e8000
NAND:  Amlogic nand flash uboot driver, Version U1.06.017 (c) 2010 Amlogic Inc.
SPI BOOT : continue i 0
No NAND device found!!!
NAND device id: ad d7 94 91 60 44 
aml_chip->hynix_new_nand_type =: 4 
NAND device: Manufacturer ID: 0xad, Chip ID: 0xad (Hynix C revision 20nm NAND 4GiB H27UBG8T2C)
1 NAND chips detected
#####aml_nand_init, with RB pins and chip->chip_delay:20
bus_cycle=5, bus_timing=6, start_cycle=6, end_cycle=7,system=5.0ns
oob size is not enough for selected bch mode: NAND_BCH60_1K_MODE force bch to mode: NAND_BCH40_1K_MODE
aml_chip->oob_fill_cnt =64,aml_chip->oob_size =640,bch_bytes =70
multi plane error for selected plane mode: NAND_TWO_PLANE_MODE force plane to : NAND_SINGLE_PLANE_MODE
aml_nand_init:oobmul =1,chip->ecc.layout->oobfree[0].length=16,aml_chip->oob_size=640
aml_nand_get_read_default_value_hynix 980 get default reg value at blk:0, page:7
aml nand env valid addr: 418000 
key start_blk=2040,end_blk=2047,aml_nand_key_init:684
aml nand key valid addr: ff000000 
aml nand key valid addr: ff200000 
aml nand key valid addr: ff400000 
aml nand key valid addr: ff600000 
CONFIG_KEYSIZE=0x10000; KEYSIZE=0xfffc; bbt=0x1330; default_keyironment_size=0xeccc
i=0,register --- nand_key
Creating 8 MTD partitions on "nandnormal":
0x000000c00000-0x000001400000 : "logo"
0x000001400000-0x000001c00000 : "aml_logo"
0x000001c00000-0x000002400000 : "recovery"
0x000002400000-0x000008c00000 : "boot"
0x000008c00000-0x000048c00000 : "system"
0x000048c00000-0x000068c00000 : "cache"
0x000068c00000-0x000078c00000 : "backup"
0x000078c00000-0x0000ff000000 : "data"
nandnormal initialized ok
detect mx chiprevD :1 and nand_type: 4
nand_curr_device =1
MMC:   SDIO Port B: 0, SDIO Port C: 1
SPI BOOT,spi_env_relocate_spec : env_relocate_spec 53 
SF: Detected MX25L3205D with page size 256, total 4 MiB

SPI NOR Flash have write protect!!!
In:    serial
Out:   serial
Err:   serial
aml_i2c_init
register usb cfg[0] = 9fe8292c
Net:   Meson_Ethernet
init suspend firmware done. (ret:0)
efuse version is not selected.
###  main_loop entered: bootdelay=1

### main_loop: bootcmd="run compatible_boot"
Hit any key to stop autoboot:  0 
m6_mbx_v1#
m6_mbx_v1#
m6_mbx_v1#
m6_mbx_v1#setenv ethaddr 84:26:90:00:00:a2
m6_mbx_v1#saveenv 
Saving Environment to SPI Flash...
SPI BOOT,spi_saveenv : saveenv 93 
Erasing SPI flash...m6_mbx_v1#reset
resetting ...
EEEE I3000000032940xf100110203:77500EEEE I400000004294_M6_BL1_3431>2534313
TE : 77172
wait pll-0x03 target is 0204 now it is 0x00000203

DDR clock is 516MHz with Low Power & 1T mode

DDR training :
DX0DLLCR:40000000
DX0DQTR:ffffffff
DX0DQSTR:3db05001
DX1DLLCR:40000000
DX1DQTR:ffffffff
DX1DQSTR:3db05001
DX2DLLCR:40000000
DX2DQTR:ffffffff
DX2DQSTR:3db05001
DX3DLLCR:40000000
DX3DQTR:ffffffff
DX3DQSTR:3db05001
Stage 00 Result 00000000
Stage 01 Result 00000000
Stage 02 Result 00000000
Stage 03 Result 00000000

HHH
Boot From SPI
0x12345678
Boot from internal device 1st SPI RESERVED

System Started



U-boot(m6_mbx_v1@0e184153) (Dec 24 2014 - 16:51:00)

aml_rtc_init
aml rtc init first time!
Clear HDMI KSV RAM
DRAM:  1 GiB
relocation Offset is: 105e8000
NAND:  Amlogic nand flash uboot driver, Version U1.06.017 (c) 2010 Amlogic Inc.
SPI BOOT : continue i 0
No NAND device found!!!
NAND device id: ad d7 94 91 60 44 
aml_chip->hynix_new_nand_type =: 4 
NAND device: Manufacturer ID: 0xad, Chip ID: 0xad (Hynix C revision 20nm NAND 4GiB H27UBG8T2C)
1 NAND chips detected
#####aml_nand_init, with RB pins and chip->chip_delay:20
bus_cycle=5, bus_timing=6, start_cycle=6, end_cycle=7,system=5.0ns
oob size is not enough for selected bch mode: NAND_BCH60_1K_MODE force bch to mode: NAND_BCH40_1K_MODE
aml_chip->oob_fill_cnt =64,aml_chip->oob_size =640,bch_bytes =70
multi plane error for selected plane mode: NAND_TWO_PLANE_MODE force plane to : NAND_SINGLE_PLANE_MODE
aml_nand_init:oobmul =1,chip->ecc.layout->oobfree[0].length=16,aml_chip->oob_size=640
aml_nand_get_read_default_value_hynix 980 get default reg value at blk:0, page:7
aml nand env valid addr: 418000 
key start_blk=2040,end_blk=2047,aml_nand_key_init:684
aml nand key valid addr: ff000000 
aml nand key valid addr: ff200000 
aml nand key valid addr: ff400000 
aml nand key valid addr: ff600000 
CONFIG_KEYSIZE=0x10000; KEYSIZE=0xfffc; bbt=0x1330; default_keyironment_size=0xeccc
i=0,register --- nand_key
Creating 8 MTD partitions on "nandnormal":
0x000000c00000-0x000001400000 : "logo"
0x000001400000-0x000001c00000 : "aml_logo"
0x000001c00000-0x000002400000 : "recovery"
0x000002400000-0x000008c00000 : "boot"
0x000008c00000-0x000048c00000 : "system"
0x000048c00000-0x000068c00000 : "cache"
0x000068c00000-0x000078c00000 : "backup"
0x000078c00000-0x0000ff000000 : "data"
nandnormal initialized ok
detect mx chiprevD :1 and nand_type: 4
nand_curr_device =1
MMC:   SDIO Port B: 0, SDIO Port C: 1
SPI BOOT,spi_env_relocate_spec : env_relocate_spec 53 
SF: Detected MX25L3205D with page size 256, total 4 MiB

SPI NOR Flash NO write protect!!!, So I will enable it...
*!* Warning - bad CRC, using default environment!!!!

In:    serial
Out:   serial
Err:   serial
aml_i2c_init
register usb cfg[0] = 9fe8292c
Net:   Meson_Ethernet
init suspend firmware done. (ret:0)
efuse version is not selected.
###  main_loop entered: bootdelay=1

### main_loop: bootcmd="setenv bootcmd run compatible_boot; saveenv; run compatible_boot; run compatible_boot"
Hit any key to stop autoboot:  0 
(Re)start USB...
USB:   dwc_usb driver version: 2.94 6-June-2012
USB (1) peri reg base: c1108420
USB (1) use clock source: XTAL input
USB (1) PHY Clock not detected!
USB (1) base addr: 0xc90c0000
Force id mode: Host
dwc_otg: Highspeed device found !

scanning bus for devices... 2 USB Device(s) found
       scanning bus for storage devices... 1 Storage Device(s) found
.
.
.
.
.
.
        8626m6.update is exist
upgrade_step = 0
Saving Environment to SPI Flash...
SPI BOOT,spi_saveenv : saveenv 93 
Erasing SPI flash...Writing to SPI flash...done
reading recovery.img
.
.
.
.
.
.
.

** Unable to read "recovery.img" from usb 0:1 **
recovery in nand!!!

NAND read: recovery offset 0x0, size 0x600000
 6291456 bytes read: OK
## Booting kernel from Legacy Image at 82000000 ...
   Image Name:   Linux-3.0.50
   Image Type:   ARM Linux Kernel Image (lzma compressed)
   Data Size:    3526982 Bytes = 3.4 MiB
   Load Address: 80008000
   Entry Point:  80008000
   Verifying Checksum ... OK
        Ramdisk start addr = 0x8235e000, len = 0x18f07e
board_usb_stop cfg: 0
   Uncompressing Kernel Image ... OK
machid from environment: 0x4e27 
EFUSE machid is not set.
Using machid 0x4e27 from environment

Starting kernel ...

[    0.000000@0] Initializing cgroup subsys cpu
[    0.000000@0] Linux version 3.0.50 (mx@xy) (gcc version 4.6.x-google 20120106 (prerelease) (GCC) ) #20 SMP PREEMPT Tue Dec 30 18:30:45 CST 2014
[    0.000000@0] CPU: ARMv7 Processor [413fc090] revision 0 (ARMv7), cr=10c53c7d
[    0.000000@0] CPU: VIPT nonaliasing data cache, VIPT aliasing instruction cache
[    0.000000@0] Machine: Amlogic Meson6 g02 customer platform
[    0.000000@0] Ignoring unrecognised tag 0x00000000
[    0.000000@0] early_mem:532: start=0x80000000, size=0x4000000
[    0.000000@0] early_mem:563: start=0x8f100000, size=0x10e00000
[    0.000000@0] early_mem:571: start=0xa0000000, size=0x20000000
[    0.000000@0] Memory policy: ECC disabled, Data cache writealloc
[    0.000000@0] PERCPU: Embedded 7 pages/cpu @c12a3000 s5600 r8192 d14880 u32768
[    0.000000@0] Built 1 zonelists in Zone order, mobility grouping on.  Total pages: 214528
[    0.000000@0] Kernel command line: root=/dev/cardblksd2 rw rootfstype=ext3 rootwait init=/init console=ttyS0,115200n8 nohlt vmalloc=256m mem=1024m logo=osd1,0x84100000,720p
[    0.000000@0] osd1:1
[    0.000000@0] 720p:6
[    0.000000@0] PID hash table entries: 2048 (order: 1, 8192 bytes)
[    0.000000@0] Dentry cache hash table entries: 65536 (order: 6, 262144 bytes)
[    0.000000@0] Inode-cache hash table entries: 32768 (order: 5, 131072 bytes)
[    0.000000@0] Memory: 64MB 270MB 512MB = 846MB total
[    0.000000@0] Memory: 846556k/846556k available, 19748k reserved, 524288K highmem
[    0.000000@0] Virtual kernel memory layout:
[    0.000000@0]     vector  : 0xffff0000 - 0xffff1000   (   4 kB)
[    0.000000@0]     fixmap  : 0xfff00000 - 0xfffe0000   ( 896 kB)
[    0.000000@0]     DMA     : 0xffc00000 - 0xffe00000   (   2 MB)
[    0.000000@0]     vmalloc : 0xe0000000 - 0xf0000000   ( 256 MB)
[    0.000000@0]     lowmem  : 0xc0000000 - 0xdff00000   ( 511 MB)
[    0.000000@0]     pkmap   : 0xbfe00000 - 0xc0000000   (   2 MB)
[    0.000000@0]     modules : 0xbf000000 - 0xbfe00000   (  14 MB)
[    0.000000@0]       .init : 0xc0008000 - 0xc0037000   ( 188 kB)
[    0.000000@0]       .text : 0xc0037000 - 0xc08958f4   (8571 kB)
[    0.000000@0]       .data : 0xc0896000 - 0xc08fe480   ( 418 kB)
[    0.000000@0]        .bss : 0xc08fe4a4 - 0xc0a99af8   (1646 kB)
[    0.000000@0] SLUB: Genslabs=13, HWalign=32, Order=0-3, MinObjects=0, CPUs=2, Nodes=1
[    0.000000@0] Preemptible hierarchical RCU implementation.
[    0.000000@0]        RCU debugfs-based tracing is enabled.
[    0.000000@0] NR_IRQS:256
[    0.000000@0] gic_init: irq_offset=0
[    0.000000@0] sched_clock: 32 bits at 1000kHz, resolution 1000ns, wraps every 4294967ms
[    0.000000@0] MESON TIMER-A c08a9ec0
[    0.000000@0] Disable timerA
[    0.000000@0] Console: colour dummy device 80x30
[    0.000000@0] console [ttyS0] enabled
[    0.243604@0] Calibrating delay loop... 2387.14 BogoMIPS (lpj=11935744)
[    0.300046@0] pid_max: default: 32768 minimum: 301
[    0.302236@0] Mount-cache hash table entries: 512
[    0.307450@0] Initializing cgroup subsys cpuacct
[    0.311406@0] Initializing cgroup subsys freezer
[    0.315982@0] CPU: Testing write buffer coherency: ok
[    0.321149@0] MESON TIMER-B c08a9f80
[    0.324528@0] Disable timerA
[    0.327383@0] Disable timerB
[    0.330253@0] Disable timerA
[    0.333235@0] L310 cache controller enabled
[    0.337280@0] l2x0: 8 ways, CACHE_ID 0x4100a0c8, AUX_CTRL 0x7e462c01, Cache size: 524288 B
[    0.345574@0]  prefetch=0x31000006
[    0.348909@0] ===actlr=0x41
[    0.351693@0] ===actlr=0x4b
[    0.354463@0] SCU_CTRL: scu_ctrl=0x69
[    0.358109@0] pl310: aux=0x7e462c01, prefetch=0x31000006
[    0.461434@1] CPU1: Booted secondary processor
[    0.521174@1] MESON TIMER-D c08aa100
[    0.521197@0] Brought up 2 CPUs
[    0.521205@0] SMP: Total of 2 processors activated (4780.85 BogoMIPS).
[    0.533171@1] Disable timerD
[    0.536186@0] devtmpfs: initialized
[    0.543299@0] clkrate [ xtal ] : 24000000
[    0.543499@0] clkrate [ pll_sys ] : 1200000000
[    0.547925@0] clkrate [ pll_fixed ] : 2000000000
[    0.552541@0] clkrate [ pll_vid2 ] : 378000000
[    0.556952@0] clkrate [ pll_hpll ] : 378000000
[    0.561450@0] clkrate [ pll_ddr ] : 516000000
[    0.565719@0] clkrate [ a9_clk ] : 1200000000
[    0.570123@0] clkrate [ clk81 ] : 200000000
[    0.574327@0] clkrate [ usb0 ] : 0
[    0.577675@0] clkrate [ usb1 ] : 12000000
[    0.582582@0] print_constraints: dummy: 
[    0.585715@0] boot_monitor: device successfully initialized.
[    0.591181@0] ** enable watchdog
[    0.594442@0] boot_monitor: driver successfully loaded.
[    0.599682@0] NET: Registered protocol family 16
[    0.605105@0] ***vcck: vcck_pwm_init
[    0.607561@0] ****** aml_eth_pinmux_setup() ******
[    0.612519@0] ****** aml_eth_clock_enable() ******
[    0.617277@0] ****** aml_eth_reset() ******
[    0.651160@0] register lm device 0
[    0.651281@0] register lm device 1
[    0.655314@0] usb_wifi_power Off
[    0.655627@0] chip version=ffbfbfff
[    0.659091@0] vdac_switch_init_module
[    0.662670@0] vdac_switch mode = 0
[    0.666183@0] tv_init_module
[    0.668884@0] major number 254 for disp
[    0.672796@0] vout_register_server
[    0.676171@0] register tv module server ok 
[    0.680529@0] /home/mx/openlinux-jbmr1/common/drivers/amlogic/i2c/aml_i2c.c : aml_i2c_init
[    0.688700@0] /home/mx/openlinux-jbmr1/common/drivers/amlogic/i2c/aml_i2c.c : aml_i2c_probe
[    0.697013@0] master_no = 0, resource = c08f435c, maseter_regs=f1108500
[    0.703746@0] aml-i2c aml-i2c.0: add adapter aml_i2c_adap0(df811828)
[    0.709937@0] aml-i2c aml-i2c.0: aml i2c bus driver.
[    0.715003@0] /home/mx/openlinux-jbmr1/common/drivers/amlogic/i2c/aml_i2c.c : aml_i2c_probe
[    0.723227@0] master_no = 1, resource = c08f43b4, maseter_regs=f11087c0
[    0.729916@0] aml-i2c aml-i2c.1: add adapter aml_i2c_adap1(df812028)
[    0.736158@0] aml-i2c aml-i2c.1: aml i2c bus driver.
[    0.741185@0] /home/mx/openlinux-jbmr1/common/drivers/amlogic/i2c/aml_i2c.c : aml_i2c_probe
[    0.749431@0] master_no = 2, resource = c08f440c, maseter_regs=f3100500
[    0.756130@0] aml-i2c aml-i2c.2: add adapter aml_i2c_adap2(df812828)
[    0.762372@0] aml-i2c aml-i2c.2: aml i2c bus driver.
[    0.767470@0] HDMI DEBUG: amhdmitx_init [1831]
[    0.771746@0] HDMI Ver: 2013Aug25a
[    0.775140@0] HDMI DEBUG: amhdmitx_probe [1638]
[    0.779912@1] Set HDMI:Chip C
[    0.782613@1] HDMI DEBUG: HDMITX_M1B_Init [3620]
[    0.787190@1] HDMI DEBUG: HDMITX_M1B_Init [3623]
[    0.791801@1] HDMI DEBUG: hdmi_hw_init [1207]
[    0.791806@0] HDMI: get hdmi platform data
[    0.791812@0] HDMI 5V Power On
[    0.803295@1] hdmitx: stat: hdmitx: misc cmd 0x15000000
[    0.808459@1] HDMI: get brd phy data
[    0.812022@1] hdmi phy setting
[    0.815095@1] HDMI: get brd phy data
[    0.818609@1] hdmi phy setting
[    0.822136@1] HDMI: reset intr mask
[    0.841206@1] HDMI DEBUG: hdmi_task_handle [1268]
[    0.841213@0] HDMI irq 1
[    0.841225@0] Reg0x196 = 0x2  Reg0x80 = 0x1  Reg0x26 = 0xc
[    0.841242@0] Reg0x196 = 0x42  Reg0x80 = 0x1  Reg0x26 = 0x4c
[    0.857213@0] bio: create slab <bio-0> at 0
[    0.858871@0] SCSI subsystem initialized
[    0.862329@0] usbcore: registered new interface driver usbfs
[    0.867815@0] usbcore: registered new interface driver hub
[    0.873385@0] usbcore: registered new device driver usb
[    0.878816@0] not display in uboot
[    0.881865@0] [0x1b7e] = 0xff
[    0.884630@0] [0x105f] = 0x0
[    0.887494@0] [0x109d] = 0x814d3928
[    0.890965@0] [0x109e] = 0x6b425012
[    0.894456@0] [0x109f] = 0x110
[    0.897475@0] [0x109c] = 0x1043e
[    0.900687@0] [0x1066] = 0x10843
[    0.903904@0] [0x1059] = 0x100
[    0.906937@0] [0x105f] = 0x80000
[    0.910148@0] [0x105f] = 0x88001
[    0.913372@0] [0x105f] = 0x80003
[    0.916572@0] [0x104a] = 0x101
[    0.919609@0] [0x107f] = 0x8c0000c3
[    0.923087@0] [0x1bb8] = 0x52
[    0.926033@0] [0x1b62] = 0x2029
[    0.929157@0] [0x1b8d] = 0x4040
[    0.932287@0] [0x1b8e] = 0x19
[    0.935233@0] [0x1b94] = 0x288
[    0.938272@0] [0x1b95] = 0xc87
[    0.941314@0] [0x1b97] = 0xce3
[    0.944347@0] [0x1b98] = 0x50
[    0.947298@0] [0x1b99] = 0xf0
[    0.950250@0] [0x1b9a] = 0x50
[    0.953206@0] [0x1b9b] = 0x2b0
[    0.956239@0] [0x1b9c] = 0xcb0
[    0.959277@0] [0x1b9d] = 0x4
[    0.962146@0] [0x1b9e] = 0x8
[    0.965006@0] [0x1ba1] = 0x4
[    0.967870@0] [0x1ba2] = 0x8
[    0.970734@0] [0x1ba4] = 0x288
[    0.973777@0] [0x1ba3] = 0xc87
[    0.976811@0] [0x1ba6] = 0x1d
[    0.979762@0] [0x1baf] = 0x2ec
[    0.982804@0] [0x1ba7] = 0x100
[    0.985838@0] [0x1ba8] = 0xa8
[    0.988789@0] [0x1ba9] = 0xa8
[    0.991745@0] [0x1baa] = 0x100
[    0.994778@0] [0x1bab] = 0x0
[    0.997642@0] [0x1bac] = 0x5
[    1.000507@0] [0x1bae] = 0x2ed
[    1.003553@0] [0x1b68] = 0x100
[    1.006583@0] [0x1b60] = 0x0
[    1.009447@0] [0x1b6e] = 0x200
[    1.012491@0] [0x1b58] = 0x0
[    1.015350@0] [0x1b7e] = 0x0
[    1.018214@0] [0x1b64] = 0x9061
[    1.021344@0] [0x1b65] = 0xa061
[    1.024464@0] [0x1b66] = 0xb061
[    1.027589@0] [0x1b78] = 0x1
[    1.030453@0] [0x1b79] = 0x1
[    1.033322@0] [0x1b7a] = 0x1
[    1.036182@0] [0x1b7b] = 0x1
[    1.039046@0] [0x1b7c] = 0x1
[    1.041915@0] [0x1b7d] = 0x1
[    1.044775@0] [0x271a] = 0xa
[    1.047639@0] [0x1bfc] = 0x1000
[    1.050764@0] [0x1c0d] = 0x3102
[    1.053893@0] [0x1c0e] = 0x54
[    1.056840@0] [0x1b80] = 0x1
[    1.059704@0] [0x1b57] = 0x0
[    1.062574@0] tvoutc_setmode[328]
[    1.065868@0] mode is: 6
[    1.068386@0] VPU_VIU_VENC_MUX_CTRL: 0xa
[    1.072295@0] viu chan = 1
[    1.074981@0] VPU_VIU_VENC_MUX_CTRL: 0xa
[    1.081305@0] print_constraints: vcck: 1070 <--> 1330 mV at 1330 mV 
[    1.085664@0] Advanced Linux Sound Architecture Driver Version 1.0.24.
[    1.092701@0] cfg80211: Calling CRDA to update world regulatory domain
[    1.098765@0] Switching to clocksource Timer-E
[    1.111187@0] Switched to NOHz mode on CPU #0
[    1.111195@1] Switched to NOHz mode on CPU #1
[    1.115931@1] MXL: register mxl101 demod driver
[    1.118765@1] register avl6211 demod driver
[    1.123009@1] SI: register si2168 demod driver
[    1.127449@1] [si2176..]si2176_tuner_init.
[    1.131511@1] [si2196..]si2196_tuner_init.
[    1.135608@1] [ctc703_module_init]:ctc703 tuner module  init
[    1.141267@1] NET: Registered protocol family 2
[    1.145805@1] IP route cache hash table entries: 16384 (order: 4, 65536 bytes)
[    1.153196@1] TCP established hash table entries: 65536 (order: 7, 524288 bytes)
[    1.160914@1] TCP bind hash table entries: 65536 (order: 7, 786432 bytes)
[    1.167764@1] TCP: Hash tables configured (established 65536 bind 65536)
[    1.173722@1] TCP reno registered
[    1.177014@1] UDP hash table entries: 256 (order: 1, 8192 bytes)
[    1.183016@1] UDP-Lite hash table entries: 256 (order: 1, 8192 bytes)
[    1.189630@1] NET: Registered protocol family 1
[    1.194119@1] Unpacking initramfs...
[    1.255973@1] Freeing initrd memory: 1596K
[    1.257089@1] highmem bounce pool size: 64 pages
[    1.259283@1] ashmem: initialized
[    1.272853@1] NTFS driver 2.1.30 [Flags: R/O].
[    1.273253@1] fuse init (API version 7.16)
[    1.276331@1] msgmni has been set to 632
[    1.280534@1] io scheduler noop registered
[    1.283762@1] io scheduler deadline registered (default)
[    1.363078@1] HDMI: EDID Ready
[    1.363143@1] CEC: Physical address: 0x1000
[    1.364660@1] CEC: Physical address: 1.0.0.0
[    1.368921@1] hdmitx: edid: found IEEEOUT
[    1.373210@1] hdmitx: stat: hdmitx: misc cmd 0x15000000
[    1.378653@1] HDMI: get current mode: 720p
[    1.382578@1] hdmitx: stat: hdmitx: misc cmd 0x15000000
[    1.387782@1] hdmitx: already init VIC = 0  Now VIC = 4
[    1.393031@1] set mode VIC 4 (cd0,cs0,pm1,vd0,1) 
[    1.397715@1] HDMI DEBUG: hdmi_hw_reset [1386]
[    1.402086@1] HDMI: get brd phy data
[    1.405665@1] hdmi phy setting
[    1.417219@1] HDMI: get brd phy data
[    1.417245@1] hdmi phy setting
[    1.418239@1] HDMI DEBUG: hdmitx_set_pll [2069]
[    1.423070@1] param->VIC:4
[    1.425710@1] mode is: 6
[    1.428196@1] VPU_VIU_VENC_MUX_CTRL: 0xa
[    1.432087@1] viu chan = 1
[    1.434857@1] VPU_VIU_VENC_MUX_CTRL: 0xa
[    1.466631@0] Sink is HDMI device
[    1.466665@0] hdmitx: config: hdmitx: conf cmd 0x14000002
[    1.469729@0] Source in HDMI Mode
[    1.473431@0] CEC not ready
[    1.514585@1] loop: module loaded
[    1.516673@1] PPP generic driver version 2.4.2
[    1.517149@1] PPP Deflate Compression module registered
[    1.521902@1] PPP BSD Compression module registered
[    1.527160@1] PPP MPPE Compression module registered
[    1.531700@1] NET: Registered protocol family 24
[    1.536359@1] tun: Universal TUN/TAP device driver, 1.6
[    1.541507@1] tun: (C) 1999-2004 Max Krasnyansky <maxk@qualcomm.com>
[    1.548461@1] usbcore: registered new interface driver asix
[    1.553533@1] usbcore: registered new interface driver cdc_ether
[    1.559477@1] usbcore: registered new interface driver net1080
[    1.565310@1] usbcore: registered new interface driver cdc_subset
[    1.571366@1] usbcore: registered new interface driver zaurus
[    1.577033@1] cdc_ncm: 04-Aug-2011
[    1.580485@1] usbcore: registered new interface driver cdc_ncm
[    1.586310@1] usbcore: registered new interface driver qf9700
[    1.592019@1] usbcore: registered new interface driver sr9600
[    1.592757@0] HDMI: audio channel num is 0
[    1.592773@0] current VIC: 4
[    1.592777@0] audio sample rate: 0
[    1.592782@0] HDMI: reset audio N para
[    1.592791@0] PCM out to HDMI
[    1.605659@0] Time out: AIU_HDMI_CLK_DATA_CTRL
[    1.605664@0] i2s_to_spdif_flag:1 
[    1.605667@0] Enable audio spdif to HDMI
[    1.605716@0] HDMI: set audio param
[    1.630244@1] usbcore: registered new interface driver cdc_acm
[    1.635696@1] cdc_acm: USB Abstract Control Model driver for USB modems and ISDN adapters
[    1.643851@1] Initializing USB Mass Storage driver...
[    1.648999@1] usbcore: registered new interface driver usb-storage
[    1.655050@1] USB Mass Storage support registered.
[    1.660016@1] usbcore: registered new interface driver usbserial
[    1.665815@1] usbserial: USB Serial Driver core
[    1.670403@1] USB Serial support registered for GSM modem (1-port)
[    1.676585@1] usbcore: registered new interface driver option
[    1.682209@1] option: v0.7.2:USB Driver for GSM modems
[    1.688046@1] mousedev: PS/2 mouse device common for all mice
[    1.694241@1] usbcore: registered new interface driver iforce
[    1.699835@1] usbcore: registered new interface driver xpad
[    1.705306@1] i2c /dev entries driver
[    1.709025@1] lirc_dev: IR Remote Control driver registered, major 250 
[    1.714605@1] IR NEC protocol handler initialized
[    1.719273@1] IR RC5(x) protocol handler initialized
[    1.724228@1] IR RC6 protocol handler initialized
[    1.728907@1] IR JVC protocol handler initialized
[    1.733603@1] IR Sony protocol handler initialized
[    1.738368@1] IR RC5 (streamzap) protocol handler initialized
[    1.744104@1] IR LIRC bridge handler initialized
[    1.748698@1] Linux video capture interface: v2.00
[    1.753696@1] usbcore: registered new interface driver uvcvideo
[    1.759374@1] USB Video Class driver (v1.1.0)
[    1.764519@1] device-mapper: uevent: version 1.0.3
[    1.769116@1] device-mapper: ioctl: 4.20.0-ioctl (2011-02-02) initialised: dm-devel@redhat.com
[    1.777361@1] cpuidle: using governor ladder
[    1.781335@1] cpuidle: using governor menu
[    1.785860@1] usbcore: registered new interface driver usbhid
[    1.791143@1] usbhid: USB HID core driver
[    1.795886@1] logger: created 256K log 'log_main'
[    1.800057@1] logger: created 256K log 'log_events'
[    1.804942@1] logger: created 256K log 'log_radio'
[    1.809683@1] logger: created 256K log 'log_system'
[    1.814438@1] vout_init_module
[    1.817397@1] start init vout module 
[    1.821187@1] create  vout attribute ok 
[    1.825213@1] ge2d_init
[    1.827518@1] ge2d_dev major:249
[    1.831224@1] ge2d start monitor
[    1.834166@1] osd_init
[    1.836489@1] vmode has setted in aml logo module
[    1.841244@1] request_fiq:152: fiq=35
[    1.844888@1] ge2d workqueue monitor start
[    1.849049@1] request_fiq:186: end
[    1.852427@1] [osd0] 0x84100000-0x850fffff
[    1.856738@1] Frame buffer memory assigned at phy:0x84100000, vir:0xe1000000, size=16384K
[    1.864677@1] ---------------clear framebuffer0 memory  
[    1.892597@0] [osd1] 0x85100000-0x851fffff
[    1.892648@0] Frame buffer memory assigned at phy:0x85100000, vir:0xe0200000, size=1024K
[    1.899137@0] ---------------clear framebuffer1 memory  
[    1.925876@0] osd probe ok  
[    1.932543@0] amlvideo-000: V4L2 device registered as video10
[    1.933332@0]  set pinmux c08f48d4
[    1.936032@0]  set pinmux c08f48dc
[    2.032825@0] UART_ttyS0:(irq = 122)
[    2.192758@0] UART_ttyS3:(irq = 125)
[    2.192913@0] dwc_otg: version 2.94a 05-DEC-2012
[    2.572726@1] hdmitx: ddc: cmd 0x10000002
[    2.572755@1] HDMITX: no HDCP key available
[    2.575259@1] hdmitx: ddc: cmd 0x10000002
[    2.579256@1] HDMITX: no HDCP key available
[    2.695370@0] USB (0) use clock source: XTAL input
[    2.896434@0] Core Release: 2.94a
[    2.896458@0] Setting default values for core params
[    3.299184@0] Using Buffer DMA mode
[    3.299207@0] OTG VER PARAM: 1, OTG VER FLAG: 1
[    3.301542@0] Working on port type = OTG
[    3.305470@0] Current port type: SLAVE
[    3.309244@0] dwc_otg lm0: DWC OTG Controller
[    3.313638@0] dwc_otg lm0: new USB bus registered, assigned bus number 1
[    3.320225@0] dwc_otg lm0: irq 62, io mem 0x00000000
[    3.325989@0] hub 1-0:1.0: USB hub found
[    3.329067@0] hub 1-0:1.0: 1 port detected
[    3.333481@0] Dedicated Tx FIFOs mode
[    3.336912@0] using timer detect id change, df80a400
[    3.442746@1] HOST mode
[    3.541937@0] Core Release: 2.94a
[    3.541959@0] Setting default values for core params
[    3.642504@1] Using Buffer DMA mode
[    3.642527@1] OTG VER PARAM: 1, OTG VER FLAG: 1
[    3.695776@1] Init: Port Power? op_state=1
[    3.695798@1] Init: Power Port (0)
[    3.697610@1] set usb port power on (board gpio 25)!
[    3.922740@1] Indeed it is in host mode hprt0 = 00021501
[    3.944761@0] Using Buffer DMA mode
[    3.944783@0] OTG VER PARAM: 1, OTG VER FLAG: 1
[    3.947116@0] Working on port type = HOST
[    3.951166@0] dwc_otg lm1: DWC OTG Controller
[    3.955511@0] dwc_otg lm1: new USB bus registered, assigned bus number 2
[    3.962151@0] dwc_otg lm1: irq 63, io mem 0x00000000
[    3.970166@0] Init: Port Power? op_state=1
[    3.971159@0] Init: Power Port (0)
[    3.975354@0] hub 2-0:1.0: USB hub found
[    3.978460@0] hub 2-0:1.0: 1 port detected
[    3.982992@0] Amlogic nand flash Kernel driver, Version K1.06.018 (c) 2010 Amlogic Inc.
[    3.990525@0] ####Version of Uboot must be newer than U1.06.011!!!!! 
[    3.997031@0] 2
[    3.998677@0] SPI BOOT, m3_nand_probe continue i 0
[    4.003481@0] chip->controller=c0a6b964
[    4.007268@0] checking ChiprevD :0
[    4.010653@0] aml_nand_probe checked chiprev:0
[    4.015121@0] init bus_cycle=17, bus_timing=10, start_cycle=10, end_cycle=10,system=5.0ns
[    4.023695@0] No NAND device found.
[    4.026950@0] NAND device id: ad d7 94 91 60 44 
[    4.031314@0] aml_chip->hynix_new_nand_type =: 4 
[    4.036030@0] NAND device: Manufacturer ID: 0xad, Chip ID: 0xad (Hynix C revision 20nm NAND 4GiB H27UBG8T2C)
[    4.045975@0] #####aml_nand_init, with RB pins and chip->chip_delay:20
[    4.052322@0] bus_cycle=4, bus_timing=5, start_cycle=5, end_cycle=6,system=5.0ns
[    4.059730@0] oob size is not enough for selected bch mode: NAND_BCH60_1K_MODE force bch to mode: NAND_BCH40_1K_MODE
[    4.070222@0] aml_chip->oob_fill_cnt =64,aml_chip->oob_size =640,bch_bytes =70
[    4.077444@0] multi plane error for selected plane mode: NAND_TWO_PLANE_MODE force plane to : NAND_SINGLE_PLANE_MODE
[    4.087934@0]  oob layout use nand base oob layout oobsize = 16,oobmul =1,mtd->oobsize =640,aml_chip->oob_size =640
[    4.099513@0] aml_nand_get_read_default_value_hynix 913 get default reg value at blk:0, page:7
[    4.102739@1] usb 1-1: new high speed USB device number 2 using dwc_otg
[    4.113582@1] Indeed it is in host mode hprt0 = 00001101
[    4.113614@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb0):    value:0x3c, for chip[0]
[    4.113622@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb1):    value:0x36, for chip[0]
[    4.113630@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb2):    value:0x5c, for chip[0]
[    4.113637@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb3):    value:0xa2, for chip[0]
[    4.113644@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb4):    value:0x40, for chip[0]
[    4.113652@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb5):    value:0x39, for chip[0]
[    4.113659@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb6):    value:0x50, for chip[0]
[    4.113666@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb7):    value:0x90, for chip[0]
[    4.113673@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb0):    value:0x3a, for chip[0]
[    4.113681@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb1):    value:0x39, for chip[0]
[    4.113688@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb2):    value:0x55, for chip[0]
[    4.113695@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb3):    value:0x9b, for chip[0]
[    4.113702@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb4):    value:0x3e, for chip[0]
[    4.113710@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb5):    value:0x3c, for chip[0]
[    4.113717@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb6):    value:0x49, for chip[0]
[    4.113724@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb7):    value:0x89, for chip[0]
[    4.113731@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb0):    value:0x38, for chip[0]
[    4.113738@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb1):    value:0x38, for chip[0]
[    4.113746@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb2):    value:0x52, for chip[0]
[    4.113753@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb3):    value:0x9d, for chip[0]
[    4.113760@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb4):    value:0x3c, for chip[0]
[    4.113767@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb5):    value:0x3b, for chip[0]
[    4.113774@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb6):    value:0x46, for chip[0]
[    4.113782@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb7):    value:0x8b, for chip[0]
[    4.113789@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb0):    value:0x34, for chip[0]
[    4.113796@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb1):    value:0x36, for chip[0]
[    4.113803@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb2):    value:0x4f, for chip[0]
[    4.113810@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb3):    value:0x9a, for chip[0]
[    4.113818@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb4):    value:0x38, for chip[0]
[    4.113825@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb5):    value:0x39, for chip[0]
[    4.113832@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb6):    value:0x43, for chip[0]
[    4.113839@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb7):    value:0x88, for chip[0]
[    4.113847@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb0):    value:0x2d, for chip[0]
[    4.113854@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb1):    value:0x34, for chip[0]
[    4.113861@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb2):    value:0x4b, for chip[0]
[    4.113869@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb3):    value:0x96, for chip[0]
[    4.113876@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb4):    value:0x31, for chip[0]
[    4.113883@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb5):    value:0x37, for chip[0]
[    4.113890@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb6):    value:0x3f, for chip[0]
[    4.113897@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb7):    value:0x84, for chip[0]
[    4.113905@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb0):    value:0x23, for chip[0]
[    4.113912@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb1):    value:0x32, for chip[0]
[    4.113919@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb2):    value:0x47, for chip[0]
[    4.113926@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb3):    value:0x93, for chip[0]
[    4.113934@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb4):    value:0x27, for chip[0]
[    4.113941@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb5):    value:0x35, for chip[0]
[    4.113948@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb6):    value:0x3b, for chip[0]
[    4.113955@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb7):    value:0x81, for chip[0]
[    4.113962@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb0):    value:0x19, for chip[0]
[    4.113970@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb1):    value:0x25, for chip[0]
[    4.113977@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb2):    value:0x3b, for chip[0]
[    4.113984@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb3):    value:0x83, for chip[0]
[    4.113991@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb4):    value:0x1d, for chip[0]
[    4.113998@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb5):    value:0x28, for chip[0]
[    4.114006@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb6):    value:0x2f, for chip[0]
[    4.114013@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb7):    value:0x71, for chip[0]
[    4.121250@0] aml nand env valid addr: 418000 
[    4.153583@0] nand env: nand_env_probe. 
[    4.628413@0] nand key: nand_key_probe. 
[    4.631717@0] key start_blk=2040,end_blk=2047,aml_nand_key_init:651
[    4.647406@0] aml nand key valid addr: ff000000 
[    4.647430@0] aml nand key valid addr: ff200000 
[    4.650976@0] aml nand key valid addr: ff400000 
[    4.655619@0] aml nand key valid addr: ff600000 
[    4.660218@0] i=0,register --- nand_key
[    4.664156@0] Creating 8 MTD partitions on "C revision 20nm NAND 4GiB H27UBG8T2C":
[    4.671553@0] 0x000000c00000-0x000001400000 : "logo"
[    4.677831@0] 0x000001400000-0x000001c00000 : "aml_logo"
[    4.683181@0] 0x000001c00000-0x000002400000 : "recovery"
[    4.688738@0] 0x000002400000-0x000008c00000 : "boot"
[    4.693146@0] 0x000008c00000-0x000048c00000 : "system"
[    4.698214@0] 0x000048c00000-0x000068c00000 : "cache"
[    4.703280@0] 0x000068c00000-0x000078c00000 : "backup"
[    4.708363@0] 0x000078c00000-0x0000ff000000 : "data"
[    4.713713@0] init_aml_nftl start
[    4.715563@0] mtd->name: system
[    4.718685@0] nftl version 140415a
[    4.722113@0] nftl part attr 0
[    4.725274@0] nftl start:512,64
[    4.729365@0] first
[    4.826104@1] scsi0 : usb-storage 1-1:1.0
[    4.833422@1] Indeed it is in host mode hprt0 = 00021501
[    4.874312@0] average_erase_count:0
[    4.874348@0] second 140,448
[    4.923334@0] current used block :371
[    4.923357@0] current_block1:371
[    4.970959@0] free block cnt = 372
[    4.970980@0] new current block is 373
[    4.972790@0] prio_garbage_collect cannot find PHY_MAPPING_PAGE ffffffff !!
[    5.012741@1] usb 2-1: new high speed USB device number 2 using dwc_otg
[    5.013962@1] Indeed it is in host mode hprt0 = 00001101
[    5.215347@1] hub 2-1:1.0: USB hub found
[    5.215677@1] hub 2-1:1.0: 4 ports detected
[    5.229745@0] prio_garbage_collect_no_last_page put_phy_block_to_free_list!
[    5.482260@0] recover_current_block_mapping : fill the current block, from page 255
[    5.484309@0] nftl ok!
[    5.486955@0] aml_nftl_blk->mbd.tr.name =system
[    5.491814@1] aml_nftl_init_bounce_buf, use cache here
[    5.496684@0] usb 2-1.3: new high speed USB device number 3 using dwc_otg
[    5.503075@0]  system: unknown partition table
[    5.507841@0] _nftl_init_bounce_buf already init 1000
[    5.512527@0] aml_nftl_add_mtd ok
[    5.515849@0] mtd->name: cache
[    5.518829@0] nftl version 140415a
[    5.522232@0] nftl part attr 0
[    5.525350@0] nftl start:256,32
[    5.528886@0] first
[    5.605037@0] average_erase_count:0
[    5.605068@0] second 7,224
[    5.608120@0] scsi1 : usb-storage 2-1.3:1.0
[    5.610951@0] current used block :248
[    5.613441@0] current_block1:248
[    5.622850@0] free block cnt = 249
[    5.622870@0] new current block is 250
[    5.624666@0] prio_garbage_collect cannot find PHY_MAPPING_PAGE ffffffff !!
[    5.738545@0] prio_garbage_collect_no_last_page put_phy_block_to_free_list!
[    5.834847@1] scsi 0:0:0:0: Direct-Access     Initio   INIC-3609        0213 PQ: 0 ANSI: 6
[    5.838413@1] sd 0:0:0:0: [sda] 976773167 512-byte logical blocks: (500 GB/465 GiB)
[    5.846489@1] sd 0:0:0:0: [sda] Write Protect is off
[    5.850591@1] sd 0:0:0:0: [sda] Write cache: disabled, read cache: enabled, supports DPO and FUA
[    5.883195@1]  sda: sda1
[    5.894360@1] sd 0:0:0:0: [sda] Attached SCSI disk
[    6.130907@0] recover_current_block_mapping : fill the current block, from page 255
[    6.132955@0] nftl ok!
[    6.135597@0] aml_nftl_blk->mbd.tr.name =cache
[    6.140383@1] aml_nftl_init_bounce_buf, use cache here
[    6.145244@0]  cache: unknown partition table
[    6.149426@1] _nftl_init_bounce_buf already init 1000
[    6.154200@1] aml_nftl_add_mtd ok
[    6.157488@1] mtd->name: backup
[    6.160616@1] nftl version 140415a
[    6.164015@1] nftl part attr 0
[    6.167053@1] nftl start:128,16
[    6.170380@1] first
[    6.209297@1] average_erase_count:0
[    6.209321@1] second 1,112
[    6.210190@1] all block full!!
[    6.212885@1] free block cnt = 127
[    6.216259@1] new current block is 126
[    6.219990@1] nftl ok!
[    6.222723@1] aml_nftl_blk->mbd.tr.name =backup
[    6.227464@1] aml_nftl_init_bounce_buf, use cache here
[    6.232358@1]  backup: unknown partition table
[    6.236711@1] _nftl_init_bounce_buf already init 1000
[    6.241430@1] aml_nftl_add_mtd ok
[    6.244744@1] mtd->name: data
[    6.247685@1] nftl version 140415a
[    6.251154@1] nftl part attr 0
[    6.254234@1] nftl start:1074,134
[    6.259186@1] first
[    6.568737@1] average_erase_count:0
[    6.568781@1] second 53,940
[    6.587660@1] current used block :1020
[    6.587683@1] current_block1:1020
[    6.604095@0] scsi 1:0:0:0: Direct-Access     Kingston DT 101 G2        PMAP PQ: 0 ANSI: 0 CCS
[    6.609018@1] sd 1:0:0:0: [sdb] 7819264 512-byte logical blocks: (4.00 GB/3.72 GiB)
[    6.616761@1] sd 1:0:0:0: [sdb] Write Protect is off
[    6.621635@0] sd 1:0:0:0: [sdb] No Caching mode page present
[    6.625339@0] sd 1:0:0:0: [sdb] Assuming drive cache: write through
[    6.637757@0] sd 1:0:0:0: [sdb] No Caching mode page present
[    6.637793@0] sd 1:0:0:0: [sdb] Assuming drive cache: write through
[    6.645159@0]  sdb: sdb1
[    6.652010@0] sd 1:0:0:0: [sdb] No Caching mode page present
[    6.652186@0] sd 1:0:0:0: [sdb] Assuming drive cache: write through
[    6.652345@1] free block cnt = 1023
[    6.652352@1] new current block is 1053
[    6.652741@1] prio_garbage_collect cannot find PHY_MAPPING_PAGE ffffffff !!
[    6.672765@0] sd 1:0:0:0: [sdb] Attached SCSI removable disk
[    6.906974@1] prio_garbage_collect_no_last_page put_phy_block_to_free_list!
[    7.151293@1] recover_current_block_mapping : fill the current block, from page 255
[    7.153309@1] nftl ok!
[    7.155986@1] aml_nftl_blk->mbd.tr.name =data
[    7.160623@1] aml_nftl_init_bounce_buf, use cache here
[    7.165572@1]  data: unknown partition table
[    7.169650@1] _nftl_init_bounce_buf already init 1000
[    7.174407@1] aml_nftl_add_mtd ok
[    7.177695@1] init_aml_nftl end
[    7.180819@1] ethernetinit(dbg[c08dc64c]=1)
[    7.185029@1] ethernet base addr is f3610000
[    7.189236@1] set_phy_mode() phy_Identifier: 0x0
[    7.193976@1] ethernet: MII PHY 0007c0f1h found at address 1, status 0x7829 advertising 01e1.
[    7.202423@1] find phy phy_Identifier=7c0f1
[    7.206526@1] *****WARNING: Haven't setup MAC address! Using random MAC address.
[    7.213895@1] mac-addr: 0:a1:6e:6a:f7:1f
[    7.217792@1] write mac add to:dfa20c48: 00 a1 6e 6a f7 1f |..nj.|
[    7.224450@1] eth0: mixed no checksumming and other settings.
[    7.230029@1] ethernet_driver probe!
[    7.233369@1] ****** aml_eth_pinmux_setup() ******
[    7.238106@1] ****** aml_eth_clock_enable() ******
[    7.243052@1] Amlogic A/V streaming port init
[    7.250366@1] amvideocap_register_memory 8e100000 6266880
[    7.252787@1] amvideocap_init
[    7.255989@1] amvideocap_init,0
[    7.258817@1] regist mpeg12 codec profile
[    7.262805@1] regist mpeg4 codec profile
[    7.266574@1] amvdec_vc1 module init
[    7.270242@1] regist vc1 codec profile
[    7.273933@1] amvdec_avs module init
[    7.277541@1] amvdec_h264 module init
[    7.281168@1] regist h264 codec profile
[    7.285125@1] regist mjpeg codec profile
[    7.288795@1] amvdec_real module init
[    7.292544@1] regist real codec profile
[    7.297243@1] request_fiq:152: fiq=35
[    7.299907@1] request_fiq:186: end
[    7.303979@1] SARADC Driver init.
[    7.306870@1] Remote Driver
[    7.309715@1] input: aml_keypad as /devices/platform/meson-remote/input/input0
[    7.317254@1] meson_remote_pinmux_setup()
[    7.320566@1] Remote platform_data g_remote_base=f3100480
[    7.325961@1] Remote date_valye======0,status == 8915d20
[    7.331266@1] remote config major:244
[    7.335446@1] physical address:0x9f0e6000
[    7.339067@1] ADC Keypad Driver init.
[    7.342814@1] Meson KeyInput init
[    7.345950@1] Key 116 registed.
[    7.349226@1] input: key_input as /devices/platform/meson-keyinput.0/input/input1
[    7.356903@1] Meson KeyInput register RTC interrupt
[    7.361209@1] Meson KeyInput major=243
[    7.366013@1]  spi_nor_probe 586
[    7.368305@1] SPI BOOT  : spi_nor_probe 591 
[    7.372577@1] spi_nor apollospi:0: mx25l3205d (4096 Kbytes)
[    7.378130@1] Creating 2 MTD partitions on "apollospi:0":
[    7.383532@1] 0x000000000000-0x000000060000 : "bootloader"
[    7.390201@1] 0x000000068000-0x000000070000 : "ubootenv"
[    7.395985@1] Memory Card media Major: 253
[    7.398483@1] card max_req_size is 128K 
[    7.403096@1] card creat process sucessful
[    7.406359@1] 
[    7.406361@1] SD/MMC initialization started......
[    8.042731@1] mmc data3 pull high
[    8.042949@0] sd_mmc_info->card_type=0
[    8.044132@0] begin SDIO check ......
[    8.070353@0] sdio_timeout_int_times = 0; timeout = 498
[    8.092946@0] sdio_timeout_int_times = 0; timeout = 497
[    8.092977@0] SEND OP timeout @1
[    8.095734@0] mmc data3 pull high
[    8.099221@0] begin SD&SDHC check ......
[    8.145292@0] sdio_timeout_int_times = 0; timeout = 498
[    8.145319@0] SEND IF timeout @2
[    8.170842@0] sdio_timeout_int_times = 0; timeout = 498
[    8.170872@0] begin MMC check ......
[    8.215296@0] sdio_timeout_int_times = 0; timeout = 498
[    8.215323@0] No any SD/MMC card detected!
[    8.218958@0] #SD_MMC_ERROR_DRIVER_FAILURE error occured in sd_voltage_validation()
[    8.226624@0] [card_force_init] unit_state 3
[    8.231021@0] [dsp]DSP start addr 0xc4000000
[    8.235164@0] [dsp]register dsp to char divece(232)
[    8.244772@0] DSP pcmenc stream buffer to [0x9e401000-0x9e601000]
[    8.246040@0] amlogic audio dsp pcmenc device init!
[    8.251775@0] amlogic audio spdif interface device init!
[    8.257422@0] using rtc device, aml_rtc, for alarms
[    8.260913@0] aml_rtc aml_rtc: rtc core: registered aml_rtc as rtc0
[    8.268188@0] gpio dev major number:240
[    8.272008@0] create gpio device success
[    8.275326@0] vdin_drv_init: major 238
[    8.279360@0] vdin0 mem_start = 0x87200000, mem_size = 0x2000000
[    8.284906@0] vdin.0 cnavas initial table:
[    8.288922@0]        128: 0x87200000-0x87a29000  3840x2228 (8356 KB)
[    8.294818@0]        129: 0x87a29000-0x88252000  3840x2228 (8356 KB)
[    8.300552@0]        130: 0x88252000-0x88a7b000  3840x2228 (8356 KB)
[    8.306410@0] vdin_drv_probe: driver initialized ok
[    8.311353@1] amvdec_656in module: init.
[    8.315143@1] amvdec_656in_init_module:major 237
[    8.319845@1] kobject (dfb30c10): tried to init an initialized object, something is seriously wrong.
[    8.328879@1] [<c0043b44>] (unwind_backtrace+0x0/0xf8) from [<c0256840>] (kobject_init+0x78/0x94)
[    8.337724@1] [<c0256840>] (kobject_init+0x78/0x94) from [<c0298098>] (device_initialize+0x28/0x6c)
[    8.346744@1] [<c0298098>] (device_initialize+0x28/0x6c) from [<c029c59c>] (platform_device_register+0x10/0x1c)
[    8.356811@1] [<c029c59c>] (platform_device_register+0x10/0x1c) from [<c00213bc>] (amvdec_656in_init_module+0xac/0x140)
[    8.367579@1] [<c00213bc>] (amvdec_656in_init_module+0xac/0x140) from [<c003770c>] (do_one_initcall+0xfc/0x164)
[    8.377666@1] [<c003770c>] (do_one_initcall+0xfc/0x164) from [<c0008988>] (kernel_init+0x98/0x13c)
[    8.386582@1] [<c0008988>] (kernel_init+0x98/0x13c) from [<c003e0c0>] (kernel_thread_exit+0x0/0x8)
[    8.396213@1] amvdec_656in probe ok.
[    8.399204@1] efuse===========================================
[    8.405385@1] efuse: device efuse created
[    8.408975@1] efuse--------------------------------------------
[    8.414840@1] keys===========================================
[    8.420503@1] keys_devno=eb00000
[    8.424178@1] efuse: device aml_keys created
[    8.427975@1] amlkeys=0
[    8.430484@1] platform_driver_register--aml_keys_driver--------------------
[    8.452726@1] 6amlogic audio data interface device init!
[    8.452759@1] aml_dvb_init 
[    8.455332@1] dvb_io_setup start
[    8.458506@1] DVB: registering new adapter (amlogic-dvb)
[    8.467806@1] DVB: async fifo 0 buf size 524288, flush size 262144
[    8.471022@1] DVB: async fifo 1 buf size 524288, flush size 262144
[    8.477720@1] [aml_fe..]aml_fe_probe ok.
[    8.480206@1] Smartcard: cannot get resource "smc0_reset"
[    8.485950@1] SMC CLK SOURCE - 200000KHz
[    8.489516@1] [***smc***] smartcard->state: 1
[    8.495538@1] aml_hw_crypto initialization.
[    8.498661@1] usbcore: registered new interface driver snd-usb-audio
[    8.505308@1] enter dummy_codec_audio_probe
[    8.509248@1] aml-pcm 0:playback preallocate_dma_buffer: area=ffd80000, addr=9eac0000, size=131072
[    8.518413@1] init controls
[    8.520014@1] iec958 0: preallocate dma buffer start=ffd00000, size=80000
[    8.527477@1] aml-pcm 1:capture preallocate_dma_buffer: area=ffce0000, addr=9eaa0000, size=65536
[    8.535574@1] asoc: dummy_codec <-> aml-dai0 mapping ok
[    8.542072@1] dummy codec control ALSA component registered!
[    8.546532@1] ALSA device list:
[    8.549522@1]   #0: AML-DUMMY-CODEC
[    8.553099@1] <--GT msg--><1> /proc/gt82x_dbg created
[    8.558275@1] GACT probability NOT on
[    8.561680@1] Mirror/redirect action on
[    8.565514@1] u32 classifier
[    8.568357@1]     Actions configured
[    8.571919@1] Netfilter messages via NETLINK v0.30.
[    8.576828@1] nf_conntrack version 0.5.0 (13252 buckets, 53008 max)
[    8.583695@1] ctnetlink v0.93: registering with nfnetlink.
[    8.588528@1] NF_TPROXY: Transparent proxy support initialized, version 4.1.0
[    8.595656@1] NF_TPROXY: Copyright (c) 2006-2007 BalaBit IT Ltd.
[    8.602266@1] xt_time: kernel timezone is -0000
[    8.606288@1] ip_tables: (C) 2000-2006 Netfilter Core Team
[    8.611703@1] arp_tables: (C) 2002 David S. Miller
[    8.616418@1] TCP cubic registered
[    8.620795@1] NET: Registered protocol family 10
[    8.625086@1] Mobile IPv6
[    8.626985@1] ip6_tables: (C) 2000-2006 Netfilter Core Team
[    8.632609@1] IPv6 over IPv4 tunneling driver
[    8.638062@1] NET: Registered protocol family 17
[    8.641473@1] NET: Registered protocol family 15
[    8.646228@1] Bridge firewalling registered
[    8.650251@1] NET: Registered protocol family 35
[    8.655132@1] VFP support v0.3: implementor 41 architecture 3 part 30 variant 9 rev 4
[    8.662628@1] DDR low power is enable.
[    8.666445@1] enter meson_pm_probe!
[    8.669830@1] meson_pm_probe done !
[    8.673891@1] ------------[ cut here ]------------
[    8.678095@1] WARNING: at /home/mx/openlinux-jbmr1/common/kernel/smp.c:459 smp_call_function_many+0xc8/0x280()
[    8.688055@1] Modules linked in:
[    8.691288@1] [<c0043b44>] (unwind_backtrace+0x0/0xf8) from [<c0061f38>] (warn_slowpath_common+0x4c/0x64)
[    8.700823@1] [<c0061f38>] (warn_slowpath_common+0x4c/0x64) from [<c0061f6c>] (warn_slowpath_null+0x1c/0x24)
[    8.710629@1] [<c0061f6c>] (warn_slowpath_null+0x1c/0x24) from [<c008f6bc>] (smp_call_function_many+0xc8/0x280)
[    8.720700@1] [<c008f6bc>] (smp_call_function_many+0xc8/0x280) from [<c008f8a8>] (smp_call_function+0x34/0x68)
[    8.723028@0] usb 2-1.2: new high speed USB device number 4 using dwc_otg
[    8.737456@1] [<c008f8a8>] (smp_call_function+0x34/0x68) from [<c0049c2c>] (clk_set_rate_a9+0x9c/0xdc)
[    8.746739@1] [<c0049c2c>] (clk_set_rate_a9+0x9c/0xdc) from [<c0049f28>] (meson_clk_set_rate+0x130/0x168)
[    8.756286@1] [<c0049f28>] (meson_clk_set_rate+0x130/0x168) from [<c0049ff4>] (clk_set_rate+0x94/0xbc)
[    8.765576@1] [<c0049ff4>] (clk_set_rate+0x94/0xbc) from [<c0052d7c>] (meson_cpufreq_target_locked.isra.2+0x14c/0x224)
[    8.776250@1] [<c0052d7c>] (meson_cpufreq_target_locked.isra.2+0x14c/0x224) from [<c0052e78>] (meson_cpufreq_target+0x24/0x3c)
[    8.787627@1] [<c0052e78>] (meson_cpufreq_target+0x24/0x3c) from [<c0388a34>] (__cpufreq_driver_target+0x50/0x64)
[    8.797866@1] [<c0388a34>] (__cpufreq_driver_target+0x50/0x64) from [<c038b1e0>] (cpufreq_governor_performance+0x28/0x30)
[    8.808800@1] [<c038b1e0>] (cpufreq_governor_performance+0x28/0x30) from [<c0388b1c>] (__cpufreq_governor+0xb0/0x138)
[    8.819388@1] [<c0388b1c>] (__cpufreq_governor+0xb0/0x138) from [<c038997c>] (__cpufreq_set_policy+0x128/0x168)
[    8.829457@1] [<c038997c>] (__cpufreq_set_policy+0x128/0x168) from [<c0389c1c>] (cpufreq_add_dev_interface+0x260/0x2c8)
[    8.840219@1] [<c0389c1c>] (cpufreq_add_dev_interface+0x260/0x2c8) from [<c038a140>] (cpufreq_add_dev+0x4bc/0x634)
[    8.850554@1] [<c038a140>] (cpufreq_add_dev+0x4bc/0x634) from [<c02996bc>] (sysdev_driver_register+0xb0/0x12c)
[    8.860535@1] [<c02996bc>] (sysdev_driver_register+0xb0/0x12c) from [<c0388fe8>] (cpufreq_register_driver+0x70/0x17c)
[    8.871125@1] [<c0388fe8>] (cpufreq_register_driver+0x70/0x17c) from [<c029bf3c>] (platform_drv_probe+0x18/0x1c)
[    8.881279@1] [<c029bf3c>] (platform_drv_probe+0x18/0x1c) from [<c029ad30>] (driver_probe_device+0x90/0x1ac)
[    8.891086@1] [<c029ad30>] (driver_probe_device+0x90/0x1ac) from [<c029aed8>] (__driver_attach+0x8c/0x90)
[    8.900633@1] [<c029aed8>] (__driver_attach+0x8c/0x90) from [<c0299f50>] (bus_for_each_dev+0x5c/0x88)
[    8.909834@1] [<c0299f50>] (bus_for_each_dev+0x5c/0x88) from [<c029a714>] (bus_add_driver+0x17c/0x244)
[    8.919121@1] [<c029a714>] (bus_add_driver+0x17c/0x244) from [<c029b384>] (driver_register+0x78/0x13c)
[    8.928410@1] [<c029b384>] (driver_register+0x78/0x13c) from [<c029c2ec>] (platform_driver_probe+0x18/0x9c)
[    8.938139@1] [<c029c2ec>] (platform_driver_probe+0x18/0x9c) from [<c003770c>] (do_one_initcall+0xfc/0x164)
[    8.947856@1] [<c003770c>] (do_one_initcall+0xfc/0x164) from [<c0008988>] (kernel_init+0x98/0x13c)
[    8.956796@1] [<c0008988>] (kernel_init+0x98/0x13c) from [<c003e0c0>] (kernel_thread_exit+0x0/0x8)
[    8.965750@1] ---[ end trace 40c11f27e5d4c63b ]---
[    8.970504@1] ------------[ cut here ]------------
[    8.975285@1] WARNING: at /home/mx/openlinux-jbmr1/common/kernel/smp.c:320 smp_call_function_single+0x150/0x1c0()
[    8.985518@1] Modules linked in:
[    8.988741@1] [<c0043b44>] (unwind_backtrace+0x0/0xf8) from [<c0061f38>] (warn_slowpath_common+0x4c/0x64)
[    8.998284@1] [<c0061f38>] (warn_slowpath_common+0x4c/0x64) from [<c0061f6c>] (warn_slowpath_null+0x1c/0x24)
[    9.008093@1] [<c0061f6c>] (warn_slowpath_null+0x1c/0x24) from [<c008f584>] (smp_call_function_single+0x150/0x1c0)
[    9.018423@1] [<c008f584>] (smp_call_function_single+0x150/0x1c0) from [<c008f8a8>] (smp_call_function+0x34/0x68)
[    9.028667@1] [<c008f8a8>] (smp_call_function+0x34/0x68) from [<c0049c2c>] (clk_set_rate_a9+0x9c/0xdc)
[    9.037951@1] [<c0049c2c>] (clk_set_rate_a9+0x9c/0xdc) from [<c0049f28>] (meson_clk_set_rate+0x130/0x168)
[    9.047499@1] [<c0049f28>] (meson_clk_set_rate+0x130/0x168) from [<c0049ff4>] (clk_set_rate+0x94/0xbc)
[    9.056788@1] [<c0049ff4>] (clk_set_rate+0x94/0xbc) from [<c0052d7c>] (meson_cpufreq_target_locked.isra.2+0x14c/0x224)
[    9.067463@1] [<c0052d7c>] (meson_cpufreq_target_locked.isra.2+0x14c/0x224) from [<c0052e78>] (meson_cpufreq_target+0x24/0x3c)
[    9.078836@1] [<c0052e78>] (meson_cpufreq_target+0x24/0x3c) from [<c0388a34>] (__cpufreq_driver_target+0x50/0x64)
[    9.089077@1] [<c0388a34>] (__cpufreq_driver_target+0x50/0x64) from [<c038b1e0>] (cpufreq_governor_performance+0x28/0x30)
[    9.100013@1] [<c038b1e0>] (cpufreq_governor_performance+0x28/0x30) from [<c0388b1c>] (__cpufreq_governor+0xb0/0x138)
[    9.110602@1] [<c0388b1c>] (__cpufreq_governor+0xb0/0x138) from [<c038997c>] (__cpufreq_set_policy+0x128/0x168)
[    9.120670@1] [<c038997c>] (__cpufreq_set_policy+0x128/0x168) from [<c0389c1c>] (cpufreq_add_dev_interface+0x260/0x2c8)
[    9.131433@1] [<c0389c1c>] (cpufreq_add_dev_interface+0x260/0x2c8) from [<c038a140>] (cpufreq_add_dev+0x4bc/0x634)
[    9.141763@1] [<c038a140>] (cpufreq_add_dev+0x4bc/0x634) from [<c02996bc>] (sysdev_driver_register+0xb0/0x12c)
[    9.151746@1] [<c02996bc>] (sysdev_driver_register+0xb0/0x12c) from [<c0388fe8>] (cpufreq_register_driver+0x70/0x17c)
[    9.162337@1] [<c0388fe8>] (cpufreq_register_driver+0x70/0x17c) from [<c029bf3c>] (platform_drv_probe+0x18/0x1c)
[    9.172492@1] [<c029bf3c>] (platform_drv_probe+0x18/0x1c) from [<c029ad30>] (driver_probe_device+0x90/0x1ac)
[    9.182299@1] [<c029ad30>] (driver_probe_device+0x90/0x1ac) from [<c029aed8>] (__driver_attach+0x8c/0x90)
[    9.191847@1] [<c029aed8>] (__driver_attach+0x8c/0x90) from [<c0299f50>] (bus_for_each_dev+0x5c/0x88)
[    9.201048@1] [<c0299f50>] (bus_for_each_dev+0x5c/0x88) from [<c029a714>] (bus_add_driver+0x17c/0x244)
[    9.210335@1] [<c029a714>] (bus_add_driver+0x17c/0x244) from [<c029b384>] (driver_register+0x78/0x13c)
[    9.219624@1] [<c029b384>] (driver_register+0x78/0x13c) from [<c029c2ec>] (platform_driver_probe+0x18/0x9c)
[    9.229347@1] [<c029c2ec>] (platform_driver_probe+0x18/0x9c) from [<c003770c>] (do_one_initcall+0xfc/0x164)
[    9.239066@1] [<c003770c>] (do_one_initcall+0xfc/0x164) from [<c0008988>] (kernel_init+0x98/0x13c)
[    9.248007@1] [<c0008988>] (kernel_init+0x98/0x13c) from [<c003e0c0>] (kernel_thread_exit+0x0/0x8)
[    9.256943@1] ---[ end trace 40c11f27e5d4c63c ]---
[    9.269144@1] android_usb gadget: Mass Storage Function, version: 2009/09/11
[    9.270554@1] android_usb gadget: Number of LUNs=2
[    9.275360@1]  lun0: LUN: removable file: (no medium)
[    9.280362@1]  lun1: LUN: removable file: (no medium)
[    9.286101@1] android_usb gadget: android_usb ready
[    9.290722@1] aml_rtc aml_rtc: setting system clock to 1970-01-02 00:27:16 UTC (88036)
[    9.298287@1] ------------[ cut here ]------------
[    9.302957@1] WARNING: at /home/mx/openlinux-jbmr1/common/fs/proc/generic.c:586 proc_register+0xec/0x1b4()
[    9.312565@1] proc_dir_entry '/proc/gt82x_dbg' already registered
[    9.318648@1] Modules linked in:
[    9.321873@1] [<c0043b44>] (unwind_backtrace+0x0/0xf8) from [<c0061f38>] (warn_slowpath_common+0x4c/0x64)
[    9.331419@1] [<c0061f38>] (warn_slowpath_common+0x4c/0x64) from [<c0061fe4>] (warn_slowpath_fmt+0x30/0x40)
[    9.341136@1] [<c0061fe4>] (warn_slowpath_fmt+0x30/0x40) from [<c0119ca8>] (proc_register+0xec/0x1b4)
[    9.350338@1] [<c0119ca8>] (proc_register+0xec/0x1b4) from [<c011a068>] (create_proc_entry+0x68/0xb4)
[    9.359542@1] [<c011a068>] (create_proc_entry+0x68/0xb4) from [<c062f6f8>] (goodix_ts_init+0x58/0xdc)
[    9.368750@1] [<c062f6f8>] (goodix_ts_init+0x58/0xdc) from [<c003770c>] (do_one_initcall+0xfc/0x164)
[    9.377855@1] [<c003770c>] (do_one_initcall+0xfc/0x164) from [<c0008988>] (kernel_init+0x98/0x13c)
[    9.386794@1] [<c0008988>] (kernel_init+0x98/0x13c) from [<c003e0c0>] (kernel_thread_exit+0x0/0x8)
[    9.395728@1] ---[ end trace 40c11f27e5d4c63e ]---
[    9.400496@1] <--GT msg--><1> /proc/gt82x_dbg created
[    9.405541@1] Error: Driver 'Goodix-TS' is already registered, aborting...
[    9.412385@1] CEC init
[    9.414977@1] CEC: CEC task process
[    9.418218@9[    9.442732@1] Changing baud from 0 to 115200
[    9.502784@1] Freeing init memory: 188K
[   10.526513@0] init: hdmi hpd_status is :49
[   10.526556@0] init: ===== resolution=
[   10.528448@0] init: ===== cvbsmode=
[   10.531918@0] init: ===== hdmimode=osd0=>x:0 ,y:0,w:1280,h:720
[   10.542569@0]  osd1=> x:0,y:0,w:18,h:18 
[   10.576065@0] init: cannot open '/initlogo.rle'
[   10.576273@0] init: load_565rle_image_mbx result is: 0
[   10.626082@0] osd0 free scale ENABLE
[   10.626113@0] vf_reg_provider:osd
[   10.652714@1] <FIQ>:vf_ext_light_unreg_provide
[   10.652718@1] 0
[   10.659830@0] init: command 'loglevel' r=0
[   10.663875@0] init: command 'export' r=0
[   10.667747@0] init: command 'export' r=0
[   10.671645@0] init: command 'export' r=0
[   10.675582@0] init: command 'export' r=0
[   10.679460@0] init: command 'export' r=0
[   10.683391@0] init: command 'export' r=0
[   10.687269@0] init: command 'export' r=0
[   10.691184@0] init: command 'symlink' r=-1
[   10.695346@0] init: command 'mkdir' r=0
[   10.699092@0] init: command 'mkdir' r=0
[   10.702943@0] init: command 'mkdir' r=0
[   10.706726@0] init: mount /tmp to target failed
[   10.711226@0] init: command 'mount' r=-1
[   10.715162@0] init: processing action 0x75e48 (property_service_init)
[   10.721637@0] init: Unable to open persistent property directory /data/property errno: 2
[   10.729743@0] init: Created socket '/dev/socket/property_service' with mode '666', user '0', group '0'
[   10.738960@0] init: command 'property_service_init' r=0
[   10.744158@0] init: processing action 0x75e90 (signal_init)
[   10.749712@0] init: command 'signal_init' r=0
[   10.754074@0] init: processing action 0x75ed8 (check_startup)
[   10.759772@0] init: command 'check_startup' r=0
[   10.764297@0] init: processing action 0x75f20 (queue_property_triggers)
[   10.770867@0] init: command 'queue_property_triggers' r=0
[   10.776269@0] init: processing action 0x75f68 (ubootenv_init)
[   10.782055@0] init: mtd partition 0, logo
[   10.785995@0] init: mtd partition 1, aml_logo
[   10.790311@0] init: mtd partition 2, recovery
[   10.794681@0] init: mtd partition 3, boot
[   10.798634@0] init: mtd partition 4, system
[   10.802850@0] init: mtd partition 5, cache
[   10.806879@0] init: mtd partition 6, backup
[   10.811050@0] init: mtd partition 7, data
[   10.815066@0] init: mtd partition 8, bootloader
[   10.819553@0] init: mtd partition 9, ubootenv
[   10.842294@0] init: property_changed: property_triggers_enabled == 1 
[   10.843164@0] init: property_changed: name [ro.ubootenv.varible.prefix] value [ubootenv.var] 
[   10.851611@0] init: ubootenv varible prefix is: ubootenv.var
[   10.857356@0] init: property_changed: property_triggers_enabled == 1 
[   10.863705@0] init: property_changed: name [ubootenv.var.outputmode] value [720p] 
[   10.871223@0] init: update_bootenv_varible name [ubootenv.var.outputmode]  value [720p] 
[   10.879431@0] init: property_changed: property_triggers_enabled == 1 
[   10.885747@0] init: property_changed: name [ubootenv.var.cvbsmode] value [480cvbs] 
[   10.893384@0] init: update_bootenv_varible name [ubootenv.var.cvbsmode]  value [480cvbs] 
[   10.901542@0] init: property_changed: property_triggers_enabled == 1 
[   10.907966@0] init: property_changed: name [ubootenv.var.hdmimode] value [720p] 
[   10.915378@0] init: update_bootenv_varible name [ubootenv.var.hdmimode]  value [720p] 
[   10.923275@0] init: value: setenv bootcmd run compatible_boot; saveenv; run compatible_boot; run compatible_boot
[   10.933416@0] init: property_changed: property_triggers_enabled == 1 
[   10.939795@0] init: property_changed: name [ubootenv.var.bootcmd] value [setenv bootcmd run compatible_boot; saveenv; run compatible_boot; run compatible_boot] 
[   10.954154@0] init: update_bootenv_varible name [ubootenv.var.bootcmd]  value [setenv bootcmd run compatible_boot; saveenv; run compatible_boot; run compatible_boot] 
[   10.968995@0] init: Get 4 varibles from /dev/mtd/mtd9 succeed!
[   10.974800@0] init: command 'ubootenv_init' r=0
[   10.979297@0] init: processing action 0x75798 (boot)
[   10.984866@0] init: command 'ifup' r=0
[   10.988074@0] init: command 'hostname' r=0
[   10.992086@0] init: command 'domainname' r=0
[   10.996444@0] init: command 'write' r=0
[   11.000143@0] init: starting 'display'
[   11.004212@0] init: property_changed: property_triggers_enabled == 1 
[   11.004377@1] init: 'display' (pid: 2596) started
[   11.015090@0] init: property_changed: name [init.svc.display] value [running] 
[   11.015136@0] init: starting 'remotecontrol'
[   11.015456@0] init: property_changed: property_triggers_enabled == 1 
[   11.015474@0] init: property_changed: name [init.svc.remotecontrol] value [running] 
[   11.015516@0] init: starting 'console'
[   11.015606@1] init: 'remotecontrol' (pid: 2600) started
[   11.015861@0] init: property_changed: property_triggers_enabled == 1 
[   11.015886@0] init: property_changed: name [init.svc.console] value [running] 
[   11.015920@0] init: starting 'recovery'
[   11.016234@0] init: property_changed: property_triggers_enabled == 1 
[   11.016261@0] init: property_changed: name [init.svc.recovery] value [running] 
[   11.016281@0] init: command 'class_start' r=0
[   11.016394@1] init: 'recovery' (pid: 2602) started
[   11.017342@1] init: waitpid returned pid 2600, status = 00000000
[   11.017363@1] init: process 'remotecontrol', pid 2600 exited
[   11.017414@1] init: property_changed: property_triggers_enabled == 1 
[   11.017425@1] init: property_changed: name [init.svc.remotecontrol] value [stopped] 
[   11.121048@0] init: 'console' (pid: 2601) started
# [   11.164512@1] tvmode set to 720p
[   11.164519@1] 
[   11.164564@1] don't set the same mode as current.
[   11.171919@1] osd0 free scale DISABLE
[   11.177505@1] osd0 free scale ENABLE
[   11.177545@1] vf_reg_provider:osd
[   11.192645@1] ==[vf_ppmgr_reset]=skip=current_reset_time:4294938392 last_reset_time:4294938391 discrete:1  interval:20 
[   11.212744@1] <FIQ>:vf_ext_light_unreg_provide
[   11.212751@1] 0
[   12.437532@0] FAT-fs (sda1): bogus number of reserved sectors
[   12.437648@0] FAT-fs (sda1): Can't find a valid FAT filesystem
[   12.488125@0] FAT-fs (sda): invalid media value (0xf3)
[   12.488164@0] FAT-fs (sda): Can't find a valid FAT filesystem
[   12.634535@0] FAT-fs (sda1): bogus number of reserved sectors
[   12.634651@0] FAT-fs (sda1): Can't find a valid FAT filesystem
[   12.669344@0] FAT-fs (sda): invalid media value (0xf3)
[   12.669378@0] FAT-fs (sda): Can't find a valid FAT filesystem
[   12.804643@1] FAT-fs (sda1): bogus number of reserved sectors
[   12.804747@1] FAT-fs (sda1): Can't find a valid FAT filesystem
[   12.842257@1] FAT-fs (sda): invalid media value (0xf3)
[   12.842290@1] FAT-fs (sda): Can't find a valid FAT filesystem
[   12.974549@1] FAT-fs (sda1): bogus number of reserved sectors
[   12.974658@1] FAT-fs (sda1): Can't find a valid FAT filesystem
[   13.023434@1] FAT-fs (sda): invalid media value (0xf3)
[   13.023485@1] FAT-fs (sda): Can't find a valid FAT filesystem
[   13.164992@1] FAT-fs (sda1): bogus number of reserved sectors
[   13.165097@1] FAT-fs (sda1): Can't find a valid FAT filesystem
[   13.196373@1] FAT-fs (sda): invalid media value (0xf3)
[   13.196424@1] FAT-fs (sda): Can't find a valid FAT filesystem
[   13.324683@1] FAT-fs (sda1): bogus number of reserved sectors
[   13.324793@1] FAT-fs (sda1): Can't find a valid FAT filesystem
[   13.361077@1] FAT-fs (sda): invalid media value (0xf3)
[   13.361111@1] FAT-fs (sda): Can't find a valid FAT filesystem
[   13.494542@1] FAT-fs (sda1): bogus number of reserved sectors
[   13.494655@1] FAT-fs (sda1): Can't find a valid FAT filesystem
[   13.550486@1] FAT-fs (sda): invalid media value (0xf3)
[   13.550541@1] FAT-fs (sda): Can't find a valid FAT filesystem
[   13.704526@1] FAT-fs (sda1): bogus number of reserved sectors
[   13.704629@1] FAT-fs (sda1): Can't find a valid FAT filesystem
[   13.739882@1] FAT-fs (sda): invalid media value (0xf3)
[   13.739930@1] FAT-fs (sda): Can't find a valid FAT filesystem

# 
# 
# 
# [   23.183224@0] EXT4-fs (cache): recovery complete
[   23.185808@0] EXT4-fs (cache): mounted filesystem with ordered data mode. Opts: 
[   31.422850@0] pcd_ep0_timer_timeout 1
[   31.422898@0] WARN::dwc_otg_handle_mode_mismatch_intr:154: Mode Mismatch Interrupt: currently in Host mode
[   31.422905@0] 
[   35.249960@0] aml_nftl_erase_part: start
[   36.705882@0] aml_nftl_erase_part: erase ok
[   36.705900@0] nftl version 140415a
[   36.705994@0] nftl part attr 0
[   36.706062@0] nftl start:512,64
[   36.707202@0] first
[   36.889143@0] average_erase_count:0
[   36.889225@0] second 0,448
[   36.889689@0] all block full!!
[   36.892828@0] free block cnt = 512
[   36.896123@0] new current block is 511
[   36.899852@0] nftl ok!
[   41.201434@1] EXT4-fs (system): mounted filesystem with ordered data mode. Opts: 
[  112.323371@1] aml_nftl_erase_part: start
[  113.016360@1] aml_nftl_erase_part: erase ok
[  113.016421@1] nftl version 140415a
[  113.018323@1] nftl part attr 0
[  113.021374@1] nftl start:256,32
[  113.024694@1] first
[  113.103855@1] average_erase_count:0
[  113.103892@1] second 0,224
[  113.104399@1] all block full!!
[  113.107438@1] free block cnt = 256
[  113.110822@1] new current block is 255
[  113.114573@1] nftl ok!
[  115.383500@1] aml_nftl_erase_part: start
[  118.230080@1] aml_nftl_erase_part: erase ok
[  118.230151@1] nftl version 140415a
[  118.232143@1] nftl part attr 0
[  118.235274@1] nftl start:1074,134
[  118.240324@1] first
[  118.599748@0] average_erase_count:0
[  118.599877@0] second 0,940
[  118.600298@0] all block full!!
[  118.603496@0] free block cnt = 1074
[  118.606822@0] new current block is 1073
[  118.615631@0] nftl ok!

# 
# 
# 
# cat /tmp/recovery.log                                                        
hardware version
Starting recovery on Fri Jan  2 00:27:18 1970
recovery filesystem table
=========================
  0 /tmp ramdisk (null) (null) 0
  1 / ramdisk (null) (null) 0
  2 /bootloader mtd bootloader (null) 0
  3 /logo mtd logo (null) 0
  4 /aml_logo mtd aml_logo (null) 0
  5 /hashtable mtd hashtable (null) 0
  6 /recovery mtd recovery (null) 0
  7 /boot mtd boot (null) 0
  8 /system ext4 /dev/block/system (null) 0
  9 /cache ext4 /dev/block/cache (null) 0
  10 /data ext4 /dev/block/data (null) 0
  11 /sdcard vfat /dev/block/sda# (null) 0
  12 /media vfat /dev/block/media (null) 0
  13 /udisk vfat /dev/block/sdb# (null) 0

I:loaded /etc/recovery.kl
I:recovery key map table
I:=========================
I:  0 type:select value:-4 key:28 15 158 -1 -1 -1
I:  1 type:down value:-3 key:108 114 109 -1 -1 -1
I:  2 type:up value:-2 key:103 104 115 -1 -1 -1
I:  3 type:back_door value:-6 key:128 26 118 -1 -1 -1
I:
framebuffer: fd 5 (1280 x 720)
       installing_text:  (1 x 1 @ 1896)
          erasing_text:  (1 x 1 @ 1599)
       no_command_text:  (1 x 1 @ 1599)
            error_text:  (1 x 1 @ 1599)
W:try mount /dev/block/sda1 ...
W:try mount /dev/block/sda2 ...
W:try mount /dev/block/sda3 ...
W:try mount /dev/block/sda4 ...
W:try mount /dev/block/sda ...
E:failed to mount /sdcard (Invalid argument)
W:try mount /dev/block/sda1 ...
W:try mount /dev/block/sda2 ...
W:try mount /dev/block/sda3 ...
W:try mount /dev/block/sda4 ...
W:try mount /dev/block/sda ...
E:failed to mount /sdcard (Invalid argument)
W:try mount /dev/block/sda1 ...
W:try mount /dev/block/sda2 ...
W:try mount /dev/block/sda3 ...
W:try mount /dev/block/sda4 ...
W:try mount /dev/block/sda ...
E:failed to mount /sdcard (Invalid argument)
W:try mount /dev/block/sda1 ...
W:try mount /dev/block/sda2 ...
W:try mount /dev/block/sda3 ...
W:try mount /dev/block/sda4 ...
W:try mount /dev/block/sda ...
E:failed to mount /sdcard (Invalid argument)
E:cannot mount /sdcard (Invalid argument)
E:Can't mount /sdcard/factory_update_param.aml
W:try mount /dev/block/sda1 ...
W:try mount /dev/block/sda2 ...
W:try mount /dev/block/sda3 ...
W:try mount /dev/block/sda4 ...
W:try mount /dev/block/sda ...
E:failed to mount /sdcard (Invalid argument)
W:try mount /dev/block/sda1 ...
W:try mount /dev/block/sda2 ...
W:try mount /dev/block/sda3 ...
W:try mount /dev/block/sda4 ...
W:try mount /dev/block/sda ...
E:failed to mount /sdcard (Invalid argument)
W:try mount /dev/block/sda1 ...
W:try mount /dev/block/sda2 ...
W:try mount /dev/block/sda3 ...
W:try mount /dev/block/sda4 ...
W:try mount /dev/block/sda ...
E:failed to mount /sdcard (Invalid argument)
W:try mount /dev/block/sda1 ...
W:try mount /dev/block/sda2 ...
W:try mount /dev/block/sda3 ...
W:try mount /dev/block/sda4 ...
W:try mount /dev/block/sda ...
E:failed to mount /sdcard (Invalid argument)
E:cannot mount /sdcard (Invalid argument)
E:Can't mount /sdcard/m6_cytc_update.zip
now go into udisk checking!!!!!!!!!!!!!!! 1
W:try mount /dev/block/sdb1 ...
has opened the command file !!!!!!!!!!!!/udisk/factory_update_param.aml 
ooooui =  3
o oui = [03]----n oui = [03]
o model_tYpe = [02]----n model_type = [02]
o user_group_id = [03]----n user_group_id = [03]
hardware version [0][0][6][1]
software version [2][0][3][4]
start id =[0][0][0][0][0][0][0][0]
stb serail = [000066]
serial = [66]
first 10 = [0000000000] == [0000000000000066]
start id l=[0], me=[66]
new 10 char = [9999999999] = [0000000000000066]
tmp_id = [999999]
start id l=[999999], me=[66]
file type is [1]
 liukevin file lenght  = [0x9a7eb30][161999664]
complete parsing the parameters......................
locale is [(null)]
Command: "/sbin/recovery" "--update_package=/udisk/g18ref-ota-20141230.zip" "--wipe_data"
Finding update package...
I:Update location: /udisk/g18ref-ota-20141230.zip
Opening update package...
I:read key e=3
I:1 key(s) loaded from /res/keys
Verifying update package...
I:comment is 1738 bytes; signature 1720 bytes from end
I:whole-file signature verified against key 0
I:verify_file returned 0
 pathname is: /udisk/g18ref-ota-20141230.zip
this is noraml board
check img rsa.img 
rsa.img: no /udisk/g18ref-ota-20141230.zip in package
check img boot.img 
normal board check_img_encrypted uboot_flag is 0 file length is 4618240 
check img recovery.img 
normal board check_img_encrypted uboot_flag is 0 file length is 5167104 
check img bootloader.img 
bootloader.img: no /udisk/g18ref-ota-20141230.zip in package
Installing update...
FormatFn : location /dev/block/systemext4_erase_volum : open
##ext4_erase_volum : erase /dev/block/system
ext4_erase_volum : OK
Creating filesystem with parameters:
    Size: 939524096
    Block size: 4096
    Blocks per group: 32768
    Inodes per group: 8192
    Inode size: 256
    Journal blocks: 3584
    Label: 
    Blocks: 229376
    Block groups: 7
    Reserved block group size: 55
Created filesystem with 11/57344 inodes and 7413/229376 blocks
warning: wipe_block_device: Discard failed

minzip: Extracted file "/system/app/AppInstaller.apk"
minzip: Extracted file "/system/app/AppStore_DBSTAR_3.3.3.apk"
minzip: Extracted file "/system/app/ApplicationsProvider.apk"
minzip: Extracted file "/system/app/BackupRestoreConfirmation.apk"
minzip: Extracted file "/system/app/CertInstaller.apk"
minzip: Extracted file "/system/app/DBStarAppManager.apk"
minzip: Extracted file "/system/app/DbstarBookReader.apk"
minzip: Extracted file "/system/app/DbstarDVB.apk"
minzip: Extracted file "/system/app/DbstarFileBrowser.apk"
minzip: Extracted file "/system/app/DbstarLauncher.apk"
minzip: Extracted file "/system/app/DbstarSettings.apk"
minzip: Extracted file "/system/app/DefaultContainerService.apk"
minzip: Extracted file "/system/app/DrmProvider.apk"
minzip: Extracted file "/system/app/FileBrowser.apk"
minzip: Extracted file "/system/app/FusedLocation.apk"
minzip: Extracted file "/system/app/HTMLViewer.apk"
minzip: Extracted file "/system/app/InputDevices.apk"
minzip: Extracted file "/system/app/KeyChain.apk"
minzip: Extracted file "/system/app/MediaProvider.apk"
minzip: Extracted file "/system/app/MultipleMediaReader.apk"
minzip: Extracted file "/system/app/Music.apk"
minzip: Extracted file "/system/app/MusicFX.apk"
minzip: Extracted file "/system/app/NoiseField.apk"
minzip: Extracted file "/system/app/OTTSettings.apk"
minzip: Extracted file "/system/app/PackageInstaller.apk"
minzip: Extracted file "/system/app/PhaseBeam.apk"
minzip: Extracted file "/system/app/PicoTts.apk"
minzip: Extracted file "/system/app/Provision.apk"
minzip: Extracted file "/system/app/RemoteIME.apk"
minzip: Extracted file "/system/app/Settings.apk"
minzip: Extracted file "/system/app/SettingsProvider.apk"
minzip: Extracted file "/system/app/SharedStorageBackup.apk"
minzip: Extracted file "/system/app/SystemUI.apk"
minzip: Extracted file "/system/app/VideoPlayer.apk"
minzip: Extracted file "/system/app/WAPPushManager.apk"
minzip: Extracted file "/system/app/icntv-n260-v.1.0.0.apk"
minzip: Extracted file "/system/app/oem_install_flash_player_jb_mr1.apk"
minzip: Extracted file "/system/bin/AmlHostsTool"
minzip: Extracted file "/system/bin/abcc"
minzip: Extracted file "/system/bin/adb"
minzip: Extracted file "/system/bin/am"
minzip: Extracted file "/system/bin/app_process"
minzip: Extracted file "/system/bin/applypatch"
minzip: Extracted file "/system/bin/atrace"
minzip: Extracted file "/system/bin/audioloop"
minzip: Extracted file "/system/bin/bmgr"
minzip: Extracted file "/system/bin/bootanimation"
minzip: Extracted file "/system/bin/bu"
minzip: Extracted file "/system/bin/bugreport"
minzip: Extracted file "/system/bin/chat"
minzip: Extracted file "/system/bin/codec"
minzip: Extracted file "/system/bin/content"
minzip: Extracted file "/system/bin/corrupt_gdt_free_blocks"
minzip: Extracted file "/system/bin/curl"
minzip: Extracted file "/system/bin/dalvikvm"
minzip: Extracted file "/system/bin/dbstar_control.sh"
minzip: Extracted file "/system/bin/dbus-daemon"
minzip: Extracted file "/system/bin/debuggerd"
minzip: Extracted file "/system/bin/decoder"
minzip: Extracted file "/system/bin/dexopt"
minzip: Extracted file "/system/bin/dhcpcd"
minzip: Extracted file "/system/bin/disk_manage.sh"
minzip: Extracted file "/system/bin/dnsmasq"
minzip: Extracted file "/system/bin/drmserver"
minzip: Extracted file "/system/bin/dumpstate"
minzip: Extracted file "/system/bin/dumpsys"
minzip: Extracted file "/system/bin/fsck.exfat"
minzip: Extracted file "/system/bin/fsck_msdos"
minzip: Extracted file "/system/bin/gdbserver"
minzip: Extracted file "/system/bin/gzip"
minzip: Extracted file "/system/bin/hostapd"
minzip: Extracted file "/system/bin/hostapd_cli"
minzip: Extracted file "/system/bin/ime"
minzip: Extracted file "/system/bin/infoTest"
minzip: Extracted file "/system/bin/init-pppd.sh"
minzip: Extracted file "/system/bin/input"
minzip: Extracted file "/system/bin/installd"
minzip: Extracted file "/system/bin/ip"
minzip: Extracted file "/system/bin/ip6tables"
minzip: Extracted file "/system/bin/iptables"
minzip: Extracted file "/system/bin/keystore"
minzip: Extracted file "/system/bin/keystore_cli"
minzip: Extracted file "/system/bin/linker"
minzip: Extracted file "/system/bin/logcat"
minzip: Extracted file "/system/bin/logwrapper"
minzip: Extracted file "/system/bin/make_ext4fs"
minzip: Extracted file "/system/bin/mdnsd"
minzip: Extracted file "/system/bin/mediaserver"
minzip: Extracted file "/system/bin/mkntfs"
minzip: Extracted file "/system/bin/mksh"
minzip: Extracted file "/system/bin/monkey"
minzip: Extracted file "/system/bin/mount.exfat"
minzip: Extracted file "/system/bin/mtpd"
minzip: Extracted file "/system/bin/ndc"
minzip: Extracted file "/system/bin/netcfg"
minzip: Extracted file "/system/bin/netd"
minzip: Extracted file "/system/bin/ntfs-3g"
minzip: Extracted file "/system/bin/ping"
minzip: Extracted file "/system/bin/pm"
minzip: Extracted file "/system/bin/pngtest"
minzip: Extracted file "/system/bin/pppd"
minzip: Extracted file "/system/bin/qemu-props"
minzip: Extracted file "/system/bin/qemud"
minzip: Extracted file "/system/bin/racoon"
minzip: Extracted file "/system/bin/radiooptions"
minzip: Extracted file "/system/bin/record"
minzip: Extracted file "/system/bin/recordvideo"
minzip: Extracted file "/system/bin/remotecfg"
minzip: Extracted file "/system/bin/requestsync"
minzip: Extracted file "/system/bin/rild"
minzip: Extracted file "/system/bin/run-as"
minzip: Extracted file "/system/bin/schedtest"
minzip: Extracted file "/system/bin/screencap"
minzip: Extracted file "/system/bin/screenshot"
minzip: Extracted file "/system/bin/sdcard"
minzip: Extracted file "/system/bin/sensorservice"
minzip: Extracted file "/system/bin/service"
minzip: Extracted file "/system/bin/servicemanager"
minzip: Extracted file "/system/bin/set_display_mode.sh"
minzip: Extracted file "/system/bin/set_ext4_err_bit"
minzip: Extracted file "/system/bin/settings"
minzip: Extracted file "/system/bin/setup_fs"
minzip: Extracted file "/system/bin/sf2"
minzip: Extracted file "/system/bin/showlease"
minzip: Extracted file "/system/bin/smarthome"
minzip: Extracted file "/system/bin/stagefright"
minzip: Extracted file "/system/bin/stream"
minzip: Extracted file "/system/bin/surfaceflinger"
minzip: Extracted file "/system/bin/svc"
minzip: Extracted file "/system/bin/system_key_server"
minzip: Extracted file "/system/bin/system_server"
minzip: Extracted file "/system/bin/tc"
minzip: Extracted file "/system/bin/testid3"
minzip: Extracted file "/system/bin/tinycap"
minzip: Extracted file "/system/bin/tinymix"
minzip: Extracted file "/system/bin/tinyplay"
minzip: Extracted file "/system/bin/toolbox"
minzip: Extracted file "/system/bin/uart-test"
minzip: Extracted file "/system/bin/udptest"
minzip: Extracted file "/system/bin/uiautomator"
minzip: Extracted file "/system/bin/usb_modeswitch"
minzip: Extracted file "/system/bin/usbpower"
minzip: Extracted file "/system/bin/usbtestpm"
minzip: Extracted file "/system/bin/vdc"
minzip: Extracted file "/system/bin/vold"
minzip: Extracted file "/system/bin/wfd"
minzip: Extracted file "/system/bin/wififix.sh"
minzip: Extracted file "/system/bin/wpa_cli"
minzip: Extracted file "/system/bin/wpa_supplicant"
minzip: Extracted file "/system/build.prop"
minzip: Extracted file "/system/etc/NOTICE.html.gz"
minzip: Extracted file "/system/etc/Third_party_apk_camera.xml"
minzip: Extracted file "/system/etc/alarm_blacklist.txt"
minzip: Extracted file "/system/etc/apns-conf.xml"
minzip: Extracted file "/system/etc/audio_effects.conf"
minzip: Extracted file "/system/etc/audio_policy.conf"
minzip: Extracted file "/system/etc/bluetooth/audio.conf"
minzip: Extracted file "/system/etc/bluetooth/auto_pairing.conf"
minzip: Extracted file "/system/etc/bluetooth/blacklist.conf"
minzip: Extracted file "/system/etc/bluetooth/input.conf"
minzip: Extracted file "/system/etc/bluetooth/network.conf"
minzip: Extracted file "/system/etc/config"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/1_focusOut.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/2_focusOut.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/6_focusOut.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/7_focusOut.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/8_focusOut.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/9_focusOut.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/Books_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/BasicInfo_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/DefaultIcon_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/Download_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/FileBrowser_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/GridInfos_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/Help_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/Media_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/MyAppliances_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/MyCenter_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/Network_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/PowerEfficiency_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/PurchaseInfo_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/Receiving_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/Setting_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/ShortcutCtrl_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/SmartHousehold_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/SmartPowerSettings_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/SmartPower_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/StateGridNews_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/LocalColumnIcon/TimingTask_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/ColumnRes/NewsPaper_losefocus.png"
minzip: Extracted file "/system/etc/dbstar/Dbstar.db"
minzip: Extracted file "/system/etc/dbstar/Smarthome.db"
minzip: Extracted file "/system/etc/dbstar/dbstar.conf"
minzip: Extracted file "/system/etc/dbstar/drm/entitle/block01"
minzip: Extracted file "/system/etc/dbstar/push.conf"
minzip: Extracted file "/system/etc/dbus.conf"
minzip: Extracted file "/system/etc/default_shortcut.cfg"
minzip: Extracted file "/system/etc/dhcpcd/dhcpcd-hooks/20-dns.conf"
minzip: Extracted file "/system/etc/dhcpcd/dhcpcd-hooks/95-configured"
minzip: Extracted file "/system/etc/dhcpcd/dhcpcd-run-hooks"
minzip: Extracted file "/system/etc/dhcpcd/dhcpcd.conf"
minzip: Extracted file "/system/etc/drm/playready/bgroupcert.dat"
minzip: Extracted file "/system/etc/drm/playready/zgpriv.dat"
minzip: Extracted file "/system/etc/event-log-tags"
minzip: Extracted file "/system/etc/fallback_fonts.xml"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_aac.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_aac.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_aac_helix.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_aac_helix.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_adpcm.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_adpcm.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_alac.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_alac.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_amr.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_amr.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_ape.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_ape.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_cook.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_cook.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_ddp_dcv.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_ddp_dcv.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_dtshd.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_dtshd.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_flac.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_flac.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_mad.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_mad.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_mad_old.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_mad_old.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_mp3_lp.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_mp3_lp.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_null.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_null.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_ogg.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_ogg.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_pcm.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_pcm.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_raac.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_raac.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_vorbis.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_vorbis.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_wma.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_wma.bin.checksum"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_wmapro.bin"
minzip: Extracted file "/system/etc/firmware/audiodsp_codec_wmapro.bin.checksum"
minzip: Extracted file "/system/etc/fw_env.config"
minzip: Extracted file "/system/etc/hosts"
minzip: Extracted file "/system/etc/init.goldfish.sh"
minzip: Extracted file "/system/etc/media_codecs.xml"
minzip: Extracted file "/system/etc/media_profiles.xml"
minzip: Extracted file "/system/etc/mixer_paths.xml"
minzip: Extracted file "/system/etc/mkshrc"
minzip: Extracted file "/system/etc/permissions/amlogic.libplayer.xml"
minzip: Extracted file "/system/etc/permissions/amlogic.subtitle.xml"
minzip: Extracted file "/system/etc/permissions/android.hardware.camera.front.xml"
minzip: Extracted file "/system/etc/permissions/android.hardware.sensor.gyroscope.xml"
minzip: Extracted file "/system/etc/permissions/android.hardware.usb.accessory.xml"
minzip: Extracted file "/system/etc/permissions/android.hardware.usb.host.xml"
minzip: Extracted file "/system/etc/permissions/android.hardware.wifi.direct.xml"
minzip: Extracted file "/system/etc/permissions/android.hardware.wifi.xml"
minzip: Extracted file "/system/etc/permissions/android.software.live_wallpaper.xml"
minzip: Extracted file "/system/etc/permissions/android.software.sip.voip.xml"
minzip: Extracted file "/system/etc/permissions/com.android.location.provider.xml"
minzip: Extracted file "/system/etc/permissions/com.google.widevine.software.drm.xml"
minzip: Extracted file "/system/etc/permissions/platform.xml"
minzip: Extracted file "/system/etc/permissions/tablet_core_hardware.xml"
minzip: Extracted file "/system/etc/ppp/ip-up"
minzip: Extracted file "/system/etc/ppp/ip-up-vpn"
minzip: Extracted file "/system/etc/ppp/peers/mcli-cdma"
minzip: Extracted file "/system/etc/ppp/peers/mcli-gsm"
minzip: Extracted file "/system/etc/recovery-resource.dat"
minzip: Extracted file "/system/etc/remote.conf"
minzip: Extracted file "/system/etc/remote.conf.dbstar"
minzip: Extracted file "/system/etc/security/cacerts/00673b5b.0"
minzip: Extracted file "/system/etc/security/cacerts/03e16f6c.0"
minzip: Extracted file "/system/etc/security/cacerts/08aef7bb.0"
minzip: Extracted file "/system/etc/security/cacerts/0d188d89.0"
minzip: Extracted file "/system/etc/security/cacerts/10531352.0"
minzip: Extracted file "/system/etc/security/cacerts/111e6273.0"
minzip: Extracted file "/system/etc/security/cacerts/1155c94b.0"
minzip: Extracted file "/system/etc/security/cacerts/119afc2e.0"
minzip: Extracted file "/system/etc/security/cacerts/11a09b38.0"
minzip: Extracted file "/system/etc/security/cacerts/12d55845.0"
minzip: Extracted file "/system/etc/security/cacerts/17b51fe6.0"
minzip: Extracted file "/system/etc/security/cacerts/1920cacb.0"
minzip: Extracted file "/system/etc/security/cacerts/1dac3003.0"
minzip: Extracted file "/system/etc/security/cacerts/1dbdda5b.0"
minzip: Extracted file "/system/etc/security/cacerts/1dcd6f4c.0"
minzip: Extracted file "/system/etc/security/cacerts/1df5ec47.0"
minzip: Extracted file "/system/etc/security/cacerts/1e1eab7c.0"
minzip: Extracted file "/system/etc/security/cacerts/1e8e7201.0"
minzip: Extracted file "/system/etc/security/cacerts/1eb37bdf.0"
minzip: Extracted file "/system/etc/security/cacerts/219d9499.0"
minzip: Extracted file "/system/etc/security/cacerts/23f4c490.0"
minzip: Extracted file "/system/etc/security/cacerts/27af790d.0"
minzip: Extracted file "/system/etc/security/cacerts/2afc57aa.0"
minzip: Extracted file "/system/etc/security/cacerts/2e8714cb.0"
minzip: Extracted file "/system/etc/security/cacerts/2fa87019.0"
minzip: Extracted file "/system/etc/security/cacerts/2fb1850a.0"
minzip: Extracted file "/system/etc/security/cacerts/33815e15.0"
minzip: Extracted file "/system/etc/security/cacerts/343eb6cb.0"
minzip: Extracted file "/system/etc/security/cacerts/399e7759.0"
minzip: Extracted file "/system/etc/security/cacerts/3a3b02ce.0"
minzip: Extracted file "/system/etc/security/cacerts/3ad48a91.0"
minzip: Extracted file "/system/etc/security/cacerts/3c58f906.0"
minzip: Extracted file "/system/etc/security/cacerts/3c860d51.0"
minzip: Extracted file "/system/etc/security/cacerts/3d441de8.0"
minzip: Extracted file "/system/etc/security/cacerts/3e7271e8.0"
minzip: Extracted file "/system/etc/security/cacerts/418595b9.0"
minzip: Extracted file "/system/etc/security/cacerts/455f1b52.0"
minzip: Extracted file "/system/etc/security/cacerts/46b2fd3b.0"
minzip: Extracted file "/system/etc/security/cacerts/48478734.0"
minzip: Extracted file "/system/etc/security/cacerts/4d654d1d.0"
minzip: Extracted file "/system/etc/security/cacerts/4e18c148.0"
minzip: Extracted file "/system/etc/security/cacerts/4fbd6bfa.0"
minzip: Extracted file "/system/etc/security/cacerts/5021a0a2.0"
minzip: Extracted file "/system/etc/security/cacerts/5046c355.0"
minzip: Extracted file "/system/etc/security/cacerts/524d9b43.0"
minzip: Extracted file "/system/etc/security/cacerts/56b8a0b6.0"
minzip: Extracted file "/system/etc/security/cacerts/57692373.0"
minzip: Extracted file "/system/etc/security/cacerts/58a44af1.0"
minzip: Extracted file "/system/etc/security/cacerts/594f1775.0"
minzip: Extracted file "/system/etc/security/cacerts/5a3f0ff8.0"
minzip: Extracted file "/system/etc/security/cacerts/5a5372fc.0"
minzip: Extracted file "/system/etc/security/cacerts/5cf9d536.0"
minzip: Extracted file "/system/etc/security/cacerts/5e4e69e7.0"
minzip: Extracted file "/system/etc/security/cacerts/60afe812.0"
minzip: Extracted file "/system/etc/security/cacerts/635ccfd5.0"
minzip: Extracted file "/system/etc/security/cacerts/67495436.0"
minzip: Extracted file "/system/etc/security/cacerts/69105f4f.0"
minzip: Extracted file "/system/etc/security/cacerts/6adf0799.0"
minzip: Extracted file "/system/etc/security/cacerts/6e8bf996.0"
minzip: Extracted file "/system/etc/security/cacerts/6fcc125d.0"
minzip: Extracted file "/system/etc/security/cacerts/72f369af.0"
minzip: Extracted file "/system/etc/security/cacerts/72fa7371.0"
minzip: Extracted file "/system/etc/security/cacerts/74c26bd0.0"
minzip: Extracted file "/system/etc/security/cacerts/75680d2e.0"
minzip: Extracted file "/system/etc/security/cacerts/7651b327.0"
minzip: Extracted file "/system/etc/security/cacerts/76579174.0"
minzip: Extracted file "/system/etc/security/cacerts/7672ac4b.0"
minzip: Extracted file "/system/etc/security/cacerts/7999be0d.0"
minzip: Extracted file "/system/etc/security/cacerts/7a481e66.0"
minzip: Extracted file "/system/etc/security/cacerts/7a819ef2.0"
minzip: Extracted file "/system/etc/security/cacerts/7d3cd826.0"
minzip: Extracted file "/system/etc/security/cacerts/7d453d8f.0"
minzip: Extracted file "/system/etc/security/cacerts/81b9768f.0"
minzip: Extracted file "/system/etc/security/cacerts/8470719d.0"
minzip: Extracted file "/system/etc/security/cacerts/84cba82f.0"
minzip: Extracted file "/system/etc/security/cacerts/85cde254.0"
minzip: Extracted file "/system/etc/security/cacerts/86212b19.0"
minzip: Extracted file "/system/etc/security/cacerts/87753b0d.0"
minzip: Extracted file "/system/etc/security/cacerts/882de061.0"
minzip: Extracted file "/system/etc/security/cacerts/895cad1a.0"
minzip: Extracted file "/system/etc/security/cacerts/89c02a45.0"
minzip: Extracted file "/system/etc/security/cacerts/8f7b96c4.0"
minzip: Extracted file "/system/etc/security/cacerts/9339512a.0"
minzip: Extracted file "/system/etc/security/cacerts/95aff9e3.0"
minzip: Extracted file "/system/etc/security/cacerts/9685a493.0"
minzip: Extracted file "/system/etc/security/cacerts/9772ca32.0"
minzip: Extracted file "/system/etc/security/cacerts/9d6523ce.0"
minzip: Extracted file "/system/etc/security/cacerts/9dbefe7b.0"
minzip: Extracted file "/system/etc/security/cacerts/9f533518.0"
minzip: Extracted file "/system/etc/security/cacerts/a0bc6fbb.0"
minzip: Extracted file "/system/etc/security/cacerts/a15b3b6b.0"
minzip: Extracted file "/system/etc/security/cacerts/a3896b44.0"
minzip: Extracted file "/system/etc/security/cacerts/a7605362.0"
minzip: Extracted file "/system/etc/security/cacerts/a7d2cf64.0"
minzip: Extracted file "/system/etc/security/cacerts/ab5346f4.0"
minzip: Extracted file "/system/etc/security/cacerts/add67345.0"
minzip: Extracted file "/system/etc/security/cacerts/aeb67534.0"
minzip: Extracted file "/system/etc/security/cacerts/b0f3e76e.0"
minzip: Extracted file "/system/etc/security/cacerts/b7db1890.0"
minzip: Extracted file "/system/etc/security/cacerts/bc3f2570.0"
minzip: Extracted file "/system/etc/security/cacerts/bcdd5959.0"
minzip: Extracted file "/system/etc/security/cacerts/bda4cc84.0"
minzip: Extracted file "/system/etc/security/cacerts/bdacca6f.0"
minzip: Extracted file "/system/etc/security/cacerts/bf64f35b.0"
minzip: Extracted file "/system/etc/security/cacerts/c215bc69.0"
minzip: Extracted file "/system/etc/security/cacerts/c33a80d4.0"
minzip: Extracted file "/system/etc/security/cacerts/c3a6a9ad.0"
minzip: Extracted file "/system/etc/security/cacerts/c527e4ab.0"
minzip: Extracted file "/system/etc/security/cacerts/c7e2a638.0"
minzip: Extracted file "/system/etc/security/cacerts/c8763593.0"
minzip: Extracted file "/system/etc/security/cacerts/ccc52f49.0"
minzip: Extracted file "/system/etc/security/cacerts/cdaebb72.0"
minzip: Extracted file "/system/etc/security/cacerts/cf701eeb.0"
minzip: Extracted file "/system/etc/security/cacerts/d16a5865.0"
minzip: Extracted file "/system/etc/security/cacerts/d537fba6.0"
minzip: Extracted file "/system/etc/security/cacerts/d59297b8.0"
minzip: Extracted file "/system/etc/security/cacerts/d64f06f3.0"
minzip: Extracted file "/system/etc/security/cacerts/d777342d.0"
minzip: Extracted file "/system/etc/security/cacerts/d8274e24.0"
minzip: Extracted file "/system/etc/security/cacerts/dbc54cab.0"
minzip: Extracted file "/system/etc/security/cacerts/ddc328ff.0"
minzip: Extracted file "/system/etc/security/cacerts/e48193cf.0"
minzip: Extracted file "/system/etc/security/cacerts/e60bf0c0.0"
minzip: Extracted file "/system/etc/security/cacerts/e775ed2d.0"
minzip: Extracted file "/system/etc/security/cacerts/e7b8d656.0"
minzip: Extracted file "/system/etc/security/cacerts/e8651083.0"
minzip: Extracted file "/system/etc/security/cacerts/ea169617.0"
minzip: Extracted file "/system/etc/security/cacerts/eb375c3e.0"
minzip: Extracted file "/system/etc/security/cacerts/ed049835.0"
minzip: Extracted file "/system/etc/security/cacerts/ed524cf5.0"
minzip: Extracted file "/system/etc/security/cacerts/ee7cd6fb.0"
minzip: Extracted file "/system/etc/security/cacerts/f4996e82.0"
minzip: Extracted file "/system/etc/security/cacerts/f58a60fe.0"
minzip: Extracted file "/system/etc/security/cacerts/f61bff45.0"
minzip: Extracted file "/system/etc/security/cacerts/f80cc7f6.0"
minzip: Extracted file "/system/etc/security/cacerts/fac084d7.0"
minzip: Extracted file "/system/etc/security/cacerts/facacbc6.0"
minzip: Extracted file "/system/etc/security/cacerts/fb126c6d.0"
minzip: Extracted file "/system/etc/security/cacerts/fde84897.0"
minzip: Extracted file "/system/etc/security/cacerts/ff783690.0"
minzip: Extracted file "/system/etc/security/otacerts.zip"
minzip: Extracted file "/system/etc/system_fonts.xml"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0421_060c"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0421_0610"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0421_0622"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0421_0627"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0471_1237"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0482_024d"
minzip: Extracted file "/system/etc/usb_modeswitch.d/04cc_225a"
minzip: Extracted file "/system/etc/usb_modeswitch.d/04e8_689a"
minzip: Extracted file "/system/etc/usb_modeswitch.d/04e8_f000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/057c_84ff"
minzip: Extracted file "/system/etc/usb_modeswitch.d/05c6_1000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/05c6_2000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/05c6_2001"
minzip: Extracted file "/system/etc/usb_modeswitch.d/05c6_6000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/05c6_f000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/05c7_1000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/072f_100d"
minzip: Extracted file "/system/etc/usb_modeswitch.d/07d1_a800"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0930_0d46"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0ace_2011"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0ace_20ff"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_6711"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_6731"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_6751"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_6771"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_6791"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_6811"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_6911"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_6951"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_6971"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7011"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7031"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7051"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7071"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7111"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7211"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7251"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7271"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7301"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7311"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7361"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7381"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7401"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7501"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7601"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7701"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7801"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_7901"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_8200"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_8201"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_8300"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_8302"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_8304"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_8400"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_c031"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_c100"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_d013"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_d031"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_d033"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_d035"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_d055"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_d057"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_d058"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_d155"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_d157"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_d255"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_d257"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0af0_d357"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0b3c_c700"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0b3c_f000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0cf3_20ff"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0d46_45a1"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0d46_45a5"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0e8d_7109"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0fce_d0cf"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0fce_d0e1"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0fce_d103"
minzip: Extracted file "/system/etc/usb_modeswitch.d/0fd1_1000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1004_1000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1004_607f"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1004_613a"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1004_613f"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1004_6190"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1033_0035"
minzip: Extracted file "/system/etc/usb_modeswitch.d/106c_3b03"
minzip: Extracted file "/system/etc/usb_modeswitch.d/106c_3b05"
minzip: Extracted file "/system/etc/usb_modeswitch.d/106c_3b06"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1076_7f40"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1199_0fff"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1266_1000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1001"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1003"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1009"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_101e"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1031"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1414"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1446"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1449"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_14ad"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_14c1"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_14d1"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_14fe"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1505"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1520"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1521"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1523"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1526"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1553"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1557"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1c0b"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1da1"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_1f01"
minzip: Extracted file "/system/etc/usb_modeswitch.d/12d1_380b"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1410_5010"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1410_5020"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1410_5030"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1410_5031"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1410_5041"
minzip: Extracted file "/system/etc/usb_modeswitch.d/148f_2578"
minzip: Extracted file "/system/etc/usb_modeswitch.d/15eb_0001"
minzip: Extracted file "/system/etc/usb_modeswitch.d/15eb_7153"
minzip: Extracted file "/system/etc/usb_modeswitch.d/16d8_6281"
minzip: Extracted file "/system/etc/usb_modeswitch.d/16d8_6803"
minzip: Extracted file "/system/etc/usb_modeswitch.d/16d8_6803__"
minzip: Extracted file "/system/etc/usb_modeswitch.d/16d8_700a"
minzip: Extracted file "/system/etc/usb_modeswitch.d/16d8_f000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/198f_bccd"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_0003"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_0026"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_0040"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_0053"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_0083"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_0101"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_0103"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_0110"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_0115"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_0120"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_0146"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_0166"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_0169"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_1001"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_1007"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_1009"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_1013"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_1175"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_1514"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_2000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_fff5"
minzip: Extracted file "/system/etc/usb_modeswitch.d/19d2_fff6"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1a8d_1000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1ab7_5700"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1b7d_0700"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1bbb_f000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1c9e_1001"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1c9e_6061"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1c9e_9200"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1c9e_9913"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1c9e_9e00"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1c9e_f000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1d09_1000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1dd6_1000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1e0e_f000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1edf_6003"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1ee8_0009"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1ee8_0013"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1ee8_0040"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1f28_0021"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1fac_0032"
minzip: Extracted file "/system/etc/usb_modeswitch.d/1fac_0130"
minzip: Extracted file "/system/etc/usb_modeswitch.d/2001_a80b"
minzip: Extracted file "/system/etc/usb_modeswitch.d/201e_2009"
minzip: Extracted file "/system/etc/usb_modeswitch.d/20a6_f00e"
minzip: Extracted file "/system/etc/usb_modeswitch.d/21f5_1000"
minzip: Extracted file "/system/etc/usb_modeswitch.d/230d_0001"
minzip: Extracted file "/system/etc/usb_modeswitch.d/230d_000d"
minzip: Extracted file "/system/etc/usb_modeswitch.d/8888_6500"
minzip: Extracted file "/system/etc/voicemail-conf.xml"
minzip: Extracted file "/system/etc/vold.fstab"
minzip: Extracted file "/system/etc/wifi/wpa_supplicant.conf"
minzip: Extracted file "/system/fonts/AndroidClock.ttf"
minzip: Extracted file "/system/fonts/AndroidClock_Highlight.ttf"
minzip: Extracted file "/system/fonts/AndroidClock_Solid.ttf"
minzip: Extracted file "/system/fonts/AndroidEmoji.ttf"
minzip: Extracted file "/system/fonts/AnjaliNewLipi-light.ttf"
minzip: Extracted file "/system/fonts/Clockopia.ttf"
minzip: Extracted file "/system/fonts/DroidNaskh-Regular-SystemUI.ttf"
minzip: Extracted file "/system/fonts/DroidNaskh-Regular.ttf"
minzip: Extracted file "/system/fonts/DroidSansArmenian.ttf"
minzip: Extracted file "/system/fonts/DroidSansDevanagari-Regular.ttf"
minzip: Extracted file "/system/fonts/DroidSansEthiopic-Regular.ttf"
minzip: Extracted file "/system/fonts/DroidSansFallback.ttf"
minzip: Extracted file "/system/fonts/DroidSansGeorgian.ttf"
minzip: Extracted file "/system/fonts/DroidSansHebrew-Bold.ttf"
minzip: Extracted file "/system/fonts/DroidSansHebrew-Regular.ttf"
minzip: Extracted file "/system/fonts/DroidSansMono.ttf"
minzip: Extracted file "/system/fonts/DroidSansTamil-Bold.ttf"
minzip: Extracted file "/system/fonts/DroidSansTamil-Regular.ttf"
minzip: Extracted file "/system/fonts/DroidSansThai.ttf"
minzip: Extracted file "/system/fonts/DroidSerif-Bold.ttf"
minzip: Extracted file "/system/fonts/DroidSerif-BoldItalic.ttf"
minzip: Extracted file "/system/fonts/DroidSerif-Italic.ttf"
minzip: Extracted file "/system/fonts/DroidSerif-Regular.ttf"
minzip: Extracted file "/system/fonts/Lohit-Bengali.ttf"
minzip: Extracted file "/system/fonts/Lohit-Kannada.ttf"
minzip: Extracted file "/system/fonts/Lohit-Telugu.ttf"
minzip: Extracted file "/system/fonts/MTLmr3m.ttf"
minzip: Extracted file "/system/fonts/NanumGothic.ttf"
minzip: Extracted file "/system/fonts/Roboto-Bold.ttf"
minzip: Extracted file "/system/fonts/Roboto-BoldItalic.ttf"
minzip: Extracted file "/system/fonts/Roboto-Italic.ttf"
minzip: Extracted file "/system/fonts/Roboto-Light.ttf"
minzip: Extracted file "/system/fonts/Roboto-LightItalic.ttf"
minzip: Extracted file "/system/fonts/Roboto-Regular.ttf"
minzip: Extracted file "/system/fonts/Roboto-Thin.ttf"
minzip: Extracted file "/system/fonts/Roboto-ThinItalic.ttf"
minzip: Extracted file "/system/fonts/RobotoCondensed-Bold.ttf"
minzip: Extracted file "/system/fonts/RobotoCondensed-BoldItalic.ttf"
minzip: Extracted file "/system/fonts/RobotoCondensed-Italic.ttf"
minzip: Extracted file "/system/fonts/RobotoCondensed-Regular.ttf"
minzip: Extracted file "/system/framework/am.jar"
minzip: Extracted file "/system/framework/android.policy.jar"
minzip: Extracted file "/system/framework/android.test.runner.jar"
minzip: Extracted file "/system/framework/apache-xml.jar"
minzip: Extracted file "/system/framework/bmgr.jar"
minzip: Extracted file "/system/framework/bouncycastle.jar"
minzip: Extracted file "/system/framework/bu.jar"
minzip: Extracted file "/system/framework/com.android.location.provider.jar"
minzip: Extracted file "/system/framework/com.google.widevine.software.drm.jar"
minzip: Extracted file "/system/framework/content.jar"
minzip: Extracted file "/system/framework/core-junit.jar"
minzip: Extracted file "/system/framework/core.jar"
minzip: Extracted file "/system/framework/ext.jar"
minzip: Extracted file "/system/framework/framework-res.apk"
minzip: Extracted file "/system/framework/framework.jar"
minzip: Extracted file "/system/framework/ime.jar"
minzip: Extracted file "/system/framework/input.jar"
minzip: Extracted file "/system/framework/javax.obex.jar"
minzip: Extracted file "/system/framework/mms-common.jar"
minzip: Extracted file "/system/framework/monkey.jar"
minzip: Extracted file "/system/framework/pm.jar"
minzip: Extracted file "/system/framework/requestsync.jar"
minzip: Extracted file "/system/framework/services.jar"
minzip: Extracted file "/system/framework/settings.jar"
minzip: Extracted file "/system/framework/svc.jar"
minzip: Extracted file "/system/framework/telephony-common.jar"
minzip: Extracted file "/system/framework/uiautomator.jar"
minzip: Extracted file "/system/lib/8192cu.ko"
minzip: Extracted file "/system/lib/Resource.irf"
minzip: Extracted file "/system/lib/amplayer/libcurl_mod.so"
minzip: Extracted file "/system/lib/amplayer/libdash_mod.so"
minzip: Extracted file "/system/lib/amplayer/libmms_mod.so"
minzip: Extracted file "/system/lib/amplayer/libvhls_mod.so"
minzip: Extracted file "/system/lib/crtbegin_so.o"
minzip: Extracted file "/system/lib/crtend_so.o"
minzip: Extracted file "/system/lib/drm/libdrmplayreadyplugin.so"
minzip: Extracted file "/system/lib/drm/libfwdlockengine.so"
minzip: Extracted file "/system/lib/egl/egl.cfg"
minzip: Extracted file "/system/lib/egl/libEGL_mali.so"
minzip: Extracted file "/system/lib/egl/libGLES_android.so"
minzip: Extracted file "/system/lib/egl/libGLESv1_CM_mali.so"
minzip: Extracted file "/system/lib/egl/libGLESv2_mali.so"
minzip: Extracted file "/system/lib/hw/audio.a2dp.default.so"
minzip: Extracted file "/system/lib/hw/audio.primary.amlogic.so"
minzip: Extracted file "/system/lib/hw/audio.primary.default.so"
minzip: Extracted file "/system/lib/hw/audio.r_submix.default.so"
minzip: Extracted file "/system/lib/hw/audio.usb.amlogic.so"
minzip: Extracted file "/system/lib/hw/audio.usb.default.so"
minzip: Extracted file "/system/lib/hw/audio_policy.default.so"
minzip: Extracted file "/system/lib/hw/camera.goldfish.so"
minzip: Extracted file "/system/lib/hw/gps.goldfish.so"
minzip: Extracted file "/system/lib/hw/gralloc.default.so"
minzip: Extracted file "/system/lib/hw/hwcomposer.amlogic.so"
minzip: Extracted file "/system/lib/hw/keystore.default.so"
minzip: Extracted file "/system/lib/hw/lights.goldfish.so"
minzip: Extracted file "/system/lib/hw/local_time.default.so"
minzip: Extracted file "/system/lib/hw/power.default.so"
minzip: Extracted file "/system/lib/hw/screen_source.amlogic.so"
minzip: Extracted file "/system/lib/hw/sensors.goldfish.so"
minzip: Extracted file "/system/lib/libAisound.so"
minzip: Extracted file "/system/lib/libDeflatingDecompressor-v3.so"
minzip: Extracted file "/system/lib/libEGL.so"
minzip: Extracted file "/system/lib/libETC1.so"
minzip: Extracted file "/system/lib/libFFTEm.so"
minzip: Extracted file "/system/lib/libGLES_trace.so"
minzip: Extracted file "/system/lib/libGLESv1_CM.so"
minzip: Extracted file "/system/lib/libGLESv2.so"
minzip: Extracted file "/system/lib/libLineBreak-v2.so"
minzip: Extracted file "/system/lib/libMali.so"
minzip: Extracted file "/system/lib/libNativeFormats-v3.so"
minzip: Extracted file "/system/lib/libOmxAudio.so"
minzip: Extracted file "/system/lib/libOmxBase.so"
minzip: Extracted file "/system/lib/libOmxClock.so"
minzip: Extracted file "/system/lib/libOmxCore.so"
minzip: Extracted file "/system/lib/libOmxVideo.so"
minzip: Extracted file "/system/lib/libOpenMAXAL.so"
minzip: Extracted file "/system/lib/libOpenSLES.so"
minzip: Extracted file "/system/lib/libRS.so"
minzip: Extracted file "/system/lib/libRSDriver.so"
minzip: Extracted file "/system/lib/libSR_AudioIn.so"
minzip: Extracted file "/system/lib/libUMP.so"
minzip: Extracted file "/system/lib/libaac_helix.so"
minzip: Extracted file "/system/lib/libadpcm.so"
minzip: Extracted file "/system/lib/libam_adp.so"
minzip: Extracted file "/system/lib/libam_mw.so"
minzip: Extracted file "/system/lib/libamadec_omx_api.so"
minzip: Extracted file "/system/lib/libamadec_wfd_out.so"
minzip: Extracted file "/system/lib/libamavutils.so"
minzip: Extracted file "/system/lib/libamffmpeg.so"
minzip: Extracted file "/system/lib/libamffmpegadapter.so"
minzip: Extracted file "/system/lib/libaml-ril.so"
minzip: Extracted file "/system/lib/libamplayer.so"
minzip: Extracted file "/system/lib/libamplayerjni.so"
minzip: Extracted file "/system/lib/libamr.so"
minzip: Extracted file "/system/lib/libamvdec.so"
minzip: Extracted file "/system/lib/libandroid.so"
minzip: Extracted file "/system/lib/libandroid_runtime.so"
minzip: Extracted file "/system/lib/libandroid_servers.so"
minzip: Extracted file "/system/lib/libandroidfw.so"
minzip: Extracted file "/system/lib/libape.so"
minzip: Extracted file "/system/lib/libaudioeffect_jni.so"
minzip: Extracted file "/system/lib/libaudioflinger.so"
minzip: Extracted file "/system/lib/libaudioutils.so"
minzip: Extracted file "/system/lib/libbcc.sha1.so"
minzip: Extracted file "/system/lib/libbcc.so"
minzip: Extracted file "/system/lib/libbcinfo.so"
minzip: Extracted file "/system/lib/libbinder.so"
minzip: Extracted file "/system/lib/libc.so"
minzip: Extracted file "/system/lib/libc_malloc_debug_leak.so"
minzip: Extracted file "/system/lib/libc_malloc_debug_qemu.so"
minzip: Extracted file "/system/lib/libcamera_client.so"
minzip: Extracted file "/system/lib/libcamera_metadata.so"
minzip: Extracted file "/system/lib/libcameraservice.so"
minzip: Extracted file "/system/lib/libchromium_net.so"
minzip: Extracted file "/system/lib/libclcore.bc"
minzip: Extracted file "/system/lib/libclcore_neon.bc"
minzip: Extracted file "/system/lib/libcommon_time_client.so"
minzip: Extracted file "/system/lib/libcook.so"
minzip: Extracted file "/system/lib/libcorkscrew.so"
minzip: Extracted file "/system/lib/libcrypto.so"
minzip: Extracted file "/system/lib/libctest.so"
minzip: Extracted file "/system/lib/libcurl.so"
minzip: Extracted file "/system/lib/libcutils.so"
minzip: Extracted file "/system/lib/libdatachunkqueue_alt.so"
minzip: Extracted file "/system/lib/libdbus.so"
minzip: Extracted file "/system/lib/libdefcontainer_jni.so"
minzip: Extracted file "/system/lib/libdiskconfig.so"
minzip: Extracted file "/system/lib/libdl.so"
minzip: Extracted file "/system/lib/libdrm1.so"
minzip: Extracted file "/system/lib/libdrm1_jni.so"
minzip: Extracted file "/system/lib/libdrmframework.so"
minzip: Extracted file "/system/lib/libdrmframework_jni.so"
minzip: Extracted file "/system/lib/libdvbpushjni.so"
minzip: Extracted file "/system/lib/libdvm.so"
minzip: Extracted file "/system/lib/libeffects.so"
minzip: Extracted file "/system/lib/libemoji.so"
minzip: Extracted file "/system/lib/libexfat.so"
minzip: Extracted file "/system/lib/libexif.so"
minzip: Extracted file "/system/lib/libexif_jni.so"
minzip: Extracted file "/system/lib/libexpat.so"
minzip: Extracted file "/system/lib/libext4_utils.so"
minzip: Extracted file "/system/lib/libfaad.so"
minzip: Extracted file "/system/lib/libfilterfw.so"
minzip: Extracted file "/system/lib/libfilterpack_imageproc.so"
minzip: Extracted file "/system/lib/libflac.so"
minzip: Extracted file "/system/lib/libfpscalculator_alt.so"
minzip: Extracted file "/system/lib/libfuse_exfat.so"
minzip: Extracted file "/system/lib/libgabi++.so"
minzip: Extracted file "/system/lib/libgccdemangle.so"
minzip: Extracted file "/system/lib/libgui.so"
minzip: Extracted file "/system/lib/libhardware.so"
minzip: Extracted file "/system/lib/libhardware_legacy.so"
minzip: Extracted file "/system/lib/libharfbuzz.so"
minzip: Extracted file "/system/lib/libhwui.so"
minzip: Extracted file "/system/lib/libiconv.so"
minzip: Extracted file "/system/lib/libicui18n.so"
minzip: Extracted file "/system/lib/libicuuc.so"
minzip: Extracted file "/system/lib/libinput.so"
minzip: Extracted file "/system/lib/libiprouteutil.so"
minzip: Extracted file "/system/lib/libjavacore.so"
minzip: Extracted file "/system/lib/libjni_latinime.so"
minzip: Extracted file "/system/lib/libjni_remoteime.so"
minzip: Extracted file "/system/lib/libjnigraphics.so"
minzip: Extracted file "/system/lib/libjpeg.so"
minzip: Extracted file "/system/lib/libkeystore_client.so"
minzip: Extracted file "/system/lib/liblog.so"
minzip: Extracted file "/system/lib/libm.so"
minzip: Extracted file "/system/lib/libmad.so"
minzip: Extracted file "/system/lib/libmdnssd.so"
minzip: Extracted file "/system/lib/libmedia.so"
minzip: Extracted file "/system/lib/libmedia_jni.so"
minzip: Extracted file "/system/lib/libmedia_native.so"
minzip: Extracted file "/system/lib/libmediaplayerservice.so"
minzip: Extracted file "/system/lib/libmtp.so"
minzip: Extracted file "/system/lib/libnativehelper.so"
minzip: Extracted file "/system/lib/libnativeutils.so"
minzip: Extracted file "/system/lib/libnbaio.so"
minzip: Extracted file "/system/lib/libnetlink.so"
minzip: Extracted file "/system/lib/libnetutils.so"
minzip: Extracted file "/system/lib/libnfc_ndef.so"
minzip: Extracted file "/system/lib/libomx_av_core_alt.so"
minzip: Extracted file "/system/lib/libomx_clock_utils_alt.so"
minzip: Extracted file "/system/lib/libomx_framework_alt.so"
minzip: Extracted file "/system/lib/libomx_timed_task_queue_alt.so"
minzip: Extracted file "/system/lib/libomx_worker_peer_alt.so"
minzip: Extracted file "/system/lib/liboptimization.so"
minzip: Extracted file "/system/lib/libpagemap.so"
minzip: Extracted file "/system/lib/libpcm.so"
minzip: Extracted file "/system/lib/libpcm_wfd.so"
minzip: Extracted file "/system/lib/libpixelflinger.so"
minzip: Extracted file "/system/lib/libplayerjni.so"
minzip: Extracted file "/system/lib/libplayready2.0.so"
minzip: Extracted file "/system/lib/libplayreadysecos_api.so"
minzip: Extracted file "/system/lib/libportable.so"
minzip: Extracted file "/system/lib/libpower.so"
minzip: Extracted file "/system/lib/libpowermanager.so"
minzip: Extracted file "/system/lib/libpush.so"
minzip: Extracted file "/system/lib/libraac.so"
minzip: Extracted file "/system/lib/libreference-ril.so"
minzip: Extracted file "/system/lib/libril.so"
minzip: Extracted file "/system/lib/librs_jni.so"
minzip: Extracted file "/system/lib/librtmp.so"
minzip: Extracted file "/system/lib/librtp_jni.so"
minzip: Extracted file "/system/lib/libsensorservice.so"
minzip: Extracted file "/system/lib/libskia.so"
minzip: Extracted file "/system/lib/libsmoothstreaming_test.so"
minzip: Extracted file "/system/lib/libsonivox.so"
minzip: Extracted file "/system/lib/libsoundpool.so"
minzip: Extracted file "/system/lib/libsparse.so"
minzip: Extracted file "/system/lib/libspeexresampler.so"
minzip: Extracted file "/system/lib/libsqlite.so"
minzip: Extracted file "/system/lib/libsqlite_jni.so"
minzip: Extracted file "/system/lib/libsrec_jni.so"
minzip: Extracted file "/system/lib/libssl.so"
minzip: Extracted file "/system/lib/libstagefright.so"
minzip: Extracted file "/system/lib/libstagefright_amrnb_common.so"
minzip: Extracted file "/system/lib/libstagefright_avc_common.so"
minzip: Extracted file "/system/lib/libstagefright_chromium_http.so"
minzip: Extracted file "/system/lib/libstagefright_enc_common.so"
minzip: Extracted file "/system/lib/libstagefright_foundation.so"
minzip: Extracted file "/system/lib/libstagefright_omx.so"
minzip: Extracted file "/system/lib/libstagefright_platformenc.so"
minzip: Extracted file "/system/lib/libstagefright_soft_aacdec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_aacenc.so"
minzip: Extracted file "/system/lib/libstagefright_soft_adifdec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_adpcmdec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_adtsdec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_alacdec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_amrdec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_amrnbenc.so"
minzip: Extracted file "/system/lib/libstagefright_soft_amrwbenc.so"
minzip: Extracted file "/system/lib/libstagefright_soft_amvp6adec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_amvp6dec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_amvp6fdec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_ddpdec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_flacenc.so"
minzip: Extracted file "/system/lib/libstagefright_soft_g711dec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_h264dec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_h264enc.so"
minzip: Extracted file "/system/lib/libstagefright_soft_latmdec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_mp2dec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_mp3dec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_mpeg4dec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_mpeg4enc.so"
minzip: Extracted file "/system/lib/libstagefright_soft_rawdec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_vorbisdec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_vpxdec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_wmadec.so"
minzip: Extracted file "/system/lib/libstagefright_soft_wmaprodec.so"
minzip: Extracted file "/system/lib/libstagefright_wfd.so"
minzip: Extracted file "/system/lib/libstagefright_yuv.so"
minzip: Extracted file "/system/lib/libstagefrighthw.so"
minzip: Extracted file "/system/lib/libstdc++.so"
minzip: Extracted file "/system/lib/libstlport.so"
minzip: Extracted file "/system/lib/libsubjni.so"
minzip: Extracted file "/system/lib/libsurfaceflinger.so"
minzip: Extracted file "/system/lib/libsurfaceflinger_ddmconnection.so"
minzip: Extracted file "/system/lib/libsuspend.so"
minzip: Extracted file "/system/lib/libsync.so"
minzip: Extracted file "/system/lib/libsystem_server.so"
minzip: Extracted file "/system/lib/libsystemwriteservice.so"
minzip: Extracted file "/system/lib/libsysutils.so"
minzip: Extracted file "/system/lib/libthread_db.so"
minzip: Extracted file "/system/lib/libthreadworker_alt.so"
minzip: Extracted file "/system/lib/libtinyalsa.so"
minzip: Extracted file "/system/lib/libttscompat.so"
minzip: Extracted file "/system/lib/libttspico.so"
minzip: Extracted file "/system/lib/libui.so"
minzip: Extracted file "/system/lib/libusb.so"
minzip: Extracted file "/system/lib/libusbhost.so"
minzip: Extracted file "/system/lib/libutils.so"
minzip: Extracted file "/system/lib/libvariablespeed.so"
minzip: Extracted file "/system/lib/libvideoeditor_core.so"
minzip: Extracted file "/system/lib/libvideoeditor_jni.so"
minzip: Extracted file "/system/lib/libvideoeditor_osal.so"
minzip: Extracted file "/system/lib/libvideoeditor_videofilters.so"
minzip: Extracted file "/system/lib/libvideoeditorplayer.so"
minzip: Extracted file "/system/lib/libvorbisidec.so"
minzip: Extracted file "/system/lib/libwebcore.so"
minzip: Extracted file "/system/lib/libwebrtc_audio_preprocessing.so"
minzip: Extracted file "/system/lib/libwilhelm.so"
minzip: Extracted file "/system/lib/libwpa_client.so"
minzip: Extracted file "/system/lib/libxml2.so"
minzip: Extracted file "/system/lib/libz.so"
minzip: Extracted file "/system/lib/plugins/com.adobe.flashplayer/libflashplayer.so"
minzip: Extracted file "/system/lib/plugins/com.adobe.flashplayer/libysshared.so"
minzip: Extracted file "/system/lib/soundfx/libaudiopreprocessing.so"
minzip: Extracted file "/system/lib/soundfx/libbundlewrapper.so"
minzip: Extracted file "/system/lib/soundfx/libdownmix.so"
minzip: Extracted file "/system/lib/soundfx/libreverbwrapper.so"
minzip: Extracted file "/system/lib/soundfx/libvisualizer.so"
minzip: Extracted file "/system/lib/ssl/engines/libkeystore.so"
minzip: Extracted file "/system/media/audio/alarms/Alarm_Beep_01.ogg"
minzip: Extracted file "/system/media/audio/alarms/Alarm_Beep_02.ogg"
minzip: Extracted file "/system/media/audio/alarms/Alarm_Beep_03.ogg"
minzip: Extracted file "/system/media/audio/alarms/Alarm_Buzzer.ogg"
minzip: Extracte[  127.093097@1] aml_nftl_erase_part: start
d file "/system/media/audio/alarms/Alarm_Classic.ogg"
minzip: Extracted file "/system/media/audio/alarms/Alarm_Rooster_02.ogg"
minzip: Extracted file "/system/media/audio/alarms/Barium.ogg"
minzip: Extracted file "/system/media/audio/alarms/Carbon.ogg"
minzip: Extracted file "/system/media/audio/alarms/Fermium.ogg"
minzip: Extracted file "/system/media/audio/alarms/Hassium.ogg"
minzip: Extracted file "/system/media/audio/alarms/Neptunium.ogg"
minzip: Extracted file "/system/media/audio/alarms/Nobelium.ogg"
minzip: Extracted file "/system/media/audio/alarms/Plutonium.ogg"
minzip: Extracted file "/system/media/audio/alarms/Scandium.ogg"
minzip: Extracted file "/system/media/audio/notifications/Adara.ogg"
minzip: Extracted file "/system/media/audio/notifications/Aldebaran.ogg"
minzip: Extracted file "/system/media/audio/notifications/Altair.ogg"
minzip: Extracted file "/system/media/audio/notifications/Antares.ogg"
minzip: Extracted file "/system/media/audio/notifications/Antimony.ogg"
minzip: Extracted file "/system/media/audio/notifications/Arcturus.ogg"
minzip: Extracted file "/system/media/audio/notifications/Argon.ogg"
minzip: Extracted file "/system/media/audio/notifications/Beat_Box_Android.ogg"
minzip: Extracted file "/system/media/audio/notifications/Bellatrix.ogg"
minzip: Extracted file "/system/media/audio/notifications/Beryllium.ogg"
minzip: Extracted file "/system/media/audio/notifications/Betelgeuse.ogg"
minzip: Extracted file "/system/media/audio/notifications/CaffeineSnake.ogg"
minzip: Extracted file "/system/media/audio/notifications/Canopus.ogg"
minzip: Extracted file "/system/media/audio/notifications/Capella.ogg"
minzip: Extracted file "/system/media/audio/notifications/Castor.ogg"
minzip: Extracted file "/system/media/audio/notifications/CetiAlpha.ogg"
minzip: Extracted file "/system/media/audio/notifications/Cobalt.ogg"
minzip: Extracted file "/system/media/audio/notifications/Cricket.ogg"
minzip: Extracted file "/system/media/audio/notifications/DearDeer.ogg"
minzip: Extracted file "/system/media/audio/notifications/Deneb.ogg"
minzip: Extracted file "/system/media/audio/notifications/Doink.ogg"
minzip: Extracted file "/system/media/audio/notifications/DontPanic.ogg"
minzip: Extracted file "/system/media/audio/notifications/Drip.ogg"
minzip: Extracted file "/system/media/audio/notifications/Electra.ogg"
minzip: Extracted file "/system/media/audio/notifications/F1_MissedCall.ogg"
minzip: Extracted file "/system/media/audio/notifications/F1_New_MMS.ogg"
minzip: Extracted file "/system/media/audio/notifications/F1_New_SMS.ogg"
minzip: Extracted file "/system/media/audio/notifications/Fluorine.ogg"
minzip: Extracted file "/system/media/audio/notifications/Fomalhaut.ogg"
minzip: Extracted file "/system/media/audio/notifications/Gallium.ogg"
minzip: Extracted file "/system/media/audio/notifications/Heaven.ogg"
minzip: Extracted file "/system/media/audio/notifications/Helium.ogg"
minzip: Extracted file "/system/media/audio/notifications/Highwire.ogg"
minzip: Extracted file "/system/media/audio/notifications/Hojus.ogg"
minzip: Extracted file "/system/media/audio/notifications/Iridium.ogg"
minzip: Extracted file "/system/media/audio/notifications/Krypton.ogg"
minzip: Extracted file "/system/media/audio/notifications/KzurbSonar.ogg"
minzip: Extracted file "/system/media/audio/notifications/Lalande.ogg"
minzip: Extracted file "/system/media/audio/notifications/Merope.ogg"
minzip: Extracted file "/system/media/audio/notifications/Mira.ogg"
minzip: Extracted file "/system/media/audio/notifications/OnTheHunt.ogg"
minzip: Extracted file "/system/media/audio/notifications/Palladium.ogg"
minzip: Extracted file "/system/media/audio/notifications/Plastic_Pipe.ogg"
minzip: Extracted file "/system/media/audio/notifications/Polaris.ogg"
minzip: Extracted file "/system/media/audio/notifications/Pollux.ogg"
minzip: Extracted file "/system/media/audio/notifications/Procyon.ogg"
minzip: Extracted file "/system/media/audio/notifications/Proxima.ogg"
minzip: Extracted file "/system/media/audio/notifications/Radon.ogg"
minzip: Extracted file "/system/media/audio/notifications/Rubidium.ogg"
minzip: Extracted file "/system/media/audio/notifications/Selenium.ogg"
minzip: Extracted file "/system/media/audio/notifications/Shaula.ogg"
minzip: Extracted file "/system/media/audio/notifications/Sirrah.ogg"
minzip: Extracted file "/system/media/audio/notifications/SpaceSeed.ogg"
minzip: Extracted file "/system/media/audio/notifications/Spica.ogg"
minzip: Extracted file "/system/media/audio/notifications/Strontium.ogg"
minzip: Extracted file "/system/media/audio/notifications/TaDa.ogg"
minzip: Extracted file "/system/media/audio/notifications/Tejat.ogg"
minzip: Extracted file "/system/media/audio/notifications/Thallium.ogg"
minzip: Extracted file "/system/media/audio/notifications/Tinkerbell.ogg"
minzip: Extracted file "/system/media/audio/notifications/Upsilon.ogg"
minzip: Extracted file "/system/media/audio/notifications/Vega.ogg"
minzip: Extracted file "/system/media/audio/notifications/Voila.ogg"
minzip: Extracted file "/system/media/audio/notifications/Xenon.ogg"
minzip: Extracted file "/system/media/audio/notifications/Zirconium.ogg"
minzip: Extracted file "/system/media/audio/notifications/moonbeam.ogg"
minzip: Extracted file "/system/media/audio/notifications/pixiedust.ogg"
minzip: Extracted file "/system/media/audio/notifications/pizzicato.ogg"
minzip: Extracted file "/system/media/audio/notifications/regulus.ogg"
minzip: Extracted file "/system/media/audio/notifications/sirius.ogg"
minzip: Extracted file "/system/media/audio/notifications/tweeters.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Andromeda.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Aquila.ogg"
minzip: Extracted file "/system/media/audio/ringtones/ArgoNavis.ogg"
minzip: Extracted file "/system/media/audio/ringtones/BOOTES.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Backroad.ogg"
minzip: Extracted file "/system/media/audio/ringtones/BeatPlucker.ogg"
minzip: Extracted file "/system/media/audio/ringtones/BentleyDubs.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Big_Easy.ogg"
minzip: Extracted file "/system/media/audio/ringtones/BirdLoop.ogg"
minzip: Extracted file "/system/m^C Extracted file "/system/media/audio/ringtones/DonMessWivIt.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Draco.ogg"
minzip: Extracted file "/system/media/audio/ringtones/DreamTheme.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Eastern_Sky.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Enter_the_Nexus.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Eridani.ogg"
minzip: Extracted file "/system/media/audio/ringtones/EtherShake.ogg"
minzip: Extracted file "/system/media/audio/ringtones/FreeFlight.ogg"
minzip: Extracted file "/system/media/audio/ringtones/FriendlyGhost.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Funk_Yall.ogg"
minzip: Extracted file "/system/media/audio/ringtones/GameOverGuitar.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Gimme_Mo_Town.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Girtab.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Glacial_Groove.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Growl.ogg"
minzip: Extracted file "/system/media/audio/ringtones/HalfwayHome.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Hydra.ogg"
minzip: Extracted file "/system/media/audio/ringtones/InsertCoin.ogg"
minzip: Extracted file "/system/media/audio/ringtones/LoopyLounge.ogg"
minzip: Extracted file "/system/media/audio/ringtones/LoveFlute.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Lyra.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Machina.ogg"
minzip: Extracted file "/system/media/audio/ringtones/MidEvi[  127.790412@1] aml_nftl_erase_part: erase ok
[  127.794515@1] nftl version 140415a
[  127.797872@1] nftl part attr 0
[  127.800925@1] nftl start:256,32
[  127.804303@1] first
lJaunt.ogg"
minzip: Extracted file "/system/media/audio/ringtones/MildlyAlarming.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Nairobi.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Nassau.ogg"
minzip: Extracted file "/system/media/audio/ringtones/NewPlayer.ogg"
minzip: Extracted file "/system/media/audio/ringtones/No_Limits.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Noises1.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Noises2.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Noises3.ogg"
minzip: Extracted file "/system/media/audio/ringtones/OrganDub.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Orion.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Paradise_Island.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Pegasus.ogg"
minzip: Extracted file "/system/media/audio/ringto[  127.884646@1] average_erase_count:0
[  127.887360@1] second 0,224
[  127.890035@1] all block full!!
[  127.893100@1] free block cnt = 256
[  127.896457@1] new current block is 255
[  127.900186@1] nftl ok!
nes/Perseus.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Playa.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Pyxis.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Revelation.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Rigel.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Ring_Classic_02.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Ring_Digital_02.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Ring_Synth_02.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Ring_Synth_04.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Road_Trip.ogg"
minzip: Extracted file "/system/media/audio/ringtones/RomancingTheTone.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Safari.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Savannah.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Scarabaeus.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Sceptrum.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Seville.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Shes_All_That.ogg"
minzip: Extracted file "/system/media/audio/ringtones/SilkyWay.ogg"
minzip: Extracted file "/system/media/audio/ringtones/SitarVsSitar.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Solarium.ogg"
minzip: Extracted file "/system/media/audio/ringtones/SpringyJalopy.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Steppin_Out.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Terminated.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Testudo.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Themos.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Third_Eye.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Thunderfoot.ogg"
minzip: Extracted file "/system/media/audio/ringtones/TwirlAway.ogg"
minzip: Extracted file "/system/media/audio/ringtones/UrsaMinor.ogg"
minzip: Extracted file "/system/media/audio/ringtones/VeryAlarmed.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Vespa.ogg"
minzip: Extracted file "/system/media/audio/ringtones/World.ogg"
minzip: Extracted file "/system/media/audio/ringtones/Zeta.ogg"
minzip: Extracted file "/system/media/audio/ui/Dock.ogg"
minzip: Extracted file "/system/media/audio/ui/Effect_Tick.ogg"
minzip: Extracted file "/system/media/audio/ui/KeypressDelete.ogg"
minzip: Extracted file "/system/media/audio/ui/KeypressReturn.ogg"
minzip: Extracted file "/system/media/audio/ui/KeypressSpacebar.ogg"
minzip: Extracted file "/system/media/audio/ui/KeypressStandard.ogg"
minzip: Extracted file "/system/media/audio/ui/Lock.ogg"
minzip: Extracted file "/system/media/audio/ui/LowBattery.ogg"
minzip: Extracted file "/system/media/audio/ui/Undock.ogg"
minzip: Extracted file "/system/media/audio/ui/Unlock.ogg"
minzip: Extracted file "/system/media/audio/ui/VideoRecord.ogg"
minzip: Extracted file "/system/media/audio/ui/camera_click.ogg"
minzip: Extracted file "/system/media/audio/ui/camera_focus.ogg"
minzip: Extracted file "/system/media/bootanimation.zip"
minzip: Extracted file "/system/media/screensaver/images/dlna.jpg"
minzip: Extracted file "/system/media/screensaver/images/miracast.jpg"
minzip: Extracted file "/system/media/screensaver/images/phone_remote.jpg"
minzip: Extracted file "/system/package_config/config"
minzip: Extracted file "/system/tts/lang_pico/de-DE_gl0_sg.bin"
minzip: Extracted file "/system/tts/lang_pico/de-DE_ta.bin"
minzip: Extracted file "/system/tts/lang_pico/en-GB_kh0_sg.bin"
minzip: Extracted file "/system/tts/lang_pico/en-GB_ta.bin"
minzip: Extracted file "/system/tts/lang_pico/en-US_lh0_sg.bin"
minzip: Extracted file "/system/tts/lang_pico/en-US_ta.bin"
minzip: Extracted file "/system/tts/lang_pico/es-ES_ta.bin"
minzip: Extracted file "/system/tts/lang_pico/es-ES_zl0_sg.bin"
minzip: Extracted file "/system/tts/lang_pico/fr-FR_nk0_sg.bin"
minzip: Extracted file "/system/tts/lang_pico/fr-FR_ta.bin"
minzip: Extracted file "/system/tts/lang_pico/it-IT_cm0_sg.bin"
minzip: Extracted file "/system/tts/lang_pico/it-IT_ta.bin"
minzip: Extracted file "/system/usr/icu/icudt48l.dat"
minzip: Extracted file "/system/usr/idc/Vendor_222a_Product_0001.idc"
minzip: Extracted file "/system/usr/idc/Vendor_dead_Product_beef.idc"
minzip: Extracted file "/system/usr/idc/ft5x06.idc"
minzip: Extracted file "/system/usr/idc/pixcir168.idc"
minzip: Extracted file "/system/usr/idc/qwerty.idc"
minzip: Extracted file "/system/usr/idc/qwerty2.idc"
minzip: Extracted file "/system/usr/idc/ssd253x-ts.idc"
minzip: Extracted file "/system/usr/keychars/Generic.kcm"
minzip: Extracted file "/system/usr/keychars/Virtual.kcm"
minzip: Extracted file "/system/usr/keychars/qwerty.kcm"
minzip: Extracted file "/system/usr/keychars/qwerty2.kcm"
minzip: Extracted file "/system/usr/keylayout/AVRCP.kl"
minzip: Extracted file "/system/usr/keylayout/Generic.kl"
minzip: Extracted file "/system/usr/keylayout/Vendor_0001_Product_0001.kl"
minzip: Extracted file "/system/usr/keylayout/Vendor_045e_Product_028e.kl"
minzip: Extracted file "/system/usr/keylayout/Vendor_046d_Product_c216.kl"
minzip: Extracted file "/system/usr/keylayout/Vendor_046d_Product_c294.kl"
minzip: Extracted file "/system/usr/keylayout/Vendor_046d_Product_c299.kl"
minzip: Extracted file "/system/usr/keylayout/Vendor_046d_Product_c532.kl"
minzip: Extracted file "/system/usr/keylayout/Vendor_054c_Product_0268.kl"
minzip: Extracted file "/system/usr/keylayout/Vendor_05ac_Product_0239.kl"
minzip: Extracted file "/system/usr/keylayout/Vendor_1b8e_Product_0cec_Version_0001.kl"
minzip: Extracted file "/system/usr/keylayout/Vendor_22b8_Product_093d.kl"
minzip: Extracted file "/system/usr/keylayout/qwerty.kl"
minzip: Extracted file "/system/usr/share/bmd/RFFspeed_501.bmd"
minzip: Extracted file "/system/usr/share/bmd/RFFstd_501.bmd"
minzip: Extracted file "/system/usr/share/zoneinfo/zoneinfo.dat"
minzip: Extracted file "/system/usr/share/zoneinfo/zoneinfo.idx"
minzip: Extracted file "/system/usr/share/zoneinfo/zoneinfo.version"
minzip: Extracted file "/system/vendor/lib/drm/libdrmwvmplugin.so"
minzip: Extracted file "/system/vendor/lib/libWVStreamControlAPI_L3.so"
minzip: Extracted file "/system/vendor/lib/libdrmdecrypt.so"
minzip: Extracted file "/system/vendor/lib/libwvdrm_L3.so"
minzip: Extracted file "/system/vendor/lib/libwvm.so"
minzip: Extracted file "/system/vendor/lib/libwvsecureos_api.so"
minzip: Extracted file "/system/xbin/add-property-tag"
minzip: Extracted file "/system/xbin/btool"
minzip: Extracted file "/system/xbin/busybox"
minzip: Extracted file "/system/xbin/check-lost+found"
minzip: Extracted file "/system/xbin/cpustats"
minzip: Extracted file "/system/xbin/dexdump"
minzip: Extracted file "/system/xbin/latencytop"
minzip: Extracted file "/system/xbin/librank"
minzip: Extracted file "/system/xbin/micro_bench"
minzip: Extracted file "/system/xbin/opcontrol"
minzip: Extracted file "/system/xbin/oprofiled"
minzip: Extracted file "/system/xbin/procmem"
minzip: Extracted file "/system/xbin/procrank"
minzip: Extracted file "/system/xbin/rawbu"
minzip: Extracted file "/system/xbin/sane_schedstat"
minzip: Extracted file "/system/xbin/showmap"
minzip: Extracted file "/system/xbin/showslab"
minzip: Extracted file "/system/xbin/sqlite3"
minzip: Extracted file "/system/xbin/strace"
minzip: Extracted file "/system/xbin/su"
write_raw_image:   partition named "logo"
old nand driver 
mtd: successfully wrote block at 0
mtd: successfully wrote block at 200000
wrote logo partition
write_raw_image:   partition named "recovery"
old nand driver 
mtd: successfully wrote block at 0
mtd: successfully wrote block at 200000
mtd: successfully wrote block at 400000
wrote recovery partition
write_raw_image:   partition named "boot"
old nand driver 
mtd: successfully wrote block at 0
mtd: successfully wrote block at 200000
mtd: successfully wrote block at 400000
wrote boot partition
script result was [0.100000]
start erase volume: /cache
Formatting /cache...
ext4_erase_volum : open
##ext4_erase_volum : erase /dev/block/cache
ext4_erase_volum : OK
Creating filesystem with parameters:
    Size: 469762048
    Block size: 4096
    Blocks per group: 32768
    Inodes per group: 7168
    Inode size: 256
    Journal blocks: 1792
    Label: 
    Blocks: 114688
    Block groups: 4
    Reserved block group size: 31
Created filesystem with 11/28672 inodes and 3694/114688 blocks
warning: wipe_block_device: Discard failed

format volume:/cache sucessed!,now fsync the format device:
start erase volume: /data
Formatting /data...
ext4_erase_volum : open
##ext4_erase_volum : erase /dev/block/data
ext4_erase_volum : OK
Creating filesystem with parameters:
    Size: 1971322880
    Block size: 4096
    Blocks per group: 32768
    Inodes per group: 8032
    Inode size: 256
    Journal blocks: 7520
    Label: 
    Blocks: 481280
    Block groups: 15
    Reserved block group size: 119
Created filesystem with 11/120480 inodes and 15809/481280 blocks
warning: wipe_block_device: Discard failed

format volume:/data sucessed!,now fsync the format device:
start erase volume: /cache
Formatting /cache...
ext4_erase_volum : open
##ext4_erase_volum : erase /dev/block/cache
ext4_erase_volum : OK
Creating filesystem with parameters:
    Size: 469762048
    Block size: 4096
    Blocks per group: 32768
    Inodes per group: 7168
    Inode size: 256
    Journal blocks: 1792
    Label: 
    Blocks: 114688
    Block groups: 4
    Reserved block group size: 31
Created filesystem with 11/28672 inodes and 3694/114688 blocks
warning: wipe_block_device: Discard failed


# [  130.060855@1] EXT4-fs (system): mounted filesystem with ordered data mode. Opts: 
[  130.081327@1] EXT4-fs (data): mounted filesystem with ordered data mode. Opts: 
[  130.091741@1] EXT4-fs (cache): mounted filesystem with ordered data mode. Opts: 


# 
# busybox ifconfig                                                             
lo        Link encap:Local Loopback  
          inet addr:127.0.0.1  Mask:255.0.0.0
          inet6 addr: ::1/128 Scope:Host
          UP LOOPBACK RUNNING  MTU:16436  Metric:1
          RX packets:0 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0 
          RX bytes:0 (0.0 B)  TX bytes:0 (0.0 B)

# busybox netcfg\                                                              
> 
# busybox netcfg                                                               
netcfg: applet not found
# [  261.994327@0] usb 2-1.3: USB disconnect, device number 3
[  270.552833@0] fcode map = 0 press ircode = 0x14, scancode = 0x0061
[  270.702752@0] fcode map = 0 release ircode = 0x14, scancode = 0x0061
[  270.803450@0] fcode map = 0 press ircode = 0x14, scancode = 0x0061
[  270.952749@0] fcode map = 0 release ircode = 0x14, scancode = 0x0061
[  272.903288@0] cacel delay work and reset watch dog
[  272.903333@0] m3_nand_reboot_notifier 1242 
[  272.906617@0] aml_nftl_reboot_notifier :system 0
[  272.911217@0] aml_nftl_reboot_notifier :cache 0
[  272.919095@0] aml_nftl_reboot_notifier :backup 0
[  272.919105@0] aml_nftl_reboot_notifier :data 0
[  272.919110@0] aml_keys_notify_reboot:1597m3_nand_shutdown 1367 chip->options:60a01
[  272.921008@0] Disabling non-boot CPUs ...
[  272.941094@1] IRQ61 no longer affine to CPU1
[  272.941175@1] Disable timerD
[  272.941258@0] CPU1: shutdown
[  272.951505@0] Restarting system with command 'normal_reboot'.
[  2E I3000000032940xf100110203:77500EEEE I400000004294_M6_BL1_3431>2534313
TE : 77172
wait pll-0x03 target is 0204 now it is 0x00000203

DDR clock is 516MHz with Low Power & 1T mode

DDR training :
DX0DLLCR:40000000
DX0DQTR:ffffffff
DX0DQSTR:3db05001
DX1DLLCR:40000000
DX1DQTR:ffffffff
DX1DQSTR:3db05001
DX2DLLCR:40000000
DX2DQTR:ffffffff
DX2DQSTR:3db05001
DX3DLLCR:40000000
DX3DQTR:ffffffff
DX3DQSTR:3db05001
Stage 00 Result 00000000
Stage 01 Result 00000000
Stage 02 Result 00000000
Stage 03 Result 00000000

HHH
Boot From SPI
0x12345678
Boot from internal device 1st SPI RESERVED

System Started



U-boot(m6_mbx_v1@0e184153) (Dec 24 2014 - 16:51:00)

aml_rtc_init
aml rtc init first time!
Clear HDMI KSV RAM
DRAM:  1 GiB
relocation Offset is: 105e8000
NAND:  Amlogic nand flash uboot driver, Version U1.06.017 (c) 2010 Amlogic Inc.
SPI BOOT : continue i 0
No NAND device found!!!
NAND device id: ad d7 94 91 60 44 
aml_chip->hynix_new_nand_type =: 4 
NAND device: Manufacturer ID: 0xad, Chip ID: 0xad (Hynix C revision 20nm NAND 4GiB H27UBG8T2C)
1 NAND chips detected
#####aml_nand_init, with RB pins and chip->chip_delay:20
bus_cycle=5, bus_timing=6, start_cycle=6, end_cycle=7,system=5.0ns
oob size is not enough for selected bch mode: NAND_BCH60_1K_MODE force bch to mode: NAND_BCH40_1K_MODE
aml_chip->oob_fill_cnt =64,aml_chip->oob_size =640,bch_bytes =70
multi plane error for selected plane mode: NAND_TWO_PLANE_MODE force plane to : NAND_SINGLE_PLANE_MODE
aml_nand_init:oobmul =1,chip->ecc.layout->oobfree[0].length=16,aml_chip->oob_size=640
aml_nand_get_read_default_value_hynix 980 get default reg value at blk:0, page:7
aml nand env valid addr: 418000 
key start_blk=2040,end_blk=2047,aml_nand_key_init:684
aml nand key valid addr: ff000000 
aml nand key valid addr: ff200000 
aml nand key valid addr: ff400000 
aml nand key valid addr: ff600000 
CONFIG_KEYSIZE=0x10000; KEYSIZE=0xfffc; bbt=0x1330; default_keyironment_size=0xeccc
i=0,register --- nand_key
Creating 8 MTD partitions on "nandnormal":
0x000000c00000-0x000001400000 : "logo"
0x000001400000-0x000001c00000 : "aml_logo"
0x000001c00000-0x000002400000 : "recovery"
0x000002400000-0x000008c00000 : "boot"
0x000008c00000-0x000048c00000 : "system"
0x000048c00000-0x000068c00000 : "cache"
0x000068c00000-0x000078c00000 : "backup"
0x000078c00000-0x0000ff000000 : "data"
nandnormal initialized ok
detect mx chiprevD :1 and nand_type: 4
nand_curr_device =1
MMC:   SDIO Port B: 0, SDIO Port C: 1
SPI BOOT,spi_env_relocate_spec : env_relocate_spec 53 
SF: Detected MX25L3205D with page size 256, total 4 MiB

SPI NOR Flash have write protect!!!
In:    serial
Out:   serial
Err:   serial
aml_i2c_init
register usb cfg[0] = 9fe8292c
Net:   Meson_Ethernet
init suspend firmware done. (ret:0)
efuse version is not selected.
###  main_loop entered: bootdelay=1

### main_loop: bootcmd="setenv bootcmd run compatible_boot; saveenv; run compatible_boot; run compatible_boot"
Hit any key to stop autoboot:  0 
(Re)start USB...
USB:   dwc_usb driver version: 2.94 6-June-2012
USB (1) peri reg base: c1108420
USB (1) use clock source: XTAL input
USB (1) PHY Clock not detected!
USB (1) base addr: 0xc90c0000
Force id mode: Host
dwc_otg: Highspeed device found !

scanning bus for devices... 1 USB Device(s) found
       scanning bus for storage devices... 0 Storage Device(s) found
##aml_autoscript---ERROR: USB device not find!!!
Saving Environment to SPI Flash...
SPI BOOT,spi_saveenv : saveenv 93 
Erasing SPI flash...Writing to SPI flash...done
detect_storage
nand exist return 0
setenv storage nand
compatible nand
detect_storage
nand exist return 0
setenv storage nand
liukevin reboot_mode(0xc8100004)=0x1010101
reboot_mode=normal
SARADC open channel(4).
SARADC open channel(4).
ir init
Booting from nand ...

NAND read: logo whole chip
 8388608 bytes read: OK

NAND read: boot offset 0x0, size 0x600000
 6291456 bytes read: OK
pre-clear hdmi ram
hdcp get form storage medium: nand
name=nand_key nand_key
read:addr:0xff000000,phy_blk_addr:2040,phy_page_addr:0,aml_nand_get_key:125
device:nand, init key ok!!
don't found keyname,uboot_key_read:1502
prefetch hdcp keys from nand failed
## Booting kernel from Legacy Image at 82000000 ...
   Image Name:   Linux-3.0.50
   Image Type:   ARM Linux Kernel Image (lzma compressed)
   Data Size:    3526982 Bytes = 3.4 MiB
   Load Address: 80008000
   Entry Point:  80008000
   Verifying Checksum ... OK
        Ramdisk start addr = 0x8235e000, len = 0x109704
board_usb_stop cfg: 0
   Uncompressing Kernel Image ... OK
machid from environment: 0x4e27 
EFUSE machid is not set.
Using machid 0x4e27 from environment

Starting kernel ...

[    0.000000@0] Initializing cgroup subsys cpu
[    0.000000@0] Linux version 3.0.50 (mx@xy) (gcc version 4.6.x-google 20120106 (prerelease) (GCC) ) #20 SMP PREEMPT Tue Dec 30 18:30:45 CST 2014
[    0.000000@0] CPU: ARMv7 Processor [413fc090] revision 0 (ARMv7), cr=10c53c7d
[    0.000000@0] CPU: VIPT nonaliasing data cache, VIPT aliasing instruction cache
[    0.000000@0] Machine: Amlogic Meson6 g02 customer platform
[    0.000000@0] Ignoring unrecognised tag 0x00000000
[    0.000000@0] early_mem:532: start=0x80000000, size=0x4000000
[    0.000000@0] early_mem:563: start=0x8f100000, size=0x10e00000
[    0.000000@0] early_mem:571: start=0xa0000000, size=0x20000000
[    0.000000@0] Memory policy: ECC disabled, Data cache writealloc
[    0.000000@0] PERCPU: Embedded 7 pages/cpu @c12a3000 s5600 r8192 d14880 u32768
[    0.000000@0] Built 1 zonelists in Zone order, mobility grouping on.  Total pages: 214528
[    0.000000@0] Kernel command line: root=/dev/cardblksd2 rw rootfstype=ext3 rootwait init=/init console=ttyS0,115200n8 logo=osd1,0x84100000,720p,full androidboot.resolution=720p hdmimode=720p cvbsmode=480cvbs hlt vmalloc=256m mem=1024m a9_clk_max=1512000000 vdachwswitch=cvbs hdmitx= mac=84:26:90:00:00:02
[    0.000000@0] osd1:1
[    0.000000@0] 720p:6
[    0.000000@0] full:2
[    0.000000@0] kernel get hdmimode form uboot is 720p
[    0.000000@0] kernel get cvbsmode form uboot is 480cvbs
[    0.000000@0] HDMI DEBUG: hdmitx_boot_para_setup [1931]
[    0.000000@0] ******** uboot setup mac-addr: 84:26:90:0:0:2
[    0.000000@0] PID hash table entries: 2048 (order: 1, 8192 bytes)
[    0.000000@0] Dentry cache hash table entries: 65536 (order: 6, 262144 bytes)
[    0.000000@0] Inode-cache hash table entries: 32768 (order: 5, 131072 bytes)
[    0.000000@0] Memory: 64MB 270MB 512MB = 846MB total
[    0.000000@0] Memory: 847092k/847092k available, 19212k reserved, 524288K highmem
[    0.000000@0] Virtual kernel memory layout:
[    0.000000@0]     vector  : 0xffff0000 - 0xffff1000   (   4 kB)
[    0.000000@0]     fixmap  : 0xfff00000 - 0xfffe0000   ( 896 kB)
[    0.000000@0]     DMA     : 0xffc00000 - 0xffe00000   (   2 MB)
[    0.000000@0]     vmalloc : 0xe0000000 - 0xf0000000   ( 256 MB)
[    0.000000@0]     lowmem  : 0xc0000000 - 0xdff00000   ( 511 MB)
[    0.000000@0]     pkmap   : 0xbfe00000 - 0xc0000000   (   2 MB)
[    0.000000@0]     modules : 0xbf000000 - 0xbfe00000   (  14 MB)
[    0.000000@0]       .init : 0xc0008000 - 0xc0037000   ( 188 kB)
[    0.000000@0]       .text : 0xc0037000 - 0xc08958f4   (8571 kB)
[    0.000000@0]       .data : 0xc0896000 - 0xc08fe480   ( 418 kB)
[    0.000000@0]        .bss : 0xc08fe4a4 - 0xc0a99af8   (1646 kB)
[    0.000000@0] SLUB: Genslabs=13, HWalign=32, Order=0-3, MinObjects=0, CPUs=2, Nodes=1
[    0.000000@0] Preemptible hierarchical RCU implementation.
[    0.000000@0]        RCU debugfs-based tracing is enabled.
[    0.000000@0] NR_IRQS:256
[    0.000000@0] gic_init: irq_offset=0
[    0.000000@0] sched_clock: 32 bits at 1000kHz, resolution 1000ns, wraps every 4294967ms
[    0.000000@0] MESON TIMER-A c08a9ec0
[    0.000000@0] Disable timerA
[    0.000000@0] Console: colour dummy device 80x30
[    0.000000@0] console [ttyS0] enabled
[    0.278128@0] Calibrating delay loop... 2393.70 BogoMIPS (lpj=11968512)
[    0.340076@0] pid_max: default: 32768 minimum: 301
[    0.342262@0] Mount-cache hash table entries: 512
[    0.347471@0] Initializing cgroup subsys cpuacct
[    0.351430@0] Initializing cgroup subsys freezer
[    0.356009@0] CPU: Testing write buffer coherency: ok
[    0.361170@0] MESON TIMER-B c08a9f80
[    0.364553@0] Disable timerA
[    0.367407@0] Disable timerB
[    0.370277@0] Disable timerA
[    0.373257@0] L310 cache controller enabled
[    0.377305@0] l2x0: 8 ways, CACHE_ID 0x4100a0c8, AUX_CTRL 0x7e462c01, Cache size: 524288 B
[    0.385599@0]  prefetch=0x31000006
[    0.388933@0] ===actlr=0x41
[    0.391718@0] ===actlr=0x4b
[    0.394488@0] SCU_CTRL: scu_ctrl=0x69
[    0.398134@0] pl310: aux=0x7e462c01, prefetch=0x31000006
[    0.501454@1] CPU1: Booted secondary processor
[    0.561195@1] MESON TIMER-D c08aa100
[    0.561220@0] Brought up 2 CPUs
[    0.561228@0] SMP: Total of 2 processors activated (4787.40 BogoMIPS).
[    0.573188@1] Disable timerD
[    0.576205@0] devtmpfs: initialized
[    0.583314@0] clkrate [ xtal ] : 24000000
[    0.583515@0] clkrate [ pll_sys ] : 1200000000
[    0.587941@0] clkrate [ pll_fixed ] : 2000000000
[    0.592557@0] clkrate [ pll_vid2 ] : 378000000
[    0.596968@0] clkrate [ pll_hpll ] : 378000000
[    0.601467@0] clkrate [ pll_ddr ] : 516000000
[    0.605735@0] clkrate [ a9_clk ] : 1200000000
[    0.610139@0] clkrate [ clk81 ] : 200000000
[    0.614344@0] clkrate [ usb0 ] : 0
[    0.617691@0] clkrate [ usb1 ] : 12000000
[    0.622594@0] print_constraints: dummy: 
[    0.625735@0] boot_monitor: device successfully initialized.
[    0.631195@0] ** enable watchdog
[    0.634462@0] boot_monitor: driver successfully loaded.
[    0.639694@0] NET: Registered protocol family 16
[    0.645119@0] ***vcck: vcck_pwm_init
[    0.647577@0] ****** aml_eth_pinmux_setup() ******
[    0.652535@0] ****** aml_eth_clock_enable() ******
[    0.657293@0] ****** aml_eth_reset() ******
[    0.691182@0] register lm device 0
[    0.691307@0] register lm device 1
[    0.695354@0] usb_wifi_power Off
[    0.695648@0] chip version=ffbfbfff
[    0.699117@0] vdac_switch_init_module
[    0.702694@0] vdac_switch mode = 0
[    0.706211@0] tv_init_module
[    0.708908@0] major number 254 for disp
[    0.712822@0] vout_register_server
[    0.716195@0] register tv module server ok 
[    0.720554@0] /home/mx/openlinux-jbmr1/common/drivers/amlogic/i2c/aml_i2c.c : aml_i2c_init
[    0.728724@0] /home/mx/openlinux-jbmr1/common/drivers/amlogic/i2c/aml_i2c.c : aml_i2c_probe
[    0.737038@0] master_no = 0, resource = c08f435c, maseter_regs=f1108500
[    0.743767@0] aml-i2c aml-i2c.0: add adapter aml_i2c_adap0(df811828)
[    0.749962@0] aml-i2c aml-i2c.0: aml i2c bus driver.
[    0.755026@0] /home/mx/openlinux-jbmr1/common/drivers/amlogic/i2c/aml_i2c.c : aml_i2c_probe
[    0.763252@0] master_no = 1, resource = c08f43b4, maseter_regs=f11087c0
[    0.769942@0] aml-i2c aml-i2c.1: add adapter aml_i2c_adap1(df812028)
[    0.776183@0] aml-i2c aml-i2c.1: aml i2c bus driver.
[    0.781207@0] /home/mx/openlinux-jbmr1/common/drivers/amlogic/i2c/aml_i2c.c : aml_i2c_probe
[    0.789456@0] master_no = 2, resource = c08f440c, maseter_regs=f3100500
[    0.796155@0] aml-i2c aml-i2c.2: add adapter aml_i2c_adap2(df812828)
[    0.802397@0] aml-i2c aml-i2c.2: aml i2c bus driver.
[    0.807496@0] HDMI DEBUG: amhdmitx_init [1831]
[    0.811771@0] HDMI Ver: 2013Aug25a
[    0.815163@0] HDMI DEBUG: amhdmitx_probe [1638]
[    0.819946@1] Set HDMI:Chip C
[    0.822636@1] HDMI DEBUG: HDMITX_M1B_Init [3620]
[    0.827215@1] HDMI DEBUG: HDMITX_M1B_Init [3623]
[    0.831827@1] HDMI DEBUG: hdmi_hw_init [1207]
[    0.831832@0] HDMI: get hdmi platform data
[    0.831837@0] HDMI 5V Power On
[    0.843320@1] hdmitx: stat: hdmitx: misc cmd 0x15000000
[    0.848483@1] HDMI: get brd phy data
[    0.852048@1] hdmi phy setting
[    0.855120@1] HDMI: get brd phy data
[    0.858633@1] hdmi phy setting
[    0.862157@1] HDMI: reset intr mask
[    0.881229@1] HDMI DEBUG: hdmi_task_handle [1268]
[    0.881237@0] HDMI irq 1
[    0.881248@0] Reg0x196 = 0x2  Reg0x80 = 0x1  Reg0x26 = 0xc
[    0.881265@0] Reg0x196 = 0x42  Reg0x80 = 0x1  Reg0x26 = 0x4c
[    0.897228@0] bio: create slab <bio-0> at 0
[    0.898901@0] SCSI subsystem initialized
[    0.902343@0] usbcore: registered new interface driver usbfs
[    0.907840@0] usbcore: registered new interface driver hub
[    0.913417@0] usbcore: registered new device driver usb
[    0.918834@0] not display in uboot
[    0.921887@0] [0x1b7e] = 0xff
[    0.924654@0] [0x105f] = 0x0
[    0.927519@0] [0x109d] = 0x814d3928
[    0.930990@0] [0x109e] = 0x6b425012
[    0.934482@0] [0x109f] = 0x110
[    0.937500@0] [0x109c] = 0x1043e
[    0.940712@0] [0x1066] = 0x10843
[    0.943929@0] [0x1059] = 0x100
[    0.946961@0] [0x105f] = 0x80000
[    0.950173@0] [0x105f] = 0x88001
[    0.953397@0] [0x105f] = 0x80003
[    0.956596@0] [0x104a] = 0x101
[    0.959634@0] [0x107f] = 0x8c0000c3
[    0.963112@0] [0x1bb8] = 0x52
[    0.966057@0] [0x1b62] = 0x2029
[    0.969182@0] [0x1b8d] = 0x4040
[    0.972312@0] [0x1b8e] = 0x19
[    0.975258@0] [0x1b94] = 0x288
[    0.978296@0] [0x1b95] = 0xc87
[    0.981339@0] [0x1b97] = 0xce3
[    0.984372@0] [0x1b98] = 0x50
[    0.987323@0] [0x1b99] = 0xf0
[    0.990274@0] [0x1b9a] = 0x50
[    0.993231@0] [0x1b9b] = 0x2b0
[    0.996264@0] [0x1b9c] = 0xcb0
[    0.999302@0] [0x1b9d] = 0x4
[    1.002171@0] [0x1b9e] = 0x8
[    1.005030@0] [0x1ba1] = 0x4
[    1.007895@0] [0x1ba2] = 0x8
[    1.010759@0] [0x1ba4] = 0x288
[    1.013802@0] [0x1ba3] = 0xc87
[    1.016835@0] [0x1ba6] = 0x1d
[    1.019786@0] [0x1baf] = 0x2ec
[    1.022829@0] [0x1ba7] = 0x100
[    1.025863@0] [0x1ba8] = 0xa8
[    1.028814@0] [0x1ba9] = 0xa8
[    1.031770@0] [0x1baa] = 0x100
[    1.034803@0] [0x1bab] = 0x0
[    1.037667@0] [0x1bac] = 0x5
[    1.040532@0] [0x1bae] = 0x2ed
[    1.043578@0] [0x1b68] = 0x100
[    1.046608@0] [0x1b60] = 0x0
[    1.049472@0] [0x1b6e] = 0x200
[    1.052515@0] [0x1b58] = 0x0
[    1.055374@0] [0x1b7e] = 0x0
[    1.058239@0] [0x1b64] = 0x9061
[    1.061369@0] [0x1b65] = 0xa061
[    1.064488@0] [0x1b66] = 0xb061
[    1.067613@0] [0x1b78] = 0x1
[    1.070478@0] [0x1b79] = 0x1
[    1.073347@0] [0x1b7a] = 0x1
[    1.076206@0] [0x1b7b] = 0x1
[    1.079071@0] [0x1b7c] = 0x1
[    1.081940@0] [0x1b7d] = 0x1
[    1.084800@0] [0x271a] = 0xa
[    1.087664@0] [0x1bfc] = 0x1000
[    1.090789@0] [0x1c0d] = 0x3102
[    1.093918@0] [0x1c0e] = 0x54
[    1.096865@0] [0x1b80] = 0x1
[    1.099729@0] [0x1b57] = 0x0
[    1.102599@0] tvoutc_setmode[328]
[    1.105893@0] mode is: 6
[    1.108411@0] VPU_VIU_VENC_MUX_CTRL: 0xa
[    1.112320@0] viu chan = 1
[    1.115006@0] VPU_VIU_VENC_MUX_CTRL: 0xa
[    1.150938@0] request_fiq:152: fiq=35
[    1.150968@0] request_fiq:186: end
[    1.175354@0] print_constraints: vcck: 1070 <--> 1330 mV at 1330 mV 
[    1.176508@0] Advanced Linux Sound Architecture Driver Version 1.0.24.
[    1.183561@0] cfg80211: Calling CRDA to update world regulatory domain
[    1.189608@0] Switching to clocksource Timer-E
[    1.201210@0] Switched to NOHz mode on CPU #0
[    1.201221@1] Switched to NOHz mode on CPU #1
[    1.206820@1] MXL: register mxl101 demod driver
[    1.208778@1] register avl6211 demod driver
[    1.212943@1] SI: register si2168 demod driver
[    1.217534@1] [si2176..]si2176_tuner_init.
[    1.221527@1] [si2196..]si2196_tuner_init.
[    1.225624@1] [ctc703_module_init]:ctc703 tuner module  init
[    1.231291@1] NET: Registered protocol family 2
[    1.235816@1] IP route cache hash table entries: 16384 (order: 4, 65536 bytes)
[    1.243184@1] TCP established hash table entries: 65536 (order: 7, 524288 bytes)
[    1.250946@1] TCP bind hash table entries: 65536 (order: 7, 786432 bytes)
[    1.257767@1] TCP: Hash tables configured (established 65536 bind 65536)
[    1.263734@1] TCP reno registered
[    1.267027@1] UDP hash table entries: 256 (order: 1, 8192 bytes)
[    1.273019@1] UDP-Lite hash table entries: 256 (order: 1, 8192 bytes)
[    1.279655@1] NET: Registered protocol family 1
[    1.284136@1] Unpacking initramfs...
[    1.340779@1] Freeing initrd memory: 1060K
[    1.341936@1] highmem bounce pool size: 64 pages
[    1.344142@1] ashmem: initialized
[    1.357668@1] NTFS driver 2.1.30 [Flags: R/O].
[    1.358057@1] fuse init (API version 7.16)
[    1.361150@1] msgmni has been set to 632
[    1.365385@1] io scheduler noop registered
[    1.368534@1] io scheduler deadline registered (default)
[    1.403960@1] HDMI: EDID Ready
[    1.404030@1] CEC: Physical address: 0x1000
[    1.405535@1] CEC: Physical address: 1.0.0.0
[    1.409798@1] hdmitx: edid: found IEEEOUT
[    1.414170@1] hdmitx: stat: hdmitx: misc cmd 0x15000000
[    1.419217@1] HDMI: get current mode: 720p
[    1.423258@1] hdmitx: stat: hdmitx: misc cmd 0x15000000
[    1.428494@1] hdmitx: already init VIC = 0  Now VIC = 4
[    1.433721@1] set mode VIC 4 (cd0,cs0,pm1,vd0,1) 
[    1.438443@1] HDMI DEBUG: hdmi_hw_reset [1386]
[    1.442818@1] HDMI: get brd phy data
[    1.446406@1] hdmi phy setting
[    1.454026@1] HDMI: get brd phy data
[    1.454233@1] hdmi phy setting
[    1.456181@1] HDMI DEBUG: hdmitx_set_pll [2069]
[    1.460473@1] param->VIC:4
[    1.463174@1] mode is: 6
[    1.465742@1] VPU_VIU_VENC_MUX_CTRL: 0xa
[    1.469624@1] viu chan = 1
[    1.472290@1] VPU_VIU_VENC_MUX_CTRL: 0xa
[    1.509428@1] Sink is HDMI device
[    1.509462@1] hdmitx: config: hdmitx: conf cmd 0x14000002
[    1.512485@1] Source in HDMI Mode
[    1.515981@1] CEC not ready
[    1.608450@1] loop: module loaded
[    1.610617@1] PPP generic driver version 2.4.2
[    1.611123@1] PPP Deflate Compression module registered
[    1.615773@1] PPP BSD Compression module registered
[    1.620999@1] PPP MPPE Compression module registered
[    1.625601@1] HDMI: audio channel num is 0
[    1.629653@1] current VIC: 4
[    1.632508@1] audio sample rate: 0
[    1.633632@0] NET: Registered protocol family 24
[    1.633644@0] tun: Universal TUN/TAP device driver, 1.6
[    1.633649@0] tun: (C) 1999-2004 Max Krasnyansky <maxk@qualcomm.com>
[    1.652101@1] HDMI: reset audio N para
[    1.655805@1] PCM out to HDMI
[    1.658829@0] usbcore: registered new interface driver asix
[    1.664421@0] usbcore: registered new interface driver cdc_ether
[    1.670361@0] usbcore: registered new interface driver net1080
[    1.671663@1] Time out: AIU_HDMI_CLK_DATA_CTRL
[    1.671669@1] i2s_to_spdif_flag:1 
[    1.671672@1] Enable audio spdif to HDMI
[    1.671722@1] HDMI: set audio param
[    1.691440@0] usbcore: registered new interface driver cdc_subset
[    1.697452@0] usbcore: registered new interface driver zaurus
[    1.703090@0] cdc_ncm: 04-Aug-2011
[    1.706606@0] usbcore: registered new interface driver cdc_ncm
[    1.712362@0] usbcore: registered new interface driver qf9700
[    1.718118@0] usbcore: registered new interface driver sr9600
[    1.724109@0] usbcore: registered new interface driver cdc_acm
[    1.729551@0] cdc_acm: USB Abstract Control Model driver for USB modems and ISDN adapters
[    1.737736@0] Initializing USB Mass Storage driver...
[    1.742851@0] usbcore: registered new interface driver usb-storage
[    1.748945@0] USB Mass Storage support registered.
[    1.753922@0] usbcore: registered new interface driver usbserial
[    1.759670@0] usbserial: USB Serial Driver core
[    1.764292@0] USB Serial support registered for GSM modem (1-port)
[    1.770452@0] usbcore: registered new interface driver option
[    1.776121@0] option: v0.7.2:USB Driver for GSM modems
[    1.781927@0] mousedev: PS/2 mouse device common for all mice
[    1.788204@0] usbcore: registered new interface driver iforce
[    1.793829@0] usbcore: registered new interface driver xpad
[    1.799151@0] i2c /dev entries driver
[    1.802921@0] lirc_dev: IR Remote Control driver registered, major 250 
[    1.808479@0] IR NEC protocol handler initialized
[    1.813139@0] IR RC5(x) protocol handler initialized
[    1.818108@0] IR RC6 protocol handler initialized
[    1.822774@0] IR JVC protocol handler initialized
[    1.827470@0] IR Sony protocol handler initialized
[    1.832235@0] IR RC5 (streamzap) protocol handler initialized
[    1.837972@0] IR LIRC bridge handler initialized
[    1.842564@0] Linux video capture interface: v2.00
[    1.847622@0] usbcore: registered new interface driver uvcvideo
[    1.853240@0] USB Video Class driver (v1.1.0)
[    1.858443@0] device-mapper: uevent: version 1.0.3
[    1.863004@0] device-mapper: ioctl: 4.20.0-ioctl (2011-02-02) initialised: dm-devel@redhat.com
[    1.871256@0] cpuidle: using governor ladder
[    1.875220@0] cpuidle: using governor menu
[    1.879728@0] usbcore: registered new interface driver usbhid
[    1.885024@0] usbhid: USB HID core driver
[    1.889778@0] logger: created 256K log 'log_main'
[    1.893956@0] logger: created 256K log 'log_events'
[    1.898793@0] logger: created 256K log 'log_radio'
[    1.903568@0] logger: created 256K log 'log_system'
[    1.908304@0] vout_init_module
[    1.911264@0] start init vout module 
[    1.915079@0] create  vout attribute ok 
[    1.919058@0] ge2d_init
[    1.921385@0] ge2d_dev major:249
[    1.925438@0] ge2d start monitor
[    1.928025@0] osd_init
[    1.928031@1] ge2d workqueue monitor start
[    1.934559@0] [osd0] 0x84100000-0x850fffff
[    1.938789@0] Frame buffer memory assigned at phy:0x84100000, vir:0xe1000000, size=16384K
[    1.946737@0] ---------------clear framebuffer0 memory  
[    1.963951@0] [osd1] 0x85100000-0x851fffff
[    1.964000@0] Frame buffer memory assigned at phy:0x85100000, vir:0xe0200000, size=1024K
[    1.970473@0] init fbdev bpp is :24
[    1.980526@0] osd probe ok  
[    1.987254@1] amlvideo-000: V4L2 device registered as video10
[    1.988028@1]  set pinmux c08f48d4
[    1.990743@1]  set pinmux c08f48dc
[    2.033590@1] UART_ttyS0:(irq = 122)
[    2.063584@1] UART_ttyS3:(irq = 125)
[    2.063735@1] dwc_otg: version 2.94a 05-DEC-2012
[    2.566020@1] USB (0) use clock source: XTAL input
[    2.653560@1] hdmitx: ddc: cmd 0x10000002
[    2.653588@1] HDMITX: no HDCP key available
[    2.656088@1] hdmitx: ddc: cmd 0x10000002
[    2.660085@1] HDMITX: no HDCP key available
[    2.777760@1] Core Release: 2.94a
[    2.777781@1] Setting default values for core params
[    3.180468@1] Using Buffer DMA mode
[    3.180490@1] OTG VER PARAM: 1, OTG VER FLAG: 1
[    3.182827@1] Working on port type = OTG
[    3.186753@1] Current port type: SLAVE
[    3.190515@1] dwc_otg lm0: DWC OTG Controller
[    3.194908@1] dwc_otg lm0: new USB bus registered, assigned bus number 1
[    3.201509@1] dwc_otg lm0: irq 62, io mem 0x00000000
[    3.207290@1] hub 1-0:1.0: USB hub found
[    3.210352@1] hub 1-0:1.0: 1 port detected
[    3.214745@1] Dedicated Tx FIFOs mode
[    3.218211@1] using timer detect id change, df108800
[    3.323591@0] HOST mode
[    3.423161@1] Core Release: 2.94a
[    3.423181@1] Setting default values for core params
[    3.523380@0] Using Buffer DMA mode
[    3.523403@0] OTG VER PARAM: 1, OTG VER FLAG: 1
[    3.576616@0] Init: Port Power? op_state=1
[    3.576638@0] Init: Power Port (0)
[    3.578453@0] set usb port power on (board gpio 25)!
[    3.825913@1] Using Buffer DMA mode
[    3.825935@1] OTG VER PARAM: 1, OTG VER FLAG: 1
[    3.828271@1] Working on port type = HOST
[    3.832313@1] dwc_otg lm1: DWC OTG Controller
[    3.836661@1] dwc_otg lm1: new USB bus registered, assigned bus number 2
[    3.843309@1] dwc_otg lm1: irq 63, io mem 0x00000000
[    3.851324@1] Init: Port Power? op_state=1
[    3.852314@1] Init: Power Port (0)
[    3.856536@1] hub 2-0:1.0: USB hub found
[    3.859616@1] hub 2-0:1.0: 1 port detected
[    3.864135@1] Amlogic nand flash Kernel driver, Version K1.06.018 (c) 2010 Amlogic Inc.
[    3.871671@1] ####Version of Uboot must be newer than U1.06.011!!!!! 
[    3.878190@1] 2
[    3.879832@1] SPI BOOT, m3_nand_probe continue i 0
[    3.884668@1] chip->controller=c0a6b964
[    3.888423@1] checking ChiprevD :0
[    3.891808@1] aml_nand_probe checked chiprev:0
[    3.896271@1] init bus_cycle=17, bus_timing=10, start_cycle=10, end_cycle=10,system=5.0ns
[    3.904848@1] No NAND device found.
[    3.908105@1] NAND device id: ad d7 94 91 60 44 
[    3.912468@1] aml_chip->hynix_new_nand_type =: 4 
[    3.917182@1] NAND device: Manufacturer ID: 0xad, Chip ID: 0xad (Hynix C revision 20nm NAND 4GiB H27UBG8T2C)
[    3.927115@1] #####aml_nand_init, with RB pins and chip->chip_delay:20
[    3.933477@1] bus_cycle=4, bus_timing=5, start_cycle=5, end_cycle=6,system=5.0ns
[    3.940876@1] oob size is not enough for selected bch mode: NAND_BCH60_1K_MODE force bch to mode: NAND_BCH40_1K_MODE
[    3.951399@1] aml_chip->oob_fill_cnt =64,aml_chip->oob_size =640,bch_bytes =70
[    3.958581@1] multi plane error for selected plane mode: NAND_TWO_PLANE_MODE force plane to : NAND_SINGLE_PLANE_MODE
[    3.969115@1] Indeed it is in host mode hprt0 = 00021501
[    3.977159@0]  oob layout use nand base oob layout oobsize = 16,oobmul =1,mtd->oobsize =640,aml_chip->oob_size =640
[    3.985963@0] aml_nand_get_read_default_value_hynix 913 get default reg value at blk:0, page:7
[    3.993367@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb0):    value:0x3c, for chip[0]
[    4.002320@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb1):    value:0x36, for chip[0]
[    4.011256@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb2):    value:0x5c, for chip[0]
[    4.020196@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb3):    value:0xa2, for chip[0]
[    4.029137@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb4):    value:0x40, for chip[0]
[    4.038086@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb5):    value:0x39, for chip[0]
[    4.047016@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb6):    value:0x50, for chip[0]
[    4.055957@0] aml_nand_get_read_default_value_hynix, Retry0st, REG(0xb7):    value:0x90, for chip[0]
[    4.064897@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb0):    value:0x3a, for chip[0]
[    4.073836@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb1):    value:0x39, for chip[0]
[    4.082769@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb2):    value:0x55, for chip[0]
[    4.091718@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb3):    value:0x9b, for chip[0]
[    4.100658@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb4):    value:0x3e, for chip[0]
[    4.109598@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb5):    value:0x3c, for chip[0]
[    4.118541@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb6):    value:0x49, for chip[0]
[    4.127480@0] aml_nand_get_read_default_value_hynix, Retry1st, REG(0xb7):    value:0x89, for chip[0]
[    4.136420@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb0):    value:0x38, for chip[0]
[    4.145360@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb1):    value:0x38, for chip[0]
[    4.153577@1] usb 1-1: new high speed USB device number 2 using dwc_otg
[    4.160944@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb2):    value:0x52, for chip[0]
[    4.160952@1] Indeed it is in host mode hprt0 = 00001101
[    4.175139@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb3):    value:0x9d, for chip[0]
[    4.184074@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb4):    value:0x3c, for chip[0]
[    4.193005@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb5):    value:0x3b, for chip[0]
[    4.201954@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb6):    value:0x46, for chip[0]
[    4.210894@0] aml_nand_get_read_default_value_hynix, Retry2st, REG(0xb7):    value:0x8b, for chip[0]
[    4.219834@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb0):    value:0x34, for chip[0]
[    4.228780@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb1):    value:0x36, for chip[0]
[    4.237715@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb2):    value:0x4f, for chip[0]
[    4.246656@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb3):    value:0x9a, for chip[0]
[    4.255596@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb4):    value:0x38, for chip[0]
[    4.264536@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb5):    value:0x39, for chip[0]
[    4.273469@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb6):    value:0x43, for chip[0]
[    4.282418@0] aml_nand_get_read_default_value_hynix, Retry3st, REG(0xb7):    value:0x88, for chip[0]
[    4.291358@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb0):    value:0x2d, for chip[0]
[    4.300298@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb1):    value:0x34, for chip[0]
[    4.309251@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb2):    value:0x4b, for chip[0]
[    4.318183@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb3):    value:0x96, for chip[0]
[    4.327121@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb4):    value:0x31, for chip[0]
[    4.336084@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb5):    value:0x37, for chip[0]
[    4.345003@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb6):    value:0x3f, for chip[0]
[    4.353952@0] aml_nand_get_read_default_value_hynix, Retry4st, REG(0xb7):    value:0x84, for chip[0]
[    4.362894@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb0):    value:0x23, for chip[0]
[    4.371852@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb1):    value:0x32, for chip[0]
[    4.380773@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb2):    value:0x47, for chip[0]
[    4.389722@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb3):    value:0x93, for chip[0]
[    4.398653@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb4):    value:0x27, for chip[0]
[    4.407602@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb5):    value:0x35, for chip[0]
[    4.416534@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb6):    value:0x3b, for chip[0]
[    4.425483@0] aml_nand_get_read_default_value_hynix, Retry5st, REG(0xb7):    value:0x81, for chip[0]
[    4.434415@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb0):    value:0x19, for chip[0]
[    4.443355@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb1):    value:0x25, for chip[0]
[    4.452296@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb2):    value:0x3b, for chip[0]
[    4.461245@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb3):    value:0x83, for chip[0]
[    4.470221@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb4):    value:0x1d, for chip[0]
[    4.479122@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb5):    value:0x28, for chip[0]
[    4.488079@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb6):    value:0x2f, for chip[0]
[    4.497023@0] aml_nand_get_read_default_value_hynix, Retry6st, REG(0xb7):    value:0x71, for chip[0]
[    4.506213@1] scsi0 : usb-storage 1-1:1.0
[    4.510792@1] Indeed it is in host mode hprt0 = 00021501
[    4.513214@0] aml nand env valid addr: 418000 
[    4.545497@0] nand env: nand_env_probe. 
[    4.546162@0] nand key: nand_key_probe. 
[    4.547687@0] key start_blk=2040,end_blk=2047,aml_nand_key_init:651
[    4.563631@0] aml nand key valid addr: ff000000 
[    4.563656@0] aml nand key valid addr: ff200000 
[    4.567199@0] aml nand key valid addr: ff400000 
[    4.571799@0] aml nand key valid addr: ff600000 
[    4.576456@0] i=0,register --- nand_key
[    4.580360@0] Creating 8 MTD partitions on "C revision 20nm NAND 4GiB H27UBG8T2C":
[    4.587799@0] 0x000000c00000-0x000001400000 : "logo"
[    4.594051@0] 0x000001400000-0x000001c00000 : "aml_logo"
[    4.599060@0] 0x000001c00000-0x000002400000 : "recovery"
[    4.604410@0] 0x000002400000-0x000008c00000 : "boot"
[    4.609276@0] 0x000008c00000-0x000048c00000 : "system"
[    4.614505@0] 0x000048c00000-0x000068c00000 : "cache"
[    4.619482@0] 0x000068c00000-0x000078c00000 : "backup"
[    4.624684@0] 0x000078c00000-0x0000ff000000 : "data"
[    4.629766@0] init_aml_nftl start
[    4.631785@0] mtd->name: system
[    4.634956@0] nftl version 140415a
[    4.638335@0] nftl part attr 0
[    4.641382@0] nftl start:512,64
[    4.645606@0] first
[    4.693615@1] usb 2-1: new high speed USB device number 2 using dwc_otg
[    4.694881@1] Indeed it is in host mode hprt0 = 00001101
[    4.790378@0] average_erase_count:0
[    4.790407@0] second 138,448
[    4.838629@0] current used block :374
[    4.838652@0] current_block1:374
[    4.865796@0] free block cnt = 374
[    4.865817@0] new current block is 373
[    4.867611@0] prio_garbage_collect cannot find PHY_MAPPING_PAGE ffffffff !!
[    4.896194@1] hub 2-1:1.0: USB hub found
[    4.896518@1] hub 2-1:1.0: 4 ports detected
[    5.031708@0] prio_garbage_collect_no_last_page put_phy_block_to_free_list!
[    5.377597@0] recover_current_block_mapping : fill the current block, from page 255
[    5.379608@0] nftl ok!
[    5.382346@0] aml_nftl_blk->mbd.tr.name =system
[    5.387187@1] aml_nftl_init_bounce_buf, use cache here
[    5.391973@0]  system: unknown partition table
[    5.396309@0] _nftl_init_bounce_buf already init 1000
[    5.401047@0] aml_nftl_add_mtd ok
[    5.404362@0] mtd->name: cache
[    5.407388@0] nftl version 140415a
[    5.410798@0] nftl part attr 0
[    5.413853@0] nftl start:256,32
[    5.417380@0] first
[    5.493462@0] average_erase_count:0
[    5.493488@0] second 6,224
[    5.496088@0] current used block :250
[    5.497647@0] current_block1:250
[    5.505823@1] scsi 0:0:0:0: Direct-Access     Initio   INIC-3609        0213 PQ: 0 ANSI: 6
[    5.510206@0] sd 0:0:0:0: [sda] 976773167 512-byte logical blocks: (500 GB/465 GiB)
[    5.520080@0] sd 0:0:0:0: [sda] Write Protect is off
[    5.522240@1] sd 0:0:0:0: [sda] Write cache: disabled, read cache: enabled, supports DPO and FUA
[    5.534117@0] free block cnt = 250
[    5.534141@0] new current block is 249
[    5.537904@0] prio_garbage_collect cannot find PHY_MAPPING_PAGE ffffffff !!
[    5.554788@1]  sda: sda1
[    5.565169@1] sd 0:0:0:0: [sda] Attached SCSI disk
[    5.714560@0] prio_garbage_collect_no_last_page put_phy_block_to_free_list!
[    6.049703@0] recover_current_block_mapping : fill the current block, from page 255
[    6.051717@0] nftl ok!
[    6.054463@0] aml_nftl_blk->mbd.tr.name =cache
[    6.059130@0] aml_nftl_init_bounce_buf, use cache here
[    6.064032@0]  cache: unknown partition table
[    6.068218@0] _nftl_init_bounce_buf already init 1000
[    6.072983@0] aml_nftl_add_mtd ok
[    6.076300@0] mtd->name: backup
[    6.079412@0] nftl version 140415a
[    6.082807@0] nftl part attr 0
[    6.085866@0] nftl start:128,16
[    6.089226@0] first
[    6.128304@0] average_erase_count:0
[    6.128327@0] second 1,112
[    6.129197@0] all block full!!
[    6.131880@0] free block cnt = 127
[    6.135278@0] new current block is 126
[    6.138993@0] nftl ok!
[    6.141679@0] aml_nftl_blk->mbd.tr.name =backup
[    6.146563@0] aml_nftl_init_bounce_buf, use cache here
[    6.151379@0]  backup: unknown partition table
[    6.155685@0] _nftl_init_bounce_buf already init 1000
[    6.160435@0] aml_nftl_add_mtd ok
[    6.163768@0] mtd->name: data
[    6.166690@0] nftl version 140415a
[    6.170162@0] nftl part attr 0
[    6.173226@0] nftl start:1074,134
[    6.178326@0] first
[    6.490666@0] average_erase_count:0
[    6.490714@0] second 19,940
[    6.497916@0] current used block :1055
[    6.497938@0] current_block1:1055
[    6.537505@0] free block cnt = 1055
[    6.537526@0] new current block is 1054
[    6.539500@0] prio_garbage_collect cannot find PHY_MAPPING_PAGE ffffffff !!
[    6.795702@0] prio_garbage_collect_no_last_page put_phy_block_to_free_list!
[    7.047378@0] recover_current_block_mapping : fill the current block, from page 255
[    7.049387@0] nftl ok!
[    7.052163@0] aml_nftl_blk->mbd.tr.name =data
[    7.056897@0] aml_nftl_init_bounce_buf, use cache here
[    7.061652@0]  data: unknown partition table
[    7.065768@0] _nftl_init_bounce_buf already init 1000
[    7.070491@0] aml_nftl_add_mtd ok
[    7.073807@0] init_aml_nftl end
[    7.076907@0] ethernetinit(dbg[c08dc64c]=1)
[    7.081093@0] ethernet base addr is f3610000
[    7.085341@0] set_phy_mode() phy_Identifier: 0x0
[    7.090054@0] ethernet: MII PHY 0007c0f1h found at address 1, status 0x7829 advertising 01e1.
[    7.098522@0] find phy phy_Identifier=7c0f1
[    7.102595@0] write mac add to:dfa28648: 84 26 90 00 00 02 |.&....|
[    7.109278@0] eth0: mixed no checksumming and other settings.
[    7.114878@0] ethernet_driver probe!
[    7.118133@0] ****** aml_eth_pinmux_setup() ******
[    7.122908@0] ****** aml_eth_clock_enable() ******
[    7.127880@0] Amlogic A/V streaming port init
[    7.135415@0] amvideocap_register_memory 8e100000 6266880
[    7.137547@0] amvideocap_init
[    7.140776@0] amvideocap_init,0
[    7.143690@0] regist mpeg12 codec profile
[    7.147582@0] regist mpeg4 codec profile
[    7.151379@0] amvdec_vc1 module init
[    7.155113@0] regist vc1 codec profile
[    7.158670@0] amvdec_avs module init
[    7.162338@0] amvdec_h264 module init
[    7.166045@0] regist h264 codec profile
[    7.169794@0] regist mjpeg codec profile
[    7.173611@0] amvdec_real module init
[    7.177349@0] regist real codec profile
[    7.182017@1] request_fiq:152: fiq=35
[    7.184780@1] request_fiq:186: end
[    7.188728@1] SARADC Driver init.
[    7.191668@1] Remote Driver
[    7.194560@1] input: aml_keypad as /devices/platform/meson-remote/input/input0
[    7.202047@1] meson_remote_pinmux_setup()
[    7.205405@1] Remote platform_data g_remote_base=f3100480
[    7.210746@1] Remote date_valye======0,status == 8915f00
[    7.216094@1] remote config major:244
[    7.220212@1] physical address:0x9f152000
[    7.223916@1] ADC Keypad Driver init.
[    7.227542@1] Meson KeyInput init
[    7.230749@1] Key 116 registed.
[    7.234052@1] input: key_input as /devices/platform/meson-keyinput.0/input/input1
[    7.241687@1] Meson KeyInput register RTC interrupt
[    7.246046@1] Meson KeyInput major=243
[    7.250813@1]  spi_nor_probe 586
[    7.253105@1] SPI BOOT  : spi_nor_probe 591 
[    7.257422@1] spi_nor apollospi:0: mx25l3205d (4096 Kbytes)
[    7.262913@1] Creating 2 MTD partitions on "apollospi:0":
[    7.268329@1] 0x000000000000-0x000000060000 : "bootloader"
[    7.275045@1] 0x000000068000-0x000000070000 : "ubootenv"
[    7.280658@1] Memory Card media Major: 253
[    7.283291@1] card max_req_size is 128K 
[    7.287795@1] card creat process sucessful
[    7.291158@1] 
[    7.291160@1] SD/MMC initialization started......
[    7.933567@1] mmc data3 pull high
[    7.933788@0] sd_mmc_info->card_type=0
[    7.934973@0] begin SDIO check ......
[    7.961191@0] sdio_timeout_int_times = 0; timeout = 498
[    7.983794@0] sdio_timeout_int_times = 0; timeout = 497
[    7.983821@0] SEND OP timeout @1
[    7.986583@0] mmc data3 pull high
[    7.990072@0] begin SD&SDHC check ......
[    8.036129@0] sdio_timeout_int_times = 0; timeout = 498
[    8.036157@0] SEND IF timeout @2
[    8.061689@0] sdio_timeout_int_times = 0; timeout = 498
[    8.061716@0] begin MMC check ......
[    8.106133@0] sdio_timeout_int_times = 0; timeout = 498
[    8.106159@0] No any SD/MMC card detected!
[    8.109799@0] #SD_MMC_ERROR_DRIVER_FAILURE error occured in sd_voltage_validation()
[    8.117463@0] [card_force_init] unit_state 3
[    8.121838@1] [dsp]DSP start addr 0xc4000000
[    8.126003@1] [dsp]register dsp to char divece(232)
[    8.135715@1] DSP pcmenc stream buffer to [0x9e401000-0x9e601000]
[    8.136876@1] amlogic audio dsp pcmenc device init!
[    8.142575@1] amlogic audio spdif interface device init!
[    8.148233@1] using rtc device, aml_rtc, for alarms
[    8.151748@1] aml_rtc aml_rtc: rtc core: registered aml_rtc as rtc0
[    8.158977@1] gpio dev major number:240
[    8.162841@1] create gpio device success
[    8.166118@1] vdin_drv_init: major 238
[    8.170186@1] vdin0 mem_start = 0x87200000, mem_size = 0x2000000
[    8.175754@1] vdin.0 cnavas initial table:
[    8.179758@1]        128: 0x87200000-0x87a29000  3840x2228 (8356 KB)
[    8.185604@1]        129: 0x87a29000-0x88252000  3840x2228 (8356 KB)
[    8.191388@1]        130: 0x88252000-0x88a7b000  3840x2228 (8356 KB)
[    8.197227@1] vdin_drv_probe: driver initialized ok
[    8.202190@1] amvdec_656in module: init.
[    8.206001@1] amvdec_656in_init_module:major 237
[    8.210686@1] kobject (df92e010): tried to init an initialized object, something is seriously wrong.
[    8.219739@1] [<c0043b44>] (unwind_backtrace+0x0/0xf8) from [<c0256840>] (kobject_init+0x78/0x94)
[    8.228579@1] [<c0256840>] (kobject_init+0x78/0x94) from [<c0298098>] (device_initialize+0x28/0x6c)
[    8.237601@1] [<c0298098>] (device_initialize+0x28/0x6c) from [<c029c59c>] (platform_device_register+0x10/0x1c)
[    8.247671@1] [<c029c59c>] (platform_device_register+0x10/0x1c) from [<c00213bc>] (amvdec_656in_init_module+0xac/0x140)
[    8.258433@1] [<c00213bc>] (amvdec_656in_init_module+0xac/0x140) from [<c003770c>] (do_one_initcall+0xfc/0x164)
[    8.268499@1] [<c003770c>] (do_one_initcall+0xfc/0x164) from [<c0008988>] (kernel_init+0x98/0x13c)
[    8.277445@1] [<c0008988>] (kernel_init+0x98/0x13c) from [<c003e0c0>] (kernel_thread_exit+0x0/0x8)
[    8.287089@1] amvdec_656in probe ok.
[    8.290023@1] efuse===========================================
[    8.296247@1] efuse: device efuse created
[    8.299809@1] efuse--------------------------------------------
[    8.305709@1] keys===========================================
[    8.311345@1] keys_devno=eb00000
[    8.315066@1] efuse: device aml_keys created
[    8.318815@1] amlkeys=0
[    8.321333@1] platform_driver_register--aml_keys_driver--------------------
[    8.343571@1] 6amlogic audio data interface device init!
[    8.343603@1] aml_dvb_init 
[    8.346180@1] dvb_io_setup start
[    8.349356@1] DVB: registering new adapter (amlogic-dvb)
[    8.358764@1] DVB: async fifo 0 buf size 524288, flush size 262144
[    8.361906@1] DVB: async fifo 1 buf size 524288, flush size 262144
[    8.368669@1] [aml_fe..]aml_fe_probe ok.
[    8.371066@1] Smartcard: cannot get resource "smc0_reset"
[    8.376767@1] SMC CLK SOURCE - 200000KHz
[    8.380303@0] [***smc***] smartcard->state: 1
[    8.385724@0] aml_hw_crypto initialization.
[    8.389545@0] usbcore: registered new interface driver snd-usb-audio
[    8.396231@0] enter dummy_codec_audio_probe
[    8.400078@0] aml-pcm 0:playback preallocate_dma_buffer: area=ffd80000, addr=9ea80000, size=131072
[    8.409323@0] init controls
[    8.410871@0] iec958 0: preallocate dma buffer start=ffd00000, size=80000
[    8.418431@0] aml-pcm 1:capture preallocate_dma_buffer: area=ffce0000, addr=9eb80000, size=65536
[    8.426420@0] asoc: dummy_codec <-> aml-dai0 mapping ok
[    8.432947@0] dummy codec control ALSA component registered!
[    8.437404@0] ALSA device list:
[    8.440377@0]   #0: AML-DUMMY-CODEC
[    8.443971@0] <--GT msg--><1> /proc/gt82x_dbg created
[    8.449138@0] GACT probability NOT on
[    8.452534@0] Mirror/redirect action on
[    8.456363@0] u32 classifier
[    8.459210@0]     Actions configured
[    8.462771@0] Netfilter messages via NETLINK v0.30.
[    8.467689@0] nf_conntrack version 0.5.0 (13252 buckets, 53008 max)
[    8.474595@0] ctnetlink v0.93: registering with nfnetlink.
[    8.479384@0] NF_TPROXY: Transparent proxy support initialized, version 4.1.0
[    8.486484@0] NF_TPROXY: Copyright (c) 2006-2007 BalaBit IT Ltd.
[    8.493120@0] xt_time: kernel timezone is -0000
[    8.497140@0] ip_tables: (C) 2000-2006 Netfilter Core Team
[    8.502581@0] arp_tables: (C) 2002 David S. Miller
[    8.507280@0] TCP cubic registered
[    8.511689@0] NET: Registered protocol family 10
[    8.515980@0] Mobile IPv6
[    8.517831@0] ip6_tables: (C) 2000-2006 Netfilter Core Team
[    8.523458@0] IPv6 over IPv4 tunneling driver
[    8.528945@0] NET: Registered protocol family 17
[    8.532331@0] NET: Registered protocol family 15
[    8.537053@0] Bridge firewalling registered
[    8.541101@0] NET: Registered protocol family 35
[    8.545998@0] VFP support v0.3: implementor 41 architecture 3 part 30 variant 9 rev 4
[    8.553479@0] DDR low power is enable.
[    8.557304@0] enter meson_pm_probe!
[    8.560683@0] meson_pm_probe done !
[    8.564765@0] ------------[ cut here ]------------
[    8.568942@0] WARNING: at /home/mx/openlinux-jbmr1/common/kernel/smp.c:459 smp_call_function_many+0xc8/0x280()
[    8.578904@0] Modules linked in:
[    8.582136@0] [<c0043b44>] (unwind_backtrace+0x0/0xf8) from [<c0061f38>] (warn_slowpath_common+0x4c/0x64)
[    8.591672@0] [<c0061f38>] (warn_slowpath_common+0x4c/0x64) from [<c0061f6c>] (warn_slowpath_null+0x1c/0x24)
[    8.601478@0] [<c0061f6c>] (warn_slowpath_null+0x1c/0x24) from [<c008f6bc>] (smp_call_function_many+0xc8/0x280)
[    8.611549@0] [<c008f6bc>] (smp_call_function_many+0xc8/0x280) from [<c008f8a8>] (smp_call_function+0x34/0x68)
[    8.621533@0] [<c008f8a8>] (smp_call_function+0x34/0x68) from [<c0049c2c>] (clk_set_rate_a9+0x9c/0xdc)
[    8.630817@0] [<c0049c2c>] (clk_set_rate_a9+0x9c/0xdc) from [<c0049f28>] (meson_clk_set_rate+0x130/0x168)
[    8.640365@0] [<c0049f28>] (meson_clk_set_rate+0x130/0x168) from [<c0049ff4>] (clk_set_rate+0x94/0xbc)
[    8.649654@0] [<c0049ff4>] (clk_set_rate+0x94/0xbc) from [<c0052d7c>] (meson_cpufreq_target_locked.isra.2+0x14c/0x224)
[    8.660328@0] [<c0052d7c>] (meson_cpufreq_target_locked.isra.2+0x14c/0x224) from [<c0052e78>] (meson_cpufreq_target+0x24/0x3c)
[    8.671705@0] [<c0052e78>] (meson_cpufreq_target+0x24/0x3c) from [<c0388a34>] (__cpufreq_driver_target+0x50/0x64)
[    8.681945@0] [<c0388a34>] (__cpufreq_driver_target+0x50/0x64) from [<c038b1e0>] (cpufreq_governor_performance+0x28/0x30)
[    8.692878@0] [<c038b1e0>] (cpufreq_governor_performance+0x28/0x30) from [<c0388b1c>] (__cpufreq_governor+0xb0/0x138)
[    8.703467@0] [<c0388b1c>] (__cpufreq_governor+0xb0/0x138) from [<c038997c>] (__cpufreq_set_policy+0x128/0x168)
[    8.713535@0] [<c038997c>] (__cpufreq_set_policy+0x128/0x168) from [<c0389c1c>] (cpufreq_add_dev_interface+0x260/0x2c8)
[    8.724298@0] [<c0389c1c>] (cpufreq_add_dev_interface+0x260/0x2c8) from [<c038a140>] (cpufreq_add_dev+0x4bc/0x634)
[    8.734631@0] [<c038a140>] (cpufreq_add_dev+0x4bc/0x634) from [<c02996bc>] (sysdev_driver_register+0xb0/0x12c)
[    8.744613@0] [<c02996bc>] (sysdev_driver_register+0xb0/0x12c) from [<c0388fe8>] (cpufreq_register_driver+0x70/0x17c)
[    8.755203@0] [<c0388fe8>] (cpufreq_register_driver+0x70/0x17c) from [<c029bf3c>] (platform_drv_probe+0x18/0x1c)
[    8.765357@0] [<c029bf3c>] (platform_drv_probe+0x18/0x1c) from [<c029ad30>] (driver_probe_device+0x90/0x1ac)
[    8.775164@0] [<c029ad30>] (driver_probe_device+0x90/0x1ac) from [<c029aed8>] (__driver_attach+0x8c/0x90)
[    8.784712@0] [<c029aed8>] (__driver_attach+0x8c/0x90) from [<c0299f50>] (bus_for_each_dev+0x5c/0x88)
[    8.793912@0] [<c0299f50>] (bus_for_each_dev+0x5c/0x88) from [<c029a714>] (bus_add_driver+0x17c/0x244)
[    8.803200@0] [<c029a714>] (bus_add_driver+0x17c/0x244) from [<c029b384>] (driver_register+0x78/0x13c)
[    8.812488@0] [<c029b384>] (driver_register+0x78/0x13c) from [<c029c2ec>] (platform_driver_probe+0x18/0x9c)
[    8.822217@0] [<c029c2ec>] (platform_driver_probe+0x18/0x9c) from [<c003770c>] (do_one_initcall+0xfc/0x164)
[    8.831935@0] [<c003770c>] (do_one_initcall+0xfc/0x164) from [<c0008988>] (kernel_init+0x98/0x13c)
[    8.840874@0] [<c0008988>] (kernel_init+0x98/0x13c) from [<c003e0c0>] (kernel_thread_exit+0x0/0x8)
[    8.849829@0] ---[ end trace 6fa0f5b0ca88f260 ]---
[    8.854582@0] ------------[ cut here ]------------
[    8.859361@0] WARNING: at /home/mx/openlinux-jbmr1/common/kernel/smp.c:320 smp_call_function_single+0x150/0x1c0()
[    8.869596@0] Modules linked in:
[    8.872816@0] [<c0043b44>] (unwind_backtrace+0x0/0xf8) from [<c0061f38>] (warn_slowpath_common+0x4c/0x64)
[    8.882363@0] [<c0061f38>] (warn_slowpath_common+0x4c/0x64) from [<c0061f6c>] (warn_slowpath_null+0x1c/0x24)
[    8.892171@0] [<c0061f6c>] (warn_slowpath_null+0x1c/0x24) from [<c008f584>] (smp_call_function_single+0x150/0x1c0)
[    8.902501@0] [<c008f584>] (smp_call_function_single+0x150/0x1c0) from [<c008f8a8>] (smp_call_function+0x34/0x68)
[    8.912744@0] [<c008f8a8>] (smp_call_function+0x34/0x68) from [<c0049c2c>] (clk_set_rate_a9+0x9c/0xdc)
[    8.922030@0] [<c0049c2c>] (clk_set_rate_a9+0x9c/0xdc) from [<c0049f28>] (meson_clk_set_rate+0x130/0x168)
[    8.931578@0] [<c0049f28>] (meson_clk_set_rate+0x130/0x168) from [<c0049ff4>] (clk_set_rate+0x94/0xbc)
[    8.940866@0] [<c0049ff4>] (clk_set_rate+0x94/0xbc) from [<c0052d7c>] (meson_cpufreq_target_locked.isra.2+0x14c/0x224)
[    8.951541@0] [<c0052d7c>] (meson_cpufreq_target_locked.isra.2+0x14c/0x224) from [<c0052e78>] (meson_cpufreq_target+0x24/0x3c)
[    8.962913@0] [<c0052e78>] (meson_cpufreq_target+0x24/0x3c) from [<c0388a34>] (__cpufreq_driver_target+0x50/0x64)
[    8.973155@0] [<c0388a34>] (__cpufreq_driver_target+0x50/0x64) from [<c038b1e0>] (cpufreq_governor_performance+0x28/0x30)
[    8.984092@0] [<c038b1e0>] (cpufreq_governor_performance+0x28/0x30) from [<c0388b1c>] (__cpufreq_governor+0xb0/0x138)
[    8.994681@0] [<c0388b1c>] (__cpufreq_governor+0xb0/0x138) from [<c038997c>] (__cpufreq_set_policy+0x128/0x168)
[    9.004749@0] [<c038997c>] (__cpufreq_set_policy+0x128/0x168) from [<c0389c1c>] (cpufreq_add_dev_interface+0x260/0x2c8)
[    9.015512@0] [<c0389c1c>] (cpufreq_add_dev_interface+0x260/0x2c8) from [<c038a140>] (cpufreq_add_dev+0x4bc/0x634)
[    9.025842@0] [<c038a140>] (cpufreq_add_dev+0x4bc/0x634) from [<c02996bc>] (sysdev_driver_register+0xb0/0x12c)
[    9.035825@0] [<c02996bc>] (sysdev_driver_register+0xb0/0x12c) from [<c0388fe8>] (cpufreq_register_driver+0x70/0x17c)
[    9.046415@0] [<c0388fe8>] (cpufreq_register_driver+0x70/0x17c) from [<c029bf3c>] (platform_drv_probe+0x18/0x1c)
[    9.056570@0] [<c029bf3c>] (platform_drv_probe+0x18/0x1c) from [<c029ad30>] (driver_probe_device+0x90/0x1ac)
[    9.066378@0] [<c029ad30>] (driver_probe_device+0x90/0x1ac) from [<c029aed8>] (__driver_attach+0x8c/0x90)
[    9.075926@0] [<c029aed8>] (__driver_attach+0x8c/0x90) from [<c0299f50>] (bus_for_each_dev+0x5c/0x88)
[    9.085126@0] [<c0299f50>] (bus_for_each_dev+0x5c/0x88) from [<c029a714>] (bus_add_driver+0x17c/0x244)
[    9.094414@0] [<c029a714>] (bus_add_driver+0x17c/0x244) from [<c029b384>] (driver_register+0x78/0x13c)
[    9.103702@0] [<c029b384>] (driver_register+0x78/0x13c) from [<c029c2ec>] (platform_driver_probe+0x18/0x9c)
[    9.113425@0] [<c029c2ec>] (platform_driver_probe+0x18/0x9c) from [<c003770c>] (do_one_initcall+0xfc/0x164)
[    9.123145@0] [<c003770c>] (do_one_initcall+0xfc/0x164) from [<c0008988>] (kernel_init+0x98/0x13c)
[    9.132086@0] [<c0008988>] (kernel_init+0x98/0x13c) from [<c003e0c0>] (kernel_thread_exit+0x0/0x8)
[    9.141021@0] ---[ end trace 6fa0f5b0ca88f261 ]---
[    9.153663@0] android_usb gadget: Mass Storage Function, version: 2009/09/11
[    9.155070@0] android_usb gadget: Number of LUNs=2
[    9.159845@0]  lun0: LUN: removable file: (no medium)
[    9.164899@0]  lun1: LUN: removable file: (no medium)
[    9.170651@0] android_usb gadget: android_usb ready
[    9.175304@0] aml_rtc aml_rtc: setting system clock to 1970-01-02 00:31:59 UTC (88319)
[    9.182757@0] ------------[ cut here ]------------
[    9.187508@0] WARNING: at /home/mx/openlinux-jbmr1/common/fs/proc/generic.c:586 proc_register+0xec/0x1b4()
[    9.197121@0] proc_dir_entry '/proc/gt82x_dbg' already registered
[    9.203157@0] Modules linked in:
[    9.206391@0] [<c0043b44>] (unwind_backtrace+0x0/0xf8) from [<c0061f38>] (warn_slowpath_common+0x4c/0x64)
[    9.215939@0] [<c0061f38>] (warn_slowpath_common+0x4c/0x64) from [<c0061fe4>] (warn_slowpath_fmt+0x30/0x40)
[    9.225656@0] [<c0061fe4>] (warn_slowpath_fmt+0x30/0x40) from [<c0119ca8>] (proc_register+0xec/0x1b4)
[    9.234873@0] [<c0119ca8>] (proc_register+0xec/0x1b4) from [<c011a068>] (create_proc_entry+0x68/0xb4)
[    9.244079@0] [<c011a068>] (create_proc_entry+0x68/0xb4) from [<c062f6f8>] (goodix_ts_init+0x58/0xdc)
[    9.253255@0] [<c062f6f8>] (goodix_ts_init+0x58/0xdc) from [<c003770c>] (do_one_initcall+0xfc/0x164)
[    9.262373@0] [<c003770c>] (do_one_initcall+0xfc/0x164) from [<c0008988>] (kernel_init+0x98/0x13c)
[    9.271312@0] [<c0008988>] (kernel_init+0x98/0x13c) from [<c003e0c0>] (kernel_thread_exit+0x0/0x8)
[    9.280247@0] ---[ end trace 6fa0f5b0ca88f263 ]---
[    9.285034@0] <--GT msg--><1> /proc/gt82x_dbg created
[    9.290070@0] Error: Driver 'Goodix-TS' is already registered, aborting...
[    9.296911@0] CEC init
[    9.299523@0] CEC: CEC task process
[    9.302737@0_[    9.323571@0] Changing baud from 0 to 115200
[    9.383625@0] Freeing init memory: 188K
[    9.387503@1] init (1): /proc/1/oom_adj is deprecated, please use /proc/1/oom_score_adj instead.
[    9.397320@1] UMP: UMP device driver  loaded
[    9.404222@0] usb 2-1.2: new high speed USB device number 3 using dwc_otg
[    9.419933@1] mail version=-1
[    9.419989@1] Mali pp1 MMU register mapped at e00ec000...
[    9.422647@1] Mali pp2 MMU register mapped at e00ee000...
[    9.491094@1] mali_meson_poweron: Interrupt received.
[    9.554103@1] mail version=1
[    9.617292@1] mali_meson_poweron: Interrupt received.
[    9.680355@1] mail version=1
[    9.683801@1] Mali: Mali device driver loaded
[    9.684409@1] boot_timer_set: <start!>,boot_timer_state=1
[   10.688562@1] init: hdmi hpd_status is :49
[   10.688597@1] tvmode set to 720p
[   10.690319@1] don't set the same mode as current.
[   10.695163@1] init: ===== resolution=720p
[   10.698910@1] init: ===== cvbsmode=480cvbs
[   10.702989@1] init: ===== hdmimode=720posd0=>x:0 ,y:0,w:1280,h:720
[   10.713879@0]  osd1=> x:0,y:0,w:18,h:18 
[   10.730923@0] init: load_565rle_image_mbx result is: 0
[   10.780725@0] osd0 free scale ENABLE
[   10.780757@0] vf_reg_provider:osd
[   10.873559@1] <FIQ>:vf_ext_light_unreg_provide
[   10.873566@1] 0
[   10.875271@0] EXT4-fs (system): INFO: recovery required on readonly filesystem
[   10.881309@0] EXT4-fs (system): write access will be enabled during recovery
[   10.895687@0] EXT4-fs (system): recovery complete
[   10.899460@0] EXT4-fs (system): mounted filesystem with ordered data mode. Opts: noauto_da_alloc
[   10.903639@0] init: Before e2fsck_main...
[   10.923812@0] init: After e2fsck_main...
[   10.932353@0] EXT4-fs (data): mounted filesystem with ordered data mode. Opts: noauto_da_alloc
[   10.947082@0] EXT4-fs (cache): mounted filesystem with ordered data mode. Opts: noauto_da_alloc
[   10.950350@0] name=nand_key nand_key
[   10.953794@0] read:addr:0xff000000,phy_blk_addr:2040,phy_page_addr:0,aml_nand_get_key:129
[   11.014625@0] init: cannot find '/sbin/sec_test', disabling 'sec_test'
[   11.020844@0] init: cannot find '/system/etc/install-recovery.sh', disabling 'flash_recovery'
[   11.027977@0] init: cannot find '/system/bin/smbd', disabling 'smbd'
[   11.031117@0] init: cannot find '/system/bin/pppoe_wrapper', disabling 'pppoe_wrapper'
[   11.040593@0] init: cannot find '/system/bin/xcmid-amlogic', disabling 'xcmidware'
[   11.057554@0] DSP pcmenc stream buffer to [0x9e401000-0x9e601000]
[   11.068841@0] init: property 'ro.usb.vendor.string' doesn't exist while expanding '${ro.usb.vendor.string}'
[   11.073400@0] init: cannot expand '${ro.usb.vendor.string}' while writing to '/sys/class/android_usb/android0/f_mass_storage/vendor_string'
[   11.086525@0] init: property 'ro.usb.product.string' doesn't exist while expanding '${ro.usb.product.string}'
[   11.095446@0] init: cannot expand '${ro.usb.product.string}' while writing to '/sys/class/android_usb/android0/f_mass_storage/product_string'
[   11.108783@0] vfm_map_store:rm default
[   11.111853@0] vfm_map_store:add default decoder ppmgr deinterlace amvideo
[   11.120698@0] android_usb: already disabled
[   11.122946@0] android_usb: already disabled
[   11.130479@0] warning: `adbd' uses 32-bit capabilities (legacy support in use)
[   11.134545@0] adb_open
[   11.136429@0] mtp_bind_config
[   11.139407@0] adb_bind_config
root@android:/ # [   11.301304@0] tvmode set to 720p
[   11.301313@0] 
[   11.301360@0] don't set the same mode as current.
[   11.511911@0] osd0 free scale ENABLE
[   11.511962@0] vf_reg_provider:osd
[   11.514226@0] osd1 free scale ENABLE
[   11.548806@0] buf=0
[   11.548811@0] 
[   11.548835@0] IEC958_mode_raw=0
[   11.573584@1] <FIQ>:vf_ext_light_unreg_provide
[   11.573591@1] 0
[   13.289574@0] osd[0] set scale, h_scale: DISABLE, v_scale: DISABLE
[   13.290208@0] osd[0].scaledata: 0 0 0 0
[   13.294056@0] osd[0].pandata: 0 1279 0 719
[   14.767757@0] adb_release
[   14.767806@0] WARN::dwc_otg_handle_mode_mismatch_intr:154: Mode Mismatch Interrupt: currently in Host mode
[   14.767812@0] 
[   14.784982@0] dwc_otg_pcd_pullup, is_on 0
[   14.785040@0] WARN::ep_dequeue:412: bogus device state
[   14.785044@0] 
[   14.790396@0] init: untracked pid 2603 exited
[   14.793762@1] adb_open
[   14.793775@1] mtp_bind_config
[   14.793807@1] adb_bind_config

root@android:/ # 
root@android:/ # 
root@android:/ # netcfg
lo       UP                                   127.0.0.1/8   0x00000049 00:00:00:00:00:00
eth0     DOWN                                   0.0.0.0/0   0x00001002 84:26:90:00:00:02
sit0     DOWN                                   0.0.0.0/0   0x00000080 00:00:00:00:00:00
ip6tnl0  DOWN                                   0.0.0.0/0   0x00000080 00:00:00:00:00:00
root@android:/ # [   31.303589@1] pcd_ep0_timer_timeout 1
[   31.303629@0] WARN::dwc_otg_handle_mode_mismatch_intr:154: Mode Mismatch Interrupt: currently in Host mode
[   31.303635@0] 
[   32.132250@1] 333000
[   37.446562@0] request_suspend_state: wakeup (3->0) at 37183010002 (1970-01-02 00:32:27.762614001 UTC)
[   40.508412@1] trun off vdac
[   47.947260@0] netdev_open
[   47.947302@0] Ethernet reset
[   47.947335@0] NET MDA descpter start addr=de115000
[   47.952311@0] phy_reset!
[   47.954460@0] set_phy_mode() phy_Identifier: 0x7c0f1
[   47.959767@0] --1--write mac add to:dfa28648: 84 26 90 00 00 02 |.&....|
[   47.966062@0] unkown current key-name,key_read_show:1286
[   47.971341@0] ret = -22
[   47.971345@0] print_buff=
[   47.976398@0] --2--write mac add to:dfa28648: 84 26 90 00 00 02 |.&....|
[   47.983055@0] write mac add to:dfa28648: 84 26 90 00 00 02 |.&....|
[   47.989307@0] Current DMA mode=0, set mode=621c100
[   47.994157@0] ether leave promiscuous mode
[   47.998163@0] ether leave all muticast mode
[   48.002326@0] changed the Multicast,mcount=1
[   48.006606@0] add mac address:33:33:00:00:00:01,bit=1
[   48.011614@0] set hash low=2,high=0
[   48.015105@0] changed the filter setting to :4
[   48.019620@0] changed the Multicast,mcount=1
[   48.023791@0] add mac address:33:33:00:00:00:01,bit=1
[   48.029349@0] changed the Multicast,mcount=2
[   48.033098@0] add mac address:33:33:00:00:00:01,bit=1
[   48.038136@0] add mac address:01:00:5e:00:00:01,bit=32
[   48.043226@0] set hash low=2,high=1
[   48.046708@0] changed the filter setting to :4
[   48.051686@0] ADDRCONF(NETDEV_UP): eth0: link is not ready
[   48.218818@0] acc_open
[   48.218866@0] acc_release
[   49.075674@1] FAT-fs (sda1): bogus number of reserved sectors
[   49.075687@1] FAT-fs (sda1): Can't find a valid FAT filesystem
[   49.452194@0] 333000
[   50.013680@0] duplex
[   50.013712@0] 100m
[   50.014124@0] ADDRCONF(NETDEV_CHANGE): eth0: link becomes ready
[   50.018247@0] changed the Multicast,mcount=3
[   50.022405@0] add mac address:33:33:00:00:00:01,bit=1
[   50.027509@0] add mac address:01:00:5e:00:00:01,bit=32
[   50.032562@0] add mac address:33:33:ff:00:00:02,bit=53
[   50.037752@0] set hash low=2,high=200001
[   50.041588@0] changed the filter setting to :4
[   50.059342@1] 333000
[   50.115447@1] Ethernet Driver ioctl (8b1b)
[   50.115481@1] Ethernet Driver unknow ioctl (8b1b) 
[   51.404916@0] changed the Multicast,mcount=3
[   51.404971@0] add mac address:33:33:00:00:00:01,bit=1
[   51.408600@0] add mac address:01:00:5e:00:00:01,bit=32
[   51.413734@0] add mac address:33:33:ff:00:00:02,bit=53
[   51.424882@0] WRITE [GPIOAO_10] 0 
[   51.708273@0] Ethernet Driver ioctl (8b1b)
[   51.708309@0] Ethernet Driver unknow ioctl (8b1b) 
[   52.411901@0] init: ubootenv.var.firstboot=(null)
[   52.479496@1] SMC CLK SOURCE - 200000KHz
[   52.481524@1] ATR from INT
[   52.648584@0] init: sys_prop: permission denied uid:1003  name:service.bootanim.exit
[   53.002806@0] init: cannot find '/system/bin/preinstall.sh', disabling 'preinstall'
[   53.005462@0] boot_timer_set: <stop!>,boot_timer_state = 1
[   53.010293@0] disable boot timer2
[   53.013631@0] get atr len:16 data: 3b 6c 00 00 4e 54 49 43 32 8d dc 28 4a 03 00 00 
[   53.971803@1] SMC CLK SOURCE - 200000KHz
[   53.973916@1] ATR from INT
[   54.493612@1] get atr len:16 data: 3b 6c 00 00 4e 54 49 43 32 8d dc 28 4a 03 00 00 
[   57.498491@1] WRITE [GPIOD_1] 1 

root@android:/ # 
root@android:/ # 
root@android:/ # 
root@android:/ # 
root@android:/ # reboot
[   62.700092@1] cacel delay work and reset watch dog
[   62.700197@1] m3_nand_reboot_notifier 1242 
[   62.703761@1] aml_nftl_reboot_notifier :system 0
[   62.708277@1] aml_nftl_reboot_notifier :cache 0
[   62.712757@1] aml_nftl_reboot_notifier :backup 0
[   62.722979@1] aml_nftl_reboot_notifier :data 0
[   62.723055@1] aml_keys_notify_reboot:1597m3_nand_shutdown 1367 chip->options:60a01
[   62.729799@1] Disabling non-boot CPUs ...
[   62.734295@1] IRQ61 no longer affine to CPU1
[   62.734494@1] Disable timerD
[   62.734748@0] CPU1: shutdown
[   62.745524@0] Restarting system.
[   62.74E I3000000032940xf100110203:77500EEEE I400000004294_M6_BL1_3431>2534313
TE : 77172
wait pll-0x03 target is 0204 now it is 0x00000203

DDR clock is 516MHz with Low Power & 1T mode

DDR training :
DX0DLLCR:40000000
DX0DQTR:ffffffff
DX0DQSTR:3db05001
DX1DLLCR:40000000
DX1DQTR:ffffffff
DX1DQSTR:3db05001
DX2DLLCR:40000000
DX2DQTR:ffffffff
DX2DQSTR:3db05001
DX3DLLCR:40000000
DX3DQTR:ffffffff
DX3DQSTR:3db05001
Stage 00 Result 00000000
Stage 01 Result 00000000
Stage 02 Result 00000000
Stage 03 Result 00000000

HHH
Boot From SPI
0x12345678
Boot from internal device 1st SPI RESERVED

System Started



U-boot(m6_mbx_v1@0e184153) (Dec 24 2014 - 16:51:00)

aml_rtc_init
aml rtc init first time!
Clear HDMI KSV RAM
DRAM:  1 GiB
relocation Offset is: 105e8000
NAND:  Amlogic nand flash uboot driver, Version U1.06.017 (c) 2010 Amlogic Inc.
SPI BOOT : continue i 0
No NAND device found!!!
NAND device id: ad d7 94 91 60 44 
aml_chip->hynix_new_nand_type =: 4 
NAND device: Manufacturer ID: 0xad, Chip ID: 0xad (Hynix C revision 20nm NAND 4GiB H27UBG8T2C)
1 NAND chips detected
#####aml_nand_init, with RB pins and chip->chip_delay:20
bus_cycle=5, bus_timing=6, start_cycle=6, end_cycle=7,system=5.0ns
oob size is not enough for selected bch mode: NAND_BCH60_1K_MODE force bch to mode: NAND_BCH40_1K_MODE
aml_chip->oob_fill_cnt =64,aml_chip->oob_size =640,bch_bytes =70
multi plane error for selected plane mode: NAND_TWO_PLANE_MODE force plane to : NAND_SINGLE_PLANE_MODE
aml_nand_init:oobmul =1,chip->ecc.layout->oobfree[0].length=16,aml_chip->oob_size=640
aml_nand_get_read_default_value_hynix 980 get default reg value at blk:0, page:7
aml nand env valid addr: 418000 
key start_blk=2040,end_blk=2047,aml_nand_key_init:684
aml nand key valid addr: ff000000 
aml nand key valid addr: ff200000 
aml nand key valid addr: ff400000 
aml nand key valid addr: ff600000 
CONFIG_KEYSIZE=0x10000; KEYSIZE=0xfffc; bbt=0x1330; default_keyironment_size=0xeccc
i=0,register --- nand_key
Creating 8 MTD partitions on "nandnormal":
0x000000c00000-0x000001400000 : "logo"
0x000001400000-0x000001c00000 : "aml_logo"
0x000001c00000-0x000002400000 : "recovery"
0x000002400000-0x000008c00000 : "boot"
0x000008c00000-0x000048c00000 : "system"
0x000048c00000-0x000068c00000 : "cache"
0x000068c00000-0x000078c00000 : "backup"
0x000078c00000-0x0000ff000000 : "data"
nandnormal initialized ok
detect mx chiprevD :1 and nand_type: 4
nand_curr_device =1
MMC:   SDIO Port B: 0, SDIO Port C: 1
SPI BOOT,spi_env_relocate_spec : env_relocate_spec 53 
SF: Detected MX25L3205D with page size 256, total 4 MiB

SPI NOR Flash have write protect!!!
In:    serial
Out:   serial
Err:   serial
aml_i2c_init
register usb cfg[0] = 9fe8292c
Net:   Meson_Ethernet
init suspend firmware done. (ret:0)
efuse version is not selected.
###  main_loop entered: bootdelay=1

### main_loop: bootcmd="run compatible_boot"
Hit any key to stop autoboot:  0 
m6_mbx_v1#
m6_mbx_v1#
m6_mbx_v1#
m6_mbx_v1#
m6_mbx_v1#setenv ethaddr 84:26:90:00:00:a2
m6_mbx_v1#saveenv 
Saving Environment to SPI Flash...
SPI BOOT,spi_saveenv : saveenv 93 
Erasing SPI flash...Writing to SPI flash...done
m6_mbx_v1#  /* expat_config.h.  Generated by configure.  */
/* expat_config.h.in.  Generated from configure.in by autoheader.  */

/* 1234 = LIL_ENDIAN, 4321 = BIGENDIAN */
#define BYTEORDER 1234

/* Define to 1 if you have the `bcopy' function. */
#define HAVE_BCOPY 1

/* Define to 1 if you have the <dlfcn.h> header file. */
#define HAVE_DLFCN_H 1

/* Define to 1 if you have the <fcntl.h> header file. */
#define HAVE_FCNTL_H 1

/* Define to 1 if you have the `getpagesize' function. */
#define HAVE_GETPAGESIZE 1

/* Define to 1 if you have the <inttypes.h> header file. */
#define HAVE_INTTYPES_H 1

/* Define to 1 if you have the `memmove' function. */
#define HAVE_MEMMOVE 1

/* Define to 1 if you have the <memory.h> header file. */
#define HAVE_MEMORY_H 1

/* Define to 1 if you have a working `mmap' system call. */
#define HAVE_MMAP 1

/* Define to 1 if you have the <stdint.h> header file. */
#define HAVE_STDINT_H 1

/* Define to 1 if you have the <stdlib.h> header file. */
#define HAVE_STDLIB_H 1

/* Define to 1 if you have the <strings.h> header file. */
#define HAVE_STRINGS_H 1

/* Define to 1 if you have the <string.h> header file. */
#define HAVE_STRING_H 1

/* Define to 1 if you have the <sys/stat.h> header file. */
#define HAVE_SYS_STAT_H 1

/* Define to 1 if you have the <sys/types.h> header file. */
#define HAVE_SYS_TYPES_H 1

/* Define to 1 if you have the <unistd.h> header file. */
#define HAVE_UNISTD_H 1

/* Define to the address where bug reports for this package should be sent. */
#define PACKAGE_BUGREPORT "expat-bugs@libexpat.org"

/* Define to the full name of this package. */
#define PACKAGE_NAME "expat"

/* Define to the full name and version of this package. */
#define PACKAGE_STRING "expat 2.0.1"

/* Define to the one symbol short name of this package. */
#define PACKAGE_TARNAME "expat"

/* Define to the version of this package. */
#define PACKAGE_VERSION "2.0.1"

/* Define to 1 if you have the ANSI C header files. */
#define STDC_HEADERS 1

/* whether byteorder is bigendian */
/* #undef WORDS_BIGENDIAN */

/* Define to specify how much context to retain around the current parse
   point. */
#define XML_CONTEXT_BYTES 1024

/* Define to make parameter entity parsing functionality available. */
#define XML_DTD 1

/* Define to make XML Namespaces functionality available. */
#define XML_NS 1

/* Define to __FUNCTION__ or "" if `__func__' does not conform to ANSI C. */
/* #undef __func__ */

/* Define to empty if `const' does not conform to ANSI C. */
/* #undef const */

/* Define to `long' if <sys/types.h> does not define. */
/* #undef off_t */

/* Define to `unsigned' if <sys/types.h> does not define. */
/* #undef size_t */
