package com.example.smartrefreshtest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

public class LooperPagerAdapter extends PagerAdapter {

    private List<String> mPics = null;
    private Context context;

    @Override
    public int getCount() {
        if (mPics != null) {
            return Integer.MAX_VALUE;
        }
        return 0;
    }

//    public LooperPagerAdapter(Context context, List<String> mPics) {
//        this.context = context;
//        this.mPics = mPics;
//    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = position % mPics.size();
        ImageView imageView = new ImageView(container.getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setBackgroundColor(mPics.get(position));
        final String url1 = mPics.get(realPosition).toString(); //这个非常重要
//        imageView.setImageResource(mPics.get(realPosition));
//        Glide.with(context).load(newStr2).into(holder.main_image);
        Glide.with(context).load(url1).into(imageView);
        //设置完数据以后,就添加到容器里
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setData(List<String> colos) {
        this.mPics = colos;
    }

    public int getDataRealSize() {
        if (mPics != null) {
            return mPics.size();
        }
        return 0;
    }
}