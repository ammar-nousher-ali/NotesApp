package com.example.notestakingappmvvm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class AddEditNoteActivity extends AppCompatActivity {

    private EditText edtTitle;
    private EditText edtDesc;
    private NumberPicker numberPickerPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        edtTitle = findViewById(R.id.edt_title);
        edtDesc = findViewById(R.id.edt_desc);
        numberPickerPriority = findViewById(R.id.number_picker_priority);

        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        Intent intent = getIntent();
        if (intent.hasExtra(Common.EXTRA_ID)) {
            setTitle("Edit Note");
            edtTitle.setText(intent.getStringExtra(Common.EXTRA_TITLE));
            edtDesc.setText(intent.getStringExtra(Common.EXTRA_DESCRIPTION));
            numberPickerPriority.setValue(intent.getIntExtra(Common.EXTRA_PRIORITY, 1));
        } else {
            setTitle("Add Note");

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note: {

                saveNote();
                return true;

            }
            default: {

                return super.onOptionsItemSelected(item);
            }

        }
    }

    private void saveNote() {
        String title = edtTitle.getText().toString();
        String desc = edtDesc.getText().toString();
        int priority = numberPickerPriority.getValue();
        if (title.trim().isEmpty() || desc.trim().isEmpty()) {
            Toast.makeText(this, "Please insert title and description", Toast.LENGTH_SHORT).show();
        } else {


            Intent data = new Intent();

            data.putExtra(Common.EXTRA_TITLE, title);
            data.putExtra(Common.EXTRA_DESCRIPTION, desc);
            data.putExtra(Common.EXTRA_PRIORITY, priority);

            int id = getIntent().getIntExtra(Common.EXTRA_ID, -1);
            if (id != -1) {
                data.putExtra(Common.EXTRA_ID, id);
            }

            setResult(RESULT_OK, data);
            finish();
        }
    }
}