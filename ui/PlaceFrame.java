package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PlaceFrame extends JFrame {

    private JCheckBox[] checkboxes;
    private JButton submitButton;
    private JButton cancelButton;
    private JTextArea selectedChoicesTextArea;
    private String studentID;
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public ArrayList<String> selectedChoices;

    public PlaceFrame(String studentID) {
        this.studentID = studentID;
        this.setSize(1200, 600);
        this.setTitle("Multiple Choice Example");
        this.setLayout(new BorderLayout());
        this.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(242, 242, 242));

        JLabel label = new JLabel();
        label.setText("StudentID: " + studentID);
        label.setForeground(new Color(36, 41, 46));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));

        checkboxes = new JCheckBox[0]; // Initialize checkboxes array
        ArrayList<String> destinations = getDestinationsFromServer();
        checkboxes = new JCheckBox[destinations.size()];

        for (int i = 0; i < destinations.size(); i++) {
            checkboxes[i] = new JCheckBox(destinations.get(i));
            checkboxes[i].setForeground(new Color(36, 41, 46));
            checkboxes[i].setFont(new Font("Arial", Font.PLAIN, 16));
            checkboxes[i].setOpaque(false);
            panel.add(checkboxes[i]);
        }

        // Create the submit and cancel buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitButton = new JButton("Submit");
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(92, 184, 92));
        submitButton.setFocusPainted(false);
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        buttonPanel.add(submitButton);

        cancelButton = new JButton("Delete all");
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(255, 102, 102));
        cancelButton.setFocusPainted(false);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 16));
        buttonPanel.add(cancelButton);

        selectedChoicesTextArea = new JTextArea();
        selectedChoicesTextArea.setEditable(false);
        selectedChoicesTextArea.setLineWrap(true);
        selectedChoicesTextArea.setWrapStyleWord(true);
        selectedChoicesTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
        selectedChoicesTextArea.setBackground(new Color(240, 240, 240));
        selectedChoicesTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(selectedChoicesTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 20));
        textAreaPanel.add(scrollPane, BorderLayout.CENTER);

        submitButton.addActionListener(e -> {
            StringBuilder choices = new StringBuilder();
            for (String choice : selectedChoices) {
                choices.append(choice).append("\n");
            }
            if (choices.length() == 0) {
                JOptionPane.showMessageDialog(this, "Please select at least one choice.");
                return;
            }

            selectedChoicesTextArea.setText(choices.toString());

            if (selectedChoices.size() > 6) {
                JOptionPane.showMessageDialog(this, "You can select a maximum of 6 choices.");
            } else {
                if(insertSelectedChoicesIntoServer()){
                    JOptionPane.showMessageDialog(this, "Your choices have been approved.");
                    System.exit(0);
                } else{
                    JOptionPane.showMessageDialog(this, "Failed to insert choices into the server. Please try again.");
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JCheckBox checkbox : checkboxes) {
                    checkbox.setSelected(false);
                }
                selectedChoices.clear();
                updateSelectedChoicesTextArea();
            }
        });


        getContentPane().setBackground(new Color(255, 255, 255));
        add(label, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(textAreaPanel, BorderLayout.EAST);

        selectedChoices = new ArrayList<>();

        for (JCheckBox checkbox : checkboxes) {
            checkbox.addItemListener((ItemListener) new CheckBoxItemListener());
        }
    }
    
    private ArrayList<String> getDestinationsFromServer() {
        ArrayList<String> destinations = new ArrayList<>();
    
        // Simulated destinations from server
        destinations.add("Argentina");
        destinations.add("Japan");
        destinations.add("Italy");
        destinations.add("Australia");
        destinations.add("Canada");
        destinations.add("Brazil");
        destinations.add("Thailand");
        destinations.add("France");
        destinations.add("Mexico");
        destinations.add("Greece");
    
        return destinations;
    }
    

    private class CheckBoxItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            JCheckBox source = (JCheckBox) e.getItem();
            String choice = source.getText();
    
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (selectedChoices.size() >= 6) {
                    source.setSelected(false); // Unselect the checkbox
                    JOptionPane.showMessageDialog(PlaceFrame.this, "You can select a maximum of 6 choices.");
                } else {
                    selectedChoices.add(choice);
                }
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                selectedChoices.remove(choice);
            }
    
            updateSelectedChoicesTextArea();
        }

    }

    private void updateSelectedChoicesTextArea() {
        StringBuilder choices = new StringBuilder();
        for (String choice : selectedChoices) {
            choices.append(choice).append("\n");
        }
        selectedChoicesTextArea.setText(choices.toString());
    }

    private boolean insertSelectedChoicesIntoServer() {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
    
            // Send the student ID and selected choices to the server
            outputStream.writeObject(selectedChoices);
            outputStream.flush();

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

    
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}