package com.unitapplications.mytweet;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.ViewHolder> {

    // variable for our array list and context
    private ArrayList<Temps> TempsArrayList;
    private Context context;

    // constructor
    public AdapterClass(ArrayList<Temps> TempsArrayList, Context context) {
        this.TempsArrayList = TempsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // on below line we are inflating our layout
        // file for our recycler view items.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // on below line we are setting data
        // to our views of recycler view item.
        Temps Temps = TempsArrayList.get(position);

        Picasso.get()
                .load(Temps.getTempl())
                .placeholder(R.drawable.sample_image_ic)
                .error(R.drawable.watermark_ic)
                .noFade()
                .into(holder.templ);

        holder.templ.setOnClickListener(view -> {
            sendTo(Temps.getTempl());
            Log.d("TAG1", "onBindViewHolder: " + Temps.getTempl());
            // Toast.makeText(context, "link is :"+Temps.getTempl(), Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    public int getItemCount() {
        // returning the size of our array list
        return TempsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our text views.
     //   private TextView name;
        private ImageView templ,open_web;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views
        //   name = itemView.findViewById(R.id.text);
           templ = itemView.findViewById(R.id.image);
       //    open_web = itemView.findViewById(R.id.open_web);

        }
    }
    public void sendTo(String url) {
        //send to read sends datat to read activity
        Intent intent;
        intent = new Intent(context, MainActivity.class);
        intent.putExtra("url_key", url);
        context.startActivity(intent);
    }

    public void sendToPref(String picUrl) {
        //send to read sends datat to read activity
        Intent intent;
        intent = new Intent(context, MainActivity.class);
        intent.putExtra("pic_url_key", picUrl);
        context.startActivity(intent);
    }
}