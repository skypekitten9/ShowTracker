package showtracker.server;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.apache.http.HttpResponse;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import showtracker.Episode;
import showtracker.Show;

import java.io.*;
import java.net.URI;
import java.net.http.HttpRequest;
import java.sql.*;

/**
 * @author Filip Spånberg
 * DatabaseReader hanterar uppkoppling till MySQL-databasen,
 * samt hanterar förfrågningar till TheTVDB
 */
public class DatabaseReader {
    private java.sql.Connection dbConn;
    private static String createTableTitles = "CREATE TABLE IMDB_TITLES (ID VARCHAR(10) NOT NULL PRIMARY KEY,NAME VARCHAR(100));";
    private static String createTableEpisodes = "CREATE TABLE IMDB_EPISODES (ID VARCHAR(10) NOT NULL PRIMARY KEY,PARENT VARCHAR(10),SEASON SMALLINT,EPISODE INT);";
    private final int show = 1;
    private String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NTY2MjUyOTksImlkIjoiU2hvd1RyYWNrZXIiLCJvcmlnX2lhdCI6MTU1NjUzODg5OSwidXNlcmlkIjo1MjQzMDIsInVzZXJuYW1lIjoiZmlsaXAuc3BhbmJlcmdxcnMifQ.NriC7481n32bFACSLLZwSAgf9Ll835_xHwxvuAHgTmqdYRs3RT0TJhetgCwRsCSNlRMmWYoROXOrYGCGLIz8izkMIS2_OwaygqiX4XBbYMwxjdcBtuhdhy-a34WureLEdGvqAUwx6tFNYWXH27x2evNGgbOMYFyN03idqQhyqHJBcXsRtAKD9NhmrL5R33y0O8jmXyu5QT-B0FWyGJ1dQ-15PK49feRauofZ1s72uaE_xTvwlyHSZbRTX5DiOtH8FZgNGMkqvARkR0B5MoqEat24-xUyjDb5VKNkhpr9oZsJwl_nZKMm8jZrKgPHHuZ6E4CUyip38EgbqPMipXqhMg";
    private String language = "en";
    private String apiKey = "be9629f";

    public void setupDBConnection() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("ai8934");
        dataSource.setPassword("ShowTracker16");
        dataSource.setServerName("195.178.232.16");

        try {
            dbConn = dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println("DatabaseReader: " + e);
        }
    }

    public int updateSql(String statement) {
        Statement stmt = null;
        int res = -1;
        try {
            stmt = dbConn.createStatement();
            res = stmt.executeUpdate(statement);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return res;
    }

    public ResultSet selectSql(String statement) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = dbConn.createStatement();
            rs = stmt.executeQuery(statement);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                rs.close();
                stmt.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return rs;
    }

    private void readTitles() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            new BufferedInputStream(
                                    new FileInputStream("ShowTracker/files/title-basics.tsv"))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line = null;
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (line != null) {
            String statement = "INSERT INTO IMDB_TITLES (ID, NAME) VALUES";
            int i = 0;
            while (i < 1000 && line != null)
                try {
                    line = br.readLine();
                    String[] lines = line.split("\\t");
                    if (lines[1].equals("tvSeries") || lines[1].equals("tvEpisode")) {
                        statement += String.format("('%s','%s'),",
                                lines[0],
                                lines[2].replaceAll("'", "&apos;"));
                        i++;
                    }
                } catch (NullPointerException npe) {
                    System.out.println("DatabaseReader.readShows: End of text reached.");
                } catch (Exception e) {
                    System.out.println("DatabaseReader.readShows: " + e + "\n" + line);
                }

            statement = statement.substring(0, statement.length() - 1) + ";";
            try {
                updateSql(statement);
            } catch (Exception e) {
                System.out.println("DatabaseReader.readShows: " + e + "\n" + line);
            }
        }

        try {
            br.close();
        } catch (IOException e) {
            System.out.println("DatabaseReader.readTitleBasics: " + e);
        }
    }

    private void readEpisodes() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream("ShowTracker/files/title-episode.tsv"))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line = null;
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (line != null) {
            String statement = "INSERT INTO IMDB_EPISODES (ID, PARENT, SEASON, EPISODE) VALUES";
            for (int i = 0; i < 1000 && line != null; i++)
                try {
                    line = br.readLine();
                    String[] lines = line.split("\\t");
                    statement += String.format("('%s','%s',%s,%s),",
                            lines[0],
                            lines[1],
                            lines[2].equals("\\N") ? "NULL" : lines[2],
                            lines[3].equals("\\N") ? "NULL" : lines[3]);
                } catch (NullPointerException npe) {
                    System.out.println("DatabaseReader.readEpisodes: End of text reached.");
                } catch (Exception e) {
                    System.out.println("DatabaseReader.readEpisodes: " + e + "\n" + line);
                }
            statement = statement.substring(0, statement.length() - 1) + ";";
            try {
                updateSql(statement);
            } catch (Exception e) {
                System.out.println("DatabaseReader.readEpisodes: " + e + "\n" + line);
            }
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String authenticateTheTVDB() {
        JSONObject obj = new JSONObject();

        //JSON string:  {"apikey":"BK2A524N2MT0IJWU","username":"filip.spanbergqrs","userkey":"J52T5FJR4CUESBPF"}
        obj.put("apikey", "BK2A524N2MT0IJWU");
        obj.put("username", "filip.spanbergqrs");
        obj.put("userkey", "J52T5FJR4CUESBPF");

        StringEntity entity = new StringEntity(obj.toString(), ContentType.APPLICATION_JSON);

        HttpPost request = new HttpPost("https://api.thetvdb.com/login");
        request.setEntity(entity);

        JSONObject joResponse = getJSONFromRequest(request);

        token = (String) joResponse.get("token");
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public JSONObject refreshToken() {
        HttpGet request = createGet("https://api.thetvdb.com/refresh_token");
        /*HttpClient httpClient = HttpClientBuilder.create().build();
        boolean status = false;
        try {
            HttpResponse response = httpClient.execute(request);
            status = (response.getStatusLine().getStatusCode() == 200);
        } catch (Exception	 e) {
            System.out.println(e);
        }
        if (status)*/
        JSONObject ret = getJSONFromRequest(request);


        return ret;
    }


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
                .uri(URI.create("http://www.omdbapi.com/?s="+sbSearchTerms.toString()+"&type=series&apikey="+apiKey+"&format="+"json"))
                .header("Accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            //Respone from API
            java.net.http.HttpResponse<String> response = java.net.http.HttpClient.newHttpClient().send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());

            //if response is ok (200)
            if(response.statusCode() == 200){
                //if response has error message
                if(response.body().isEmpty()) return  null;
                if(response.body().contains("Incorrect IMDb ID.")) return null;
                if(response.body().contains("Too many results.")){
                    req = HttpRequest.newBuilder()
                            .uri(URI.create("http://www.omdbapi.com/?t="+sbSearchTerms.toString()+"&type=series&apikey="+apiKey+"&format="+"json"))
                            .header("Accept", "application/json")
                            .method("GET", HttpRequest.BodyPublishers.noBody())
                            .build();
                    response = java.net.http.HttpClient.newHttpClient().send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
                }
                if(response.body().contains("Series not found!")){
                    return null;
                }

                //otherwise it parses the shows and returns the double array
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(response.body());
                JSONArray array = (JSONArray) obj.get("Search");
                if(array == null)
                {
                    array = new JSONArray();
                    array.add(obj);
                }
                String[][] shows = new String[array.size()][3];
                for (int i = 0; i < array.size(); i++) {
                    JSONObject arrObj = (JSONObject) array.get(i);
                    shows[i][0] = (String) arrObj.get("Title") + " ("+ arrObj.get("Year")+")";
                    shows[i][1] = (String) arrObj.get("imdbID");
                    shows[i][2] = (String) arrObj.get("Poster");
                    System.out.println(shows[i][0]);
                    System.out.println(shows[i][1]);
                    System.out.println(shows[i][2]);
                }
                return shows;

            }
            else {
                return null;
            }
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
            return null;
        }



        /*
        HttpGet request = createGet("https://api.thetvdb.com/search/series?name=" + sbSearchTerms);
        JSONObject joResponse = getJSONFromRequest(request);
        String error = (String) joResponse.get("Error");
        if (error == null) {
            JSONArray jaResponse = (JSONArray) joResponse.get("data");


            String[][] shows = new String[jaResponse.size()][2];
            for (int i = 0; i < jaResponse.size(); i++) {
                shows[i][0] = (String) ((JSONObject) jaResponse.get(i)).get("seriesName");
                shows[i][1] = Long.toString((Long) ((JSONObject) jaResponse.get(i)).get("id"));
                System.out.println(shows[i][0]);
                System.out.println(shows[i][1]);
            }
            return shows;
        } else {
            System.out.println(error);
            return null;
        }

         */


    }

    public JSONObject searchShowID(String id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://www.omdbapi.com/?i="+id+"&apikey="+apiKey+"&format="+"json"))
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
                .uri(URI.create("http://www.omdbapi.com/?i="+id+"&apikey="+apiKey+"&format="+"json"+"&season="+season))
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

        /*
        JSONObject joResponse = getJSONFromRequest(request);
        String error = (String) joResponse.get("Error");
        if (error == null) {
            JSONArray jaResponse = (JSONArray) joResponse.get("data");
            return jaResponse;
        } else {
            System.out.println(error);
            return null;
        }

         */
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

        while (jaEpisodes != null){
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
        for (Episode e: show.getEpisodes())
            System.out.print(e.getName() + ", ");
        return show;
    }

    private HttpGet createGet(String route) {
        HttpGet get = new HttpGet(route);
        get.setHeader("Authorization", "Bearer " + token);
        get.setHeader("Accept-Language", language);
        return get;
    }

    private JSONObject getJSONFromRequest(HttpUriRequest request) {
        JSONObject joResponse = null;
        HttpClient httpClient = HttpClientBuilder.create().build();
        JSONParser parser = new JSONParser();

        try {
            HttpResponse response = httpClient.execute(request);
            InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            joResponse = (JSONObject) parser.parse(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return joResponse;
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