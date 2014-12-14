package org.ir362.indexing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.ir362.Config;

import edu.uci.jforests.util.Pair;

public class DirectIndex {
    private static final Logger log = Logger.getLogger( DirectIndex.class.getName() );
	
	HashMap<Integer,ArrayList<Pair<Integer, Integer>>> docPosting;

	public DirectIndex() {
		this.docPosting = new HashMap<Integer,ArrayList<Pair<Integer, Integer>>>();
	}
	
	public void loadFromInvertedIndex(InvertedIndex index) {
        log.info("loadingDirectIndexFromInvertedIndex");
		Posting p;
		int docID, tf;

        for (int i = 0; i < index.terms.size(); ++i) {
            if (i % 20000 == 0) log.info("" + i + " inverted Posting Loaded");
            p = index.termPostingMap.get(index.terms.get(i));
            for (int j = 0; j < p.df; ++j) {
            	docID = p.data[j][0];
                tf = p.data[j][1];
                if (!docPosting.containsKey(docID)) docPosting.put(docID, new ArrayList<Pair<Integer, Integer>>());
                docPosting.get(docID).add(new Pair(p.term_index, tf));
            }
        }
	}
	
	public static void createDirectIndex() {
    	new DiskIndexManager(Config.index_folder).saveCollectionDirectPosting(
    			new CorpusIndexMaker().makeIndexFromCorpus(CorpusIndexMaker.corpus_folder, false));
	}

	public static final void main(String args) {
		createDirectIndex();
	}
}
