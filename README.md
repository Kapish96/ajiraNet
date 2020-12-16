# ajiraNet

This application is a SpringBoot application and contains one rest end point for 3 different Network Operation.
3 Network operations are: CREATE, MODIFY and FETCH

The CREATE operation can add a device(COMPUTER/REPEATER) or can add a connection depending upon the data being sent in the Request Body.
The MODIFY operation can modify the strength of a COMPUTER device.
The FETCH operation can fetch all devices or can fetch a route between two devices.

In General the application support the following Commands:
Add a device to a network.
Add connections between two devices.
List all the devices
To fetch the route that must be taken if information is to be passed between two devices.
To set device strength

The data sent in the request must be the raw data i.e Text/Plain.
Data Parsing is done in the application itself.
