/*------------------------------------------------------------------------------
 **     Ident: Fabrice Veniard
 **    Author: Fabrice Veniard
 ** Copyright: (c) May 26, 2012 Fabrice Veniard All Rights Reserved.
 **------------------------------------------------------------------------------
 ** Fabrice Veniard                  |  No part of this file may be reproduced  
 ** @f8full                          |  or transmitted in any form or by any        
 **                                  |  means, electronic or mechanical, for the      
 ** H2J Montréal                     |  purpose, without the express written    
 ** Québec                           |  permission of the copyright holder.
 *------------------------------------------------------------------------------
 *
 *   This file is part of casserolesencours.
 *
 *   casserolesencours is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   casserolesencours is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with casserolesencours.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.f8full.casserolesencours;

import java.util.ArrayList;

//import com.alohar.demo.PlaceEvent.EVENT_TYPE;
import com.alohar.user.callback.ALPlaceEventListener;
import com.alohar.user.content.data.UserStay;

public class EventsManager implements ALPlaceEventListener {

	public static final EventsManager instance = new EventsManager();
	
	private EventsManager() {
	}
	
	public static synchronized EventsManager getInstance(){
		return instance;
	}
	
	//public ArrayList<PlaceEvent> events = new ArrayList<PlaceEvent>();
	public long trackingTime = System.currentTimeMillis();

	@Override
	public void onArrival(double latitude, double longitude) {
		/*PlaceEvent newEvent = new PlaceEvent();
		newEvent.time = System.currentTimeMillis();
		newEvent.type = EVENT_TYPE.ARRIVAL;
		newEvent.latitude = latitude;
		newEvent.longitude = longitude;
		events.add(0, newEvent);*/
	}

	@Override
	public void onDeparture(double latitude, double longitude) {
		/*PlaceEvent newEvent = new PlaceEvent();
		newEvent.time = System.currentTimeMillis();
		newEvent.type = EVENT_TYPE.DEPATURE;
		newEvent.latitude = latitude;
		newEvent.longitude = longitude;
		events.add(0, newEvent);*/
	}

	@Override
	public void onUserStayChanged(UserStay newUserStay) {
		/*PlaceEvent newEvent = new PlaceEvent();
		newEvent.time = System.currentTimeMillis();
		newEvent.type = EVENT_TYPE.UPDATE;
		newEvent.stay = newUserStay;
		events.add(0, newEvent);*/
	}
	
}
