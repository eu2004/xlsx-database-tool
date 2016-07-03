package ro.eu.xlsxdb;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import ro.eu.xlsxdb.database.XLSXFileTable;
import ro.eu.xlsxdb.database.XLSXFileTableExporter;
import ro.eu.xlsxdb.database.XSLXTableDao;
import ro.eu.xlsxdb.xlsxloader.XLSXFile;
import ro.eu.xlsxdb.xlsxloader.XLSXLoader;
import ro.eu.xlsxdb.xlsxloader.XLSXLoaderException;

public class ApplicationExecutor {
	private final Logger logger;
	private XLSXLoader xlsxLoader;
	private XSLXTableDao xslxTableDao;

	public ApplicationExecutor(Logger logger, XLSXLoader xlsxLoader, XSLXTableDao xslxTableDao) {
		this.logger = logger;
		this.xlsxLoader = xlsxLoader;
		this.xslxTableDao = xslxTableDao;
	}

	public void execute(String folderPath, String action) {
		List<XLSXFile> xslxFiles = loadXLSX(xlsxLoader, folderPath);
		switch (action) {
		case "load": {
			xslxFiles.forEach(xlsxFile -> {
				xslxTableDao.loadXLSXFile(new XLSXFileTable(xlsxFile));
			});
			break;
		}
		case "export": {
			xslxFiles.forEach(xlsxFile -> {
				try {
					new XLSXFileTableExporter(new XLSXFileTable(xlsxFile))
							.exportXLSXFileTableToSQLScript(xlsxFile.getName() + ".sql");
				} catch (Exception e) {
					logger.error("Error exporting to sql file " + xlsxFile.getName() + " : " + e.getMessage(), e);
				}
			});
			break;
		}
		default:
			logger.error(action + " is not valid");
		}
	}

	private List<XLSXFile> loadXLSX(XLSXLoader loader, String folderPath) {
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
