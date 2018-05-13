package com.example.elitebook.location;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import java.util.ArrayList;
import java.util.Random;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        android.location.LocationListener,com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE = 1234;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1234567;
    private Button btnCamera;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    private static final String title = "Alert!";
    private static final String message = "You have just entered an area of Historial Significance";
    private NotificationHelper mNotificationHelper;
    private static final String TAG = "KIERAN";
    public static ArrayList <String> fLocation = new ArrayList <String>();
    private final String PO = "GPO";
    private final String NC = "NCI";
    private final ArrayList<GeoQuery> queries = new ArrayList <>();


    DatabaseReference ref;
    GeoFire geoFire;
    VerticalSeekBar mSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        ref = FirebaseDatabase.getInstance().getReference("MyLocation");
        geoFire = new GeoFire(ref);
        mNotificationHelper = new NotificationHelper(this);

        mSeekBar = (VerticalSeekBar)findViewById(R.id.verticalSeekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

                                                @Override
                                                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                         //           mMap.animateCamera(CameraUpdateFactory.zoomTo(i),2000,null);
                                                }

                                                @Override
                                                public void onStartTrackingTouch(SeekBar seekBar) {

                                                }

                                                @Override
                                                public void onStopTrackingTouch(SeekBar seekBar) {

                                                }
                                            });

        btnCamera =(Button)findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });



        setUpLocation();
        Log.d(TAG,"SetUpLocation called");
    }

    private void openCamera() {
        Intent intent = new Intent(this,CameraActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        Log.d(TAG,"buildGoogleApiClient called");
                        createLocationRequest();
                        Log.d(TAG,"createLocationRequest called");
                        displayLocation();
                        Log.d(TAG,"Display Location  called");


                    }
                }
                break;
        }
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);

        } else {
            if (checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
                Log.d(TAG,"build Google Api Client , Create Location Request , Display Location called");

            }
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            {
                return;
            }
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            geoFire.setLocation("You", new GeoLocation(latitude, longitude));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,latitude),12.0f));


            Log.d(TAG, "your location" + latitude + longitude);
        }


        else {
            Log.d(TAG, "Can't get your location AKA NULL");
        }

    }
        // create repeating loction requests
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }
        //connecting to client so we can use location services
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();

            else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
    //   mMap.animateCamera(CameraUpdateFactory.newLatLng(mLastLocation));


           // GROFENCE1
            final LatLng GPO = new LatLng(53.349472,-6.2605703);
            mMap.addCircle(new CircleOptions()
                    .center(GPO)
                    .radius(0.05)
                    .strokeColor(Color.BLUE)
                    .fillColor(0x22000FF)
                    .strokeWidth(5.0f)
            );
            //GEOFENCE 2
        final LatLng NCI = new LatLng(53.348984,-6.2432225);
                mMap.addCircle(new CircleOptions()
                        .center(NCI)
                        .radius(0.05)
                        .strokeColor(Color.BLUE)
                        .fillColor(0x22000FF)
                        .strokeWidth(5.0f)
                );
        final GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(GPO.latitude,GPO.longitude),0.05f);
        final GeoQuery geoQuery1 = geoFire.queryAtLocation(new GeoLocation(NCI.latitude,NCI.longitude),0.05f);
        queries.add(geoQuery);
        queries.add(geoQuery1);
                        // NCI QUERY
                    geoQuery1.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            Notification.Builder builder = mNotificationHelper.getChannel1Notification(title, message);
                            mNotificationHelper.getmManger().notify(new Random().nextInt(), builder.build());
                            fLocation.clear();
                            fLocation.add(NC);
                            Log.d(TAG,"you're at NCI"+fLocation);


                        }


                        @Override
                        public void onKeyExited(String key) {
                            fLocation.clear();

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {
                            Log.d(TAG, "resting in the GPO area");


                        }

                        @Override
                        public void onGeoQueryReady() {


                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            Log.e("ERROR", "" + error);

                        }
                    });
                    // query 2
        //
        //
        //
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {

                    Notification.Builder builder = mNotificationHelper.getChannel1Notification(title, message);
                    mNotificationHelper.getmManger().notify(new Random().nextInt(), builder.build());
                    fLocation.clear();
                    fLocation.add(PO);
                    Log.d(TAG,"you're at the gpo"+fLocation);




                }


                @Override
                public void onKeyExited(String key) {



                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            });

Log.d(TAG,"onMapReady"+fLocation);

    }

  @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED)
        {
            return;

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }






}
