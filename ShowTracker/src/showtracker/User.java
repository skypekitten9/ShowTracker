package showtracker;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.*;

public class User implements Serializable {

    private static final long serialVersionUID = -6358452193067562790L;
    private transient ImageIcon profilePicture = null;
    private String userName;
    private String userPassword;

    private ArrayList<Show> shows = new ArrayList<>();

    public User(String userName, ImageIcon profilePicture) {
        this.userName = userName;
        this.profilePicture = profilePicture;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setProfilePicture(ImageIcon profilePicture) {
        this.profilePicture = new ImageIcon();
    }

    public ImageIcon getProfilePicture() {
        return profilePicture;
    }

    public void addShow(Show show) {
        if (shows.contains(show)) {
            int i = 1;
            String newName;
            do {
                newName = show.getName() + " (" + i++ + ")";
            } while (shows.contains(new Show(newName)));
            do {
                newName = JOptionPane.showInputDialog("A show with that name already exists, please enter a new name.", newName);
            } while (shows.contains(new Show(newName)));
            if (newName != null)
                show.setName(newName);
        }
        shows.add(show);
    }

    public void updateShow(Show show) {
        for (Show s : shows) {
            if (show.equals(s))
                for (Episode e : show.getEpisodes())
                    if (!s.containsById(e))
                        s.addEpisode(e);
            s.sortEpisodes();
        }
    }

    public void removeShow(String id_IMDB) {
        for (int i = 0; i < shows.size(); i++) {

            if(shows.get(i).getImdbId().equals(id_IMDB))
            {
                shows.remove(i);
                return;
            }
        }
        System.err.println("Show not in user library!");
    }

    public boolean containsShow(String id_IMDB)
    {
        for (int i = 0; i < shows.size(); i++) {

            if(shows.get(i).getImdbId().equals(id_IMDB))
            {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Show> getShows() {
        return shows;
    }
}