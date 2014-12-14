package org.ir362;

import java.awt.print.Printable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import edu.uci.jforests.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.ir362.indexing.DiskIndexManager;
import org.ir362.indexing.DocMeta;
import org.ir362.indexing.InvertedIndex;
import org.ir362.matching.ResultSet;
import org.ir362.querying.Manager;
import org.ir362.querying.Request;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class SearchController {
	public String snippet = "";
	int i = snippet.length();
	public String pubDate = "";
	private static String text_prefix = "text:";
	private static String pubDate_prefix = "pubDate:";
	public final static String  files_folder = Config.project_folder_path + "corpus/";
	
	static SearchController sController = null;
	
	
	public String[] last_search_terms; // TODO 这个应该和某次搜索联系起来
	
	InvertedIndex index;
	
	public SearchController(){
        index = new DiskIndexManager(Config.index_folder).loadIndexFromDisk();;
	}
	
	public String getSnippet(int docID, String[] terms) {//test file：9870
		//找到文章里面相应字符串的内容，然后添加到字符串里面
		String path = files_folder + docID;
		String Text = "";
		FileInputStream fileS = null;
		BufferedReader br = null;
		//记录terms出现在那些位置,第一维是编号，第二维是位置信息
		int[][] positionOfQuery = new int[terms.length][];
		//用于记录位置一样的信息
		ArrayList<Integer> finalPosition = new ArrayList<Integer>(); 
//		finalPosition.add(1,1);
		
		try {
			fileS = new FileInputStream(new File(path));
			br = new BufferedReader(new InputStreamReader(fileS,"UTF-8"));
			
			
        	String str = "";
        	str = br.readLine();   
             
            str = br.readLine(); 
        	if (str.startsWith(pubDate_prefix)) {
        		pubDate = str.substring(pubDate_prefix.length());
    		}
          
            str = br.readLine(); 
            str = br.readLine(); 
             
            str = br.readLine(); 
            if (str.startsWith(text_prefix)) {
            	Text = str.substring(text_prefix.length());
    		}
            int number = 0;
            
			//for  ：查询词,循环查出这些字串都在Text所有位置
            for(int i = 0;i<terms.length;i++){
            	SundySearch sundySearch = new SundySearch(Text,terms[i]);
            	positionOfQuery[i] = new int[sundySearch.normalMatch().size()];
            	for(int j = 0;j < sundySearch.normalMatch().size();j++){
            		positionOfQuery[i][j] = sundySearch.normalMatch().get(j);
            		number++;
            	}
            	
            }
            
            
            //for ：比较这些词都在什么位置
            //都和第一个比较，if位置相同，则将其放入array表        
           
            int[] position = new int[number];
            int count = 0;
            for(int i = 0;i<positionOfQuery.length;i++){
            	for(int j = 0;j<positionOfQuery[i].length;j++){
            		position[count] = positionOfQuery[i][j];
            		count++;
            		}
            }
            
            //查这个array表，如果非空，则进行排序，计算出能够达到110字符的最大间距，获取这个区间的字符串给snippet
            //否则，计算positionOfQuery中满足小于110字符的最大长度的两个位置，获取这个区间的字符串给snippet
            //如果只有一个存在的话，把从第一个开始+70这个区间的字符串给snippet
            int beginIndex = 0;
            int endIndex = 110;
            int distance = 0;
            int distanceMax = 0;
//            int[] position = new int[finalPosition.size()];
            for(int p = 0;p<finalPosition.size();p++){
            	position[p] = (int) finalPosition.get(p);
            }		
			 Arrays.sort(position);
            
            if(finalPosition.size() != 1){
            	
    			 for(int i = 0;i<position.length;i++){
    				 for(int j=0;j<position.length;j++){
    					 distance = Math.abs(position[i]-position[j]);
    					 if((distance<=110)&&(distance>=distanceMax)){
    						 distanceMax = distance;
    						if(position[i]>position[j]){
    							beginIndex = position[j];
    							endIndex = position[i];
    						}
    						if(position[i]<position[j]){
    							beginIndex = position[i];
    							endIndex = position[j];
    							
    						}
    					 }
    						 
    				 }
    			 }
    			 if((endIndex-beginIndex)<110){
    				 if(((Text.length()-endIndex)<(110-(endIndex-beginIndex))))
    					 snippet = Text.substring(beginIndex-5, Text.length());
    				 else
    					 snippet = Text.substring(beginIndex, endIndex+(110-(endIndex-beginIndex)));
    			 }
    			 else
    				 snippet = Text.substring(beginIndex, endIndex);
            }else {
            	snippet = Text.substring(0, 110);;
            }
			
            
			br.close();
			fileS.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return pubDate +"-"+snippet+"...";
	}

	public static void testGetSnippet() {
        SearchController sc = getController();
        String[] terms = {"掩护","张娴"};
        String result = sc.getSnippet(9870, terms);
        System.out.println(result);
	}
	
	public ArrayList<Integer> getDocIDs(String query) {
		ArrayList<Integer> docIDs = new ArrayList<Integer>();
		Manager queryingManager = new Manager(index);

		Request rq = queryingManager.newRequest(query);

		queryingManager.preProcess(rq);  // we obtain
		queryingManager.runMatching(rq);
		ResultSet rs = rq.getResultSet();

		int rank = 0;
		for(Pair<Integer, Double> p : rs.getResultArray())
            docIDs.add(p.getFirst());
		
		ArrayList<String> search_terms = rq.getMatchingTerms().getTerms();
		this.last_search_terms =  search_terms.toArray(new String[search_terms.size()]);
		return docIDs;
	}
	
	public String getJsonDocIDs(String query) {
        ArrayList<Integer> docIDs = getDocIDs(query);
		JSONArray json_arr = new JSONArray();
		for (int id: docIDs)
            json_arr.add(id);
		return json_arr.toJSONString();
	}
	
	public String getSingleDocJsonInfo(int docID, String[] terms) {
		if (terms == null)
			terms = new String[]{};
		JSONObject json = new JSONObject();
		DocMeta docMeta = index.documentsMetaInfo.get(docID);
		if (docMeta == null)
            return "";
		json.put("id", docID);
		json.put("title", docMeta.title);
		json.put("url", docMeta.url);
		json.put("commentNumber", docMeta.commentNumber);
		json.put("date", docMeta.pubDate);
		json.put("snippet", getSnippet(docID, terms));
		return json.toJSONString();
	}
	
	static public SearchController getController() {
		if (sController == null) {
			sController = new SearchController();
		}
		return sController;
	}

	public static void testAPI() {
		SearchController sc = new SearchController();
		System.out.println(sc.getJsonDocIDs("新闻体育"));
		//System.out.println(sc.getSingleDocJsonInfo(88825, sc.last_search_terms));
		System.out.println(sc.getSingleDocJsonInfo(12345, sc.last_search_terms));
	}

    public static void main(String[] args) {
    	testAPI();
    }
}
