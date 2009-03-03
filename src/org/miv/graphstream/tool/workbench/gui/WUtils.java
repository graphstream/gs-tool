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
package org.miv.graphstream.tool.workbench.gui;

import org.miv.graphstream.tool.workbench.Context;
import org.miv.graphstream.tool.workbench.WNotificationServer;
import org.miv.graphstream.tool.workbench.cli.CLI;
import org.miv.graphstream.tool.workbench.cli.CLICommand;
import org.miv.graphstream.tool.workbench.event.NotificationListener;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.File;

import java.lang.reflect.Method;

import java.net.URL;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

/**
 * A set of static methods which allow 'Click &amp; Play' actions.
 * 
 * @author Guilhelm Savin
 *
 */
public class WUtils
{
	protected static final Map<String,ImageIcon> ICONS = new HashMap<String,ImageIcon>();
	/*
	 * Some code to load icons and store them in ICONS.
	 */
	static
	{
		String [][] iconsUrl =
		{
				{ "node", "org/miv/graphstream/tool/workbench/ressources/node32.png" },
				{ "edge", "org/miv/graphstream/tool/workbench/ressources/edge32.png" },
				{ "action:node_add", "org/miv/graphstream/tool/workbench/ressources/nodeadd32.png" },
				{ "action:node_del", "org/miv/graphstream/tool/workbench/ressources/nodedelete32.png" },
				{ "action:node_info", "org/miv/graphstream/tool/workbench/ressources/nodeinfo32.png" },
				{ "action:select",  "org/miv/graphstream/tool/workbench/ressources/select32.png"  },
				{ "action:edge_add", "org/miv/graphstream/tool/workbench/ressources/edgeadd32.png" },
				{ "action:edge_del", "org/miv/graphstream/tool/workbench/ressources/edgedelete32.png" },
				{ "action:edge_info", "org/miv/graphstream/tool/workbench/ressources/edgeinfo32.png" },
				{ "action:configure", "org/miv/graphstream/tool/workbench/ressources/configure32.png" },
				{ "term", "org/miv/graphstream/tool/workbench/ressources/term_32.png" },
				{ "gs_logo", "org/miv/graphstream/tool/workbench/ressources/gs_logo.png" },
				{ "key",  "org/miv/graphstream/tool/workbench/ressources/key_16.png"  },
				{ "file:new", "org/miv/graphstream/tool/workbench/ressources/filenew.png" },
				{ "file:open", "org/miv/graphstream/tool/workbench/ressources/fileopen.png" },
				{ "file:save", "org/miv/graphstream/tool/workbench/ressources/filesave.png" },
				{ "file:saveas", "org/miv/graphstream/tool/workbench/ressources/filesaveas.png" },
				{ "exit", "org/miv/graphstream/tool/workbench/ressources/exit.png" },
				{ "edit:undo", "org/miv/graphstream/tool/workbench/ressources/undo.png" },
				{ "edit:redo", "org/miv/graphstream/tool/workbench/ressources/redo.png" },
				{ "edit:copy", "org/miv/graphstream/tool/workbench/ressources/editcopy.png" },
				{ "edit:cut", "org/miv/graphstream/tool/workbench/ressources/editcut.png" },
				{ "edit:paste", "org/miv/graphstream/tool/workbench/ressources/editpaste.png" },
				{ "help", "org/miv/graphstream/tool/workbench/ressources/help.png" },
				{ "splash", "org/miv/graphstream/tool/workbench/ressources/splash.png" },
				{ "gears", "org/miv/graphstream/tool/workbench/ressources/gears32.png" }
		};
		
		for( int i = 0; i < iconsUrl.length; i++ )
		{
			URL url = ClassLoader.getSystemResource( iconsUrl [i][1] );
			if( url != null )
			{
				ImageIcon ii = new ImageIcon( url );
				if( ii != null )
					ICONS.put( iconsUrl [i][0], ii );
			}
		}
	}
	
	/**
	 * Get a workbench icon.
	 * 
	 * @param name name of the icon.
	 * @return icon associated to the name
	 */
	public static final ImageIcon getImageIcon( String name )
	{
		return ICONS.get( name );
	}
	/**
	 * Show an error message.
	 * 
	 * @param parent parent component
	 * @param message message to display
	 */
	public static void errorMessage( Component parent, String message )
	{
		JOptionPane.showMessageDialog(
				parent, message, "error", JOptionPane.ERROR_MESSAGE ); 
	}
	/**
	 * Show a warning message.
	 * 
	 * @param parent parent component
	 * @param message message to display
	 */
	public static void warningMessage( Component parent, String message )
	{
		JOptionPane.showMessageDialog(
				parent, message, "warning", JOptionPane.WARNING_MESSAGE ); 
	}
	/**
	 * Show an information message.
	 * 
	 * @param parent
	 * @param message
	 */
	public static void informationMessage( Component parent, String message )
	{
		JOptionPane.showMessageDialog(
				parent, message, "information", JOptionPane.INFORMATION_MESSAGE ); 
	}
	/**
	 * New graph wizard.
	 * 
	 * @param parent parent component
	 * @param cli CLI used
	 */
	public static void newGraph( Component parent, CLI cli )
	{
		NewGraphDialog ngd = new NewGraphDialog( parent, cli );
		ngd.setVisible( true );
	}
	/**
	 * Open a filechooser to select a file which will be read to 
	 * create a new context.
	 * 
	 * @param parent parent component
	 * @param cli CLI used
	 */
	public static void openGraph( Component parent, CLI cli )
	{
		OpenGraphFileChooser ogfc = new OpenGraphFileChooser( cli, parent );
		ogfc.run();
	}
	/**
	 * Open a file chooser to select a file and save the graph to it.
	 * 
	 * @param parent parent component
	 * @param cli CLI used
	 * @param ctx context containing graph to save
	 */
	public static void saveGraph( Component parent, CLI cli, Context ctx )
	{
		SaveGraphFileChooser sgfc = new SaveGraphFileChooser( cli, parent, ctx, true );
		sgfc.run();
	}
	/**
	 * Just set the file where graph will be saved later.
	 * 
	 * @param parent parent component
	 * @param ctx context which we want to select the output file
	 */
	public static void selectFile( Component parent, Context ctx )
	{
		SaveGraphFileChooser sgfc = new SaveGraphFileChooser( null, parent, ctx, false );
		sgfc.run();
	}
	
	public static void initGraphFileFilters( JFileChooser jfc )
	{
		jfc.addChoosableFileFilter( new ExtFileFilter( "[.]dgs", "Dynamic Graph Stream format (*.dgs)" ) );
		jfc.addChoosableFileFilter( new ExtFileFilter( "[.]dot", "GraphViz \"dot\" format (*.dot) " ) );
		jfc.addChoosableFileFilter( new ExtFileFilter( "[.](dot|dgs)", "Graph format (*.dgs,*.dot) " ) );
	}
	/**
	 * Return an automatic id based on the given format.
	 * This will replace "%n" occurence by a number depending on nodes count.
	 * 
	 * @param ctx
	 * @param format
	 * @return a valid node id
	 */
	public static String getAutomaticNodeId( Context ctx, String format )
	{
		int count = ctx.getGraph().getNodeCount();
		String id = null;

		if( format.contains( "%n" ) )
		{
			do
			{
				id = format.replaceFirst( "%n", Integer.toString( count++ ) );
			}
			while( ctx.getGraph().getNode(id) != null );
		}
		else return format;
		
		return id;
	}
	/**
	 * Return an automatic id based on the given format.
	 * This will replace "%n" occurence by a number depending on edges count.
	 * 
	 * @param ctx
	 * @param format
	 * @return a valid edge id
	 */
	public static String getAutomaticEdgeId( Context ctx, String format )
	{
		int count = ctx.getGraph().getEdgeCount();
		String id = null;
		
		if( format.contains( "%n" ) )
		{
			do
			{
				id = format.replaceFirst( "%n", Integer.toString( count++ ) );
			}
			while( ctx.getGraph().getEdge(id) != null );
		}
		else return format;
		
		return id;
	}
	/**
	 * Graphical way to select a pattern.
	 * 
	 * @param cli
	 */
	public static void selectPattern( Component parent, CLI cli )
	{
		SelectPatternDialog spd = new SelectPatternDialog( parent, cli );
		spd.setVisible( true );
	}
	
	private static Font defaultFont = null;
	private static String defaultFontName = "org/miv/graphstream/tool/workbench/ressources/AG-Stencil.ttf";
	
	public static Font getDefaultFont()
	{
		if( defaultFont == null )
		{
			try
			{
				defaultFont = Font.createFont(
					Font.TRUETYPE_FONT,
					ClassLoader.getSystemResourceAsStream(defaultFontName) ).deriveFont(16.0f);
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		
		return defaultFont;
	}
	
// Classes declarations	

	static class SelectPatternDialog extends JDialog
		implements ActionListener
	{
		public static final long serialVersionUID = 0x00A01901L;
		
		protected JCheckBox nodes, edges;
		protected JTextField pattern;
		protected CLI cli;
		
		public SelectPatternDialog( Component parent, CLI cli )
		{
			super( 
					(Frame) (parent == null ? null :
					parent instanceof Dialog ? (Dialog) parent :
					parent instanceof Frame ? (Frame) parent : null), "Select pattern" );
			
			this.cli = cli;
			
			setLayout( new GridLayout( 3, 1 ) );
			nodes = new JCheckBox( "nodes", true );
			edges = new JCheckBox( "edges", true );
			pattern = new JTextField( ".*", 20 );
			
			JPanel tmp = new JPanel();
			tmp.add( nodes );
			tmp.add( edges );
			add( tmp );
			
			tmp = new JPanel();
			tmp.add( new JLabel( "Pattern: " ) );
			tmp.add( pattern );
			add( tmp );
			
			JButton ok, cancel;
			ok = new JButton( "select" );
			ok.setActionCommand( "select.valid" );
			ok.addActionListener( this );
			cancel = new JButton( "cancel" );
			cancel.setActionCommand( "select.cancel" );
			cancel.addActionListener( this );
			tmp = new JPanel();
			tmp.add( ok );
			tmp.add( cancel );
			add( tmp );
			
			pack();
			setResizable( false );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			if( e == null ) return;
			else if( e.getActionCommand().equals( "select.cancel" ) )
				setVisible( false );
			else if( e.getActionCommand().equals( "select.valid" ) )
				select();
		}
		
		protected void select()
		{
			if( nodes.isSelected() || edges.isSelected() )
			{
				String cmd = "select all";
				
				if( ! ( nodes.isSelected() && edges.isSelected() ) )
				{
					if( nodes.isSelected() )
						cmd += " nodes";
					else
						cmd += " edges";
				}
				
				cmd += " \"" + pattern.getText() + "\"";
				
				String error = cli.execute( cmd );
				if( CLI.isErrorMessage( error ) )
					errorMessage( this, CLI.getMessage( error ) );
				else if( CLI.isWarningMessage( error ) )
					warningMessage( this, CLI.getMessage( error ) );
			}
			
			setVisible( false );
		}
	}
	
	static class NewGraphDialog extends JDialog 
		implements ChangeListener, ActionListener
	{
		public static final long serialVersionUID = 0x00A01301L;
		
		protected Component parent;
		protected CLI cli;
		protected JTextField graphId;
		protected JTextField graphFile;
		protected JTextField graphClass;
		protected JCheckBox autolayout;
		protected JCheckBox setFile;
		protected JCheckBox setClass;
		protected JCheckBox autoid;
		protected JButton selectFile;
		
		public NewGraphDialog( Component parent, CLI cli )
		{
			super( 
				(Frame) (parent == null ? null :
				parent instanceof Dialog ? (Dialog) parent :
				parent instanceof Frame ? (Frame) parent : null), "New graph" );
			
			this.parent = parent;
			this.cli = cli;
			graphId = new JTextField( 20 );
			graphFile = new JTextField( 20 );
			graphClass = new JTextField( 20 );
			autolayout = new JCheckBox( "autolayout", false );
			setFile = new JCheckBox( "Save in file", false );
			setClass = new JCheckBox( "Graph class", false );
			autoid = new JCheckBox( "Auto-id", true );
			selectFile = new JButton( "..." );
			
			setFile.addChangeListener( this );
			selectFile.setPreferredSize( 
					new Dimension( 
							graphFile.getPreferredSize().height, 
							graphFile.getPreferredSize().height ) );
			selectFile.setActionCommand( "choose" );
			selectFile.addActionListener( this );
			setClass.addChangeListener( this );
			autoid.addChangeListener( this );
			
			JPanel g1 = new JPanel(), g2 = new JPanel(), g3 = new JPanel(), g4 = new JPanel();
			JPanel bigLeft = new JPanel(), bigRight = new JPanel();
			JPanel tmp;
			
			setLayout( new GridLayout( 1, 2 ) );
			
			g1.setLayout( new GridLayout( 4, 1 ) );
			g1.add( autoid );
			tmp = new JPanel();
			tmp.add( graphId );
			g1.add( tmp );
			g1.add( setClass );
			tmp = new JPanel();
			tmp.add( graphClass );
			g1.add( tmp );
			g1.setBorder( BorderFactory.createTitledBorder( "Informations" ) );

			g2.setLayout( new GridLayout( 2, 1 ) );
			g2.add( setFile );
			tmp = new JPanel();
			tmp.add( graphFile );
			tmp.add( selectFile );
			g2.add( tmp );
			g2.setBorder( BorderFactory.createTitledBorder( "File" ) );
			
			g3.setLayout( new GridLayout( 1, 1 ) );
			g3.add( autolayout );
			g3.setBorder( BorderFactory.createTitledBorder( "Display options" ) );
			
			JButton button = new JButton( "create" );
			button.setActionCommand( "create" );
			button.addActionListener( this );
			g4.add( button );
			button = new JButton( "cancel" );
			button.setActionCommand( "cancel" );
			button.addActionListener( this );
			g4.add( button );
			
			JPanel sep = new JPanel();
			sep.setPreferredSize( new Dimension( 1, g4.getPreferredSize().height ) );
			bigLeft.setLayout( new BorderLayout() );
			bigLeft.add( g1, BorderLayout.NORTH );
			bigLeft.add( g3, BorderLayout.CENTER );
			bigLeft.add( sep, BorderLayout.SOUTH );
			
			bigRight.setLayout( new BorderLayout() );
			bigRight.add( g2, BorderLayout.CENTER );
			bigRight.add( g4, BorderLayout.SOUTH );
			
			add( bigLeft );
			add( bigRight );
			
			pack();
			setResizable( false );
			stateChanged( null );
		}
		
		protected void create()
		{
			String cmd = "create graph";
			String id = graphId.getText().trim();
			
			if( ! autoid.isSelected() )
			{
				if( id.length() == 0 )
				{
					warningMessage( this, "enter an id or use autoid" );
					return;
				}
				
				if( cli.getCore().getContext( id ) != null )
				{
					errorMessage( this, "this id is already taken" );
					return;
				}
				
				cmd += " \"" + id + "\"";
			}
			
			if( setClass.isSelected() )
			{
				try
				{
					Class.forName( graphClass.getText() );
				}
				catch( ClassNotFoundException e )
				{
					errorMessage( this, "unknown graph class" );
					return;
				}
				
				cmd += " use class " + graphClass.getText().trim();
			}
			
			if( setFile.isSelected() && graphFile.getText().length() > 0 )
			{
				File f = new File( graphFile.getText() );
				
				cmd += " set file \"" + f.getAbsolutePath() + "\"";
				
				if( f.exists() )
				{
					int r;
					
					r = JOptionPane.showConfirmDialog( this, 
							"file exists, overwrite ?", "warning",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
					
					if( r != JOptionPane.YES_OPTION )
						return;
					
					cmd += " overwrite";
				}
			}
			
			cmd += " and display";
			
			if( autolayout.isSelected() )
				cmd += " autolayout";
			
			String error = cli.execute(cmd);
			
			if( CLI.isErrorMessage( error ) )
				errorMessage( this, CLI.getMessage( error ) );
			else if( CLI.isWarningMessage( error ) )
				warningMessage( this, CLI.getMessage( error ) );
			
			setVisible( false );
		}
		
		protected void choose()
		{
			JFileChooser jfc = new JFileChooser( "." );
			int r = jfc.showOpenDialog( this );
			
			if( r == JFileChooser.APPROVE_OPTION && jfc.getSelectedFile() != null )
			{
				graphFile.setText( jfc.getSelectedFile().getAbsolutePath() );
			}
		}
		
	// ChangeListener implementation
		
		public void stateChanged( ChangeEvent e )
		{
			graphFile.setEnabled( setFile.isSelected() );
			selectFile.setEnabled( setFile.isSelected() );
			graphClass.setEnabled( setClass.isSelected() );
			graphId.setEnabled( ! autoid.isSelected() );
			if( autoid.isSelected() )
				autoid.setText( "Auto-id" );
			else
				autoid.setText( "Graph id:" );
		}
		
	// ActionListener implementation
		
		public void actionPerformed( ActionEvent e )
		{
			if( e.getActionCommand().equals( "cancel" ) )
				setVisible( false );
			else if( e.getActionCommand().equals( "create" ) )
				create();
			else if( e.getActionCommand().equals( "choose" ) )
				choose();
		}
	}
	
	static class OpenGraphFileChooser extends JFileChooser
	{
		public static final long serialVersionUID = 0x00A01101L;
		
		protected OpenGraphAccessory accessory;
		protected CLI cli;
		protected Component parent;
		
		public OpenGraphFileChooser( CLI cli, Component parent )
		{
			super( "." );
			
			this.cli = cli;
			this.parent = parent;
			
			this.accessory = new OpenGraphAccessory();
			setAccessory( this.accessory );
			
			initGraphFileFilters( this );
		}
		
		public void approveSelection()
		{
			if( accessory.check() )
				super.approveSelection();
		}
		
		public void run()
		{
			int r;
			
			r = showOpenDialog( parent );
			
			switch( r )
			{
			case APPROVE_OPTION:
			{
				String id = null, reader = null;
				if( accessory.useGraphId.isSelected() && ! accessory.graphId.getText().equals("") )
					id = accessory.graphId.getText();
				if( accessory.useReader.isSelected() && ! accessory.readerClass.getText().equals("") )
					reader = accessory.readerClass.getText();
				
				openGraph( getSelectedFile(), id, reader, accessory.autolayout.isSelected() );
				break;
			}
			case ERROR_OPTION:		
				errorMessage( this, "cannot open file" ); break;
			}
		}
		
		protected void openGraph( File f, String id, String reader, boolean autolayout )
		{
			if( f == null ) return;
			String cmd = String.format( "open graph \"%s\"%s%s and display%s", 
					f.getAbsolutePath(),
					reader != null ? " using reader class " + reader : "",
					id != null ? " use id \"" + id + "\"" : "",
					autolayout ? " autolayout" : "" );

			String error = cli.execute( cmd );
			if( CLI.isErrorMessage( error ) )
				errorMessage( this, CLI.getMessage( error ) );
			else if( CLI.isWarningMessage( error ) )
				warningMessage( this, CLI.getMessage( error ) );
		}
	}
	
	static class OpenGraphAccessory extends JPanel implements ChangeListener
	{
		public static final long serialVersionUID = 0x00A01001L;
		
		protected JTextField graphId;
		protected JTextField readerClass;
		protected JCheckBox  useGraphId;
		protected JCheckBox  useReader;
		protected JCheckBox  autolayout;
		
		public OpenGraphAccessory()
		{
			this.graphId = new JTextField(20);
			this.readerClass = new JTextField(20);
			this.useReader = new JCheckBox( "Use reader:", false );
			this.readerClass.setEnabled( false );
			this.useReader.addChangeListener( this );
			this.useGraphId = new JCheckBox( "Graph id:", false );
			this.graphId.setEnabled( false );
			this.useGraphId.addChangeListener( this );
			this.autolayout = new JCheckBox( "Use autolayout", false );
			
			JPanel tmp = new JPanel();
			tmp.setLayout( new GridLayout(5,1) );
			tmp.add( useGraphId );
			tmp.add( graphId );
			tmp.add( useReader );
			tmp.add( readerClass );
			tmp.add( autolayout );
			add( tmp );
		}
		
		public boolean check()
		{
			boolean r = true;
			
			useGraphId.setForeground( Color.BLACK );
			useReader.setForeground( Color.BLACK );
			useReader.setToolTipText( "" );
			useGraphId.setToolTipText( "" );
			
			if( useGraphId.isSelected() )
			{
				if( ! graphId.getText().matches( CLICommand.PATTERN_ID ) )
				{
					useGraphId.setForeground( Color.RED );
					useGraphId.setToolTipText( "bad id" );
					r = false;
				}
			}
			
			if( useReader.isSelected() )
			{
				try
				{
					Class.forName( readerClass.getText() );
				}
				catch( ClassNotFoundException e )
				{
					useReader.setForeground( Color.RED );
					useReader.setToolTipText( "reader not found" );
					r = false;
				}
			}
			
			return r;
		}
		
		public void stateChanged( ChangeEvent e )
		{
			readerClass.setEnabled( useReader.isSelected() );
			graphId.setEnabled( useGraphId.isSelected() );
		}
	}
	
	static class SaveGraphFileChooser extends JFileChooser
	{
		public static final long serialVersionUID = 0x00101201L;
		
		protected CLI cli;
		protected Component parent;
		protected Context ctx;
		protected boolean save;
		
		public SaveGraphFileChooser( CLI cli, Component parent, Context ctx, boolean save )
		{
			super( ctx.getDefaultFile() == null ? "." : ctx.getDefaultFile() );
			
			this.ctx = ctx;
			this.cli = cli;
			this.parent = parent;
			this.save = save;
			
			initGraphFileFilters( this );
		}
		
		public void run()
		{
			int r;
			
			r = showSaveDialog( parent );
			
			switch( r )
			{
			case APPROVE_OPTION:
			{
				if( getSelectedFile() == null )
					return;
				
				ctx.setDefaultFile( getSelectedFile().getAbsolutePath() );
				
				if( save ) cli.execute( String.format( "save graph \"%s\"", 
						ctx.getGraph().getId() ) );
				
				break;
			}
			case ERROR_OPTION:		
				errorMessage( this, "cannot save file" ); break;
			}
		}
	}
	
	static class ExtFileFilter extends FileFilter
	{
		protected String ext;
		protected String description;
		
		public ExtFileFilter( String ext, String description )
		{
			this.ext = ext;
			this.description = description;
		}
		
		public boolean accept( File f )
		{
			return f != null && 
				( f.getName().matches( "^.*" + ext + "$" ) || f.isDirectory() );
		}
		
		public String getDescription()
		{
			return description;
		}
	}
	/**
	 * Defines an object to reload.
	 */
	static class ReloadField
	{
		Object obj;
		String gettext;
		Method method;
		
		public ReloadField( Object obj, String gettext, String method )
		{
			this.obj = obj;
			this.gettext = gettext;
			
			try
			{
				this.method = obj.getClass().getMethod(method, String.class);
			}
			catch( Exception e )
			{
				
			}
		}
		
		public void reload()
		{
			try
			{
				method.invoke(obj,WGetText.getTextLookup(gettext));
			}
			catch( Exception e )
			{
				System.err.printf( "reloading object:\n\t%s\n", 
						e.getMessage() == null ? e.getClass() : e.getMessage() );
			}
		}
	}
	/**
	 * Defines a pool containing objects to reload. 
	 */
	static class ReloadPool
		extends LinkedList<ReloadField>
		implements NotificationListener
	{
		private static final long serialVersionUID = 0x0001L;
		
		public ReloadPool()
		{
			WNotificationServer.connect(this);
		}
		
		public void reload()
		{
			for( int i = 0; i < size(); i++ )
				get(i).reload();
		}
		
		public void handleNotification( Notification n )
		{
			if( n == Notification.langChanged ) reload();
		}
	}
	/**
	 * Contains all objects to reload.
	 */
	private static final ReloadPool reloadPool = new ReloadPool(); 
	/**
	 * Add an object to be reload when lang changed.
	 * 
	 * @param obj object to reload
	 * @param gettext text to reload
	 * @param callMethod method to call, taking String as argument
	 */
	public static void reloadOnLangChanged( Object obj, String gettext, String callMethod )
	{
		reloadPool.add( new ReloadField(obj,gettext,callMethod) );
	}
}
