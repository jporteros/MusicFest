package es.upsa.mimo.musicfest.Fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import es.upsa.mimo.musicfest.Activities.EventDetailActivity;
import es.upsa.mimo.musicfest.Adapters.CardAdapterEvent;
import es.upsa.mimo.musicfest.Helpers.JsonParser;
import es.upsa.mimo.musicfest.Model.Event;
import es.upsa.mimo.musicfest.R;


public class AroundEventsFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
    private final static String TAG = "AroundEventsFragment";
    private static final int LOCATION_REQUEST_CODE = 1;
    private static final int PETICION_CONFIG_UBICACION = 2;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private ArrayList<Event> events= new ArrayList<>();
    private HashMap<Marker, Event> mapEvents = new HashMap<>();

    public AroundEventsFragment() {
        // Required empty public constructor
    }

    public static AroundEventsFragment newInstance() {
        AroundEventsFragment fragment = new AroundEventsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v=inflater.inflate(R.layout.fragment_around_events, container, false);

        SupportMapFragment mapFragment= SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().add(R.id.map_around,mapFragment).commit();
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()){

            buildGoogleApiClient();
            mGoogleApiClient.connect();

        }
        return v;
    }

    /**
     * Create API CLIENT
     */
    private synchronized void buildGoogleApiClient() {
        Log.d("location","buildapiclient");
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
      //  Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
      //  Log.d("location","location onconnect"+location.toString());
        if(getContext()==null){
            return;
        }


        Log.d("location","location fi check");
        mLocationRequest = new LocationRequest();
        // 30' = 1800+1000
        mLocationRequest.setInterval(1800*1000);
        mLocationRequest.setFastestInterval(1800*1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest locSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .build();
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,locSettingsRequest);

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()){
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("araoundEvents","Configuracion correcta");
                     //   LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, AroundEventsFragment.this);

                        startLocationTimer();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("araoundEvents","Configuracion required");
                        try {
                            status.startResolutionForResult(getActivity(),PETICION_CONFIG_UBICACION);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            Log.e("TAG","Error al intentar solucionar la configuracion de ubicacion");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("araoundEvents","Configuracion unavailable");
                        break;
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PETICION_CONFIG_UBICACION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationTimer();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("TAG", "El usuario no ha realizado los cambios de configuracion");
                        break;
                }
                break;
        }
    }

    public void startLocationTimer(){
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, AroundEventsFragment.this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if(mLastLocation!= null){
                double dLatitude = mLastLocation.getLatitude();
                double dLongitude = mLastLocation.getLongitude();

                Log.d("location","on loc changed"+dLatitude+dLongitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 16));
            }

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }
    }



    @Override
    public void onConnectionSuspended(int i) {
        Log.d("location","location on suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("location","lon c failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mMap.clear();
        mapEvents.clear();
        double dLatitude = mLastLocation.getLatitude();
        double dLongitude = mLastLocation.getLongitude();
        Log.d("location","on loc changed"+dLatitude+dLongitude);




        RequestQueue queue= Volley.newRequestQueue(getContext());
        Resources res = getResources();
        String url= "http://api.songkick.com/api/3.0/events.json?apikey="+res.getString(R.string.api_key)+"&location=geo:"+dLatitude+","+dLongitude;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d(TAG,"response is: "+response);
                events=JsonParser.eventsParser(response,getContext());

                Log.d(TAG,events.toString());

                MarkerOptions markerOptions;
                Marker marker;
                for (Event event : events) {LatLng eventsMap = new LatLng(Double.parseDouble(event.getLat()), Double.parseDouble(event.getLng()));
                   LatLng camera = eventsMap;
                    markerOptions=new MarkerOptions()
                            .title(event.getTitle())
                            .snippet(event.getVenue_name())
                            .position(eventsMap);
                    marker= mMap.addMarker(markerOptions);
                    mapEvents.put(marker,event);
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Response ERROR");
            }
        });
        queue.add(stringRequest);


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 16));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        Log.d("location","on map ready");
        // Controles UI
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
            @Override
            public void onInfoWindowClick(Marker marker) {
                Event e= mapEvents.get(marker);
                if(e!=null){
                    Intent intent= new Intent(getContext(), EventDetailActivity.class);
                    intent.putExtra("event",e);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            // ¿Permisos asignados?
            if (permissions.length > 0 &&
                    permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(getContext(), "Error de permisos", Toast.LENGTH_LONG).show();
            }

        }
    }
}
