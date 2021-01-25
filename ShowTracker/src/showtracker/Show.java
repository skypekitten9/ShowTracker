package showtracker;

import java.io.Serializable;
import java.util.*;

public class Show implements Serializable {
    private static final long serialVersionUID = -7641780883231752094L;
    private String tvdbId;
    private String imdbId;
    private String name;
    private String description;
    private LinkedList<Episode> episodes = new LinkedList<>();
    private Date lastWatched;

    public Show(String name) {
        this.name = name;
        setLastWatched();
    }

    public void setTvdbId(String tvdbId) {
        this.tvdbId = tvdbId;
    }

    public String getTvdbId() {
        return tvdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addEpisode(Episode episode) {
        episodes.add(episode);
    }

    public LinkedList<Episode> getEpisodes() {
        return episodes;
    }

    public void sortEpisodes() {
        Collections.sort(episodes);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setLastWatched() {
        lastWatched = Calendar.getInstance().getTime();
    }

    public Date getLastWatched() {
        return lastWatched;
    }

    public LinkedList<Double> getSeasons() {
        LinkedList<Double> seasons = new LinkedList<>();
        for (Episode e : episodes)
            if (!seasons.contains(e.getSeasonNumber()))
                seasons.add(e.getSeasonNumber());
        Collections.sort(seasons);
        return seasons;
    }

    public LinkedList<Episode> getSeason(double d) {
        LinkedList<Episode> season = new LinkedList<>();
        for (Episode e : episodes)
            if (e.getSeasonNumber() == d)
                season.add(e);
        Collections.sort(season);
        return season;
    }

    public Episode getEpisode(double season, double episode) {
        for (Episode e : episodes)
            if (e.getSeasonNumber() == season && e.getEpisodeNumber() == episode)
                return e;
        return null;
    }

    public Episode getFirstUnwatched() {
        for (Episode e : episodes)
            if (!e.isWatched() && e.getSeasonNumber() != 0)
                return e;
        return null;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Show))
            return false;

        Show s = (Show) o;

        return s.getName().equals(name);
    }

    public boolean containsById(Episode e) {
        return episodes.stream().anyMatch(listItem -> (new IdComparator()).compare(listItem, e) == 0);
    }

    private class IdComparator implements Comparator<Episode> {

        @Override
        public int compare(Episode e1, Episode e2) {
            return (Integer.parseInt(e1.getTvdbId()) - Integer.parseInt(e2.getTvdbId()));
        }
    }
}