package com.popcorp.parser.skidkaonline.dto;

import com.google.gson.annotations.SerializedName;
import com.popcorp.parser.skidkaonline.entity.SaleComment;

import java.util.ArrayList;

public class SaleCommentsDTO {

    @SerializedName("error")
    private boolean error;

    @SerializedName("comments")
    private ArrayList<SaleComment> comments;

    public ArrayList<SaleComment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<SaleComment> comments) {
        this.comments = comments;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
