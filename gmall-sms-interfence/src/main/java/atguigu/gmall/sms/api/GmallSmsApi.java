package atguigu.gmall.sms.api;

import atguigu.gmall.sms.vo.SkuSaleVo;
import com.atguigu.core.bean.Resp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by Administrator on 2020/2/19.
 */
public interface GmallSmsApi {

    @PostMapping("sms/skubounds/sku/sale/save")
    public Resp<Object> saveSale(@RequestBody SkuSaleVo skuSaleVo);
}
