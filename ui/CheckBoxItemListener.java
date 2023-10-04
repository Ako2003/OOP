package ui;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import java.util.HashSet;
import java.util.Set;

class CheckBoxItemListener implements ItemListener {
    public static Set<String> myArray = new HashSet<String>();
    private String studentID;
    public CheckBoxItemListener(String studentID) {
        this.studentID = studentID;
    }
    @Override
    public void itemStateChanged(ItemEvent e) {
        JCheckBox source = (JCheckBox) e.getSource();
        if (source.isSelected()) {
            myArray.add(source.getText());
        } else {
            myArray.remove(source.getText());
        }
        PlaceFrame frame= new PlaceFrame(studentID);
        frame.revalidate();
    }
}