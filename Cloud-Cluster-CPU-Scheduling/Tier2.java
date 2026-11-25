import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;

public class Tier2 {
    private PriorityQueue<Process> queue;

    public Tier2() {
      queue = new PriorityQueue<>((p1, p2) -> Integer.compare(p1.getEffectivePriority(), p2.getEffectivePriority())
        );
    }
    public void addProcess(Process p, int currentTime) {
        p.enterTier2(currentTime);
        queue.add(p);
    }

    public void updateAging(int currentTime, int agingInterval) {
        List<Process> waitingProcesses = new ArrayList<>(queue);
        Tier3.updateAging(waitingProcesses, currentTime, agingInterval);
        queue.clear();
        queue.addAll(waitingProcesses);
    }

    public Process getNextProcess() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
    
    public List<Process> getProcesses() {
        return new ArrayList<>(queue);
    }
    
    public String snapshot() {
        if (queue.isEmpty()) return "[Empty]";
        List<Process> list = new ArrayList<>(queue);
        list.sort((a, b) -> Integer.compare(a.getTier2EntryTime(), b.getTier2EntryTime()));
        return list.toString();
    }

}
