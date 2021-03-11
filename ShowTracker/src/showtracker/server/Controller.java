package showtracker.server;

import com.formdev.flatlaf.FlatLightLaf;
import showtracker.Envelope;
import showtracker.Helper;
import showtracker.Show;
import showtracker.User;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;

public class Controller {
    private DatabaseReader dbr = new DatabaseReader();
    private GUI gui = new GUI(this);
    private Connection connection = new Connection(this);
    public static final boolean debug = true;
    private HashMap<String, String> users = new HashMap<>();

    public Controller() {
        gui.start();
        File foFiles = new File("files/");
        File foUsers = new File("files/users/");
        if (!foFiles.exists())
            foFiles.mkdir();
        if (!foUsers.exists())
            foUsers.mkdir();

        if (new File("files/users.obj").exists())
            users = (HashMap<String, String>) Helper.readFromFile("files/users.obj");
        if (new File("files/token.obj").exists())
            dbr.setToken((String) Helper.readFromFile("files/token.obj"));
    }

    Envelope receiveEnvelope(Envelope input) {
        Envelope returnEnvelope = null;

        switch (input.getType()) {
            case "searchShows":
                String searchTerms = (String) input.getContent();
                String[][] response = dbr.searchShows(searchTerms);
                returnEnvelope = new Envelope(response, "shows");
                break;
            case "getShow":
                String[] episodeQuery = (String[]) input.getContent();
                Show show = dbr.generateShow(episodeQuery);
                returnEnvelope = new Envelope(show, "show");
                break;
            case "signUp":
                String[] signup = (String[]) input.getContent();
                returnEnvelope = signUp(signup);
                break;
            case "logIn":
                String[] login = (String[]) input.getContent();
                returnEnvelope = loginUser(login);
                break;
            case "updateUser":
                User usUpdate = (User) input.getContent();
                returnEnvelope = updateUser(usUpdate);
                break;
            case "updateShow":
                Show shUpdate = (Show) input.getContent();
                shUpdate = dbr.updateShow(shUpdate);
                returnEnvelope = new Envelope(shUpdate, "updated");
                break;

            case "updatePassword":
                String[] password = (String[]) input.getContent();
                returnEnvelope = updatePass(password);
                break;
            case "isUsernameAvailable":
                String userName = (String) input.getContent();
                returnEnvelope = isUsernameAvailable(userName);
                break;

        }
        return returnEnvelope;
    }

    private Envelope isUsernameAvailable(String userName)
    {
        File userFile = new File("files/users/" + userName + ".usr" );
        return new Envelope(!userFile.exists(), "reply");
    }

    private Envelope updatePass(String[] userInfo) {
        String password = users.get(userInfo[0]);
        if (password.equals(userInfo[1])) {
            users.put(userInfo[0], userInfo[2]);
            Helper.writeToFile(users, "files/users/" + userInfo[0] + ".usr" ); ///
            return new Envelope("Password changed", "reply");
        } else {
            return new Envelope("No match with current password!", "reply");
        }
    }

    public Envelope signUp(String[] userInfo) {
        String stUserName = users.get(userInfo[0]);
        if (stUserName == null) {
            User user = new User(userInfo[0], null);
            synchronized (this) {
                users.put(userInfo[0], userInfo[1]);
                Helper.writeToFile(users, "files/users.obj");
                Helper.writeToFile(user, "files/users/" + userInfo[0] + ".usr");
            }
            return new Envelope("User registered", "signin");
        } else {
            return new Envelope("Username already taken", "signin");
        }
    }

    private Envelope loginUser(String[] userInfo) {
        User user = null;
        String password = users.get(userInfo[0]);
        if (password != null && password.equals(userInfo[1]))
            user = (User) Helper.readFromFile("files/users/" + userInfo[0] + ".usr");
        return new Envelope(user, "user");
    }

    private Envelope updateUser(User user) {
        if (user != null) {
            Helper.writeToFile(user, "files/users/" + user.getUserName() + ".usr");
            return new Envelope("Profile saved", "confirmation");
        } else {
            return new Envelope("Failed to save profile.", "rejection");
        }
    }

    void startConnection(int threads) {
        connection.startConnection(threads);
    }

    void stopConnection() {
        System.out.println("Controller exiting...");
        connection.stopConnection();
        System.out.println("Controller exited.");
    }

    void setThreadCount(int i) {
        gui.setActiveThreads(i);
    }



    public static void main(String[] args) {
        FlatLightLaf.install();
        try {
            UIManager.setLookAndFeel( "com.formdev.flatlaf.FlatDarkLaf" ); //Funkar


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println( "Failed to initialize LaF" );
        }

        Controller controller = new Controller();
    }

    // TODO: ta bort kod efter testning

//    public String authenticateTheTVDB() {
//        String token = dbr.authenticateTheTVDB();
//        Helper.writeToFile(token, "files/token.obj");
//        return token;
//    }

}