package com.bki.sqliteedit;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TablesListActivity extends AppCompatActivity {
    String dbPath = "";
    LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables_list);

        Intent intent = getIntent();
        dbPath = intent.getStringExtra("db");


        // We copy the database file in cache directory so we can use it later

        String tempDbPath = this.getCacheDir().getAbsolutePath();
        Shell.Result res = Shell.su("cp " + dbPath + " '" + tempDbPath + "'").exec();

        if(res.isSuccess()) {
            tempDbPath = tempDbPath +  File.separator + new File(dbPath).getName();

            Log.d("success", "db has been copied to cache");

            // The file has been copied with root permissions on it. To fix that,
            // We find the group id of the current running app
            Shell.Result appUid = Shell.su("awk '/^" + getApplicationContext().getPackageName() + "/ {print $2}' /data/system/packages.list").exec();
            if (appUid.getOut().size() == 1) {
                int appUID = Integer.parseInt(appUid.getOut().get(0));
                // We change the owner of the file
                Shell.su("chown " + appUID + " " + tempDbPath).exec();
            }


            List<String> tables = new SQLiteDatabaseHelper(this, tempDbPath, null, 6).fetchAllTables();

            ListView listView = (findViewById(R.id.tblList));

            if (tables.size() > 0) Log.d("firstEl", "first el is " +tables.get(0));

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_db_table, tables);
            listView.setAdapter(arrayAdapter);
        }


    }



}