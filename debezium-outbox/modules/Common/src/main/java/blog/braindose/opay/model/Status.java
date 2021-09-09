package blog.braindose.opay.model;

/**
 * The status of the payment or transaction processing
 * SUBMITTED - The transaction is newly created or submitted
 * PROCESSING - The transaction is being processed or WIP
 * COMPLETED - The transaction is completed successfully
 * REJECTED - The transaction is rejected
 * APPROVED - The transaction is approved
 * FAILED - The transaction has failed.
 */
public enum Status {
    SUBMITTED,
    PROCESSING,
    COMPLETED,
    REJECTED,
    APPROVED,
    FAILED;
}
