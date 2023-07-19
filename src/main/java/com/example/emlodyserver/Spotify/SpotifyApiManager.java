package com.example.emlodyserver.Spotify;

import com.example.emlodyserver.Playlist;
import com.example.emlodyserver.Response.Errors;
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Base64;

public class SpotifyApiManager {
    private final String CLIENT_ID="a7bff5059afb4b969966df56c651f6e8";
    private final String CLIENT_SECRET="24d87c25cc524e59ba0fa5b4d36cd157";

    private String imageUrl;

    public Playlist getPlaylistUrl(String emotion) throws IOException {
        String token=getSpotifyAccessToken();
        if(token!=null) {
            OkHttpClient client = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/search").newBuilder();
            urlBuilder.addQueryParameter("q", emotion + " Mix");
            urlBuilder.addQueryParameter("type", "playlist");
            String url = urlBuilder.build().toString();
            System.out.println(url);

            Request request = new Request.Builder()
                    .header("Authorization", "Bearer "+token)
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response.code()==200)
                {
                    String responseString = response.body().string();
                    return getPlaylistUrlFromJson(responseString);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getSpotifyAccessToken() {
        OkHttpClient client = new OkHttpClient();

        String clientCredentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedCredentials = Base64.getEncoder().encodeToString(clientCredentials.getBytes());

        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build();

        Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .post(requestBody)
                .addHeader("Authorization", "Basic " + encodedCredentials)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseString = response.body().string();
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(responseString);
            JSONObject jsonObject=new JSONObject(jsonResponse);
            System.out.println(responseString);
            System.out.println("TOKEN: "+jsonObject.get("access_token").toString());
            return jsonObject.get("access_token").toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    private Playlist getPlaylistUrlFromJson(String jsonString) throws Exception {
        String imageUrl;
        JSONParser parser = new JSONParser();
        JSONObject jsonResponse = (JSONObject) parser.parse(jsonString);
        JSONObject jsonObject=new JSONObject(jsonResponse);
        JSONObject playlists= getJsonPlaylists(jsonObject);
        JSONArray items= getJsonItems(playlists);
        JSONObject urls= getJsonExternalUrls(items);
        imageUrl=getImageUrl(items);
        String playlistUrl= getJsonPlaylistUrls(urls);
        System.out.println(playlistUrl);
        Playlist playlist=new Playlist(playlistUrl,imageUrl);
        return playlist;

    }
    private JSONObject getJsonPlaylists(JSONObject spotifyJson) throws Exception {
        if(spotifyJson!=null){
            return (JSONObject) spotifyJson.get("playlists");
        }
        throw new Exception(Errors.getSomethingWentWrong());
    }
    private  JSONArray getJsonItems(JSONObject spotifyJson) throws Exception {
        if(spotifyJson!=null){
            return (JSONArray) spotifyJson.get("items");
        }
        throw new Exception(Errors.getSomethingWentWrong());
    }
    private String getImageUrl(JSONArray spotifyJson){
        return  (String) ((JSONObject)((JSONArray)((JSONObject) spotifyJson.get(0)).get("images")).get(0)).get("url");

    }
    private JSONObject getJsonExternalUrls(JSONArray spotifyJson) throws Exception {
        if(spotifyJson!=null){
            return  (JSONObject) ((JSONObject) spotifyJson.get(0)).get("external_urls");
        }
        throw new Exception(Errors.getSomethingWentWrong());
    }
    private String getJsonPlaylistUrls(JSONObject spotifyJson) throws Exception {
        if(spotifyJson!=null){
            return  (String) spotifyJson.get("spotify");
        }
        throw new Exception(Errors.getSomethingWentWrong());
    }
    public String getImagePlaylistUrl() throws IOException {
        return imageUrl;
    }
}