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
 *     -Java's built in ArrayList class for keeping track of the RA's and Duty'
 *     -Java's built in BufferReader class for reading the config file
 *     -Java's built in FileReader class for reading the config file
 *     -Java's built in IOException for handling issues with file reading
 */
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import u4.reshall.dutyScheduler.customErrors.InvalidFileContentsException;
import u4.reshall.dutyScheduler.customErrors.PreferenceConsistencyException;
import u4.reshall.dutyScheduler.customErrors.ImpossiblePreferencesException;
import u4.reshall.dutyScheduler.customErrors.GreedyException;
import u4.reshall.dutyScheduler.customErrors.ErrorChecker;

/**
 * Main class for running the RA duty scheduling algorithm. 
 * 
 * @author Matthew Mussomele
 */
public class Scheduler {

    private static final long NANOS_PER_SEC = 1000000000;
    private static final double MUTATE_DEFAULT = 0.21602435146951632;
    private static final int HRS_PER_DAY = 24;
    private static final int MINS_PER_HR = 60;
    private static final int SECS_PER_MINUTE = 60;
    private static final int MILLIS_PER_SEC = 1000;

    static final int SEED_COUNT;
    static final int EVOLVE_ITERS;
    static final int NUM_RUNS;
    static final int RESOURCE_FACTOR;
    static final int ALLOWED_SEED_ATTEMPTS;
    static final double MUTATION_CHANCE;
    static final boolean ALLOW_ILLEGALS;
    static final boolean ALLOW_GREEDY;
    static final boolean CONSIDER_ADJACENTS;
    static final String DATA_FILE;
    
    private static ArrayList<RA> raList;
    private static ArrayList<Duty> dutyList;
    private static HashMap<String, Duty> dutyLookup;

    /**
     * The following initializer reads all necessary data from the config file. If the file
     * is absent the program uses the default global package constant values. If the file is
     * formatted improperly, then the program logs an exception and exits.
     */
    static {
        int defaultsc = 10;
        int defaultei = 100;
        int defaultnr = 100;
        int defaultrf = 10;
        int defaultasa = 1000;
        double defaultmc = MUTATE_DEFAULT;
        boolean defaultai = false;
        boolean defaultag = false;
        boolean defaultca = true;
        String defaultdf = "data.json";
        BufferedReader reader = null;
        try {
            int lineNumber = 0;
            reader = new BufferedReader(new FileReader("scheduler.config"));
            String line = reader.readLine();
            while (line != null) {
                String[] data = line.split("=");
                String fieldName = data[0];
                String fieldValue = data[1];
                switch (fieldName) {
                    case "SEED_COUNT":
                        defaultsc = Integer.parseInt(fieldValue);
                        if (defaultsc % 2 == 1) {
                            throw new IllegalArgumentException("Must have an even number" 
                                                               + " of Schedules.");
                        }
                        break;
                    case "EVOLVE_ITERS":
                        defaultei = Integer.parseInt(fieldValue);
                        break;
                    case "NUM_RUNS":
                        defaultnr = Integer.parseInt(fieldValue);
                        if (defaultnr <= 0) {
                            throw new IllegalArgumentException("Must run algorithm at least once.");
                        }
                        break;
                    case "RESOURCE_FACTOR":
                        defaultrf = Integer.parseInt(fieldValue);
                        break;
                    case "ALLOWED_SEED_ATTEMPTS":
                        defaultasa = Integer.parseInt(fieldValue);
                        break;
                    case "MUTATION_CHANCE":
                        defaultmc = Double.parseDouble(fieldValue);
                        break;
                    case "ALLOW_ILLEGALS":
                        defaultai = Boolean.parseBoolean(fieldValue);
                        break;
                    case  "ALLOW_GREEDY":
                        defaultag = Boolean.parseBoolean(fieldValue);
                        break;
                    case "CONSIDER_ADJACENTS":
                        defaultca = Boolean.parseBoolean(fieldValue);
                        break;
                    case "DATA_FILE":
                        if (!fieldValue.endsWith(".json")) {
                            throw new InvalidFileContentsException(String.format("Data file must " 
                                    + "be a json file, was a .%s.", fieldValue.split(".")[1]));
                        } else {
                            defaultdf = fieldValue;
                        }
                        break;
                    default:
                        throw new InvalidFileContentsException(String.format("Invalid field name" 
                                    + " %s on line %d.", fieldName, lineNumber));
                }
                lineNumber += 1;
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Using default values.");
        } catch (InvalidFileContentsException e) {
            ErrorChecker.printExceptionToLog(e);
        } catch (IllegalArgumentException e) {
            ErrorChecker.printExceptionToLog(e);
        } finally {
            SEED_COUNT = defaultsc;
            EVOLVE_ITERS = defaultei;
            NUM_RUNS = defaultnr;
            RESOURCE_FACTOR = defaultrf;
            ALLOWED_SEED_ATTEMPTS = defaultasa;
            MUTATION_CHANCE = defaultmc;
            ALLOW_ILLEGALS = defaultai;
            ALLOW_GREEDY = defaultag;
            CONSIDER_ADJACENTS = defaultca;
            DATA_FILE = defaultdf;
            dutyList = new ArrayList<Duty>();
            raList = new ArrayList<RA>();
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.out.println("Could not close config reader.");
            }
        }
    }

    private Scheduler() {
        throw new AssertionError();
    }

    private static void parseData() {
        JSONObject data = new JSONObject(new String(readFile()));
        createDutyList(data.getJSONArray("dates"));
        dutyLookup = new HashMap<String, Duty>(dutyList.size());
        for (Duty duty : dutyList) {
            dutyLookup.put(duty.toString(), duty);
        }
        createRAList(data.getJSONArray("residentAssistants"));
        try {
            ErrorChecker.evalPrefs(raList, dutyList);
            ErrorChecker.checkConsistency();
            if (!ALLOW_ILLEGALS) {
                ErrorChecker.checkImpossible();
            } 
            if (!ALLOW_GREEDY) {
                ErrorChecker.checkGreedy(raList);
            }
        } catch (PreferenceConsistencyException e) {
            ErrorChecker.printExceptionToLog(e);
        } catch (ImpossiblePreferencesException e) {
            ErrorChecker.printExceptionToLog(e);
        } catch (GreedyException e) {
            ErrorChecker.printExceptionToLog(e);
        } catch (InvalidFileContentsException e) {
            ErrorChecker.printExceptionToLog(e);
        }
    }

    private static String readFile() {
        BufferedReader reader = null;
        String result = "";
        try {
            File dataFile = new File(DATA_FILE);
            if (!dataFile.exists()) {
                throw new IOException("Missing data file: " + DATA_FILE);
            }
            reader = new BufferedReader(new FileReader(dataFile));
            String line = reader.readLine();
            while (line != null) {
                result += line;
                line = reader.readLine();
            }
        } catch (IOException e) {
            ErrorChecker.printExceptionToLog(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Could not close data reader.");
            }
        }
        return result;
    }

    private static void createRAList(JSONArray jsonRAs) {
        try {
            for (int i = 0; i < jsonRAs.length(); i += 1) {
                JSONObject ra = jsonRAs.getJSONObject(i);
                RA.RABuilder builder = new RA.RABuilder(ra.getString("name"), dutyList.size(), 
                                                  ra.getInt("duties"));
                JSONArray prefs = ra.getJSONArray("preferences");
                for (int j = 0; j < prefs.length(); j += 1) {
                    JSONObject pref = prefs.getJSONObject(j);
                    builder.putPreference(dutyLookup.get(pref.getString("duty")), 
                                          pref.getInt("prefVal"));
                }
                raList.add(builder.build());
            }
        } catch (JSONException e) {
            ErrorChecker.printExceptionToLog(e);
        }
    }

    private static void createDutyList(JSONArray jsonDuties) {
        try {
            for (int i = 0; i < jsonDuties.length(); i += 1) {
                JSONObject duty = jsonDuties.getJSONObject(i);
                dutyList.add(new Duty(duty.getInt("year"), duty.getInt("month"), 
                                      duty.getInt("day")));
            }
        } catch (JSONException e) {
            ErrorChecker.printExceptionToLog(e);
        }
    }

    private static Schedule run() {
        Schedule best = null;
        Schedule localBest = null;
        Generation thisGen = null;
        for (int i = 0; i < NUM_RUNS; i += 1) {
            thisGen = new Generation();
            thisGen.seed(raList, dutyList);
            localBest = thisGen.evolve();
            if (best == null || localBest.getCost() < best.getCost()) {
                best = localBest;
            }
        }
        return best;
    }

    private static String runTime(long nanos) {
        long minutes = TimeUnit.NANOSECONDS.toMinutes(nanos) 
                        - (TimeUnit.NANOSECONDS.toHours(nanos) * MINS_PER_HR);
        long seconds = TimeUnit.NANOSECONDS.toSeconds(nanos) 
                        - (TimeUnit.NANOSECONDS.toMinutes(nanos) * SECS_PER_MINUTE);
        long millis = TimeUnit.NANOSECONDS.toMillis(nanos)
                        - (TimeUnit.NANOSECONDS.toSeconds(nanos) * MILLIS_PER_SEC);
        return String.format("Time Elapsed: %d minutes, %d seconds, %d milliseconds", 
                             minutes, seconds, millis);
    }

    private static void printResults(Schedule best, String runTimeReport) {
        String resultsFile = "schedule_" 
                        + (new SimpleDateFormat("MM-dd-yyyy-hh:mm")).format(new Date()) + ".sched";
        PrintWriter out = null;
        try {
            out = new PrintWriter(resultsFile);
            out.println(runTimeReport);
            out.println("Duty Assignments:\n\n");
            out.println(best.toString());
        } catch (IOException e) {
            ErrorChecker.printExceptionToLog(e);
        } finally {
            out.close();
        }
    }

    public static void main(String[] args) {
        try {
            long timeElapsed = System.nanoTime();
            parseData();
            Schedule best = run();
            printResults(best, runTime(System.nanoTime() - timeElapsed));
        } catch (Exception e) {
            ErrorChecker.printExceptionToLog(e);
        }
    }

}
