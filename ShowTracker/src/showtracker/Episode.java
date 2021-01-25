package showtracker;

import java.io.Serializable;

public class Episode implements Comparable<Episode>, Serializable {
    private static final long serialVersionUID = -8815667314209923140L;
    private String tvdbId;
    private String imdbId;
    private String name;
    private double episodeNumber;
    private double seasonNumber;
    private String description;
    private boolean watched = false;
    private Show show;

    public Episode(Show show, int episodeNumber, int seasonNumber) {
        this.show = show;
        this.episodeNumber = episodeNumber;
        this.seasonNumber = seasonNumber;
    }

    public void setTvdbId(String id) {
        tvdbId = id;
    }

    public String getTvdbId() {
        return tvdbId;
    }

    public void setImdbId(String id) {
        imdbId = id;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getName() {
        return name;
    }

    public double getEpisodeNumber() {
        return episodeNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Double getSeasonNumber() {
        return seasonNumber;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
        show.setLastWatched();
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Episode))
            return false;

        Episode e = (Episode) o;

        return (e.getEpisodeNumber() == episodeNumber) && (e.getSeasonNumber() == seasonNumber);
    }

    @Override
    public int compareTo(Episode o) {
        if (seasonNumber > o.getSeasonNumber())
            return 1;
        else if (seasonNumber < o.getSeasonNumber())
            return -1;
        else if (episodeNumber > o.getEpisodeNumber())
            return 1;
        else if (episodeNumber < o.getEpisodeNumber())
            return -1;
        else
            return 0;
    }
}