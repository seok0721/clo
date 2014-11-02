package kr.ac.gachon.clo.apprtc;

public interface IQueue<T> {

	public void enqueue(T element);

	public T dequeue();
}