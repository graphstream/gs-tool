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
package org.graphstream.tool.workbench.gui;


import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowEvent;

import java.io.StringReader;

import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextField;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.graphstream.tool.workbench.cli.CLI;

/**
 * A Swing interface to send command to a CLI.
 * 
 * @see org.graphstream.tool.workbench.cli.CLI
 * 
 * @author Guilhelm Savin
 *
 */
public class CLITerm extends JPanel 
	implements ActionListener, CaretListener, WindowFocusListener
{
	/**
	 * Serial version UID.
	 */
	public static final long serialVersionUID = 0x040010000001L;
	/**
	 * CLI object to send command.
	 */
	protected CLI cli;
	/**
	 * Panel used to display commands and results.
	 */
	protected JTextPane textPanel;
	/**
	 * Prompt used by user to enter command.
	 */
	protected JTextField textField;
	/**
	 * Prompt label.
	 */
	protected String prompt = "user@graphstream> ";
	/**
	 * Frame used by display.
	 */
	protected JFrame frame = null;
	/**
	 * Commands history.
	 */
	protected LinkedList<String> history = new LinkedList<String>();
	/**
	 * Position in history.
	 */
	protected int historyPosition = 0;
	
	protected int closeAction = JFrame.EXIT_ON_CLOSE;
	
	protected HTMLEditorKit editor = new HTMLEditorKit();
	
	protected HTMLDocument doc;
	
	protected Font font;
	
	/**
	 * Create a new CLI terminal.
	 * 
	 * @param cli CLI
	 */
	public CLITerm( CLI cli )
	{
		this.cli = cli;
		if( history.size() == 0 ) history.add( "" );
		
		this.font= new Font( "Monospaced", Font.PLAIN, 12 );
		
		buildTerm();
	}
	/**
	 * Build the GUI.
	 *
	 */
	protected void buildTerm()
	{
		JScrollPane scrollPane;
		
		setPreferredSize( new Dimension( 600, 250 ) );
		
		textPanel = new JTextPane();
		textField = new JTextField( prompt );
		
		setLayout( new BorderLayout() );
		
		scrollPane = new JScrollPane( textPanel );
		
		add( scrollPane, BorderLayout.CENTER );
		add( textField,  BorderLayout.SOUTH );
		
		textField.addActionListener( this );
		textField.addKeyListener( new UpDownPromptListener() );
		textField.addCaretListener( this );
		textField.setDocument( new PromptDocument() );
		textField.setBorder( null );
		textField.setFont( font );
		caretUpdate( null );
		
		if( WCss.hasStyleSheet("cliterm") )
			editor.setStyleSheet(WCss.getStyleSheet("cliterm"));
		
		doc = (HTMLDocument) editor.createDefaultDocument();
		
		textPanel.setEditorKit( editor );
		textPanel.setDocument( doc );
		StringReader in = new StringReader( 
				"<html>" +
				"<head></head>" +
				"<body id='cliterm'>" +
				"</body></html>" );
		try
		{
			editor.read( in, doc, 0 );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		textPanel.setEditable( false );
		textPanel.setFont( font );
	}
	/**
	 * Call when user hit the 'return' case.
	 */
	public void actionPerformed( ActionEvent e )
	{
		if( e.getSource() == textField )
		{
			String cmd = textField.getText().substring( prompt.length() );
			String r = "", spe = "";
			if( cli != null ) r = cli.execute( cmd );
			
			resetPrompt("");
			
			if( r.length() > 0 ) r += "\n";
			if( CLI.isErrorMessage( r ) )
				spe = " class='error'";
			else if( CLI.isWarningMessage( r ) )
				spe = " class='warning'";
			r = CLI.getMessage( r );
			r = r.replaceAll( "\n", "<br/>" );
			try
			{
				Element root = doc.getElement( "cliterm" );
				doc.insertBeforeEnd( root, "<div class='entry'>" +
						"<span class='command'>&gt; " + cmd + "</span><br/>" + 
						"<span" + spe + ">" + r + "</span></div>" );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
			
			history.add( 1, cmd );
			historyPosition = 0;
		}
	}
	/**
	 * Show the frame containing this CLI terminal.
	 *
	 */
	public void display()
	{
		if( frame == null )
		{
	        frame = new JFrame( "GraphStream CLI Terminal" );
			frame.add( this );
			frame.pack();
			frame.setIconImage( WUtils.getImageIcon( "system:terminal" ).getImage() );
			frame.setDefaultCloseOperation( closeAction );
			frame.addWindowFocusListener( this );
		}
		
		frame.setVisible( true );
	}
	/**
	 * CaretListener implementation.
	 * Used to control text display in prompt.
	 */
	public void caretUpdate( CaretEvent e )
	{
		if( textField.getCaretPosition() < prompt.length() )
		{
			if( textField.getText().length() < prompt.length() )
				textField.setText( prompt );
			textField.setCaretPosition( prompt.length() );
		}
	}
	
	public void setCloseAction( int ca )
	{
		this.closeAction = ca;
		if( frame != null ) frame.setDefaultCloseOperation( ca );
	}
	
	public int getCloseAction()
	{
		return closeAction;
	}
	/**
	 * Reset the prompt.
	 * 
	 * @param val content of the prompt
	 */
	protected void resetPrompt( String val )
	{
		try
		{
			textField.getDocument().remove( prompt.length(), textField.getDocument().getLength()-prompt.length() );
			textField.getDocument().insertString( prompt.length(), val, null );
		}
		catch( BadLocationException ex )
		{
			ex.printStackTrace();
		}
	}
	
// WindowFocusListener implementation
	
	public void windowGainedFocus(WindowEvent e)
	{
		textField.requestFocus();
	}
	
	public void windowLostFocus(WindowEvent e) 
	{
		
	}
	
	
// Stuff
	
	/**
	 * Document used in the prompt.
	 * 
	 * @author Guilhelm Savin
	 */
	class PromptDocument 
		extends PlainDocument
	{
		public static final long serialVersionUID = 0x040010010001L;
		
		public void remove( int off, int len ) throws BadLocationException
		{
			if( off >= prompt.length() ) super.remove( off, len );
			else if( len > prompt.length() ) super.remove( prompt.length(), len-prompt.length()+off );
		}
	}
	/**
	 * Catch 'up' and 'down' key pressed to navigate in history.
	 * 
	 * @author Guilhelm Savin
	 *
	 */
	class UpDownPromptListener
		extends KeyAdapter
	{
		public UpDownPromptListener()
		{
		}
		
		public void keyPressed( KeyEvent e )
		{
			if( e.getKeyCode() == KeyEvent.VK_DOWN )
			{
				historyPosition = Math.max( 0, historyPosition-1 );
				updatePrompt();
			}
			else if( e.getKeyCode() == KeyEvent.VK_UP )
			{
				historyPosition = Math.min( history.size()-1, historyPosition+1 );
				updatePrompt();
			}
		}
		
		protected void updatePrompt()
		{
			if( history.size() > 0 && historyPosition != -1 )
			{
				resetPrompt( history.get( historyPosition ) );
			}
		}
	}
	
// Used for tests
	
	public static void main( String [] args )
	{
		CLITerm term = new CLITerm( null );
		term.display();
	}
}
