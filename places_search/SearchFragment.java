package com.example.jeeweonlee.places_search;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements
                            GoogleApiClient.OnConnectionFailedListener,
                            GoogleApiClient.ConnectionCallbacks{

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private TextView mNameView;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));


    private static final String TAG = "Search_Fragment";

    public static final String EXTRA_TEXT_KEYWORD = "com.example.jeeweonlee.places_search.EXTRA_TEXT_KEYWORD";
    public static final String EXTRA_TEXT_CATEGORY = "com.example.jeeweonlee.places_search.EXTRA_TEXT_CATEGORY";
    public static final String EXTRA_TEXT_DISTANCE = "com.example.jeeweonlee.places_search.EXTRA_TEXT_DISTANCE";
    public static final String EXTRA_TEXT_FROM = "com.example.jeeweonlee.places_search.EXTRA_TEXT_FROM";
    public static final String EXTRA_TEXT_OTHERLOC = "com.example.jeeweonlee.places_search.EXTRA_TEXT_OTHERLOC";

    private Button search_button;
    private Button clear_button;
    private TextView keyword_warning;
    private TextView otherLoc_warning;
    private RadioGroup radio_group;
    private AutoCompleteTextView otherLoc_input;
    private EditText keyword_input;
    private Spinner category_spinner;
    private EditText distance_input;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.search_fragment, container, false);

        keyword_input = (EditText) view.findViewById(R.id.keyword_input);
        category_spinner = (Spinner) view.findViewById(R.id.category_spinner);
        distance_input = (EditText) view.findViewById(R.id.distance_input);
        radio_group = (RadioGroup) view.findViewById(R.id.group_radio);
        otherLoc_input = (AutoCompleteTextView) view.findViewById(R.id.otherLoc_input);
        keyword_warning = (TextView) view.findViewById(R.id.keyword_warning);
        otherLoc_warning = (TextView) view.findViewById(R.id.otherLoc_warning);

        // For buttons :: Search, Clear
        search_button = (Button) view.findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchTable(view);
            }
        });
        clear_button = (Button) view.findViewById(R.id.clear_button);
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearForm(view);
            }
        });
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton other_radio = (RadioButton) view.findViewById(R.id.other_radio);

                if(checkedId == other_radio.getId()){

                    otherLoc_input.setText("");
                    otherLoc_input.setFocusable(true);
                    otherLoc_input.setEnabled(true);
                    otherLoc_input.setFocusableInTouchMode(true);

                }else{
                    otherLoc_input.setText("");
                    otherLoc_input.setHint("Text in the Location");
                    otherLoc_warning.setVisibility(View.GONE);

                    otherLoc_input.setFocusable(false);
                    otherLoc_input.setFocusableInTouchMode(false);
                    otherLoc_input.setEnabled(false);

                }
            }
        });

        // Auto-complete
        otherLoc_input.setThreshold(3);
        mNameView = (TextView) view.findViewById(R.id.name);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        mPlaceArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        otherLoc_input.setAdapter(mPlaceArrayAdapter);

        return view;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());
    }


    private void showSearchTable(View view) {

        // Get Form info :: keyword, category, distance, from, otherLoc
        EditText keyword_input = (EditText) view.findViewById(R.id.keyword_input);
        String keyword = keyword_input.getText().toString();
        Spinner category_spinner = (Spinner) view.findViewById(R.id.category_spinner);
        String category = category_spinner.getSelectedItem().toString();
        EditText distance_input = (EditText) view.findViewById(R.id.distance_input);
        String distance = distance_input.getText().toString();
        if(distance.length() == 0) {
            distance = "" + 10;
        }
        String from_txt = ((RadioButton) view.findViewById(radio_group.getCheckedRadioButtonId())).getText().toString();
        String from = "";
        String otherLoc;
        if(from_txt.equals("Current location")){
            from = "current";
            otherLoc = "";
        }else {
            from = "other";
            otherLoc = otherLoc_input.getText().toString();
        }

        // Validaton check
        boolean key_val = false;
        boolean otherLoc_val = false;
        if(keyword.trim().length() == 0){
            keyword_warning.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(),"Please fix all fields with errors",Toast.LENGTH_SHORT).show();
        }else{
            keyword_warning.setVisibility(View.GONE);
            key_val = true;
        }
        if(from.equals("other") && (otherLoc.trim().length() == 0)){
            otherLoc_warning.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(),"Please fix all fields with errors",Toast.LENGTH_SHORT).show();
        }else{
            otherLoc_warning.setVisibility(View.GONE);
            otherLoc_val = true;
        }
        if(key_val && otherLoc_val) {
//            Toast.makeText(getActivity(),"you pressed SEARCH!",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), SearchTable.class);
            intent.putExtra(EXTRA_TEXT_KEYWORD, keyword);
            intent.putExtra(EXTRA_TEXT_CATEGORY, category);
            intent.putExtra(EXTRA_TEXT_DISTANCE, distance);
            intent.putExtra(EXTRA_TEXT_FROM, from);
            intent.putExtra(EXTRA_TEXT_OTHERLOC, otherLoc);
            startActivity(intent);

        }

    }

    public void clearForm(View view) {
        ((EditText) view.findViewById(R.id.keyword_input)).setText("");
        ((Spinner) view.findViewById(R.id.category_spinner)).setSelection(0);
        ((RadioButton) view.findViewById(R.id.current_radio)).setChecked(true);
        ((EditText) view.findViewById(R.id.distance_input)).setText("");
        ((EditText) view.findViewById(R.id.otherLoc_input)).setText("");

        keyword_warning.setVisibility(View.GONE);
        otherLoc_warning.setVisibility(View.GONE);

    }

}
