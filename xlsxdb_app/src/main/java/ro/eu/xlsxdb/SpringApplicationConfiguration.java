package ro.eu.xlsxdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
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
import ro.eu.xlsxdb.database.accessor.DBAccessorException;
import ro.eu.xlsxdb.database.accessor.TableRowCallbackHandler;
import ro.eu.xlsxdb.database.accessor.TableRowCallbackHandlerFactory;
import ro.eu.xlsxdb.database.accessor.XSLXRowCallbackHandler;
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
			private final Logger logger = Logger.getLogger(DBAccessor.class);
			
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

			@Override
			public boolean tableExists(String tableName) throws DBAccessorException {
				boolean exists = false;
				ResultSet rs = null;
		        Connection conn = null;
		        try {
		        	conn = jdbcTemplate.getDataSource().getConnection();
		            rs = conn.getMetaData().getTables(null, null, "%", null);
		            while (rs.next() && !exists) {
		            	exists = rs.getString("TABLE_NAME").toUpperCase().equals(tableName.toUpperCase());
		            }
		        }catch (SQLException ex) {
		        	logger.error("Error in finding a table " + ex.getMessage(), ex);
		        	throw new DBAccessorException("Error during searching for table [" + tableName + "] : " + ex.getMessage());
		        }finally{
		        	try {
		        		if (rs != null) {
		        			rs.close();
		        		}
					} catch (SQLException e) {
						logger.warn("Error closing result set " + e.getMessage(), e);
					}
		        	try {
		        		if (conn != null) {
		        			conn.close();
		        		}
					} catch (SQLException e) {
						logger.error("Error closing db connection " + e.getMessage(), e);
					}
		        }
		        
		        return exists;
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
