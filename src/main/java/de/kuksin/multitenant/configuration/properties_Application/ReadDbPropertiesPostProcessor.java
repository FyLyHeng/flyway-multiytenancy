package de.kuksin.multitenant.configuration.properties_Application;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@Component
public class ReadDbPropertiesPostProcessor implements EnvironmentPostProcessor {

    /**
     * Name of the custom property source added by this post processor class
     */
    private static final String PROPERTY_SOURCE_NAME = "configTenant";


    /**
     * Adds Spring Environment custom logic. This custom logic fetch properties from database and setting highest precedence
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        System.out.println("here working for load config properties__\n"+environment);

        Map<String, Object> propertySource = new HashMap<>();

        try {

            // Build manually datasource to ServiceConfig
            DataSource ds = DataSourceBuilder
                    .create()
                    .username(environment.getProperty("spring.datasource.username"))
                    .password(environment.getProperty("spring.datasource.password"))
                    .url(environment.getProperty("spring.datasource.url"))
                    .driverClassName(environment.getProperty("spring.datasource.driverClassName"))
                    .build();

            // Fetch all properties

            Connection connection = ds.getConnection();

            System.out.println("here working for load config properties");

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM data_source_config WHERE is_active = '1' ");

            ResultSet rs = preparedStatement.executeQuery();

            // Populate all properties into the property source
            while (rs.next()) {

                JSONObject data = convertToMap(rs.getString("value"));
                System.out.println("Child Tenant Load "+rs.getString("name"));
                propertySource.putAll(data);
            }

            rs.close();
            preparedStatement.clearParameters();
            preparedStatement.close();
            connection.close();

            // Create a custom property source with the highest precedence and add it to Spring Environment
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, propertySource));

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject convertToMap (String jsonString) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(jsonString);
        return json;
    }
}
