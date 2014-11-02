package kr.ac.gachon.clo.apprtc.handler;

import org.json.JSONObject;

public interface EventHandler {

	void handle(JSONObject data);

	String getEvent();
}