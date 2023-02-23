package com.touchbiz.chatgpt.common.dto;

import com.touchbiz.common.entity.query.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BasePageQuery  extends BaseQuery {

    private Integer pageNo =1;

    public Integer getPageIndex(){
        if(pageNo != null){
            return pageNo;
        }
        return super.getPageIndex();
    }
}
