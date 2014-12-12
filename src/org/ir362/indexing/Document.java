package org.ir362.indexing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.bind.ParseConversionEvent;

import org.fnlp.nlp.cn.CNFactory;
import org.fnlp.util.exception.LoadModelException;

import edu.uci.jforests.util.ArraysUtil;

public class Document {
    private String path;
    private int id;

    // 信息在此处 TODO: 信息格式转换
    private String title="";
    private String pubDate="";
    private String url="";
    private String commentNumber="";
    private String text="";

	public Document(String path) throws IOException {
		this.path = path;
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
			return splitWords(text);
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
        String[] words = factory.seg(str);
        return words;
	}
    public static void main(String[] args) throws Exception {
        for(String word : splitWords("关注自然语言处理、语音识别、深度学习等方向的前沿技术和业界动态。")) {
            System.out.print(word + "|");
        }
        System.out.println();
    }
}
