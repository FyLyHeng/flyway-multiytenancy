package de.kuksin.multitenant.configuration.properties.tenantServiceProvide;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.hibernate.HikariConnectionProvider;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @This class is all about high level service of Tenant like validate, insert, get ,...
 */

@Component
@EnableAutoConfiguration
public class TenantServiceResolver {
    private final Environment env;
    private final DBUtilServiceProvide dbUtilServiceProvide;
    private JdbcTemplate jdbcTemplate;

    public TenantServiceResolver(Environment env, DBUtilServiceProvide dbUtilServiceProvide) {
        this.env = env;
        this.dbUtilServiceProvide = dbUtilServiceProvide;
    }


    @PostConstruct
    private void init() {
        DataSource datasource = dbUtilServiceProvide.build(env);
        jdbcTemplate = new JdbcTemplate(datasource);
    }


    public Map<String,Object> findTenantByName(String tenantName) throws ParseException {
        String tenantInfo = null;

        try {
            tenantInfo = jdbcTemplate.queryForObject("SELECT value FROM data_source_config WHERE is_active = '1' AND name = ?",String.class,tenantName);
        }catch (EmptyResultDataAccessException e){
            //e.printStackTrace();
        }


        if (tenantInfo==null){
            return null;
        }else {
            System.out.println("tenantInfo"+tenantInfo);
            return dbUtilServiceProvide.bindTenantValueToMap(tenantInfo,tenantName);
        }
    }


    public List<JSONObject> findActiveAllTenant() throws ParseException {

        List<String> tenantInfo = jdbcTemplate.queryForList("SELECT value FROM data_source_config WHERE is_active = '1' ", String.class);

        List<JSONObject> list = new ArrayList<>();
        for (String s : tenantInfo) {
            System.out.println(s);
            list.add(dbUtilServiceProvide.tenantValueFormatStringJsonToMap(s));
        }
        return list;
    }


    public Map<String,Object> insertNewTenantInfo(String tenantName) throws ParseException {
        String tenantValue = dbUtilServiceProvide.tenantValueFormatMapToStringJson(tenantName);
        jdbcTemplate.update("INSERT INTO data_source_config (name,is_active,description,value) VALUES(?,'1',?,?)",tenantName,"test",tenantValue);

        return findTenantByName(tenantName);
    }


    public void removeTenant (String tenantName){
        jdbcTemplate.update("DELETE FROM data_source_config WHERE name =?",tenantName);
    }


    public void checkAndCreateNotExistDB (String tenantName){
        jdbcTemplate.execute("SELECT 'CREATE DATABASE MYDB2' WHERE NOT EXISTS (SELECT From pg_database WHERE datname ='MYDB2')\\gexec");
    }

    //create new DB (tenant)
    public DataSource createDataSourceForTenantId(Map<String,Object> tenantInfo ,String tenantDatabaseName) throws SQLException {

//        HikariConfig tenantHikariConfig = new HikariConfig();
//        tenantHikariConfig.setJdbcUrl((String) tenantInfo.get("url"));
//        tenantHikariConfig.setPassword((String) tenantInfo.get("password"));
//        tenantHikariConfig.setUsername((String) tenantInfo.get("username"));
//        tenantHikariConfig.setDriverClassName((String) tenantInfo.get("driverClassName"));
//        //tenantHikariConfig.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
//        System.out.println(tenantHikariConfig.getJdbcUrl());
//        return new HikariDataSource(tenantHikariConfig);


/*        Properties dsProps = new Properties();
        dsProps.setProperty("url", (String) tenantInfo.get("url"));
        dsProps.setProperty("username", "postgres");
        dsProps.setProperty("password", "P@ssw0rd");

        Properties configProps = new Properties();
        configProps.setProperty("driverClassName", (String) tenantInfo.get("driverClassName"));
        configProps.setProperty("jdbcUrl", (String) tenantInfo.get("url"));*/



        Properties dsProps = new Properties();
        dsProps.setProperty("tenants.datasources.vw2.url", "jdbc:postgresql://localhost:5432/vw2?autoReconnect=true&useSSL=false&createDatabaseIfNotExist=true");
        dsProps.setProperty("tenants.datasources.vw2.username", "postgres");
        dsProps.setProperty("tenants.datasources.vw2.password", "P@ssw0rd");
        dsProps.put("javax.persistence.create-database-schemas", true);

        Properties configProps = new Properties();
        configProps.setProperty("driverClassName", (String) tenantInfo.get("driverClassName"));
        configProps.setProperty("jdbcUrl", (String) tenantInfo.get("url"));

        HikariConfig hc = new HikariConfig(configProps);
        hc.setDataSourceProperties(dsProps);
        return new HikariDataSource(hc);
    }
}
