package de.kuksin.multitenant.configuration.datasource;

import de.kuksin.multitenant.configuration.properties.tenantServiceProvide.TenantServiceResolver;
import de.kuksin.multitenant.configuration.web.ThreadTenantStorage;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;


public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    @Autowired
    TenantServiceResolver tenantServiceResolver;

    @Autowired
    DataSourceConfiguration sourceConfiguration;

    @Override
    protected Object determineCurrentLookupKey() {
        return ThreadTenantStorage.getTenantId();
    }


    @Override
    protected DataSource determineTargetDataSource() {

        String tenantName = (String) determineCurrentLookupKey();
        DataSource currentTenant = null;
        System.out.println("Current X-Tenant-ID : "+tenantName);

        try {

            //Tenant null in case first init boot project
            if (tenantName == null)
                return super.determineTargetDataSource();


                Map<String,Object> tenant = tenantServiceResolver.findTenantByName(tenantName);
                if (tenant == null){
                    System.out.println("####### START Register New Tenant DataSource: "+tenantName+" #######");
                    currentTenant = registerNewTenant(tenantName);
                }else {
                    currentTenant = super.determineTargetDataSource();
                }

        } catch (ParseException ignored) {}

        return currentTenant;
    }



    private DataSource registerNewTenant(String tenantName) {

        DataSource newTenantDataSource = null;
        try {
            //insert into master_data_source_config
            Map<String,Object> tenant = tenantServiceResolver.insertNewTenantInfo(tenantName);
            System.out.println("New Requesting Tenant "+tenant);


            //create new database
            newTenantDataSource = tenantServiceResolver.createDataSourceForTenantId(tenant,tenantName);
            sourceConfiguration.flywayMigrate(newTenantDataSource);

            //auto reload properties
            //some thing about reload wait a bit bro

        }catch (Exception e){
            System.out.println("####### Fail To Register New Tenant DataSource: "+tenantName+" Have Been Remove #######");
            tenantServiceResolver.removeTenant(tenantName);
        }




        return newTenantDataSource;
    }

}