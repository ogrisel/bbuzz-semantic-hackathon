package corpusrefiner;

import java.io.IOException;
import java.util.List;

import opennlp.tools.util.Span;

/**
 * Content element and annotation being reviewed and refined by the user.
 */
public interface CorpusItem {

    /**
     * Id of the content item, must be unique inside the corpus.
     */
    String getId();

    String getPreviousContentItemId();

    String getNextContentItemId();

    String getAnnotatedContent();

    void setAnnotatedContent(String annotatedContent) throws IOException;

    void setAnnotatedContent(String content, List<Span> annotations) throws IOException;

    String getContent();

    List<Span> getAnnotations();

    void validate();

    void discard();

    boolean isDiscarded();

    boolean isValid();

}
