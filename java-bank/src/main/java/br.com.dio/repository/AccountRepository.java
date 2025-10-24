package br.com.dio.repository;

import br.com.dio.expcetion.AccountNotFoundException;
import br.com.dio.expcetion.PixInUseException;
import br.com.dio.model.AccountWallet;
import br.com.dio.model.MoneyAudit;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.dio.repository.commonsRepository.checkFundsForTransaction;
import static java.time.temporal.ChronoUnit.SECONDS;

public class AccountRepository {

    private final List<AccountWallet> accounts = new ArrayList<>();

    public  AccountWallet create(final List<String> pix, final long initialFunds){
        if (!accounts.isEmpty()) {
            var pixInUse = accounts.stream().flatMap(a -> a.getPix().stream()).toList();
            for (var p : pix) {
                if (pixInUse.contains(p)) {
                    throw new PixInUseException("O pix '" + p + "' já está em uso");
                }
            }
        }
        var newAccount = new AccountWallet(initialFunds, pix);
        accounts.add(newAccount);
        return newAccount;
    }


    public  void deposit(final String pix, final long fundsAmount){
        var target = findByPix(pix);
        target.addMoney(fundsAmount, "depósito");
    }


    public  long withdraw( final String pix, final  long amount){
        var source = findByPix(pix);
        checkFundsForTransaction(source, amount);
        source.reducedMoney(amount);
        return amount;
    }

    public  void transferMoney(final String sourcePix, final String targetPix, final long amount){
        var source = findByPix(sourcePix);
        checkFundsForTransaction(source, amount);
        var target = findByPix(targetPix);
        var message = "pix enviando de '" + sourcePix + "' para'" + targetPix + "'";
        target.addMoney(source.reducedMoney(amount), source.getService(), message);
    }


    public AccountWallet findByPix(final String pix) {
        return accounts.stream()
                .filter(a -> a.getPix().contains(pix))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("A conta com a chave pix '" + pix + "' não existe ou foi encerrada"));
    }

    public List<AccountWallet> list() {
        return this.accounts;
    }

    public Map<OffsetDateTime, List<MoneyAudit>> getHistory(final String pix) {
        var wallet = findByPix(pix);
        var audit = wallet.getFinancialTransactions();
        return audit.stream()
                .collect(Collectors.groupingBy(t -> t.CreatedAt().truncatedTo(SECONDS)));
    }

}
