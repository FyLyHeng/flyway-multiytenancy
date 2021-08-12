package de.kuksin.multitenant.configuration.properties.tenantServiceProvide;

import com.google.gson.Gson;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


/**
 * @This class is all about deep service database like convert, bind, ...
 */


@Component
public class DBUtilServiceProvide {


    //PostgrestSQL Register new DataBase
    public String databaseURLFromPostgresSQLJdbcUrl(String tenantURL){
        String BaseURL_ALLOW_CREATE = "?createDatabaseIfNotExist=true&verifyServerCertificate=false&useSSL=false&requireSSL=false&useUnicode=yes";
        return tenantURL+BaseURL_ALLOW_CREATE;
    }


    //Load Master datasource properties and Bulid
    public DataSource build(Environment environment){
        return DataSourceBuilder
                .create()
                .username(environment.getProperty("spring.datasource.username"))
                .password(environment.getProperty("spring.datasource.password"))
                .url(environment.getProperty("spring.datasource.url"))
                .driverClassName(environment.getProperty("spring.datasource.driverClassName"))
                .build();
    }


    //Format tenant value for Load to properties
    public JSONObject tenantValueFormatStringJsonToMap(String jsonString) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(jsonString);
        return json;
    }
    public String tenantValueFormatMapToStringJson (String tenantID) {
        Map<String,Object> tenantInsertFormat = new HashMap<>();
        tenantInsertFormat.put("tenants.datasources."+tenantID+".url:","jdbc:postgresql://localhost:5432/"+tenantID+"?createDatabaseIfNotExist=true&verifyServerCertificate=false&useSSL=false&requireSSL=false&useUnicode=yes");
        tenantInsertFormat.put("tenants.datasources."+tenantID+".driverClassName:","org.postgresql.Driver");
        tenantInsertFormat.put("tenants.datasources."+tenantID+".username:","postgres");
        tenantInsertFormat.put("tenants.datasources."+tenantID+".password:","P@ssw0rd");

        Gson gson = new Gson();
        String json = gson.toJson(tenantInsertFormat);
        return json;
    }


    //Format tenant info
    public Map<String,Object> bindTenantValueToMap(Map<String,Object> rawTenantInfo, String tenantName){
        Map<String,Object> result = new HashMap<>();

        result.put("url",rawTenantInfo.get("tenants.datasources."+tenantName+".url:"));
        result.put("username",rawTenantInfo.get("tenants.datasources."+tenantName+".username:"));
        result.put("password",rawTenantInfo.get("tenants.datasources."+tenantName+".password:"));
        result.put("driverClassName",rawTenantInfo.get("tenants.datasources."+tenantName+".driverClassName:"));
        return result;
    }
    public Map<String,Object> bindTenantValueToMap(String tenantValueStringJson, String tenantName) throws ParseException {

        JSONObject rawValue = this.tenantValueFormatStringJsonToMap(tenantValueStringJson);
        return bindTenantValueToMap(rawValue,tenantName);
    }


}
