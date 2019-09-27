package network;

import java.util.ArrayList;
import java.util.Random;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import main.Secrets;
import network.messages.AllPiecesMessage;
import network.messages.ConcededMessage;
import network.messages.CurrentTurnMessage;
import network.messages.GameStartedMessage;
import network.messages.HostConcedeMessage;
import network.messages.JoinColorTakenMessage;
import network.messages.JoinNameTakenMessage;
import network.messages.JoinRequestMessage;
import network.messages.JoinSuccessMessage;
import network.messages.JoinTakeoverMessage;
import network.messages.KickedComputerStandinMessage;
import network.messages.KickedFocedConcedeMessage;
import network.messages.KickedGamePauseMessage;
import network.messages.KickedGameResumeMessage;
import network.messages.LastWillMessage;
import network.messages.ListGamesMessage;
import network.messages.NewGameMessage;
import network.messages.NoPreviousRoundsMessage;
import network.messages.PlayPieceMessage;
import network.messages.PlayerScoresMessage;
import network.messages.PlayersMessage;
import network.messages.TurnOrderMessage;
import network.messages.UnrecognizedPlayerMessage;

public class Network implements MqttCallback {

	private static final String ROOT_TOPIC = "BlueLagoon/";
	private static final String LOBBY_TOPIC = ROOT_TOPIC + "Games";

	// A bit inefficient but I don't want to work through all the possible issues and subtle
	// bugs. Plus, using a uniform QOS guarantees ordering.
	private static final int QOS = 2;

	private MqttClient client;
	public ArrayList<LobbyGame> gameList;

	public Network() {
		AllPiecesMessage.register();
		ConcededMessage.register();
		CurrentTurnMessage.register();
		GameStartedMessage.register();
		HostConcedeMessage.register();
		JoinColorTakenMessage.register();
		JoinNameTakenMessage.register();
		JoinRequestMessage.register();
		JoinSuccessMessage.register();
		JoinTakeoverMessage.register();
		KickedComputerStandinMessage.register();
		KickedFocedConcedeMessage.register();
		KickedGamePauseMessage.register();
		KickedGameResumeMessage.register();
		LastWillMessage.register();
		ListGamesMessage.register();
		NewGameMessage.register();
		NoPreviousRoundsMessage.register();
		PlayerScoresMessage.register();
		PlayersMessage.register();
		PlayPieceMessage.register();
		TurnOrderMessage.register();
		UnrecognizedPlayerMessage.register();

		String host = "tcp://" + Secrets.DEFAULT_SERVER_URL + ":" + Secrets.DEFAULT_PORT;
		String username = Secrets.DEFAULT_USERNAME;
		String password = Secrets.DEFAULT_PASSWORD;
		String clientId = "BlueLagoon-" + (new Random()).nextLong();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(true);
		options.setUserName(username);
		options.setPassword(password.toCharArray());
		options.setWill("LastWills", new LastWillMessage(clientId).toJson().getBytes(), QOS, false);

		try {
			client = new MqttClient(host, clientId, new MemoryPersistence());

			client.setCallback(this);
			client.connect(options);

			client.subscribe("BlueLagoon/Games", QOS);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("MqttException handler not yet written in StartScreen of StartScreen.", e);
		}

		send(new ListGamesMessage());
	}

	private void send(Message message) {
		try {
			client.publish(LOBBY_TOPIC, message.toJson().getBytes(), QOS, false);
		} catch (MqttPersistenceException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("MqttPersistenceException handler not yet written in send of Network.", e);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("MqttException handler not yet written in send of Network.", e);
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("connectionLost of MqttCallback not yet implemented.");
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		if (topic.equals("BlueLagoon/Games")) {
			Message parsedMessage = MessageParser.parseMessage(message.toString());
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {

	}

	public static class LobbyGame {
		public String topic;
		public String name;
	}
}
