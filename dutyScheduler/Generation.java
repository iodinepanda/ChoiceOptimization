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
 *     -Java's built in ArrayList class for seeding
 *     -Java's built in TreeSet class for storing Schedule permuations.
 *     -Java's built in static Collections for Duty shuffling
 *     -Java's built in RuntimeException for when seeding seems impossible
 *     -Java's built in Collection class for constructing
 */
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Collections;
import java.util.Set;
import java.util.Collection;
import choiceOptimizer.Population;
import choiceOptimizer.Chooser;
import choiceOptimizer.Item;

/**
 * A class used to represent a generation of RA Duty Schedules for use in the genetic algorithm.
 *
 * @author Matthew Mussomele
 */
public class Generation<C extends Chooser, I extends Item> implements Population<C, I> {

    TreeSet<Schedule> schedules;
    ArrayList<C> rList;
    ArrayList<I> dList;

    {
        schedules = new TreeSet<Schedule>();
        rList = new ArrayList<C>();
        dList = new ArrayList<I>();
    }

    /**
     * Constructs a new, empty Generation of Schedules
     */
    public Generation() {
    }

    /**
     * Constructs a new Generation with the same Schedules as any Collection of Schedules.
     * 
     * @param  other A Collection of Schedules
     */
    public Generation(Collection<Schedule> other) {
        schedules.addAll(other);
    }
    
    /**
     * Seeds this Generation with randomly generated valid Schedules.
     * 
     * @param raList The list of RA's to put in the Schedules
     * @param duties The list of Duty's to assign to the RA's
     */
    public void seed(Collection<C> raList, Collection<I> duties) {
        rList = new ArrayList<C>(raList);
        dList = new ArrayList<I>(duties);
        schedules.clear();
        int attempts = 0;

        //Generate the need number of seed Schedules
        for (int i = 0; i < Scheduler.SEED_COUNT; i += 1) {
            //get another viable Schedule
            Schedule next = getNextSeed(rList, new ArrayList<I>(dList));
            if (next == null) { //if it was not a good schedule, we need to try again
                i -= 1;
                attempts += 1;
                if (attempts > Scheduler.ALLOWED_SEED_ATTEMPTS) {
                    Exception e = new RuntimeException("Seeding the generations timed out."
                            + " Check your preferences.");
                    ErrorChecker.printExceptionToLog(e);
                }
            } else { //otherwise we can add it and move on
                schedules.add(next);
                attempts = 0;
            }
        }
    }

    /*
     * Returns either a valid random scheduling or null if this iteration was impossible.
     */
    private Schedule getNextSeed(ArrayList<C> raList, ArrayList<I> duties) {
        Schedule.ScheduleBuilder seedBuilder = new Schedule.ScheduleBuilder(raList.size(),
                                                                            duties.size());
        Collections.shuffle(duties);
        int doneCount = 0;
        try {
            while (duties.size() > 0) { //while there are unassigned duties
                //if all RAs are scheduled but there are duties left, then the json file was invalid
                if (doneCount == raList.size()) { 
                    throw new RuntimeException("The sum of the required duties per RA"
                                                        +  " does not equal the total duty count.");
                }
                doneCount = 0;
                for (C ra : raList) { //loop through the RAs, assigning duties one by one
                    if (seedBuilder.doneAssigning((RA) ra)) {
                        doneCount += 1;
                        continue;
                    } else {
                        I firstEligible = getFirstEligible(ra, duties);
                        if (firstEligible == null) { 
                            return null; //no elligible duties left means invalid schedule
                        } else {
                            seedBuilder.putAssignment((RA) ra, (Duty) firstEligible);
                            duties.remove(firstEligible);
                        }
                    }
                }
            }
            return seedBuilder.build();
        } catch (RuntimeException e) {
            ErrorChecker.printExceptionToLog(e);
        }
        return null;
    }

    private I getFirstEligible(C ra, ArrayList<I> duties) {
        for (I duty : duties) {
            if (ra.eligibleItem(duty) || Scheduler.ALLOW_ILLEGALS) {
                return duty;
            }
        }
        return null;
    }

    /**
     * Evolves this Generation, iteratively improving the cost of its Schedules
     * 
     * @return The best Schedule created thus far.
     */
    public Schedule evolve() {
        if (Scheduler.EVOLVE_ITERS < 1) {
            throw new IllegalArgumentException("Must evolve the schedule generation at least once");
        } else {
            for (int i = 0; i < Scheduler.EVOLVE_ITERS; i += 1) {
                this.step();
            }
        }
        return schedules.first();
    }

    /*
     * Gets the right-middle element from the Generations sorted list of Schedules.
     */
    private Schedule getMiddle() {
        int i = 0;
        for (Schedule s : schedules) {
            if (i == schedules.size() / 2) {
                return s;
            }
            i += 1;
        }
        return null;
    }

    /*
     * Steps the evolution of this Schedule one iteration further.
     */
    private void step() {
        Schedule middleSchedule = getMiddle();
        Set<Schedule> removing = new TreeSet<Schedule>(schedules.tailSet(middleSchedule));
        for (Schedule toRemove : removing) {
            schedules.remove(toRemove);
        }
        ArrayList<Schedule> babySchedules = new ArrayList<Schedule>(schedules.size());
        for (Schedule s : schedules) {         
            Schedule next = (Schedule) s.mutate();
            while (next == null || schedules.contains(next) || babySchedules.contains(next)) {
                next = getNextSeed(rList, new ArrayList<I>(dList));
            }
            babySchedules.add(next);
        }
        schedules.addAll(babySchedules);
    }

}
