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
package org.graphstream.tool.workbench;

import org.graphstream.tool.workbench.event.ContextChangeListener;
import org.graphstream.tool.workbench.event.ContextEvent;
import org.graphstream.tool.workbench.event.ContextListener;
import org.graphstream.tool.workbench.event.NotificationListener;
import org.graphstream.tool.workbench.event.WorkbenchListener;
import org.graphstream.tool.workbench.event.NotificationListener.Notification;

import java.util.concurrent.ConcurrentLinkedQueue;

public class WNotificationServer
	implements ContextChangeListener, ContextListener, WorkbenchListener
{
	private static ConcurrentLinkedQueue<NotificationListener> listeners =
		new ConcurrentLinkedQueue<NotificationListener>();
	private static final WNotificationServer server = new WNotificationServer();
	
	public static void connect( NotificationListener listener )
	{
		listeners.add(listener);
	}
	
	public static void disconnect( NotificationListener listener )
	{
		listeners.remove(listener);
	}
	
	public static void dispatch( NotificationListener.Notification notification )
	{
		for( NotificationListener listener : listeners )
			listener.handleNotification(notification);
	}
	
	public static void init( WCore core )
	{
		server.initInternal(core);
	}
	
	private WCore core = null;
	
	public WNotificationServer()
	{
	}
	
	private void initInternal( WCore core )
	{
		if( this.core != null )
		{
			this.core.removeContextChangeListener(this);
			this.core.removeContextListener(this);
			this.core.removeWorkbenchListener(this);
		}
		
		this.core = core;
		
		core.addContextChangeListener(this);
		core.addContextListener(this);
		core.addWorkbenchListener(this);
		
		if( core.getContextCount() == 0 )
			dispatch(Notification.noContext);
	}
	
	public void contextChanged( ContextEvent e )
	{
		dispatch(Notification.contextChanged);
	}
	
	public void contextAdded( ContextEvent e )
	{
		dispatch(Notification.contextAdded);
	}
	
	public void contextRemoved( ContextEvent e )
	{
		dispatch(Notification.contextRemoved);
		
		if( core.getContextCount() == 0 )
			dispatch( Notification.noContext );
	}
	
	public void contextShow( ContextEvent e )
	{
		dispatch( Notification.contextShow );
	}
	
	public void contextAutolayoutChanged( ContextEvent ce )
	{
		dispatch( Notification.contextAutolayoutChanged );
	}
	
	public void contextGraphOperation( ContextEvent ce,	GraphOperation op, Object data )
	{
		dispatch( Notification.contextGraphOperation );
	}
}
