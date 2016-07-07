package com.canadiansolar.a50wportablesolargenerator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class BT extends AppCompatActivity {
    public UUID MY_UUID;
    public final String bondedDevice = "HC-06";
    TextView Voltage, Current, Temperature, status;
    Button connectB, disconnectB, refreshB;

    Thread connectThread;
    Thread manageConnectedThread;
    Thread sendingThread;

    Handler rHandle;

    private BluetoothAdapter mBluetoothAdapter;

    BluetoothSocket mmSocket;
    InputStream mmInStream;
    OutputStream mmOutStream;
    BluetoothDevice mmDevice;


    String voltageString;
    String currentString;
    String temperatureString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bt);
        setTitle("Bluetooth");
        rHandle = new Handler();

        Temperature = (TextView) findViewById(R.id.temp);
        Current = (TextView) findViewById(R.id.C);
        Voltage = (TextView) findViewById(R.id.V);
        status = (TextView) findViewById(R.id.status);

        connectB = (Button) findViewById(R.id.connectButton);
        disconnectB = (Button) findViewById(R.id.disconnectButton);
        refreshB = (Button) findViewById(R.id.refreshButton);

        disconnectB.setVisibility(View.INVISIBLE);
        refreshB.setVisibility(View.INVISIBLE);

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
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            if (mmSocket != null && mmSocket.isConnected()) {
                mmSocket.close();
                Log.d("UI_Thread", "Disconnected");

                connectB.setVisibility(View.VISIBLE);
                disconnectB.setVisibility(View.INVISIBLE);
                refreshB.setVisibility(View.INVISIBLE);
                status.setText("Disconnected");
                status.setTextColor(Color.GREEN);
                Voltage.setText("N/A");
                Current.setText("N/A");
                Temperature.setText("N/A");
            } else {
                Log.d("UI_Thread", "No Active Connection");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Connect(View view) {
        status.setText("Connecting");
        status.setTextColor(Color.YELLOW);
        //Start Worker Thread since the connecting process is a block call.
        connectThread = new ConnectThread(mmDevice);
        connectThread.start();
    }

    public void SendLB(View view) throws IOException {

        if (mmSocket != null && mmSocket.isConnected()) {
            mmOutStream.write("\n\r".getBytes());
            Log.d("Serial Write", "Write Successfully");
        }
    }

    public void Disconnect(View view) throws IOException {
        if (mmSocket != null && mmSocket.isConnected()) {
            mmSocket.close();
            Log.d("UI_Thread", "Disconnected");

            connectB.setVisibility(View.VISIBLE);
            disconnectB.setVisibility(View.INVISIBLE);
            refreshB.setVisibility(View.INVISIBLE);
            status.setText("Disconnected");
            status.setTextColor(Color.GREEN);
            Voltage.setText("N/A");
            Current.setText("N/A");
            Temperature.setText("N/A");
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

                if (mmSocket != null && mmSocket.isConnected()) {
                    //Change Button Visibility and status TextView
                    rHandle.post(new Runnable() {
                        @Override
                        public void run() {
                            connectB.setVisibility(View.INVISIBLE);
                            disconnectB.setVisibility(View.VISIBLE);
                            refreshB.setVisibility(View.VISIBLE);
                            status.setText("Connected");
                            status.setTextColor(Color.CYAN);
                        }
                    });
                }

                // Do work to manage the connection (in a separate thread)
                manageConnectedThread = new manageConnectedThread(mmSocket);
                manageConnectedThread.start();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();

                    //Change status TextView
                    rHandle.post(new Runnable() {
                        @Override
                        public void run() {
                            status.setText("Connection Failed");
                            status.setTextColor(Color.RED);
                        }
                    });

                    Log.d("ERROR", "Connection Failed!!!!!!");
                } catch (IOException closeException) {
                }
            }
        }

    }

    public class manageConnectedThread extends Thread {
        public manageConnectedThread(BluetoothSocket socket) throws IOException {
            Log.d("M_Thread", "Manage Thread has Started");
            sendingThread = new sendingThread();
            sendingThread.start();

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

            mmOutStream.write("\n\r".getBytes());


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
//                       Code below are for test receiving only.
/*                        String hex = Integer.toHexString(buffer[index] & 0xFF);
                        if (hex.length() == 1) {
                            hex = '0' + hex;
                        }
                        Log.d("RECEIVED", hex);*/

//                      Analyze the data once all 16 numbers are received.
                        if (index == 15) {
                            voltageString = Double.toString((((buffer[1] & 0xFF) * 255) + (buffer[2] & 0xFF)) / 100.0);
                            Log.d("VOLTAGE", voltageString);
                            currentString = Double.toString((((buffer[3] & 0xFF) * 255) + (buffer[4] & 0xFF)) / 100.0);
                            Log.d("CURRENT", currentString);
                            temperatureString = Integer.toString(buffer[11]);
                            Log.d("TEMPERATURE", temperatureString);

//                      Update UI.
                            rHandle.post(new Runnable() {
                                @Override
                                public void run() {
                                    Voltage.setText(voltageString);
                                    Current.setText(currentString);
                                    Temperature.setText(temperatureString);
                                }
                            });

                        }
                    }
                    sleep(200);

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