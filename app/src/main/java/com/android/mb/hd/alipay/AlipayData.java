package com.android.mb.hd.alipay;

import java.io.Serializable;

/**
 * Created by cgy on 19/2/3.
 */

public class AlipayData implements Serializable{

    private String paytype;
    private PayinfoBean payinfo;
    private String OutTradeNo;
    private String order_id;
    private boolean rs;

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public PayinfoBean getPayinfo() {
        return payinfo;
    }

    public void setPayinfo(PayinfoBean payinfo) {
        this.payinfo = payinfo;
    }

    public String getOutTradeNo() {
        return OutTradeNo;
    }

    public void setOutTradeNo(String OutTradeNo) {
        this.OutTradeNo = OutTradeNo;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public boolean isRs() {
        return rs;
    }

    public void setRs(boolean rs) {
        this.rs = rs;
    }

    public static class PayinfoBean {
        /**
         * status : 1
         * type : 2
         * paymenet : app_id=2019013163216307&biz_content=%7b%22body%22%3a%22%e8%ae%a2%e5%8d%95%e6%94%af%e4%bb%98%22%2c%22out_trade_no%22%3a%221549127130360858%22%2c%22product_code%22%3a%22QUICK_MSECURITY_PAY%22%2c%22subject%22%3a%22%e8%ae%a2%e5%8d%95%e6%94%af%e4%bb%98%22%2c%22timeout_express%22%3a%2230m%22%2c%22total_amount%22%3a%220.01%22%7d&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3a%2f%2fhongbao.5979wenhua.com%2fpayment%2falipay%2fnotify.php&sign_type=RSA2&timestamp=2019-02-03+01%3a05%3a30&version=1.0&sign=IZ2h5UDzsbhpkAS%2fzwW%2b9OT2Sr%2fSymzqYsQULQk9VGl08dHs3p9tHaxnpA%2bjhjJwfTBLtVj0Ckgs4ud6ZNjOLHdSL9kxvUFFuRmz9CHu8lcI3%2fTZrL7gFDmsiDdjAobnENxrgXs0hwHIa%2b6C9EzzKPbCRxV2IwzW%2bTII8%2f%2bXn6jt88tuqPZywPJmmrROgy6n8MUDjjZttcJNqknMpInzFwFdeTXVAXa7x7cbo7yvi8IKjoiR613tJqo1XFUqT5jm6H%2bW4hQwhXw8ohNGI4%2bs3GfNadRp670jsZmNoOzbuz49fh8qUtuIj6W6FNKfU8lhGEM3yHMaZ42XEPjcZFEU9w%3d%3d
         */

        private int status;
        private int type;
        private String paymenet;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getPaymenet() {
            return paymenet;
        }

        public void setPaymenet(String paymenet) {
            this.paymenet = paymenet;
        }
    }
}
