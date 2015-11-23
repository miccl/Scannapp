package michii.de.scannapp._view.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import michii.de.scannapp.R;
import michii.de.scannapp.model.business_logic.document.Picture;
import michii.de.scannapp.model.data.file.BitmapUtil;
import michii.de.scannapp.model.data.file.DateUtil;
import michii.de.scannapp.model.data.file.FileUtil;

/**
 * ViewAdapter for {@link michii.de.scannapp._view.fragments.PictureViewFragment}.
 * Provides itemViews for a list of {@link Picture}.
 * @author Michii
 * @since 05.06.2015
 */
public class PictureViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<Picture> mPictures = new ArrayList<>();

    public PictureViewAdapter(Context context, List<Picture> pictures) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mPictures = pictures;
    }

    public void setData(List<Picture> listPictures) {
        mPictures = listPictures;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mLayoutInflater.inflate(R.layout.row_picture_view, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        PictureViewHolder pictureHolder = (PictureViewHolder) holder;
        Picture currentPic = mPictures.get(position);
        pictureHolder.size.setText(FileUtil.formatBytes(currentPic.getSize()));
        pictureHolder.date.setText(DateUtil.getDateString(currentPic.getDate()));
//        pictureHolder.title.setText(currentPic.getTitle());
        pictureHolder.title.setText(position+1 + "");
        loadImage(pictureHolder.image, Uri.parse(currentPic.getUriString()));
    }

    public void update(int position, Picture picture) {
        Picture pic = mPictures.get(position);
        pic.setTitle(picture.getTitle());
        notifyItemChanged(position);
    }

    /**
     * Loads the image specified by the given uri in the given imageView.
     * @param imageView imageView to fill
     * @param uri image file's uri
     */
    private void loadImage(ImageView imageView, Uri uri) {
        int size = mContext.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_height);

        //TODO load asynchronously (maybe use bibos like Picasso)
        try {
            Bitmap bitmap = BitmapUtil.decodeFile(mContext, uri, size, 2 * size);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mPictures.size();
    }

    public void remove(Picture pic) {
        int position = mPictures.indexOf(pic);
        mPictures.remove(pic);
        if(position == 0) { // Bug: See https://github.com/lucasr/twoway-view/issues/134
            notifyDataSetChanged();
        } else {
            notifyItemRemoved(position);
        }
    }



    class PictureViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView date;
        public  TextView size;
        public RelativeLayout box;

        public PictureViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv_image);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            date = (TextView) itemView.findViewById(R.id.tv_date);
            size = (TextView) itemView.findViewById(R.id.tv_space);
            box = (RelativeLayout) itemView.findViewById(R.id.rl_box);
        }
    }
}
