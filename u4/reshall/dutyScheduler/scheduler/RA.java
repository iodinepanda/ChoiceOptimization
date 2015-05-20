package u4.reshall.dutyScheduler.scheduler;

/**
 * Copyright (C) 2015 Matthew Mussomele
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
 *     -Java's built in HashMap class for quick accessing of duty preference values.
 *     -Java's built in HashSet class for quick lookup of duty eligibility.
 *     -My static ErrorChecker class for data validation and reporting
 *     -My IntegerBoundException class for more descriptive error reporting.
 */
import java.util.HashMap;
import java.util.HashSet;
import u4.reshall.dutyScheduler.customErrors.ErrorChecker;
import u4.reshall.dutyScheduler.customErrors.IntegerBoundException;

/**
 * A class representing Resident Assistants for use with my genetic scheduling algorithm.
 * This class is immutable. 
 * 
 * @author Matthew Mussomele
 */

public class RA {
        
    private static final int ILLEGAL_DUTY = Integer.MAX_VALUE;

    private HashMap<Duty, Integer> preferences;
    private String name;
    private int dutiesToAssign;
    private HashSet<Duty> invalidDuties;

    /**
     * A private constructor for the RA class. This is private to enforce usage of the RABuilder
     * nested class, which allows for the immutability of RA instances.
     * 
     * @param  builder An RABuilder instance that this RA is constructed from
     */
    private RA(RABuilder builder) {
        preferences = new HashMap<Duty, Integer>(builder.prefs);
        name = builder.n;
        dutiesToAssign = builder.dta;
        invalidDuties = new HashSet<Duty>(builder.iD);
    }

    /**
     * Evaluates whether the given duty is a valid assignment.
     * 
     * @param  duty A Duty object to check the validity of
     * @return      true if this RA instance can cover this duty, false otherwise
     */
    public boolean eligibleDuty(Duty duty) {
        return !invalidDuties.contains(duty);
    }

    /**
     * Gets the preference value of a duty relative to this RA instance
     * 
     * @param  duty A Duty object to get the preference of
     * @return The weight of duty in this RA instance's preferences. Lower values are more prefered.
     */
    public int dutyWeight(Duty duty) {  
        return (preferences.containsKey(duty)) ? preferences.get(duty) : ILLEGAL_DUTY;
    }

    /**
     * Gets the number of duties that need to be assigned to an RA
     * 
     * @return the number of duties this RA instance is to be assigned
     */
    public int requiredDuties() {
        return dutiesToAssign;
    }

    /**
     * Returns a string representation of this RA object
     * 
     * @return The name of the RA represented by this instance
     */
    @Override
    public String toString() {
        return name;
    } 

    /**
     * Compares this to another Object for equivalency.
     * 
     * @param  other The other Object to be compared against.
     * @return true if the the other Object is an RA and if it has the same name, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof RA) {
            return name.equals(((RA) other).toString());
        } else {
            return false;
        }
    }

    /**
     * Gets an integer representation of this RA for hashing.
     * @return the hashCode() of the name of the RA represented by this instance
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * A static builder class to allow RA instances to be immutable.
     */
    public static class RABuilder {

        private HashMap<Duty, Integer> prefs;
        private String n;
        private int tD;
        private int dta;
        private HashSet<Duty> iD;

        /**
         * Creates a new RABuilder.
         * 
         * @param  name           The name of the RA to be constructed.
         * @param  totalDuties    The total number of duties being scheduled.
         * @param  dutiesToAssign The number of duties the to-be constructed RA should be given.
         */     
        public RABuilder(String name, int totalDuties, int dutiesToAssign) {
            this.prefs = new HashMap<Duty, Integer>(totalDuties);
            this.iD = new HashSet<Duty>(totalDuties);

            this.n = name;
            this.tD = totalDuties;
            this.dta = dutiesToAssign;
        }

        /**
         * Associates the given Duty with the given preference value.
         *     
         * @param duty     The Duty instance to associate with the preference.
         * @param priority The preference of the given Duty instance. Lower means more prefered.
         */
        public void putPreference(Duty duty, Integer priority) {
            try {
                ErrorChecker.inBounds("priority", priority, 0, tD);                
            } catch (IntegerBoundException e) {
                ErrorChecker.printExceptionToLog(e);
            } catch (IllegalArgumentException e) {
                ErrorChecker.printExceptionToLog(e);
            }
            if (priority == 0) {
                iD.add(duty);
            } else {
                prefs.put(duty, priority);
            }
        }   

        /**
         * Builds a new immutable RA instance from this RABuilder.
         * 
         * @return A new RA instance.
         */
        public RA build() {
            return new RA(this);
        }

    }

}
