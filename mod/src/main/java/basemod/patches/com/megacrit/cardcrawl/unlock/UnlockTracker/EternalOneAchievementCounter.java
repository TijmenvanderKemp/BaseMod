package basemod.patches.com.megacrit.cardcrawl.unlock.UnlockTracker;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

@SpirePatch(
    cls = "com.megacrit.cardcrawl.unlock.UnlockTracker",
    method = "allAchievementsExceptPlatinumUnlocked"
)
public class EternalOneAchievementCounter {

  public static SpireReturn<Boolean> Prefix() {

    return SpireReturn.Return(
        UnlockTracker.achievementPref.data.keySet().stream()
            .filter(it -> !BaseMod.isModdedAchievement(it))
            .count()
            >= 45
    );
  }
}
