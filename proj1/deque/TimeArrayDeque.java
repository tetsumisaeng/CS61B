package deque;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeArrayDeque {
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
        timeGet();
        timeSize();
    }

    public static void timeGet() {
        int[] Ns = {1000, 2000, 4000, 8000, 16000, 32000, 64000};
        double[] times = new double[Ns.length];
        int[] opCounts = Ns;
        for (int i = 0; i < Ns.length; i++) {
            ArrayDeque<Integer> lld = new ArrayDeque<>();
            for (int j = 0; j < Ns[i]; j++) {
                lld.addLast(j);
            }
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < Ns[i]; j++) {
                lld.get(j);
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
            ArrayDeque<Integer>[] llds = new ArrayDeque[Ns[i]];
            for (int j = 0; j < Ns[i]; j++) {
                llds[j] = new ArrayDeque<>();
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
}

