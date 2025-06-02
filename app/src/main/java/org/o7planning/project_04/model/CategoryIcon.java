package org.o7planning.project_04.model;

public class CategoryIcon {
    private String name;
    private int ResId;

    public CategoryIcon(String name, int ResId){
        this.name = name;
        this.ResId= ResId;

    }
    public  String getName(){
        return  name;
    }
    public int getResId(){return ResId;}
}
