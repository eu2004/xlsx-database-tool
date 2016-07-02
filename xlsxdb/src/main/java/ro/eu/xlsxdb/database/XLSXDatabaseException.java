package ro.eu.xlsxdb.database;

/**
 * Created by emilu on 5/22/2016.
 */
public class XLSXDatabaseException extends Exception{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XLSXDatabaseException(Throwable t) {
        super(t);
    }

    public XLSXDatabaseException(String message) {
        super(message);
    }
}
