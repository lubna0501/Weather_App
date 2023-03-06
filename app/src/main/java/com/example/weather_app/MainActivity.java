package com.example.weather_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.BuildConfig;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
EditText edit;
Button submit;
TextView text,texttemp ,temptext,textpressure,textmin,textmax ;
    private static final int REQUEST_CHECK_SETTINGS =100;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS =10000;
    private static final long FATEST_UPDATE_INTERVAL_IN_MILLISECONDS =1000;
    private static final String TAG =  MainActivity.class.getSimpleName();

    private FusedLocationProviderClient mfuseslocationclient;
    private SettingsClient msettingsclient;
    private LocationRequest mlocationRequest;
    private LocationSettingsRequest mlocationSettingsRequest;
    private LocationCallback mlocationCallback;
    private Location mcurrentlocation;
    private boolean mRequestingLocationUpdates =false;
    private static final DecimalFormat decfor = new DecimalFormat("0.00");

    //String url= "https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";
String apikey ="1e377f41e54fae1563a6be92a9bcffda";
String name,newyork,singapore,delhi,mumbai,sydney,melbourne;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        // getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

         texttemp=findViewById(R.id.texttemp);
        textpressure=findViewById(R.id.textpressure);
        textmin=findViewById(R.id.textmin);
        textmax=findViewById(R.id.textmax);
        text= findViewById(R.id.text);
         temptext=findViewById(R.id.temptext);
//         textnewyork=findViewById(R.id.textnewyork);
//        textsingapore=findViewById(R.id.textsinga);
//        textmumbai=findViewById(R.id.textmumbai);
//        textdelhi=findViewById(R.id.textdelhi);
//        textsydney=findViewById(R.id.textsydney);
//        textmelbourne=findViewById(R.id.textmelbourne);


        mfuseslocationclient= LocationServices.getFusedLocationProviderClient(this);
        msettingsclient = LocationServices.getSettingsClient(this);

        mlocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mcurrentlocation = locationResult.getLastLocation();

//                double latitude= mcurrentlocation.getLatitude();
//                double longitude = mcurrentlocation.getLongitude();

                Geocoder gc= new Geocoder(MainActivity.this , Locale.getDefault());
                List<Address> addresses= null;
                try {
                    addresses=gc.getFromLocation(mcurrentlocation.getLatitude(), mcurrentlocation.getLongitude(), 1);
                    text.setText(addresses.get(0).getLocality());
                    if (text.getText().toString().equals("")) {
                        text.setError("Please set valid");
                    }
                    else {
                        name= String.valueOf(text.getText());


                       // texttemp.setText(name);
                        Retrofit retrofit=new Retrofit.Builder()
                                .baseUrl("https://api.openweathermap.org/data/2.5/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        // name = text.getText().toString().trim();
                        weatherapi myapi=retrofit.create(weatherapi.class);
                        Call<Example> exampleCall = myapi.getweather(name,apikey);

                        exampleCall.enqueue(new Callback<Example>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onResponse(Call<Example> call, Response<Example> response) {
                                if(response.code()==404)
                                {
                                    Toast.makeText(MainActivity.this, "please enter valid city", Toast.LENGTH_SHORT).show();
                                }
                                else if(!(response.isSuccessful()))
                                {
                                    Toast.makeText(MainActivity.this,response.code(), Toast.LENGTH_SHORT).show();
                                }
                                //   String res1 = response.body().string();
                                Example mydata = response.body();
                                MainModel.Main main = mydata.getMain();
                                Double temp= main.getTemp();
                                Integer humidity= main.getHumidity();
                                Double min= main.getTempMin();
                                Double max = main.getTempMax();
                                Integer pressure = main.getPressure();
                                decfor.setRoundingMode(RoundingMode.UP);
                                Integer temperature= (int)(temp-273.15);
                                temptext.setText(String.valueOf(temperature)+"C" );
                                texttemp.setText(String.valueOf(humidity));
                                textmin.setText(String.valueOf(decfor.format(min-273.15))+"C");
                                textmax.setText(String.valueOf(decfor.format(max-273.15))+"C");
                                textpressure.setText(String.valueOf(pressure));
                            }


                            @Override
                            public void onFailure(Call<Example> call, Throwable t) {
                                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //   text.setText();

            }
        };

        mlocationRequest = LocationRequest.create()
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FATEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder= new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mlocationRequest);
        mlocationSettingsRequest = builder.build();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied())
                        {
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

//        newyork= String.valueOf(textnewyork.getText());
//        singapore= String.valueOf(textsingapore.getText());
//        delhi= String.valueOf(textdelhi.getText());
//        mumbai= String.valueOf(textmumbai.getText());
//        sydney= String.valueOf(textsydney.getText());
//        melbourne= String.valueOf(textmelbourne.getText());

//        Retrofit retrofit=new Retrofit.Builder()
//                .baseUrl("https://api.openweathermap.org/data/2.5/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        // name = text.getText().toString().trim();
//        weatherapi myapi=retrofit.create(weatherapi.class);
//        Call<Example> exampleCall1 = myapi.getweather(newyork,apikey);
//        exampleCall1.enqueue(new Callback<Example>() {
//            @Override
//            public void onResponse(Call<Example> call, Response<Example> response) {
////                                if(response.code()==404)
////                                {
////                                    Toast.makeText(MainActivity.this, "please enter valid city", Toast.LENGTH_SHORT).show();
////                                }
////                                else if(!(response.isSuccessful()))
////                                {
////                                    Toast.makeText(MainActivity.this,response.code(), Toast.LENGTH_SHORT).show();
////                                }
//
//                Example mydata = response.body();
//                MainModel.Main main = mydata.getMain();
//                Double temp= main.getTemp();
//                Integer temperature= (int)(temp-273.15);
//                textnewyork.setText(String.valueOf(temperature)+"C" );
//            }
//
//            @Override
//            public void onFailure(Call<Example> call, Throwable t) {
//                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        Call<Example> exampleCall2 = myapi.getweather(singapore,apikey);
//        exampleCall2.enqueue(new Callback<Example>() {
//            @Override
//            public void onResponse(Call<Example> call, Response<Example> response) {
////                                if(response.code()==404)
////                                {
////                                    Toast.makeText(MainActivity.this, "please enter valid city", Toast.LENGTH_SHORT).show();
////                                }
////                                else if(!(response.isSuccessful()))
////                                {
////                                    Toast.makeText(MainActivity.this,response.code(), Toast.LENGTH_SHORT).show();
////                                }
//
//                Example mydata = response.body();
//                MainModel.Main main = mydata.getMain();
//                Double temp= main.getTemp();
//                Integer temperature= (int)(temp-273.15);
//                textsingapore.setText(String.valueOf(temperature)+"C" );
//            }
//
//            @Override
//            public void onFailure(Call<Example> call, Throwable t) {
//                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        Call<Example> exampleCall3 = myapi.getweather(mumbai,apikey);
//        exampleCall3.enqueue(new Callback<Example>() {
//            @Override
//            public void onResponse(Call<Example> call, Response<Example> response) {
////                                if(response.code()==404)
////                                {
////                                    Toast.makeText(MainActivity.this, "please enter valid city", Toast.LENGTH_SHORT).show();
////                                }
////                                else if(!(response.isSuccessful()))
////                                {
////                                    Toast.makeText(MainActivity.this,response.code(), Toast.LENGTH_SHORT).show();
////                                }
//                Example mydata = response.body();
//                MainModel.Main main = mydata.getMain();
//                Double temp= main.getTemp();
//                Integer temperature= (int)(temp-273.15);
//                textmumbai.setText(String.valueOf(temperature)+"C" );
//            }
//
//            @Override
//            public void onFailure(Call<Example> call, Throwable t) {
//                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        Call<Example> exampleCall4 = myapi.getweather(delhi,apikey);
//        exampleCall4.enqueue(new Callback<Example>() {
//            @Override
//            public void onResponse(Call<Example> call, Response<Example> response) {
////                                if(response.code()==404)
////                                {
////                                    Toast.makeText(MainActivity.this, "please enter valid city", Toast.LENGTH_SHORT).show();
////                                }
////                                else if(!(response.isSuccessful()))
////                                {
////                                    Toast.makeText(MainActivity.this,response.code(), Toast.LENGTH_SHORT).show();
////                                }
//
//                Example mydata = response.body();
//                MainModel.Main main = mydata.getMain();
//                Double temp= main.getTemp();
//                Integer temperature= (int)(temp-273.15);
//                textdelhi.setText(String.valueOf(temperature)+"C" );
//            }
//
//            @Override
//            public void onFailure(Call<Example> call, Throwable t) {
//                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        Call<Example> exampleCall5 = myapi.getweather(sydney,apikey);
//        exampleCall5.enqueue(new Callback<Example>() {
//            @Override
//            public void onResponse(Call<Example> call, Response<Example> response) {
////                                if(response.code()==404)
////                                {
////                                    Toast.makeText(MainActivity.this, "please enter valid city", Toast.LENGTH_SHORT).show();
////                                }
////                                else if(!(response.isSuccessful()))
////                                {
////                                    Toast.makeText(MainActivity.this,response.code(), Toast.LENGTH_SHORT).show();
////                                }
//
//                Example mydata = response.body();
//                MainModel.Main main = mydata.getMain();
//                Double temp= main.getTemp();
//                Integer temperature= (int)(temp-273.15);
//                textsydney.setText(String.valueOf(temperature)+"C" );
//
//            }
//
//            @Override
//            public void onFailure(Call<Example> call, Throwable t) {
//                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        Call<Example> exampleCall6 = myapi.getweather(melbourne,apikey);
//        exampleCall6.enqueue(new Callback<Example>() {
//            @Override
//            public void onResponse(Call<Example> call, Response<Example> response) {
//                //    if(response.code()==404)
////                                {
////                                    Toast.makeText(MainActivity.this, "please enter valid city", Toast.LENGTH_SHORT).show();
////                                }
////                                else if(!(response.isSuccessful()))
////                                {
////                                    Toast.makeText(MainActivity.this,response.code(), Toast.LENGTH_SHORT).show();
////                                }
//
//                Example mydata = response.body();
//                MainModel.Main main = mydata.getMain();
//                Double temp= main.getTemp();
//                Integer temperature= (int)(temp-273.15);
//                textmelbourne.setText(String.valueOf(temperature)+"C" );
//            }
//
//            @Override
//            public void onFailure(Call<Example> call, Throwable t) {
//                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

//        Retrofit retrofit=new Retrofit.Builder()
//                .baseUrl("https://api.openweathermap.org/data/2.5/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        // name = text.getText().toString().trim();
//        weatherapi myapi=retrofit.create(weatherapi.class);
//        Call<Example> exampleCall = myapi.getweather(name,apikey);
//        exampleCall.enqueue(new Callback<Example>() {
//            @Override
//            public void onResponse(Call<Example> call, Response<Example> response) {
//                if(response.code()==404)
//                {
//                    Toast.makeText(MainActivity.this, "please enter valid city", Toast.LENGTH_SHORT).show();
//                }
//                else if(!(response.isSuccessful()))
//                {
//                    Toast.makeText(MainActivity.this,response.code(), Toast.LENGTH_SHORT).show();
//                }
//             //   String res1 = response.body().string();
//                Example mydata = response.body();
//                MainModel.Main main = mydata.getMain();
//                Double temp= main.getTemp();
//                Integer temperature= (int)(temp-273.15);
//                texttemp.setText(String.valueOf(temperature)+"C" );
//              }
//
//            @Override
//            public void onFailure(Call<Example> call, Throwable t) {
//                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void openSettings()
    {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri =Uri.fromParts("package", BuildConfig.APPLICATION_ID,null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startLocationUpdates()
    {
        msettingsclient.checkLocationSettings(mlocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        mfuseslocationclient.requestLocationUpdates(mlocationRequest, mlocationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                Log.i(TAG, "location settings are not satisfied. attempting to upgrade location settings");

                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "locationsettings are inadequate, and cannot be fixed here.fix in settings";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private  void stopLocationUpdates()
    {
        mfuseslocationclient.removeLocationUpdates(mlocationCallback).addOnCompleteListener(this,task -> Log.d(TAG, "location updates stopped !"));
    }
    private  boolean checkPermission()
    {
        int permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return  permissionState== PackageManager.PERMISSION_GRANTED;
    }
    protected void onResume()
    {
        super.onResume();
        if (mRequestingLocationUpdates && checkPermission())
        {
            startLocationUpdates();
        }
    }
    protected void onPause()
    {
        super.onPause();
        if(mRequestingLocationUpdates)
        {
            stopLocationUpdates();
        }
    }

}