package ro.eu.xlsxdb.database.accessor;

import javax.sql.DataSource;

/**
 * Provides a set of common operations on a database and also is an access point
 * to the database as a datasource.
 * 
 * @author emilu
 *
 */
public interface DBAccessor {
	public DataSource getDataSource() throws DBAccessorException;

	public boolean tableExists(String tableName) throws DBAccessorException;

	public void query(String sql, TableRowCallbackHandler rch) throws DBAccessorException;

	public int update(final String sql) throws DBAccessorException;
}