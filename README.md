NetLightServer
==============

A lightweight http service for an app to communicate with. 
The server will then send the data in a formatted way to a serial port,
where a led device with a proper read software is attached to. 
The led device controls led stripes. 

Protocol
========

The protocol for this server is realy simple.

These constanst will be used :

    deviceAddress :
    the address for the device attached to the serial port.
    It's ment for the use of multiple devices on one serial line. Each device will get a address.

    stripeAddress :
    the led stripe address where the led stripe is attached to the selected device.

The structure of the protocol. 
One packet is build with these components:

    :net:usbled:cmd:*:deviceAddress:command:val1:val2:val3:val4

One use of these protocol is for the rgb color command:

The syntax of the command:

    :net:usbled:cmd:*:2:deviceAddress:2:rval:gval:bval:stripeAddress

Dependencies
============

This project is based on my [UsbLedLib](https://github.com/DeltaCore/UsbLedLib).
If you want to extend the program, i will create w way to use custom commands and so on,
like a jar file put in a specific location wich get's loaded.

Install
=======

If you want to use the project in eclipse, first clone this repo : [UsbLedLib](https://github.com/DeltaCore/UsbLedLib)
After that, clone this repo : [NetLightServer](https://github.com/DeltaCore/NetLightServer)

You are no able to modify the project to fit your needs.

If you just want to use the project as is, clone the two repos and export the project as runnable jar file.
Make sure that you export the required libraries into the generated jar file.
Then, you can just start the file with : 
>java -jar NetLightServer.jar
or what ever you named you file.

The internals
=============

This section describes how the project is build in it's self.

First, we got the mail class NetLightServer located in 'net.ccmob.netlight.server.core'.
This class has a static instance to get access to it's properties from every other class.
It has these properties:

- httpHandler, wich handles the http commands. More in the HTTPCommand section.
- httpCommands, these are the commands registrated as commands to search for when a http command get's tested. More in Custom NetCommandHandler
- config, this is the Config file instance. You can add defaults, set values, get values and save the file.
- ledDevice, over this class the interaction with the UsbLedLib is made. The led device handles the storage of the colors for all stripes and addresses.

Here the methods you can use:

- getConfig() -> returns the config
- getLedDevice() -> return the current LedDevice instance
- getHttpCommandHandler() -> returns an ArrayList<NetCommandHandler>. Here are the NetCommandHandler are stored, after they got created. The HTTPInputHandler searches in this array for the right class to match the incoming command with.
- queueMessage(MessageType type, String moduleName ,String s) -> MessageType is a Enum and sets what is written in from of the message s. The modulename is written in between the MessageType and the message. If the moduleName is empty, it won't get printed.



How to write custom commands
============================

First, make a class like this:

    package com.example.netlight.commands

    import net.ccmob.netlight.server.handler.NetCommandHandler;
    import java.util.regex.Matcher;

    public class MyOwnCommand extends NetCommandHandler {

        public MyOwnCommand(){
            super("TheRegexWichGetMatchedWithTheIncommingCommand")
        }

        @Override
        public void handleCommand(String data, Matcher matcher) {
    
        }

    }

This is the basic structure of a command class.

Let's say you want to create a command wich turns of the stripes and want to use this pattern:

> :net:usbled:turn:off

Now you have to build the regex for that.
For this one it's pretty simple. just use that as regex: 

    package com.example.netlight.commands

    import net.ccmob.netlight.server.handler.NetCommandHandler;
    import java.util.regex.Matcher;

    public class MyOwnCommand extends NetCommandHandler {

        public MyOwnCommand(){
            super(":net:usbled:turn:off")
        }

        @Override
        public void handleCommand(String data, Matcher matcher) {

        }

    }

Now we need to write the code what happens when this command get's sent and received.
We wanted to turn of the first led stripe.

    package com.example.netlight.commands

    import net.ccmob.netlight.server.handler.NetCommandHandler;

    public class MyOwnCommand extends NetCommandHandler {

        public MyOwnCommand(){
            super(":net:usbled:turn:off")
        }

        @Override
        public void handleCommand(String data, Matcher matcher) {
            this.getDevice().setAdress(1);
            this.getDevice().setStripeAdress(1);
            this.getDevice().setColor(0,0,0);
        }

    }


That's it. This is a working command.
It set's the color of the first stripe on the first device to red: 0, green: 0, blue: 0

Now, to a little more complicated section. To use arguments passed by the command:

Our class:

    package com.example.netlight.commands

    import net.ccmob.netlight.server.handler.NetCommandHandler;

    public class MyOwnColorCommand extends NetCommandHandler {

        public MyOwnCommand(){
            super(":net:usbled:turn:on:([A-Za-z]{1,})") 
        }

        @Override
        public void handleCommand(String data, Matcher matcher) {
            this.getDevice().setAdress(1);
            this.getDevice().setStripeAdress(1);
            RGBColor color = new RGBColor();
            String color = matcher.group(1);
            if(color.equalsIgnoreCase("red")){
                color.setR(255);
            }else if(color.equalsIgnoreCase("green")){
                color.setG(255);
            }else if(color.equalsIgnoreCase("blue")){
                color.setB(255);
            }
            this.getDevice().setColor(color);
        }

    }

Now the explanation:

The new regex: 
>:net:usbled:turn:on:([A-Za-z]{1,})

The part '([A-Za-z]{1,})' is very important. It says that the matcher will give us the string back (indicated by the '()').
And the string can only contains a string wich has at least one letter till infinite ('{1,}')
More important, in the string, the only allowed characters are A-Z and a-z.

Now get the string (wich should be a color) -> String color = matcher.group(1);
wich get's the first string it finds and it should store.

Thats it. If you have problems, write me an email to [marcel(at)ccmob(dot)net](mailto:marcel@ccmob.net).

How to register custom commands
===============================

In the future there will be an easy way to implement your own commands with a command leading functionality (like put your xy.class in a specific folder and they get loaded or something like that.)

For now, you have to write it in the source file NetLightServer.java in the initialize method where this command is written
> /* Put here your custom command instance creations */

For example :

> new MyCommand();

Thats it. The class NetCommandHandler wich is the parent class for your command will register the command for you into the httpCommandHandler ArrayList in the NetLightServer class.













