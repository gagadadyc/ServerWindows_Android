package com.imdyc.sw.serverwindows;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.imdyc.sw.serverwindows.application.ResInfoApplication;
import com.imdyc.sw.serverwindows.bean.MemoryInfo;
import com.imdyc.sw.serverwindows.bean.ServerInfo;
import com.imdyc.sw.serverwindows.utility.MapIntent;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

    private PopupWindow popupWindow;// 声明PopupWindow
    private View popupView;// 声明PopupWindow对应的视图
    private TranslateAnimation animation;// 声明平移动画

    private LinkedHashMap<Float, Float> memoryMap;//预加载内存数据
    private LinkedHashMap<Float, Float>  cpuMap = new LinkedHashMap<>();//预加载cpu数据
    private String[] values ;//定义x轴标签
    private SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    private Gson gson = new Gson();


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
        for (int i = 1; i <= 2; i++) {
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


    private void RisingServerInfo(final String ServerName) {
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


        //监控
        popupView.findViewById(R.id.diagram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //子线程中不可执行视图相关操作，使用handler将操作送到主线程执行
                final Handler mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        popupWindow.dismiss();
                    }
                };

                volley_Post(ServerName);


                //若未取回数据，则休眠等待
                Thread thread = new Thread(){
                    @Override
                    public void run(){
                        boolean threadbl = false;
                        try {
                            int i = 0;

                            //轮询方式查找getSharedPreferences，Server无数据则继续查找
                            while (!threadbl && getSharedPreferences("ServerWindows", Context.MODE_PRIVATE).getString("Server", "") == "") {
                                Thread.sleep(1000);//如果找不到，则睡眠1秒钟再访问。
                                i++;
                                System.out.println("i="+i);
                                if(i>10){
                                    Looper.prepare();
                                    Toast.makeText(ConsoleActivity.this, "访问超时", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                    threadbl = Thread.interrupted();
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //若线程未设置中断，则说明访问到了数据
                        if(!threadbl) {
                            SharedPreferences preferences=getSharedPreferences("ServerWindows", Context.MODE_PRIVATE);
                            String serverJson=preferences.getString("Server", "");  // getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值

                            memoryMap = getSPMemMap(serverJson);
                            values = getSPMemArr(serverJson);

//                            Looper.prepare();
//                            popupWindow.dismiss();//跳转前先将附属的窗体关闭，不然会引起窗体泄露
//                            Looper.loop();


                            mHandler.sendEmptyMessage(0);

                            Intent intent = new Intent(ConsoleActivity.this,ChartActivity.class);
                            intent.putExtra("ServerName", inner_SERVERNAME);
                            MapIntent memoryMapIntent = new MapIntent();//由于Serializable不支持LinkedHashMap，所以使用实现LinkedHashMap接口的自定义类
                            memoryMapIntent.setMap(memoryMap);
                            intent.putExtra("memoryMapIntent",memoryMapIntent);
                            intent.putExtra("values",values);

                            Looper.prepare();
                            startActivity(intent);
                            Looper.loop();

//

                        }
                    }
                };
                thread.start();
            }
        });

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
    /**
     * 取得存放在SharedPreferences中的信息
     */
    private LinkedHashMap<Float, Float> getSPMemMap(String serverJson) {

        //Json反序列化
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);
        List<MemoryInfo> memoryList = serverInfo.getMemory();

        memoryMap = new LinkedHashMap();

        int i = 0;
        Iterator<MemoryInfo> iterator = memoryList.iterator();
        //迭代memoryList，并装填内存数据
        while (iterator.hasNext()){
            MemoryInfo momoryInfo = iterator.next();
            memoryMap.put((float)i,Float.parseFloat(momoryInfo.getUsed_Percent())); //将内存百分比转为float

        }

        return memoryMap;
    }
    private String[] getSPMemArr(String serverJson){
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);
        List<MemoryInfo> memoryList = serverInfo.getMemory(); //接收数据

        values = new String[memoryList.size()];  //接收时间作为x轴坐标

        int i = 0;
        Iterator<MemoryInfo> iterator = memoryList.iterator();
        //迭代memoryList，并装填内存时间轴
        while (iterator.hasNext()){
            MemoryInfo momoryInfo = iterator.next();
            SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
            values[i]=sdf.format(momoryInfo.getTime());
            i++;
        }
        return values;
    }

    /**
     * 向服务器提交Post请求
     * @param serverName 请求哪个服务器的信息
     */

    private void volley_Post(String serverName) {

        String url = "http://192.168.0.107:8080/sw/ResInfo";
        //请求数据
        HashMap<String,String> RequstMap = new HashMap<String,String>();
        RequstMap.put("serverName",serverName);

        JSONObject jsonObject = new JSONObject(RequstMap);
        JsonObjectRequest JOrequest = new JsonObjectRequest(Request.Method.POST,url,jsonObject,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {

                //获取存储文件
                SharedPreferences sharedPreferences = getSharedPreferences("ServerWindows", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Server",arg0.toString());//将Json格式的服务器数据存入SharedPreferences中
                editor.commit();
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError arg0){
                Toast.makeText(ConsoleActivity.this,arg0.toString()+"请求失败",Toast.LENGTH_SHORT).show();
            }
        });
        JOrequest.setTag("ServerInfoPost");

        ResInfoApplication.getHttpQueues().add(JOrequest);
    }






}
