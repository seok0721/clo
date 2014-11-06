package kr.ac.gachon.clo.event;

import org.json.JSONObject;

public interface EventHandler {

	void onMessage(JSONObject data);

	String getEvent();
}