package hw.java_core.bank.concurrent_bank;

import java.util.HashMap;
import java.util.Map;

public class ConcurrentBank {
  private final Map<Integer, BankAccount> customers;
  private int counter;

  public ConcurrentBank() {
    this.customers = new HashMap<>();
    this.counter = 0;
  }


  public synchronized BankAccount createAccount(long amount) {
    BankAccount bankAccount = new BankAccount(counter, amount);
    customers.put(counter, bankAccount);
    counter++;
    return bankAccount;
  }

  public synchronized void transfer(BankAccount sender, BankAccount receiver, long value) {
    validateAccount(sender);
    validateAccount(receiver);

    if (sender.equals(receiver)){
      throw new IllegalArgumentException("Cannot transfer to the same account");
    }

    if (value <= 0){
      throw new IllegalArgumentException("Transfer value must be positive");
    }

    BankAccount first;
    BankAccount second;

    if (sender.getId() < receiver.getId()) {
      first = sender;
      second = receiver;
    } else {
      first = receiver;
      second = sender;
    }

    synchronized (first){
      synchronized (second){
        if (sender.getBalance() < value){
          throw new IllegalArgumentException("Not enough money");
        }
        long newReceiverBalance = Math.addExact(receiver.getBalance(), value);
        sender.withdraw(value);
        receiver.deposit(value);
      }
    }
  }

  public synchronized String getTotalBalance() {
    long totalBalance = 0;

    for (BankAccount customer :  customers.values()) {
      totalBalance += customer.getBalance();
    }

    return Long.toString(totalBalance);
  }

  private void validateAccount(BankAccount account) {
    if (account == null
        || customers.get(account.getId()) != account) {
      throw new IllegalArgumentException(
          "Account does not belong to this bank"
      );
    }
  }
}
