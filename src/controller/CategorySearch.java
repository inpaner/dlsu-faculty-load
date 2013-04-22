package controller;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import model.Category;
import model.Faculty;
import model.Workload;
import view.CategoryListPanel;
import view.FacultyListPanel;

public class CategorySearch {
    private static Category category;
    
    public static Category dialog() {
        // TODO JDialog should be implemented in view
        final JDialog dialog = new JDialog();
        final CategoryListPanel panel = new CategoryListPanel(true);
        panel.initPopup();
        ActionListener acceptListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                category = panel.selectedCategory();
                dialog.dispose();
            }
        };
        ActionListener cancelListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                category = null;
                dialog.dispose();
            }
        };
        panel.addAddListener(acceptListener);
        panel.addEditListener(cancelListener); // TODO Hmm
        panel.addCancelListener(cancelListener);
        dialog.setModalityType(ModalityType.TOOLKIT_MODAL);
        dialog.setContentPane(panel);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return category;
    }
}
