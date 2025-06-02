package org.o7planning.project_04.model;

public class categoryStat {
    private  String cateName;
    private float amount;
    private  int color;

    public categoryStat(String cateName , float amount, int color){
        this.cateName = cateName;
        this.amount = amount;
        this.color = color;
    }
    public String getCateName(){return cateName;}
    public  float getAmount(){return  amount;}
    public int getColor(){return color;}
}
