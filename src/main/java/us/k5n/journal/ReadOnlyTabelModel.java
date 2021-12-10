package us.k5n.journal;

import javax.swing.table.AbstractTableModel;

/**
 * Implement a TableModel so we can sort the table by clicking on the column
 * header.
 * 
 * @author Craig Knudsen, craig@kn.us.
 */
public class ReadOnlyTabelModel extends AbstractTableModel {
	private String[] columnNames = null;
	private Object[][] data = null;
	private int rows, cols;

	public ReadOnlyTabelModel(String[] header, int rows, int cols) {
		this.columnNames = header;
		this.data = new Object[rows][cols];
		this.rows = rows;
		this.cols = cols;
	}

	/**
	 * Reset the size. The caller needs to call setValueAt for each row and column
	 * after this call.
	 * 
	 * @param r
	 */
	public void setRowCount ( int r ) {
		this.rows = r;
		this.data = new Object[rows][cols];
		super.fireTableDataChanged ();
	}

	public int getColumnCount () {
		return columnNames.length;
	}

	public int getRowCount () {
		return data.length;
	}

	public String getColumnName ( int col ) {
		return columnNames[col];
	}

	public Object getValueAt ( int row, int col ) {
		return data[row][col];
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for each
	 * cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	public Class getColumnClass ( int c ) {
		Object o = getValueAt ( 0, c );
		if ( o == null )
			return null;
		else
			return o.getClass ();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable ( int row, int col ) {
		return false;
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt ( Object value, int row, int col ) {
		data[row][col] = value;
	}

}
