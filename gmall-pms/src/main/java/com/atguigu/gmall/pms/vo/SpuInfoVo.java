package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * Created by Administrator on 2020/2/17.
 */
@Data
public class SpuInfoVo extends SpuInfoEntity {

    private List<String> spuImages;

    private List<BaseAttrVo> baseAttrs;

    private List<SkuInfoVo> skus;
}
