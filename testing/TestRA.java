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
import duty_scheduler.RA.RABuilder;
import duty_scheduler.Duty;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRA {

    private RA test;

    @Before
    public void setUp() {
        RABuilder builder = new RABuilder("Bob", 3, 3);
        builder.putPreference(new Duty(2015, 1, 1), 1);
        builder.putPreference(new Duty(2015, 1, 2), 2);
        builder.putPreference(new Duty(2015, 1, 3), 3);
        test = builder.build();
    }

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(GitletPublicTest.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println(String.format("Ran %d tests.\n%d failed.\n%d succeeded.", 
                                    result.getRunCount(), result.getFailureCount(), 
                                    result.getRunCount() - result.getFailureCount()));
    }

}
