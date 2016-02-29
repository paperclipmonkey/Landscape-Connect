# Imports the monkeyrunner modules used by this program
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
import time

# Connects to the current device, returning a MonkeyDevice object
device = MonkeyRunner.waitForConnection()

# Installs the Android package. Notice that this method returns a boolean, so you can test
# to see if the installation worked.
device.installPackage('app/build/outputs/apk/app-debug.apk')

# sets a variable with the package's internal name
package = 'uk.co.threeequals.landscapeconnect'

# sets a variable with the name of an Activity in the package
#activity = package + '.QuestionnairesActivity'

# sets the name of the component to start
#runComponent = package + '/' + activity

# Runs the component
#device.startActivity(component=runComponent)

# Presses the Menu button
#device.press('KEYCODE_MENU', MonkeyDevice.DOWN_AND_UP)

# Takes a screenshot

def takeActivityScreenshot(activity):
	# sets the name of the component to start
	runComponent = package + '/' + activity

	# Runs the component
	device.startActivity(component=runComponent)

	# Wait for the activity to start
	MonkeyRunner.sleep(3)

	result = device.takeSnapshot()
	# Writes the screenshot to a file
	result.writeToFile('screenshots/' + activity + '.png','png')


	# - - - - - - - - - - - MENU PAGE - - - - - - - - - - - - - -
	device.touch(100, 100, 'DOWN_AND_UP')

	# Wait for the activity to start
	MonkeyRunner.sleep(0.5)

	result2 = device.takeSnapshot()
	# Writes the screenshot to a file
	result2.writeToFile('screenshots/' + activity + '-menu.png','png')

	# - - - - - - - - - - - UPLOAD QUEUE - - - - - - - - - - - - - -
	device.touch(100, 690, 'DOWN_AND_UP')
	#device.touch(1000, 1700, 'DOWN_AND_UP')

	# Wait for the activity to start
	MonkeyRunner.sleep(0.5)

	result = device.takeSnapshot()
	# Writes the screenshot to a file
	result.writeToFile('screenshots/' + activity + '-uploads.png','png')

	# - - - - - - - - - - - ABOUT QUEUE - - - - - - - - - - - - - -
	device.touch(100, 100, 'DOWN_AND_UP')
	MonkeyRunner.sleep(1)
	device.touch(100, 900, 'DOWN_AND_UP')

	# Wait for the activity to start
	MonkeyRunner.sleep(0.5)

	result = device.takeSnapshot()
	# Writes the screenshot to a file
	result.writeToFile('screenshots/' + activity + '-about.png','png')

	# - - - - - - - - - - - INSTALL QUESTIONNAIRE PAGE - - - - - - - - - - - - - -
	device.touch(100, 100, 'DOWN_AND_UP')#Menu
	MonkeyRunner.sleep(1)
	device.touch(100, 550, 'DOWN_AND_UP')#Questionnaires
	MonkeyRunner.sleep(0.5)
	device.touch(950, 1650, 'DOWN_AND_UP')#New button
	MonkeyRunner.sleep(0.5)

	result = device.takeSnapshot()
	# Writes the screenshot to a file
	result.writeToFile('screenshots/' + activity + '-install-1.png','png')

	device.touch(950, 1475, 'DOWN_AND_UP')#Code button
	MonkeyRunner.sleep(0.5)

	result = device.takeSnapshot()
	# Writes the screenshot to a file
	result.writeToFile('screenshots/' + activity + '-install-2.png','png')

	device.type('2F5A5\n')#Text entered
	MonkeyRunner.sleep(0.5)

	result = device.takeSnapshot()
	# Writes the screenshot to a file
	result.writeToFile('screenshots/' + activity + '-install-3.png','png')

	device.touch(850, 800, 'DOWN_AND_UP')#OK button
	# Wait for the activity to start

	result = device.takeSnapshot()
	# Writes the screenshot to a file
	result.writeToFile('screenshots/' + activity + '-install-4.png','png')

	MonkeyRunner.sleep(7)#Wait for questionnaire to install

	result = device.takeSnapshot()
	# Writes the screenshot to a file
	result.writeToFile('screenshots/' + activity + '-install-5.png','png')


	# - - - - - - - - - - - QUESTIONNAIRE PAGE - - - - - - - - - - - - - -
	device.touch(100, 300, 'DOWN_AND_UP')#Click first questionnaire in list
	# Wait for the activity to start
	MonkeyRunner.sleep(1)

	result = device.takeSnapshot()
	# Writes the screenshot to a file
	result.writeToFile('screenshots/' + activity + '-questionnaire.png','png')


	# - - - - - - - - - - - NEW RESPONSE PAGE 1 - - - - - - - - - - - - - -
	device.touch(1000, 150, 'DOWN_AND_UP')#Click ... menu button
	MonkeyRunner.sleep(0.5)
	device.touch(1000, 150, 'DOWN_AND_UP')#Click new response
	MonkeyRunner.sleep(0.5)
	device.touch(800, 1080, 'DOWN_AND_UP')#Allow access to location

	# Wait for the activity to start
	MonkeyRunner.sleep(1)

	result = device.takeSnapshot()
	# Writes the screenshot to a file
	result.writeToFile('screenshots/' + activity + '-response-1.png','png')

	
	# - - - - - - - - - - - NEW RESPONSE PAGE 2 - - - - - - - - - - - - - -
	device.touch(550, 600, 'DOWN_AND_UP')#New photo button
	# Wait for the activity to start
	MonkeyRunner.sleep(0.5)
	device.touch(800, 1100, 'DOWN_AND_UP')#Allow access button 1
	MonkeyRunner.sleep(0.5)
	device.touch(800, 1100, 'DOWN_AND_UP')#Allow access button 2
	MonkeyRunner.sleep(3)
	device.touch(550, 1600, 'DOWN_AND_UP')#Take photo button 1
	MonkeyRunner.sleep(3)
	device.touch(550, 1600, 'DOWN_AND_UP')#Take photo button 2

	MonkeyRunner.sleep(2)


	result = device.takeSnapshot()
	# Writes the screenshot to a file
	result.writeToFile('screenshots/' + activity + '-response-2.png','png')


takeActivityScreenshot(package + '.QuestionnairesActivity')