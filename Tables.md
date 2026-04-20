## Schéma 1 — Cœur métier

> Relations entre les abonnés, leurs plans et leurs add-ons.

```mermaid
erDiagram
    SUBSCRIBER {
        Long        id              
        String      name
        String      email
        Plan        plan
        LocalDateTime subscribedAt
        LocalDateTime cancelledAt  "null si actif"
        Company       company
    }

    PAYMENT_RECORD {
        Long          id
        Subscriber    subscriber
        LocalDateTime timestamp
        Integer       price
        PaymentType   paymentType   "SUBSCRIPTION | ADDON"
        AddOnType     addOnType     "nullable — si ADDON payment"
        Company       company
    }
 
    PLAN {
        Long    id         
        Integer price
        PlanType planType   "FREE | PRO | ULTIMATE"
    }
 
    ADD_ON {
        Long             id               
        AddOnType        addOnType        "LEARNING | ONBOARDING_FEES | MORE_STORAGE"
        SubscriptionType subscriptionType "ONE_TIME | RECURRING"
        Integer          price
    }
 
    SUBSCRIBER_ADD_ON {
        Long          id
        Subscriber    subscriber  
        AddOn         addOn      
        LocalDateTime startedAt
        LocalDateTime endedAt        "null si actif"
        Company       company
    }
 
    SUBSCRIBER }o--|| PLAN             : "souscrit à (ManyToOne)"
    SUBSCRIBER ||--o{ PAYMENT_RECORD : "a effectué (OneToMany)"
    SUBSCRIBER ||--o{ SUBSCRIBER_ADD_ON : "a souscrit"
    ADD_ON     ||--o{ SUBSCRIBER_ADD_ON : "est souscrit via"
    
```

## Schéma 3 — Snapshots analytiques

> Tables autonomes de métriques, sans FK vers les entités métier.  
> Chaque snapshot est déclenché par un `Trigger` (événement système).

```mermaid
erDiagram
    CHURN_SNAPSHOT {
        Long          id               
        LocalDateTime timestamp
        float         rate              "taux de churn au moment T"
        Long          activeSubscribers
        Trigger       reason            "événement déclencheur"
        Company       company
    }
 
    MRR_SNAPSHOT {
        Long          id         
        LocalDateTime timestamp
        BigDecimal    amount     "MRR total cumulé"
        BigDecimal    delta      "variation due à l'action"
        Trigger       reason
        Company       company
    }
 
    LTV_SNAPSHOT {
        Long          id              
        LocalDateTime timestamp
        Double        amountTheoric   "LTV théorique calculée"
        BigDecimal    amountReal      "LTV réelle observée"
        Trigger       reason
        Company       company
    }
 
    RUNWAY_SNAPSHOT {
        Long          id         
        LocalDateTime timestamp
        BigDecimal    liquidity  "trésorerie disponible"
        BigDecimal    totalCost  "coûts mensuels totaux"
        Double        runway     "mois de runway restants"
        Trigger       reason
        Company       company
    }
```

## Énumérations

### `PlanType`
```
FREE | PRO | ULTIMATE
```

### `AddOnType`
```
LEARNING | ONBOARDING_FEES | MORE_STORAGE
```

### `PaymentType`
```
SUBSCRIPTION | ADDON
```

### `SubscriptionType`
```
ONE_TIME | RECURRING
```