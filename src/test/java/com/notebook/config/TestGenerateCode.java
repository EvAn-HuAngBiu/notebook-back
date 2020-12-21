package com.notebook.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Project: notebook
 * File: TestGenerateCode
 *
 * @author evan
 * @date 2020/11/4
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestGenerateCode {
    @Test
    public void testGenerateUserCode() {
        AutoGenerator ag = new AutoGenerator();

        // GlobalConfig
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setActiveRecord(false)
                .setAuthor("evan")
                .setOpen(true)
                .setFileOverride(true)
                .setOutputDir("/Users/evan/Java/notebook/src/main/java")
                // .setOutputDir("/Users/evan/Java")
                .setBaseResultMap(true)
                .setBaseColumnList(true)
                .setDateType(DateType.TIME_PACK)
                .setEntityName("%sDo")
                .setServiceName("%sService")
                .setServiceImplName("%sServiceImpl")
                .setControllerName("%sController")
                .setIdType(IdType.AUTO);
        ag.setGlobalConfig(globalConfig);

        // DataSource
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL)
                .setDriverName("com.mysql.cj.jdbc.Driver")
                .setUrl("jdbc:mysql://localhost:3306/notebook?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC")
                .setUsername("notebook_root")
                .setPassword("notebook123-");
        ag.setDataSource(dataSourceConfig);

        // StrategyConfig
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setCapitalMode(true)
                .setNaming(NamingStrategy.underline_to_camel)
                .setTablePrefix("notebook_")
                .setInclude("notebook_comment")
                .setEntityLombokModel(true)
                .setRestControllerStyle(true)
                .setVersionFieldName("version")
                .setLogicDeleteFieldName("deleted");
        ag.setStrategy(strategyConfig);

        // PackageConfig
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("com.notebook")
                .setEntity("domain")
                .setService("service")
                .setServiceImpl("service")
                .setController("controller")
                .setMapper("dao.mapper")
                .setXml("dao.mapper");
        ag.setPackageInfo(packageConfig);
        ag.execute();
    }
}
