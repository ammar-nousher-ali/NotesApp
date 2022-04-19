package com.example.notestakingappmvvm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton btnAddNote = findViewById(R.id.btn_add_note);
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, Common.ADD_NOTE_REQUEST);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

//        noteViewModel= new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                //update recycler view
                adapter.setNotes(notes);
//                Toast.makeText(MainActivity.this, "onchanged", Toast.LENGTH_SHORT).show();
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(Common.EXTRA_ID,note.getId());
                intent.putExtra(Common.EXTRA_TITLE, note.getTitle());
                intent.putExtra(Common.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(Common.EXTRA_PRIORITY, note.getPriority());
                startActivityForResult(intent,Common.EDIT_NOTE_REQUEST);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Common.ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            String title = data.getStringExtra(Common.EXTRA_TITLE);
            String desc = data.getStringExtra(Common.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(Common.EXTRA_PRIORITY, 1);

            Note note = new Note(title, desc, priority);
            noteViewModel.insert(note);

            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
        }else if(requestCode == Common.EDIT_NOTE_REQUEST && resultCode == RESULT_OK){

            int id = data.getIntExtra(Common.EXTRA_ID,-1);
            if (id==-1) {
                Toast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(Common.EXTRA_TITLE);
            String desc = data.getStringExtra(Common.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(Common.EXTRA_PRIORITY, 1);
            Note note = new Note(title,desc,priority);
            note.setId(id);
            noteViewModel.update(note);

            Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes: {
                noteViewModel.deleteAllNotes();
                Toast.makeText(this, "All notes Deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);

            }

        }
    }
}