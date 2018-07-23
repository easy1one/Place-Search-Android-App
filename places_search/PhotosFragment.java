package com.example.jeeweonlee.places_search;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

public class PhotosFragment extends Fragment {
    private static final String TAG = "photo_fragment";

    private String cur_placeID;

    private GeoDataClient myGeoDataClient;
    private List<PlacePhotoMetadata> photosDataList;

    private RecyclerView recyclerView_photo;
    private int index;

    private TextView no_photo_label;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photos_fragment, container, false);

        recyclerView_photo = (RecyclerView) view.findViewById(R.id.recyclerView_photo);
        no_photo_label = (TextView) view.findViewById(R.id.no_photo_label);

        cur_placeID = DetailActivity.selected_placeId;
        myGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = myGeoDataClient.getPlacePhotos(cur_placeID);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                photosDataList = new ArrayList<>();
                PlacePhotoMetadataResponse photos = task.getResult();
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                index = 0;
                for (PlacePhotoMetadata photoMetadata: photoMetadataBuffer){
                    photoMetadata = photoMetadataBuffer.get(index).freeze();
                    photosDataList.add(photoMetadata);
                    index++;
                }

                photoMetadataBuffer.release();
                showAlbum();
            }
        });

        return view;
    }

    public void showAlbum () {

//        Toast.makeText(getActivity(), "photo size:" + photosDataList.size(), Toast.LENGTH_SHORT).show();
        if(photosDataList.size() == 0){
            no_photo_label.setVisibility(View.VISIBLE);
        }else{
            no_photo_label.setVisibility(View.GONE);
        }

        CustomPhotoAdapter myAdapter = new CustomPhotoAdapter(getActivity(), photosDataList) ;
        recyclerView_photo.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView_photo.setAdapter(myAdapter);
    }

}