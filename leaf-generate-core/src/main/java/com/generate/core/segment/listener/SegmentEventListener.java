package com.generate.core.segment.listener;


import com.generate.core.segment.bean.Segment;
import com.generate.core.segment.buffer.SegmentBuffer;
import com.generate.core.segment.database.entity.LeafInfo;
import com.generate.core.segment.event.SegmentEvent;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class SegmentEventListener {

    @Autowired
    private AsyncEventBus eventBus;

    @PostConstruct
    public void init(){
        eventBus.register(this);
    }


    @Subscribe
    public void doFillSegement(SegmentEvent event) {
        log.info("deal fill event = {}", event);
        SegmentBuffer segmentBuffer = event.getSegmentBuffer();
        Throwable ex = null;
        Segment segment = null;
        try {
            LeafInfo leafInfo = event.getLeafService().getLeafInfo(event.getTag());
            log.info(" deal res leaf info = {}", leafInfo);
            segment = leafInfoToSegment(leafInfo);
        } catch (Exception e) {
            ex = e;
            log.error("throw get leaf info exception");
        }
        segmentBuffer.setNextSegment(segment);
        // complete change state
        segmentBuffer.fillComplete(ex);
    }


    private Segment leafInfoToSegment(LeafInfo leafInfo) {
        Segment segment = new Segment();
        segment.setCurId(leafInfo.getCurId());
        segment.setMinId(leafInfo.getCurId());
        segment.setMaxId(leafInfo.getMaxId());
        return segment;
    }
}
