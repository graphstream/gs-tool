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

import org.miv.graphstream.tool.workbench.WNotificationServer;
import org.miv.graphstream.tool.workbench.event.NotificationListener;
import org.miv.graphstream.tool.workbench.xml.WXElement;
import org.miv.graphstream.tool.workbench.xml.WXmlConstants;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

import java.io.InputStream;

import java.net.URL;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class WMenuBar
	extends JMenuBar
	implements NotificationListener, WXmlConstants
{
	public static final long serialVersionUID = 0x00A00301L;

	private static final Pattern getaction = Pattern.compile("^@getaction\\((.*)\\)$");
	
	public static final String GSWB_MENUBAR_XML = "org/miv/graphstream/tool/workbench/xml/gswb-menu.xml";
	
	public void handle( JMenu parent, WXElement wxe, ActionListener listener )
	{
		if( wxe.is(SPEC_MENU_MENU) )
		{
			String id	= wxe.getAttribute(QNAME_GSWB_MENU_MENU_ID);
			String name = wxe.getAttribute(QNAME_GSWB_MENU_MENU_NAME);
			String icon = wxe.getAttribute(QNAME_GSWB_MENU_MENU_ICON);
			
			String disableOn = wxe.getAttribute(QNAME_GSWB_MENU_MENU_DISABLEON);
			String enableOn  = wxe.getAttribute(QNAME_GSWB_MENU_MENU_ENABLEON);
			
			JMenu menu = new JMenu( WGetText.getTextLookup(name) );

			if( icon != null )
			{
				if( icon.startsWith("@"))
				{
					menu.setIcon( WUtils.getImageIcon(icon.substring(1)) );
				}
				else
				{
					URL iconURL = ClassLoader.getSystemResource(icon);

					if( iconURL != null )
						menu.setIcon( new ImageIcon(iconURL) );
				}
			}

			if( id != null )
				registerComponent(id,menu);
			
			if( parent == null )
				add( menu );
			else
				parent.add(menu);
			
			if( disableOn != null )
			{
				String [] events = disableOn.split(",");
				
				for( String event : events )
					disableOn(menu,event);
			}
			
			if( enableOn != null )
			{
				String [] events = enableOn.split(",");
				
				for( String event : events )
					enableOn(menu,event);
			}
			
			Iterator<WXElement> ite = wxe.iteratorOnChildren();
			while( ite.hasNext() )
				handle(menu,ite.next(),listener);
			
			WUtils.reloadOnLangChanged(menu,name,"setText");
		}
		else if( wxe.is(SPEC_MENU_ITEM) )
		{
			String command = wxe.getAttribute(QNAME_GSWB_MENU_ITEM_COMMAND);
			Matcher m;

			String disableOn = wxe.getAttribute(QNAME_GSWB_MENU_ITEM_DISABLEON);
			String enableOn  = wxe.getAttribute(QNAME_GSWB_MENU_ITEM_ENABLEON);

			JMenuItem item = null;
			
			if( command == null )
			{
				m = null;
			}
			else
			{
				m = getaction.matcher(wxe.getAttribute(QNAME_GSWB_MENU_ITEM_COMMAND));
			}
			
			if( m != null && m.matches() && WActions.hasAction(m.group(1)) )
			{
				item = parent.add( WActions.getAction(m.group(1)));
			}
			
			else
			{
				int strokeKey 		= 0;
				int strokeModifier 	= 0;
				boolean useStroke 	= false;
				boolean useModifier = false;
				String name 		= wxe.getAttribute(QNAME_GSWB_MENU_ITEM_NAME);

				if( wxe.getAttribute(QNAME_GSWB_MENU_ITEM_STROKEKEY) != null )
				{
					try
					{
						strokeKey = KeyEvent.class.getDeclaredField(
								wxe.getAttribute(QNAME_GSWB_MENU_ITEM_STROKEKEY)).getInt(null);
						useStroke = true;
					}
					catch( Exception e )
					{
						System.err.printf( "unknown key : %s\n",
								wxe.getAttribute(QNAME_GSWB_MENU_ITEM_STROKEKEY) );
					}
				}

				if( wxe.getAttribute(QNAME_GSWB_MENU_ITEM_STROKEMODIFIER) != null )
				{
					String [] mods = wxe.getAttribute(QNAME_GSWB_MENU_ITEM_STROKEMODIFIER).split(",");
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

				if( wxe.getAttribute(QNAME_GSWB_MENU_ITEM_TYPE) == null || 
						wxe.getAttribute(QNAME_GSWB_MENU_ITEM_TYPE).equals("menuitem") )
					item = new JMenuItem();
				else if( wxe.getAttribute(QNAME_GSWB_MENU_ITEM_TYPE).equals("checkbox") )
					item = new JCheckBoxMenuItem();
				else
				{
					// TODO
					System.err.printf( "%s not yet implemented\n",
							wxe.getAttribute(QNAME_GSWB_MENU_ITEM_TYPE) );
				}

				if( name != null )
					item.setText( WGetText.getTextLookup(name) );

				if( wxe.getAttribute(QNAME_GSWB_MENU_ITEM_ICON) != null )
				{
					if( wxe.getAttribute(QNAME_GSWB_MENU_ITEM_ICON).startsWith("@"))
					{
						item.setIcon( WUtils.getImageIcon(
								wxe.getAttribute(QNAME_GSWB_MENU_ITEM_ICON).substring(1)) );
					}
					else
					{
						URL iconURL = ClassLoader.getSystemResource(
								wxe.getAttribute(QNAME_GSWB_MENU_ITEM_ICON));

						if( iconURL != null )
							item.setIcon( new ImageIcon(iconURL) );
					}
				}

				if( useStroke && useModifier )
					((JMenuItem) item).setAccelerator( KeyStroke.getKeyStroke(strokeKey,strokeModifier) );
				else if( useStroke )
					((JMenuItem) item).setAccelerator( KeyStroke.getKeyStroke((char)strokeKey) );

				item.addActionListener(listener);
				
				if( command != null )
					item.setActionCommand(wxe.getAttribute(QNAME_GSWB_MENU_ITEM_COMMAND));

				if( parent != null )
					parent.add(item);
				
				WUtils.reloadOnLangChanged(item,name,"setText");
			}
			
			if( disableOn != null )
			{
				String [] events = disableOn.split(",");
				
				for( String event : events )
					disableOn(item,event);
			}
			
			if( enableOn != null )
			{
				String [] events = enableOn.split(",");
				
				for( String event : events )
					enableOn(item,event);
			}
		}
		else if( wxe.is(SPEC_MENU_SEPARATOR) )
		{
			if( parent != null )
				parent.addSeparator();
		}
		else if( wxe.is(SPEC_MENU) )
		{
			Iterator<WXElement> ite = wxe.iteratorOnChildren();
			
			while( ite.hasNext() )
				handle( null, ite.next(), listener );
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
		handle( null, WGetText.readGetTextXml(null,in), al );
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
