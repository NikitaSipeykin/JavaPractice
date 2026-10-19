package hw.java_core.bank.concurrent_bank;

import hw.java_core.bank.concurrent_bank.BankAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

  @Test
  void shouldCreateAccountWithInitialBalance() {
    BankAccount account = new BankAccount(1, 1000);

    assertEquals(1000, account.getBalance());
    assertEquals(1, account.getId());
  }

  @Test
  void shouldAllowZeroInitialBalance() {
    BankAccount account = new BankAccount(1, 0);

    assertEquals(0, account.getBalance());
  }

  @Test
  void shouldRejectNegativeInitialBalance() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new BankAccount(1, -100)
    );
  }

  @Test
  void shouldDepositMoney() {
    BankAccount account = new BankAccount(1, 1000);

    account.deposit(500);

    assertEquals(1500, account.getBalance());
  }

  @Test
  void shouldWithdrawMoney() {
    BankAccount account = new BankAccount(1, 1000);

    account.withdraw(300);

    assertEquals(700, account.getBalance());
  }

  @Test
  void shouldAllowWithdrawingEntireBalance() {
    BankAccount account = new BankAccount(1, 1000);

    account.withdraw(1000);

    assertEquals(0, account.getBalance());
  }

  @Test
  void shouldRejectZeroDeposit() {
    BankAccount account = new BankAccount(1, 1000);

    assertThrows(
        IllegalArgumentException.class,
        () -> account.deposit(0)
    );

    assertEquals(1000, account.getBalance());
  }

  @Test
  void shouldRejectNegativeDeposit() {
    BankAccount account = new BankAccount(1, 1000);

    assertThrows(
        IllegalArgumentException.class,
        () -> account.deposit(-100)
    );

    assertEquals(1000, account.getBalance());
  }

  @Test
  void shouldRejectZeroWithdrawal() {
    BankAccount account = new BankAccount(1, 1000);

    assertThrows(
        IllegalArgumentException.class,
        () -> account.withdraw(0)
    );

    assertEquals(1000, account.getBalance());
  }

  @Test
  void shouldRejectNegativeWithdrawal() {
    BankAccount account = new BankAccount(1, 1000);

    assertThrows(
        IllegalArgumentException.class,
        () -> account.withdraw(-100)
    );

    assertEquals(1000, account.getBalance());
  }

  @Test
  void shouldRejectWithdrawalWhenBalanceIsInsufficient() {
    BankAccount account = new BankAccount(1, 100);

    assertThrows(
        IllegalArgumentException.class,
        () -> account.withdraw(200)
    );

    assertEquals(100, account.getBalance());
  }
}
