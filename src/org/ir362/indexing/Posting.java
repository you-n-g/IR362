package org.ir362.indexing;
import java.util.ArrayList;

import org.fnlp.ml.types.alphabet.IAlphabet;

public class Posting {
	public int term_index;
	int df;
	public int data[][]; // 存储如 [<docID, tf>] 之类的信息
	public Posting(ArrayList<ArrayList<Integer>> postIndexMap, int term_index) {
		this.term_index = term_index;
		df = postIndexMap.size();
		data = new int[df][2];
		for (int i = 0; i < df; ++i) {
			data[i][0] = postIndexMap.get(i).get(0);
			data[i][1] = postIndexMap.get(i).get(1);
		}
	}
	
	public Posting(int term_index, int df) {
		this.term_index = term_index;
		this.df = df;
		data = new int[df][2];
	}

	public double getDocumentFrequency() {
		return df;
	}
}
