import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import java.util.Locale;
public class Main {
    public static void main(String[] args) {

        Locale.setDefault(Locale.US);
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
//        Scanner sc = new Scanner(System.in);
//
//        System.out.print("Number of processes: ");
//        int n = sc.nextInt();
//
//        List<Process> processes = new ArrayList<>();
//
//        for (int i = 0; i < n; i++) {
//            String id = "P" + (i + 1);
//            System.out.print(id + " -> arrival: ");
//            int arrival = sc.nextInt();
//            System.out.print(id + " -> burst: ");
//            int burst = sc.nextInt();
//            System.out.print(id + " -> priority: ");
//            int priority = sc.nextInt();
//            processes.add(new Process(id, arrival, burst, priority));
//        }
//
//        System.out.print("Quantum: ");
//        int quantum = sc.nextInt();
//        System.out.print("Context-switch: ");
//        int cs = sc.nextInt();
//        System.out.print("Aging-interval: ");
//        int agingInterval = sc.nextInt();
//        while(agingInterval == 0){
//            System.out.print("Aging-interval should be greater than zero!");
//            System.out.print("Aging-interval: ");
//            agingInterval = sc.nextInt();
//        }
//
//        System.out.println("\n===== ACS (Q=" + quantum + ", CS=" + cs + ", aging=" + agingInterval + ") =====");
//
//        ACSSimulator simulator = new ACSSimulator(processes,quantum,cs,agingInterval);
//
//        simulator.runSimulation();
//        simulator.printResults();
//
//        sc.close();
    }
}