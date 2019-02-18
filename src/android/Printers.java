package cordova.plugin.wifiPrinters;

import cordova.plugin.baseDevice.BaseDevice;

public class Printers extends BaseDevice {

    public String name;

    public Printers() {
    }

    public Printers(String ip, String mac) {
        super.ip = ip;
        super.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


