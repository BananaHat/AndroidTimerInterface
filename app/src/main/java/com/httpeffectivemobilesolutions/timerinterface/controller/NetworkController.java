package com.httpeffectivemobilesolutions.timerinterface.controller;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.httpeffectivemobilesolutions.timerinterface.model.TimerModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Gabriel on 7/9/2015.
 * This class will hold all the logic needed to find, connect to and read data from network based
 * timers.
 */
public class NetworkController {

    private static final String TAG = NetworkController.class.getSimpleName();
    /**
     * Port that the Arduino timers broadcast on
     */
    private static final int DISCOVERY_PORT = 5048;
    /**
     * list of IP addresses so we don't add the same timer twice;
     */
    private ArrayList<InetAddress> mTimerAddresses= new ArrayList();
    /**
     * Application Context
     */
    private Context mContext;
    /**
     * Main thread Handler
     */
    private Handler mHandler;

    /**
     * Creates a new NetworkController that will have access to the application context and handler.
     * @param context
     * @param handler
     */
    NetworkController(Context context, Handler handler){

        mContext = context;
        mHandler = handler;

    }

    /**
     * This method starts a new thread that listens for broadcasts sent out by the timers
     * @param timeout
     */
    public void broadcastListen(final int timeout){

        new Thread(){
            public void run(){
                Log.d(TAG, "Listening for broadcasts");
                try {
                    DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT, InetAddress.getByName("0.0.0.0"));
                    socket.setBroadcast(true);
                    socket.setSoTimeout(timeout);
                    long endTime = System.currentTimeMillis() + timeout;
                    byte[] buf = new byte[1024];
                    while (endTime > System.currentTimeMillis()) {
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);
                        String s = new String(packet.getData(), 0, packet.getLength());
                        Log.d(TAG, s);
                        final InetAddress address = InetAddress.getByName(s.replace("multiponics:", ""));
                        if (!mTimerAddresses.contains(address)) {
                            mTimerAddresses.add(address);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AddTimer(address);
                                }
                            });
                        }
                    }
                } catch (SocketException e) {
                    Log.e(TAG, e.toString(), e);
                } catch (IOException e) {
                    Log.e(TAG, e.toString(), e);
                }
            }
        }.start();
    }

    /**]
     * Adds a found timer;
     * @param address
     */
    public void AddTimer(final InetAddress address){
        new Thread(){
            public void run() {
                try {
                    String json = getHttpReply(3000, new URL("http://" + address.getHostName() + "/id"));
                    if (null != json) {
                        Log.d(TAG, json);
                        TimerModel timer = new TimerModel(json);
                        TimerModel.addTimer(timer);
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.toString(), e);
                }
            }
        }.start();
    }

    /**
     * Makes an HTTP connection to the URL and returns the reply as a string.
     * @param timeout
     * @param url
     * @return
     * @throws IOException
     */
    public String getHttpReply(final int timeout, URL url) throws IOException {



        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);
            InputStream is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = getStringFromInputStream(is, "UTF-8");

            is.close();
            conn.disconnect();

            return contentAsString;


        } catch (MalformedURLException e) {
            Log.e(TAG, e.toString(), e);
        } catch (ProtocolException e) {
            Log.e(TAG, e.toString(), e);
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
        }
        return null;
    }


    /**
     * Creates a string from the input stream of the HTTP server.
     * @param stream
     * @param charsetName
     * @return
     * @throws IOException
     */
    public static String getStringFromInputStream(InputStream stream, String charsetName) throws IOException
    {
        int n;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, charsetName);
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
        return writer.toString();
    }
}
