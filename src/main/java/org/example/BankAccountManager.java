package org.example;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BankAccountManager {
    private static final Logger logger = LogManager.getLogger(BankAccountManager.class);
    private static Scanner scanner = new Scanner(System.in);
    private static AccountRegistry registry = new AccountRegistry();
    private static BankAccount currentAccount;

    public static void main(String[] args) {
        try {
            logger.info("Менеджер банковских счетов запущен");
            while (true) {
                displayMenu();
                int choice = getUserChoice();
                switch (choice) {
                    case 1:
                        openAccount();
                        break;
                    case 2:
                        depositMoney();
                        break;
                    case 3:
                        withdrawMoney();
                        break;
                    case 4:
                        showBalance();
                        break;
                    case 5:
                        showTransactions();
                        break;
                    case 6:
                        searchTransactions();
                        break;
                    case 7:
                        searchByAccountNumber();
                        break;
                    case 8:
                        searchByDetails();
                        break;
                    case 9:
                        selectAccount();
                        break;
                    case 10:
                        logger.info("Программа завершена");
                        System.out.println("Программа завершена");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Неверный выбор. Попробуйте снова");
                        logger.warn("Неверный выбор в меню: {}", choice);
                }
            }
        } catch (Exception e) {
            logger.error("ошибка: {}", e.getMessage());
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
    }

    private static void displayMenu() {
        System.out.println("Банковский счёт");
        System.out.println("1. Открыть счёт");
        System.out.println("2. Положить деньги");
        System.out.println("3. Снять деньги");
        System.out.println("4. Показать баланс");
        System.out.println("5. Показать список транзакций");
        System.out.println("6. Поиск транзакций");
        System.out.println("7. Поиск по номеру счёта");
        System.out.println("8. Поиск по реквизитам");
        System.out.println("9. Выбрать счёт");
        System.out.println("10. Выход");
        System.out.print("Выберите опцию: ");
        logger.debug("Меню отображено");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            logger.warn("Некорректный ввод для выбора меню");
            return -1;
        }
    }

    private static void openAccount() {
        System.out.print("Введите номер счёта (20 цифр): ");
        String accountNumber = scanner.nextLine();
        if (accountNumber.length() != 20) {
            System.out.println("Номер счёта должен состоять из 20 цифр");
            logger.error("Неверный формат номера счёта: {}", accountNumber);
            return;
        }
        System.out.print("Введите КПП (9 цифр): ");
        String kpp = scanner.nextLine();
        if (kpp.length() != 9) {
            System.out.println("КПП должен состоять из 9 цифр");
            logger.error("Неверный формат КПП: {}", kpp);
            return;
        }
        System.out.print("Введите БИК (9 цифр): ");
        String bik = scanner.nextLine();
        if (bik.length() != 9) {
            System.out.println("БИК должен состоять из 9 цифр");
            logger.error("Неверный формат БИК: {}", bik);
            return;
        }
        System.out.print("Введите название банка: ");
        String bankName = scanner.nextLine();
        if (bankName.isEmpty()) {
            System.out.println("Название банка не может быть пустым");
            logger.error("Пустое название банка");
            return;
        }
        BankDetails details = new BankDetails(accountNumber, kpp, bik, bankName);
        BankAccount account = new BankAccount(details);
        registry.addAccount(account);
        currentAccount = account;
        System.out.println("Счёт успешно открыт");
        logger.info("Аккаунта создан: {}", accountNumber);
    }

    private static void depositMoney() {
        if (currentAccount == null) {
            System.out.println("Сначала выберите или откройте счёт");
            logger.warn("Deposit attempted without selected account");
            return;
        }
        System.out.print("Введите сумму для пополнения: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            currentAccount.deposit(amount);
            System.out.printf("Сумма %.2f успешно зачислена", amount);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат суммы");
            logger.error("Неверный формат суммы(DepositMoney)");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            logger.error("Ошибка депозита: {}", e.getMessage());
        }
    }

    private static void withdrawMoney() {
        if (currentAccount == null) {
            System.out.println("Сначала выберите или откройте счёт");
            logger.warn("Вывод без выбранного аккаунта");
            return;
        }
        System.out.print("Введите сумму для снятия: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            currentAccount.withdraw(amount);
            System.out.printf("Сумма %.2f успешно снята", amount);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат суммы.");
            logger.error("Неверный формат суммы");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            logger.error("Ошибка вывода: {}", e.getMessage());
        }
    }

    private static void showBalance() {
        if (currentAccount == null) {
            System.out.println("Сначала выберите или откройте счёт");
            logger.warn("Проверка баланса без выбранного аккаунта");
            return;
        }
        System.out.printf("Текущий баланс: %.2f", currentAccount.getBalance());
        logger.info("Баланс показан: {} для аккаунта {}", currentAccount.getBalance(), currentAccount.getDetails().getAccountId());
    }

    private static void showTransactions() {
        if (currentAccount == null) {
            System.out.println("Сначала выберите или откройте счёт");
            logger.warn("Поиск транкзакций без выбранного аккаунта");
            return;
        }
        currentAccount.showTransactions();
    }

    private static void searchTransactions() {
        if (currentAccount == null) {
            System.out.println("Сначала выберите или откройте счёт");
            logger.warn("Поиск без выбранного аккаунта");
            return;
        }
        System.out.print("Введите тип транзакции (DEPOSIT/WITHDRAW, или Enter для пропуска) ");
        String type = scanner.nextLine();
        type = type.isEmpty() ? null : type;

        System.out.print("Введите минимальную сумму (или Enter для пропуска) ");
        Double minAmount = null;
        String minInput = scanner.nextLine();
        if (!minInput.isEmpty()) {
            try {
                minAmount = Double.parseDouble(minInput);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат минимальной суммы");
                logger.error("Неверный формат");
                return;
            }
        }

        System.out.print("Введите максимальную сумму (или Enter для пропуска) ");
        Double maxAmount = null;
        String maxInput = scanner.nextLine();
        if (!maxInput.isEmpty()) {
            try {
                maxAmount = Double.parseDouble(maxInput);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат максимальной суммы");
                logger.error("Неверный формат");
                return;
            }
        }
        currentAccount.searchTransactions(type, minAmount, maxAmount);
    }

    private static void searchByAccountNumber() {
        System.out.print("Введите номер счёта для поиска ");
        String accountNumber = scanner.nextLine();
        registry.findByAccountNumber(accountNumber);
    }

    private static void searchByDetails() {
        System.out.print("Введите КПП (или Enter для пропуска) ");
        String kpp = scanner.nextLine();
        kpp = kpp.isEmpty() ? null : kpp;
        System.out.print("Введите БИК (или Enter для пропуска) ");
        String bik = scanner.nextLine();
        bik = bik.isEmpty() ? null : bik;
        registry.searchByDetails(kpp, bik);
    }
    private static void selectAccount() {
        System.out.print("Введите номер счёта для выбора ");
        String accountNumber = scanner.nextLine();
        BankAccount account = registry.findByAccountNumber(accountNumber);
        if (account != null) {
            currentAccount = account;
            System.out.println("Счёт " + accountNumber + " выбран");
            logger.info("Аккаунт выбран: {}", accountNumber);
        }
    }
}
