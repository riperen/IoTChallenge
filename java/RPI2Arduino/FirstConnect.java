import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;

import com.eclipsesource.json.JsonObject;

public class FirstConnect {
	SerialPort serialPort;

	private static final String PORT_NAMES[] = {
	"/dev/ttyACM0"};

	private BufferedReader input;
	private OutputStream output;
	private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 115200;

	HashMap <Integer, ArduinoListener> desks = new HashMap <Integer, ArduinoListener> ();
	

	public void initialize(){
		//System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM*");

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		while (portEnum.hasMoreElements())
		{
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			System.out.println(currPortId.getName());
			if(currPortId.getName().startsWith("/dev/ttyACM"))
			{
				portId = currPortId;

				try {
					serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

					serialPort.setSerialPortParams(DATA_RATE,
						SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

					
					ArduinoListener al = new ArduinoListener(serialPort);
					int ardNr;
					while(al.desknr == -1)
					{
						System.out.println("Waiting for desknr: " + al.desknr);
						Thread.sleep(1000);
					}
					System.out.println("Found deskrn: " + al.desknr);

					desks.put(new Integer(al.desknr), al);

					

				} catch (Exception e)
				{
					e.printStackTrace();
				}

			}



		}

		
	}
	




	

	public static void main(String[] Args) throws Exception
	{
		FirstConnect first = new FirstConnect();
		first.initialize();

		Thread t = new Thread() 
		{
			public void run() 
			{
				try { Thread.sleep(1000000); } catch (InterruptedException ie) {}
			}
		};
		t.start();
		System.out.println("Started"); 

	}

}

 class ArduinoListener implements SerialPortEventListener {
	
	SerialPort port;

	private BufferedReader input;
	private OutputStream output;
	private String tmpJson = "";
	public int desknr = -1;
	public String roomnr;

	Properties prop = new Properties();

	


	public ArduinoListener(SerialPort portArduino){
		this.port = portArduino;
		try
		{
			input = new BufferedReader(new InputStreamReader(port.getInputStream()));
			output = port.getOutputStream();

			port.addEventListener(this);
			port.notifyOnDataAvailable(true);


			InputStream input = new FileInputStream("config.properties");
 
		
			prop.load(input);

			this.roomnr = prop.getProperty("roomnr");

		} catch (Exception ex)
		{

		}
	}

	public synchronized void close()
	{
		if(port != null)
		{
			port.removeEventListener();
			port.close();
		}

	}	

	private void processJson(String jsonString)
	{
		
		try{
			
			JsonObject jsonObject = JsonObject.readFrom( jsonString );
			
			if(desknr == -1)
				desknr = jsonObject.get("readerNr").asInt();
			else
			{

			}

			JsonObject returnObject = new JsonObject();
			returnObject.add("roomId",roomnr);
			returnObject.add("deskNum",desknr);
			returnObject.add("status",jsonObject.get("status").asInt());
			
			if(jsonObject.get("status").asInt()==2)
			{
				returnObject.add("cardId",jsonObject.get("serial").asString());
				returnObject.add("deviceId", jsonObject.get("deviceid").asString());
			}
			System.out.println(port.getName() + ": " + returnObject.toString());


		
		} catch (Exception ex)
		{

			System.out.println("Parsing issue: " + jsonString);
			ex.printStackTrace();
		}
	

	}

	public synchronized void serialEvent(SerialPortEvent oEvent)
	{
		if(oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE)
		{
			try 
			{
				String inputLine = input.readLine();
				inputLine.replace("\n", "");

				if( inputLine.startsWith("{") && !tmpJson.endsWith("}") )
				{
					tmpJson = inputLine;
					//System.out.println("1: " +  tmpJson);
				}  else if (inputLine.startsWith("{") && tmpJson.endsWith("}"))
				{
					tmpJson = inputLine;
					//System.out.println("2: " + tmpJson);
					processJson(tmpJson);

				}else if (!tmpJson.equals(""))
				{
					tmpJson = tmpJson + inputLine;
					//System.out.println("3: " +  tmpJson);
					if(tmpJson.endsWith("}"))
					{
						processJson(tmpJson);
					}
				} else
				{
					System.out.println("No Valid Json: " +tmpJson);
				}

			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}

	public void reserve(String reservation)
	{
		if (port != null)
			try{
				output.write(reservation.getBytes());
			} catch (Exception ex)
			{
				
			}

	}

}


