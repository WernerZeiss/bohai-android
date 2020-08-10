package com.rice.bohai

import android.util.Log
import com.rice.racar.web.RiceHttpK

class Constant {

    //    enum class BaseType {
    //        ALPHA, BETA
    //    }

    companion object {
        const val BUGLY_APPID = "ae0dfccfa0"
        const val BUGLY_APPKEY = "08fea09d-5553-44e6-bd3e-a4ec672b2db8"
        const val WECHAT_APP_ID = "wx65fc44f36458169e"
        const val BETA = 0
        const val ALPHA = 1
        const val SITA = 2
        const val ONLINE = 3

        var baseType = BETA

        @JvmStatic
        fun getBaseUrl(): String {
            when (baseType) {
                ALPHA, SITA -> {
                    return BASE_URL_HAIBO
                }
                BETA -> {
                    return BASE_URL_XHRD_TEST
                }
                else -> {
                    return BASE_URL_XHRD
                }
            }
        }

        const val BASE_URL_HAIBO = "https://haibo.ricecs.cn/api/v1/" //服务器地址
        const val BASE_URL_XHRD = "https://xhrdapp.scxhrd.com/api/v1/" //服务器地址
        const val BASE_URL_XHRD_TEST = "https://test.scxhrd.com/api/v1/" //测试服地址
        const val SYSTEM_CONFIG = "system-config" //系统配置参数
        const val GET_MOBILE_CODE = "get-mobile-code" //获取验证码
        const val REGISTER = "register" //注册
        const val LOGIN = "login" //登录
        const val GET_USER = "get-user" //获取用户信息
        const val FORGET_PASSWORD = "forget-password" //忘记密码
        const val NOTICE_LIST = "notice-list" //公告信息
        const val NEWS_LIST = "news-list" //行业资讯
        const val BIG_TYPE_LIST = "big-type-list" //产品大分类列表
        const val PRODUCT_TYPE_LIST = "product-type-list" //产品分类列表
        const val INTEGRAL_PRODUCT_TYPE_LIST = "integral-product-type-list" //首页产品子分类列表
        const val PRODUCT_LIST = "product-list" //产品列表
        const val PRODUCT_TRANSACTION_DETAIL = "product-transaction-detail" //价格列表
        const val IS_FIRST_ORDER = "is-first-order" //是否首单
        const val PRODUCT_SALE_RECORD = "product-sale-record" //交易记录
        const val PRODUCT_DETAIL = "product-detail" //产品详情
        const val INTEGRAL_PRODUCT_DETAIL = "integral-product-detail" //首页产品详情
        const val SET_TRANSACTION_PASSWORD = "set-transaction-password" //设置交易密码
        const val FORGET_TRANSACTION_PASSWORD = "forget-transaction-password" //忘记交易密码
        const val UPDATE_PASSWORD = "update-password" //修改交易密码
        const val CAROUSEL_LIST = "carousel-list" //轮播图列表
        const val MY_POSITION = "my-position" //我的订单
        const val UPDATE_ADDRESS = "update-address" //添加/修改地址
        const val DELETE_ADDRESS = "delete-address" //删除地址
        const val ADDRESS_IS_DEFAULT = "address-is-default" //设置默认地址
        const val ADDRESS_LIST = "address-list" //地址列表
        const val AREA_LIST = "area-list" //省市区数据
        const val POSITION_PICK_UP = "position-pick-up" //订单提货
        const val POSITION_PICK_UP_GET_FREIGHT_PRICE =
            "position-pick-up-get-freight-price" //订单提货计算运费
        const val INTEGRAL_PRODUCT_LIST = "integral-product-list" //积分商城列表
        const val EXCHANGE_PRODUCT = "exchange-product" //兑换积分商品
        const val MY_INTEGRAL_RECORD = "my-integral-record" //我的积分记录
        const val ORDER_CONVERSION_INTEGRAL = "order-conversion-integral" //订单转积分
        const val PRICE_CONVERSION_INTEGRAL = "price-conversion-integral" //现金转积分
        const val EXTEND_LIST = "extend-list" //推广图片列表
        const val MY_PROFIT_RECORD = "my-profit-record" //我的销售配货
        const val MY_FRIENDS = "my-friends" //我的好友
        const val PRICE_CHANGE_RECORD = "price-change-record" //我的钱包收益
        const val MY_SETTLEMENT_POSITION = "my-settlement-position" //我的提货金额
        const val UPDATE_USER = "update-user" //修改用户信息
        const val UPLOAD_IMAGE = "upload-image" //图片上传
        const val ORDER_LIST = "order-list" //订单列表
        const val GET_DEFAULT_ADDRESS = "get-default-address" //获取默认地址
        const val HANG_UP_POSITION = "hang-up-position" //我的持仓→转让
        const val CANCEL_SALE = "cancel-sale" //我的持仓→取消转让
        const val MY_PROFIT_POSITION_SALE = "my-profit-position-sale" //销售配货→转让
        const val ORDER_DETAIL = "order-detail" //订单详情
        const val PROFIT_POSITION_CONVERSION_INTEGRAL =
            "profit-position-conversion-integral" //销售配货兑换积分
        const val CANCEL_INTEGRAL_ORDER = "cancel-integral-order" //取消订单
        const val SURE_ORDER = "sure-order" //确认收货
        const val PROFIT_RECORD = "profit-record" //销售配货明细
        const val MESSAGE_LIST = "message-list" //我的消息
        const val MESSAGE_DETAIL = "message-detail" //消息详情
        const val UPDATE_SIGNATURE = "update-signature" //转让上传签名
        const val CANCEL_PROFIT_SALE = "cancel-profit-sale" //销售配货→取消转让/拼团
        const val EXCHANGE_INTEGRAL_PRODUCT_LIST = "exchange-integral-product-list" //兑换记录
        const val MY_PROTOCOL = "my-protocol" //我的协议列表
        const val DOWNLOAD_PROTOCOL = "download-protocol" //下载协议
        const val JOIN_SHOP_CART = "join-shop-cart" //加入购物车
        const val MY_SHOP_CART = "my-shop-cart" //我的购物车
        const val CART_EXCHANGE_PRODUCT = "cart-exchange-product" //购物车提交兑换
        const val UPDATE_SHOP_CART = "update-shop-cart" //修改购物车
        const val BATCH_DELETE_CART = "batch-delete-cart" //删除购物车商品
        const val RECHARGE_ORDER = "recharge-order" //充值提交订单
        const val WX_PAY_ORDER = "get-wxpay-data" //获取微信支付数据
        const val ALI_PAY_ORDER = "recharge-ali-pay" //获取支付宝支付数据
        const val BUY_POSITION = "buy-position" //购买持仓
        const val LOAD_MESSAGE = "load-message" //轮询交易是否成功
        const val RECHARGE_RECORD = "recharge-record" //充值记录
        const val BANK_LIST = "bank-list" //银行列表
        const val BIND_BANK = "bind-bank" //绑定银行卡
        const val CITY_LIST = "city-list" //绑定银行卡省市列表
        const val PRESENT = "present" //提现
        const val PRESENT_RECORD_LIST = "present-record-list" //提现记录
        const val PICK_UP_NUMBER = "pick-up-number" //获取可兑换数量
        const val PICK_UP_SETTLEMENT = "pick-up-settlement" //提货券提货
        const val SIGN_REGISTER = "sign-register" //同意数字证书协议
        const val CHECK_CONTRACT = "check-contract" //签名地址及状态
        const val HELP_CENTER_LIST = "help-center-list" //帮助中心列表
        const val PINTUAN_PRODUCT = "group-buy-goods-info" //拼团商品信息
        const val PINTUAN_ORDER_LIST = "get-user-order-list" // 获取当前会员所有订单列表
        const val PINTUAN_BUY = "buy-goods-now" //立即购买
        const val PINTUAN_ADD = "add-group-buy" // 手动拼团
        const val PINTUAN_AUTO_ADD = "handle-auto-group" // 自动拼团
        const val PINTUAN_PRODUCE_LIST = "get-bonus-order-log" //拼团明细
        const val PINTUAN_PRODUCE_PROCESS = "get-group-order-info" //拼团进度
        const val PINTUAN_BUY_HISTORY = "get-buy-log" // 拼团购买记录
        const val CHUBEI_MONEY = "get-show-total-tickets" //总储备值
        const val CHUBEI_RULE = "get-chu-bei-rules" //储备规则
        const val CHUBEI_RULE_RANK = "get-chu-bei-rank-list" //储备排行
        const val GET_MY_BANK_CARD_LIST = "get-my-bank-card-list"//获取银行卡列表
        const val DEFAULT_BANK = "default-bank"//设置默认银行卡
        const val REAL_NAME = "real-name"//新版两要素实名认证

        const val REQUEST_SELECT_ADDRESS = 0x00001 //选择收货地址
        const val REQUEST_SELECT_ORDER = 0x00002 //选择订单
        const val REQUEST_XSPH2JF = 0x00003 //销售配货转积分
        const val REQUEST_SHOP_CAR_BUY = 0x00004 //购物车批量兑换
        const val REQUEST_MESSAGE_LIST = 0x00005 //消息中心


        const val IMAGE_PRE_URL = "https://qiniu.scxhrd.com/static/img/"//图片前缀
    }
}