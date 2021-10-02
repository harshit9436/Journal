package Util;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.journal.JournalListActivity;
import com.example.journal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class JournalRecyclerViewAdapter extends RecyclerView.Adapter<JournalRecyclerViewAdapter.ViewHolder>  {
    private Context context;
    private List<Journal> journalList;
    onJournalClickListener onJournalClickListener;

    public JournalRecyclerViewAdapter() {
    }

    public JournalRecyclerViewAdapter( List<Journal> journalList,onJournalClickListener onJournalClickListener) {
        this.journalList = journalList;
        this.onJournalClickListener = onJournalClickListener;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_row_layout , parent , false);

        return new ViewHolder(view, onJournalClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull  JournalRecyclerViewAdapter.ViewHolder holder, int position) {

        Journal journal = journalList.get(position);
        String imageURL = journal.getImageUrl();
        String title = journal.getTitle().toString().trim();
        String thought = journal.getThought().toString().trim();

        Picasso.get().load(imageURL).placeholder(R.drawable.landscape).fit().into(holder.imageView);

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds()*1000);

        holder.title_textview.setText(title);
        holder.thought_textview.setText(thought);
        holder.dateAdded_textview.setText(timeAgo);
        holder.name_textview.setText(JournalApi.getInstance().getUsername());

    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private TextView title_textview , thought_textview, dateAdded_textview, name_textview;
        private Button share_info;
        private onJournalClickListener onJournalClickListener;


        public ViewHolder(@NonNull View itemView, onJournalClickListener onJournalClickListener) {
            super(itemView);
            this.onJournalClickListener = onJournalClickListener;
            itemView.setOnClickListener(this);
            imageView  = itemView.findViewById(R.id.row_imageview);
            title_textview = itemView.findViewById(R.id.row_title_textview);
            thought_textview = itemView.findViewById(R.id.row_thought_textview);
            dateAdded_textview = itemView.findViewById(R.id.row_dateCreated_textview);
            name_textview = itemView.findViewById(R.id.name_row_textview);
        }

        @Override
        public void onClick(View v) {
            Journal  journal= journalList.get(getAdapterPosition());

            if(v.getId() == name_textview.getId()){
                onJournalClickListener.onShareButtonClick(journal);

            }
            else{
                onJournalClickListener.onJournalClick(journal);
            }


        }
    }

public interface onJournalClickListener{
        void onJournalClick(Journal journal);
        void onShareButtonClick(Journal journal);
}
}
