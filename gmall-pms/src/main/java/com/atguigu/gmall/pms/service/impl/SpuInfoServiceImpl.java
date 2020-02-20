package com.atguigu.gmall.pms.service.impl;

import atguigu.gmall.sms.vo.SkuSaleVo;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.service.ProductAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import com.atguigu.gmall.pms.vo.BaseAttrVo;
import com.atguigu.gmall.pms.vo.SkuInfoVo;

import com.atguigu.gmall.pms.vo.SpuInfoVo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SpuInfoDao;
import com.atguigu.gmall.pms.service.SpuInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescDao descDao;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoDao skuInfoDao;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private AttrDao attrDao;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private GmallSmsClient gmallSmsClient;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuInfo(QueryCondition condition, Long catId) {

        //分装分页
        IPage<SpuInfoEntity> page =new Query<SpuInfoEntity>().getPage(condition);

        //封装查询条件
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        // 如果分类id不为0，要根据分类id查，否则查全部
        if (catId != 0){
            wrapper.eq("catalog_id", catId);
        }
        // 如果用户输入了检索条件，根据检索条件查
        String key = condition.getKey();
        if (StringUtils.isNotBlank(key)){
            wrapper.and(t -> t.like("spu_name", key).or().like("id", key));
        }

        return new PageVo(this.page(page, wrapper));
    }

    @Override
    @Transactional
    public void saveSpuInfoVO(SpuInfoVo spuInfoVo) {
        //1保存spu相关的三张表

        //1.1保存pms_spu_info
             spuInfoVo.setPublishStatus(1); // 默认是已上架
            spuInfoVo.setCreateTime(new Date());
            spuInfoVo.setUodateTime(spuInfoVo.getCreateTime());
            this.save(spuInfoVo);
        Long spuId = spuInfoVo.getId();
        // 1.2. 保存spu的描述信息 pms_spu_info_desc
        List<String> spuImages = spuInfoVo.getSpuImages();
        if(!CollectionUtils.isEmpty(spuImages)){
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuId);
            spuInfoDescEntity.setDecript(StringUtils.join(spuImages,","));

            this.descDao.insert(spuInfoDescEntity);
        }

        // 1.3. 保存spu的规格参数信息 pms_product_attr_value

       List<BaseAttrVo> baseAttrs = spuInfoVo.getBaseAttrs();
        if(!CollectionUtils.isEmpty(baseAttrs)){
            List<ProductAttrValueEntity> attrValueEntities = baseAttrs.stream().map(BaseAttrVo -> {
                ProductAttrValueEntity attrValueEntity = BaseAttrVo;
                attrValueEntity.setSpuId(spuId);
                return attrValueEntity;
            }).collect(Collectors.toList());

            productAttrValueService.saveBatch(attrValueEntities);
        }
        //2.保存sku相关的三张表
        List<SkuInfoVo> skuInfoVos = spuInfoVo.getSkus();
        if(CollectionUtils.isEmpty(skuInfoVos)){
            return;
        }
        skuInfoVos.forEach(skuInfoVo -> {
        //2.1保存pms_sku_Info
            skuInfoVo.setSpuId(spuId);
            skuInfoVo.setSkuCode(UUID.randomUUID().toString());
            skuInfoVo.setBrandId(spuInfoVo.getBrandId());
            skuInfoVo.setCatalogId(spuInfoVo.getCatalogId());
            List<String> images = skuInfoVo.getImages();
            if(!CollectionUtils.isEmpty(images)){
                skuInfoVo.setSkuDefaultImg(StringUtils.isNotBlank(skuInfoVo.getSkuDefaultImg())?skuInfoVo.getSkuDefaultImg():images.get(0));
            }
            this.skuInfoDao.insert(skuInfoVo);
            Long skuId = skuInfoVo.getSkuId();
            //2.2保存pms_sku_image
            if(!CollectionUtils.isEmpty(images)){
                List<SkuImagesEntity> skuImagesEntities = images.stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setImgUrl(image);
                    skuImagesEntity.setSkuId(skuId);
                    //设置是否默认图片
                    skuImagesEntity.setDefaultImg(StringUtils.equals(skuInfoVo.getSkuDefaultImg(), image) ? 1 : 0);
                    return skuImagesEntity;
                }).collect(Collectors.toList());

                this.skuImagesService.saveBatch(skuImagesEntities);
            }
        //2.3保存pms_sale_attr_value
            List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVo.getSaleAttrs();
            if(!CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.forEach(skuSaleAttrValueEntity -> skuSaleAttrValueEntity.setSkuId(skuId));
                this.skuSaleAttrValueService.saveBatch(saleAttrs);
            }
            //3.保存营销信息相关的三张表(通过feign远程调用)
            SkuSaleVo skuSaleVo = new SkuSaleVo();
            BeanUtils.copyProperties(skuInfoVo,skuSaleVo);
            skuSaleVo.setSkuId(skuId);
            this.gmallSmsClient.saveSale(skuSaleVo);
        });
    }

}