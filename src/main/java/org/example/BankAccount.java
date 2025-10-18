package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BankAccount {
    private static final Logger logger = LogManager.getLogger(BankAccount.class);
    private double balance;
    private final List<Transaction> transactions;
    private final BankDetails details;
    private static final String CSV_FILE = "transactions.csv";

    public BankAccount(BankDetails details) {
        this.balance = 0.00;
        this.transactions = new ArrayList<>();
        this.details = details;
        loadTransactionsFromFile();
        logger.info("Банковский счёт инициализирован для {}", details.getAccountId());
    }

    public void deposit(double amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной");
        }
        balance += amount;
        transactions.add(new Transaction("DEPOSIT", amount, LocalDateTime.now()));
        saveTransactionToFile();
        logger.info("Пополнение {} на счёт {}", amount, details.getAccountId());
        new AccountRegistry().updateAccountBalance(this);
    }

    public void withdraw(double amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть положительной");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Недостаточно средств на счёте");
        }
        balance -= amount;
        transactions.add(new Transaction("WITHDRAW", amount, LocalDateTime.now()));
        saveTransactionToFile();
        logger.info("Снято {} со счёта {}", amount, details.getAccountId());
        new AccountRegistry().updateAccountBalance(this);
    }


    public void showTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("Транзакции отсутствуют");
            logger.warn("Транзакции по счёту {} не найдены", details.getAccountId());
            return;
        }
        System.out.println("Список транзакций для счёта " + details.getAccountId());
        for (Transaction t : transactions) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.printf("ID: %s, Тип: %s, Сумма: %.2f, Дата: %s", t.getId(), t.getType(), t.getAmount(), t.getDate().format(formatter));
        }
        logger.info("Отображены транзакции по счёту {}", details.getAccountId());
    }

    public void searchTransactions(String type, Double minAmount, Double maxAmount) {
        boolean found = false;
        System.out.println("Результаты поиска транзакций для счёта " + details.getAccountId() + ":");
        for (Transaction t : transactions) {
            boolean sim = true;
            if (t.getAmount() < minAmount) {
                sim = false;
            }
            if (t.getAmount() > maxAmount) {
                sim = false;
            }
            if (sim) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                System.out.printf("ID: %s, Тип: %s, Сумма: %.2f, Дата: %s", t.getId(), t.getType(), t.getAmount(), t.getDate().format(formatter));
                found = true;
            }
        }
        if (!found) {
            System.out.println("Транзакции не найдены.");
            logger.warn("Транзакции по счёту {} не найдены по указанным критериям", details.getAccountId());
        } else {
            logger.info("Поиск по счёту  завершён");
        }
    }

    private void loadTransactionsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].startsWith(details.getAccountId())) {
                    String type = parts[1];
                    double amount = Double.parseDouble(parts[2]);
                    LocalDateTime date = LocalDateTime.parse(parts[3], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    transactions.add(new Transaction(type, amount, date));
                    if (type.equals("DEPOSIT")) {
                        balance += amount;
                    } else {
                        balance -= amount;
                    }
                }
            }
            logger.info("Транзакции загружены для счёта {}", details.getAccountId());
        } catch (FileNotFoundException e) {
            logger.info("Файл транзакций для счёта {} не найден", details.getAccountId());
        } catch (IOException | IllegalArgumentException e) {
            logger.error("Ошибка при загрузке транзакций для счёта {}: {}", details.getAccountId(), e.getMessage());
        }
    }

    private void saveTransactionToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE, true))) {
            Transaction last = transactions.getLast();
            writer.write(details.getAccountId() + "," + last.toString());
            writer.newLine();
            logger.info("Транзакция сохранена для счёта {}", details.getAccountId());
        } catch (IOException e) {
            logger.error("Ошибка при сохранении транзакции для счёта {}: {}", details.getAccountId(), e.getMessage());
        }
    }

    public double getBalance() {
        return balance;
    }
    public BankDetails getDetails() {
        return details;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

}
