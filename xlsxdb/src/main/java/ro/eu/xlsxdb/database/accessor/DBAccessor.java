package ro.eu.xlsxdb.database.accessor;

import javax.sql.DataSource;

public interface DBAccessor {
	public DataSource getDataSource();
	
	public void query(String sql, TableRowCallbackHandler rch);
	
	public int update(final String sql);
}
