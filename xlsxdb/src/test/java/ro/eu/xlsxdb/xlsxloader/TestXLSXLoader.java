package ro.eu.xlsxdb.xlsxloader;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ro.eu.xlsxdb.TestApplicationConfiguration;

import java.io.File;

/**
 * Created by emilu on 5/21/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestApplicationConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class TestXLSXLoader {
	private static final Logger logger = Logger.getLogger(TestXLSXLoader.class);

	@Autowired
	private XLSXLoader xlsxLoader;

	@Test
	public void testLoad() throws XLSXLoaderException {
		XLSXFile xlsxFile = xlsxLoader.load(new File("src/test/resources/test.xlsx"));
		Assert.assertTrue(xlsxFile != null);
		Assert.assertTrue(xlsxFile.getColumns() != null);
		Assert.assertTrue(xlsxFile.getColumns().size() == 8);
		for (XLSXColumn column : xlsxFile.getColumns()) {
			Assert.assertTrue(column != null);
			logger.info(column.getName() + " " + column.getType() + " " + column.getIndex());
		}

		Assert.assertEquals(xlsxFile.getRows().size(), 1000);
	}
}
