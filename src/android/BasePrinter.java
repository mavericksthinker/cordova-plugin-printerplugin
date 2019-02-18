package cordova.plugin.base;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import cordova.plugin.exceptions.GenericPrinterException;

public abstract class BasePrinter {


    protected UsbManager usbManager;
    public static UsbDeviceConnection usbDeviceConnection;
    protected UsbInterface usbInterface;
    protected UsbEndpoint usbEndpoint;

    /**
     *
     * @param usbManager
     */
    public BasePrinter(UsbManager usbManager) {
        this.usbManager = usbManager;
    }

    /**
     * Open the USB ports where the printer is connected
     * @throws GenericPrinterException
     */
    public abstract void open(UsbDevice printerDevice) throws GenericPrinterException;

    /**
     * Prints the message sent as a parameter
     * @param messages
     * @throws GenericPrinterException
     */
    public abstract  void print(byte[] messages) throws GenericPrinterException;

    /**
     * Close used resources and open connections
     * @throws GenericPrinterException
     */
    public abstract void close() throws GenericPrinterException;



}



