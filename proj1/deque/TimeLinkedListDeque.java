package deque;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeLinkedListDeque {
    private static void printTimingTable(int[] Ns, double[] times, int[] opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.length; i += 1) {
            int N = Ns[i];
            double time = times[i];
            int opCount = opCounts[i];
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAddRemove();
        timeSize();
        timeForEachLoop();
    }

    public static void timeAddRemove() {
        int[] Ns = {1000000, 2000000, 4000000, 8000000, 16000000, 32000000, 64000000};
        double[] times = new double[Ns.length];
        int[] opCounts = Ns;
        for (int i = 0; i < Ns.length; i++) {
            LinkedListDeque<Integer> lld = new LinkedListDeque<>();
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < Ns[i]; j++) {
                int operationNumber = StdRandom.uniform(0, 4);
                if (operationNumber == 0) {
                    // addFirst
                    lld.addFirst(j);
                }
                if (operationNumber == 1) {
                    // addLast
                    lld.addLast(j);
                }
                if (operationNumber == 2) {
                    // removeFirst
                    lld.removeFirst();
                }
                if (operationNumber == 3) {
                    // removeLast
                    lld.removeLast();
                }
            }
            double timeInSeconds = sw.elapsedTime();
            times[i] = timeInSeconds;
        }
        printTimingTable(Ns, times, opCounts);
    }

    public static void timeSize() {
        int[] Ns = {100, 200, 400, 800, 1600, 3200, 6400};
        double[] times = new double[Ns.length];
        int[] opCounts = Ns;
        for (int i = 0; i < Ns.length; i++) {
            LinkedListDeque<Integer>[] llds = new LinkedListDeque[Ns[i]];
            for (int j = 0; j < Ns[i]; j++) {
                llds[j] = new LinkedListDeque<>();
                for (Integer k = 0; k < j; k++) {
                    llds[j].addLast(k);
                }
            }
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < Ns[i]; j++) {
                llds[j].size();
            }
            double timeInSeconds = sw.elapsedTime();
            times[i] = timeInSeconds;
        }
        printTimingTable(Ns, times, opCounts);
    }

    public static void timeForEachLoop() {
        int[] Ns = {100000, 200000, 400000, 800000, 1600000, 3200000, 6400000};
        double[] times = new double[Ns.length];
        int[] opCounts = Ns;
        for (int i = 0; i < Ns.length; i++) {
            LinkedListDeque<Integer> lld = new LinkedListDeque<>();
            for (int j = 0; j < Ns[i]; j++) {
                lld.addLast(j);
            }
            Stopwatch sw = new Stopwatch();
            for (int j : lld) {
                j++;
            }
            double timeInSeconds = sw.elapsedTime();
            times[i] = timeInSeconds;
        }
        printTimingTable(Ns, times, opCounts);
    }

}
