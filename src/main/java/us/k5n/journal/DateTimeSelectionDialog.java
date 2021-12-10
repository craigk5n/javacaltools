package us.k5n.journal;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import us.k5n.ical.BogusDataException;
import us.k5n.ical.Date;

public class DateTimeSelectionDialog extends JDialog {
	JTextField dayOfMonth, year;
	JComboBox month;
	JTextField hour, minute, second;
	JCheckBox timeEnabled;
	Date date;
	boolean userAccepted = false;

	public static Date showDateTimeSelectionDialog ( JFrame parent, Date date ) {
		DateTimeSelectionDialog dts = new DateTimeSelectionDialog ( parent, date );
		Date d = dts.userAccepted ? dts.date : null;
		return d;
	}

	public DateTimeSelectionDialog(JFrame parent, Date date) {
		super ( (JFrame) null );
		setDefaultCloseOperation ( JDialog.DISPOSE_ON_CLOSE );
		this.setModal ( true );
		if ( date == null )
			date = Date.getCurrentDateTime ( "DTSTART" );
		this.date = date;
		this.getContentPane ().setLayout ( new BorderLayout () );
		JPanel buttonPanel = new JPanel ();
		buttonPanel.setLayout ( new FlowLayout () );
		JButton cancelButton = new JButton ( "Cancel" );
		cancelButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				cancel ();
			}
		} );
		buttonPanel.add ( cancelButton );

		JButton okButton = new JButton ( "Ok" );
		okButton.addActionListener ( new ActionListener () {
			public void actionPerformed ( ActionEvent event ) {
				ok ();
			}
		} );
		buttonPanel.add ( okButton );

		this.getContentPane ().add ( buttonPanel, BorderLayout.SOUTH );

		JPanel topPanel = createDateTimeSelection ( date );

		this.getContentPane ().add ( topPanel, BorderLayout.CENTER );

		this.pack ();
		this.setVisible ( true );
	}

	private JPanel createDateTimeSelection ( Date date ) {
		int[] vProportions = { 1, 2 };
		int[] hProportions = { 1, 3 };

		ProportionalLayout vLayout = new ProportionalLayout ( vProportions,
		    ProportionalLayout.VERITCAL_LAYOUT );
		JPanel panel = new JPanel ();
		panel.setLayout ( vLayout );

		JPanel datePanel = new JPanel ();
		ProportionalLayout hLayout1 = new ProportionalLayout ( hProportions,
		    ProportionalLayout.HORIZONTAL_LAYOUT );
		datePanel.add ( new JLabel ( "Date: " ) );
		JPanel dateSelPanel = new JPanel ();
		dateSelPanel.setLayout ( new FlowLayout () );

		dayOfMonth = new JTextField ();
		dayOfMonth.setColumns ( 2 );
		dayOfMonth.setText ( "" + date.getDay () );
		dateSelPanel.add ( dayOfMonth );

		Vector sortOptions = new Vector ();
		sortOptions.add ( "Jan" );
		sortOptions.add ( "Feb" );
		sortOptions.add ( "Mar" );
		sortOptions.add ( "Apr" );
		sortOptions.add ( "May" );
		sortOptions.add ( "Jun" );
		sortOptions.add ( "Jul" );
		sortOptions.add ( "Aug" );
		sortOptions.add ( "Sep" );
		sortOptions.add ( "Oct" );
		sortOptions.add ( "Nov" );
		sortOptions.add ( "Dec" );
		month = new JComboBox ( sortOptions );
		month.setSelectedIndex ( date.getMonth () - 1 );
		dateSelPanel.add ( month );

		year = new JTextField ();
		year.setColumns ( 4 );
		year.setText ( "" + date.getYear () );
		dateSelPanel.add ( year );

		datePanel.add ( dateSelPanel );
		panel.add ( datePanel );

		JPanel timePanel = new JPanel ();
		timePanel.setLayout ( new GridLayout ( 2, 1 ) );

		JPanel enabledPanel = new JPanel ();
		enabledPanel.setLayout ( new ProportionalLayout ( hProportions,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		enabledPanel.add ( new JLabel ( "Time: " ) );

		timeEnabled = new JCheckBox ( "Enabled" );
		timeEnabled.setSelected ( !date.isDateOnly () );
		enabledPanel.add ( timeEnabled );

		timePanel.add ( enabledPanel );

		JPanel timeSubPanel = new JPanel ();
		timeSubPanel.setLayout ( new ProportionalLayout ( hProportions,
		    ProportionalLayout.HORIZONTAL_LAYOUT ) );
		timeSubPanel.add ( new JLabel ( " " ) );

		JPanel hmsPanel = new JPanel ();
		FlowLayout leftFlow = new FlowLayout ();
		leftFlow.setAlignment ( FlowLayout.LEFT );
		hmsPanel.setLayout ( leftFlow );
		hour = new JTextField ();
		hour.setColumns ( 2 );
		hour.setText ( "" + date.getHour () );
		hmsPanel.add ( hour );
		hmsPanel.add ( new JLabel ( ":" ) );
		minute = new JTextField ();
		minute.setColumns ( 2 );
		minute.setText ( "" + date.getMinute () );
		hmsPanel.add ( minute );
		hmsPanel.add ( new JLabel ( ":" ) );
		second = new JTextField ();
		second.setColumns ( 2 );
		second.setText ( "" + date.getSecond () );
		hmsPanel.add ( second );
		timeSubPanel.add ( hmsPanel );

		timePanel.add ( timeSubPanel );

		panel.add ( timePanel );

		panel.setBorder ( BorderFactory.createEtchedBorder () );

		return panel;
	}

	protected void cancel () {
		this.dispose ();
	}

	protected int getIntValue ( JTextField field, String fieldName, int minVal,
	    int maxVal ) {
		int ret = -1;
		try {
			ret = Integer.parseInt ( field.getText () );
		} catch ( Exception e ) {
			// invalid value
		}
		if ( ret < minVal || ret > maxVal ) {
			JOptionPane.showMessageDialog ( this, "Invalid value for " + fieldName,
			    "Invalid Date", JOptionPane.ERROR_MESSAGE );
			ret = -1;
		}
		return ret;
	}

	protected void ok () {
		Date d = null;
		int Y, M, D, h, m, s;
		if ( ( Y = getIntValue ( year, "year", 1900, 3000 ) ) < 0 )
			return;
		M = month.getSelectedIndex () + 1;
		if ( ( D = getIntValue ( dayOfMonth, "day of month", 1, 31 ) ) < 0 )
			return;
		try {
			if ( timeEnabled.isSelected () ) {
				if ( ( h = getIntValue ( hour, "hour", 0, 23 ) ) < 0 )
					return;
				if ( ( m = getIntValue ( minute, "minute", 0, 59 ) ) < 0 )
					return;
				if ( ( s = getIntValue ( second, "second", 0, 59 ) ) < 0 )
					return;
				d = new Date ( this.date.getName (), Y, M, D, h, m, s );
			} else {
				d = new Date ( this.date.getName (), Y, M, D );
			}
		} catch ( BogusDataException e1 ) {
			JOptionPane.showMessageDialog ( this,
			    "Invalid date: " + e1.getMessage (), "Invalid Date",
			    JOptionPane.ERROR_MESSAGE );
			return;
		}
		this.userAccepted = true;
		this.date = d;
		this.dispose ();
	}
}
