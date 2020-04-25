package WolfShotz.Wyrmroost.content.items;

import WolfShotz.Wyrmroost.content.items.base.ArmorMaterialList;
import WolfShotz.Wyrmroost.content.items.base.ItemArmorBase;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class DrakeArmorItem extends ItemArmorBase
{
    private static final UUID WEAPON_KNOCKBACK_MODIFER = UUID.fromString("bc5d758a-f8ba-4859-8bb3-503209cbbce8");
    private static final UUID ARMOR_KNOCKBACK_RESISTANCE = UUID.fromString("eaa010aa-299d-4c76-9f02-a1283c9e890b");

    private boolean fullSet = false;

    public DrakeArmorItem(EquipmentSlotType equipType)
    {
        super(ArmorMaterialList.DRAKE, equipType);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack)
    { // Todo better handle this
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot);

        if (fullSet)
        {
            if (slot == this.slot)
            {
                multimap.put(SharedMonsterAttributes.ATTACK_KNOCKBACK.getName(), new AttributeModifier(WEAPON_KNOCKBACK_MODIFER, "Weapon modifier", 1000d, AttributeModifier.Operation.MULTIPLY_TOTAL));
                multimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(ARMOR_KNOCKBACK_RESISTANCE, "Armor modifier", 10, AttributeModifier.Operation.ADDITION));
            }
//            else fullSet = false;
        }

        return multimap;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> lines, ITooltipFlag p_77624_4_)
    {
        lines.add(new TranslationTextComponent("item.wyrmroost.armors.set", new StringTextComponent("Drake").applyTextStyle(TextFormatting.DARK_GREEN)));
        lines.add(new StringTextComponent(""));
        lines.add(new TranslationTextComponent("item.wyrmroost.armors.drakedesc"));
    }

    public void setFullSet(boolean flag) { this.fullSet = flag; }
}
