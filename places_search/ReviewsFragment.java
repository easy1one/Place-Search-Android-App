package com.example.jeeweonlee.places_search;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.RemoteInput;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReviewsFragment extends Fragment {
    private static final String TAG = "review_fragment";

    private RequestQueue requestQueue;

    static public String cur_placeID;
    private boolean LOADING;

    private Spinner spinner_comp;
    private Spinner spinner_order;
    private TextView no_review_label;
    private ListView ggReview_listView;
    private ListView yelpReview_listView;
    private ViewGroup progressView;

    private ArrayList<Reviews> ggReview_list;
    private ArrayList<Reviews> yelpReview_list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.reviews_fragment, container, false);
        LOADING = false;
        showProgressingView();

        cur_placeID = DetailActivity.selected_placeId;

        ggReview_list = InfoFragment.reviewsList;
        yelpReview_list = new ArrayList<>();

        // Get yelp review :: http call
        Yelp yelpFromInfo = InfoFragment.yelpInfo;

        String y_name="", y_lat = "", y_lon = "", y_city ="", y_state="", y_country="", y_postal="", y_add1 = "", y_phone="";
         y_name = yelpFromInfo.getName();
         y_lat = yelpFromInfo.getLatitude();
         y_lon = yelpFromInfo.getLongitude();
         y_city = yelpFromInfo.getCity();
         y_state = yelpFromInfo.getState();
         y_country = yelpFromInfo.getCountry();
         y_postal = yelpFromInfo.getPostal_code();
         y_add1 = yelpFromInfo.getAddress1();
         y_phone = yelpFromInfo.getPhone_num();

        String url = "http://searchtableappver.appspot.com/json/?name=" + y_name + "&latitude=" + y_lat +
                "&longitude=" + y_lon + "&city=" + y_city + "&state=" + y_state +
                "&country=" + y_country + "&postal_code=" + y_postal + "&address1=" + y_add1 +
                "&phone_num=" + y_phone + "&func=yelp_func";

        ggReview_listView = (ListView) view.findViewById(R.id.ggReview_list);
        yelpReview_listView = (ListView) view.findViewById(R.id.yelpReview_list);
        no_review_label = (TextView) view.findViewById(R.id.no_review_label);


        // Spinner1 for companies
        spinner_comp = (Spinner) view.findViewById(R.id.review_comp);

        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinnerComp, android.R.layout.simple_dropdown_item_1line);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_comp.setAdapter(spinner_adapter);
        spinner_comp.setSelection(0);

        // Spinner2 for orders
        spinner_order = (Spinner) view.findViewById(R.id.review_order);
        ArrayAdapter<CharSequence> spinner_adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.spinnerOrder, android.R.layout.simple_dropdown_item_1line);
        spinner_adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_order.setAdapter(spinner_adapter2);
        spinner_order.setSelection(0);


        spinner_comp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                String selectedComp = spinner_comp.getSelectedItem().toString();

                if(selectedComp.equals("Google Reviews")){
                    yelpReview_listView.setVisibility(View.GONE);

                    if(ggReview_list.size() == 0){
                        no_review_label.setVisibility(View.VISIBLE);
                        ggReview_listView.setVisibility(View.GONE);

                    }else{
                        no_review_label.setVisibility(View.GONE);
                        ggReview_listView.setVisibility(View.VISIBLE);
                        displayReview(view, ggReview_list, ggReview_listView, "Default Order");
                        spinner_order.setSelection(0);
                    }

//                    Toast.makeText(getActivity().getApplicationContext(), "Display gg ", Toast.LENGTH_SHORT).show();

                }else{
                    ggReview_listView.setVisibility(View.GONE);

                    if(yelpReview_list.size() == 0){
                        no_review_label.setVisibility(View.VISIBLE);
                        ggReview_listView.setVisibility(View.GONE);
                    }else{
                        no_review_label.setVisibility(View.GONE);
                        yelpReview_listView.setVisibility(View.VISIBLE);
                        displayReview(view, yelpReview_list, yelpReview_listView, "Default Order");
                        spinner_order.setSelection(0);
                    }

//                    Toast.makeText(getActivity().getApplicationContext(), "Display yelp ", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here

            }

        });

        spinner_order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                ArrayList<Reviews> compRev_list;
                ListView com_listView;

                if(spinner_comp.getSelectedItem().toString().equals("Google Reviews")){
                    compRev_list = ggReview_list;
                    com_listView = ggReview_listView;
                }else{
                    compRev_list = yelpReview_list;
                    com_listView = yelpReview_listView;
                }

                displayReview(view, compRev_list, com_listView, spinner_order.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });


        requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(getActivity().getApplicationContext(), "Get Yelp Json!", Toast.LENGTH_SHORT).show();

                        // For table contents
                        try {
                            JSONArray jsonArray = response.getJSONArray("reviews");

                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject review = jsonArray.getJSONObject(i);

                                String r_url = "";
                                String r_user_imgUrl = "";
                                String r_user_name = "";
                                String r_rating = "";
                                String r_time_created = "";
                                String r_text = "";
                                try{
                                    r_url = review.getString("url").toString();
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try{
                                    JSONObject userJson = review.getJSONObject("user");
                                    r_user_imgUrl = userJson.getString("image_url").toString();
                                    r_user_name = userJson.getString("name").toString();
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try{
                                    r_rating = review.getString("rating").toString();
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try{
                                    r_time_created = review.getString("time_created").toString();
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try{
                                    r_text = review.getString("text").toString();
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                yelpReview_list.add(new Reviews(r_url, r_user_imgUrl, r_user_name, r_rating, "", r_time_created, r_text));

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        CustomListAdapterReview adapter = new CustomListAdapterReview(
                                getActivity().getApplicationContext(), R.layout.reviews_fragment, yelpReview_list
                        );
                        yelpReview_listView.setAdapter(adapter);

                        hideProgressingView();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideProgressingView();
                no_review_label.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity().getApplicationContext(), "Nothing found!", Toast.LENGTH_SHORT).show();

            }
        });

        requestQueue.add(jsonObjectRequest);

        // Clickable ltem in the list :: gg & yelp reviews
        clickableReviewCell(ggReview_listView);
        clickableReviewCell(yelpReview_listView);

        return view;
    }

    private void clickableReviewCell(ListView lv) {
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Reviews entry= (Reviews) parent.getAdapter().getItem(position);

//                        Toast.makeText(getActivity(), "Cilcked " + entry.getAuthor_name() + "'s review", Toast.LENGTH_SHORT).show();
                        String url = entry.getAuthor_url();
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse(url.toString()));
                        startActivity(intent);
                    }
                }
        );
    }

    private void displayReview(View view, ArrayList<Reviews> list, ListView review_list, String order) {

        ArrayList<Reviews> reOrder_list = new ArrayList<>();
        for(Reviews r:list){
            reOrder_list.add(r);
        }

        if (order.equals("Highest Rating")){
            Collections.sort(reOrder_list, Reviews.HiRatingComparator);
//            Toast.makeText(getActivity().getApplicationContext(), "Click Highest Rating", Toast.LENGTH_SHORT).show();
        }else if (order.equals("Lowest Rating")){
            Collections.sort(reOrder_list, Reviews.LowRatingComparator);
//            Toast.makeText(getActivity().getApplicationContext(), "Click Lowest Rating", Toast.LENGTH_SHORT).show();
        }else if (order.equals("Most Recent")){
            Collections.sort(reOrder_list, Reviews.RecentComparator);
//            Toast.makeText(getActivity().getApplicationContext(), "Click Most Recent", Toast.LENGTH_SHORT).show();
        }else if (order.equals("Least Recent")){
            Collections.sort(reOrder_list, Reviews.OldComparator);
//            Toast.makeText(getActivity().getApplicationContext(), "Click Least Recent", Toast.LENGTH_SHORT).show();
        }else{
            // Default Order :: do nothing
//            Toast.makeText(getActivity().getApplicationContext(), "WHAT!!!", Toast.LENGTH_SHORT).show();
        }

        // Fill up the list
        CustomListAdapterReview adapter = new CustomListAdapterReview(
                getActivity().getApplicationContext(), R.layout.reviews_fragment, reOrder_list
        );
        review_list.setAdapter(adapter);
//        Toast.makeText(getActivity().getApplicationContext(), "REORDERED!", Toast.LENGTH_SHORT).show();

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
