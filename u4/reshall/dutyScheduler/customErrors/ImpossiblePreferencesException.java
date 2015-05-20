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

/**
 * A custom error built to notify users when RA preferences have created an impossible set of scheduling constraints.
 * @author Matthew Mussomele
 *
 */
@SuppressWarnings("serial")
public class ImpossiblePreferencesException extends Exception {

	/**
	 * Instructs the user that the preferences are invalid, and tells them to either tell the algorithm to ignore the constraints or change the constraints.
	 */
	public ImpossiblePreferencesException() {
		super("One or more duties have been listed as undoable by every RA. "
				+ "\nPlease run the algorithm again with the 'allowBads' argument or change the preference values."
				+ "\nIt is advised to resolve this issue by changing the preferences, as running the algorithm with 'allowBads' drastically increases the time required to find a good solution.");
		
	}

	public ImpossiblePreferencesException(String message) {
		super(message);
		
	}

	public ImpossiblePreferencesException(Throwable cause) {
		super(cause);
		
	}

	public ImpossiblePreferencesException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ImpossiblePreferencesException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
