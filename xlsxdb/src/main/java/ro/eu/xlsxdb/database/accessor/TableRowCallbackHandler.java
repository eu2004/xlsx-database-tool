package ro.eu.xlsxdb.database.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface TableRowCallbackHandler {
	void processRow(ResultSet rs) throws SQLException;
}