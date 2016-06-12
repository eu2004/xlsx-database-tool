package ro.eu.xlsxdb.database;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import ro.eu.xlsxdb.xlsxloader.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * Created by emilu on 5/21/2016.
 */
public class XSLXTableDao {
    private static final Logger logger = Logger.getLogger(XSLXTableDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void loadXLSXFile(XLSXFileTable xlsxFileTable) {
        this.dropTableIfExists(xlsxFileTable.getTableName());
        jdbcTemplate.update(SQLQueriesUtils.generateSQLCreateTable(xlsxFileTable));
        loadXLSXDataFile(xlsxFileTable);
    }

    public void dropTableIfExists(String tableName) {
        boolean exists = true;
        try {
            //LIMIT <limit>
            jdbcTemplate.query(String.format("select * from %s LIMIT 1", tableName), new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                }
            });
        }catch (InvalidDataAccessResourceUsageException ex) {
            exists = false;
        }

        if (exists) {
            jdbcTemplate.update(String.format("drop table %s;", tableName));
            logger.info(tableName + " dropped");
        }
    }

    public Iterator<XLSXRow> selectTable(String tableName) {
        final List<XLSXRow> rows = new ArrayList<XLSXRow>();
        jdbcTemplate.query(String.format("select * from %s", tableName), new XSLXRowCallbackHandler(rows));
        return rows.iterator();
    }

    private void loadXLSXDataFile(XLSXFileTable xlsxFileTable) {
        logger.info("Start loading file " + xlsxFileTable.getTableName());
        List<XLSXRow> rows = xlsxFileTable.getRows();
        for(XLSXRow row : rows) {
            insertRow(xlsxFileTable.getTableName(), xlsxFileTable.getColumns(), row);
        }
        logger.info("File " + xlsxFileTable + " loaded.");
    }

    private void insertRow(String name, List<XLSXFileTableColumn> columns, XLSXRow row) {
        jdbcTemplate.update(SQLQueriesUtils.generateSQLInsertRow(name, columns, row));
    }

    private static class XSLXRowCallbackHandler implements RowCallbackHandler {
        private final Map<Integer, XLSXColumn> columnsDefinition = new HashMap<Integer, XLSXColumn>(1);
        private List<XLSXRow> rows;

        public XSLXRowCallbackHandler(List<XLSXRow> rows) {
            this.rows = rows;
        }

        public void processRow(ResultSet resultSet) throws SQLException {
            XLSXRow row = new XLSXRow();
            row.setCells(new ArrayList<XLSXCell>(1));
            loadRow(row, resultSet);
            this.rows.add(row);
        }

        private void loadRow(XLSXRow row, ResultSet resultSet) throws SQLException {
            int columnCount = resultSet.getMetaData().getColumnCount();
            for(int i = 0; i < columnCount; i++) {
                XLSXColumn column = getXLSXColumnDefinition(i + 1, resultSet.getMetaData());
                XLSXCell xlsxCell = new XLSXCell();
                xlsxCell.setValue(getValue(column, resultSet));
                xlsxCell.setColumn(column);
                row.getCells().add(xlsxCell);
            }
        }

        private Object getValue(XLSXColumn column, ResultSet resultSet) throws SQLException {
            switch (column.getType()) {
                case BOOLEAN:
                    return resultSet.getBoolean(column.getIndex() + 1);
                case NUMERIC:
                    return resultSet.getFloat(column.getIndex() + 1);
                case DATE:
                    return resultSet.getTimestamp(column.getIndex() + 1);
                default:
                    return resultSet.getString(column.getIndex() + 1);
            }
        }

        private XLSXColumn getXLSXColumnDefinition(int index, ResultSetMetaData metaData) throws SQLException {
            if (columnsDefinition.get(index) != null) {
                return columnsDefinition.get(index);
            }

            String name = metaData.getColumnName(index);
            int sqlType = metaData.getColumnType(index);
            XLSXColumnType columnType = null;
            switch (sqlType) {
                case Types.BOOLEAN:
                    columnType = XLSXColumnType.BOOLEAN;
                    break;
                case Types.BIGINT:
                case Types.DECIMAL:
                case Types.DOUBLE:
                case Types.INTEGER:
                case Types.FLOAT:
                    columnType = XLSXColumnType.NUMERIC;
                    break;
                case Types.DATE:
                case Types.TIMESTAMP:
                    columnType = XLSXColumnType.DATE;
                    break;
                default:
                    columnType = XLSXColumnType.TEXT;
            }
            XLSXColumn column = new XLSXColumn();
            column.setType(columnType);
            column.setName(name);
            column.setIndex(index - 1);
            columnsDefinition.put(index, column);
            return column;
        }
    }
}