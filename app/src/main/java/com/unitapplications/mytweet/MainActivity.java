package com.unitapplications.mytweet;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import java.util.UUID;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LAST_LINK = "last_link";
    public static final String LAST_TEXT = "last_text";
    public static final String NAME_SAVED = "name_text";
    public static final String USERNAME_SAVED = "username_text";
    public static final String DP_SAVED = "dp_string";
    public static final String DATE_CHECK ="date_check";
    public static final String PLATFORM_CHECK = "platform_check";

    ContentResolver resolver;
    SharedPreferences sharedPreferences;
    String getLastLink, getLastText,getName,getUsername,getDp;
    ImageView imageView, text_color;
    ImageView dp_iv2;

    ImageView browse, save, online,cancel, saved_iv, color_text_bg,theme;
    EditText et;
    TextView tweet_tv,name_tv,username_tv,edit_profile,
            platform_tv, dateTime;
    RelativeLayout layout;
    LinearLayout doneLayout;
    String poetry_txt, name_txt,username_txt,gotDpString;
    Uri imageUri;
    Button share,change_text;
    ColorDrawable cd;
    Uri selectedImg;
    float textSize;
    ActivityResultLauncher<String[]> resultLauncher;
    ActivityResultLauncher<Intent> GetImage;
    SeekBar sizeSeekbar, opacitySeekbar,shadowBar;
    String pic_url;
    ConstraintLayout action_bar;
    private ViewGroup mainLayout;
    private boolean isReadPermissionGranted = false;
    private boolean isWritePermissionGranted = false;
    private int xDelta;
    private int yDelta;
    private int default_color;
    LinearLayout tweet_layout;
    CheckBox cb_date,cb_platform;
    String encodedDrawable;
    boolean gotDateCheck,gotPlatformCheck;

    int counter = 0;//this is theme changing purpose
    int counter2 = 0;//this is theme changing purpose

    @Override
    protected void onStart() {
        super.onStart();
        //did this to make sure everything is laoding on start
        loadAll();
        try {
            tweet_tv.setText(getLastText);
            name_tv.setText(getName);
            username_tv.setText(getUsername);
            gotDpString = getDp;

            if (gotDpString != null) {
                Bitmap b = decodeBase64(gotDpString);
                dp_iv2.setImageBitmap(b);
            }
            if (gotDateCheck){
                cb_date.setChecked(true);
                dateTime.setVisibility(View.GONE);
            }
            if (gotPlatformCheck){
                cb_platform.setChecked(true);
                platform_tv.setVisibility(View.GONE);
            }
        }
        catch (Exception e){ log("not started"); }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();//to hide action bar
//to make animation
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        text_color = findViewById(R.id.text_color);
        layout = findViewById(R.id.layout);

        color_text_bg = findViewById(R.id.colo_text_bg);
        action_bar = findViewById(R.id.customActionBar);

        tweet_layout = findViewById(R.id.tweet_layout);
        doneLayout = findViewById(R.id.doneLayout);

        imageView = findViewById(R.id.imageView);
        browse = findViewById(R.id.gallery_open);
        save = findViewById(R.id.save_img);

        tweet_tv = findViewById(R.id.tweet_tv);
        online = findViewById(R.id.all_template);
        change_text = findViewById(R.id.change_text);
        et = findViewById(R.id.et);
        platform_tv = findViewById(R.id.platform_tv);
        dateTime = findViewById(R.id.dateTime);
        theme = findViewById(R.id.theme);

        username_tv = findViewById(R.id.username_tv);
        name_tv = findViewById(R.id.name_tv);
        dp_iv2 = findViewById(R.id.dp_iv2);
        edit_profile = findViewById(R.id.edit_profile);

        cb_date = findViewById(R.id.cb_date);
        cb_platform = findViewById(R.id.cb_platform);

        opacitySeekbar = findViewById(R.id.opacitySeekBar);
        sizeSeekbar = findViewById(R.id.sizeSeekbar);
        shadowBar = findViewById(R.id.shadowSeekbar);
        imageView = findViewById(R.id.imageView);

        mainLayout = layout;//here some medthods using same layout thats y

        cancel = findViewById(R.id.cancel);
        share = findViewById(R.id.share_bt);
        saved_iv = findViewById(R.id.saved_iv);

        resolver = getContentResolver(); // just a global variable
        default_color = 0;//for color picker
        textSize = 12.0f;
        cd = new ColorDrawable(0xFFFF6666);



        try {
            // in this try method i get strings of note and title
            // from write main activty to show on read activity
            Intent intent1 = getIntent();
            pic_url = intent1.getStringExtra("url_key");

            //profile details
            name_txt = intent1.getStringExtra("name_key");
            username_txt = intent1.getStringExtra("username_key");
            gotDpString = intent1.getStringExtra("dp_key");

            if (name_txt!=null || username_txt !=null) {
                name_tv.setText(name_txt);
                username_tv.setText(username_txt);
            }
            if (gotDpString != null) {
                Bitmap b = decodeBase64(gotDpString);
                dp_iv2.setImageBitmap(b);
            }

            if (pic_url != null) {
                Picasso.get()
                        .load(pic_url)
                        .placeholder(R.drawable.sample_image_ic)
                        .into(imageView);
            }
        } catch (Exception e) {  }

        //setting currate date time
        String dateTime_txt = getDateTime();
        dateTime.setText(dateTime_txt);

        //adding ontouch to make layout drag able
        tweet_layout.setOnTouchListener(onTouchListener());

        resultLauncher = registerForActivityResult(
                //this permision asker
                new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        try {
                            if (result.get(Manifest.permission.READ_EXTERNAL_STORAGE != null)) {
                                isReadPermissionGranted = result.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                            if (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE != null)) {
                                isWritePermissionGranted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            }
                        } catch (Exception ignored) {
                            //Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // this code pasess the name and bitmap to saveImage function that save image
        GetImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //may be this verify that permision is there or not

                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Bundle bundle = result.getData().getExtras();
                            Bitmap bitmap = (Bitmap) bundle.get("data");
                            if (isWritePermissionGranted) {
                                if (SaveImage("MY_IMAge" + UUID.randomUUID(), bitmap)) ;
                                Toast.makeText(MainActivity.this, "Saved Image Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "No Saved Image Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        save.setOnClickListener(view -> {
            //here we are calling this method that makes bitmap from any layout
            save.startAnimation(animation);
            doneLayout.setVisibility(View.VISIBLE);//this to show done layout
            layout.setVisibility(View.GONE);
            getBitmap();//this is important because this is ultimate method
            //that does all things like making bitmap and saving in gallery

            //saving the profile details
            saveName(name_tv.getText().toString());
            saveUsername(username_tv.getText().toString());
            saveDp(gotDpString);

        });
        browse.setOnClickListener(view -> {
            //this is to open gallery and select
            browse.startAnimation(animation);
            Intent intent = new Intent(
                    Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });
        online.setOnClickListener(view -> {
            //to open online activity
            online.startAnimation(animation);
            startActivity(new Intent(MainActivity.this, templateActivity.class));
        });

        tweet_tv.setOnClickListener(view -> {
            //just settext to edittext to edit it easily
            et.setText(tweet_tv.getText());
        });

        change_text.setOnClickListener(view -> {
            //changes and save text
            poetry_txt = et.getText().toString();
            tweet_tv.setText(poetry_txt);
            saveText(poetry_txt);

        });

        cancel.setOnClickListener(view -> {
            //close done layout
            cancel.startAnimation(animation);
            doneLayout.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
        });
        share.setOnClickListener(view -> {
            //share the edited image
            share.startAnimation(animation);
            doneLayout.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            shareImageUri(imageUri);
        });

        edit_profile.setOnClickListener(view -> {
            //opens edit profile activity
          startActivity(new Intent(MainActivity.this,profile.class));
        });

        requestPermission();//this request permission if not granted yet
        //or at first start of app

        opacitySeekbar.setOnSeekBarChangeListener(//this control opacity of tweet layout
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        float alpha_value = i / 100f;
                        tweet_layout.setAlpha(alpha_value);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        sizeSeekbar.setOnSeekBarChangeListener(//this control size of textview or tweet view
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            tweet_tv.setTextSize(i/4);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
        shadowBar.setOnSeekBarChangeListener(//this to control shadow of tweet
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        tweet_layout.setElevation(i/4f);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
        text_color.setOnClickListener(view -> {//this to change text color
            text_color.startAnimation(animation);
            openColorPickerDialogue();
        });

        color_text_bg.setOnClickListener(view -> {// this to change tweet layout color
            colorPicBg();});
        dateTime.setOnClickListener(view -> {
            //set date time
             dateTime.setText(getDateTime());
        });
        cb_date.setOnClickListener(view -> {
            //close or hide date
           check_date();
        });
        cb_platform.setOnClickListener(view -> {
            //close or hide platform
            check_platform();
        });


        platform_tv.setOnClickListener(view -> {
            //this to change name of platform onclick
            String platform = "Twitter for ";
            String [] list = {"Iphone","Android","Desktop"};
            // i made it very simple
            counter++;
            if (counter >= list.length) counter = 0;

            platform_tv.setText(platform + list [counter]); //set the new message.

        });
        theme.setOnClickListener(view -> {
            //this is same as platform but its large

            int [] imgs = {R.drawable.light_ic,R.drawable.dark_ic,R.drawable.grey_ic};

            counter2++;
            if (counter2 >= imgs.length) counter2 = 0;

            theme.setImageResource(imgs[counter2]);

            if (imgs [counter2]==R.drawable.light_ic){
                //changing colors accordingly
                tweet_layout.setBackgroundColor(getResources().getColor(R.color.white));
                imageView.setBackgroundColor(getResources().getColor(R.color.white));
                tweet_tv.setTextColor(getResources().getColor(R.color.black));
                dateTime.setTextColor(getResources().getColor(R.color.black));
                name_tv.setTextColor(getResources().getColor(R.color.black));
                username_tv.setTextColor(getResources().getColor(R.color.black));
            }
            else if (imgs [counter2]==R.drawable.dark_ic){
                tweet_layout.setBackgroundColor(getResources().getColor(R.color.black));
                imageView.setBackgroundColor(getResources().getColor(R.color.black));
                tweet_tv.setTextColor(getResources().getColor(R.color.white));
                dateTime.setTextColor(getResources().getColor(R.color.white));
                name_tv.setTextColor(getResources().getColor(R.color.white));
                username_tv.setTextColor(getResources().getColor(R.color.white));
            }
            else{
                tweet_layout.setBackgroundColor(getResources().getColor(R.color.grey_900));
                imageView.setBackgroundColor(getResources().getColor(R.color.grey_900));
                tweet_tv.setTextColor(getResources().getColor(R.color.black));
                dateTime.setTextColor(getResources().getColor(R.color.black));
                name_tv.setTextColor(getResources().getColor(R.color.black));
                username_tv.setTextColor(getResources().getColor(R.color.black));
            }

        });
    }//OnCreate

    public void bitMapToString(Bitmap bitmap) {
        //this method covert bitmap to string
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos1);
        byte[] arr1 = baos1.toByteArray();
        encodedDrawable = Base64.encodeToString(arr1, Base64.DEFAULT);

    }
    public void check_date() {
        //this is to check checkbox of date
        if (cb_date.isChecked()) {
            saveDateCheck(true);
            dateTime.setVisibility(View.GONE);

        }
        else{
            saveDateCheck(false);
            dateTime.setVisibility(View.VISIBLE);
        }
    }
    public void check_platform() {
        //this is to check checkbox of platform
        if (cb_platform.isChecked()) {
            savePlatformCheck(true);
            platform_tv.setVisibility(View.GONE);

        }
        else{
            savePlatformCheck(false);
            platform_tv.setVisibility(View.VISIBLE);
        }
    }
    public String getDateTime(){
        //mothod to get date time
        long date = System.currentTimeMillis();

        //these can be modified using special leters
        SimpleDateFormat dateF = new SimpleDateFormat("MMM dd yyyy");
        SimpleDateFormat timeF = new SimpleDateFormat("h:mm a");

        String date_str = dateF.format(date);
        String time_str = timeF.format(date);
        String final_dateTime = time_str +" • "+date_str;//arranging date and tiem

        return final_dateTime;

    }
    private void saveUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_SAVED, username);
        editor.apply();
    }

    private void saveName(String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME_SAVED, name);
        editor.apply();
    }
    private void saveDp(String dp) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DP_SAVED, dp);
        editor.apply();
    }

    public Bitmap decodeBase64(String input) {
        //this method coverts string image to bitmap
        //to set image from shared prefs
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    private void saveUri(String pic_link) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_LINK, pic_link);
        editor.apply();
    }

    private void saveText(String text_last) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_TEXT, text_last);
        editor.apply();
    }
    private void saveDateCheck(boolean date_check) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DATE_CHECK, date_check);
        editor.apply();
    }
    private void savePlatformCheck(boolean platform_check) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PLATFORM_CHECK, platform_check);
        editor.apply();
    }


    private void loadAll() {
        //this is to get all values form shared prefs
        getLastLink = sharedPreferences.getString(LAST_LINK, "");
        getLastText = sharedPreferences.getString(LAST_TEXT, "Seekh kar Gayi hai vo Mohabbat Mujhse");
        getName = sharedPreferences.getString(NAME_SAVED, "Basic Tweets");
        getUsername = sharedPreferences.getString(USERNAME_SAVED, "basic0tweets");
        getDp = sharedPreferences.getString(DP_SAVED, encodedDrawable);
        gotDateCheck =sharedPreferences.getBoolean(DATE_CHECK,false);
        gotPlatformCheck =sharedPreferences.getBoolean(PLATFORM_CHECK,false);
    }


    private void log(String l) {
        Log.d("taggi", l);
    }
        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //this is important
            //this get the uri of selected image from gallery

        if (resultCode == RESULT_OK && data != null) {
            selectedImg = data.getData();
            String filePath;
            log("URI = " + selectedImg);
            if (selectedImg != null && "content".equals(selectedImg.getScheme())) {
                Cursor cursor = this.getContentResolver().query(selectedImg, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                cursor.moveToFirst();
                filePath = cursor.getString(0);
                cursor.close();
            } else {
                filePath = selectedImg.getPath();
            }
            log("Chosen path = " + filePath);

            Picasso.get()
                    .load(selectedImg)
                    .resize(layout.getWidth(), layout.getHeight())
                    .placeholder(R.drawable.sample_image_ic)
                    .into(imageView);
            saveUri(selectedImg.toString());
        }
    }


    private boolean SaveImage(String title, Bitmap bitmap) {
        //and i don't know much about these things
        //but i'll learn it soooon
        Uri ImageCollection;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

        } else {
            ImageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        ContentValues values = new ContentValues();
        // here we can change the name type of image
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        imageUri = resolver.insert(ImageCollection, values);
        saveUri(imageUri.toString());


        try {
            OutputStream outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Objects.requireNonNull(outputStream);
            return true;
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Image note Saved", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }

    private void shareImageUri(Uri uri) {
        //this to share image
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, "Share"));
    }

    private void requestPermission() { // this is all about permissions

        boolean minSdk = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;

        isReadPermissionGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;


        isWritePermissionGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;

        isWritePermissionGranted = isWritePermissionGranted || minSdk;

        List<String> permissionRequest = new ArrayList<String>();

        if (!isReadPermissionGranted) {
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!isWritePermissionGranted) {
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionRequest.isEmpty()) {
            resultLauncher.launch(permissionRequest.toArray(new String[0]));
        }

    }

    private void getBitmap() {
        //here the last things is done when saving image
        Bitmap bmp = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        layout.draw(canvas);

        if (isWritePermissionGranted) {
            if (SaveImage(UUID.randomUUID().toString(), bmp)) ;
            // Toast.makeText(MainActivity.this, "Saved Image Successfully", Toast.LENGTH_SHORT).show();
            //    bitMapToString(bmp);
            saved_iv.setImageURI(imageUri);
        } else {
            if (SaveImage(UUID.randomUUID().toString(), bmp)) ;
            Toast.makeText(MainActivity.this, "Compartmentally Saved Image Successfully", Toast.LENGTH_SHORT).show();
            //  Toast.makeText(MainActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
    }


    private View.OnTouchListener onTouchListener() {
        //this method is to drag any view
        //currently its set to tweeet layout
        return new View.OnTouchListener() {


            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();

                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_UP:
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                                .getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;
                }

                mainLayout.invalidate();

                return true;

            }

        };
    }

    public void openColorPickerDialogue() {
        //this is imported library of color picker dialog

        // the AmbilWarnaDialog callback needs 3 parameters
        // one is the context, second is default color,
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(this, default_color,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        default_color = color;
                        tweet_tv.setTextColor(default_color);

                        if (et.hasSelection()||tweet_tv.hasSelection()){
                            Spannable spannableString = new
                                    SpannableStringBuilder(et.getText());
                            spannableString.setSpan(new ForegroundColorSpan(default_color),
                                    et.getSelectionStart(),
                                    et.getSelectionEnd(),
                                    0);
                            et.setText(spannableString);
                            tweet_tv.setText(spannableString);

                        }
                        else {

                        }
                    }
                });
        colorPickerDialogue.show();

    }
    public void colorPicBg() {
        //this is same i just repeat it
        // and i regret to break DRY (Don't repeat yourself) rule

        // the AmbilWarnaDialog callback needs 3 parameters
        // one is the context, second is default color,
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(this, default_color,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        default_color = color;

                        imageView.setBackgroundColor(default_color);

                    }
                });
        colorPickerDialogue.show();

    }
}