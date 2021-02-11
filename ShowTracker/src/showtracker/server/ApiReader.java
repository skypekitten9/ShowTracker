package showtracker.server;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
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
    private Scanner s3 = new Scanner(System.in);

    public void seasonsAndEpisodes(){
        System.out.println("Mata in en serie eller film i konsolen! ");

        System.out.println("Mata in säsong: ");

        System.out.println("Mata in avsnitt: ");

        System.out.println(url+s1.nextLine()+season+s2.nextInt()+episode+s3.nextInt()+json+apiKey);
    }

    public void getSeasons(){
        System.out.println("Mata först in en serie/film och tryck Enter! \nMata sedan in säsongen du vill ha info om! ");
        System.out.println("(Säsongen kan endast skrivas som en siffra)");

        System.out.println(url+s1.nextLine()+season+s2.nextInt()+json+apiKey);
    }

    public static void main(String[] args) throws IOException {
        ApiReader api = new ApiReader();
       // api.seasonsAndEpisodes();
        api.getSeasons();
       // URL url = new URL(api.url+api.s1.nextLine()+api.season+api.s2.nextInt()+api.json+api.apiKey);
        URL url = new URL("http://www.omdbapi.com/?t=&apikey=be9629f");
        connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int status = connection.getResponseCode();
        System.out.println(status);

    }
}
