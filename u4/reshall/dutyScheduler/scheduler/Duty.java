package u4.reshall.dutyScheduler.scheduler;

/**
 * Copyright (C) 2015 Matthew Mussomele, Amit Akula
 *
 *  This file is part of ChoiceOptimizationAlgorithm
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
 * Imports:
 *     -Java's built in Calendar class to reduce code redundancy
 *     -My ErrorChecker class, used to validate input before instantiating this class.
 *     -My IntegerBoundException class for more descriptive error messages.
 */
import java.util.Calendar;
import u4.reshall.dutyScheduler.customErrors.ErrorChecker;
import u4.reshall.dutyScheduler.customErrors.IntegerBoundException;

/**
 * A class used to represent a day on which RA Duty takes place. 
 * This class is immutable.
 *
 * @author Matthew Mussomele
 */
public class Duty implements Comparable<Duty> {

    private static final int MIN_YEAR = 2014;
    private static final int MAX_MONTH = 11;
    private static final int MIN_MONTH = 0;
    private String stringRep;

    private Calendar date;

    /**
     * Creates a new Duty instance that represents the date month/day/year. 
     * 
     * @param  year  The year of this date. Must be later than 2014.
     * @param  month The month of this date. Must be in between 0 and 11, inclusive.
     * @param  day   The day of this date. It is trusted that this date is valid. 
     */
    public Duty(int year, int month, int day) {
        date = Calendar.getInstance();
        try {
            ErrorChecker.inBounds("year", year, MIN_YEAR, -1);
            ErrorChecker.inBounds("month", month, MIN_MONTH, MAX_MONTH);
        } catch (IntegerBoundException e) {
            ErrorChecker.printExceptionToLog(e);
        
        } 
        date.set(year, month, day); //Note: the constructor trusts that the day is valid
        String formatString = "%d-";
        if (month < 10) {
            formatString += "0%d-";
        } else {
            formatString += "%d-";
        }
        if (day < 10) {
            formatString += "0%d";
        } else {
            formatString += "%d";
        }
        stringRep = String.format(formatString, year, month, day);
    }

    /**
     * Returns a string representing this Duty. 
     * The format is unspecified, but is guarenteed to reflect the date directly.
     * 
     * @return a string representation of this Duty.
     */
    @Override
    public String toString() {
        return stringRep;
    }

    /**
     * Returns a hashcode of this Duty.
     * 
     * @return a hashcode of this Duty.
     */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * Returns the time represented by this Duty.
     * 
     * @return the milliseconds since the Epoch
     */
    public long getTime() {
        return date.getTimeInMillis();
    }

    /**
     * Compares this Duty to another by comparing the dates they represent.
     *         
     * @param  other another object to compare against 
     * @return       0 if they represent the same time, 1 if this Duty is later, -1 otherwise.
     */
    public int compareTo(Duty other) {
        long difference = getTime() - other.getTime();
        if (difference > 0) {
            return 1;
        } else if (difference < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Compares this Duty against another Object for equialency.
     * 
     * @param  other The object to compare this Duty against
     * @return       True if they represent the same day, false otherwise or if other is not a Duty
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Duty) {
            return this.toString().equals(((Duty) other).toString());
        } else {
            return false;
        }
    }

}
