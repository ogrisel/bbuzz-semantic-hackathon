package corpusrefiner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestRefinerController {

    private static final Charset UTF8 = Charset.forName("utf-8");

    protected static String filename = "automated_opennlp_ner_file.txt";

    protected static Map<String,CorpusItem> corpus;

    @BeforeClass
    public static void lookupTestFile() throws IOException {
        URL corpusUrl = TestRefinerController.class.getClassLoader().getResource(filename);
        if (corpusUrl == null) {
            throw new IOException(filename + " could not be found in the classpath");
        }
        File corpusToRefine = new File(corpusUrl.getPath());
        corpus = CorpusFileLoader.load(corpusToRefine, UTF8);
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
    public void testCorpusRefinement() {
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
    }

}
