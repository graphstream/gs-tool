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

//import org.miv.graphstream.io.GraphParseException;
//import org.miv.graphstream.io.GraphReader;
//import org.miv.graphstream.io.GraphReaderFactory;
import org.miv.graphstream.tool.workbench.Context;

//import java.io.IOException;
import java.io.File;

public class GraphOperations extends CLICommandPool
{
	private static final String PATTERN = "^((display|open|select|save|list|create|save) graph|(disable|enable) graph autolayout)(.*)$";

	protected static final String CMD_CREATE_GRAPH = "^create graph(?: \"(" + PATTERN_ID + ")\")?(?: use class (\\w+(?:[.]\\w+)*))?(" + PATTERN_ATTRIBUTES + ")?(?: set file \"(.*)\"( overwrite)?)?( and display(.*)?)?$";
	protected static final String CMD_DISPLAY_GRAPH = "^display graph(?: (?:\"(" + PATTERN_ID + ")\"|(\\d+)))?( autolayout)?$";
	protected static final String CMD_LIST_GRAPH = "^list(?: of)? graph(s)?$";
	protected static final String CMD_SELECT_GRAPH = "^select graph (?:(\\d+)|\"(" + PATTERN_ID + ")\")$";
	protected static final String CMD_OPEN_GRAPH = "^open graph \"(" + PATTERN_FILE + ")\"(?: using reader (\\w+(?:[.]\\w+)*))?(?: with id \"(" + PATTERN_ID + ")\")?( and display( .*)?)?";
	protected static final String CMD_SAVE_GRAPH = "^save graph(?: (?:\"(" + PATTERN_ID + ")\"|(\\d+)))?(?: to \"(" + PATTERN_FILE + ")\"( (?:ow|overwrite))?)?$";
	protected static final String CMD_AUTOLAYOUT = "^(enable|disable) graph autolayout(?: (?:\"(" + PATTERN_ID + ")\"|(\\d+)))?$";
	
	static
	{
		CLI.registerCommand( new GraphOperations() );
	}
	
	protected GraphOperations()
	{
		super( PATTERN );
		
		subcommands.add( new CreateCommand() );
		subcommands.add( new OpenCommand() );
		subcommands.add( new DisplayCommand() );
		subcommands.add( new ListCommand() );
		subcommands.add( new SelectCommand() );
		subcommands.add( new AutoLayoutCommand() );
		subcommands.add( new SaveCommand() );
	}
	
	protected String _execute_( CLI cli, String cmd )
	{
		return execute( cli, cmd );
	}


	/**
	 * Call when match a CMD_CREATE_GRAPH command.
	 * 
	 * @param cmd command
	 * @return <i>R_OK</i> if all is ok
	 */
	class CreateCommand extends CLICommand
	{
		public CreateCommand()
		{
			super( CMD_CREATE_GRAPH );
			
			attributes.put( "id", 1 );
			attributes.put( "class", 2 );
			attributes.put( "attributes", 3 );
			attributes.put( "file", 4 );
			attributes.put( "overwrite", 5 );
			attributes.put( "display", 6 );
			attributes.put( "displayopts", 7 );
			
			usage = "create graph \"<i>id</i>\" [use class <i>binary.class</i>] [attributes] [set file \"<i>path</i>\" [overwrite]] [and display]";
		}
		
		public String execute( CLI cli, String cmd )
		{
			CLICommandResult ccr = result( cmd );
			if( ! ccr.isValid() ) return usage();
		
			String r = R_OK;
			
			Context ctx = null;
			if( ! ccr.hasAttribute( "class" ) )
			{
				if( ccr.hasAttribute( "id" ) )
					ctx = cli.core.createNewGraph( ccr.getAttribute( "id" ) );
				else
					ctx = cli.core.createNewGraph();
			}
			else
			{
				if( ccr.hasAttribute( "id" ) )
					ctx = cli.core.createNewGraph( ccr.getAttribute( "id" ), ccr.getAttribute( "class" ) );
				else
					ctx = cli.core.createNewGraph( ccr.getAttribute( "class" ), true );
			}
			
			if( ccr.hasAttribute( "attributes" ) )
				fillAttributes( ctx.getGraph(), ccr.getAttribute( "attributes" ) );
			
			if( ccr.hasAttribute( "file" ) )
			{
				File f = new File( ccr.getAttribute( "file" ) );
				if( ! f.exists() || ccr.hasAttribute( "overwrite" ) )
					ctx.setDefaultFile( ccr.getAttribute( "file" ) );
				else
				{
					if( CLI.isWarningMessage( r ) )
						r += ", " + "file exists (use \"overwrite\" option)";
					else if( ! CLI.isErrorMessage( r ) )
						r = createWarningMessage( "file exists (use \"overwrite\" option)" );
				}
			}
			
			if( ccr.hasAttribute( "display" ) )
				_execute_( cli, "display graph \"" + ctx.getGraph().getId() + "\"" +
						( ccr.hasAttribute( "displayopts" ) ? ccr.getAttribute( "displayopts" ) : "" ) );
		
			return r;
		}
	}
	/**
	 * Open a graph.
	 * 
	 * @param cmd
	 * @return
	 */
	class OpenCommand extends CLICommand
	{
		public OpenCommand()
		{
			super( CMD_OPEN_GRAPH );
			
			attributes.put( "file", 1 );
			attributes.put( "reader", 2 );
			attributes.put( "id", 3 );
			attributes.put( "display", 4 );
			attributes.put( "display attributes", 5 );
			
			usage = "open graph \"<i>file</i>\" [using reader <i>class</i>] [with id \"<i>id</i>\"] [and display]";
		}
		
		public String execute( CLI cli, String cmd )
		{
			CLICommandResult ccr = result( cmd );
			if( ! ccr.isValid() ) return usage();
		
			File f = new File( ccr.getAttribute( "file" ) );
		
			String id, error;
			if( ccr.hasAttribute( "id" ) ) 	id = ccr.getAttribute( "id" );
			else							id = f.getName();
			
			if( CLI.isErrorMessage( error = _execute_( cli, "create graph \"" + id + "\"" ) ) )
				return error;
			
			Context ctx = cli.core.getContext(id);
			if( ctx == null ) return createErrorMessage( "context \"" + 
					id + "\" not created" );
			
			ctx.readGraph(f.getAbsolutePath(),ccr.getAttribute( "reader" ));
			
			if( ccr.hasAttribute( "display" ) )
				_execute_( cli, "display graph \"" + id + "\"" 
						+ ( ccr.hasAttribute( "display attributes" ) ? 
							ccr.getAttribute( "display attributes" ) : "" ) );
		
			return R_OK;
		}
	}
	/**
	 * Display graph.
	 * 
	 * @param cmd command
	 * @return <i>R_OK</i> if all is ok
	 */
	class DisplayCommand extends CLICommand
	{
		public DisplayCommand()
		{
			super( CMD_DISPLAY_GRAPH );

			attributes.put( "label", 1 );
			attributes.put( "id", 2 );
			attributes.put( "autolayout", 3 );
			
			usage = "display graph [<i>index</i>|\"<i>id</i>\"] [autolayout]";
		}
		
		public String execute( CLI cli, String cmd )
		{
			CLICommandResult ccr = result( cmd );
			if( ! ccr.isValid() ) return usage();
		
			Context ctx = null;
			
			if( ! ccr.hasAttribute( "id" ) && ! ccr.hasAttribute( "label" ) )
				ctx = cli.getCore().getActiveContext();
			else
			{
				if( ccr.hasAttribute( "label" ) )
					ctx = cli.core.getContext( ccr.getAttribute( "label" ) );
				else
					ctx = cli.core.getContext( new Integer(ccr.getAttribute( "id" ) ) );
			}
			
			if( ctx == null ) return createErrorMessage( "no context found" );
			
			ctx.setAutolayout( ccr.hasAttribute( "autolayout" ) );
			cli.getCore().showContext( ctx );
			
			return R_OK;
		}
	}
	/**
	 * Return the list of all graphs.
	 * 
	 * @param cmd
	 * @return
	 */
	class ListCommand extends CLICommand
	{
		public ListCommand()
		{
			super( CMD_LIST_GRAPH );
			
			usage = "list [of] graph[s]";
		}
		
		public String execute( CLI cli, String cmd )
		{
			StringBuffer buffer = new StringBuffer();
			int size = (int) Math.log10( cli.core.getContextCount() );
		
			String format = size < 1 ? "%d" : "%0" + size + "d";
		
			for( int i=0; i<cli.core.getContextCount(); i++ )
			{
				buffer.append( String.format( format, i ) ).append(": ").append( cli.core.getContextId(i) );
				if( cli.core.getActiveContextNumber() == i ) buffer.append( " *" );
				buffer.append( "\n" );
			}
		
			return buffer.toString();
		}
	}
	/**
	 * Select a graph as active graph.
	 * 
	 * @author Guilhelm Savin
	 * 
	 */
	class SelectCommand extends CLICommand
	{
		public SelectCommand()
		{
			super( CMD_SELECT_GRAPH );
			
			attributes.put( "n", 1 );
			attributes.put( "id", 2 );
			
			usage = "select graph [<i>index</i>|\"<i>id</i>\"]";
		}
		
		public String execute( CLI cli, String cmd )
		{
			CLICommandResult ccr = result( cmd );
			if( ! ccr.isValid() ) return usage();
			
			if( ccr.hasAttribute( "id" ) )
			{
				cli.core.selectContext( ccr.getAttribute( "id" ) );
			}
			else
			{
				cli.core.selectContext( new Integer( ccr.getAttribute( "n" ) ) );
			}
		
			return R_OK;
		}
	}
	
	class SaveCommand extends CLICommand
	{
		public SaveCommand()
		{
			super( CMD_SAVE_GRAPH );
			
			attributes.put( "id", 1 );
			attributes.put( "index", 2 );
			attributes.put( "file", 3 );
			attributes.put( "overwrite", 4 );
			
			usage = "save graph [\"<i>id</i>\"|<i>index</i>] [to \"<i>path</i>\" [overwrite]]";
		}
		
		public String execute( CLI cli, String cmd )
		{
			CLICommandResult ccr = result( cmd );
			if( ! ccr.isValid() ) return usage();
			
			Context ctx = null;
			
			if( ! ccr.hasAttribute( "id" ) && ! ccr.hasAttribute( "index" ) )
				ctx = cli.getCore().getActiveContext();
			else
			{
				if( ccr.hasAttribute( "id" ) )
					ctx = cli.core.getContext( ccr.getAttribute( "id" ) );
				else
					ctx = cli.core.getContext( new Integer(ccr.getAttribute( "index" ) ) );
			}
			
			if( ctx == null ) return createErrorMessage( "no context" );
			
			if( ! ccr.hasAttribute( "file" ) && ctx.getDefaultFile() == null )
				return createErrorMessage( "no file for this context" );
			
			if( ccr.hasAttribute( "file" ) )
			{
				if( ! ccr.getAttribute( "file" ).equals( ctx.getDefaultFile() ) )
				{
					File f = new File( ccr.getAttribute( "file" ) );
					if( f.exists() && ! ccr.hasAttribute( "overwrite" ) )
						return createWarningMessage( "file exists, use \"<i>overwrite</i>\"" );
					
					ctx.setDefaultFile( f.getAbsolutePath() );
				}
			}
			
			cli.getCore().saveContext( ctx );
			
			return R_OK;
		}
	}
	
	class AutoLayoutCommand extends CLICommand
	{
		public AutoLayoutCommand()
		{
			super( CMD_AUTOLAYOUT );
			
			attributes.put( "action", 1 );
			attributes.put( "id" , 2 );
			attributes.put( "index", 3 );
			
			usage = "{enable|disable} graph autolayout {\"<i>id</i>\"|<i>index</i>}";
		}
		
		public String execute( CLI cli, String cmd )
		{
			CLICommandResult ccr = result( cmd );
			if( ! ccr.isValid() ) return usage();
			
			Context ctx = null;
			
			if( ! ccr.hasAttribute( "id" ) && ! ccr.hasAttribute( "index" ) )
				ctx = cli.getCore().getActiveContext();
			else
			{
				if( ccr.hasAttribute( "id" ) )
					ctx = cli.core.getContext( ccr.getAttribute( "id" ) );
				else
					ctx = cli.core.getContext( new Integer(ccr.getAttribute( "index" ) ) );
			}
			
			if( ctx == null ) return createErrorMessage( "no context found" );
			ctx.setAutolayout( ccr.getAttribute( "action" ).equals( "enable" ) );
			
			return R_OK;
		}
	}
}
