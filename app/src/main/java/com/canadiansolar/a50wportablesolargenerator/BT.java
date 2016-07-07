package com.canadiansolar.a50wportablesolargenerator;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import static java.lang.Integer.*;
import static java.lang.String.*;


public class BT extends Activity {
    public UUID MY_UUID;
    public final String bondedDevice = "HC-06";
    TextView Voltage, Current, Temperature;

    Thread connectThread;
    Thread manageConnectedThread;
    Thread sendingThread;

    Handler rHandle;

    private Set<BluetoothDevice> BTDevices;
    private BluetoothAdapter mBluetoothAdapter;

    BluetoothSocket mmSocket;
    InputStream mmInStream;
    OutputStream mmOutStream;
    BluetoothDevice mmDevice;

    String voltageStringH;
    String voltageStringL;
    String voltageString;
    String currentString;
    String temperatureString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);
        rHandle = new Handler();
        Temperature = (TextView) findViewById(R.id.temp);


        //Set default BT Adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
        }
        //IMPORTANT: This UUID is exclusively for Serial. Do NOT change and make sure it contains lower case ONLY, or the 'fromString' won't work.
        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        Log.d("UUID", MY_UUID.toString());

    }

    @Override
    protected void onResume() {
        super.onResume();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        //Go through all the paired device and find the matched one. Then set the matched device as connection target
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals(bondedDevice)) {
                mmDevice = device;
                Log.d("device", mmDevice.toString());
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mmSocket != null && mmSocket.isConnected()) {
            try {
                mmSocket.close();
                Log.d("UI_Thread", "Disconnected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("UI_Thread", "No Active Connection");
        }
    }

    public void Connect(View view) {
        //Start Worker Thread since the connecting process is a block call.
        connectThread = new ConnectThread(mmDevice);
        connectThread.start();
    }

    public void SendLB(View view) throws IOException {
        mmOutStream.write("\n\r".getBytes());
        Log.d("Serial Write", "Write Successfully");
    }

    public void Disconnect(View view) throws IOException {
        if (mmSocket != null && mmSocket.isConnected()) {
            //sendingThread.interrupt();
            mmSocket.close();
            Log.d("UI_Thread", "Disconnected");
        } else {
            Log.d("UI_Thread", "No Active Connection");
        }
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
            if (mmSocket != null && mmSocket.isConnected()) {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                Log.d("C_Thread", "Connecting");
                mmSocket.connect();
                Log.d("C_Thread", "Connected Successfully!");
                // Do work to manage the connection (in a separate thread)
                manageConnectedThread = new manageConnectedThread(mmSocket);
                manageConnectedThread.start();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                    Log.d("ERROR", "Connection Failed!!!!!!");
                } catch (IOException closeException) {
                }
            }
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
            final byte[] buffer = new byte[16];  // buffer store for the stream
            int[] data = new int[16];
            //Always waiting for data
            while (mmSocket.isConnected()) {
                try {
                    // Read from the InputStream.This is a BLOCKING CALL, the thread will wait this call to complete. In other words, it will wait until exception or data is received.
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

                    sleep(100);//Small delay to let data fill the buffer
//                    rHandle.post(new Runnable() {
//                        @Override
//                        public void run() {
//                        Temperature.setText(Byte.toString(buffer[10]));
//                        }
//                    });


                    voltageString = Integer.toString(((buffer[1] & 0xFF) * 255) + (buffer[2] & 0xFF));

                    currentString = Integer.toString(((buffer[3] & 0xFF) * 255) + (buffer[4] & 0xFF));

                    temperatureString = Integer.toString(buffer[11]);

                    Log.d("TEMPERATURE", temperatureString);
                    Log.d("VOLTAGE", voltageString);
                    Log.d("CURRENT", currentString);

                } catch (IOException e) {
                    //Log.d("E", "IO");
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
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

    public class sendingThread extends Thread {
        public void sendingThread() {
        }

        public void run() {
            while (mmSocket != null && mmSocket.isConnected()) {
                try {
                    mmOutStream.write("\n\r".getBytes());
                    Log.d("Serial Write", "Write Successfully");
                    sleep(1000);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}