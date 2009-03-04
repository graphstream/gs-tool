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

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class WDialog
	extends JDialog
{
	private static final long serialVersionUID = 0x0001L;
	
	private JComponent content;
	
	public WDialog( JFrame parent, String name, JComponent content )
	{
		super(parent);
		
		this.content = content;
		
		setTitle(name);
		setName(name);
		
		content.setBorder( BorderFactory.createLoweredBevelBorder() );
		
		add(content);
		
		pack();
		
		WDialogManager.registerDialog(this);
	}
	
	public JComponent getContent()
	{
		return content;
	}
}
