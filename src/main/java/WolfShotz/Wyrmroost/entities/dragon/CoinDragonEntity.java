package WolfShotz.Wyrmroost.entities.dragon;

import WolfShotz.Wyrmroost.items.CoinDragonItem;
import WolfShotz.Wyrmroost.registry.WRItems;
import WolfShotz.Wyrmroost.registry.WRSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Simple Entity really, just bob and down in the same spot, and land to sleep at night. Easy.
 */
public class CoinDragonEntity extends MobEntity
{
    public static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(CoinDragonEntity.class, DataSerializers.VARINT);
    public static String DATA_VARIANT = "Variant";

    public CoinDragonEntity(EntityType<? extends CoinDragonEntity> type, World worldIn) { super(type, worldIn); }

    @Override
    protected void registerGoals() { goalSelector.addGoal(0, new LookAtGoal(this, PlayerEntity.class, 4)); }

    @Override
    protected void registerData()
    {
        super.registerData();
        dataManager.register(VARIANT, rand.nextInt(5));
    }

    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putInt(DATA_VARIANT, getVariant());
    }

    @Override
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        setVariant(compound.getInt(DATA_VARIANT));
    }

    public int getVariant() { return dataManager.get(VARIANT); }

    public void setVariant(int variant) { dataManager.set(VARIANT, variant); }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4);
        getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.02);
    }

    // move up if too low, move down if too high, else, just bob up and down
    @Override
    public void travel(Vec3d positionIn)
    {
        if (isAIDisabled()) return;
        double moveSpeed = 0.02;
        double yMot;
        double altitiude = getAltitude();
        if (altitiude < 1.5) yMot = moveSpeed;
        else if (altitiude > 3) yMot = -moveSpeed;
        else yMot = Math.sin(ticksExisted * 0.1) * 0.0035;

        setMotion(getMotion().add(0, yMot, 0));
        move(MoverType.SELF, getMotion());
        setMotion(getMotion().scale(0.91));
    }

    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand)
    {
        if (player.getHeldItem(hand).interactWithEntity(player, this, hand)) return true;

        ItemEntity itemEntity = new ItemEntity(world, getPosX(), getPosY(), getPosZ(), getItemStack());
        double x = player.getPosX() - getPosX();
        double y = player.getPosY() - getPosY();
        double z = player.getPosZ() - getPosZ();
        itemEntity.setMotion(x * 0.1, y * 0.1 + Math.sqrt(Math.sqrt(x * x + y * y + z * z)) * 0.08, z * 0.1);
        world.addEntity(itemEntity);
        remove();
        return true;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize size) { return size.height * 0.8645f; }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) { return new ItemStack(WRItems.COIN_DRAGON.get()); }

    @Override
    public boolean isOnLadder() { return false; }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) { return false; }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {}

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() { return WRSounds.ENTITY_COINDRAGON_IDLE.get(); }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return WRSounds.ENTITY_COINDRAGON_IDLE.get(); }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() { return WRSounds.ENTITY_COINDRAGON_IDLE.get(); }

    public double getAltitude()
    {
        BlockPos.Mutable pos = new BlockPos.Mutable(getPosition().down());
        while (pos.getY() > 0 && !world.getBlockState(pos).isSolid()) pos.setY(pos.getY() - 1);
        return getPosY() - pos.getY();
    }

    public ItemStack getItemStack()
    {
        ItemStack stack = new ItemStack(WRItems.COIN_DRAGON.get());
        stack.getOrCreateTag().put(CoinDragonItem.DATA_ENTITY, serializeNBT());
        if (hasCustomName()) stack.setDisplayName(getCustomName());
        return stack;
    }
}