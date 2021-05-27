package com.ab.demomovie;

public class CategoryModel {

    private int cid;
    private String cname;

    public CategoryModel (int cid, String cname) {
        this.cid = cid;
        this.cname = cname;
    }

    public int getCid () {
        return cid;
    }

    public void setCid (int cid) {
        this.cid = cid;
    }

    public String getCname () {
        return cname;
    }

    public void setCname (String cname) {
        this.cname = cname;
    }
}
