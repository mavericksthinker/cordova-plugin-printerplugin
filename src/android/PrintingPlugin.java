package cordova.plugin.printerplugin;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
//import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.OutputStream;
//import java.util.Hashtable;
//import java.net.InetAddress;
import java.util.Arrays;
//import java.util.EnumMap;
//import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
//import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;
//import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
//import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;
//import android.os.Handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
//import android.util.Xml.Encoding;
import android.util.Base64;
//import android.view.Gravity;
//import android.widget.Toast;
import cordova.plugin.base.BasePrinter;
import cordova.plugin.exceptions.GenericPrinterException;
import cordova.plugin.usbImplementation.Printer;
import cordova.plugin.interfaces.IGenericPrinter;
import cordova.plugin.interfaces.IVariables;
//import org.json.JSONException;
//import org.json.JSONObject;
import java.util.List;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import cordova.plugin.baseDevice.BaseDevice;
import cordova.plugin.wifiPrinters.Printers;

import java.io.BufferedReader;
import java.io.FileReader;
//import java.net.DatagramPacket;
//import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
//import java.util.LinkedList;

import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static java.lang.Integer.toHexString;


/**
 * Communication between native Java and JavaScript
 */
public class PrintingPlugin extends CordovaPlugin {

    private final String PRINT = "Printing Plugin";
    private final String TAG = "USBPrinter";
    private UsbManager usbManager; //USB device manager
    private UsbDevice usbDevice;//Represents the current printer
    private PendingIntent pendingIntent;
    private IGenericPrinter iGenericPrinter; //Represents the implementation of each printer
    private BasePrinter basePrinter; //Represents the implementation of each printer
    private JSONArray entirePrintersList;
    private BluetoothAdapter mBluetoothAdapter = null;
    //private boolean qrcode = false;
    private static final String LOG_TAG = "BluetoothPrinter";
    private BluetoothSocket mmSocket; //Represents the bluetooth socket
    private BluetoothDevice mDevice, mmDevice; //Represents the current Device
    private OutputStream mmOutputStream; //Handles the output stream of data transferred to the printer
    private InputStream mmInputStream;
    private byte[] readBuffer;
    private int readBufferPosition;
    //    private final int WHITE = 0xFFFFFFFF;
//    private final int BLACK = 0xFF000000;
    private boolean success = false;
    private static boolean enableServices;
    private boolean wifiPrinters = false;
    private static final String TAGS = "WifiPrinter";
    private final static int TIME_OUT = 3000;
    private final static int PORT = 9100;
    private final static String DEFAULT_MAC = "00:00:00:00:00:00";
    private final static String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
    private final static int BUFF = 8 * 1024;
    @SuppressLint("StaticFieldLeak")
    public PrintingPlugin instance;
    private boolean isStart;
    private List<BaseDevice> list = new ArrayList<BaseDevice>();
    private WifiManager wifiManager;
    private String ipOfTheWifiPrinter;
    private Socket client = null;
    private volatile boolean stopWorker; //utilized to disconnect from the printer
    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        if (action.equals("list")) {
            //String dialog = "{theme : 'HOLO_LIGHT',progressStyle : 'SPINNER',cancelable : true,title : 'Please Wait...',message : 'Checking for connectivity services...'}";
            Toast.makeText(cordova.getActivity(), "Scanning...", Toast.LENGTH_LONG).show();
            final JSONArray json = new JSONArray();
            enableServices = enableServices(callbackContext, json);
            Toast.makeText(cordova.getActivity(), "Scanning for Printers....", Toast.LENGTH_LONG).show();
            try {
                findUSBPrinters(callbackContext, listWifiPrinters(listBT(json)));
            } catch (Exception e) {
                e.printStackTrace();
                //callbackContext.error(e.getMessage());
            }
            return true;
        } else if (action.equals("connect")) {
            String name = args.getString(0);
//            String dialog = "{theme : 'DEVICE_DARK',progressStyle : 'SPINNER',cancelable : true,title : 'Please Wait...',message : 'Connecting to the printer...'}";
//            dialogPopUp(dialog);
            Toast.makeText(cordova.getActivity(), "Connecting...", Toast.LENGTH_SHORT).show();
            if (mmSocket != null) {
                if (mmSocket.isConnected()) {
                    try {
                        mmSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (client != null) {
                if (client.isConnected()) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (Printer.usbDeviceConnection != null) {
                try {
                    disconnectUSB(callbackContext);
                } catch (Exception e) {
                    Log.d(LOG_TAG, e.getMessage());
                }
            }
            success = false;
            wifiPrinters = false;
            Log.d(LOG_TAG, entirePrintersList.toString());
            Boolean found = false;
            for (int i = 0; i < entirePrintersList.length(); i++) {
                if (name.equals(entirePrintersList.getString(i))) {
                    found = true;
                    if (success = findBT(name)) {
                        try {
                            Log.d(LOG_TAG, "bluetooth printer");
                            wifiPrinters = false;
                            Log.d(LOG_TAG, String.valueOf(wifiPrinters));
                            Log.d(LOG_TAG, String.valueOf(success));
                            connectBT(callbackContext);
                        } catch (IOException e) {
                            Log.e(LOG_TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    } else if (wifiPrinters = findWifiPrinter(name)) {
                        Log.d(LOG_TAG, "wifi printer module");
                        Log.d(LOG_TAG, String.valueOf(wifiPrinters));
                        Log.d(LOG_TAG, String.valueOf(success));
                        try {
                            success = false;
                            wifiPrinters = true;
                            connectToWifiPrinter(callbackContext);
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    } else if (!success && !wifiPrinters) {
                        Log.d(LOG_TAG, String.valueOf(wifiPrinters));
                        Log.d(LOG_TAG, String.valueOf(success));
                        wifiPrinters = false;
                        connectToUSBPrinter(callbackContext, name);
                    } else {
                        callbackContext.error("Printer Not Found: " + name + ". Unable to connect");
                    }
                }
            }
            if (!found) {
                Toast.makeText(cordova.getActivity(), "Please specify appropriate printer name", Toast.LENGTH_LONG).show();
                callbackContext.error("No Such printer found");
            }
            return true;
        } else if (action.equals("connectManually")) {
            success = false;
            wifiPrinters = false;
            String name = args.getString(0);
//            String dialog = "{theme : 'DEVICE_DARK',progressStyle : 'SPINNER',cancelable : true,title : 'Please Wait...',message : 'Connecting to the printer...'}";
//            dialogPopUp(dialog);
            Toast.makeText(cordova.getActivity(), "Connecting...", Toast.LENGTH_SHORT).show();
            if (mmSocket != null) {
                if (mmSocket.isConnected()) {
                    try {
                        mmSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (client != null) {
                if (client.isConnected()) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (Printer.usbDeviceConnection != null) {
                try {
                    disconnectUSB(callbackContext);
                } catch (Exception e) {
                    Log.d(LOG_TAG, e.getMessage());
                }
            }
            if (success = findBT(name)) {
                try {
                    wifiPrinters = false;
                    connectBT(callbackContext);
                } catch (IOException e) {
                    Log.e(PRINT, e.getMessage());
                    e.printStackTrace();
                }
            } else if (success = findWifiPrinter(name)) {
                try {
                    success = false;
                    wifiPrinters = true;
                    connectToWifiPrinter(callbackContext);
                } catch (Exception e) {
                    Log.e(PRINT, e.getMessage());
                    e.printStackTrace();
                }
            } else if (!success && !wifiPrinters) {
                wifiPrinters = false;
                connectToUSBPrinter(callbackContext, name);
            } else {
                callbackContext.error("Printer Not Found: " + name + ". Unable to connect");
            }

            return true;
        } else if (action.equals("disconnect")) {
            try {
                if (success) {
                    success = false;
                    disconnectBT(callbackContext);
                } else if (wifiPrinters) {
                    wifiPrinters = false;
                    disconnect(callbackContext);
                } else if (!success)
                    disconnectUSB(callbackContext);
                else callbackContext.error("Unable to Disconnect");
            } catch (IOException e) {
                Log.e(PRINT, e.getMessage());
                e.printStackTrace();
            }
            return true;
        } else if (action.equals("printImage")) {
            //String dialog = "{theme : 'TRADITIONAL',progressStyle : 'SPINNER',cancelable : true,title : 'Please Wait...',message : 'Printing...'}";
            try {
                String msg = "/" + args.getString(0);
                //Input stream
                InputStream mInputStream = new FileInputStream(Environment.getExternalStorageDirectory() + msg);
                //For storing the data in bytes
                byte[] buffer = new byte[8192];
                int bytesRead;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                    while ((bytesRead = mInputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] bytes = output.toByteArray();
                printImage(callbackContext, Base64.encodeToString(bytes, Base64.DEFAULT));
            } catch (IOException e) {
                Log.e(PRINT, e.getMessage());
                e.printStackTrace();
                callbackContext.error(e.getMessage());
            }
            return true;
        } else if (action.equals("printText") || action.equals("printQrCode")) {
            try {
                String msg = args.getString(0);
                printText(callbackContext, msg);
            } catch (IOException e) {
                Log.e(PRINT, e.getMessage());
                e.printStackTrace();
                //Toast.makeText(cordova.getActivity(), "Unable to print. No connected printer...", Toast.LENGTH_SHORT).show();
                callbackContext.error(e.getMessage());
            }
            return true;
        } else if (action.equals("printPOSCommand")) {
            try {
                String msg = args.getString(0);
                printPOSCommand(callbackContext, hexStringToBytes(msg));
            } catch (IOException e) {
                Log.e(PRINT, e.getMessage());
                e.printStackTrace();
                //Toast.makeText(cordova.getActivity(), "Unable to print. No connected printer...", Toast.LENGTH_SHORT).show();
                callbackContext.error(e.getMessage());
            }
            return true;
        } else if (action.equals("printBarcode")) {
            try {
                String barCode = args.getString(0);
                String barType = args.getString(1);
                String barWidth = args.getString(2);
                String barHeight = args.getString(3);
                String barHriFont = args.getString(4);
                String barHriPos = args.getString(5);
                //Log.d(PRINT, "Types :" + barType + "Width :" + barWidth + "Height :" + barHeight + "HriFont :" + barHriFont + "HriPos :" + barHriPos);
                printBarCode(callbackContext, barCode, barType, barWidth, barHeight, barHriFont, barHriPos);

            } catch (IOException e) {
                e.printStackTrace();
                //Toast.makeText(cordova.getActivity(), "Unable to print. No connected printers...", Toast.LENGTH_SHORT).show();
                callbackContext.error(e.getMessage());
            }
            return true;
        } else if (action.equals("print")) {
            try {
                outputStreamToPrinter(callbackContext);
            } catch (IOException e) {
                e.printStackTrace();
                //Toast.makeText(cordova.getActivity(), "Unable to print. No connected printers...", Toast.LENGTH_SHORT).show();
                callbackContext.error(e.getMessage());
            }
            return true;
        }
        return false;
    }

    //This will return the array list of paired bluetooth printers
    private JSONArray listBT(JSONArray json) {
        try {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    /*
                     Hashtable map = new Hashtable();
                     map.put("type", device.getType());
                     map.put("address", device.getAddress());
                     map.put("name", device.getName());
                     JSONObject jObj = new JSONObject(map);
                     */
                    if (btPrinter(device))
                        json.put(device.getName());
                    Log.d(LOG_TAG, device.getName());
                }
                //callbackContext.success(json);
                //Log.d(LOG_TAG, "Bluetooth Device Found: " + mmDevice.getName());
            } else {
                Log.d(LOG_TAG, "No bluetooth printer found. Please check if the printer is paired");
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            //callbackContext.error(errMsg);
        }
        return json;
    }

    // This will find a bluetooth printer device
    private boolean findBT(String name) {
        try {
            Log.d(LOG_TAG, "finding bluetooth printer");
            //Bluetooth Device Adapter
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Log.e(LOG_TAG, "No bluetooth adapter available");
                //Toast.makeText(cordova.getActivity(), "No Bluetooth Adapter found....", Toast.LENGTH_LONG).show();
            } else if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                cordova.getActivity().startActivityForResult(enableBluetooth, 0);
//                Toast.makeText(cordova.getActivity(), "Please check the connectivity services and try again...", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "Bluetooth is turned off,Please turn it on if you want to connect to a bluetooth printer");
                return false;
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equalsIgnoreCase(name)) {
                        mmDevice = device;
                        return true;
                    }
                }
            }

            //Log.d(LOG_TAG, "Bluetooth Device Found: " + mmDevice.getName());
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
            //callbackContext.error(errMsg);
        }
        return false;
    }

    // Tries to open a connection to the bluetooth printer device
    private void connectBT(CallbackContext callbackContext) throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();
            Toast.makeText(cordova.getActivity(), "Successfully connected to the bluetooth printer...", Toast.LENGTH_SHORT).show();
            //Log.d(LOG_TAG, "Bluetooth Opened: " + mmDevice.getName());
            callbackContext.success("Bluetooth Opened: " + mmDevice.getName());
        } catch (IOException e) {
            String errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
            Toast.makeText(cordova.getActivity(), "Unable to Connect...", Toast.LENGTH_SHORT).show();
            callbackContext.error(errMsg);
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
            Toast.makeText(cordova.getActivity(), "Unable to Connect...", Toast.LENGTH_SHORT).show();
            callbackContext.error(errMsg);
        }
    }

    private boolean btPrinter(BluetoothDevice name) throws IOException {
        try {
            mDevice = name;
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            if (mmSocket.isConnected()) {
                mmSocket.close();
                return true;
            }
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
        }
        return false;
    }


    // After opening a connection to bluetooth printer device,
    // we have to listen and check if a data were sent to be printed.
    private void beginListenForData() {
        try {
            //final Handler handler = new Handler();
            // This is the ASCII code for a newline character
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            //The tread that is being utilized
            Thread workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        /*
                                         final String data = new String(encodedBytes, "US-ASCII");
                                         readBufferPosition = 0;
                                         handler.post(new Runnable() {
                                         public void run() {
                                         myLabel.setText(data);
                                         }
                                         });
                                         */
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //This will send data to bluetooth printer
    private void printText(CallbackContext callbackContext, String msg) throws IOException {
        try {
            Log.d(LOG_TAG, String.valueOf(success));
            output.write(msg.getBytes());
            Log.d(PRINT, "Successfully appended the text data");
//            if (success) {
//                mmOutputStream.write(msg.getBytes());
//                callbackContext.success("Data Sent to Bluetooth printer");
//            } else if (wifiPrinters) {
//                prints(callbackContext, msg.getBytes());
//            } else if (!success && Printer.usbDeviceConnection != null)
//                print(callbackContext, msg.getBytes());
            // tell the user data were sent
            //Log.d(LOG_TAG, "Data Sent");

        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e(PRINT, errMsg);
            e.printStackTrace();
            Toast.makeText(cordova.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            callbackContext.error("Error in printText() : " + errMsg);
        }
    }

    private void printPOSCommand(CallbackContext callbackContext, byte[] buffer) throws IOException {
        try {
            Log.d(PRINT, String.valueOf(success));
            output.write(buffer);
//            if (success) {
//                mmOutputStream.write(buffer);
//                callbackContext.success("Data Sent");
//            } else if (wifiPrinters) {
//                prints(callbackContext, buffer);
//            } else if (!success && Printer.usbDeviceConnection != null)
//                print(callbackContext, buffer);
            // tell the user data were sent
            Log.d(PRINT, "Successfully appended the POS Command");
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e(PRINT, errMsg);
            e.printStackTrace();
            Toast.makeText(cordova.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            callbackContext.error("Error in printPOSCommand() : " + errMsg);
        }
    }

    // disconnect bluetooth printer.
    private void disconnectBT(CallbackContext callbackContext) throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            Toast.makeText(cordova.getActivity(), "Bluetooth printer disconnected", Toast.LENGTH_SHORT).show();
            callbackContext.success("Bluetooth Disconnect");
        } catch (IOException e) {
            String errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
            Toast.makeText(cordova.getActivity(), "Unable to disconnect", Toast.LENGTH_SHORT).show();
            callbackContext.error(errMsg);
        }
    }

    //This will append the image data to the ByteArrayOutputStream
    private void printImage(CallbackContext callbackContext, String msg) throws IOException {
        try {
            final String pureBase64Encoded = msg.substring(msg.indexOf(",") + 1);
            final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);

            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            int mWidth = bitmap.getWidth();
            int mHeight = bitmap.getHeight();

            bitmap = resizeImage(bitmap, 48 * 8, mHeight);

            byte[] decodedBitmap = decodeBitmap(bitmap);
            output.write(decodedBitmap);
            Log.d(PRINT, "Successfully appended the image data");
//            if (success) {
//                mmOutputStream.write(bt);
//                callbackContext.success("Data Sent");
//            } else if (wifiPrinters) {
//                prints(callbackContext, bt);
//            } else if (!success && Printer.usbDeviceConnection != null)
//                print(callbackContext, bt);
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
            //Toast.makeText(cordova.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            callbackContext.error("Error is printImage() : " + errMsg);
        }
    }

    private void outputStreamToPrinter(CallbackContext callbackContext) throws IOException {
        try {
            byte[] finalOutputStream = output.toByteArray();
            if (success) {
                mmOutputStream.write(finalOutputStream);
                callbackContext.success("Data Sent");
            } else if (wifiPrinters) {
                prints(callbackContext, finalOutputStream);
            } else if (!success && Printer.usbDeviceConnection != null)
                print(callbackContext, finalOutputStream);

            output.reset();
            Log.d(PRINT, "Reset on ByteArrayOutputStream is successfully done");
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
            Toast.makeText(cordova.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            callbackContext.error(errMsg);
        }
    }

    private Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > w) {
            float scaleWidth = ((float) w) / width;
            //float scaleHeight = ((float) h) / height + 24;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleWidth);
            return Bitmap.createBitmap(bitmap, 0, 0, width,
                    height, matrix, true);
        } else {
            Bitmap resizedBitmap = Bitmap.createBitmap(w, height + 24, Config.RGB_565);
            Canvas canvas = new Canvas(resizedBitmap);
            Paint paint = new Paint();
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(bitmap, (w - width) / 2, 0, paint);
            return resizedBitmap;
        }
    }

    private byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        List<String> list = new ArrayList<String>(); //binaryString list
        StringBuffer sb;
        //int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;
        StringBuilder zeroStr = new StringBuilder();
        if (zeroCount > 0) {
            //bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr.append("0");
            }
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;
                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160) {
                    sb.append("0");
                } else {
                    sb.append("1");
                }
            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";

        //construct xL and xH
        //there are 8 pixels per byte. In case of modulo: add 1 to compensate.
        bmpWidth = bmpWidth % 8 == 0 ? bmpWidth / 8 : (bmpWidth / 8 + 1);
        int xL = bmpWidth % 256;
        int xH = (bmpWidth - xL) / 256;

        String xLHex = toHexString(xL);
        String xHHex = toHexString(xH);
        if (xLHex.length() == 1) {
            xLHex = "0" + xLHex;
        }
        if (xHHex.length() == 1) {
            xHHex = "0" + xHHex;
        }
        String widthHexString = xLHex + xHHex;


        //construct yL and yH
        int yL = bmpHeight % 256;
        int yH = (bmpHeight - yL) / 256;

        String yLHex = toHexString(yL);
        String yHHex = toHexString(yH);
        if (yLHex.length() == 1) {
            yLHex = "0" + yLHex;
        }
        if (yHHex.length() == 1) {
            yHHex = "0" + yHHex;
        }
        String heightHexString = yLHex + yHHex;

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    private void printBarCode(CallbackContext callbackContext, String message, String barType, String barWidth, String barHeight, String barHriFont, String barHriPos) throws IOException {

        byte[] codeData = message.getBytes();
        //Log.d(LOG_TAG,"Type :"+Integer.parseInt(barType) +"Width :"+ Integer.parseInt(barWidth)+"Height :"+ Integer.parseInt(barHeight)+"HriFont :"+ Integer.parseInt(barHriFont)+"HriPos :"+ Integer.parseInt(barHriPos));
        try {
            ArrayList<Byte> command = new ArrayList<Byte>();

            //Width
            command.add((byte) 0x1D);
            command.add((byte) 0x77);
            command.add((byte) Integer.parseInt(barWidth));//Barcode Width
            //command.add((byte) 0x03);//Barcode Width

            //Height
            command.add((byte) 0x1D);
            command.add((byte) 0x68);
            command.add((byte) Integer.parseInt(barHeight));//Barcode Height
            //command.add((byte) 0x52);//Barcode Height

            //Position
            command.add((byte) 0x1D);
            command.add((byte) 0x48);
            command.add((byte) Integer.parseInt(barHriPos));//Horizontal position
            //command.add((byte) 0x02);//Horizontal position

            //human readable characters size
            command.add((byte) 0x1D);
            command.add((byte) 0x66);
            command.add((byte) Integer.parseInt(barHriFont));//Human readable characters
            command.add((byte) 0x01);//Human readable characters

            //Barcode
            command.add((byte) 0x1D);
            command.add((byte) 0x6B);
            //command.add((byte) 0x46);//Barcode Type
            command.add((byte) Integer.parseInt(barType));//Barcode Type
            command.add((byte) message.length());
            for (byte byt : codeData) command.add(byt);
            command.add((byte) 0x00);
            byte[] bytes = new byte[command.size()];
            for (int i = 0; i < command.size(); i++) {
                bytes[i] = command.get(i);
            }
            Log.i("INFO", Arrays.toString(bytes));
            output.write(bytes);
//            if (success) {
//                mmOutputStream.write(bytes);
//                callbackContext.success("Data sent");
//            } else if (wifiPrinters) {
//                prints(callbackContext, bytes);
//            } else if (!success && Printer.usbDeviceConnection != null)
//               print(callbackContext, bytes);
            Log.d(PRINT, "Successfully appended the barcode data");
        } catch (IOException e) {
            String errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
            Toast.makeText(cordova.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            callbackContext.error(errMsg);
//        } catch (GenericPrinterException e) {
//            e.printStackTrace();
//            Toast.makeText(cordova.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
//            callbackContext.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(cordova.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            callbackContext.error(e.getMessage());
        }
    }

    private List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);

                String hexString = myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;

    }

    private String myBinaryStrToHexString(String binaryStr) {
        StringBuilder hex = new StringBuilder();
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        String hexStr = "0123456789ABCDEF";
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i])) {
                hex.append(hexStr, i, i + 1);
            }
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i])) {
                hex.append(hexStr, i, i + 1);
            }
        }

        return hex.toString();
    }

    private String[] binaryArray = {"0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"};

    private byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        return sysCopy(commandList);
    }

    //New implementation, change old
    private byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    private boolean enableServices(final CallbackContext callbackContext, final JSONArray json) {
        String errMsg;
        try {

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                errMsg = "No bluetooth adapter available";
                Log.e(LOG_TAG, errMsg);
                //callbackContext.error(errMsg);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Log.d(LOG_TAG, "No bluetooth adapter available,continuing search for Wifi and USB Printers");
                            enableServices = enableWifi();
                            if (enableServices) {
                                findUSBPrinters(callbackContext, listWifiPrinters(json));
                            } else {
                                Log.d(TAGS, "Wifi was not enabled, Searching for USB printers");
                                findUSBPrinters(callbackContext, json);
                            }

                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage());
                        }
                    }
                }.start();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Log.d(LOG_TAG, "Bluetooth is not enabled. Enabling now...");
                //Toast.makeText(cordova.getActivity(), "Enable Bluetooth...", Toast.LENGTH_SHORT).show();
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                this.cordova.getActivity().startActivityForResult(enableBluetooth, 0);
                this.cordova.setActivityResultCallback(this);
                Thread.sleep(3000);
                enableServices = enableWifi();
                Log.d(LOG_TAG, "Wifi is enabled : " + String.valueOf(enableServices));
                enableServices = false;
            } else
                enableServices = enableWifi();
            Log.d(LOG_TAG, "Bluetooth and wifi is enabled : " + String.valueOf(enableServices));
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return enableServices;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void findUSBPrinters(final CallbackContext callbackContext, JSONArray printers) {

        if (entirePrintersList != null)
            entirePrintersList = new JSONArray();
        usbManager = (UsbManager) this.cordova.getActivity().getSystemService(Context.USB_SERVICE);
        basePrinter = new Printer(usbManager);
        Log.i(TAG, "Searching for the printers connected to USB");
        iGenericPrinter = (IGenericPrinter) basePrinter;

        //JSONObject jsonObj = new JSONObject();
        try {
            List<UsbDevice> lstPrinters = iGenericPrinter.findPrinters();
            for (UsbDevice usbDevice : lstPrinters) {
                if (UsbConstants.USB_CLASS_PRINTER == usbDevice.getInterface(0).getInterfaceClass()) {
                    // JSONObject printerObj = new JSONObject();
                    // printerObj.put("productName", usbDevice.getProductName());
                    // printerObj.put("manufacturerName", usbDevice.getManufacturerName());
                    // printerObj.put("deviceId", usbDevice.getDeviceId());
                    // printerObj.put("serialNumber", usbDevice.getSerialNumber());
                    // printerObj.put("vendorId", usbDevice.getVendorId());
                    printers.put(usbDevice.getProductName());
                }
            }
            if (printers.length() <= 0) {
                throw new GenericPrinterException("No printers are connected.");
            }
            entirePrintersList = printers;
            callbackContext.success(printers);
        } catch (GenericPrinterException e) {
            Log.e(TAG, e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }

    private void connectToUSBPrinter(final CallbackContext callbackContext, String printerName) {
        try {
            iGenericPrinter = (IGenericPrinter) basePrinter;
            usbDevice = iGenericPrinter.findPrinterByName(printerName);
            if (usbDevice == null)
                throw new Exception("There are no printers connected");
            pendingIntent = PendingIntent.getBroadcast(cordova.getActivity().getBaseContext(), 0,
                    new Intent(IVariables.USB_PERMISSION), 0);
            requestPermission(callbackContext);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }

    /**
     * It is necessary to grant permits to the usb port, so permits are requested
     */
    @SuppressLint("NewApi")
    private void requestPermission(final CallbackContext callbackContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    IntentFilter filter = new IntentFilter(
                            IVariables.USB_PERMISSION);
                    cordova.getActivity().registerReceiver(broadcastReceiver, filter);
                    if (usbDevice != null) {
                        usbManager.requestPermission(usbDevice,
                                pendingIntent);
                        Toast.makeText(cordova.getActivity(), "Successfully connected to the USB printer...", Toast.LENGTH_SHORT).show();
                        callbackContext.success("Permission was granted to access the USB Device and usb printer was connected");
                    } else {
                        Log.e(TAG, "USB printer was not found");
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    callbackContext.error(e.getMessage());
                }
            }
        }).start();
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                Log.i(TAG, "The Broadcast object starts");
                if (IVariables.USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        final UsbDevice printerDevice = intent
                                .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(
                                UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (printerDevice != null) {
                                Log.i("Info", "Permits are granted.");
                                Log.i(TAG, "We try to open the connection to the USB port.");
                                basePrinter.open(printerDevice);
                                Log.i(TAG, "The connection to the USB port was opened successfully.");
                                //print(printerDevice);
                            }
                        } else {
                            Log.i(TAG, "Permission denied to the printing object.");
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }

    };

    /**
     * The process of sending bytes to the selected USB printer starts.
     * //@param printerDevice
     */
    public void print(final CallbackContext callbackContext, final byte[] message) throws GenericPrinterException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "We try to send the message to the printer.");
                    basePrinter.print(message);
                    Log.i(TAG, "Successfully printed on USB Printer");
                    callbackContext.success("Printed successfully via USB printer");
                } catch (GenericPrinterException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(cordova.getActivity(), "Unable to print...", Toast.LENGTH_LONG).show();
                    callbackContext.error(e.getMessage());
                }
            }
        }).start();
    }

    private void disconnectUSB(CallbackContext callbackContext) {
        try {
            Log.i(TAG, "close connections");
            basePrinter.close();
            cordova.getActivity().unregisterReceiver(broadcastReceiver);
            Toast.makeText(cordova.getActivity(), "USB printer disconnected", Toast.LENGTH_SHORT).show();
            callbackContext.success("Disconnected from the usb printer");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            Toast.makeText(cordova.getActivity(), "Unable to Disconnect", Toast.LENGTH_LONG).show();
            callbackContext.error(e.getMessage());
        }

    }

    private boolean enableWifi() {
        try {

            Log.d(TAGS, "enabling wifi");
            wifiManager = (WifiManager) cordova.getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (wifiManager != null && !wifiManager.isWifiEnabled()) {
                Toast.makeText(cordova.getActivity(), "Enabling wifi...", Toast.LENGTH_SHORT).show();
                wifiManager.setWifiEnabled(true);
            }
        } catch (Exception e) {
            Log.e(TAGS, e.getMessage());
            return false;
        }
        return true;
    }

    @SuppressLint("HandlerLeak")
    private JSONArray listWifiPrinters(final JSONArray printers) {
        wifiManager = (WifiManager) cordova.getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            if (wifiManager != null && !wifiManager.isWifiEnabled()) {
                //Toast.makeText(cordova.getActivity(), "Enabling wifi...", Toast.LENGTH_SHORT).show();
                return printers;
            }
            if (isStart) {
                return null;
            }
            isStart = true;
            list.clear();
            Log.d(TAGS, "Getting printer info");
            final Printers printer = getInfo();
            Log.d(TAGS, "Obtain the info of Wifi Printers");
            List<String> ips = new ArrayList<String>();
            int START_IP = 1;
            int END_IP = 254;
            for (int i = START_IP; i <= END_IP; i++) {
                String starIp = getStarOrEndIp(printer.ip, i, true);
                ips.add(starIp);
//                Log.d(TAGS, "Adding task");
//                if (tryToConnect(starIp)) {
//                    printers.put(starIp);
//                    Log.d(TAGS, "Printers found in wifi" + printers.toString());
//                }
            }
//            final Object wait=new Object();
//            synchronized (wait){

            scanWifi(ips, new PrintingPlugin.OnIPScanningCallback() {
                @Override
                public void onScanningComplete(List<String> results) {
                    Log.d(TAGS, "onScanningComplete: " + results.size() + " : " + results.toString());
                    Printers printerList;
                    for (String printerIps : results) {
                        String mac = getHardwareAddress(printerIps);
                        printerList = new Printers(printerIps, mac);
                        printers.put(printerIps);
                        list.add(printerList);
                    }
                    Log.d(TAGS, "The List of all wifi Printers : " + list);
//                    wait.notify();
                }
            });
//                wait.wait();
//            }
            isStart = false;
        } catch (Exception e) {
            Log.e(TAGS, "Error while scanning for wifi" + e.getMessage());
        }
        return printers;
    }

    private Integer index = 0;

    private void resetIndex() {
        index = 0;
    }

    private void scanWifi(final List<String> ips, final PrintingPlugin.OnIPScanningCallback callback) {
        Log.d(TAGS, " scanWifi");
        final Vector<String> results = new Vector<String>();
        final int totalSize = ips.size();
        final int splitSize = 10;
        resetIndex();
        final long start = System.currentTimeMillis();
        for (int i = 0; i < totalSize; i += splitSize) {

            Log.d(TAGS, " scanning batch: " + i);
            final List<String> child = new ArrayList<String>(ips.subList(i, Math.min(totalSize, i + splitSize)));
            executeTask(new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
//                    synchronized (index) {

                    for (String ip : child) {

                        Log.d(TAGS, " scanning : " + index + ", ip: " + ip);
                        index++;
                        //boolean isPrinter = connect(ip);
                        if (connect(ip)) {
                            results.add(ip);
                            callback.onScanningComplete(results);
                            results.clear();
                        }
                        long time = System.currentTimeMillis() - start;
                        Log.d(TAGS, "time taken :" + time);
                        if (index == ips.size() - 1) {
                            long end = System.currentTimeMillis();
                            Log.d(TAGS, "scanning time: " + (end - start) / 1000);
                            //callback.onScanningComplete(results);
                            return null;
                        }
//                            } else {
//                                index++;
//                            }
//                        }
                    }
                    return null;
                }

            });
        }
        try {
            Thread.sleep(33000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //        for (int i = 0; i < ips.size(); i += 10) {
//            List<String> subIps=new ArrayList<>();
//            AsyncTaskHelper.executeTask(new AsyncTask<Void, Void, Boolean>() {
//
//                @Override
//                protected Boolean doInBackground(Void... voids) {
//                    return connect();
//                }
//
//                @Override
//                protected void onPostExecute(Boolean aBoolean) {
//                    super.onPostExecute(aBoolean);
//                }
//            });
//        }
    private static void executeTask(AsyncTask asyncTask) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Executor myExecutor = Executors.newFixedThreadPool(20);
            asyncTask.executeOnExecutor(myExecutor);
        } else {
            asyncTask.execute();
        }
    }

    private boolean findWifiPrinter(String ipOfThePrinter) {
        if (enableWifi()) {
            for (BaseDevice printer : list) {
                if (printer.ip.equals(ipOfThePrinter))
                    ipOfTheWifiPrinter = printer.ip;
                Log.d(TAGS, "The Printer to be connected : " + ipOfTheWifiPrinter);
                return true;
            }
        }
        return false;
    }

    private void connectToWifiPrinter(CallbackContext callbackContext) {
        final int TIME_OUT_FOR_CONNECTION = 3000;
        Log.d(TAGS, "Trying to connect to the wifiPrinter: " + ipOfTheWifiPrinter);
        try {
            SocketAddress sa = new InetSocketAddress(ipOfTheWifiPrinter, PORT);
            client = new Socket();
            client.connect(sa, TIME_OUT_FOR_CONNECTION);
            mmOutputStream = client.getOutputStream();
            mmInputStream = client.getInputStream();
            beginListenForData();
            Toast.makeText(cordova.getActivity(), "Successfully Connected to the Wifi Printer...", Toast.LENGTH_SHORT).show();
            callbackContext.success("Successfully Connected to the :" + ipOfTheWifiPrinter + " WifiPrinter");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(cordova.getActivity(), "Unable to Connect...", Toast.LENGTH_SHORT).show();
            callbackContext.error(e.getMessage());
        }

    }


    private void prints(CallbackContext callbackContext, byte[] data) throws Exception {
        try {
            if (client.isConnected()) {
                mmOutputStream.write(data);
                callbackContext.success("Successfully Printed on Wifi Printer");
            } else {
                Toast.makeText(cordova.getActivity(), "No Printer Connected, Please connect to a printer and try again...", Toast.LENGTH_LONG).show();
                callbackContext.error("No wifi printers connected");
            }
        } catch (Exception e) {
            Log.e(TAGS, e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }

    private void disconnect(CallbackContext callbackContext) {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            client.close();
            Toast.makeText(cordova.getActivity(), "Wifi Printer disconnected", Toast.LENGTH_SHORT).show();
            callbackContext.success("Bluetooth Disconnect");
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e(TAGS, errMsg);
            e.printStackTrace();
            Toast.makeText(cordova.getActivity(), "Unable to Disconnect", Toast.LENGTH_LONG).show();
            callbackContext.error(errMsg);
        }
    }

    private Printers getInfo() {
        // WifiManager wifi = (WifiManager) cordova.getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        @SuppressLint("HardwareIds") String macText = info.getMacAddress();
        String ipText = intToIp(info.getIpAddress());
        Printers printer = new Printers();
        printer.ip = ipText;
        printer.mac = macText;
        return printer;
    }

    private String intToIp(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 24) & 0xFF);
    }

    private String getStarOrEndIp(String ip, int count, boolean isStart) {
        ip = ip.substring(0, (ip.lastIndexOf(".") + 1));
        StringBuilder buffer = new StringBuilder(ip);
        ip = isStart ? buffer.append(count).toString() : buffer.append(count).toString();
        return ip;
    }

//    private synchronized boolean tryToConnect(final String ip) {
//        Log.d(TAGS, ip);
//        return (connect(ip));
//    }

    private boolean connect(String ip) {
//        Log.d(TAGS, "Start Scan:" + ip);
        Socket client = null;
        try {
            SocketAddress sa = new InetSocketAddress(ip, PORT);
            client = new Socket();
            client.connect(sa, TIME_OUT);
            //mmOutputStream = client.getOutputStream();
            //mmOutputStream.write(data);
            Log.d(TAGS, "Connected to the printer");
            client.close();
            return true;
        } catch (IOException e) {
//            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String getHardwareAddress(String ip) {

        String hw = DEFAULT_MAC;
        Log.d(TAGS, ip);
        try {
            if (ip != null) {
                String ptrn = String.format(MAC_RE, ip.replace(".", "\\."));
                Pattern pattern = Pattern.compile(ptrn);
                BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"), BUFF);
                String line;
                Matcher matcher;
                while ((line = bufferedReader.readLine()) != null) {
                    matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        hw = matcher.group(1);
                        break;
                    }
                }
                bufferedReader.close();
            } else {
                Log.e(TAGS, "ip is null");
            }
        } catch (IOException e) {
            Log.e(TAGS, "Can't open/read file ARP: " + e.getMessage());
            return hw;
        }
        return hw;
    }

    public interface OnIPScanningCallback {
        void onScanningComplete(List<String> results);
    }

}





