package com.popcorp.parser.skidkaonline.dto;

import com.google.gson.annotations.SerializedName;
import com.popcorp.parser.skidkaonline.entity.SaleComment;

public class SaleCommentDTO {

    @SerializedName("error")
    private boolean error;

    @SerializedName("comment")
    private SaleComment comment;

    public SaleCommentDTO(boolean error, SaleComment comment) {
        this.error = error;
        this.comment = comment;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public SaleComment getComment() {
        return comment;
    }

    public void setComment(SaleComment comment) {
        this.comment = comment;
    }
}
