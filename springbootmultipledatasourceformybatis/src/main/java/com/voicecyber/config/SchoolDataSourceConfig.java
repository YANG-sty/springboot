package com.voicecyber.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Created by Sean on 2018/7/31
 *
 * @author Sean
 */
@Configuration
@MapperScan(basePackages = "com.voicecyber.mapper.school",sqlSessionTemplateRef = "schoolSqlSessionTemplate")
/**
 * MapperScan 扫描 Mapper 接口并容器管理，包路径精确到 能和另外一个数据源做区分 sqlSessionFactoryRef 表示定义了 key ，表示一个唯一 SqlSessionFactory 实例
 */
public class SchoolDataSourceConfig {
    @Bean(name = "schoolDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.school")
    @Primary
    /**
     * Primary 标志这个 Bean 如果在多个同类 Bean 候选时，该 Bean 优先被考虑。「多数据源配置的时候注意，必须要有一个主数据源，用 @Primary 标志该 BEan
     */
    public DataSource schoolDataSource(){
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "schoolSqlSessionFactory")
    /**
     *     Primary注解表示在同一个接口有多个实现类可以注入的时候，默认选择哪一个
     */
    @Primary
    public SqlSessionFactory schoolSqlSessionFactory( @Qualifier("schoolDataSource")DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/school/*.xml"));
        return bean.getObject();
    }
    @Bean(name = "schoolTransactionManager")
    @Primary
    public DataSourceTransactionManager schoolDataSourceTransactionManager(@Qualifier("schoolDataSource")DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }
    @Bean(name = "schoolSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate schoolSqlSessionTemplate(@Qualifier("schoolSqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return  new SqlSessionTemplate(sqlSessionFactory);
    }
}
