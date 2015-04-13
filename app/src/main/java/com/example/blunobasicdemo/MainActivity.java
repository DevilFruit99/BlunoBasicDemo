package com.example.blunobasicdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        onCreateProcess();														//onCreate Process by BlunoLibrary
        
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
                    pauseScroll.setImageResource(R.drawable.pausesign);
                } else {
                    pauseScroll.setImageResource(R.drawable.playsign);
                }
            }
        });

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

        try {
            FileOutputStream out = openFileOutput(fileName, Context.MODE_APPEND);
            out.write(theString.getBytes());
            out.close();
            //add Toast
            // writer = new FileWriter(file);
            // writer.write(theString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        serialReceivedText.append(theString);                            //append the text into the EditText

        if (scroll) {
            scrollToBottom();
        }


        //The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
					
	}

}