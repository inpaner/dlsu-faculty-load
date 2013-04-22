package view;

import javax.swing.JDialog;
import javax.swing.JPanel;

public class SearchDialog extends JDialog {
    public SearchDialog() {
        setModalityType(ModalityType.TOOLKIT_MODAL);
        setSize(400, 600);
        setLocationRelativeTo(null);
    }
    
    public void setPanel(JPanel panel) {
        setContentPane(panel);
        setVisible(true);
    }
    

}
