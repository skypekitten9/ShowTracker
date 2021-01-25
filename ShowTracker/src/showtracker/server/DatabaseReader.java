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
import showtracker.Episode;
import showtracker.Helper;
import showtracker.Show;

import java.io.*;
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

    public String[][] searchTheTVDBShows(String searchTerms) {
        String[] arSearchTerms = searchTerms.split(" ");
        StringBuilder sbSearchTerms = new StringBuilder(arSearchTerms[0]);
        for (int i = 1; i < arSearchTerms.length; i++)
            sbSearchTerms.append("%20").append(arSearchTerms[i]);

        HttpGet request = createGet("https://api.thetvdb.com/search/series?name=" + sbSearchTerms);
        JSONObject joResponse = getJSONFromRequest(request);
        String error = (String) joResponse.get("Error");
        if (error == null) {
            JSONArray jaResponse = (JSONArray) joResponse.get("data");

            String[][] shows = new String[jaResponse.size()][2];
            for (int i = 0; i < jaResponse.size(); i++) {
                shows[i][0] = (String) ((JSONObject) jaResponse.get(i)).get("seriesName");
                shows[i][1] = Long.toString((Long) ((JSONObject) jaResponse.get(i)).get("id"));
            }
            return shows;
        } else {
            System.out.println(error);
            return null;
        }
    }

    public JSONObject searchTheTVDBShow(String id) {
        HttpGet request = createGet("https://api.thetvdb.com/series/" + id);
        JSONObject joResponse = (JSONObject) getJSONFromRequest(request).get("data");

        return joResponse;
    }

    public JSONArray getEpisodesOfShow(String id, int page) {
        HttpGet request = createGet("https://api.thetvdb.com/series/" + id + "/episodes?page=" + page);
        JSONObject joResponse = getJSONFromRequest(request);
        String error = (String) joResponse.get("Error");
        if (error == null) {
            JSONArray jaResponse = (JSONArray) joResponse.get("data");
            return jaResponse;
        } else {
            System.out.println(error);
            return null;
        }
    }

    public Show generateShow(String[] arShow) {
        System.out.println("DatabaseReader: Generating show \"" + arShow[0] + "\"...");
        JSONObject joShow = searchTheTVDBShow(arShow[1]);
        Show show = new Show((String) joShow.get("seriesName"));
        show.setDescription((String) joShow.get("overview"));
        show.setTvdbId(Long.toString((Long) joShow.get("id")));
        show.setImdbId((String) joShow.get("imdbId"));

        int page = 1;

        JSONArray jaEpisodes = getEpisodesOfShow(arShow[1], page);

        do {
            System.out.println(jaEpisodes);
            for (Object o : jaEpisodes) {
                JSONObject jo = (JSONObject) o;

                int inSeason = ((Long) jo.get("airedSeason")).intValue();
                int inEpisode = ((Long) jo.get("airedEpisodeNumber")).intValue();
                String name = (String) jo.get("episodeName");
                String tvdbId = Long.toString((Long) jo.get("id"));
                String imdbId = (String) jo.get("imdbId");
                String description = Helper.decodeUnicode((String) jo.get("overview"));

                Episode episode = new Episode(show, inEpisode, inSeason);
                episode.setTvdbId(tvdbId);
                episode.setImdbId(imdbId);
                episode.setName(name);
                episode.setDescription(description);
                show.addEpisode(episode);
            }
            page++;
            jaEpisodes = getEpisodesOfShow(arShow[1], page);
        } while (jaEpisodes != null);

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
        String[] searchRequest = {show.getName(), show.getTvdbId()};
        Show latest = generateShow(searchRequest);
        for (Episode e : latest.getEpisodes())
            if (!show.containsById(e))
                show.addEpisode(e);
        show.sortEpisodes();
        return show;
    }
}