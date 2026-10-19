package hw.java_core.bank.concurrent_bank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentBankTest {

  @Test
  void shouldCreateAccountsWithUniqueIds() {
    ConcurrentBank bank = new ConcurrentBank();

    BankAccount first = bank.createAccount(1000);
    BankAccount second = bank.createAccount(500);

    assertNotEquals(first.getId(), second.getId());
    assertEquals(1000, first.getBalance());
    assertEquals(500, second.getBalance());
  }

  @Test
  void shouldReturnTotalBalance() {
    ConcurrentBank bank = new ConcurrentBank();

    bank.createAccount(1000);
    bank.createAccount(500);

    assertEquals("1500", bank.getTotalBalance());
  }

  @Test
  void shouldTransferMoneyBetweenAccounts() {
    ConcurrentBank bank = new ConcurrentBank();

    BankAccount sender = bank.createAccount(1000);
    BankAccount receiver = bank.createAccount(500);

    bank.transfer(sender, receiver, 200);

    assertEquals(800, sender.getBalance());
    assertEquals(700, receiver.getBalance());
    assertEquals("1500", bank.getTotalBalance());
  }

  @Test
  void shouldTransferMoneyInOppositeDirection() {
    ConcurrentBank bank = new ConcurrentBank();

    BankAccount first = bank.createAccount(1000);
    BankAccount second = bank.createAccount(500);

    bank.transfer(first, second, 200);
    bank.transfer(second, first, 100);

    assertEquals(900, first.getBalance());
    assertEquals(600, second.getBalance());
    assertEquals("1500", bank.getTotalBalance());
  }

  @Test
  void shouldRejectTransferWhenBalanceIsInsufficient() {
    ConcurrentBank bank = new ConcurrentBank();

    BankAccount sender = bank.createAccount(100);
    BankAccount receiver = bank.createAccount(500);

    assertThrows(
        IllegalArgumentException.class,
        () -> bank.transfer(sender, receiver, 200)
    );

    assertEquals(100, sender.getBalance());
    assertEquals(500, receiver.getBalance());
    assertEquals("600", bank.getTotalBalance());
  }

  @Test
  void shouldRejectZeroTransfer() {
    ConcurrentBank bank = new ConcurrentBank();

    BankAccount sender = bank.createAccount(1000);
    BankAccount receiver = bank.createAccount(500);

    assertThrows(
        IllegalArgumentException.class,
        () -> bank.transfer(sender, receiver, 0)
    );

    assertEquals("1500", bank.getTotalBalance());
  }

  @Test
  void shouldRejectNegativeTransfer() {
    ConcurrentBank bank = new ConcurrentBank();

    BankAccount sender = bank.createAccount(1000);
    BankAccount receiver = bank.createAccount(500);

    assertThrows(
        IllegalArgumentException.class,
        () -> bank.transfer(sender, receiver, -100)
    );

    assertEquals("1500", bank.getTotalBalance());
  }

  @Test
  void shouldRejectTransferToSameAccount() {
    ConcurrentBank bank = new ConcurrentBank();

    BankAccount account = bank.createAccount(1000);

    assertThrows(
        IllegalArgumentException.class,
        () -> bank.transfer(account, account, 100)
    );

    assertEquals(1000, account.getBalance());
  }

  @Test
  void shouldRejectAccountFromAnotherBank() {
    ConcurrentBank firstBank = new ConcurrentBank();
    ConcurrentBank secondBank = new ConcurrentBank();

    BankAccount sender = firstBank.createAccount(1000);
    BankAccount foreignAccount = secondBank.createAccount(500);

    assertThrows(
        IllegalArgumentException.class,
        () -> firstBank.transfer(sender, foreignAccount, 100)
    );

    assertEquals(1000, sender.getBalance());
    assertEquals(500, foreignAccount.getBalance());
  }

  @Test
  void shouldRejectNullAccount() {
    ConcurrentBank bank = new ConcurrentBank();
    BankAccount account = bank.createAccount(1000);

    assertThrows(
        IllegalArgumentException.class,
        () -> bank.transfer(null, account, 100)
    );

    assertEquals(1000, account.getBalance());
  }

  @Test
  void shouldPreserveTotalBalanceDuringConcurrentTransfers()
      throws Exception {

    ConcurrentBank bank = new ConcurrentBank();

    BankAccount first = bank.createAccount(100_000);
    BankAccount second = bank.createAccount(100_000);

    int threadCount = 8;
    int transfersPerThread = 1000;

    ExecutorService executor =
        Executors.newFixedThreadPool(threadCount);

    CountDownLatch start = new CountDownLatch(1);
    List<Future<?>> futures = new ArrayList<>();

    try {
      for (int i = 0; i < threadCount; i++) {
        final boolean forward = i % 2 == 0;

        futures.add(executor.submit(() -> {
          start.await();

          for (int j = 0; j < transfersPerThread; j++) {
            if (forward) {
              bank.transfer(first, second, 1);
            } else {
              bank.transfer(second, first, 1);
            }
          }

          return null;
        }));
      }

      start.countDown();

      for (Future<?> future : futures) {
        future.get(30, TimeUnit.SECONDS);
      }

      assertEquals("200000", bank.getTotalBalance());

      assertEquals(
          200_000,
          first.getBalance() + second.getBalance()
      );
    } finally {
      executor.shutdownNow();
    }
  }

  @Test
  void shouldRejectDepositWhenBalanceOverflows() {
    BankAccount account =
        new BankAccount(1, Long.MAX_VALUE);

    assertThrows(
        ArithmeticException.class,
        () -> account.deposit(1)
    );

    assertEquals(Long.MAX_VALUE, account.getBalance());
  }
}
