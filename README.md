## 1. Topic - Events

| Topic               | Events dedans                                                 |
|---------------------|---------------------------------------------------------------|
| subscription-events | `SubscriptionStarted`, `PlanChanged`, `SubscriptionCancelled` |
| payment-events      | `PaymentSucceeded`, `PaymentFailed`, `OneTimePayment`         |
| addon-events        | `AddonAdded`, `AddonRemoved`                                  |



## 2. Structure des Events

### 2.1 Règle générale

Tous les events doivent avoir une **enveloppe commune** :

- `event_id` : identifiant unique de l'event
- `event_type` : nom de l'event
- `timestamp` : date/heure de l'événement
- `subscriber_id` : identifiant du client

➡️ Ensuite, chaque event possède ses champs spécifiques.

---

### 2.2 Topic: `subscription-events`

#### `SubscriptionStarted`
- `name` : nom du client
- `plan_id` : plan choisi (starter, pro, enterprise...)
- `mrr_amount` : montant mensuel (€)

#### `PlanChanged`
- `old_plan_id`
- `new_plan_id`
- `old_mrr_amount`
- `new_mrr_amount` : la différence = expansion ou contraction MRR

#### `SubscriptionCancelled`
- `reason` : raison de l’annulation (optionnel mais précieux)

---

### 2.3 Topic: `payment-events`

#### `PaymentSucceeded`
- `amount` : montant encaissé
- `period` : mois concerné (ex: 2024-03)

#### `PaymentFailed`
- `amount`
- `reason` : carte expirée, fonds insuffisants…
- `attempt_number` : numéro de tentative (1, 2, 3...)

#### `OneTimePayment`
- `amount`
- `description` : onboarding, formation…

---

###  2.4 Topic: `addon-events`

#### `AddonAdded`
- `addon_id`
- `addon_name`
- `mrr_amount` : ajout au MRR mensuel

#### `AddonRemoved`
- `addon_id`
- `addon_name`
- `mrr_amount` : retrait du MRR mensuel  


## 3. Metrics

### 3.1 MRR (Monthly Recurring Revenue)

C’est la métrique la plus simple à suivre, car chaque event impactant le MRR contient `mrr_amount`.

#### 📈 Évolution du MRR par event

- `SubscriptionStarted` → MRR **+ mrr_amount**
- `PlanChanged` → MRR **+ (new_mrr_amount - old_mrr_amount)**
- `SubscriptionCancelled` → MRR **- mrr_amount du client**
- `AddonAdded` → MRR **+ mrr_amount**
- `AddonRemoved` → MRR **- mrr_amount**

#### ℹ️ Notes
- `PaymentSucceeded` et `PaymentFailed` **n’impactent pas le MRR**
- Ils servent uniquement à **confirmer ou signaler un risque** sur un MRR déjà comptabilisé

---

###  3.2 Churn Rate

**Formule :** Churn Rate = clients perdus ce mois / clients en début de mois


**Déclencheur :**
- `SubscriptionCancelled` uniquement

**Compteurs :**

- `active_subscribers`
    - incrémenté par `SubscriptionStarted`
    - décrémenté par `SubscriptionCancelled`

- `churned_this_month`
    - remis à 0 chaque mois
    - incrémenté par `SubscriptionCancelled`

---

### 3.3 LTV

Deux valeurs à maintenir par client :

#### LTV théorique

LTV théorique = mrr_amount / churn_rate global


#### LTV réel

LTV réel = somme de tous les PaymentSucceeded du client


**Note :**
- Le LTV réel n'est calculable qu'à la fin de vie du client
- Lors de `SubscriptionCancelled`, on compare les deux

---

### 3.4 Runway

**Formule :**


Runway = cash_in_bank / monthly_burn


**Mise à jour du cash :**

- `PaymentSucceeded` → cash **+ amount**
- `OneTimePayment` → cash **+ amount**

**Paramètre externe :**

- `monthly_burn`
    - donnée externe (salaires, infra, etc.)
    - fournie comme paramètre de configuration  