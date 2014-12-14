package org.ir362.indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.print.attribute.standard.NumberOfDocuments;

import org.ir362.Config;

public class InvertedIndex {
	public ArrayList<String> terms;
	public HashMap<String, Posting> termPostingMap; // text的倒排索引
	public HashMap<String, Posting> termTitlePostingMap; // title的倒排索引
	public HashMap<Integer, DocMeta> documentsMetaInfo;
	public static Set stopWordSet = new HashSet<String>();
	
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

	public void clearStopWords() {
		ArrayList<String> clear_terms = new ArrayList<String>();
		FileInputStream fileS = null;
		BufferedReader br = null;
		String stopword = null;

		try {
			fileS = new FileInputStream(new File(Config.project_folder_path + "StopWordsTable.txt"));
			br = new BufferedReader(new InputStreamReader(fileS,"UTF-8"));
			while((stopword = br.readLine())!=null)
				stopWordSet.add(stopword);
			
			br.close();
			fileS.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (String term: terms)
            if(stopWordSet.contains(term)){
                termPostingMap.remove(term);
                termTitlePostingMap.remove(term);
            }
            else
                clear_terms.add(term);
		terms = clear_terms;
	}

    public static final void main(String args[]) {
    	
    }

}
