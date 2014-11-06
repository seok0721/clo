package kr.ac.gachon.clo.event;

public interface ExecuteResultHandler {

	void onSuccess();

	void onFailure(String message);
}