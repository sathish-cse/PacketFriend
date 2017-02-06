package ace.cse.finalyrproject.spotter;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class CurrenLocationMap extends AppCompatActivity implements OnMapReadyCallback {

    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curren_location_map);

        initMap();

    }

    private void initMap() {

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Bundle bundle = getIntent().getExtras();
        LatLng latLng = new LatLng(Double.parseDouble(bundle.getString("Latitude")), Double.parseDouble(bundle.getString("Longitude")));
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        try {
            geocoder = new Geocoder(CurrenLocationMap.this, Locale.ENGLISH);
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(bundle.getString("Latitude")), Double.parseDouble(bundle.getString("Longitude")), 1);

            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("\n");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                //Toast.makeText(getApplicationContext(),strReturnedAddress.toString(),Toast.LENGTH_LONG).show();

                map.addMarker(new MarkerOptions().position(latLng).title(strReturnedAddress.toString()));
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                map.animateCamera(CameraUpdateFactory.zoomTo(13));
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"can't get Address!",Toast.LENGTH_LONG).show();
        }

    }
}