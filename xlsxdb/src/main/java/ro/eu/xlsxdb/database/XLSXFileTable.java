package ro.eu.xlsxdb.database;

import java.util.ArrayList;
import java.util.List;

import ro.eu.xlsxdb.xlsxloader.XLSXColumn;
import ro.eu.xlsxdb.xlsxloader.XLSXFile;
import ro.eu.xlsxdb.xlsxloader.XLSXRow;

public class XLSXFileTable {
	private XLSXFile xlsxFile;
	private String tableName;
	private List<XLSXFileTableColumn> columns;

	public XLSXFileTable(XLSXFile xlsxFile) {
		this.xlsxFile = xlsxFile;
		this.tableName = buildTableName(xlsxFile.getName());
		this.columns = buildTableColumns(xlsxFile.getColumns());
	}

	public String getTableName() {
		return tableName;
	}

	public List<XLSXRow> getRows() {
		return xlsxFile.getRows();
	}

	public List<XLSXFileTableColumn> getColumns() {
		return columns;
	}

	private String buildTableName(String name) {
		if (name == null || name.trim().length() == 0) {
			return "Table_" + System.currentTimeMillis();
		}

		StringBuilder tableName = new StringBuilder("table_");
		for (Character ch : name.toCharArray()) {
			if (Character.isLetterOrDigit(ch)) {
				tableName.append(ch);
			} else {
				tableName.append("_");
			}
		}
		return tableName.toString().toLowerCase();
	}

	private List<XLSXFileTableColumn> buildTableColumns(List<XLSXColumn> xlsxColumns) {
		List<XLSXFileTableColumn> tableColumns = new ArrayList<>();
		xlsxColumns.forEach(column -> tableColumns.add(buildTableColumn(column, tableColumns)));
		return tableColumns;
	}

	private XLSXFileTableColumn buildTableColumn(XLSXColumn column, List<XLSXFileTableColumn> tableColumns) {
		String colName = buildColumnName(column);
		StringBuilder colNameBuilder = new StringBuilder(colName);
		tableColumns.forEach(xlsxFileTableColumn -> {
			if (xlsxFileTableColumn.getIndex() != column.getIndex()) {
				if (xlsxFileTableColumn.getName().equals(colNameBuilder.toString())) {
					colNameBuilder.append("0");
				}
			}
		});
		return new XLSXFileTableColumn(column, colNameBuilder.toString());
	}

	private String buildColumnName(XLSXColumn column) {
		String columnName = column.getName();
		StringBuilder columnNameBuilder = new StringBuilder("col_");
		for (Character ch : columnName.toCharArray()) {
			if (Character.isLetterOrDigit(ch)) {
				columnNameBuilder.append(ch);
			} else {
				columnNameBuilder.append("_");
			}
		}
		return columnNameBuilder.toString().toLowerCase();
	}

	@Override
	public String toString() {
		return "XLSXFileTable [xlsxFile=" + xlsxFile + "]";
	}
}
