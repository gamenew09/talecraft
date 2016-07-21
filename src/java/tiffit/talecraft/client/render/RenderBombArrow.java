package tiffit.talecraft.client.render;

import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tiffit.talecraft.entity.projectile.EntityBombArrow;

@SideOnly(Side.CLIENT)
public class RenderBombArrow extends RenderArrow<EntityBombArrow>
{
    public static final ResourceLocation RES_ARROW = new ResourceLocation("talecraft:textures/entity/projectiles/bombarrow.png");

    public RenderBombArrow(RenderManager manager){
        super(manager);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityBombArrow entity)
    {
        return RES_ARROW;
    }
}
