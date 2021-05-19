package pt.ipbeja.estig.fifteen.gui;

import pt.ipbeja.estig.fifteen.model.Move;

/**
 * The fifteen puzzle view
 *
 * @author João Paulo Barros
 * @version 2021/05/18
 */
public interface View {
    void notifyView(Move move, Boolean winning, int tValue);
}
