### redis-aof-analysis
此程序是用来解析redis的持久化文件的，目前只解析.aof结尾的文件

### 运行方式
java -jar redis.aof redis.log
运行时，传入两个参数，第一个参数表示要解析的redis的aof文件，
第二个参数为输出目录。
如果参数为空，则会从当前jar的目录下读取 .aof 文件，
并默认生成输出一个redis.log文件

### 此方法解析暂时不支持中文
因为RandomAccessFile不支持使用utf8读取

### 如果需要解析的使用 waoffle 解析
- 安装
- waoffle. npm install -g waoffle
- 解析
- waoffle < appendonly.aof > 100.txt