package ttnapp;

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
	
	private static final String LINE_SEPARATOR = "==============================";
	
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
	
	private static void onMessageEventHandler(String devId, DataMessage data){		

		UplinkMessage uplinkMessage = (UplinkMessage) data;
	
		System.out.println(LINE_SEPARATOR);
		System.out.println("Message from: " + devId);
		System.out.println("Message counter: " + uplinkMessage.getCounter());
		System.out.println("Message fields:");
		for (String fieldName: uplinkMessage.getPayloadFields().keySet()) {
			System.out.println("- "+fieldName+": "+ uplinkMessage.getPayloadFields().get(fieldName));
		}
		System.out.println(LINE_SEPARATOR);
	
	}
			
	private static void onErrorEventHandler(Throwable _error){
		System.out.println(LINE_SEPARATOR);
		System.err.println("Error: " + _error.getMessage());
		System.out.println(LINE_SEPARATOR);
	}
	
	
    public static void main(String[] args) throws Exception {
        
    	String region = System.getenv("region");
        String appId = System.getenv("appId");
        String accessKey = System.getenv("accessKey");

        Client client = new Client(region, appId, accessKey);
        
        client.onConnected(TTNApp::onConnectedEventHandler);
        client.onActivation(TTNApp::onActivationEventHandler);
        client.onMessage(TTNApp::onMessageEventHandler);        
        client.onError(TTNApp::onErrorEventHandler);
             
        client.start();    
    }

}