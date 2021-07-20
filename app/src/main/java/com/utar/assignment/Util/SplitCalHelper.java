package com.utar.assignment.Util;

public class SplitCalHelper {

    //normal split equally
    public double splitNormal(int per, double amount){
        double result = 0.0;
        result=amount/per;
        Math.round(result);
        return result;
    }

}
