package basemod.test.achievements;

import basemod.abstracts.CustomAchievement;
import basemod.helpers.AchievementJSON;

public class StrikeAchievementCustomBorder extends CustomAchievement {
  public static final String ID = "testMod:achievement2";
  public StrikeAchievementCustomBorder() {
    super(ID, AchievementJSON.fromJson("achievements/achievement-1.json"));
    setCustomBorderPath("achievements/customBorder.png");
    setCustomLockedBorderPath("achievements/customLockedBorder.png");
  }
}
