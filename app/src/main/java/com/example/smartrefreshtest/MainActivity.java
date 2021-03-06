package com.example.smartrefreshtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView BarTheme;
    private TextView BarDay;
    private ImageButton btn_mainicon;
    private RecyclerView recyclerView;
    private TextView ii;
    private Map map;
    private TextView main_barmonth;

    int n = 0;
    int p = 0;
    private ViewPager vp;
    private String imageUrl[];
    private List<ImageView> data;
    private Map imagemap;
    private List<Map<String, Object>> ImageUrlList = new ArrayList<>();
    private boolean isStart = false;
    private MyThread t;
    private LinearLayout ll_tag;
    private ImageView tag[];
    private Handler mHandler;

    private String year_str;
    private String month_str;
    private String day_str;
    private String NowTime;
    private String NowTimeNoChange;
    private String NowTimeLoadMore;
    private int kk = 0;
    private int pp = 0;
    private int jj = -1;

//    private LooperPagerAdapter mLooperPagerAdapter;
//    private MyViewPager mLoopPager;
//    private static List<String> sPics = new ArrayList<>();
//    static {
//        sPics.add("https://pic1.zhimg.com/v2-2a531353d98bc142413ae142b7a705b0.jpg");
////        sPics.add(R.drawable.icon2);
////        sPics.add(R.drawable.icon1);
//    }
//    private Handler mHandler;
//    private boolean mIsTouch = false;
//    private LinearLayout mPointContainer;

    List<Map<String, Object>> list = new ArrayList<>();



    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vp = (ViewPager) findViewById(R.id.vp);
        ll_tag = (LinearLayout) findViewById(R.id.ll_tag);

//        initView();
//        mHandler = new Handler();
//

        BarTheme = findViewById(R.id.main_bartheme);
        BarDay = findViewById(R.id.main_barday);
        btn_mainicon = findViewById(R.id.btn_mainicon);
        recyclerView = findViewById(R.id.recyclerView);
        ii = findViewById(R.id.ii);
        main_barmonth = findViewById(R.id.main_barmonth);


//        init();

        //知乎日报 字体加粗
        TextPaint textPaint = BarTheme.getPaint();
        textPaint.setFakeBoldText(true);
        TextPaint textPaint1 = BarDay.getPaint();
        textPaint1.setFakeBoldText(true);

//        btn_mainicon.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(Main.this, Home.class);
//                startActivity(intent);
//            }
//        });
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("加载中");
        progressDialog.setMessage("正在加载中......");
        progressDialog.setCancelable(true);
        progressDialog.show();

        Thread thread;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    Date date = new Date();
                    //获取今日新闻
                    URL url = new URL("http://news-at.zhihu.com/api/3/stories/latest");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    //读取刚刚获取的输入流
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    showResponseToday(response.toString());
                    showResponseTop(response.toString());
                    //其他日期新闻
                    NowTime = getOldDate(0);
                    for (int i = 0; i < 3; i++) {
                        URL url1 = new URL("http://news.at.zhihu.com/api/1.2/stories/before/" + NowTime);
                        connection = (HttpURLConnection) url1.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        InputStream in1 = connection.getInputStream();
                        //读取刚刚获取的输入流
                        reader = new BufferedReader(new InputStreamReader(in1));
                        StringBuilder response1 = new StringBuilder();
                        String line1;
                        while ((line1 = reader.readLine()) != null) {
                            response1.append(line1);
                        }
                        showResponse(response1.toString());
                        kk = kk - 1;
                        NowTime = getOldDate(kk);
                        progressDialog.dismiss();}
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        thread.start();


        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {

                Thread thread;

                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection connection = null;
                        BufferedReader reader = null;
                        try {
                            Date date = new Date();
                            //获取今日新闻
                            URL url = new URL("http://news-at.zhihu.com/api/3/stories/latest");
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(8000);
                            connection.setReadTimeout(8000);
                            InputStream in = connection.getInputStream();
                            //读取刚刚获取的输入流
                            reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            showResponseToday(response.toString());
                            showResponseTop(response.toString());
                            //之前新闻
                            NowTimeNoChange = getOldDate(0);
                            pp = 0;
                            kk = -3;
                            jj = -4;
                            for (int i = 0; i < 3; i++) {
                                URL url1 = new URL("http://news.at.zhihu.com/api/1.2/stories/before/" + NowTimeNoChange);
                                connection = (HttpURLConnection) url1.openConnection();
                                connection.setRequestMethod("GET");
                                connection.setConnectTimeout(8000);
                                connection.setReadTimeout(8000);
                                InputStream in1 = connection.getInputStream();
                                //读取刚刚获取的输入流
                                reader = new BufferedReader(new InputStreamReader(in1));
                                StringBuilder response1 = new StringBuilder();
                                String line1;
                                while ((line1 = reader.readLine()) != null) {
                                    response1.append(line1);
                                }
                                showResponse(response1.toString());
                                pp = pp - 1;
                                NowTimeNoChange = getOldDate(pp);}
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                });

                thread.start();

                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {

                Thread thread;

                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection connection = null;
                        BufferedReader reader = null;
                        try {
                            Date date = new Date();
                            //获取新闻
                            NowTimeLoadMore = getOldDate(kk);
                            URL url = new URL("http://news.at.zhihu.com/api/1.2/stories/before/" + NowTimeLoadMore);
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(8000);
                            connection.setReadTimeout(8000);
                            InputStream in = connection.getInputStream();
                            //读取刚刚获取的输入流
                            reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            showResponse(response.toString());
                            kk = kk - 1;
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                });

                thread.start();

                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });

    }

    // @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    //今日新闻的
    public void showResponseToday(final String string) {

        list.clear();
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("stories");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                int news_id = jsonObject1.getInt("news_id");
                String url = jsonObject1.getString("url");
                String title = jsonObject1.getString("title");
                String hint = jsonObject1.getString("hint");
                String news_id = jsonObject1.getString("id");
                JSONArray jsonArray3 = jsonObject1.getJSONArray("images");
                String images = jsonArray3.get(0).toString();

                map = new HashMap();

//                map.put("news_id",news_id);
                map.put("url", url);
                map.put("images", images);
                map.put("title", title);
                map.put("hint", hint);
                map.put("id", news_id);
                map.put("cheak", "1");
                map.put("item", "0");

                list.add(map);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //
//                            textView.setText(string);
//                    String news_id = list.get(0).get("news_id").toString();
//                    String url = list.get(0).get("url").toString();
//                    String thumbnail = list.get(0).get("thumbnail").toString();
//                    String title = list.get(2).get("title").toString();

//                    String s = "news_id:" + news_id + " url:" + url + " thumbnail:" + thumbnail + " title:" + title;
//                    ii.setText(title);

                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));//纵向
                    recyclerView.setAdapter(new MainAdapter(MainActivity.this, list));
                    recyclerView.setNestedScrollingEnabled(false);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    //之前日期新闻的
    public void showResponse(final String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("news");
            for (int i = -1; i < jsonArray.length(); i++) {
                if (i == -1) {
                    String date11 = getOldDate11(kk-1);
                    map = new HashMap();
                    map.put("cheak", "0");
                    map.put("item", date11);
                    list.add(map);
                } else {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                int news_id = jsonObject1.getInt("news_id");
                    String url = jsonObject1.getString("url");
                    String images = jsonObject1.getString("image");
                    String title = jsonObject1.getString("title");
                    String hint = jsonObject1.getString("hint");
                    String news_id = jsonObject1.getString("id");


                    map = new HashMap();

//                map.put("news_id",news_id);
                    map.put("url", url);
                    map.put("images", images);
                    map.put("title", title);
                    map.put("hint", hint);
                    map.put("id", news_id);
                    map.put("cheak", "1");
                    map.put("item", "0");


                    list.add(map);
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    main_barmonth.setText(NowTime);

                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));//纵向
                    recyclerView.setAdapter(new MainAdapter(MainActivity.this, list));
                    recyclerView.setNestedScrollingEnabled(false);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //头条新闻的
    public void showResponseTop(final String string) {


        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("top_stories");
            imageUrl = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                imageUrl[i] = jsonObject1.getString("image");
                String url = jsonObject1.getString("url");

                imagemap = new HashMap();
                imagemap.put("url", url);

                ImageUrlList.add(imagemap);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 0:
                        n++;
                        Bitmap bitmap = (Bitmap) msg.obj;
//                        ImageView iv = new ImageView(MainActivity.this);
//                        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                        iv.setImageBitmap(bitmap);
//                        data.add(iv);
                        if (n == imageUrl.length) {
                            vp.setAdapter(new MyPagerAdapter(data, ImageUrlList, MainActivity.this));
                            creatTag();
                            isStart = true;
                            t = new MyThread();
                            t.start();
                        }
                        break;
                    case 1:
                        int page = (Integer) msg.obj;
                        vp.setCurrentItem(page);

                        break;
                }
            };
        };

        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                p = position;
                int currentIndex = (position % imageUrl.length);
                for (int i = 0; i < tag.length; i++) {
                    if (i == currentIndex) {
                        tag[i].setBackgroundResource(R.drawable.shape_point_selected);
                    } else {
                        tag[i].setBackgroundResource(R.drawable.shape_point_normal);
                    }
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                //}
            }
        });

        data=new ArrayList<ImageView>();
        for(int i=0;i<imageUrl.length;i++){
            getImageFromNet(imageUrl[i]);
            ImageView iv = new ImageView(MainActivity.this);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            iv.setImageBitmap(bitmap);
            Glide.with(MainActivity.this).load(imageUrl[i]).into(iv);
            data.add(iv);
        }

    }

    private void getImageFromNet(final String imagePath) {
        new Thread(){
            public void run() {
                try {
                    URL url=new URL(imagePath);
                    HttpURLConnection con=(HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(10*1000);
                    InputStream is=con.getInputStream();
                    Bitmap bitmap= BitmapFactory.decodeStream(is);
                    Message message=new Message();
                    message.what=0;
                    message.obj=bitmap;
                    mHandler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            };
        }.start();
    }

    //控制图片轮播
    class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            while(isStart){
                Message message=new Message();
                message.what=1;
                message.obj=p;
                mHandler.sendMessage(message);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                p++;
            }
        }
    }

    protected void creatTag() {
        tag = new ImageView[imageUrl.length];
        for (int i = 0; i < imageUrl.length; i++) {

            tag[i] = new ImageView(MainActivity.this);
            if (i == 0) {
                tag[i].setBackgroundResource(R.drawable.shape_point_selected);
            } else {
                tag[i].setBackgroundResource(R.drawable.shape_point_normal);
            }
            tag[i].setPadding(15, 15, 15, 15);
            tag[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ll_tag.addView(tag[i]);
        }
    }

//    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        //当我这个界面绑定到窗口的时候
//        mHandler.post(mLooperTask);
//    }
//
//    @Override
//    public void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        mHandler.removeCallbacks(mLooperTask);
//    }
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//
//    }
//
//    private Runnable mLooperTask = new Runnable() {
//        @Override
//        public void run() {
//            if (!mIsTouch) {
//                //切换viewPager里的图片到下一个
//                int currentItem = mLoopPager.getCurrentItem();
//                mLoopPager.setCurrentItem(++currentItem, true);
//            }
//            mHandler.postDelayed(this, 3000);
//        }
//    };
//
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    private void initView() {
//        //就是找到这个viewPager控件
//        mLoopPager = (MyViewPager) this.findViewById(R.id.looper_pager);
//        //设置适配器
//        mLooperPagerAdapter = new LooperPagerAdapter();
//        mLooperPagerAdapter.setData(sPics);
//        mLoopPager.setAdapter(mLooperPagerAdapter);
//        mLoopPager.setOnViewPagerTouchListener(this);
//        mLoopPager.addOnPageChangeListener(this);
//        mPointContainer = (LinearLayout) this.findViewById(R.id.points_container);
//        //根据图片的个数,去添加点的个数
//        insertPoint();
//        mLoopPager.setCurrentItem(mLooperPagerAdapter.getDataRealSize() * 100, false);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    private void insertPoint() {
//        for (int i = 0; i < sPics.size(); i++) {
//            View point = new View(this);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40, 40);
//            point.setBackground(getResources().getDrawable(R.drawable.shape_point_normal));
//            layoutParams.leftMargin = 20;
//            point.setLayoutParams(layoutParams);
//            mPointContainer.addView(point);
//        }
//    }
//
//    //@Override
//    public void onPagerTouch(boolean isTouch) {
//        mIsTouch = isTouch;
//    }
//
//
//    //@Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//    }
//
//    //@Override
//    public void onPageSelected(int position) {
//        //这个方法的调用,其实是viewPager停下来以后选中的位置
//        int realPosition;
//        if (mLooperPagerAdapter.getDataRealSize() != 0) {
//            realPosition = position % mLooperPagerAdapter.getDataRealSize();
//        } else {
//            realPosition = 0;
//        }
//        setSelectPoint(realPosition);
//    }
//
//    private void setSelectPoint(int realPosition) {
//        for (int i = 0; i < mPointContainer.getChildCount(); i++) {
//            View point = mPointContainer.getChildAt(i);
//            if (i != realPosition) {
//                //那就是白色
//                point.setBackgroundResource(R.drawable.shape_point_normal);
//            } else {
//                //选中的颜色
//                point.setBackgroundResource(R.drawable.shape_point_selected);
//            }
//        }
//    }
//
//    //@Override
//    public void onPageScrollStateChanged(int state) {
//
//    }

    //计算过去时间
    public static String getOldDate(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        LogUtil.d("前7天==" + dft.format(endDate));
        return dft.format(endDate);
    }

    //计算过去时间
    public static String getOldDate11(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("MM月dd日");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        LogUtil.d("前7天==" + dft.format(endDate));
        return dft.format(endDate);
    }
}

