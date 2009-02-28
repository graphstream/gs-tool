/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */
package org.miv.graphstream.tool.workbench.gui;

import org.miv.graphstream.tool.workbench.event.NotificationListener;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

import java.io.InputStream;

import java.net.URL;

import java.util.LinkedList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class WMenuBar
	extends JMenuBar
	implements NotificationListener
{
	public static final long serialVersionUID = 0x00A00301L;

	static final String GSWB_MENUBAR_XML = "org/miv/graphstream/tool/workbench/gui/wb-menu-skeletton.xml";
	
	/*
	 * XML Menu Skeletton
	 * ------------------
	 * 
	 * [gswb:menubar] : root tag of the file
	 * [menu] {id,name,icon,disableOn,enableOn} : starts a menu
	 * [item] {id,name,type,strokeKey,strokeMask,command,icon,disableOn,enableOn} : adds a new item
	 * [separator] : a menu separator
	 *
	 */

	static String BALISE_ROOT = "gswb-menubar";
	
	class SkelettonHandler extends DefaultHandler
	{
		
		LinkedList<JMenu> queue;
		boolean rooted;
		ActionListener actionListener;
		
		public SkelettonHandler( ActionListener al )
		{
			queue = new LinkedList<JMenu>();
			rooted = false;
			actionListener = al;
		}
		
		public void startElement (String uri, String localName,
			      String qName, Attributes atts)
			throws SAXException
		{
			if( ! rooted && ! qName.equals( BALISE_ROOT ) )
			{
				throw new SAXException( "skeletton must start with " + BALISE_ROOT );
			}
			
			rooted = true;
			JComponent last = null;
			
			if( qName.equals( "menu" ) )
			{
				JMenu menu = new JMenu( atts.getValue("name") );
				
				if( atts.getValue("icon") != null )
				{
					if( atts.getValue("icon").startsWith("@"))
					{
						menu.setIcon( WorkbenchUtils.getImageIcon(atts.getValue("icon").substring(1)) );
					}
					else
					{
						URL iconURL = ClassLoader.getSystemResource(atts.getValue("icon"));
						if( iconURL != null )
							menu.setIcon( new ImageIcon(iconURL) );
					}
				}
				
				queue.addFirst(menu);
				last = menu;
			}
			else if( qName.equals( "item" ) )
			{
				last = handleNewItem(atts);
			}
			else if( qName.equals("separator") )
			{
				if( queue.size() > 0 )
					queue.getFirst().addSeparator();
			}
			
			if( atts.getValue("disableOn") != null )
			{
				String [] notifs = atts.getValue("disableOn").split(",");
				for( String notif : notifs )
					WMenuBar.this.disableOn(last,notif);
			}
			
			if( atts.getValue("enableOn") != null )
			{
				String [] notifs = atts.getValue("enableOn").split(",");
				for( String notif : notifs )
					WMenuBar.this.enableOn(last,notif);
			}
				
			if( last != null && atts.getValue("id") != null )
				WMenuBar.this.registerComponent(atts.getValue("id"),last);
		}
		
		public void endElement(String uri, String localName, String qName)
		{
			if( qName.equals("menu") )
			{
				JMenu m = queue.poll();
				if( queue.size() > 0 )
					queue.getFirst().add(m);
				else
					WMenuBar.this.add(m);
			}
		}
		
		protected JComponent handleNewItem( Attributes atts )
		{
			int strokeKey = 0;
			int strokeModifier = 0;
			boolean useStroke = false;
			boolean useModifier = false;
			
			if( atts.getValue("strokeKey") != null )
			{
				try
				{
					strokeKey = KeyEvent.class.getDeclaredField(atts.getValue("strokeKey")).getInt(null);
					useStroke = true;
				}
				catch( Exception e )
				{
					System.err.printf( "unknown key : %s\n", atts.getValue("strokeKey") );
				}
			}
			
			if( atts.getValue("strokeModifier" ) != null )
			{
				String [] mods = atts.getValue("strokeModifier").split(",");
				if( mods != null )
					for( String mod : mods )
					{
						try
						{
							strokeModifier |= InputEvent.class.getDeclaredField(mod).getInt(null);
							useModifier = true;
						}
						catch( Exception e )
						{
							System.err.printf( "unknown modifier : \"%s\"\n", mod );
						}
					}
			}
			
			JMenuItem item = null;
			
			if( atts.getValue("type") == null || atts.getValue("type").equals("menuitem") )
				item = new JMenuItem( atts.getValue("name") );
			else if( atts.getValue("type").equals("checkbox") )
				item = new JCheckBoxMenuItem( atts.getValue("name") );
			else
			{
				// TODO
				System.err.printf( "%s not yet implemented\n", atts.getValue("type") );
			}
			
			if( atts.getValue("icon") != null )
			{
				if( atts.getValue("icon").startsWith("@"))
				{
					item.setIcon( WorkbenchUtils.getImageIcon(atts.getValue("icon").substring(1)) );
				}
				else
				{
					URL iconURL = ClassLoader.getSystemResource(atts.getValue("icon"));
					if( iconURL != null )
						item.setIcon( new ImageIcon(iconURL) );
				}
			}

			if( useStroke && useModifier )
				((JMenuItem) item).setAccelerator( KeyStroke.getKeyStroke(strokeKey,strokeModifier) );
			else if( useStroke )
				((JMenuItem) item).setAccelerator( KeyStroke.getKeyStroke((char)strokeKey) );
			
			item.addActionListener(actionListener);
			if( atts.getValue("command") != null )
				item.setActionCommand(atts.getValue("command"));
			
			if( queue.size() > 0 )
				queue.getFirst().add(item);
			
			return item;
		}
	}
	
	static class ComponentPool
		extends LinkedList<JComponent>
	{
		private static final long serialVersionUID = 0x0001L;
		
		public void disable()
		{
			java.util.Iterator<JComponent> ite = iterator();
			
			while( ite.hasNext() )
				ite.next().setEnabled(false);
		}
		
		public void enable()
		{
			java.util.Iterator<JComponent> ite = iterator();
			
			while( ite.hasNext() )
				ite.next().setEnabled(true);
		}
	}
	
	HashMap<String,JComponent> idMapping;
	HashMap<Notification,ComponentPool> toDisable;
	HashMap<Notification,ComponentPool> toEnable;
	
	public WMenuBar( ActionListener listener )
	{
		super();
		
		setPreferredSize( new Dimension( 200, 25 ) );
		
		idMapping 	= new HashMap<String,JComponent>();
		toDisable 	= new HashMap<Notification,ComponentPool>();
		toEnable 	= new HashMap<Notification,ComponentPool>();
		
		InputStream skel = ClassLoader.getSystemResourceAsStream(GSWB_MENUBAR_XML);
		if( skel == null )
			System.err.printf( "can not load xml skeletton file : \"%s\"\n", GSWB_MENUBAR_XML );
		else
			loadXml(listener,skel);
		
		WNotificationServer.connect(this);
	}
	
	private void loadXml( ActionListener al, InputStream in )
	{
		XMLReader rx;
		
		try
		{
			rx = XMLReaderFactory.createXMLReader();
			rx.setContentHandler(new SkelettonHandler(al));
			rx.parse(new InputSource(in));
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public void registerComponent( String id, JComponent c )
	{
		idMapping.put(id,c);
	}
	
	public JComponent getRegisteredComponent( String id )
	{
		return idMapping.get(id);
	}
	
	public void disableOn( JComponent c, String n )
	{
		Notification notif = Notification.valueOf(n);
		
		if( ! toDisable.containsKey(notif) )
			toDisable.put(notif,new ComponentPool());
		
		toDisable.get(notif).add(c);
	}
	
	public void enableOn( JComponent c, String n )
	{
		Notification notif = Notification.valueOf(n);
		
		if( ! toEnable.containsKey(notif) )
			toEnable.put(notif,new ComponentPool());
		
		toEnable.get(notif).add(c);
	}
	
	public void handleNotification( Notification n )
	{
		if( toDisable.containsKey(n) )
			toDisable.get(n).disable();
		if( toEnable.containsKey(n) )
			toEnable.get(n).enable();
	}
}
