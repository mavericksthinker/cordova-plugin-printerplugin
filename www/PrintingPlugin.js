var exec = require('cordova/exec');

var CordovaPrint = {
    list: function(fnSuccess, fnError){
       exec(fnSuccess, fnError, "PrintingPlugin", "list", []);
    },
    connect: function(fnSuccess, fnError, name){
       exec(fnSuccess, fnError, "PrintingPlugin", "connect", [name]);
    },
    connectManually: function(fnSuccess, fnError, name){
       exec(fnSuccess, fnError, "PrintingPlugin", "connectManually", [name]);
    },
    disconnect: function(fnSuccess, fnError){
       exec(fnSuccess, fnError, "PrintingPlugin", "disconnect", []);
    },
    print: function(fnSuccess, fnError, str){
       exec(fnSuccess, fnError, "PrintingPlugin", "print", [str]);
    },
    text: function(fnSuccess, fnError, str){
       exec(fnSuccess, fnError, "PrintingPlugin", "printText", [str]);
    },
    image: function(fnSuccess, fnError, str){
       exec(fnSuccess, fnError, "PrintingPlugin", "printImage", [str]);
    },
    posCommand: function(fnSuccess, fnError, str){
       exec(fnSuccess, fnError, "PrintingPlugin", "printPOSCommand", [str]);
    },
    barcode: function(fnSuccess, fnError,barStr,barType, barWidth,barHeight, hriFont, hriPos){
       var barCode = barcode(barStr,barType,barWidth,barHeight,hriFont,hriPos);
       exec(fnSuccess, fnError, "PrintingPlugin", "printBarcode", [barStr,barType,barWidth,barHeight,hriFont,hriPos]);
    },
    qr_Code: function(fnSuccess, fnError,str,model,size,eclevel){
      const justify_center = '\x1B\x61\x01';
      const justify_left   = '\x1B\x61\x00';
      var qr_model       =  toHex(model);          // 31 or 32
      var qr_size        =  toHex(size);           // size
      var qr_eclevel     =  toHex(eclevel);        // error correction level (30, 31, 32, 33 - higher)
      const qr_data        = str;
      const qr_pL          = String.fromCharCode((qr_data.length + 3) % 256);
      const qr_pH          = String.fromCharCode((qr_data.length + 3) / 256);

       exec(fnSuccess, fnError, "PrintingPlugin", "printQrCode", [justify_center +
                                                                                       '\x1D\x28\x6B\x04\x00\x31\x41' + qr_model + '\x00' +        // Select the model
                                                                                       '\x1D\x28\x6B\x03\x00\x31\x43' + qr_size +                  // Size of the model
                                                                                       '\x1D\x28\x6B\x03\x00\x31\x45' + qr_eclevel +               // Set n for error correction
                                                                                       '\x1D\x28\x6B' + qr_pL + qr_pH + '\x31\x50\x30' + qr_data + // Store data
                                                                                       '\x1D\x28\x6B\x03\x00\x31\x51\x30' +                        // Print
                                                                                       '\n\n\n' +
                                                                                    justify_left]);
        },

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

 };
 //exports the module to be utilized in cordova    
 module.exports = CordovaPrint;


//checks the validity fo the barcode
 function barcode(ESCPOS_BARCONTENT, ESCPOS_BARTYPE, ESCPOS_BARWIDTH, ESCPOS_BARHEIGHT, ESCPOS_HRIFONT, ESCPOS_HRIPOSITION) {
                          if (!validateBarcode(ESCPOS_BARTYPE, ESCPOS_BARCONTENT))
                                  return "INVALID BARCODE FORMAT\n" + ESCPOS_BARCONTENT + "\n";
       }
//=====================================================================================================================================
// Return barcode codes by name
var ESCPOS_BARCODE_CODE = {
        UPC_A: 65,
        UPC_B: 66,
        EAN_13: 67,
        EAN_8: 68,
        Code39: 69,
        Interleaved_2of5: 70,
        Codabar: 71,
        Code_93: 72,
        Code_128: 73,
        UCC_Ean_128: 74,
}

function toHex(dec) {

  switch(dec){

  case 49 : dec = '\x31';
  break;
  case 50 : dec = '\x32';
  break;
  case 51 : dec = '\x33';
  break;
  case 1: dec = '\x01';
  break;
   case 2: dec = '\x02';
    break;
     case 3: dec = '\x03';
      break;
       case 4: dec = '\x04';
        break;
         case 5: dec = '\x05';
          break;
           case 6: dec = '\x06';
            break;
             case 7: dec = '\x07';
              break;
               case 8||08: dec = '\x08';
                break;
                 case 9: dec = '\x09';
                  break;
                   case 10: dec = '\x0A';
                     break;
                     case 11: dec = '\x0B';
                       break;
                       case 12: dec = '\x0C';
                         break;
                         case 13: dec = '\x0D';
                           break;
                           case 14: dec = '\x0E';
                             break;
                             case 48: dec = '\x30';
                               break;
                               default: dec = null;

  }

   return dec;

}

// Validates a Barcode according to the rules found on https://www.epson-biz.com/modules/ref_escpos/index.php?content_id=128
function validateBarcode(ESCPOS_BARTYPE, ESCPOS_BARCONTENT) {
        switch (ESCPOS_BARTYPE) {
                case "UPC_A":
                case 65:
                        if (ESCPOS_BARCONTENT.length != 11 && ESCPOS_BARCONTENT.length != 12)
                                return false

                        // Just allow numeric values
                        if (!(/^[0-9]+$/.test(ESCPOS_BARCONTENT)))
                                return false
                        break
                case "UPC_B":
                case 66:
                        if (!(ESCPOS_BARCONTENT.length >= 6 && ESCPOS_BARCONTENT.length <= 8) && ESCPOS_BARCONTENT.length != 11 && ESCPOS_BARCONTENT.length != 12)
                                return false

                        // Just allow numeric values
                        if (!(/^[0-9]+$/.test(ESCPOS_BARCONTENT)))
                                return false
                        break
                case "EAN_13":
                case 67:
                        if (ESCPOS_BARCONTENT.length != 12 && ESCPOS_BARCONTENT.length != 13)
                                return false

                        // Just allow numeric values
                        if (!(/^[0-9]+$/.test(ESCPOS_BARCONTENT)))
                                return false
                        break
                case "EAN_8":
                case 68:
                        if (ESCPOS_BARCONTENT.length != 7 && ESCPOS_BARCONTENT.length != 8)
                                return false

                        // Just allow numeric values
                        if (!(/^[0-9]+$/.test(ESCPOS_BARCONTENT)))
                                return false
                        break
                case "Code39":
                case 69:
                        if (!(ESCPOS_BARCONTENT.length >= 1 && ESCPOS_BARCONTENT.length <= 255))
                                return false

                        // 0 – 9, A – Z, SP, $, %, *, +, -, ., /
                        if (!(/^[0-9A-Z $%*+-./]+$/.test(ESCPOS_BARCONTENT)))
                                return false

                        break
                case "Interleaved_2of5":
                case 70:
                        if (ESCPOS_BARCONTENT.length % 2 !== 0 || !(ESCPOS_BARCONTENT.length >= 2 && ESCPOS_BARCONTENT.length <= 254))
                                return false
                        // Just allow numeric values
                        if (!(/^[0-9]+$/.test(ESCPOS_BARCONTENT)))
                                return false
                        break
                case "Codabar":
                case 71:
                        if (!(ESCPOS_BARCONTENT.length >= 2 && ESCPOS_BARCONTENT.length <= 255))
                                return false

                        // 0 – 9, A – D, a – d, $, +, −, ., /, :
                        if (!(/^[0-9A-Da-d $+-./:]+$/.test(ESCPOS_BARCONTENT)))
                                return false
                        break
                case "Code_93":
                case 72:
                        if (!(ESCPOS_BARCONTENT.length >= 2 && ESCPOS_BARCONTENT.length <= 255))
                                return false

                        // all ascii
                        if (!(/^[\x00-\x7F]+$/.test(ESCPOS_BARCONTENT)))
                                return false

                case "Code_128":
                case 73:
                        if (!(ESCPOS_BARCONTENT.length >= 2 && ESCPOS_BARCONTENT.length <= 255))
                                return false

                        let type = ESCPOS_BARCONTENT.substr(0, 2)

                        // all ascii but starting with either {A|{B|{C
                        if (!(/^({A|{B|{C)[\x00-\x7F]+$/.test(ESCPOS_BARCONTENT)))
                                return false
                        break
                case "UCC_Ean_128":
                case 74:
                        if (!(ESCPOS_BARCONTENT.length >= 2 && ESCPOS_BARCONTENT.length <= 255))
                                return false

                        // all ascii
                        if (!(/^[\x00-\x7F]+$/.test(ESCPOS_BARCONTENT)))
                                return false
                        break
                default:
                        return true
                        break;
        }

        return true
}

