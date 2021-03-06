package es.upsa.mimo.musicfest.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.internal.Utils;
import es.upsa.mimo.musicfest.Activities.EventDetailActivity;
import es.upsa.mimo.musicfest.Adapters.CardAdapterEvent;
import es.upsa.mimo.musicfest.Adapters.PlaceAutocompleteAdapter;
import es.upsa.mimo.musicfest.Helpers.JsonParser;
import es.upsa.mimo.musicfest.Manifest;
import es.upsa.mimo.musicfest.Model.Event;
import es.upsa.mimo.musicfest.R;

import static android.R.attr.bitmap;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Event> events = new ArrayList<>();

    private SimpleDateFormat dateFormatter;
    private double lat,lon;
    private CardAdapterEvent mAdapter;
    private PlaceAutocompleteAdapter mPlaceAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private String direc, cityBD;
    protected static GoogleApiClient mGoogleApiClient;
    private Calendar calendar;
    private Geocoder mGeocoder;
    private final static String TAG = "EventsFragment";
    private static final LatLngBounds BOUNDS = new LatLngBounds(new LatLng(-85, -180), new LatLng(85, 180));

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

        if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
            try {
                buildGoogleApiClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        mAutocompleteView = (AutoCompleteTextView)
                v.findViewById(R.id.autoCompleteTextView);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_concert);

        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAdapter = new PlaceAutocompleteAdapter(getContext(), mGoogleApiClient, BOUNDS,
                null);
        mAutocompleteView.setAdapter(mPlaceAdapter);

        mGeocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


/*
        RequestQueue queue= Volley.newRequestQueue(getContext());
        final Resources res = getResources();
        String url= "http://api.songkick.com/api/3.0/events.json?apikey="+res.getString(R.string.api_key)+"&artist_name=amaral";


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d(TAG,"response is: "+response);
                events= JsonParser.eventsParser(response,getContext());
              //  eventParser(response);
                mAdapter = new CardAdapterEvent(events);
                mAdapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG,"Evento clickado"+events.get(mRecyclerView.getChildAdapterPosition(view)).toString());
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
*/
        return v;
    }
    private synchronized void buildGoogleApiClient() {
        Log.d("location","buildapiclient");
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onStart() {
        Log.d(TAG,"Onstart");
        if (mGoogleApiClient != null) {
            Log.d(TAG,"Onstart dentro");
            mGoogleApiClient.connect();
        }

        super.onStart();
    }

    @Override
    public void onStop() {
        Log.d(TAG,"OnStop");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    //Places
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            InputMethodManager inputMethodManager;
            inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mAutocompleteView.getWindowToken(), 0);

            final AutocompletePrediction item = mPlaceAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i("newEventplace", "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            //Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
            //       Toast.LENGTH_SHORT).show();
            Log.i("newEventplace", "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("newEventplace", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);


            direc=place.getAddress().toString();

            lat=place.getLatLng().latitude;
            lon=place.getLatLng().longitude;
            direc=place.getAddress().toString();

            Log.d("CITYBD",lat+"____"+lon);

            RequestQueue queue= Volley.newRequestQueue(getContext());
            Resources res = getResources();
            Calendar calendar2= Calendar.getInstance();
            calendar2.add(Calendar.DAY_OF_MONTH,30);

            Date date_start= Calendar.getInstance().getTime();
            Date date_end= calendar2.getTime();
            String dateStart = dateFormatter.format(date_start);
            String dateEnd = dateFormatter.format(date_end);

            String url= "http://api.songkick.com/api/3.0/events.json?apikey="+res.getString(R.string.api_key)+"&location=geo:"+lat+","+lon+"&min_date="+dateStart+"&max_date="+dateEnd;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    Log.d(TAG,"response is: "+response);
                    events=JsonParser.eventsParser(response,getContext());
                    mAdapter = new CardAdapterEvent(events);
                    mAdapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG,"Evento clickado"+events.get(mRecyclerView.getChildAdapterPosition(view)).toString());
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
            // Format details of the place for display and show it in a TextView.
      /*      mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }

            Log.i("newEventplace", "Place details received: " + place.getName());
*/
            places.release();

        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e("newEventplace", res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e("newEventplace", "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getContext(),
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }
}
