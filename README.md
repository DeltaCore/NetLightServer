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
    deviceAddress -> the address for the device attached to the serial port.
    It's ment for the use of multiple devices on one serial line. The got addressed.

    stripeAddress -> the led stripe address where the led stripe is attached to the selected device.

> :net:usbled:cmd:*:deviceAddress:command:val1:val2:val3:val4 <

One use of these protocol is for the rgb color command:

The syntax of the command:

> :net:usbled:cmd:*:2:deviceAddress:2:rval:gval:bval:stripeAddress <