package ro.eu.xlsxdb.database;

import ro.eu.xlsxdb.xlsxloader.XLSXColumn;

public class XLSXFileTableColumn {
	private XLSXColumn xlsxColumn;
	private String tableColumnName;

	public XLSXFileTableColumn(XLSXColumn xlsxColumn, String tableColumnName) {
		this.xlsxColumn = xlsxColumn;
		this.tableColumnName = tableColumnName;
	}

	public String getName() {
		return tableColumnName;
	}

	public TableColumnType getType() {
		switch (xlsxColumn.getType()) {
		case BOOLEAN:
			return TableColumnType.BOOLEAN;
		case DATE:
			return TableColumnType.TIMESTAMP;
		case NUMERIC:
			return TableColumnType.FLOAT;
		default:
			return TableColumnType.VARCHAR;
		}
	}

	public int getIndex() {
		return xlsxColumn.getIndex();
	}

	@Override
	public String toString() {
		return "XLSXFileTableColumn [tableColumnName=" + tableColumnName + "]";
	}

}
