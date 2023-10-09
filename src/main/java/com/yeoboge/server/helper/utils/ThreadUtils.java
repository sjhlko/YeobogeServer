package com.yeoboge.server.helper.utils;

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
}
