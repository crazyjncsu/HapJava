package com.guok.hap.impl.json;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.guok.hap.impl.responses.OkResponse;

class HapJsonResponse extends OkResponse {
	
	private static final Map<String, String> headers = Collections.unmodifiableMap(
			new HashMap<String, String>() {
				private static final long serialVersionUID = 1L;
				{
					put("Content-type", "application/hap+json");
				}
			});

	public HapJsonResponse(byte[] body) {
		super(body);
	}
	
	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

}
