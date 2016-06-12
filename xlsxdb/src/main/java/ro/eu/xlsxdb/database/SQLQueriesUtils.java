package ro.eu.xlsxdb.database;

import ro.eu.xlsxdb.xlsxloader.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by emilu on 5/22/2016.
 */
public class SQLQueriesUtils {
	private static final ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		}
	};

	private SQLQueriesUtils() {
	}

	public static String generateSQLCreateTable(XLSXFileTable xlsxFile) {
		String sqlQuery = "create table ";
		sqlQuery += xlsxFile.getTableName();
		sqlQuery += " (";
		Iterator<XLSXFileTableColumn> columnIterator = xlsxFile.getColumns().iterator();
		while (columnIterator.hasNext()) {
			sqlQuery += generateSQLCreateTableColumn(columnIterator.next());
			if (columnIterator.hasNext()) {
				sqlQuery += ",";
			}
		}
		sqlQuery += " );";
		return sqlQuery;
	}

	public static String generateSQLInsertRow(String tableName, List<XLSXFileTableColumn> columns, XLSXRow row) {
		String sqlQuery = "INSERT INTO ";
		sqlQuery += tableName + " (";
		sqlQuery += getInsertIntoTableColumns(columns);
		sqlQuery += ") VALUES ( ";
		sqlQuery += getInsertIntoTableValues(columns, row);
		sqlQuery += ");";
		return sqlQuery;
	}

	private static String generateSQLCreateTableColumn(XLSXFileTableColumn column) {
		return column.getName() + " " + getSQLColumnType(column.getType());
	}

	private static String getSQLColumnType(TableColumnType type) {
		switch (type) {
		case VARCHAR:
			return type.name() + "(255)";
		default:
			return type.name();
		}
	}

	private static String getInsertIntoTableValues(List<XLSXFileTableColumn> columns, XLSXRow row) {
		String sqlQuery = "";
		Iterator<XLSXFileTableColumn> columnIterator = columns.iterator();
		while (columnIterator.hasNext()) {
			XLSXFileTableColumn column = columnIterator.next();
			sqlQuery += getInsertIntoTableValue(column.getType(), getCell(row.getCells(), column.getIndex()));
			if (columnIterator.hasNext()) {
				sqlQuery += ",";
			}
		}
		return sqlQuery;
	}

	private static XLSXCell getCell(List<XLSXCell> cells, int index) {
		if (cells.size() <= index) {
			return null;
		}
		return cells.get(index);
	}

	private static String getInsertIntoTableValue(TableColumnType type, XLSXCell cell) {
		switch (type) {
		case BOOLEAN:
			return cell != null ? cell.getValue().toString().toUpperCase() : "NULL";
		case TIMESTAMP:
			return cell != null ? getInsertIntoTableDateValue((Date) cell.getValue()) : "NULL";
		case FLOAT:
			return cell != null ? getInsertIntoTableNumericValue((Double) cell.getValue()) : "NULL";
		default:
			return cell != null ? "'" + cell.getValue().toString().replaceAll("'", "''") + "'" : "NULL";
		}
	}

	private static String getInsertIntoTableNumericValue(Double value) {
		return value.toString();
	}

	private static String getInsertIntoTableDateValue(Date value) {
		// TIMESTAMP '2008-08-08 20:08:08'
		try {
			return "TIMESTAMP '" + formatter.get().format(value) + "'";
		} finally {
			formatter.remove();
		}
	}

	private static String getInsertIntoTableColumns(List<XLSXFileTableColumn> columns) {
		String sqlQuery = "";
		Iterator<XLSXFileTableColumn> columnIterator = columns.iterator();
		while (columnIterator.hasNext()) {
			sqlQuery += columnIterator.next().getName();
			if (columnIterator.hasNext()) {
				sqlQuery += ",";
			}
		}
		return sqlQuery;
	}
}