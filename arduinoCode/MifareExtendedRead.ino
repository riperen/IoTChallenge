/**************************************************************************/
/*! 
    @file     readMifare.pde
    @author   Adafruit Industries
    @license  BSD (see license.txt)

    This example will wait for any ISO14443A card or tag, and
    depending on the size of the UID will attempt to read from it.
   
    If the card has a 4-byte UID it is probably a Mifare
    Classic card, and the following steps are taken:
   
    - Authenticate block 4 (the first block of Sector 1) using
      the default KEYA of 0XFF 0XFF 0XFF 0XFF 0XFF 0XFF
    - If authentication succeeds, we can then read any of the
      4 blocks in that sector (though only block 4 is read here)
   
    If the card has a 7-byte UID it is probably a Mifare
    Ultralight card, and the 4 byte pages can be read directly.
    Page 4 is read by default since this is the first 'general-
    purpose' page on the tags.


    This is an example sketch for the Adafruit PN532 NFC/RFID breakout boards
    This library works with the Adafruit NFC breakout 
      ----> https://www.adafruit.com/products/364
 
    Check out the links above for our tutorials and wiring diagrams 
    These chips use I2C to communicate

    Adafruit invests time and resources providing this open source code, 
    please support Adafruit and open-source hardware by purchasing 
    products from Adafruit!
*/
/**************************************************************************/
#include <Wire.h>
#include <Adafruit_NFCShield_I2C.h>
#include "config.h"

#define IRQ   (2)
#define RESET (3)  // Not connected by default on the NFC Shield

//colorled
int redPin = 11;
int greenPin = 10;
int bluePin = 9;

uint8_t uidOld[] = { 0, 0, 0, 0, 0, 0, 0 };

int cardCount = 0;
boolean reserved = false;

String desk; 
String name="";
String device="";
String serial="";


Adafruit_NFCShield_I2C nfc(IRQ, RESET);

void setup(void) {
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(bluePin, OUTPUT); 
  
  
  Serial.begin(115200);
  Serial.println("Hello!");

  nfc.begin();

  uint32_t versiondata = nfc.getFirmwareVersion();
  if (! versiondata) {
    Serial.print("Didn't find PN53x board");
    while (1); // halt
  }
  // Got ok data, print it out!
  Serial.print("Found chip PN5"); Serial.println((versiondata>>24) & 0xFF, HEX); 
  Serial.print("Firmware ver. "); Serial.print((versiondata>>16) & 0xFF, DEC); 
  Serial.print('.'); Serial.println((versiondata>>8) & 0xFF, DEC);
  
  // configure board to read RFID tags
  nfc.SAMConfig();
  
  Serial.println("Waiting for an ISO14443A Card ...");
  
  desk = DESKID;
}


boolean ByteArrayCompare(uint8_t a[],uint8_t b[],int array_size)
{
    for (int i = 0; i < array_size; ++i)
      if (a[i] != b[i])
        return(false);
    return(true);
}


byte bcdToDec(byte val)  {
// Convert binary coded decimal to normal decimal numbers
  return ( (val/16*10) + (val%16) );
}

String getDeskID()
{
  return desk;
}

String getId(const byte * data, const uint32_t numBytes)
{
   uint32_t szPos;
  String tmp = "";
 for (szPos=0; szPos < numBytes; szPos++)
  {
    
     tmp.concat(bcdToDec(data[szPos]));
     //(char)data[szPos]
     // Serial.print((char)data[szPos]);
  } 
  return tmp;
}


void printHex(int num, int precision) {
      char tmp[16];
      char format[128];

      sprintf(format, "0x%%.%dX", precision);

      sprintf(tmp, format, num);
      Serial.print(tmp);
}


String getString(const byte * data, const uint32_t numBytes)
{
  uint32_t szPos;
  String tmp = "";
 for (szPos=0; szPos < numBytes; szPos++)
  {
 
    if (data[szPos] <= 0x1F)

      tmp.concat(" ");
      
     // Serial.print(".");
    else
     tmp.concat((char)data[szPos]);
     // Serial.print((char)data[szPos]);
  } 
  tmp.trim();
  return tmp;
}

void setColor(int red, int green, int blue)
{
#ifdef COMMON_ANODE
red = 255 - red;
green = 255 - green;
blue = 255 - blue;
#endif
analogWrite(redPin, red);
analogWrite(greenPin, green);
analogWrite(bluePin, blue);
}


char inputBuffer[10];

void loop(void) {
  
  if(Serial.available() > 0)
  {
    reserved = true;
    setColor(255,0,0);
    Serial.readBytes(inputBuffer, Serial.available());
    delay(10000);
  } else {
    reserved = false;
  
    setColor(0,255,0);
  
    uint8_t success;
    //uint8_t uidOld[] = { 0, 0, 0, 0, 0, 0, 0 };
    uint8_t uid[] = { 0, 0, 0, 0, 0, 0, 0 };  // Buffer to store the returned UID
    uint8_t uidLength;                        // Length of the UID (4 or 7 bytes depending on ISO14443A card type)
    // Wait for an ISO14443A type cards (Mifare, etc.).  When one is found
    // 'uid' will be populated with the UID, and uidLength will indicate
    // if the uid is 4 bytes (Mifare Classic) or 7 bytes (Mifare Ultralight)
    
  
    
      success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength);
      
      if (success && !ByteArrayCompare(uidOld, uid, 4)) {
        
        setColor(0,0,255);
        // Display some basic information about the card
        //Serial.println("Found an ISO14443A card");
        //Serial.print("  UID Length: ");Serial.print(uidLength, DEC);Serial.println(" bytes");
        //Serial.print("  UID Value: ");
        //nfc.PrintHex(uid, uidLength);
        String result = "";
        
        result.concat("{\"readerNr\":" + getDeskID() +",\"status\":1");
        //Serial.println("{\"readerNr\":" + getDeskID() +",\"status\":2");
        
        serial = getId(uid, uidLength);
        
        result.concat(",\"serial\":\""+getId(uid, uidLength)+"\"");
        //Serial.println(",\"serial\":"+getId(uid, uidLength));
        //Serial.print(" Old UID Value: ");
        //nfc.PrintHex(uidOld, uidLength);
        //Serial.println("");
        memcpy(uidOld, uid, 4);
        if (uidLength == 4)
        {
          // We probably have a Mifare Classic card ... 
          // Now we need to try to authenticate it for read/write access
          // Try with the factory default KeyA: 0xFF 0xFF 0xFF 0xFF 0xFF 0xFF
          
          
          uint8_t keya[6] = { 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };
          
          name = "";
          device = "";
          
          for(int i=4; i <7; i++)
          {
       
            success = nfc.mifareclassic_AuthenticateBlock(uid, uidLength, i, 0, keya);
          
            if (success)
            {
              uint8_t data[16];
             success = nfc.mifareclassic_ReadDataBlock(i, data);
          
              if (success)
              {
    
                String a = getString(data,16);
                name.concat(a);
               
              }
              else
              {
                Serial.println("Ooops ... unable to read the requested block.  Try another key?");
              }
            }
            else
            {
              Serial.println("Ooops ... authentication failed: Try another key?");
            }
          }
          for(int i=8; i <9; i++)
          {
       
            success = nfc.mifareclassic_AuthenticateBlock(uid, uidLength, i, 0, keya);
          
            if (success)
            {
              uint8_t data[16];
             success = nfc.mifareclassic_ReadDataBlock(i, data);
          
              if (success)
              {
    
                String a = getString(data,16);
                device.concat(a);
               
              }
              else
              {
                Serial.println("Ooops ... unable to read the requested block.  Try another key?");
              }
            }
            else
            {
              Serial.println("Ooops ... authentication failed: Try another key?");
            }
          }
          
          
          result.concat(",\"name\":\""+name+"\"");
          result.concat(",\"deviceid\":\""+device+"\"");
          result.concat("}");
          
          Serial.println(result);
          
          //Serial.println(",\"name\":\""+name+"\"");
          //Serial.println(",\"deviceid\":\""+id+"\"");
          //Serial.println("}");
           delay(2000);
        }
      } else if(success && ByteArrayCompare(uidOld, uid, 4))
      {
         Serial.println("{\"readerNr\":" + getDeskID() +",\"status\":2,\"serial\":\"" + serial + "\",\"deviceid\":\"" + device +"\"}");
         setColor(0,0,255);
         cardCount++;
        
        if(cardCount >12)
         {
          uint8_t empty[] = {0,0,0,0};
          memcpy(uidOld,empty , 4);
          cardCount = 0;
         } 
         delay(2000);
      } else
     {
      uint8_t empty[] = {0,0,0,0};
      if( !ByteArrayCompare( empty, uidOld, 4) )
      {
        Serial.println("{\"readerNr\":" + getDeskID() +",\"status\":3,\"serial\":\"" + serial + "\",\"deviceid\":\"" + device +"\"}");
      } else
      {
        Serial.println("{\"readerNr\":" + getDeskID() +",\"status\":0}");
      }
      
      setColor(0,255,0);
      
      memcpy(uidOld,empty , 4);
      cardCount = 0;
      delay(2000);
    }
  }
}

