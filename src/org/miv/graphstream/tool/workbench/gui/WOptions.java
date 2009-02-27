package org.miv.graphstream.tool.workbench.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.InputStream;

import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ListDataListener;

import org.miv.graphstream.tool.workbench.WAlgorithm;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WOptions
	extends JDialog
	implements ItemListener
{
	private static final long serialVersionUID = 0x0001L;
	
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
	
	public WOptions()
	{
		setTitle( "Options" );
		
		ComboBoxModel cbm = new SkinModel();
		JComboBox jcb = new JComboBox(cbm);
		jcb.addItemListener(this);
		
		JPanel skinPanel = new JPanel();
		skinPanel.setBorder( BorderFactory.createTitledBorder("Skin") );
		skinPanel.add(jcb);
		
		add(skinPanel);
		
		pack();
	}
	
	public void itemStateChanged( ItemEvent ie )
	{
		if( ie.getItem() instanceof String )
			setSkin( (String) ie.getItem() );
	}
	
	protected void setSkin( String name )
	{
		try
		{
			javax.swing.JFrame.setDefaultLookAndFeelDecorated(false);
			javax.swing.JDialog.setDefaultLookAndFeelDecorated(false);
			
			UIManager.getLookAndFeel().uninitialize();
			UIManager.setLookAndFeel(name);
			
			javax.swing.JFrame.setDefaultLookAndFeelDecorated(true);
			javax.swing.JDialog.setDefaultLookAndFeelDecorated(true);
		}
		catch( Exception e )
		{
			javax.swing.JOptionPane.showMessageDialog(null, String.format( "Can not load LookAndFeel\n" +
					"%s\nsubstance-lite.jar is in your classpath ?", 
					e.getMessage() == null ? e.getClass() : e.getMessage() ),
					"Skin error", javax.swing.JOptionPane.ERROR_MESSAGE); 
		}
	}
	
	public void loadUserSetting()
	{
		
	}
	
	public void loadSettings( InputStream in )
	{
		
	}
}
