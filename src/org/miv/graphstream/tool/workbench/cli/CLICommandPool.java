/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.tool.workbench.cli;

import java.util.LinkedList;

/**
 * A special command which can contains other subcommands.
 * 
 * @author Guilhelm Savin
 *
 */
public abstract class CLICommandPool extends CLICommand
{
	protected String fullPattern;
	protected LinkedList<CLICommand> subcommands = new LinkedList<CLICommand>();
	
	public CLICommandPool( String fullPattern )
	{
		super( fullPattern );
	}
	
	@Override
	public String execute(CLI cli, String cmd)
	{
		for( CLICommand c: subcommands )
		{
			if( c.isValid( cmd ) )
				return c.execute( cli, cmd );
		}
		
		return createErrorMessage( "unknown command in pool" );
	}

	public String usage()
	{
		StringBuffer buffer = new StringBuffer();
		String sep = "";
		for( CLICommand c: subcommands )
		{
			buffer.append( sep ).append( c.usage() );
			sep = "\n";
		}
		
		return buffer.toString();
	}
}
