package com.example.blunobasicdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.example.blunobasicdemo.MainActivity;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class UserActivity extends Activity {
    protected String allData="";
    public static ParseObject reading=UserActivity.reading=new ParseObject("reading");
    String []dataArray=new String[40];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        //PARSE INITIALIZING CHANNEL
        Parse.initialize(this, "GgQpxLEo1O4uL5OV3DEOY84CffBDXLXPXUTPPFuY", "vu6LxoaBicQgybVq20rBRPU7GeowILWmwoc6Lq3K");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        // Locate the class table named "reading" in Parse.com
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                "reading");
        // Locate the objectId from the class
        query.getInBackground("ibPBwcW6tI", new GetCallback<ParseObject>() {

            @Override
            public void done(ParseObject object, ParseException e) {
                //Locate the column named "Temp" and set the string
                ParseFile fileObj = (ParseFile)object.get("Temp");
                fileObj.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if (e == null) {
                            Log.d("test", "We've got data in data.");
                            // Decode the Byte[] into
                            String s = new String(bytes);
                            dataArray = s.split("\\n");
                            System.out.println("dataarr size="+dataArray.length);

                            DataPoint[] dataPoints = new DataPoint[40];

                            for(int i=0;i<dataArray.length;i++) {
                               String[] temp = dataArray[i].split(",");
                                double tempd = Double.parseDouble(temp[1]);
                                System.out.println("temp="+tempd);
                                dataPoints[i]=new DataPoint(i,tempd);
                               //System.out.println("tempd="+tempd);

                            }
                            GraphView graph = (GraphView) findViewById(R.id.graph);
                            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
                            graph.addSeries(series);
                        } else {
                            Log.d("test",
                                    "There was a problem downloading the data.");
                        }
                    }
                });
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
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
            Intent dummyActivity = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(dummyActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
