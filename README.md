# IncrementalUpdate
实现增量更新的差分和合并的android程序

1.BsDiff和BsPatch分别是差分功能和合并功能的两个工程。

2.各自工程下的jni文件夹有c部分的代码，可以自己动手编译so库，本人是用的ndk-r8e版本编译，r13版本编译后在andorid4.1上加载so库时报有些依赖没有。

3.BsPatch工程自己修改一下版本号或添加一些资源到assets文件夹得到一个新版本。

4.代码比较简陋，BsPatch合并功能没有做签名校验。
