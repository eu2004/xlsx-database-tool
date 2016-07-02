package ro.eu.xlsxdb.database.accessor;

import java.util.Collection;

import ro.eu.xlsxdb.xlsxloader.XLSXRow;

public abstract class TableRowCallbackHandlerFactory {
	public abstract TableRowCallbackHandler createCallback(Collection<XLSXRow> rows);
}
