#Landscape Connect Android

Landscape Connect is a new way to gather responses to landscape based questionnaires. It supports grabbing of GPS and a photo as well as a fully customised questionnaire.

##Usage
The Android code has been developed inside Android studio with Gradle. 

It's designed to work with a JSON Api which is described in the Api documentation.

##Testing
ADB Monkey can be used to perform automated monkey testing on the app:
 `adb shell monkey -p uk.co.threeequals.landscapeconnect -v 15000`
 
 
Additionally MonkeyRunner can be used to take screenshots of the app:
`monkeyrunner screenshots.py `