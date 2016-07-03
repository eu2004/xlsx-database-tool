package ro.eu.xlsxdb.database;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ro.eu.xlsxdb.TestApplicationConfiguration;
import ro.eu.xlsxdb.xlsxloader.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by emilu on 5/21/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestApplicationConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class TestXLSXTableDao {
	private static final Logger logger = Logger.getLogger(TestXLSXTableDao.class);

	@Autowired
	private XSLXTableDao xslxTableDao;

	@Autowired
	private XLSXLoader xlsxLoader;

	@Test
	public void testLoad() throws XLSXLoaderException {
		XLSXFile xlsxFile = xlsxLoader.load(new File("src\\test\\resources\\test_3.xlsx"));
		XLSXFileTable xlsxFileTable = new XLSXFileTable(xlsxFile);
		xslxTableDao.dropTableIfExists(xlsxFileTable.getTableName());
		try {
			xslxTableDao.loadXLSXFile(xlsxFileTable);
			Iterator<XLSXRow> iterator = xslxTableDao.selectTable(xlsxFileTable.getTableName());
			StringBuilder stringBuilder = new StringBuilder();
			List<XLSXRow> rows = new ArrayList<>();
			iterator.forEachRemaining(xlsxRow -> {
				xlsxRow.getCells().forEach(xlsxCell -> {
					stringBuilder.append(xlsxCell.getValue()).append(" ");
				});
				logger.info(stringBuilder.toString());
				stringBuilder.delete(0, stringBuilder.length());
				rows.add(xlsxRow);
			});

			Assert.assertEquals(rows.size(), 1000);
		} finally {
			xslxTableDao.dropTableIfExists(xlsxFileTable.getTableName());
		}
	}

	@Test
	public void testColumnType() throws XLSXLoaderException {
		XLSXFile xlsxFile = xlsxLoader.load(new File("src\\test\\resources\\test_4.xlsx"));
		XLSXFileTable xlsxFileTable = new XLSXFileTable(xlsxFile);
		xslxTableDao.dropTableIfExists(xlsxFileTable.getTableName());
		try {
			xslxTableDao.loadXLSXFile(xlsxFileTable);
			Iterator<XLSXRow> iterator = xslxTableDao.selectTable(xlsxFileTable.getTableName());
			StringBuilder stringBuilder = new StringBuilder();
			List<XLSXRow> rows = new ArrayList<>();
			iterator.forEachRemaining(xlsxRow -> {
				xlsxRow.getCells().forEach(xlsxCell -> {
					stringBuilder.append(xlsxCell.getValue()).append(" ");
				});
				logger.info(stringBuilder.toString());
				stringBuilder.delete(0, stringBuilder.length());
				rows.add(xlsxRow);
			});
			rows.forEach(row -> {
				Assert.assertEquals(XLSXColumnType.TEXT, row.getCells().get(0).getColumn().getType());
				Assert.assertEquals(XLSXColumnType.BOOLEAN, row.getCells().get(1).getColumn().getType());
				Assert.assertEquals(XLSXColumnType.NUMERIC, row.getCells().get(2).getColumn().getType());
				Assert.assertEquals(XLSXColumnType.DATE, row.getCells().get(3).getColumn().getType());
			});
			Assert.assertEquals(rows.size(), 3);
		} finally {
			xslxTableDao.dropTableIfExists(xlsxFileTable.getTableName());
		}
	}

	@Test
	public void testDropTableIfExists() throws XLSXLoaderException {
		XLSXFile xlsxFile = xlsxLoader.load(new File("src\\test\\resources\\test_3.xlsx"));
		XLSXFileTable xlsxFileTable = new XLSXFileTable(xlsxFile);
		boolean dropped = xslxTableDao.dropTableIfExists(xlsxFileTable.getTableName());
		Assert.assertFalse(dropped);

		xslxTableDao.loadXLSXFile(xlsxFileTable);
		dropped = xslxTableDao.dropTableIfExists(xlsxFileTable.getTableName());
		Assert.assertTrue(dropped);
	}
}
