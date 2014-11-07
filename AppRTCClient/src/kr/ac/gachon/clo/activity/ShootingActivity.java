package kr.ac.gachon.clo.activity;

import java.util.ArrayList;

import kr.ac.gachon.clo.R;
import kr.ac.gachon.clo.service.PeerConnectionGenerator;
import kr.ac.gachon.clo.service.PeerConnectionPool;
import kr.ac.gachon.clo.service.SocketService;
import kr.ac.gachon.clo.view.ShootingView;

import org.webrtc.PeerConnectionFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShootingActivity extends Activity {

	private static final String TAG = ShootingActivity.class.getSimpleName();
	private ShootingView shootingView;

	Intent intent;

	//by JW
	Button btnComment, btnInfo;
	DrawerLayout mDrawerLayout;
	LinearLayout mDrawerLinear, infoPage;
	ListView mDrawerList, commentPage;

	MyListAdapter MyAdapter;
	ArrayList<MyItem> arItem;

	View btnShow;
	ImageView btnPlay;
	ImageView btnLike;
	TextView shootTitle, txtPop;
	public int swShow=0; //버튼을 보여주기 위한부분
	public int swBtn=1; //버튼을 눌렀을때를 위한부분

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		Log.i(TAG, "onCreate");

		PeerConnectionFactory.initializeAndroidGlobals(this, true, true);

		shootingView = (ShootingView)findViewById(R.id.shootingView);
		shootingView.init(this);


		//버튼들 초기화해주는 부분 byJW
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
		TextView user = (TextView)findViewById(R.id.txt_user);
		TextView title = (TextView)findViewById(R.id.txt_title);
		TextView address = (TextView)findViewById(R.id.txt_location);


		//인텐트를 통해서 세팅하는 부분
		intent = getIntent();
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

		ButtonEvent();


		PeerConnectionGenerator.getInstance().start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.i(TAG, "onDestroy");

		SocketService.getInstance().destroy();

		PeerConnectionGenerator.getInstance().stop();
		PeerConnectionPool.getInstance().release();
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
				btnComment.setBackgroundColor(Color.rgb(255,255,255));
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



	private void ButtonEvent() {

		//Initialize Button
		btnShow = (View)findViewById(R.id.shootingView);
		btnPlay = (ImageView)findViewById(R.id.btnPlay);
		txtPop = (TextView)findViewById(R.id.txtPop);
		shootTitle = (TextView)findViewById(R.id.shootTitle);
		String title = intent.getStringExtra("title");
		String pop = "982";

		txtPop.setText("접속자수 : " + pop);
		shootTitle.setText(title);

		btnShow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(swShow == 0) {
					btnPlay.setVisibility(View.VISIBLE);
					txtPop.setVisibility(View.VISIBLE);
					shootTitle.setVisibility(View.VISIBLE);
					swShow=1;
				} else {
					btnPlay.setVisibility(View.GONE);
					txtPop.setVisibility(View.GONE);
					shootTitle.setVisibility(View.GONE);
					swShow=0;
				}
			}
		});

		btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(swBtn==0){
					//btnPlay.setOnClickListener(new BroadcastStartButtonHandler());

					Toast.makeText(ShootingActivity.this, "방송을 시작합니다.", Toast.LENGTH_SHORT).show();
					btnPlay.setImageBitmap(bitmapResize(R.drawable.stop, 200, 200));
					swBtn=1;

					Log.e("Start swBtn :", String.valueOf(swBtn));
				}
				else {
					//btnPlay.setOnClickListener(new BroadcastStopButtonHandler());

					Toast.makeText(ShootingActivity.this, "방송을 종료합니다.", Toast.LENGTH_SHORT).show();
					btnPlay.setImageBitmap(bitmapResize(R.drawable.start, 200, 200));
					swBtn=0;

					Log.e("Stop swBtn :", String.valueOf(swBtn));
				}
			}
		});
	}
	//비트맵 리사이징 부분
	private Bitmap bitmapResize(int res, int height, int width){

		final BitmapFactory.Options bitOption = new BitmapFactory.Options();
		bitOption.inSampleSize = 2;

		Bitmap bm = BitmapFactory.decodeResource(getResources(), res, bitOption);
		bm = Bitmap.createScaledBitmap(bm, height, width, true);

		return bm;
	}
}