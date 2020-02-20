package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;


import java.util.List;

/**
 * Created by Administrator on 2020/2/17.
 */
public class BaseAttrVo extends ProductAttrValueEntity {


    //重写set方法
    public void setValueSelected(List<String> selected){

        if(CollectionUtils.isEmpty(selected)){
            return;
        }

        this.setAttrValue(StringUtils.join(selected,","));
    }




}
