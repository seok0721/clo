package kr.ac.gachon.clo.view;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public interface ShootingView {

	Button getCommentButton();
	Button getInfoButton();
	ImageView getPlayButton();

	LinearLayout getInfoPanel();
	ListView getCommentPanel();

	TextView getTitleView();
	TextView getClientCount();
}