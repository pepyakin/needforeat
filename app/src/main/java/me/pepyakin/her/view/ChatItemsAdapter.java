package me.pepyakin.her.view;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import me.pepyakin.her.model.ChatItem;

final class ChatItemsAdapter
        extends RecyclerView.Adapter<ChatItemsAdapter.ViewHolder> {

    private List<ChatItem> items = Collections.emptyList();

    public void setItems(List<ChatItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView messageTextView = new TextView(parent.getContext());
        return new ViewHolder(messageTextView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatItem chatItem = items.get(position);

        int desiredGravity;
        if (chatItem.inbound) {
            desiredGravity = Gravity.START;
        } else {
            desiredGravity = Gravity.END;
        }
        holder.messageTextView.setText(chatItem.text);
        holder.messageTextView.setGravity(desiredGravity);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    final static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView messageTextView;

        public ViewHolder(TextView messageTextView) {
            super(messageTextView);
            this.messageTextView = messageTextView;
        }
    }
}
