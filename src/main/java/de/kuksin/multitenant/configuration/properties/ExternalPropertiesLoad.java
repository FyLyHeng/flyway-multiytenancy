package de.kuksin.multitenant.configuration.properties;

import de.kuksin.multitenant.configuration.properties.tenantServiceProvide.DBUtilServiceProvide;
import de.kuksin.multitenant.configuration.properties.tenantServiceProvide.TenantServiceResolver;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExternalPropertiesLoad implements BeanPostProcessor, InitializingBean, EnvironmentAware {

    public JdbcTemplate jdbcTemplate;
    public ConfigurableEnvironment environment;
    final String PROPERTY_SOURCE_NAME = "configTenant";

    @Autowired
    TenantServiceResolver tenantServiceResolver;
    @Autowired
    DBUtilServiceProvide dbUtilServiceProvide;


    @PostConstruct
    private void init() {
        DataSource datasource = dbUtilServiceProvide.build(environment);
        jdbcTemplate = new JdbcTemplate(datasource);
    }



    @Override
    public void afterPropertiesSet() throws Exception {
        List<JSONObject> listTenantActive = tenantServiceResolver.findActiveAllTenant();

        Map<String, Object> EXTERNAL_TENANT_INFO = new HashMap<>();

        for (int i = 0; i < listTenantActive.size(); i++) {
            EXTERNAL_TENANT_INFO.putAll(listTenantActive.get(i));
        }

        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, EXTERNAL_TENANT_INFO));

    }


    @Override
    public void setEnvironment(Environment environment) {
        if(environment instanceof ConfigurableEnvironment){
            this.environment = (ConfigurableEnvironment) environment;
        }
    }
}
