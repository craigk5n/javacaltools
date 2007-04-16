package us.k5n.journal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import us.k5n.ical.Constants;
import us.k5n.ical.Journal;
import us.k5n.ical.Summary;

/**
 * Main class for k5njournal application.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 * 
 */
public class Main extends JFrame implements Constants, RepositoryChangeListener {
	public static final String DEFAULT_DIR_NAME = "k5njournal";
	JFrame parent;
	JLabel messageArea;
	Repository dataRepository;
	JTree dateTree;
	DefaultMutableTreeNode dateTreeTopNode;
	ReadOnlyTable journalListTable;
	ReadOnlyTabelModel journalListTableModel;
	JournalViewPanel journalView;
	Vector filteredJournalEntries;
	final static String[] journalListTableHeader = { "Date", "Subject" };
	final static String[] monthNames = { "", "January", "February", "March",
	    "April", "May", "June", "July", "August", "September", "October",
	    "November", "December" };
	JButton newButton, editButton, deleteButton;

	class DateFilterTreeNode extends DefaultMutableTreeNode {
		public int year, month, day;
		public String label;

		public DateFilterTreeNode(String l, int y, int m, int d) {
			super ( l );
			this.year = y;
			this.month = m;
			this.day = d;
			this.label = l;
		}

		public String toString () {
			return label;
		}
	}

	public Main() {
		this ( 600, 600 );
	}

	public Main(int w, int h) {
		super ( "k5njournal" );
		this.parent = this;
		// TODO: save user's preferred size on exit and set here
		setSize ( w, h );

		setDefaultCloseOperation ( EXIT_ON_CLOSE );
		Container contentPane = getContentPane ();

		// Load data
		dataRepository = new Repository ( getDataDirectory (), false );
		// Ask to be notified when the repository changes (user adds/edits
		// an entry)
		dataRepository.addChangeListener ( this );

		// Create a menu bar
		setJMenuBar ( createMenu () );

		contentPane.setLayout ( new BorderLayout () );

		// Add message/status bar at bottom
		messageArea = new JLabel ( "Welcome to k5njournal..." );
		contentPane.add ( messageArea, BorderLayout.SOUTH );

		contentPane.add ( createToolBar (), BorderLayout.NORTH );

		JPanel navArea = createJournalSelectionPanel ();
		journalView = new JournalViewPanel ();
		JSplitPane splitPane = new JSplitPane ( JSplitPane.VERTICAL_SPLIT, navArea,
		    journalView );
		splitPane.setOneTouchExpandable ( true );
		splitPane.setResizeWeight ( 0.5 );
		// TODO: get this value from last session
		splitPane.setDividerLocation ( 200 );
		contentPane.add ( splitPane, BorderLayout.CENTER );

		// Populate Date JTree
		updateDateTree ();
		handleDateFilterSelection ( 0, null );
		// filteredJournalEntries = dataRepository.getAllEntries ();
		// updateFilteredJournalList ();

		this.setVisible ( true );
	}

	JToolBar createToolBar () {
		JToolBar toolbar = new JToolBar ();
		newButton = makeNavigationButton ( null, "new", "Add new Journal entry",
		    "New..." );
		newButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				new EditWindow ( parent, new Dimension ( 500, 500 ), dataRepository,
				    null );
			}
		} );
		toolbar.add ( newButton );

		editButton = makeNavigationButton ( null, "edit", "Edit Journal entry",
		    "Edit..." );
		toolbar.add ( editButton );
		editButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// Get selected item and open edit window
				int ind = journalListTable.getSelectedRow ();
				if ( ind >= 0 && ind < filteredJournalEntries.size () ) {
					Journal j = (Journal) filteredJournalEntries.elementAt ( ind );
					new EditWindow ( parent, new Dimension ( 500, 500 ), dataRepository,
					    j );
				}
			}
		} );

		deleteButton = makeNavigationButton ( null, "delete",
		    "deleteButton Journal entry", "Delete" );
		toolbar.add ( deleteButton );
		deleteButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// Get selected item and open edit window
				int ind = journalListTable.getSelectedRow ();
				if ( ind >= 0 && ind < filteredJournalEntries.size () ) {
					Journal j = (Journal) filteredJournalEntries.elementAt ( ind );
					if ( JOptionPane.showConfirmDialog ( parent,
					    "Are you sure you want\nto delete this entry?", "Confirm Delete",
					    JOptionPane.YES_NO_OPTION ) == 0 ) {
						try {
							dataRepository.deleteJournal ( j );
						} catch ( IOException e1 ) {
							showError ( "Error deleting." );
							e1.printStackTrace ();
						}
					}
				} else {
					System.err.println ( "Index out of range: " + ind );
				}
			}
		} );

		return toolbar;
	}

	void updateToolbar ( int numSelected ) {
		editButton.setEnabled ( numSelected == 1 );
		deleteButton.setEnabled ( numSelected == 1 );
	}

	public void setMessage ( String msg ) {
		this.messageArea.setText ( msg );
	}

	public JMenuBar createMenu () {
		JMenuItem item;

		JMenuBar bar = new JMenuBar ();

		JMenu fileMenu = new JMenu ( "File" );

		item = new JMenuItem ( "Exit" );
		item.setAccelerator ( KeyStroke.getKeyStroke ( 'X', Toolkit
		    .getDefaultToolkit ().getMenuShortcutKeyMask () ) );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// TODO: check for unsaved changes
				// TODO: save current size of main window for use next time
				System.exit ( 0 );
			}
		} );

		fileMenu.add ( item );

		bar.add ( fileMenu );

		// Add help bar to right end of menubar
		bar.add ( Box.createHorizontalGlue () );

		JMenu helpMenu = new JMenu ( "Help" );

		item = new JMenuItem ( "About..." );
		item.setAccelerator ( KeyStroke.getKeyStroke ( 'A', Toolkit
		    .getDefaultToolkit ().getMenuShortcutKeyMask () ) );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// TODO: add logo, etc...
				JOptionPane.showMessageDialog ( parent,
				    "k5njournal Version 0.1\n\nDeveloped by k5n.us\n\n"
				        + "Go to www.k5n.us for more info." );
			}
		} );
		helpMenu.add ( item );

		bar.add ( helpMenu );

		return bar;
	}

	/**
	 * Create the file selection area on the top side of the window. This will
	 * include a split pane where the left will allow navigation and selection of
	 * dates and the right will allow the selection of a specific entry.
	 * 
	 * @return
	 */
	protected JPanel createJournalSelectionPanel () {
		JPanel topPanel = new JPanel ();
		topPanel.setLayout ( new BorderLayout () );

		JTabbedPane tabbedPane = new JTabbedPane ();

		JPanel byDate = new JPanel ();
		byDate.setLayout ( new BorderLayout () );
		tabbedPane.addTab ( "Date", byDate );
		dateTreeTopNode = new DefaultMutableTreeNode ( "All" );
		dateTree = new JTree ( dateTreeTopNode );

		MouseListener ml = new MouseAdapter () {
			public void mousePressed ( MouseEvent e ) {
				int selRow = dateTree.getRowForLocation ( e.getX (), e.getY () );
				TreePath selPath = dateTree.getPathForLocation ( e.getX (), e.getY () );
				if ( selRow != -1 ) {
					if ( e.getClickCount () == 1 ) {
						handleDateFilterSelection ( selRow, selPath );
					} else if ( e.getClickCount () == 2 ) {
						// Do something for double-click???
					}
				}
			}
		};
		dateTree.addMouseListener ( ml );

		JScrollPane scrollPane = new JScrollPane ( dateTree );
		byDate.add ( scrollPane, BorderLayout.CENTER );

		// TODO: add category tab filter
		// JPanel byCategory = new JPanel ();
		// tabbedPane.addTab ( "Category", byCategory );

		JPanel journalListPane = new JPanel ();
		journalListPane.setLayout ( new BorderLayout () );

		journalListTableModel = new ReadOnlyTabelModel ( journalListTableHeader, 0,
		    2 );
		TableSorter sorter = new TableSorter ( journalListTableModel );
		journalListTable = new ReadOnlyTable ( sorter );
		sorter.setTableHeader ( journalListTable.getTableHeader () );
		// journalListTable.setAutoResizeMode ( JTable.AUTO_RESIZE_OFF );
		journalListTable.setRowSelectionAllowed ( true );

		// Add selection listener to table
		journalListTable.getSelectionModel ().addListSelectionListener (
		    new ListSelectionListener () {
			    public void valueChanged ( ListSelectionEvent event ) {
				    int ind = journalListTable.getSelectedRow ();
				    int numSel = journalListTable.getSelectedRowCount ();
				    updateToolbar ( numSel );
				    if ( numSel == 0 ) {
					    journalView.clear ();
				    } else if ( !event.getValueIsAdjusting () && ind >= 0
				        && ind < filteredJournalEntries.size () ) {
					    int[] selRows = journalListTable.getSelectedRows ();
					    // The call below might actually belong in ReadOnlyTable.
					    // However, we would need to add a MouseListener to ReadOnlyTable
					    // and make sure that one got called before this one.
					    journalListTable.setHighlightedRows ( selRows );
					    if ( selRows != null && selRows.length == 1 ) {
						    Journal journal = (Journal) filteredJournalEntries
						        .elementAt ( ind );
						    journalView.setJournal ( journal );
					    } else {
						    // more than one selected
						    journalView.clear ();
					    }
				    } else {
					    journalListTable.clearHighlightedRows ();
				    }
			    }
		    } );

		JScrollPane journalListTableScroll = new JScrollPane ( journalListTable );
		journalListPane.add ( journalListTableScroll, BorderLayout.CENTER );

		JSplitPane splitPane = new JSplitPane ( JSplitPane.HORIZONTAL_SPLIT,
		    tabbedPane, journalListPane );
		splitPane.setOneTouchExpandable ( true );
		// splitPane.setResizeWeight ( 0.5 );
		splitPane.setDividerLocation ( 250 );

		topPanel.add ( splitPane, BorderLayout.CENTER );

		return topPanel;
	}

	void handleDateFilterSelection ( int row, TreePath path ) {
		int year = -1;
		int month = -1;
		if ( path == null || path.getPathCount () < 2 ) {
			// "All"
		} else {
			DateFilterTreeNode dateFilter = (DateFilterTreeNode) path
			    .getLastPathComponent ();
			//System.out.println ( "Showing entries for " + dateFilter.year + "/"
			//    + dateFilter.month );
			year = dateFilter.year;
			if ( dateFilter.month > 0 )
				month = dateFilter.month;
		}
		if ( year < 0 ) {
			filteredJournalEntries = dataRepository.getAllEntries ();
		} else if ( month < 0 ) {
			filteredJournalEntries = dataRepository.getEntriesByYear ( year );
		} else {
			filteredJournalEntries = dataRepository.getEntriesByMonth ( year, month );
		}
		this.updateFilteredJournalList ();
	}

	void updateDateTree () {
		// Remove all old entries
		dateTreeTopNode.removeAllChildren ();
		// Get entries, starting with years
		int[] years = dataRepository.getYears ();
		for ( int i = 0; years != null && i < years.length; i++ ) {
			DateFilterTreeNode yearNode = new DateFilterTreeNode ( "" + years[i],
			    years[i], 0, 0 );
			dateTreeTopNode.add ( yearNode );
			int[] months = dataRepository.getMonthsForYear ( years[i] );
			for ( int j = 0; months != null && j < months.length; j++ ) {
				DateFilterTreeNode monthNode = new DateFilterTreeNode (
				    monthNames[months[j]], years[i], months[j], 0 );
				yearNode.add ( monthNode );
			}
		}
		dateTree.expandRow ( 0 );
		// Select "All" by default
		dateTree.setSelectionRow ( 0 );
		updateToolbar ( 0 );
	}

	/**
	 * Update the JTable of Journal entries based on the Journal objects in the
	 * filteredJournalEntries Vector.
	 */
	void updateFilteredJournalList () {
		journalListTableModel.setRowCount ( filteredJournalEntries.size () );
		journalListTable.clearHighlightedRows ();
		for ( int i = 0; i < filteredJournalEntries.size (); i++ ) {
			Journal entry = (Journal) filteredJournalEntries.elementAt ( i );
			journalListTable.setValueAt ( new DisplayDate ( entry.getStartDate () ),
			    i, 0 );
			Summary summary = entry.getSummary ();
			journalListTable.setValueAt (
			    summary == null ? "-" : summary.getValue (), i, 1 );
		}
		//System.out.println ( "Displaying " + filteredJournalEntries.size ()
		//    + " entries" );

		journalListTable.repaint ();
	}

	/**
	 * Get the data directory that data files for this application will be stored
	 * in.
	 * 
	 * @return
	 */
	// TODO: allow user preferences to override this setting
	File getDataDirectory () {
		String s = (String) System.getProperty ( "user.home" );
		if ( s == null ) {
			System.err.println ( "Could not find user.home setting." );
			System.err.println ( "Using current directory instead." );
			s = ".";
		}
		File f = new File ( s );
		if ( f == null )
			fatalError ( "Invalid user.home value '" + s + "'" );
		if ( !f.exists () )
			fatalError ( "Home directory '" + f + "' does not exist." );
		if ( !f.isDirectory () )
			fatalError ( "Home directory '" + f + "'is not a directory" );
		// Use the home directory as the base. Data files will
		// be stored in a subdirectory.
		File dir = new File ( f, DEFAULT_DIR_NAME );
		if ( !dir.exists () ) {
			if ( !dir.mkdirs () )
				fatalError ( "Unable to create data directory: " + dir );
			showMessage ( "The following directory was created\n"
			    + "to store data files:\n\n" + dir );
		}
		if ( !dir.isDirectory () )
			fatalError ( "Not a directory: " + dir );
		return dir;
	}

	void showMessage ( String message ) {
		JOptionPane.showMessageDialog ( parent, message, "Notice",
		    JOptionPane.INFORMATION_MESSAGE );
	}

	void showError ( String message ) {
		System.err.println ( "Error: " + message );
		JOptionPane
		    .showMessageDialog ( parent, message, "Error", JOptionPane.ERROR );
	}

	void fatalError ( String message ) {
		System.err.println ( "Fatal error: " + message );
		JOptionPane.showMessageDialog ( parent, message, "Fatal Error",
		    JOptionPane.ERROR );
		System.exit ( 1 );
	}

	protected JButton makeNavigationButton ( String imageName,
	    String actionCommand, String toolTipText, String altText ) {
		JButton button;

		// Look for the image.
		String imgLocation = "images/" + imageName;
		URL imageURL = this.getClass ().getResource ( imgLocation );

		if ( imageURL != null ) { // image found
			button = new JButton ();
			button.setIcon ( new ImageIcon ( imageURL, altText ) );
		} else {
			// no image found
			button = new JButton ( altText );
			if ( imageName != null )
				System.err.println ( "Resource not found: " + imgLocation );
		}

		button.setActionCommand ( actionCommand );
		button.setToolTipText ( toolTipText );

		return button;
	}

	public void journalAdded ( Journal journal ) {
		handleDateFilterSelection ( 0, null );
	}

	public void journalUpdated ( Journal journal ) {
		handleDateFilterSelection ( 0, null );
	}

	public void journalDeleted ( Journal journal ) {
		handleDateFilterSelection ( 0, null );
	}

	/**
	 * @param args
	 */
	public static void main ( String[] args ) {
		new Main ();
	}

}
