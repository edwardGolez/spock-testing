package com.synacy.lesson04.exercise.domain;

import java.math.BigDecimal;

/**
 * Created by kenichigouang on 5/5/17.
 */
public class InsufficientDepositException extends Exception {

    private BankAccount bankAccount;
    private BigDecimal amountToDeposit;
    private BigDecimal minimumDeposit;

    public InsufficientDepositException(BankAccount bankAccount, BigDecimal amountToDeposit, BigDecimal minimumDeposit) {
        this.bankAccount = bankAccount;
        this.amountToDeposit = amountToDeposit;
        this.minimumDeposit = minimumDeposit;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public BigDecimal getAmountToDeposit() {
        return amountToDeposit;
    }

    public void setAmountToDeposit(BigDecimal amountToDeposit) {
        this.amountToDeposit = amountToDeposit;
    }

    public BigDecimal getMinimumDeposit() {
        return minimumDeposit;
    }

    public void setMinimumDeposit(BigDecimal minimumDeposit) {
        this.minimumDeposit = minimumDeposit;
    }
}
