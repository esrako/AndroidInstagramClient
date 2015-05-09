package com.mycompany.instagramclient;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ekucukog on 5/5/2015.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    Transformation transformation = new RoundedTransformationBuilder()
            //.borderColor(Color.BLACK)
            //.borderWidthDp(3)
            .cornerRadiusDp(35)
            .oval(true)
            .build();

    Linkify.TransformFilter filter = new Linkify.TransformFilter() {
        public final String transformUrl(final Matcher match, String url) {
            return match.group();
        }
    };
    Pattern mentionPattern = Pattern.compile("@([A-Za-z0-9_-]+)");
    String mentionScheme = "http://www.twitter.com/";
    Pattern hashtagPattern = Pattern.compile("#([A-Za-z0-9_-]+)");
    String hashtagScheme = "http://www.twitter.com/search/";
    Pattern urlPattern = Patterns.WEB_URL;

    // View lookup cache
    private static class ViewHolder {
        TextView tvCaption;
        ImageView ivPhoto;
        TextView tvUsername;
        TextView tvTime;
        TextView tvLike;
        ImageView ivProfile;
    }

    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramPhoto photo = getItem(position);

        ViewHolder viewHolder;
        if(convertView == null){//not recycled view
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);

            viewHolder.tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
            viewHolder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.tvLike = (TextView) convertView.findViewById(R.id.tvLike);
            viewHolder.ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvCaption.setText(photo.caption);
        if(!photo.caption.equals("")){
            //Linkify.addLinks(viewHolder.tvCaption, Linkify.ALL);
            Linkify.addLinks(viewHolder.tvCaption, mentionPattern, mentionScheme, null, filter);
            Linkify.addLinks(viewHolder.tvCaption, hashtagPattern, hashtagScheme, null, filter);
            Linkify.addLinks(viewHolder.tvCaption, urlPattern, null, null, filter);
            viewHolder.tvCaption.setAutoLinkMask(0);
        }

        viewHolder.tvUsername.setText(photo.username);
        CharSequence s = DateUtils.getRelativeTimeSpanString(photo.createdTime * 1000,
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS);
        viewHolder.tvTime.setText(s);

        DecimalFormat format = new DecimalFormat("#,###");
        viewHolder.tvLike.setText(format.format(photo.likesCount) + " likes");

        viewHolder.ivPhoto.setImageResource(0);//clears if image view was recycled
        Picasso.with(getContext())
                .load(photo.imageUrl)
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerInside()
                .into(viewHolder.ivPhoto);

        viewHolder.ivProfile.setImageResource(0);//clears if image view was recycled
        Picasso.with(getContext())
                .load(photo.profileImageUrl)
                //.placeholder(R.drawable.ph_profile)
                //.fit()
                .resize(100,0)
                .transform(transformation)
                .into(viewHolder.ivProfile);

        return convertView;
    }
}
