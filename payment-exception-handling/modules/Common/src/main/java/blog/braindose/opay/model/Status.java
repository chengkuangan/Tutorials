package blog.braindose.opay.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * The status of the payment or transaction processing
 * SUBMITTED - The transaction is newly created or submitted
 * VALIDATED - The transaction is validated for credit check, fraud check and aml check
 * PROCESSING - The transaction is being processed or WIP
 * COMPLETED - The transaction is completed successfully
 * REJECTED - The transaction is rejected
 * APPROVED - The transaction is approved
 * FAILED - The transaction has failed.
 */

 @RegisterForReflection
public enum Status {
    SUBMITTED,
    VALIDATED,
    PROCESSING,
    COMPLETED,
    REJECTED,
    APPROVED,
    FAILED;
}
