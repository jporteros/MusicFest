package es.upsa.mimo.musicfest.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.upsa.mimo.musicfest.Activities.EventDetailActivity;
import es.upsa.mimo.musicfest.Adapters.CardAdapterEvent;
import es.upsa.mimo.musicfest.Helpers.JsonParser;
import es.upsa.mimo.musicfest.Model.Event;
import es.upsa.mimo.musicfest.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistEventsFragment extends Fragment {

    private final static String TAG = "ArtistEventsFragment";
    private CardAdapterEvent mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Event> events = new ArrayList<>();
    @BindView(R.id.edit_text_artist)
    EditText artist_edit;
    @BindView(R.id.searchButton)
    Button btn_search;

    public ArtistEventsFragment() {
        // Required empty public constructor
    }

    public static ArtistEventsFragment newInstance() {

        ArtistEventsFragment fragment = new ArtistEventsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_artist_events, container, false);

        ButterKnife.bind(this,v);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_concert);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager;
                inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(artist_edit.getWindowToken(), 0);
                final String artist = artist_edit.getText().toString();

                if (TextUtils.isEmpty(artist)) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.add_artist, Toast.LENGTH_SHORT).show();
                }else{
                    RequestQueue queue= Volley.newRequestQueue(getContext());
                    Resources res = getResources();
                    String url= "http://api.songkick.com/api/3.0/events.json?apikey="+res.getString(R.string.api_key)+"&artist_name="+artist;
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){

                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG,"response is: "+response);
                            events= JsonParser.eventsParser(response,getContext());
                            mAdapter = new CardAdapterEvent(events);
                            mAdapter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent= new Intent(getContext(), EventDetailActivity.class);
                                    intent.putExtra("event",events.get(mRecyclerView.getChildAdapterPosition(view)));
                                    startActivity(intent);
                                }
                            });
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
                }

            }
        });

        return v;
    }

}
