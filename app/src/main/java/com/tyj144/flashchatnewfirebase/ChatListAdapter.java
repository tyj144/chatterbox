package com.tyj144.flashchatnewfirebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tylerjiang on 1/3/18.
 */

public class ChatListAdapter extends BaseAdapter {

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mSnapshotList;

    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Log.d("FlashChat", "Loading snapshot: " + dataSnapshot.toString());
            mSnapshotList.add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public ChatListAdapter(Activity activity, DatabaseReference ref, String name) {

        mActivity = activity;
        mDatabaseReference = ref.child("messages");
        mDatabaseReference.addChildEventListener(mListener);

        mDisplayName = name;
        mSnapshotList = new ArrayList<DataSnapshot>();

    }

    // holds onto a chat message row
    static class ViewHolder {
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    @Override
    public int getCount() {
        return mSnapshotList.size();
    }

    @Override
    public InstantMessage getItem(int i) {
        DataSnapshot snapshot = mSnapshotList.get(i);

        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // check if an old view can be recycled
        if (view == null) {
            // make a new view
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.chat_msg_row, viewGroup, false);

            final ViewHolder holder = new ViewHolder();
            holder.authorName = (TextView) view.findViewById(R.id.author);
            holder.body = (TextView) view.findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();

            view.setTag(holder);
        }

        final InstantMessage message = getItem(i);
        final ViewHolder holder = (ViewHolder) view.getTag();

        boolean isUser = mDisplayName.equals(message.getAuthor());
        setChatRowAppearance(isUser, holder);

        String author = message.getAuthor();
        holder.authorName.setText(author);

        String body = message.getMessage();
        holder.body.setText(body);

        // use old view
        return view;
    }

    private void setChatRowAppearance(boolean isUser, ViewHolder holder) {
        if (isUser) {
            // right align
            holder.params.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.parseColor("#27AE60"));
            holder.body.setBackgroundResource(R.drawable.bubble2);
        }
        else {
            // right align
            holder.params.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble1);
        }

        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);
    }

    public void cleanup() {
        mDatabaseReference.removeEventListener(mListener);
    }
}
