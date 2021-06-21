package pjc21.mods.thirstywater;

import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Objects;

@Mod(ThirstyWater.MODID)
public class ThirstyWater
{
    public static final String MODID = "thirstywater";

    public ThirstyWater() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid = ThirstyWater.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class BottleFilledEvent {

        @SubscribeEvent
        public static void onBottleFilledEvent(PlayerInteractEvent.RightClickItem event) {

            if (!event.getWorld().isClientSide) {
                if (event.getItemStack().getItem() instanceof GlassBottleItem && Objects.requireNonNull(event.getItemStack().getItem().getRegistryName()).toString().equals("minecraft:glass_bottle")) {

                    BlockRayTraceResult raytraceresult = getPlayerPOVHitResult(event.getWorld(), event.getPlayer());

                    if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
                        BlockPos blockpos = raytraceresult.getBlockPos();
                        BlockState blockState = event.getWorld().getBlockState(blockpos);

                        if (event.getWorld().getFluidState(blockpos).is(FluidTags.WATER)) {

                            if (blockState.getBlock() instanceof IBucketPickupHandler) {
                                event.setCanceled(true);
                                if (!event.getPlayer().isCreative()) {
                                    event.getItemStack().shrink(1);
                                }
                                ItemHandlerHelper.giveItemToPlayer(event.getPlayer(), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
                                ((IBucketPickupHandler)blockState.getBlock()).takeLiquid(event.getWorld(), blockpos, blockState);
                            }
                        }
                    }
                }
            }
        }
    }

    protected static BlockRayTraceResult getPlayerPOVHitResult(World world, PlayerEntity playerEntity) {
        float f = playerEntity.xRot;
        float f1 = playerEntity.yRot;
        Vector3d vector3d = playerEntity.getEyePosition(1.0F);
        float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = Objects.requireNonNull(playerEntity.getAttribute(ForgeMod.REACH_DISTANCE.get())).getValue();
        Vector3d vector3d1 = vector3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return world.clip(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.SOURCE_ONLY, playerEntity));
    }
}
