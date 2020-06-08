#                         CORDOVA PRINTING PLUGIN

# cordova-plugin-printerplugin
 ---
A cordova plugin for printing on android platform, which support text,image(BitMap),qrCode,Barcode printing via ESCPOS commands.

## Support
 ---
- Text
- POS Commands
- Image Printing(Bitmap) 
- Barcode Printing(via ESCPOS commands) 
- QR_Code printing(via ESCPOS commands

## Install
 ---
Using the Cordova CLI and NPM, run:

```
cordova plugin add https://Bishal_Mav@bitbucket.org/Bishal_Mav/cordova-plugin-printerplugin.git
```
or
```
Download the repo and install it via

cordova plugin add /path/to/plugin/directory
```

## Usage
  
 ---

#### Get list of paired bluetooth printers
```
CordovaPrint.bluetoothList(function(data){
        console.log("Success");
        console.log(data); //list of printer in data array
    },function(err){
        console.log("Error");
        console.log(err);
    })
```


#### Get list of paired wifi printers
```
CordovaPrint.wifiList(function(data){
        console.log("Success");
        console.log(data); //list of printer in data array
    },function(err){
        console.log("Error");
        console.log(err);
    })
```

#### Get list of paired USB printers
```
CordovaPrint.usbList(function(data){
        console.log("Success");
        console.log(data); //list of printer in data array
    },function(err){
        console.log("Error");
        console.log(err);
    })
```


#### Connect to the printer
```
CordovaPrint.connect(function(data){
	console.log("Success");
	console.log(data)
},function(err){
	console.log("Error");
	console.log(err)
}, "PrinterName")
```


#### Disconnect printer
```
CordovaPrint.disconnect(function(data){
	console.log("Success");
	console.log(data)
},function(err){
	console.log("Error");
	console.log(err)
})
```

**The concept of appending the data is followed to make it similar to the ESCPOS printing in Electron to be able to create a wrapper and make it effective and easy to use.** 

#### Appends text data
```
CordovaPrint.text(function(data){
    console.log("Success");
    console.log(data)
},function(err){
    console.log("Error");
    console.log(err)
}, "String to Print")
```


#### Appends image data
```
CordovaPrint.image(function(data){
    console.log("Success");
    console.log(data)
},function(err){
    console.log("Error");
    console.log(err)
}, "NameOfTheFile")
//The file path should be from the root folder(can be changed if needed in the printerplugin.java)
```


#### Appends qr_Code data
```
CordovaPrint.qr_Code(function(data){
    console.log("Success");
    console.log(data)
},function(err){
    console.log("Error");
    console.log(err)
},"https://facebook.com/",50,05,51)
//str,model,size,eclevel
```


#### Appends barcode data
```
CordovaPrint.barcode(function(data){
    console.log("Success");
    console.log(data)
},function(err){
    console.log("Error");
    console.log(err)
}, '12345678',70,3,80,0,2)
//barcodeString,barcodeType, barcodeWidth,barcodeHeight, hriFont, hriPos
```


#### Appends POS Commands
```
CordovaPrint.posCommand(function(data){
    console.log("Success");
    console.log(data)
},function(err){
    console.log("Error");
    console.log(err)
}, "0C")
//OC is a POS command for page feed
Other commands are listed below
```

#### Send data to printer
```
CordovaPrint.print(function(data){
    console.log("Success");
    console.log(data)
},function(err){
    console.log("Error");
    console.log(err)
}, "Base64 String of Image")
```


#### Initiate progress dialog
```
CordovaPrint.initDialog(function(data){
    console.log("Success");
    console.log(data)
},function(err){
    console.log("Error");
    console.log(err)
}, darkDialogTheme)
darkDialogTheme = "{theme : 'DEVICE_DARK',progressStyle : 'SPINNER',cancelable : true,title : 'Please Wait...',message : 'Scanning for Wifi printers...'}";
```

#### Dismiss the progress dialog
```
 CordovaPrint.dismissDialog(function(msg){
    console.log(msg);
 },function(err){
    console.log("Unable to dismiss");
    console.log(err);
   });
```



# Functions
----------

### .init(_arguments_)

Initialize the progress dialog and set various parameters.
These are the valid options:

`theme`: can be one of the following:
`TRADITIONAL`, `DEVICE_DARK`, `DEVICE_LIGHT` (default), `HOLO_DARK`, `HOLO_LIGHT`


`progressStyle`: can be one of the following:
`SPINNER` (default), `HORIZONTAL`

`cancelable`: `true` (default) or `false`

`title`: title of the progress dialog (defaults to empty)

`message`: contents of the progress dialog (defaults to empty)

`autoHide`:can hide automatically

    CordovaPrint.initDialog({
        theme : 'HOLO_DARK',
        progressStyle : 'SPINNER',
        cancelable : true,
        title : 'Please Wait...',
        message : 'Contacting server ...',
        autoHide:3000
    });

### .dismiss()

Dismiss the progress dialog.

    CordovaPrint.dismissDialog();


## ESCPOS_Commands
  ---
    RESET:'1B40',
    FONTA:'1B4D00',
    FONTB:'1B4D01',
    NORMAL:'1B4500',
    BOLD: '1B4501',
    NOUNDERLINE: '1B2D00',
    THINUNDERLINE: '1B2D01',
    THICKUNDERLINE:'1B2D02',
    SETSIZE: function (width, height) {
        var command = '1D21' + ("00" + ((width - 1) * 16) + (height - 1).toString(16)).slice(-2);
              return command
        },
    SMOOTH_ON: '1D62FF',
    SMOOTH_OFF: '1D6200',
    DOUBLE_ON: '1B4701',
    DOUBLE_OFF: '1B4700',
    UPSDOWN_ON: '1B7B01',
    UPSDOWN_OFF:'1B7B00',
    TURN90_ON: '1B5601',
    TURN90_OFF: '1B5600',
    INVERT_ON: '1D4201',
    INVERT_OFF:'1D4200',
    LEFT: '1B6100',
    CENTER:'1B6101',
    RIGHT:'1B6102',
    // mere cutting command will cut paper in the middle of your text due to different positions of print- and cuthead
    // looks like not all printers support different cutting modes
    CUT_FULL:'1D564100',
    CUT_PARTIAL: '1D564200',
    // feed x motionunits and cut
    FEEDCUT_FULL: function (units) {
        command ='1D5641' + ("00" + units.toString(16)).slice(-2);
              return command
       },
    FEEDCUT_PARTIAL: function (units) {
        command = '1D5642' + ("00" + units.toString(16)).slice(-2);
              return command
       },
    FEEDUNITS_ANDPRINT: function (units) {
        var command = '1B4A' + ("00" + units.toString(16)).slice(-2);
              return command;
       },
    FEEDLINES_ANDPRINT: function (units) {
        var command = '1B64' + ("00" + units.toString(16)).slice(-2);
              return command;
       },
    // not supported by all printers
    PRINT_GOBACK: function (units) {
        var command = '1B65' + ("00" + units.toString(16)).slice(-2);
              return command;
       },
    RIGHT_SPACE: function (units) {
        var command = '1B20' + ("00" + units.toString(16)).slice(-2);
              return command;
       },
    LINE_SPACE: function (units) {
        var command = '1B33' + ("00" + units.toString(16)).slice(-2);
              return command;
       },
    LINE_SPACE_DEFAULT: '1B32',

    OPEN_DRAWER_1: '1B700019',// Open Cashdrawer 1
    OPEN_DRAWER_1b: '1B703019', // Open Cashdrawer 1 EPSON version 2, try this if the first version does not work
    OPEN_DRAWER_2: '1B700119', // Open Cashdrawer 2
    OPEN_DRAWER_2b:'1B703119',
    // Named Constants for selecting the characterset
    // had to tryout a lot to get the right characterset and codepage settings for my printer and language
    USA: '1B5200',
    FRANCE: '1B5201',
    GERMANY: '1B5202',
    UK: '1B5203',
    DENMK_1: '1B5204',
    SWEDEN: '1B5205',
    ITALY: '1B5206',
    SPAIN_1: '1B5207',
    JAPAN: '1B5208',
    NORWAY: '1B5209',
    DENMK_2: '1B520A',
    SPAIN_2: '1B520B',
    LATINAMERICA:'1B520C',
    KOREAN:'1B520D',
    SLOVENIA_CROATIA:'1B520E',
    CHINA:'1B520F',
    VIETNAM:'1B5210',
    ARABIA:'1B5211',

    // Named Constants for selecting the codepages as defined by STAR using the SPECIAL COMMAND for STAR PRINTERS
    Default: '1B1D7400',
    USA_437:'1B1D7401',
    Katakana:'1B1D7402',
    StdEurope_437:'1B1D7403',
    Multilingual_858:'1B1D7404',
    Latin2_852:'1B1D7405',
    Portuguese_860:'1B1D7406',
    Icelandic_861:'1B1D7407',
    CanadianFrench_863:'1B1D7408',
    Nordic_865: '1B1D7409',
    CyrillicRussian_866:'1B1D740A',
    CyrillicBulgarian_855:'1B1D740B',
    Turkish_857:'1B1D740C',
    Hebrew_862:'1B1D740D',
    Arabic_864:'1B1D740E',
    Greek_737:'1B1D740F',
    Greek_851:'1B1D7410',
    Greek_869:'1B1D7411',
    Greek_928:'1B1D7412',
    Lithuanian_772:'1B1D7413',
    Lithuanian_774:'1B1D7414',
    Thai_874:'1B1D7415',
    WindowsLatin1_1252:'1B1D7420',
    WindowsLatin2_1250:'1B1D7421',
    WindowsCyrillic_1251:'1B1D7422',
    IBMRussian_3840:'1B1D7440',
    Gost_3841:'1B1D7441',
    Polish_3843:'1B1D7442',
    CS2_3844:'1B1D7443',
    Hungarian_3845:'1B1D7444',
    Turkish_3846:'1B1D7445',
    BrazilABNT_3847:'1B1D7446',
    BrazilABICOMP_3848:'1B1D7447',
    Arabic_1001: '1B1D7448',
    LithuanianKBL_2001:'1B1D7449',
    Estonian1_3001:'1B1D744A',
    Estonian2_3002:'1B1D744B',
    Latvian1_3011:'1B1D744C',
    Latvian2_3012:'1B1D744D',
    Bulgarian_3021:'1B1D744E',
    Maltese_3041:'1B1D744F',
    Thai_CC42:'1B1D7460',
    Thai_CC11:'1B1D7461',
    Thai_CC13:'1B1D7462',
    Thai_CC14:'1B1D7463',
    Thai_CC16:'1B1D7464',
    Thai_CC17:'1B1D7465',
    Thai_CC18:'1B1D7466'



