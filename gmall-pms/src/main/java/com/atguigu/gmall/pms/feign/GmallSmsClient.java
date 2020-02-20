package com.atguigu.gmall.pms.feign;

import atguigu.gmall.sms.api.GmallSmsApi;
import atguigu.gmall.sms.vo.SkuSaleVo;
import com.atguigu.core.bean.Resp;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by Administrator on 2020/2/19.
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi{


}
