package com.dyaco.spirit_commercial.model.webapi;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class GzipRequestInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // 只壓縮 POST / PUT / PATCH 方法的 Body
        if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
            return chain.proceed(originalRequest);
        }

        Request compressedRequest = originalRequest.newBuilder()
                .header("Content-Encoding", "gzip")  // 告知 Server 這是 Gzip 格式
                .method(originalRequest.method(), gzip(originalRequest.body()))
                .build();

        return chain.proceed(compressedRequest);
    }

    // 使用 Gzip 壓縮 RequestBody
    private RequestBody gzip(final RequestBody body) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public void writeTo(@NonNull BufferedSink sink) throws IOException {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                BufferedSink gzipSink = Okio.buffer(Okio.sink(gzipOutputStream));

                body.writeTo(gzipSink);
                gzipSink.close();
                gzipOutputStream.close();

                sink.write(byteArrayOutputStream.toByteArray());
            }
        };
    }
}
