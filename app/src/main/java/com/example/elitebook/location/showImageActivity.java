package com.example.elitebook.location;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.elitebook.location.MapsActivity.fLocation;


public class showImageActivity extends AppCompatActivity  implements GestureDetector.OnGestureListener,View.OnClickListener{


    public static ArrayList <String> mLocation = fLocation;
    private GestureDetectorCompat gestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private ImageView filter ;
    private ImageView mImageView ;
    private Button saveButton;
    private Button cancelButton;
    private String mImageLocation;
    private String GALLERY_LOCATION = "Peregrine Gallery";
    private File mGalleryFolder;
    private String TAG = "KIERAN";
    private String MY_PREFS_NAME= "KIERAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
          String mImageLocation = "";
        Log.d(TAG,"ONCREATE METHOD"+mLocation);

        Log.d(TAG,"ONCREATE METHOD"+mLocation);
        createGallery();


        filter = findViewById(R.id.filter);
       mImageView = findViewById(R.id.showImageView);


        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    savePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    cancel();
            }
        });


        Bundle extras = getIntent().getExtras();
        assert extras != null;
        byte [] bytes = extras.getByteArray("capture");

        if (bytes != null){
             mImageView = findViewById(R.id.showImageView);
             this.gestureDetector = new GestureDetectorCompat(this,this);
           //  gestureDetector.setOnDoubleTapListener(this);

            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            Bitmap rotateBitmap = rotate(decodedBitmap);
            mImageView.setImageBitmap(rotateBitmap);
            Toast.makeText(showImageActivity.this,"Swipe Left for a Filter",Toast.LENGTH_LONG).show();
        }


/*
        private Bitmap joinImages(Bitmap image, Bitmap filter){
            // Bitmap result = Bitmap.createBitmap(firstImage.getWidth(),firstImage.getHeight(),firstImage.getConfig());
            combo = Bitmap.createBitmap(firstImage.getWidth(),firstImage.getHeight(),firstImage.getConfig());
            Canvas canvas = new Canvas(combo);
            canvas.drawBitmap(firstImage,0f,0f,null);
            canvas.drawBitmap(secondImage,10,10,null);
            return combo;
        }
            */
    }


    private void createGallery() {
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mGalleryFolder = new File(storageDirectory,GALLERY_LOCATION);
        if(!mGalleryFolder.exists()){
            mGalleryFolder.mkdirs();
        }
    }
    File createImage(Canvas canvas) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";

        File mImage = File.createTempFile(imageFileName, ".jpg", mGalleryFolder);
        mImageLocation = mImage.getAbsolutePath();
        Toast.makeText(this, "FILE SAVED TO PEREGRINE GALLERY", Toast.LENGTH_LONG).show();
        return mImage;
    }





    private void cancel() {
        Intent intent = new Intent(this,CameraActivity.class);
        startActivity(intent);
    }




    private Bitmap rotate(Bitmap decodedBitmap) {
        int w = decodedBitmap.getWidth();
        int h = decodedBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        return Bitmap.createBitmap(decodedBitmap,0,0,w,h,matrix,true);

    }






    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
                return false;
            }
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                onLeftSwipe();


            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                onRightSwipe();

            }
            return true;
        } catch (Exception e) {

        }
        return false;
    }
// add filter based on string element
    private boolean onLeftSwipe() {
        if (mLocation.contains("GPO"))
             {
                ImageView filter = findViewById(R.id.filter);
                filter.setImageResource(R.drawable.kieran);
                Log.d(TAG,"ArrayList contents"+mLocation);
            }

                    else if(mLocation.contains("NCI")) {
                ImageView filter = findViewById(R.id.filter);
                filter.setImageResource(R.drawable.nci);
                Log.d(TAG,"ArrayList contents something weird is going on"+mLocation);

            }

        return true;
        }

// Remove any filter.
    private boolean onRightSwipe() {

                ImageView filter = findViewById(R.id.filter);
                filter.setImageDrawable(null);

        return true;
        }
// end


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    @Override
    public void onClick(View v) {

    }
    private void savePhoto() throws IOException {
        BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap.Config c = bitmap.getConfig();
        Bitmap combo = Bitmap.createBitmap(w,h,c);
        Canvas canvas = new Canvas(combo);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.kieran);
        canvas.drawBitmap(bitmap,0f,0f,null);
        canvas.drawBitmap(bm,10,10,null);

        createImage(canvas);
        mLocation.clear();
        Log.d(TAG,"Last line of code"+mLocation);
    }

}

