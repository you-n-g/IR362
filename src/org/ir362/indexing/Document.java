package org.ir362.indexing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.fnlp.nlp.cn.CNFactory;

public class Document {
    private static final Logger log = Logger.getLogger( Document.class.getName() );

    private String path;
    private int id;
    private boolean is_splitted;

    // 信息在此处 TODO: 信息格式转换
    private String title="";
    private String pubDate="";
    private String url="";
    private String commentNumber="";
    private String text="";

	public Document(String path, boolean is_splitted) throws IOException {
		this.path = path;
		this.is_splitted = is_splitted;
		String tmp[] = path.split("/");
		this.id = Integer.parseInt(tmp[tmp.length - 1]); // 获得用户id
		parse();
	}
	
	public int getID() {
		return this.id;
	}
	
	private static String title_prefix = "Title:";
	private static String pubDate_prefix = "pubDate:";
	private static String url_prefix = "url:";
	private static String commentNumber_prefix = "commentNumber:";
	private static String text_prefix = "text:";
	
	public void parseLine(String line) {
		if (line.startsWith(title_prefix)) {
			title = line.substring(title_prefix.length());
		}
		if (line.startsWith(pubDate_prefix)) {
			pubDate = line.substring(pubDate_prefix.length());
		}
		if (line.startsWith(url_prefix)) {
			url = line.substring(url_prefix.length());
		}
		if (line.startsWith(commentNumber_prefix)) {
			commentNumber = line.substring(commentNumber_prefix.length());
		}
		if (line.startsWith(text_prefix)) {
			text = line.substring(text_prefix.length());
		}
	}
	
	public void parse() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                parseLine(line);
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
        } finally {
            br.close();
        }
	}
	
	public String[] getTextTerms() {
		try {
			if (is_splitted) {
				return (title + "\t" + text).split("\t");
			} else {
				// TODO 这个地方希望将 text 和 title 都集中在一起
                return splitWords(text);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String[] getTitleTerms() {
		try {
			if (is_splitted) 
				return (title).split("\t");
			else 
                return splitWords(title);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	static CNFactory factory = null;
	public static String[] splitWords(String str) throws Exception {
        // 创建中文处理工厂对象，并使用“models”目录下的模型文件初始化
		if (factory == null)
             factory = CNFactory.getInstance("models");

        // 使用分词器对中文句子进行分词，得到分词结果
        String[] words = {};
		try {
            words = factory.seg(str);
		} catch (NullPointerException e) {
			log.info("Error when spliting:" + str);
		}
        return words;
	}

	public void saveParsedFile(String path) {
		// TODO 我这里是当字符串处理的， 如果类型变了可能会导致后面的冲突
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(path), "utf-8"));
            writer.write(title_prefix + StringUtils.join(splitWords(title), '\t'));
            writer.newLine();
            writer.write(pubDate_prefix + pubDate);
            writer.newLine();
            writer.write(url_prefix + url);
            writer.newLine();
            writer.write(commentNumber_prefix + commentNumber);
            writer.newLine();
            writer.write(text_prefix + StringUtils.join(splitWords(text), '\t'));
        } catch (Exception ex) {
          // report
        } finally {
           try {writer.close();} catch (Exception ex) {}
        }
	}
	

	private static void saveSplittedCorpus() throws IOException {
		int numberOfDocuments = 0;
		Document doc;
    	for (String path: CorpusIndexMaker.listFilesForFolder(new File(CorpusIndexMaker.corpus_folder))) {
    		if (numberOfDocuments % 100 == 0) log.info("Parsed " + numberOfDocuments + " Documents");
            doc = new Document(path, false);
            doc.saveParsedFile(new File(CorpusIndexMaker.splitted_corpus_folder, CorpusIndexMaker.getBaseName(path)).getPath());
    		numberOfDocuments += 1;
    	}
	}

    public static void main(String[] args) throws Exception {
    	/* 
        for(String word : splitWords("关注自然语言处理、语音识jj别、深度学习等方向的前沿技术和业界动态。")) {
            System.out.print(word + "|");
        }
        System.out.println();
        */
    	//saveSplittedCorpus();  // 我的功能是将 未分词的语聊转换为已分词语料
    }
}
