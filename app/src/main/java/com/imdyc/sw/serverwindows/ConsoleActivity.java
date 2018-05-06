package com.imdyc.sw.serverwindows;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.imdyc.sw.serverwindows.bean.ServerInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 邓远超 on 2018/5/3.
 * 控制台页面
 */
public class ConsoleActivity extends Activity implements AdapterView.OnItemClickListener {


    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> dataViewList;  //视图显示的server信息list
    private List<ServerInfo> dataReceiveList; //从服务器接收到的server信息list
    private Button serverButton;  //服务器按钮
    // 声明PopupWindow
    private PopupWindow popupWindow;

    // 声明PopupWindow对应的视图
    private View popupView;

    // 声明平移动画
    private TranslateAnimation animation;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console);

        //控制台服务器列表
        listView = (ListView) findViewById(R.id.listView_Console);
        dataViewList = new ArrayList<Map<String, Object>>();


        dataReceiveList = new ArrayList<ServerInfo>();

        //测试数据，android&服务器连通后删
        for (int i = 1; i <= 12; i++) {
            ServerInfo serverInfo = new ServerInfo();
            serverInfo.setId(1);
            serverInfo.setName("Server" + i);
            serverInfo.setIp("172.168.0." + i);
            dataReceiveList.add(serverInfo);
        }

        simpleAdapter = new SimpleAdapter(this, getData(dataReceiveList), R.layout.console_server_icon,
                new String[]{"console_server_icon", "console_server_text", "console_server_ip"},
                new int[]{R.id.console_server_icon, R.id.console_server_text, R.id.console_server_ip});
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(this);

    }

    /**
     * listview的监听器，点击其中一行时，弹出该行对应服务器的菜单
     * 菜单内容有：关机，重启，折线图
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(position);  //listview的内容装入map，positon为点击的行数。从零开始
        Toast.makeText(ConsoleActivity.this, "map=" + map.toString() + " id=" + id, Toast.LENGTH_SHORT).show();
        RisingServerInfo((String) map.get("console_server_text"));//弹出底部菜单,参数为服务器id

    }


    //将服务器传过来的主机信息包装成List
    private List<Map<String, Object>> getData(List<ServerInfo> ServerInfo) {


        //服务器个数
        int ServerCon = ServerInfo.size();

        for (int i = 1; i <= ServerCon; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("console_server_icon", R.mipmap.ic_launcher);
            map.put("console_server_text", "  " + ServerInfo.get(i - 1).getName());
            map.put("console_server_ip", "  " + ServerInfo.get(i - 1).getIp());

            dataViewList.add(map);
        }
        return dataViewList;
    }

    /**
     * 底部弹出窗口
     **/


    private void RisingServerInfo(String ServerName) {
        if (popupWindow == null) {
            //导入底部菜单的xml
            popupView = View.inflate(this, R.layout.console_botmenu, null);
            // 参数2,3分别是popupwindow的宽度和高度
            popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

//            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                @Override
//                public void onDismiss() {
//                    lighton();
//                }
//            });
        }
        //顶部TextView为服务器编号
        TextView serverTextView = (TextView) popupView.findViewById(R.id.bot_server_name);
        serverTextView.setText(ServerName);

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


        final String inner_SERVERNAME = ServerName;  //在匿名内部类中访问的外部变量需用final声明

        //重启
        popupView.findViewById(R.id.reboot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击重启后向用户弹出确认对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(ConsoleActivity.this);
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a){
                        Toast.makeText(ConsoleActivity.this, "reboot", Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    }
                });
                builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a){
                        Toast.makeText(ConsoleActivity.this, "取消reboot", Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    }
                });
                builder.setTitle("重要信息");
                builder.setMessage("确认要向"+ inner_SERVERNAME +"发出 重启 命令吗？这会使该服务器上正在运行的进程停止");
                builder.show();
            }
        });


        //关机
        popupView.findViewById(R.id.shutdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击重启后向用户弹出确认对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(ConsoleActivity.this);
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a){
                        Toast.makeText(ConsoleActivity.this, "shutdown", Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    }
                });
                builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a){
                        Toast.makeText(ConsoleActivity.this, "取消shutdown", Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    }
                });
                builder.setTitle("重要信息");
                builder.setMessage("确认要向"+ inner_SERVERNAME +"发出 关机 命令吗？这会使该服务器上正在运行的进程停止");
                builder.show();
            }
        });
        // 在点击之后设置popupwindow的销毁
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        // 设置popupWindow的显示位置，此处是在手机屏幕底部且水平居中的位置
        popupWindow.showAtLocation(this.findViewById(R.id.console_botmenu_re), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupView.startAnimation(animation);
    }


//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.rlIcon:
//                // TODO 弹出popupwind选择拍照或者从相册选择
//                changeIcon(view);
//                lightoff();
//                break;
//        }

    //点击按钮弹出底部窗口
//        LayoutInflater inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);  //使用LayoutInflater寻找layout下的xml文件
//        View console_server_icon_v = inflater.inflate(R.layout.console_server_icon, null);  //将指定的xml文件转换成view对象
//        serverButton = (Button) console_server_icon_v.findViewById(R.id.console_server_button);  //使用完整形式实例化Button
//        serverButton.setOnClickListener(new View.OnClickListener() {
//            //重写点击事件的处理方法onClick()
//            @Override
//            public void onClick(View v) {
//                //显示Toast信息
//                System.out.println(getApplicationContext()+"++++++++++++++++++++++++++++++++++++233333333333333333333333333333333333333");
//                Toast.makeText(getApplicationContext(), "你点击了按钮", Toast.LENGTH_SHORT).show();
//            }
//        });

}
