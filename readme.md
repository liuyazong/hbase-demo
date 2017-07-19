# HBase
## 基础概念
   * 表 table
   * 行 row
   * 列族 column family
   * 列 column
   * 单元格 cell
   * 分区 region

    HBase中扩展和负载均衡的基本单元，是以row key排序的连续的存储区间。
    若region超过配置的最大值（hbase.hregion.max.filesize），系统会从row key的中间位置将改region拆分为大致相等两个子分区。
    没个region只能由一个region server加载，没个region server可以加载多个region。
    当某个region server挂掉时，系统会将该server加载的分区转移到其他server。
## 环境搭建
    
   以下安装了一个伪分布式的HBase环境
### 安装Java
### 安装Hadoop
* 下载
        
     [下载hadoop](https://hadoop.apache.org)
     版本2.7.3
     
* 配置hdfs

    编辑etc/hadoop/core-site.xml
    
        <configuration>
            <property>
                <name>fs.defaultFS</name>
                <value>hdfs://localhost:9000</value>
            </property>
        </configuration>
        
    编辑etc/hadoop/hdfs-site.xml
    
        <configuration>
            <property>
                    <name>dfs.replication</name>
                    <value>1</value>
            </property>
            <property>
                    <name>dfs.name.dir</name>
                    <value>file://${user.home}/hadoopinfra/hdfs/namenode</value>
            </property>
            <property>
                    <name>dfs.data.dir</name>
                    <value>file://${user.home}/hadoopinfra/hdfs/datanode</value>
            </property>
        </configuration>
* 准备文件系统
     
        bin/hdfs namenode -format
* 启动/停止hdfs
     
        sbin/start-dfs.sh
        sbin/stop-dfs.sh
* 访问web ui
     
        http://localhost:50070/
* 配置yarn
    
    编辑etc/hadoop/mapred-site.xml
     
        <configuration>
            <property>
                <name>mapreduce.framework.name</name>
                <value>yarn</value>
            </property>
        </configuration>
        
    编辑etc/hadoop/yarn-site.xml
     
        <configuration>
            <property>
                <name>yarn.nodemanager.aux-services</name>
                <value>mapreduce_shuffle</value>
            </property>
        </configuration>
* 启动/停止yarn
      
        sbin/start-yarn.sh
        sbin/stop-yarn.sh
* 访问web
     
        http://localhost:8088/
### 安装HBase
* 下载

    [HBase](https://hbase.apache.org)
     版本1.2.6，匹配的Hadoop版本为2.7.3
     
* 配置

    编辑conf/hbase-site.xml
        
        <configuration>
            <property>
                    <name>hbase.zookeeper.property.dataDir</name>
                    <value>${user.home}/zookeeper</value>
            </property>
            <property>
                    <name>hbase.rootdir</name>
                    <value>hdfs://localhost:9000/hbase</value>
            </property>
            <property>
                    <name>hbase.cluster.distributed</name>
                    <value>true</value>
            </property>
        </configuration>
* 启动/停止HBase
    
        bin/start-hbase.sh
        bin/stop-hbase.sh
* HBase shell
    
        bin/hbase shell
* 访问web
        
        http://localhost:16010
## 客户端API

* org.apache.hadoop.hbase.client.ConnectionFactory
    
    负责Connection的创建，调用者负责管理Connection的生命周期（调用它的close()方法）。
* org.apache.hadoop.hbase.client.Connection
    * 封装到服务器的低层链接和到zookeeper的链接
    * 共享的、线程安全的
        
        到服务器的链接、元数据缓存、zookeeper链接等，被通过Connection得到的Table、Admin实例共享
    * 重量级的
    
        一个应用只创建一个该实例即可。
* org.apache.hadoop.hbase.HBaseConfiguration

    用于加载HBase的配置文件（hbase-site.xml和hbase-default.xml）
* org.apache.hadoop.hbase.client.Table
    * 轻量级的
    * 非线程安全的
* org.apache.hadoop.hbase.CellUtil

    用于创建单元格的工具类
* org.apache.hadoop.hbase.util.Bytes
    
    HBase中存储的都是byte，该类提供了操作byte的便捷方法
* org.apache.hadoop.hbase.client.Get/ org.apache.hadoop.hbase.client.Put/org.apache.hadoop.hbase.client.Delete
    
    配合Table类实现对HBase的增删改查
* org.apache.hadoop.hbase.client.ResultScanner/org.apache.hadoop.hbase.client.Scan
    