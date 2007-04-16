package us.k5n.journal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import us.k5n.ical.Categories;
import us.k5n.ical.Date;
import us.k5n.ical.Description;
import us.k5n.ical.Journal;
import us.k5n.ical.Sequence;
import us.k5n.ical.Summary;

/**
 * Create a Journal entry edit window.
 * 
 * @author Craig Knudsen, craig@k5n.us
 * @version $Id$
 */
public class EditWindow extends JDialog {
	Repository repo;
	Journal journal;
	Sequence seq = null;
	JFrame parent;
	JTextField subject;
	JTextField categories;
	JLabel startDate;
	JTextArea description;

	public EditWindow(JFrame parent, Dimension preferredSize, Repository repo,
	    Journal journal) {
		super ( parent );
		super.setSize ( preferredSize );
		// TODO: don't make this modal once we add code to check
		// things like deleting this entry in the main window, etc.
		// super.setModal ( true );
		setDefaultCloseOperation ( JDialog.DISPOSE_ON_CLOSE );

		this.parent = parent;
		this.repo = repo;
		this.journal = journal;

		if ( this.journal == null ) {
			this.journal = new Journal ( "", "", Date.getCurrentDateTime ( "DTSTART" ) );
		} else {
			// Create an updated sequence number for use only if we save
			// (So don't put it in the original Journal object yet)
			if ( this.journal.getSequence () == null )
				seq = new Sequence ( 1 );
			else
				seq = new Sequence ( this.journal.getSequence ().getNum () + 1 );
		}
		// Make sure there is a Summary and Description
		if ( this.journal.getSummary () == null )
			this.journal.setSummary ( new Summary () );
		if ( this.journal.getDescription () == null )
			this.journal.setDescription ( new Description () );
		if ( this.journal.getCategories () == null )
			this.journal.setCategories ( new Categories () );

		System.out.println ( "Is date only: "
		    + ( this.journal.getStartDate ().isDateOnly () ? "true" : "false" ) );

		createWindow ();
		setVisible ( true );
	}

	private void createWindow () {
		setLayout ( new BorderLayout () );

		JPanel buttonPanel = new JPanel ();
		buttonPanel.setLayout ( new FlowLayout () );
		JButton saveButton = new JButton ( "Save" );
		saveButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				// Save (write file)
				save ();
			}
		} );
		buttonPanel.add ( saveButton );
		JButton closeButton = new JButton ( "Close" );
		closeButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				close ();
			}
		} );
		buttonPanel.add ( closeButton );
		add ( buttonPanel, BorderLayout.SOUTH );

		JPanel allButButtons = new JPanel ();
		allButButtons.setLayout ( new BorderLayout () );
		allButButtons.setBorder ( BorderFactory.createEmptyBorder ( 5, 5, 5, 5 ) );

		JPanel upperPanel = new JPanel ();
		upperPanel.setBorder ( BorderFactory.createEtchedBorder () );
		GridLayout grid = new GridLayout ( 3, 1 );
		grid.setHgap ( 15 );
		grid.setVgap ( 5 );
		upperPanel.setLayout ( grid );
		int[] proportions = { 20, 80 };

		JPanel subjectPanel = new JPanel ();
		subjectPanel.setLayout ( new ProportionalLayout ( proportions,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		JLabel prompt = new JLabel ( "Subject: " );
		prompt.setHorizontalAlignment ( SwingConstants.RIGHT );
		subjectPanel.add ( prompt );
		subject = new JTextField ();
		if ( journal != null && journal.getSummary () != null )
			subject.setText ( journal.getSummary ().getValue () );
		subjectPanel.add ( subject );
		upperPanel.add ( subjectPanel );

		JPanel datePanel = new JPanel ();
		datePanel.setLayout ( new ProportionalLayout ( proportions,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		prompt = new JLabel ( "Date: " );
		prompt.setHorizontalAlignment ( SwingConstants.RIGHT );
		datePanel.add ( prompt );
		JPanel subDatePanel = new JPanel ();
		FlowLayout flow = new FlowLayout ();
		flow.setAlignment ( FlowLayout.LEFT );
		subDatePanel.setLayout ( flow );
		startDate = new JLabel ();
		DisplayDate d = new DisplayDate ( journal.getStartDate () );
		startDate.setText ( d.toString () );
		subDatePanel.add ( startDate );
		JButton dateSel = new JButton ( "..." );
		dateSel.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				Date newDate = DateTimeSelectionDialog.showDateTimeSelectionDialog (
				    parent, journal.getStartDate () );
				if ( newDate != null ) {
					journal.setStartDate ( newDate );
					DisplayDate d = new DisplayDate ( journal.getStartDate () );
					startDate.setText ( d.toString () );
				}
				// update screen
				/*
				 * DateTimeSelectionDialog dts = new DateTimeSelectionDialog ( parent,
				 * journal.getStartDate () ); System.out.println ( "About to add
				 * listner"); dts.addListener ( new DateTimeSelectionListener () {
				 * public void dateSelected ( Date selectedDate ) { journal.setStartDate (
				 * selectedDate ); } } ); System.out.println ( "listener added!");
				 */
			}
		} );
		dateSel.setMargin ( new Insets ( 0, 5, 0, 5 ) );
		subDatePanel.add ( dateSel );
		datePanel.add ( subDatePanel );
		upperPanel.add ( datePanel );

		JPanel catPanel = new JPanel ();
		catPanel.setLayout ( new ProportionalLayout ( proportions,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		prompt = new JLabel ( "Categories: " );
		prompt.setHorizontalAlignment ( SwingConstants.RIGHT );
		catPanel.add ( prompt );
		categories = new JTextField ();
		if ( journal != null && journal.getCategories () != null )
			categories.setText ( journal.getCategories ().getValue () );
		catPanel.add ( categories );
		upperPanel.add ( catPanel );

		allButButtons.add ( upperPanel, BorderLayout.NORTH );

		// TODO: eventually add some edit buttons/icons here when
		// we support more than plain text.
		JPanel descrPanel = new JPanel ();
		descrPanel.setLayout ( new BorderLayout () );
		description = new JTextArea ();
		description.setLineWrap ( true );
		if ( journal != null && journal.getDescription () != null )
			description.setText ( journal.getDescription ().getValue () );
		JScrollPane scrollPane = new JScrollPane ( description );
		descrPanel.add ( scrollPane, BorderLayout.CENTER );
		allButButtons.add ( descrPanel, BorderLayout.CENTER );

		add ( allButButtons, BorderLayout.CENTER );
	}

	void save () {
		// Note: LAST-MODIFIED gets updated by call to saveJournal
		if ( seq != null ) {
			journal.setSequence ( seq );
			seq = null;
		}
		try {
			this.journal.getDescription ().setValue ( description.getText () );
			this.journal.getSummary ().setValue ( subject.getText ().trim () );
			this.journal.getCategories ().setValue ( categories.getText ().trim () );
			repo.saveJournal ( this.journal );
		} catch ( IOException e2 ) {
			// TODO: add error handler that pops up a window here
			e2.printStackTrace ();
		}
		System.out.println ( "Is date only: "
		    + ( this.journal.getStartDate ().isDateOnly () ? "true" : "false" ) );
		// TODO: update main window
		this.dispose ();
	}

	void chooseDate () {
		DateTimeSelectionDialog dts = new DateTimeSelectionDialog ( parent, journal
		    .getStartDate () );
	}

	void close () {
		// TODO: check for unsaved changes
		this.dispose ();
	}
}
