package ro.eu.xlsxdb.database.accessor;

public class DBAccessorException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String ERROR_MESSAGE = "Failed to execute database operation because of : %s";

	public DBAccessorException(Throwable exception) {
		super(exception);
	}

	public DBAccessorException(String exceptionMessage) {
		super(String.format(ERROR_MESSAGE, exceptionMessage));
	}
}
