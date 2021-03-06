package com.guok.hap.impl.http;

import java.io.IOException;

public interface HomekitClientConnection {

	HttpResponse handleRequest(HttpRequest request) throws IOException;

	byte[] decryptRequest(byte[] ciphertext);
	
	byte[] encryptResponse(byte[] plaintext) throws IOException;
	
	void close();

	/**
	 * output/Notification
	 * @param message
	 */
	void outOfBand(HttpResponse message);
	
}
