# windows-filename-sort
File name are sorted by windows rule

文件名称采用windows资源管理器的排序算法排列

---
下面列一下Windows文件名的排序规则(不一定全)
* 每个文件名按规则拆分成多个比较单元
* 特殊字符采用ascii码位于数字前面
* 特殊字符半角 < 特殊字符全角
* 特殊字符 < 数字 < 字母 < 中文
* 负号(可多个)与任何数字组成负数算作一个比较单元
* 同个比较单元，负号少的 < 负号多的
* 拆分出来的数字单元按数值大小比较，比如: a(1).txt < a(2).txt < a(11).txt 
* 数字按绝对值的方式比较，绝对值相同的，负数位于正数前面
* 数值相等的，数字位数较多的位于数字位数较少的前面，比如: 000.txt < 00.txt < 0.txt
* 中文采用拼音的顺序比较，比如: 国.txt < 人.txt < 中.txt
* 还有各种组合的特殊场景比较
* ...

具体示例可以参考下 [测试文件1](https://github.com/kookob/windows-filename-sort/blob/master/src/com/obroom/filenamesort/test/%E6%B5%8B%E8%AF%95%E5%88%97%E8%A1%A81.txt) 文件里面的排序结果。