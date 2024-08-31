package cy.jdkdigital.trophymanager.compat;

import cy.jdkdigital.trophymanager.init.ModBlocks;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin
{
    @Override
    public void register(EmiRegistry registry) {
        registry.removeEmiStacks(EmiStack.of(ModBlocks.TROPHY.get()));
    }
}
