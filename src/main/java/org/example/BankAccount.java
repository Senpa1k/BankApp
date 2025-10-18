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
        LoadTransactionsFromFile();
        logger.info("Банковский счёт инициализирован для {}", details.GetAccountId());
    }

    public void Deposit(double amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной");
        }
        balance += amount;
        transactions.add(new Transaction("DEPOSIT", amount, LocalDateTime.now()));
        SaveTransactionToFile();
        logger.info("Пополнение {} на счёт {}", amount, details.GetAccountId());
        new AccountRegistry().UpdateAccountBalance(this);
    }

    public void Withdraw(double amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть положительной");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Недостаточно средств на счёте");
        }
        balance -= amount;
        transactions.add(new Transaction("WITHDRAW", amount, LocalDateTime.now()));
        SaveTransactionToFile();
        logger.info("Снято {} со счёта {}", amount, details.GetAccountId());
        new AccountRegistry().UpdateAccountBalance(this);
    }


    public void ShowTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("Транзакции отсутствуют");
            logger.warn("Транзакции по счёту {} не найдены", details.GetAccountId());
            return;
        }
        System.out.println("Список транзакций для счёта " + details.GetAccountId());
        for (Transaction t : transactions) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.printf("ID: %s, Тип: %s, Сумма: %.2f, Дата: %s%n", t.GetId(), t.GetType(), t.GetAmount(), t.GetDate().format(formatter));
        }
        logger.info("Отображены транзакции по счёту {}", details.GetAccountId());
    }

    public void SearchTransactions(String type, Double minAmount, Double maxAmount) {
        boolean found = false;
        System.out.println("Результаты поиска транзакций для счёта " + details.GetAccountId() + ":");
        for (Transaction t : transactions) {
            boolean sim = true;
            if (t.GetAmount() < minAmount) {
                sim = false;
            }
            if (t.GetAmount() > maxAmount) {
                sim = false;
            }
            if (sim) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                System.out.printf("ID: %s, Тип: %s, Сумма: %.2f, Дата: %s%n", t.GetId(), t.GetType(), t.GetAmount(), t.GetDate().format(formatter));
                found = true;
            }
        }
        if (!found) {
            System.out.println("Транзакции не найдены.");
            logger.warn("Транзакции по счёту {} не найдены по указанным критериям", details.GetAccountId());
        } else {
            logger.info("Поиск по счёту  завершён");
        }
    }

    private void LoadTransactionsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].startsWith(details.GetAccountId())) {
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
            logger.info("Транзакции загружены для счёта {}", details.GetAccountId());
        } catch (FileNotFoundException e) {
            logger.info("Файл транзакций для счёта {} не найден", details.GetAccountId());
        } catch (IOException | IllegalArgumentException e) {
            logger.error("Ошибка при загрузке транзакций для счёта {}: {}", details.GetAccountId(), e.getMessage());
        }
    }

    private void SaveTransactionToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE, true))) {
            Transaction last = transactions.getLast();
            writer.write(details.GetAccountId() + "," + last.toString());
            writer.newLine();
            logger.info("Транзакция сохранена для счёта {}", details.GetAccountId());
        } catch (IOException e) {
            logger.error("Ошибка при сохранении транзакции для счёта {}: {}", details.GetAccountId(), e.getMessage());
        }
    }

    public double GetBalance() {
        return balance;
    }
    public BankDetails GetDetails() {
        return details;
    }
    public void SetBalance(double balance) {
        this.balance = balance;
    }

}