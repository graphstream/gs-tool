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
package org.miv.graphstream.tool.workbench.cli;

import org.miv.graphstream.tool.workbench.Context;
import org.miv.graphstream.tool.workbench.WCore;
import org.miv.graphstream.tool.workbench.event.ContextEvent;
import org.miv.graphstream.tool.workbench.event.ContextChangeListener;

import java.util.LinkedList;

/**
 * CLI will be used to act on Context.
 * It can interpret String command to modify the context.
 * 
 * @author Guilhelm Savin
 *
 */
public class CLI implements ContextChangeListener
{
	/**
	 * All known commands.
	 */
	protected static final LinkedList<CLICommand> commands = new LinkedList<CLICommand>();
	/**
	 * Register a new command.
	 * 
	 * @param cc a new command
	 */
	protected static final void registerCommand( CLICommand cc )
	{
		commands.addLast( cc );
	}
	
	static
	{
		registerCommand( new HelpCommand() );
		// Not clean but actually needed
		new NodeOperations();
		new EdgeOperations();
		new GraphOperations();
		new SelectOperators();
		new ClipboardOperators();
		new ListOperators();
		new ElementOperators();
		new ForOperators();
		new HistoryOperators();
	}
	
	protected static final String PATTERN_ID = "[\\w\\d_-[.]]+";
	protected static final String PATTERN_KEY = "[\\w\\d_-[.]]+";
	protected static final String PATTERN_VAL = "(?:\".+\"|\\d+[.]\\d+f?|\\d+L?)";
	protected static final String PATTERN_ATTRIBUTES = "(?: " + PATTERN_KEY + "=" + PATTERN_VAL + ")*";
	protected static final String PATTERN_FILE = "[\\w\\d [.]-_/\\\\:]+";
	
	protected static final String CMD_HELP = "^help$";
	public static final String R_OK = "";
	protected static final String ERROR_PREFIX = "$ERROR$";
	protected static final String WARNING_PREFIX = "$WARNING$";
	
	public static final boolean isErrorMessage( String message )
	{
		return message.startsWith( ERROR_PREFIX );
	}
	
	public static final boolean isWarningMessage( String message )
	{
		return message.startsWith( WARNING_PREFIX );
	}
	
	public static final String getMessage( String message )
	{
		if( isErrorMessage( message ) )
			return message.substring( ERROR_PREFIX.length() );
		if( isWarningMessage( message ) )
			return message.substring( WARNING_PREFIX.length() );
		
		return message;
	}
	
	protected Context ctx;
	protected WCore core;
	
	public CLI( WCore core )
	{
		this.core = core;
		this.ctx  = null;
	}
	
	public String execute( String cmd )
	{
		cmd = cmd.trim();
		
		for( CLICommand c: commands )
		{
			if( c.isValid( cmd ) )
				return c.execute( this, cmd );
		}
		
		return CLICommand.createErrorMessage( "unknown command \"<i>" + cmd + "</i>\"" );
	}
	
	public WCore getCore()
	{
		return core;
	}
	
// Special commands
	
	static class HelpCommand extends CLICommand
	{
		public HelpCommand()
		{
			super( "^help$" );
			
			usage = "help";
		}
		
		@Override
		public String execute(CLI cli, String cmd)
		{
			StringBuffer buffer = new StringBuffer();
			
			for( CLICommand cc: CLI.commands )
			{
				buffer.append( cc.usage() );
				buffer.append( cc.usage().endsWith( "\n" ) ? "" : "\n");
			}
			return buffer.toString();
		}

	}
	
// ContextChangeListener implementation
	
	/**
	 * Called when the context changes.
	 * 
	 * @see org.miv.graphstream.tool.workbench.ContextChangeListener
	 * @param e the ContextChange event
	 */
	public void contextChanged(ContextEvent e)
	{
		this.ctx = e.getContext();
	}
}
