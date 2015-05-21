package dutyScheduler;

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
 */
import java.util.HashMap;
import java.util.HashSet;
import choiceOptimizer.AbstractChooser;
import choiceOptimizer.Item;

/**
 * A class representing Resident Assistants for use with my genetic scheduling algorithm.
 * This class is immutable. 
 * 
 * @author Matthew Mussomele
 */

public class RA extends AbstractChooser {
        
    private int dutiesToAssign;

    /**
     * A private constructor for the RA class. This is private to enforce usage of the RABuilder
     * nested class, which allows for the immutability of RA instances.
     * 
     * @param  builder An RABuilder instance that this RA is constructed from
     */
    private RA(RABuilder builder) {
        preferences = new HashMap<Item, Integer>(builder.prefs);
        name = builder.n;
        dutiesToAssign = builder.dta;
        invalidItems = new HashSet<Item>(builder.iD);
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
     * A static builder class to allow RA instances to be immutable.
     */
    public static class RABuilder {

        private HashMap<Item, Integer> prefs;
        private String n;
        private int tD;
        private int dta;
        private HashSet<Item> iD;

        /**
         * Creates a new RABuilder.
         * 
         * @param  name           The name of the RA to be constructed.
         * @param  totalDuties    The total number of duties being scheduled.
         * @param  dutiesToAssign The number of duties the to-be constructed RA should be given.
         */     
        public RABuilder(String name, int totalDuties, int dutiesToAssign) {
            this.prefs = new HashMap<Item, Integer>(totalDuties);
            this.iD = new HashSet<Item>(totalDuties);

            this.n = name;
            this.tD = totalDuties;
            this.dta = dutiesToAssign;
        }

        /**
         * Associates the given Item with the given preference value.
         *     
         * @param duty     The Item instance to associate with the preference.
         * @param priority The preference of the given Item instance. Lower means more prefered.
         */
        public void putPreference(Item duty, Integer priority) {
            try {
                ErrorChecker.inBounds("priority", priority, 0, tD);                
            } catch (IllegalArgumentException e) {
                ErrorChecker.printExceptionToLog(e);
            } catch (RuntimeException e) {
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
