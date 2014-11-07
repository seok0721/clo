package kr.ac.gachon.clo.view;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public interface ReadyView {

	Button getOnAirButton();

	EditText getTitleName();

	TextView getAddress();
	TextView getName();

	ImageView getThumbnailImage();
}