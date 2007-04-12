package us.k5n.journal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * Overide methods of JTable to customize: no cell editing, alternating
 * background colors for odd/even rows, resize to fit
 * 
 * @version $Id$
 * @author Craig Knudsen, craig@k5n.us
 */
public class ReadOnlyTable extends JTable {
	int[] highlightedRows = null;
	Color lightGray;
	private boolean firstPaint = true;

	public void clearHighlightedRows () {
		this.highlightedRows = null;
	}

	public void setHighlightedRow ( int row ) {
		this.highlightedRows = new int[1];
		highlightedRows[0] = row;
		repaint ();
	}

	public void setHighlightedRows ( int[] rows ) {
		this.highlightedRows = rows;
		repaint ();
	}

	private boolean rowIsHighlighted ( int row ) {
		if ( highlightedRows == null )
			return false;
		for ( int i = 0; i < highlightedRows.length; i++ ) {
			if ( highlightedRows[i] == row )
				return true;
		}
		return false;
	}

	public Component prepareRenderer ( TableCellRenderer renderer, int rowIndex,
	    int vColIndex ) {
		Component c = super.prepareRenderer ( renderer, rowIndex, vColIndex );
		if ( rowIsHighlighted ( rowIndex ) ) {
			c.setBackground ( Color.blue );
			c.setForeground ( Color.white );
			// } else if ( rowIndex % 2 == 0 && !isCellSelected ( rowIndex, vColIndex
			// ) ) {
		} else if ( rowIndex % 2 == 0 ) {
			c.setBackground ( lightGray );
			c.setForeground ( Color.black );
		} else {
			// If not shaded, match the table's background
			c.setBackground ( getBackground () );
			c.setForeground ( Color.black );
		}
		return c;
	}

	public ReadOnlyTable(int rows, int cols) {
		super ( rows, cols );
		lightGray = new Color ( 220, 220, 220 );
	}

	public ReadOnlyTable(TableModel tm) {
		super ( tm );
		lightGray = new Color ( 220, 220, 220 );
	}

	public boolean isCellEditable ( int row, int col ) {
		return false;
	}

	public void paint ( Graphics g ) {
		if ( firstPaint ) {
			// After first paint call, we can get font metric info. So,
			// call autoResize to adjust column widths.
			// autoResize ();
			// doLayout ();
			firstPaint = false;
		}
		super.paint ( g );
	}

	private void autoResize () {
		System.out.println ( "Calculating column widths for report." );
		FontMetrics fm = getGraphics ().getFontMetrics ();
		int[] widths = new int[getColumnCount ()];
		for ( int i = 0; i < getColumnCount (); i++ ) {
			TableColumn tc = this.getColumnModel ().getColumn ( i );
			String label = (String) tc.getHeaderValue ();
			widths[i] = fm.stringWidth ( label ) + 2
			    * getColumnModel ().getColumnMargin () + 15;
		}
		for ( int row = 0; row < getRowCount (); row++ ) {
			for ( int col = 0; col < getColumnCount (); col++ ) {
				String val = this.getValueAt ( row, col ).toString ();
				int width = ( val == null ? 0 : fm.stringWidth ( val )
				    + ( 2 * getColumnModel ().getColumnMargin () ) + 6 );
				if ( width > widths[col] )
					widths[col] = width;
			}
		}

		int totalW = 0;
		for ( int i = 0; i < getColumnCount (); i++ ) {
			totalW += widths[i];
		}
		Dimension d = getPreferredSize ();
		d = new Dimension ( totalW + 50, d.height );
		// setPreferredSize ( d );
		for ( int i = 0; i < getColumnCount (); i++ ) {
			TableColumn tc = getColumnModel ().getColumn ( i );
			tc.setPreferredWidth ( widths[i] );
		}
	}

	// Implement table header tool tips.
	protected JTableHeader createDefaultTableHeader () {
		return new JTableHeader ( columnModel ) {
			public String getToolTipText ( MouseEvent e ) {
				java.awt.Point p = e.getPoint ();
				int index = columnModel.getColumnIndexAtX ( p.x );
				String text = columnModel.getColumn ( index ).getHeaderValue ()
				    .toString ();
				return "Click to sort by " + text;
			}
		};
	}
}
