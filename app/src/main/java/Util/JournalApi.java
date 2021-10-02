package Util;

import android.app.Application;

public class JournalApi extends Application {
    private String username;
    private String userID;
    private String DocumentID;
    public static JournalApi instance;
    public JournalApi(){}

    public static JournalApi getInstance(){
        if(instance==null){
            instance = new JournalApi();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDocumentID() {
        return DocumentID;
    }

    public void setDocumentID(String documentID) {
        DocumentID = documentID;
    }
}
