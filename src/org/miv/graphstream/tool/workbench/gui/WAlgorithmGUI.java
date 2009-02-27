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

import org.miv.graphstream.tool.workbench.WAlgorithm;
import org.miv.graphstream.tool.workbench.cli.CLI;
import org.miv.graphstream.tool.workbench.event.AlgorithmListener;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;

import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class WAlgorithmGUI
	extends JDialog
	implements ActionListener, AlgorithmListener
{
	private static final long serialVersionUID = 0x0001L;
	
	public static ImageIcon ALGORITHM_ICON = null;
	static
	{
		ALGORITHM_ICON = new ImageIcon(ClassLoader.getSystemResource(
		"org/miv/graphstream/tool/workbench/gears_32.png"));
	}
	
	static HashMap<String,String> fields = new HashMap<String,String>();
	static
	{
		fields.put( "java.lang.String",		WAlgorithmParametersGUIFieldString.class.getName() );
		fields.put( "boolean", 				WAlgorithmParametersGUIFieldBoolean.class.getName() );
		fields.put( "java.lang.Boolean", 	WAlgorithmParametersGUIFieldBoolean.class.getName() );
		fields.put( "int",					WAlgorithmParametersGUIFieldInteger.class.getName() );
		fields.put( "java.lang.Integer",	WAlgorithmParametersGUIFieldInteger.class.getName() );
		fields.put( "float",				WAlgorithmParametersGUIFieldFloat.class.getName() );
		fields.put( "java.lang.Float",		WAlgorithmParametersGUIFieldFloat.class.getName() );
		fields.put( "double",				WAlgorithmParametersGUIFieldDouble.class.getName() );
		fields.put( "java.lang.Double",		WAlgorithmParametersGUIFieldDouble.class.getName() );
		
		fields.put( "org.miv.graphstream.graph.Node",
				WAlgorithmParametersGUIFieldNode.class.getName() );
	}
	
	class WAlgorithmParametersGUI
		extends JDialog
		implements ActionListener
	{
		private static final long serialVersionUID = 0x0001L;
		
		LinkedList<WAlgorithmParametersGUIField> params = new LinkedList<WAlgorithmParametersGUIField>();
		
		public WAlgorithmParametersGUI()
		{
			setTitle( "Parameters" );
			setLayout( new GridLayout(WAlgorithmGUI.this.algorithm.getParametersCount()+1,2) );
			
			for( int i = 0; i < WAlgorithmGUI.this.algorithm.getParametersCount(); i++ )
			{
				WAlgorithm.Parameter p = WAlgorithmGUI.this.algorithm.getParameter(i);

				add( new JLabel( p.getName() ));
				
				String clazz = fields.get(p.getClassName() );
				WAlgorithmParametersGUIField field = null;
				
				if( clazz == null )
				{
					field = new WAlgorithmParametersGUIFieldString(p);
				}
				else
				{
					try
					{
						Class<?> c = Class.forName(clazz);
						java.lang.reflect.Constructor<?> co = c.getConstructor(p.getClass());
						field = (WAlgorithmParametersGUIField) co.newInstance(p);
					}
					catch( Exception e )
					{
						e.printStackTrace();
						field = new WAlgorithmParametersGUIFieldString(p);
					}
				}
				
				add( field );
				params.add(field);
			}
			
			add(new JPanel());
			
			JPanel okPanel = new JPanel();
			JButton ok = new JButton("close");
				ok.setActionCommand("close");
				ok.addActionListener(this);
				okPanel.add(ok);
			
			add( okPanel );
			
			pack();
		}
		
		public void actionPerformed( ActionEvent ae )
		{
			if( ae.getActionCommand().equals("close") )
				setVisible(false);
		}
		
		public Object [] getValues()
		{
			Object [] values = new Object [params.size()];
			
			for( int i = 0; i < params.size(); i++ )
				values [i] = params.get(i).getValue();
			
			return values;
		}
		
		public String toString()
		{
			StringBuffer buffer = new StringBuffer("{");
			String sep = "";
			
			for( int i = 0; i < params.size(); i++ )
			{
				buffer.append(sep).append(params.get(i).getValue());
				sep=";";
			}
			
			buffer.append("}");
			
			return buffer.toString();
		}
	}
	
	public static abstract class WAlgorithmParametersGUIField
		extends JPanel
	{
		private static final long serialVersionUID = 0x0001L;
		
		public abstract Object getValue();
	}
	
	public static class WAlgorithmParametersGUIFieldString
		extends WAlgorithmParametersGUIField
	{
		private static final long serialVersionUID = 0x0001L;

		JTextField value;
		
		public WAlgorithmParametersGUIFieldString( WAlgorithm.Parameter param )
		{
			setLayout( new BorderLayout() );
			
			value = new JTextField(20);
			add( value, BorderLayout.CENTER );
		}
		
		public Object getValue()
		{
			return value.getText();
		}
	}
	
	public static class WAlgorithmParametersGUIFieldBoolean
		extends WAlgorithmParametersGUIField
	{
		private static final long serialVersionUID = 0x0001L;
		
		JCheckBox value;
		
		public WAlgorithmParametersGUIFieldBoolean( WAlgorithm.Parameter param )
		{
			setLayout( new BorderLayout() );

			value = new JCheckBox();
			add( value, BorderLayout.CENTER );
		}
		
		public Object getValue()
		{
			return value.isSelected();
		}
	}
	
	public static class WAlgorithmParametersGUIFieldInteger
		extends WAlgorithmParametersGUIField
	{
		private static final long serialVersionUID = 0x0001L;
		
		JSpinner value;
		
		public WAlgorithmParametersGUIFieldInteger( WAlgorithm.Parameter param )
		{
			setLayout( new BorderLayout() );
			
			SpinnerNumberModel model = new SpinnerNumberModel(1,0,100,1);
			value = new JSpinner(model);
			add( value, BorderLayout.CENTER );
		}
		
		public Object getValue()
		{
			return value.getValue();
		}
	}
	
	public static class WAlgorithmParametersGUIFieldFloat
		extends WAlgorithmParametersGUIFieldString
	{
		private static final long serialVersionUID = 0x0001L;
		
		public WAlgorithmParametersGUIFieldFloat( WAlgorithm.Parameter param )
		{
			super(param);
		}
		
		public Object getValue()
		{
			return new Float( (String) super.getValue() );
		}
	}
	
	public static class WAlgorithmParametersGUIFieldDouble
		extends WAlgorithmParametersGUIFieldString
	{
		private static final long serialVersionUID = 0x0001L;
		
		public WAlgorithmParametersGUIFieldDouble( WAlgorithm.Parameter param )
		{
			super(param);
		}
		
		public Object getValue()
		{
			return new Double( (String) super.getValue() );
		}
	}
	
	public static class WAlgorithmParametersGUIFieldNode
		extends WAlgorithmParametersGUIField
	{
		private static final long serialVersionUID = 0x0001L;
		
		JComboBox value;
		
		public WAlgorithmParametersGUIFieldNode( WAlgorithm.Parameter p )
		{
			WAlgorithmParametersGUIFieldElementModel model = 
				new WAlgorithmParametersGUIFieldElementModel( WElementList.getNodeModel() );
			
			value = new JComboBox(model);
			add(value);
		}
		
		public Object getValue()
		{
			return value.getSelectedItem();
		}
	}
	
	public static class WAlgorithmParametersGUIFieldElementModel
		implements ComboBoxModel
	{
		String selected;
		ListModel model;
		
		public WAlgorithmParametersGUIFieldElementModel( ListModel model )
		{
			this.model = model;
			
			if( model.getSize() > 0 )
				selected = (String) model.getElementAt(0);
			else selected = null;
		}
		
		public void addListDataListener(ListDataListener l)
		{
			model.addListDataListener(l);
		}
		
		public void removeListDataListener(ListDataListener l)
		{
			model.removeListDataListener(l);
		}
		
		public Object getElementAt(int index)
		{
			return model.getElementAt(index);
		}
		
		public int getSize()
		{
			return model.getSize();
		}
		
		public Object getSelectedItem()
		{
			return selected;
		}
		
        public void setSelectedItem(Object anItem)
        {
        	selected = (String) anItem;
        }
	}
	
	private WAlgorithm algorithm;
	private WAlgorithmParametersGUI params;
	private CLI cli;
	private JButton run, stop;
	
	public WAlgorithmGUI( CLI cli, WAlgorithm algorithm )
	{
		this.cli = cli;
		this.algorithm = algorithm;
		
		params = new WAlgorithmParametersGUI();
		params.setModal(true);
		
		setLayout( new BorderLayout() );
		setTitle( "Algorithm : " + algorithm.getName() );
		
		HTMLEditorKit editor = new HTMLEditorKit();
		HTMLDocument doc = (HTMLDocument) editor.createDefaultDocument();
		StringReader in = new StringReader( 
				"<html>" +
				"<head></head>" +
				"<body><div id='content'>" + 
				algorithm.getDescription() + 
				"</div></body></html>" );
		try
		{
			editor.read(in,doc,0);
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		JTextPane desc = new JTextPane();
		desc.setEditorKit(editor);
		desc.setDocument(doc);
		//desc.setText(algorithm.getDescription());
		desc.setEditable(false);
		add( desc, BorderLayout.CENTER );
		
		run = new JButton("run");
			run.setActionCommand( "algorithm.run" );
			run.addActionListener(this);
		JButton options = new JButton( "options" );
			options.setActionCommand( "algorithm.configure" );
			options.addActionListener(this);
		if( algorithm.getParametersCount() == 0 )
			options.setEnabled(false);
		stop = new JButton("stop");
			stop.setActionCommand("algorithm.stop");
			stop.addActionListener(this);
			stop.setEnabled(false);
		
		JPanel buttons = new JPanel();
		buttons.add(run);
		if( algorithm.isDynamic() )
			buttons.add(stop);
		buttons.add(options);
		add( buttons, BorderLayout.SOUTH );
		
		setIconImage(ALGORITHM_ICON.getImage());
		
		pack();
		
		algorithm.addAlgorithmListener(this);
	}
	
	public WAlgorithm getAlgorithm()
	{
		return algorithm;
	}
	
	public void actionPerformed( ActionEvent ae )
	{
		if( ae.getActionCommand().equals("algorithm.run") )
			execute();
		else if( ae.getActionCommand().equals("algorithm.configure") )
			params.setVisible(true);
		else if( ae.getActionCommand().equals("algorithm.stop") )
			algorithm.stop();
	}
	
	public void execute()
	{
		if( ! run.isEnabled() )
			return;
		
		run.setEnabled(false);
		
		Runnable r = new Runnable()
		{
			public void run()
			{
				algorithm.execute(cli.getCore().getActiveContext().getGraph(),
						params.getValues());
			}
		};
		
		Thread t = new Thread( r, "algorithm" );
		t.start();
	}
	
	public void algorithmStart( WAlgorithm algo )
	{
		if( SwingUtilities.isEventDispatchThread() )
		{
			run.setEnabled(false);
			stop.setEnabled(true);
		}
		else
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				public void run()
				{
					run.setEnabled(false);
					stop.setEnabled(true);
				}
			} );
		}
	}
	
	public void algorithmError( WAlgorithm algo, String error )
	{
		if( SwingUtilities.isEventDispatchThread() )
		{

			JOptionPane.showMessageDialog(this, error, "Algorithm error", 
					JOptionPane.ERROR_MESSAGE );
		}
		else
		{
			final String ferror = error;
			SwingUtilities.invokeLater( new Runnable()
			{
				public void run()
				{
					JOptionPane.showMessageDialog(WAlgorithmGUI.this, ferror, "Algorithm error", 
							JOptionPane.ERROR_MESSAGE );
				}
			} );
		}
		JOptionPane.showMessageDialog(this, error, "Algorithm error", 
				JOptionPane.ERROR_MESSAGE );
	}
	
	public void algorithmEnd( WAlgorithm algo )
	{
		if( SwingUtilities.isEventDispatchThread() )
		{
			run.setEnabled(true);
			stop.setEnabled(false);
		}
		else
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				public void run()
				{
					run.setEnabled(true);
					stop.setEnabled(false);
				}
			} );
		}
	}
}
