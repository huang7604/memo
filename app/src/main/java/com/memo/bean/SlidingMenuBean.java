package com.memo.bean;

/**
 * Created by user on 2017/3/1.
 */
public class SlidingMenuBean {
    private int id;
    private int image;
    private String title;
    private int number;

    public SlidingMenuBean(){

    }

    public void setId(int id){
        this.id=id;
    }

    public int getId(){
        return id;
    }

    public void setImage(int image){
        this.image=image;
    }

    public int getImage(){
        return image;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public String getTitle(){
        return title;
    }

    public void setNumber(int number){
        this.number=number;
    }

    public int getNumber(){
        return number;
    }

    @Override
    public boolean equals(Object object){
        if(object==null){
            return false;
        }else {
            if(object instanceof SlidingMenuBean){
                SlidingMenuBean slidingMenuBean=(SlidingMenuBean)object;
                if(slidingMenuBean.getId()==this.id){
                    return true;
                }
            }
        }
        return false;
    }
}
