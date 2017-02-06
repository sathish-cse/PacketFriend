package ace.cse.finalyrproject.spotter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

import model.User;

public class UsersChatAdapter extends RecyclerView.Adapter<UsersChatAdapter.ViewHolderUsers> {

    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";
    private List<User> mUsers;
    private Context mContext;
    private String mCurrentUserEmail,mcurrentUserUrl,mReceiverMail,latLng;
    private Long mCurrentUserCreatedAt;
    private String mCurrentUserId;

    private DatabaseReference databaseReference;


    public UsersChatAdapter(Context context, List<User> fireChatUsers) {
        mUsers = fireChatUsers;
        mContext = context;
    }

    @Override
    public ViewHolderUsers onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderUsers(mContext, LayoutInflater.from(parent.getContext()).inflate(R.layout.user_profile, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolderUsers holder, int position) {

        User fireChatUser = mUsers.get(position);



        // Set avatar
     /*   int userAvatarId= ChatHelper.getDrawableAvatarId(fireChatUser.getAvatarId());
        Drawable avatarDrawable = ContextCompat.getDrawable(mContext,userAvatarId);
        holder.getUserAvatar().setImageDrawable(avatarDrawable);*/

        holder.getUserAvatar().setImageResource(R.drawable.headshot_7);
        // Set display name
        holder.getUserDisplayName().setText(fireChatUser.getDisplayName());

        // Set presence status
        holder.getStatusConnection().setText(fireChatUser.getEmail());


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void refill(User users) {
        mUsers.add(users);
        notifyDataSetChanged();
    }

    public void changeUser(int index, User user) {
        mUsers.set(index,user);
        notifyDataSetChanged();
    }

   /* public void setCurrentUserInfo(String userUid, String email, long createdAt,String imageUrl) {
        mCurrentUserId = userUid;
        mCurrentUserEmail = email;
        mCurrentUserCreatedAt = createdAt;
        mcurrentUserUrl = imageUrl;
    }*/

    public void setCurrentUserInfo(String userUid, String email, long createdAt) {
        mCurrentUserId = userUid;
        mCurrentUserEmail = email;
        mCurrentUserCreatedAt = createdAt;
    }

    public void clear() {
        mUsers.clear();
    }


    /* ViewHolder for RecyclerView */
    public class ViewHolderUsers extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mUserAvatar;
        private TextView mUserDisplayName;
        private TextView mStatusConnection;
        private Context mContextViewHolder;

        public ViewHolderUsers(Context context, View itemView) {
            super(itemView);
            mUserAvatar = (ImageView)itemView.findViewById(R.id.img_avatar);
            mUserDisplayName = (TextView)itemView.findViewById(R.id.text_view_display_name);
            mStatusConnection = (TextView)itemView.findViewById(R.id.text_view_connection_status);
            mContextViewHolder = context;

            itemView.setOnClickListener(this);
        }

        public ImageView getUserAvatar() {
            return mUserAvatar;
        }

        public TextView getUserDisplayName() {
            return mUserDisplayName;
        }
        public TextView getStatusConnection() {
            return mStatusConnection;
        }


        @Override
        public void onClick(View view) {

            final String[] option = new String[] { "Location", "Chat", "Cancel",};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                    android.R.layout.select_dialog_item, option);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub)

                    User user = mUsers.get(getLayoutPosition());

                    String chatRef = user.createUniqueChatRef(mCurrentUserCreatedAt,mCurrentUserEmail);

                    mReceiverMail = user.getReceiverMail(mCurrentUserCreatedAt,mCurrentUserEmail);

                    Intent chatIntent = null;
                    switch (which) {
                        case 0:
                             chatIntent= new Intent(mContextViewHolder, CurrenLocationMap.class);
                            chatIntent.putExtra(ExtraIntent.EXTRA_CURRENT_USER_ID, mCurrentUserId);
                            chatIntent.putExtra(ExtraIntent.EXTRA_RECIPIENT_ID, user.getRecipientId());
                            chatIntent.putExtra(ExtraIntent.EXTRA_CHAT_REF, chatRef);
                            chatIntent.putExtra("Latitude","12.9770093");
                            chatIntent.putExtra("Longitude","80.2239374");
                            break;
                        case 1:
                            chatIntent = new Intent(mContextViewHolder, ChatActivity.class);
                            chatIntent.putExtra(ExtraIntent.EXTRA_CURRENT_USER_ID, mCurrentUserId);
                            chatIntent.putExtra(ExtraIntent.EXTRA_RECIPIENT_ID, user.getRecipientId());
                            chatIntent.putExtra(ExtraIntent.EXTRA_CHAT_REF, chatRef);

                            break;
                        case 2:
                            dialog.dismiss();
                            break;
                        default:
                            break;
                    }


                    // Start new activity
                    mContextViewHolder.startActivity(chatIntent);
                }
            });

            final AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    // String decode to bitmap
    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    public String setData()
    {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference getChildListener = databaseReference.child(mReceiverMail);
        getChildListener.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                latLng = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return latLng;
    }
}
