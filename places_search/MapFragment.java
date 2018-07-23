package com.example.jeeweonlee.places_search;

import android.graphics.Color;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    MapView map_mapView;
    private GoogleMap gg_map;
    private LatLng ori;
    private LatLng des;

    private PlacesAutoAdapter mPlacesAutoAdapter;
    private GeoDataClient mGeodataClient;
    private RequestQueue mrequestQueue;

    private Spinner map_spinner;
    private AutoCompleteTextView map_fromAuto;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private List<Polyline> path_list;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);

        mGeodataClient = Places.getGeoDataClient(getActivity(),null);
        mPlacesAutoAdapter = new PlacesAutoAdapter(getActivity(), mGeodataClient, BOUNDS_MOUNTAIN_VIEW, null);
        path_list = new ArrayList<>();

        map_fromAuto = (AutoCompleteTextView) view.findViewById(R.id.map_from_auto);
        map_spinner = (Spinner) view.findViewById(R.id.map_mode);
        map_mapView = (MapView) view.findViewById(R.id.map_view);

        // create Spinner
        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinnerMap, android.R.layout.simple_dropdown_item_1line);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        map_spinner.setAdapter(spinner_adapter);
        map_spinner.setSelection(0);
        map_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(map_fromAuto.getText().length() != 0) {
                    showDirection();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // create Auto complete
        map_fromAuto.setAdapter(mPlacesAutoAdapter);
        map_fromAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDirection();
            }
        });

//        // Map
        map_mapView.onCreate(savedInstanceState);

        map_mapView.onResume();

        try{
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }catch (Exception e){
            // Create Map error
            Toast.makeText(getActivity().getApplicationContext(), "MAP Initialize error", Toast.LENGTH_SHORT).show();
        }

        map_mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gg_map = googleMap;
                des = new LatLng(Double.parseDouble(InfoFragment.cur_latitude), Double.parseDouble(InfoFragment.cur_longitude));
//                Toast.makeText(getActivity().getApplicationContext(), "lat:" +InfoFragment.cur_latitude , Toast.LENGTH_SHORT).show();

                gg_map.addMarker(new MarkerOptions().position(des).title(InfoFragment.cur_name)).showInfoWindow();

                CameraPosition mCameraPosition = new CameraPosition.Builder().target(des).zoom(17).build();
                gg_map.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
            }
        });

        return view;
    }

    private void showDirection(){
        final String origin_fromAuto = map_fromAuto.getText().toString();
        String mode_fromSpinner = map_spinner.getSelectedItem().toString();

        for(Polyline p:path_list){
            p.remove(); // reset the previous path
        }
        path_list.clear();

        mrequestQueue = Volley.newRequestQueue(getActivity());

        String url = "http://searchtableappver.appspot.com/json/?originFromAuto=" + origin_fromAuto +"&desFromAuto="
                + InfoFragment.cur_latitude +","+ InfoFragment.cur_longitude + "&mode=" + mode_fromSpinner.toLowerCase() + "&func=direct_func";
//        Toast.makeText(getActivity().getApplicationContext(), "11"+url, Toast.LENGTH_SHORT).show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                            ori = new LatLng(jsonArray.getJSONObject(0).getJSONObject("start_location").getDouble("lat"),
                                            jsonArray.getJSONObject(0).getJSONObject("start_location").getDouble("lng"));
                            gg_map.addMarker(new MarkerOptions().position(ori).title(origin_fromAuto)).showInfoWindow();

                            CameraPosition mCameraPosition = new CameraPosition.Builder().target(ori).zoom(13).build();
                            gg_map.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));

                            // display direction!
                            String[] poly_path = getFullPath(jsonArray.getJSONObject(0).getJSONArray("steps"));
                            int path_len = poly_path.length;
                            for(int i=0;i<path_len;i++){
                                PolylineOptions mPolylineOptions = new PolylineOptions();
                                mPolylineOptions.color(Color.BLUE);
                                mPolylineOptions.width(10);
                                mPolylineOptions.addAll(PolyUtil.decode(poly_path[i]));

                                path_list.add(gg_map.addPolyline(mPolylineOptions));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity().getApplicationContext(), "noting from json", Toast.LENGTH_SHORT).show();
                        }

                    }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity().getApplicationContext(), "Nothing found!", Toast.LENGTH_SHORT).show();
                }
            });

        mrequestQueue.add(jsonObjectRequest);
    }

    private String[] getFullPath(JSONArray jsonArr){
        int len = jsonArr.length();
        String[] lines = new String[len];

        for(int i=0;i<len;i++){
            try{
                lines[i] = getSinglePath(jsonArr.getJSONObject(i));
            }catch (JSONException e){
                // error
            }
        }
        return lines;
    }
    private String getSinglePath(JSONObject jsonObj){
        String line = "";
        try{
            line = jsonObj.getJSONObject("polyline").getString("points");
        }catch (JSONException e){
            //
        }
        return line;
    }

}
