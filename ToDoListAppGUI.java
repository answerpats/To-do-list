import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToDoListAppGUI {
    private static final String FILE_NAME = "tasks.txt";
    private static List<Task> tasks = new ArrayList<>();
    private static DefaultTableModel tableModel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListAppGUI::createAndShowGUI);
        loadTasks();
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("To-Do List Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400);
        frame.setLayout(new BorderLayout());

        // Table to display tasks
        String[] columnNames = {"#", "Task", "Status", "Due Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable taskTable = new JTable(tableModel);
        taskTable.setFont(new Font("Arial", Font.PLAIN, 14));
        taskTable.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(taskTable);

        // Load existing tasks into the table
        refreshTaskTable();

        // Buttons
        JButton addButton = new JButton("Add Task");
        JButton markCompleteButton = new JButton("Mark Complete");
        JButton deleteButton = new JButton("Delete Task");
        JButton exitButton = new JButton("Exit");

        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        markCompleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(markCompleteButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exitButton);

        // Action Listeners
        addButton.addActionListener(e -> addTask());
        markCompleteButton.addActionListener(e -> markTaskComplete(taskTable.getSelectedRow()));
        deleteButton.addActionListener(e -> deleteTask(taskTable.getSelectedRow()));
        exitButton.addActionListener(e -> {
            saveTasks();
            JOptionPane.showMessageDialog(frame, "Goodbye!");
            System.exit(0);
        });

        // Layout
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Show GUI
        frame.setVisible(true);
    }

    private static void addTask() {
        String taskName = JOptionPane.showInputDialog(null, "Enter task name:", "Add Task", JOptionPane.PLAIN_MESSAGE);
        if (taskName == null || taskName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Task name cannot be empty.");
            return;
        }

        String dueDateString = JOptionPane.showInputDialog(null, "Enter due date (yyyy-MM-dd):", "Add Task", JOptionPane.PLAIN_MESSAGE);
        if (dueDateString == null || dueDateString.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Due date cannot be empty.");
            return;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dueDate = dateFormat.parse(dueDateString);
            tasks.add(new Task(taskName, false, dueDate));
            refreshTaskTable();
            JOptionPane.showMessageDialog(null, "Task added!");
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid date format. Please use yyyy-MM-dd.");
        }
    }

    private static void markTaskComplete(int index) {
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "Please select a task to mark as complete.");
            return;
        }
        tasks.get(index).setCompleted(true);
        refreshTaskTable();
        JOptionPane.showMessageDialog(null, "Task marked as complete!");
    }

    private static void deleteTask(int index) {
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "Please select a task to delete.");
            return;
        }
        tasks.remove(index);
        refreshTaskTable();
        JOptionPane.showMessageDialog(null, "Task deleted!");
    }

    private static void refreshTaskTable() {
        tableModel.setRowCount(0); // Clear the table
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String dueDate = task.getDueDate() != null ? dateFormat.format(task.getDueDate()) : "No Date";
            tableModel.addRow(new Object[]{i + 1, task.getName(), task.isCompleted() ? "Completed" : "Incomplete", dueDate});
        }
    }

    private static void loadTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                String name = parts[0];
                boolean isCompleted = Boolean.parseBoolean(parts[1]);
                Date dueDate = parts.length > 2 ? dateFormat.parse(parts[2]) : null;
                tasks.add(new Task(name, isCompleted, dueDate));
            }
        } catch (IOException | ParseException e) {
            System.out.println("No existing tasks found. Starting fresh.");
        }
    }

    private static void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Task task : tasks) {
                String dueDate = task.getDueDate() != null ? dateFormat.format(task.getDueDate()) : "";
                writer.write(task.getName() + ";" + task.isCompleted() + ";" + dueDate);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks.");
        }
    }
}

class Task {
    private String name;
    private boolean isCompleted;
    private Date dueDate;

    public Task(String name, boolean isCompleted, Date dueDate) {
        this.name = name;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
    }

    public String getName() {
        return name;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
