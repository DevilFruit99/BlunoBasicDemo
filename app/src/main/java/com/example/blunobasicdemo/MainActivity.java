package com.example.blunobasicdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

public class MainActivity  extends BlunoLibrary {
    public FileWriter writer;
    File root = Environment.getExternalStorageDirectory();
    private Button buttonScan;
	private Button buttonSerialSend;
	private EditText serialSendText;
	private TextView serialReceivedText;
    private ScrollView mScrollView;
    private ImageButton pauseScroll;
    private boolean scroll = true;
    private String fileName = "BlunoBasicDemo.csv";
    private String EXTFOLDERNAME = "BlunoBasicDemo";
    private double HOT_TEMP = 100.0;
    private double NORMAL_TEMP = 90.0;
    private int TEMP_FLAG = 0;
    private File rootDir;
    private File dataFile;
    //private File cloudFile;
    private String bunchOfLines="";
    private int i=0;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        onCreateProcess();														//onCreate Process by BlunoLibrary

        //INITIALIZE WAKE LOCK TO KEEP DEVICE AWAKE
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        //PARSE INITIALIZING CHANNEL
        Parse.initialize(this, "GgQpxLEo1O4uL5OV3DEOY84CffBDXLXPXUTPPFuY", "vu6LxoaBicQgybVq20rBRPU7GeowILWmwoc6Lq3K");
        ParseInstallation.getCurrentInstallation().saveInBackground();


        //MAKE A DIR IN APP ROOT FOLDER
        rootDir = getStorageDir(this, "Bluno Files");
        dataFile = new File(rootDir,"blunoData.csv");

        //ENABLES PUSH NOTIFICATIONS, REGISTERS DEVICE WITH PARSE AND ALLOWS NOTIFICATIONS
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });


/*
        ParseFile dataFile = (ParseFile)reading.get("Temp");

        dataFile.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    // data has the bytes for the image
                    System.out.println(data.toString());
                } else {
                    // something went wrong
                }
            }
        });
*/
        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200
		
        serialReceivedText=(TextView) findViewById(R.id.serialReveicedText);	//initial the EditText of the received data
        mScrollView = (ScrollView) findViewById(R.id.SCROLLER_ID);

        File file = getApplicationContext().getFileStreamPath(fileName);
        Toast toast;
        if (file.exists()) {
            toast = Toast.makeText(getApplicationContext(), "The file exists" + file.getAbsolutePath(), Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(getApplicationContext(), "The file Does Not exists", Toast.LENGTH_SHORT);
        }
        toast.show();
        //try {
        //    writer.write("Temp,CO,Smoke");
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
			}
		});

        pauseScroll = (ImageButton) findViewById(R.id.pauseScrollButton);
        pauseScroll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                scroll = !scroll;
                if (scroll) {
                    pauseScroll.setImageResource(R.drawable.pause);
                } else {
                    pauseScroll.setImageResource(R.drawable.play);
                }
            }
        });

	}


    public File getStorageDir(Context context, String dirName) {
        // make a directory in the app directory.
        File file = new File(context.getExternalFilesDir(
                null), dirName);
        if (!file.mkdirs()) {
            Log.e("File", "Directory not created");
        }
        return file;
    }

	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();														//onResume Process by BlunoLibrary
	}
	
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);                    //onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();														//onPause Process by BlunoLibrary
    }
	
	protected void onStop() {
		super.onStop();
		onStopProcess();														//onStop Process by BlunoLibrary
	}
    
	@Override
    protected void onDestroy() {
        super.onDestroy();	
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

    private void scrollToBottom() {
        mScrollView.post(new Runnable() {
            public void run() {
                mScrollView.smoothScrollTo(0, serialReceivedText.getBottom());
            }
        });
    }

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
		switch (theConnectionState) {											//Four connection state
		case isConnected:
			buttonScan.setText("Connected");
			break;
		case isConnecting:
			buttonScan.setText("Connecting");
			break;
		case isToScan:
			buttonScan.setText("Scan");
			break;
		case isScanning:
			buttonScan.setText("Scanning");
			break;
		case isDisconnecting:
			buttonScan.setText("isDisconnecting");
			break;
		default:
			break;
		}
	}

	@Override
	public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
		// TODO Auto-generated method stub
        i++;
        bunchOfLines+= i+","+theString;
        if(i%40==0) {
            i=0;
            //WRITE TO EXTERNAL STORAGE
            try {
                FileOutputStream out = new FileOutputStream(dataFile);
                out.write(bunchOfLines.getBytes());
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //SAVE TO CLOUD
            byte[] data = bunchOfLines.getBytes();
            ParseFile file = new ParseFile("bluno.txt",data);
            file.saveInBackground();

           UserActivity.reading.put("Temp", file);
           UserActivity.reading.saveInBackground();
           Toast toast = Toast.makeText(getApplicationContext(),"file uploaded",Toast.LENGTH_LONG);
           toast.show();

            bunchOfLines="";
        }

       serialReceivedText.append(theString);                            //append the text into the EditText


        String []values = theString.split(",");
        Double temp;
        try {
            if(!values.equals("")) {
                temp = Double.parseDouble(values[0]);
            }
            else{
                temp = NORMAL_TEMP;
            }
        //serialReceivedText.append(temp.toString()+"\n");

        ParsePush push = new ParsePush();

        if(temp>HOT_TEMP){
            if(TEMP_FLAG==0) {
                push.setMessage("Emergency: temp in room is " + temp);
                push.sendInBackground();
                TEMP_FLAG = 2;
            }
            //Toast toast = Toast.makeText(getApplicationContext(),"HOT",Toast.LENGTH_SHORT);
            //toast.show();
        }
        if(TEMP_FLAG==2&&temp<=(NORMAL_TEMP+4)){
            //safe condition
           push.setMessage("Fire is put out. Temp in room is " + temp);
           push.sendInBackground();
            TEMP_FLAG=1;
        }
        else if(TEMP_FLAG==1&&temp<=NORMAL_TEMP){
            TEMP_FLAG = 0;
        }
        }
        catch (Exception e){
            Log.i("Double Error","Something went wrong while parsing");
        }

        if (scroll) {
            scrollToBottom();
        }


        //The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
					
	}

}