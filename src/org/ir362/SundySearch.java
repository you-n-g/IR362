package org.ir362;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class SundySearch {
    String text = null;
    String pattern = null;
    int currentPos = 0;

    /**
     * 匹配后的子串第一个字符位置列表
     */
    List<Integer> matchedPosList = new LinkedList<Integer>();
    
    /**
     * 匹配字符的Map,记录改匹配字符串有哪些char并且每个char最后出现的位移
     */
    Map<Character, Integer> map = new HashMap<Character, Integer>();

    public SundySearch(String text, String pattern) {
        this.text = text;
        this.pattern = pattern;
        this.initMap();
    };

    /**
     * Sunday匹配时，用来存储Pattern中每个字符最后一次出现的位置，从左到右的顺序
     */
    private void initMap() {
        for (int i = 0; i < pattern.length(); i++) {
            this.map.put(pattern.charAt(i), i);

        }
    }

    /**
     * 普通的字符串递归匹配，匹配失败就前进一位
     */
    public List<Integer> normalMatch() {
        //匹配失败，继续往下走
        if (!matchFromSpecialPos(currentPos)) {
            currentPos += 1;

            if ((text.length() - currentPos) < pattern.length()) {
                return matchedPosList;
            }
            normalMatch();
        } else {
            //匹配成功，记录位置
            matchedPosList.add(currentPos);
            currentPos += 1;
            normalMatch();
        }
        
        return matchedPosList;
    }

    /**
     * Sunday匹配，假定Text中的K字符的位置为：当前偏移量+Pattern字符串长度+1
     */
    public List<Integer> sundayMatch() {
        // 如果没有匹配成功
        if (!matchFromSpecialPos(currentPos)) {
            // 如果Text中K字符没有在Pattern字符串中出现，则跳过整个Pattern字符串长度
            if ((currentPos + pattern.length() + 1) < text.length()
                    && !map.containsKey(text.charAt(currentPos + pattern.length() + 1))) {
                currentPos += pattern.length();
            }else {
                // 如果Text中K字符在Pattern字符串中出现，则将Text中K字符的位置和Pattern字符串中的最后一次出现K字符的位置对齐
                if ((currentPos + pattern.length() + 1) > text.length()) {
                    currentPos += 1;
                } else {
                    currentPos += pattern.length() - (Integer) map.get(text.charAt(currentPos + pattern.length()));
                }
            }
            
            // 匹配完成，返回全部匹配成功的初始位移
            if ((text.length() - currentPos) < pattern.length()) {
                return matchedPosList;
            }
            
            sundayMatch();
        }else {
            // 匹配成功前进一位然后再次匹配
            matchedPosList.add(currentPos);
            currentPos += 1;
            sundayMatch();
        }
        return matchedPosList;
    }

    /**
     * 检查从Text的指定偏移量开始的子串是否和Pattern匹配
     */
    public boolean matchFromSpecialPos(int pos) {
        if ((text.length()-pos) < pattern.length()) {
            return false;
        }

        for (int i = 0; i < pattern.length(); i++) {
            if (text.charAt(pos + i) == pattern.charAt(i)) {
                if (i == (pattern.length()-1)) {
                    return true;
                }
                continue;
            } else {
                break;
            }
        }
        
        return false;
    }

    public static void main(String[] args) {
    	String str = "hello 啊啊 阿道夫 adfsadfklf adf234masdfsdfdsfdsfdsffwerwrewrerwerwersdf2666sdflsdfk";
    	String gap =  "adf";
        SundySearch sundySearch = new SundySearch(str,gap);
        
        long begin = System.nanoTime();
        System.out.println("NormalMatch:" + sundySearch.normalMatch());
        System.out.println("NormalMatch:" + sundySearch.normalMatch());
        System.out.println("NormalMatch:" + (System.nanoTime() - begin));
        int[] a = new int[sundySearch.normalMatch().size()]; 
        for(int i = 0;i<sundySearch.normalMatch().size();i++){
        	a[i] = sundySearch.normalMatch().get(i);
        	System.out.println(a[i]);
        }
        System.out.println(str.substring(a[0], a[1]));
        
        begin = System.nanoTime();
        System.out.println("SundayMatch:" + sundySearch.sundayMatch());
        System.out.println("SundayMatch:" + (System.nanoTime() - begin));

    }
}