package io.github.LGCMcLovin.msClubKeno.managers;

import io.github.LGCMcLovin.msClubKeno.MSClubKeno;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

public class EconManager
{
     private static final MSClubKeno instance =  MSClubKeno.getInstance();

      private static Currency currency;
      private static EconomyService economyService;

      private static PluginContainer pluginContainer;


    public static void setEconomyService()
    {
        Optional<EconomyService> serviceOpt = Sponge.getServiceManager().provide(EconomyService.class);
        if(serviceOpt.isPresent() && Sponge.getPluginManager().fromInstance(instance).isPresent())
        {

               currency = serviceOpt.get().getDefaultCurrency();
               economyService = serviceOpt.get();
               pluginContainer = Sponge.getPluginManager().fromInstance(instance).get();
        }
    }

    public static EconomyService getEconomyService()
    {


        return economyService;
    }


    public static ResultType withdraw(Player player, double charge)
    {

        Optional<UniqueAccount> accountOpt = economyService.getOrCreateAccount(player.getUniqueId());
        if(!accountOpt.isPresent())
        {
            return ResultType.FAILED;
        }

        UniqueAccount account = accountOpt.get();

        TransactionResult result = account.withdraw
                (
                        currency, BigDecimal.valueOf(charge), Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, pluginContainer).add(EventContextKeys.PLAYER, player).build(), instance, player)
                );
        if (result.getResult() != ResultType.SUCCESS) {
            player.sendMessage(Text.builder("Account withdrawl failed").color(TextColors.RED).build());
        return ResultType.FAILED;
        }

        return ResultType.SUCCESS;
    }

    public static ResultType deposit(Player player, double payout)
    {
        Optional<UniqueAccount> accountOpt = economyService.getOrCreateAccount(player.getUniqueId());
        if(!accountOpt.isPresent())
        {
            return ResultType.FAILED;
        }

        UniqueAccount account = accountOpt.get();

        TransactionResult result = account.deposit
                (
                        currency, BigDecimal.valueOf(payout), Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, pluginContainer).add(EventContextKeys.PLAYER, player).build(), instance, player)
                );
        if (result.getResult() != ResultType.SUCCESS) {
            player.sendMessage(Text.builder("Account withdrawl failed").color(TextColors.RED).build());
            return ResultType.FAILED;
        }

        return ResultType.SUCCESS;
    }

    public static Currency getCurrency()
    {
        return currency;
    }
}
