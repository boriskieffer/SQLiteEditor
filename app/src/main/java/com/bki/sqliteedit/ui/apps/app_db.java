package com.bki.sqliteedit.ui.apps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bki.sqliteedit.MainActivity;
import com.bki.sqliteedit.R;
import com.bki.sqliteedit.TablesListActivity;
import com.bki.sqliteedit.ui.apps.AppsFragment.AppAdapter.ViewHolder;
import com.topjohnwu.superuser.Shell;

import java.util.ArrayList;
import java.util.List;

public class app_db extends AppCompatActivity {

    List<Database> appDatabases = new ArrayList<Database>();
    public LayoutInflater layoutInflater;
    private String pkg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_db);

        // Get passed data
        Intent intent = getIntent();
        String app_name = intent.getStringExtra("pkgName");
        pkg = intent.getStringExtra("pkg");

        new Thread(() -> {
            Context that = this;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ListView appDbListView = findViewById(R.id.app_db_list);
                    DatabaseAdapter DbListAdapter = new DatabaseAdapter(that, getDatabases());
                    if (appDatabases.size() > 0) {
                        appDbListView.setAdapter(DbListAdapter);
                    } else {
                        Toast.makeText(that, "No databases found!", Toast.LENGTH_LONG).show();
                    }
                    findViewById(R.id.loading_text).setVisibility(View.INVISIBLE);
                }
            });
        }).start();





        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(app_name);
    }

    private List<Database> getDatabases (){

        Log.d("info", "app pkg : " + pkg);

        String cmd = "grep -R -i 'SQLite*' /data/data/"+ pkg +" | awk '{print $3}'";

        Log.d("cmd", cmd);

        Shell.Result res = Shell.su(cmd).exec();
        for(int i=0;i<res.getOut().size();i++) {
            String dbPath = res.getOut().get(i);
            String dbName = dbPath.substring(dbPath.lastIndexOf('/')+1, dbPath.length());

            appDatabases.add(new Database(dbPath, dbName));
        }

        return appDatabases;
    }

    public class DatabaseAdapter extends BaseAdapter {
        private List<Database> dbList;

        public DatabaseAdapter(Context context, List<Database> dbList) {
            layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.dbList = dbList;
        }

        @Override
        public int getCount() {
            return dbList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DbLvHolder listViewHolder;

            if (convertView == null) {
                listViewHolder = new DbLvHolder();



                convertView = layoutInflater.inflate(R.layout.db_file, parent, false);
                convertView.setOnClickListener(v -> {
                    Intent intent = new Intent(getApplicationContext(), TablesListActivity.class);
                    intent.putExtra("db", appDatabases.get(position).getPath());
                    startActivity(intent);
                });
                
                listViewHolder.dbName = (TextView)convertView.findViewById(R.id.db_name);

                convertView.setTag(listViewHolder);
            }else{
                listViewHolder = (DbLvHolder) convertView.getTag();
            }

            listViewHolder.dbName.setText(appDatabases.get(position).getName());


            return convertView;
        }
        class DbLvHolder {
            TextView dbName;
        }
    }

    public class Database {
        private String path;
        private String name;
        public Database(String path, String name) {
            this.path = path;
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public String getName() {
            return name;
        }
    }
}