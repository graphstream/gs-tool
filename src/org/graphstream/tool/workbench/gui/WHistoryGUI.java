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

import org.graphstream.tool.workbench.WCore;
import org.graphstream.tool.workbench.WHistory;
import org.graphstream.tool.workbench.WNotificationServer;
import org.graphstream.tool.workbench.event.ContextChangeListener;
import org.graphstream.tool.workbench.event.ContextEvent;
import org.graphstream.tool.workbench.event.NotificationListener;

public class WHistoryGUI 
	extends JPanel
	implements NotificationListener, ContextChangeListener
{
	private static final long serialVersionUID = 0x040130000001L;
	
	static class HistoryActionRenderer
		extends JPanel
		implements ListCellRenderer
	{
		private static final long serialVersionUID = 0x040130010001L;
		
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
				icon.setIcon( WUtils.getImageIcon("action:nodedel") );
				title.setText("Del node");
				description.setText(((WHistory.AbstractAddElementHistoryAction)value).getId());
			}
			else if( value instanceof WHistory.AddNodeHistoryAction )
			{
				icon.setIcon( WUtils.getImageIcon("action:nodeadd") );
				title.setText("Add node");
				description.setText(((WHistory.AbstractAddElementHistoryAction)value).getId());
			}
			else if( value instanceof WHistory.DelEdgeHistoryAction )
			{
				icon.setIcon( WUtils.getImageIcon("action:edgedel") );
				title.setText("Del edge");
				description.setText(((WHistory.AbstractAddElementHistoryAction)value).getId());
			}
			else if( value instanceof WHistory.AddEdgeHistoryAction )
			{
				icon.setIcon( WUtils.getImageIcon("action:edgeadd") );
				title.setText("Add edge");
				description.setText(((WHistory.AbstractAddElementHistoryAction)value).getId());
			}
			else if( value instanceof WHistory.PasteHistoryAction )
			{
				icon.setIcon( WUtils.getImageIcon("edit:paste") );
				title.setText( WGetText.getText("edit:paste") );
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
