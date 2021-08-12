//package de.kuksin.multitenant.configuration.properties;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import org.hibernate.jpa.HibernatePersistenceProvider;
//import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//import java.util.Properties;
//
//@Configuration
//@ConfigurationProperties(prefix = "datasources.ld")
//@EnableTransactionManagement
//@EnableJpaRepositories(entityManagerFactoryRef = "postgreEntityManagerFactory", transactionManagerRef = "postgreTransactionManager")
//public class LdConfiguration extends HikariConfig {
//
//    @Bean(name = "postgreDataSource")
//    @Primary
//    public DataSource dataSource() {
//        return new HikariDataSource(this);
//    }
//
//    @Bean(name = "postgreEntityManagerFactory")
//    @Primary
//    public LocalContainerEntityManagerFactoryBean postgreEntityManagerFactory(final EntityManagerFactoryBuilder builder, @Qualifier("postgreDataSource") final DataSource dataSource)
//    {
//        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
//        entityManagerFactoryBean.setJpaVendorAdapter(this.vendorAdaptor());
//        entityManagerFactoryBean.setDataSource(dataSource);
//        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
//        entityManagerFactoryBean.setPersistenceUnitName("postgre");
//        entityManagerFactoryBean.setPackagesToScan("es.oplus.ld.model");
//        entityManagerFactoryBean.setJpaProperties(this.jpaHibernateProperties());
//        entityManagerFactoryBean.afterPropertiesSet();
//        return entityManagerFactoryBean;
//    }
//
//    @Bean(name = "postgreTransactionManager")
//    @Primary
//    public PlatformTransactionManager postgreTransactionManager(@Qualifier("postgreEntityManagerFactory") final EntityManagerFactory emf) {
//        return new JpaTransactionManager(emf);
//    }
//
//    private HibernateJpaVendorAdapter vendorAdaptor() {
//        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        // put all the adapter properties here, such as show sql
//        return vendorAdapter;
//    }
//
//    private Properties jpaHibernateProperties() {
//        final Properties properties = new Properties();
//        // put all required jpa propeties here
//        return properties;
//    }
//
//}
