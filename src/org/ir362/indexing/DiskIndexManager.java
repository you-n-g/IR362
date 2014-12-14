package org.ir362.indexing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.ir362.Config;

import edu.uci.jforests.util.Pair;

/**
 * 约定好的索引格式如下
 * 
 * data.meta
 * 	第一行: 文档数量
 * 	第二行: 总的文档长度
 * 
 * data.dict
 * 	每一行: 词项\tDocumentFreq
 * 
 * data.posting
 * 	每一行: 
 * 		对应到 data.dict中的每一行，然后有 DocumentFreq * 2 项
 * 		每项 的内容 格式为  <docid tf>
 * 
 * data.doc.meta
 * 	每一行:
 * 		DocID +  TODO 各种信息
 * 
 * 
 * @author young
 *
 */

public class DiskIndexManager {
    private static final Logger log = Logger.getLogger( DiskIndexManager.class.getName() );
	
	public String index_folder;

	public DiskIndexManager(String index_folder) {
		this.index_folder = index_folder;
	}

    private String getCollectionMetaPath() {
    	return index_folder + "data.meta";
    }
    
    private void saveCollectionMeta(InvertedIndex index) {
    	log.info("Creating Meta.......");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(getCollectionMetaPath()), "utf-8"));
            writer.write(String.valueOf(index.getNumberOfDocuments()));
            writer.newLine();
            writer.write(String.valueOf(index.getTotalDocumentLength()));
        } catch (IOException ex) {
          // report
        } finally {
           try {writer.close();} catch (Exception ex) {}
        }
    }

    private String getCollectionDictPath() {
    	return index_folder + "data.dict";
    }

    private void saveCollectionDict(InvertedIndex index) {
    	log.info("Creating Dict.......");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(getCollectionDictPath()), "utf-8"));
            for (int i = 0; i < index.terms.size(); ++i) {
                if (i % 8000 == 0) log.info("" + i + "dict Finished");
            	if (i != 0) writer.newLine();
            	writer.write(index.terms.get(i));
                writer.write("\t");
            	writer.write(String.valueOf(index.termPostingMap.get(index.terms.get(i)).df));
            }
        } catch (IOException ex) {
          // report
        } finally {
           try {writer.close();} catch (Exception ex) {}
        }
	}

    private String getCollectionPostingPath() {
    	return index_folder + "data.posting";
    }

	private void saveCollectionPosting(InvertedIndex index) {
    	log.info("Creating PostingIndex.......");
        Posting p = null;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(getCollectionPostingPath()), "utf-8"));
            for (int i = 0; i < index.terms.size(); ++i) {
                if (i % 8000 == 0) log.info("" + i + "PostingIndex Finished");
            	if (i != 0) writer.newLine();
            	p = index.termPostingMap.get(index.terms.get(i));
            	for (int j = 0; j < p.df; ++j) {
                    if (j != 0) writer.write("\t");
                    writer.write(String.valueOf(p.data[j][0]));
                    writer.write("\t");
                    writer.write(String.valueOf(p.data[j][1]));
            	}
            }
        } catch (IOException ex) {
          // report
        } finally {
           try {writer.close();} catch (Exception ex) {}
        }
	}

    private String getCollectionDocMetaPath() {
    	return index_folder + "data.doc.meta";
    }

	private void saveCollectionDocMeta(InvertedIndex index) {
    	log.info("Creating DocMeta.......");
        BufferedWriter writer = null;
        boolean flag;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(getCollectionDocMetaPath()), "utf-8"));
            
            int i = 0;
            flag = true;
            for (Entry<Integer, DocMeta> entry: index.documentsMetaInfo.entrySet()) {
            	++i;
                if (i % 8000 == 0) log.info("" + i + "DocMeta Finished");
            	if (flag)
            		flag = false;
            	else 
            	writer.newLine();
                writer.write(String.valueOf(entry.getKey()));
                writer.write("\t");
                writer.write(String.valueOf(entry.getValue().docLength));
                writer.write("\t");
                writer.write(String.valueOf(entry.getValue().commentNumber));
                writer.write("\t");
                writer.write(entry.getValue().pubDate);
                writer.write("\t");
                writer.write(entry.getValue().url);
                writer.write("\t");
                writer.write(entry.getValue().title);
            }
        } catch (IOException ex) {
          // report
        } finally {
           try {writer.close();} catch (Exception ex) {}
        }
	}

    private String getCollectionTitlePostingPath() {
    	return index_folder + "data.title.posting";
    }

	private void saveCollectionTitlePosting(InvertedIndex index) {
    	log.info("Creating Title PostingIndex.......");
        Posting p = null;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(getCollectionTitlePostingPath()), "utf-8"));
            for (int i = 0; i < index.terms.size(); ++i) {
                if (i % 2000 == 0) log.info("" + i + " title Posting Finished");
            	if (i != 0) writer.newLine();
            	p = index.termTitlePostingMap.get(index.terms.get(i));
            	for (int j = 0; j < p.df; ++j) {
                    if (j != 0) writer.write("\t");
                    writer.write(String.valueOf(p.data[j][0]));
                    writer.write("\t");
                    writer.write(String.valueOf(p.data[j][1]));
            	}
            }
        } catch (IOException ex) {
          // report
        } finally {
           try {writer.close();} catch (Exception ex) {}
        }
	}

    private String getCollectionDirectPostingPath() {
    	return index_folder + "data.direct.posting";
    }

    //正排索引相关
	public void saveCollectionDirectPosting(InvertedIndex index) {
    	log.info("Creating Direct PostingIndex.......");
        Posting p = null;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(getCollectionDirectPostingPath()), "utf-8"));

            DirectIndex dindex = new DirectIndex();
            dindex.loadFromInvertedIndex(index);

            int i = 0;
            for (Map.Entry<Integer,ArrayList<Pair<Integer, Integer>>> entry: dindex.docPosting.entrySet()) {
                if (i % 2000 == 0) log.info("" + i + " Direct Posting Finished");
            	if (i++ != 0) writer.newLine();
                writer.write(String.valueOf(entry.getKey()));
            	for (Pair<Integer, Integer> pair: entry.getValue()) {
                    writer.write("\t");
                    writer.write(String.valueOf(pair.getFirst()));
                    writer.write("\t");
                    writer.write(String.valueOf(pair.getSecond()));
            	}
            }
        } catch (IOException ex) {
          // report
        } finally {
           try {writer.close();} catch (Exception ex) {}
        }
	}


    public void saveIndexToDisk(InvertedIndex index) {
    	saveCollectionMeta(index);
    	saveCollectionDict(index);
    	saveCollectionPosting(index);
    	saveCollectionDocMeta(index);
    	saveCollectionTitlePosting(index);
    	saveCollectionDirectPosting(index);
    }

	private void loadCollectionMeta(InvertedIndex index) throws IOException {
    	log.info("Loading DocMeta.......");
        BufferedReader br = new BufferedReader(new FileReader(getCollectionMetaPath()));
        String line;
        try {
            line = br.readLine();
            index.setNumberOfDocuments(Integer.parseInt(line));
            line = br.readLine();
            index.setTotalDocumentLength(Integer.parseInt(line));
        } finally {
            br.close();
        }
	}

	private void loadCollectionDict(InvertedIndex index) throws IOException {
    	log.info("Loading Dict.......");
        BufferedReader br = new BufferedReader(new FileReader(getCollectionDictPath()));
        String line;
        String items[];
        int term_index=0;
        try {
            line = br.readLine();
            while (line != null) {
            	items = line.split("\t");
            	index.terms.add(items[0]);
            	index.termPostingMap.put(items[0], new Posting(term_index, Integer.parseInt(items[1])));
            	// 这里也要初始化title的倒排索引
            	index.termTitlePostingMap.put(items[0], new Posting(term_index));
                line = br.readLine();
                if (term_index % 2000 == 0) log.info("" + term_index + " terms in Dict loading Finished");
                term_index += 1;
            }
        } finally {
            br.close();
        }
	}

	private void loadCollectionPosting(InvertedIndex index) throws IOException {
    	log.info("Loading Posting.......");
        BufferedReader br = new BufferedReader(new FileReader(getCollectionPostingPath()));
        String line;
        String items[];
        Posting p;
        int term_index=0;
        try {
            line = br.readLine();
            while (line != null) {
            	items = line.split("\t");
            	p = index.termPostingMap.get(index.terms.get(term_index));
            	for (int i = 0; i < p.df; ++i) {
            		p.data[i][0] = Integer.parseInt(items[i * 2]);
            		p.data[i][1] = Integer.parseInt(items[i * 2 + 1]);
            	}
                line = br.readLine();
                if (term_index % 2000 == 0) log.info("" + term_index + " Posting loading Finished");
                term_index += 1;
            }
        } finally {
            br.close();
        }
	}

	private void loadCollectionDocMeta(InvertedIndex index) throws IOException{
    	log.info("Loading DocMeta.......");
        BufferedReader br = new BufferedReader(new FileReader(getCollectionDocMetaPath()));
        String line;
        String items[];
        int finished_docs = 0;
        try {
            line = br.readLine();
            while (line != null) {
            	items = line.split("\t");
            	index.documentsMetaInfo.put(Integer.parseInt(items[0]),
            			new DocMeta(Integer.parseInt(items[1]), Integer.parseInt(items[2]), items[3], items[4], items[5]));
                line = br.readLine();
                if (finished_docs % 2000 == 0) log.info("" + finished_docs + " DocMeta loading Finished");
                finished_docs += 1;
            }
        } finally {
            br.close();
        }
	}

	private void loadCollectionTitlePosting(InvertedIndex index) throws IOException {
    	log.info("Loading Title Posting.......");
        BufferedReader br = new BufferedReader(new FileReader(getCollectionPostingPath()));
        String line;
        String items[];
        Posting p;
        int term_index=0;
        try {
            line = br.readLine();
            while (line != null) {
            	items = line.split("\t");
            	p = index.termTitlePostingMap.get(index.terms.get(term_index));
            	p.init_data(items.length / 2);
            	for (int i = 0; i < p.df; ++i) {
            		p.data[i][0] = Integer.parseInt(items[i * 2]);
            		p.data[i][1] = Integer.parseInt(items[i * 2 + 1]);
            	}
                line = br.readLine();
                if (term_index % 2000 == 0) log.info("" + term_index + "title Posting loading Finished");
                term_index += 1;
            }
        } finally {
            br.close();
        }
	}

    public InvertedIndex loadIndexFromDisk() {
    	InvertedIndex index = new InvertedIndex();
    	try {
			loadCollectionMeta(index);
            loadCollectionDict(index);
            loadCollectionPosting(index);
            loadCollectionDocMeta(index);
            loadCollectionTitlePosting(index);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return index;
    }
    
    public static void createCorpus() {
//    	new DiskIndexManager(Config.index_folder).saveIndexToDisk(new CorpusIndexMaker().makeIndexFromCorpus(CorpusIndexMaker.splitted_corpus_folder, true));
    	new DiskIndexManager(Config.index_folder).saveIndexToDisk(new CorpusIndexMaker().makeIndexFromCorpus(CorpusIndexMaker.corpus_folder, false));
    }
    
    public static void clearDiskIndexStopWords() {
        DiskIndexManager dmanager = new DiskIndexManager(Config.index_folder);
    	InvertedIndex index = dmanager.loadIndexFromDisk();
    	index.clearStopWords();
    	dmanager.saveIndexToDisk(index);
    }

    public static void update_title() {
    	DiskIndexManager dm = new DiskIndexManager(Config.index_folder);
    	CorpusIndexMaker cm = new CorpusIndexMaker();
    	InvertedIndex index = dm.loadIndexFromDisk();
    	cm.reloadMetaDocTitleFromDoc(index, CorpusIndexMaker.corpus_folder);
    	dm.saveCollectionDocMeta(index);
    }
    
	public static final void main(String args[]) {
		 createCorpus();
//		update_title();
		//new DiskIndexManager().loadIndexFromDisk();
    }
}