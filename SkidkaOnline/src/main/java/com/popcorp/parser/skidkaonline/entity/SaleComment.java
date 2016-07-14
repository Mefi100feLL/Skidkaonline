package com.popcorp.parser.skidkaonline.entity;

public class SaleComment implements DomainObject{

    public static final String REPOSITORY = "saleCommentRepository";

    private int saleId;
    private String author;
    private String whom;
    private String text;
    private long dateTime;

    public SaleComment(int saleId, String author, String whom, String text, long dateTime) {
        this.saleId = saleId;
        this.author = author;
        this.whom = whom;
        this.text = text;
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SaleComment)) return false;
        SaleComment saleComment = (SaleComment) object;
        return getSaleId() == saleComment.getSaleId()
                && getAuthor().equals(saleComment.getAuthor())
                && getDateTime() == saleComment.getDateTime();
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getWhom() {
        return whom;
    }

    public void setWhom(String whom) {
        this.whom = whom;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
}
