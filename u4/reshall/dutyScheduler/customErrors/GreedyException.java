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

import java.util.ArrayList;

/**
 * A custom exception built to notify users when RAs have labeled too many duties as undoable.
 * @author Matthew Mussomele
 *
 */

@SuppressWarnings("serial")
public class GreedyException extends Exception {

	public GreedyException(String message) {
		super(message);
	}
	
	public GreedyException(ArrayList<String> greedyRAs){
		super(buildMessage(greedyRAs));
	}
	
	/**
	 * Builds a string containing the names of all the greedy RAs and suggests how to remedy the preference configuration.
	 * @param greedyRAs an <code>ArrayList</code> containing the names of all the greedy RAs
	 * @return a string containing the greedy RAs name's and an exception message
	 */
	public static String buildMessage(ArrayList<String> greedyRAs){
		String message = "The following RAs have listed too many duties as undoable: ";
		for(String name: greedyRAs)
			message += String.format("%s,", name);
		return message.substring(0, message.length() - 1) + "\nRun the algorithm again with the 'allowGreedy' command, or change the preference input.";
	}

	public GreedyException(Throwable cause) {
		super(cause);
	}

	public GreedyException(String message, Throwable cause) {
		super(message, cause);
	}

	public GreedyException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
