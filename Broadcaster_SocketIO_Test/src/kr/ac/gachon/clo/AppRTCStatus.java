package kr.ac.gachon.clo;

public class AppRTCStatus {

	private String roomName;

	public boolean hasRoom() {
		return (roomName != null);
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
}