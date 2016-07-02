package ro.eu.xlsxdb.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import ro.eu.xlsxdb.database.accessor.DBAccessor;
import ro.eu.xlsxdb.database.accessor.TableRowCallbackHandlerFactory;
import ro.eu.xlsxdb.xlsxloader.XLSXRow;

/**
 * Created by emilu on 5/21/2016.
 */
public class XSLXTableDao {
    private static final Logger logger = Logger.getLogger(XSLXTableDao.class);

    private DBAccessor databaseAccessor;
    private TableRowCallbackHandlerFactory xslxRowCallbackHandlerFactory;

    public DBAccessor getDatabaseAccessor() {
		return databaseAccessor;
	}

	public void setDatabaseAccessor(DBAccessor databaseAccessor) {
		this.databaseAccessor = databaseAccessor;
	}

	public TableRowCallbackHandlerFactory getXslxRowCallbackHandlerFactory() {
		return xslxRowCallbackHandlerFactory;
	}

	public void setXslxRowCallbackHandlerFactory(TableRowCallbackHandlerFactory xslxRowCallbackHandlerFactory) {
		this.xslxRowCallbackHandlerFactory = xslxRowCallbackHandlerFactory;
	}

	public void loadXLSXFile(XLSXFileTable xlsxFileTable) {
        this.dropTableIfExists(xlsxFileTable.getTableName());
        databaseAccessor.update(SQLQueriesUtils.generateSQLCreateTable(xlsxFileTable));
        loadXLSXDataFile(xlsxFileTable);
    }

    public void dropTableIfExists(String tableName) {
        boolean exists = false;
        ResultSet rs = null;
        Connection conn = null;
        try {
        	conn = databaseAccessor.getDataSource().getConnection();
            rs = conn.getMetaData().getTables(null, null, "%", null);
            while (rs.next() && !exists) {
            	exists = rs.getString("TABLE_NAME").toUpperCase().equals(tableName.toUpperCase());
            }
        }catch (SQLException ex) {
        }finally{
        	try {
        		if (rs != null) {
        			rs.close();
        		}
			} catch (SQLException e) {
			}
        	try {
        		if (conn != null) {
        			conn.close();
        		}
			} catch (SQLException e) {
			}
        }

        if (exists) {
            databaseAccessor.update(String.format("drop table %s;", tableName));
            logger.info(tableName + " dropped");
        }
    }

    public Iterator<XLSXRow> selectTable(String tableName) {
        final List<XLSXRow> rows = new ArrayList<XLSXRow>();
        databaseAccessor.query(String.format("select * from %s", tableName), xslxRowCallbackHandlerFactory.createCallback(rows));
        return rows.iterator();
    }

    private void loadXLSXDataFile(XLSXFileTable xlsxFileTable) {
        logger.info("Start loading file " + xlsxFileTable.getTableName());
        List<XLSXRow> rows = xlsxFileTable.getRows();
        for(XLSXRow row : rows) {
            insertRow(xlsxFileTable.getTableName(), xlsxFileTable.getColumns(), row);
        }
        logger.info("File " + xlsxFileTable + " loaded.");
    }

    private void insertRow(String name, List<XLSXFileTableColumn> columns, XLSXRow row) {
        databaseAccessor.update(SQLQueriesUtils.generateSQLInsertRow(name, columns, row));
    }

    //TODO move to another project
    
}
