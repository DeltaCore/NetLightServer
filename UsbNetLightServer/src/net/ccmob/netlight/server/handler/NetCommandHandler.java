package net.ccmob.netlight.server.handler;

import java.util.regex.Matcher;

import net.ccmob.netlight.server.core.NetLightServer;
import net.ccmob.netlight.server.utils.CallBack;
import net.ccmob.netlight.server.utils.LedDevice;
import net.ccmob.netlight.server.utils.MessageType;
import net.ccmob.usbled.lib.communication.ControllerInterface;
import net.ccmob.usbled.lib.communication.UsbLedException;

/**
 * 
 * @author Marcel Benning
 * @date 27.07.2014
 *
 */

public abstract class NetCommandHandler {

	private String regex = "";
	private CallBack callBack;
	
	public NetCommandHandler(String commandRegex) {
		this.setRegex(commandRegex);
		NetLightServer.getInstance().getHttpCommandHandler().add(this);
	}
	
	public void handle(String input, CallBack callBack, Matcher matcher){
		if(!ControllerInterface.isConnected()){
			try {
				ControllerInterface.connect(NetLightServer.getInstance().getConfig().getValue("uart"));
			} catch (UsbLedException e) {
				e.printStackTrace();
			}
		}
		this.setCallBack(callBack);
		this.handleCommand(input, matcher);
	}

	public abstract void handleCommand(String data, Matcher matcher);
	
	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	public void sendBack(String s){
		this.getCallBack().sendString(s);
	}

	/**
	 * @return the callBack
	 */
	private CallBack getCallBack() {
		return callBack;
	}

	/**
	 * @param callBack the callBack to set
	 */
	private void setCallBack(CallBack callBack) {
		this.callBack = callBack;
	}
	
	protected void sysPrint(String s, MessageType t){
		NetLightServer.getInstance().queueMessage(t, "HTTP-Command", s);
	}
	
	protected LedDevice getDevice(){
		return NetLightServer.getInstance().getLedDevice();
	}
	
}
