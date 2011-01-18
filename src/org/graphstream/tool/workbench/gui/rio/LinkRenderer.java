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
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;

public class LinkRenderer 
	extends JPanel
	implements PropertyChangeListener
{
	private static final long serialVersionUID = 0x06003000L;
	
	protected static final BasicStroke linkStroke  = new BasicStroke( 5, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND, 0, new float [] { 1, 5 }, 0 );

	protected static final Color BORDER 		= new Color(1,1,1,0.9f);
	protected static final Color VIRTUAL_BORDER = new Color(1,1,1,0.3f);
	//protected static final Color BORDER_ON_DRAG = new Color(1,1,1,0.6f);
	
	protected static final Color COLOR_SRC		= new Color(1,1,1,0.1f);
	protected static final Color COLOR_TRG		= new Color(1,1,1,0.8f);
	
	IOComponent src;
	IOComponent trg;
	
	int [] p1 = {0,0};
	int [] p2 = {0,0};
	
	boolean		virtual;
	
	public LinkRenderer( IOComponent src, IOComponent trg )
	{
		this(src,trg,false);
	}
	
	public LinkRenderer( IOComponent src, IOComponent trg, boolean virtual )
	{
		this.src = src;
		this.trg = trg;
		this.virtual = virtual;
		
		setOpaque(false);
		
		rebuild();
		
		src.addPropertyChangeListener("position",this);
		src.addPropertyChangeListener("dragged",this);
		trg.addPropertyChangeListener("position",this);
		trg.addPropertyChangeListener("dragged",this);
		
		rebuild();
	}
	
	public void paintComponent( Graphics g )
	{
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		g2d.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
	    g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON );
	    g2d.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY );
	    g2d.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
	    g2d.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );
		
		//g2d.setColor( virtual ? VIRTUAL_BORDER : BORDER );
	    g2d.setPaint( new GradientPaint( 5 + p1[0], 5 + p1[1], COLOR_TRG, 5 + p2[0], 5 + p2[1], COLOR_SRC ) );
	    g2d.setStroke(linkStroke);
	    g2d.drawLine( 5 + p1[0], 5 + p1[1], 5 + p2[0], 5 + p2[1] );
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		rebuild();
	}
	
	protected void computePoints()
	{
		int x1,x2,y1,y2;
	    
	    if( src.getX() > trg.getX() )
	    {
	    	x1 = 0;
	    	x2 = Math.abs(src.getLocation().x - trg.getLocation().x);
	    }
	    else
	    {
	    	x1 = Math.abs(src.getLocation().x - trg.getLocation().x);
	    	x2 = 0;
	    }
	    
	    if( src.getY() > trg.getY() )
	    {
	    	y1 = 0;
	    	y2 = Math.abs(src.getLocation().y - trg.getLocation().y);
	    }
	    else
	    {
	    	y1 = Math.abs(src.getLocation().y - trg.getLocation().y);
	    	y2 = 0;
	    }
	    
	    double teta = 0;
	    
	    double x = x1 - x2;
	    double y = y1 - y2;
	    
	    int c = src.getWidth() / 2;
	    
	    teta = Math.atan2(y, x) + Math.PI;
	    
	    p1 [0] = x1 + (int) ( Math.cos(teta) * c );
	    p1 [1] = y1 + (int) ( Math.sin(teta) * c );
	    p2 [0] = x2 + (int) ( Math.cos(Math.atan2(-y, -x) + Math.PI) * c );
	    p2 [1] = y2 + (int) ( Math.sin(Math.atan2(-y, -x) + Math.PI) * c );
	}
	
	protected void rebuild()
	{
		Dimension dim = getPreferredSize();
		
		dim.setSize(
				Math.abs(src.getLocation().x - trg.getLocation().x) + 10,
				Math.abs(src.getLocation().y - trg.getLocation().y) + 10
		);
		
		setLocation(
				Math.min(src.getLocation().x,trg.getLocation().x) + src.getWidth() / 2 - 5,
				Math.min(src.getLocation().y,trg.getLocation().y) + src.getHeight() / 2 - 5
		);
		
		setPreferredSize(dim);
		setSize(dim);

		computePoints();
		revalidate();
		repaint();
	}
	
	public void setVirtual( boolean on )
	{
		virtual = on;
	}
	
	public boolean isVirtual()
	{
		return virtual;
	}
}
