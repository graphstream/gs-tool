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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import org.miv.graphstream.tool.workbench.WCore;
import org.miv.graphstream.tool.workbench.WHistory;
import org.miv.graphstream.tool.workbench.WNotificationServer;
import org.miv.graphstream.tool.workbench.event.ContextChangeListener;
import org.miv.graphstream.tool.workbench.event.ContextEvent;
import org.miv.graphstream.tool.workbench.event.NotificationListener;

public class WHistoryGUI 
	extends JPanel
	implements NotificationListener, ContextChangeListener
{
	private static final long serialVersionUID = 0x0001L;
	
	static class HistoryActionRenderer
		extends JPanel
		implements ListCellRenderer
	{
		private static final long serialVersionUID = 0x0001L;
		
		JLabel 	icon;
		JLabel	title;
		JLabel	description;
		
		public HistoryActionRenderer()
		{
			icon 		= new JLabel();
			title 		= new JLabel();
			description = new JLabel();
			
			if( WFonts.hasFont("dialog:title") )
				title.setFont(WFonts.getFont("dialog:title"));
			
			if( WFonts.hasFont("dialog:infos") )
				description.setFont(WFonts.getFont("dialog:infos"));
			
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
			if( value instanceof WHistory.DelNodeHistoryAction )
			{
				icon.setIcon( WUtils.getImageIcon("action:node_del") );
				title.setText("Del node");
				description.setText(((WHistory.AbstractAddElementHistoryAction)value).getId());
			}
			else if( value instanceof WHistory.AddNodeHistoryAction )
			{
				icon.setIcon( WUtils.getImageIcon("action:node_add") );
				title.setText("Add node");
				description.setText(((WHistory.AbstractAddElementHistoryAction)value).getId());
			}
			else if( value instanceof WHistory.DelEdgeHistoryAction )
			{
				icon.setIcon( WUtils.getImageIcon("action:edge_del") );
				title.setText("Del edge");
				description.setText(((WHistory.AbstractAddElementHistoryAction)value).getId());
			}
			else if( value instanceof WHistory.AddEdgeHistoryAction )
			{
				icon.setIcon( WUtils.getImageIcon("action:edge_add") );
				title.setText("Add edge");
				description.setText(((WHistory.AbstractAddElementHistoryAction)value).getId());
			}
			else if( value instanceof WHistory.PasteHistoryAction )
			{
				icon.setIcon( WUtils.getImageIcon("edit:paste") );
				title.setText( WGetText.getText("menu:paste") );
				description.setText( String.format( "%d elements",
						((WHistory.PasteHistoryAction) value).size()) );
			}
			
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
	
	DefaultListModel model;
	JList list;
	
	public WHistoryGUI()
	{
		model = new DefaultListModel();
		
		list = new JList(model);
			list.setCellRenderer( new HistoryActionRenderer() );
		
		setPreferredSize( new Dimension( 200, 300 ) );
		setLayout( new BorderLayout() );
		add( new JScrollPane(list), BorderLayout.CENTER );
		
		WNotificationServer.connect(this);
		WCore.getCore().addContextChangeListener(this);
	}
	
	public void handleNotification( Notification n )
	{
		if( n == Notification.historyUndo )
		{
			model.remove(0);
		}
		else if( n == Notification.historyRedo )
		{
			model.add(0,WCore.getCore().getActiveContext().getHistory().getLast());
		}
		else if( n == Notification.historyNew )
		{
			model.add(0,WCore.getCore().getActiveContext().getHistory().getLast());
		}
	}
	
	public void contextChanged( ContextEvent ce )
	{
		model.clear();
		
		if( ce.getContext() == null )
			return;
		
		WHistory history = ce.getContext().getHistory();
		List<WHistory.HistoryAction> list = history.getHistory();
		
		for( int i = history.getIndex(); i < list.size(); i++ )
			model.addElement(list.get(i));
	}
}
