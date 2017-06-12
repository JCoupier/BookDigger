package com.example.android.bookdigger;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * BookDigger created by JCoupier on 07/06/2017.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    // The ViewHolder which caches the ImageView and the two TextViews
    private static class ViewHolder {
        public TextView title;
        public TextView author;
        public ImageView bookCover;
    }

    public BookAdapter(Context context, ArrayList<Book> books){
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        // Check if the existing view is being reused, otherwise inflate the view
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);

            // Set the viewHolder
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.book_title);
            viewHolder.author = (TextView) convertView.findViewById(R.id.book_authors);
            viewHolder.bookCover = (ImageView) convertView.findViewById(R.id.book_thumbnail);

            // Store the viewHolder with the views.
            convertView.setTag(viewHolder);
        } else {

            // Avoid calling findViewById() on resource everytime by using the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Find the earthquake at the given position in the list of earthquakes
        Book currentBook = getItem(position);

        // Assign values if the object is not null
        if (currentBook != null){
            // get the TextView from the ViewHolder and then set the text (title)
            viewHolder.title.setText(currentBook.getTitle());
            // get the TextView from the ViewHolder and then set the text (authors)
            viewHolder.author.setText(currentBook.getAuthor());

            // Use the Picasso library to display the smallThumbnail of the current book.
            // If there is no thumbnail or if after three try the thumbnail can't be downloaded:
            // an image placeholder is displayed instead.
            Picasso.with(getContext()).load(currentBook.getSmallThumbnailUrl())
                    .placeholder(R.drawable.book_placeholder)
                    .error(R.drawable.book_placeholder)
                    .into(viewHolder.bookCover);
        }

        // Return the whole list item layout (containing 3 TextViews)
        // so that it can be shown in the ListView
        return convertView;
    }
}
