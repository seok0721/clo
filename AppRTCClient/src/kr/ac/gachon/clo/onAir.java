package kr.ac.gachon.clo;

import java.util.ArrayList;

import kr.ac.gachon.clo.apprtc.impl.BroadcastService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class onAir extends Activity {
    DrawerLayout mDrawerLayout;
    LinearLayout mDrawerLinear;
    ListView mDrawerList;

    MyListAdapter MyAdapter;
    ArrayList<MyItem> arItem;

    public Button btnComment, btnInfo;
    TextView user, title, address;

    ListView commentPage;
    LinearLayout infoPage;

    private static final String TAG = SignInActivity.class.getSimpleName();
    private ShootingView shootingView;

    private View btnStart;
    private View btnStop;

    int sw=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_air);

        //Initialize TabBtn
        btnComment = (Button)findViewById(R.id.btn_comment);
        btnInfo = (Button)findViewById(R.id.btn_info);

        btnComment.setOnClickListener(mClickListener);
        btnInfo.setOnClickListener(mClickListener);

        //Initialize View
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLinear = (LinearLayout) findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.left_list);

        //Initialize menu(comments, information)
        commentPage = (ListView)findViewById(R.id.left_list);
        infoPage = (LinearLayout)findViewById(R.id.left_info);

        //Initialize Textview
        user = (TextView)findViewById(R.id.txt_user);
        title = (TextView)findViewById(R.id.txt_title);
        address = (TextView)findViewById(R.id.txt_location);

        //인텐트를 통해서 세팅하는 부분
        Intent intent = getIntent();
        user.setText(intent.getStringExtra("name"));
        title.setText(intent.getStringExtra("title"));
        address.setText(intent.getStringExtra("address"));

        arItem = new ArrayList<MyItem>();
        MyItem mi;

        //input Data
        mi = new MyItem("I wanna go oracle", "Nick"); arItem.add(mi);
        mi = new MyItem("Hello World", "Jungwoon"); arItem.add(mi);
        mi = new MyItem("Objective C", "Dave"); arItem.add(mi);
        mi = new MyItem("Node.js", "Prazy"); arItem.add(mi);

        MyAdapter = new MyListAdapter(this, R.layout.list_form, arItem);

        mDrawerList.setAdapter(MyAdapter);
    }

    //Custumizing List View
    class MyItem{
        String Comment;
        String User;

        MyItem(String aComment, String aUser) {
            Comment = aComment;
            User = aUser;
        }
    }

    class MyListAdapter extends BaseAdapter {
        Context maincon;
        LayoutInflater Inflater;
        ArrayList<MyItem> arSrc;
        int layout;

        public MyListAdapter(Context context, int alayout, ArrayList<MyItem> aarSrc){
            maincon = context;
            Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            arSrc=aarSrc;
            layout=alayout;
        }

        public int getCount(){
            return arSrc.size();
        }

        public String getItem(int position){
            return arSrc.get(position).User;
        }

        public long getItemId(int position){
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;

            if (convertView == null) {
                convertView = Inflater.inflate(layout, parent, false);
            }

            //UserComment of listView
            TextView comment = (TextView)convertView.findViewById(R.id.list_comment);
            comment.setText(arSrc.get(pos).Comment);

            //UserId of listView
            TextView user = (TextView)convertView.findViewById(R.id.list_user);
            user.setText(arSrc.get(pos).User);

            return convertView;
        }
    }

    //TabBtn Listener
    Button.OnClickListener mClickListener = new Button.OnClickListener() {
        public void onClick(View v){
            commentPage.setVisibility(View.GONE);
            infoPage.setVisibility(View.GONE);

            switch(v.getId()){
                case R.id.btn_comment :
                    commentPage.setVisibility(View.VISIBLE);
                    btnComment.setBackgroundColor(Color.rgb(255, 255, 255));
                    btnComment.setTextColor(Color.rgb(38,38,38));
                    btnInfo.setBackgroundColor(Color.rgb(232,232,232));
                    btnInfo.setTextColor(Color.rgb(155,155,155));
                    break;

                case R.id.btn_info :
                    infoPage.setVisibility(View.VISIBLE);
                    btnComment.setBackgroundColor(Color.rgb(232,232,232));
                    btnComment.setTextColor(Color.rgb(155, 155, 155));
                    btnInfo.setBackgroundColor(Color.rgb(255,255,255));
                    btnInfo.setTextColor(Color.rgb(38, 38, 38));
                    break;
            }
        }
    };


    private void initView() {
        //setContentView(R.layout.activity_my);

        shootingView = (ShootingView)findViewById(R.id.shootingView);
        shootingView.init(this);
    }

    private void bindEvent() {

        btnStart = (View)findViewById(R.id.shootingView);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sw == 0) {
                    BroadcastService.getInstance().start();
                    Toast.makeText(onAir.this, "Broadcast Start", Toast.LENGTH_SHORT).show();
                    sw=1;
                } else {
                    BroadcastService.getInstance().stop();
                    Toast.makeText(onAir.this, "Broadcast Stop", Toast.LENGTH_SHORT).show();
                    sw=0;
                }
            }
        });

    }


}
