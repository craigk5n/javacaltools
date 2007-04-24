package us.k5n.journal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import us.k5n.ical.Categories;
import us.k5n.ical.Constants;
import us.k5n.ical.DataStore;
import us.k5n.ical.Description;
import us.k5n.ical.ICalendarParser;
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
	public static final String VERSION = "0.2.5 (24 Apr 2007)";
	JFrame parent;
	JLabel messageArea;
	Repository dataRepository;
	JTree dateTree;
	DefaultMutableTreeNode dateTreeAllNode;
	ReadOnlyTable journalListTable;
	ReadOnlyTabelModel journalListTableModel;
	JournalViewPanel journalView = null;
	// filteredJournalEntries is the Vector of Journal objects filtered
	// by dates selected by the user. (Not yet filtered by search text.)
	Vector filteredJournalEntries;
	// filteredSearchedJournalEntries is filtered by both date selection
	// and text search.
	Vector filteredSearchedJournalEntries;
	final static String[] journalListTableHeader = { "Date", "Subject" };
	final static String[] monthNames = { "", "January", "February", "March",
	    "April", "May", "June", "July", "August", "September", "October",
	    "November", "December" };
	JButton newButton, editButton, deleteButton;
	JMenuItem exportSelected;
	JTextField searchTextField;
	String searchText = null;
	private static File lastExportDirectory = null;

	class DateFilterTreeNode extends DefaultMutableTreeNode {
		public int year, month, day;
		public String label;

		public DateFilterTreeNode(String l, int y, int m, int d, int count) {
			super ( l );
			this.year = y;
			this.month = m;
			this.day = d;
			this.label = l + " (" + count + ")";
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
		setWindowsLAF ();
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
		JPanel messagePanel = new JPanel ();
		messagePanel.setLayout ( new BorderLayout () );
		messagePanel.setBorder ( BorderFactory.createEmptyBorder ( 2, 4, 2, 4 ) );
		messageArea = new JLabel ( "Welcome to k5njournal..." );
		messagePanel.add ( messageArea, BorderLayout.CENTER );
		contentPane.add ( messagePanel, BorderLayout.SOUTH );

		contentPane.add ( createToolBar (), BorderLayout.NORTH );

		JPanel navArea = createJournalSelectionPanel ();
		// JPanel viewPanel = new JPanel ();
		// viewPanel.setLayout ( new BorderLayout () );
		// JPanel searchPanel = new JPanel ();
		// searchPanel.setLayout ( new FlowLayout () );
		// searchPanel.add ( new JLabel ( "Search: " ) );
		// searchPanel.add ( new JTextField ( 20 ) );
		// viewPanel.add ( searchPanel );
		journalView = new JournalViewPanel ();

		// viewPanel.add ( journalView, BorderLayout.CENTER );
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
				if ( ind >= 0 && filteredSearchedJournalEntries != null
				    && ind < filteredSearchedJournalEntries.size () ) {
					DisplayDate dd = (DisplayDate) journalListTable.getValueAt ( ind, 0 );
					Journal j = (Journal) dd.getUserData ();
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
				if ( ind >= 0 && filteredSearchedJournalEntries != null
				    && ind < filteredSearchedJournalEntries.size () ) {
					Journal j = (Journal) filteredSearchedJournalEntries.elementAt ( ind );
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
		exportSelected.setEnabled ( numSelected >= 1 );
	}

	public void setMessage ( String msg ) {
		this.messageArea.setText ( msg );
	}

	public JMenuBar createMenu () {
		JMenuItem item;

		JMenuBar bar = new JMenuBar ();

		JMenu fileMenu = new JMenu ( "File" );

		JMenu exportMenu = new JMenu ( "Export" );
		// exportMenu.setMnemonic ( 'X' );
		fileMenu.add ( exportMenu );

		item = new JMenuItem ( "All" );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				exportAll ();
			}
		} );
		exportMenu.add ( item );
		item = new JMenuItem ( "Visible" );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				exportVisible ();
			}
		} );
		exportMenu.add ( item );
		item = new JMenuItem ( "Selected" );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				exportSelected ();
			}
		} );
		exportMenu.add ( item );
		exportSelected = item;

		fileMenu.addSeparator ();

		item = new JMenuItem ( "Exit" );
		item.setAccelerator ( KeyStroke.getKeyStroke ( 'Q', Toolkit
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

		/*
		 * commented out because of bug in JDK that causes NullPointerException when
		 * we update the UI L&F. JMenu settingsMenu = new JMenu ( "Settings" );
		 * 
		 * item = new JMenuItem ( "Look & Feel" ); item.addActionListener ( new
		 * ActionListener () { public void actionPerformed ( ActionEvent event ) {
		 * selectLookAndFeel ( parent, parent ); } } ); settingsMenu.add ( item );
		 * bar.add ( settingsMenu );
		 */

		// Add help bar to right end of menubar
		bar.add ( Box.createHorizontalGlue () );

		JMenu helpMenu = new JMenu ( "Help" );

		item = new JMenuItem ( "About..." );
		item.setAccelerator ( KeyStroke.getKeyStroke ( 'A', Toolkit
		    .getDefaultToolkit ().getMenuShortcutKeyMask () ) );
		item.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// TODO: add logo, etc...
				JOptionPane.showMessageDialog ( parent, "k5njournal\nVersion "
				    + VERSION + "\n\nDeveloped by k5n.us\n\n"
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
		dateTreeAllNode = new DefaultMutableTreeNode ( "All" );
		dateTree = new JTree ( dateTreeAllNode );

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

		JPanel searchPanel = new JPanel ();
		searchPanel.setBorder ( BorderFactory.createEmptyBorder ( 4, 4, 4, 4 ) );
		searchPanel.setLayout ( new BorderLayout () );
		searchPanel.add ( new JLabel ( "Search: " ), BorderLayout.WEST );
		JButton searchClear = new JButton ( "Clear" );
		searchPanel.add ( searchClear, BorderLayout.EAST );
		searchClear.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				searchCleared ();
			}
		} );
		searchTextField = new JTextField ();
		searchTextField.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				searchUpdated ();
			}
		} );
		searchPanel.add ( searchTextField, BorderLayout.CENTER );
		journalListPane.add ( searchPanel, BorderLayout.NORTH );

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
				    if ( journalView != null ) {
					    int ind = journalListTable.getSelectedRow ();
					    int numSel = journalListTable.getSelectedRowCount ();
					    updateToolbar ( numSel );
					    if ( numSel == 0 ) {
						    journalView.clear ();
					    } else if ( !event.getValueIsAdjusting () && ind >= 0
					        && filteredSearchedJournalEntries != null
					        && ind < filteredSearchedJournalEntries.size () ) {
						    int[] selRows = journalListTable.getSelectedRows ();
						    // The call below might actually belong in ReadOnlyTable.
						    // However, we would need to add a MouseListener to
						    // ReadOnlyTable
						    // and make sure that one got called before this one.
						    journalListTable.setHighlightedRows ( selRows );
						    if ( selRows != null && selRows.length == 1 ) {
							    DisplayDate dd = (DisplayDate) journalListTable.getValueAt (
							        ind, 0 );
							    Journal journal = (Journal) dd.getUserData ();
							    if ( journal != null )
								    journalView.setJournal ( journal );
						    } else {
							    // more than one selected
							    journalView.clear ();
						    }
					    } else {
						    journalListTable.clearHighlightedRows ();
					    }
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
			// System.out.println ( "Showing entries for " + dateFilter.year + "/"
			// + dateFilter.month );
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

	// Rebuild the Date JTree.
	// TODO: What we should really be doing is updating the JTree so that
	// we can preserve what year nodes were open and what objects were
	// selected.
	void updateDateTree () {
		dateTree.setShowsRootHandles ( true );
		// Remove all old entries
		dateTreeAllNode.removeAllChildren ();
		// Get entries, starting with years
		int[] years = dataRepository.getYears ();
		for ( int i = 0; years != null && i < years.length; i++ ) {
			Vector yearEntries = dataRepository.getEntriesByYear ( years[i] );
			DateFilterTreeNode yearNode = new DateFilterTreeNode ( "" + years[i],
			    years[i], 0, 0, yearEntries == null ? 0 : yearEntries.size () );
			dateTreeAllNode.add ( yearNode );
			int[] months = dataRepository.getMonthsForYear ( years[i] );
			for ( int j = 0; months != null && j < months.length; j++ ) {
				Vector monthEntries = dataRepository.getEntriesByMonth ( years[i],
				    months[j] );
				DateFilterTreeNode monthNode = new DateFilterTreeNode (
				    monthNames[months[j]], years[i], months[j], 0,
				    monthEntries == null ? 0 : monthEntries.size () );
				yearNode.add ( monthNode );
			}
		}
		DefaultTreeModel dtm = (DefaultTreeModel) dateTree.getModel ();
		dtm.nodeStructureChanged ( dateTreeAllNode );
		dateTree.expandRow ( 0 );
		// Select "All" by default
		dateTree.setSelectionRow ( 0 );
		dateTree.repaint ();
		updateToolbar ( 0 );
	}

	/**
	 * User pressed the Enter key in the search text.
	 */
	void searchUpdated () {
		searchText = searchTextField.getText ();
		updateFilteredJournalList ();
	}

	void searchCleared () {
		searchTextField.setText ( "" );
		searchText = null;
		updateFilteredJournalList ();
	}

	// Filter the specified Vector of Journal objects by
	// the searchText using a regular expression.
	private Vector filterSearchText ( Vector entries ) {
		Vector ret;
		Pattern pat;
		Matcher m;

		if ( searchText == null || searchText.trim ().length () == 0 )
			return entries;

		// remove any characters that are not regular expression safe
		StringBuffer sb = new StringBuffer ();
		for ( int i = 0; i < searchText.length (); i++ ) {
			char ch = searchText.charAt ( i );
			if ( ch >= 'a' || ch <= 'Z' || ch >= 'A' || ch <= 'Z' || ch >= '0'
			    || ch <= '9' || ch == ' ' ) {
				sb.append ( ch );
			}
		}
		if ( sb.length () == 0 )
			return entries;

		ret = new Vector ();
		pat = Pattern.compile ( sb.toString (), Pattern.CASE_INSENSITIVE );
		// System.out.println ( "Pattern: " + pat );
		for ( int i = 0; i < entries.size (); i++ ) {
			Journal j = (Journal) entries.elementAt ( i );
			Description d = j.getDescription ();
			boolean matches = false;
			// Search summary, categories, and description
			Summary summary = j.getSummary ();
			if ( summary != null ) {
				m = pat.matcher ( summary.getValue () );
				if ( m.find () ) {
					matches = true;
				}
			}
			if ( !matches ) {
				Categories cats = j.getCategories ();
				if ( cats != null ) {
					m = pat.matcher ( cats.getValue () );
					if ( m.find () ) {
						matches = true;
					}
				}
			}
			if ( !matches ) {
				if ( d != null ) {
					m = pat.matcher ( d.getValue () );
					if ( m.find () ) {
						matches = true;
					}
				}
			}
			if ( matches ) {
				ret.addElement ( j );
			}
		}
		return ret;
	}

	/**
	 * Update the JTable of Journal entries based on the Journal objects in the
	 * filteredJournalEntries Vector.
	 */
	void updateFilteredJournalList () {
		filteredSearchedJournalEntries = filterSearchText ( filteredJournalEntries );
		journalListTableModel
		    .setRowCount ( filteredSearchedJournalEntries == null ? 0
		        : filteredSearchedJournalEntries.size () );
		journalListTable.clearHighlightedRows ();
		for ( int i = 0; filteredSearchedJournalEntries != null
		    && i < filteredSearchedJournalEntries.size (); i++ ) {
			Journal entry = (Journal) filteredSearchedJournalEntries.elementAt ( i );
			if ( entry.getStartDate () != null ) {
				journalListTable.setValueAt ( new DisplayDate ( entry.getStartDate (),
				    entry ), i, 0 );
			} else {
				journalListTable.setValueAt ( new DisplayDate ( null, entry ), i, 0 );
			}
			Summary summary = entry.getSummary ();
			journalListTable.setValueAt (
			    summary == null ? "-" : summary.getValue (), i, 1 );
		}
		this.showStatusMessage ( "" + filteredSearchedJournalEntries.size ()
		    + " entries "
		    + ( searchText == null ? "" : "matched '" + searchText + "'" ) );

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

	void showStatusMessage ( String string ) {
		this.messageArea.setText ( string );
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

	/**
	 * Set the Look and Feel to be Windows.
	 */
	public static void setWindowsLAF () {
		try {
			UIManager
			    .setLookAndFeel ( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
		} catch ( Exception e ) {
			System.err.println ( "Unabled to load Windows UI: " + e.toString () );
		}
	}

	public void selectLookAndFeel ( Component toplevel, Frame dialogParent ) {
		LookAndFeel lafCurrent = UIManager.getLookAndFeel ();
		System.out.println ( "Current L&F: " + lafCurrent );
		UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels ();
		String[] choices = new String[info.length];
		int sel = 0;
		for ( int i = 0; i < info.length; i++ ) {
			System.out.println ( "  " + info[i].toString () );
			choices[i] = info[i].getClassName ();
			if ( info[i].getClassName ().equals ( lafCurrent.getClass ().getName () ) )
				sel = i;
		}
		Object uiSelection = JOptionPane.showInputDialog ( dialogParent,
		    "Select Look and Feel", "Look and Feel",
		    JOptionPane.INFORMATION_MESSAGE, null, choices, choices[sel] );
		UIManager.LookAndFeelInfo selectedLAFInfo = null;
		for ( int i = 0; i < info.length; i++ ) {
			if ( uiSelection.equals ( choices[i] ) )
				selectedLAFInfo = info[i];
		}
		if ( selectedLAFInfo != null ) {
			try {
				System.out.println ( "Changing L&F: " + selectedLAFInfo );
				UIManager.setLookAndFeel ( selectedLAFInfo.getClassName () );
				// SwingUtilities.updateComponentTreeUI ( parent );
				// parent.pack ();
			} catch ( Exception e ) {
				System.err.println ( "Unabled to load L&F: " + e.toString () );
			}
		} else {
			System.err.println ( "No L&F selected" );
		}
	}

	protected void exportAll () {
		export ( "Export All", dataRepository.getAllEntries () );
	}

	protected void exportVisible () {
		export ( "Export Visible", filteredJournalEntries );
	}

	protected void exportSelected () {
		Vector selected = new Vector ();
		int[] sel = journalListTable.getSelectedRows ();
		if ( sel == null || sel.length == 0 ) {
			showError ( "You have not selected any entries" );
			return;
		}
		for ( int i = 0; i < sel.length; i++ ) {
			DisplayDate dd = (DisplayDate) journalListTable.getValueAt ( i, 0 );
			Journal journal = (Journal) dd.getUserData ();
			selected.addElement ( journal );
		}
		export ( "Export Selected", selected );
	}

	private void export ( String title, Vector journalEntries ) {
		JFileChooser fileChooser;
		File outFile = null;

		if ( lastExportDirectory == null )
			fileChooser = new JFileChooser ();
		else
			fileChooser = new JFileChooser ( lastExportDirectory );
		fileChooser.setFileSelectionMode ( JFileChooser.FILES_ONLY );
		fileChooser.setFileFilter ( new ICSFileChooserFilter () );
		fileChooser.setDialogTitle ( "Select Output File for " + title );
		fileChooser.setApproveButtonText ( "Save as ICS File" );
		fileChooser
		    .setApproveButtonToolTipText ( "Export entries to iCalendar file" );
		int ret = fileChooser.showOpenDialog ( this );
		if ( ret == JFileChooser.APPROVE_OPTION ) {
			outFile = fileChooser.getSelectedFile ();
		} else {
			// Cancel
			return;
		}
		// If no file extension provided, use ".ics
		String basename = outFile.getName ();
		if ( basename.indexOf ( '.' ) < 0 ) {
			// No filename extension provided, so add ".csv" to it
			outFile = new File ( outFile.getParent (), basename + ".ics" );
		}
		System.out.println ( "Selected File: " + outFile.toString () );
		lastExportDirectory = outFile.getParentFile ();
		if ( outFile.exists () && !outFile.canWrite () ) {
			JOptionPane.showMessageDialog ( parent,
			    "You do not have the proper\npermissions to write to:\n\n"
			        + outFile.toString () + "\n\nPlease select another file.",
			    "Save Error", JOptionPane.PLAIN_MESSAGE );
			return;
		}
		if ( outFile.exists () ) {
			if ( JOptionPane.showConfirmDialog ( parent,
			    "Overwrite existing file?\n\n" + outFile.toString (),
			    "Overwrite Confirm", JOptionPane.YES_NO_OPTION ) != 0 ) {
				JOptionPane.showMessageDialog ( parent, "Export canceled.",
				    "Export canceled", JOptionPane.PLAIN_MESSAGE );
				return;
			}
		}
		try {
			PrintWriter writer = new PrintWriter ( new FileWriter ( outFile ) );
			// Now write!
			ICalendarParser p = new ICalendarParser ( PARSE_LOOSE );
			DataStore dataStore = p.getDataStoreAt ( 0 );
			for ( int i = 0; i < journalEntries.size (); i++ ) {
				Journal j = (Journal) journalEntries.elementAt ( i );
				dataStore.storeJournal ( j );
			}
			writer.write ( p.toICalendar () );
			writer.close ();
			JOptionPane.showMessageDialog ( parent, "Exported to:\n\n"
			    + outFile.toString (), "Export", JOptionPane.PLAIN_MESSAGE );
		} catch ( IOException e ) {
			JOptionPane.showMessageDialog ( parent,
			    "An error was encountered\nwriting to the file:\n\n"
			        + e.getMessage (), "Save Error", JOptionPane.PLAIN_MESSAGE );
			e.printStackTrace ();
		}
	}

	public void journalAdded ( Journal journal ) {
		this.updateDateTree ();
		handleDateFilterSelection ( 0, null );
	}

	public void journalUpdated ( Journal journal ) {
		this.updateDateTree ();
		handleDateFilterSelection ( 0, null );
	}

	public void journalDeleted ( Journal journal ) {
		this.updateDateTree ();
		handleDateFilterSelection ( 0, null );
	}

	/**
	 * @param args
	 */
	public static void main ( String[] args ) {
		new Main ();
	}

}

/**
 * Create a class to use as a file filter for exporting to ics.
 */
class ICSFileChooserFilter extends javax.swing.filechooser.FileFilter {
	public boolean accept ( File f ) {
		if ( f.isDirectory () )
			return true;
		String name = f.getName ();
		if ( name.toLowerCase ().endsWith ( ".ics" ) )
			return true;
		return false;
	}

	public String getDescription () {
		return "*.ics (iCalendar Files)";
	}
}
