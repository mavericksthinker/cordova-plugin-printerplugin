package cordova.plugin.interfaces;

import android.hardware.usb.UsbDevice;
import cordova.plugin.exceptions.GenericPrinterException;

import java.util.ArrayList;

public interface IGenericPrinter {

    /**
     * Scan all printers connected to a USB port
     * @return
     * @throws GenericPrinterException
     */
    ArrayList findPrinters() throws GenericPrinterException;

    /**
     * Returns the current printer to use
     * @param name
     * @return
     * @throws GenericPrinterException
     */
    UsbDevice findPrinterByName(String name) throws GenericPrinterException;


}



