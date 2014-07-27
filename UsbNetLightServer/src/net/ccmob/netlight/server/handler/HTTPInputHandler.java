package net.ccmob.netlight.server.handler;

import net.ccmob.netlight.server.utils.MessageType;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ccmob.netlight.server.core.NetLightServer;
import net.ccmob.netlight.server.utils.CallBack;

/**
 * 
 * @author Marcel Benning
 * @date 27.07.2014
 *
 */

public class HTTPInputHandler implements CallBack {

	private ServerSocket socket;
	private Socket client;
	private int port = 11980;
	private boolean init = false;
	private boolean running = false;
	private DataOutputStream clientOutputStream;

	public HTTPInputHandler(int port) {
		this.setPort(port);
	}

	public void stop() {
		if (init) {
			try {
				this.setRunning(false);
				this.getSocket().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the socket
	 */
	private ServerSocket getSocket() {
		return socket;
	}

	/**
	 * @param socket
	 *            the socket to set
	 */
	private void setSocket(ServerSocket socket) {
		this.socket = socket;
	}

	/**
	 * @return the client
	 */
	private Socket getClient() {
		return client;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	private void setClient(Socket client) {
		this.client = client;
	}

	/**
	 * @return the port
	 */
	private int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	private void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running
	 *            the running to set
	 */
	protected void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * @return the clientOutputStream
	 */
	private DataOutputStream getClientOutputStream() {
		return clientOutputStream;
	}

	/**
	 * @param clientOutputStream
	 *            the clientOutputStream to set
	 */
	private void setClientOutputStream(DataOutputStream clientOutputStream) {
		this.clientOutputStream = clientOutputStream;
	}

	public void run() {
		try {
			listen();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void listen() throws Exception {
		this.setSocket(new ServerSocket(this.getPort()));
		this.init = true;
		this.setRunning(true);
		print("Server socket open. Listening on port " + this.getPort()
				+ " ...");
		BufferedReader reader;
		while (this.isRunning()) {
			this.setClient(this.getSocket().accept());
			this.setClientOutputStream(new DataOutputStream(this.getClient()
					.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(this.getClient()
					.getInputStream()));
			this.sendString(construct_http_header());
			String line = reader.readLine();
			if (line.toUpperCase().startsWith("GET")) {
				this.handleInput(line);
			}
			this.getClientOutputStream().close();
			this.getClient().close();
		}
		print("HTTP Handler stopped.");
	}

	private void handleInput(String line) {
		String[] parts = line.split(" ");
		String cmd = parts[1].substring(1);
		Pattern p;
		Matcher m;
		boolean flag = false;
		for (NetCommandHandler handler : NetLightServer.getInstance().getHttpCommandHandler()) {
			p = Pattern.compile(handler.getRegex());
			m = p.matcher(cmd);
			if (m.matches()) {
				flag = true;
				handler.handle(cmd, this, m);
			}
		}
		if (!flag) {
			sendString("Command not found.");
		}
	}

	public void sendString(String s) {
		try {
			this.getClientOutputStream().writeBytes(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void print(String s) {
		NetLightServer.getInstance().queueMessage(MessageType.INFO, "HTTP", s);
	}

	private String construct_http_header() {
		String s = "HTTP/1.0 ";
		s = s + "200 OK";
		s = s + "\r\n";
		s = s + "Connection: close\r\n";
		s = s + "Server: UsbNetPlugin v0\r\n";
		s = s + "Content-Type: text/html\r\n";
		s = s + "\r\n";
		return s;
	}

}
