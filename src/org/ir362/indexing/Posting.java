package org.ir362.indexing;
import java.util.ArrayList;

import org.fnlp.ml.types.alphabet.IAlphabet;

import edu.uci.jforests.util.Pair;

public class Posting {
	public int term_index;
	int df;
	public int data[][]; // 存储如 [<docID, tf>] 之类的信息
	public Posting(ArrayList<Pair<Integer, Integer>> postIndexMap, int term_index) {
		this.term_index = term_index;
		df = postIndexMap.size();
		data = new int[df][2];
		for (int i = 0; i < df; ++i) {
			data[i][0] = postIndexMap.get(i).getFirst();
			data[i][1] = postIndexMap.get(i).getSecond();
		}
	}
	
	public Posting(int term_index, int df) {
		this.term_index = term_index;
		this.df = df;
		data = new int[df][2];
	}

	public Posting(int term_index) {
		this.term_index = term_index;
		// df 和 data 待会生成！！！！！
		// 需要到时候调用 data
	}
	
	public void init_data(int df) {
		this.df = df;
		data = new int[df][2];
	}

	public double getDocumentFrequency() {
		return df;
	}
}
