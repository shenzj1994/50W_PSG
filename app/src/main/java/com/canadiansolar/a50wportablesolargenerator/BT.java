package com.canadiansolar.a50wportablesolargenerator;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class BT extends Activity {
    public UUID MY_UUID;

    Thread connectThread;
    Thread manageConnectedThread;

    private Set<BluetoothDevice> BTDevices;
    private BluetoothAdapter mBluetoothAdapter;

    BluetoothSocket mmSocket;
    InputStream mmInStream;
    OutputStream mmOutStream;
    BluetoothDevice mmDevice;

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
        //IMPORTANT: This UUID is exclusively for Serial. Do NOT change and make sure it contains lower case ONLY, or the 'fromString' won't work.
        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        Log.d("UUID", MY_UUID.toString());
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
        connectThread = new ConnectThread(mmDevice);
        connectThread.start();
    }

    public void SendLB(View view) {
        try {
            mmOutStream.write("\n\r".getBytes());
            Log.d("Serial Write", "Write Successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Disconnect(View view) throws IOException {
        mmSocket.close();
    }

    public class ConnectThread extends Thread {


        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
            Log.d("C_Thread", "Connecting Thread has Started");

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
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                Log.d("C_Thread", "Connecting");
                mmSocket.connect();
                Log.d("C_Thread", "Connected Successfully!");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
            }
            // Do work to manage the connection (in a separate thread)
            manageConnectedThread = new manageConnectedThread(mmSocket);
            manageConnectedThread.start();
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    public class manageConnectedThread extends Thread {
        public manageConnectedThread(BluetoothSocket socket) {
            Log.d("M_Thread", "Manage Thread has Started");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {

            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream

            //Always waiting for data
            while (true) {
                try {
                    // Read from the InputStream
                    mmInStream.read(buffer);

                    //Read out first 16 bytes in the buffer, in Hex.Only valid data which starts from 01 will be kept.
                    for (int index = 0; index < 16; index++) {
                        if (buffer[0] != 1) {
                            break;
                        }
                        String hex = Integer.toHexString(buffer[index] & 0xFF);
                        if (hex.length() == 1) {
                            hex = '0' + hex;
                        }
                        Log.d("RECEIVED", hex);
                    }

                } catch (IOException e) {
                    Log.d("E", "IO");
                    break;
                }
            }
        }


        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }


    }
}