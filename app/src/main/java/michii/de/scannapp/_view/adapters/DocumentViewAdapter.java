package michii.de.scannapp._view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import michii.de.scannapp.R;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.model.data.file.DateUtil;

/**
 * ViewAdapter for {@link michii.de.scannapp._view.fragments.DocumentViewFragment}.
 * Provides itemViews for a list of {@link Document}. *
 * @author Michii
 * @since 02.05.2015
 */
public class DocumentViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Document> mDocumentList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;


    public DocumentViewAdapter(Context context, List<Document> documentList) {
        mLayoutInflater = LayoutInflater.from(context);
        mDocumentList = documentList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_document_view, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        DocumentViewHolder documentHolder = (DocumentViewHolder) holder;
        Document currentDoc = mDocumentList.get(position);
        documentHolder.title.setText(currentDoc.getTitle());
        documentHolder.date.setText(DateUtil.getDateString(currentDoc.getDate()));
        documentHolder.info.setText(currentDoc.getAttachedPictures().size() + "");

    }

    @Override
    public int getItemCount() {
        return mDocumentList.size();
    }

    public void delete(Document doc) {
        int position = mDocumentList.indexOf(doc);
        mDocumentList.remove(doc);
        if (position == 0) { // Bug: See https://github.com/lucasr/twoway-view/issues/134
            notifyDataSetChanged();
        } else {
            notifyItemRemoved(position);
        }
    }

    public void setData(List<Document> documentList) {
        mDocumentList = documentList;
        notifyDataSetChanged();
    }

    public void update(int position, Document changed_document) {
        Document doc = mDocumentList.get(position);
        doc.setTitle(changed_document.getTitle());
        notifyItemChanged(position);
    }

    public void add(Document doc) {
        notifyItemInserted(mDocumentList.indexOf(doc));
    }

    class DocumentViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView date;
        public TextView info;

        public DocumentViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv_image);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            date = (TextView) itemView.findViewById(R.id.tv_date);
            info = (TextView) itemView.findViewById(R.id.tv_info);

        }

    }
}
