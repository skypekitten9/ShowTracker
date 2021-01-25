package showtracker.client;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import showtracker.Show;
import showtracker.User;

public class ClientController {

    private User user;
    private Profile pnlProfile;
    private ShowList pnlShowList;
    private Home pnlHome;
    private SearchShows pnlSearchShows;
    private Login pnlLogin;
    private JFrame frame = new JFrame();
    private JPanel centerPanel = new JPanel();
    private Connection connection = new Connection("127.0.0.1", 5555);
    private JPanel bottomPanel;

    public void initiatePanels() {
        pnlProfile = new Profile(this);
        pnlShowList = new ShowList(this);
        pnlHome = new Home(this);
        pnlSearchShows = new SearchShows(this);
        pnlLogin = new Login(this);

        centerPanel.setLayout(new CardLayout());

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 5, 1, 1));

        generateNavigationButton("profile", "Profile", pnlProfile);
        generateNavigationButton("list", "ShowList", pnlShowList);
        generateNavigationButton("home", "Home", pnlHome);
        generateNavigationButton("search", "SearchShows", pnlSearchShows);
        generateNavigationButton("exit", "Logout", pnlLogin);

        setButtonsEnabled(false);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        setPanel("Logout", null);
    }

    private void drawAll() {
        pnlShowList.draw();
        pnlHome.draw();
        pnlProfile.draw();
    }

    private void generateNavigationButton(String imagePath, String text, JPanel panel) {
        ImageIcon ii = new ImageIcon("images/" + imagePath + ".png");
        Image image = ii.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ii = new ImageIcon(image);
        JButton button = new JButton(ii);
        button.addActionListener(e -> setPanel(text, null));
        bottomPanel.add(button);
        centerPanel.add(panel, text);
    }

    public void startApplication() {

        frame.add(centerPanel, BorderLayout.CENTER);
        frame.setSize(new Dimension(350, 500));
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setTitle("ShowTracker");

        // Making user the user is updated on exit
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                updateUser(user);
                ((JFrame) (e.getComponent())).dispose();
            }
        });
    }

    public void setPanel(String panel, Show s) {
        CardLayout cl = (CardLayout) (centerPanel.getLayout());

        if (panel.equals("Home"))
            pnlHome.draw();
        else if (panel.equals("ShowList"))
            pnlShowList.draw();
        else if (panel.equals("Profile"))
            pnlProfile.draw();
        else if (panel.equals("Logout")) {
            setButtonsEnabled(false);
            pnlLogin.draw();
            pnlLogin.revalidate();
            if (user != null)
                new Thread(() -> updateUser(user)).run();
            //startApplication();
        } else if (panel.equals("Info"))
            centerPanel.add(new ShowInfo(s, this), "Info");

        cl.show(centerPanel, panel);
    }

    public void setButtonsEnabled(boolean enabled) {
        Component[] buttons = bottomPanel.getComponents();
        for (Component c: buttons)
            c.setEnabled(enabled);
    }

    public User logIn(String username, String password) {
        String[] userInfo = {username, password};
        return (User) connection.packEnvelope(userInfo, "logIn");
    }

    public void signUp(String username, String password, String email) {
        String[] userInfo = {username, password, email};
        connection.packEnvelope(userInfo, "signUp");
        finalizeUser(new User(username, email, null));
    }

    public void finalizeUser(User user) {
        setUser(user);
        setButtonsEnabled(true);
        setPanel("Home", null);
        System.out.println("Welcome back!");
    }

    public String updatePassword(String username, String oldPassword, String newPassword) {
        String[] updatePassword = {username, oldPassword, newPassword};
        return (String) connection.packEnvelope(updatePassword, "updatePassword");
    }
    

    public Show updateShow(Show show) {
        return (Show) connection.packEnvelope(show, "updateShow");
    }

    public String updateUser(User user) {
        return (String) connection.packEnvelope(user, "updateUser");
    }

    public String[][] searchShows(String searchTerms) {
        return (String[][]) connection.packEnvelope(searchTerms, "searchShows");
    }

    public void generateShow(String showname, String showID) {
        String[] generateShowRequest = {showname, showID};
        Show show = (Show) connection.packEnvelope(generateShowRequest, "getShow");
        user.addShow(show);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static void main(String[] args) {
        ClientController cc = new ClientController();
        cc.initiatePanels();
        cc.startApplication();
    }
}