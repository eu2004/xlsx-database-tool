package ro.eu.xlsxdb.xlsxloader;

import java.util.List;

/**
 * Created by emilu on 5/21/2016.
 */
public class XLSXFile {
	private List<XLSXColumn> columns;
	private List<XLSXRow> rows;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<XLSXRow> getRows() {
		return rows;
	}

	public void setRows(List<XLSXRow> rows) {
		this.rows = rows;
	}

	public List<XLSXColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<XLSXColumn> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "XLSXFile [name=" + name + "]";
	}
}
