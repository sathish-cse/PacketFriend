package ace.cse.finalyrproject.spotter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LocationView extends AppCompatActivity {

    private WebView webview;
    String content;
    Integer columns,rows;
    List<String> columnname= new ArrayList<String>();
    JSONArray jsonarray=new JSONArray();
    JSONObject jsonobj=new JSONObject();
    public static int index=-1;
    public static String marker="";
    ArrayList<String> Latitude = new ArrayList<>();
    ArrayList<String> Lognitude = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_view);

        webview=(WebView)findViewById(R.id.webView);


        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.setInitialScale(2);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new WebAppInterface(this), "Android");
        LoadData();

        Toast.makeText(getApplicationContext(),"Click address to view the map..",Toast.LENGTH_LONG).show();
    }

    private void LoadData()
    {
        try {
            jsonarray = new JSONArray(CurrentLocation.Result);
            for (int i = 0; i < jsonarray.length(); i++) {
                jsonobj = jsonarray.getJSONObject(i);
            }
            rows = jsonarray.length();
            columns = jsonobj.length();

            JSONArray jArray = new JSONArray(CurrentLocation.Result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject object = jArray.optJSONObject(i);
                Iterator<String> iterator = object.keys();
                while (iterator.hasNext()) {
                    columnname.add(iterator.next());
                }
            }

            content = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<style>\n" +
                    "table {\n" +
                    "    font-family: arial, sans-serif;\n" +
                    "    border-collapse: collapse;\n" +
                    "    width: 100%;\n" +
                    "}\n" +
                    "tfoot { background-color: #6666ff;color:#fcfcfc;}" +
                    "\n" +
                    "td, th {\n" +
                    "    border: 1px solid #dddddd;\n" +
                    "    text-align: left;\n" +
                    "    padding: 8px;\n" +
                    "}\n" +
                    "\n" +
                    "th{\n" +
                    "    background-color: #FF9900;\n" +
                    "color: #fcfcfc" +
                    "}\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    "<script type=\"text/javascript\">\n" +
                    "    function showAndroidToast(toast) {\n" +
                    "        Android.showToast(toast);\n" +
                    "    }\n" +
                    "</script>"+
                    "\n" +
                    "<table>\n" +
                    "  <tr>\n";
            for (int i = 0; i < columns; i++) {
                content = content + " <th> <center> " + columnname.get(i).toUpperCase() + "</center></th>\n";
            }
            content = content + "  </tr>\n";

            Integer sno=0;
            for (int i = 0; i < rows; i++) {
                content = content + "  <tr onClick=\"showAndroidToast('"+i+"')\" />\n";
                for (int j = 0; j < columns; j++) {
                    try {
                        sno=i+1;
                        jsonobj = jsonarray.getJSONObject(i);
                        if (columnname.get(j).equals("SNo")) {
                            content = content + "    <td><center>" +sno.toString()  + "</center></td>\n";
                        }
                        else{
                            content = content + "    <td><center>" + jsonobj.getString(columnname.get(j)) + "</center></td>\n";
                            if(columnname.get(j).equals("Latitude"))
                                Latitude.add(jsonobj.getString("Latitude"));
                            if(columnname.get(j).equals("Longitude"))
                                Lognitude.add(jsonobj.getString("Longitude"));
                        }

                    } catch (Exception er) {
                    }
                }
                content = content + "  </tr>\n";
            }
            content = content + "</table>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>\n";
            webview.loadData(content, "text/html", null);
        }
        catch(Exception er)
        {

        }
    }


    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(int toast) {
            //  Toast.makeText(getApplicationContext(),toast , Toast.LENGTH_SHORT).show();

            index=toast;
            Intent i=new Intent(LocationView.this,CurrenLocationMap.class);
            i.putExtra("Latitude",Latitude.get(toast));
            i.putExtra("Longitude",Lognitude.get(toast));
            startActivity(i);

            //Log.e("~~~~~~``",toast);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
