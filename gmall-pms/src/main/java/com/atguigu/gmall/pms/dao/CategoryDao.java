package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author lizhenfeng
 * @email 1974917228@qq.com
 * @date 2020-02-02 09:35:42
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
