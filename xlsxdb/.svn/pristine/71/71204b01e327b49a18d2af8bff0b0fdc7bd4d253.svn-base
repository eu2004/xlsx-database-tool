package ro.eu.xlsxdb.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ro.eu.xlsxdb.TestApplicationConfiguration;
import ro.eu.xlsxdb.xlsxloader.XLSXFile;
import ro.eu.xlsxdb.xlsxloader.XLSXLoader;
import ro.eu.xlsxdb.xlsxloader.XLSXLoaderException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestApplicationConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class TestXLSXFileTableExporter {

	@Autowired
	private XLSXLoader xlsxLoader;

	@Test
	public void testLoad() throws XLSXLoaderException {
		XLSXFile xlsxFile = xlsxLoader.load(Paths.get("src\\test\\resources\\test_3.xlsx").toFile());
		XLSXFileTable xlsxFileTable = new XLSXFileTable(xlsxFile);
		XLSXFileTableExporter exporter = new XLSXFileTableExporter(xlsxFileTable);
		try {
			Path exportedFile = exporter.exportXLSXFileTableToSQLScript("test.sql");
			File exportedFileObj = exportedFile.toFile();
			Assert.assertTrue(exportedFileObj.exists());
			Assert.assertTrue(exportedFileObj.length() > 0);
			Predicate<String> createTablePredicate = new Predicate<String>() {
				@Override
				public boolean test(String line) {
					return line.equals(SQLQueriesUtils.generateSQLCreateTable(xlsxFileTable));
				}
			};
			Assert.assertTrue(Files.lines(exportedFile).anyMatch(createTablePredicate));

			Predicate<String> insertTableRowPredicate = new Predicate<String>() {
				@Override
				public boolean test(String line) {
					return line.equals(SQLQueriesUtils.generateSQLInsertRow(xlsxFileTable.getTableName(),
							xlsxFileTable.getColumns(), xlsxFileTable.getRows().get(0)));
				}
			};
			Assert.assertTrue(Files.lines(exportedFile).anyMatch(insertTableRowPredicate));
		} catch (FileNotFoundException e) {
			Assert.fail("Should not throw exception " + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			Assert.fail("Should not throw exception " + e.getMessage());
		} catch (IOException e) {
			Assert.fail("Should not throw exception " + e.getMessage());
		}
	}
}
