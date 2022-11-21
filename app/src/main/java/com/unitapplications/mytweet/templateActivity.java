package com.unitapplications.mytweet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;


public class templateActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterClass adapter;
    private ArrayList<Temps> templateList;
    FirebaseDatabase fbDb;
    TextView photo_bt,ill_bt; 
    Button new_tmp_bt;
    Temps templates;
    DatabaseReference getImg;
    DatabaseReference DatabaseRef;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(templateActivity.this,MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
        Objects.requireNonNull(getSupportActionBar()).hide(); //hide action bar

       templates = new Temps(); //model class
        recyclerView = findViewById(R.id.recyclerView);
        photo_bt = findViewById(R.id.photo_edit);
        ill_bt = findViewById(R.id.ill_edit);
        templateList = new ArrayList<>();

        init();//set recyclerview

        fbDb = FirebaseDatabase.getInstance();
        DatabaseReference DatabaseRef = fbDb.getReference();
        DatabaseReference getImg = DatabaseRef.child("Temps/Temps");//this is path of database
        getImg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                templateList.clear();
                //in this method we are getting value from database
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //this is get every single value in to templatelist(arraylist)
                    Temps tempss = dataSnapshot.getValue(Temps.class);
                    templateList.add(tempss);
                    init();//then set to recylverview
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        photo_bt.setOnClickListener(view -> {
            
            photo_bt.setBackgroundColor(getResources().getColor(R.color.grey_900));
            ill_bt.setBackgroundColor(getResources().getColor(R.color.cream));
            photo_bt.setTextColor(getResources().getColor(R.color.white));
            ill_bt.setTextColor(getResources().getColor(R.color.teal_700));
              });
        ill_bt.setOnClickListener(view -> {
            
            ill_bt.setBackgroundColor(getResources().getColor(R.color.grey_900));
            photo_bt.setBackgroundColor(getResources().getColor(R.color.cream));
            ill_bt.setTextColor(getResources().getColor(R.color.white));
            photo_bt.setTextColor(getResources().getColor(R.color.teal_700));
                    });

        if (isConnected()) {//to check internet connection status
            log("Internet Connected");
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }//onCreate

    public boolean isConnected() {
        //method to check connection of internet
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    private void log(String l) {
        Log.d("taggy",l);
    }
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    public void init(){
        //i am using this init function to make sure the recylerview not adding all notes again and again
        recyclerView.hasFixedSize();
        Collections.shuffle(templateList);//to shuffle the items
        GridLayoutManager layoutManager = new GridLayoutManager(templateActivity.this, 2);
        adapter = new AdapterClass(templateList, templateActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

}