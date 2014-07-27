package net.ccmob.netlight.server.core;

import java.io.File;
import java.util.ArrayList;

import net.ccmob.netlight.server.config.Config;
import net.ccmob.netlight.server.handler.HTTPInputHandler;
import net.ccmob.netlight.server.handler.NetCommandHandler;
import net.ccmob.netlight.server.handler.commands.ColorHandlers;
import net.ccmob.netlight.server.utils.LedDevice;
import net.ccmob.netlight.server.utils.MessageType;

/**
 * 
 * @author Marcel Benning
 * @date 27.07.2014
 *
 */

public class NetLightServer {

	private Config config;
	private ArrayList<NetCommandHandler> httpCommandHandler;
	private static NetLightServer instance;
	private LedDevice ledDevice;
	private HTTPInputHandler httpHandler;
	
	public static void main(String[] args){
		NetLightServer server = new NetLightServer();
		setNetLightServer(server);
		server.initialize();
		server.start();
	}

	public void initialize(){
		this.setConfig(new Config(new File("config.cfg")));
		this.getConfig().addDefault("port", "8080");
		this.getConfig().addDefault("uart", "/dev/tty.usbmodem1331");
		this.getConfig().save();
		this.setLedDevice(new LedDevice());
		this.setHttpCommandHandler(new ArrayList<NetCommandHandler>());
		new ColorHandlers.ColorHandler();
		new ColorHandlers.CurrentColorHandler();
		this.setHttpHandler(new HTTPInputHandler(Integer.valueOf(this.getConfig().getValue("port"))));
	}
	
	/**
	 * @return the config
	 */
	public Config getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	private void setConfig(Config config) {
		this.config = config;
	}

	/**
	 * @return the httpCommandHandler
	 */
	public ArrayList<NetCommandHandler> getHttpCommandHandler() {
		return httpCommandHandler;
	}

	/**
	 * @param httpCommandHandler the httpCommandHandler to set
	 */
	private void setHttpCommandHandler(
			ArrayList<NetCommandHandler> httpCommandHandler) {
		this.httpCommandHandler = httpCommandHandler;
	}

	/**
	 * @param ledDevice the ledDevice to set
	 */
	private void setLedDevice(LedDevice ledDevice) {
		this.ledDevice = ledDevice;
	}

	public LedDevice getLedDevice() {
		return ledDevice;
	}
	
	private static void setNetLightServer(NetLightServer server){
		NetLightServer.instance = server;
	}
	
	/**
	 * @return the httpHandler
	 */
	private HTTPInputHandler getHttpHandler() {
		return httpHandler;
	}

	/**
	 * @param httpHandler the httpHandler to set
	 */
	private void setHttpHandler(HTTPInputHandler httpHandler) {
		this.httpHandler = httpHandler;
	}

	public static NetLightServer getInstance(){
		return NetLightServer.instance;
	}
	
	public void queueMessage(MessageType type, String moduleName ,String s){
		System.out.println("[" + type.toString() + "]" + (moduleName.trim().isEmpty() ? "" : ("[" + moduleName + "] ")) + s);
	}

	public void start(){
		this.getHttpHandler().run();
	}
	
}
