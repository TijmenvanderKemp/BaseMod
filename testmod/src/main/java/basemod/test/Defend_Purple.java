package basemod.test;

import basemod.abstracts.CustomCard;
import basemod.devcommands.unlock.Unlock;
import basemod.test.achievements.StrikeAchievement;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class Defend_Purple extends CustomCard {
	public static final String ID = "Defend_P";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 1;
	private static final int BLOCK_AMT = 5;
	private static final int UPGRADE_PLUS_BLOCK = 3;

	public Defend_Purple() {
		super(ID, NAME, TestMod.makePath(TestMod.ASSET_FOLDER, TestMod.DEFEND_PURPLE), COST, DESCRIPTION, AbstractCard.CardType.SKILL,
				ColorEnumPatch.PURPLE, AbstractCard.CardRarity.BASIC, AbstractCard.CardTarget.SELF);

		this.baseBlock = BLOCK_AMT;
	}

	public void use(AbstractPlayer p, AbstractMonster m) {
		UnlockTracker.achievementPref.putBoolean(StrikeAchievement.ID, false);
		UnlockTracker.achievementPref.flush();
		if (com.megacrit.cardcrawl.core.Settings.isDebug) {
			AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, 50));
		} else {
			AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
		}
	}

	public AbstractCard makeCopy() {
		return new Defend_Purple();
	}

	public void upgrade() {
		if (!this.upgraded) {
			upgradeName();
			upgradeBlock(UPGRADE_PLUS_BLOCK);
		}
	}

	public boolean isDefend() {
		return true;
	}
}