package ro.eu.xlsxdb.application;

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
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ro.eu.xlsxdb.database.XSLXTableDao;
import ro.eu.xlsxdb.database.accessor.DBAccessor;
import ro.eu.xlsxdb.database.accessor.TableRowCallbackHandler;
import ro.eu.xlsxdb.database.accessor.TableRowCallbackHandlerFactory;
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
				jdbcTemplate.query(sql, (ResultSetExtractor) rch);
			}
			
			@Override
			public DataSource getDataSource() {
				return jdbcTemplate.getDataSource();
			}
		};
    }
    
    private static class SpringXSLXRowCallbackHandler implements TableRowCallbackHandler, RowCallbackHandler {
        private final Map<Integer, XLSXColumn> columnsDefinition = new HashMap<Integer, XLSXColumn>(1);
        private Collection<XLSXRow> rows;

        public SpringXSLXRowCallbackHandler(Collection<XLSXRow> rows) {
            this.rows = rows;
        }

        public void processRow(ResultSet resultSet) throws SQLException {
            XLSXRow row = new XLSXRow();
            row.setCells(new ArrayList<XLSXCell>(1));
            loadRow(row, resultSet);
            this.rows.add(row);
        }

        private void loadRow(XLSXRow row, ResultSet resultSet) throws SQLException {
            int columnCount = resultSet.getMetaData().getColumnCount();
            for(int i = 0; i < columnCount; i++) {
                XLSXColumn column = getXLSXColumnDefinition(i + 1, resultSet.getMetaData());
                XLSXCell xlsxCell = new XLSXCell();
                xlsxCell.setValue(getValue(column, resultSet));
                xlsxCell.setColumn(column);
                row.getCells().add(xlsxCell);
            }
        }

        private Object getValue(XLSXColumn column, ResultSet resultSet) throws SQLException {
            switch (column.getType()) {
                case BOOLEAN:
                    return resultSet.getBoolean(column.getIndex() + 1);
                case NUMERIC:
                    return resultSet.getFloat(column.getIndex() + 1);
                case DATE:
                    return resultSet.getTimestamp(column.getIndex() + 1);
                default:
                    return resultSet.getString(column.getIndex() + 1);
            }
        }

        private XLSXColumn getXLSXColumnDefinition(int index, ResultSetMetaData metaData) throws SQLException {
            if (columnsDefinition.get(index) != null) {
                return columnsDefinition.get(index);
            }

            String name = metaData.getColumnName(index);
            int sqlType = metaData.getColumnType(index);
            XLSXColumnType columnType = null;
            switch (sqlType) {
                case Types.BOOLEAN:
                    columnType = XLSXColumnType.BOOLEAN;
                    break;
                case Types.BIGINT:
                case Types.DECIMAL:
                case Types.DOUBLE:
                case Types.INTEGER:
                case Types.FLOAT:
                    columnType = XLSXColumnType.NUMERIC;
                    break;
                case Types.DATE:
                case Types.TIMESTAMP:
                    columnType = XLSXColumnType.DATE;
                    break;
                default:
                    columnType = XLSXColumnType.TEXT;
            }
            XLSXColumn column = new XLSXColumn();
            column.setType(columnType);
            column.setName(name);
            column.setIndex(index - 1);
            columnsDefinition.put(index, column);
            return column;
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
