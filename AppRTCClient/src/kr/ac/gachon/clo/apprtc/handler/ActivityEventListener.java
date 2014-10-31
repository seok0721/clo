package kr.ac.gachon.clo.apprtc.handler;

import java.util.EventListener;
import java.util.EventObject;

public interface ActivityEventListener extends EventListener {

	EventObject getEvent();
}