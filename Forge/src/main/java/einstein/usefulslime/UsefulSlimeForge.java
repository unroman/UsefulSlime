package einstein.usefulslime;

import einstein.usefulslime.init.ModItems;
import einstein.usefulslime.platform.ForgeRegistryHelper;
import einstein.usefulslime.util.BounceHandler;
import einstein.usefulslime.util.LivingFallData;
import einstein.usefulslime.util.PlayerTickData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

@Mod(UsefulSlime.MOD_ID)
public class UsefulSlimeForge {

    public UsefulSlimeForge() {
        UsefulSlime.init();
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onBuildContents);
        ForgeRegistryHelper.ITEMS.register(modEventBus);
        ForgeRegistryHelper.BLOCKS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onFall);
        MinecraftForge.EVENT_BUS.addListener(this::missingMappings);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerTick);
    }

    void onFall(LivingFallEvent event) {
        LivingFallData data = new LivingFallData(event.getEntity(), event.getDistance(), event.getDamageMultiplier());
        UsefulSlime.onFall(data);
        event.setDamageMultiplier(data.getDamageMultiplier());
        event.setDistance(data.getDistance());

        if (data.isCanceled()) {
            event.setCanceled(true);
        }
    }

    void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            if (BounceHandler.BOUNCING_ENTITIES.containsKey(player)) {
                BounceHandler.BOUNCING_ENTITIES.get(player).onPlayerTick(new PlayerTickData(player));
            }
        }
    }

    void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> tab = event.getTabKey();
        var entries = event.getEntries();

        if (tab == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            entries.putAfter(new ItemStack(Items.SPYGLASS), new ItemStack(ModItems.SLIME_SLING.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        else if (tab == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.SLIPPERY_SLIME_BLOCK_ITEM);
        }
        else if (tab == CreativeModeTabs.COMBAT) {
            entries.putAfter(new ItemStack(Items.TURTLE_HELMET), new ItemStack(ModItems.SLIME_HELMET.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(ModItems.SLIME_HELMET.get()), new ItemStack(ModItems.SLIME_CHESTPLATE.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(ModItems.SLIME_CHESTPLATE.get()), new ItemStack(ModItems.SLIME_LEGGINGS.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(ModItems.SLIME_LEGGINGS.get()), new ItemStack(ModItems.SLIME_BOOTS.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        else if (tab == CreativeModeTabs.FOOD_AND_DRINKS) {
            entries.putBefore(new ItemStack(Items.MUSHROOM_STEW), new ItemStack(ModItems.JELLO.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    void missingMappings(MissingMappingsEvent event) {
        for (MissingMappingsEvent.Mapping<Item> mapping : event.getMappings(ForgeRegistries.ITEMS.getRegistryKey(), UsefulSlime.MOD_ID)) {
            if (mapping.getKey().getPath().equals("slimesling")) {
                mapping.remap(ModItems.SLIME_SLING.get());
            }
        }
    }
}
