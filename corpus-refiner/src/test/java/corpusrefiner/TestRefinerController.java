package corpusrefiner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestRefinerController {

    private static final Charset UTF8 = Charset.forName("utf-8");

    protected static String filename = "automated_opennlp_ner_file.txt";

    protected Map<String,CorpusItem> corpus;

    protected File refinedCorpusFile;

    @Before
    public void lookupTestFile() throws IOException {
        URL corpusUrl = TestRefinerController.class.getClassLoader().getResource(filename);
        if (corpusUrl == null) {
            throw new IOException(filename + " could not be found in the classpath");
        }
        File corpusToRefine = new File(corpusUrl.getPath());
        corpus = FileCorpusStorage.load(corpusToRefine, UTF8);
        refinedCorpusFile = File.createTempFile("corpus-refiner-", ".txt");
    }

    @After
    public void cleanUpTempfiles() {
        FileUtils.deleteQuietly(refinedCorpusFile);
    }

    @Test
    public void testCorpusLoader() throws IOException {
        assertEquals(1000, corpus.size());

        // follow the links to traverse the corpus
        int count = 1;
        String firstId = filename + ":0";
        CorpusItem item = corpus.get(firstId);
        while (item.getNextContentItemId() != null) {
            item = corpus.get(item.getNextContentItemId());
            count++;
        }
        assertEquals(1000, count);

        // reverse traversal
        count = 1;
        while (item.getPreviousContentItemId() != null) {
            item = corpus.get(item.getPreviousContentItemId());
            count++;
        }
        assertEquals(1000, count);
    }

    @Test
    public void testCorpusRefinement() throws IOException {
        RefinerController controller = new RefinerController(corpus);
        CorpusItem item = controller.first();
        assertEquals("automated_opennlp_ner_file.txt:0", item.getId());
        assertEquals(null, item.getPreviousContentItemId());
        assertEquals("automated_opennlp_ner_file.txt:1", item.getNextContentItemId());

        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:0").isValid());
        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:0").isDiscarded());
        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:1").isValid());
        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:1").isDiscarded());
        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:2").isValid());
        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:2").isDiscarded());

        item = controller.next();
        assertEquals("automated_opennlp_ner_file.txt:1", item.getId());
        assertEquals("automated_opennlp_ner_file.txt:0", item.getPreviousContentItemId());
        assertEquals("automated_opennlp_ner_file.txt:2", item.getNextContentItemId());

        // validate the first element after having read the context info
        controller.validate(item.getPreviousContentItemId());

        assertTrue(controller.getItem("automated_opennlp_ner_file.txt:0").isValid());
        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:0").isDiscarded());
        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:1").isValid());
        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:1").isDiscarded());
        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:2").isValid());
        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:2").isDiscarded());

        // validate the first element after having read the context info
        controller.validate(item.getPreviousContentItemId());

        item = controller.next();
        assertEquals("automated_opennlp_ner_file.txt:2", item.getId());
        assertEquals("automated_opennlp_ner_file.txt:1", item.getPreviousContentItemId());
        assertEquals("automated_opennlp_ner_file.txt:3", item.getNextContentItemId());

        controller.discard(item.getId());
        assertTrue(controller.getItem("automated_opennlp_ner_file.txt:0").isValid());
        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:0").isDiscarded());
        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:1").isValid());
        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:1").isDiscarded());
        assertFalse(controller.getItem("automated_opennlp_ner_file.txt:2").isValid());
        assertTrue(controller.getItem("automated_opennlp_ner_file.txt:2").isDiscarded());

        // save the work back to disk
        FileCorpusStorage.save(refinedCorpusFile, corpus);
        List<String> lines = FileUtils.readLines(refinedCorpusFile);

        // just one validated line (majority are empty lines)
        assertEquals(1000, lines.size());
        assertEquals("The first pre-commercial demonstration network in the southern hemisphere"
                     + " was built in <START:location> Adelaide <END> , "
                     + "<START:location> South Australia <END> by m.Net Corporation in February 2002"
                     + " using UMTS on 2100 MHz .", lines.get(0));
        for (int i = 1; i < 1000; i++) {
            assertEquals(String.format("Line %d is not empty", i), "", lines.get(i));
        }
    }

}
