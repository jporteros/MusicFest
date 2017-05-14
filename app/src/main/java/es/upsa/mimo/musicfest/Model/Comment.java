package es.upsa.mimo.musicfest.Model;

/**
 * Created by Javier on 14/05/2017.
 */

public class Comment {

    public String uid;
    public String cid;
    public String username;
    public String body;
    public String date;

    public Comment() {
    }

    public Comment(String uid, String cid, String username, String body, String date) {
        this.uid = uid;
        this.cid = cid;
        this.username = username;
        this.body = body;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "uid='" + uid + '\'' +
                ", cid='" + cid + '\'' +
                ", username='" + username + '\'' +
                ", body='" + body + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
