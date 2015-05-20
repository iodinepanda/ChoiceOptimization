package u4.reshall.dutyScheduler.customErrors;

/**
 * Copyright (C) 2015 Matthew Mussomele
 *
 * 	This file is part of ChoiceOptimizationAlgorithm
 * 	
 *  ChoiceOptimizationAlgorithm is free software: you can redistribute it 
 *  and/or modify it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the 
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

public class PreferenceConsistencyException extends Exception {

	public PreferenceConsistencyException() {
		super("One or more RAs have not labeled their doable duties from 1 to n, where n is totalDuties - (duties labeled as cant do). Please revise the preferences.");
		
	}

	public PreferenceConsistencyException(String message) {
		super(message);
		
		// TODO Auto-generated constructor stub
	}

	public PreferenceConsistencyException(Throwable cause) {
		super(cause);
		
		// TODO Auto-generated constructor stub
	}

	public PreferenceConsistencyException(String message, Throwable cause) {
		super(message, cause);
		
		// TODO Auto-generated constructor stub
	}

	public PreferenceConsistencyException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
		// TODO Auto-generated constructor stub
	}

}
