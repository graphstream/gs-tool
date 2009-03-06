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

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Node;

import org.miv.graphstream.tool.workbench.WCore;
import org.miv.graphstream.tool.workbench.WSelection;
import org.miv.graphstream.tool.workbench.event.ContextChangeListener;
import org.miv.graphstream.tool.workbench.event.ContextEvent;
import org.miv.graphstream.tool.workbench.event.SelectionListener;
import org.miv.graphstream.tool.workbench.event.SelectionEvent;

import java.util.Iterator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class WSelectionGUI extends JList 
	implements ContextChangeListener, SelectionListener
{
	public static final long serialVersionUID = 0x040190000001L;
	
	static class ElementRenderer
		extends JPanel
		implements ListCellRenderer
	{
		private static final long serialVersionUID = 0x040190010001L;

		JLabel 	icon;
		JLabel	title;
		JLabel	description;

		public ElementRenderer()
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

			c.insets = new Insets(-10,10,0,0);

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
	
	DefaultListModel 	model;
	
	public WSelectionGUI()
	{
		this.model = new DefaultListModel();
		
		WCore.getCore().addContextChangeListener( this );
		WCore.getCore().addSelectionListener(this);
		
		setModel( this.model );
		setCellRenderer( new ElementRenderer() );
		
		addMouseListener( new MouseAdapter()
		{
			public void mouseClicked( MouseEvent e )
			{
				if( e.getClickCount() == 2 )
				{
					new WElementInfo( (Element) getSelectedValue() );
				}
			}
		});
	}
	
	public void contextChanged( ContextEvent e )
	{
		if( WCore.getCore().getActiveContext() == null )
			return;
		
		WSelection s = WCore.getCore().getActiveContext().getSelection();
		
		Iterator<Element> ite = s.iterator();
		
		while( ite.hasNext() )
			addElement(ite.next());
	}
	
	protected void addElement( Element e )
	{
		if( ! model.contains(e) )
			model.addElement(e);
	}
	
	protected void removeElement( Element e )
	{
		model.removeElement(e);
	}
	
// SelectionListener implementation
	
	public void selectionAdd( SelectionEvent e )
	{
		if( e.getElement() != null )		
			addElement( e.getElement() );
	}
	
	public void selectionRemove( SelectionEvent e )
	{
		if( e.getElement() != null )		
			removeElement( e.getElement() );
	}
	
	public void selectionCleared( SelectionEvent e )
	{
		model.clear();
	}
}
