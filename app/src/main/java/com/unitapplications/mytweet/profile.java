package com.unitapplications.mytweet;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {

    CircleImageView dp ;//circle imageview
    EditText username_et,name_et;
    Button save_profile;
    String name_txt,username_txt;//to get text of editext name and username
    SharedPreferences sharedPreferences;//to save name username from here only
    Uri selectedDp;//this is uri of selected image
    String encodedDp;//this is image in form of string

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Objects.requireNonNull(getSupportActionBar()).hide();

        save_profile = findViewById(R.id.save_profile);
        username_et = findViewById(R.id.username_et);
        name_et = findViewById(R.id.name_et);
        dp = findViewById(R.id.profile_pic);

        //setting the value of name and username from here
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);

        save_profile.setOnClickListener(view -> {
            if (name_et.getText()!=null){
                name_txt = name_et.getText().toString();
                saveName(name_txt);
             }
            if (username_et.getText()!=null){
                username_txt = username_et.getText().toString();
                saveUsername(username_txt);
             }
            makeBitmap();//this make bitmap of profile pic imageview
            //and then the bitmap is converted in string that is encodedDp

            saveDp(encodedDp);//saving the encodedDp

            //sharing value to main activity
            Intent intent = new Intent(profile.this,MainActivity.class);
            intent.putExtra("name_key",name_txt);
            intent.putExtra("username_key",username_txt);
            intent.putExtra("dp_key",encodedDp);
            startActivity(intent);

        });
        dp.setOnClickListener(view -> {
            //opening gallery to select image for dp
            Intent intent = new Intent(
                    Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

    }//onCreate

    private void saveUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MainActivity.USERNAME_SAVED, username);
        editor.apply();
    }

    private void saveName(String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MainActivity.NAME_SAVED, name);
        editor.apply();
    }
    private void saveDp(String dp) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MainActivity.DP_SAVED, dp);
        editor.apply();
    }
    public void makeBitmap() {
            //this make bitmap of imageview by drawing on convas what is present
        //with in the view like we mention here dp
        //so it take its height and weight and then draw the picture into bitmap
        Bitmap bmp3 = Bitmap.createBitmap(dp.getWidth(), dp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas3 = new Canvas(bmp3);
        dp.draw(canvas3);
        bitMapToString(bmp3);//sending to convert bitmap to string format
        saveDp(encodedDp);
    }
    public void bitMapToString(Bitmap bitmap) {
        // this method converts the bitmap to string to save in shared prefs
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos1);
        byte[] arr1 = baos1.toByteArray();
       encodedDp = Base64.encodeToString(arr1, Base64.DEFAULT);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //here the picture is coming after selection from gallery
        if (resultCode == RESULT_OK && data != null) {
            selectedDp = data.getData();
            String filePath;
            if (selectedDp != null && "content".equals(selectedDp.getScheme())) {
                Cursor cursor = this.getContentResolver().query(selectedDp, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                cursor.moveToFirst();
                filePath = cursor.getString(0);
                cursor.close();
            } else {
                filePath = selectedDp.getPath();
            }


            Picasso.get()
                    .load(selectedDp)
                    .resize(dp.getWidth(), dp.getHeight())
                    .placeholder(R.drawable.sample_image_ic)
                    .centerCrop()
                    .into(dp);
            dp.setAlpha(1.0f);
            //here its is getting set to imageview using picaso

        }
    }
}