package es.upsa.mimo.musicfest.Fragments;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import es.upsa.mimo.musicfest.Adapters.CardAdapterEvent;
import es.upsa.mimo.musicfest.Model.Event;
import es.upsa.mimo.musicfest.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Event> events = new ArrayList<>();
    private CardAdapterEvent mAdapter;
    private final static String TAG = "EventsFragment";
    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance() {

       // Bundle args = new Bundle();

        EventsFragment fragment = new EventsFragment();
       // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v= inflater.inflate(R.layout.fragment_events, container, false);

        RequestQueue queue= Volley.newRequestQueue(getContext());
        Resources res = getResources();
        String url= "http://api.songkick.com/api/3.0/events.json?apikey="+res.getString(R.string.api_key)+"&artist_name=amaral";


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d(TAG,"response is: "+response);
                eventParser(response);
                mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_concert);
                mAdapter = new CardAdapterEvent(events);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Response ERROR");
            }
        });
        queue.add(stringRequest);

        return v;
    }
    public void eventParser(String response){

        try {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            int tam = jsonObject.getAsJsonObject("resultsPage").getAsJsonObject("results").getAsJsonArray("event").size();
            events.clear();
            for (int i = 0; i < tam; i++) {
                JsonObject eventObject = jsonObject.getAsJsonObject("resultsPage").getAsJsonObject("results").getAsJsonArray("event").get(i).getAsJsonObject();
                Event event = new Event();
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
                Resources res = getResources();
                String img = String.format(res.getString(R.string.api_img_eventsURL), event.getId());
                Log.d(TAG, "ulr de imagen format:" + img);
                event.setImg(img);
                // event.setEndDate( eventObject.getAsJsonObject("end").get("date").getAsString());
                events.add(event);
                Log.d(TAG, "evento: " + event.toString());
                Log.d(TAG, "from gson" + eventObject);
            }
        }catch (NullPointerException e){
            Log.e(TAG,"NullPointerException in eventParser Method");
        }
    }

}
