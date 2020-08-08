package com.rice.bohai.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.rice.bohai.Constant
import com.rice.bohai.R
import com.rice.bohai.model.ShopcarModel
import com.rice.imageloader.GlideLoadUtils
import com.rice.tool.Arith
import com.rice.tool.TextUtils
import com.rice.tool.ToastUtil

class ShopcarAdapter(var context: Context, data: MutableList<ShopcarModel>, var mode: Int = MODE_SHOP_CAR) :
        BaseQuickAdapter<ShopcarModel, BaseViewHolder>(R.layout.item_shopcar, data) {

    companion object {
        const val MODE_SHOP_CAR = 0 //购物车选择
        const val MODE_INFO = 1 //展示信息
    }

    var onSelectedListener: OnSelectedListener? = null
    var onNumberChangeListener: OnNumberChangeListener? = null

    interface OnSelectedListener {
        fun onSelected(isSelectedAll: Boolean)
    }

    interface OnNumberChangeListener {
        fun onNumberChange(number: Int)
    }

    @SuppressLint("SimpleDateFormat")
    override fun convert(helper: BaseViewHolder, bean: ShopcarModel) {
        helper.setText(R.id.textName, bean.product_name)
        helper.setText(R.id.textNum, bean.number.toString() + bean.unit)
        helper.setText(R.id.editNumber, bean.number.toString())
        var price = ""
        if (bean.price == "0.00") {
            price = bean.integral_num + "积分"
        } else if (bean.integral_num == "0") {
            price = mContext.resources.getString(R.string.CNY) + bean.price
        } else {
            price = mContext.resources.getString(R.string.CNY) + bean.price + "+" + bean.integral_num + "积分"
        }
        helper.setText(R.id.textPrice, price)
        var img = helper.getView<ImageView>(R.id.img)
        GlideLoadUtils.getInstance().glideLoad(mContext, TextUtils.getImgUrl(Constant.getBaseUrl(), bean.image),
                mContext.resources.getDimensionPixelOffset(R.dimen.dp_6), img)
        if (bean.isChecked) {
            helper.setImageDrawable(R.id.imgCheck, mContext.resources.getDrawable(R.drawable.icon_check_shopcar_focus))
        } else {
            helper.setImageDrawable(R.id.imgCheck, mContext.resources.getDrawable(R.drawable.icon_check_shopcar))
        }
        when (mode) {
            MODE_SHOP_CAR -> {
                helper.setGone(R.id.imgCheck, true)
                helper.setGone(R.id.llNumber, true)
                helper.setGone(R.id.textNum, false)
                var constraintRoot = helper.getView<ConstraintLayout>(R.id.constraintRoot)
                constraintRoot.setOnClickListener {
                    bean.isChecked = !bean.isChecked
                    onSelectedListener?.onSelected(isSelectedAll())
                    notifyDataSetChanged()
                }
                helper.addOnClickListener(R.id.textBtnSub)
                helper.addOnClickListener(R.id.textBtnAdd)
                helper.addOnClickListener(R.id.editNumber)
                //                var editNumber = helper.getView<EditText>(R.id.editNumber)
                //                editNumber.addTextChangedListener(object : TextWatcher {
                //                    override fun afterTextChanged(s: Editable?) {
                //                        var input = editNumber.text.toString()
                //                        if (TextUtils.isEmpty(input) || !TextUtils.isPositiveInt(input)) {
                //                            ToastUtil.showShort("请输入正确的数量")
                //                        } else {
                ////                            onNumberChangeListener?.onNumberChange(input.toInt())
                //                            bean.number = input.toInt()
                ////                            notifyDataSetChanged()
                //                        }
                //                    }
                //
                //                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
                //                    }
                //
                //                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //
                //                    }
                //                })
            }
            MODE_INFO -> {
                helper.setGone(R.id.imgCheck, false)
                helper.setGone(R.id.llNumber, false)
                helper.setGone(R.id.textNum, true)
            }
        }
    }

    /**
     * 选中/取消选中所有项
     * @param checked true选中/false取消选中
     */
    fun selectedAll(checked: Boolean) {
        for (item in data) {
            item.isChecked = checked
        }
        notifyDataSetChanged()
    }

    /**
     * 是否选中了全部
     */
    fun isSelectedAll(): Boolean {
        var isSelected = true
        for (item in data) {
            if (!item.isChecked) {
                isSelected = false
                break
            }
        }
        return isSelected
    }

    /**
     * 返回当前选中项价格
     */
    fun getSelectedPrice(): String {
        var priceDb = 0.0
        var internal = 0.0
        for (item in data) {
            if (item.isChecked) {
                priceDb = Arith.add(priceDb, Arith.mul(item.number.toDouble(), item.price.toDouble()))
                internal = Arith.add(internal, Arith.mul(item.number.toDouble(), item.integral_num.toDouble()))
            }
        }
        var price = ""
        if (priceDb.toString() == "0.00" || priceDb.toString() == "0.0") {
            price = "${internal}积分"
        } else if (internal.toString() == "0" || internal.toString() == "0.0" || internal.toString() == "0.00") {
            price = mContext.resources.getString(R.string.CNY) + priceDb.toString()
        } else {
            price = mContext.resources.getString(R.string.CNY) + priceDb.toString() + "+" + internal + "积分"
        }
        return price
    }

    /**
     * 获取选中项
     */
    fun getSelectedItems(): MutableList<ShopcarModel> {
        var list: MutableList<ShopcarModel> = ArrayList()
        for (item in data) {
            if (item.isChecked) {
                list.add(item)
            }
        }
        return list
    }

    /**
     * 获取选中项ID
     */
    fun getSelectedIds(): String {
        var positions = ""
        for (item in data) {
            if (item.isChecked) {
                positions += "${item.id},"
            }
        }
        if (positions.length > 1) {
            positions = positions.substring(0, positions.lastIndex)
        }
        return positions
    }

}
