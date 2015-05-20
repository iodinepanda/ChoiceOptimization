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

import java.util.Collection;

public interface Mapping<K extends Chooser, V extends Item> extends Comparable <Mapping<K, V>> {

    public Collection<V> getAssignments(K chooser);
    public Mapping<K, V> mutate();
    public double getCost();
    public int equals(Mapping<K, V> other);
    
    @Override public int compareTo(Mapping<K, V> other);
    @Override public int hashCode();

}