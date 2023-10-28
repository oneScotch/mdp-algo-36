package com.example.androidgroup36;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothPage extends AppCompatActivity {
    private static final String TAG = "Bluetooth Popup Page";
    private String connStatus;
    BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> newBTDevices;
    public ArrayList<BluetoothDevice> pairedBTDevices;
    public DeviceListAdapter newDeviceListAdapter;
    public DeviceListAdapter pairedDeviceListAdapter;
    TextView connStatusTextView;
    ListView otherDevicesListView;
    ListView pairedDevicesListView;
    Button connectBtn;
    ProgressDialog myDialog;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    BluetoothConnectionService mBluetoothConnection;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothDevice mBTDevice;

    boolean retryConnection = false;
    Handler reconnectionHandler = new Handler();


    //Reconnection to ensure that the bluetooth attempts to reconnect to the device
    Runnable reconnectionRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (BluetoothConnectionService.BluetoothConnectionStatus == false) {
                    Log.d(TAG, "Reconnecting");
                    startBTConnection(mBTDevice, myUUID);
                    Toast.makeText(BluetoothPage.this, "Reconnection Success", Toast.LENGTH_SHORT).show();

                }
                reconnectionHandler.removeCallbacks(reconnectionRunnable);
                retryConnection = false;
            } catch (Exception e) {
                Log.d(TAG, "Reconnecting False");
                e.printStackTrace();
                Toast.makeText(BluetoothPage.this, "Failed to reconnect, trying in 5 second", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bttest);
        Log.d(TAG, "I'm created!");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        mBTDevice=
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Switch bluetoothSwitch = (Switch) findViewById(R.id.bluetoothSwitch);
        if(mBluetoothAdapter.isEnabled()){
            bluetoothSwitch.setChecked(true);
            bluetoothSwitch.setText("ON");
        }

        otherDevicesListView = (ListView) findViewById(R.id.otherDevicesListView);
        pairedDevicesListView = (ListView) findViewById(R.id.pairedDevicesListView);
        newBTDevices = new ArrayList<>();
        pairedBTDevices = new ArrayList<>();

        connectBtn = (Button) findViewById(R.id.connectBtn);
        IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(onAndOffWatcher, BTIntent);

        IntentFilter discoverIntent = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(scannerModeWatcher, discoverIntent);
        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(scannerWatcher, discoverDevicesIntent);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(pairingWatcher, filter);

        IntentFilter filter2 = new IntentFilter("ConnectionStatus");
        LocalBroadcastManager.getInstance(this).registerReceiver(connectionWatcher, filter2);

        otherDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBluetoothAdapter.cancelDiscovery();
//                pairedDevicesListView.setAdapter(mPairedDevlceListAdapter);

                String deviceName = newBTDevices.get(i).getName();
                String deviceAddress = newBTDevices.get(i).getAddress();
                Log.d(TAG, "onItemClick: A device is selected.");
                Log.d(TAG, "onItemClick: DEVICE NAME: " + deviceName);
                Log.d(TAG, "onItemClick: DEVICE ADDRESS: " + deviceAddress);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Log.d(TAG, "onItemClick: Initiating pairing with " + deviceName);
                    boolean success = newBTDevices.get(i).createBond();
                    if(success) Log.d(TAG,"will pair");
                    Log.d(TAG,"*********************************************************");
                    Log.d(TAG,"after createBond");
                    pairedBTDevices.clear();
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    for(BluetoothDevice d : pairedDevices){
                        Log.d(TAG,"*********************************************************");
                        Log.d(TAG, "Paired Devices: "+ d.getName() +" : " + d.getAddress());
                        pairedBTDevices.add(d);
                        pairedDeviceListAdapter = new DeviceListAdapter(BluetoothPage.this, R.layout.device_adapter_view, pairedBTDevices);
                    }
                    pairedDevicesListView.setAdapter(pairedDeviceListAdapter);
                    mBluetoothConnection = new BluetoothConnectionService(BluetoothPage.this);
                    mBTDevice = newBTDevices.get(i);
                }
            }
        });

        pairedDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBluetoothAdapter.cancelDiscovery();
                otherDevicesListView.setAdapter(newDeviceListAdapter);

                String deviceName = pairedBTDevices.get(i).getName();
                String deviceAddress = pairedBTDevices.get(i).getAddress();
                Log.d(TAG, "onItemClick: A device is selected.");
                Log.d(TAG, "onItemClick: DEVICE NAME: " + deviceName);
                Log.d(TAG, "onItemClick: DEVICE ADDRESS: " + deviceAddress);
                //   com.example.androidgroup18.BluetoothConnectionService.btDevice =mPairedBTDevices.get(i);
                Toast.makeText(BluetoothPage.this, deviceName, Toast.LENGTH_LONG).show();
                mBluetoothConnection = new BluetoothConnectionService(BluetoothPage.this);
                mBTDevice = pairedBTDevices.get(i);
            }
        });

        // on and off bluetooth switch
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Log.d(TAG, "onChecked: Switch button toggled. Enabling/Disabling Bluetooth");
                if(isChecked){
                    compoundButton.setText("ON");
                }else
                {
                    compoundButton.setText("OFF");
                }

                if(mBluetoothAdapter ==null){
                    Log.d(TAG, "enableDisableBT: Device does not support Bluetooth capabilities!");
                    Toast.makeText(BluetoothPage.this, "Device Does Not Support Bluetooth capabilities!", Toast.LENGTH_LONG).show();
                    compoundButton.setChecked(false);
                }
                else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Log.d(TAG, "enableDisableBT: enabling Bluetooth");
                        Log.d(TAG, "enableDisableBT: Making device discoverable for 600 seconds.");

                        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600);
                        startActivity(discoverableIntent);

                        compoundButton.setChecked(true);

                        IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                        registerReceiver(onAndOffWatcher, BTIntent);

                        IntentFilter discoverIntent = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                        registerReceiver(scannerModeWatcher, discoverIntent);
                    }
                    if (mBluetoothAdapter.isEnabled()) {
                        Log.d(TAG, "enableDisableBT: disabling Bluetooth");
                        mBluetoothAdapter.disable();

                        IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                        registerReceiver(onAndOffWatcher, BTIntent);
                    }
                }
            }
        });

        // connect button
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBTDevice ==null)
                {
                    Toast.makeText(BluetoothPage.this, "Please Select a Device before connecting.", Toast.LENGTH_LONG).show();
                }
                else {
                    startConnection();
                }
            }
        });


        connStatusTextView = (TextView) findViewById(R.id.connStatusTextView);
        connStatus ="Disconnected";
        sharedPreferences = getApplicationContext().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("connStatus"))
            connStatus = sharedPreferences.getString("connStatus", "");

        connStatusTextView.setText(connStatus);


        myDialog = new ProgressDialog(BluetoothPage.this);
        myDialog.setMessage("Waiting for other device to reconnect...");
        myDialog.setCancelable(false);
        myDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        /* Back Button to return to main page*/
        Button btButton = findViewById(R.id.btBack);
        btButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    // Search Button
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void toggleButtonScan(View view){
        Log.d(TAG, "toggleButton: Scanning for unpaired devices.");
        newBTDevices.clear();
        if(mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(BluetoothPage.this, "Please turn on Bluetooth first!", Toast.LENGTH_SHORT).show();
            }
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
                Log.d(TAG, "toggleButton: Cancelling Discovery.");

                checkBTPermissions();

                mBluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(scannerWatcher, discoverDevicesIntent);
            } else if (!mBluetoothAdapter.isDiscovering()) {
                checkBTPermissions();

                mBluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(scannerWatcher, discoverDevicesIntent);
            }
            pairedBTDevices.clear();
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            Log.d(TAG,String.valueOf(mBluetoothAdapter.isDiscovering()));
            Log.d(TAG,"Checking isDiscovering above");
            Log.d(TAG, "toggleButton: Number of paired devices found: "+ pairedDevices.size());
            for(BluetoothDevice d : pairedDevices){
//                Log.d(TAG,"*********************I'm called ! *****************************");
                Log.d(TAG, "Paired Devices: "+ d.getName() +" : " + d.getAddress());
                pairedBTDevices.add(d);
                pairedDeviceListAdapter = new DeviceListAdapter(this, R.layout.device_adapter_view, pairedBTDevices);
                pairedDevicesListView.setAdapter(pairedDeviceListAdapter);
            }
        }
    }

     // check bluetooth permission before using it
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");

        }
    }

    // handles the on and off of bluetooth
    private final BroadcastReceiver onAndOffWatcher = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "mBroadcastReceiver1:"+this.getClass().getSimpleName());
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onAndOffWatcher: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onAndOffWatcher: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onAndOffWatcher: STATE ON");

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onAndOffWatcher: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    // handles the scan mode
    private final BroadcastReceiver scannerModeWatcher = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "scannerModeWatcher:"+this.getClass().getSimpleName());
            if (action.equals(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                final int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "scannerModeWatcher: Discoverability Enabled.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "scannerModeWatcher: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "scannerModeWatcher: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "scannerModeWatcher: Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "scannerModeWatcher: Connected.");
                        break;
                }
            }
        }
    };

    // handles new devices discovered
    private BroadcastReceiver scannerWatcher = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");
            Log.d(TAG, "scannerWatcher:"+this.getClass().getSimpleName());
            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                newBTDevices.add(device);
                Log.d(TAG, "onReceive: "+ device.getName() +" : " + device.getAddress());
                newDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, newBTDevices);
                otherDevicesListView.setAdapter(newDeviceListAdapter);

            }
        }
    };

    // handles pairing of devices
    private BroadcastReceiver pairingWatcher = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "pairingWatcher:"+this.getClass().getSimpleName());
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BOND_BONDED.");
                    pairedBTDevices.clear();
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    for(BluetoothDevice d : pairedDevices){
                        Log.d(TAG,"*********************************************************");
                        Log.d(TAG, "Paired Devices: "+ d.getName() +" : " + d.getAddress());
                        pairedBTDevices.add(d);
                        pairedDeviceListAdapter = new DeviceListAdapter(BluetoothPage.this, R.layout.device_adapter_view, pairedBTDevices);
                    }
                    pairedDevicesListView.setAdapter(pairedDeviceListAdapter);
                    Toast.makeText(BluetoothPage.this, "Successfully paired with " + mDevice.getName(), Toast.LENGTH_SHORT).show();
                    mBTDevice = mDevice;
                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.d(TAG, "BOND_BONDING.");
                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_NONE){
                    Log.d(TAG, "BOND_NONE.");
                }
            }
        }
    };

    // handles connection status changes
    private BroadcastReceiver connectionWatcher = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice mDevice = intent.getParcelableExtra("Device");
            Log.d(TAG, "I'm in BLUETOOTH POPUP");
            String status = intent.getStringExtra("Status");
            sharedPreferences = getApplicationContext().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            TextView connStatusTextView = findViewById(R.id.connStatusTextView);
            Log.d(TAG, "I'm working");
            Log.d(TAG, status);
            if(status.equals("connected")){
                try {
                    myDialog.dismiss();
                    //  Log.d(TAG, String.valueOf(bluetooth_home.myDialog));
                    //  bluetooth_home.myDialog.dismiss();

                } catch(NullPointerException e){
                    e.printStackTrace();
                }

                Log.d(TAG, "connectionWatcher: Device now connected to "+mDevice.getName());
                Toast.makeText(BluetoothPage.this, "Device now connected to "+mDevice.getName(), Toast.LENGTH_SHORT).show();
                editor.putString("connStatus", "Connected to " + mDevice.getName());
                connStatusTextView.setText("Connected to " + mDevice.getName());
            }
            else if(status.equals("disconnected") && retryConnection == false){
                Log.d(TAG, "connectionWatcher: Disconnected from "+mDevice.getName());
                Toast.makeText(BluetoothPage.this, "Disconnected from "+mDevice.getName(), Toast.LENGTH_SHORT).show();
                mBluetoothConnection = new BluetoothConnectionService(BluetoothPage.this);
                //mBluetoothConnection.startAcceptThread();


                sharedPreferences = getApplicationContext().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("connStatus", "Disconnected");

                connStatusTextView.setText("Disconnected");
                editor.commit();

                try {
                    try {
                        if (myDialog != null) myDialog.show();
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Local dialog failure");
                    }
                    //     Log.d(TAG, String.valueOf(bluetooth_home.myDialog));
                    //      bluetooth_home.myDialog.show();
                }catch (Exception e){
                    Log.d(TAG, "BluetoothPopUp: connectionWatcher Dialog show failure");
                }
                retryConnection = true;
                reconnectionHandler.postDelayed(reconnectionRunnable, 5000);

            }
            if(status.equals("disconnected")) connStatusTextView.setText("Disconnected");
            editor.commit();
        }
    };
    // establish connection wrapper function
    public void startConnection(){
        startBTConnection(mBTDevice,myUUID);
    }

    // establish connection main function
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection");

        mBluetoothConnection.startClientThread(device, uuid);
    }

    //Unregisters receivers to avoid memory leaks
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
        try {
            unregisterReceiver(onAndOffWatcher);
            Log.d(TAG, "success unregister onAndOffWatcher");
            unregisterReceiver(scannerWatcher);
            Log.d(TAG, "success unregister scannerWatcher");
            unregisterReceiver(scannerModeWatcher);
            Log.d(TAG, "success unregister scannerModeWatcher");
            unregisterReceiver(pairingWatcher);
            Log.d(TAG, "success unregister pairingWatcher");
            LocalBroadcastManager.getInstance(this).unregisterReceiver(connectionWatcher);
            Log.d(TAG, "success unregister connectionWatcher");
        } catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }

//    @Override
//    protected void onPause() {
//        Log.d(TAG, "onPause: called");
//        super.onPause();
//        try {
//            unregisterReceiver(mBroadcastReceiver1);
//            Log.d(TAG, "success unregister1");
//            unregisterReceiver(mBroadcastReceiver2);
//            Log.d(TAG, "success unregister2");
//            unregisterReceiver(mBroadcastReceiver3);
//            Log.d(TAG, "success unregister3");
//            unregisterReceiver(mBroadcastReceiver4);
//            Log.d(TAG, "success unregister4");
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver5);
//            Log.d(TAG, "success unregister5");
//        } catch(IllegalArgumentException e){
//            e.printStackTrace();
//        }
//    }
/*
    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("mBTDevice", mBTDevice);
        data.putExtra("myUUID",myUUID);
        setResult(RESULT_OK, data);
        super.finish();
    }*/
}