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
 * A custom error built to notify users that numerical input falls outside the allowed bounds of the context.
 * @author Matthew Mussomele
 *
 */
@SuppressWarnings("serial")
public class IntegerBoundException extends Exception {

	public IntegerBoundException() {
		super();
		
	}

	public IntegerBoundException(String message) {
		super(message);
		
	}
	
	public IntegerBoundException(String var, int lowerBound, int upperBound, int gotVal){
		super(String.format("Arg %s must be greater than or equal to %d and less than or equal to %d, got %d.", var, lowerBound, upperBound, gotVal));
		
	}
	
	public IntegerBoundException(String var, int lower, int gotVal, boolean low){
		super(String.format("Arg %s must be greater than or equal to %d, got %d.", var, lower, gotVal));
		
	}
	
	public IntegerBoundException(String var, int upper, int gotVal){
		super(String.format("Arg %s must be less than or equal to %d, got %d.", var, upper, gotVal));
		
	}

	public IntegerBoundException(Throwable cause) {
		super(cause);
		
	}

	public IntegerBoundException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public IntegerBoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
