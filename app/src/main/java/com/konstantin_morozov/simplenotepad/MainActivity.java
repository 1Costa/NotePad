package com.konstantin_morozov.simplenotepad;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.konstantin_morozov.simplenotepad.data.NoteItem;
import com.konstantin_morozov.simplenotepad.data.NotesDataSource;

import java.util.List;
import java.util.Map;

//public class MainActivity extends AppCompatActivity
public class MainActivity extends ListActivity {

    public static final int EDITOR_ACTIVITY_REQUEST = 1001;
    public static final int MENU_DELETE_ID = 1002;
    private int currentNoteId;
    private NotesDataSource datasource;
    List<NoteItem> notesList;

    //NoteItem note ;
    //ListView listView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SharedPreferences notePrefs = getSharedPreferences("notes", Context.MODE_PRIVATE);
        //Map<String, ?> notesMap = notePrefs.getAll();

        //String size = String.valueOf(notesMap.size());
        //Toast.makeText(this, size, Toast.LENGTH_SHORT).show();

        //listView = (ListView) findViewById(R.id.listView) ;

        registerForContextMenu(getListView());

        datasource = new NotesDataSource(this);
        refreshDisplay();

        //android.app.ActionBar actionbar= getActionBar();
        //ActionBar actionbar= getSupportActionBar();
        //actionbar.setDisplayShowHomeEnabled(true);
        //actionbar.setIcon(R.mipmap.ic_launcher);
    }
    private void refreshDisplay(){

        notesList = datasource.findAll();
        ArrayAdapter<NoteItem> adapter = new ArrayAdapter<NoteItem>(this,R.layout.list_item_layout, notesList);

        //listView.setAdapter(adapter);

        setListAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create){
            createNote();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNote() {
        NoteItem note = NoteItem.getNew();
        Intent intent = new Intent(this,NoteEditorActivity.class);
        intent.putExtra("key",note.getKey());
        intent.putExtra("text", note.getText());

        startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
    }

    @Override

    protected void onListItemClick(ListView l, View v, int position, long id) {

        NoteItem note = notesList.get(position);
        Intent intent = new Intent(this,NoteEditorActivity.class);
        intent.putExtra("key",note.getKey());
        intent.putExtra("text", note.getText());

        startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        String text = data.getStringExtra("text");
        if(text.length() > 0) {

            if (requestCode == EDITOR_ACTIVITY_REQUEST && resultCode == RESULT_OK) {
                NoteItem note = new NoteItem();
                note.setKey(data.getStringExtra("key"));
                note.setText(data.getStringExtra("text"));
                datasource.update(note);
                refreshDisplay();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        currentNoteId = (int) info.id;
        menu.add(0,MENU_DELETE_ID, 0, "Delete");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId()== MENU_DELETE_ID){
            NoteItem note = notesList.get(currentNoteId);
            datasource.remove(note);
            refreshDisplay();
        }

        return super.onContextItemSelected(item);
    }
}
