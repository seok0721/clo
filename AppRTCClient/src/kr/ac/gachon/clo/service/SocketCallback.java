package kr.ac.gachon.clo.service;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;

import java.util.ArrayList;
import java.util.List;

import kr.ac.gachon.clo.event.ActivityEventHandler;
import kr.ac.gachon.clo.event.ActivityExecuteResultHandler;
import kr.ac.gachon.clo.event.EventHandler;
import kr.ac.gachon.clo.handler.HandshakeHandler;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

public class SocketCallback implements IOCallback {

	private static final String TAG = SocketCallback.class.getSimpleName();
	private ActivityExecuteResultHandler frontActivityHandler;
	private List<EventHandler> eventHandlerList = new ArrayList<EventHandler>();

	@Override
	public void onConnect() {
		frontActivityHandler.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				frontActivityHandler.onSuccess();
			}
		});
	}

	@Override
	public void onDisconnect() {
		frontActivityHandler.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				frontActivityHandler.onFailure("서버와 접속이 끊어졌습니다.");
			}
		});
	}

	@Override
	public void onError(final SocketIOException e) {
		Log.e(TAG, e.getMessage(), e);

		frontActivityHandler.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				frontActivityHandler.onFailure("서버에 접속하는 중 오류가 발생하였습니다.");
				frontActivityHandler.onFailure(e.getMessage());
			}
		});
	}

	@Override
	public void onMessage(String message, IOAcknowledge ack) {
		Log.w(TAG, "사용하지 않는 메소드 입니다.");
	}

	@Override
	public void onMessage(JSONObject data, IOAcknowledge ack) {
		Log.w(TAG, "사용하지 않는 메소드 입니다.");
	}

	@Override
	public void on(String event, IOAcknowledge ack, Object... param) {
		try {
			final JSONObject data = (JSONObject)param[0];

			for(final EventHandler eventHandler : eventHandlerList) {
				if(event.equals(eventHandler.getEvent())) {
					if(!(eventHandler instanceof ActivityEventHandler)) {
						eventHandler.onMessage(data);
						return;
					}

					Activity activity = ((ActivityEventHandler)eventHandler).getActivity();
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							eventHandler.onMessage(data);
						}
					});

					return;
				}
			}

			throw new Exception(String.format("알 수 없는 이벤트: %s", event));
		} catch(Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public SocketCallback() {
		eventHandlerList.add(new HandshakeHandler());
	}

	public void setFrontActivityHandler(ActivityExecuteResultHandler frontActivityHandler) {
		this.frontActivityHandler = frontActivityHandler;
	}

	public void addEventHandler(EventHandler eventHandler) {
		eventHandlerList.add(eventHandler);
	}
}