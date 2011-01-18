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

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.tool.workbench.WCore;
import org.graphstream.tool.workbench.WSelection;
import org.graphstream.tool.workbench.event.ContextChangeListener;
import org.graphstream.tool.workbench.event.ContextEvent;
import org.graphstream.tool.workbench.event.SelectionEvent;
import org.graphstream.tool.workbench.event.SelectionListener;


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
