import java.util.List;

public class Tier3 {
    public static void updateAging(List<Process> tier2Queue, int currentTime, int agingInterval) {

        if (agingInterval <= 0)
            throw new IllegalArgumentException("Aging interval must be greater than zero.");

        if (tier2Queue == null)
            throw new IllegalArgumentException("Tier 2 queue cannot be null.");

        for (Process p : tier2Queue) {
            int waitingTime = Math.max(0, currentTime - p.getTier2EntryTime());

            int agingLevels = waitingTime / agingInterval;
            int newPriority = p.getInitialPriority() - agingLevels;
            newPriority = Math.max(1, newPriority);
            p.setEffectivePriority(newPriority);
        }
    }
}
