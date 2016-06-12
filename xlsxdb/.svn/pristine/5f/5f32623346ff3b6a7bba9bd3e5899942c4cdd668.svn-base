package ro.eu.xlsxdb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ro.eu.xlsxdb.database.XSLXTableDao;
import ro.eu.xlsxdb.xlsxloader.XLSXLoader;

/**
 * Created by emilu on 5/21/2016.
 */
@Configuration
@PropertySource("classpath:jdbc.properties")
public class ApplicationConfiguration {
    @Autowired
    private Environment env;

    @Bean(name="XLSXLoader")
    public XLSXLoader xlsxLoader() {
        return new XLSXLoader();
    }

    @Bean(name="jdbcTemplate")
    public JdbcTemplate getJdbcTemplate() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(env.getProperty("jdbc.url"),
                env.getProperty("jdbc.username"), env.getProperty("jdbc.password"));
        driverManagerDataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        return new JdbcTemplate(driverManagerDataSource);
    }

    @Bean(name="xslxTableDao")
    public XSLXTableDao createXSLXTableDao() {
        return new XSLXTableDao();
    }
}
