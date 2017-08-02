package com.memo.dbUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2017/3/3.
 */
public class GridViewUtil {

    public static ArrayList<HashMap<String,Object>> getGridViewArrayList(String[] title,int[] image,String[] name ){
        ArrayList<HashMap<String,Object>> list=new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<title.length;i++){
            HashMap<String,Object> hashMap=new HashMap<String, Object>();
            hashMap.put(name[0],image[i]);
            hashMap.put(name[1],title[i]);
            list.add(hashMap);
        }

        return list;
    }

    public static void ChangeArrayList(String[] title,int[] image,String[] name,ArrayList<HashMap<String,Object>> list){
        for(int i=0;i<title.length;i++){
            HashMap<String,Object> hashMap=new HashMap<String, Object>();
            hashMap.put(name[0],image[i]);
            hashMap.put(name[1],title[i]);
            list.add(hashMap);
        }
    }
}
