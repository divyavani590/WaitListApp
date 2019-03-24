package info.androidhive.sqlite.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import info.androidhive.sqlite.R;
import info.androidhive.sqlite.database.WaitListDatabaseHelper;
import info.androidhive.sqlite.database.model.Waitlist;
import info.androidhive.sqlite.utils.MyDividerItemDecoration;
import info.androidhive.sqlite.utils.RecyclerTouchListener;

public class WaitListActivity extends AppCompatActivity {
    private WaitListAdapter mAdapter;
    private List<Waitlist> notesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noNotesView;

    private WaitListDatabaseHelper db;
    private String tbl_name = "";
    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_notes_view);
        Intent intent = getIntent();
        tbl_name = intent.getStringExtra("course");
        title = intent.getStringExtra("courseTitle");
        db = new WaitListDatabaseHelper(this);
        setTitle(title);
        notesList.addAll(db.getAllNotes(tbl_name));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });

        mAdapter = new WaitListAdapter(this, notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotes();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createNote(String note, String priority) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(tbl_name, note, priority);

        // get the newly inserted note from db
        Waitlist n = db.getNote(tbl_name,id);

        if (n != null) {
            // adding new note to array list at 0 position
            notesList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(String note, String priority, int position) {
        Waitlist n = notesList.get(position);
        // updating note text
        n.setNote(note);
        n.setPriority(priority);

        // updating note in db
        db.updateNote(tbl_name,n);

        // refreshing the list
        notesList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteNote(tbl_name,notesList.get(position));

        // removing the note from the list
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, notesList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showNoteDialog(final boolean shouldUpdate, final Waitlist note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.wait_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(WaitListActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_wait_title) : getString(R.string.lbl_edit_wait_title));

        final Spinner dropdown = view.findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        String[] items = new String[]{"Graduate", "UG-Year 4", "UG-Year 3"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        if (shouldUpdate && note != null) {
            inputNote.setText(note.getNote());
            String priority = note.getPriority();
            int spinnerPosition = adapter.getPosition(priority);
            dropdown.setSelection(spinnerPosition);
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString())) {
                    Toast.makeText(WaitListActivity.this, "Enter Syudent Name!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }
                String priority = dropdown.getSelectedItem().toString();
                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
                    updateNote(inputNote.getText().toString(), priority, position);

                } else {
                    // create new note
                    createNote(inputNote.getText().toString(), priority);
                }
            }
        });
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (db.getNotesCount(tbl_name) > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }
}
