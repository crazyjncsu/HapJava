package com.guok.hap.impl.responses;

import com.guok.hap.impl.http.HttpResponse;

public class NotFoundResponse implements HttpResponse {

	@Override
	public int getStatusCode() {
		return 404;
	}

}
