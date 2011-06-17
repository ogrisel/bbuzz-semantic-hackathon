package corpusrefiner.cli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import jline.ConsoleReader;
import corpusrefiner.RefinerController;

public class DiscardActionListener implements ActionListener {
    
    protected ConsoleReader reader;

    protected RefinerController controller;


    public DiscardActionListener(ConsoleReader reader, RefinerController controller) {
        this.reader = reader;
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            reader.printString("discard!");
        } catch (IOException e1) {
            System.err.println(e1);
        }
    }

}
