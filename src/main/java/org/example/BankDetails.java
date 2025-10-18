package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BankDetails {
    private static final Logger logger = LogManager.getLogger(BankDetails.class);
    private final String accountId;
    private String kpp;
    private String bik;
    private String bankName;

    public BankDetails(String accountId, String kpp, String bik, String bankName) {
        this.accountId = accountId;
        this.kpp = kpp;
        this.bik = bik;
        this.bankName = bankName;
        logger.info("Реквизиты созданы для счёта: {}", accountId);
    }

    public String getAccountId() {
        return accountId;
    }
    public String getKpp() {
        return kpp;
    }
    public String getBik() {
        return bik;
    }
    public String getBankName() {
        return bankName;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }
    public void setBik(String bik) {
        this.bik = bik;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }


}