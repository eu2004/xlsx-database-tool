package ro.eu.xlsxdb.database;

import java.io.File;

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
public class TestXLSXFileTable {
	@Autowired
	private XLSXLoader xlsxLoader;

	@Test
	public void testLoad() throws XLSXLoaderException {
		XLSXFile xlsxFile = xlsxLoader.load(new File("src\\test\\resources\\test_3.xlsx"));
		XLSXFileTable xlsxFileTable = new XLSXFileTable(xlsxFile);
		Assert.assertEquals("table_test_3_xlsx", xlsxFileTable.getTableName());
		Assert.assertEquals("col_column1", xlsxFileTable.getColumns().get(0).getName());
		Assert.assertEquals("col_column10", xlsxFileTable.getColumns().get(1).getName());
		Assert.assertEquals("col_column100", xlsxFileTable.getColumns().get(9).getName());
	}
}
