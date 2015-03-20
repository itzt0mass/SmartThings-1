/**
 *  Life360 Helper-Phrase Change
 *  Version 1.00 3/20/15
 *
 *
 *  Copyright 2015 Michael Struck
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *  Turn specific switched on when it gets dark. Can also turn off specific switches when it becomes light again based on brightness from a connected light sensor.
 *
 */

definition(
    name: "Life360 Helper-Phrase Change",
    namespace: "MichaelStruck",
    author: "Michael Struck",
    description: "Monitors the Life360 presence sensors to trigger differnt Hello, Home phrases based on selected modes.",
    category: "Convenience",
    iconUrl: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/Life360-Helper/life360.png",
    iconX2Url: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/Life360-Helper/life360@2x.png",
    iconX3Url: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/Life360-Helper/life360@2x.png")

preferences {
	page(name: "getPref")
}

def getPref() {
    dynamicPage(name: "getPref", title: "Choose Presence Sensors and Phrases", install:true, uninstall: true) {
    section("Choose the Life360 presence sensors you want to monitor...") {
		input "people", "capability.presenceSensor", multiple: true, title: "Life360 Presence Sensor" 
	}
	section("When presence is detected and in one of these modes...") {
		input "modes", "mode", title: "Mode(s)", required: true, multiple: true
	}
	def phrases = location.helloHome?.getPhrases()*.label
		if (phrases) {
        		phrases.sort()
			section("Perform the following Hello, Home phrase when...") {
				input "phrase1", "enum", title: "Any of the modes above are active", required: true, options: phrases
				input "phrase2", "enum", title: "None of the modes above are active", required: true, options: phrases
			}
        	}
    	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	init()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	init()
}

def init(){
	subscribe(people, "presence", presence)
}

def presence(evt){
	if (everyoneIsPresent()){
    		if (runMode()) {
        		location.helloHome.execute(settings.phrase1)
    		} 
		 else {
    			location.helloHome.execute(settings.phrase2)
    		}
	 }
}

private everyoneIsPresent() {
	def result = true
	for (person in people) {
		if (person.currentPresence == "not present") {
			result = false
			break
		}
	}
	return result
}

private runMode() {
	def result = modes.contains(location.mode)
	return result
}
