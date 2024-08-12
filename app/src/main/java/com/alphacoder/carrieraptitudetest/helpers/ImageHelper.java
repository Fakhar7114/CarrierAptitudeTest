package com.alphacoder.carrieraptitudetest.helpers;

import android.content.Context;
import android.widget.ImageView;

import com.alphacoder.carrieraptitudetest.R;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageHelper {

    public static void load(Context context, ImageView image, String url){
        try {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.ic_empty_bg)
                    .into(image);
        }
        catch (Exception e){
            image.setImageResource(R.drawable.ic_empty_bg);
        }
    }
}
