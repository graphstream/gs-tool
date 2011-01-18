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
