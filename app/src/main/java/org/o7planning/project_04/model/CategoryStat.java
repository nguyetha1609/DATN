package org.o7planning.project_04.model;

public class CategoryStat {
    private  String cateName;
    private float amount;
    private  int color;

    public CategoryStat(String cateName , float amount, int color){
        this.cateName = cateName;
        this.amount = amount;
        this.color = color;
    }
    public String getCateName(){return cateName;}
    public  float getAmount(){return  amount;}
    public int getColor(){return color;}
}
