package com.conquestrefabricated.content.spoilage;

/** Implemented by a container that keeps some of its slots from spoiling - salt at work on meat. */
public interface SpoilageHold {

    /** Whether what is in this slot is, for now, kept from going off. */
    boolean holds(int slot);
}
