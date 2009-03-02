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
		normalMode,
		langChanged,
		clipboardCopy,
		clipboardCut,
		clipboardPaste,
		clipboardEmpty,
		historyUndo,
		historyRedo,
		historyUndoEnable,
		historyUndoDisable,
		historyRedoEnable,
		historyRedoDisable,
		selectionEmpty,
		selectionAdd,
		selectionRemove
	}
	
	void handleNotification( Notification notification );
}
