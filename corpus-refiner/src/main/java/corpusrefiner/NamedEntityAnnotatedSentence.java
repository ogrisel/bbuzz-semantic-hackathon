package corpusrefiner;

import java.util.Arrays;
import java.util.List;

import opennlp.tools.util.Span;

public class NamedEntityAnnotatedSentence implements CorpusItem {

    protected final String id;
    protected String prevId;
    protected String nextId;
    protected String sentence;
    protected Span[] annotations;
    protected boolean validated = false;
    protected boolean discarded = false;

    public NamedEntityAnnotatedSentence(String id,
                                        String sentence,
                                        Span[] annotations) {
        this.id = id;
        this.sentence = sentence;
        this.annotations = annotations;
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

    @Override
    public String getContent() {
        return sentence;
    }

    @Override
    public List<Span> getAnnotations() {
        return Arrays.asList(annotations);
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
