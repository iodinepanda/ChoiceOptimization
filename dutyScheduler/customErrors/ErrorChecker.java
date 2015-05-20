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

import dutyScheduler.scheduler.RA;
import dutyScheduler.scheduler.Duty;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.File;

/**
 * A class meant for performing error checking and data validation on inputs to the algorithm.
 * @author Matthew Mussomele
 *
 */
public class ErrorChecker {
	
	private static int[] cantDosByRA;
	private static int[] cantDosByDuty;
	private static int[] prefSumPerRA;
	
	/**
	 * Checks if a given integer with value 'val' is within the allowed bounds.
	 * @param var the name of the variable that is being tested
	 * @param val the value of the variable that is being tested
	 * @param lower the lower bound on the integer's allowed values (inclusive), -1 is used to denote no lower bound 
	 * @param upper the upper bound on the integer's allowed values (inclusive), -1 is used to denote no upper bound
	 * @throws IntegerBoundException throws a custom error message if the integer is outside the allowed bounds
	 * @throws IllegalArgumentException throws an exception if both the upper and lower bounds are undefined
	 */
	public static void inBounds(String var, int val, int lower, int upper) 
							    throws IntegerBoundException, IllegalArgumentException {
		if (upper == -1) {
			if (lower == -1) {
				throw new IllegalArgumentException("At least one bound must be defined.");
			} else {
				overBound(var, val, lower);
			}
		} else if (lower == -1) {
			underBound(var, val, upper);
		} else if (val > upper || val < lower) {
			throw new IntegerBoundException(var, lower, upper, val);
		}
	}
	
	/**
	 * Assigns the integer at index j of cantDosByRA to how many duties the RA at index j of raList has labeled undoable, and assigns the integer at index i of cantDosByDuty to how many RAs have labeled the DutyDate at index i of dutyList undoable.
	 * @param raList an <code>ArrayList</code> containing all of the RAs
	 * @param dutyList an <code>ArrayList</code> containing all of the duties
	 * @throws InvalidFileContentsException  throws an exception if the required duties does not equal the total duties in the json data file
	 */
	public static void evalPrefs(ArrayList<RA> raList, ArrayList<Duty> dutyList) 
								 throws InvalidFileContentsException {
		cantDosByRA = new int[raList.size()];
		cantDosByDuty = new int[dutyList.size()];
		prefSumPerRA = new int[raList.size()];
		int dutiesToAssign = 0;
		for(int i = 0; i < dutyList.size(); i++){
			for(int j = 0; j < raList.size(); j++){
				if(i == 0)
					dutiesToAssign += raList.get(j).requiredDuties();
				if(!raList.get(j).eligibleDuty(dutyList.get(i))){
					cantDosByDuty[i]++;
					cantDosByRA[j]++;
				}
				else{
					prefSumPerRA[j] += raList.get(j).dutyWeight(dutyList.get(i));
				}
			}
		}
		if(dutiesToAssign != dutyList.size())
			throw new InvalidFileContentsException("The number of duties to assign to each RA does not sum to the number of duties that need assignment. ");
	}
	
	/**
	 * Checks and makes sure the preferences are (probably) of the form 1, 2, ..., n. 
	 * @throws PreferenceConsistencyException throws an exception if this is not true
	 */
	public static void checkConsistency() throws PreferenceConsistencyException {
		for(int i = 0; i < prefSumPerRA.length; i++)
			if(prefSumPerRA[i] != (sumTo(cantDosByDuty.length - cantDosByRA[i])))
				throw new PreferenceConsistencyException();
	}
	
	private static int sumTo(int n){
		return n*(n + 1) / 2;
	}
	
	/**
	 * Checks to see if the set of preferences has made the duties impossible to schedule by standard scheduling rules.
	 * @throws ImpossiblePreferencesException throws an exception if one or more duties have been labeled as undoable by every RA
	 */
	public static void checkImpossible() throws ImpossiblePreferencesException {
		for(int i = 0; i < cantDosByDuty.length; i++)
			if(cantDosByDuty[i] == cantDosByRA.length)
				throw new ImpossiblePreferencesException();
	}
	
	/**
	 * Checks to see if one or more RAs have labeled too many duties as undoable. This is to prevent RAs from labeled every duty but the ones they really want as undoable.
	 * @param raList an <code>ArrayList</code> containing all of the RAs
	 * @throws GreedyException raises an exception if one or more RAs have listed too many duties as undoable
	 */
	public static void checkGreedy(ArrayList<RA> raList) throws GreedyException {
		ArrayList<String> badRAs = new ArrayList<String>();
		for(int i = 0; i < cantDosByRA.length; i++){
			if(cantDosByRA[i] >= .5*cantDosByDuty.length){
				badRAs.add(raList.get(i).toString());
			}
		}
		if(badRAs.size() > 0)
			throw new GreedyException(badRAs);
	}
	
	/**
	 * 
	 * @param var the name of the variable that is being tested
	 * @param val the value of the variable that is being tested
	 * @param lower the lower bound on the integer's allowed values (inclusive)
	 * @throws IntegerBoundException throws a custom error message if the integer is outside the allowed bounds
	 */
	public static void overBound(String var, int val, int lower) throws IntegerBoundException {
		if(val >= lower)
			return;
		throw new IntegerBoundException(var, lower, val, true);
	}
	
	/**
	 * 
	 * @param var the name of the variable that is being tested
	 * @param val the value of the variable that is being tested
	 * @param upper the upper bound on the integer's allowed values (inclusive)
	 * @throws IntegerBoundException throws a custom error message if the integer is outside the allowed bounds
	 */
	public static void underBound(String var, int val, int upper) throws IntegerBoundException {
		if(val <= upper)
			return;
		throw new IntegerBoundException(var, upper, val);
	}
	
	/**
	 * Prints a thrown exception to an error log file and exits. If recording the exception fails, a message is printed.
	 * @param e A thrown exception to be recorded.
	 */
	public static void printExceptionToLog(Exception e) {
		String dateTime = (new SimpleDateFormat("MM-dd-yyyy-hh:mm:ss")).format(new Date());
		File log = new File("error_log.txt");
		PrintWriter out = null; 
		try {
			if (!log.exists()) {
				log.createNewFile();
			} 
			out = new PrintWriter(new FileWriter(log, true));
			String message = dateTime + "\n";
			int len = message.length();
			for (int i = 0; i < len; i += 1) {
				message += "_";
			}
			out.println(message);
			e.printStackTrace(out);
			out.println("\n");
		} catch (IOException ioe) {
			System.out.println("Error logging failed.");
		} finally {
			out.close();
			System.exit(-1);
		}
	}

}
