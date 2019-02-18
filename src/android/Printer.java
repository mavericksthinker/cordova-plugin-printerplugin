package cordova.plugin.usbImplementation;

import android.annotation.SuppressLint;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Message;
import android.util.Log;

import cordova.plugin.base.BasePrinter;
import cordova.plugin.exceptions.GenericPrinterException;
import cordova.plugin.interfaces.IGenericPrinter;

import java.nio.ByteBuffer;
import java.util.ArrayList;


public class Printer extends BasePrinter implements IGenericPrinter {
    public Printer(UsbManager usbManager) {
        super(usbManager);
    }

    private final String TAG = "USBPrinter(BasePrinter)";

    @SuppressLint("NewApi")
    @Override
    public void open(final UsbDevice printerDevice) throws GenericPrinterException {

        try {
            //We get the first Device Interface
            usbInterface = printerDevice.getInterface(0);
            //The endPoint will be the second one as that is where the communication really is
            usbEndpoint = usbInterface.getEndpoint(1);
            //Open the USB port of the printer
            usbDeviceConnection = usbManager.openDevice(printerDevice);
            //We enforce the interface for claiming
            usbDeviceConnection.claimInterface(usbInterface, true);
        }catch(Exception e) {
            Log.e(TAG, e.getMessage());
            throw  new GenericPrinterException(e.getMessage());
        }

    }

    @SuppressLint("NewApi")
    @Override
    public void print(byte[] bytes) throws GenericPrinterException {
        try {
        ByteBuffer outputBuffer = ByteBuffer.allocate(bytes.length);
        UsbRequest usbRequest = new UsbRequest();
        usbRequest.initialize(usbDeviceConnection, usbEndpoint);
        usbRequest.queue(outputBuffer, bytes.length);
        if (usbDeviceConnection.requestWait() == usbRequest) {
            Log.i(TAG, outputBuffer.getChar(0) + "");
            Message msg = new Message();
            msg.obj = outputBuffer.array();
            outputBuffer.clear();
        } else {
            Log.i(TAG, "We have no messages received.");
        }
        int transfered = usbDeviceConnection.bulkTransfer(usbEndpoint,
                bytes,
                bytes.length, 5000);
        Log.i(TAG, "message sent with transfer : " +
                transfered);
    }catch(Exception e) {
        Log.e(TAG, e.getMessage());
        throw  new GenericPrinterException(e.getMessage());
    }


    }

    @SuppressLint("NewApi")
    @Override
    public void close() throws GenericPrinterException {
        try {
            usbDeviceConnection.releaseInterface(usbInterface);
            Log.i("Info", "Interface released");
            usbDeviceConnection.close();
            Log.i("Info", "Usb connection closed");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            throw  new GenericPrinterException(e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    @Override
    public ArrayList findPrinters() throws GenericPrinterException {
        Log.i(TAG, String.format("Found: %s Devices ",
                usbManager.getDeviceList().size()));
        try{
            if(usbManager.getDeviceList().size()<=0)
                Log.e(TAG,"No connected printers.");

            return new ArrayList<UsbDevice>(usbManager.getDeviceList().values());
        }catch(Exception e) {
            Log.e(TAG, e.getMessage());
            throw  new GenericPrinterException(e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    @Override
    public UsbDevice findPrinterByName(final String name) throws GenericPrinterException {
        UsbDevice printerDevice=null;
        try {
            Log.i(TAG, String.format("Found: %s Devices ",
                    usbManager.getDeviceList().size()));
            for (UsbDevice device : usbManager.getDeviceList().values()) {
                Log.i(TAG, String.format("Device Name: %s", device.getProductName()));
                if (name.equals(device.getProductName())) {
                    printerDevice = device;
                    break;
                }
            }
            return printerDevice;
        }catch(Exception e) {
            Log.e(TAG, e.getMessage());
            throw  new GenericPrinterException(e.getMessage());
        }
    }
}




