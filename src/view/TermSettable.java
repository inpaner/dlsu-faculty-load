package view;

import java.awt.Component;

public interface TermSettable<T extends Component> {
    //TODO combine 
    public void setTerm(int year, int term);
    public void refresh();
}
