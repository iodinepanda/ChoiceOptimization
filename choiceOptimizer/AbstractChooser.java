package choiceOptimizer;

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

import java.util.HashMap;
import java.util.HashSet;

public abstract class AbstractChooser implements Chooser {

    protected HashMap<Item, Integer> preferences;
    protected String name;
    protected HashSet<Item> invalidItems;

    /**
     * Evaluates whether the given item is a valid assignment.
     * 
     * @param  item A item object to check the validity of
     * @return      true if this Chooser instance can cover this item, false otherwise
     */
    public boolean eligibleItem(Item item) {
        return !invalidItems.contains(item);
    }

    /**
     * Gets the preference value of a item relative to this Chooser instance
     * 
     * @param  item A item object to get the preference of
     * @return The weight of item in this Chooser instance's preferences. Lower values are more prefered.
     */
    public int itemWeight(Item item) {
        Integer toReturn = preferences.get(item);
        return toReturn == null ? Integer.MAX_VALUE : toReturn;
    }

    /**
     * Returns a string representation of this Chooser object
     * 
     * @return The name of the Chooser represented by this instance
     */
    @Override public String toString() {
        return name;
    }

    /**
     * Compares this to another Object for equivalency.
     * 
     * @param  other The other Object to be compared against.
     * @return true if the the other Object is an AbstractChooser and if it has the same name, false otherwise
     */
    @Override public boolean equals(Object other) {
        if (other instanceof AbstractChooser) {
            return name.equals(((AbstractChooser) other).toString());
        } else {
            return false;
        }
    }

    /**
     * Gets an integer representation of this Chooser for hashing.
     *
     * @return the hashCode() of the name of the Chooser represented by this instance
     */
    @Override public int hashCode() {
        return name.hashCode();
    }

}