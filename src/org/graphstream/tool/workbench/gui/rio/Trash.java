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
package org.graphstream.tool.workbench.gui.rio;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class Trash
	extends JPanel
{
	private static final long serialVersionUID = 0x06009000L;

	protected static final Color BASIC = new Color( 0.8f, 1, 0.2f, 0.5f );
	protected static final Color TRASH = new Color( 1, 0, 0, 0.6f );
	protected static final BasicStroke STROKE = new BasicStroke( 3, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );
	protected static final BasicStroke TRASH_STROKE = new BasicStroke( 10, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );
	
	GSLinker 	linker;
	IOComponent	ioc;
	
	public Trash( GSLinker linker )
	{
		this.linker = linker;
		
		setSize( 50, 50 );
		setOpaque(false);
		setBackground( BASIC );
		setForeground( Color.BLACK );
		
		addMouseListener( new MouseAdapter()
		{
			public void mouseEntered( MouseEvent e )
			{
				Trash.this.ioc  = Trash.this.linker.getDraggedComponent();
				
				Trash.this.setBackground( Trash.this.ioc != null ? TRASH : BASIC );
				Trash.this.getParent().repaint(
						Trash.this.getX(),
						Trash.this.getY(),
						Trash.this.getWidth(),
						Trash.this.getHeight());
				Trash.this.repaint();
			}
			
			public void mouseExited( MouseEvent e )
			{
				Trash.this.setBackground(BASIC);
				Trash.this.getParent().repaint(
						Trash.this.getX(),
						Trash.this.getY(),
						Trash.this.getWidth(),
						Trash.this.getHeight());
				Trash.this.repaint();
				
				Trash.this.ioc = null;
			}
			
			public void mouseReleased( MouseEvent e )
			{
				if( Trash.this.ioc != null )
					Trash.this.linker.removeIOComponent( Trash.this.ioc );
				
				Trash.this.ioc = null;
			}
		});
		
		addMouseMotionListener( new MouseMotionAdapter()
		{
			public void mouseDragged( MouseEvent e )
			{
				Trash.this.setLocation(
						Trash.this.getX() + e.getX() - Trash.this.getWidth() / 2,
						Trash.this.getY() + e.getY() - Trash.this.getHeight() / 2
				);
			}
		});
	}
	
	public void paintComponent( Graphics g )
	{
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		g2d.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
	    g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON );
	    g2d.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY );
	    g2d.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
	    g2d.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );
	    
	    g2d.setStroke(TRASH_STROKE);
		g2d.setColor( getBackground() );
		
		g2d.drawOval( getWidth() / 2 - 10, getHeight() / 2 - 10, 20, 20 );
		g2d.drawLine( 
				5, 
				getHeight() - 5, 
				getWidth() / 2 + (int) ( Math.cos( 3 * Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( 3 * Math.PI / 4 ) * 10 )
		);
		g2d.drawLine( 
				5, 
				5, 
				getWidth() / 2 + (int) ( Math.cos( -3 * Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( -3 * Math.PI / 4 ) * 10 )
		);
		g2d.drawLine( 
				getWidth() - 5, 
				getHeight() - 5, 
				getWidth() / 2 + (int) ( Math.cos( Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( Math.PI / 4 ) * 10 )
		);
		g2d.drawLine( 
				getWidth() - 5, 
				5, 
				getWidth() / 2 + (int) ( Math.cos( -Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( -Math.PI / 4 ) * 10 )
		);
		
	    g2d.setStroke(STROKE);
		g2d.setColor( getForeground() );
		
		g2d.drawOval( getWidth() / 2 - 10, getHeight() / 2 - 10, 20, 20 );
		g2d.drawLine( 
				5, 
				getHeight() - 5, 
				getWidth() / 2 + (int) ( Math.cos( 3 * Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( 3 * Math.PI / 4 ) * 10 )
		);
		g2d.drawLine( 
				5, 
				5, 
				getWidth() / 2 + (int) ( Math.cos( -3 * Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( -3 * Math.PI / 4 ) * 10 )
		);
		g2d.drawLine( 
				getWidth() - 5, 
				getHeight() - 5, 
				getWidth() / 2 + (int) ( Math.cos( Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( Math.PI / 4 ) * 10 )
		);
		g2d.drawLine( 
				getWidth() - 5, 
				5, 
				getWidth() / 2 + (int) ( Math.cos( -Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( -Math.PI / 4 ) * 10 )
		);
	}
}
