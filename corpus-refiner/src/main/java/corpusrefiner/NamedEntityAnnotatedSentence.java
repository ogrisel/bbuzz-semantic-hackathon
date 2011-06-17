package corpusrefiner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;

import org.apache.commons.lang.StringUtils;

public class NamedEntityAnnotatedSentence implements CorpusItem {

    protected final String id;
    protected NameSample nameSample;
    protected String prevId;
    protected String nextId;
    protected boolean validated = false;
    protected boolean discarded = false;

    public NamedEntityAnnotatedSentence(String id, NameSample nameSample) {
        this.id = id;
        this.nameSample = nameSample;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getPreviousContentItemId() {
        return prevId;
    }

    @Override
    public String getNextContentItemId() {
        return nextId;
    }

    public String getAnnotatedContent() {
        return nameSample.toString();
    }

    public void setAnnotatedContent(String rawContent) throws IOException {
        nameSample = NameSample.parse(rawContent, false);
    }

    @Override
    public void setAnnotatedContent(String content, List<Span> annotations) throws IOException {
        Span reducedNames[] = NameFinderME.dropOverlappingSpans(annotations.toArray(new Span[annotations
                .size()]));
        String whitespaceTokenizedLine[] = WhitespaceTokenizer.INSTANCE.tokenize(content);
        nameSample = new NameSample(whitespaceTokenizedLine, reducedNames, false);
    }

    @Override
    public String getContent() {
        return StringUtils.join(nameSample.getSentence(), " ");
    }

    @Override
    public List<Span> getAnnotations() {
        return Arrays.asList(nameSample.getNames());
    }

    @Override
    public void validate() {
        this.validated = true;
        this.discarded = false;
    }

    @Override
    public void discard() {
        this.validated = false;
        this.discarded = true;
    }

    @Override
    public boolean isDiscarded() {
        return discarded;
    }

    @Override
    public boolean isValid() {
        return validated;
    }

}
