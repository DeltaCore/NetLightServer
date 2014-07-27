package net.ccmob.netlight.server.utils;

import java.util.HashMap;

import net.ccmob.usbled.lib.communication.Device;
import net.ccmob.usbled.lib.communication.UsbLedException;
import net.ccmob.usbled.lib.dataTypes.Command;
import net.ccmob.usbled.lib.dataTypes.RGBColor;
import net.ccmob.usbled.lib.dataTypes.RGBColorCommand;

/**
 * 
 * @author Marcel Benning
 * @date 27.07.2014
 *
 */

public class LedDevice {

    private Command cmd;
    private int adress = 1;
    private int stripeAdress = 1;
    private HashMap<Integer, HashMap<Integer, RGBColor>> colorMap = new HashMap<Integer, HashMap<Integer, RGBColor>>();
    private HashMap<Integer, HashMap<Integer, RGBColor>> saveColorMap = new HashMap<Integer, HashMap<Integer, RGBColor>>();

    public void setColor(int r, int g, int b) {
        this.setCmd(new RGBColorCommand(getAdress(), getStripeAdress(), r, g, b));
        this.setCurrentColor(r, g, b);
        System.out.println("New color : " + this.getCurrentColor().toString());
        try {
            Device.getInstance().handleCommand(getCmd());
        } catch (UsbLedException e) {
            System.out.println("[LedDevice] " + e.getMessage());
        }
    }

    public void setColor(RGBColor c) {
        this.setColor(c.getR(), c.getG(), c.getB());
    }

    public void fade(RGBColor c, int speedMs) {
        int r = this.getCurrentColor().getR(), g = this.getCurrentColor().getG(), b = this.getCurrentColor().getB();
        for (int i = 0; i < 255; i++) {
            if (r > c.getR()) {
                r--;
            } else if (r < c.getR()) {
                r++;
            }
            if (g > c.getG()) {
                g--;
            } else if (g < c.getG()) {
                g++;
            }
            if (b > c.getB()) {
                b--;
            } else if (b < c.getB()) {
                b++;
            }
            System.out.println("new Color: " + new RGBColor(r, g, b).toString());
            setColor(r, g, b);
            sleep(speedMs);
        }
    }

    /**
     * @return the cmd
     */
    protected Command getCmd() {
        return cmd;
    }

    /**
     * @param cmd
     *            the cmd to set
     */
    protected void setCmd(Command cmd) {
        this.cmd = cmd;
    }

    /**
     * @return the adress
     */
    public int getAdress() {
        return adress;
    }

    /**
     * @return the stripeAdress
     */
    public int getStripeAdress() {
        return stripeAdress;
    }

    /**
     * @param adress
     *            the adress to set
     */
    public void setAdress(int adress) {
        this.adress = adress;
    }

    /**
     * @param stripeAdress
     *            the stripeAdress to set
     */
    public void setStripeAdress(int stripeAdress) {
        this.stripeAdress = stripeAdress;
    }

    public void returnToSaveColor(int speedMs) {
        RGBColor c = this.getCurrentColor();
        int r = c.getR();
        int g = c.getG();
        int b = c.getB();
        System.out.println("SaveColor: " + new RGBColor(r, g, b).toString());
        for (int i = 0; i < 255; i++) {
            if (r > this.getSaveColor().getR()) {
                r--;
            } else if (r < this.getSaveColor().getR()) {
                r++;
            }
            if (g > this.getSaveColor().getG()) {
                g--;
            } else if (g < this.getSaveColor().getG()) {
                g++;
            }
            if (b > this.getSaveColor().getB()) {
                b--;
            } else if (b < this.getSaveColor().getB()) {
                b++;
            }
            System.out.println("new Color: " + new RGBColor(r, g, b).toString());
            setColor(r, g, b);
            sleep(speedMs);
        }
    }

    public void saveColor() {
        if(cmd == null){
            this.setCurrentColor(0, 0, 0);
        }
        if (!this.getSaveColorMap().containsKey(this.getAdress())) {
            this.getSaveColorMap().put(this.getAdress(), new HashMap<Integer, RGBColor>());
        }
        if (!this.getSaveColorMap().get(this.getAdress()).containsKey(this.getStripeAdress())) {
            this.getSaveColorMap().get(this.getAdress()).put(this.getStripeAdress(), this.getCurrentColor());
        } else {
            this.getSaveColorMap().get(this.getAdress()).get(this.getStripeAdress()).set(this.getCurrentColor());
        }
    }

    public RGBColor getSaveColor() {
        if((!this.getSaveColorMap().containsKey(this.getAdress())) || (!this.getSaveColorMap().get(this.getAdress()).containsKey(this.getStripeAdress()))){
            this.saveColor();
        }
        return this.getSaveColorMap().get(this.getAdress()).get(this.getStripeAdress());
    }

    public void setCurrentColor(int r, int g, int b) {
        if (!this.getColorMap().containsKey(this.getAdress())) {
            this.getColorMap().put(this.getAdress(), new HashMap<Integer, RGBColor>());
        }
        if (!this.getColorMap().get(this.getAdress()).containsKey(this.getStripeAdress())) {
            this.getColorMap().get(this.getAdress()).put(this.getStripeAdress(), new RGBColor(r, g, b));
        } else {
            this.getColorMap().get(this.getAdress()).get(this.getStripeAdress()).set(new RGBColor(r, g, b));
        }
    }
    
    public RGBColor getCurrentColor() {
        if((!this.getColorMap().containsKey(this.getAdress())) || (!this.getColorMap().get(this.getAdress()).containsKey(this.getStripeAdress()))){
            this.setCurrentColor(0, 0, 0);
        }
        return this.getColorMap().get(this.getAdress()).get(this.getStripeAdress());
    }
    
    public RGBColor getCurrentColorById(int adress, int stripeAdress){
    	if((!this.getColorMap().containsKey(adress)) || (!this.getColorMap().get(adress).containsKey(stripeAdress))){
            this.setCurrentColor(0, 0, 0);
        }
        return this.getColorMap().get(adress).get(stripeAdress);
    }
    
    protected void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the colorMap
     */
    public HashMap<Integer, HashMap<Integer, RGBColor>> getColorMap() {
        return colorMap;
    }

    /**
     * @param colorMap
     *            the colorMap to set
     */
    public void setColorMap(HashMap<Integer, HashMap<Integer, RGBColor>> colorMap) {
        this.colorMap = colorMap;
    }

    /**
     * @return the saveColorMap
     */
    private HashMap<Integer, HashMap<Integer, RGBColor>> getSaveColorMap() {
        return saveColorMap;
    }

    /**
     * @param saveColorMap
     *            the saveColorMap to set
     */
    public void setSaveColorMap(HashMap<Integer, HashMap<Integer, RGBColor>> saveColorMap) {
        this.saveColorMap = saveColorMap;
    }

}
