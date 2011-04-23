package org.graphstream.tool.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class TitleLabel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4096602903174491162L;

	Image background;

	public TitleLabel(String label) {
		GridBagConstraints c = new GridBagConstraints();
		JLabel l = new JLabel(label, JLabel.LEFT);

		setLayout(new GridBagLayout());

		l.setFont(Resources.getBoldFont(25.0f));
		l.setOpaque(false);

		c.insets = new Insets(5, 10, 5, 10);
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;

		add(l, c);

		background = Resources.getImage(Resources.LINES, 0, 0, true);
	}

	protected void paintComponent(Graphics g) {
		if (background != null) {
			if (g instanceof Graphics2D) {
				Graphics2D g2d = (Graphics2D) g;

				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
			}

			int w = background.getWidth(this);

			for (int i = 0; i < getWidth(); i += w) {
				g.drawImage(background, i, 0, null);
			}
		}
	}
}