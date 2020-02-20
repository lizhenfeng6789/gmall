package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2020/2/17.
 */
@Data
public class SkuInfoVo extends SkuInfoEntity {


    // 积分活动
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    private List<Integer> work;

    // 满减活动
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;

    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;

    private List<SkuSaleAttrValueEntity> saleAttrs;

    private List<String> images;
}
