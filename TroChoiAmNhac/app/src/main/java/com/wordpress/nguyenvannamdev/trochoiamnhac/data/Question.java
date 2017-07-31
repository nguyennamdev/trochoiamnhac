package com.wordpress.nguyenvannamdev.trochoiamnhac.data;



/**
 * Created by NAM COI on 1/11/2017.
 */

public class  Question {
    private String question;
    private String urlPath;
    private String answer;


    public Question() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


    public Question(String question, String urlPath, String answer) {
        this.question = question;
        this.urlPath = urlPath;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }
}
