package kr.ac.gachon.clo.webrtc;

import org.webrtc.MediaConstraints;

public class SrtpMediaConstraints extends MediaConstraints {

	public SrtpMediaConstraints() {
		this.optional.add(new KeyValuePair("DtlsSrtpKeyAgreement", "true"));
	}
}