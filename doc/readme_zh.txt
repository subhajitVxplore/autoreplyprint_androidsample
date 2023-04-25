autoreplyprint.aar
	开发包库文件
autoreplyprint_zh.txt
	接口说明文件
samplepos
	票据打印
samplelabel
	标签打印
samplepage
	页模式打印

注意：
	普通票据打印机，可以使用samplepos测试
	大资源的票据打印机，如果支持页模式功能，可以使用samplepage测试
	标签打印机，可以使用samplelabel测试
	导航到接口函数所在的文件之后，如果看不到注释或说明，请选择右上角的Attach Sources...

怎么集成：
1 流程就是，打开端口，发送各种打印指令，打完了关闭端口，虽然接口很多，但是流程很固定。
2 具体的接口怎么用，看例子，比较详细。
