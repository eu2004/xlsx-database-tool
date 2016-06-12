package ro.eu.xlsxdb;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ro.eu.xlsxdb.database.XLSXFileTable;
import ro.eu.xlsxdb.database.XLSXFileTableExporter;
import ro.eu.xlsxdb.database.XSLXTableDao;
import ro.eu.xlsxdb.xlsxloader.XLSXFile;
import ro.eu.xlsxdb.xlsxloader.XLSXLoader;
import ro.eu.xlsxdb.xlsxloader.XLSXLoaderException;

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
		ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
		List<XLSXFile> xslxFiles = loadXLSX((XLSXLoader) context.getBean("XLSXLoader"), folderPath);
		switch (action) {
			case "load": {
				xslxFiles.forEach(xlsxFile -> {
					((XSLXTableDao) context.getBean("xslxTableDao")).loadXLSXFile(new XLSXFileTable(xlsxFile));
				});
				break;
			}
			case "export": {
				xslxFiles.forEach(xlsxFile -> {
					try {
						new XLSXFileTableExporter(new XLSXFileTable(xlsxFile)).exportXLSXFileTableToSQLScript(xlsxFile.getName() + ".sql");
					} catch (Exception e) {
						logger.error("Error exporting to sql file " + xlsxFile.getName() + " : " + e.getMessage(), e);
					}
				});
				break;
			}
		}
	}

	private static List<XLSXFile> loadXLSX(XLSXLoader loader, String folderPath) {
		List<XLSXFile> loadedFiles = new ArrayList<>();
		try (Stream<Path> pathStream = Files.walk(Paths.get(folderPath), FileVisitOption.FOLLOW_LINKS)) {
			pathStream.filter((p) -> p.toFile().getAbsolutePath().endsWith("xlsx")).forEach(p -> {
				try {
					loadedFiles.add(loader.load(p.toFile()));
				} catch (XLSXLoaderException e) {
					logger.error("Error loading file " + e.getMessage(), e);
				}
			});
		} catch (final IOException e) {
			logger.error("Error iterating through files " + e.getMessage(), e);
		}
		return loadedFiles;
	}
}
