package corpusrefiner.cli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import jline.ConsoleReader;
import corpusrefiner.CorpusItem;
import corpusrefiner.FileCorpusStorage;
import corpusrefiner.RefinerController;

public class RefinerCLI {

    public static final String COMMANDS_MESSAGE = "Commands:\n" + "  'space': validate\n"
                                                  + "  'e': edit current line and validate upon ENTER\n"
                                                  + "  'd': discard\n"
                                                  + "  'arrow up or left': go to previous item\n"
                                                  + "  'arrow down or right': go to next item\n"
                                                  + "  's': save validated items and quit\n"
                                                  + "  'q': quit without saving\n";
    public static char[] COMMANDS = new char[] {' ', 'd'};
    	
    public void runSession(InputStream in,
                           PrintStream userPrintStream,
                           RefinerController controller,
                           File outputCorpusFile) throws IOException {

        CorpusItem ci = controller.first();
        Writer writer = new PrintWriter(new OutputStreamWriter(userPrintStream, System.getProperty(
            "jline.WindowsTerminal.output.encoding", System.getProperty("file.encoding"))));
        ConsoleReader reader = new ConsoleReader(in, writer);
        reader.setBellEnabled(false);
        int command = 0;
        boolean quit = false;
        while (!quit) {
            reader.setDefaultPrompt("");
            reader.clearScreen();
            
            //command part
            reader.printNewline();
            reader.printString(COMMANDS_MESSAGE);
            reader.printNewline();
            reader.printString(String.format("Last command code: %d", command));
            reader.printNewline();
            reader.printNewline();
            
            //context part
            reader.printString("Previous item :");
            reader.printNewline();
            viewPrevious(controller, reader);
            reader.printNewline();
            
            printCorpusItemStatus(ci,reader);
            
            reader.printString(ci.getAnnotatedContent());
            reader.printNewline();
           
            command = reader.readVirtualKey();
           
            switch (command) {
                case 2: // arrow left
                    ci = handlePrevious(controller, reader);
                    break;

                case 16: // arrow up
                    ci = handlePrevious(controller, reader);
                    break;

                case 6: // arrow right
                    ci = handleNext(controller, reader);
                    break;

                case 14: // arrow down
                    ci = handleNext(controller, reader);
                    break;

                case ' ':
                    controller.validate(ci.getId());
                    ci = handleNext(controller, reader);
                    break;

                case 'v':
                    controller.validate(ci.getId());
                    ci = handleNext(controller, reader);
                    break;

                case 'e':
                    reader.clearScreen();
                    reader.printString("Enter correction (annotations start with '<START:type>'"
                                       + " and end with '<END>'), validate with 'enter':");
                    reader.printNewline();
                    reader.printNewline();
                    reader.putString(ci.getAnnotatedContent());
                    String line = reader.readLine();
                    if (!line.isEmpty()) {
                        ci.setAnnotatedContent(line);
                        controller.validate(ci.getId());
                        ci = handleNext(controller, reader);
                    }
                    break;

                case 'd':
                    controller.discard(ci.getId());
                    ci = handleNext(controller, reader);
                    break;

                case 's':
                    reader.clearScreen();
                    reader.printString("Saving to: " + outputCorpusFile.getAbsolutePath());
                    reader.printNewline();
                    controller.saveToFile(outputCorpusFile);
                    quit = true;
                    break;

                case 'q':
                	quit = confirmQuit(reader);
                    
                    break;

                default:
                    reader.flushConsole();
                    break;
            }
        }
    }

    private boolean confirmQuit(ConsoleReader reader) throws IOException {
    	reader.printNewline();
    	reader.printString("Sure to quit without saving ? (y / n)");
        reader.printNewline();
        //String yn = reader.readLine();
        int yn = reader.readVirtualKey();
        
        switch (yn) {
        case 'y' :
        	return true;
        
        case 'n' :
        	return false;
        	
        default:
        	reader.printString("Please type 'y' or 'n'");
            reader.printNewline();
        	return confirmQuit(reader);
        }
	}

	private void printCorpusItemStatus(CorpusItem ci, ConsoleReader reader) throws IOException {
    	if (ci.isDiscarded()) {
            reader.printString("[d] ");
        } else if (ci.isValid()) {
            reader.printString("[v] ");
        } else {
            reader.printString("[ ] ");
        }
	}

	protected CorpusItem handleNext(RefinerController controller, ConsoleReader reader) throws IOException {
        if (controller.hasNext()) {
            return controller.next();
        } else {
        	reader.printString("No next item for this file");
        	reader.printNewline();
            return controller.getCurrentItem();
        }
    }
    
    protected CorpusItem handlePrevious(RefinerController controller, ConsoleReader reader) throws IOException {
        if (controller.hasPrevious()) {
            return controller.previous();
        } else {
        	reader.printString("No previous item for this file");
        	reader.printNewline();
            return controller.getCurrentItem();
        }
    }
    
    protected void viewPrevious(RefinerController controller, ConsoleReader reader) throws IOException {
        if (controller.hasPrevious()) {
        	printCorpusItemStatus(controller.viewPrevious(), reader);
            reader.printString(controller.viewPrevious().getAnnotatedContent());
            reader.printNewline();
        } else {
        	reader.printString("No previous item for this file");
        	reader.printNewline();
        }
    }
    public static void main(String[] args) {
        // parse CLI configuration to find input corpus file and output corpus file (and later models)
        RefinerCLI cli = new RefinerCLI();
        if (args.length < 2) {
            System.err.println("ERROR: need a OpenNLP corpus file as first argument"
                               + " and output file as second argument");
            System.exit(1);
        }
        try {
            File inputCorpusFile = new File(args[0]);
            File outputCorpusFile = new File(args[1]);
            Map<String,CorpusItem> corpus = FileCorpusStorage.load(inputCorpusFile, Charset.forName("UTF-8"));
            RefinerController ctl = new RefinerController(corpus);
            System.out.println("Refining: " + inputCorpusFile.getAbsolutePath());
            cli.runSession(System.in, System.out, ctl, outputCorpusFile);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
