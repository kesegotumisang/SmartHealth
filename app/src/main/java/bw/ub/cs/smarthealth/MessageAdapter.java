package bw.ub.cs.smarthealth;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private final int LEFT = 0, RIGHT = 1;

    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private Context mContext;
    private ArrayList<MessageModel> messages;

    public MessageAdapter(Context mContext, ArrayList<MessageModel> messages){
        this.mContext = mContext;
        this.messages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = null;
        switch (i){
            case LEFT:
                viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.message_item_left, viewGroup, false));
                break;
            case RIGHT:
                viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.message_item_right, viewGroup, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        Glide.with(mContext)
                .asBitmap()
                .load(messages.get(i).getProfileImg())
                .apply(bitmapTransform(new CircleCrop()))
                .into(viewHolder.profilePictureImageView);

        viewHolder.usernameTextView.setText(messages.get(i).getUsername());
        //viewHolder.timeStampTextView.setText(Utils.getDate(new Date(messages.get(i).getTimestamp()), mContext));
        viewHolder.messageTextView.setText(messages.get(i).getMessage());
    }

    @Override
    public int getItemCount() {
        return messages!=null?messages.size():0;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getUserId().equals(uid)?RIGHT:LEFT;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView timeStampTextView, usernameTextView, messageTextView;
        ImageView profilePictureImageView;

        ViewHolder(View itemView) {
            super(itemView);

            profilePictureImageView = (ImageView) itemView.findViewById(R.id.profilePictureImageView);

            timeStampTextView = (TextView) itemView.findViewById(R.id.timeStampTextView);
            usernameTextView = (TextView) itemView.findViewById(R.id.usernameTextView);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);


        }
    }


}
