package com.yeoboge.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.util.ArrayList;

/**
 * 보드게임 데이터에서 한글화 여부를 판별하기 위한 유틸 클래스
 *
 * @author Seo Jeong Hee
 */
public class GetKorName {

    /**
     * 특정 문자열에 한글이 포함되어있는지 확인함
     *
     * @param str 확인할 문자열
     */
    public static boolean isKoreanCharacter(String str) {
        return str.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
    }

    /**
     * 보드게임 정보 xml 에서 보드게임의 이름에 해당하는 노드의 값을 찾아 리턴함
     *
     */
    public static String getKorName(String xmlData){
        String ret = "";
        try {
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode rootNode = xmlMapper.readTree(xmlData);
            JsonNode boardGameNode = rootNode.get("boardgame");
            if (boardGameNode != null) {
                JsonNode nameNode = boardGameNode.get("name");
                if(nameNode!=null) {
                    for (JsonNode jsonNode : nameNode) {
                        String name = String.valueOf(jsonNode.get(""));
                        if (isKoreanCharacter(name)) {
                            ret = name.replaceAll("\"", "");
                        }
                    }
                }
                return ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 보드게임 정보 xml 에서 보드게임의 카테고리에 해당하는 노드의 값을 찾아 리턴함
     *
     */
    public static ArrayList<String> getCategory(String xmlData){
        ArrayList<String> ret = new ArrayList<>();
        try {
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode rootNode = xmlMapper.readTree(xmlData);
            JsonNode boardGameNode = rootNode.get("boardgame");
            if (boardGameNode != null) {
                JsonNode categoryNode = boardGameNode.get("boardgamesubdomain");
                if(categoryNode!=null) {
                    for (JsonNode jsonNode : categoryNode) {
                        if(jsonNode.asText().contains("Games")){
                            ret.add(jsonNode.asText().split(" ")[0]);
                        }
                    }
                    return ret;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
