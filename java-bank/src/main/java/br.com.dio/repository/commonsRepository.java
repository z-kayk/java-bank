package br.com.dio.repository;


import br.com.dio.expcetion.NoFoundsEnoughException;
import br.com.dio.model.Wallet;
import br.com.dio.model.Money;
import br.com.dio.model.MoneyAudit;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static br.com.dio.model.BankService.ACCOUNT;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class commonsRepository {

    public  static  void checkFundsForTransaction(final Wallet source, final  long amount) {
        if (source.getFunds() < amount) {
            throw new NoFoundsEnoughException("sua conta não tem dinheiro o suficiente para realizar essa transação");
        }

    }

    public static List<Money> generateMoney(final UUID transactionsId, final long funds, final String description) {
        var history = new MoneyAudit(transactionsId, ACCOUNT, description, OffsetDateTime.now());
        return Stream.generate(() -> new Money(history)).limit(funds).toList();
    }
}
