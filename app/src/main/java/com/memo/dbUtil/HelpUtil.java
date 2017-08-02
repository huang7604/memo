package com.memo.dbUtil;

import java.util.HashMap;

/**
 * Created by user on 2017/3/24.
 */
public class HelpUtil {
    public HashMap<String,String> getMemoContentHashMap(String content){
        HashMap<String,String> hashMap=new HashMap<String,String>();
        String[] content_array=content.split("&");
        for (int i=0;i<content_array.length;i++){

        }
        return hashMap;
    }

}
