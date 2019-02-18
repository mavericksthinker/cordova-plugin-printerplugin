// This script sets OSName variable as follows:
// "Windows"    for all versions of Windows
// "MacOS"      for all versions of Macintosh OS
// "Linux"      for all versions of Linux
// "UNIX"       for all other UNIX flavors
// "Unknown OS" indicates failure to detect the OS

var OSName = "Unknown OS";
  if (navigator.userAgent.indexOf("Win") != -1) OSName = "Windows";
  if (navigator.appVersion.indexOf("X11")!=-1) OSName="UNIX";
  if (navigator.userAgent.indexOf("Mac") != -1) OSName = "Macintosh";
  if (navigator.userAgent.indexOf("Linux") != -1) OSName = "Linux";
  if (navigator.userAgent.indexOf("Android") != -1) OSName = "Android";
  if (navigator.userAgent.indexOf("like Mac") != -1) OSName = "iOS";
  console.log("Operating System : "+OSName);
 if(OSName === 'Android'){
  bluetoothList = function(success,error){
     CordovaPrint.bluetoothList(success,error);
  },
  wifiList = function(success,error){
     CordovaPrint.wifiList(success,error);
  },
  usbList = function(success,error){
     CordovaPrint.usbList(success,error);
  },
  connect = function(success,error,printerName){
     CordovaPrint.connect(success,error,printerName);
  },
  disconnect = function(success,error){
     CordovaPrint.disconnect(success,error);
  },
  text = function(success,error,str){
     CordovaPrint.text(success,error,str);
  },
  image = function(success,error,imgPath,imgMode,dither,imgThreshold){
     CordovaPrint.image(success,error,imgPath);
  },
  posCommand = function(success,error,cmd){
     CordovaPrint.posCommand(success,error,cmd);
  },
  qr_Code = function(success,error,str,model,size,eclevel){
     CordovaPrint.qr_Code(success,error,str,model,size,eclevel);
  },
  barcode =  function(success,error,barStr,barType, barWidth,barHeight, hriFont, hriPos){
     CordovaPrint.barcode(success,error,barStr,barType, barWidth,barHeight, hriFont, hriPos);
  },
  print = function(success,error,printerName){
     CordovaPrint.print(success,error);
  },
  initDialog = function(success,error,style){
     CordovaPrint.initDialog(success,error,style);
  },
  dismissDialog  = function(success,error){
     CordovaPrint.dismissDialog(success,error);
  },
   RESET = CordovaPrint.RESET,
   FONTA = CordovaPrint.FONTA,
   FONTB = CordovaPrint.FONTB,
   NORMAL = CordovaPrint.NORMAL,
   BOLD = CordovaPrint.BOLD,
   NOUNDERLINE = CordovaPrint.NOUNDERLINE,
   THINUNDERLINE = CordovaPrint.THINUNDERLINE,
   THICKUNDERLINE = CordovaPrint.THICKUNDERLINE,
   SETSIZE = CordovaPrint.SETSIZE,
   SMOOTH_ON = CordovaPrint.SMOOTH_ON,
   SMOOTH_OFF = CordovaPrint.SMOOTH_OFF,
   DOUBLE_ON = CordovaPrint.DOUBLE_ON,
   DOUBLE_OFF = CordovaPrint.DOUBLE_OFF,
   UPSDOWN_ON = CordovaPrint.UPSDOWN_ON,
   UPSDOWN_OFF = CordovaPrint.UPSDOWN_OFF,
   TURN90_ON = CordovaPrint.TURN90_ON,
   TURN90_OFF = CordovaPrint.TURN90_OFF,
   INVERT_ON = CordovaPrint.INVERT_ON,
   INVERT_OFF = CordovaPrint.INVERT_OFF,
   LEFT = CordovaPrint.LEFT,
   CENTER = CordovaPrint.CENTER,
   RIGHT = CordovaPrint.RIGHT,
   // mere cutting command will cut paper in the middle of your text due to different positions of print- and cuthead
   // looks like not all printers support different cutting modes         
   CUT_FULL = CordovaPrint.CUT_FULL,
   CUT_PARTIAL = CordovaPrint.CUT_PARTIAL,
   // feed x motionunits and cut
   FEEDCUT_FULL = CordovaPrint.FEEDCUT_FULL,
   FEEDCUT_PARTIAL = CordovaPrint.FEEDCUT_PARTIAL,
   FEEDUNITS_ANDPRINT = CordovaPrint.FEEDUNITS_ANDPRINT,
   FEEDLINES_ANDPRINT = CordovaPrint.FEEDLINES_ANDPRINT,
   // not supported by all printers
   PRINT_GOBACK = CordovaPrint.PRINT_GOBACK,
   RIGHT_SPACE = CordovaPrint.RIGHT_SPACE,
   LINE_SPACE = CordovaPrint.LINE_SPACE,
   LINE_SPACE_DEFAULT = CordovaPrint.LINE_SPACE_DEFAULT,

   OPEN_DRAWER_1 = CordovaPrint.OPEN_DRAWER_1, // Open Cashdrawer 1
   OPEN_DRAWER_1b = CordovaPrint.OPEN_DRAWER_1b, // Open Cashdrawer 1 EPSON version 2, try this if the first version does not work
   OPEN_DRAWER_2 = CordovaPrint.OPEN_DRAWER_2, // Open Cashdrawer 2
   OPEN_DRAWER_2b = CordovaPrint.OPEN_DRAWER_2b, // Open Cashdrawer 2 EPSON version 2, try this if the first version does not work
   
         STAR_Default = CordovaPrint.Default,
         STAR_USA_437 = CordovaPrint.USA_437,
         STAR_Katakana = CordovaPrint.Katakana,
         STAR_StdEurope_437 = CordovaPrint.StdEurope_437,
         STAR_Multilingual_858 = CordovaPrint.Multilingual_858,
         STAR_Latin2_852 = CordovaPrint.Latin2_852,
         STAR_Portuguese_860 = CordovaPrint.Portuguese_860,
         STAR_Icelandic_861 = CordovaPrint.Icelandic_861,
         STAR_CanadianFrench_863 = CordovaPrint.CanadianFrench_863,
         STAR_Nordic_865 = CordovaPrint.Nordic_865,
         STAR_CyrillicRussian_866 = CordovaPrint.CyrillicRussian_866,
         STAR_CyrillicBulgarian_855 = CordovaPrint.CyrillicBulgarian_855,
         STAR_Turkish_857 = CordovaPrint.Turkish_857,
         STAR_Hebrew_862 = CordovaPrint.Hebrew_862,
         STAR_Arabic_864 = CordovaPrint.Arabic_864,
         STAR_Greek_737 = CordovaPrint.Greek_737,
         STAR_Greek_851 = CordovaPrint.Greek_851,
         STAR_Greek_869 = CordovaPrint.Greek_869,
         STAR_Greek_928 = CordovaPrint.Greek_928,
         STAR_Lithuanian_772 = CordovaPrint.Lithuanian_772,
         STAR_Lithuanian_774 = CordovaPrint.Lithuanian_774,
         STAR_Thai_874 = CordovaPrint.Thai_874,
         STAR_WindowsLatin1_1252 = CordovaPrint.WindowsLatin1_1252,
         STAR_WindowsLatin2_1250 = CordovaPrint.WindowsLatin2_1250,
         STAR_WindowsCyrillic_1251 = CordovaPrint.WindowsCyrillic_1251,
         STAR_IBMRussian_3840 = CordovaPrint.IBMRussian_3840,
         STAR_Gost_3841 = CordovaPrint.Gost_3841,
         STAR_Polish_3843 = CordovaPrint.Polish_3843,
         STAR_CS2_3844 = CordovaPrint.CS2_3844,
         STAR_Hungarian_3845 = CordovaPrint.Hungarian_3845,
         STAR_Turkish_3846 = CordovaPrint.Turkish_3846,
         STAR_BrazilABNT_3847 = CordovaPrint.BrazilABNT_3847,
         STAR_BrazilABICOMP_3848 = CordovaPrint.BrazilABICOMP_3848,
         STAR_Arabic_1001 = CordovaPrint.Arabic_1001,
         STAR_LithuanianKBL_2001 = CordovaPrint.LithuanianKBL_2001,
         STAR_Estonian1_3001 = CordovaPrint.Estonian1_3001,
         STAR_Estonian2_3002 = CordovaPrint.Estonian2_3002,
         STAR_Latvian1_3011 = CordovaPrint.Latvian1_3011,
         STAR_Latvian2_3012 = CordovaPrint.Latvian2_3012,
         STAR_Bulgarian_3021 = CordovaPrint.Bulgarian_3021,
         STAR_Maltese_3041 = CordovaPrint.Maltese_3041,
         STAR_Thai_CC42 = CordovaPrint.Thai_CC42,
         STAR_Thai_CC11 = CordovaPrint.Thai_CC11,
         STAR_Thai_CC13 = CordovaPrint.Thai_CC13,
         STAR_Thai_CC14 = CordovaPrint.Thai_CC14,
         STAR_Thai_CC16 = CordovaPrint.Thai_CC16,
         STAR_Thai_CC17 = CordovaPrint.Thai_CC17,
         STAR_Thai_CC18 = CordovaPrint.Thai_CC18;
 }else if(OSName === 'Windows'||OSName === 'Linux'||OSName ==='Macintosh'){

  let Printer = require('./escpos_printing.js')

  bluetoothList = function(success,error){
    Printer.ESCPOS_INIT();
    return Printer.ESCPOS_PRINTERLIST;
  },
    wifiList = function(success,error){
      Printer.ESCPOS_INIT();
      return Printer.ESCPOS_PRINTERLIST;
    },
    usbList = function(success,error){
      Printer.ESCPOS_INIT();
      return Printer.ESCPOS_PRINTERLIST;
    },
    connect = function(success,error,printerName){
      Printer.ESCPOS_PRINT(printerName);
    },
    text = function(success,error,str){
      Printer.append(str);
    },
    image = async function(success,error,imgPath,imgMode,dither,imgThreshold){
Printer.append(await Printer.ESCPOS_IMAGEFILE(this.window,imgPath,imgMode,dither,imgThreshold));
    },
    posCommand = function(success,error,cmd){
       Printer.append(cmd);
    },
    qr_Code = function(success,error,str,model,size,eclevel){
       Printer.append(Printer.ESCPOS_QRCODE(str,model,size,eclevel));
    },
    barcode =  function(success,error,barStr,barType, barWidth,barHeight, hriFont, hriPos){
       Printer.append(Printer.ESCPOS_BARCODE(barStr,barType, barWidth,barHeight, hriFont, hriPos));
    },
    print = function(success,error,printerName){
      Printer.ESCPOS_PRINT(printerName);
    },
//=====================================================================================================================================
// Named Constants for selecting the characterset
// had to tryout a lot to get the right characterset and codepage settings for my printer and language
//-------------------------------------------------------------------------------------------------------------------------------------

    USA = Printer.ESCPOS_CHARSET.USA,
    FRANCE = Printer.ESCPOS_CHARSET.FRANCE,
    GERMANY = Printer.ESCPOS_CHARSET.GERMANY,
    UK = Printer.ESCPOS_CHARSET.UK,
    DENMK_1 = Printer.ESCPOS_CHARSET.DENMK_1,
    SWEDEN = Printer.ESCPOS_CHARSET.SWEDEN,
    ITALY = Printer.ESCPOS_CHARSET.ITALY,
    SPAIN_1 = Printer.ESCPOS_CHARSET.SPAIN_1,
    JAPAN = Printer.ESCPOS_CHARSET.JAPAN,
    NORWAY = Printer.ESCPOS_CHARSET.NORWAY,
    DENMK_2 = Printer.ESCPOS_CHARSET.DENMK_2,
    SPAIN_2 = Printer.ESCPOS_CHARSET.SPAIN_2,
    LATINAMERICA = Printer.ESCPOS_CHARSET.LATINAMERICA,
    KOREAN = Printer.ESCPOS_CHARSET.KOREAN,
    SLOVENIA_CROATIA = Printer.ESCPOS_CHARSET.SLOVENIA_CROATIA,
    CHINA = Printer.ESCPOS_CHARSET.CHINA,
    VIETNAM = Printer.ESCPOS_CHARSET.VIETNAM,
    ARABIA = Printer.ESCPOS_CHARSET.ARABIA,

//=====================================================================================================================================
// Named Constants for selecting the codepages as defined by EPSON using the standard ESCPOS Command
// had to tryout a lot to get the right characterset and codepage settings for my printer and language
//-------------------------------------------------------------------------------------------------------------------------------------


    USA_437 = Printer.ESCPOS_CP_EPSON.USA_437,
    KATAKANA = Printer.ESCPOS_CP_EPSON.KATAKANA,
    MULTILINGUAL_850 = Printer.ESCPOS_CP_EPSON.MULTILINGUAL_850,
    PORTUGUESE_860 = Printer.ESCPOS_CP_EPSON.PORTUGUESE_860,
    CANADIANFRENCH_863 = Printer.ESCPOS_CP_EPSON.CANADIANFRENCH_863,
    NORDIC_865 = Printer.ESCPOS_CP_EPSON.NORDIC_865,
    SIPLIFIEDKANJI_HIRAKANA = Printer.ESCPOS_CP_EPSON.SIPLIFIEDKANJI_HIRAKANA,
    SIPLIFIEDKANJI_II = Printer.ESCPOS_CP_EPSON.SIPLIFIEDKANJI_II,
    SIPLIFIEDKANJI_III = Printer.ESCPOS_CP_EPSON.SIPLIFIEDKANJI_III,
    LATINI_1252 = Printer.ESCPOS_CP_EPSON.LATINI_1252,
    CYRILLIC2_866 = Printer.ESCPOS_CP_EPSON.CYRILLIC2_866,
    LATINII_852 = Printer.ESCPOS_CP_EPSON.LATINII_852,
    EURO_858 = Printer.ESCPOS_CP_EPSON.EURO_858,
    HEBREW_862 = Printer.ESCPOS_CP_EPSON.HEBREW_862,
    ARABIC_864 = Printer.ESCPOS_CP_EPSON.ARABIC_864,
    THAI_42 = Printer.ESCPOS_CP_EPSON.THAI_42,
    GREEK_1253 = Printer.ESCPOS_CP_EPSON.GREEK_1253,
    TURKISH_1254 = Printer.ESCPOS_CP_EPSON.TURKISH_1254,
    BALTIC_1257 = Printer.ESCPOS_CP_EPSON.BALTIC_1257,
    FARSI = Printer.ESCPOS_CP_EPSON.FARSI,
    CYRILLIC_1251 = Printer.ESCPOS_CP_EPSON.CYRILLIC_1251,
    GREEK_737 = Printer.ESCPOS_CP_EPSON.GREEK_737,
    BALTIC_775 = Printer.ESCPOS_CP_EPSON.BALTIC_775,
    THAI_14 = Printer.ESCPOS_CP_EPSON.THAI_14,
    HEBREWNEW_1255 = Printer.ESCPOS_CP_EPSON.HEBREWNEW_1255,
    THAI11 = Printer.ESCPOS_CP_EPSON.THAI11,
    THAI8 = Printer.ESCPOS_CP_EPSON.THAI8,
    CYRILLIC_855 = Printer.ESCPOS_CP_EPSON.CYRILLIC_855,
    TURKISH_857 = Printer.ESCPOS_CP_EPSON.TURKISH_857,
    GREEK_928 = Printer.ESCPOS_CP_EPSON.GREEK_928,
    THAI_16 = Printer.ESCPOS_CP_EPSON.THAI_16,
    ARABIC_1256 = Printer.ESCPOS_CP_EPSON.ARABIC_1256,
    VIETNAM_1258 = Printer.ESCPOS_CP_EPSON.VIETNAM_1258,
    KHMER = Printer.ESCPOS_CP_EPSON.KHMER,
    CZECH_1250 = Printer.ESCPOS_CP_EPSON.CZECH_1250,
//=====================================================================================================================================
// Named Constants for selecting the codepages as defined by STAR using the standard ESCPOS Command
// had to tryout a lot to get the right characterset and codepage settings for my printer and language
//-------------------------------------------------------------------------------------------------------------------------------------

    
        CP_STAR_USA_437 = Printer.ESCPOS_CP_STAR.USA_437,
        CP_STAR_KATAKANA = Printer.ESCPOS_CP_STAR.KATAKANA,
        CP_STAR_MULTILINGUAL_850 = Printer.ESCPOS_CP_STAR.MULTILINGUAL_850,
        CP_STAR_PORTUGUESE_860 = Printer.ESCPOS_CP_STAR.PORTUGUESE_860,
        CP_STAR_CANADIANFRENCH_863 = Printer.ESCPOS_CP_STAR.CANADIANFRENCH_863,
        CP_STAR_CP_STAR_NORDIC_865 = Printer.ESCPOS_CP_STAR.NORDIC_865,
        CP_STAR_LATINI_1252 = Printer.ESCPOS_CP_STAR.LATINI_1252,
        CP_STAR_CYRILLIC2_866 = Printer.ESCPOS_CP_STAR.CYRILLIC2_866,
        CP_STAR_LATINII_852 = Printer.ESCPOS_CP_STAR.LATINII_852,
        CP_STAR_EURO_858 = Printer.ESCPOS_CP_STAR.EURO_858,
        CP_STAR_THAICC_42 = Printer.ESCPOS_CP_STAR.THAICC_42,
        CP_STAR_THAICC_11 = Printer.ESCPOS_CP_STAR.THAICC_11,
        CP_STAR_THAICC_13 = Printer.ESCPOS_CP_STAR.THAICC_13,
        CP_STAR_THAICC_14 = Printer.ESCPOS_CP_STAR.THAICC_14,
        CP_STAR_THAICC_16 = Printer.ESCPOS_CP_STAR.THAICC_16,
        CP_STAR_THAICC_17 = Printer.ESCPOS_CP_STAR.THAICC_17,
        CP_STAR_THAICC_18 = Printer.ESCPOS_CP_STAR.THAICC_18,
//=====================================================================================================================================
// Named Constants for selecting the codepages as defined by STAR using the SPECIAL COMMAND for STAR PRINTERS (only?) 
// had to tryout a lot to get the right characterset and codepage settings for my printer and language
//-------------------------------------------------------------------------------------------------------------------------------------
     
         STAR_Default = Printer.STAR_CP.Default,
         STAR_USA_437 = Printer.STAR_CP.USA_437,
         STAR_Katakana = Printer.STAR_CP.Katakana,
         STAR_StdEurope_437 = Printer.STAR_CP.StdEurope_437,
         STAR_Multilingual_858 = Printer.STAR_CP.Multilingual_858,
         STAR_Latin2_852 = Printer.STAR_CP.Latin2_852,
         STAR_Portuguese_860 = Printer.STAR_CP.Portuguese_860,
         STAR_Icelandic_861 = Printer.STAR_CP.Icelandic_861,
         STAR_CanadianFrench_863 = Printer.STAR_CP.CanadianFrench_863,
         STAR_STAR_Nordic_865 = Printer.STAR_CP.Nordic_865,
         STAR_CyrillicRussian_866 = Printer.STAR_CP.CyrillicRussian_866,
         STAR_CyrillicBulgarian_855 = Printer.STAR_CP.CyrillicBulgarian_855,
         STAR_Turkish_857 = Printer.STAR_CP.Turkish_857,
         STAR_Hebrew_862 = Printer.STAR_CP.Hebrew_862,
         STAR_Arabic_864 = Printer.STAR_CP.Arabic_864,
         STAR_Greek_737 = Printer.STAR_CP.Greek_737,
         STAR_Greek_851 = Printer.STAR_CP.Greek_851,
         STAR_Greek_869 = Printer.STAR_CP.Greek_869,
         STAR_Greek_928 = Printer.STAR_CP.Greek_928,
         STAR_Lithuanian_772 = Printer.STAR_CP.Lithuanian_772,
         STAR_Lithuanian_774 = Printer.STAR_CP.Lithuanian_774,
         STAR_Thai_874 = Printer.STAR_CP.Thai_874,
         STAR_WindowsLatin1_1252 = Printer.STAR_CP.WindowsLatin1_1252,
         STAR_WindowsLatin2_1250 = Printer.STAR_CP.WindowsLatin2_1250,
         STAR_WindowsCyrillic_1251 = Printer.STAR_CP.WindowsCyrillic_1251,
         STAR_IBMRussian_3840 = Printer.STAR_CP.IBMRussian_3840,
         STAR_Gost_3841 = Printer.STAR_CP.Gost_3841,
         STAR_Polish_3843 = Printer.STAR_CP.Polish_3843,
         STAR_CS2_3844 = Printer.STAR_CP.CS2_3844,
         STAR_Hungarian_3845 = Printer.STAR_CP.Hungarian_3845,
         STAR_Turkish_3846 = Printer.STAR_CP.Turkish_3846,
         STAR_BrazilABNT_3847 = Printer.STAR_CP.BrazilABNT_3847,
         STAR_BrazilABICOMP_3848 = Printer.STAR_CP.BrazilABICOMP_3848,
         STAR_Arabic_1001 = Printer.STAR_CP.Arabic_1001,
         STAR_LithuanianKBL_2001 = Printer.STAR_CP.LithuanianKBL_2001,
         STAR_Estonian1_3001 = Printer.STAR_CP.Estonian1_3001,
         STAR_Estonian2_3002 = Printer.STAR_CP.Estonian2_3002,
         STAR_Latvian1_3011 = Printer.STAR_CP.Latvian1_3011,
         STAR_Latvian2_3012 = Printer.STAR_CP.Latvian2_3012,
         STAR_Bulgarian_3021 = Printer.STAR_CP.Bulgarian_3021,
         Maltese_3041 = Printer.STAR_CP.Maltese_3041,
         STAR_Thai_CC42 = Printer.STAR_CP.Thai_CC42,
         STAR_Thai_CC11 = Printer.STAR_CP.Thai_CC11,
         STAR_Thai_CC13 = Printer.STAR_CP.Thai_CC13,
         STAR_Thai_CC14 = Printer.STAR_CP.Thai_CC14,
         STAR_Thai_CC16 = Printer.STAR_CP.Thai_CC16,
         STAR_Thai_CC17 = Printer.STAR_CP.Thai_CC17,
         STAR_Thai_CC18 = Printer.STAR_CP.Thai_CC18,
         RESET = Printer.ESCPOS_CMD.RESET,
         FONTA = Printer.ESCPOS_CMD.FONTA,
         FONTB = Printer.ESCPOS_CMD.FONTB,
         NORMAL = Printer.ESCPOS_CMD.NORMAL,
         BOLD = Printer.ESCPOS_CMD.BOLD,
         NOUNDERLINE = Printer.ESCPOS_CMD.NOUNDERLINE,
         THINUNDERLINE = Printer.ESCPOS_CMD.THINUNDERLINE,
         THICKUNDERLINE = Printer.ESCPOS_CMD.THICKUNDERLINE,
         SETSIZE = Printer.ESCPOS_CMD.SETSIZE,
         SMOOTH_ON = Printer.ESCPOS_CMD.SMOOTH_ON,
         SMOOTH_OFF = Printer.ESCPOS_CMD.SMOOTH_OFF,
         DOUBLE_ON = Printer.ESCPOS_CMD.DOUBLE_ON,
         DOUBLE_OFF = Printer.ESCPOS_CMD.DOUBLE_OFF,
         UPSDOWN_ON = Printer.ESCPOS_CMD.UPSDOWN_ON,
         UPSDOWN_OFF = Printer.ESCPOS_CMD.UPSDOWN_OFF,
         TURN90_ON = Printer.ESCPOS_CMD.TURN90_ON,
         TURN90_OFF = Printer.ESCPOS_CMD.TURN90_OFF,
         INVERT_ON = Printer.ESCPOS_CMD.INVERT_ON,
         INVERT_OFF = Printer.ESCPOS_CMD.INVERT_OFF,
         LEFT = Printer.ESCPOS_CMD.LEFT,
         CENTER = Printer.ESCPOS_CMD.CENTER,
         RIGHT = Printer.ESCPOS_CMD.RIGHT,
         // mere cutting command will cut paper in the middle of your text due to different positions of print- and cuthead
         // looks like not all printers support different cutting modes         
         CUT_FULL = Printer.ESCPOS_CMD.CUT_FULL,
         CUT_PARTIAL = Printer.ESCPOS_CMD.CUT_PARTIAL,
         // feed x motionunits and cut
         FEEDCUT_FULL = Printer.ESCPOS_CMD.FEEDCUT_FULL,
         FEEDCUT_PARTIAL = Printer.ESCPOS_CMD.FEEDCUT_PARTIAL,
         FEEDUNITS_ANDPRINT = Printer.ESCPOS_CMD.FEEDUNITS_ANDPRINT,
         FEEDLINES_ANDPRINT = Printer.ESCPOS_CMD.FEEDLINES_ANDPRINT,
         // not supported by all printers
         PRINT_GOBACK = Printer.ESCPOS_CMD.PRINT_GOBACK,
         RIGHT_SPACE = Printer.ESCPOS_CMD.RIGHT_SPACE,
         LINE_SPACE =Printer.ESCPOS_CMD.LINE_SPACE,
         LINE_SPACE_DEFAULT = Printer.ESCPOS_CMD.LINE_SPACE_DEFAULT,

         OPEN_DRAWER_1 = Printer.ESCPOS_CMD.OPEN_DRAWER_1, // Open Cashdrawer 1
         OPEN_DRAWER_1b = Printer.ESCPOS_CMD.OPEN_DRAWER_1b, // Open Cashdrawer 1 EPSON version 2, try this if the first version does not work
         OPEN_DRAWER_2 = Printer.ESCPOS_CMD.OPEN_DRAWER_2, // Open Cashdrawer 2
         OPEN_DRAWER_2b = Printer.ESCPOS_CMD.OPEN_DRAWER_2b, // Open Cashdrawer 2 EPSON version 2, try this if the first version does not work
         ESCPOS_ERROR = Printer.ESCPOS_LASTERROR;
 }

