package showtracker.server;

public class ApiReader {
    String url = "http://www.omdbapi.com/?t=";
    String apiKey = "&apikey=be9629f";
    String json = "?format=JSON";
    String serie = "friends";
    public void connectToApi(){
        System.out.println(url+serie+apiKey);
    }
}
