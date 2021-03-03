
package showtracker.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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

    private void draw() {

        drawSearchBarPanel();

        setLayout(new BorderLayout());

        add(jpSearchBar, BorderLayout.NORTH);
        add(jspSearchResult, BorderLayout.CENTER);

    }

    private void drawSearchBarPanel() {
        jpSearchBar.setBackground(Color.GREEN);
        jpSearchBar.setSize(350, 100);
        jpSearchBar.setLayout(new FlowLayout());
        tfSearchBar.setPreferredSize(new Dimension(200, 20));

        JButton searchBarBtn = new JButton("search");
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

            JLabel lbl = new JLabel("<html><font size = '3', padding-left: 50px>" + searchRequest + "</font></html>");
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

            mainPanel.setPreferredSize(new Dimension(300, 30));

            mainPanel.setLayout(new BorderLayout());

            boolean addStart = true;
            String buttonTag = "Add";
            if (cc.getUser().containsShow(s[1]))
            {
                buttonTag = "Remove";
                addStart = false;
            }
            JButton btnAdd = new JButton(buttonTag);

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
                    boolean success = false;

                    if (add) {
                        add = false;
                        success = cc.generateShow(showname, showID);
                    } else
                    {
                        add = true;
                        success = true;
                    }
                    if(success) addRemove(s[1], btnAdd, add);
                    else add = true;
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

    public void reset()
    {
        jpSearchResult.removeAll();
        tfSearchBar.setText("Enter name of the show here");
    }

    public static void main(String[] args) {
        ClientController cc = new ClientController();
        SearchShows ss = new SearchShows(cc);

        JFrame frame = new JFrame();
        frame.add(ss);
        frame.setSize(new Dimension(350, 500));
        frame.setVisible(true);
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
            show.setImdbId("_"+show.getName());
            show.setDescription("This show has been added manually and therefore has no description.");
            for (int s = 0; s < tfSeasons.length; s++)
                for (int e = 0; e < episodes[s]; e++){
                    Episode episode = new Episode(show, e + 1, s + 1);
                    episode.setName("");
                    episode.setDescription("This episode has been added manually and therefore has no description.");
                    show.addEpisode(episode);
                }

            show.sortEpisodes();
            cc.getUser().addShow(show);
            JOptionPane.showMessageDialog(null, "The show has been added");
        }
    }
}