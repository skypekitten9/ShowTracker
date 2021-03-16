
package showtracker.client;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import showtracker.Episode;
import showtracker.Show;
import showtracker.User;

public class SearchShows extends JPanel {

    private static ClientController cc = new ClientController();

    private User user;
    private ArrayList<Show> databasResponse = new ArrayList<Show>();

    private JTextField tfSearchBar = new JTextField("Enter name of the show here");
    private JTextField tfShowName = new JTextField();
    private JButton searchBarBtn = new JButton("Search");

    private JPanel jpSearchBar = new JPanel();
    private JPanel jpSearchResult = new JPanel();
    private JPanel jpMyOwnShowPanel = new JPanel();
    private JPanel jpMyShow = new JPanel();

    private JScrollPane jspSearchResult = new JScrollPane();

    private ImageIcon image;

    private JButton btnCreateOwnShow;

    public SearchShows(ClientController cc) {
        this.cc = cc;
        this.user = cc.getUser();
        draw();
    }

    public void redrawComponents() {

        tfSearchBar.updateUI();
        jpSearchBar.updateUI();
        jpSearchResult.updateUI();

        jpMyOwnShowPanel.updateUI();
        jpMyShow.updateUI();

        jspSearchResult.updateUI();

        searchBarBtn.updateUI();

        draw();


    }

    public void draw() {

        drawSearchBarPanel();

        setLayout(new BorderLayout());

        add(jpSearchBar, BorderLayout.NORTH);
        add(jspSearchResult, BorderLayout.CENTER);
        jspSearchResult.getVerticalScrollBar().setUnitIncrement(16);

    }

    private void drawSearchBarPanel() {
        jpSearchBar.setSize(350, 100);
        jpSearchBar.setLayout(new FlowLayout());
        tfSearchBar.setPreferredSize(new Dimension(200, 20));
        jspSearchResult.getViewport().setBackground(UIManager.getColor("panel.background"));
        tfSearchBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tfSearchBar.setText("");
            }
        });
        tfSearchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    drawSearchResultPanel(tfSearchBar.getText());
                }
            }
        });

        searchBarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawSearchResultPanel(tfSearchBar.getText());
            }
        });

        jpSearchBar.add(tfSearchBar);
        jpSearchBar.add(searchBarBtn);
    }

    private void drawSearchResultPanel(String searchRequest) {

        jpSearchResult.removeAll();
        String[][] searchResults = cc.searchShows(searchRequest);
        if (searchResults != null) {
            jpSearchResult.setLayout(new GridLayout(searchResults.length, 2));
            System.out.println("SHOW HITTAT");
            updateSearchResults(searchResults);
        } else {
            jpSearchResult.setLayout(new BorderLayout());

            System.out.println("SHOW EJ HITTAT");
            searchRequest = "<html>" + "Your Search '" + searchRequest + "' was not found <br>" + "Have you tried the following:<br>"
                    + "- Make sure your search is spelled correctly<br>" + "- Try different keywords<br>"
                    + "- Or click the button below to create your own tracker" + "</html>";

            JLabel lbl = new JLabel("<html><font size = '3', padding-left: 50px>" + searchRequest + "</font></html>", JLabel.CENTER);
            lbl.setPreferredSize(new Dimension(jpSearchResult.getWidth() - 5, jpSearchResult.getHeight() / 2));

            ImageIcon addImage = new ImageIcon("images/notes-add.png");
            Image addImg = addImage.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            ImageIcon addImageIcon = new ImageIcon(addImg);

            btnCreateOwnShow = new JButton(addImageIcon);

            btnCreateOwnShow.addActionListener(e -> drawNoSearchResultPanel());
            jpSearchResult.add(lbl, BorderLayout.CENTER);
            jpSearchResult.add(btnCreateOwnShow, BorderLayout.SOUTH);
        }

        jspSearchResult.setViewportView(jpSearchResult);
        jpSearchResult.revalidate();

    }

    private void updateSearchResults(String[][] searchResults) {

        GridBagConstraints gbc = new GridBagConstraints();
        jpSearchResult.setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.HORIZONTAL;

        for (String[] s : searchResults) {
            JPanel mainPanel = new JPanel();
            ImageIcon imageIcon = null;
            if (s[2] != null && !s[2].equalsIgnoreCase("N/A")) {
                try {
                    URL url = new URL(s[2]);
                    Image i = ImageIO.read(url);
                    imageIcon = new ImageIcon(i);
                } catch (IOException e) {
                    imageIcon = null;
                    e.printStackTrace();
                }
            }
            if (imageIcon == null) {
                mainPanel.setPreferredSize(new Dimension(300, 30));
            } else mainPanel.setPreferredSize(new Dimension(300, 50));


            mainPanel.setLayout(new BorderLayout());
            System.out.println(s[2]);
            boolean addStart = true;
            String buttonTag = "Add";
            if (cc.getUser().containsShow(s[1])) {
                buttonTag = "Remove";
                addStart = false;
            }
            JButton btnAdd = new JButton(buttonTag);

            if (imageIcon != null) {
                JLabel imageLabel = new JLabel(new ImageIcon(imageIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)), JLabel.CENTER);
                ImageIcon finalImageIcon = imageIcon;
                imageLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JOptionPane.showMessageDialog(null, finalImageIcon);
                    }

                });
                mainPanel.add(imageLabel, BorderLayout.EAST);
            }


            btnAdd.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.WHITE),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));

            boolean finalAddStart = addStart;
            btnAdd.addActionListener(new ActionListener() {
                boolean add = finalAddStart;
                private String id = s[1];

                @Override
                public void actionPerformed(ActionEvent e) {
                    String showname = s[0];
                    String showID = s[1];
                    String showimage = s[2];
                    boolean success = false;

                    if (add) {
                        add = false;
                        success = cc.generateShow(showname, showID, showimage);

                    } else {
                        add = true;
                        success = true;

                    }
                    if (success) {
                        addRemove(s[1], btnAdd, add);
                        showFlashMsg("Show added", showname + " is added to your list");
                    } else add = true;

                }
            });
            mainPanel.add(btnAdd, BorderLayout.WEST);
            mainPanel.add(new JLabel(" " + s[0]), BorderLayout.CENTER);

            gbc.gridx = 0;
            gbc.weightx = 1;
            jpSearchResult.add(mainPanel, gbc);
        }

        JPanel panel = new JPanel();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weighty = 1;
        jpSearchResult.add(panel, gbc);

    }

    protected void drawNoSearchResultPanel() {
        jpSearchResult.removeAll();
        jpMyOwnShowPanel.removeAll();
        jpSearchResult.setLayout(new BorderLayout());
        jpMyOwnShowPanel.setLayout(new BoxLayout(jpMyOwnShowPanel, BoxLayout.Y_AXIS));
        tfShowName.setText(tfSearchBar.getText());
        JButton submit = new JButton("Submit");
        JTextField tfNbrOfSeasons = new JTextField();
        jpMyOwnShowPanel.add(new JLabel("Name: "));
        jpMyOwnShowPanel.add(tfShowName);
        jpMyOwnShowPanel.add(new JLabel("Number of Seasons"));
        jpMyOwnShowPanel.add(tfNbrOfSeasons);
        jpMyOwnShowPanel.add(submit, BorderLayout.SOUTH);
        submit.addActionListener(e -> createMyOwnShowPanel(tfNbrOfSeasons.getText()));
        jpSearchResult.add(jpMyOwnShowPanel, BorderLayout.NORTH);
        jspSearchResult.setViewportView(jpSearchResult);

    }

    protected void addRemove(String id, JButton btnAdd, boolean add) {
        if (add == false) {
            btnAdd.setText("Remove");
        } else {
            btnAdd.setText("Add");
            cc.getUser().removeShow(id);
        }

    }

    public void reset() {
        jpSearchResult.removeAll();
        tfSearchBar.setText("Enter name of the show here");
    }

    private void createMyOwnShowPanel(String input) {
        try {
            int nbrOfSeasons = Integer.parseInt(input);
            GridBagConstraints gbc = new GridBagConstraints();
            jpMyShow.setLayout(new GridBagLayout());
            gbc.fill = GridBagConstraints.HORIZONTAL;
            jpMyShow.removeAll();

            JPanel panel;
            JTextField[] tfSeasons = new JTextField[nbrOfSeasons];
            JButton btCreate = new JButton("Create");

            for (int i = 0; i < nbrOfSeasons; i++) {
                panel = new JPanel();
                panel.setPreferredSize(new Dimension(300, 40));
                panel.setLayout(new GridLayout(2, 1));
                JTextField tfNbrOfEpisodes = new JTextField();
                panel.add(new JLabel("Season" + (i + 1) + " :"));
                panel.add(tfNbrOfEpisodes);
                tfSeasons[i] = tfNbrOfEpisodes; // sätter in varje textfield i en array
                gbc.gridx = 0;
                gbc.weightx = 1;
                jpMyShow.add(panel, gbc);
            }

            btCreate.addActionListener(e -> createMyShow(tfSeasons));
            jpMyShow.add(btCreate, gbc);
            jpSearchResult.add(jpMyShow);
            jspSearchResult.setViewportView(jpSearchResult);

            JPanel pnl = new JPanel();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.weighty = 1;
            jpMyShow.add(pnl, gbc);

        } catch (Exception e) {
            System.out.println(input + " is not a valid integer number");
        }
    }

    private void createMyShow(JTextField[] tfSeasons) {
        boolean parseIntSuccess = false;
        int[] episodes = new int[tfSeasons.length];
        String seasons = "";
        try {
            for (int i = 0; i < tfSeasons.length; i++) {
                seasons = tfSeasons[i].getText();
                episodes[i] = Integer.parseInt(seasons);
            }
            parseIntSuccess = true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, seasons + " is not valid, please enter a number.");
        }
        if (parseIntSuccess) {
            Show show = new Show(tfShowName.getText());
            show.setImdbId("_" + show.getName());
            show.setDescription("This show has been added manually and therefore has no description.");
            for (int s = 0; s < tfSeasons.length; s++)
                for (int e = 0; e < episodes[s]; e++) {
                    Episode episode = new Episode(show, e + 1, s + 1);
                    episode.setName("");
                    episode.setDescription("This episode has been added manually and therefore has no description.");
                    show.addEpisode(episode);
                }

            show.sortEpisodes();
            cc.getUser().addShow(show);
        }
    }

    private void showFlashMsg(String title, String msg) {
        JOptionPane pane = new JOptionPane(msg,
                JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        final JDialog dialog = pane.createDialog(null, title);

        Timer timer = new Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
        dialog.setVisible(true);
        dialog.dispose();
    }

}