package ui;

import dao.StudentDAO;
import models.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainWindow extends JFrame {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTable searchTable;
    private DefaultTableModel searchTableModel;
    private StudentDAO studentDAO;
    private JTabbedPane tabbedPane;
    private JPanel statisticsPanel;

    public MainWindow() {
        setTitle("SmartStudent - Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        studentDAO = new StudentDAO();

        // Color scheme
        Color primaryColor = new Color(41, 128, 185);
        Color backgroundColor = new Color(236, 240, 241);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(backgroundColor);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(1000, 50));
        JLabel titleLabel = new JLabel("Student Management Dashboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();

        // View Students Tab
        tabbedPane.addTab("View Students", createViewPanel());

        // Add Student Tab
        tabbedPane.addTab("Add Student", createAddPanel());

        // Search Tab
        tabbedPane.addTab("Search", createSearchPanel());

        // Statistics Tab
        tabbedPane.addTab("Statistics", createStatisticsPanel());

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createViewPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));

        // Table
        String[] columns = {"ID", "Name", "Roll No", "Department", "Email", "Phone", "Marks"};
        tableModel = new DefaultTableModel(columns, 0);
        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Arial", Font.PLAIN, 11));
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        studentTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(studentTable);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton refreshButton = createButton("Refresh");
        JButton updateButton = createButton("Update");
        JButton deleteButton = createButton("Delete");

        refreshButton.addActionListener(e -> loadStudents());
        updateButton.addActionListener(e -> openUpdateDialog());
        deleteButton.addActionListener(e -> deleteSelectedStudent());

        buttonPanel.add(refreshButton);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(updateButton);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(deleteButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadStudents();
        return panel;
    }

    private JPanel createAddPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(236, 240, 241));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Labels and Fields
        JTextField nameField = new JTextField(20);
        JTextField rollNoField = new JTextField(20);
        JTextField departmentField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField marksField = new JTextField(20);

        addFormRow(panel, gbc, "Name:", nameField, 0);
        addFormRow(panel, gbc, "Roll No:", rollNoField, 1);
        addFormRow(panel, gbc, "Department:", departmentField, 2);
        addFormRow(panel, gbc, "Email:", emailField, 3);
        addFormRow(panel, gbc, "Phone:", phoneField, 4);
        addFormRow(panel, gbc, "Marks:", marksField, 5);

        // Add Button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JButton addButton = createButton("Add Student");
        addButton.addActionListener(e -> {
            try {
                Student student = new Student(
                    nameField.getText(),
                    rollNoField.getText(),
                    departmentField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    Double.parseDouble(marksField.getText())
                );
                if (studentDAO.addStudent(student)) {
                    JOptionPane.showMessageDialog(panel, "Student added successfully!");
                    nameField.setText("");
                    rollNoField.setText("");
                    departmentField.setText("");
                    emailField.setText("");
                    phoneField.setText("");
                    marksField.setText("");
                    loadStudents();
                    refreshStatistics();
                } else {
                    JOptionPane.showMessageDialog(panel, "Error adding student!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Marks must be a number!");
            }
        });
        panel.add(addButton, gbc);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));

        // Search Options Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(236, 240, 241));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField searchField = new JTextField(20);
        JComboBox<String> searchType = new JComboBox<>(new String[]{"Name", "Department", "Roll No"});
        JButton searchButton = createButton("Search");
        JButton refreshAllButton = createButton("Show All");

        searchButton.addActionListener(e -> {
            String query = searchField.getText();
            String type = (String) searchType.getSelectedItem();
            List<Student> results = null;

            if (type.equals("Name")) {
                results = studentDAO.searchByName(query);
            } else if (type.equals("Department")) {
                results = studentDAO.searchByDepartment(query);
            } else if (type.equals("Roll No")) {
                Student student = studentDAO.searchByRollNo(query);
                if (student != null) {
                    results = new java.util.ArrayList<>();
                    results.add(student);
                }
            }

            displaySearchResults(results);
        });

        refreshAllButton.addActionListener(e -> {
            searchField.setText("");
            displaySearchResults(studentDAO.getAllStudents());
        });

        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(searchType);
        searchPanel.add(new JLabel("  Query:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(refreshAllButton);

        // Results Table
        String[] columns = {"ID", "Name", "Roll No", "Department", "Email", "Phone", "Marks"};
        searchTableModel = new DefaultTableModel(columns, 0);
        searchTable = new JTable(searchTableModel);
        searchTable.setFont(new Font("Arial", Font.PLAIN, 11));
        searchTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        searchTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(searchTable);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatisticsPanel() {
        statisticsPanel = new JPanel();
        statisticsPanel.setLayout(new GridBagLayout());
        statisticsPanel.setBackground(new Color(236, 240, 241));
        
        updateStatisticsPanel();
        
        return statisticsPanel;
    }

    private void updateStatisticsPanel() {
        statisticsPanel.removeAll();
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        Color statColor = new Color(41, 128, 185);

        // Total Students
        gbc.gridx = 0;
        gbc.gridy = 0;
        addStatCard(statisticsPanel, gbc, "Total Students", String.valueOf(studentDAO.getTotalStudents()), statColor);

        // Highest Marks
        gbc.gridx = 1;
        addStatCard(statisticsPanel, gbc, "Highest Marks", String.format("%.2f", studentDAO.getHighestMarks()), statColor);

        // Lowest Marks
        gbc.gridx = 2;
        addStatCard(statisticsPanel, gbc, "Lowest Marks", String.format("%.2f", studentDAO.getLowestMarks()), statColor);

        // Average Marks
        gbc.gridx = 0;
        gbc.gridy = 1;
        addStatCard(statisticsPanel, gbc, "Average Marks", String.format("%.2f", studentDAO.getAverageMarks()), statColor);

        statisticsPanel.revalidate();
        statisticsPanel.repaint();
    }

    private void refreshStatistics() {
        if (statisticsPanel != null) {
            updateStatisticsPanel();
        }
    }

    private void addStatCard(JPanel panel, GridBagConstraints gbc, String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new GridLayout(2, 1));
        card.setBackground(color);
        card.setPreferredSize(new Dimension(150, 100));
        card.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(JLabel.CENTER);

        card.add(titleLabel);
        card.add(valueLabel);

        panel.add(card, gbc);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(jLabel, gbc);

        gbc.gridx = 1;
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(field, gbc);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 1));
        return button;
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        List<Student> students = studentDAO.getAllStudents();
        for (Student student : students) {
            tableModel.addRow(new Object[]{
                student.getId(),
                student.getName(),
                student.getRollNo(),
                student.getDepartment(),
                student.getEmail(),
                student.getPhone(),
                student.getMarks()
            });
        }
    }

    private void displaySearchResults(List<Student> students) {
        searchTableModel.setRowCount(0);
        if (students != null) {
            for (Student student : students) {
                searchTableModel.addRow(new Object[]{
                    student.getId(),
                    student.getName(),
                    student.getRollNo(),
                    student.getDepartment(),
                    student.getEmail(),
                    student.getPhone(),
                    student.getMarks()
                });
            }
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            if (studentDAO.deleteStudent(id)) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                loadStudents();
                refreshStatistics();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting student!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to delete!");
        }
    }

    private void openUpdateDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Student student = getStudentById(id);

            if (student != null) {
                JDialog dialog = new JDialog(this, "Update Student", true);
                dialog.setSize(400, 350);
                dialog.setLocationRelativeTo(this);

                JPanel panel = new JPanel();
                panel.setLayout(new GridBagLayout());
                panel.setBackground(new Color(236, 240, 241));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(8, 8, 8, 8);

                JTextField nameField = new JTextField(student.getName(), 20);
                JTextField rollNoField = new JTextField(student.getRollNo(), 20);
                JTextField departmentField = new JTextField(student.getDepartment(), 20);
                JTextField emailField = new JTextField(student.getEmail(), 20);
                JTextField phoneField = new JTextField(student.getPhone(), 20);
                JTextField marksField = new JTextField(String.valueOf(student.getMarks()), 20);

                addFormRow(panel, gbc, "Name:", nameField, 0);
                addFormRow(panel, gbc, "Roll No:", rollNoField, 1);
                addFormRow(panel, gbc, "Department:", departmentField, 2);
                addFormRow(panel, gbc, "Email:", emailField, 3);
                addFormRow(panel, gbc, "Phone:", phoneField, 4);
                addFormRow(panel, gbc, "Marks:", marksField, 5);

                gbc.gridx = 0;
                gbc.gridy = 6;
                gbc.gridwidth = 2;
                JButton updateButton = createButton("Update");
                updateButton.addActionListener(e -> {
                    try {
                        Student updatedStudent = new Student(
                            id,
                            nameField.getText(),
                            rollNoField.getText(),
                            departmentField.getText(),
                            emailField.getText(),
                            phoneField.getText(),
                            Double.parseDouble(marksField.getText())
                        );
                        if (studentDAO.updateStudent(updatedStudent)) {
                            JOptionPane.showMessageDialog(dialog, "Student updated successfully!");
                            loadStudents();
                            refreshStatistics();
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Error updating student!");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "Marks must be a number!");
                    }
                });
                panel.add(updateButton, gbc);

                dialog.add(panel);
                dialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to update!");
        }
    }

    private Student getStudentById(int id) {
        List<Student> students = studentDAO.getAllStudents();
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }
}
