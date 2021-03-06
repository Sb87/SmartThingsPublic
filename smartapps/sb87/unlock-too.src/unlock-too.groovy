/**
 *  Unlock Too
 *
 *  Copyright 2015 Steven Barnett
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Unlock Too",
    namespace: "Sb87",
    author: "Steven Barnett",
    description: "When one lock is unlocked, unlock another.",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Locks") {
		input "monitorLocks", "capability.lock", title:"When this lock is unlocked...", required: true, multiple: true
		input "targetLocks", "capability.lock", title:"...also unlock this lock", required: true, multiple: true
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(monitorLocks, "lock.unlocked", unlockedHandler)
}

def unlockedHandler(evt) {
    log.debug "Unlock detected at ${evt.device}"
    def now = new Date();
    def earlier = new Date(now.getTime() - 10000) // 10 seconds ago
    targetLocks.each { lock ->
        def events = lock.eventsSince(earlier, [max: 1]);
        if (0 == events.size  && "locked" == lock.currentLock) { // only unlock if no events recently and locked
            log.debug "Unlock ${lock}"
            lock.unlock();
        }
    }
}
