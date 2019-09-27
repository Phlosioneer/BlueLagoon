module BlueLagoon {
	requires transitive java.desktop;
	requires transitive core;
	requires transitive TiledMapLoader;
	requires transitive org.eclipse.paho.client.mqttv3;
	requires transitive gson;

	exports main;
}