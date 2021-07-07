package com.example.smores.adapters;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smores.R;
import com.example.smores.entities.Smores;
import com.example.smores.listeners.SmoresListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
     private List<Smores> smore;
     private SmoresListener smoresListener;
     private Timer timer;
     private List<Smores> smoresSource;

     public NotesAdapter(List<Smores> smore, SmoresListener smoresListener)
     {
         this.smore = smore;
         this.smoresListener = smoresListener;
         smoresSource = smore;
     }


     @Override
     public int getItemViewType(int position) {
         return position;
     }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,
                        parent,
                        false

                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
            holder.setNote(smore.get(position));
            holder.layoutNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    smoresListener.onSmoresClicked(smore.get(position), position);
                }
            });
    }

    @Override
    public int getItemCount() {
        return smore.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutNote;


        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutNote = itemView.findViewById(R.id.layoutNote);
        }

        void setNote(Smores smore) {
            textTitle.setText(smore.getTitle());
            if (smore.getSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(smore.getSubtitle());
            }
            textDateTime.setText(smore.getDateTime());
        }
    }

    public void searchNotes(final String searchKeyword) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    smore = smoresSource;
                } else {
                    ArrayList<Smores> temp = new ArrayList<>();
                    for (Smores note : smoresSource) {
                        if (note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || note.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(note);
                        }
                    }
                    smore = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        },500);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
