package org.miv.graphstream.tool.workbench.gui;

import org.miv.graphstream.tool.workbench.event.NotificationListener;
import org.miv.graphstream.tool.workbench.xml.WXElement;
import org.miv.graphstream.tool.workbench.xml.WXmlConstants;
import org.miv.graphstream.tool.workbench.xml.WXmlHandler;

import java.awt.BorderLayout;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import java.io.StringReader;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class WHelp
	extends JDialog
	implements NotificationListener, WXmlConstants
{
	private static final long serialVersionUID = 0X0001L;
	
	static void init() {}
	
	private static final String GSWB_HELP_XML = "org/miv/graphstream/tool/workbench/ressources/gswb-help.xml";
	private static final String GSWB_HELP_CSS = "org/miv/graphstream/tool/workbench/ressources/gswb-help.css";
	
	private static final StyleSheet style = new StyleSheet();
	static
	{
		java.net.URL url = ClassLoader.getSystemResource(GSWB_HELP_CSS);
		
		if( url != null )
			style.importStyleSheet(url);
	}
	/*
	class HelpHandler
		extends WGetText.GetTextHandler
	{
		boolean rooted = false;
		StringBuffer content;
		boolean readingContent = false;
		SectionContent current = null;
		String sectionName = null;
		String subsectionName = null;
		
		public HelpHandler()
		{
		}
		
		public void startElementFiltered(String uri, String localName,
			      String qName, Attributes atts)
			throws SAXException
		{
			if( readingContent )
			{
				content.append("<" + qName );
				for( int i = 0; i < atts.getLength(); i++ )
				{
					content.append( String.format( " %s=\"%s\"", atts.getQName(i), atts.getValue(i) ));
				}
				content.append(">");
			}
			else if( qName.equals("section") )
			{
				sectionName = atts.getValue("name");
				current 	= new SectionContent();
			}
			else if( qName.equals("subsection") )
			{
				subsectionName 	= atts.getValue("name");
				readingContent 	= true;
				content			= new StringBuffer();
			}
		}
		
		public void charactersFiltered(char[] ch, int start, int length)
		{
			if( readingContent && ch != null )
				content.append(ch,start,length);
		}
		
		public void endElementFiltered(String uri, String localName, String qName)
		{
			if( readingContent && ! qName.equals("subsection") )
			{
				content.append("</" + qName + ">");
			}
			else if( qName.equals("section") )
			{
				addSection( sectionName, current );
				sectionName = null;
				current = null;
			}
			else if( qName.equals("subsection") )
			{
				current.newSubSection( subsectionName, content.toString() );
				subsectionName = null;
				content = null;
				readingContent = false;
			}
		}
	}
	*/
	class HelpHandler
		implements WXmlHandler.WXElementHandler
	{
		public void handle( WXElement wxe )
		{
			if( wxe.is(SPEC_HELP_SECTION) )
			{
				SectionContent content = new SectionContent();
				String name = wxe.getAttribute(QNAME_GSWB_HELP_SECTION_NAME);
				
				Iterator<WXElement> ite = wxe.iteratorOnChildren();
				while( ite.hasNext() )
				{
					WXElement subsection = ite.next();
					
					content.newSubSection(
							subsection.getAttribute(QNAME_GSWB_HELP_SUBSECTION_NAME),
							subsection.getContent() );
				}
				
				newSection( name, content );
			}
		}
	}
	
	static class SectionContent
		extends JPanel
		implements ItemListener
	{
		private static final long serialVersionUID = 0x0001L;
		
		private HashMap<String,Document> 	contents;
		private DefaultComboBoxModel		subsections;
		private JTextPane					content;
		private HTMLEditorKit				editor;
		
		public SectionContent()
		{
			contents 	= new HashMap<String,Document>();
			subsections = new DefaultComboBoxModel();
			content		= new JTextPane();
			editor		= new HTMLEditorKit();
			
			content.setEditable(false);
			content.setPreferredSize(new java.awt.Dimension(400,300));
			content.setEditorKit(editor);
			
			editor.setStyleSheet(style);
			
			JComboBox box = new JComboBox(subsections);
			box.addItemListener(this);
			
			setLayout( new BorderLayout() );
			add( box, BorderLayout.NORTH );
			add( content, BorderLayout.CENTER );
		}
		
		public void newSubSection( String name, String content )
		{
			Document doc = editor.createDefaultDocument();
			contents.put(name,doc);
			subsections.addElement(name);
			
			StringReader in = new StringReader( 
					"<html><head></head>" +
					"<body><div id='content'>" + content + "</div></body></html>" );
			
			try
			{
				editor.read(in,doc,0);
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		
		public void itemStateChanged( ItemEvent e )
		{
			content.setDocument(contents.get(e.getItem()));
		}
	}
	
	private static final WHelp help = new WHelp();
	public static void showHelp()
	{
		help.setVisible(true);
	}
	
	JTabbedPane sections;
	
	private WHelp()
	{
		setTitle( "Help" );
		setLayout( new BorderLayout() );
		
		sections = new JTabbedPane();
		add( sections, BorderLayout.CENTER );
		
		load();
		
		WNotificationServer.connect(this);
	}
	
	private void reload()
	{
		sections.removeAll();
		load();
	}
	
	private void load()
	{
		WGetText.readGetTextXml( new HelpHandler(),
				ClassLoader.getSystemResourceAsStream(GSWB_HELP_XML));
	}
	
	public void newSection( String name, SectionContent content )
	{
		sections.addTab( name, WUtils.getImageIcon("help"), content );
		pack();
	}
	
	public void handleNotification( Notification n )
	{
		if( n == Notification.langChanged )
			reload();
	}
}
