package com.yeoboge.server.helper.utils;

import com.yeoboge.server.enums.error.CommonErrorCode;
import com.yeoboge.server.handler.AppException;

import java.util.concurrent.CountDownLatch;

/**
 * 스레드 관련 유틸 클래스
 */
public class ThreadUtils {
    /**
     * 특정 시간 동안 스레드의 동작을 대기시킴.
     *
     * @param milliSeconds 스레드가 대기할 시간
     */
    public static void waitForSeconds(long milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 비동기 작업을 수행해야 하는 스레드가 해당 작업들이 완료될 때까지 대기할 때,
     * 몇 개의 작업이 완료 되었는지 확인 할 {@link CountDownLatch}를 반환함.
     * @param numJobForAwait 스레드가 작업 완료를 기다려야 하는 작업의 수
     * @return 해당 작업 수 만큼 대기할 {@link CountDownLatch}
     */
    public static CountDownLatch getThreadAwaiter(int numJobForAwait) {
        return new CountDownLatch(numJobForAwait);
    }

    /**
     * 비동기 작업을 수행할 때 해당 스레드가 모든 작업이 완료될 때까지 대기하도록 강제함.
     * @param latch 몇 개의 비동기 작업이 완료 되었는 지 확인 할 {@link CountDownLatch}
     */
    public static void awaitUntilJobsDone(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
