package corpusrefiner.cli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import corpusrefiner.RefinerController;

import jline.ConsoleReader;

public class ValidateActionListener implements ActionListener {

    protected ConsoleReader reader;

    protected RefinerController controller;

    public ValidateActionListener(ConsoleReader reader, RefinerController controller) {
        this.reader = reader;
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            reader.printString("validate!");
        } catch (IOException e1) {
            System.err.println(e1);
        }
    }

}
