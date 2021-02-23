package showtracker.server;

import com.mysql.cj.xdevapi.JsonParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiReader {
    private static HttpURLConnection connection;
    private String url = "http://www.omdbapi.com/?t=";
    private String apiKey = "&apikey=be9629f";
    private String json = "&?format=JSON";
    private String serie = "friends";
    private String season = "&season=";
    private String episode = "&Episode=";

    private JTextField nameOfSerieTxf;

    private Scanner s1 = new Scanner(System.in);
    private Scanner s2 = new Scanner(System.in);
    //private Scanner s3 = new Scanner(System.in);

    public void seasonsAndEpisodes(){
        System.out.println("Mata in en serie eller film i konsolen! ");

        System.out.println("Mata in säsong: ");

        System.out.println("Mata in avsnitt: ");

        //System.out.println(url+s1.nextLine()+season+s2.nextInt()+episode+s3.nextInt()+json+apiKey);
    }

    public void getSeasons(){
        System.out.println("Mata först in en serie/film och tryck Enter! \nMata sedan in säsongen du vill ha info om! ");
        System.out.println("(Säsongen kan endast skrivas som en siffra)");

        System.out.println(url+s1.nextLine()+season+s2.nextInt()+json+apiKey);
    }

    public static void main(String[] args) throws IOException {
        ApiReader api = new ApiReader();
       // api.seasonsAndEpisodes();
        //api.getSeasons();
        System.out.println("Mata först in en serie/film och tryck Enter! \nMata sedan in säsongen du vill ha info om! ");
        System.out.println("(Säsongen kan endast skrivas som en siffra)");
        //URL url = new URL(api.url+api.s1.nextLine()+api.season+api.s2.nextInt()+api.json+api.apiKey);
        //System.out.println(url);
       // URL url = new URL("http://www.omdbapi.com/?t=&apikey=be9629f");
        //connection = (HttpURLConnection) url.openConnection();
        //System.out.println(api.url+api.s1.nextLine()+api.season+api.s2.nextInt()+api.json+api.apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api.url+api.s1.nextLine()+api.season+api.s2.nextInt()+api.json+api.apiKey))
                .header("Accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            if(response.statusCode() == 200){

                System.out.println(response.body());
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(response.body());
                System.out.println(obj.get("Title"));

                JSONArray array = (JSONArray) obj.get("Episodes");
                for (int i = 0; i < array.size(); i++) {
                    System.out.println(array.get(i));
                }
            }
        } catch (InterruptedException | ParseException e) {
            e.printStackTrace();
        }
        /*
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int status = connection.getResponseCode();
        System.out.print("Response: "+status);
        if (status<299){
            System.out.println(" success!");
        }

         */

    }
}
