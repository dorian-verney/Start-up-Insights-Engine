package com.start_up_insight_engine.service;

import com.start_up_insight_engine.repository.AddOnRepository;
import com.start_up_insight_engine.repository.LtvSnapshotRepository;
import com.start_up_insight_engine.repository.RunwaySnapshotRepository;
import com.start_up_insight_engine.transport.OneTimePaymentEvent;
import com.start_up_insight_engine.transport.PaymentFailedEvent;
import com.start_up_insight_engine.transport.PaymentSucceededEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private LtvSnapshotRepository ltvSnapshotRepository;

    @Autowired
    private RunwaySnapshotRepository runwaySnapshotRepository;

    public void handlePaymentSucceeded(PaymentSucceededEvent event){

    }

    public void handlePaymentFailed(PaymentFailedEvent event){

    }

    public void handleOneTimePayment(OneTimePaymentEvent event){

    }
}
