package org.ir362.querying;

import org.ir362.indexing.InvertedIndex;
import org.ir362.matching.MatchingTerms;
import org.ir362.matching.ResultSet;


public class Request {
	private String original_query;
	private MatchingTerms mt=null;
	private InvertedIndex index=null;
	private ResultSet result;

	public Request(String original_query, InvertedIndex index) {
		this.original_query = original_query;
		this.index = index;
	}
	
	public void setMatchingTerms(MatchingTerms mt) {
		this.mt = mt;
	}

	public MatchingTerms getMatchingTerms() {
		return this.mt;
	}

	public InvertedIndex getIndex() {
		return index;
	}

	public void setResultSet(ResultSet result) {
		this.result = result;
	}

	public ResultSet getResultSet() {
		return result;
	}
	
	public String get_original_query() {
		return original_query;
	}
}
