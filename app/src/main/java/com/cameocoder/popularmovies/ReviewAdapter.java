package com.cameocoder.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cameocoder.popularmovies.data.MovieContract.ReviewEntry;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewItemHolder> {

    public final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    private Activity activity;
    Cursor cursor;

    public ReviewAdapter(Activity activity) {
        this.activity = activity;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public ReviewItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.review_item, parent, false);
        return new ReviewItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewItemHolder holder, int position) {
        cursor.moveToPosition(position);

        final String author = cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_AUTHOR));
        final String content = cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_CONTENT));
        final String url = cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_URL));
        holder.reviewAuthor.setText(author);
        holder.reviewContent.setText(content);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }

    public class ReviewItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.review_author)
        TextView reviewAuthor;
        @Bind(R.id.review_content)
        TextView reviewContent;

        public ReviewItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
