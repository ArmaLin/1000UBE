package com.dyaco.spirit_commercial.model.webapi;

import androidx.annotation.NonNull;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import okhttp3.Dns;
public class CustomDns implements Dns {
    private final long timeout;
    public CustomDns(long timeout) {
        this.timeout = timeout;
    }
    @NonNull
    @Override
    public List<InetAddress> lookup(@NonNull final String hostname) throws UnknownHostException {
        try {
            FutureTask<List<InetAddress>> task = new FutureTask<>(
                    () -> Arrays.asList(InetAddress.getAllByName(hostname)));
            new Thread(task).start();
            return task.get(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            UnknownHostException unknownHostException =
                    new UnknownHostException("Broken system behaviour for dns lookup of " + hostname);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
}