package corpusrefiner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.util.PlainTextByLineStream;

import org.apache.commons.lang.StringUtils;

/**
 * Flat file storage for loading a corpus to refine.
 */
public class CorpusFileLoader {

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
            // TODO: use a detokenizer while storing the offsets
            String content = StringUtils.join(sample.getSentence(), " ");
            currentSentence = new NamedEntityAnnotatedSentence(id, content, sample.getNames());
            if (prevSentence != null) {
                currentSentence.prevId = prevSentence.getId();
                prevSentence.nextId = currentSentence.getId();
            }
            corpus.put(currentSentence.getId(), currentSentence);
            i++;
        }
        return corpus;
    }

}
