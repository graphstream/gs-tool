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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class WAbout
	extends JDialog
{
	private static final long serialVersionUID = 0x0001L;
	
	private static final WAbout about = new WAbout();
	
	public static void whatAbout()
	{
		about.setVisible(true);
	}
	
	private WAbout()
	{
		setTitle( "About" );
		setLayout( new BorderLayout() );
		setResizable(false);
		setUndecorated(true);
		
		Icon splash = WUtils.getImageIcon("splash");
		JLabel splashLabel = new JLabel(splash);
		splashLabel.setBounds(0,0,splash.getIconWidth(),splash.getIconHeight());
		
		JLayeredPane layers = new JLayeredPane();
		layers.setPreferredSize(splashLabel.getPreferredSize());
		layers.add( splashLabel, new Integer(0) );
		
		JLabel institute = new JLabel( "LITIS, University of Le Havre (FR)" );
		institute.setFont( WUtils.getDefaultFont() );
		institute.setForeground( Color.WHITE );
		institute.setBounds( splash.getIconWidth() - 15 - institute.getPreferredSize().width, 15,
				institute.getPreferredSize().width,
				institute.getPreferredSize().height );
		
		layers.add( institute, new Integer(1) );
		
		JPanel autors = new JPanel();
		String [] autorsData = getAutors();
		autors.setLayout(new GridLayout(autorsData.length,1));
		autors.setOpaque(false);
		for( String a : autorsData )
		{
			JLabel al = new JLabel(a,JLabel.LEFT);
			al.setFont( WUtils.getDefaultFont() );
			al.setForeground( Color.WHITE );
			autors.add(al);
		}
		autors.setBounds( 15, 15, 
				(int) autors.getPreferredSize().getWidth(),
				(int) autors.getPreferredSize().getHeight() );
		
		layers.add( autors, new Integer(1) );
		
		JLabel copyrights = new JLabel( "Copyright (c) 2006 - 2009" );
		copyrights.setFont(WUtils.getDefaultFont());
		copyrights.setForeground( Color.WHITE );
		copyrights.setBounds( 15, splash.getIconHeight() - 15 - copyrights.getPreferredSize().height,
				copyrights.getPreferredSize().width, copyrights.getPreferredSize().height );
		layers.add( copyrights, new Integer(1) );
		
		add(layers,BorderLayout.CENTER);
		
		pack();
	}
	
	public String [] getAutors()
	{
		return 	new String [] {
				"Julien Baudry",
				"Antoine Dutot",
				"Yoann Pigné",
				"Guilhelm Savin" };
	}
}
