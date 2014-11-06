package kr.ac.gachon.clo.event;

import android.app.Activity;

public interface ActivityExecuteResultHandler extends ExecuteResultHandler {

	Activity getActivity();
}