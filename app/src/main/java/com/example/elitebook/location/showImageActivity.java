package com.example.elitebook.location;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class showImageActivity extends AppCompatActivity  {

    //String myValue = LoginScreen.getMyString();
    private ArrayList <String> fLocation = MapsActivity.fLocation;
    private final String PO = "GPO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        byte [] bytes = extras.getByteArray("capture");

        if (bytes != null){
            ImageView image = findViewById(R.id.showImageView);

            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

            Bitmap rotateBitmap = rotate(decodedBitmap);
            image.setImageBitmap(rotateBitmap);
            Toast.makeText(showImageActivity.this,"Geo Filter for "+fLocation,Toast.LENGTH_LONG).show();
        }


        // picture is loaded above Filters below

        for(String element:fLocation){
            if(element.equalsIgnoreCase("GPO")){
                ImageView filter = findViewById(R.id.filter);
                filter.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }

    private Bitmap rotate(Bitmap decodedBitmap) {
        int w = decodedBitmap.getWidth();
        int h = decodedBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        return Bitmap.createBitmap(decodedBitmap,0,0,w,h,matrix,true);

    }
}
