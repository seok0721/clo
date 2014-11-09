package kr.ac.gachon.clo.activity;

import java.util.ArrayList;

import kr.ac.gachon.clo.Global;
import kr.ac.gachon.clo.R;
import kr.ac.gachon.clo.handler.CameraViewHandler;
import kr.ac.gachon.clo.handler.CommentButtonHandler;
import kr.ac.gachon.clo.handler.HandshakeHandler;
import kr.ac.gachon.clo.handler.InfoButtonHandler;
import kr.ac.gachon.clo.handler.PlayButtonHandler;
import kr.ac.gachon.clo.handler.ViewerCountHandler;
import kr.ac.gachon.clo.service.PeerConnectionGenerator;
import kr.ac.gachon.clo.service.SocketService;
import kr.ac.gachon.clo.view.ShootingView;
import kr.ac.gachon.clo.webrtc.CameraView;

import org.webrtc.PeerConnectionFactory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

public class ShootingActivity extends Activity implements ShootingView {

	private static final String TAG = ShootingActivity.class.getSimpleName();
	private CameraView cameraView;
	private LinearLayout  pnlInfo;
	private ListView pnlComment;
	private Button btnComment;
	private Button btnInfo;
	private ImageView btnPlay;
	// private ImageView btnLike;
	private TextView txtTitle;
	private TextView txtHudTitle;
	private TextView txtClientCount;
	private TextView txtUser;
	private TextView txtAddress;
	private MyListAdapter MyAdapter;
	private ArrayList<MyItem> arItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shooting);

		PeerConnectionFactory.initializeAndroidGlobals(this, true, true); // #1

		cameraView = (CameraView)findViewById(R.id.cameraView);
		cameraView.initialize(this); // #2
		cameraView.setOnClickListener(new CameraViewHandler(this));

		PeerConnectionGenerator.setup(); // #3

		SocketService.getInstance().addEventHandler(new HandshakeHandler());
		ViewerCountHandler.getInstance().setShootingActivity(this);

		btnComment = (Button)findViewById(R.id.btn_comment);
		btnComment.setOnClickListener(new CommentButtonHandler(this));

		btnInfo = (Button)findViewById(R.id.btn_info);
		btnInfo.setOnClickListener(new InfoButtonHandler(this));

		btnPlay = (ImageView)findViewById(R.id.btnPlay);
		btnPlay.setOnClickListener(new PlayButtonHandler(this));

		pnlComment = (ListView)findViewById(R.id.left_list);
		pnlInfo = (LinearLayout)findViewById(R.id.left_info);

		txtUser = (TextView)findViewById(R.id.txt_user);
		txtUser.setText(getIntent().getStringExtra("name"));

		txtTitle = (TextView)findViewById(R.id.txt_title);
		txtTitle.setText(Global.getChannel());

		txtHudTitle = (TextView)findViewById(R.id.shootTitle);
		txtHudTitle.setText(Global.getChannel());

		txtAddress = (TextView)findViewById(R.id.txt_location);
		txtAddress.setText(getIntent().getStringExtra("address"));

		txtClientCount = (TextView)findViewById(R.id.txtPop);
		txtClientCount.setText("접속자: 0명");

		arItem = new ArrayList<MyItem>();
		MyItem mi;

		//input Data
		mi = new MyItem("I wanna go oracle", "Nick"); arItem.add(mi);
		mi = new MyItem("Hello World", "Jungwoon"); arItem.add(mi);
		mi = new MyItem("Objective C", "Dave"); arItem.add(mi);
		mi = new MyItem("Node.js", "Prazy"); arItem.add(mi);

		MyAdapter = new MyListAdapter(this, R.layout.list_form, arItem);
		pnlComment.setAdapter(MyAdapter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.i(TAG, "onDestroy");

		SocketService.getInstance().destroy();

		PeerConnectionGenerator.getInstance().close();
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

		public MyListAdapter(Context context, int alayout, ArrayList<MyItem> aarSrc) {
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

	@Override
	public Button getCommentButton() {
		return btnComment;
	}

	@Override
	public Button getInfoButton() {
		return btnInfo;
	}

	@Override
	public LinearLayout getInfoPanel() {
		return pnlInfo;
	}

	@Override
	public ListView getCommentPanel() {
		return pnlComment;
	}

	@Override
	public ImageView getPlayButton() {
		return btnPlay;
	}

	@Override
	public TextView getTitleView() {
		return txtTitle;
	}

	@Override
	public TextView getClientCount() {
		return txtClientCount;
	}
}