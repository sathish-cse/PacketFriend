package ace.cse.finalyrproject.spotter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FindFriendsHomeActivity extends AppCompatActivity
        implements LocationListener {

    private Geocoder geocoder;
    TextView address;
    private ProgressBar spinner;
    ImageView imgmap;
    public static String map;
    Button mark,view;
    String formattedDate="";
    Double Latitude,Langnitude;
    public  static String Result="";
    boolean GpsStatus ;
    LocationManager locationManager ;
    private String displayName;
    private String email;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public static String USERMAIL,DISPLAYNAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends_home);
        address=(TextView)findViewById(R.id.address);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        mark=(Button)findViewById(R.id.mark);
        mark.setVisibility(View.GONE);
        view=(Button)findViewById(R.id.view);
        view.setVisibility(View.GONE);

        imgmap=(ImageView)findViewById(R.id.mapimg);


        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        //To setup location manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //To request location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);

        imgmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(FindFriendsHomeActivity.this,CurrenLocationMap.class);
                startActivity(i);
            }
        });

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate = df.format(c.getTime());
        // formattedDate have current date/time


        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLocation();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewData();
            }
        });

        CheckGpsStatus();

        // Get Instance for firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Get Instance
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // GET USER MAIL ID
        USERMAIL = firebaseAuth.getCurrentUser().getEmail().replace(".","_");
    }

    @Override
    public void onLocationChanged(Location location) {

        //To hold location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Latitude=location.getLatitude();
        Langnitude=location.getLongitude();
        try {
            geocoder = new Geocoder(FindFriendsHomeActivity.this, Locale.ENGLISH);
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("\n");
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                // Toast.makeText(getApplicationContext(),strReturnedAddress.toString(),Toast.LENGTH_LONG).show();
                address.setText(strReturnedAddress.toString());
                spinner.setVisibility(View.GONE);
                mark.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
            }
            else{
                Toast.makeText(getApplicationContext(),"No Address returned!",Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"can't get Address!",Toast.LENGTH_LONG).show();
        }
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


    public void viewData()
    {
        Intent intent = new Intent(FindFriendsHomeActivity.this,FriendListActivity.class);
        startActivity(intent);
    }


    public void CheckGpsStatus(){

        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(GpsStatus == true)
        {
            Toast.makeText(getApplicationContext(),"Location Services is Enabled",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(),"Location Services is Disabled",Toast.LENGTH_SHORT).show();
            showDialog2();
        }

    }

    public void showDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Please Enable Location Services");

        builder.setPositiveButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent1);
            }
        });
        builder.show();
    }

    private void addLocation()
    {

        databaseReference.child(USERMAIL).setValue(Latitude.toString() + ":" + Langnitude.toString());

        Toast.makeText(getApplicationContext(),"Successfully saved..",Toast.LENGTH_SHORT).show();

    }

}
