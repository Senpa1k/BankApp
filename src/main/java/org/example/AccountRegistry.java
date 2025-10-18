package org.example;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AccountRegistry {
    private static final Logger logger = LogManager.getLogger(AccountRegistry.class);
    private Map<String, BankAccount> accounts;
    private static final String CSV_FILE = "accounts.csv";

    public AccountRegistry() {
        this.accounts = new HashMap<>();
        loadAccountsFromFile();
        logger.info("Список счетов инициализирован");
    }

    public void addAccount(BankAccount account) {
        accounts.put(account.getDetails().getAccountId(), account);
        saveAccountToFile(account);
        logger.info("Добавлен счёт: {}", account.getDetails().getAccountId());
    }

    public BankAccount findByAccountNumber(String accountNumber) {
        BankAccount account = accounts.get(accountNumber);
        if (account != null) {
            logger.info("Найден счёт по номеру: {}", accountNumber);
            System.out.println("По accountNumber " + accountNumber + " найден счёт: " + account.getDetails().getBankName() + ", КПП: " + account.getDetails().getKpp() + ", БИК: " + account.getDetails().getBik());
        } else {
            logger.warn("Счёт с номером {} не найден", accountNumber);
            System.out.println("Счёт с номером " + accountNumber + " не найден.");
        }
        return account;
    }

    public void searchByDetails(String kpp, String bik) {
        boolean found = false;
        System.out.println("Результаты поиска по реквизитам:");
        for (BankAccount account : accounts.values()) {
            boolean matches = true;
            if (kpp != null && !account.getDetails().getKpp().equalsIgnoreCase(kpp)) {
                matches = false;
            }
            if (bik != null && !account.getDetails().getBik().equalsIgnoreCase(bik)) {
                matches = false;
            }
            if (matches) {
                System.out.println("Счёт: " + account.getDetails().getAccountId() + ", Банк: " + account.getDetails().getBankName() + ", КПП: " + account.getDetails().getKpp() + ", БИК: " + account.getDetails().getBik());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Счета не найдены.");
            logger.warn("Счета по указанным критериям не найдены");
        } else {
            logger.info("Поиск по реквизитам завершён");
        }
    }

    private void checkDir() {
        File file = new File(CSV_FILE);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error("Ошибка при создании файла счетов: {}", e.getMessage());
            }
        }
    }

    private void loadAccountsFromFile() {
        checkDir();
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String accountId = parts[0].trim();
                    String bankName = parts[1].trim();
                    String kpp = parts[2].trim();
                    String bik = parts[3].trim();
                    double balance = parts.length == 5 ? Double.parseDouble(parts[4].trim()) : 0.0;
                    BankDetails details = new BankDetails(accountId,kpp, bik,bankName);
                    BankAccount account = new BankAccount(details);
                    account.setBalance(balance);
                    accounts.put(accountId, account);
                }
            }
            logger.info("Счета загружены из файла");
        } catch (FileNotFoundException e) {
            logger.info("Файл счетов не найден, начинаем с пустого списка");
        } catch (IOException e) {
            logger.error("Ошибка при загрузке счетов: {}", e.getMessage());
        }
    }

    private void saveAccountToFile(BankAccount account) {
        checkDir();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE, true))) {
            BankDetails details = account.getDetails();
            writer.write(String.join(",", details.getAccountId(), details.getBankName(), details.getKpp(), details.getBik(),String.valueOf(account.getBalance())));
            writer.newLine();
            logger.info("Счёт сохранён в файл: {}", details.getAccountId());
        } catch (IOException e) {
            logger.error("Ошибка при сохранении счёта: {}", e.getMessage());
        }
    }
    public void updateAccountBalance(BankAccount account) {
        checkDir();
        File tempFile = new File("accounts_temp.csv");
        File originalFile = new File(CSV_FILE);
        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile)); BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equals(account.getDetails().getAccountId())) {
                    BankDetails d = account.getDetails();
                    writer.write(String.join(",", d.getAccountId(), d.getBankName(), d.getKpp(), d.getBik(), String.valueOf(account.getBalance())));
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            logger.error("Ошибка при обновлении баланса счёта: {}", e.getMessage());
        }
        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
        }
    }


}
