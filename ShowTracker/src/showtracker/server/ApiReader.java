package showtracker.server;

import javax.swing.*;
import java.util.Scanner;

public class ApiReader {

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

    public static void main(String[] args) {
        ApiReader api = new ApiReader();
       // api.seasonsAndEpisodes();
        api.getSeasons();

    }
}
