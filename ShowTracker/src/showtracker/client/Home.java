package showtracker.client;

import showtracker.Episode;
import showtracker.Helper;
import showtracker.Show;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collections;

/**
 * @author Filip Spånberg
 * En panel som visar nästa avsnitt tittare ska se
 */
public class Home extends JPanel {
    private ClientController cc;
    private DecimalFormat df = new DecimalFormat("0.#");
    private JScrollPane scrollPane = new JScrollPane();
    private JViewport jvp;

    public Home(ClientController cc) {
        this.cc = cc;

        scrollPane.getViewport().setBackground(UIManager.getColor("panel.background"));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(335, 400));
        add(scrollPane);
    }

    /**
     * Metod för att rita upp de senaste avsnitten
     */
    void draw() {
        //Message for user if there are no shows to display
        if (cc.getUser().getShows().isEmpty()) {
            JLabel label = new JLabel("<html><p style = \"text-align:center;\"> You have nothing more to watch!<br>Add more shows by pressing the search button.</p></html>", JLabel.CENTER);
            scrollPane.setViewportView(label);
            scrollPane.revalidate();
            scrollPane.repaint();
            return;
        }

        //Else add all shows to panel
        Box box = Box.createVerticalBox();
        for (Show s : cc.getUser().getShows())
            System.out.println(s.getLastWatched());
        Collections.sort(cc.getUser().getShows(), new Helper.LastWatchedComparator());
        for (Show sh : cc.getUser().getShows()) {
            Episode currentEpisode = sh.getFirstUnwatched();

            if (currentEpisode != null) {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setMaximumSize(new Dimension(600, 100));
                panel.setBorder(BorderFactory.createBevelBorder(1));

                //knappen
                JButton button = new JButton("<html>Episode<br>watched</html>");
                //panel.setPreferredSize(new Dimension(50,35));
                button.addActionListener(new EpisodeListener(currentEpisode));
                panel.add(button, BorderLayout.EAST);

                JLabel label = new JLabel(String.format("<html><div style=\"width:150px;\">%s<br>Season %s, episode %s%s<br>%s</div></html>",
                        sh.getName(), //titel serie
                        df.format(currentEpisode.getSeasonNumber()), //säsongsnummer
                        df.format(currentEpisode.getEpisodeNumber()),//avsnittsnummer
                        currentEpisode.getName() != null && !currentEpisode.getName().equals("") ? ":<br>" + currentEpisode.getName() : "", "Network: " + sh.getNetwork() + "") );// avsnittsnamn
                panel.add(label, BorderLayout.CENTER);


                if (sh.getImage() != null && !sh.getImage().equalsIgnoreCase("N/A")) {
                    ImageIcon imageIcon = null;
                    try {
                        URL url = new URL(sh.getImage());
                        Image i = ImageIO.read(url);
                        imageIcon = new ImageIcon(i);
                    } catch (IOException e) {
                        imageIcon = null;
                        e.printStackTrace();
                    }
                    if (imageIcon != null) {
                        //JLabel imageLabel = new JLabel(new ImageIcon(imageIcon.getImage().getScaledInstance(35, 40, Image.SCALE_SMOOTH)), JLabel.CENTER);
                        JLabel imageLabel = new JLabel(new ImageIcon(imageIcon.getImage().getScaledInstance(60, 70,0)), JLabel.CENTER);
                        panel.setPreferredSize(new Dimension(50,50));
                        panel.add(imageLabel, BorderLayout.WEST);
                        ImageIcon finalImageIcon = imageIcon;
                        imageLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                JOptionPane.showMessageDialog(null, finalImageIcon);
                            }
                        });
                    }
                }
                JLabel lbWidth = new JLabel();
                lbWidth.setPreferredSize(new Dimension(300, 1));
                panel.add(lbWidth, BorderLayout.SOUTH);
                panel.updateUI();
                box.add(panel);
            }
        }
        scrollPane.setViewportView(box);
        scrollPane.revalidate();
        scrollPane.repaint();
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }

    public void redraw(){
        this.updateUI();
        scrollPane.updateUI();
        scrollPane.getVerticalScrollBar().updateUI();
        scrollPane.getViewport().updateUI();
    }

    public JPanel logoPanel() {
        JPanel logoPanel = new JPanel();

        ImageIcon logoImage = new ImageIcon("images/logo.jpg");
        Image logoImg = logoImage.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon logoImgIcon = new ImageIcon(logoImg);
        JLabel imageLabel = new JLabel(logoImgIcon);

        logoPanel.setLayout(new BorderLayout());
        logoPanel.add(imageLabel, BorderLayout.CENTER);

        return logoPanel;
    }

    private class EpisodeListener implements ActionListener {
        private Episode ep;

        public EpisodeListener(Episode ep) {
            this.ep = ep;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(ep.getName() + ", " + ep);
            ep.setWatched(true);
            draw();
        }
    }
}