package com.insight.service;

import com.insight.database.entity.*;
import com.insight.exceptions.EventProcessingException;
import com.insight.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class Helper {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private MrrService mrrService;

    @Autowired
    private LtvService ltvService;

    @Autowired
    private ChurnService churnService;

    @Autowired
    private SubscriberAddOnService subscriberAddOnService;


    public Subscriber requireSubscriber(Long id) throws EventProcessingException {
        return subscriberRepository.findById(id)
                .orElseThrow(() -> new EventProcessingException(
                        "Subscriber not found: " + id));
    }

    public Company requireCompany(Long id) throws EventProcessingException {
        return companyService.findById(id)
                .orElseThrow(() -> new EventProcessingException(
                        "Company not found: " + id));
    }

    public BigDecimal lastMrrAmount(Company company){
        return mrrService.findLastOne(company)
                .map(MrrSnapshot::getAmount)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal lastLtvAmountReal(Company company){
        return ltvService.findLastOne(company)
                .map(LtvSnapshot::getAmountReal)
                .orElse(BigDecimal.ZERO);
    }

    public float lastChurnRate(Company company) {
        return churnService.findLastOne(company)
                .map(ChurnSnapshot::getRate)
                .orElse(0F);
    }

    public Long lastChurnActives(Company company) {
        return churnService.findLastOne(company)
                .map(ChurnSnapshot::getActiveSubscribers)
                .orElse(0L);
    }

    public Double computeTheoricLtv(Subscriber sub, Company company, double adjustment) {
        BigDecimal addonSum = subscriberAddOnService.sumActiveAddonsBySubscriber(sub);
        double sumAddonPlanSub = (addonSum == null ? 0.0 : addonSum.doubleValue())
                + sub.getPlan().getPrice()
                + adjustment;

        float lastRate = lastChurnRate(company);
        return lastRate == 0F
                ? sumAddonPlanSub
                : sumAddonPlanSub / lastRate;
    }
}
