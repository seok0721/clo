package kr.ac.gachon.clo.view;

import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public interface SignUpView {

	Button getSignUpButton();

	Bitmap getThumbnailBitmap();

	EditText getEmail();
	EditText getPassword();
	EditText getConfirmPassword();
	EditText getName();

	ImageView getThumbnail();
}