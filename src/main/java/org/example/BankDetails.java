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

    public String GetAccountId() {
        return accountId;
    }
    public String GetKpp() {
        return kpp;
    }
    public String GetBik() {
        return bik;
    }
    public String GetBankName() {
        return bankName;
    }

    public void SetKpp(String kpp) {
        this.kpp = kpp;
    }
    public void SetBik(String bik) {
        this.bik = bik;
    }
    public void SetBankName(String bankName) {
        this.bankName = bankName;
    }


}