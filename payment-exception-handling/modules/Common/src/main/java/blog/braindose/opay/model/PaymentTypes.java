package blog.braindose.opay.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * To indicate the type of payment for the origination.
 * CASA - Payment using CASA accounts
 * CREDIT_CARD - Payment using Credit card
 * ATM - Payment using ATM
 */

@RegisterForReflection
public enum PaymentTypes {
    CASA,
    CREDIT_CARD,
    ATM
}
