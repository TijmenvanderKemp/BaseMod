package basemod.patches.com.megacrit.cardcrawl.unlock.UnlockTracker;

import basemod.BaseMod;
import basemod.abstracts.CustomAchievement;
import basemod.helpers.CustomAchievementBorderTextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.stats.AchievementGrid;
import com.megacrit.cardcrawl.screens.stats.AchievementItem;

@SpirePatch(
    cls = "com.megacrit.cardcrawl.screens.stats.AchievementGrid",
    method = "render"
)
public class RenderBordersOverCustomAchievementInGrid {
  /**
   * If the achievement is a custom one, automatically render the golden border if unlocked,
   * or the greyscale border with the lock if locked
   */
  public static void Postfix(AchievementGrid __instance, SpriteBatch ___sb, float ___renderY) {
    CustomAchievementBorderTextureLoader.loadDefaultBorderTextures();
    for (int i = 0; i < __instance.items.size(); i++) {
      AchievementItem achievement = __instance.items.get(i);
      if (BaseMod.isModdedAchievement(achievement.key)) {
        TextureRegion textureRegion = CustomAchievementBorderTextureLoader.chooseTextureRegion(achievement);
        float zoomEffect = achievement.hb.hovered ? 1.1f : 1f;
        float spacing = 200 * Settings.scale;
        float x = 560.0F * Settings.scale + (i % 5) * spacing;
        // Integer division is on purpose here, we're trying to figure out on which row of 5 achievements this belongs
        float y = ___renderY - (i / 5) * spacing + 680.0F * Settings.yScale;

        if (achievement.isUnlocked) {
          ___sb.setColor(Color.WHITE.cpy());
        }
        else {
          // Fixes an issue where it blended the border and the achievement badge together, it had something to do with
          // the alpha of the color in the spritebatch.
          ___sb.setColor(new Color(0.8f, 0.8f, 0.8f, 1.0f));
        }
        ___sb.draw(textureRegion,
            x - textureRegion.getRegionWidth() / 2.0F,
            y - textureRegion.getRegionHeight() / 2.0F,
            textureRegion.getRegionWidth() / 2.0F,
            textureRegion.getRegionHeight() / 2.0F,
            textureRegion.getRegionWidth(),
            textureRegion.getRegionHeight(),
            Settings.scale * zoomEffect,
            Settings.scale * zoomEffect,
            0.0F);
      }
    }
  }
}
