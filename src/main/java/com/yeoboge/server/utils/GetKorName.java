package com.yeoboge.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class GetKorName {
    public static boolean isKoreanCharacter(String str) {
        return str.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
    }
    public static String getKorName(String xmlData){
        String ret = "";
        try {
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode rootNode = xmlMapper.readTree(xmlData);

            JsonNode boardGameNode = rootNode.get("boardgame");
            if (boardGameNode != null) {
                JsonNode nameNode = boardGameNode.get("name");
                for (JsonNode jsonNode : nameNode) {
                    String name = String.valueOf(jsonNode.get(""));
                    if(isKoreanCharacter(name)){
                        System.out.println(name);
                        ret = name.replaceAll("\"","");
                    }
                }
                return ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
