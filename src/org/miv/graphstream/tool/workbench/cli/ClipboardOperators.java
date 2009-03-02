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
 * Defines the clipboard operators like copy, cut or paste
 * selection.
 * 
 * @author Guilhelm Savin
 *
 */
public class ClipboardOperators extends CLICommand
{
	public static final String PATTERN = "^(copy|cut|paste)$";
	
	static
	{
		CLI.registerCommand( new ClipboardOperators() );
	}
	
	public ClipboardOperators()
	{
		super( PATTERN );
		
		attributes.put( "action", 1 );
		
		usage = "{copy,cut,paste}";
	}
	
	@Override
	public String execute(CLI cli, String cmd)
	{
		CLICommandResult ccr = result( cmd );
		if( ! ccr.isValid() ) return usage();
		
		if( ccr.getAttribute( "action" ).equals( "copy" ) ) 
			cli.core.getClipboard().copy();
		else if( ccr.getAttribute( "action" ).equals( "cut" ) ) 
			cli.core.getClipboard().cut();
		else 
			cli.core.getClipboard().paste();
		
		return R_OK;
	}

}
