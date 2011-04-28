package org.graphstream.tool.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;

public class EnumComboBox extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2648376232791766130L;

	static Color border = new Color(0, 0, 0);
	static Color background = new Color(50, 50, 50);
	static Color text = new Color(240, 240, 240);
	static Color labelback = new Color(30, 30, 30);

	JLabel selected;
	Class<? extends Enum<?>> choices;

	public EnumComboBox(Class<? extends Enum<?>> choices) {
		this.choices = choices;

		GridBagConstraints c = new GridBagConstraints();

		selected = new JLabel(choices.getEnumConstants()[0].name());
		selected.setForeground(text);
		selected.setFont(Resources.getRegularFont(12.0f));

		IconButton openAlternative = IconButton.createIconButton(
				IconButton.Type.DOWN, selected.getPreferredSize().height + 2,
				null, new OpenChoicesAction());

		setLayout(new GridBagLayout());

		c.gridwidth = 1;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(1, 7, 1, 1);

		add(selected, c);

		c.weightx = 0.0;
		c.insets = new Insets(1, 1, 1, 2);
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;

		add(openAlternative, c);
	}

	protected void paintComponent(Graphics g) {
		g.setColor(background);
		g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

		g.setColor(border);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
	}

	class Choices extends JPanel implements FocusListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3605605810816910542L;

		Choices() {
			JScrollPane scroll = new JScrollPane();
			scroll.setPreferredSize(new Dimension(EnumComboBox.this.getWidth(),
					200));
			scroll.setOpaque(false);
			scroll.getViewport().setOpaque(false);
			scroll.setBorder(null);
			setBackground(background);
			
			scroll.addFocusListener(this);
			add(scroll);
		}

		public void focusGained(FocusEvent e) {
			System.err.printf("gain\n");
		}

		public void focusLost(FocusEvent e) {
			System.err.printf("lost\n");
		}
	}

	class OpenChoicesAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1759297585641231141L;

		public void actionPerformed(ActionEvent e) {
			Popup popup = PopupFactory
					.getSharedInstance()
					.getPopup(
							EnumComboBox.this,
							new Choices(),
							EnumComboBox.this.getLocationOnScreen().x,
							EnumComboBox.this.getLocationOnScreen().y
									+ EnumComboBox.this.getHeight());
			popup.show();
		}
	}
}
