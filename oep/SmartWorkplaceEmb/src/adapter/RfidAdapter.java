package adapter;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import com.bea.wlevs.ede.api.EventSender;
import com.bea.wlevs.ede.api.RunnableBean;
import com.bea.wlevs.ede.api.StreamSender;
import com.bea.wlevs.ede.api.StreamSource;
import com.eclipsesource.json.JsonObject;

public class RfidAdapter implements RunnableBean, StreamSource {

	private static final int SLEEP_MILLIS = 1000;
	private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 115200;
	private boolean suspended;
	private StreamSender eventSender;
	private SerialPort serialPort;

	HashMap<Integer, ArduinoListener> desks = new HashMap<Integer, ArduinoListener>();

	public RfidAdapter() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		suspended = false;
		initializeReaders();

		while (!isSuspended()) {
			// Keep adapter alive
			try {
				synchronized (this) {
					wait(SLEEP_MILLIS);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void initializeReaders() {
		CommPortIdentifier portId = null;
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();

		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum
					.nextElement();
			System.out.println(currPortId.getName());
			if (currPortId.getName().startsWith("/dev/ttyACM")) {
				portId = currPortId;

				try {
					serialPort = (SerialPort) portId.open(this.getClass()
							.getName(), TIME_OUT);

					serialPort.setSerialPortParams(DATA_RATE,
							SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);

					ArduinoListener al = new ArduinoListener(serialPort);
					while (al.desknr == -1) {
						System.out.println("Waiting for desknr: " + al.desknr);
						Thread.sleep(1000);
					}
					System.out.println("Found deskrn: " + al.desknr);

					desks.put(new Integer(al.desknr), al);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bea.wlevs.ede.api.StreamSource#setEventSender(com.bea.wlevs.ede.api
	 * .StreamSender)
	 */
	public void setEventSender(StreamSender sender) {
		eventSender = sender;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bea.wlevs.ede.api.SuspendableBean#suspend()
	 */
	public synchronized void suspend() {
		suspended = true;
	}

	private synchronized boolean isSuspended() {
		return suspended;
	}

	class ArduinoListener implements SerialPortEventListener {

		SerialPort port;

		private BufferedReader input;
		private OutputStream output;
		private String tmpJson = "";
		public int desknr = -1;
		public String roomnr;

		Properties prop = new Properties();

		public ArduinoListener(SerialPort portArduino) {
			this.port = portArduino;
			try {
				input = new BufferedReader(new InputStreamReader(
						port.getInputStream()));
				output = port.getOutputStream();

				port.addEventListener(this);
				port.notifyOnDataAvailable(true);

				InputStream input = new FileInputStream("config.properties");

				prop.load(input);

				this.roomnr = prop.getProperty("roomnr");

			} catch (Exception ex) {

			}
		}

		public synchronized void close() {
			if (port != null) {
				port.removeEventListener();
				port.close();
			}

		}

		private void processJson(String jsonString) {
			try {

				JsonObject jsonObject = JsonObject.readFrom(jsonString);

				if (desknr == -1)
					desknr = jsonObject.get("readerNr").asInt();

				JsonObject returnObject = new JsonObject();
				returnObject.add("roomId", roomnr);
				returnObject.add("deskNum", desknr);
				returnObject.add("status", jsonObject.get("status").asInt());

				if (jsonObject.get("status").asInt() == 2) {
					returnObject.add("cardId", jsonObject.get("serial")
							.asString());
					returnObject.add("deviceId", jsonObject.get("deviceid")
							.asString());
				}
				System.out.println(port.getName() + ": "
						+ returnObject.toString());

			} catch (Exception ex) {

				System.out.println("Parsing issue: " + jsonString);
				ex.printStackTrace();
			}

		}

		public synchronized void serialEvent(SerialPortEvent oEvent) {
			if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				try {
					String inputLine = input.readLine();
					inputLine.replace("\n", "");

					if (inputLine.startsWith("{") && !tmpJson.endsWith("}")) {
						tmpJson = inputLine;
						// System.out.println("1: " + tmpJson);
					} else if (inputLine.startsWith("{")
							&& tmpJson.endsWith("}")) {
						tmpJson = inputLine;
						// System.out.println("2: " + tmpJson);
						processJson(tmpJson);

					} else if (!tmpJson.equals("")) {
						tmpJson = tmpJson + inputLine;
						// System.out.println("3: " + tmpJson);
						if (tmpJson.endsWith("}")) {
							processJson(tmpJson);
						}
					} else {
						System.out.println("No Valid Json: " + tmpJson);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void reserve(String reservation) {
			if (port != null) {
				try {
					output.write(reservation.getBytes());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

	}
}
