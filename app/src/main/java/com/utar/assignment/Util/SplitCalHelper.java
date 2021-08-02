package com.utar.assignment.Util;

public class SplitCalHelper {

    //normal split equally
    public double splitNormal(int per, double amount){
        double result = amount/per;
        Math.round(result);
        return result;
    }

    public double splitbyEachPercentage(int percentage, double amount){
        double result = amount * percentage / 100;

        Math.round(result);

        return result;
    }

}
