package ro.eu.xlsxdb.xlsxloader;

/**
 * Created by emilu on 5/21/2016.
 */
public class XLSXLoaderException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XLSXLoaderException(Throwable t) {
		super(t);
	}

	public XLSXLoaderException(String message) {
		super(message);
	}
}
