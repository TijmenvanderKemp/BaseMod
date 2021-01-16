package basemod.helpers;

import basemod.abstracts.CustomAchievement;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.stats.AchievementItem;

public class CustomAchievementBorderTextureLoader {
  private static TextureRegion unlockedBorder;
  private static TextureRegion lockedBorder;

  public static void loadDefaultBorderTextures() {
    if (unlockedBorder == null) {
      unlockedBorder = new TextureRegion(ImageMaster.loadImage("img/achievements/achievementBorder.png"));
    }
    if (lockedBorder == null) {
      lockedBorder = new TextureRegion(ImageMaster.loadImage("img/achievements/lockedBorder.png"));
    }
  }

  /**
   * If this is a base game achievement, or there is no custom border for the achievement set, return the preloaded
   * border. Otherwise, load with the path that's set on the CustomAchievement.
   */
  public static TextureRegion chooseTextureRegion(AchievementItem achievement) {
    if (achievement.isUnlocked) {
      if (achievement instanceof CustomAchievement) {
        CustomAchievement customAchievement = (CustomAchievement) achievement;
        if (customAchievement.getCustomBorderPath() != null) {
          return new TextureRegion(ImageMaster.loadImage(customAchievement.getCustomBorderPath()));
        }
      }
      return unlockedBorder;
    }
    else {
      if (achievement instanceof CustomAchievement) {
        CustomAchievement customAchievement = (CustomAchievement) achievement;
        if (customAchievement.getCustomLockedBorderPath() != null) {
          return new TextureRegion(ImageMaster.loadImage(customAchievement.getCustomLockedBorderPath()));
        }
      }
      return lockedBorder;
    }
  }
}
