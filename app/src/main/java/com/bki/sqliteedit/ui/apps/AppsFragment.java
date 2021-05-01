package com.bki.sqliteedit.ui.apps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bki.sqliteedit.R;

import java.util.ArrayList;
import java.util.List;

public class AppsFragment extends Fragment {
    private List<AppList> installedApps;
    private AppAdapter installedAppAdapter;
    ListView userInstalledApps;

    private AppsViewModel appsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        appsViewModel = new ViewModelProvider(this).get(AppsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_apps, container, false);

        userInstalledApps = (ListView) root.findViewById(R.id.test);

        installedApps = getInstalledApps();
        installedAppAdapter = new AppAdapter(getContext(), installedApps);
        userInstalledApps.setAdapter(installedAppAdapter);



        return root;
    }

    private List<AppList> getInstalledApps() {
        PackageManager pm = getActivity().getPackageManager();

        final List<AppList> apps = new ArrayList<AppList>();
        final List <ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                String appName = (String) pm.getApplicationLabel(packageInfo);
                Drawable icon = packageInfo.loadIcon(getActivity().getPackageManager());
                String packageName = packageInfo.packageName;
                String packageVer = "";
                try {
                     packageVer = getActivity().getPackageManager().getPackageInfo(packageName, 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    packageVer = "???";
                    e.printStackTrace();

                }

                apps.add(new AppList(appName, icon, packageName, packageVer));
            }
        }
        return apps;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }



    public class AppAdapter extends BaseAdapter {
        public LayoutInflater layoutInflater;
        public List<AppList> listStorage;

        public AppAdapter(Context context, List<AppList> customizedListView){
            layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listStorage = customizedListView;
        }

        @Override
        public int getCount() {
            return listStorage.size();
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
            ViewHolder listViewHolder;
            listViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item, parent, false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), app_db.class);
                    Log.d("pos", "selecting pos " + String.valueOf(position));
                    intent.putExtra("pkg", listStorage.get(position).getPackages());
                    intent.putExtra("pkgName", listStorage.get(position).getName());
                    startActivity(intent);

                }
            });

            listViewHolder.textInListView = (TextView)convertView.findViewById(R.id.app_name);
            listViewHolder.imageInListView = (ImageView)convertView.findViewById(R.id.app_icon);
            listViewHolder.packageInListView=(TextView)convertView.findViewById(R.id.app_package);
            listViewHolder.verInListView=(TextView)convertView.findViewById(R.id.app_pkgver);
            convertView.setTag(listViewHolder);

            listViewHolder.textInListView.setText(listStorage.get(position).getName());
            listViewHolder.imageInListView.setImageDrawable(listStorage.get(position).getIcon());
            listViewHolder.packageInListView.setText(listStorage.get(position).getPackages());
            listViewHolder.verInListView.setText(listStorage.get(position).getVersion());


            return convertView;
        }

        class ViewHolder{
            TextView textInListView;
            ImageView imageInListView;
            TextView packageInListView;
            TextView verInListView;
        }
        }
    }

    class AppList{
        private String name;
        private Drawable icon;
        private String packages;
        private String packageVer;

        public AppList(String name,Drawable icon, String packages, String packageVer){
            this.name = name;
            this.icon=icon;
            this.packages = packages;
            this.packageVer = packageVer;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return packageVer;
        }

        public Drawable getIcon() {
            return icon;
        }

        public String getPackages() {
            return packages;
        }
}