package basemod.helpers;

import basemod.BaseMod;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomAchievementUnlocker {
  private static final Logger LOGGER = LogManager.getLogger(CustomAchievementUnlocker.class);

  public static void unlockAchievement(String key) {
    if (UnlockTracker.achievementPref.getBoolean(key, false)) {
      // Player already has the achievement
      return;
    }
    if (!BaseMod.isModdedAchievement(key)) {
      LOGGER.info(String.format("This is not a modded achievement, ignoring it in %s",
          CustomAchievementUnlocker.class.getSimpleName()));
      return;
    }
    UnlockTracker.achievementPref.putBoolean(key, true);
    LOGGER.info(String.format("Achievement Unlocked: %s", key));
    UnlockTracker.achievementPref.flush();
  }
}
