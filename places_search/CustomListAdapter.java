package com.example.jeeweonlee.places_search;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter<MyPlace> {

    ArrayList<MyPlace> places;
    Context context;
    int resource;

    private ImageView fav_icon;

    public CustomListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<MyPlace> places) {
        super(context, resource, places);
        this.places = places;
        this.context = context;
        this.resource = resource;
    }

    private class ViewHolder {
        public ImageView imageViewProduct;
        public TextView txtName;
        public TextView txtAddress;
        public ImageView favIcon;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_list_layout, null, true);

            // Set tag
            holder = new ViewHolder();
            holder.imageViewProduct = (ImageView)  convertView.findViewById(R.id.imageViewProduct);
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.txtAddress = (TextView) convertView.findViewById(R.id.txtAddress);
            holder.favIcon = (ImageView)  convertView.findViewById(R.id.favIcon);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MyPlace place = getItem(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewProduct);
        Picasso.with(context).load(place.getImage()).into(imageView);
        TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
        txtName.setText(place.getName());
        TextView txtAddress = (TextView) convertView.findViewById(R.id.txtAddress);
        txtAddress.setText(place.getAddress());

        fav_icon = (ImageView) convertView.findViewById(R.id.favIcon);


        // holder has its position
        holder.favIcon.setTag(position);
        // check selected fav icon
        holder.favIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // +
                int pos = (Integer) view.getTag(); // it is real selected pos

            }
        });

        return convertView;
    }



}
