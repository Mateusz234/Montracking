# Montracking
Montracking is a system for monitoring and tracking objects. 
It's done by raspberry pi camera module connected with raspberry pi 3B. Camera is placed on the
handmade stand with 2 motors that can rotate camera in two axes. It can rotate for about 
180 degrees in X and 90 degrees in Y axis.
System features:
	- control panel with video stream from camera
	- choosing object to find 
	- searching function - camera rotates periodically in X and Y to find selected object
	- track object - when object found, take its coordinates from screen and the 
	  difference of middle of the screen and centre of the object will be deviation
	  that will be an input of PI regulator implemented in java.
	
