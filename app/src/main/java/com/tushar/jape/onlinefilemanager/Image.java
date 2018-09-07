package com.tushar.jape.onlinefilemanager;

class Image {
    private String fileName, key;

    Image(){
        fileName = "";
        key = "";
    }

    Image(String name, String key){
        fileName = name;
        this.key = key;
    }

    String getFileName(){
        return fileName;
    }

    String getKey(){
        return key;
    }

    void setFileName(String name){
        fileName = name;
    }
}
