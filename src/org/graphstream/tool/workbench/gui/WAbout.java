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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WAbout
	extends JDialog
{
	private static final long serialVersionUID = 0x040020000001L;
	
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
		setBackground(new Color(0,0,0));
		
		Icon splash = WUtils.getImageIcon("splash");
		JLabel splashLabel = new JLabel(splash);
		splashLabel.setBounds(0,0,splash.getIconWidth(),splash.getIconHeight());
		
		JPanel layers = new JPanel();
		layers.setLayout( new BorderLayout() );
		layers.setUI(null);
		
		layers.add( splashLabel, BorderLayout.CENTER );
		
		JPanel infos = new JPanel();
		infos.setOpaque(true);
		infos.setBackground(Color.BLACK);
		infos.setUI(null);
		infos.setLayout( new BorderLayout() );
		infos.setBorder( BorderFactory.createMatteBorder(0,10,10,10,Color.BLACK) );
		JLabel institute = new JLabel( "LITIS, University of Le Havre (FR)" );
		institute.setFont( WFonts.getFont("dialog:infos") );
		institute.setForeground(Color.GRAY);
		
		infos.add( institute, BorderLayout.NORTH );
		
		JPanel autors = new JPanel();
		autors.setUI(null);
		autors.setOpaque(true);
		autors.setBackground(new Color(0,0,0));
		String [] autorsData = getAutors();
		autors.setLayout(new GridLayout(autorsData.length,1));
		for( String a : autorsData )
		{
			JLabel al = new JLabel(a,JLabel.CENTER);
			al.setFont( WFonts.getFont("dialog:title") );
			al.setForeground( Color.WHITE );
			autors.add(al);
		}
		
		infos.add( autors, BorderLayout.CENTER );
		
		JLabel copyrights = new JLabel( "Copyright (c) 2006 - 2009", JLabel.RIGHT );
		copyrights.setFont(WFonts.getFont("dialog:infos"));
		copyrights.setForeground(Color.GRAY);
		infos.add( copyrights, BorderLayout.SOUTH);
		
		layers.add(infos,BorderLayout.SOUTH);
		
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
