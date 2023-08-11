package com.itoah.demo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlidingWindowRateLimiter {
    // 时间窗口大小，单位毫秒
    private long windowSize;
    // 分片窗口数
    private int shardNum;
    // 允许通过请求数
    private int maxRequestCount;
    // 各个窗口内请求计数
    private int[] shardRequestCount;
    // 请求总数
    private int totalCount;
    // 当前窗口下标
    private int shardId;
    // 每个小窗口大小，毫秒
    private long tinyWindowSize;
    // 窗口右边界
    private long windowBorder;

    // 传入总时间窗口大小、分片窗口数量、允许通过请求数
    public SlidingWindowRateLimiter(long windowSize, int shardNum, int maxRequestCount) {
        this.windowSize = windowSize;
        this.shardNum = shardNum;
        this.maxRequestCount = maxRequestCount;
        // 初始化各个分片窗口的请求计数
        shardRequestCount = new int[shardNum];
        // 计算每个小窗口的大小
        tinyWindowSize = windowSize/ shardNum;
        // 初始化窗口右边界为当前时间
        windowBorder=System.currentTimeMillis();
    }

    public synchronized boolean tryAcquire() {
        // 获取当前时间
        long currentTime = System.currentTimeMillis();
        // 如果当前时间已经超过了窗口右边界，则需要移动窗口，重置请求计数
        if (currentTime > windowBorder){
            do {
                // 移动当前窗口到下一个小窗口
                shardId = (++shardId) % shardNum;
                // 减去当前窗口内的请求技术
                totalCount -= shardRequestCount[shardId];
                // 重置当前窗口内的请求技术为0
                shardRequestCount[shardId]=0;
                // 更新窗口右边界到下一个小窗口的位置
                windowBorder += tinyWindowSize;
            }while (windowBorder < currentTime);
        }
        // 如果请求总数小于允许通过的请求数，则通过请求
        if (totalCount < maxRequestCount){
            log.info("tryAcquire success,{}",shardId);
            // 当前小窗口的请求计数加1
            shardRequestCount[shardId]++;
            // 请求总数加1
            totalCount++;
            return true;
        }else{
            // 如果请求总数大于等于允许通过的请求数，则限流
            log.info("tryAcquire fail,{}",shardId);
            return false;
        }
    }

}