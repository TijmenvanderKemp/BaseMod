package basemod.test.achievements;

import basemod.abstracts.CustomAchievement;
import basemod.helpers.AchievementJSON;

public class StrikeAchievement extends CustomAchievement {
  public static final String ID = "testMod:achievement1";
  public StrikeAchievement() {
    super(ID, AchievementJSON.fromJson("achievements/achievement-1.json"));
  }
}
