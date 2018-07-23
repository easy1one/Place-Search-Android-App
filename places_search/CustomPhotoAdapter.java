package com.example.jeeweonlee.places_search;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class CustomPhotoAdapter extends
        RecyclerView.Adapter<CustomPhotoAdapter.MyViewHolder> {

    private Context context;
    private List<PlacePhotoMetadata> photo_list;
    private GeoDataClient mGeoDataClient;

    public CustomPhotoAdapter(Context context, List list) {

        this.context = context;
        this.photo_list = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item_photo;
        ImageView img_photo;

        public MyViewHolder(View itemView) {
            super(itemView);
            item_photo = (LinearLayout) itemView.findViewById(R.id.item_photo);
            img_photo = (ImageView) itemView.findViewById(R.id.img_photo);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.custom_list_layout_photo, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        mGeoDataClient = Places.getGeoDataClient(context, null);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photo_list.get(position));
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap photoBitmap = photo.getBitmap();
                holder.img_photo.setImageBitmap(photoBitmap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photo_list.size();
    }
}
