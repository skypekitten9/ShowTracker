package showtracker.client;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.LineBorder;

import showtracker.Episode;
import showtracker.Helper;
import showtracker.Show;

public class ShowInfo extends JPanel {
	private ClientController cc;
	private JPanel mainPanel = new JPanel();
	private ArrayList<SeasonListener> listeners = new ArrayList<>();
	private Show show;

	public ShowInfo(Show show, ClientController cc) {
		this.cc = cc;
		this.show = show;
		for (double d: show.getSeasons())
			listeners.add(new SeasonListener(d));

		initiatePanels();
		draw();
	}

	private void initiatePanels() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(mainPanel);
		scrollPane.setLayout(new ScrollPaneLayout());
		scrollPane.setBackground(Color.CYAN);

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.add(Box.createHorizontalGlue());
		
		ImageIcon infoImage = new ImageIcon("images/info.png");
		Image infoImg = infoImage.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		ImageIcon infoImgIcon = new ImageIcon(infoImg);

		JButton infoBtn = new JButton(infoImgIcon);
		infoBtn.setPreferredSize(new Dimension(30, 50));
		
		infoBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, 
			"<html><body><p style=\"width: 200px;\">" + show.getDescription() + "</p></body></html>", "Show info", JOptionPane.PLAIN_MESSAGE);
			}
			
		});
//		infoBtn.addActionListener(e -> Helper.message("<html><body><p style=\"width: 200px;\">" + show.getDescription() + "</p></body></html>"));

		JPanel headerBar = new JPanel();
		headerBar.setBounds(0, 0, 500, 50);
		headerBar.setLayout(new BorderLayout());
		headerBar.setPreferredSize(new Dimension(500, 50));
		headerBar.add(new JLabel(show.getName()));
		headerBar.add(infoBtn, BorderLayout.EAST);
		headerBar.setBorder(new LineBorder(Color.black));

		setLayout(new BorderLayout());
		add(headerBar, BorderLayout.NORTH);
		add(scrollPane);
	}

	private void draw() {
		mainPanel.removeAll();
		for (SeasonListener sl : listeners) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			panel.add(Box.createHorizontalGlue());
			JButton button;
			if (sl.getSeason() == 0)
				button = new JButton("Specials");
			else
				button = new JButton("Season " + Helper.df.format(sl.getSeason()));
			button.setMinimumSize(new Dimension(100, 30));
			button.setMaximumSize(new Dimension(100, 30));
			button.addActionListener(sl);
			panel.add(button);
			if (sl.getOpen())
				for (Episode ep : show.getEpisodes())
					if (ep.getSeasonNumber() == sl.getSeason()) {
						JLabel lbl = new JLabel("Episode " + Helper.df.format(ep.getEpisodeNumber()) + " - " + ep.getName());
						lbl.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								JOptionPane.showMessageDialog(null, "<html><body><p style=\"width: 200px;\">" + show.getEpisode(ep.getSeasonNumber(), ep.getEpisodeNumber()).getDescription() + "</p></body></html>", ep.getName(), JOptionPane.INFORMATION_MESSAGE);
							}
						});
						panel.add(lbl);
						JCheckBox checkBox = new JCheckBox();
						checkBox.setSelected(ep.isWatched());
						checkBox.addActionListener(new EpisodeListener(ep));
						panel.add(checkBox);
			}
			mainPanel.add(panel);
		}
		revalidate();
		repaint();
	}

	private class SeasonListener implements ActionListener {
		private double season;
		private boolean open = false;

		public SeasonListener(double season) {
			this.season = season;
		}

		public double getSeason() {
			return season;
		}

		public boolean getOpen() {
			return open;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			open = !open;
			draw();
		}
	}

	private class EpisodeListener implements ActionListener {
		private Episode episode;

		public EpisodeListener(Episode episode) {
			this.episode = episode;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean isWatched = ((JCheckBox) e.getSource()).isSelected();
			episode.setWatched(isWatched);
		}
	}
}