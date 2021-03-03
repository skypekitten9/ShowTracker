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
        while (isShowNameValid((show.getName())))
        {
            String newName = "";
            newName = JOptionPane.showInputDialog("A show with that name already exists or the name was empty, please enter a new name.", show.getName());

            if ( newName == null) return;
            if(newName.isBlank()) continue;
            show.setName(newName);
        }
        shows.add(show);
    }

    public void updateShow(Show show) {
        if(show == null){
            return;
        }
        for (Show s : shows) {
            if (show.equals(s))
                for (Episode e : show.getEpisodes())
                    if (!s.containsById(e))
                        s.addEpisode(e);
            s.sortEpisodes();
        }
    }

    public void removeShow(String id_IMDB) {
        for (int i = shows.size()-1; i >= 0; i--) {
            if(shows.get(i).getImdbId() == null){
                shows.remove(i);
            }
        }

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

    public boolean containsShowName(String name)
    {
        for (int i = 0; i < shows.size(); i++) {

            if(shows.get(i).getName().equals(name))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isShowNameValid(String name)
    {
        if(name == null || name.isBlank() || name.isEmpty()) return true;
        return containsShowName(name);
    }

    public ArrayList<Show> getShows() {
        return shows;
    }
}