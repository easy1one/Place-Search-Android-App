package com.example.jeeweonlee.places_search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomListAdapterReview extends ArrayAdapter<Reviews> {

    ArrayList<Reviews> reviews;
    Context context;
    int resource;

    public CustomListAdapterReview(@NonNull Context context, int resource, @NonNull ArrayList<Reviews> reviews) {
        super(context, resource, reviews);
        this.reviews = reviews;
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_list_layout_review, null, true);
        }
        Reviews reviews = getItem(position);

        TextView author_name = (TextView) convertView.findViewById(R.id.name);
        author_name.setText(reviews.getAuthor_name());

        ImageView img = (ImageView) convertView.findViewById(R.id.img);
        Picasso.with(context).load(reviews.getProfile_photo_url()).into(img);

        int rate = Integer.parseInt(reviews.getRating());
        RatingBar val_rating = convertView.findViewById(R.id.val_rating);
        val_rating.setRating(rate);

        TextView con_time = (TextView) convertView.findViewById(R.id.time);
        con_time.setText(reviews.getCon_time());

        TextView text = (TextView) convertView.findViewById(R.id.text);
        text.setText(reviews.getText());

        return convertView;
    }
}
