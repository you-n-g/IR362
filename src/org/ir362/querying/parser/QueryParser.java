package org.ir362.querying.parser;

import java.util.HashSet;
import java.util.logging.Logger;

import org.ir362.indexing.Document;

public class QueryParser {
    private static final Logger log = Logger.getLogger( QueryParser.class.getName() );
	
	protected static HashSet<String> quoteSet = new HashSet<String>() {{
		add("\"");	add("“");	add("”");
	}};;
	
	protected static HashSet<String> prefixSet = new HashSet<String>() {{
		add("+");	add("-");
	}};;
	
	protected static HashSet<String> splitSet = new HashSet<String>() {{
		add("!");	add(",");	add(".");	add(":");	add(";");	add(" ");
		add("+");	add("\t");	add("\n");	add("\r");	add("(");	add(")");
		add("<");	add(">");	add("{");	add("}");	add("[");	add("]");
		add("?");	add("\"");	add("|");	add("！");	add("，");	add("。");
		add("：");	add("；");	add("　");	add("（");	add("）");	add("《");
		add("》");	add("？");	add("“");	add("”");	add("…");	add("—");
	}};;
	
	public QueryParser() {}
	
	public static MultiTermQuery parseQuery(String queryString) {
		MultiTermQuery query = new MultiTermQuery();
		if (null == queryString || queryString.isEmpty()) {
			return query;
		}
		/*  因为引入分词器， 停用词也处理好了, 所以直接用分词器,  TODO 目前还没支持required flag */
		try {
			for (String st: Document.splitWords(queryString)) {
				// TODO  所有词都看成 requireFlag = True
				query.add(new SingleTermQuery(true, st));
				log.info("分词结果包含： " + st);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return query;

		/*  下面是原来分词的代码， 
		int length = queryString.length();
		boolean quoteFlag = false;
		boolean requireFlag = true;
		int begin = 0;
		for (int i = 0; i < length; i++) {
			String ch = queryString.substring(i, i + 1);
			if (quoteSet.contains(ch)) {
				query.add(new SingleTermQuery(requireFlag, queryString.substring(begin, i)));
				if (quoteFlag) {
					quoteFlag = false;
				} else {
					quoteFlag = true;
				}
				requireFlag = true;
				begin = i + 1;
			} else if (!quoteFlag) {
				if ((i == begin) && prefixSet.contains(ch)) {
					if ("-".equals(ch)) {
						requireFlag = false;
					}
					begin++;
				} else if (splitSet.contains(ch)) {
					query.add(new SingleTermQuery(requireFlag, queryString.substring(begin, i)));
					quoteFlag = false;
					requireFlag = true;
					begin = i + 1;
				}
			}
		}
		if (begin < length) {
			query.add(new SingleTermQuery(requireFlag, queryString.substring(begin, length)));
		}
		return query;
         */
	}
	
	public static void main(String[] args) {
		if (null != args && args.length > 0) {
			String queryString = args[0];
			MultiTermQuery query = parseQuery(queryString);
			for (int i = 0; i < query.length(); i++) {
				System.out.println(query.get(i).toString());
			}
		}
	}
	
}