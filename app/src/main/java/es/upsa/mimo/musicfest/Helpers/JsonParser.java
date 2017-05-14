package es.upsa.mimo.musicfest.Helpers;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import es.upsa.mimo.musicfest.Model.Artist;
import es.upsa.mimo.musicfest.Model.Event;
import es.upsa.mimo.musicfest.R;

/**
 * Created by Javier on 14/05/2017.
 */

public class JsonParser {

    public static ArrayList<Event> eventsParser (String response, Context context){

        ArrayList<Event> events = new ArrayList<>();
        ArrayList<Artist> performers= new ArrayList<>();

        try {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            int tam = jsonObject.getAsJsonObject("resultsPage").getAsJsonObject("results").getAsJsonArray("event").size();
            events.clear();
            for (int i = 0; i < tam; i++) {
                JsonObject eventObject = jsonObject.getAsJsonObject("resultsPage").getAsJsonObject("results").getAsJsonArray("event").get(i).getAsJsonObject();
                Event event = new Event();

                int tamPerformers = eventObject.getAsJsonArray("performance").size();
                for (int j =0;j<tamPerformers;j++) {
                    Artist artist = new Artist();
                    JsonObject artistObject = eventObject.getAsJsonArray("performance").get(j).getAsJsonObject();
                    artist.setName(artistObject.getAsJsonObject("artist").get("displayName").getAsString());
                    artist.setUri(artistObject.getAsJsonObject("artist").get("uri").getAsString());
                    artist.setId(artistObject.getAsJsonObject("artist").get("id").getAsString());
                    Log.d("jsonparser",artist.toString());
                    performers.add(artist);
                }
                event.setPerformers(performers);
                event.setType(eventObject.get("type").getAsString());
                event.setTitle(eventObject.get("displayName").getAsString());
                event.setId(eventObject.get("id").getAsString());
                event.setCity_name(eventObject.getAsJsonObject("location").get("city").getAsString());
                event.setWeb_url(eventObject.get("uri").getAsString());
                event.setVenue_name(eventObject.getAsJsonObject("venue").get("displayName").getAsString());
                event.setLat(eventObject.getAsJsonObject("location").get("lat").getAsString());
                event.setLng(eventObject.getAsJsonObject("location").get("lng").getAsString());
                event.setStartDate(eventObject.getAsJsonObject("start").get("date").getAsString());
                event.setEndDate(eventObject.getAsJsonObject("end") != null ? eventObject.getAsJsonObject("end").get("date").getAsString() : "null");
                Resources res = context.getResources();
                String img = String.format(res.getString(R.string.api_img_eventsURL), event.getId());

                event.setImg(img);
                // event.setEndDate( eventObject.getAsJsonObject("end").get("date").getAsString());
                events.add(event);
            }
        }catch (NullPointerException e){
            Log.e("aaa","NullPointerException in eventParser Method");
        }
        return events;
    }
}
