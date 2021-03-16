package showtracker.server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import showtracker.Episode;
import showtracker.Show;

import java.io.*;
import java.net.URI;
import java.net.http.HttpRequest;

/**
 * @author Filip Spånberg
 * DatabaseReader hanterar uppkoppling till MySQL-databasen,
 * samt hanterar förfrågningar till TheTVDB
 */
public class DatabaseReader {

    private String apiKey = "be9629f";

    /**
     * Method to search the API for a show with the provided input.
     *
     * @param searchTerms String that contains the search term
     * @return a double string array that contains shows or null if no shows are found
     */
    public String[][] searchShows(String searchTerms) {
        String[] arSearchTerms = searchTerms.split(" ");
        StringBuilder sbSearchTerms = new StringBuilder(arSearchTerms[0]);
        for (int i = 1; i < arSearchTerms.length; i++)
            sbSearchTerms.append("%20").append(arSearchTerms[i]);

        //Building request for api
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://www.omdbapi.com/?s=" + sbSearchTerms.toString() + "&type=series&apikey=" + apiKey + "&format=" + "json"))
                .header("Accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            //Respone from API
            java.net.http.HttpResponse<String> response = java.net.http.HttpClient.newHttpClient().send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());

            //if response is ok (200)
            if (response.statusCode() == 200) {
                //if response has error message
                if (response.body().isEmpty()) return null;
                if (response.body().contains("Incorrect IMDb ID.")) return null;
                if (response.body().contains("Too many results.")) {
                    req = HttpRequest.newBuilder()
                            .uri(URI.create("http://www.omdbapi.com/?t=" + sbSearchTerms.toString() + "&type=series&apikey=" + apiKey + "&format=" + "json"))
                            .header("Accept", "application/json")
                            .method("GET", HttpRequest.BodyPublishers.noBody())
                            .build();
                    response = java.net.http.HttpClient.newHttpClient().send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
                }
                if (response.body().contains("Series not found!")) {
                    return null;
                }

                //otherwise it parses the shows and returns the double array
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(response.body());
                JSONArray array = (JSONArray) obj.get("Search");
                if (array == null) {
                    array = new JSONArray();
                    array.add(obj);
                }
                String[][] shows = new String[array.size()][3];
                for (int i = 0; i < array.size(); i++) {
                    JSONObject arrObj = (JSONObject) array.get(i);
                    shows[i][0] = (String) arrObj.get("Title") + " (" + arrObj.get("Year") + ")";
                    shows[i][1] = (String) arrObj.get("imdbID");
                    shows[i][2] = (String) arrObj.get("Poster");
                    System.out.println(shows[i][0]);
                    System.out.println(shows[i][1]);
                    System.out.println(shows[i][2]);
                }
                return shows;

            } else {
                return null;
            }
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public JSONObject searchShowID(String id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://www.omdbapi.com/?i=" + id + "&apikey=" + apiKey + "&format=" + "json"))
                .header("Accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        JSONObject joResponse = null;
        try {
            java.net.http.HttpResponse response = java.net.http.HttpClient.newHttpClient().send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            JSONParser parser = new JSONParser();
            joResponse = (JSONObject) parser.parse(response.body().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return joResponse;
    }

    public JSONArray getEpisodesOfShow(String id, int season) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://www.omdbapi.com/?i=" + id + "&apikey=" + apiKey + "&format=" + "json" + "&season=" + season))
                .header("Accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        //HttpGet request = createGet("http://www.omdbapi.com/?i="+id+"&apikey="+apiKey+"&format="+"json"+"&season="+season);
        try {
            java.net.http.HttpResponse response = java.net.http.HttpClient.newHttpClient().send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(response.body().toString());
            return (JSONArray) object.get("Episodes");

        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
            return null;
        }


    }

    public Show generateShow(String[] arShow) {
        System.out.println("DatabaseReader: Generating show \"" + arShow[0] + "\"...");
        JSONObject joShow = searchShowID(arShow[1]);
        Show show = new Show((String) joShow.get("Title"));
        show.setDescription((String) joShow.get("Plot"));
        show.setImage((String) joShow.get("Poster"));
        //show.setTvdbId(Long.toString((Long) joShow.get("id")));
        show.setImdbId((String) joShow.get("imdbID"));

        //int totalSeasons = Integer.parseInt((String) joShow.get("totalSeasons"));
        int season = 1;

        JSONArray jaEpisodes = getEpisodesOfShow(arShow[1], season);

        while (jaEpisodes != null) {
            System.out.println(jaEpisodes);
            for (Object o : jaEpisodes) {
                JSONObject jo = (JSONObject) o;

                int inSeason = season;//((Long) jo.get("airedSeason")).intValue();
                int inEpisode = Integer.parseInt((String) jo.get("Episode"));
                String name = (String) jo.get("Title");
                //String tvdbId = Long.toString((Long) jo.get("id"));
                String imdbId = (String) jo.get("imdbID");
                String description = "PLACEHOLDER XD";//Helper.decodeUnicode((String) jo.get("hehe xd"));

                Episode episode = new Episode(show, inEpisode, inSeason);
                //episode.setTvdbId(tvdbId);
                episode.setImdbId(imdbId);
                episode.setName(name);
                episode.setDescription(description);
                show.addEpisode(episode);
            }
            season++;
            jaEpisodes = getEpisodesOfShow(arShow[1], season);
        }
        show.sortEpisodes();
        System.out.println("DatabaseReader: Show created.");
        for (Episode e : show.getEpisodes())
            System.out.print(e.getName() + ", ");
        return show;
    }

    public Show updateShow(Show show) {
        String[] searchRequest = {show.getName(), show.getImdbId()};
        Show latest = generateShow(searchRequest);
        for (Episode e : latest.getEpisodes())
            if (!show.containsById(e))
                show.addEpisode(e);
        show.sortEpisodes();
        return show;
    }

}