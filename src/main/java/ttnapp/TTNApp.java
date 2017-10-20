package ttnapp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.thethingsnetwork.data.common.Connection;
import org.thethingsnetwork.data.common.messages.ActivationMessage;
import org.thethingsnetwork.data.common.messages.DataMessage;
import org.thethingsnetwork.data.common.messages.UplinkMessage;
import org.thethingsnetwork.data.mqtt.Client;


/**
 *
 * @author Michele di Girolamo
 */
public class TTNApp {
	
	private static final String DEFAULT_REGION = "eu";
	private static final String DEFAULT_APPID = "testing-lorawan-mdg";
	private static final String DEFAULT_ACCESSKEY = "ttn-account-v2._Hr9svW80OV4eEJeeNPD0sWriSOeLYXfe9Jyypeo_Lc"; 
	private static final String LINE_SEPARATOR = "==============================";
	private static final String LOG_FILE_FOLDER ="log";
	private static final String LOG_FILE_NAME_PREFIX = "log";
	private static final String LOG_FILE_NAME_SUFFIX = ".dat";
	private final static DateFormat DEFAULT_TIMESTAMP_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmssS");
	
	private static void onConnectedEventHandler(Connection _client){
		System.out.println(LINE_SEPARATOR);
		System.out.println("App connected to the broker!");
		System.out.println(LINE_SEPARATOR);
	}
	
	private static void onActivationEventHandler(String _devId, ActivationMessage _data){
		System.out.println(LINE_SEPARATOR);
		System.out.println("New device activated!");
		System.out.println("Device ID: " +  _devId);
		System.out.println("Device address: " + _data.getDevAddr());
		System.out.println(LINE_SEPARATOR);
	}
	
	private static void onMessageEventHandler(String devId, DataMessage data) {		

		UplinkMessage uplinkMessage = (UplinkMessage) data;
	
		System.out.println(LINE_SEPARATOR);
		System.out.println("Message from: " + devId);
		System.out.println("Message counter: " + uplinkMessage.getCounter());
		System.out.println("Message fields:");
		for (String fieldName: uplinkMessage.getPayloadFields().keySet()) {
			System.out.println("- "+fieldName+": "+ uplinkMessage.getPayloadFields().get(fieldName));
		}
		System.out.println(LINE_SEPARATOR);
		
		try {
			logUplinkMessage(uplinkMessage);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
			
	private static void onErrorEventHandler(Throwable _error){
		System.out.println(LINE_SEPARATOR);
		System.err.println("Error: " + _error.getMessage());
		System.out.println(LINE_SEPARATOR);
	}
	
	
    public static void main(String[] args) throws Exception {
        
    	String region = System.getenv("region") != null ? System.getenv("region") : DEFAULT_REGION;
        String appId = System.getenv("appId") != null ? System.getenv("appId") : DEFAULT_APPID;
        String accessKey = System.getenv("accessKey") != null ? System.getenv("accessKey") : DEFAULT_ACCESSKEY;

        Client client = new Client(region, appId, accessKey);
        
        client.onConnected(TTNApp::onConnectedEventHandler);
        client.onActivation(TTNApp::onActivationEventHandler);
        client.onMessage(TTNApp::onMessageEventHandler);        
        client.onError(TTNApp::onErrorEventHandler);
             
        client.start();    
    }
    
    private static void logUplinkMessage(UplinkMessage uplinkMessage) throws FileNotFoundException{
    	
    	//create the results path
		String logPath = LOG_FILE_FOLDER 
				+ "/"
				+ LOG_FILE_NAME_PREFIX
				+ LOG_FILE_NAME_SUFFIX;
		
		FileOutputStream fileOut = new FileOutputStream(logPath, true);
		PrintWriter printWriter = new PrintWriter(fileOut, true);
		
		String timestamp = uplinkMessage.getMetadata().getTime();
		String devId = uplinkMessage.getDevId();
		int counter = uplinkMessage.getCounter();
		String dataRate = uplinkMessage.getMetadata().getDataRate();
		double frequency = uplinkMessage.getMetadata().getFrequency();
		byte[] rawPayload = uplinkMessage.getPayloadRaw();
		StringBuilder rawPayloadString = new StringBuilder();
		for(byte b:rawPayload){
			rawPayloadString.append(String.format("%02X", b));
		}
		//double rssi = uplinkMessage.getMetadata().getGateways().get(0).getRssi();
		//double snr = uplinkMessage.getMetadata().getGateways().get(0).getSnr();
				
		//printWriter.format("%s\t%s\t%d\t%s\t%f\t%f\t%f\t%s\n", timestamp, devId, counter, dataRate, frequency, rssi, snr, rawPayloadString);		
		printWriter.format("%s\t%s\t%d\t%s\t%f\t%s\n", timestamp, devId, counter, dataRate, frequency, rawPayloadString);
		
		printWriter.close();		
    	
    }
    
}