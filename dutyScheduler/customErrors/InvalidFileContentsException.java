package dutyScheduler.customErrors;

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
 * Raises an error if the contents of the json file are improperly formatted.
 * @author Matthew Mussomele
 *
 */
@SuppressWarnings("serial")
public class InvalidFileContentsException extends Exception {

	public InvalidFileContentsException() {
		super();
		
	}

	public InvalidFileContentsException(String message) {
		super(message);
		
	}

	public InvalidFileContentsException(Throwable cause) {
		super(cause);
		
	}

	public InvalidFileContentsException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public InvalidFileContentsException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
