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
        LoadAccountsFromFile();
        logger.info("Список счетов инициализирован");
    }

    public void AddAccount(BankAccount account) {
        accounts.put(account.GetDetails().GetAccountId(), account);
        SaveAccountToFile(account);
        logger.info("Добавлен счёт: {}", account.GetDetails().GetAccountId());
    }

    public BankAccount FindByAccountNumber(String accountNumber) {
        BankAccount account = accounts.get(accountNumber);
        if (account != null) {
            logger.info("Найден счёт по номеру: {}", accountNumber);
            System.out.println("По accountNumber " + accountNumber + " найден счёт: " + account.GetDetails().GetBankName() + ", КПП: " + account.GetDetails().GetKpp() + ", БИК: " + account.GetDetails().GetBik());
        } else {
            logger.warn("Счёт с номером {} не найден", accountNumber);
            System.out.println("Счёт с номером " + accountNumber + " не найден.");
        }
        return account;
    }

    public void SearchByDetails(String kpp, String bik) {
        boolean found = false;
        System.out.println("Результаты поиска по реквизитам:");
        for (BankAccount account : accounts.values()) {
            boolean matches = true;
            if (kpp != null && !account.GetDetails().GetKpp().equalsIgnoreCase(kpp)) {
                matches = false;
            }
            if (bik != null && !account.GetDetails().GetBik().equalsIgnoreCase(bik)) {
                matches = false;
            }
            if (matches) {
                System.out.println("Счёт: " + account.GetDetails().GetAccountId() + ", Банк: " + account.GetDetails().GetBankName() + ", КПП: " + account.GetDetails().GetKpp() + ", БИК: " + account.GetDetails().GetBik());
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

    private void CheckDir() {
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

    private void LoadAccountsFromFile() {
        CheckDir();
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
                    account.SetBalance(balance);
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

    private void SaveAccountToFile(BankAccount account) {
        CheckDir();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE, true))) {
            BankDetails details = account.GetDetails();
            writer.write(String.join(",", details.GetAccountId(), details.GetBankName(), details.GetKpp(), details.GetBik(),String.valueOf(account.GetBalance())));
            writer.newLine();
            logger.info("Счёт сохранён в файл: {}", details.GetAccountId());
        } catch (IOException e) {
            logger.error("Ошибка при сохранении счёта: {}", e.getMessage());
        }
    }
    public void UpdateAccountBalance(BankAccount account) {
        CheckDir();
        File tempFile = new File("accounts_temp.csv");
        File originalFile = new File(CSV_FILE);
        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile)); BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equals(account.GetDetails().GetAccountId())) {
                    BankDetails d = account.GetDetails();
                    writer.write(String.join(",", d.GetAccountId(), d.GetBankName(), d.GetKpp(), d.GetBik(), String.valueOf(account.GetBalance())));
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
