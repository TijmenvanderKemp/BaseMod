package basemod.patches.com.megacrit.cardcrawl.unlock.UnlockTracker;

import basemod.helpers.CustomAchievementUnlocker;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(
    cls = "com.megacrit.cardcrawl.unlock.UnlockTracker",
    method = "unlockAchievement"
)
public class UnlockCustomAchievementPatch {

  public static void Prefix(String key) {
    CustomAchievementUnlocker.unlockAchievement(key);
  }
}
