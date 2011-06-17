package corpusrefiner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 * Flat file storage for loading a corpus to refine and saving the refined corpus.
 */
public class FileCorpusStorage {

    public static Map<String,CorpusItem> load(File corpusFile, Charset charset) throws IOException {
        final NameSampleDataStream samples = new NameSampleDataStream(new PlainTextByLineStream(
                new InputStreamReader(new FileInputStream(corpusFile), charset)));

        // The LinkedHashMap implementation is required to be able to find the first element of the corpus
        // easily
        HashMap<String,CorpusItem> corpus = new LinkedHashMap<String,CorpusItem>();

        int i = 0;
        NamedEntityAnnotatedSentence prevSentence = null;
        NamedEntityAnnotatedSentence currentSentence = null;
        NameSample sample;

        while ((sample = samples.read()) != null) {
            prevSentence = currentSentence;
            String id = String.format("%s:%d", corpusFile.getName(), i);
            currentSentence = new NamedEntityAnnotatedSentence(id, sample);
            if (prevSentence != null) {
                currentSentence.prevId = prevSentence.getId();
                prevSentence.nextId = currentSentence.getId();
            }
            corpus.put(currentSentence.getId(), currentSentence);
            i++;
        }
        return corpus;
    }

    public static void save(File outputCorpusFile, Map<String,CorpusItem> corpus) throws IOException {
        FileWriter fileWriter = new FileWriter(outputCorpusFile);
        try {
            // assume the item ordering is preserved by the map implementation
            for (CorpusItem item : corpus.values()) {
                if (item.isValid()) {
                    fileWriter.write(item.getAnnotatedContent());
                }
                // output a new line even if invalid to be able to re-align refined corpora by multiple
                // annotators
                fileWriter.write('\n');
            }
        } finally {
            fileWriter.close();
        }
    }
}
