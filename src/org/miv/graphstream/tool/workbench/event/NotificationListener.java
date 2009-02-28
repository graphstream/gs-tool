package org.miv.graphstream.tool.workbench.event;

public interface NotificationListener
{
	public static enum Notification
	{
		contextChanged,
		contextAdded,
		contextRemoved,
		contextShow,
		contextAutolayoutChanged,
		contextElementOperation,
		noContext,
		fullMode,
		normalMode
	}
	
	void handleNotification( Notification notification );
}
