package com.example.switchbot2;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TextFile {
    //TODO ==== 전역변수 정의 부분 ====
    private String saveStorage = ""; //저장된 파일 경로
    private String saveData = ""; //저장된 파일 내용
    private String textFileName = "/Data.txt";
    private File storageDir;

    private String posOn;
    private String posCommon;
    private String posOff;
    private String onTimer;
    private String offTimer;

    public TextFile() {
        //TODO 파일 생성
        storageDir =  new File("/data/user/0/com.example.switchbot2/files/" + "/SaveStorage"); //TODO 저장 경로
        //TODO 폴더 생성
        if(!storageDir.exists()){ //TODO 폴더 없을 경우
            storageDir.mkdir(); //TODO 폴더 생성
        }
        saveStorage = String.valueOf(storageDir+textFileName); //TODO 경로 저장 /storage 시작
        if(getSaveText() !=null) {
            SplitWord(getSaveText());
        }
    }


    //TODO ==== 텍스트 저장 메소드 ====
    public void setSaveText(String data){
        try {
            Log.d("Test","setSaveText start : "+data);
            saveData = data; //TODO 변수에 값 대입

            //BufferedWriter buf = new BufferedWriter(new FileWriter(storageDir+textFileName, true)); //TODO 다중으로 내용적음 (TRUE)
            BufferedWriter buf = new BufferedWriter(new FileWriter(saveStorage, false)); //TODO 한개 내용만 표시됨 (FALSE)
            buf.append(saveData); //TODO 날짜 쓰기
            buf.newLine(); //TODO 개행
            buf.close();
          //  SplitWord(data);

           // Log.d("Test","setSaveText : "+data);
        }
        catch (Exception e){
            //Log.d("Text","setSaveText Error "+e);
            e.printStackTrace();
        }
    }



    //TODO ==== 텍스트 호출 메소드 ====
    public String getSaveText(){
        try {
           // Log.d("Test","saveStorage : "+storageDir);
            if(saveStorage != null && saveStorage.length() > 0){
                String line = ""; //TODO 한줄씩 읽기
                try {
                    BufferedReader buf = new BufferedReader(new FileReader(saveStorage));
                    line=buf.readLine();
                    buf.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return line;
            }
            else {

              //  Log.d("Test","저장된 텍스트가 없습니다. 텍스트를 저장해주세요");
               // Log.d("Test","saveStorage : "+storageDir);
            }
        }
        catch (Exception e){

            e.printStackTrace();
        }
        return null;
    }

    private void SplitWord(String text) {
        Log.d("Test","SplitWord : "+text);
        String[] tmpWord = text.split(":");
        if(tmpWord.length > 4) {
            posOn = tmpWord[1];
            posCommon= tmpWord[2];
            posOff= tmpWord[3];
            onTimer= tmpWord[4];
            offTimer= tmpWord[5];
        }
        else {

        }
    }
    public String GetInitText() {
        String initText;
        long now = System.currentTimeMillis(); //TODO 현재시간 받아오기
        Date date = new Date(now); //TODO Date 객체 생성
        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
        String nowTime = sdf.format(date);
        initText = "INIT" +nowTime+getSaveText();
        return initText;
    }

    public String GetPosOn() {return posOn;}
    public String GetPosCommon() {return posCommon;}
    public String GetPosOff() {return posOff;}
    public String GetOnTimer() {return onTimer;}
    public String GetOffTimer() {return offTimer;}
}
