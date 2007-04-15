package us.k5n.journal;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * The ProportionalLayout class implements a LayoutManager where the caller can
 * specify which proportion each component should be allocated.
 * 
 * @author cknudsen
 * 
 */
public class ProportionalLayout implements LayoutManager {
	private int[] proportions;
	private int total; // The total of the proportions
	private int num; // The number in the array

	/**
	 * Constructs a ProportinalLayout instance with the specified horizontal
	 * component proportions
	 * 
	 * @param proportions
	 *          An int array of values indicating horizontal proportions. An array
	 *          of 2,1,1 would give the first component added half the space
	 *          horizontally, the second and the third would each get a quarter.
	 *          More components would not be given any space at all. When there
	 *          are less than the expected number of components the unused values
	 *          in the proportions array will correspond to blank space in the
	 *          layout.
	 */
	public ProportionalLayout(int[] proportions) {
		this.proportions = proportions;
		num = proportions.length;
		for ( int i = 0; i < num; i++ ) {
			int prop = proportions[i];
			total += prop;
		}
	}

	private Dimension layoutSize ( Container parent, boolean minimum ) {
		Dimension dim = new Dimension ( 0, 0 );
		synchronized ( parent.getTreeLock () ) {
			int n = parent.getComponentCount ();
			int cnt = 0;
			for ( int i = 0; i < n; i++ ) {
				Component c = parent.getComponent ( i );
				if ( c.isVisible () ) {
					Dimension d = ( minimum ) ? c.getMinimumSize () : c
					    .getPreferredSize ();
					dim.width += d.width;
					if ( d.height > dim.height )
						dim.height = d.height;
				}
				cnt++;
				if ( cnt == num )
					break;
			}
		}
		Insets insets = parent.getInsets ();
		dim.width += insets.left + insets.right;
		dim.height += insets.top + insets.bottom;
		return dim;
	}

	/**
	 * Lays out the container.
	 */
	public void layoutContainer ( Container parent ) {
		Insets insets = parent.getInsets ();
		synchronized ( parent.getTreeLock () ) {
			int n = parent.getComponentCount ();
			Dimension pd = parent.getSize ();
			// do layout
			int cnt = 0;
			int totalwid = pd.width - insets.left - insets.right;
			int x = insets.left;
			for ( int i = 0; i < n; i++ ) {
				Component c = parent.getComponent ( i );
				int wid = proportions[i] * totalwid / total;
				c.setBounds ( x, insets.top, wid, pd.height - insets.bottom
				    - insets.top );
				x += wid;
				cnt++;
				if ( cnt == num )
					break;
			}
		}
	}

	public Dimension minimumLayoutSize ( Container parent ) {
		return layoutSize ( parent, false );
	}

	public Dimension preferredLayoutSize ( Container parent ) {
		return layoutSize ( parent, false );
	}

	/**
	 * Not used by this class
	 */
	public void addLayoutComponent ( String name, Component comp ) {
	}

	/**
	 * Not used by this class
	 */
	public void removeLayoutComponent ( Component comp ) {
	}

	public String toString () {
		StringBuffer sb = new StringBuffer ();
		sb.append ( getClass ().getName () ).append ( '[' );
		int len = proportions.length;
		for ( int i = 0; i < len; i++ ) {
			sb.append ( 'p' ).append ( i ).append ( '=' ).append ( proportions[i] );
			if ( i != len - 1 )
				sb.append ( ',' );
		}
		sb.append ( ']' );
		return sb.toString ();
	}
}
