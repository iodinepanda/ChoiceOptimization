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

public abstract class AbstractItem implements Item {

    protected String stringRep;

    /**
     * Returns a string representing this Item. 
     * The format is unspecified, but is guarenteed to reflect the item directly.
     * 
     * @return a string representation of this Item.
     */
    @Override public String toString() {
        return stringRep;
    }

    /**
     * Returns a hashcode of this Item.
     * 
     * @return a hashcode of this Item.
     */
    @Override public int hashCode() {
        return stringRep.hashCode();
    }

    /**
     * Compares this Item against another Object for equialency.
     * 
     * @param  other The object to compare this Item against
     * @return       True if they represent the same item, false otherwise or if other is not a Item
     */
    @Override public boolean equals(Object other) {
        if (other instanceof AbstractItem) {
            return stringRep.equals(((AbstractItem) other).stringRep);
        } else {
            return false;
        }
    }

    @Override abstract public int compareTo(Item other);

}
