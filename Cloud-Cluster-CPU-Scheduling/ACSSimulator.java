import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ACSSimulator {
    private int currentTime = 0;
    private int contextSwitch;
    private int agingInterval;

    private List<Process> allProcesses;
    private List<Process> admitted = new ArrayList<>();
    private Process lastProcess;
    private List<String> timeline = new ArrayList<>();
    private StringBuilder log = new StringBuilder();
    private Tier1 tier1;
    private Tier2 tier2 = new Tier2();
    private String pad = "               ";

    public ACSSimulator(List<Process> processes, int quantum, int contextSwitch, int agingInterval) {
        this.allProcesses = processes;
        this.tier1 = new Tier1(quantum);
        this.contextSwitch = contextSwitch;
        this.agingInterval = agingInterval;
    }
    public String getLog() {
        return log.toString();
    }

    public List<String> getTimeline() {
        return timeline;
    }

    private void addArrival() {
        for (Process p : allProcesses) {
            if (p.arrivalTime <= currentTime && !admitted.contains(p)) {
                tier1.addProcess(p);
                admitted.add(p);
            }
        }
    }

    private String snapshots() {
        return "Tier 1: " + tier1.snapshot() + "\n" + pad + "Tier 2: " + tier2.snapshot();
    }
    private void timeUnitPass(String label, String text) {
        if (text == null)
            text = snapshots();

        String last = timeline.isEmpty() ? "" : timeline.get(timeline.size() - 1);
        String name = (label.equals(last) || label.equals("CS")) ? "" : label;

        log.append(String.format("%-6d%-9s%s%n", currentTime, name, text));

        timeline.add(label);
        currentTime++;
        addArrival();
    }

    public void runSimulation() {
        log.append(String.format("%-6s%-9s%s%n", "Time", "Process", "Tier"));
        int completed = 0;
        addArrival();

        while (completed < allProcesses.size()) {
            if (tier1.isEmpty() && !tier2.isEmpty()) {
                tier2.updateAging(currentTime, agingInterval);
            }

            List<Process> waiting = tier2.getProcesses();
            waiting.sort(Comparator.comparingInt(Process::getTier2EntryTime));

            if (waiting.size() > 1 && waiting.stream().anyMatch(p -> p.getEffectivePriority() != p.getInitialPriority())) {
                for (Process w : waiting) {
                    int waited = currentTime - w.getTier2EntryTime();
                    int levels = waited / agingInterval;
                    String line = w.id + ": Waited from T=" + w.getTier2EntryTime() + " to T=" + currentTime
                            + " (" + waited + " units). ";
                    if (waited > 0) {
                        line += "Aging: floor(" + waited + "/" + agingInterval + ") = " + levels + ". ";
                        line += "Effective Priority = max(1, " + w.getInitialPriority() + " - " + levels + ") = ";
                    } else {
                        line += "Effective Priority = ";
                    }
                    log.append(line).append(w.getEffectivePriority()).append("\n");
                }
            }

            String before = snapshots();

            Process p = null;
            if (!tier1.isEmpty())
                p = tier1.getNextProcess();
            else if (!tier2.isEmpty())
                p = tier2.getNextProcess();

            if (p == null) {
                lastProcess = null;
                timeUnitPass("Idle", null);
                continue;
            }

            boolean switched = false;
            if (lastProcess != null && lastProcess != p && contextSwitch > 0) {
                String note = "Context switch " + (lastProcess.remainingTime > 0
                        ? "saves " + lastProcess.id + " and loads " : "loads ") + p.id;
                for (int i = 0; i < contextSwitch; i++)
                    timeUnitPass("CS", i == 0 ? note + "\n" + pad + before : null);
                switched = true;
            }

            if (p.firstStartTime == -1)
                p.firstStartTime = currentTime;

            int slice;
            if (p.getTier2EntryTime() == -1)
                slice = Math.min(tier1.getQuantum(), p.remainingTime);
            else
                slice = p.remainingTime;

            for (int i = 0; i < slice; i++) {
                timeUnitPass(p.id, (i == 0 && !switched) ? before : null);
                p.remainingTime--;
            }

            lastProcess = p;
            if (p.remainingTime == 0) {
                p.completionTime = currentTime;
                completed++;
            } else {
                tier2.addProcess(p, currentTime);
            }

            log.append(String.format("%-6d%-9s%s%n", currentTime, "", snapshots()));
        }

        for (Process p : allProcesses) {
            p.turnarroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnarroundTime - p.burstTime;
            p.responseTime = p.firstStartTime - p.arrivalTime;
        }
    }
}
