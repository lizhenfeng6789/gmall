package com.atguigu.gmall.sms.service.impl;

import atguigu.gmall.sms.vo.SkuSaleVo;
import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.service.SkuFullReductionService;
import com.atguigu.gmall.sms.service.SkuLadderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuLadderDao skuLadderDao;

    @Autowired
    private SkuFullReductionDao skuFullReductionDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public void saveSale(SkuSaleVo skuSaleVo) {
        //保存sms_sku_bounds
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        skuBoundsEntity.setBuyBounds(skuSaleVo.getBuyBounds());
        skuBoundsEntity.setGrowBounds(skuSaleVo.getGrowBounds());
        skuBoundsEntity.setSkuId(skuSaleVo.getSkuId());
        List<Integer> work = skuSaleVo.getWork();
        skuBoundsEntity.setWork(work.get(3)*1+work.get(2)*2+work.get(1)*4+work.get(0)*8);
        this.save(skuBoundsEntity);
        //保存sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setFullCount(skuSaleVo.getFullCount());
        skuLadderEntity.setDiscount(skuSaleVo.getDiscount());
        skuLadderEntity.setSkuId(skuSaleVo.getSkuId());
        skuLadderEntity.setAddOther(skuSaleVo.getLadderAddOther());
        this.skuLadderDao.insert(skuLadderEntity);
        //保存sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        skuFullReductionEntity.setFullPrice(skuSaleVo.getFullPrice());
        skuFullReductionEntity.setReducePrice(skuSaleVo.getReducePrice());
        skuFullReductionEntity.setSkuId(skuSaleVo.getSkuId());
        skuFullReductionEntity.setAddOther(skuSaleVo.getFullAddOther());
        this.skuFullReductionDao.insert(skuFullReductionEntity);

    }

}