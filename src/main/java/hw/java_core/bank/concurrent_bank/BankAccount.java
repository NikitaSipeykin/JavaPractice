package hw.java_core.bank.concurrent_bank;

public class BankAccount {
  private final int id;
  private long amount;

  public BankAccount(int id, long amount) {
    if (amount < 0){
      throw new IllegalArgumentException("Amount must be positive");
    }
    this.id = id;
    this.amount = amount;
  }


  protected synchronized void deposit(long value){
    if (value <= 0){
      throw new IllegalArgumentException("Value must be positive");
    }
    this.amount = Math.addExact(this.amount, value);
  }

  protected synchronized void withdraw(long value){
    if (value <= 0){
      throw new IllegalArgumentException("Value must be positive");
    }

    if (amount < value){
      throw new IllegalArgumentException("Not enough money");
    }else {
      this.amount = this.amount - value;
    }
  }


  public synchronized long getBalance(){
    return amount;
  }

  public int getId() {
    return id;
  }
}
