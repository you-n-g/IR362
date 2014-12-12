package org.ir362.querying;

import java.io.IOException;

import org.ir362.indexing.InvertedIndex;
import org.ir362.matching.Matching;
import org.ir362.matching.MatchingTerms;
import org.ir362.matching.ResultSet;

public class Manager {
	private InvertedIndex index;
	public Manager(InvertedIndex _index)
	{
		if (_index == null)
			throw new IllegalArgumentException("Null index specified to manager. The index may be loaded increctly");
		this.index = _index;
	}
	
	public Request newRequest(String query)
	{
		Request q = new Request(query, index);
		return q;
	}

	public void preProcess(Request rq) {
		// 其琛的 parser 是在这个地方执行的
		rq.setMatchingTerms(new MatchingTerms(rq));
	}
	
	public void runMatching(Request rq) {
		// 这个 matching 用的方法是 TAAT!!!! 因为简单…… daat的我实在是看着头疼

        //TODO 其实我都没用 WeightingModel这个东西，而是直接编码在一起

        Matching matching = new Matching(rq.getIndex());

        MatchingTerms mt = rq.getMatchingTerms();
        // TODO 等待 Parser的出现， 我就不用 query来获得terms了


        // TODO add score modifier， 我们先实现一个无视 score modifier的版本
        //mt.addDocumentScoreModifier(new BooleanScoreModifier(requirement_list_positive));

        //mt.normaliseTermWeights();
        // what if I don't normalise the weights
        try{
                ResultSet rs = matching.match(mt);
                rs.initialize(); // 将rs初始化， 排序也干掉。
                rq.setResultSet(rs);
        } catch (IOException ioe) {
        }
        /* 
        */
	}	
}
