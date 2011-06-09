package corpusrefiner;

import static org.junit.Assert.assertEquals;

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
        while (item.getPreviousContentItemId() !=null) {
            item = corpus.get(item.getPreviousContentItemId());
            count++;
        }
        assertEquals(1000, count);
    }

}
