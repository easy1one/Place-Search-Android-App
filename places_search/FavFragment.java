package com.example.jeeweonlee.places_search;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavFragment extends Fragment{
    private static final String TAG = "fav_fragment";

    private TextView no_fav_label;
    private ListView fav_listView;
    private RecyclerView fav_recyclerView;
    private CustomRecyclerAdapterFav re_adapter;

    //+
    private Map<String,MyPlace> tmpFavList;
    private ArrayList<MyPlace> favList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fav_fragment, container, false);

        // For favorites
        tmpFavList = new HashMap<>(); // from local storage
        favList = new ArrayList<>();

        fav_listView = view.findViewById(R.id.fav_listView);
        no_fav_label = view.findViewById(R.id.no_fav_label_plz);
        fav_recyclerView = view.findViewById(R.id.recyclerView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initiData();

//        Toast.makeText(getActivity(), "The onResume() event", Toast.LENGTH_SHORT).show();
    }

    /** Called when another activity is taking focus. */
    @Override
    public void onPause() {
        super.onPause();
//        Toast.makeText(getActivity(), "The onPause() event", Toast.LENGTH_SHORT).show();
    }

    /** Called when the activity is no longer visible. */
    @Override
    public void onStop() {
        super.onStop();
//        Toast.makeText(getActivity(), "The onStop() event", Toast.LENGTH_SHORT).show();

        // clear the recyclerView
        //first clear the recycler view so items are not populated twice
        favList.clear();
    }

    public void initiData() {

            //+ Read data from the favData
            try{
                String file_name = "favData.tmp";
                File file = new File(getActivity().getFilesDir(), file_name);
//                    Toast.makeText(context, "here?", Toast.LENGTH_SHORT).show();

                if(file.exists()) {
                    ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
                    tmpFavList = (HashMap) is.readObject();
                    is.close();
//                Toast.makeText(getActivity(), "Open my favorite HashMap", Toast.LENGTH_SHORT).show();
                }else{
                    tmpFavList = new HashMap<>();
//                Toast.makeText(getActivity(), "NOTHING..", Toast.LENGTH_SHORT).show();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "ERROR: NOT Found File", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "ERROR: IO Exception", Toast.LENGTH_SHORT).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "ERROR: Cannot find class", Toast.LENGTH_SHORT).show();
            }

            // for no data in myFavList
            favList.clear();
            if(tmpFavList.size() == 0){
                no_fav_label.setVisibility(View.VISIBLE);
                fav_recyclerView.setVisibility(View.GONE);

            }else{
                no_fav_label.setVisibility(View.GONE);
                fav_recyclerView.setVisibility(View.VISIBLE);

                for(MyPlace p:tmpFavList.values()){
                    favList.add(p);
                }
                // recycler
                re_adapter = new CustomRecyclerAdapterFav(getActivity(), favList, FavFragment.this);
                fav_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                fav_recyclerView.setAdapter(re_adapter);

                re_adapter.notifyDataSetChanged();

            }

//        Toast.makeText(getActivity(), "The onResume() event", Toast.LENGTH_SHORT).show();

    }



}
