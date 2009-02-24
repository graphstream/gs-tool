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

/**
 * This class defines the for commands.
 * It allows users to repeat easily some commands.
 * For example, create nodes with a generic id like "node#n" can be done by
 * executing command:
 * <pre>for $I from 0 to 100 do add node "leaf$I" ; add edge "link$I" "root" "leaf$I"</pre>
 * 
 * @author Guilhelm Savin
 *
 */
public class ForOperators extends CLICommandPool
{
	static
	{
		CLI.registerCommand( new ForOperators() );
	}
	
	protected static final String PATTERN = "^for .*$";
	protected static final String VARIABLE = "\\$\\w+[_-[.]\\d\\w]*";
	protected static final String CMD_FOR_EACH = 
		"^for each (" + VARIABLE + ") in \\{(.*)\\} do (.*)";
	protected static final String CMD_FOR =
		"^for (" + VARIABLE + ") from (\\d+) to (\\d+)(?: step (\\d+))? do (.*)$";
	
	public ForOperators()
	{
		super( PATTERN );
		
		subcommands.add( new ForEachCommand() );
		subcommands.add( new ForCommand() );
	}
	
	protected String _execute_( CLI cli, String cmd )
	{
		return execute( cli, cmd );
	}
	/**
	 * Used to execute a command with a set of values.
	 * 
	 * @author Guilhelm Savin
	 *
	 */
	class ForEachCommand extends CLICommand
	{
		public ForEachCommand()
		{
			super( CMD_FOR_EACH );
			
			attributes.put( "variable", 1 );
			attributes.put( "values", 2 );
			attributes.put( "command", 3 );
			
			usage = "for each <i>$VAR</i> in {<i>val0,val1,val2,...</i>} do cmd1 ; cmd2 ; ...";
		}
		
		public String execute( CLI cli, String cmd )
		{
			CLICommandResult ccr = result( cmd );
			if( ! ccr.isValid() ) return usage();
			
			String var = ccr.getAttribute( "variable" ).replace( "$", "\\$" );
			
			String [] values = ccr.getAttribute( "values" ).split( "," );
			String [] commands = ccr.getAttribute( "command" ).split( ";" );
			
			for( String val: values )
				for( String exe: commands )
					cli.execute( exe.trim().replaceAll( var, val ) );
			
			return R_OK;
		}
	}
	/**
	 * Used to execute a command for a start value to an end value.
	 * 
	 * @author Guilhelm Savin
	 *
	 */
	class ForCommand extends CLICommand
	{
		public ForCommand()
		{
			super( CMD_FOR );
			
			attributes.put( "variable", 1 );
			attributes.put( "from", 2 );
			attributes.put( "to", 3 );
			attributes.put( "step", 4 );
			attributes.put( "commands", 5 );
			
			usage = "for <i>$VAR</i> from <i>start</i> to <i>end</i> [step <i>x</i>] do cmd1 ; cmd 2 ; ...";
		}
		
		public String execute( CLI cli, String cmd )
		{
			CLICommandResult ccr = result( cmd );
			if( ! ccr.isValid() ) return usage();
			
			String var = ccr.getAttribute( "variable" ).replace( "$", "\\$" );
			String [] commands = ccr.getAttribute( "commands" ).split( ";" );
			Integer start = null, end = null, step = 1;
			try
			{
				start = new Integer( ccr.getAttribute( "from" ) );
				end   = new Integer( ccr.getAttribute( "to" ) );
				if( ccr.hasAttribute( "step" ) )
					step = new Integer( ccr.getAttribute( "step" ) );
			}
			catch( Exception e )
			{
				return createErrorMessage( 
						"'start', 'end' and 'step' must be an integer value\n" +
						e.getMessage() );
			}
			
			for( int i = start; i < end; i+=step )
			{
				for( String exe: commands )
					cli.execute( exe.trim().replaceAll( var, Integer.toString( i ) ) );
			}
			
			return R_OK;
		}
	}
}
