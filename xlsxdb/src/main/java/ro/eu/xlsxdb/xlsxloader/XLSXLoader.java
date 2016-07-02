package ro.eu.xlsxdb.xlsxloader;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by emilu on 5/21/2016.
 */
public class XLSXLoader {
    private static final Logger logger = Logger.getLogger(XLSXLoader.class);

    public XLSXFile load(File xlsxFile) throws XLSXLoaderException {
    	if (xlsxFile == null || !xlsxFile.exists() || !xlsxFile.isFile()) {
            throw new XLSXLoaderException(String.format("%s is not a file", xlsxFile));
        }
    	try {
			return load(xlsxFile.getName(), new BufferedInputStream(Files.newInputStream(Paths.get(xlsxFile.getAbsolutePath()))));
		} catch (IOException e) {
			throw new XLSXLoaderException(e);
		}
    }
    
    /**
     * Loads a xlsx file from an input stream. After the loading is finished, the stream is close.
     * 
     * @param xlsxFileStream the stream
     * @return an XLSXFile object
     * @throws XLSXLoaderException if any error occurs during loading
     */
    public XLSXFile load(String xlsxFileName, InputStream xlsxFileStream) throws XLSXLoaderException {
        try (InputStream inputStream = xlsxFileStream){
            // Finds the workbook instance for XLSX file
            XSSFWorkbook myWorkBook = new XSSFWorkbook(inputStream);

            // Return first sheet from the XLSX workbook
            XSSFSheet mySheet = myWorkBook.getSheetAt(0);

            // Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = mySheet.iterator();

            //load table header
            Map<Integer, XLSXColumn> columns = loadColumns(rowIterator.next());
            List<XLSXRow> rows = new ArrayList<XLSXRow>();
            // Traversing over each row of XLSX file
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                // For each row, iterate through each columns
                loadRow(columns, rows, row.cellIterator());
            }
            myWorkBook.close();
            XLSXFile xlsxResultFile = new XLSXFile();
            xlsxResultFile.setName(xlsxFileName);
            xlsxResultFile.setColumns(new ArrayList<XLSXColumn>(columns.values()));
            xlsxResultFile.setRows(rows);
            return xlsxResultFile;
        }catch (Exception ex) {
            throw new XLSXLoaderException(ex);
        } 
    }

    private void loadRow(final Map<Integer, XLSXColumn> columns, final List<XLSXRow> rows, final Iterator<Cell> cellIterator) {
        XLSXRow row = new XLSXRow();
        row.setCells(new ArrayList<XLSXCell>());
        rows.add(row);
        cellIterator.forEachRemaining(cell -> {
        	trySetColumnType(columns, cell);
            try {
				setRow(columns, row, cell);
			} catch (XLSXLoaderException e) {
				logger.error("Error processing cell " + e.getMessage(), e);
			}
        });
    }

    private void setRow(Map<Integer, XLSXColumn> columns, XLSXRow row, Cell cell) throws XLSXLoaderException {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                row.getCells().add(createCell(columns, cell, cell.getStringCellValue()));
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    row.getCells().add(createCell(columns, cell, cell.getDateCellValue()));
                } else {
                    row.getCells().add(createCell(columns, cell, cell.getNumericCellValue()));
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                row.getCells().add(createCell(columns, cell, cell.getBooleanCellValue()));
                break;
            default :
                logger.warn("No type found for " + cell.getColumnIndex() + " " + cell.getRowIndex() + " cell");
        }
    }

    private XLSXCell createCell(Map<Integer, XLSXColumn> columns, Cell cell, String stringCellValue) throws XLSXLoaderException {
        if (!columns.get(cell.getColumnIndex()).getType().equals(XLSXColumnType.TEXT)) {
            throw new XLSXLoaderException(String.format("%s cell has a different type %s than %s",
                    "[" + cell.getRowIndex() + " " + cell.getColumnIndex() + "]", "TEXT", columns.get(cell.getColumnIndex()).getType()));
        }
        XLSXCell xlsxCell = new XLSXCell();
        xlsxCell.setColumn(columns.get(cell.getColumnIndex()));
        xlsxCell.setValue(stringCellValue);
        return xlsxCell;
    }

    private XLSXCell createCell(Map<Integer, XLSXColumn> columns, Cell cell, Double numericCellValue) throws XLSXLoaderException {
        if (!columns.get(cell.getColumnIndex()).getType().equals(XLSXColumnType.NUMERIC)) {
            throw new XLSXLoaderException(String.format("%s cell has a different type %s than %s",
                    "[" + cell.getRowIndex() + " " + cell.getColumnIndex() + "]", "NUMERIC", columns.get(cell.getColumnIndex()).getType()));
        }
        XLSXCell xlsxCell = new XLSXCell();
        xlsxCell.setColumn(columns.get(cell.getColumnIndex()));
        xlsxCell.setValue(numericCellValue);
        return xlsxCell;
    }

    private XLSXCell createCell(Map<Integer, XLSXColumn> columns, Cell cell, Boolean booleanCellValue) throws XLSXLoaderException {
        if (!columns.get(cell.getColumnIndex()).getType().equals(XLSXColumnType.BOOLEAN)) {
            throw new XLSXLoaderException(String.format("%s cell has a different type %s than %s",
                    "[" + cell.getRowIndex() + " " + cell.getColumnIndex() + "]", "BOOLEAN", columns.get(cell.getColumnIndex()).getType()));
        }

        XLSXCell xlsxCell = new XLSXCell();
        xlsxCell.setColumn(columns.get(cell.getColumnIndex()));
        xlsxCell.setValue(booleanCellValue);
        return xlsxCell;
    }

    private XLSXCell createCell(Map<Integer, XLSXColumn> columns, Cell cell, Date dateCellValue) throws XLSXLoaderException {
        if (!columns.get(cell.getColumnIndex()).getType().equals(XLSXColumnType.DATE)) {
            throw new XLSXLoaderException(String.format("%s cell has a different type %s than %s",
                    "[" + cell.getRowIndex() + " " + cell.getColumnIndex() + "]", "DATE", columns.get(cell.getColumnIndex()).getType()));
        }

        XLSXCell xlsxCell = new XLSXCell();
        xlsxCell.setColumn(columns.get(cell.getColumnIndex()));
        xlsxCell.setValue(dateCellValue);
        return xlsxCell;
    }

    private void trySetColumnType(Map<Integer, XLSXColumn> columns, Cell cell) {
        //type already set
        if (!columns.get(cell.getColumnIndex()).getType().equals(XLSXColumnType.UNKNOWN)) {
            return;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                columns.get(cell.getColumnIndex()).setType(XLSXColumnType.TEXT);
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    columns.get(cell.getColumnIndex()).setType(XLSXColumnType.DATE);
                }else {
                    columns.get(cell.getColumnIndex()).setType(XLSXColumnType.NUMERIC);
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                columns.get(cell.getColumnIndex()).setType(XLSXColumnType.BOOLEAN);
                break;
            default :
        }
    }

    private Map<Integer, XLSXColumn> loadColumns(Row firstRow) {
        Map<Integer, XLSXColumn> columns = new LinkedHashMap<Integer, XLSXColumn>(1);
        Iterator<Cell> cellIterator = firstRow.cellIterator();
        while(cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (cell.getStringCellValue() == null || cell.getStringCellValue().trim().length() == 0) {
                continue;
            }
            XLSXColumn column = new XLSXColumn();
            column.setIndex(cell.getColumnIndex());
            column.setName(cell.getStringCellValue());
            //set default column type
            column.setType(XLSXColumnType.UNKNOWN);
            columns.put(cell.getColumnIndex(), column);
        }
        return columns;
    }
}
