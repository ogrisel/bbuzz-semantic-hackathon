package corpusrefiner;

import java.util.Iterator;
import java.util.Map;

public class RefinerController implements Iterator<CorpusItem> {

    protected final Map<String,CorpusItem> corpus;
    
    protected CorpusItem currentItem;

    public RefinerController(Map<String,CorpusItem> corpus) {
        if (corpus.isEmpty()) {
            throw new IllegalArgumentException("Cannot refine an empty corpus");
        }
        this.corpus = corpus;
        // initialize the currentItem
        first();
    }

    @Override
    public boolean hasNext() {
        return currentItem != null && currentItem.getNextContentItemId() != null;
    }

    public boolean hasPrevious() {
        return currentItem != null && currentItem.getPreviousContentItemId() != null;
    }

    /**
     * Rewind the cursor position to the first element of the corpus.
     */
    public CorpusItem first() {
        // assume the corpus iterator is consistent to the
        currentItem = corpus.values().iterator().next();
        return currentItem;
    }

    @Override
    public CorpusItem next() {
        if (hasNext()) {
            currentItem = corpus.get(currentItem.getNextContentItemId());
            return currentItem;
        }
        return null;
    }

    public CorpusItem previous() {
        if (hasPrevious()) {
            currentItem = corpus.get(currentItem.getPreviousContentItemId());
            return currentItem;
        }
        return null;
    }

    @Override
    public void remove() {
        discard(currentItem.getId());
    }

    public CorpusItem getItem(String id) {
        return corpus.get(id);
    }
    
    public void discard(String id) {
        getItem(id).discard();
    }

    public void validate(String id) {
        getItem(id).validate();
    }

    public void replace(CorpusItem correctedItem) {
        correctedItem.validate();
        corpus.put(correctedItem.getId(), correctedItem);
    }
}
