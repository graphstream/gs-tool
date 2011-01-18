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

import org.graphstream.tool.workbench.WCore;
import org.graphstream.tool.workbench.WNotificationServer;
import org.graphstream.tool.workbench.WCore.ActionMode;
import org.graphstream.tool.workbench.event.NotificationListener;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;


/**
 *
 */
public class WActions
{
	private static final ActionMap actions = new ActionMap();
	
	static void load()
	{
		actions.put( "file:new", 		new NewFileAction() );
		actions.put( "file:open", 		new OpenFileAction() );
		actions.put( "file:save", 		new SaveFileAction() );
		actions.put( "file:saveas", 	new SaveAsFileAction() );
		actions.put( "file:restore", 	new RestoreFileAction() );
		
		actions.put( "system:help", 	new HelpAction() );
		actions.put( "system:options", 	new OptionsAction() );
		actions.put( "system:terminal", new OpenTerminalAction() );
		actions.put( "system:exit", 	new ExitAction() );
		actions.put( "system:saveus", 	new SaveUserSettingsAction() );
		actions.put( "system:deleteus", new DeleteUserSettingsAction() );
		
		actions.put( "edit:copy", 		new CopyAction() );
		actions.put( "edit:cut", 		new CutAction() );
		actions.put( "edit:paste", 		new PasteAction() );
		actions.put( "edit:undo", 		new UndoAction() );
		actions.put( "edit:redo", 		new RedoAction() );
		actions.put( "edit:find", 		new FindAction() );
		
		actions.put( "action:nodeadd", 	new CoreAction("action:nodeadd",ActionMode.ADD_NODE) );
		actions.put( "action:nodedel", 	new CoreAction("action:nodedel",ActionMode.DEL_NODE) );
		actions.put( "action:edgeadd", 	new CoreAction("action:edgeadd",ActionMode.ADD_EDGE) );
		actions.put( "action:edgedel", 	new CoreAction("action:edgedel",ActionMode.DEL_EDGE) );
		actions.put( "action:select", 	new CoreAction("action:select",	ActionMode.SELECT) );
		actions.put( "action:info", 	new CoreAction("action:info",	ActionMode.INFO) );
		actions.put( "action:configure",new ConfigureAction() );
		
		actions.put( "help:manual",		new OpenLinkAction("help:manual","http:/graphstream.sourceforge.net/Manual.html") );
		actions.put( "help:tutorials",	new OpenLinkAction("help:tutorials","http:/graphstream.sourceforge.net/tutorials.html") );
		
		WNotificationServer.connect( new NotificationListener()
		{
			public void handleNotification( Notification n )
			{
				if( n == Notification.langChanged )
				{
					for( Object key : actions.keys() )
					{
						Action a = actions.get(key);
						
						if( a instanceof BaseAction )
							( (BaseAction) a ).init();
					}
				}
			}
		});
	}
	
	public static boolean hasAction( String key )
	{
		return actions.get(key) != null;
	}
	
	public static Action getAction( String key )
	{
		return actions.get(key);
	}
	
	public static ActionMap getMap()
	{
		return actions;
	}
	
	static abstract class BaseAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 0x040040010001L;
		
		String key;
		
		public BaseAction( String key )
		{
			this.key = key;
			
			init();
		}
		
		public void init()
		{
			putValue( Action.NAME, 				WGetText.getText( key ) );
			putValue( Action.SHORT_DESCRIPTION, WGetText.getText( "tooltip:" + key ) );
			putValue( Action.SMALL_ICON, 		WIcons.getIcon( key ) );
		}
	}
	
	static class CLIAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x040040020001L;
		
		String cmd;
		
		public CLIAction( String key, String cmd )
		{
			super(key);
			
			this.cmd = cmd;
		}
		
		public void actionPerformed( ActionEvent e )
		{
			WCore.getCore().getCLI().execute(cmd);
		}
	}
	
	static class NewFileAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x0400400B0001L;
		
		public NewFileAction()
		{
			super("file:new");
			putValue( Action.ACCELERATOR_KEY, 	KeyStroke.getKeyStroke( KeyEvent.VK_N, KeyEvent.CTRL_MASK ) );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			WUtils.newGraph( null, WCore.getCore().getCLI() );
		}
	}
	
	static class OpenFileAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x0400400C0001L;
		
		public OpenFileAction()
		{
			super("file:open");
			putValue( Action.ACCELERATOR_KEY, 	KeyStroke.getKeyStroke( KeyEvent.VK_O, KeyEvent.CTRL_MASK ) );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			WUtils.openGraph( null, WCore.getCore().getCLI() );
		}
	}
	
	static class SaveFileAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x040040140001L;
		
		public SaveFileAction()
		{
			super("file:save");
			putValue( Action.ACCELERATOR_KEY, 	KeyStroke.getKeyStroke( KeyEvent.VK_S, KeyEvent.CTRL_MASK ) );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			if( WCore.getCore().getActiveContext().getDefaultFile() == null )
				WUtils.selectFile(null,WCore.getCore().getActiveContext());
			
			WCore.getCore().saveContext();
			WUserSettings.newFileUsed(WCore.getCore().getActiveContext().getDefaultFile());
		}
	}
	
	static class SaveAsFileAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x040040130001L;
		
		public SaveAsFileAction()
		{
			super("file:saveas");
			putValue( Action.ACCELERATOR_KEY, 	KeyStroke.getKeyStroke( KeyEvent.VK_S, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK ) );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			WUtils.selectFile(null,WCore.getCore().getActiveContext());
			actions.get("file:save").actionPerformed(e);
		}
	}
	
	static class RestoreFileAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x040040120001L;
		
		public RestoreFileAction()
		{
			super("file:restore");
		}
		
		public void actionPerformed( ActionEvent e )
		{
			String file = WCore.getCore().getActiveContext().getDefaultFile();
			
			if( file != null )
				WCore.getCore().getActiveContext().readGraph(file,null);
		}
	}
	
	static class ExitAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x040040080001L;
		
		public ExitAction()
		{
			super("system:exit");
			putValue( Action.ACCELERATOR_KEY, 	KeyStroke.getKeyStroke( KeyEvent.VK_Q, KeyEvent.CTRL_MASK ) );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			WCore.getCore().exit();
		}
	}

	static class UndoAction
		extends CLIAction
	{
		private static final long serialVersionUID = 0x040040160001L;
		
		public UndoAction()
		{
			super("edit:undo","undo");
			putValue( Action.ACCELERATOR_KEY, 	KeyStroke.getKeyStroke( KeyEvent.VK_Z, KeyEvent.CTRL_MASK ) );
		}
	}

	static class RedoAction
		extends CLIAction
	{
		private static final long serialVersionUID = 0x040040110001L;
		
		public RedoAction()
		{
			super("edit:redo","redo");
			putValue( Action.ACCELERATOR_KEY, 	KeyStroke.getKeyStroke( KeyEvent.VK_Z, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK ) );
		}
	}
	
	static class CopyAction
		extends CLIAction
	{
		private static final long serialVersionUID = 0x040040040001L;
		
		public CopyAction()
		{
			super("edit:copy","copy");
			putValue( Action.ACCELERATOR_KEY, 	KeyStroke.getKeyStroke( KeyEvent.VK_C, KeyEvent.CTRL_MASK ) );
		}
	}

	static class CutAction
		extends CLIAction
	{
		private static final long serialVersionUID = 0x040040060001L;
		
		public CutAction()
		{
			super("edit:cut","cut");
			putValue( Action.ACCELERATOR_KEY, 	KeyStroke.getKeyStroke( KeyEvent.VK_X, KeyEvent.CTRL_MASK ) );
		}
	}

	static class PasteAction
		extends CLIAction
	{
		private static final long serialVersionUID = 0x040040100001L;
		
		public PasteAction()
		{
			super("edit:paste","paste");
			putValue( Action.ACCELERATOR_KEY, 	KeyStroke.getKeyStroke( KeyEvent.VK_V, KeyEvent.CTRL_MASK ) );
		}
	}

	static class FindAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x040040090001L;
		
		public FindAction()
		{
			super("edit:find");
			putValue( Action.ACCELERATOR_KEY, 	KeyStroke.getKeyStroke( KeyEvent.VK_F, KeyEvent.CTRL_MASK ) );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			WSearch.showSearchModule();
		}
	}
	
	static class OptionsAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x0400400F0001L;
		
		public OptionsAction()
		{
			super("system:options");
		}
		
		public void actionPerformed( ActionEvent e )
		{
			WOptions.showOptionsModule();
		}
	}
	
	static class HelpAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x0400400A0001L;

		public HelpAction()
		{
			super("system:help");
		}

		public void actionPerformed( ActionEvent e )
		{
			WHelp.showHelpModule();
		}
	}
	
	static class CoreAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x040040050001L;
		
		ActionMode mode;
		
		public CoreAction( String key, ActionMode mode )
		{
			super(key);
			this.mode = mode;
		}
		
		public void actionPerformed( ActionEvent e )
		{
			WCore.getCore().setActionMode(mode);
			
			for( Object oKey : actions.keys() )
			{
				Action a = actions.get(oKey);
				
				if( a != this && a instanceof CoreAction )
					a.setEnabled(true);
			}
			
			setEnabled(false);
		}
	}
	
	static class ConfigureAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x040040030001L;
		
		public ConfigureAction()
		{
			super("action:configure");
		}
		
		public void actionPerformed( ActionEvent e )
		{
			WActionAccessory.showAccessory();
		}
	}
	
	static class OpenTerminalAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x0400400E0001L;
		
		public OpenTerminalAction()
		{
			super("system:terminal");
		}
		
		public void actionPerformed( ActionEvent e )
		{
			WCore.getCore().openTerminal();
		}
	}
	
	static class OpenLinkAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x0400400D0001L;
		
		String link;
		
		public OpenLinkAction( String key, String link )
		{
			super(key);
			this.link = link;
		}
		
		public void actionPerformed( ActionEvent e )
		{
			JOptionPane.showMessageDialog(
					null, 
					WGetText.getText("content:visitlink") + " :\n" + link, 
					WGetText.getText("system:externallink"), 
					JOptionPane.INFORMATION_MESSAGE,
					WIcons.getIcon("system:link") );
		}
	}
	
	static class SaveUserSettingsAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x040040150001L;
		
		public SaveUserSettingsAction()
		{
			super("system:saveus");
		}
		
		public void actionPerformed( ActionEvent e )
		{
			WUserSettings.saveUserSettings();
		}
	}
	
	static class DeleteUserSettingsAction
		extends BaseAction
	{
		private static final long serialVersionUID = 0x040040070001L;
		
		public DeleteUserSettingsAction()
		{
			super("system:deleteus");
		}
		
		public void actionPerformed( ActionEvent e )
		{
			int r = JOptionPane.showConfirmDialog(
					null,
					"Delete user settings ?" );
			
			if( r == JOptionPane.YES_OPTION )
				WUserSettings.deleteUserSettings();
		}
	}
}
