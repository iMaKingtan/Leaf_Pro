package com.generate.core.segment.buffer;

import com.generate.common.exception.BizException;
import com.generate.core.segment.bean.IdWrapper;
import com.generate.core.LeafService;
import com.generate.core.segment.bean.Segment;
import com.generate.core.segment.event.SegmentEvent;
import com.generate.core.segment.policy.FetchPolicy;
import com.google.common.eventbus.AsyncEventBus;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@Slf4j
public class SegmentBuffer {

    public static final int NORMAL = 0;
    public static final int FILLING_NEXT_BUFFER = 1;
    public static final int FILLED_NEXT_BUFFER = 2;
    public static final int CHANGE_NEXT_BUFFER = 3;
    // 四个状态,分别从填充下一个状态位、填充完毕下一个状态位和改变缓存
    private volatile int state = NORMAL;
    // 两个缓存,不需要使用数组
    private volatile Segment curSegment;
    private volatile Segment nextSegment;

    private final LeafService leafService;
    private static final long STATE_OFFSET;
    private static final Unsafe UNSAFE;
    private final AsyncEventBus asyncEventBus;
    private final FetchPolicy fetchPolicy;
    // tag和buffer对应
    private final String tag;
    private Throwable ex;
    private static final String THE_SAFE = "theSafe";

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            // 使用Unsafe做CAS替换
            UNSAFE = (Unsafe) field.get(null);
            STATE_OFFSET = UNSAFE.objectFieldOffset(SegmentBuffer.class.getDeclaredField("state"));
        } catch (Exception e) {
            throw new BizException(e.getMessage(), e);
        }
    }

    public SegmentBuffer(LeafService leafService, 
                        AsyncEventBus asyncEventBus,
                        FetchPolicy fetchPolicy, 
                        String tag) {
        this.leafService = leafService;
        this.asyncEventBus = asyncEventBus;
        this.fetchPolicy = fetchPolicy;
        this.tag = tag;
        // init segment
        this.curSegment = new Segment();
    }

    /**
     * 从当前buffer中获取num个key值
     * @param num
     * @return
     */
    public IdWrapper nextId(int num) {
        checkSegment();
        checkException();
        IdWrapper idWrapper = null;
        // 自旋计数
        int roll = 0;
        // 自旋,最多两次
        while(true) {
            // 将num个id放进threadLocal
            idWrapper = curSegment.getNextId(num);
            if (idWrapper == null) {
                checkSegment();
                // 自旋锁,直到备选缓存准备好
                while (true) {
                    checkException();
                    if (this.state == NORMAL) {
                        break;
                    }
                    // 准备好,方便后续线程进行转换,避免了Lock的长时间等待
                    if (state == FILLED_NEXT_BUFFER
                            && nextSegment != null
                            && compareAndSwapState(FILLED_NEXT_BUFFER, CHANGE_NEXT_BUFFER)) {
                        log.info("{} next segment is ok", tag);
                        changeSegment();
                        break;
                    }
                    roll++;

                    if (roll > 1000){
                        synchronized (this) {
                            try {
                                // 扩容的时候其他线程都在这里wait
                                this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            } else {
                break;
            }
        }
        return idWrapper;
    }

    private void checkException() {
        if(ex != null){
            if(ex instanceof BizException){
                throw (BizException)ex;
            }else {
                throw new BizException("check exception tag = " + tag ,ex);
            }
        }

    }

    private void checkSegment() {
        // 使用百分比大于设置的阈值,需要填充?由于多线程环境下填充,所以需要CAS修改状态位
        // TODO: 2023/3/10 动态调整阈值,请求量越高,那么阈值越低,建议和step同时更新
        if (curSegment.getUsedPercent().compareTo(fetchPolicy.nextSegmentFetchPercent(this.tag)) >= 0
                && nextSegment == null
                // 状态从正常变成填充
                && compareAndSwapState(NORMAL, FILLING_NEXT_BUFFER)) {
            log.info("start fill next segment , tag = {}", tag);
            fillNextSegmentEvent();
        }

    }

    private void fillNextSegmentEvent() {
        SegmentEvent event = new SegmentEvent(this,tag,leafService,fetchPolicy.segmentFetchSize(tag));
        // 发布事件,由异步线程池处理
        asyncEventBus.post(event);
    }

    private void changeSegment() {
        curSegment = nextSegment;
        nextSegment = null;
        state = NORMAL;
        synchronized (this) {
            this.notifyAll();
        }

    }

    public void fillComplete(Throwable ex){
        this.ex = ex;
        state = FILLED_NEXT_BUFFER;
        synchronized (this){
            // 这里只需要一个线程去修改状态,然后让这个线程去唤醒其他所有线程
            this.notify();
        }
    }

    public void setNextSegment(Segment segment) {
        this.nextSegment = segment;
    }

    private boolean compareAndSwapState(int curState, int newState) {
        return UNSAFE.compareAndSwapInt(this, STATE_OFFSET, curState, newState);
    }
}
