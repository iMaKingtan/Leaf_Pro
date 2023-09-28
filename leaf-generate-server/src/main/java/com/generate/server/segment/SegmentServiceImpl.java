package com.generate.server.segment;

import com.generate.core.LeafService;
import com.generate.core.segment.policy.FetchPolicy;
import com.generate.core.segment.service.IdGenerateService;
import com.generate.core.segment.support.SegmentIdGenerate;
import com.google.common.eventbus.AsyncEventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service("SegmentService")
@ConditionalOnProperty(prefix = "id.generate",name = "segment",havingValue = "true")
public class SegmentServiceImpl implements IdGenerateService {

    @Autowired
    FetchPolicy fetchPolicy;
    @Autowired
    LeafService leafService;
    @Autowired
    AsyncEventBus asyncEventBus;

    private SegmentIdGenerate segmentIdGenerate;

    @PostConstruct
    public void init(){
        log.info("init segmentServiceImpl ... ");
        segmentIdGenerate = new SegmentIdGenerate(fetchPolicy,leafService,asyncEventBus);
        log.info("init segmentServiceImpl down");
    }
    @Override
    public long getId(String key) {
        return segmentIdGenerate.getId(key);
    }
}
