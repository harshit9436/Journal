package Util;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class Journal {
    private String title;
    private String thought;
    private String userId;
    private String userName;
    private Timestamp timeAdded;
    private String imageUrl;
    private String documentID;

    public Journal() {
    }

    public Journal(String title, String thought, String userId, String userName, Timestamp timeAdded, String imageUrl, String documentID) {
        this.title = title;
        this.thought = thought;
        this.userId = userId;
        this.userName = userName;
        this.timeAdded = timeAdded;
        this.imageUrl = imageUrl;
        this.documentID = documentID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}
