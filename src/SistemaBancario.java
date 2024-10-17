import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SistemaBancario {
  private static final double SALDO_INICIAL = 1000.0;

  public static void main(String[] args) {
    ContaBancaria conta = new ContaBancaria(SALDO_INICIAL);

    try {
      BufferedReader reader = new BufferedReader(new FileReader("D:\\DEV\\TrabalhoThreads\\src\\operacoes.txt"));

      String linha;
      while ((linha = reader.readLine()) != null) {
        String[] partes = linha.split(",");
        String operacao = partes[0].trim().toLowerCase();
        double valor = Double.parseDouble(partes[1].trim());

        ThreadOperadora thread = new ThreadOperadora(conta, operacao, valor);
        thread.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      Thread.sleep(5000); 
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("\nResultado final:");
    System.out.println("Saldo final: R$ " + conta.getSaldo());
  }
}

class ContaBancaria {
  private double saldo;
  private final Lock lock = new ReentrantLock();

  public ContaBancaria(double saldoInicial) {
    this.saldo = saldoInicial;
  }

  public void depositar(double valor) {
    lock.lock();
    try {
      saldo += valor;
      System.out.println("Depósito de R$ " + valor + ". Saldo atual: R$ " + saldo);
    } finally {
      lock.unlock();
    }
  }

  public void sacar(double valor) {
    lock.lock();
    try {
      if (saldo >= valor) {
        saldo -= valor;
        System.out.println("saque de R$ " + valor + ". Saldo atual: R$ " + saldo);
      } else {
        System.out.println("Saldo insuficiente");
      }
    } finally {
      lock.unlock();
    }
  }

  public double getSaldo() {
    return saldo;
  }
}

class ThreadOperadora extends Thread {
  private ContaBancaria conta;
  private String operacao;
  private double valor;

  public ThreadOperadora(ContaBancaria conta, String operacao, double valor) {
    this.conta = conta;
    this.operacao = operacao;
    this.valor = valor;
  }

  @Override
  public void run() {
    switch (operacao) {
      case "depositar":
        conta.depositar(valor);
        break;
      case "sacar":
        conta.sacar(valor);
        break;
    }
  }
}