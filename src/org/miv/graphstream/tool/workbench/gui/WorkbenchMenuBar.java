/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.tool.workbench.gui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class WorkbenchMenuBar extends JMenuBar
{
	public static final long serialVersionUID = 0x00A00301L;
	
	private static void installItems( JMenuBar bar, ActionListener al )
	{
		JMenu menu, submenu;
		JMenuItem item;
		
		menu = new JMenu( "File" ); bar.add( menu );
			item = new JMenuItem( "New" );  
				item.setActionCommand( "new.graph" ); 
				item.addActionListener( al );
				item.setAccelerator( 
						KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK ) );
				menu.add( item );
			item = new JMenuItem( "Open" ); 
				item.setActionCommand( "open.graph" );
				item.addActionListener( al );
				item.setMnemonic( KeyEvent.VK_O );
				item.setAccelerator( 
						KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK ) );
				menu.add( item );
			item = new JMenuItem( "Save" );
				item.setActionCommand( "save.graph" );
				item.addActionListener( al );
				item.setAccelerator( 
						KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK ) );
				menu.add( item );
			menu.addSeparator();
			item = new JMenuItem( "Quit" );
				item.setActionCommand( "quit" );
				item.addActionListener( al );
				item.setAccelerator( 
						KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK ) );
				menu.add( item );
		menu = new JMenu( "Edit" ); bar.add( menu );
			submenu = new JMenu( "Selection" );
				item = new JMenuItem( "select all" );
				item.setActionCommand( "@CLI:select all" );
				item.addActionListener( al );
				item.setAccelerator( 
						KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK ) );
				submenu.add( item );
				item = new JMenuItem( "unselect all" );
				item.setActionCommand( "@CLI:unselect all" );
				item.addActionListener( al );
				item.setAccelerator( 
						KeyStroke.getKeyStroke(KeyEvent.VK_A, 
								KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK ) );
				submenu.add( item );
			menu.add( submenu );
			item = new JMenuItem( "Copy" );
				item.setActionCommand( "@CLI:copy" );
				item.addActionListener( al );
				item.setAccelerator( 
						KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK ) );
				menu.add( item );
			item = new JMenuItem( "Cut" );
				item.setActionCommand( "@CLI:cut" );
				item.addActionListener( al );
				item.setAccelerator( 
						KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK ) );
				menu.add( item );
			item = new JMenuItem( "Paste" );
				item.setActionCommand( "@CLI:paste" );
				item.addActionListener( al );
				item.setAccelerator( 
						KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK ) );
				menu.add( item );
			menu.addSeparator();
			item = new JMenuItem( "Options" );
				item.setActionCommand( "options" );
				item.addActionListener( al );
				menu.add( item );
		menu = new JMenu( "Help" ); bar.add( menu );
			item = new JMenuItem( "Manual" );
				item.setActionCommand( "menu.help.manual" );
				item.addActionListener( al );
				menu.add( item );
			item = new JMenuItem( "Tutorials" );
				item.setActionCommand( "menu.help.tutorials" );
				item.addActionListener( al );
				menu.add( item );
			item = new JMenuItem( "About" );
				item.setActionCommand( "menu.help.about" );
				item.addActionListener( al );
				menu.add( item );
	}
	
	public WorkbenchMenuBar( ActionListener listener )
	{
		super();
		
		setPreferredSize( new Dimension( 200, 25 ) );
		
		installItems( this, listener );
	}
}
