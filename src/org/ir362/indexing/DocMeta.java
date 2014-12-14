package org.ir362.indexing;

public class DocMeta {
	public int docLength;
	public int commentNumber;
	public String pubDate;
	public String url;
	public String title;

	public DocMeta(int docLength, int commentNumber, String pubDate, String url, String title) {
		this.docLength = docLength;
		this.commentNumber = commentNumber;
		this.pubDate = pubDate;
		this.url = url;
		this.title = title;
	}
}
