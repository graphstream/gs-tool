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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class WAlgorithmManager
	implements ActionListener
{
	private static WMenuBar menuBar = null;
	
	static final void init( WGui gui )
	{
		menuBar = gui.getWMenuBar();
	}
	
	static final void registerAlgorithm( WAlgorithmGUI wag )
	{
		new WAlgorithmManager(wag);
	}
	
	private WAlgorithmGUI managedDialog;
	private JMenuItem associatedMenuEntry;
	
	private WAlgorithmManager( WAlgorithmGUI wag )
	{
		this.managedDialog = wag;
		
		this.associatedMenuEntry = new JMenuItem(wag.getAlgorithm().getName(),WAlgorithmGUI.ALGORITHM_ICON);
		
		JComponent menu = null;

		if( wag.getAlgorithm().getCategory() == null ||
				wag.getAlgorithm().getCategory().equals("") ||
				wag.getAlgorithm().getCategory().equals("default") )
		{
			menu = menuBar.getRegisteredComponent("algorithm");
		}
		else
		{
			menu = menuBar.getRegisteredComponent("algorithm:" + wag.getAlgorithm().getCategory() );
			if( menu == null )
				menu = menuBar.getRegisteredComponent("algorithm");
		}
		
		associatedMenuEntry.addActionListener(this);
		((JMenu) menu).add(associatedMenuEntry);
	}
	
	public void actionPerformed( ActionEvent e )
	{
		managedDialog.setVisible(true);
	}
}
