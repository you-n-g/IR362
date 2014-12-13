package org.ir362.indexing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.print.attribute.standard.NumberOfDocuments;

public class InvertedIndex {
	public ArrayList<String> terms;
	public HashMap<String, Posting> termPostingMap; // text的倒排索引
	public HashMap<String, Posting> termTitlePostingMap; // title的倒排索引
	public HashMap<Integer, DocMeta> documentsMetaInfo;
	
	private int numberOfDocuments;
	private int totalLengthOfDocuments; // 所有文档按词项数量统计出来的总长度


    public InvertedIndex() {
         terms = new ArrayList<String>();
         termPostingMap = new HashMap<String, Posting>();
         termTitlePostingMap = new HashMap<String, Posting>();
         documentsMetaInfo = new HashMap<Integer, DocMeta>();
    }
    
    public void addElement(String term, Posting posting){
    	terms.add(term);
    	termPostingMap.put(term, posting);
    }
    
    public void addDocMeta(int docID, DocMeta docMeta) {
    	documentsMetaInfo.put(docID, docMeta);
    }
    
    public double getAverageDocumentLength() {
    	return (double)(totalLengthOfDocuments) / (double)(numberOfDocuments) ;
    }

	public void setTotalDocumentLength(int totalLengthOfDocuments) {
		this.totalLengthOfDocuments = totalLengthOfDocuments;
	}
	
	public int getTotalDocumentLength(){
		return this.totalLengthOfDocuments;
	}
	
	public void setNumberOfDocuments(int numberOfDocuments) {
		this.numberOfDocuments = numberOfDocuments;
	}

	public int getNumberOfDocuments() {
		return this.numberOfDocuments = numberOfDocuments;
	}

    public static final void main(String args[]) {
    	
    }

}
