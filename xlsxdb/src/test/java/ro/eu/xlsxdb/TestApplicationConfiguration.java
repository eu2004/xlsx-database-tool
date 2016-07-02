package ro.eu.xlsxdb;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ro.eu.xlsxdb.application.SpringApplicationConfiguration;

/**
 * Created by emilu on 5/22/2016.
 */
@Configuration
@PropertySource("classpath:test-jdbc.properties")
public class TestApplicationConfiguration extends SpringApplicationConfiguration{
}
