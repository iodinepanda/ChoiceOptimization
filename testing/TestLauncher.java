package testing;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

public class TestLauncher {

    @RunWith(Suite.class)
    @Suite.SuiteClasses({
        TestRA.class, TestSchedule.class/*, TestDuty.class, TestGeneration.class*/
    })
    public static class TestSuite { }

    /**
     * Runs JUnit tests and prints verbose information about their results.
     * PrintPosition function and general main structue taken from Josh Hug's 
     * jh61b package. See https://github.com/Berkeley-CS61B/skeleton/tree/master/lib
     * @param args command line args, this function takes none
     */
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestSuite.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println(String.format("Time: %.3f", result.getRunTime() * .001));
        if (result.getFailureCount() > 0) {
            int i = 1;
            System.out.println(String.format("%d tests failed:", result.getFailureCount()));
            for (Failure fail : result.getFailures()) {
                System.out.println(String.format("%d) %s", i++, fail.getTestHeader()));
                Throwable e = fail.getException();
                if (e instanceof AssertionError) {
                    if (e.getMessage() == null) {
                        System.out.println("    Assertion failed");
                    } else {
                        System.out.println(String.format("    %s", e.getMessage()));
                    }
                } else {
                    if (e.getCause() != null) {
                        e = e.getCause();
                    }
                    System.out.println(String.format("    %s", e));
                }
                for (StackTraceElement frame : e.getStackTrace()) {
                    if (frame.getClassName().startsWith("org.junit.")) {
                        continue;
                    }
                    printPosition(frame);
                }
                System.out.println();
            }
        }
        System.out.print(String.format("Ran %d tests.", result.getRunCount()));
        if (result.getFailureCount() == 0) {
            System.out.println(" All passed.");
        } else {
            System.out.println(String.format(" %d failed.", result.getFailureCount()));
        }
    }

    /**
     * Prints frame's position in the file.
     * @param frame the fram to print
     */
    private static void printPosition (StackTraceElement frame) {
        if (frame.isNativeMethod ()) {
            System.out.println(String.format("    at %s.%s (native method)",
                               frame.getClassName(),
                               frame.getMethodName()));
        } else {
            System.out.println(String.format("    at %s.%s:%d (%s)",
                               frame.getClassName(),
                               frame.getMethodName(),
                               frame.getLineNumber(),
                               frame.getFileName()));
        }
    }

}