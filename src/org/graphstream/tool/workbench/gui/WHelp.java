/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.tool.workbench.gui;

import org.graphstream.tool.workbench.WNotificationServer;
import org.graphstream.tool.workbench.event.NotificationListener;
import org.graphstream.tool.workbench.xml.WXElement;
import org.graphstream.tool.workbench.xml.WXmlConstants;
import org.graphstream.tool.workbench.xml.WXmlHandler;

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

public class WHelp
	extends JDialog
	implements NotificationListener, WXmlConstants
{
	private static final long serialVersionUID = 0x040120000001L;
	
	static void init() {}
	
	private static final String GSWB_HELP_XML = "org/miv/graphstream/tool/workbench/xml/gswb-help.xml";
	
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
		private static final long serialVersionUID = 0x040120020001L;
		
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
			
			if( WCss.hasStyleSheet("help") )
				editor.setStyleSheet(WCss.getStyleSheet("help"));
			
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
	public static void showHelpModule()
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
