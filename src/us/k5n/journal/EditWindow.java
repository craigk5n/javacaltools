package us.k5n.journal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import javax.swing.SwingConstants;

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
	JTextArea subject;
	JLabel categories;
	JLabel startDate;
	JTextArea description;

	public EditWindow(JFrame parent, Dimension preferredSize, Repository repo,
	    Journal journal) {
		super ( parent );
		super.setSize ( preferredSize );
		// TODO: don't make this modal once we add code to check
		// things like deleting this entry in the main window, etc.
		super.setModal ( true );
		setDefaultCloseOperation ( JDialog.DISPOSE_ON_CLOSE );

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
			// Make sure there is a Summary and Description
			if ( journal.getSummary () == null )
				journal.setSummary ( new Summary () );
			if ( journal.getDescription () == null )
				journal.setDescription ( new Description () );
		}
		
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
		subjectPanel.setLayout ( new ProportionalLayout ( proportions ) );
		JLabel prompt = new JLabel ( "Subject: " );
		prompt.setHorizontalAlignment ( SwingConstants.RIGHT );
		subjectPanel.add ( prompt );
		subject = new JTextArea ();
		if ( journal != null && journal.getSummary () != null )
			subject.setText ( journal.getSummary ().getValue () );
		subjectPanel.add ( subject );
		upperPanel.add ( subjectPanel );

		JPanel datePanel = new JPanel ();
		datePanel.setLayout ( new ProportionalLayout ( proportions ) );
		prompt = new JLabel ( "Date: " );
		prompt.setHorizontalAlignment ( SwingConstants.RIGHT );
		datePanel.add ( prompt );
		startDate = new JLabel ();
		DisplayDate d = new DisplayDate ( journal.getStartDate () );
		startDate.setText ( d.toString () );
		datePanel.add ( startDate );
		upperPanel.add ( datePanel );

		JPanel catPanel = new JPanel ();
		catPanel.setLayout ( new ProportionalLayout ( proportions ) );
		prompt = new JLabel ( "Categories: " );
		prompt.setHorizontalAlignment ( SwingConstants.RIGHT );
		catPanel.add ( prompt );
		categories = new JLabel ();
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
		this.journal.getDescription ().setValue ( description.getText () );
		this.journal.getSummary ().setValue ( subject.getText () );
		try {
			repo.saveJournal ( this.journal );
		} catch ( IOException e ) {
			// TODO: add error handler that pops up a window here
			e.printStackTrace ();
		}
		System.out.println ( "Is date only: "
		    + ( this.journal.getStartDate ().isDateOnly () ? "true" : "false" ) );
		// TODO: update main window
		this.dispose ();
	}

	void close () {
		// TODO: check for unsaved changes
		this.dispose ();
	}
}
