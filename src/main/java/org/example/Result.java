package org.example;

public class Result {
    private char chr;
    private String string;
    private int amountOfLetter;



    Result(char chr, String string, int amountOfLetter){
        this.chr = chr;
        this.string = string;
        this.amountOfLetter = amountOfLetter;
    }

    public char getChr() {
        return chr;
    }

    public String getString() {
        return string;
    }

    public int getAmountOfLetter() {
        return amountOfLetter;
    }
}

