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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.tool.workbench.WCore;

/**
 * 
 *
 */
public class WSearch
	extends JDialog
	implements ActionListener
{
	private static final long serialVersionUID = 0x040180000001L;
	
	private static final WSearch searchModule = new WSearch();
	
	public static boolean init()
	{
		return searchModule != null;
	}
	
	public static void showSearchModule()
	{
		searchModule.setVisible(true);
	}
	
	static class SearchResultRenderer
		extends JPanel
		implements ListCellRenderer
	{
		private static final long serialVersionUID = 0x040180010001L;

		JLabel 	icon;
		JLabel	title;
		JLabel	description;

		public SearchResultRenderer()
		{
			icon 		= new JLabel();
			title 		= new JLabel();
			description = new JLabel();

			title.setFont( title.getFont().deriveFont(Font.BOLD,14.0f) );
			description.setFont( description.getFont().deriveFont(Font.PLAIN,12.0f) );

			description.setForeground(description.getForeground().brighter().brighter());

			GridBagLayout bag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();

			setLayout( bag );

			c.anchor = GridBagConstraints.WEST;
			c.gridwidth = 1;
			c.gridheight = 2;
			c.weighty = 1.0;

			icon.setPreferredSize( new Dimension(32,32) );
			bag.setConstraints(icon,c);
			add(icon);

			c.fill = GridBagConstraints.BOTH;

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridheight = 1;
			c.weighty = 0.0;
			c.weightx = 3.0;

			bag.setConstraints(title,c);
			add(title);

			c.insets = new Insets(-10,5,0,0);

			bag.setConstraints(description,c);
			add(description);
		}

		public Component getListCellRendererComponent( JList list, Object value,
				int index, boolean isSelected, boolean hasFocus )
		{
			description.setText(value.getClass().getCanonicalName());
			
			if( value instanceof Element )
				title.setText(((Element)value).getId());
			
			if( value instanceof Node )
				icon.setIcon( WUtils.getImageIcon("node") );
			else if( value instanceof Edge )
				icon.setIcon( WUtils.getImageIcon("edge") );

			if(isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			return this;
		}
	}
	
	JTextField			pattern;
	DefaultListModel 	model;
	JList 				list;
	JCheckBox			isRegularExpression;
	JCheckBox			searchNodes;
	JCheckBox			searchEdges;
	
	private WSearch()
	{
		setTitle(WGetText.getText("edit:find"));
		
		GridBagLayout bag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		Insets nullInsets = new Insets(0,0,0,0);
		
		setLayout(bag);
		//setResizable(false);
		
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		JLabel label = new JLabel( WGetText.getText("edit:find") );
		c.weightx = 0;
		c.insets = new Insets(0,10,0,10);
		bag.setConstraints(label,c);
		add(label);
		
		pattern = new JTextField(20);
		c.insets = nullInsets;
		c.weightx = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		bag.setConstraints(pattern,c);
		add(pattern);
		
		c.weightx = 0;
		c.gridx = 1;
		c.gridy++;
		c.gridwidth = GridBagConstraints.REMAINDER;
		isRegularExpression = new JCheckBox(WGetText.getText("regex"),true);
		bag.setConstraints(isRegularExpression,c);
		add(isRegularExpression);

		c.gridx = 0;
		c.gridwidth = GridBagConstraints.RELATIVE;
		searchNodes = new JCheckBox(WGetText.getText("nodes"),true);
		bag.setConstraints(searchNodes,c);
		add(searchNodes);

		JButton button = new JButton("search");
		button.setActionCommand("search");
		button.addActionListener(this);
		c.gridx = 1;
		c.gridy++;
		c.gridwidth = GridBagConstraints.REMAINDER;
		bag.setConstraints(button,c);
		add(button);
		
		c.gridx = 0;
		c.gridwidth = GridBagConstraints.RELATIVE;
		searchEdges = new JCheckBox(WGetText.getText("edges"),true);
		bag.setConstraints(searchEdges,c);
		add(searchEdges);
		
		model = new DefaultListModel();
		list = new JList(model);
		list.setCellRenderer(new SearchResultRenderer());
		list.setSelectionMode( javax.swing.ListSelectionModel.SINGLE_SELECTION );
		list.addMouseListener( new MouseAdapter()
		{
			public void mouseClicked( MouseEvent e )
			{
				if( e.getClickCount() == 2 )
				{
					new WElementInfo( (Element) list.getSelectedValue() );
				}
			}
		});
		JPanel panel = new JPanel();
		panel.setLayout( new BorderLayout() );
		panel.setPreferredSize( new java.awt.Dimension(500,100) );//bag.preferredLayoutSize(this) );
		panel.add( new JScrollPane(list), BorderLayout.CENTER );
		
		c.insets = new Insets(20,0,0,0);
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0;
		c.weighty = 2.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = GridBagConstraints.REMAINDER;
		bag.setConstraints(panel,c);
		add(panel);
		
		pack();
		
		WUtils.reloadOnLangChanged(this,"edit:find","setTitle");
		WUtils.reloadOnLangChanged(label,"edit:find","setTitle");
		WUtils.reloadOnLangChanged(searchNodes,"nodes","setText");
		WUtils.reloadOnLangChanged(searchEdges,"edges","setText");
		WUtils.reloadOnLangChanged(isRegularExpression,"regex","setText");
	}
	
	public void actionPerformed( ActionEvent e )
	{
		if( WCore.getCore().getActiveContext() == null )
		{
			WUtils.errorMessage(this, "No context" );
			return;
		}
		
		Iterator<? extends Element> ite;
		
		model.clear();
		
		if( searchNodes.isSelected() )
		{
			ite = WCore.getCore().getActiveContext().getGraph().getNodeIterator();
			search(ite);
		}
		
		if( searchEdges.isSelected() )
		{
			ite = WCore.getCore().getActiveContext().getGraph().getEdgeIterator();
			search(ite);
		}
	}
	
	protected void search( Iterator<? extends Element> ite )
	{
		while( ite.hasNext() )
		{
			Element e = ite.next();
			
			if( isRegularExpression.isSelected() )
			{
				if( e.getId().matches(pattern.getText()) )
					model.addElement(e);
			}
			else
			{
				if( e.getId().equals(pattern.getText()) )
					model.addElement(e);
			}
		}
	}
}
