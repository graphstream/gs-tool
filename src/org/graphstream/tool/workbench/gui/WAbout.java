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
