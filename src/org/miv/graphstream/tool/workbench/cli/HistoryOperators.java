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

public class HistoryOperators
	extends CLICommand
{
	public static final String PATTERN = "^(undo|redo)$";
	
	static
	{
		CLI.registerCommand( new HistoryOperators() );
	}
	
	public HistoryOperators()
	{
		super(PATTERN);
		
		attributes.put( "action", 1 );
		
		usage = "undo | redo";
	}
	
	@Override
	public String execute(CLI cli, String cmd)
	{
		CLICommandResult ccr = result( cmd );
		
		if( ccr.isValid() )
		{
			if( ccr.getAttribute("action").equals("undo") )
				cli.getCore().getActiveContext().getHistory().undo();
			else
				cli.getCore().getActiveContext().getHistory().redo();
			
			return R_OK;
		}
		
		return "unvalid command";
	}

}
