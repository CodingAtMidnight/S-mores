package com.example.smores.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.smores.R;
import com.example.smores.adapters.NotesAdapter;
import com.example.smores.database.SmoresDatabase;
import com.example.smores.entities.Smores;
import com.example.smores.listeners.SmoresListener;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity implements SmoresListener {
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTES = 3;



    private RecyclerView notesRecyclerView;
    private List<Smores> smoresList;
    private NotesAdapter notesAdapter;

    private int smoresClickedPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        smoresList = new ArrayList<>();
        notesAdapter = new NotesAdapter(smoresList, this);
        notesRecyclerView.setAdapter(notesAdapter);

        getNotes(REQUEST_CODE_SHOW_NOTES, false);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (smoresList.size() != 0) {
                    notesAdapter.searchNotes(s.toString());
                }
            }
        });
    }

    @Override
    public void onSmoresClicked(Smores smore, int position) {
            smoresClickedPosition = position;
            Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
            intent.putExtra("isViewOrUpdate", true);
            intent.putExtra("smores", smore);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted) {
        @SuppressLint("StaticFieldLeak")
        class GetSmoresTask extends AsyncTask<Void, Void, List<Smores>> {
            @Override
            protected List<Smores> doInBackground(Void... voids) {
                return SmoresDatabase
                        .getDatabase(getApplicationContext())
                        .noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Smores> smores) {
                super.onPostExecute(smores);
                if (requestCode == REQUEST_CODE_SHOW_NOTES) {
                    smoresList.addAll(smores);
                    notesAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    smoresList.add(0, smores.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    smoresList.remove(smoresClickedPosition);
                    if (isNoteDeleted) {
                        notesAdapter.notifyItemRemoved(smoresClickedPosition);
                    } else {
                        smoresList.add(smoresClickedPosition, smores.get(smoresClickedPosition));
                        notesAdapter.notifyItemChanged(smoresClickedPosition);
                    }

                }
            }

        }
        new GetSmoresTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNotes(REQUEST_CODE_ADD_NOTE, false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
            }
        }
    }
}