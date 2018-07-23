package com.example.jeeweonlee.places_search;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    static public String cur_location;
    private Double cur_lat;
    private Double cur_lon;

    static public ArrayList<MyPlace> myFavList;
    static public Map<String,Integer> findFavIndex; // place_id, index in the myFavList

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Places Search");

        myFavList = new ArrayList<>();
        findFavIndex = new HashMap<>();

        // To get current location
        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(getApplicationContext(), "Permission denied to get location", Toast.LENGTH_SHORT).show();
        }else {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            try {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            cur_lat = currentLocation.getLatitude();
                            cur_lon = currentLocation.getLongitude();
                            cur_location = cur_lat.toString() +","+ cur_lon.toString();
//                            Toast.makeText(getApplicationContext(), "lat:" + currentLocation.getLatitude() + ",lon:" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to get current location:", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (SecurityException e) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }
        }


        //
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // icon on title
        tabLayout.getTabAt(0).setIcon(R.drawable.search);
        tabLayout.getTabAt(1).setIcon(R.drawable.fill_white);

    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchFragment(), "SEARCH");
        adapter.addFragment(new FavFragment(), "FAVORITES");
        viewPager.setAdapter(adapter);

    }

    public Map<String,MyPlace> tmpFavList;
    public void clearLocalData() {

        //+ Read data from the favData
        try{
            String file_name = "favData.tmp";
            File file = new File(this.getFilesDir(), file_name);
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
            Toast.makeText(this, "ERROR: NOT Found File", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR: IO Exception", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR: Cannot find class", Toast.LENGTH_SHORT).show();
        }

        tmpFavList.clear();

        // Write on the File
        try{
            String file_name = "favData.tmp";
            File file = new File(this.getFilesDir(), file_name);

            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
            os.writeObject(tmpFavList);
            os.close();
//                Toast.makeText(this, "Clear the data:" + tmpFavList.size(), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR: NOT Found File", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR: IO Exception", Toast.LENGTH_SHORT).show();
        }

//        Toast.makeText(this, "The onResume() event", Toast.LENGTH_SHORT).show();

    }

}