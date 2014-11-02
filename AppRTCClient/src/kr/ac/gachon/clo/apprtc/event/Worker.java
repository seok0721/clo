package kr.ac.gachon.clo.apprtc.event;

import org.json.JSONObject;

import android.app.Activity;

public interface Worker {

	void onMessage(JSONObject data);

	Activity getActivity();

	String getEvent();
}