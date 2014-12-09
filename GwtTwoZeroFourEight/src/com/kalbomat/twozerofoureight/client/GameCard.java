package com.kalbomat.twozerofoureight.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.canvas.dom.client.CssColor;
import com.kalbomat.twozerofoureight.client.GwtTwoZeroFourEight.AnimationUpdateType;

public class GameCard extends RoundSquare 
{
	public static final int CARD_WIDTH = 110;
	public static Map<Integer, GameCardSetting> gameCardSettingsMap = createGameCartSettings();
	private static final Position NEW_CARD_POSITION = new Position(25, 25);
	private static final Position ANIMATION_EXPAND = new Position(1, 1);
	private static final Position ANIMATION_NEW_CARD = new Position(5, 5);
	
	private int value;
	private boolean combined;
	private boolean newCard;
	private Position previousPosition;
	private Position step;

	public GameCard(Position position)
	{
		super(position, CARD_WIDTH, gameCardSettingsMap.get(0).cssColor);
		this.value = 0;
	}

	public int getValue() 
	{
		return value;
	}

	public void setValue(int value) 
	{
		this.value = value;
		update(gameCardSettingsMap.get(value > 2048 ? 2048 : value).cssColor);
	}

	public boolean isCombined()
	{
		return combined;
	}

	public void setCombined(boolean combined)
	{
		this.combined = combined;
	}

	public void setNewCard(boolean newCard)
	{
		this.newCard = newCard;
	}
	
	public void setStep(Position step)
	{
		this.step = step;
	}
	
	public Position getPreviousPosition()
	{
		return previousPosition;
	}

	public void setPreviousPosition(Position previousPosition)
	{
		if(previousPosition != null)
		{
			this.previousPosition = new Position(previousPosition);
			
			//set up the step
			step = Position.sub(getPosition(), getPreviousPosition());
			step.mult(1.0/GwtTwoZeroFourEight.ANIMATION_STEPS);
		}
		else
		{
			this.previousPosition = null;
		}
	}
	
	public void update(AnimationUpdateType animationUpdateType)
	{
		switch (animationUpdateType)
		{
		case TRANSITION:
			if(previousPosition != null) 
			{
				previousPosition.add(step);
			}
			break;
			
		case EXPAND:
			if(combined) 
			{
				previousPosition.sub(ANIMATION_EXPAND);
				update(getPosition(), getWidth() + 2);
			}
			break;
			
		case NEWCARDUPDATE:
			if(newCard)
			{
				getPosition().sub(ANIMATION_NEW_CARD);
				update(getPosition(), getWidth() + 10);
			}
			break;
		}		
	}
	
	public void afterUpdate()
	{
		if(combined) update(getPosition(), CARD_WIDTH);
		if(newCard) update(getPreviousPosition(), CARD_WIDTH); //put cards in right place if arrow keys pressed to fast
	}
	
	public void prepareForNewCard()
	{
		if(getValue() != 0)
		{
			this.newCard = true;
			setPreviousPosition(getPosition());
			getPosition().add(NEW_CARD_POSITION);
			update(getPosition(), CARD_WIDTH - 50);
		}		
	}
	
	@Override
	public void draw(Context2d context) 
	{
		double x = 0, y = 0, w = 0; //for value fillText
		
		if(this.previousPosition == null || this.newCard)
		{
			super.draw(context);
			
			if(value != 0)
			{
				x = getPosition().getX() + getWidth() / 2;
				y = getPosition().getY() + getWidth() / 2;
				w = getWidth() - 2 * GwtTwoZeroFourEight.PADDING_WIDTH;
			}
		}
		else
		{
			context.setFillStyle(getColour());
			context.beginPath();
			context.moveTo(previousPosition.getX() + getR(), previousPosition.getY());
			context.lineTo(previousPosition.getX() + getWidth() - getR(), previousPosition.getY());
			context.quadraticCurveTo(previousPosition.getX() + getWidth(), previousPosition.getY(), previousPosition.getX() + getWidth(), previousPosition.getY() + getR());
			context.lineTo(previousPosition.getX() + getWidth(), previousPosition.getY() + getWidth() - getR());
			context.quadraticCurveTo(previousPosition.getX() + getWidth(), previousPosition.getY() + getWidth(), previousPosition.getX() + getWidth() - getR(), previousPosition.getY() + getWidth());
			context.lineTo(previousPosition.getX() + getR(), previousPosition.getY() + getWidth());
			context.quadraticCurveTo(previousPosition.getX(), previousPosition.getY() + getWidth(), previousPosition.getX(), previousPosition.getY() + getWidth() - getR());
			context.lineTo(previousPosition.getX(), previousPosition.getY() + getR());
			context.quadraticCurveTo(previousPosition.getX(), previousPosition.getY(), previousPosition.getX() + getR(), previousPosition.getY());
			context.fill();
			
			if(value != 0)
			{
				x = getPreviousPosition().getX() + getWidth() / 2;
				y = getPreviousPosition().getY() + getWidth() / 2;
				w = getWidth() - 2 * GwtTwoZeroFourEight.PADDING_WIDTH;
			}
		}
		
		if(value != 0)
		{
			context.setFont(gameCardSettingsMap.get(value > 2048 ? 2048 : value).fontSettings);
			context.setFillStyle(gameCardSettingsMap.get(value > 2048 ? 2048 : value).fontColor);
			context.setTextAlign(TextAlign.CENTER);
			context.setTextBaseline(TextBaseline.MIDDLE);
			context.fillText(Integer.toString(value), x, y, w);
		}
		
	}
	
	private static Map<Integer, GameCardSetting> createGameCartSettings()
	{
		Map<Integer, GameCardSetting> settings = new HashMap<Integer, GameCard.GameCardSetting>();
		settings.put(0, new GameCardSetting(CssColor.make("rgba(238, 228, 218, 0.35)"), "bold 55px arial", "black"));
		settings.put(2, new GameCardSetting(CssColor.make("#eee4da"), "bold 55px arial", "black"));
		settings.put(4, new GameCardSetting(CssColor.make("#ede0c8"), "bold 55px arial", "black"));
		settings.put(8, new GameCardSetting(CssColor.make("#f2b179"), "bold 55px arial", "white"));
		settings.put(16, new GameCardSetting(CssColor.make("#f59563"), "bold 55px arial", "white"));
		settings.put(32, new GameCardSetting(CssColor.make("#f67c5f"), "bold 55px arial", "white"));
		settings.put(64, new GameCardSetting(CssColor.make("#f65e3b"), "bold 55px arial", "white"));
		settings.put(128, new GameCardSetting(CssColor.make("#edcf72"), "bold 45px arial", "white"));
		settings.put(256, new GameCardSetting(CssColor.make("#edcc61"), "bold 45px arial", "white"));
		settings.put(512, new GameCardSetting(CssColor.make("#edc850"), "bold 45px arial", "white"));
		settings.put(1024, new GameCardSetting(CssColor.make("#edc53f"), "bold 35px arial", "white"));
		settings.put(2048, new GameCardSetting(CssColor.make("#edc22e"), "bold 30px arial", "white"));

		return settings;
	}
	
	
	private static class GameCardSetting
	{
		private CssColor cssColor;
		private String fontSettings;
		private String fontColor;

		public GameCardSetting(CssColor cssColor, String fontSettings, String fontColor) 
		{
			this.cssColor = cssColor;
			this.fontSettings = fontSettings;
			this.fontColor = fontColor;
		}
	}
}