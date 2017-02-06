package ace.cse.finalyrproject.spotter;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.location.Address;
        import android.location.Geocoder;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.Build;
        import android.os.Bundle;
        import android.provider.Settings;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.support.v4.content.ContextCompat;
        import android.text.format.DateFormat;
        import android.view.View;
        import android.support.design.widget.NavigationView;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.animation.Animation;
        import android.view.animation.AnimationUtils;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.MapFragment;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;

        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.List;
        import java.util.Locale;

public class CurrentLocation extends AppCompatActivity
        implements LocationListener {

    private Geocoder geocoder;
    TextView address;
    private ProgressBar spinner;
    ImageView imgmap;
    public static String map;
    Button mark,view,delete;
    SQLiteDatabase db;
    String formattedDate="";
    Double Latitude,Langnitude;
    public  static String Result="";
    boolean GpsStatus ;
    LocationManager locationManager ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);
        address=(TextView)findViewById(R.id.address);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        db=openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS location(SNo VARCHAR,date VARCHAR,Latitude VARCHAR,Longitude VARCHAR,address VARCHAR);");

        mark=(Button)findViewById(R.id.mark);
        mark.setVisibility(View.GONE);
        view=(Button)findViewById(R.id.view);
        view.setVisibility(View.GONE);
        delete = (Button)findViewById(R.id.delete);
        delete.setVisibility(View.GONE);

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
                Intent i=new Intent(CurrentLocation.this,CurrenLocationMap.class);
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
                db.execSQL("INSERT INTO location VALUES('"+0+"','"+formattedDate+"','"+Latitude.toString()+"','"+Langnitude.toString()+"','"+address.getText().toString()+"');");
                Toast.makeText(getApplicationContext(),"Location Added..",Toast.LENGTH_LONG).show();
                /* String name="",mob="",email="";
                Date d1= new Date();
              //  CharSequence s1  = DateFormat.format("MMMM d, yyyy ", d1.getTime());
                Cursor c=db.rawQuery("SELECT * FROM location", null);
                if(c.moveToFirst())
                {
                    do {

                        name= c.getString(0);
                        mob= c.getString(1);
                        email=c.getString(2);

                    }while (c.moveToNext());
                }
                Toast.makeText(getApplicationContext(),"date: "+name+"lat : "+mob+"log : "+email,Toast.LENGTH_LONG).show();*/
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewData();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        CheckGpsStatus();
    }


  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/



    @Override
    public void onLocationChanged(Location location) {

        //To hold location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Latitude=location.getLatitude();
        Langnitude=location.getLongitude();
        try {
            geocoder = new Geocoder(CurrentLocation.this, Locale.ENGLISH);
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
                delete.setVisibility(View.VISIBLE);
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
        JSONArray resultSet 	= new JSONArray();
        JSONObject returnObj 	= new JSONObject();
        Cursor cursor=db.rawQuery("SELECT * FROM location", null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        // Msg("Data",resultSet.toString(),MainActivity.this);
        Result=resultSet.toString();
        cursor.close();

        Intent i1=new Intent(CurrentLocation.this,LocationView.class);
        startActivity(i1);
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Delete all visit history?");

        builder.setPositiveButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.execSQL("DELETE FROM location;");
            }
        });

        builder.show();
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


}