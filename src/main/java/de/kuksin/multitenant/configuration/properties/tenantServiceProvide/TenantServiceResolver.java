package de.kuksin.multitenant.configuration.properties.tenantServiceProvide;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.hibernate.HikariConnectionProvider;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @This class is all about high level service of Tenant like validate, insert, get ,...
 */

@Component
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
//        jdbcTemplate.update("UPDATE data_source_config SET is_active = '0' WHERE name =?",tenantName);
        jdbcTemplate.update("DELETE FROM data_source_config WHERE name =?",tenantName);
    }


    //create new DB (tenant)
    public DataSource createDataSourceForTenantId(Map<String,Object> tenantInfo ,String tenantDatabaseName) {

/*        HikariConfig tenantHikariConfig = new HikariConfig();
        //String tenantJdbcURL = dbUtilServiceProvide.databaseURLFromPostgresSQLJdbcUrl((String) tenantInfo.get("url"));
        tenantHikariConfig.setJdbcUrl((String) tenantInfo.get("url"));
        tenantHikariConfig.setPassword((String) tenantInfo.get("password"));
        tenantHikariConfig.setUsername((String) tenantInfo.get("username"));
        tenantHikariConfig.setPoolName(tenantDatabaseName + "-db-pool");
        tenantHikariConfig.setDriverClassName((String) tenantInfo.get("driverClassName"));
        //tenantHikariConfig.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
        System.out.println(tenantHikariConfig.getJdbcUrl());
        return new HikariDataSource(tenantHikariConfig);*/


        Properties dsProps = new Properties();
        dsProps.setProperty("url", (String) tenantInfo.get("url"));
//        dsProps.setProperty("username", "app_user");
//        dsProps.setProperty("password", "123456");
        dsProps.setProperty("user", "postgres");
        dsProps.setProperty("password", "P@ssw0rd");

        Properties configProps = new Properties();
        configProps.setProperty("driverClassName", (String) tenantInfo.get("driverClassName"));
        configProps.setProperty("jdbcUrl", (String) tenantInfo.get("url"));

        HikariConfig hc = new HikariConfig(configProps);
        hc.setDataSourceProperties(dsProps);

        if (hc.getDataSource()!=null){
            System.out.println("Yessss bro");
            System.out.println(hc.getDataSource());
            return new HikariDataSource(hc);
        }else {

            return null;
        }
    }
}
