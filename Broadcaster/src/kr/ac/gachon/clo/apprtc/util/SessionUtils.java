package kr.ac.gachon.clo.apprtc.util;

import org.webrtc.MediaConstraints;

public class SessionUtils {

	private static MediaConstraints constraint = new MediaConstraints();

	static {
		constraint.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
		// constraint.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
		// constraint.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
	}

	public static MediaConstraints getConstraint() {
		return constraint;
	}

	private SessionUtils() {}
}