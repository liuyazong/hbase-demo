package yz.hbase.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * author: liuyazong
 * datetime: 2017/7/17 下午4:31
 */
@Slf4j
@Configuration
@PropertySources({@PropertySource(value = "classpath:application.properties")})
public class HBaseConfig {
    @Value("${hadoop.home.dir}")
    private String hadoopHomeDir;

    @Bean(destroyMethod = "close")
    public Connection connection() throws Exception {
        Connection connection = ConnectionFactory.createConnection(HBaseConfiguration.create());
        log.debug("HBase connection: {}", connection);
        System.setProperty("hadoop.home.dir", this.hadoopHomeDir);
        return connection;
    }
}
