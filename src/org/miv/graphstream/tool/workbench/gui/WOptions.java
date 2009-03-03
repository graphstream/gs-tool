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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.LinkedList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataListener;

import org.miv.graphstream.tool.workbench.xml.WXmlConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WOptions
	extends JDialog
	implements ItemListener, ChangeListener, ActionListener, WXmlConstants
{
	private static final long serialVersionUID = 0x0001L;
	
	protected static String userSettings = "";
	
	static class SettingsHandler extends DefaultHandler
	{
		public SettingsHandler()
		{
		}
		
		public void startElement (String uri, String localName,
			      String qName, Attributes atts)
			throws SAXException
		{
			
		}
		
		public void characters(char[] ch, int start, int length)
		{
			
		}
		
		public void endElement(String uri, String localName, String qName)
		{
			
		}
	}
	
	protected static String [] skins =
	{
		"javax.swing.plaf.basic.BasicLookAndFeel",
		"javax.swing.plaf.metal.MetalLookAndFeel",
		"javax.swing.plaf.multi.MultiLookAndFeel",
		"javax.swing.plaf.synth.SynthLookAndFeel",
		"org.jvnet.substance.skin.SubstanceBusinessLookAndFeel",
		"org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel",
		"org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel",
		"org.jvnet.substance.skin.SubstanceCremeLookAndFeel",
		"org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel",
		"org.jvnet.substance.skin.SubstanceSaharaLookAndFeel",
		"org.jvnet.substance.skin.SubstanceModerateLookAndFeel",
		"org.jvnet.substance.skin.SubstanceOfficeSilver2007LookAndFeel",
		"org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel",
		"org.jvnet.substance.skin.SubstanceNebulaLookAndFeel",
		"org.jvnet.substance.skin.SubstanceNebulaBrickWallLookAndFeel",
		"org.jvnet.substance.skin.SubstanceAutumnLookAndFeel",
		"org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel",
		"org.jvnet.substance.skin.SubstanceMistAquaLookAndFeel",
		"org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel",
		"org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel",
		"org.jvnet.substance.skin.SubstanceRavenLookAndFeel",
		"org.jvnet.substance.skin.SubstanceMagmaLookAndFeel",
		"org.jvnet.substance.skin.SubstanceChallengerDeepLookAndFeel",
		"org.jvnet.substance.skin.SubstanceEmeraldDuskLookAndFeel",
		"com.sun.java.swing.plaf.gtk.GTKLookAndFeel"
	};
	
	static class SkinModel
		implements ComboBoxModel
	{
		LinkedList<ListDataListener> listeners = new LinkedList<ListDataListener>();
		int selected = 0;
		
		public SkinModel()
		{
			String name = UIManager.getLookAndFeel().getClass().getName();
			
			selected = find(name);
			
			if( selected == -1 )
				selected = 0;
		}
		
		public void addListDataListener(ListDataListener l)
		{
			listeners.add(l);
		}
		
		public void removeListDataListener(ListDataListener l)
		{
			listeners.remove(l);
		}
		
		public Object getElementAt(int index)
		{
			return skins [index];
		}
		
		public int getSize()
		{
			return skins.length;
		}
		
		public Object getSelectedItem()
		{
			return skins [selected];
		}
		
        public void setSelectedItem(Object anItem)
        {
        	int index = find(anItem);
        	
        	if( index != -1 )
        		selected = index;
        }
        
        protected int find( Object skin )
        {
        	int index = 0;
        	
        	while( index < skins.length && ! skins [index].equals(skin) )
        		index++;
        	
			if( index >= skins.length )
				return -1;
			
			return index;
        }
	}
	
	protected WGui gui;
	protected JCheckBox fullMode;
	protected SkinModel	skinModel;
	
	public WOptions( WGui gui )
	{
		GridBagLayout 		bag = new GridBagLayout();
		GridBagConstraints	c	= new GridBagConstraints();
		
		setTitle( "Options" );
		setLayout( bag );
		
		this.gui = gui;
		
		JLabel 		label;
		JComboBox	comboBox;
		JPanel		panel;
		//JCheckBox	checkBox;
		JButton		button;
		
		skinModel = new SkinModel();
		comboBox = new JComboBox(skinModel);
		comboBox.addItemListener(this);
		
		label = new JLabel("skin");
			c.weightx = 1.0;
			c.ipadx = 10;
		bag.setConstraints(label,c);
		add(label);
			c.gridwidth = GridBagConstraints.REMAINDER;
			//c.ipadx = 0;
		bag.setConstraints(comboBox,c);
		add(comboBox);
		
		DefaultComboBoxModel langModel = new DefaultComboBoxModel();
		
		for( java.util.Locale locale : WGetText.getEnabledLocales() )
		{
			langModel.addElement( locale );
		}
		
		langModel.setSelectedItem( java.util.Locale.getDefault() );
		
		comboBox = new JComboBox(langModel);
		comboBox.addItemListener( new ItemListener()
			{
				public void itemStateChanged( ItemEvent ie )
				{
					WUserSettings.getUserSettings().setLocale((java.util.Locale) ie.getItem());
					WGetText.setLocale( (java.util.Locale) ie.getItem() );
				}
			});
		
		label = new JLabel("locale");
			c.weightx = 1.0;
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.ipadx = 10;
		bag.setConstraints(label,c);
		add(label);
			c.fill = GridBagConstraints.BOTH;
			c.gridwidth = GridBagConstraints.REMAINDER;
		bag.setConstraints(comboBox,c);
		add(comboBox);
		
		fullMode = new JCheckBox("Full mode");
		fullMode.addChangeListener(this);
		bag.setConstraints(fullMode,c);
		add(fullMode);
		
		WUtils.reloadOnLangChanged(fullMode,"@gettext(options:fullmode)","setText");
		
		button = new JButton( WGetText.getText("options:save") );
			button.addActionListener(this);
			button.setActionCommand("options.save");
		panel = new JPanel();
		panel.add(button);
		bag.setConstraints(panel,c);
		add(panel);
		
		WUtils.reloadOnLangChanged(button,"@gettext(options:save)","setText");
		
		pack();
	}
	
	public void itemStateChanged( ItemEvent ie )
	{
		if( ie.getItem() instanceof String )
			WUserSettings.getUserSettings().setLookAndFeel( (String) ie.getItem() );
	}
	
	public void stateChanged( ChangeEvent e )
	{
		if( e.getSource() == fullMode )
		{
			gui.setFullMode(fullMode.isSelected());
		}
	}
	
	public void actionPerformed( ActionEvent e )
	{
		if( e.getActionCommand().equals("options.save") )
			WUserSettings.saveUserSettings();
	}
}
