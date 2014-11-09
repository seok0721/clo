package kr.ac.gachon.clo.service;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.gachon.clo.Global;
import kr.ac.gachon.clo.event.ActivityEventHandler;
import kr.ac.gachon.clo.event.ActivityExecuteResultHandler;
import kr.ac.gachon.clo.event.EventHandler;
import kr.ac.gachon.clo.handler.HandshakeHandler;
import kr.ac.gachon.clo.handler.ViewerCountHandler;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

public class SocketService {

	private static final String TAG = SocketService.class.getSimpleName();
	private static SocketService instance = new SocketService();
	private SocketCallback callback = new SocketCallback();
	private SocketIO socket;

	public static SocketService getInstance() {
		return instance;
	}

	public void setFrontActivityHandler(ActivityExecuteResultHandler frontActivityHandler) {
		callback.setFrontActivityHandler(frontActivityHandler);
	}

	public void getViewerCount() {
		socket.emit("viewer_count");
	}

	public void start() {
		if(socket != null && socket.isConnected()) {
			Log.w(TAG, "이미 서버에 접속되어 있습니다.");
			return;
		}

		try {
			socket = new SocketIO("http://211.189.20.193:10000");
			socket.connect(callback);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void stop() {
		if(socket != null && socket.isConnected()) {
			socket.disconnect();
		}
	}

	public void signIn(String email, String password) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("email", email);
		param.put("pwd", password);

		sendMessage("signin", param);
	}

	public void signUp(String email, String password, String name, String base64EncodedBitmap) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("email", email);
		param.put("pwd", password);
		param.put("name", name);
		param.put("img", base64EncodedBitmap);

		sendMessage("signup", param);
	}

	public void createRoom(String title) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("title", title);

		sendMessage("create", param);
	}

	public void destroy() {
		sendMessage("destroy");
	}

	public void handshake(String viewer, String sdp) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("viewer", viewer);
		param.put("channel", Global.getChannel());
		param.put("sdp", sdp);

		sendMessage("handshake2", param);
	}

	public void signOut() {
		sendMessage("signout");
	}

	public void addEventHandler(EventHandler eventHandler) {
		callback.addEventHandler(eventHandler);
	}

	public void removeEventHandler(EventHandler eventHandler) {
		callback.removeEventHandler(eventHandler);
	}

	private void sendMessage(String event, Map<String, Object> param) {
		if((socket == null) || !socket.isConnected()) {
			Log.e(TAG, "시그널링 서버와 연결되지 않았습니다.");
			return;
		}

		if(param == null) {
			socket.emit(event);
			return;
		}

		JSONObject json = new JSONObject();

		try {
			for(String key : param.keySet()) {
				json.put(key, param.get(key));
			}

			socket.emit(event, json);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);

			try {
				json.put("ret", 1);
			} catch(Exception ex) {}

			callback.on(event, null, json);
		}
	}

	private void sendMessage(String event) {
		sendMessage(event, null);
	}

	private static class SocketCallback implements IOCallback {

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
			eventHandlerList.add(ViewerCountHandler.getInstance());
		}

		public void setFrontActivityHandler(ActivityExecuteResultHandler frontActivityHandler) {
			this.frontActivityHandler = frontActivityHandler;
		}

		public void addEventHandler(EventHandler eventHandler) {
			eventHandlerList.add(eventHandler);
		}

		public void removeEventHandler(EventHandler eventHandler) {
			Iterator<EventHandler> iter = eventHandlerList.iterator();

			while(iter.hasNext()) {
				EventHandler mEventhandler = iter.next();

				if(eventHandler == mEventhandler) {
					iter.remove();
					return;
				}
			}
		}
	}
}