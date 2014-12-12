package org.ir362.indexing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;

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
	
	public static final String index_folder="/home/young/workspace4.4/IR362/index/";

	public DiskIndexManager() {
	}

    private String getCollectionMetaPath() {
    	return index_folder + "data.meta";
    }
    
    private void saveCollectionMeta(InvertedIndex index) {
    	System.out.println("Creating Meta.......");
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
    	System.out.println("Creating Dict.......");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(getCollectionDictPath()), "utf-8"));
            for (int i = 0; i < index.terms.size(); ++i) {
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
    	System.out.println("Creating PostingIndex.......");
        Posting p = null;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(getCollectionPostingPath()), "utf-8"));
            for (int i = 0; i < index.terms.size(); ++i) {
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
    	System.out.println("Creating DocMeta.......");
        BufferedWriter writer = null;
        boolean flag;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(getCollectionDocMetaPath()), "utf-8"));
            
            flag = true;
            for (Entry<Integer, DocMeta> entry: index.documentsMetaInfo.entrySet()) {
            	if (flag)
            		flag = false;
            	else 
            		writer.newLine();
                writer.write(String.valueOf(entry.getKey()));
                writer.write("\t");
                writer.write(String.valueOf(entry.getValue().docLength));
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
    }

	private void loadCollectionMeta(InvertedIndex index) throws IOException {
    	System.out.println("Loading DocMeta.......");
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
    	System.out.println("Loading Dict.......");
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
                line = br.readLine();
                term_index += 1;
            }
        } finally {
            br.close();
        }
	}

	private void loadCollectionPosting(InvertedIndex index) throws IOException {
    	System.out.println("Loading Posting.......");
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
                term_index += 1;
            }
        } finally {
            br.close();
        }
	}

	private void loadCollectionDocMeta(InvertedIndex index) throws IOException{
    	System.out.println("Loading DocMeta.......");
        BufferedReader br = new BufferedReader(new FileReader(getCollectionDocMetaPath()));
        String line;
        String items[];
        try {
            line = br.readLine();
            while (line != null) {
            	items = line.split("\t");
            	index.documentsMetaInfo.put(Integer.parseInt(items[0]), new DocMeta(Integer.parseInt(items[1])));
                line = br.readLine();
            }
        } finally {
            br.close();
        }
	}

    public InvertedIndex loadIndexFromDisk() {
    	// TODO  如何从磁盘中读取索引
    	InvertedIndex index = new InvertedIndex();
    	try {
			loadCollectionMeta(index);
            loadCollectionDict(index);
            loadCollectionPosting(index);
            loadCollectionDocMeta(index);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return index;
    }

	public static final void main(String args[]) {
    	//new DiskIndexManager().saveIndexToDisk(new CorpusIndexMaker().makeIndexFromCorpus(CorpusIndexMaker.corpus_folder));
		new DiskIndexManager().loadIndexFromDisk();
    }
}