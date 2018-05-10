package com.imdyc.sw.serverwindows;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

//    private ListView listView;
//    private SimpleAdapter simpleAdapter;
//    private List<Map<String,Object>> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        //控制台服务器列表
//        listView = (ListView)findViewById(R.id.listView_Console);
//        dataList = new ArrayList<Map<String, Object>>();
//        simpleAdapter = new SimpleAdapter(this,getData(),R.layout.console_server_icon,
//                new String[]{"console_server_icon","server_text","server_ip"},
//                new int[]{R.id.console_server_icon,R.id.server_text,R.id.server_ip});
//        listView.setAdapter(simpleAdapter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//之前悬浮的邮件按钮，现已删除
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

//    //将服务器传过来的主机信息包装成List
//    private  List<Map<String, Object>> getData() {
//        //服务器个数
//        int ServerCon = 20;
//        for(int i=1;i<=ServerCon;i++){
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("console_server_icon",R.mipmap.ic_launcher);
//            map.put("server_text", " " + "Server"+i);
//            map.put("server_ip", " " + "192.168.0.1");
//
//            dataList.add(map);
//        }
//        return dataList;
//    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Handle the camera action
        //menu/activity_main_drawer.xml(左拉菜单)中的列表项，一项对应一行
        if (id == R.id.nav_console) {

            Intent intent = new Intent(this,ConsoleActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_security) {

            Intent intent = new Intent(this,SecurityActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_ServerQuantityManagement) {

//        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
