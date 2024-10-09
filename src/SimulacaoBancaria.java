import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

class ContaBancaria {
    private double saldo;

    public ContaBancaria(double saldoInicial) {
        this.saldo = saldoInicial;
    }

    public synchronized void sacar(double valor) {
        saldo -= valor;
        System.out.println(Thread.currentThread().getName() + " sacou R$" + valor + ". Saldo atual: R$" + saldo);
    }

    public synchronized void depositar(double valor) {
        saldo += valor;
        System.out.println(Thread.currentThread().getName() + " depositou R$" + valor + ". Saldo atual: R$" + saldo);
    }

    public double getSaldo() {
        return saldo;
    }
}

class OperacaoThread extends Thread {
    private ContaBancaria conta;
    private String operacao;
    private double valor;

    public OperacaoThread(ContaBancaria conta, String operacao, double valor) {
        this.conta = conta;
        this.operacao = operacao;
        this.valor = valor;
    }

    @Override
    public void run() {
        if ("depositar".equalsIgnoreCase(operacao)) {
            conta.depositar(valor);
        } else if ("sacar".equalsIgnoreCase(operacao) && valor > 0) {
            conta.sacar(valor);
        } else {
            System.out.println("Operação inválida: " + operacao + " com valor: " + valor);
        }
    }
}

public class SimulacaoBancaria {
    public static void criarArquivoOperacoes() {
        String caminhoArquivo = "operacoes.txt";
        File file = new File(caminhoArquivo);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(caminhoArquivo)) {
                writer.write("depositar,345\n");
                writer.write("sacar,678\n");
                writer.write("depositar,999\n");
                writer.write("sacar,999\n");
                writer.write("depositar,0\n");
                writer.write("sacar,123\n");
                writer.write("depositar,456\n");
                writer.write("sacar,789\n");
                writer.write("depositar,234\n");
                writer.write("sacar,567\n");
                writer.write("depositar,890\n");
                writer.write("sacar,321\n");
                writer.write("depositar,654\n");
                writer.write("sacar,987\n");
                writer.write("depositar,111\n");
                writer.write("sacar,222\n");
                writer.write("depositar,333\n");
                writer.write("sacar,444\n");
                writer.write("depositar,555\n");
                writer.write("sacar,666\n");
                System.out.println("Arquivo 'operacoes.txt' criado com sucesso.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        criarArquivoOperacoes();
        ContaBancaria conta = new ContaBancaria(0.0);
        List<Thread> threads = new ArrayList<>();
        String caminhoArquivo = "operacoes.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] partes = linha.split(",");
                if (partes.length < 2) {
                    System.out.println("Linha mal formatada: " + linha);
                    continue;
                }

                String operacao = partes[0].trim();
                double valor;
                try {
                    valor = Double.parseDouble(partes[1].trim());
                } catch (NumberFormatException e) {
                    System.out.println("Valor inválido: " + partes[1]);
                    continue;
                }

                OperacaoThread thread = new OperacaoThread(conta, operacao, valor);
                threads.add(thread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Saldo final na conta: R$" + conta.getSaldo());
    }
}
