package us.k5n.ical;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringBufferInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A simple RSS 2.0 parser that will import as much relavant data as possible
 * from the RSS format.
 * 
 * @author Craig Knudsen, craig@k5n.us (AI-assisted: Grok-4.1-Fast)
 */
public class RSSImporter extends CalendarParser {

	/**
	 * Create a RSSImporter.
	 */
	public RSSImporter(int parseMethod) {
		super(parseMethod);
	}

	public boolean parse(InputStream is) throws IOException {
		return parse(new InputStreamReader(is));
	}

	public boolean parse(Reader reader) throws IOException {
		int nerrors = 0;

		BufferedReader r = new BufferedReader(reader);
		StringBuffer data = new StringBuffer();
		boolean done = false;

		while (!done) {
			String line = r.readLine();
			if (line != null) {
				data.append(line);
				data.append("\n");
			} else {
				done = true;
			}
		}
		reader.close();
		StringBufferInputStream sbis = new StringBufferInputStream(data
				.toString());

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(sbis);

			NodeList list = document.getElementsByTagName("channel");
			int len = list.getLength();
			if (list.getLength() < 1) {
				reportParseError(new ParseError(0,
						"RSS Parse error: no <channel> tag found "));
				return false;
			}
			Node topNode = list.item(0);
			list = topNode.getChildNodes();
			len = list.getLength();

			for (int i = 0; i < len; i++) {
				Node n = list.item(i);

				if (n.getNodeType() == Node.ELEMENT_NODE) {
					String nodeName = n.getNodeName();
					if ("item".equals(nodeName)) {
						RSSItem item = new RSSItem(n);
						Event event = item.toEvent();
						for (int j = 0; j < dataStores.size(); j++) {
							DataStore ds = dataStores.get(j);
							ds.storeEvent(event);
						}
					}
				}
			}
		} catch (ParserConfigurationException e1) {
			reportParseError(new ParseError(0, "RSS Parse error: "
					+ e1.getMessage()));
			return false;
		} catch (SAXException e2) {
			reportParseError(new ParseError(0, "RSS Parse error: "
					+ e2.getMessage()));
			return false;
		} catch (BogusDataException e3) {
			reportParseError(new ParseError(0, "RSS Parse error: "
					+ e3.getMessage()));
			return false;
		} catch (ParseException e4) {
			reportParseError(new ParseError(0, "RSS Parse error: "
					+ e4.getMessage()));
			return false;
		}

		return true;
	}

	// TODO: support date formats other than "12/31/1999" or "12/31/99"
	private Date parseDateTime(String dateType, String date, String time)
			throws ParseException {
		int Y, M, D, h, m, s;
		String[] args = null;
		Date ret = null;
		args = date.split("[/-]");
		if (args == null || args.length != 3) {
			throw new ParseException("Invalid date", date);
		}
		try {
			Y = Integer.parseInt(args[2]);
			// hack: handle 2-digit years
			if (Y < 100 && Y < 70)
				Y += 1900;
			else if (Y < 100)
				Y += 2000;
			M = Integer.parseInt(args[0]);
			D = Integer.parseInt(args[1]);
		} catch (NumberFormatException e1) {
			throw new ParseException("Invalid date: " + e1.getMessage(), date);
		}

		try {
			ret = new Date(dateType, Y, M, D);
		} catch (BogusDataException e1) {
			throw new ParseException("Invalid date: " + e1.getMessage(), date);
		}

		if (time != null && time.length() > 0) {
			args = time.split("[ :]");
			try {
				h = Integer.parseInt(args[0]);
				m = Integer.parseInt(args[1]);
				s = Integer.parseInt(args[2]);
				if (args.length > 3) {
					// look for AM or PM
					if (args[3].toUpperCase().equals("PM")) {
						if (h < 12)
							h += 12;
					} else if (args[3].toUpperCase().equals("AM")) {
						if (h == 12)
							h = 0; // 12AM = 0
					}
				}
			} catch (NumberFormatException e1) {
				throw new ParseException("Invalid date time: " + e1.getMessage(),
						date);
			}
			ret.setHour(h);
			ret.setMinute(m);
			ret.setSecond(s);
			ret.setDateOnly(false);
		}
		return ret;
	}
}

class RSSItem {
	public String title = null, link = null, description = null, pubdate = null,
			guid = null, author = null, date = null;

	public RSSItem(Node topNode) {
		NodeList list = topNode.getChildNodes();
		int len = list.getLength();

		for (int i = 0; i < len; i++) {
			Node n = list.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = n.getNodeName();
				if ("title".equals(nodeName)) {
					this.title = XmlUtils.xmlNodeGetValue(n);
				} else if ("description".equals(nodeName)) {
					this.description = XmlUtils.xmlNodeGetValue(n);
				} else if ("link".equals(nodeName)) {
					this.link = XmlUtils.xmlNodeGetValue(n);
				} else if ("pubdate".equals(nodeName)) {
					this.pubdate = XmlUtils.xmlNodeGetValue(n);
				} else if ("guid".equals(nodeName)) {
					this.guid = XmlUtils.xmlNodeGetValue(n);
				} else if ("author".equals(nodeName)) {
					this.author = XmlUtils.xmlNodeGetValue(n);
				} else if ("dc:date".equals(nodeName)) {
					this.date = XmlUtils.xmlNodeGetValue(n);
				} else {
					// ignore other fields
				}
			}
		}
	}

	public Event toEvent() throws BogusDataException, ParseException {
		// RSS date format is "2008-01-25T18:44:09Z"
		Date startDate = null;
		if (date == null) {
			startDate = Date.getCurrentDate("DTSTART");
		} else {
			String s = date.replaceAll("-", "");
			startDate = new Date("DTSTART:" + s);
		}
		Event e = new Event(title, description, startDate, 0);
		if (this.link != null) {
			e.setUrl(new URL(link));
		}

		return e;
	}

}

class XmlUtils {
	/**
	 * * For tags such as <name>xxx</name>, get the "xxx" for the Node.
	 * 
	 * @param node
	 *             the XML Node object
	 * @return the String value
	 */
	public static String xmlNodeGetValue(Node node) {
		NodeList list = node.getChildNodes();
		int len = list.getLength();
		if (len > 1)
			System.err.println("  Error: length of node=" + len + " for tag <"
					+ node.getNodeName() + ">");
		for (int i = 0; i < len; i++) {
			Node n = list.item(i);
			// System.out.println ( " " + i + "> name=" + n.getNodeName() + ", value="
			// +
			// n.getNodeValue () + ", type=" + n.getNodeType() );
			if (n.getNodeType() == Node.TEXT_NODE) {
				return (n.getNodeValue());
			}
		}
		return (null); // not found
	}

	/**
	 * For tags such as <name attr="xxx" />, get the "xxx".
	 */
	public static String xmlNodeGetAttribute(Node node, String name) {
		NamedNodeMap list = node.getAttributes();
		if (list == null)
			return null;
		int len = list.getLength();
		if (len == 0)
			return null;
		for (int i = 0; i < len; i++) {
			Node n = list.item(i);
			// System.out.println ( " " + i + "> name=" + n.getNodeName() + ", value="
			// +
			// n.getNodeValue () + ", type=" + n.getNodeType() );
			if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
				Attr attr = (Attr) n;
				if (name.equalsIgnoreCase(attr.getName())) {
					return attr.getValue();
				}
			}
		}
		return (null); // not found
	}
}
