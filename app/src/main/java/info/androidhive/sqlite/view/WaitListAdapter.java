package info.androidhive.sqlite.view;

/**
 * Created by Divya.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import info.androidhive.sqlite.R;
import info.androidhive.sqlite.database.model.Waitlist;

public class WaitListAdapter extends RecyclerView.Adapter<WaitListAdapter.MyViewHolder> {

    private Context context;
    private List<Waitlist> notesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView note;
        public TextView dot;
        public TextView priority;

        public MyViewHolder(View view) {
            super(view);
            note = view.findViewById(R.id.note);
           // dot = view.findViewById(R.id.dot);
            priority = view.findViewById(R.id.priority);
        }
    }


    public WaitListAdapter(Context context, List<Waitlist> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wait_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Waitlist note = notesList.get(position);

        holder.note.setText(note.getNote());

        // Displaying dot from HTML character code
       // holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying timestamp
        holder.priority.setText(note.getPriority());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
}
