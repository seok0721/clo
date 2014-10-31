package kr.ac.gachon.clo.apprtc;

public interface IPeerConnectionGenerator extends IDaemon, Runnable {

	public void orderToCreateConnection();
}