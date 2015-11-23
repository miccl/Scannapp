package michii.de.scannapp._view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import michii.de.scannapp.R;
import michii.de.scannapp.model.business_logic.document.Account;

/**
 *  ViewAdapter for {@link michii.de.scannapp._view.fragments.AccountViewFragment}.
 * Provides itemViews for a list of {@link Account}.
 * @author Michii
 * @since 02.07.2015
 */
public class AccountViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mLayoutInflater;
    private List<Account> mAccounts = new ArrayList<>();

    /**
     * Constructs a new adapter using the specified context and account list
     * @param context the execution context
     * @param accounts the accounts to present
     */
    public AccountViewAdapter(Context context, List<Account> accounts) {
        mLayoutInflater = LayoutInflater.from(context);
        mAccounts = accounts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_account_view, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Account currentAccount = mAccounts.get(position);
        CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
        categoryViewHolder.name.setText(currentAccount.getName());
        categoryViewHolder.email.setText(currentAccount.getEmail());
    }

    @Override
    public int getItemCount() {
        return mAccounts.size();
    }

    public void delete(Account account) {
        int position = mAccounts.indexOf(account);
        mAccounts.remove(account);
        if (position == 0) { // Bug: See https://github.com/lucasr/twoway-view/issues/134
            notifyDataSetChanged();
        } else {
            notifyItemRemoved(position);
        }

    }

    public void add(Account account) {
        mAccounts.add(account);
        notifyItemInserted(mAccounts.indexOf(account));
    }

    public void update(int position, Account account) {
            Account acc = mAccounts.get(position);
            acc.setEmail(account.getEmail());
            acc.setName(account.getName());
            notifyItemChanged(position);
    }

    public void setData(List<Account> accounts) {
        mAccounts = accounts;
        notifyDataSetChanged();
    }


    class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        private TextView email;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            email = (TextView) itemView.findViewById(R.id.tv_email);
        }

    }
}
