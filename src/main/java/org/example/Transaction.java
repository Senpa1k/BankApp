package org.example;

import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Transaction {
    private static final Logger logger = LogManager.getLogger(Transaction.class);
    private String id;
    private String type;
    private double amount;
    private LocalDateTime date;

    public Transaction(String type, double amount, LocalDateTime date) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.date = date;
        logger.info("Создана транзакция: {} {}", type, amount);
    }

    public String getId() {
        return id;
    }
    public String getType() {
        return type;
    }
    public double getAmount() {
        return amount;
    }
    public LocalDateTime getDate() {
        return date;
    }
    @Override
    public String toString() {
        return String.join(",", id, type, String.valueOf(amount), date.toString());
    }

}