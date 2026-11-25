import java.util.List;
import java.util.ArrayList;

public class SJFPreemptive {
    public List<String> execute(List<Process> processes, int cs) {
        int t = 0;
        int done = 0;
        Process prev = null;
        List<String> chart = new ArrayList<>();

        while (done < processes.size()) {
            Process next = null;
            for (Process p : processes) {
                if (p.arrivalTime <= t && p.remainingTime > 0) {
                    if (next == null || p.remainingTime < next.remainingTime)
                        next = p;
                }
            }

            if (next == null) {
                chart.add("IDLE");
                t++;
                prev = null;
                continue;
            }
            if (prev != null && prev != next) {
                for (int i = 0; i < cs; i++) {
                    chart.add("CS");
                    t++;
                }

                next = null;
                for (Process p : processes) {
                    if (p.arrivalTime <= t && p.remainingTime > 0) {
                        if (next == null || p.remainingTime < next.remainingTime)
                            next = p;
                    }
                }
            }

            if (next.firstStartTime == -1)
                next.firstStartTime = t;

            chart.add(next.id);
            next.remainingTime--;
            t++;
            prev = next;
            if (next.remainingTime == 0) {
                next.completionTime = t;
                done++;
            }
        }
        for (Process p : processes) {
            p.turnarroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnarroundTime - p.burstTime;
            p.responseTime = p.firstStartTime - p.arrivalTime;
        }
        return chart;
    }
}