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
package org.graphstream.tool.workbench.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.KeyStroke;

/**
 * WDialogManager is a bridge between the WDialog object
 * and an associated menu entry.
 * 
 */
public final class WDialogManager
	implements ActionListener
{
	private static JMenu dialogMenu = null;
	private static int currentKey = 0;
	
	static final void init( WGui gui )
	{
		dialogMenu = (JMenu) gui.getWMenuBar().getRegisteredComponent("dialog");
	}
	
	static final void registerDialog( WDialog wd )
	{
		new WDialogManager(wd);
	}
	
	private WDialog managedDialog;
	private JCheckBoxMenuItem associatedMenuEntry;
	
	private WDialogManager( WDialog wd )
	{
		this.managedDialog = wd;
		this.associatedMenuEntry = new JCheckBoxMenuItem(wd.getName());
		
		if( currentKey < 10 )
		{
			try
			{
				associatedMenuEntry.setAccelerator( KeyStroke.getKeyStroke(
							Character.forDigit(currentKey++,10) ));
			}
			catch( Exception e )
			{
			}
		}
		
		associatedMenuEntry.addActionListener(this);
		dialogMenu.add(associatedMenuEntry);
		
		actionPerformed(null);
		
		wd.addWindowListener( new WindowAdapter()
		{
			public void windowClosing( WindowEvent we )
			{
				associatedMenuEntry.setSelected(false);
			}
			
			public void windowOpened( WindowEvent we )
			{
				associatedMenuEntry.setSelected(true);
			}
		});
	}
	
	public void actionPerformed( ActionEvent e )
	{
		managedDialog.setVisible(associatedMenuEntry.isSelected());
	}
}
