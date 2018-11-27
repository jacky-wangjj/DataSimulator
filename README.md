环境：jdk1.8

**功能介绍**
1. 数据模拟，将生成的模拟数据保存到excel文件中。
2. 数据模拟，将生成的模拟数据通过TCP进行传输，模拟实时数据流。
3. 数据模拟，通过kafka进行生产、消费。

**使用方法**
1. 模拟数据的相关配置在config.json文件，可配置项包括：
    `numOfParams`：参数个数；
    `fileDir`：生成excle文件的保存路径；
    `fileFormat`：excle文件的格式xls或xlsx；
    `sheetCapacity`：每个sheet最多有多少行；
    `tableCapacity`：每个excle文件最多包含几个sheet；
    `bufSize`：数据buf大小，即多少行数据写一次文件；
    `constName`：常量参数的excle文件名字；
    `duration`：模拟多长时间的数据
    `paramConfs`：参数配置，参数个数可变；
        `id`：参数id；
        `name`：参数名字；
        `timeInterval`：时间间隔，0表示常量，非0表示时间序列；
        `paramRanges`：剩余参数配置；
        `paramRange`：随机数范围，两个参数表示int型随机数，三个参数表示double型随机数；
2. 配置文件site.properties
`tcp相关配置`：的IP和port配置；
`zookeeper相关配置`：ip和port配置；
`kafka相关配置`：ip、port、生产者和消费者相关配置；

3. shell脚本
startUp.sh [参数]
eg: startUp.sh DataSimulator

4. startUp.sh脚本格式转换
vim startUp.sh
:set fileformat=unix
:wq
然后cat -v startUp.sh检查隐藏字符^M是否去除；
添加执行权限：chmod a+x startUp.sh