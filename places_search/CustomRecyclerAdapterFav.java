package com.example.jeeweonlee.places_search;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

import static com.example.jeeweonlee.places_search.SearchTable.EXTRA_TEXT_ADDRESS;
import static com.example.jeeweonlee.places_search.SearchTable.EXTRA_TEXT_IMG;
import static com.example.jeeweonlee.places_search.SearchTable.EXTRA_TEXT_NAME;
import static com.example.jeeweonlee.places_search.SearchTable.EXTRA_TEXT_PLACEID_DETAIL;

public class CustomRecyclerAdapterFav extends
        RecyclerView.Adapter<CustomRecyclerAdapterFav.MyViewHolder> {

    FavFragment frag;

    private ArrayList<MyPlace> places;
    private Context context;

    //+
    private Map<String,MyPlace> tmpFavList;


    public CustomRecyclerAdapterFav (Context context, ArrayList<MyPlace> places, FavFragment frag) {
        this.places = places;
        this.context = context;
        this.frag = frag;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyPlace p = places.get(position);
        Picasso.with(context).load(p.getImage()).into(holder.imageViewProduct);
        holder.txtName.setText(p.getName());
        holder.txtAddress.setText(p.getAddress());
        holder.favIcon.setImageResource(R.drawable.fill_heart);
        holder.favIcon.setTag("fill_heart");
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout item_row;

        public ImageView imageViewProduct;
        public TextView txtName;
        public TextView txtAddress;
        public ImageView favIcon;

        public MyViewHolder(View view) {
            super(view);
            item_row = (LinearLayout) view.findViewById(R.id.item_row);

            imageViewProduct = (ImageView) view.findViewById(R.id.imageViewProduct);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtAddress = (TextView) view.findViewById(R.id.txtAddress);
            favIcon = (ImageView) view.findViewById(R.id.favIcon);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_list_layout, parent, false);

        final MyViewHolder mviewholder = new MyViewHolder(v);
//        Toast.makeText(context, "size: " + places.size(), Toast.LENGTH_SHORT).show();
//
        mviewholder.item_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPlace selected_p = places.get(mviewholder.getAdapterPosition());

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(EXTRA_TEXT_PLACEID_DETAIL, selected_p.getPlace_id());
                intent.putExtra(EXTRA_TEXT_NAME, selected_p.getName());
                intent.putExtra(EXTRA_TEXT_IMG, selected_p.getImage());
                intent.putExtra(EXTRA_TEXT_ADDRESS, selected_p.getAddress());
                context.startActivity(intent);
            }
        });

        //
        final ImageView favIcon = (ImageView) v.findViewById(R.id.favIcon);
        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // selected MyPlace object info :: selected_p
                MyPlace selected_p = places.get(mviewholder.getAdapterPosition());

                //+ Read data from the favData
                try{
                    String file_name = "favData.tmp";
                    File file = new File(context.getFilesDir(), file_name);
//                    Toast.makeText(context, "here?", Toast.LENGTH_SHORT).show();

                    if(file.exists()) {
                        ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
                        tmpFavList = (HashMap) is.readObject();
                        is.close();
//                        Toast.makeText(context, "Open my favorite HashMap", Toast.LENGTH_SHORT).show();
                    }else{
                        tmpFavList = new HashMap<>();
//                        Toast.makeText(context, "NOTHING..", Toast.LENGTH_SHORT).show();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "ERROR: NOT Found File", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "ERROR: IO Exception", Toast.LENGTH_SHORT).show();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "ERROR: Cannot find class", Toast.LENGTH_SHORT).show();
                }

                // Remove datat from facorites table
                    Toast.makeText(context, selected_p.getName()+"was deleted from favorites", Toast.LENGTH_SHORT).show();
                favIcon.setImageResource(R.drawable.heart_outline_black);
                favIcon.setTag("fill_white");
                tmpFavList.remove(selected_p.getPlace_id());

                // Remove data from
                places.remove(selected_p);

//                Toast.makeText(context, "REMOVE! size:" + tmpFavList.size(), Toast.LENGTH_SHORT).show();

                // Write on the File
                try{
                    String file_name = "favData.tmp";
                    File file = new File(context.getFilesDir(), file_name);

                    ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
                    os.writeObject(tmpFavList);
                    os.close();
//                    Toast.makeText(context, "deleted data size:" + tmpFavList.size(), Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "ERROR: NOT Found File", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "ERROR: IO Exception", Toast.LENGTH_SHORT).show();
                }

                // refresh recycler
                frag.onResume();

            }
        });

        return mviewholder;
    }


}