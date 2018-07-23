package com.example.jeeweonlee.places_search;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.*;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class SearchTable extends AppCompatActivity {

    public static final String EXTRA_TEXT_PLACEID_DETAIL = "com.example.jeeweonlee.places_search.EXTRA_TEXT_PLACEID_DETAIL";
    public static final String EXTRA_TEXT_NAME = "com.example.jeeweonlee.places_search.EXTRA_TEXT_NAME";
    public static final String EXTRA_TEXT_IMG = "com.example.jeeweonlee.places_search.EXTRA_TEXT_IMG";
    public static final String EXTRA_TEXT_ADDRESS = "com.example.jeeweonlee.places_search.EXTRA_TEXT_ADDRESS";

    private RequestQueue requestQueue;

    private String keyword;
    private String category;
    private String distance;
    private String from;
    private String otherLoc;
    private String location;
    private boolean LOADING;

    private TextView urlView;
    private TextView responseName;
    private ViewGroup progressView;

    private ListView res_list;
    private RecyclerView recyclerView;
    private CustomRecyclerAdapter re_adapter;

    private Button next_btn;
    private Button prev_btn;
    private TextView no_place_lable;

    private ArrayList<ArrayList<MyPlace>> myPlaceBook;
    private ArrayList<MyPlace> arrayList;
    private int curPage;
    private String next_page_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LOADING = false;
        showProgressingView();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_table);
        setTitle("Search results");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        keyword = intent.getStringExtra(SearchFragment.EXTRA_TEXT_KEYWORD);
        category = intent.getStringExtra(SearchFragment.EXTRA_TEXT_CATEGORY);
        distance = intent.getStringExtra(SearchFragment.EXTRA_TEXT_DISTANCE);
        int radius = Integer.parseInt(distance)*1069;
        from = intent.getStringExtra(SearchFragment.EXTRA_TEXT_FROM);
        otherLoc = intent.getStringExtra(SearchFragment.EXTRA_TEXT_OTHERLOC);
        if(from.equals("current")){
//            location = "34.0093,-118.2584";
            location = MainActivity.cur_location;
        }else{
            location = otherLoc;
        }

        // PHP request
        String url = "http://searchtableappver.appspot.com/json/?keyword="+ keyword + "&type=" + category + "&location=" + location + "&radius=" + radius + "&from=" + from + "&func=nearby_func";
//        Toast.makeText(getApplicationContext(), "" + url, Toast.LENGTH_SHORT).show();

        requestQueue = Volley.newRequestQueue(this);

        res_list = (ListView) findViewById(R.id.res_list);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        next_btn = (Button) findViewById(R.id.next_button);
        prev_btn = (Button) findViewById(R.id.prev_button);
        no_place_lable = (TextView) findViewById(R.id.no_place_label);

        myPlaceBook = new ArrayList<>();
        arrayList = new ArrayList<>();
        curPage = 0;
        next_page_token = "";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(getApplicationContext(), "Get Json!", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(), "response:!"+response.toString(), Toast.LENGTH_SHORT).show();

                        Boolean noData_flag = true;
                        // For table contents
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");

                            for(int i=0;i<jsonArray.length();i++){

                                JSONObject result = jsonArray.getJSONObject(i);
                                arrayList.add(new MyPlace(
                                    result.getString("place_id").toString(),
                                    result.getString("name").toString(),
                                    result.getString("icon").toString(),
                                    result.getString("vicinity").toString()
                                ));
                            }

                            if(arrayList.size() == 0)
                            {noData_flag = true;}
                            else{
                                noData_flag = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //error
                        }

                        if(noData_flag){
                            no_place_lable.setVisibility(View.VISIBLE);
                        }else{
                            no_place_lable.setVisibility(View.GONE);
                        }

                        // recycler
                        re_adapter = new CustomRecyclerAdapter(SearchTable.this, arrayList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(SearchTable.this));
                        recyclerView.setAdapter(re_adapter);


                        //copy it to book
                        myPlaceBook.add(arrayList);
//                        Toast.makeText(getApplicationContext(), "myPlaceBook size:" + myPlaceBook.size(), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(), "myPlaceBook's 1st item in 1st page:" + myPlaceBook.get(0).get(0).getName(), Toast.LENGTH_SHORT).show();

                        // For next page
                        try {
                            String next_p = response.getString("next_page_token").toString();
//                            Toast.makeText(getApplicationContext(), next_p, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(getApplicationContext(), next_p.toString(), Toast.LENGTH_SHORT).show();
                            next_page_token = next_p;
                            next_btn.setEnabled(true);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            next_btn.setEnabled(false);
                        }

                        hideProgressingView();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideProgressingView();
                no_place_lable.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "ERROR: Nothing found!", Toast.LENGTH_SHORT).show();
                //error

            }

            //Fav Handle

        });

        requestQueue.add(jsonObjectRequest);
        LOADING = true;

        // Button Handler
        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curPage--;
                buttonHandler();

                // recycler
                re_adapter = new CustomRecyclerAdapter(SearchTable.this, myPlaceBook.get(curPage));
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchTable.this));
                recyclerView.setAdapter(re_adapter);

            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curPage++;
                buttonHandler();

                if(curPage <= myPlaceBook.size()-1){
                    re_adapter = new CustomRecyclerAdapter(SearchTable.this, myPlaceBook.get(curPage));
                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchTable.this));
                    recyclerView.setAdapter(re_adapter);

                }else{
                    saveNextPageToMySerchBook(next_page_token);
                }
            }
        });

    }

    private void saveNextPageToMySerchBook(String next_p) {
        LOADING = false;
        showProgressingView();

        String url = "http://searchtableappver.appspot.com/json/?page_token=" + next_p + "&func=next_func";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<MyPlace> tempList = new ArrayList<>();

                        // For table contents
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");

                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject result = jsonArray.getJSONObject(i);

                                tempList.add(new MyPlace(
                                        result.getString("place_id").toString(),
                                        result.getString("name").toString(),
                                        result.getString("icon").toString(),
                                        result.getString("vicinity").toString()
                                ));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        myPlaceBook.add(tempList);
                        re_adapter = new CustomRecyclerAdapter(SearchTable.this, myPlaceBook.get(curPage));
                        recyclerView.setLayoutManager(new LinearLayoutManager(SearchTable.this));
                        recyclerView.setAdapter(re_adapter);

                        // For next page
                        try {
                            String next_p = response.getString("next_page_token").toString();
                            next_page_token = next_p;
                            next_btn.setEnabled(true);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            next_btn.setEnabled(false);
                        }

                        hideProgressingView();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideProgressingView();
                Toast.makeText(getApplicationContext(), "Nothing found!", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    private void buttonHandler(){
        if(curPage == 0){
            prev_btn.setEnabled(false);
        }else{
            prev_btn.setEnabled(true);
        }

        if(curPage >= myPlaceBook.size()-1){
            next_btn.setEnabled(false);
        }else{
            next_btn.setEnabled(true);
        }
    }

    private void showProgressingView() {

        if (!LOADING) {
            LOADING = true;
            progressView = (ViewGroup) getLayoutInflater().inflate(R.layout.loading_spinner, null);
            View v = this.findViewById(android.R.id.content).getRootView();
            ViewGroup viewGroup = (ViewGroup) v;
            viewGroup.addView(progressView);
        }
    }

    private void hideProgressingView() {
        View v = this.findViewById(android.R.id.content).getRootView();
        ViewGroup viewGroup = (ViewGroup) v;
        viewGroup.removeView(progressView);
        LOADING = false;
    }


    /** Called when the activity is about to become visible. */
    @Override
    protected void onStart() {
        super.onStart();
//        Toast.makeText(getApplicationContext(),"The onStart() event", Toast.LENGTH_SHORT).show();
    }

    /** Called when the activity has become visible. */
    @Override
    protected void onResume() {
        super.onResume();

        if(myPlaceBook.size() != 0){
            re_adapter.notifyDataSetChanged();
        }

//        Toast.makeText(getApplicationContext(), "The onResume() event", Toast.LENGTH_SHORT).show();

    }

    /** Called when another activity is taking focus. */
    @Override
    protected void onPause() {
        super.onPause();
//        Toast.makeText(getApplicationContext(), "The onPause() event", Toast.LENGTH_SHORT).show();
    }

    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop() {
        super.onStop();
//        Toast.makeText(getApplicationContext(), "The onStop() event", Toast.LENGTH_SHORT).show();
    }


}