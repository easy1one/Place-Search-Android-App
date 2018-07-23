package com.example.jeeweonlee.places_search;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    public static String selected_placeId;
    public static String selected_name;
    public static String selected_img;
    public static String selected_address;

    private TabLayout tabLayout;
    private ImageView fav_click;
    private ImageView twitter_click;

    //+
    private Map<String,MyPlace> tmpFavList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.detail_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        selected_placeId = intent.getStringExtra(SearchTable.EXTRA_TEXT_PLACEID_DETAIL);
        selected_name = intent.getStringExtra(SearchTable.EXTRA_TEXT_NAME);
        selected_img = intent.getStringExtra(SearchTable.EXTRA_TEXT_IMG);
        selected_address = intent.getStringExtra(SearchTable.EXTRA_TEXT_ADDRESS);

//        Toast.makeText(getApplicationContext(), "img:" + selected_img, Toast.LENGTH_SHORT).show();

        setTitle(selected_placeId);
        Toolbar detail_title = (Toolbar) findViewById(R.id.detail_title);
        detail_title.setTitle(selected_name);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        // Add icons on the title
        tabLayout.getTabAt(0).setIcon(R.drawable.info_outline);
        tabLayout.getTabAt(1).setIcon(R.drawable.photo);
        tabLayout.getTabAt(2).setIcon(R.drawable.map);
        tabLayout.getTabAt(3).setIcon(R.drawable.review);


        // For twitter
        twitter_click = (ImageView) findViewById(R.id.twitter_img);
        twitter_click.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

//                Toast.makeText(getApplicationContext(), "selected_name?" + selected_name, Toast.LENGTH_SHORT).show();

                StringBuilder twitter_url = new StringBuilder();
                twitter_url.append("https://twitter.com/intent/tweet?text=Check out ");
                twitter_url.append(selected_name);
                twitter_url.append(" located at ");
                twitter_url.append(selected_address);
                twitter_url.append(". Website: &url=");
                twitter_url.append(InfoFragment.cur_web);
                twitter_url.append("&hashtags=TravelAndEntertainmentSearch");

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(twitter_url.toString()));
                startActivity(intent);

            }
        });

        // For Favorites :: data
        fav_click = (ImageView) findViewById(R.id.fav_img);
        //+ Read data from the favData
        try{
            String file_name = "favData.tmp";
            File file = new File(getApplicationContext().getFilesDir(), file_name);
//                    Toast.makeText(context, "here?", Toast.LENGTH_SHORT).show();

            if(file.exists()) {
                ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
                tmpFavList = (HashMap) is.readObject();
                is.close();
//                Toast.makeText(getApplicationContext(), "Open my favorite HashMap", Toast.LENGTH_SHORT).show();
            }else{
                tmpFavList = new HashMap<>();
//                Toast.makeText(getApplicationContext(), "NOTHING..", Toast.LENGTH_SHORT).show();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "ERROR: NOT Found File", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "ERROR: IO Exception", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "ERROR: Cannot find class", Toast.LENGTH_SHORT).show();
        }

        // Check if it's already in the fav list
        if (tmpFavList.containsKey(selected_placeId)){
            // it is!
            fav_click.setTag("fill_heart");
            fav_click.setImageResource(R.drawable.fill_heart);
        }else{
            // not yet
            fav_click.setTag("fill_white");
            fav_click.setImageResource(R.drawable.fill_white);
        }

        // For Favorites :: click
        fav_click.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Add it to Favorite list
                if(fav_click.getTag().equals("fill_white")){
                    Toast.makeText(getApplicationContext(), selected_name +" was added to favorites", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(), selected_img +"", Toast.LENGTH_SHORT).show();

                    fav_click.setImageResource(R.drawable.fill_heart);
                    fav_click.setTag("fill_heart");

                    MyPlace cur_fav = new MyPlace(
                            selected_placeId,
                            selected_name,
                            selected_img,
                            selected_address
                    );
                    tmpFavList.put(selected_placeId,cur_fav);

                }else{ // Remove it to Favorite list
                    Toast.makeText(getApplicationContext(), selected_name +" was deleted from favorites", Toast.LENGTH_SHORT).show();

                    fav_click.setImageResource(R.drawable.fill_white);
                    fav_click.setTag("fill_white");
                    tmpFavList.remove(selected_placeId);
//                    Toast.makeText(getApplicationContext(), "REMOVE! size:" + tmpFavList.size(), Toast.LENGTH_SHORT).show();
                }

                // Write on the File
                try{
                    String file_name = "favData.tmp";
                    File file = new File(getApplicationContext().getFilesDir(), file_name);

                    ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
                    os.writeObject(tmpFavList);
                    os.close();
//                    Toast.makeText(getApplicationContext(), "Successfully Saved data:" + tmpFavList.size(), Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "ERROR: NOT Found File", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "ERROR: IO Exception", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter =  new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new InfoFragment(), "INFO");
        adapter.addFragment(new PhotosFragment(), "PHOTOS");
        adapter.addFragment(new MapFragment(), "MAP");
        adapter.addFragment(new ReviewsFragment(), "REVIEWS");
        viewPager.setAdapter(adapter);

    }

}
