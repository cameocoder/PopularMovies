package com.cameocoder.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cameocoder.popularmovies.data.MovieContract.TrailerEntry;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerItemHolder> {

    public final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    private Activity activity;
    Cursor cursor;

    public TrailerAdapter(Activity activity) {
        this.activity = activity;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }


    public String getFirstTrailerUrl() {
        if (cursor != null && cursor.moveToFirst()) {
            final String key = cursor.getString(cursor.getColumnIndex(TrailerEntry.COLUMN_KEY));
            return getTrailerUriString(key);
        }
        return null;
    }

    @Override
    public TrailerItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.trailer_item, parent, false);
        return new TrailerItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrailerItemHolder holder, int position) {
        cursor.moveToPosition(position);

        final String name = cursor.getString(cursor.getColumnIndex(TrailerEntry.COLUMN_NAME));
        final String key = cursor.getString(cursor.getColumnIndex(TrailerEntry.COLUMN_KEY));
        holder.trailerTitle.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getTrailerUriString(key))));
            }
        });
    }

    @NonNull
    private String getTrailerUriString(String key) {
        return activity.getString(R.string.youtube_trailer_base_url) + key;
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }


    public class TrailerItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.trailer_title)
        TextView trailerTitle;

        public TrailerItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
