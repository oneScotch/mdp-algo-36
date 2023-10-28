package com.example.androidgroup36;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.nio.charset.Charset;
import java.util.UUID;

public class xMainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "MainActivity";

    // Declare Variables
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Context context;
    BluetoothDevice mBTDevice;

    private static ArenaMap arenaMap;
    static TextView txtRobotDirection, txtRobotCoord, txtXPosition, txtYPosition;

    private static UUID myUUID;
    ProgressDialog myDialog;

    public static TextView chatBoxIncoming;
    EditText chatText;
    Button btSend;

    public static TextView statusMsg;

    Button leftArrow,rightArrow,upArrow,downArrow;
    Button btnChangeObsFace, btnResetArena, btnStartRobot, btnResetRobot, btnStartFastest, btnStartDetection;

    public boolean musicStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("incomingMessage"));

        MainActivity.context = getApplicationContext();
        this.sharedPreferences();
        editor.putString("message", "");
        editor.putString("direction","None");
        editor.putString("connStatus", "Disconnected");
        editor.commit();

        //Arena map
        arenaMap = new ArenaMap(this);
        arenaMap = findViewById(R.id.mapView);
        txtRobotDirection = findViewById(R.id.txtRobotDirection);
        txtRobotCoord = findViewById(R.id.txtRobotPosition);
        txtXPosition = findViewById(R.id.txtXPosition);
        txtYPosition = findViewById(R.id.txtYPosition);

        //Process Dialog
        myDialog = new ProgressDialog(MainAxctivity.this);
        myDialog.setMessage("Waiting for other device to reconnect...");
        myDialog.setCancelable(false);
        myDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        btSend = (Button) findViewById(R.id.btSend);
        chatBoxIncoming = (TextView) findViewById(R.id.chatBox);
        chatText = (EditText) findViewById(R.id.editText);
        chatBoxIncoming.setMovementMethod(new ScrollingMovementMethod());

        //Retrieving the preferences from main activity page
        //sharedPreferences = getActivity().getSharedPreferences(("Shared Preferences"), Context.MODE_PRIVATE);

        //Calling for messageReceiver
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("incomingMessage")); //WJ: needed for bluetooth comms

        btSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String textMsg = "" + chatText.getText().toString(); //Converts text to string
               /* SharedPreferences.Editor pref = sharedPreferences.edit();
                pref.putString("message", sharedPreferences.getString("message",""));
                pref.commit();*/
                // This method should set the text into the incoming chat box.
                //chatBoxIncoming.append(sharedPreferences.getString("message","")+ "\n" + textMsg);
                Log.d(TAG,"Message sent  " +textMsg );
                chatText.setText(("")); //Reset the textbox
                chatBoxIncoming.append("\n"+textMsg);
                msgLog(textMsg);
                BluetoothConnectionService.sendMessage(textMsg); //input parameter is a string, sendMessage sends string type only
            }
        });
        leftArrow = findViewById(R.id.buttonLeft);
        upArrow = findViewById(R.id.buttonFront);
        downArrow = findViewById(R.id.buttonBack);
        rightArrow = findViewById(R.id.buttonRight);
        statusMsg = findViewById(R.id.statusMsg);

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Robot moving to the left");

                //updateStatus("Robot moving left...");
                // Insert function to move the robot
                if (arenaMap.getCanDrawRobot()  == true) {
                    BluetoothConnectionService.sendMessage("STM:L");
                    arenaMap.moveRobot("a");
                }
                else
                {
                    updateStatus("Failed. Set Robot First");
                }

            }
        });

        upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "Robot moving forward");

                // updateStatus("Robot moving forward...");
                // ChatFragment.chatBoxIncoming.append("w\n");
                // Insert function to move the robot
                if (arenaMap.getCanDrawRobot()  == true) {
                    BluetoothConnectionService.sendMessage("STM|FC050");
                    arenaMap.moveRobot("w");
                }
                else
                {
                    updateStatus("Failed. Set Robot First");
                }
            }
        });

        downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "Robot moving backwards");

                // updateStatus("Robot moving back...");
                //ChatFragment.chatBoxIncoming.append("s\n");
                // Insert function to move the robot
                if (arenaMap.getCanDrawRobot()  == true) {
                    BluetoothConnectionService.sendMessage("STM|BC050");
                    arenaMap.moveRobot("s");
                }
                else
                {
                    updateStatus("Failed. Set Robot First");
                }
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "Robot moving to the right");
                //updateStatus("Robot moving right...");

                //  ChatFragment.chatBoxIncoming.append("d\n");
                // Insert function to move the robot
                if (arenaMap.getCanDrawRobot() == true) {
                    BluetoothConnectionService.sendMessage("STM:R");
                    arenaMap.moveRobot("d");
                }
                else
                {
                    updateStatus("Failed. Set Robot First");
                }
            }
        });

        btnStartRobot = findViewById(R.id.btnStartRobot);
        btnResetRobot = findViewById(R.id.btnResetRobot);
        btnResetArena = findViewById(R.id.btnReset);

        btnResetArena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "btnResetArena: Resetting Arena");
                //  Toast.makeText(getContext(), "Resetting Arena!", Toast.LENGTH_SHORT).show();
                arenaMap.arenaReset();
                statusMsg.setText("Robot is not ready");
            }
        });

        btnStartRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "btnStartRobot: Setting start point for robot");
                arenaMap.robotReset();
                arenaMap.setStartingPoint(true);
                Log.d(TAG, "btnStartRobot: setStartingPoint = true");
                statusMsg.setText("Robot is Ready to be deployed");
            }
        });

        btnResetRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "btnResetRobot: Robot Reseted!");
                //   Toast.makeText(getContext(), "Sending Obstacles!", Toast.LENGTH_SHORT).show();
                arenaMap.robotReset();
                statusMsg.setText("Robot is not ready");

            }

        });

        btnStartFastest = findViewById(R.id.btnStartFastest);
        btnStartDetection = findViewById(R.id.btnStartDetection);
        btnChangeObsFace = findViewById(R.id.btnChangeObsFace);
        btnChangeObsFace.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG,"btnChangeObsFace: Setting obstacle face!");
                arenaMap.setObstacleFace();
            }
        });
        btnStartDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "btnSendObstacles: Sending obstacles");
                //  Toast.makeText(getContext(), "Looking for target!", Toast.LENGTH_SHORT).show();
                BluetoothConnectionService.sendMessage("StartDetection");
                statusMsg.setText("Looking for target");

                //Log.d(TAG, "number of set obs" + arenaMap.getSetObstacles());
                statusMsg.setText("Looking for target");
            }
        });

        btnStartFastest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"This is start fastest");
                statusMsg.setText("Starting fastest path");
                BluetoothConnectionService.sendMessage("STM|START");
                statusMsg.setText("Fastest Path");
            }
        });
    }

    public static void setRobotDetails(int x, int y, String direction) {
        Log.d(TAG, "setRobotDetails: Getting current robot coordinates");

        if (x == -1 && y == -1) {
            txtRobotCoord.setVisibility(View.INVISIBLE);
            txtRobotDirection.setVisibility(View.INVISIBLE);
        } else {
            txtRobotCoord.setText(String.valueOf(x) + "," +
                    String.valueOf(y));
            txtRobotDirection.setText(direction);
        }
    }

    public static ArenaMap getArenaMap(){
        return arenaMap;
    }

    public static void setXPosition(int x){
        Log.d(TAG, "ZXC setXPosition" + x);
        txtXPosition.setText(String.valueOf(x));
    }

    public static void setYPosition(int Y){
        Log.d(TAG, "ZXC setYPosition" + Y);
        txtYPosition.setText(String.valueOf(Y));
    }

    private static void showLog(String message) {
        Log.d(TAG, message);
    }

    private BroadcastReceiver mBroadcastReceiver5 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"BroadcastReceiver 5: ");

            BluetoothDevice mDevice = intent.getParcelableExtra("Device");
            String status = intent.getStringExtra("Status");
            sharedPreferences();

            if(status.equals("connected")){
                try {
                    myDialog.dismiss();
                } catch(NullPointerException e){
                    e.printStackTrace();
                }

                Log.d(TAG, "mBroadcastReceiver5: Successfully connected to ");
                Toast.makeText(MainActivity.this, "Successfully connected", Toast.LENGTH_LONG).show();

                editor.putString("connStatus", "Connected");


            }
            else if(status.equals("disconnected")){
                Log.d(TAG, "mBroadcastReceiver5: Disconnected");
                Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_LONG).show();
                editor.putString("connStatus", "Disconnected");

                myDialog.show();
            }
            arenaMap.invalidate();
            editor.commit();
        }
    };
/*
    BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("theMessage");
            Log.d(TAG,"Message received: "+ message);

            //Required steps for message receive
          //  String newMessage = getMessageChecked(message);
            *//*if (newMessage.equals("invalid")){
                Log.d(TAG,"Message is invalid");
            } else {
                arenaMap.updateMap(newMessage);
                sharedPreferences();
                String receivedText = sharedPreferences.getString("message", "") + "\n" + newMessage;
                editor.putString("message", receivedText);
                editor.commit();

                refreshMessageReceived();
            }*/
          /*  ChatFragment.getChatBoxIncoming().setText(message);
        }
    };*/

    public static void refreshMessageReceived() {
        String received = sharedPreferences.getString("message", "");
        chatBoxIncoming.setText(sharedPreferences.getString("message", ""));
    }

    //Create a message checker to ensure that a correct message is received.


    public static void sharedPreferences() {
        sharedPreferences = MainActivity.getSharedPreferences(MainActivity.context);
        editor = sharedPreferences.edit();
    }

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    mBTDevice = (BluetoothDevice) data.getExtras().getParcelable("mBTDevice");
                    myUUID = (UUID) data.getSerializableExtra("myUUID");
                }
        }
    }

   @Override
    protected void onDestroy(){
        super.onDestroy();
        try{
            //LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver5);
        } catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        try{
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver5);
        } catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        try{
            IntentFilter filter2 = new IntentFilter("ConnectionStatus");
            LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver5, filter2);
        } catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }


    // Send message to bluetooth
    public static void printMessage(String message) {
        showLog("Entering printMessage");
        editor = sharedPreferences.edit();

        if (BluetoothConnectionService.BluetoothConnectionStatus == true) {
            byte[] bytes = message.getBytes(Charset.defaultCharset());
            BluetoothConnectionService.write(bytes);
        }
        showLog(message);
        editor.putString("message", chatBoxIncoming.getText() + "\n" + message);
        editor.commit();
       // refreshMessageReceived();
    }


    public void bluetoothActivity(View v){
        Intent i = new Intent(this,BluetoothPage.class);
        startActivity(i);
    }

    private static void msgLog (String message)
    {
        Log.d("This is the message:",message);
    }

    //Allows other activity/fragment to call to view message
    public static TextView getChatBoxIncoming()
    {
        return chatBoxIncoming;
    }
    //Need to instantiate broadcast receiver object whenever u want to receive info
    BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("receivedMessage"); //defined from bluetooth service connection
            Log.d(TAG,"Message received: "+ message);
            if(message.contains("{"))
            {
                statusMsg.setText(message.split("\"")[3]);
            }
            //Weird edge case that made updateMap crash when receiving ","
            if(!message.equals(",")||message==",")
            {
                arenaMap.mapUpdate(message);
            }

            
            chatBoxIncoming.append("\n"+message);

        }
    };

    public void sendObstacle(View v) {
        Log.d(TAG, "btnSendObstacles: Sending obstacles");
        //  Toast.makeText(getContext(), "Looking for target!", Toast.LENGTH_SHORT).show();
        //BluetoothConnectionService.sendMessage("START");
        //statusMsg.setText("Looking for target");
        BluetoothConnectionService.sendMessage(arenaMap.getObstacles() + "\n");

        //Log.d(TAG, "number of set obs" + arenaMap.getSetObstacles());
        //statusMsg.setText("Looking for target");
    }

    //Update where my vehicle is going...
    private void updateStatus(String message) {
        Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    //Checking if my robot is going out of bounds
    public boolean checkValidMovement(){
        int coordinates [] = arenaMap.getCurCoord();
        if ((coordinates[0] != 0 && coordinates[0] != 19) && (coordinates[1] != 0 && coordinates[1] != 19)) {
            return  true;
        } else {
            return false;
        }
    }



    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}