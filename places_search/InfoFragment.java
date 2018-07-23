package com.example.jeeweonlee.places_search;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import java.util.ArrayList;
import java.util.List;

public class InfoFragment extends Fragment {
    private static final String TAG = "info_fragment";

    private RequestQueue requestQueue;

    private TextView val_address;
    private TextView val_phone;
    private TextView val_price;
    private RatingBar val_rating;
    private TextView val_ggPage;
    private TextView val_web;
    private ViewGroup progressView;

    private TableRow row_address;
    private TableRow row_phone;
    private TableRow row_price;
    private TableRow row_rating;
    private TableRow row_ggPage;
    private TableRow row_web;

    private boolean LOADING;
    static public String cur_placeID;
    static public String cur_name;
    static public String cur_latitude;
    static public String cur_longitude;
    static public String cur_web;
    static public ArrayList<Reviews> reviewsList;
    static public Yelp yelpInfo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.info_fragment, container, false);
        LOADING = false;
        showProgressingView();

        // PHP request
        cur_placeID = DetailActivity.selected_placeId;
        String url = "http://searchtableappver.appspot.com/json/?place_id=" + cur_placeID + "&func=detail_func";
//        Toast.makeText(getActivity().getApplicationContext(), "url:" + url, Toast.LENGTH_SHORT).show();

        requestQueue = Volley.newRequestQueue(getActivity());
        val_address = view.findViewById(R.id.val_address);
        val_phone = view.findViewById(R.id.val_phone);
        val_price = view.findViewById(R.id.val_price);
        val_rating = (RatingBar) view.findViewById(R.id.val_rating);
        val_ggPage = view.findViewById(R.id.val_ggPage);
        val_web = view.findViewById(R.id.val_webpage);
        row_address = view.findViewById(R.id.row_address);
        row_phone = view.findViewById(R.id.row_phone);
        row_price = view.findViewById(R.id.row_price);
        row_rating = view.findViewById(R.id.row_rating);
        row_ggPage = view.findViewById(R.id.row_ggPage);
        row_web = view.findViewById(R.id.row_webpage);

        reviewsList = new ArrayList<Reviews>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(getActivity().getApplicationContext(), "Get json:" + response.toString(), Toast.LENGTH_SHORT).show();

                        // For table contents
                        try {

                            JSONObject jsonObj = new JSONObject(response.toString());
                            JSONObject jsonDetail = jsonObj.getJSONObject("result");

                            // 1. address
                            try{
                                String address = jsonDetail.getString("vicinity");
                                val_address.setText(address);

                            }catch (JSONException e) {
                                row_address.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                            // 2. phone number
                            try{
                                final String phone = jsonDetail.getString("formatted_phone_number").toString();
                                val_phone.setText(phone);
                                val_phone.setMovementMethod(LinkMovementMethod.getInstance());
                                String text = "<a href=''>" + phone + "</a>";
                                val_phone.setText(Html.fromHtml(text));
                                val_phone.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        String tel = "tel:" + phone;
                                        intent.setData(Uri.parse(tel));
                                        startActivity(intent);
                                    }
                                });

                            }catch (JSONException e) {
                                e.printStackTrace();
                                row_phone.setVisibility(View.GONE);
                            }
                            // 3. price level
                            try{
                                String price = jsonDetail.getString("price_level").toString();
                                String dollar="";
                                if(price.equals("1")) dollar="$";
                                else if(price.equals("2")) dollar="$$";
                                else if(price.equals("3")) dollar="$$$";
                                else if(price.equals("4")) dollar="$$$$";
                                else dollar="$$$$$";
                                val_price.setText(dollar);

                            }catch (JSONException e) {
                                e.printStackTrace();
                                row_price.setVisibility(View.GONE);
                            }
                            // 4. rating
                            try{
                                String rating = jsonDetail.getString("rating").toString();
//                                val_rating.setText(rating);
                                val_rating.setRating(Float.parseFloat(rating));

                            }catch (JSONException e) {
                                e.printStackTrace();
                                row_rating.setVisibility(View.GONE);
                            }
                            // 5. google url
                            try{
                                String ggPage = jsonDetail.getString("url").toString();
                                val_ggPage.setMovementMethod(LinkMovementMethod.getInstance());
                                String text = "<a href='" + ggPage + "'>" + ggPage + "</a>";
                                val_ggPage.setText(Html.fromHtml(text));

                            }catch (JSONException e) {
                                e.printStackTrace();
                                row_ggPage.setVisibility(View.GONE);
                            }
                            // 6. website
                            try{
                                String web = jsonDetail.getString("website").toString();
                                cur_web = web;
                                val_web.setMovementMethod(LinkMovementMethod.getInstance());
                                String text = "<a href='" + web + "'>" + web + "</a>";
                                val_web.setText(Html.fromHtml(text));

                            }catch (JSONException e) {
                                e.printStackTrace();
                                row_web.setVisibility(View.GONE);
                            }
                            // 8. reviews
                            try{
                                JSONArray review_arr = jsonDetail.getJSONArray("reviews");

                                for(int i=0;i<review_arr.length();i++){
                                    JSONObject reviewDetail = review_arr.getJSONObject(i);

                                    reviewsList.add(new Reviews(
                                            reviewDetail.getString("author_url").toString(),
                                            reviewDetail.getString("profile_photo_url").toString(),
                                            reviewDetail.getString("author_name").toString(),
                                            reviewDetail.getString("rating").toString(),
                                            reviewDetail.getString("time").toString(),
                                            "",
                                            reviewDetail.getString("text").toString()
                                    ));

                                }
                            }catch (JSONException e) {
                                e.printStackTrace();
                                // EMPTY REVIEW SESSION! ERROR HANDLE HERE!!!
                            }

//                                Toast.makeText(getActivity().getApplicationContext(), "before" + cur_name , Toast.LENGTH_SHORT).show();

                                // For yelp
                                String name = "";
                                String latitude = "";
                                String longitude = "";
                                String city = "";
                                String state = "";
                                String country = "";
                                String postal_code = "";
                                String address1 = "";
                                String phone_num = "";
                                // (1) name
                                try {
                                    name = jsonDetail.getString("name").toString();
                                    cur_name = name;

                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // (2,3) latitude, longitude
                                try {
                                    JSONObject geoJson = jsonDetail.getJSONObject("geometry");
                                    JSONObject locJson = geoJson.getJSONObject("location");
                                    latitude = locJson.getString("lat").toString();
                                    longitude = locJson.getString("lng").toString();

                                    cur_latitude = latitude;
                                    cur_longitude = longitude;

                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // (4,5,6) city, state, country, postal_code
                                try {
                                    JSONArray addCompJson = jsonDetail.getJSONArray("address_components");
                                    for(int i=0;i<addCompJson.length();i++){
                                        JSONObject addCom = addCompJson.getJSONObject(i);
                                        JSONArray typesJson = addCom.getJSONArray("types");
                                        String type = typesJson.get(0).toString();
                                        if(type.equals("country")){
                                            country = addCom.getString("short_name");
                                        }else if(type.equals("administrative_area_level_1")){
                                            state = addCom.getString("short_name");
                                        }else if(type.equals("locality")) {
                                            city = addCom.getString("short_name");
                                        }else if(type.equals("postal_code")){
                                            postal_code = addCom.getString("short_name");
                                        }
                                    }

                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // (7) address1
                                try {
                                    String address1Json = jsonDetail.getString("formatted_address").toString();
                                    // FIX it? ->Get only street address???
                                    String[] tmp_address1 = address1Json.split(",");
                                    address1 = tmp_address1[0];

                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // (8) phone_num
                                try {
                                    phone_num = jsonDetail.getString("international_phone_number").toString().replaceAll("[()\\s-]+", "");

                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                yelpInfo = new Yelp(name, latitude, longitude, city, state, country, postal_code, address1, phone_num);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity().getApplicationContext(), "Nothing found!", Toast.LENGTH_SHORT).show();
                        }


                        hideProgressingView();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideProgressingView();
                Toast.makeText(getActivity().getApplicationContext(), "Nothing found!", Toast.LENGTH_SHORT).show();

            }
        });

        requestQueue.add(jsonObjectRequest);





        return view;
    }
    private void showProgressingView() {

        if (!LOADING) {
            LOADING = true;
            progressView = (ViewGroup) getLayoutInflater().inflate(R.layout.loading_spinner, null);
            View v = getActivity().findViewById(android.R.id.content).getRootView();
            ViewGroup viewGroup = (ViewGroup) v;
            viewGroup.addView(progressView);
        }
    }

    private void hideProgressingView() {
        View v = getActivity().findViewById(android.R.id.content).getRootView();
        ViewGroup viewGroup = (ViewGroup) v;
        viewGroup.removeView(progressView);
        LOADING = false;
    }
}
