package com.canadiansolar.a50wportablesolargenerator;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;


public class BT extends Activity {
    public UUID MY_UUID;

    Thread workerThread;

    private Set<BluetoothDevice> BTDevices;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mmDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);
    }


    @Override
    protected void onStart() {
        super.onStart();
        //Set default BT Adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        MY_UUID = UUID.randomUUID();
        String suuid = MY_UUID.toString();
        Log.d("UUID", suuid);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    public void Connect_HC(View view) {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        //Go through all the paired device and find the matched one. Then set the matched device as connection target
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals("HC-06")) {
                mmDevice = device;
                Log.d("device", mmDevice.toString());
                break;
            }
        }

        //Start Worker Thread since the connecting process is a block call.
        workerThread = new ConnectThread(mmDevice);
        workerThread.start();
    }

    public void onDestroy(View view) {
        super.onDestroy();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
            Log.d("Thread", "Start ConnectThread");

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();
            Log.d("Thread", "Start Run");

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                Log.d("Thread", "Tried Connect");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
            }

            // Do work to manage the connection (in a separate thread)

        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
/*        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }*/
    }


}