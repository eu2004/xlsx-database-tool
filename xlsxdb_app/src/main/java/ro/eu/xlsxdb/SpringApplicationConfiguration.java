package ro.eu.xlsxdb;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ro.eu.xlsxdb.database.XSLXTableDao;
import ro.eu.xlsxdb.database.accessor.DBAccessor;
import ro.eu.xlsxdb.database.accessor.TableRowCallbackHandler;
import ro.eu.xlsxdb.database.accessor.TableRowCallbackHandlerFactory;
import ro.eu.xlsxdb.database.accessor.XSLXRowCallbackHandler;
import ro.eu.xlsxdb.xlsxloader.XLSXCell;
import ro.eu.xlsxdb.xlsxloader.XLSXColumn;
import ro.eu.xlsxdb.xlsxloader.XLSXColumnType;
import ro.eu.xlsxdb.xlsxloader.XLSXLoader;
import ro.eu.xlsxdb.xlsxloader.XLSXRow;

/**
 * Created by emilu on 5/21/2016.
 */
@Configuration
@PropertySource("classpath:jdbc.properties")
public class SpringApplicationConfiguration {
    @Autowired
    private Environment env;

    @Bean(name="XLSXLoader")
    public XLSXLoader xlsxLoader() {
        return new XLSXLoader();
    }

    @Bean(name="databaseaccessor")
    public DBAccessor getDBAccessor() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(env.getProperty("jdbc.url"),
                env.getProperty("jdbc.username"), env.getProperty("jdbc.password"));
        driverManagerDataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        
        return new DBAccessor() {
			private JdbcTemplate jdbcTemplate = new JdbcTemplate(driverManagerDataSource);
			@Override
			public int update(String sql) {
				return jdbcTemplate.update(sql);
			}
			
			@Override
			public void query(String sql, TableRowCallbackHandler rch) {
				jdbcTemplate.query(sql, (RowCallbackHandler) rch);
			}
			
			@Override
			public DataSource getDataSource() {
				return jdbcTemplate.getDataSource();
			}
		};
    }
    
    private static class SpringXSLXRowCallbackHandler extends XSLXRowCallbackHandler implements RowCallbackHandler {
        public SpringXSLXRowCallbackHandler(Collection<XLSXRow> rows) {
            super(rows);
        }
    }

    @Bean(name="xslxTableDao")
    public XSLXTableDao createXSLXTableDao() {
    	TableRowCallbackHandlerFactory xslxRowCallbackHandlerFactory = new TableRowCallbackHandlerFactory() {
			@Override
			public TableRowCallbackHandler createCallback(Collection<XLSXRow> rows) {
				return new SpringXSLXRowCallbackHandler(rows);
			}
		};
		
		XSLXTableDao xslxTableDao = new XSLXTableDao();
    	xslxTableDao.setDatabaseAccessor(getDBAccessor());
		xslxTableDao.setXslxRowCallbackHandlerFactory(xslxRowCallbackHandlerFactory);
        return xslxTableDao;
    }
}
