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
 *     -Java's built in HashMap class for scheduling.
 *     -Java's built in ArrayList class for storing assignments.
 *     -Java's built in Random class for getting random RA's
 *     -Java's built in Set class 
 *     -My static ErrorChecker class for data validation and reporting
 */
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import choiceOptimizer.AbstractMapping;
import choiceOptimizer.Mapping;

/**
 * A class for representing a schedule of RA Duties.
 * 
 * @author Matthew Mussomele
 */
public class Schedule extends AbstractMapping<RA, Duty> {

    private static final double MILLIS_PER_DAY = 86400000.0;
    private static final double ADJACENCY_PENALTY = 2.0;

    /*
     * Constructs a new Schedule from the given ScheduleBuilder
     */
    private Schedule(ScheduleBuilder builder) {
        mappings = new HashMap<RA, ArrayList<Duty>>(builder.map);
        items = builder.d;
        cost = calculateCost();
    }

    /**
     * Creates a mutated defensive copy of this Schedule.
     * 
     * @return a mutated copy of this Schedule
     */
    public Mapping<RA, Duty> mutate() {
        ScheduleBuilder mutator = new ScheduleBuilder(mappings.size(), items);
        RA[] swapping = getTwoAtRandom();
        ArrayList<Duty> firstSwap = new ArrayList<Duty>(getAssignments(swapping[0]));
        ArrayList<Duty> secondSwap = new ArrayList<Duty>(getAssignments(swapping[1]));
        swapDuties(firstSwap, secondSwap);
        for (RA ra : mappings.keySet()) {
            if (ra.equals(swapping[0])) {
                mutator.putAssignmentList(ra, firstSwap);
            } else if (ra.equals(swapping[1])) {
                mutator.putAssignmentList(ra, secondSwap);
            } else {
                mutator.putAssignmentList(ra, new ArrayList<Duty>(getAssignments(ra)));
            }
        }
        return mutator.build();
    }

    /*
     * Takes in two ArrayLists and swaps random elements in place.
     */
    private void swapDuties(ArrayList<Duty> firstSwap, ArrayList<Duty> secondSwap) {
        Random gen = new Random();
        for (int i = 0; i < Math.min(firstSwap.size(), secondSwap.size()); i += 1) {
            if (gen.nextDouble() < Scheduler.MUTATION_CHANCE) {
                Duty temp = firstSwap.get(i);
                firstSwap.set(i, secondSwap.get(i));
                secondSwap.set(i, temp);
            }
        }
    }

    /**
     * Returns a String representation of this schedule. While the information in the String will
     * not change, the exact format is not specified.   
     * 
     * @return A listing of RA's, the cost of their assignments and their assignments. 
     */
    @Override
    public String toString() {
        String result = "";
        for (RA ra : mappings.keySet()) {
            result += ra.toString() + " " + Double.toString(assignmentsCost(ra)) + "\n";
            for (Duty duty : mappings.get(ra)) {
                result += "\t" + duty.toString() + "\n";
            }
            result += "\n";
        }
        return result;
    }

    /**
     * Gets two RA's from this schedule and returns them at random.
     * @return an array holding exactly two distinct RAs from this schedule.
     */
    public RA[] getTwoAtRandom() {
        Random gen = new Random();
        int first = -1;
        int second = -1;
        while (first == second) {
            first = gen.nextInt(mappings.size());
            second = gen.nextInt(mappings.size());
        }
        RA[] toReturn = new RA[2];
        int i = 0;
        for (RA ra : mappings.keySet()) {
            if (i == first) {
                toReturn[0] = ra;
            } else if (i == second) {
                toReturn[1] = ra;
            }   
            i += 1;
        }
        return toReturn;
    }

    /**
     * A class used to construct Schedule instances while allowing immutability.
     */
    public static class ScheduleBuilder {

        private HashMap<RA, ArrayList<Duty>> map;
        private int d;
        private int r;

        /**
         * Constructs a ScheduleBuilder instance.
         * 
         * @param  raCount   The number of RAs that will be scheduled.
         * @param  dutyCount The number of Duty's that will be assigned.
         */
        public ScheduleBuilder(int raCount, int dutyCount) {
            try {
                if (raCount < 2) {
                    throw new IllegalArgumentException("Must have at least 2 RAs.");
                } else if (dutyCount < 1) {
                    throw new IllegalArgumentException("Must have at least 1 Duty.");
                } else {
                    this.map = new HashMap<RA, ArrayList<Duty>>(raCount);
                    this.d = dutyCount;
                    this.r = raCount;
                }
            } catch (IllegalArgumentException e) {
                ErrorChecker.printExceptionToLog(e);
            }
        }

        /**
         * Assigns the given Duty to the given RA in this schedule.
         * 
         * @param ra   The RA to assign the Duty to
         * @param duty The Duty to assign
         */
        public void putAssignment(RA ra, Duty duty) {
            if (!map.containsKey(ra)) {
                map.put(ra, new ArrayList<Duty>());
            } 
            map.get(ra).add(duty);
        }

        /**
         * Assigns all the Duty instances in an ArrayList to the given RA
         * 
         * @param ra     The Ra to assign the Duties to
         * @param duties The list of Duty instances to be assigned. 
         */
        public void putAssignmentList(RA ra, ArrayList<Duty> duties) {
            map.put(ra, duties);
        }

        public boolean doneAssigning(RA ra) {
            ArrayList<Duty> assignedSoFar = map.get(ra);
            return assignedSoFar != null && assignedSoFar.size() == ra.requiredDuties();
        }

        /**
         * Clears all assignments from this ScheduleBuilder
         */
        public void clear() {
            this.map.clear();
        }

        /**
         * Constructs a new Schedule instance, if it is valid.
         * 
         * @return A Schedule instance if the assignments were valid, null otherwise.
         */
        public Schedule build() {
            if (detValid()) {
                return new Schedule(this);
            } else {
                return null;
            }
        }

        /*
         * Evaluates this ScheduleBuilder's validity.
         */
        private boolean detValid() {
            int dutySum = 0;
            Set<RA> keys = map.keySet();
            if (keys.size() < r) {
                return false;
            }
            for (RA ra : map.keySet()) {
                dutySum += map.get(ra).size();
            }
            return dutySum == d;
        }

    }    

    /*
     * Calculates the cost of a this Schedule
     */
    private double calculateCost() {
        double myCost = 0;
        double maxCost = Double.MIN_VALUE;
        int maxDiscrepancy = Integer.MIN_VALUE;
        for (RA ra : mappings.keySet()) {
            double thisCost = assignmentsCost(ra);
            int thisDiscrepancy = Math.abs(ra.requiredDuties() - mappings.get(ra).size());
            if (thisCost > maxCost) {
                maxCost = thisCost;
            }
            if (thisDiscrepancy > maxDiscrepancy) {
                maxDiscrepancy = thisDiscrepancy;
            }
            myCost += thisCost + thisDiscrepancy;
        }
        return myCost + maxCost + maxDiscrepancy;
    }

    /*
     * Calculates the cost of a single RAs assignments in this Schedule.
     */
    private double assignmentsCost(RA ra) {
        ArrayList<Duty> thisSchedule = mappings.get(ra);
        if (thisSchedule == null || thisSchedule.size() == 0) {
            return 0;
        }
        double myCost = 0;
        Duty last = null;
        for (Duty duty : thisSchedule) {
            myCost += ra.itemWeight(duty);
            if (Scheduler.CONSIDER_ADJACENTS) {
                myCost += adjacencyCost(last, duty);
            }
            last = duty;
        }
        return myCost / thisSchedule.size();
    }

    /*
     * Calculates any additional costs incurred by two duties being next to each other.
     */
    private double adjacencyCost(Duty prev, Duty next) {
        if (prev == null) {
            return 0;
        } else {
            long dayDifference = Math.round((next.getTime() - prev.getTime()) / MILLIS_PER_DAY);
            return dayDifference < 2 ? ADJACENCY_PENALTY : 0;
        }
    }

}
