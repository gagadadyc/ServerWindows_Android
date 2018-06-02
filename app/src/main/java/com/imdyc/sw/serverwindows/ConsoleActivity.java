package com.imdyc.sw.serverwindows;

import android.app.Activity;
import android.content.Context;
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
import com.imdyc.sw.serverwindows.bean.CPU;
import com.imdyc.sw.serverwindows.bean.Disk;
import com.imdyc.sw.serverwindows.bean.Memory;
import com.imdyc.sw.serverwindows.bean.Processes;
import com.imdyc.sw.serverwindows.bean.ServerInfo;
import com.imdyc.sw.serverwindows.bean.SysPoint;
import com.imdyc.sw.serverwindows.utility.MapIntent;

import org.json.JSONObject;

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


    private List<SysPoint> sysPointList;//从MainActivity传来的服务器信息

    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> dataViewList;  //视图显示的server信息list
    private ArrayList<SysPoint> dataReceiveList; //从服务器接收到的server信息list

    private PopupWindow popupWindow;// 声明PopupWindow
    private View popupView;// 声明PopupWindow对应的视图
    private TranslateAnimation animation;// 声明平移动画

    private LinkedHashMap<Float, Float> memoryMap;//内存数据
    private LinkedHashMap<Float, Float>  cpuMap;//cpu数据
    private LinkedHashMap<Float, Float> readDiskMap;//写磁盘数据
    private LinkedHashMap<Float, Float> writeDiskMap;//写磁盘数据
    private LinkedHashMap<Float, Float>  totalProcessMap;//进程总和数据
    private LinkedHashMap<Float, Float> sleepingProcessMap;//睡眠进程数据
    private LinkedHashMap<Float, Float> zombiesProcessMap;//僵尸进程数据
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

        Intent intent = getIntent();

        //控制台服务器列表
//        sysPointList = (ArrayList<SysPoint>) intent.getSerializableExtra("sysPointList");//获取服务器信息
        listView = (ListView) findViewById(R.id.listView_Console);

        dataReceiveList = (ArrayList<SysPoint>) intent.getSerializableExtra("sysPointList");//获取服务器信息

        simpleAdapter = new SimpleAdapter(this, getData(dataReceiveList), R.layout.console_server_icon,
                new String[]{"console_server_icon", "console_server_text", "console_server_uptime",
                        "console_server_ncpus","console_server_nusers"},
                new int[]{R.id.console_server_icon, R.id.console_server_text, R.id.console_server_uptime,
                        R.id.console_server_ncpus,R.id.console_server_nusers});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(this);
    }

    //将服务器传过来的主机信息包装成List
    private List<Map<String, Object>> getData(ArrayList<SysPoint> ServerInfo) {

        //服务器个数
        int ServerCon = ServerInfo.size();

        dataViewList = new ArrayList<>();
        for (int i = 0; i < ServerCon; i++) {

            SharedPreferences preferences = getSharedPreferences("ServerWindows", Context.MODE_PRIVATE);
            String serverJson = preferences.getString("sysConfirmMap", "");  // 取出确认表
            Map<String, Object> confirmMap = new Gson().fromJson(serverJson, Map.class);

            //如果map中存在该主机名作为key的键值对，并且value为false，则跳过此次循环（不加入显示列表）
            if (confirmMap != null && confirmMap.containsKey(ServerInfo.get(i).getHost()) && !(boolean) confirmMap.get(ServerInfo.get(i).getHost())) {
                continue;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("console_server_icon", R.mipmap.ic_launcher);
            map.put("console_server_text", ServerInfo.get(i).getHost());  //服务器名
            map.put("console_server_uptime", ServerInfo.get(i).getUptime_format());  //运行时间
            map.put("console_server_ncpus", (int)Float.parseFloat(ServerInfo.get(i).getN_cpus()));  //核心数
            map.put("console_server_nusers", (int)Float.parseFloat(ServerInfo.get(i).getN_users()));  //用户数
//            map.put("console_server_ip",ServerInfo.get(i).getIp());

            dataViewList.add(map);
        }
        return dataViewList;
    }

    /**
     * listview的监听器，点击其中一行时，弹出该行对应服务器的菜单
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(position);  //listview的内容装入map，positon为点击的行数。从零开始
        RisingServerInfo((String) map.get("console_server_text"));//弹出底部菜单,参数为服务器id
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


        //内存/CPU
        popupView.findViewById(R.id.diagram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ConsoleActivity.this, "数据正在拉取,请稍后", Toast.LENGTH_SHORT).show();
                //请求信息
                volley_Post(ServerName,"ResInfo");
                //子线程轮询等待信息
                threadWaitJump(ServerName,"ResInfo");
            }
        });

        //磁盘
        popupView.findViewById(R.id.reboot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ConsoleActivity.this, "数据正在拉取,请稍后", Toast.LENGTH_SHORT).show();
                //请求信息
                volley_Post(ServerName,"Disk");
                //子线程轮询等待信息
                threadWaitDisk(ServerName,"Disk");
            }
        });


        //进程
        popupView.findViewById(R.id.shutdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ConsoleActivity.this, "数据正在拉取,请稍后", Toast.LENGTH_SHORT).show();
                //请求信息
                volley_Post(ServerName,"Process");
                //子线程轮询等待信息
                threadWaitProcesses(ServerName,"Process");

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
     * @param serverJson 服务器传来的Json数据
     * 将存放在SharedPreferences中的Json信息进行反序列化，取出内存信息
     */
    private LinkedHashMap<Float, Float> getSPMemMap(String serverJson) {

        //Json反序列化
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);
        List<Memory> memoryList = serverInfo.getMemory();

        memoryMap = new LinkedHashMap<>();

        int i = 0;
        Iterator<Memory> iterator = memoryList.iterator();
        //迭代memoryList，并装填内存数据
        while (iterator.hasNext()){
            Memory memory = iterator.next();
            memoryMap.put((float)i,Float.parseFloat(memory.getUsed_Percent())); //将内存百分比转为float
            i++;
        }

        return memoryMap;
    }

    /**
     * @param serverJson 服务器传来的Json数据
     * 将存放在SharedPreferences中的Json信息进行反序列化，取出cpu信息
     */
    private LinkedHashMap<Float, Float> getSPCPUMap(String serverJson) {

        //Json反序列化
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);

        List<CPU> cpuInfoList = serverInfo.getCPU();

        cpuMap = new LinkedHashMap<>();
        int i = 0;
        Iterator<CPU> iterator = cpuInfoList.iterator();

        //迭代cpuList，装填cpu数据
        while (iterator.hasNext()){
            CPU cpuInfo = iterator.next();
            Float shiyong = 100f-Float.parseFloat(cpuInfo.getUsage_idle()); //将cpu空闲百分比转为float型的使用百分比
            cpuMap.put((float)i,shiyong);
            i++;
        }
        return cpuMap;
    }

    /**
     * @param serverJson 服务器传来的Json数据
     * 将存放在SharedPreferences中的Json信息进行反序列化，取出ReadDisk信息
     */
    private LinkedHashMap<Float, Float> getReadDiskMap(String serverJson) {

        //Json反序列化
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);

        List<Disk> DiskList = serverInfo.getDisks();

        readDiskMap = new LinkedHashMap<>();

        int i = 0;
        Iterator<Disk> iterator = DiskList.iterator();
        //迭代cpuList，装填cpu数据
        while (iterator.hasNext()){
            Disk disk = iterator.next();
            readDiskMap.put((float)i,disk.getRead_speed());
            i++;
        }
        return readDiskMap;
    }

    /**
     * @param serverJson 服务器传来的Json数据
     * 将存放在SharedPreferences中的Json信息进行反序列化，取出writeDisk信息
     */
    private LinkedHashMap<Float, Float> getWriteDiskMap(String serverJson) {

        //Json反序列化
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);

        List<Disk> DiskList = serverInfo.getDisks();

        writeDiskMap = new LinkedHashMap<>();

        int i = 0;
        Iterator<Disk> iterator = DiskList.iterator();
        //迭代List，装填磁盘数据
        while (iterator.hasNext()){
            Disk disk = iterator.next();
            writeDiskMap.put((float)i,disk.getWrite_speed());
            i++;
        }
        return writeDiskMap;
    }

    /**
     * 总线程数量
     * @param serverJson
     * @return
     */
    private LinkedHashMap<Float, Float> getTotalProcessMap(String serverJson) {

        //Json反序列化
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);

        List<Processes> processList = serverInfo.getProcesses();

        totalProcessMap = new LinkedHashMap<>();

        int i = 0;
        Iterator<Processes> iterator = processList.iterator();
        //迭代List，装填进程信息数据
        while (iterator.hasNext()){
            Processes processes = iterator.next();
            totalProcessMap.put((float)i,processes.getTotal());
            i++;
        }
        return totalProcessMap;
    }
    /**
     * 睡眠线程数量
     * @param serverJson
     * @return
     */
    private LinkedHashMap<Float, Float> getSleepingProcessMap(String serverJson) {

        //Json反序列化
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);

        List<Processes> processList = serverInfo.getProcesses();

        sleepingProcessMap = new LinkedHashMap<>();

        int i = 0;
        Iterator<Processes> iterator = processList.iterator();
        //迭代List，装填进程信息数据
        while (iterator.hasNext()){
            Processes processes = iterator.next();
            sleepingProcessMap.put((float)i,processes.getSleeping());
            i++;
        }
        return sleepingProcessMap;
    }

    /**
     * 僵尸线程数量
     * @param serverJson
     * @return
     */
    private LinkedHashMap<Float, Float> getZombiesProcessMap(String serverJson) {

        //Json反序列化
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);

        List<Processes> processList = serverInfo.getProcesses();

        zombiesProcessMap = new LinkedHashMap<>();

        int i = 0;
        Iterator<Processes> iterator = processList.iterator();
        //迭代List，装填进程信息数据
        while (iterator.hasNext()){
            Processes processes = iterator.next();
            zombiesProcessMap.put((float)i,processes.getZombies());
            i++;
        }
        return zombiesProcessMap;
    }



    /**
     * 自定义内存/CPU图表的x轴标签
     * @param serverJson 服务器传来的Json数据
     * @return X轴自定义标签数组
     */
    private String[] getSPMemArr(String serverJson){
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);
        List<Memory> memoryList = serverInfo.getMemory(); //接收数据

        values = new String[memoryList.size()];  //接收时间作为x轴坐标

        int i = 0;
        Iterator<Memory> iterator = memoryList.iterator();
        //迭代memoryList，并装填内存时间轴
        while (iterator.hasNext()){
            Memory memory = iterator.next();
            SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
            values[i]=sdf.format(memory.getTime());
            i++;
        }
        return values;
    }

    /**
     * 自定义线程图表x轴标签
     * @param serverJson 服务器传来的Json数据
     * @return X轴自定义标签数组
     */
    private String[] getProcessArr(String serverJson){
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);
        List<Processes> processList = serverInfo.getProcesses(); //接收数据

        values = new String[processList.size()];  //接收时间作为x轴坐标

        int i = 0;
        Iterator<Processes> iterator = processList.iterator();
        //迭代memoryList，并装填内存时间轴
        while (iterator.hasNext()){
            Processes processes = iterator.next();
            SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
            values[i]=sdf.format(processes.getTime());
            i++;
        }
        return values;
    }


    /**
     * 自定义磁盘图表x轴标签
     * @param serverJson 服务器传来的Json数据
     * @return X轴自定义标签数组
     */
    private String[] getDiskArr(String serverJson){
        ServerInfo serverInfo = gson.fromJson(serverJson, ServerInfo.class);
        List<Disk> diskList = serverInfo.getDisks(); //接收数据

        values = new String[diskList.size()];  //接收时间作为x轴坐标

        int i = 0;
        Iterator<Disk> iterator = diskList.iterator();
        //迭代memoryList，并装填内存时间轴
        while (iterator.hasNext()){
            Disk disk = iterator.next();
            SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
            values[i]=sdf.format(disk.getTime());
            i++;
        }
        return values;
    }


    /**
     * 向服务器提交Post请求
     * @param serverName 请求哪个服务器的信息
     * @param InfoType 对应的数据类型，这将影响到请求的URL和存储的sharedPreferences标识
     */

    private void volley_Post(String serverName,final String InfoType) {


        String url = "http://192.168.1.187:8080/sw/"+InfoType;
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
                editor.putString(InfoType,arg0.toString());//将Json格式的服务器数据存入SharedPreferences中
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

    /**
     * 子线程轮询获取服务器返回的CPU/内存信息
     * @param inner_SERVERNAME 服务器名，访问界面字符串获得
     */
    private void threadWaitJump(final String inner_SERVERNAME,final String InfoType){
        //子线程中不可执行视图相关操作，使用handler将操作送到主线程执行
        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                popupWindow.dismiss();
            }
        };
        //若未取回数据，则休眠等待
        Thread thread2 = new Thread(){
            @Override
            public void run(){
                boolean threadbl = false;
                try {
                    int i = 0;

                    //轮询方式查找getSharedPreferences，Server无数据则继续查找
                    while (!threadbl && getSharedPreferences("ServerWindows", Context.MODE_PRIVATE).getString(InfoType, "") == "") {
                        Thread.sleep(1000);//如果找不到，则睡眠1秒钟再访问。
                        i++;
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
                    SharedPreferences preferences = getSharedPreferences("ServerWindows", Context.MODE_PRIVATE);
                    String serverJson=preferences.getString(InfoType, "");  // getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(InfoType,"");//读取完毕后将SharedPreferences中的值设为空，避免下一次读取由于线程同步问题读到旧数据
                    editor.commit();



                    memoryMap = getSPMemMap(serverJson);
                    cpuMap = getSPCPUMap(serverJson);
                    values = getSPMemArr(serverJson);


                    Intent intent = new Intent(ConsoleActivity.this,MCChartActivity.class);
                    intent.putExtra("ServerName", inner_SERVERNAME);

                    MapIntent memoryMapIntent = new MapIntent();//由于Serializable不支持LinkedHashMap，所以使用实现LinkedHashMap接口的自定义类
                    memoryMapIntent.setLinkedHashMap(memoryMap);
                    intent.putExtra("memoryMapIntent",memoryMapIntent);

                    memoryMapIntent = new MapIntent();
                    memoryMapIntent.setLinkedHashMap(cpuMap);
                    intent.putExtra("cpuMapIntent",memoryMapIntent);

                    intent.putExtra("values",values);

                    //跳转前先将附属的窗体关闭，不然会引起窗体泄露
                    mHandler.sendEmptyMessage(0);
                    Looper.prepare();
                    startActivity(intent);
                    Looper.loop();
                }
            }
        };
        thread2.start();
    }

    /**
     * 子线程轮询获取服务器返回的 磁盘 信息
     * @param inner_SERVERNAME 服务器名，访问界面字符串获得
     * @param InfoType 数据类型，对应请求的URL
     */
    private void threadWaitDisk(final String inner_SERVERNAME,final String InfoType){
        //子线程中不可执行视图相关操作，使用handler将操作送到主线程执行
        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                popupWindow.dismiss();
            }
        };
        //若未取回数据，则休眠等待
        Thread thread2 = new Thread(){
            @Override
            public void run(){
                boolean threadbl = false;
                try {
                    int i = 0;

                    //轮询方式查找getSharedPreferences，Server无数据则继续查找
                    while (!threadbl && getSharedPreferences("ServerWindows", Context.MODE_PRIVATE).getString(InfoType, "") == "") {
                        Thread.sleep(1000);//如果找不到，则睡眠1秒钟再访问。
                        i++;
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
                    SharedPreferences preferences = getSharedPreferences("ServerWindows", Context.MODE_PRIVATE);
                    String serverJson=preferences.getString(InfoType, "");  // getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(InfoType,"");//读取完毕后将SharedPreferences中的值设为空，避免下一次读取由于线程同步问题读到旧数据
                    editor.commit();

                    readDiskMap = getReadDiskMap(serverJson);
                    writeDiskMap = getWriteDiskMap(serverJson);
                    values = getDiskArr(serverJson);


                    Intent intent = new Intent(ConsoleActivity.this,DiskChartActivity.class);
                    intent.putExtra("ServerName", inner_SERVERNAME);

                    MapIntent mapIntent = new MapIntent();//由于Serializable不支持LinkedHashMap，所以使用实现LinkedHashMap接口的自定义类
                    mapIntent.setLinkedHashMap(readDiskMap);
                    intent.putExtra("readDiskMapIntent",mapIntent);

                    mapIntent = new MapIntent();
                    mapIntent.setLinkedHashMap(writeDiskMap);
                    intent.putExtra("writeDiskMapIntent",mapIntent);

                    intent.putExtra("values",values);

                    //跳转前先将附属的窗体关闭，不然会引起窗体泄露
                    mHandler.sendEmptyMessage(0);
                    Looper.prepare();
                    startActivity(intent);
                    Looper.loop();
                }
            }
        };
        thread2.start();
    }

    /**
     * 子线程轮询获取服务器返回的 进程 信息
     * @param inner_SERVERNAME 服务器名，访问界面字符串获得
     * @param InfoType 数据类型，对应请求的URL
     */
    private void threadWaitProcesses(final String inner_SERVERNAME,final String InfoType){
        //子线程中不可执行视图相关操作，使用handler将操作送到主线程执行
        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                popupWindow.dismiss();
            }
        };
        //若未取回数据，则休眠等待
        Thread thread3 = new Thread(){
            @Override
            public void run(){
                boolean threadbl = false;
                try {
                    int i = 0;

                    //轮询方式查找getSharedPreferences，Server无数据则继续查找
                    while (!threadbl && getSharedPreferences("ServerWindows", Context.MODE_PRIVATE).getString(InfoType, "") == "") {
                        Thread.sleep(1000);//如果找不到，则睡眠1秒钟再访问。
                        i++;
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
                    SharedPreferences preferences = getSharedPreferences("ServerWindows", Context.MODE_PRIVATE);
                    String serverJson=preferences.getString(InfoType, "");  // getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(InfoType,"");//读取完毕后将SharedPreferences中的值设为空，避免下一次读取由于线程同步问题读到旧数据
                    editor.commit();

                    totalProcessMap = getTotalProcessMap(serverJson); //总进程
                    sleepingProcessMap= getSleepingProcessMap(serverJson); //睡眠进程
                    zombiesProcessMap = getZombiesProcessMap(serverJson);  //僵尸进程
                    values = getProcessArr(serverJson);  //x轴


                    Intent intent = new Intent(ConsoleActivity.this,ProcessActivity.class);
                    intent.putExtra("ServerName", inner_SERVERNAME);

                    MapIntent mapIntent = new MapIntent();//由于Serializable不支持LinkedHashMap，所以使用实现LinkedHashMap接口的自定义类
                    mapIntent.setLinkedHashMap(totalProcessMap);
                    intent.putExtra("totalProcessMap",mapIntent);

                    mapIntent = new MapIntent();
                    mapIntent.setLinkedHashMap(sleepingProcessMap);
                    intent.putExtra("sleepingProcessMap",mapIntent);

                    mapIntent = new MapIntent();
                    mapIntent.setLinkedHashMap(zombiesProcessMap);
                    intent.putExtra("zombiesProcessMap",mapIntent);

                    intent.putExtra("values",values);

                    //跳转前先将附属的窗体关闭，不然会引起窗体泄露
                    mHandler.sendEmptyMessage(0);
                    Looper.prepare();
                    startActivity(intent);
                    Looper.loop();
                }
            }
        };
        thread3.start();
    }


    //点击重启后向用户弹出确认对话框
//                AlertDialog.Builder builder = new AlertDialog.Builder(ConsoleActivity.this);
//                builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int a){
//                        Toast.makeText(ConsoleActivity.this, "shutdown", Toast.LENGTH_SHORT).show();
//                        popupWindow.dismiss();
//                    }
//                });
//                builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int a){
//                        Toast.makeText(ConsoleActivity.this, "取消shutdown", Toast.LENGTH_SHORT).show();
//                        popupWindow.dismiss();
//                    }
//                });
//                builder.setTitle("重要信息");
//                builder.setMessage("确认要向"+ inner_SERVERNAME +"发出 关机 命令吗？这会使该服务器上正在运行的进程停止");
//                builder.show();

}
