package com.generate.core.segment.support;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.generate.common.exception.BizException;
import com.generate.common.lifecycle.AbstractGenerateLifeCycle;
import com.generate.core.IdGenerate;
import com.generate.core.LeafService;
import com.generate.core.segment.bean.IdWrapper;
import com.generate.core.segment.buffer.SegmentBuffer;
import com.generate.core.segment.policy.FetchPolicy;
import com.google.common.eventbus.AsyncEventBus;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SegmentIdGenerate extends AbstractGenerateLifeCycle implements IdGenerate {
    // private final EnhanceThreadLocal threadLocal = new EnhanceThreadLocal();
    private final ThreadLocal<Map<String,IdWrapper>> threadLocal = new ThreadLocal<>();
    private final ConcurrentHashMap<String, SegmentBuffer> segmentBufferMap = new ConcurrentHashMap<>(256);
    private final ConcurrentHashSet<Thread> threadHashSet = new ConcurrentHashSet<>(256);
    private final FetchPolicy fetchPolicy;
    private final LeafService leafService;
    private final AsyncEventBus asyncEventBus;

    public SegmentIdGenerate(FetchPolicy fetchPolicy, LeafService leafService, AsyncEventBus asyncEventBus) {
        this.fetchPolicy = fetchPolicy;
        this.leafService = leafService;
        this.asyncEventBus = asyncEventBus;
    }

    @Override
    public String getName() {
        return "segment id generate";
    }

    @Override
    public long getId(String bizTag) {
        return fetchPolicy.threadLocalCacheEnabled() ? getIdForThreadLocal(bizTag) : getIdForSegment(bizTag);
    }

    private long getIdForSegment(String bizTag) {
        try {
            SegmentBuffer segmentBuffer = segmentBufferMap.get(bizTag);
            if (segmentBuffer == null) {
                synchronized (this) {
                    if (segmentBufferMap.get(bizTag) == null) {
                        log.info("SegmentBuffer: {} tag is creating...");
                        segmentBuffer = new SegmentBuffer(leafService, asyncEventBus, fetchPolicy, bizTag);
                        segmentBufferMap.put(bizTag, segmentBuffer);
                    }
                }
            }
            assert segmentBuffer != null;
            IdWrapper idWrapper = segmentBuffer.nextId(1);
            return idWrapper.getCurId();
        } catch (Exception e) {
            e.printStackTrace();
            segmentBufferMap.remove(bizTag);
            log.error("获取segment id 异常 e = {}" , e.getMessage());
            throw e;
        }
    }

    private Map<String,IdWrapper> getWrapperMap(){
        Map<String, IdWrapper> wrapperMap = threadLocal.get();
        if (wrapperMap == null){
            wrapperMap = new HashMap<>(256);
            threadLocal.set(wrapperMap);
        }
        return wrapperMap;
    }

    private long getIdForThreadLocal(String bizTag) {
        Map<String, IdWrapper> wrapperMap = getWrapperMap();
        IdWrapper idWrapper = wrapperMap.get(bizTag);
        // 如果idWrapper不够用了
        // TODO: 2023/2/4 如果需要填充,需要自己去填充
        if(isFilled(idWrapper)){
            log.info("开启threadLocal 无缓存，开始填充...");
            SegmentBuffer segmentBuffer = segmentBufferMap.get(bizTag);
            if(segmentBuffer == null){
                log.error("bizTag:{}未找到",bizTag);
                // TODO: 2023/3/5 重构将提前加入缓存
                segmentBuffer = new SegmentBuffer(leafService, asyncEventBus, fetchPolicy, bizTag);
                segmentBufferMap.put(bizTag, segmentBuffer);
                // throw new BizException(bizTag+"key未找到");
            }
            boolean flag = true;
            try {
                assert segmentBuffer != null;
                idWrapper = segmentBuffer.nextId(fetchPolicy.threadLocalFetchSize(bizTag));
            }catch (Exception e){
                e.printStackTrace();
                segmentBufferMap.remove(bizTag);
                flag = false;
                throw e;
            }finally {
                if(flag){
                    threadHashSet.add(Thread.currentThread());
                    wrapperMap.put(bizTag,idWrapper);
                }
            }
        }
        long curId = idWrapper.getCurId();
        idWrapper.setCurId(curId + 1);
        return curId;
    }

    private boolean isFilled(IdWrapper idWrapper) {
        return idWrapper == null || idWrapper.getCurId() >= idWrapper.getMaxId();
    }

    @Override
    protected void doInit() {
        // 注册监听器

    }

    @Override
    protected void doStart() {

    }

    @Override
    protected void doStop() {

    }
}
