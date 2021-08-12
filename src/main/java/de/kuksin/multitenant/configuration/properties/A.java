//package de.kuksin.multitenant.configuration.properties;
//
//import de.kuksin.multitenant.configuration.datasource.DataSourceConfiguration;
//import de.kuksin.multitenant.configuration.properties.tenantServiceProvide.DBUtilServiceProvide;
//import de.kuksin.multitenant.configuration.properties.tenantServiceProvide.TenantServiceResolver;
//import de.kuksin.multitenant.configuration.web.ThreadTenantStorage;
//import net.minidev.json.JSONObject;
//import net.minidev.json.parser.ParseException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.EnvironmentAware;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.core.env.Environment;
//import org.springframework.core.env.MapPropertySource;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
//import javax.annotation.PostConstruct;
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class A extends AbstractRoutingDataSource implements EnvironmentAware {
//
//    public JdbcTemplate jdbcTemplate;
//    public ConfigurableEnvironment environment;
//    final String PROPERTY_SOURCE_NAME = "configTenant";
//    DataSource datasource ;
//
//    @Autowired
//    TenantServiceResolver tenantServiceResolver;
//    @Autowired
//    DBUtilServiceProvide dbUtilServiceProvide;
//
//    @Autowired
//    DataSourceConfiguration sourceConfiguration;
//
//    @PostConstruct
//    private void init() {
//        DataSource datasource = dbUtilServiceProvide.build(environment);
//        this.datasource = datasource;
//        jdbcTemplate = new JdbcTemplate(datasource);
//    }
//
//    @Override
//    public void setEnvironment(Environment environment) {
//        if(environment instanceof ConfigurableEnvironment){
//            this.environment = (ConfigurableEnvironment) environment;
//        }
//    }
//
//
////###############################################################
//
//
//    /**
//     * Determine the current lookup key. This will typically be
//     * implemented to check a thread-bound transaction context.
//     * <p>Allows for arbitrary keys. The returned key needs
//     * to match the stored lookup key type, as resolved by the
//     * {@link #resolveSpecifiedLookupKey} method.
//     */
//    @Override
//    protected Object determineCurrentLookupKey() {
//        return ThreadTenantStorage.getTenantId();
//    }
//
//    @Override
//    protected DataSource determineTargetDataSource() {
//
//        String tenantName = (String) determineCurrentLookupKey();
//        DataSource currentTenant = null;
//        System.out.println("Current X-Tenant-ID : "+tenantName);
//
//        try {
//
//            //Tenant null in case first init boot project
//            if (tenantName == null)
//                return datasource;
//
//
//            Map<String,Object> tenant = tenantServiceResolver.findTenantByName(tenantName);
//            if (tenant == null){
//                System.out.println("####### START Register New Tenant DataSource: "+tenantName+" #######");
//                currentTenant = registerNewTenant(tenantName);
//            }else {
//                currentTenant = getDatasource(tenant,tenantName);
//            }
//
//        } catch (ParseException ignored) {}
//
//        return currentTenant;
//    }
//
//
//
//    private DataSource registerNewTenant(String tenantName) {
//
//        DataSource newTenantDataSource = null;
//        try {
//            //insert into master_data_source_config
//            Map<String,Object> tenant = tenantServiceResolver.insertNewTenantInfo(tenantName);
//            System.out.println("New Requesting Tenant "+tenant);
//
//
//            //create new database
//            newTenantDataSource = tenantServiceResolver.createDataSourceForTenantId(tenant,tenantName);
//            sourceConfiguration.flywayMigrate(newTenantDataSource);
//
//            //auto reload properties
//            //some thing about reload wait a bit bro
//
//        }catch (Exception e){
//            System.out.println("####### Fail To Register New Tenant DataSource: "+tenantName+" Have Been Remove #######");
//            tenantServiceResolver.removeTenant(tenantName);
//        }
//
//
//        return newTenantDataSource;
//    }
//
//    private DataSource getDatasource (Map<String,Object> tenantInfo, String tenantName){
//        DataSource existDataSource = tenantServiceResolver.createDataSourceForTenantId(tenantInfo,tenantName);
//        sourceConfiguration.flywayMigrate(existDataSource);
//        return existDataSource;
//    }
//
//
//    //###############################################################
//    @Override
//    public void afterPropertiesSet() {
//        List<JSONObject> listTenantActive = null;
//        try {
//            listTenantActive = tenantServiceResolver.findActiveAllTenant();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        Map<String, Object> EXTERNAL_TENANT_INFO = new HashMap<>();
//
//        for (int i = 0; i < listTenantActive.size(); i++) {
//            EXTERNAL_TENANT_INFO.putAll(listTenantActive.get(i));
//        }
//
//        super.setDefaultTargetDataSource(jdbcTemplate);
//        super.setTargetDataSources(new HashMap<>());
//        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, EXTERNAL_TENANT_INFO));
//
//    }
//}
