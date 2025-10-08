/*
 * Copyright (c) 2016 Garmin International. All Rights Reserved.
 * <p></p>
 * This software is the confidential and proprietary information of
 * Garmin International.
 * You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement
 * you entered into with Garmin International.
 * <p></p>
 * Garmin International MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. Garmin International SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * <p></p>
 *
 * Created by tritsch on 8/11/2016.
 */

package com.dyaco.spirit_commercial.garmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ThreadUtil {
    private static final ListeningExecutorService EXECUTOR = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    private static final ListeningScheduledExecutorService LISTENABLE_SCHEDULED_EXECUTOR = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor());

    private static final ConcurrentHashMap<String, ListeningExecutorService> LOGGING_EXECUTORS = new ConcurrentHashMap<>();

    @NonNull
    public static ListeningExecutorService executor() {
        return EXECUTOR;
    }

    public static ScheduledExecutorService scheduledExecutor() {
        return SCHEDULED_EXECUTOR;
    }

    public static ListeningExecutorService createSingleExecutor() {
        return MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
    }

    public static ListeningScheduledExecutorService listeningScheduledExecutor() {
        return LISTENABLE_SCHEDULED_EXECUTOR;
    }

    @Nullable
    public static <T> T getIgnoreException(ListenableFuture<T> future) {
        try {
            return Futures.getChecked(future, Exception.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static Exception getNullOrException(ListenableFuture<?> future) {
        try {
            Futures.getChecked(future, Exception.class);
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    @Nullable
    public static <T> T getIgnoreException(ListenableFuture<T> future, long timeout, TimeUnit unit) {
        try {
            return Futures.getChecked(future, Exception.class, timeout, unit);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static <T> T getWithGenericException(ListenableFuture<T> future) throws Exception {
        return Futures.getChecked(future, Exception.class);
    }

    public static ListeningExecutorService getCustomLogSingleExecutor(String macAddress) {
        if (!LOGGING_EXECUTORS.containsKey(macAddress)) {
            LOGGING_EXECUTORS.put(macAddress, createSingleExecutor());
        }

        return LOGGING_EXECUTORS.get(macAddress);
    }

    public static <T> ListenableFuture<T> withIgnoreFailure(ListenableFuture<T> withTimeout) {
        SettableFuture<T> ret = SettableFuture.create();

        withTimeout.addListener(() -> ret.set(null), ThreadUtil.executor());

        return ret;
    }

    public static <T> T getOrThrowWrappedException(ListenableFuture<T> future) throws Throwable {
        try {
            return Futures.getChecked(future, Exception.class);
        } catch (Exception e) {
            Throwable root = e.getCause() == null ? e : e.getCause();


            throw root;
        }
    }

    public interface FutureFailureCallback extends FutureCallback<Object> {
        @Override
        default void onSuccess(@Nullable Object result) {
        }
    }

    public interface FutureSuccessCallback<T> extends FutureCallback<T> {
        @Override
        default void onFailure(@NonNull Throwable t) {
        }
    }
}
