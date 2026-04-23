package com.insight.init;

import com.insight.database.entity.*;
import com.insight.database.enums.*;
import com.insight.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final CompanyRepository companyRepository;
    private final SubscriberRepository subscriberRepository;
    private final PlanRepository planRepository;
    private final AddOnRepository addOnRepository;
    private final SubscriberAddOnRepository subscriberAddOnRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final MrrSnapshotRepository mrrSnapshotRepository;
    private final ChurnSnapshotRepository churnSnapshotRepository;
    private final LtvSnapshotRepository ltvSnapshotRepository;
    private final RunwaySnapshotRepository runwaySnapshotRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (subscriberRepository.findTopByOrderByIdDesc().isPresent()) {
            log.info("[DataSeeder] Base déjà initialisée, on passe.");
            return;
        }

        log.info("[DataSeeder] Initialisation des données de démo...");

        Company acme    = companyRepository.save(Company.builder().name("Acme Corp").build());
        Company bright  = companyRepository.save(Company.builder().name("Bright Labs").build());
        Company cloud   = companyRepository.save(Company.builder().name("Cloud Nine").build());
        Company delta   = companyRepository.save(Company.builder().name("Delta Tech").build());

        seedAddOns();
        seedSubscribersAndRelatedData(acme, bright, cloud, delta);
        seedMetricSnapshots(acme, bright, cloud, delta);

        log.info("[DataSeeder] Initialisation terminée.");
    }

    private void seedAddOns() {
        addOnRepository.save(new AddOn(null, AddOnType.LEARNING,        AddOnBillingType.RECURRING, 29));
        addOnRepository.save(new AddOn(null, AddOnType.ONBOARDING_FEES, AddOnBillingType.ONE_TIME,  199));
        addOnRepository.save(new AddOn(null, AddOnType.MORE_STORAGE,    AddOnBillingType.RECURRING, 9));
    }

    // ── Répartition ──────────────────────────────────────────────────────────
    // Acme Corp   → Alice (PRO),      Bob (ULTIMATE)
    // Bright Labs → Charlie (FREE),   Diana (PRO, cancelled)
    // Cloud Nine  → Eve (ULTIMATE)
    // Delta Tech  → Frank (FREE, cancelled)
    private void seedSubscribersAndRelatedData(Company acme, Company bright, Company cloud, Company delta) {
        Plan freePlan     = planRepository.findByPlanType(PlanType.FREE).orElseThrow();
        Plan proPlan      = planRepository.findByPlanType(PlanType.PRO).orElseThrow();
        Plan ultimatePlan = planRepository.findByPlanType(PlanType.ULTIMATE).orElseThrow();

        AddOn learningAddOn   = addOnRepository.findByAddOnType(AddOnType.LEARNING).orElseThrow();
        AddOn onboardingAddOn = addOnRepository.findByAddOnType(AddOnType.ONBOARDING_FEES).orElseThrow();
        AddOn storageAddOn    = addOnRepository.findByAddOnType(AddOnType.MORE_STORAGE).orElseThrow();

        LocalDateTime now = LocalDateTime.now();

        // ── Acme Corp ─────────────────────────────────────────────────────────
        Subscriber alice = subscriberRepository.save(Subscriber.builder()
                .name("Alice Martin").email("alice.martin@example.com")
                .plan(proPlan).subscribedAt(now.minusMonths(6))
                .company(acme).build());

        Subscriber bob = subscriberRepository.save(Subscriber.builder()
                .name("Bob Dupont").email("bob.dupont@example.com")
                .plan(ultimatePlan).subscribedAt(now.minusMonths(5))
                .company(acme).build());

        // ── Bright Labs ───────────────────────────────────────────────────────
        subscriberRepository.save(Subscriber.builder()
                .name("Charlie Leroy").email("charlie.leroy@example.com")
                .plan(freePlan).subscribedAt(now.minusMonths(4))
                .company(bright).build());

        Subscriber diana = subscriberRepository.save(Subscriber.builder()
                .name("Diana Bernard").email("diana.bernard@example.com")
                .plan(proPlan).subscribedAt(now.minusMonths(3)).cancelledAt(now.minusMonths(1))
                .company(bright).build());

        // ── Cloud Nine ────────────────────────────────────────────────────────
        Subscriber eve = subscriberRepository.save(Subscriber.builder()
                .name("Eve Moreau").email("eve.moreau@example.com")
                .plan(ultimatePlan).subscribedAt(now.minusMonths(3))
                .company(cloud).build());

        // ── Delta Tech ────────────────────────────────────────────────────────
        subscriberRepository.save(Subscriber.builder()
                .name("Frank Petit").email("frank.petit@example.com")
                .plan(freePlan).subscribedAt(now.minusMonths(2)).cancelledAt(now.minusWeeks(2))
                .company(delta).build());

        // ── SubscriberAddOns ──────────────────────────────────────────────────
        subscriberAddOnRepository.save(SubscriberAddOn.builder()
                .subscriber(alice).addOn(learningAddOn).startedAt(now.minusMonths(5)).company(acme).build());

        subscriberAddOnRepository.save(SubscriberAddOn.builder()
                .subscriber(bob).addOn(onboardingAddOn)
                .startedAt(now.minusMonths(5)).endedAt(now.minusMonths(4)).company(acme).build());
        subscriberAddOnRepository.save(SubscriberAddOn.builder()
                .subscriber(bob).addOn(storageAddOn).startedAt(now.minusMonths(4)).company(acme).build());

        subscriberAddOnRepository.save(SubscriberAddOn.builder()
                .subscriber(eve).addOn(learningAddOn).startedAt(now.minusMonths(2)).company(cloud).build());
        subscriberAddOnRepository.save(SubscriberAddOn.builder()
                .subscriber(eve).addOn(storageAddOn).startedAt(now.minusMonths(2)).company(cloud).build());

        // ── PaymentRecords ────────────────────────────────────────────────────
        // Alice : 6x PRO (99) + 5x LEARNING (29)  → acme
        for (int i = 6; i >= 1; i--) {
            paymentRecordRepository.save(PaymentRecord.builder()
                    .subscriber(alice).timestamp(now.minusMonths(i))
                    .price(99).paymentType(PaymentType.SUBSCRIPTION).company(acme).build());
        }
        for (int i = 5; i >= 1; i--) {
            paymentRecordRepository.save(PaymentRecord.builder()
                    .subscriber(alice).timestamp(now.minusMonths(i))
                    .price(29).paymentType(PaymentType.ADDON).addOnType(AddOnType.LEARNING).company(acme).build());
        }

        // Bob : 5x ULTIMATE (499) + 1x ONBOARDING_FEES (199) + 4x MORE_STORAGE (9)  → acme
        for (int i = 5; i >= 1; i--) {
            paymentRecordRepository.save(PaymentRecord.builder()
                    .subscriber(bob).timestamp(now.minusMonths(i))
                    .price(499).paymentType(PaymentType.SUBSCRIPTION).company(acme).build());
        }
        paymentRecordRepository.save(PaymentRecord.builder()
                .subscriber(bob).timestamp(now.minusMonths(5))
                .price(199).paymentType(PaymentType.ADDON).addOnType(AddOnType.ONBOARDING_FEES).company(acme).build());
        for (int i = 4; i >= 1; i--) {
            paymentRecordRepository.save(PaymentRecord.builder()
                    .subscriber(bob).timestamp(now.minusMonths(i))
                    .price(9).paymentType(PaymentType.ADDON).addOnType(AddOnType.MORE_STORAGE).company(acme).build());
        }

        // Diana : 2x PRO (99)  → bright
        paymentRecordRepository.save(PaymentRecord.builder()
                .subscriber(diana).timestamp(now.minusMonths(3))
                .price(99).paymentType(PaymentType.SUBSCRIPTION).company(bright).build());
        paymentRecordRepository.save(PaymentRecord.builder()
                .subscriber(diana).timestamp(now.minusMonths(2))
                .price(99).paymentType(PaymentType.SUBSCRIPTION).company(bright).build());

        // Eve : 3x ULTIMATE (499) + 2x LEARNING (29) + 2x MORE_STORAGE (9)  → cloud
        for (int i = 3; i >= 1; i--) {
            paymentRecordRepository.save(PaymentRecord.builder()
                    .subscriber(eve).timestamp(now.minusMonths(i))
                    .price(499).paymentType(PaymentType.SUBSCRIPTION).company(cloud).build());
        }
        for (int i = 2; i >= 1; i--) {
            paymentRecordRepository.save(PaymentRecord.builder()
                    .subscriber(eve).timestamp(now.minusMonths(i))
                    .price(29).paymentType(PaymentType.ADDON).addOnType(AddOnType.LEARNING).company(cloud).build());
            paymentRecordRepository.save(PaymentRecord.builder()
                    .subscriber(eve).timestamp(now.minusMonths(i))
                    .price(9).paymentType(PaymentType.ADDON).addOnType(AddOnType.MORE_STORAGE).company(cloud).build());
        }

        // Charlie et Frank : FREE → aucun paiement
    }

    private void seedMetricSnapshots(Company acme, Company bright, Company cloud, Company delta) {
        LocalDateTime now = LocalDateTime.now();

        seedAcmeSnapshots(acme, now);
        seedBrightSnapshots(bright, now);
        seedCloudSnapshots(cloud, now);
        seedDeltaSnapshots(delta, now);
    }

    // ── Acme Corp : Alice (PRO=99) + Bob (ULTIMATE=499) ──────────────────────
    // MRR : 99 → 598 → 627 → 636 (stable, 0 churn)
    private void seedAcmeSnapshots(Company c, LocalDateTime now) {
        mrrSnapshotRepository.save(MrrSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(6)).amount(new BigDecimal("99")).delta(new BigDecimal("99")).build());
        mrrSnapshotRepository.save(MrrSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(5)).amount(new BigDecimal("598")).delta(new BigDecimal("499")).build());
        mrrSnapshotRepository.save(MrrSnapshot.builder().company(c).reason(Trigger.ADDON_ADDED)
                .timestamp(now.minusMonths(5).plusHours(1)).amount(new BigDecimal("627")).delta(new BigDecimal("29")).build());
        mrrSnapshotRepository.save(MrrSnapshot.builder().company(c).reason(Trigger.ADDON_ADDED)
                .timestamp(now.minusMonths(4).plusHours(1)).amount(new BigDecimal("636")).delta(new BigDecimal("9")).build());

        churnSnapshotRepository.save(ChurnSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(6)).rate(0f).activeSubscribers(1L).build());
        churnSnapshotRepository.save(ChurnSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(5)).rate(0f).activeSubscribers(2L).build());

        ltvSnapshotRepository.save(LtvSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(6)).amountTheoric(0.0).amountReal(new BigDecimal("99.00")).build());
        ltvSnapshotRepository.save(LtvSnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(5)).amountTheoric(0.0).amountReal(new BigDecimal("462.50")).build());
        ltvSnapshotRepository.save(LtvSnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(4)).amountTheoric(0.0).amountReal(new BigDecimal("520.33")).build());
        ltvSnapshotRepository.save(LtvSnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(1)).amountTheoric(0.0).amountReal(new BigDecimal("677.83")).build());

        runwaySnapshotRepository.save(RunwaySnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(6)).liquidity(new BigDecimal("99")).totalCost(new BigDecimal("800")).runway(0.12).build());
        runwaySnapshotRepository.save(RunwaySnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(5)).liquidity(new BigDecimal("925")).totalCost(new BigDecimal("800")).runway(1.16).build());
        runwaySnapshotRepository.save(RunwaySnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(4)).liquidity(new BigDecimal("1561")).totalCost(new BigDecimal("800")).runway(1.95).build());
        runwaySnapshotRepository.save(RunwaySnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(1)).liquidity(new BigDecimal("3086")).totalCost(new BigDecimal("800")).runway(3.86).build());
    }

    // ── Bright Labs : Charlie (FREE) + Diana (PRO=99, cancelled -1m) ──────────
    // MRR : 0 → 99 → 0 (Diana cancelled) | Churn : 50%
    private void seedBrightSnapshots(Company c, LocalDateTime now) {
        mrrSnapshotRepository.save(MrrSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(4)).amount(new BigDecimal("0")).delta(new BigDecimal("0")).build());
        mrrSnapshotRepository.save(MrrSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(3)).amount(new BigDecimal("99")).delta(new BigDecimal("99")).build());
        mrrSnapshotRepository.save(MrrSnapshot.builder().company(c).reason(Trigger.SUB_CANCELLED)
                .timestamp(now.minusMonths(1)).amount(new BigDecimal("0")).delta(new BigDecimal("-99")).build());

        churnSnapshotRepository.save(ChurnSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(4)).rate(0f).activeSubscribers(1L).build());
        churnSnapshotRepository.save(ChurnSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(3)).rate(0f).activeSubscribers(2L).build());
        // Diana cancelled : 1/2 = 50%
        churnSnapshotRepository.save(ChurnSnapshot.builder().company(c).reason(Trigger.SUB_CANCELLED)
                .timestamp(now.minusMonths(1)).rate(0.5f).activeSubscribers(1L).build());

        ltvSnapshotRepository.save(LtvSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(3)).amountTheoric(0.0).amountReal(new BigDecimal("99.00")).build());
        ltvSnapshotRepository.save(LtvSnapshot.builder().company(c).reason(Trigger.SUB_CANCELLED)
                .timestamp(now.minusMonths(1)).amountTheoric(198.0).amountReal(new BigDecimal("198.00")).build());

        runwaySnapshotRepository.save(RunwaySnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(3)).liquidity(new BigDecimal("99")).totalCost(new BigDecimal("500")).runway(0.20).build());
        runwaySnapshotRepository.save(RunwaySnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(2)).liquidity(new BigDecimal("198")).totalCost(new BigDecimal("500")).runway(0.40).build());
    }

    // ── Cloud Nine : Eve (ULTIMATE=499) + addons ──────────────────────────────
    // MRR : 499 → 537 (LEARNING + MORE_STORAGE) | Churn : 0%
    private void seedCloudSnapshots(Company c, LocalDateTime now) {
        mrrSnapshotRepository.save(MrrSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(3)).amount(new BigDecimal("499")).delta(new BigDecimal("499")).build());
        mrrSnapshotRepository.save(MrrSnapshot.builder().company(c).reason(Trigger.ADDON_ADDED)
                .timestamp(now.minusMonths(2).plusHours(1)).amount(new BigDecimal("528")).delta(new BigDecimal("29")).build());
        mrrSnapshotRepository.save(MrrSnapshot.builder().company(c).reason(Trigger.ADDON_ADDED)
                .timestamp(now.minusMonths(2).plusHours(2)).amount(new BigDecimal("537")).delta(new BigDecimal("9")).build());

        churnSnapshotRepository.save(ChurnSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(3)).rate(0f).activeSubscribers(1L).build());

        ltvSnapshotRepository.save(LtvSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(3)).amountTheoric(0.0).amountReal(new BigDecimal("499.00")).build());
        ltvSnapshotRepository.save(LtvSnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(2)).amountTheoric(0.0).amountReal(new BigDecimal("756.00")).build());
        ltvSnapshotRepository.save(LtvSnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(1)).amountTheoric(0.0).amountReal(new BigDecimal("1293.00")).build());

        runwaySnapshotRepository.save(RunwaySnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(3)).liquidity(new BigDecimal("499")).totalCost(new BigDecimal("600")).runway(0.83).build());
        runwaySnapshotRepository.save(RunwaySnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(2)).liquidity(new BigDecimal("1294")).totalCost(new BigDecimal("600")).runway(2.16).build());
        runwaySnapshotRepository.save(RunwaySnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(1)).liquidity(new BigDecimal("1831")).totalCost(new BigDecimal("600")).runway(3.05).build());
    }

    // ── Delta Tech : Frank (FREE, cancelled -2w) ──────────────────────────────
    // MRR : 0 | Churn : 100%
    private void seedDeltaSnapshots(Company c, LocalDateTime now) {
        mrrSnapshotRepository.save(MrrSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(2)).amount(new BigDecimal("0")).delta(new BigDecimal("0")).build());
        mrrSnapshotRepository.save(MrrSnapshot.builder().company(c).reason(Trigger.SUB_CANCELLED)
                .timestamp(now.minusWeeks(2)).amount(new BigDecimal("0")).delta(new BigDecimal("0")).build());

        churnSnapshotRepository.save(ChurnSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(2)).rate(0f).activeSubscribers(1L).build());
        // Frank cancelled : 1/1 = 100%
        churnSnapshotRepository.save(ChurnSnapshot.builder().company(c).reason(Trigger.SUB_CANCELLED)
                .timestamp(now.minusWeeks(2)).rate(1.0f).activeSubscribers(0L).build());

        ltvSnapshotRepository.save(LtvSnapshot.builder().company(c).reason(Trigger.SUB_STARTED)
                .timestamp(now.minusMonths(2)).amountTheoric(0.0).amountReal(new BigDecimal("0.00")).build());
        ltvSnapshotRepository.save(LtvSnapshot.builder().company(c).reason(Trigger.SUB_CANCELLED)
                .timestamp(now.minusWeeks(2)).amountTheoric(0.0).amountReal(new BigDecimal("0.00")).build());

        runwaySnapshotRepository.save(RunwaySnapshot.builder().company(c).reason(Trigger.PAYMENT_SUCCEEDED)
                .timestamp(now.minusMonths(2)).liquidity(new BigDecimal("0")).totalCost(new BigDecimal("300")).runway(0.0).build());
    }
}
