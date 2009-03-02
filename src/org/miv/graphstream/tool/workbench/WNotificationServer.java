package org.miv.graphstream.tool.workbench;


import org.miv.graphstream.graph.Element;
import org.miv.graphstream.tool.workbench.event.ContextListener;
import org.miv.graphstream.tool.workbench.event.ContextChangeListener;
import org.miv.graphstream.tool.workbench.event.ContextEvent;
import org.miv.graphstream.tool.workbench.event.NotificationListener;
import org.miv.graphstream.tool.workbench.event.ContextListener.ElementOperation;
import org.miv.graphstream.tool.workbench.event.NotificationListener.Notification;
import org.miv.graphstream.tool.workbench.event.WorkbenchListener;
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
	
	public void contextElementOperation( ContextEvent ce, Element e, 
			ElementOperation op, Object data )
	{
		dispatch( Notification.contextElementOperation );
	}
}
