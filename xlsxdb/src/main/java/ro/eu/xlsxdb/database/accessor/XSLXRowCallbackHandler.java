package ro.eu.xlsxdb.database.accessor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ro.eu.xlsxdb.xlsxloader.XLSXCell;
import ro.eu.xlsxdb.xlsxloader.XLSXColumn;
import ro.eu.xlsxdb.xlsxloader.XLSXColumnType;
import ro.eu.xlsxdb.xlsxloader.XLSXRow;

public abstract class XSLXRowCallbackHandler implements TableRowCallbackHandler {
	private final Map<Integer, XLSXColumn> columnsDefinition = new HashMap<Integer, XLSXColumn>(1);
	private Collection<XLSXRow> rows;

	public XSLXRowCallbackHandler(Collection<XLSXRow> rows) {
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
		for (int i = 0; i < columnCount; i++) {
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