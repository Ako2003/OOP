package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class StudentIDFrame extends JFrame {

    private JTextField idTextField;
    private JButton submitButton;

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public StudentIDFrame() {
        this.setSize(400, 200);
        this.setTitle("Student ID Entry");
        this.setLayout(new BorderLayout());
        this.setResizable(false);

        // Change the position of the window
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2 );


        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(242, 242, 242));

        JLabel label = new JLabel();
        label.setText("Enter your student ID:");
        label.setForeground(new Color(36, 41, 46));
        label.setFont(new Font("Arial", Font.PLAIN, 16));

        idTextField = new JTextField();
        idTextField.setPreferredSize(new Dimension(200, 30));
        idTextField.setFont(new Font("Arial", Font.PLAIN, 14));

        submitButton = new JButton("Submit");
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(92, 184, 92));
        submitButton.setFocusPainted(false);
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));

        panel.add(label, BorderLayout.CENTER);
        panel.add(idTextField, BorderLayout.CENTER);
        panel.add(submitButton, BorderLayout.SOUTH);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentID = idTextField.getText();
                if (studentID.isEmpty()) {
                    JOptionPane.showMessageDialog(StudentIDFrame.this, "Please enter your student ID.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (sendStudentID(studentID)) {
                        JOptionPane.showMessageDialog(StudentIDFrame.this, "Student with ID " + studentID + " successfully sent to the server!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        idTextField.setText(""); // Clear the text field after submission

                        // Open the PlaceFrame page
                        PlaceFrame placeFrame = new PlaceFrame(studentID);
                        placeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        placeFrame.setVisible(true);

                        // Close the StudentIDFrame page
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(StudentIDFrame.this, "An error occurred while sending the student ID. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        getContentPane().setBackground(new Color(255, 255, 255));
        add(panel, BorderLayout.CENTER);
    }

    private boolean sendStudentID(String studentID) {
        try {
            // Create a socket connection to the server
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);

            // Create output stream to send data
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            // Write the student ID to the output stream
            outputStream.writeObject(studentID);
            outputStream.flush();

            // Create input stream to receive response from the server
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void main(String[] args) {
        // Create an instance of StudentIDFrame and make it visible
        StudentIDFrame frame = new StudentIDFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
