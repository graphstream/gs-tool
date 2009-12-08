package org.graphstream.tool.workbench.gui;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
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
	private static final long serialVersionUID = 0x0400C0000001L;

	public WElementInfo( Element e )
	{
		GridBagLayout bag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		Insets labelInsets = new Insets( 5, 10, 5, 10 );
		Insets infosInsets = new Insets( 5, 0, 5, 0 );
		
		JLabel label;
		
		setTitle( "element : " + e.getId() );
		setLayout(bag);
		setIconImage( WUtils.getImageIcon( "gs_logo" ).getImage() );
		//setResizable(false);
		
		c.insets = labelInsets;
		c.weightx = 1.0;
		c.gridwidth = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel("type",JLabel.RIGHT);
		label.setFont( label.getFont().deriveFont(Font.BOLD,14.0f) );
		bag.setConstraints(label,c);
		add(label);
		
		c.weightx = 2.0;
		c.insets = infosInsets;
		c.gridwidth = GridBagConstraints.REMAINDER;
		label = new JLabel( e.getClass().getCanonicalName() );
		label.setFont( label.getFont().deriveFont(Font.PLAIN,12.0f) );
		bag.setConstraints(label,c);
		add(label);
		
		if( e instanceof Node )
		{
			Node n = (Node) e;
			
			c.insets = labelInsets;
			c.weightx = 1.0;
			c.gridwidth = 1;
			c.gridy++;
			label = new JLabel("degree",JLabel.RIGHT);
			label.setFont( label.getFont().deriveFont(Font.BOLD,14.0f) );
			bag.setConstraints(label,c);
			add(label);
			
			c.weightx = 2.0;
			c.insets = infosInsets;
			c.gridwidth = GridBagConstraints.REMAINDER;
			label = new JLabel( String.format("%d (in:%d,out:%d)",n.getDegree(),n.getInDegree(),n.getOutDegree()) );
			label.setFont( label.getFont().deriveFont(Font.PLAIN,12.0f) );
			bag.setConstraints(label,c);
			add(label);
		}
		else if( e instanceof Edge )
		{
			Edge edge = (Edge) e;
			
			c.insets = labelInsets;
			c.weightx = 1.0;
			c.gridwidth = 1;
			c.gridy++;
			label = new JLabel("directed",JLabel.RIGHT);
			label.setFont( label.getFont().deriveFont(Font.BOLD,14.0f) );
			bag.setConstraints(label,c);
			add(label);
			
			c.weightx = 2.0;
			c.insets = infosInsets;
			c.gridwidth = GridBagConstraints.REMAINDER;
			label = new JLabel( Boolean.toString(edge.isDirected()) );
			label.setFont( label.getFont().deriveFont(Font.PLAIN,12.0f) );
			bag.setConstraints(label,c);
			add(label);
			
			c.insets = labelInsets;
			c.weightx = 1.0;
			c.gridwidth = 1;
			c.gridy++;
			label = new JLabel("source",JLabel.RIGHT);
			label.setFont( label.getFont().deriveFont(Font.BOLD,14.0f) );
			bag.setConstraints(label,c);
			add(label);
			
			c.weightx = 2.0;
			c.insets = infosInsets;
			c.gridwidth = GridBagConstraints.REMAINDER;
			label = new JLabel( edge.getSourceNode().getId() );
			label.setFont( label.getFont().deriveFont(Font.PLAIN,14.0f) );
			bag.setConstraints(label,c);
			add(label);
			
			c.insets = labelInsets;
			c.weightx = 1.0;
			c.gridwidth = 1;
			c.gridy++;
			label = new JLabel("target",JLabel.RIGHT);
			label.setFont( label.getFont().deriveFont(Font.BOLD,14.0f) );
			bag.setConstraints(label,c);
			add(label);
			
			c.weightx = 2.0;
			c.insets = infosInsets;
			c.gridwidth = GridBagConstraints.REMAINDER;
			label = new JLabel( edge.getTargetNode().getId() );
			label.setFont( label.getFont().deriveFont(Font.PLAIN,14.0f) );
			bag.setConstraints(label,c);
			add(label);
		}
		
		DefaultTableModel model = new DefaultTableModel();
		JTable attsTable = new JTable(model);
		attsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
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
		attsPanel.setPreferredSize( new Dimension( 300,100) );//bag.preferredLayoutSize(this));
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridy++;
		c.weightx = 0.0;
		c.weighty = 2.0;
		c.fill = GridBagConstraints.BOTH;
		bag.setConstraints(attsPanel,c);
		add(attsPanel);
		
		pack();
		
		setVisible(true);
	}
}
