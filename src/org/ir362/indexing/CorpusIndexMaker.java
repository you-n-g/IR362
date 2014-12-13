package org.ir362.indexing;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.lang.model.element.Element;


/* 这里假设语料的文件名就是数字，而且就是文档id */

public class CorpusIndexMaker {
    private static final Logger log = Logger.getLogger( CorpusIndexMaker.class.getName() );
    public final static String  corpus_folder = "/home/young/workspace4.4/IR362/corpus";
    
    // 从语料庫建立索引相关
    public static ArrayList<String> listFilesForFolder(final File folder) {
    	// 这里保证返回的 文档名称都是已经拍好序了
    	log.info("Begin Loading listFilesForFolder....");
    	ArrayList<String> file_paths = new ArrayList<String>();
    	TreeMap<Integer, Integer> map4sort = new TreeMap<Integer, Integer>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                //listFilesForFolder(fileEntry);
            	continue; //如果发现文件夹就不作为
            } else {
                //file_paths.add(fileEntry.getAbsolutePath());
                try {
                    map4sort.put(Integer.parseInt(fileEntry.getName()), 0);
                } catch (NumberFormatException e) {
                    log.info("skip: " + fileEntry.getName());
                }
            }
        }

        int last = -1; // 我在这里只是为了保证一下排序的正确性
		for (Map.Entry<Integer, Integer> entry: map4sort.entrySet()) {
			assert(last <= entry.getKey());
			last = entry.getKey();
            file_paths.add(folder.getAbsolutePath() + '/' +  entry.getKey());
		}
        return file_paths;
    }

    public InvertedIndex makeIndexFromCorpus(String corpus_folder) {
        ArrayList<Document> documents = new ArrayList<Document>();

        // 对应的格式为   词项: [(docID, freq)]
        HashMap<String, ArrayList<ArrayList<Integer>>> postIndexMap = new HashMap<String, ArrayList<ArrayList<Integer>>>();

        Document doc = null;
        InvertedIndex index = new InvertedIndex();

        // 统计所有词汇的语聊
        int totalLengthOfDocuments = 0;
        int numberOfDocuments = 0;
        int docLength;
    	for (String path: listFilesForFolder(new File(corpus_folder))) {
    		if (numberOfDocuments % 100 == 0) log.info("Parsed " + numberOfDocuments + " Documents");
    		//documents.add
    		numberOfDocuments += 1;
    		try {
				doc = new Document(path);
				// TODO 这里需要分析一下其他几个field的内容， 然后将其存储到meta中
				docLength = 0;
				for (String term: doc.getTextTerms()) {
					totalLengthOfDocuments += 1;
					docLength += 1;
					if (postIndexMap.containsKey(term)) {
						ArrayList<ArrayList<Integer>> pairList = postIndexMap.get(term);
						if (pairList.get(pairList.size() - 1).get(0) == doc.getID()) {
							pairList.get(pairList.size() - 1).set(1, pairList.get(pairList.size() - 1).get(1) + 1);
						} else {
							ArrayList<Integer> new_pair = new ArrayList<Integer>();
							new_pair.add(doc.getID());
							new_pair.add(1);
							pairList.add(new_pair);
						}
					} else {
                        ArrayList<Integer> pair = new ArrayList<Integer>();
                        pair.add(doc.getID());
                        pair.add(1);
                        ArrayList<ArrayList<Integer>> element = new ArrayList<ArrayList<Integer>>();
                        element.add(pair);
						postIndexMap.put(term, element);
					}
				}
				index.addDocMeta(doc.getID(), new DocMeta(docLength));
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	index.setTotalDocumentLength(totalLengthOfDocuments);
    	index.setNumberOfDocuments(numberOfDocuments);
    	int term_index = 0;
		for (Map.Entry<String, ArrayList<ArrayList<Integer>>> entry: postIndexMap.entrySet()) {
			index.addElement(entry.getKey(), new Posting(entry.getValue(), term_index));
			term_index++;
		}
    	return index;
    }
    
	public static final void main(String args[]) {
		InvertedIndex index = new CorpusIndexMaker().makeIndexFromCorpus(corpus_folder);
    }
}