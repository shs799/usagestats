package com.privacyFirst.usageStats;

public class packageName {
    final private String s1,s2;
    packageName(String s1, String s2){
        int min=Math.min(s1.length(),s2.length());
        for(int i=0;i<min;i++){
            if(s1.charAt(i)!=s2.charAt(i)){
                this.s1=s1.substring(i);
                this.s2=s2.substring(i);
                return;
            }
        }
        this.s1=s1.substring(min);
        this.s2=s2.substring(min);
    }
    public String getS1() {
        return s1;
    }
    public String getS1RemoveDot(){
        for(int i=0;i<s1.length();i++){
            if(s1.charAt(i)!='.'){
                return s1.substring(i);
            }
        }
        return s1;
    }
    public String getS2() {
        return s2;
    }
}

