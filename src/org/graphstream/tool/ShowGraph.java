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

package org.graphstream.tool;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.oldUi.GraphViewer;
import org.graphstream.oldUi.GraphViewerRemote;
import org.graphstream.oldUi.swing.SwingGraphViewer;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import org.util.Environment;

/**
 * A simple (user friendly?) viewer for graphs and dynamic graphs.
 * 
 * @author Antoine Dutot
 * @author Yoann Pign�
 */
public class ShowGraph extends JFrame implements ActionListener, ChangeListener
{
// Attributes
	
	/**
	 * The graph reader.
	 */
	protected FileSource reader;
	
	/**
	 * Time to sleep between each step in the graph file.
	 */
	protected int sleepMs = 100;
	
	/**
	 * Name of the graph read.
	 */
	protected String fileName; 
	
// Attributes
	
    private static final long serialVersionUID = 1L;
	
    /**
     * The file chooser (keep it to conserve the last used directory).
     */
    protected JFileChooser fileChooser;

    /**
     * The status bar.
     */
    protected JPanel statusBar;
    
    /**
     * The menu bar.
     */
    protected JMenuBar menuBar;

    /**
     * The menu.
     */
    protected JMenu fileMenu, styleMenu, predefStylesMenu;
    
    /**
     * The menu item.
     */
    protected JMenuItem exitMenuItem;
    
    /**
     * The menu item.
     */
    protected JMenuItem clearCssMenuItem, addCssMenuItem, defaultCssMenuItem;
    
    /**
     * Predefined style sheet menu items.
     */
    protected JMenuItem darkStyleMenuItem, lightStyleMenuItem, alphaStyleMenuItem; 
    
    /**
     * The tool bar.
     */
    protected JToolBar toolBar;
    
// Attributes

    /**
     * The graph to display.
     */
    protected Graph graph;
    
    /**
     * The graph viewer.
     */
    protected GraphViewer viewer;
    
    /**
     * The graph viewer command.
     */
    protected GraphViewerRemote viewerRemote;

    /**
     * Read/pause reading the graph file.
     */
    protected JButton pausePlay;
    
    /**
     * Current step in the dynamic graph file.
     */
    protected JLabel step;
    
    /**
     * Reading speed (the pause in milliseconds).
     */
    protected JSlider speed; 

    /**
     * The status.
     */
    protected JLabel status;
    
// Attributes
    
    /**
     * The timer for reading.
     */
    protected Timer timer;
    
    /**
     * The current step in the dynamic graph file.
     */
    protected int curStep;
    
// Constructors

	public static void main( String args[] )
	{
		new ShowGraph( args );
	}
	
	public ShowGraph( String args[] )
	{
		setLAF();
		Environment env = Environment.getGlobalEnvironment();
		
		env.readCommandLine( args );
		env.setParameter( "SwingGraphRenderer.interpolateBounds", "1" );
		
		if( env.getParameter( "h" ).length() > 0 || env.getParameter( "help" ).length() > 0 )
			showHelp();
		
		boolean layout = true;
		
		fileName     = env.getParameter( "input" );
		fileName     = askForGraphFileName( fileName );
		timer        = new Timer( sleepMs, this );
		graph        = new MultiGraph( fileName );
		layout       = ! env.getBooleanParameter( "noLayout" );
		viewer       = new SwingGraphViewer( graph, layout, true );
		viewerRemote = viewer.newViewerRemote();
		String css   = env.getParameter( "css" );
		
		if( css != null && css.length() > 0 )
		     graph.addAttribute( "stylesheet", String.format( "url('%s')", css ) );
		else graph.addAttribute( "stylesheet", defaultStyleSheet );
		
		buildUI();
		openGraph();
		
		timer.setCoalesce( true );
		timer.setRepeats( true );
		timer.start();
	}
	
	protected void showHelp()
	{
		System.out.printf( "Usage: %s [options]%n", getClass().getName() );
		System.out.printf( "  available options:%n" );
		System.out.printf( "    -input=<file> .......... use the given file as input (you can pass a directory used by the file chooser).%n" );
		System.out.printf( "    -css=<file> ............ use the given file as style sheet.%n" );
		System.out.printf( "    -noLayout .............. do not automatically layout the graph.%n" );
		System.out.printf( "    -h or -help ............ this help message.%n" );
		
		System.exit( 0 );
	}
	
	protected String askForGraphFileName( String fileName )
	{
		String dirMarker = System.getProperties().getProperty( "file.separator" );

		if( fileName != null && fileName.length() > 0 )
		{
			// If there is already a file name test if this is a directory, in which
			// case we must still open the file chooser.
			
			File f = new File( fileName );
		
			if( ! f.exists() )
			{
				JOptionPane.showMessageDialog( this, "File not found.", "Error",
					JOptionPane.ERROR_MESSAGE );
				System.err.printf( "The given file ('%s') does not seem to exit.", fileName );
				System.exit( 1 );			
			}
			if( f.isDirectory() )
			{
				if( ! fileName.endsWith( dirMarker ) )
					fileName = String.format( "%s%s", fileName, dirMarker );
			}
			else
			{
				// This is not a directory use this as the input graph.
				
				if( f.isFile() )
				{
					return fileName;
				}
			}
		}
		else
		{
			// If there is no file name at all, start from the current directory.
			
			fileName = System.getProperty( "user.dir" );
			
			if( ! fileName.endsWith( dirMarker ) )
				fileName = String.format( "%s%s", fileName, dirMarker );
		}

		if( fileChooser == null )
			fileChooser = new JFileChooser();
		
		// No file name set a start directory.
			
		if( fileName != null && ( fileName.endsWith( dirMarker ) ) )
			fileChooser.setCurrentDirectory( new File( fileName ) );
			
		fileChooser.setDialogTitle( "Select a graph to read" );
			
		// And ask for a file name.
		
		if( fileChooser.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION )
		{
			return fileChooser.getSelectedFile().getAbsolutePath();
		}
		else
		{
			JOptionPane.showMessageDialog( this, "No graph file to read.", "Error",
				JOptionPane.ERROR_MESSAGE );
			System.err.printf( "No graph file to read, use the '-input=file' command line option or use the file chooser." );
			System.exit( 1 );
		}
		
		return null;
	}

	protected String askForCSSFileName()
	{
		if( fileChooser == null )
		{
			String fileName  = System.getProperty( "user.dir" );
			String dirMarker = System.getProperties().getProperty( "file.separator" );
			
			if( ! fileName.endsWith( dirMarker ) )
				fileName = String.format( "%s%s", fileName, dirMarker );

			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory( new File( fileName ) );
		}
		
		fileChooser.setDialogTitle( "Select a style sheet to read" );
			
		if( fileChooser.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION )
		{
			return fileChooser.getSelectedFile().getAbsolutePath();
		}
		else
		{
			JOptionPane.showMessageDialog( this, "No style sheet file to read.", "Error",
				JOptionPane.ERROR_MESSAGE );
			System.err.printf( "No style sheet file to read, use the '-css=file' command line option or use the file chooser." );
		}
		
		return null;
	}
	
	protected void buildUI()
	{
		ImageIcon icon24 = createImageIcon( "GraphStreamSmallLogo24.png", "" );

		// The menu.
		
		menuBar   = new JMenuBar();
		fileMenu  = new JMenu( "File" );
		styleMenu = new JMenu( "Style" );
		
		exitMenuItem       = new JMenuItem( "Exit" );
		clearCssMenuItem   = new JMenuItem( "Clear CSS" );
		addCssMenuItem     = new JMenuItem( "Add CSS" );
		defaultCssMenuItem = new JMenuItem( "Default CSS" );
		predefStylesMenu   = new JMenu( "Predefined Styles" );
		darkStyleMenuItem  = new JMenuItem( "Dark" );
		lightStyleMenuItem = new JMenuItem( "Light" );
		alphaStyleMenuItem = new JMenuItem( "Alpha" );
		
		fileMenu.add( exitMenuItem );
		styleMenu.add( clearCssMenuItem );
		styleMenu.add( addCssMenuItem );
		styleMenu.add( defaultCssMenuItem );
		styleMenu.add( predefStylesMenu );
		predefStylesMenu.add( darkStyleMenuItem );
		predefStylesMenu.add( lightStyleMenuItem );
		predefStylesMenu.add( alphaStyleMenuItem );
		
		exitMenuItem.addActionListener( this );
		clearCssMenuItem.addActionListener( this );
		addCssMenuItem.addActionListener( this );
		defaultCssMenuItem.addActionListener( this );
		darkStyleMenuItem.addActionListener( this );
		lightStyleMenuItem.addActionListener( this );
		alphaStyleMenuItem.addActionListener( this );
		
		menuBar.add( fileMenu );
		menuBar.add( styleMenu );
		
		setJMenuBar( menuBar );
		
		// The tool bar.
		
		toolBar   = new JToolBar();
		pausePlay = new JButton( "Pause" );
		speed     = new JSlider();

		pausePlay.addActionListener( this );
		speed.addChangeListener( this );
		speed.setPaintLabels( false );
		speed.setValue( 100-(sleepMs/10) );
		
		pausePlay.setToolTipText( "Pause or play a dynamic graph." );
		speed.setToolTipText( "Specify the reading speed for dynamic graphs." );
		
		add( toolBar, BorderLayout.NORTH );
		toolBar.setLayout( new BoxLayout( toolBar, BoxLayout.X_AXIS ) );
		toolBar.add( Box.createHorizontalStrut( 10 ) );
		toolBar.add( pausePlay );
		toolBar.add( Box.createHorizontalGlue() );
		toolBar.add( new JLabel( "Speed : " ) );
		toolBar.add( speed );
		
		// The status
		
		statusBar = new JPanel();
		status    = new JLabel();

		statusBar.add( status );
		add( statusBar, BorderLayout.SOUTH );
		
		// The frame.
		
		setTitle( "Show Graph" );
		setIconImage( icon24.getImage() );
		viewerRemote.setQuality( 4 );
		viewerRemote.setStepsVisible( true );
		add( (JComponent)viewer.getComponent(), BorderLayout.CENTER );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setSize( 400, 400 );
		setVisible( true );
	}

	protected void openGraph()
	{
		try
        {
            reader = FileSourceFactory.sourceFor( fileName );
           // GraphReaderListenerHelper helper = new GraphReaderListenerHelper( graph );
            reader.addSink( graph );
            
            reader.begin( fileName );
            updateStatus();
        }
//        catch( NotFoundException e )
//        {
//			JOptionPane.showMessageDialog( this,
//					String.format( "Cannot find graph file '%s' (%s).", fileName, e.getMessage() ),
//					"Error whiel reading graph",
//					JOptionPane.ERROR_MESSAGE );
//			e.printStackTrace();
//			System.exit( 0 );
//        }
        catch( IOException e )
        {
			JOptionPane.showMessageDialog( this,
					String.format( "I/O error while reading graph '%s' (%s).", fileName, e.getMessage() ),
					"Error while reading graph",
					JOptionPane.ERROR_MESSAGE );
			e.printStackTrace();
			System.exit( 0 );
        }
//        catch( GraphParseException e )
//        {
//			JOptionPane.showMessageDialog( this,
//					String.format( "Parse error while reading graph '%s' (%s).", fileName, e.getMessage() ),
//					"Error while reading graph",
//					JOptionPane.ERROR_MESSAGE );
//			e.printStackTrace();
//			System.exit( 0 );
//        }
	}

	protected void readGraphStep()
	{
		try
        {
			if( reader != null )
			{
				boolean hasNext = reader.nextStep();
				curStep ++;
            
				if( ! hasNext )
				{
					reader.end();
					reader = null;
					pausePlay.setEnabled( false );
					speed.setEnabled( false );
					timer.stop();
				}

	            updateStatus();
			}
        }
        catch( IOException e )
        {
			JOptionPane.showMessageDialog( this,
					String.format( "I/O error while reading graph '%s' (%s).", fileName, e.getMessage() ),
					"Error while reading graph",
					JOptionPane.ERROR_MESSAGE );
			e.printStackTrace();
			System.exit( 0 );
        }
//        catch( GraphParseException e )
//        {
//			JOptionPane.showMessageDialog( this,
//					String.format( "Parse error while reading graph '%s' (%s).", fileName, e.getMessage() ),
//					"Error while reading graph",
//					JOptionPane.ERROR_MESSAGE );
//			e.printStackTrace();
//			System.exit( 0 );
//        }
	}
	
	protected void updateStatus()
	{
		int n = graph.getNodeCount();
		int e = graph.getEdgeCount();
		
		String s;
		
		if( reader != null )
		{
			if( curStep > 1 )
			     s = String.format( "Reading ... (step %d)", curStep );
			else s = "Reading ...";
		}
		else
		{
			if( curStep > 1 )
			     s = String.format( "OK (%d steps)", curStep );
			else s = "OK";
		}
		
		status.setText( String.format(
				"%5d node%s  %5d edge%s  %s",
				n, n == 1 ? "" : "s", 
				e, e == 1 ? "" : "s",
				s
			) );
	}

	public void actionPerformed( ActionEvent e )
    {
		if( e.getSource() == timer )
		{
			readGraphStep();
		}
		else if( e.getSource() == clearCssMenuItem )
		{
			graph.removeAttribute( "stylesheet" );
		}
		else if( e.getSource() == addCssMenuItem )
		{
			String fileName = askForCSSFileName();
			
			graph.addAttribute( "stylesheet", String.format( "url('%s')", fileName ) );
		}
		else if( e.getSource() == defaultCssMenuItem )
		{
			graph.addAttribute( "stylesheet", defaultStyleSheet );
		}
		else if( e.getSource() == darkStyleMenuItem )
		{
			graph.addAttribute( "stylesheet", defaultDarkStyleSheet );
		}
		else if( e.getSource() == lightStyleMenuItem )
		{
			graph.addAttribute( "stylesheet", defaultLightStyleSheet );			
		}
		else if( e.getSource() == alphaStyleMenuItem )
		{
			graph.addAttribute( "stylesheet", defaultAlphaStyleSheet );
		}
		else if( e.getSource() == exitMenuItem )
		{
			System.exit( 0 );
		}
		else if( e.getSource() == pausePlay )
		{
			if( timer.isRunning() )
			{
				pausePlay.setText( "Play" );
				timer.stop();
			}
			else
			{
				pausePlay.setText( "Pause" );
				timer.start();
			}
		}
    }

	public void stateChanged( ChangeEvent e )
    {
		if( e.getSource() == speed )
		{
			timer.setDelay( (100 - speed.getValue())*10 );
		}
    }
	
// Access
	
// Commands

	protected void setLAF()
	{
        try 
        {   
            UIManager.LookAndFeelInfo[] installed = UIManager.getInstalledLookAndFeels();
                
            for( int i=0; i<installed.length; i++ )
            {   
                if( installed[i].getName().startsWith( "GTK" ) ) 
                {   
                    UIManager.setLookAndFeel( installed[i].getClassName() );
                    i = installed.length;
                }   
            }   
        }   
        catch( Exception e ) 
        {   
            e.printStackTrace();
        }   
	}
	
	protected ImageIcon createImageIcon(String path, String description)
	{
	    java.net.URL imgURL = getClass().getResource( path );
	    
	    if (imgURL != null)
	    {
	        return new ImageIcon( imgURL, description );
	    }
	    else
	    {
	    	return new ImageIcon( path, description );
	    }
	}
	
	protected static String defaultStyleSheet =
		"node { width: 5px; }" +
		"edge { color: #808080; arrow-length:10px; arrow-width:3px; }";
	
	protected static String defaultDarkStyleSheet =
		"graph { background-color: #303030; }" +
		"node  { color: #D0D0D0; }" +
		"edge  { color: #909090; }";
	
	protected static String defaultLightStyleSheet =
		"graph { background-color: white; }" +
		"node  { color: #202020; }" +
		"edge  { color: #707070; }";
	
	protected static String defaultAlphaStyleSheet =
		"graph { background-color: white; }" +
		"node  { color: rgba(20,20,20,64); }" +
		"edge  { color: rgba(20,20,20,32); }";
}