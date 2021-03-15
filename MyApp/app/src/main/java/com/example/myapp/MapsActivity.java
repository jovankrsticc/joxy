package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.clustering.ClusterManager;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private DatabaseReference reference,objReference;
    private FirebaseAuth fAuth;
    private StorageReference sReference;
    private LocationManager locationManager;
    private final int MIN_TIME=1000;
    private final int MIN_DISTANCE=1;
    private String userID;
    private Button btn,dodajObjekat,radiusBtn,dateBtn;
    private SearchView searchView;
    private HashMap<String,Marker> hashMapMarker = new HashMap<>();
    private Collection<Marker> markers;
//private static final String TAG="MapsActivity";
    public boolean crtajKrug = false;
    public CircleOptions circleOptions;
    EditText radius,date;
    String radiusString="";
    Circle mapCircle;
    MyLocation currentLocation;



    DatabaseReference geo = FirebaseDatabase.getInstance().getReference().child("Geofence");
    GeoFire geoFire = new GeoFire(geo);
    ArrayList<String> arrayListUserIds = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fAuth= FirebaseAuth.getInstance();
        userID=fAuth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        objReference=FirebaseDatabase.getInstance().getReference().child("Objects");
        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        sReference= FirebaseStorage.getInstance().getReference();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchView=findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String search=searchView.getQuery().toString();
                if (search != null || !search.equals(""))
                {
                    markers=hashMapMarker.values();
                    for(Marker m : markers)
                    {
                        if(m.getTitle().equals(search))
                        {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(),16));
                            return true;
                        }
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        dateBtn=findViewById(R.id.btnDate);
        date=findViewById(R.id.editTextDate);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        radiusBtn=findViewById(R.id.btnRadius);
        radius=findViewById(R.id.editRadius);
        radiusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapCircle != null)
                {
                    mapCircle.remove();
                }
                if(!radius.getText().toString().isEmpty())
                {
                    if(!radius.getText().toString().equals(radiusString))
                    {
                        if(radiusString.equals(""))
                            crtajKrug = !crtajKrug;
                        radiusString = radius.getText().toString();
                        addCircle(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), Float.valueOf(radiusString));
                    }
                    else
                    {
                        crtajKrug = !crtajKrug;
                        radiusString = "";
                    }
                }

            }
        });


        dodajObjekat = findViewById(R.id.dodajObjekat);
        dodajObjekat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, DodajObjekatActivity.class));
                //finish();
            }
        });

       createNotificationChannel();


       // readChanges();


    }



  /*  private void showAll() {

        reference.getParent().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dsp: snapshot.getChildren())
                {

                    User user=dsp.getValue(User.class);
                    String id=dsp.getKey();
                    addMarker(user,id);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

  /*  private void readChanges() {

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        try {

                               //mMap.clear();
                               User user=snapshot.getValue(User.class);
                                addMarker(user,userID);
                                LatLng location=new LatLng(user.myLocation.getLatitude(),user.myLocation.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,5));
                            }

                    catch (Exception e)
                        {
                            Toast.makeText(MapsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }


            }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }*/

 public void show()
 {
     reference.getParent().addChildEventListener(new ChildEventListener() {
         @Override
         public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
             if(snapshot.exists())
             {
                 try {

                  //   Toast.makeText(MapsActivity.this,"Child added"+snapshot.getValue(),Toast.LENGTH_SHORT).show();

                     addMarker(snapshot.getValue(User.class),snapshot.getKey(),"friend");
                 }
                 catch (Exception e)
                 {
                     Toast.makeText(MapsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                 }
             }

         }

         @Override
         public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
             if(snapshot.exists())
             {
                 try {

                //    Toast.makeText(MapsActivity.this,"Child changed"+snapshot.getValue(),Toast.LENGTH_SHORT).show();
                         Marker marker = hashMapMarker.get(snapshot.getKey());
                         if(marker!=null)
                         {
                             marker.remove();
                             hashMapMarker.remove(snapshot.getKey());
                         }
                      //
                        //
                         addMarker(snapshot.getValue(User.class),snapshot.getKey(),"friend");
                        // LatLng l=new LatLng(snapshot.child("myLocation").getValue(MyLocation.class).getLatitude(),snapshot.child("myLocation").getValue(MyLocation.class).getLongitude());
                        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l,5));

                 }

                 catch (Exception e)
                 {
                     Toast.makeText(MapsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                 }
             }

         }

         @Override
         public void onChildRemoved(@NonNull DataSnapshot snapshot) {

         }

         @Override
         public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });
 }

    public void addMarker(User user,String id,String typeMarker)
    {

            if(typeMarker.equals("friend"))
            {
                LatLng location = new LatLng(user.myLocation.getLatitude(), user.myLocation.getLongitude());
                sReference.child("profile_images").child(id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(MapsActivity.this)
                                .asBitmap()
                                .load(uri.toString())
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, java.lang.Object model, Target<Bitmap> target, boolean isFirstResource) {

                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, java.lang.Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(location)
                                                .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                                .title(user.userName)
                                                .snippet("Ime: "+user.firstName+"\n"+
                                                        "Prezime: "+user.lastName+"\n")


                                        );
                                        mMap.setOnMarkerClickListener(MapsActivity.this);
                                        hashMapMarker.put(id,marker);

                                        return true;

                                    }
                                })
                                .centerCrop()
                                .preload();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsActivity.this,"GRESKA"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        addMarker(user,id,"korisnik");
                    }
                });

            }
            else if(typeMarker.equals("korisnik"))
            {
                LatLng location = new LatLng(user.myLocation.getLatitude(), user.myLocation.getLongitude());


                Marker marker1 = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(user.userName)
                        .icon(BitmapFromVector(getApplicationContext(),R.drawable.ic_baseline_person_pin_circle_24))
                        .snippet("Ime: "+user.firstName+"\n"+"Prezime: "+user.lastName)

                );


                mMap.setOnMarkerClickListener(MapsActivity.this);
                hashMapMarker.put(id,marker1);

            }
            else
            {
                Toast.makeText(MapsActivity.this,"Greska",Toast.LENGTH_SHORT).show();
            }

    }

    private BitmapDescriptor BitmapFromVector(Context applicationContext, int id)
    {
        Drawable vectorDrawable = ContextCompat.getDrawable(applicationContext, id);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    void showObjectMarker()
    {
        objReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
             if(snapshot.exists())
             {
                 try{

                     addObjectMarker(snapshot.getValue(MyObject.class),snapshot.getKey());
                 }
                 catch (Exception e)
                 {
                     Toast.makeText(MapsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                 }
             }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists())
                {
                    try
                    {
                        Marker marker = hashMapMarker.get(snapshot.getKey());
                        if(marker!=null)
                        {
                            marker.remove();
                            hashMapMarker.remove(snapshot.getKey());
                        }
                        addObjectMarker(snapshot.getValue(MyObject.class),snapshot.getKey());

                    }
                    catch (Exception e)
                    {

                    }
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void addObjectMarker(MyObject myObject,String id)
    {
        LatLng mylocation = new LatLng(myObject.location.getLatitude(), myObject.location.getLongitude());
        Toast.makeText(MapsActivity.this,"LAT "+mylocation.latitude+"  LONG "+mylocation.longitude,Toast.LENGTH_SHORT).show();
        Marker objMarker = mMap.addMarker(new MarkerOptions()
                .position(mylocation)
                .title(myObject.name)
                .icon(BitmapFromVector(getApplicationContext(),R.drawable.ic_baseline_store_24))

        );


        mMap.setOnMarkerClickListener(MapsActivity.this);
        hashMapMarker.put(id,objMarker);
    }

    private void addCircle(LatLng latlng, float radius) {
        circleOptions = new CircleOptions();
        circleOptions.center(latlng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
        circleOptions.fillColor(Color.argb(64, 255, 0, 0));
        circleOptions.strokeWidth(4);
        circleOptions.visible(true);
        mapCircle = mMap.addCircle(circleOptions);
        //mMap.addCircle(circleOptions);
    }

    /*public void drawCircle()
    {
            addCircle(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), Float.valueOf(radiusString));
    }*/

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            CharSequence name = "channel";
            String destription = "channel for notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifications", name, importance);
            channel.setDescription(destription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification() {
        Intent intent = new Intent(this, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notifications")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("SPOTTED!")
                .setContentText("User spotted nearby")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(100, builder.build());
    }

    private void getLocationUpdates() {
        if(locationManager!=null)
        {
            if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            {
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DISTANCE,this);
                }
                else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME,MIN_DISTANCE,this);
                }
                else
                {
                    Toast.makeText(MapsActivity.this,"No provider.",Toast.LENGTH_LONG).show();
                }

            }
            else
            {
                ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 44) {

              //  getCurrentLocation();
            }
            else if (requestCode == 101) {

                getLocationUpdates();
            }
        }
        else
        {
            Toast.makeText(MapsActivity.this,"Permission Required",Toast.LENGTH_LONG).show();
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationUpdates();
        showObjectMarker();
        show();

        //myMarker=mMap.addMarker()
        // Add a marker in Sydney and move the camera

    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location!=null)
        {
            MyLocation myLocation= new MyLocation(location.getLatitude(),location.getLongitude());
            saveLocation(myLocation);
        }
        else
        {
            Toast.makeText(MapsActivity.this,"No location",Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLocation(MyLocation location) {
        reference.child("myLocation").setValue(location);
        currentLocation=location;
        if(crtajKrug)
        {
            if(mapCircle!=null)
                mapCircle.remove();
            addCircle(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), Float.valueOf(radiusString));
        }

        geoFire.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 2);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!key.equals(userID))
                {
                    arrayListUserIds.add(key);
                    sendNotification();
                }
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
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }


}