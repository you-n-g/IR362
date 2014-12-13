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

import edu.uci.jforests.util.Pair;
import org.ir362.Config;


/* 这里假设语料的文件名就是数字，而且就是文档id */

public class CorpusIndexMaker {
    private static final Logger log = Logger.getLogger( CorpusIndexMaker.class.getName() );
    public final static String  corpus_folder = Config.project_folder_path + "corpus/";
    public final static String  splitted_corpus_folder = Config.project_folder_path + "splitted_corpus/";
    
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
    
    public static String getBaseName(String path) {
    	if (path == null) return null;
    	String[] st=path.split("/");
    	return st[st.length - 1];
    }

    public InvertedIndex makeIndexFromCorpus(String corpus_folder, boolean is_splitted) {
    	// 会将语料庫load成索引，  这里可能不包含 title 的倒排索引

        // 对应的格式为   词项: [(docID, freq)]
        HashMap<String, ArrayList<Pair<Integer, Integer>>> postIndexMap = new HashMap<String, ArrayList<Pair<Integer, Integer>>>();

        Document doc = null;
        InvertedIndex index = new InvertedIndex();

        // 统计所有词汇的语聊
        int totalLengthOfDocuments = 0;
        int numberOfDocuments = 0;
        int docLength;
    	for (String path: listFilesForFolder(new File(corpus_folder))) {
    		if (numberOfDocuments % 500 == 0) {
    			log.info("Parsed " + numberOfDocuments + " Documents");
    		}
    		numberOfDocuments += 1;
    		try {
				doc = new Document(path, is_splitted);
				// TODO 这里需要分析一下其他几个field的内容， 然后将其存储到meta中
				docLength = 0;
				Pair<Integer, Integer> tmp_pair;
				for (String term: doc.getTextTerms()) {
					totalLengthOfDocuments += 1;
					docLength += 1;
					if (postIndexMap.containsKey(term)) {
						ArrayList<Pair<Integer, Integer>> pairList = postIndexMap.get(term);
						tmp_pair = pairList.get(pairList.size() - 1);
						if (tmp_pair.getFirst() == doc.getID())
							tmp_pair.snd += 1;
						else
							pairList.add(new Pair<Integer, Integer>(doc.getID(), 1));
					} else {
                        ArrayList<Pair<Integer, Integer>> element = new ArrayList<Pair<Integer, Integer>>();
                        element.add(new Pair<Integer, Integer>(doc.getID(), 1));
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
		for (Map.Entry<String, ArrayList<Pair<Integer, Integer>>> entry: postIndexMap.entrySet()) {
			index.addElement(entry.getKey(), new Posting(entry.getValue(), term_index));
			postIndexMap.remove(entry); // 为了释放内存
			term_index++;
		}
		makeTitlePostingIndexFromCorpus(index, corpus_folder, is_splitted); // 最后才加载titlePosting
    	return index;
    }
    
    /**
     * 我为什么不集成在 makeIndexFromCorpus 里面呢？？？？
     * 直接写在那里面固然方便， 但是
     * 
     * 因为 我们面对的问题是 已经建好了其他索引，  但是title的索引还没建好
     * 所以将从 disk load 过来的 index放这边， 然后再去从 corpus中加载title
     * 
     * @param index
     */
    public void makeTitlePostingIndexFromCorpus(InvertedIndex index, String corpus_folder,  boolean is_splitted) {
        // 对应的格式为   词项: [(docID, freq)]
        HashMap<String, ArrayList<Pair<Integer, Integer>>> postIndexMap = new HashMap<String, ArrayList<Pair<Integer, Integer>>>();

        //先将词典加载上来
        for (String st: index.terms)
        	postIndexMap.put(st, new ArrayList<Pair<Integer, Integer>>());

        Document doc = null;

        int numberOfDocuments = 0;
    	for (String path: listFilesForFolder(new File(corpus_folder))) {
    		if (numberOfDocuments % 500 == 0) {
    			log.info("Parsed " + numberOfDocuments + " Documents' title!!!");
    		}
    		numberOfDocuments += 1;
    		try {
				doc = new Document(path, is_splitted);
				// TODO 这里需要分析一下其他几个field的内容， 然后将其存储到meta中
				Pair<Integer, Integer> tmp_pair;
				for (String term: doc.getTitleTerms()) {
					// ** 因为建立text索引时title已经放在text中， 所以title中的每个词汇必定在text中了 ** 
                    ArrayList<Pair<Integer, Integer>> pairList = postIndexMap.get(term);
                    if (pairList.size() > 0 &&  pairList.get(pairList.size() - 1).getFirst() == doc.getID()) {
                        pairList.get(pairList.size() - 1).snd += 1;
                    }
                    else
                        pairList.add(new Pair<Integer, Integer>(doc.getID(), 1));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	int term_index = 0;
		for (Map.Entry<String, ArrayList<Pair<Integer, Integer>>> entry: postIndexMap.entrySet()) {
			index.termTitlePostingMap.put(entry.getKey(), new Posting(entry.getValue(), term_index));
			postIndexMap.remove(entry); // 为了释放内存
			term_index++;
		}
    }

	public static final void main(String args[]) {
		//InvertedIndex index = new CorpusIndexMaker().makeIndexFromCorpus(splitted_corpus_folder, true);
    }
}