package com.sammie.barbershopclientmodel.Model;

import com.google.firebase.firestore.FieldValue;

public class MyNotification {
    private String uid,title,content;
    private boolean read;
    private FieldValue serverTimeStamp;


    public MyNotification() {
    }

    public MyNotification(String uid, String title, String content, boolean read) {
        this.uid = uid;
        this.title = title;
        this.content = content;
        this.read = read;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public FieldValue getServerTimeStamp() {
        return serverTimeStamp;
    }

    public void setServerTimeStamp(FieldValue serverTimeStamp) {
        this.serverTimeStamp = serverTimeStamp;
    }
}
