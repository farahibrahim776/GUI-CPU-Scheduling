public class Process {
    String id;
    int remainingTime;
    int burstTime;
    int arrivalTime;
    int waitingTime;
    int turnarroundTime;
    int completionTime;
    int priority;
    int responseTime;
    int firstStartTime = -1;
    int initialPriority;
    int effectivePriority;
    int tier2EntryTime;

    public Process(String id,int arrivalTime,int burstTime,int priority){
        this.id=id;
        this.arrivalTime=arrivalTime;
        this.burstTime=burstTime;
        this.remainingTime=burstTime;
        this.priority=priority;
        this.initialPriority = priority;
        this.effectivePriority = priority;
        this.tier2EntryTime = -1;
    }
    public int getInitialPriority() {
        return initialPriority;
    }

    public int getEffectivePriority() {
        return effectivePriority;
    }

    public void setEffectivePriority(int effectivePriority) {
        this.effectivePriority = effectivePriority;
    }

    public int getTier2EntryTime() {
        return tier2EntryTime;
    }

    public void enterTier2(int currentTime) {
        this.tier2EntryTime = currentTime;
        this.effectivePriority = this.initialPriority;
    }

    public String toString() {
        return id + " (remaining: " + remainingTime + ", Priority: " + effectivePriority + ")";
    }
}
