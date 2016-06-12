package ro.eu.xlsxdb.xlsxloader;

/**
 * Created by emilu on 5/21/2016.
 */
public class XLSXCell {
    private XLSXColumn column;
    private Object value;

    public XLSXColumn getColumn() {
        return column;
    }

    public void setColumn(XLSXColumn column) {
        this.column = column;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
