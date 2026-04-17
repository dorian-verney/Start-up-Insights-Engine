INSERT INTO plan (price, plan_type) VALUES (0, 'FREE') ON CONFLICT (plan_type) DO NOTHING;
INSERT INTO plan (price, plan_type) VALUES (99, 'PRO') ON CONFLICT (plan_type) DO NOTHING;
INSERT INTO plan (price, plan_type) VALUES (499, 'ULTIMATE') ON CONFLICT (plan_type) DO NOTHING;