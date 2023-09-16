package com.yeoboge.server.helper.utils;

import com.yeoboge.server.enums.error.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 보드게임 데이터 등 csv 파일을 파싱하여 DB에 저장하기 위한 메서드를 포함한 유틸 클래스
 *
 * @author Seo Jeong Hee
 */
public class CsvParsing {
    private String filePath;
    private BufferedReader bufferedReader;
    private List<String[]> readCSV;
    private int index;

    /**
     * 특정 경로의 csv 파일을 읽어와서 {@link BufferedReader} 에 저장함
     *
     * @param filePath 읽어올 csv 파일의 경로
     * @throws IOException
     */
    public CsvParsing(String filePath) throws IOException {
        this.filePath = filePath;
        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.filePath),
                "UTF-8"));
        readCSV = new ArrayList<>();
        makeList(bufferedReader);
        this.index = 0;
    }

    /**
     * {@link BufferedReader} 에서 한줄씩 읽어오면, 해당 줄을 readCSV 리스트에 저장함
     *
     * @throws IOException
     */
    public void makeList(BufferedReader bufferedReader) throws IOException {
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            String[] lineContents = line.split("\t",-1);
            readCSV.add(lineContents);
        }
    }

    /**
     * 특정 csv 파일을 한줄씩 저장한 리스트를 한줄씩 읽음
     *
     * @return 특정 csv 파일의 한 줄을 리턴함
     */
    public String[] nextRead(){
        if(readCSV.size() == index){
            return null;
        }
        return readCSV.get(index++);
    }

}
