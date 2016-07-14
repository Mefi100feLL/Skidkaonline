package com.popcorp.parser.skidkaonline.mapper;

import com.popcorp.parser.skidkaonline.entity.SaleComment;
import com.popcorp.parser.skidkaonline.util.SaleCommentDateTimeMapper;

public class SaleCommentMapper {

    public static SaleComment getComment(int saleId, SaleComment comment){
        comment.setSaleId(saleId);
        comment.setAuthor(comment.getAuthor().replaceAll("</?small>", "").replace("(Гость)", "").trim());
        comment.setText(comment.getText().trim());
        comment.setDateTime(SaleCommentDateTimeMapper.getDateTimeInLong(comment.getWhom()));
        return comment;
    }
}
