package kr.ac.gachon.clo.view;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public interface SignInView {

	Button getSignInButton();

	EditText getEmail();
	EditText getPassword();

	TextView getSignUpLabel();
}