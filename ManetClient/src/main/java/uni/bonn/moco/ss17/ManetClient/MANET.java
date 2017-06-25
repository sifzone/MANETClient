package uni.bonn.moco.ss17.ManetClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import uni.bonn.moco.ss17.ManetClient.MANET.MESSAGE_TYPE;
import uni.bonn.moco.ss17.Adhoc.Model.DataMessage;
import uni.bonn.moco.ss17.Adhoc.Model.HelloMessage;
import uni.bonn.moco.ss17.Adhoc.Model.TopologyMessage;
import uni.bonn.moco.ss17.Adhoc.Model.WelcomeMessage;

public class MANET {

	private final static String serverName = "hive-01.informatik.uni-bonn.de";
	private final static int serverPort = 8888;
	private Socket myClient;
	private DataOutputStream outToServer;

	public enum MESSAGE_TYPE {
		WELCOME, HELLO, TOPOLOGY, DATA
	}

	private ManetNode thisNode;
	private MESSAGE_TYPE messageType;
	private JSONParser parser;
	private JSONObject jsonObj; // Message holding
	private Timer timer;

	public MANET() {

		parser = new JSONParser();
		timer = new Timer();

		try {

			System.out.println("Connecting to " + serverName + " on port " + serverPort);
			myClient = new Socket(serverName, serverPort);

			System.out.println("Just connected to " + myClient.getRemoteSocketAddress());

			outToServer = new DataOutputStream(myClient.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(myClient.getInputStream()));

			String receivedStream;
			while ((receivedStream = inFromServer.readLine()) != null) {

				System.out.println("RECEIVED: " + receivedStream);
				messageClassification(receivedStream);
			}

			outToServer.close();
			inFromServer.close();
			myClient.close();
		} catch (IOException io) {
			System.err.println(io);
		}
	}

	public void messageClassification(String message) {

		Object obj;
		try {
			obj = parser.parse(message);
			jsonObj = (JSONObject) obj;

			if (jsonObj.containsKey("type")) {

				String type = (String) jsonObj.get("type");
				if (type.compareTo("welcome") == 0) {

					messageType = MESSAGE_TYPE.WELCOME;
				}
				else if(type.compareTo("hello") == 0) {
					messageType = MESSAGE_TYPE.HELLO;
				}
				else if(type.compareTo("topology") == 0){
					messageType = MESSAGE_TYPE.TOPOLOGY;
				}
				else if(type.compareTo("data") == 0){
					messageType = MESSAGE_TYPE.DATA;
				}

				messageProcessing();
			}

		} catch (ParseException e) {
			System.out.println("Multiple MESSAGEs received!");
			String[] messages = message.split("}");
			for (String msg : messages) {

				msg = msg + "}";
				System.out.println(msg);

				try {
					obj = parser.parse(msg);

					jsonObj = (JSONObject) obj;

					if (jsonObj.containsKey("type")) {

						String type = (String) jsonObj.get("type");
						if (type.compareTo("welcome") == 0) {

							messageType = MESSAGE_TYPE.WELCOME;
						}
						else if(type.compareTo("hello") == 0) {
							messageType = MESSAGE_TYPE.HELLO;
						}
						else if(type.compareTo("topology") == 0){
							messageType = MESSAGE_TYPE.TOPOLOGY;
						}
						else if(type.compareTo("data") == 0){
							messageType = MESSAGE_TYPE.DATA;
						}
					}

					messageProcessing();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void messageProcessing() {

		if (messageType == MESSAGE_TYPE.WELCOME) {
			/**
			 * Connected to MANET
			 */
			thisNode = new ManetNode();

			// Received WELCOME message

			WelcomeMessage wm = welcomeMessageProcessing();
			thisNode.setId(wm.getId());
			System.out.println("This node has been initiated with id " + thisNode.getId());

			/**
			 * HELLO MESSAGE @every 2 seconds
			 */
			final HelloMessage helloMessage = new HelloMessage();

			helloMessage.setSender(thisNode.getId());
			helloMessage.setType("hello");
			helloMessage.setTime(new Date());

			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {

					try {
						System.out.println("SEND: " + helloMessage.getJSONFormat().toJSONString());
						outToServer.writeBytes(helloMessage.getJSONFormat().toJSONString());
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}, 0, (2 * 1000));

			/**
			 * TOPOLOGY MESSAGE @every 5 seconds
			 */
			final TopologyMessage topologyMessage = new TopologyMessage();

			thisNode.setSequence(thisNode.getSequence() + 1);
			topologyMessage.setSender(thisNode.getId());
			topologyMessage.setType("topology");
			topologyMessage.setSequence(thisNode.getSequence());
			topologyMessage.setNeighbours(thisNode.getNeighbors());

			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {

					try {
						System.out.println("SEND: " + topologyMessage.getJSONFormat().toJSONString());
						outToServer.writeBytes(topologyMessage.getJSONFormat().toJSONString());
						thisNode.setSequence(thisNode.getSequence() + 1);
						topologyMessage.setSequence(thisNode.getSequence());
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}, 0, (5 * 1000));

			/**
			 * INSPECTION of vicinity of other nodes @every 10 seconds
			 */
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {

					if (!thisNode.getNeighbors().isEmpty()) {

//						for (Long key : thisNode.getNeighbors()) {
						for(Iterator<Long> iter = thisNode.getNeighbors().iterator(); iter.hasNext();){
							Long key = iter.next();
							Date neighborTime = thisNode.getNeighborsTimer().get(key);
							Date currentTime = new Date();

							if ((currentTime.getTime() - neighborTime.getTime()) > (10 * 1000))
								thisNode.deleteNeighbors(key);

							if (thisNode.getNeighbors().isEmpty())
								break;
						}
					} else
						System.out.println("NO neighbors!");

				}
			}, 0, (10 * 1000));

			/**
			 * INSPECTION of TOPOLOGY of other nodes @every 10 seconds
			 */
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {

					if (!thisNode.getTopologyNodes().isEmpty()) {

//						for (Long key : thisNode.getTopologyNodes()) {
						for(Iterator<Long> iter = thisNode.getTopologyNodes().iterator(); iter.hasNext();){
							Long key = iter.next();
							TopologyMessage tMessage = thisNode.getTopologyOfNodes(key);
							Date receivedTime = tMessage.getTime();
							Date currentTime = new Date();

							if ((currentTime.getTime() - receivedTime.getTime()) > (10 * 1000))
								thisNode.deleteTopology(key);

							if (thisNode.getTopologyNodes().isEmpty())
								break;
						}
					}

				}
			}, 0, (10 * 1000));

		} else if (messageType == MESSAGE_TYPE.HELLO) {

			
			HelloMessage helloMessage = helloMessageProcessing();
			
			if(!thisNode.getNeighbors().contains(helloMessage.getSender())){
				DataMessage dMessage = new DataMessage();
				dMessage.setSender(thisNode.getId());
				dMessage.setType("data");
				dMessage.setReceiver(helloMessage.getSender());
				dMessage.setData("Thanks for Joining as a Neighbor");
				dMessage.setTime(new Date());				
				dMessage.setNext_hop(helloMessage.getSender());
				
				try {
					System.out.println("SEND: " + dMessage.getJSONFormat().toJSONString());
					outToServer.writeBytes(dMessage.getJSONFormat().toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			thisNode.addHelloMessage(helloMessage);
			

		} else if (messageType == MESSAGE_TYPE.TOPOLOGY) {

			TopologyMessage topologyMessage = topologyMessageProcessing();

			boolean newTMsg = thisNode.setTopologyOfNodes(topologyMessage.getSender(), topologyMessage);
			if (newTMsg) {
				try {
					System.out.println("FORWARD: " + topologyMessage.getJSONFormat().toJSONString());
					outToServer.writeBytes(topologyMessage.getJSONFormat().toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (messageType == MESSAGE_TYPE.DATA) {
			DataMessage dMessage = dataMessageProcessing();
			if (dMessage.getReceiver() == thisNode.getId()) {
				System.out.println("RECEIVED DATA: " + dMessage.getData());
			} else if (dMessage.getNext_hop() == thisNode.getId()) {
				Long next_hop = thisNode.getNextHop(dMessage.getReceiver());
				dMessage.setNext_hop(next_hop);

				try {
					System.out.println("FORWARD: " + dMessage.getJSONFormat().toJSONString());
					outToServer.writeBytes(dMessage.getJSONFormat().toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public WelcomeMessage welcomeMessageProcessing() {
		WelcomeMessage welcomeMessage = new WelcomeMessage();

		welcomeMessage.setId((Long) jsonObj.get("id"));
		welcomeMessage.setType((String) jsonObj.get("type"));
		welcomeMessage.setTime(new Date());

		return welcomeMessage;
	}

	public HelloMessage helloMessageProcessing() {
		HelloMessage helloMessage = new HelloMessage();

		helloMessage.setSender((Long) jsonObj.get("sender"));
		helloMessage.setType((String) jsonObj.get("type"));
		helloMessage.setTime(new Date());

		return helloMessage;
	}

	public TopologyMessage topologyMessageProcessing() {
		TopologyMessage topologyMessage = new TopologyMessage();

		topologyMessage.setSender((Long) jsonObj.get("sender"));
		topologyMessage.setType((String) jsonObj.get("type"));
		topologyMessage.setSequence((Long) jsonObj.get("sequence"));
		topologyMessage.setNeighbours((List<Long>) jsonObj.get("neighbors"));
		topologyMessage.setTime(new Date());

		return topologyMessage;
	}

	public DataMessage dataMessageProcessing() {
		DataMessage dataMessage = new DataMessage();

		dataMessage.setSender((Long) jsonObj.get("sender"));
		dataMessage.setType((String) jsonObj.get("type"));
		dataMessage.setReceiver((Long) jsonObj.get("receiver"));
		dataMessage.setNext_hop((Long) jsonObj.get("next_hop"));
		dataMessage.setData((String) jsonObj.get("data"));
		dataMessage.setTime(new Date());

		return dataMessage;
	}
}
