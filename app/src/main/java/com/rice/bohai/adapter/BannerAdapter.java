package com.rice.bohai.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.demono.adapter.InfinitePagerAdapter;
import com.rice.activity.BannerDetailActivity;
import com.rice.bohai.Constant;
import com.rice.bohai.R;
import com.rice.bohai.model.BannerModel;
import com.rice.imageloader.GlideLoadUtils;
import com.rice.tool.ActivityUtils;
import com.rice.tool.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends InfinitePagerAdapter {

    private List<BannerModel> data = new ArrayList();
    private Context mContext;

    public BannerAdapter(Context mContext, List<BannerModel> data) {
        this.mContext = mContext;
        this.data.clear();
        this.data.addAll(data);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup container) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_banner, container, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GlideLoadUtils.getInstance().glideLoad(mContext, TextUtils.getImgUrl(Constant.getBaseUrl(), data.get(position).getImage()),
                mContext.getResources().getDimensionPixelOffset(R.dimen.dp_8), holder.img);
        holder.img.setOnClickListener(
                v -> {
                    Bundle b = new Bundle();
                    b.putBoolean("textX", true);
                    b.putString("url", data.get(position).getContent());
                    b.putString("title", data.get(position).getTitle());
                    ActivityUtils.openActivity(mContext, BannerDetailActivity.class, b);
                });
        return convertView;
    }

    private class ViewHolder {
        private ImageView img;

        public ViewHolder(View v) {
            img = v.findViewById(R.id.img);
        }
    }

}