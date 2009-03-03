package org.miv.graphstream.tool.workbench.gui;

import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.Edge;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class WElementInfo
	extends JDialog
{
	private static final long serialVersionUID = 0x0001L;

	public WElementInfo( Element e )
	{
		GridBagLayout bag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		Insets labelInsets = new Insets( 0, 10, 0, 10 );
		Insets infosInsets = new Insets( 0, 0, 0, 0 );
		
		JLabel label;
		
		setTitle( "element : " + e.getId() );
		setLayout(bag);
		setIconImage( WUtils.getImageIcon( "gs_logo" ).getImage() );
		setResizable(false);
		
		c.insets = labelInsets;
		c.weightx = 1.0;
		c.gridwidth = 1;
		label = new JLabel("type");
		label.setFont( WFonts.getFont("dialog:title") );
		bag.setConstraints(label,c);
		add(label);
		
		c.weightx = 2.0;
		c.insets = infosInsets;
		c.gridwidth = GridBagConstraints.REMAINDER;
		label = new JLabel( e.getClass().getCanonicalName() );
		label.setFont( WFonts.getFont("dialog:infos") );
		bag.setConstraints(label,c);
		add(label);
		
		if( e instanceof Node )
		{
			Node n = (Node) e;
			
			c.insets = labelInsets;
			c.weightx = 1.0;
			c.gridwidth = 1;
			label = new JLabel("degree");
			label.setFont( WFonts.getFont("dialog:title") );
			bag.setConstraints(label,c);
			add(label);
			
			c.weightx = 2.0;
			c.insets = infosInsets;
			c.gridwidth = GridBagConstraints.REMAINDER;
			label = new JLabel( String.format("%d (in:%d,out:%d)",n.getDegree(),n.getInDegree(),n.getOutDegree()) );
			label.setFont( WFonts.getFont("dialog:infos") );
			bag.setConstraints(label,c);
			add(label);
		}
		else if( e instanceof Edge )
		{
			Edge edge = (Edge) e;
			
			c.insets = labelInsets;
			c.weightx = 1.0;
			c.gridwidth = 1;
			label = new JLabel("directed");
			label.setFont( WFonts.getFont("dialog:title") );
			bag.setConstraints(label,c);
			add(label);
			
			c.weightx = 2.0;
			c.insets = infosInsets;
			c.gridwidth = GridBagConstraints.REMAINDER;
			label = new JLabel( Boolean.toString(edge.isDirected()) );
			label.setFont( WFonts.getFont("dialog:infos") );
			bag.setConstraints(label,c);
			add(label);
			
			c.insets = labelInsets;
			c.weightx = 1.0;
			c.gridwidth = 1;
			label = new JLabel("source");
			label.setFont( WFonts.getFont("dialog:title") );
			bag.setConstraints(label,c);
			add(label);
			
			c.weightx = 2.0;
			c.insets = infosInsets;
			c.gridwidth = GridBagConstraints.REMAINDER;
			label = new JLabel( edge.getSourceNode().getId() );
			label.setFont( WFonts.getFont("dialog:infos") );
			bag.setConstraints(label,c);
			add(label);
			
			c.insets = labelInsets;
			c.weightx = 1.0;
			c.gridwidth = 1;
			label = new JLabel("target");
			label.setFont( WFonts.getFont("dialog:title") );
			bag.setConstraints(label,c);
			add(label);
			
			c.weightx = 2.0;
			c.insets = infosInsets;
			c.gridwidth = GridBagConstraints.REMAINDER;
			label = new JLabel( edge.getTargetNode().getId() );
			label.setFont( WFonts.getFont("dialog:infos") );
			bag.setConstraints(label,c);
			add(label);
		}
		
		DefaultTableModel model = new DefaultTableModel();
		JTable attsTable = new JTable(model);
		
		model.addColumn("key");
		model.addColumn("value");
		
		Iterator<String> ite = e.getAttributeKeyIterator();
		while( ite != null && ite.hasNext() )
		{
			String key = ite.next();
			model.addRow( new Object [] { key, e.getAttribute(key) } );
		}
		
		JScrollPane scrollAtts = new JScrollPane(attsTable);
		JPanel attsPanel = new JPanel();
		attsPanel.setLayout( new BorderLayout() );
		attsPanel.add(scrollAtts,BorderLayout.CENTER);
		attsPanel.setPreferredSize( bag.preferredLayoutSize(this));
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 3;
		c.weightx = 0.0;
		bag.setConstraints(attsPanel,c);
		add(attsPanel);
		
		pack();
		
		setVisible(true);
	}
}
