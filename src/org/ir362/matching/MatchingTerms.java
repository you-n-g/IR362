package org.ir362.matching;


import java.util.ArrayList;

import org.ir362.querying.Request;
import org.ir362.querying.parser.MultiTermQuery;
import org.ir362.querying.parser.QueryParser;

// 我是最终用来真正做matching的 terms
// 会用parser来分析query
// TODO 我本身的每个词汇还应该带有一些属性，这些属性应该包含 这个词是要还是不要
public class MatchingTerms {
	Request rq;
	MultiTermQuery mtq=null;
	public MatchingTerms(Request rq) {
		this.rq = rq;
		// 其琛的parser就是在这里发挥的作用
		this.mtq = QueryParser.parseQuery(rq.get_original_query());
	}
	public ArrayList<String> getTerms() {
		ArrayList<String> terms = new ArrayList<String>();
		for (int i = 0; i < mtq.length(); ++i)
            terms.add(mtq.get(i).getTerm());
		return terms;
	}
}