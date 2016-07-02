package ro.eu.xlsxdb.application;

import java.io.File;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ro.eu.xlsxdb.ApplicationExecutor;
import ro.eu.xlsxdb.database.XSLXTableDao;
import ro.eu.xlsxdb.xlsxloader.XLSXLoader;

/**
 * Created by emilu on 5/21/2016.
 */
public class ApplicationLauncher {
	private static final Logger logger = Logger.getLogger(ApplicationLauncher.class);

	public static void main(String[] args) {
		// check input arguments
		if (args.length == 0) {
			throw new IllegalArgumentException("Invalid argument exception. Use: load.bat <directory path>");
		}

		// extract parameters
		String folderPath = args[0];
		String action = args.length > 1 ? args[1] : "load";

		// validate parameters value
		File folder = Paths.get(folderPath).toFile();
		if (!folder.exists() || !folder.isDirectory()) {
			throw new IllegalArgumentException(folderPath + " is not valid");
		}

		// launch app
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringApplicationConfiguration.class);
		ApplicationExecutor applicationExecutor = new ApplicationExecutor(logger, (XLSXLoader) context.getBean("XLSXLoader"), (XSLXTableDao) context.getBean("xslxTableDao"));
		applicationExecutor.execute(folderPath, action);
	}
}
