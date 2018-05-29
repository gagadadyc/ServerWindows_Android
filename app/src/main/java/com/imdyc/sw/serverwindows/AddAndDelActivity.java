package com.imdyc.sw.serverwindows;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.imdyc.sw.serverwindows.bean.SysPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 邓远超 on 2018/5/27.
 * 由主菜单：添加/删除服务器进入的界面
 */
public class AddAndDelActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> dataViewList;  //视图显示的server信息list
    private ArrayList<SysPoint> dataReceiveList; //从服务器接收到的server信息list

    private PopupWindow popupWindow;// 声明PopupWindow
    private View popupView;// 声明PopupWindow对应的视图
    private TranslateAnimation animation;// 声明平移动画

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adddel);

        Intent intent = getIntent();

        //控制台服务器列表
//        sysPointList = (ArrayList<SysPoint>) intent.getSerializableExtra("sysPointList");//获取服务器信息
        listView = (ListView) findViewById(R.id.listView_adddel);

        dataReceiveList = (ArrayList<SysPoint>) intent.getSerializableExtra("sysPointList");//获取服务器信息

        simpleAdapter = new SimpleAdapter(this, getData(dataReceiveList), R.layout.adddel_icon,
                new String[]{"console_server_icon", "console_server_text"},
                new int[]{R.id.console_server_icon, R.id.console_server_text});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(this);

        TextView textView = (TextView) findViewById(R.id.adddel_add);
        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(AddAndDelActivity.this, "拉取可添加服务器列表", Toast.LENGTH_SHORT).show();
                RisingServerInfo();//弹出底部菜单,参数为服务器id
            }
        });

    }

    //将服务器传过来的主机信息包装成List
    private List<Map<String, Object>> getData(ArrayList<SysPoint> ServerInfo) {

        //服务器个数
        int ServerCon = ServerInfo.size();

        dataViewList = new ArrayList<>();
        for (int i = 0; i < ServerCon; i++) {

            SharedPreferences preferences = getSharedPreferences("ServerWindows", Context.MODE_PRIVATE);
            String serverJson=preferences.getString("sysConfirmMap", "");  // 取出确认表
            Map<String, Object> confirmMap = new Gson().fromJson(serverJson,Map.class);


            //如果map中存在该主机名作为key的键值对，并且value为false，则跳过此次循环（不加入显示列表）
            if(confirmMap !=null && confirmMap.containsKey(ServerInfo.get(i).getHost()) && !(boolean)confirmMap.get(ServerInfo.get(i).getHost())){
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("console_server_icon", R.mipmap.ic_launcher);
            map.put("console_server_text", ServerInfo.get(i).getHost());  //服务器名
//            map.put("console_server_ip",ServerInfo.get(i).getIp());

            dataViewList.add(map);
        }
        return dataViewList;
    }

    /**
     * listview的监听器，点击其中一行时，弹出该行对应服务器的菜单
     * 菜单内容有：关机，重启，折线图
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(position);  //listview的内容装入map，positon为点击的行数。从零开始
        confirm((String) map.get("console_server_text"));
    }

    private void confirm(final String serverName){
        //点击删除后向用户弹出确认对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(AddAndDelActivity.this);
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a){
                        Map<String,Boolean> map = new HashMap<>();
                        map.put(serverName,false);
                        //转成json格式易于存储
                        Gson gson = new Gson();
                        String jsonStr = gson.toJson(map);
                        //写入删除标记
                        SharedPreferences sharedPreferences = getSharedPreferences("ServerWindows", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("sysConfirmMap",jsonStr);//将Json格式的服务器数据存入SharedPreferences中
                        editor.apply();
                        Toast.makeText(AddAndDelActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a){
                    }
                });
                builder.setTitle("重要信息");
                builder.setMessage("确认删除服务器："+ serverName +"吗？");
                builder.show();
    }

    private void RisingServerInfo() {
        if (popupWindow == null) {
            //导入底部菜单的xml
            popupView = View.inflate(this, R.layout.adddel_botmemu, null);
            // 参数2,3分别是popupwindow的宽度和高度
            popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

        }

        // 设置背景图片，使滑出动画产生效果
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);

        // 设置点击popupwindow外屏幕其它地方消失
        popupWindow.setOutsideTouchable(true);

        // 平移动画相对于手机屏幕的底部开始，X轴不变，Y轴从1变0
        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(300);//平移动画过程为300毫秒
// ////
        ListView listViewBot = (ListView) findViewById(R.id.listView_adddel_bot);

        simpleAdapter = new SimpleAdapter(this, getData(dataReceiveList), R.layout.console_server_icon,
                new String[]{"console_server_text"},
                new int[]{ R.id.console_server_text});
        listViewBot.setAdapter(simpleAdapter);
        listViewBot.setOnItemClickListener(this);

//////
        // 在点击之后设置popupwindow的销毁
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        // 设置popupWindow的显示位置，此处是在手机屏幕底部且水平居中的位置
        popupWindow.showAtLocation(this.findViewById(R.id.console_botmenu_re), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupView.startAnimation(animation);
    }
}
