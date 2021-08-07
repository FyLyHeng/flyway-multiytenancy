package de.kuksin.multitenant.configuration;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.HashMap;
import java.util.Map;

//@Configuration
//public class AutoDDLConfig
//{

/*    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${schemas.list}")
    private String schemasList;

*//*    public AutoDDLConfig(String username, String password, String schemasList) {
        this.username = username;
        this.password = password;
        this.schemasList = schemasList;
    }*//*

    @Bean
    public void bb()
    {

        if (StringUtils.isBlank(schemasList))
        {
            return;
        }

        String[] tenants = schemasList.split(",");

        for (String tenant : tenants)
        {
            tenant = tenant.trim();
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver"); // Change here to MySql Driver
            dataSource.setSchema(tenant);
            dataSource.setUrl("jdbc:postgresql://localhost:5432/" + tenant
                    + "?autoReconnect=true&characterEncoding=utf8&useSSL=false&useTimezone=true&serverTimezone=Asia/Kolkata&useLegacyDatetimeCode=false&allowPublicKeyRetrieval=true");
            dataSource.setUsername(username);
            dataSource.setPassword(password);

            LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
            emfBean.setDataSource(dataSource);
            emfBean.setPackagesToScan("de**"); // Here mention JPA entity path / u can leave it scans all packages
            emfBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            emfBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
            Map<String, Object> properties = new HashMap<>();

            properties.put("hibernate.hbm2ddl.auto", "update");
            properties.put("hibernate.default_schema", tenant);

            emfBean.setJpaPropertyMap(properties);
            emfBean.setPersistenceUnitName(dataSource.toString());
            emfBean.afterPropertiesSet();
        }

    }*/

//}
