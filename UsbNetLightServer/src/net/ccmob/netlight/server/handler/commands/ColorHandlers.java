package net.ccmob.netlight.server.handler.commands;

import java.util.regex.Matcher;

import net.ccmob.netlight.server.handler.NetCommandHandler;
import net.ccmob.usbled.lib.dataTypes.RGBColor;

/**
 * 
 * @author Marcel Benning
 * @date 27.07.2014
 *
 */

public class ColorHandlers {

	public static class ColorHandler extends NetCommandHandler {

		public ColorHandler() {
			super(
					"^:net:usbled:cmd:[*]:([0-9]{1,3}):([0-9]{1,3}):([0-9]{1,3}):([0-9]{1,3}):([0-9]{1,3}):([0-9]{1,3})");
		}

		@Override
		public void handleCommand(String data, Matcher matcher) {
			int cmd = Integer.valueOf(matcher.group(2));
			if (cmd == 2) { // == rgb color command
				System.out.println("Command = 2");
				int addr = Integer.valueOf(matcher.group(1));
				int sAddr = Integer.valueOf(matcher.group(6));
				int r = Integer.valueOf(matcher.group(3));
				int g = Integer.valueOf(matcher.group(4));
				int b = Integer.valueOf(matcher.group(5));
				this.getDevice().setAdress(addr);
				this.getDevice().setStripeAdress(sAddr);
				this.getDevice().setColor(r, g, b);
				this.sendBack("net:usbled:cmd:s");
			} else {
				this.sendBack("net:usbled:404cmd");
			}
		}
	}

	public static class CurrentColorHandler extends NetCommandHandler {

		public CurrentColorHandler() {
			super(
					"^:net:usbled:cmd:[*]:getCurrentColor:([0-9]{1,3}):([0-9]{1,3}):#");
		}

		@Override
		public void handleCommand(String data, Matcher matcher) {
			int adress = Integer.valueOf(matcher.group(1));
			int stripeAdress = Integer.valueOf(matcher.group(1));
			RGBColor rgbColor = this.getDevice().getCurrentColorById(adress,
					stripeAdress);
			String back = "net:usbled:color:" + rgbColor.getR() + ":"
					+ rgbColor.getG() + ":" + rgbColor.getB() + ":#";
			this.sendBack(back);
		}
	}
}