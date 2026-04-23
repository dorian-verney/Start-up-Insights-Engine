package com.insight.database.enums;

public enum DltStatus {
    PENDING,     // New DLT
    REPLAYED,    // Replayed done
    DISCARDED    // Lost, stopped replying
}

