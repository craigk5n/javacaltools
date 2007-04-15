package us.k5n.journal;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import us.k5n.ical.Journal;
import us.k5n.ical.Summary;

public class JournalViewPanel extends JPanel {
	private Journal journal;
	private JLabel date;
	private JLabel subject;
	private JLabel categories;
	private JTextArea text;

	public JournalViewPanel() {
		super ();

		journal = null;

		setLayout ( new BorderLayout () );

		JPanel topPanel = new JPanel ();
		topPanel.setLayout ( new GridLayout ( 3, 1 ) );

		JPanel subpanel = new JPanel ();
		subpanel.setLayout ( new BorderLayout () );
		subpanel.add ( new JLabel ( "Date: " ), BorderLayout.WEST );
		date = new JLabel ();
		subpanel.add ( date, BorderLayout.CENTER );
		topPanel.add ( subpanel );

		subpanel = new JPanel ();
		subpanel.setLayout ( new BorderLayout () );
		subpanel.add ( new JLabel ( "Subject: " ), BorderLayout.WEST );
		subject = new JLabel ();
		subpanel.add ( subject, BorderLayout.CENTER );
		topPanel.add ( subpanel );

		subpanel = new JPanel ();
		subpanel.setLayout ( new BorderLayout () );
		subpanel.add ( new JLabel ( "Categories: " ), BorderLayout.WEST );
		categories = new JLabel ();
		subpanel.add ( categories, BorderLayout.CENTER );
		topPanel.add ( subpanel );

		add ( topPanel, BorderLayout.NORTH );

		text = new JTextArea ();
		text.setEditable ( false );
		JScrollPane scrollPane = new JScrollPane ( text );

		add ( scrollPane, BorderLayout.CENTER );
	}

	public void clear () {
		date.setText ( "" );
		subject.setText ( "" );
		categories.setText ( "" );
		text.setText ( "" );
	}

	public void setJournal ( Journal j ) {
		this.journal = j;
		if ( j.getStartDate () != null ) {
			DisplayDate d = new DisplayDate ( j.getStartDate () );
			date.setText ( d.toString () );
		} else {
			date.setText ( "None" );
		}
		Summary s = j.getSummary ();
		if ( s != null ) {
			subject.setText ( s.getValue () );
		} else {
			subject.setText ( "(None)" );
		}
		if ( j.getCategories () != null ) {
			categories.setText ( j.getCategories ().getValue () );
		} else {
			categories.setText ( "(None)" );
		}
		if ( j.getDescription () != null ) {
			text.setText ( j.getDescription ().getValue () );
		} else {
			text.setText ( "" );
		}
	}
}
