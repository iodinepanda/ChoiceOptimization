package testing;

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

import duty_scheduler.RA;
import duty_scheduler.Duty;
import duty_scheduler.Schedule;
import duty_scheduler.Schedule.ScheduleBuilder;

import java.util.ArrayList;

/**
 * Class containing utiliy methods for testing purposes.
 *
 * @author Matthew Mussomele
 */
public class TestUtils {

    private static final int FIRST = 0;

    /**
     * Generates all possible schedules using the two list of RAs and Duty's.
     * Uses the Counting QuickPerm Algorithm - http://www.quickperm.org/quickperm.html
     * 
     * @param  raList   The RAs that need duties assigned
     * @param  dutyList The Duty's to assign
     * @return          The cost of the best possible schedule with these preferences.
     */
    public static double getOptimalCost(ArrayList<RA> raList, ArrayList<Duty> dutyList) {
        double bestCost = Double.MAX_VALUE;
        int n = dutyList.size();
        int[] p = new int[n];
        int i = 1;
        while (i < n) {
            if (p[i] < i) {
                int j = ((i % 2) == 0) ? 0 : p[i];
                swap(dutyList, i, j);
                Schedule permutation = buildSchedule(raList, new ArrayList<Duty>(dutyList));
                bestCost = (permutation != null && permutation.getCost() < bestCost) ? permutation.getCost() : bestCost;
                p[i] += 1;
                i = 1;
            } else {
                p[i] = 0;
                i += 1;
            }
        }
        return bestCost;
    }

    /**
     * Swaps the Duty instances at indices i and j
     * 
     * @param duties The list to swap in
     * @param i      The index of the first Duty
     * @param j      The index of the second Duty
     */
    private static void swap(ArrayList<Duty> duties, int i, int j) {
        Duty temp = duties.get(i);
        duties.set(i, duties.get(j));
        duties.set(j, temp);
    }

    /**
     * Constructs and returns a schedule corresponding to the given ArrayList of duties.
     * The ArrayList contains a permutation of the master Duty list, and they are assigned
     * in order to a list of RA instances that never changes order. By using this method on
     * all permutations of the master Duty list, we generate all possible Schedule instances.
     *     
     * @param  raList   The list of RA instances
     * @param  dutyList The permuted list of Duty instances
     * @return          A Schedule build using the two lists
     */
    private static Schedule buildSchedule(ArrayList<RA> raList, ArrayList<Duty> dutyList) {
        ScheduleBuilder builder = new ScheduleBuilder(raList.size(), dutyList.size());
        for (RA ra : raList) {
            for (int i = 0; i < ra.requiredDuties(); i += 1) {
                builder.putAssignment(ra, dutyList.remove(FIRST));
            }
        }
        return builder.build();
    }

}
