import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame {

    private DefaultTableModel model = new DefaultTableModel(new String[]{"Process", "Arrival", "Burst", "Priority"}, 0) {
        public boolean isCellEditable(int row, int col) {
            return col != 0;
        }
    };
    private JTable table = new JTable(model);
    private JTextField quantumField = new JTextField("3", 4);
    private JTextField csField = new JTextField("0", 4);
    private JTextField agingField = new JTextField("4", 4);
    private JComboBox<String> algoBox = new JComboBox<>(new String[]{"ACS", "SJF (Preemptive)"});
    private JTextArea output = new JTextArea();
    private GanttPanel gantt = new GanttPanel();

    public GUI() {
        super("CPU Scheduler Simulator");

        model.addRow(new Object[]{"P1", 0, 4, 2});
        model.addRow(new Object[]{"P2", 1, 7, 3});
        model.addRow(new Object[]{"P3", 3, 2, 1});

        JButton addBtn = new JButton("Add Process");
        addBtn.addActionListener(e -> model.addRow(new Object[]{"P" + (model.getRowCount() + 1), 0, 1, 1}));

        JButton removeBtn = new JButton("Remove Last");
        removeBtn.addActionListener(e -> {
            if (model.getRowCount() > 0)
                model.removeRow(model.getRowCount() - 1);
        });

        JButton runBtn = new JButton("Run");
        runBtn.addActionListener(e -> run());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Algorithm:"));
        top.add(algoBox);
        top.add(new JLabel("Quantum:"));
        top.add(quantumField);
        top.add(new JLabel("Context switch:"));
        top.add(csField);
        top.add(new JLabel("Aging interval:"));
        top.add(agingField);
        top.add(addBtn);
        top.add(removeBtn);
        top.add(runBtn);

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Processes"));
        JScrollPane ganttScroll = new JScrollPane(gantt);
        ganttScroll.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        JScrollPane outputScroll = new JScrollPane(output);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Output"));

        JPanel center = new JPanel(new GridLayout(3, 1));
        center.add(tableScroll);
        center.add(ganttScroll);
        center.add(outputScroll);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        setSize(1000, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void run() {
        try {
            if (table.isEditing())
                table.getCellEditor().stopCellEditing();

            int quantum = Integer.parseInt(quantumField.getText().trim());
            int cs = Integer.parseInt(csField.getText().trim());
            int aging = Integer.parseInt(agingField.getText().trim());

            if (model.getRowCount() == 0)
                throw new IllegalArgumentException("Add at least one process.");
            if (quantum <= 0 || aging <= 0 || cs < 0)
                throw new IllegalArgumentException("Quantum and aging interval must be > 0, context switch must be >= 0.");

            List<Process> list = new ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                String id = model.getValueAt(i, 0).toString();
                int arrival = Integer.parseInt(model.getValueAt(i, 1).toString().trim());
                int burst = Integer.parseInt(model.getValueAt(i, 2).toString().trim());
                int priority = Integer.parseInt(model.getValueAt(i, 3).toString().trim());
                if (arrival < 0 || burst <= 0 || priority < 1)
                    throw new IllegalArgumentException("Arrival >= 0, burst > 0, priority >= 1 (in " + id + ").");
                list.add(new Process(id, arrival, burst, priority));
            }

            String log = "";
            List<String> timeline;
            if (algoBox.getSelectedIndex() == 0) {
                ACSSimulator sim = new ACSSimulator(list, quantum, cs, aging);
                sim.runSimulation();
                log = sim.getLog();
                timeline = sim.getTimeline();
            } else {
                timeline = new SJFPreemptive().execute(list, cs);
            }

            output.setText(log + results(list));
            output.setCaretPosition(0);
            gantt.setTimeline(timeline);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter whole numbers only.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String results(List<Process> list) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append(String.format("%-9s%-9s%-7s%-8s%-12s%-9s%-9s%n",
                "Process", "Arrival", "Burst", "Finish", "Turnaround", "Waiting", "Response"));
        double totalWT = 0, totalTAT = 0;
        for (Process p : list) {
            sb.append(String.format("%-9s%-9d%-7d%-8d%-12d%-9d%-9d%n", p.id, p.arrivalTime, p.burstTime,
                    p.completionTime, p.turnarroundTime, p.waitingTime, p.responseTime));
            totalWT += p.waitingTime;
            totalTAT += p.turnarroundTime;
        }
        sb.append(String.format("Average Turnaround Time: %.2f%nAverage Waiting Time:     %.2f%n",
                totalTAT / list.size(), totalWT / list.size()));
        return sb.toString();
    }

    static class GanttPanel extends JPanel {
        private static final int W = 30;
        private List<String> timeline = new ArrayList<>();

        void setTimeline(List<String> timeline) {
            this.timeline = timeline;
            setPreferredSize(new Dimension(timeline.size() * W + 30, 80));
            revalidate();
            repaint();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int i = 0; i < timeline.size(); i++) {
                String s = timeline.get(i);
                Color c;
                if (s.equals("CS"))
                    c = Color.GRAY;
                else if (s.equals("Idle"))
                    c = Color.WHITE;
                else
                    c = Color.getHSBColor((Integer.parseInt(s.substring(1)) * 0.17f) % 1f, 0.45f, 1f);

                int x = 10 + i * W;
                g.setColor(c);
                g.fillRect(x, 15, W, 35);
                g.setColor(Color.BLACK);
                g.drawRect(x, 15, W, 35);
                g.drawString(s.equals("Idle") ? "-" : s, x + 6, 38);
                g.drawString(String.valueOf(i), x, 68);
            }
            if (!timeline.isEmpty())
                g.drawString(String.valueOf(timeline.size()), 10 + timeline.size() * W, 68);
        }
    }
}