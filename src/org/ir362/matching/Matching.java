package org.ir362.matching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ir362.indexing.InvertedIndex;
import org.ir362.indexing.Posting;


/** 匹配的真正代码在我们这里 */ 

public  class Matching
{
	/** The index used for retrieval. */ 
	private InvertedIndex index;
	protected double numberOfDocuments;
	protected double averageDocumentLength;
	
	
	public Matching(InvertedIndex index) 
	{
		this.index = index;
	}


	public ResultSet match(MatchingTerms mt) throws IOException {
		// init some variable
		ResultSet rs = new ResultSet();
		numberOfDocuments = index.getNumberOfDocuments();
		averageDocumentLength = index.getAverageDocumentLength();
		double df, idf, tf, docLen;
		double titleDf, titleIdf, titleTf, titleDocLen;


        int docid;
        double score;
        Posting p;
        
        double hotDoc;
        double TitleScore;
        Posting pTitle;

        /** The constant k_1.*/
        double k_1 = 1.2d;
        /** The constant b.*/
        double b = 0.75d;

		for (String term: mt.getTerms()) {
                p = index.termPostingMap.get(term);
                pTitle = index.termTitlePostingMap.get(term);
                if(pTitle == null)
                	titleIdf = 0;
                if (p == null) 
                	continue; // 如果都没有出现这个词项，则跳过
                
                df = (double)p.getDocumentFrequency();
                idf = Math.log(numberOfDocuments / df); // 这个+1到底要不要

                titleDf = (double)pTitle.getDocumentFrequency();
                titleIdf = 10*Math.log(numberOfDocuments / titleDf);

                for (int i = 0; i < df; ++i) {
                        docid = p.data[i][0];
                        tf = p.data[i][1];
                        titleTf = pTitle.data[i][1];
                        hotDoc = index.documentsMetaInfo.get(docid).commentNumber;
                        hotDoc = hotDoc/100000;
                        
                        docLen = index.documentsMetaInfo.get(docid).docLength;
                        double Robertson_tf = (k_1*tf)/(tf+k_1*(1-b+b*docLen/averageDocumentLength));
                        double Robertson_title_tf = (k_1*5*titleTf)/(titleTf+k_1*(1-b+b*docLen/averageDocumentLength));

                        score = Robertson_tf * idf;
                        TitleScore = Robertson_title_tf*titleIdf+0.05;
                        score = score + TitleScore +hotDoc;
                        rs.scoresMap.adjustOrPutValue(docid, score, score);
                }
		}
        return rs;
	}
}
