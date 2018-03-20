//package com.three.pay.aliutils;
//
//import android.text.TextUtils;
//
//import com.three.pay.bean.PayInfo;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//
//public class PayUrlGenerator {
//
//    private PayInfo payInfo;
//    public PayUrlGenerator(PayInfo payInfo) {
//        this.payInfo = payInfo;
//    }
//
//    public String generatePayUrl() {
//
//        if (this.payInfo == null) {
//            //L.e(TAG, " +++++ orderInfo is null");
//            return "";
//        }
//
//        validatePayInfo(payInfo);
//
//        String orderInfo = this.convertOrderInfoToString();
//
//        // 对订单做RSA 签名
//
//        String sign = signUrl(orderInfo);
//        try {
//            // 仅需对sign 做URL编码
//
//            sign = URLEncoder.encode(sign, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        // 完整的符合支付宝参数规范的订单信息
//
//        StringBuilder sb = new StringBuilder(orderInfo);
//        sb.append("&sign=\"");
//        sb.append(sign);
//        sb.append("\"&sign_type=\"");
//        //sb.append(ThirdConfigManager.CONFIG.AliPay.SIGN_TYPE);
//        sb.append("\"");
//
//        final String payUrl = sb.toString();
//        //L.i(TAG, "pay order isgn info:" + payUrl);
//
//        return payUrl;
//    }
//
//    private String convertOrderInfoToString() {
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("partner=\"");        // 签约合作者身份ID
//        //sb.append(ConstantKeys.AliPay.PARTNER_ID);
//
//        sb.append("\"&seller_id=\"");     // 签约卖家支付宝账号
//
//        //sb.append(ConstantKeys.AliPay.SELLER_ID);
//
//        sb.append("\"&out_trade_no=\""); // 商户网站唯一订单号
//
//        sb.append(this.payInfo.getOrderNo());
//
//        sb.append("\"&subject=\"");     // 商品名称
//
//        sb.append(this.payInfo.getSubject());
//
//        sb.append("\"&body=\"");        // 商品详情
//
//        sb.append(this.payInfo.getBody());
//
//        sb.append("\"&total_fee=\"");    // 商品金额
//
//        sb.append(this.payInfo.getPrice());
//
//        sb.append("\"&notify_url=\"");    // 服务器异步通知页面路径
//
//        sb.append(this.payInfo.getNotifyUrl());
//
//        sb.append("\"&service=\"mobile.securitypay.pay"); // 服务接口名称， 固定值
//
//
//        sb.append("\"&payment_type=\"1");    // 支付类型， 固定值
//
//
//        sb.append("\"&_input_charset=\"");    // 参数编码， 固定值
//
//        //sb.append(ConstantKeys.AliPay.INPUT_CHARSET);
//
//        // 设置未付款交易的超时时间
//
//        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
//
//        // 取值范围：1m～15d。
//
//        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
//
//        // 该参数数值不接受小数点，如1.5h，可转换为90m。
//
//        sb.append("\"&it_b_pay=\"30m\"");
//
//        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
//
////        sb.append("&return_url=\"");
//
////        sb.append("http://m.alipay.com\"");
//
//        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
//
////        sb.append("&paymethod=\"expressGateway\"");
//
//
//        String fullUrl = sb.toString();
//        //L.i(TAG, "alilpay orderInfo :"+fullUrl);
//        return fullUrl;
//    }
//
//    // used RSA algorithm
//
//    // 对订单做RSA 签名
//
//    private String signUrl(String urlToSign) {
//
//
//        return null;
//    }
//
//    /**
//     * 验证 支付信息的有效性
//     *
//     * @return void
//     * @autour BaoHong.Li
//     * @date 2015-7-16 下午5:56:21
//     * @update (date)
//     */
//    private void validatePayInfo(PayInfo payInfo) {
//
//        if (TextUtils.isEmpty(payInfo.getOrderNo())) {
//            throw new IllegalArgumentException(" payInfo.orderNo is  null !");
//        }
//
//        if (TextUtils.isEmpty(payInfo.getBody())) {
//            throw new IllegalArgumentException(" payInfo.body is  null !");
//        }
//
//        if (TextUtils.isEmpty(payInfo.getPrice())) {
//            throw new IllegalArgumentException(" payInfo.price is  null !");
//        }
//
//        if (TextUtils.isEmpty(payInfo.getSubject())) {
//            throw new IllegalArgumentException(" payInfo.subject is  null !");
//        }
//
//        if (TextUtils.isEmpty(payInfo.getNotifyUrl())) {
//            throw new IllegalArgumentException(" payInfo.notifyUrl is  null !");
//        }
//
//    }
//
//}