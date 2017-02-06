package ace.cse.finalyrproject.spotter;

/**
 * Created by lenovo on 16/1/17.
 */



        import android.content.Context;
        import android.content.Intent;
        import android.support.v7.widget.PopupMenu;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.bumptech.glide.Glide;

        import java.util.List;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private Context mContext;
    private List<Menu> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    public MenuAdapter(Context mContext, List<Menu> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Menu album = albumList.get(position);
        holder.title.setText(album.getName());

        // loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
              //  showPopupMenu(holder.overflow);

                if( position == 0) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, CurrentLocation.class);
                    context.startActivity(intent);
                }
                else if ( position == 1)
                {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, FindFriendsHomeActivity.class);
                    context.startActivity(intent);
                }
                else if ( position == 2)
                {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("type","atm");
                    context.startActivity(intent);
                }
                else if ( position == 3)
                {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("type","hospital");
                    context.startActivity(intent);
                }
                else if ( position == 4)
                {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("type","restaurant");
                    context.startActivity(intent);
                }
                else if ( position == 5)
                {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("type","school");
                    context.startActivity(intent);
                }

            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }
*/


    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
