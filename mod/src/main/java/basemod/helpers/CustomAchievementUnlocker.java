package basemod.helpers;

import basemod.BaseMod;
import com.megacrit.cardcrawl.screens.stats.AchievementItem;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class CustomAchievementUnlocker {
  private static final Logger LOGGER = LogManager.getLogger(CustomAchievementUnlocker.class);
  private static HashMap<String, AchievementItem> achievements = new HashMap<>();

  public static void unlockAchievement(String key) {
    if (UnlockTracker.achievementPref.getBoolean(key, false)) {
      // Player already has the achievement
      return;
    }
    if (!BaseMod.isModdedAchievement(key)) {
      LOGGER.info("{} is not a modded achievement, ignoring it in {}",
          key, CustomAchievementUnlocker.class.getSimpleName());
      return;
    }
    UnlockTracker.achievementPref.putBoolean(key, true);
    LOGGER.info("Achievement Unlocked: {}", key);
    UnlockTracker.achievementPref.flush();

    CustomAchievementPopupRenderer.addAchievementToRenderQueue(achievements.get(key));
  }

  public static void register(AchievementItem achievement) {
    achievements.put(achievement.key, achievement);
  }
}
