package com.kalbomat.twozerofoureight.client;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.kalbomat.twozerofoureight.client.AboutWindow.AboutCloseHandler;
import com.kalbomat.twozerofoureight.client.GameHeader.GameHeaderHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GwtTwoZeroFourEight implements EntryPoint 
{
	private static final String UNSUPPORTED_BROWSER = "Your browser does not support the HTML5 Canvas";
	public static final int GAME_WIDITH = 500;
	public static final int PADDING_WIDTH = 12;
	private static final CssColor BOARD_BACKGROUND_COLOR = CssColor.make("#bbada0");
	private static final int[] CARD_COORDINATES = {12, 134, 256, 378}; //{ padding_width, 2 * padding_width + GameCard.card_widith, 3 * padding_width + 2 * GameCard.card_widith, 4 * padding_width + 3 * GameCard.card_widith };
	public static final int ANIMATION_STEPS = 5;
	private static final int REFRESH_PERIOD = 20;
	private static final String SCORE_LOCAL_STORAGE_KEY = "gwttwozerofoureightscore";
	private static final String BEST_VAL_LOCAL_STORAGE_KEY = "gwttwozerofoureightbestvalue";
	private static final String BOARD_LOCAL_STORAGE_KEY = "gwttwozerofoureightboard";
	
	private FocusPanel fp;
	private Canvas canvas;
	private Context2d context;	
	private RoundSquare background;
	private GameCard[][] gameCards;
	private Storage localStorage;
	private IHeaderWidget gameHeader;
	
	private int score = 0;
	private int bestScore = 0;
	private boolean generateNewCard = false;
	private boolean checkForEndOftheGame = false;
	private boolean running = false;
	private Timer timer;
	
	private Anchor about;
	private AboutWindow aboutWindow;

	private StringBuilder sbBoard = new StringBuilder();
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() 
	{
		canvas = Canvas.createIfSupported();
		if (canvas == null)
		{
			// If not supported then show a message
			RootPanel.get().add(new Label(UNSUPPORTED_BROWSER));
			return;
		}
		
		localStorage = Storage.getLocalStorageIfSupported();
		
		canvas.setWidth(GAME_WIDITH + "px");
		canvas.setHeight(GAME_WIDITH + "px");
		canvas.setCoordinateSpaceWidth(GAME_WIDITH);
		canvas.setCoordinateSpaceHeight(GAME_WIDITH);
		
		//add canvas to FocusPanel and then add handlers to FocusPanel
		fp = new FocusPanel();
		fp.setWidth(GAME_WIDITH + "px");
		fp.add(canvas);
		
		gameHeader = new GameHeader();
		aboutWindow = new AboutWindow();
		about = new Anchor("About");
		
		RootPanel.get("headerContainer").add(gameHeader);
		RootPanel.get("gameContainer").add(fp);
		RootPanel.get("descriptionContainer").add(about);
		
		context = canvas.getContext2d();
		
		//init handlers
		initHandlers();
		
		//init objects
		initialSettings();
		
		//read from localStore if exists
		if(localStorage != null) initBoardDataFromLocalStore();
		
		if(gameHeader.isAnimation())
		{
			start();
		}
		else
		{
			doMove();
		}
				
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() 
		{
			@Override
			public void execute() 
			{
				fp.setFocus(true);
			}
		});
	}
	
	private void initialSettings()
	{
		// init objects
		background = new RoundSquare(new Position(), GAME_WIDITH, BOARD_BACKGROUND_COLOR);
		gameCards = new GameCard[4][4];
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				gameCards[i][j] = new GameCard(new Position(CARD_COORDINATES[i], CARD_COORDINATES[j]));
			}
		}

		generateNewCard = true;
		checkForEndOftheGame = true;
		
		gameHeader.setScore(score = 0);
	}
	
	private void initBoardDataFromLocalStore()
	{
		String strBest = localStorage.getItem(BEST_VAL_LOCAL_STORAGE_KEY);
		if(strBest != null && isNumeric(strBest))
		{
			bestScore = Integer.parseInt(strBest);
			gameHeader.setBestScore(bestScore);
		}
		
		String strScore = localStorage.getItem(SCORE_LOCAL_STORAGE_KEY);
		if(strScore != null && isNumeric(strScore))
		{
			score = Integer.parseInt(strScore);
			gameHeader.setScore(score);
		}
		
		if(localStorage.getItem(BOARD_LOCAL_STORAGE_KEY) != null)
		{
			String[] lsBoard = localStorage.getItem(BOARD_LOCAL_STORAGE_KEY).split(";");
			if(lsBoard.length == 16)
			{
				for(int k=0; k<16; k++)
				{
					if(isNumeric(lsBoard[k]))
					{
						int i = k / 4;
						int j = k % 4;
						
						gameCards[i][j].setValue(Integer.parseInt(lsBoard[k]));
						if(gameHeader.isAnimation()) gameCards[i][j].prepareForNewCard();
						generateNewCard = false;
					}
				}
			}
		}
	}
	
	private void start()
	{
		running = true;
		timer = new Timer()
		{
			int i=0;
			@Override
			public void run()
			{
				if(i < ANIMATION_STEPS)
				{
					doUpdate(AnimationUpdateType.UPDATE);
					drawGame();
				}
				else if(i >= ANIMATION_STEPS && i < 2 * ANIMATION_STEPS)
				{
					doUpdate(AnimationUpdateType.FADEOUTUPDATE);
					drawGame();
				}
				else if(i >= 2 * ANIMATION_STEPS && i < 3 * ANIMATION_STEPS)
				{
					//wait
				}
				else if(i >= 3 * ANIMATION_STEPS && i < 4 * ANIMATION_STEPS)
				{
					if(generateNewCard) generateRandomCard();
					
					doUpdate(AnimationUpdateType.NEWCARDUPDATE);
					drawGame();
				}
				else
				{					
					stop();
				}
				i++;
			}
		};
		timer.scheduleRepeating(REFRESH_PERIOD);
	}
	
	private void stop()
	{
		if(timer != null) timer.cancel();
		
		doMove();
	}
	
	private void doMove()
	{
		if(generateNewCard) generateRandomCard();
		clear();
		drawGame();
		
		if(localStorage != null) 
		{
			localStorage.setItem(BOARD_LOCAL_STORAGE_KEY, createStringFromBoardValues());
			localStorage.setItem(SCORE_LOCAL_STORAGE_KEY, Integer.toString(score));			
		}
		
		generateNewCard = false;	
		running = false;
		
		if (endOfTheGame())
		{
			drawEndOfTheGameCover();
		}
	}
	
	private void doUpdate(AnimationUpdateType animationUpdateType)
	{
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				gameCards[i][j].update(animationUpdateType);
			}
		}
	}
	
	private void drawGame()
	{
		background.draw(context);
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				gameCards[i][j].draw(context);	
			}
		}
	}
	
	private void clear()
	{
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				gameCards[i][j].afterUpdate();		
				gameCards[i][j].setCombined(false);
				gameCards[i][j].setPreviousPosition(null);
				gameCards[i][j].setStep(null);
				gameCards[i][j].setNewCard(false);
			}
		}
	}
	
	private void generateRandomCard()
	{
		// find all empty positions
		List<GameCard> emptyCards = new ArrayList<GameCard>();
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				if (gameCards[i][j].getValue() == 0)
				{
					emptyCards.add(gameCards[i][j]);
				}
			}
		}
		// generate rand num
		if (!emptyCards.isEmpty()) 
		{
			int r1 = Random.nextInt(emptyCards.size());
			
			// generate two initial cards if empty board
			if(emptyCards.size() == 16)
			{
				int r2 = r1;
				
				do
				{
					r2 = Random.nextInt(emptyCards.size());
				} while (r2 == r1);
				
				emptyCards.get(r2).setValue(Math.random() < 0.9 ? 2 : 4);
				if(gameHeader.isAnimation()) emptyCards.get(r2).prepareForNewCard();
			}

			emptyCards.get(r1).setValue(Math.random() < 0.9 ? 2 : 4);
			if(gameHeader.isAnimation()) emptyCards.get(r1).prepareForNewCard();
			
			if(emptyCards.size() == 1) checkForEndOftheGame = true; 
		}
		else
		{
			checkForEndOftheGame = true;
		}

		generateNewCard = false;
	}
	
	private void createMove(Direction direction)
	{
		if(running) stop();
		
		if (endOfTheGame())
		{
			drawEndOfTheGameCover();
			running = false;
			generateNewCard = false;
			return;
		}

		GameCard[] rc = new GameCard[4];
		
		for (int j = 0; j < 4; j++)
		{
			if(direction == Direction.RIGHT || direction == Direction.LEFT)
			{
				for (int i = 0; i < 4; i++)
				{
					rc[i] = gameCards[i][j];
				}
			}
			else
			{
				rc = gameCards[j];
			}
			
			rowColumnMove(rc, direction);
		}

		gameHeader.setScore(score);
		
		if(score > bestScore)
		{
			gameHeader.setBestScore(bestScore = score);
			
			if(localStorage != null)
			{
				localStorage.setItem(BEST_VAL_LOCAL_STORAGE_KEY, Integer.toString(bestScore));
			}
		}
		
		if(gameHeader.isAnimation())
		{
			start();
		}
		else
		{
			doMove();
		}		
	}
	
	private void rowColumnMove(GameCard[] rc, Direction direction)
	{
		for(int m=1; m<4; m++)
		{
			int i = Math.abs(direction.getdMax() - m);
			if (rc[i].getValue() == 0) continue;
			
			for (int p = m - 1; p > -1; p--)
			{
				int k = Math.abs(p - direction.getdMax());
				
				if (rc[k].getValue() != 0 && (rc[i].getValue() != rc[k].getValue() || rc[k].isCombined()))
				{
					if (k + direction.getdOff() != i)
					{
						rc[k + direction.getdOff()].setValue(rc[i].getValue());
						if(gameHeader.isAnimation()) rc[k + direction.getdOff()].setPreviousPosition(rc[i].getPosition());
						rc[i].setValue(0);
						generateNewCard = true;
						break;
					}
					break;
				}
				
				if (rc[k].getValue() != 0 && rc[i].getValue() == rc[k].getValue() && !rc[k].isCombined())
				{
					score += rc[k].getValue() * 2;

					rc[k].setValue(rc[k].getValue() * 2);
					rc[k].setCombined(true);
					if(gameHeader.isAnimation()) rc[k].setPreviousPosition(rc[i].getPosition());
					rc[i].setValue(0);
					generateNewCard = true;
					break;
				}
				
				if (k == direction.getdMax())
				{
					rc[k].setValue(rc[i].getValue());
					if(gameHeader.isAnimation()) rc[k].setPreviousPosition(rc[i].getPosition());
					rc[i].setValue(0);
					generateNewCard = true;
					break;
				}
			}			
		}
	}
	
	private boolean endOfTheGame()
	{
		if(!checkForEndOftheGame) return false;
		
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				if(gameCards[i][j].getValue() == 0) return false;
				if (j < 3 && gameCards[i][j].getValue() == gameCards[i][j + 1].getValue()) return false;
				if (i < 3 && gameCards[i][j].getValue() == gameCards[i + 1][j].getValue()) return false;
			}
		}
		return true;
	}
	
	private void drawEndOfTheGameCover()
	{
		drawGame();
		RoundSquare endGameCover = new RoundSquare(new Position(), GAME_WIDITH, CssColor.make("rgba(255, 255, 255, 0.60)"));
		endGameCover.draw(context);
		context.setFont("bold 55px arial");
		context.setFillStyle("black");
		context.setTextAlign(TextAlign.CENTER);
		context.setTextBaseline(TextBaseline.MIDDLE);
		context.fillText("Game over!", GAME_WIDITH / 2, GAME_WIDITH / 2, GAME_WIDITH);
	}
	
	private void initHandlers()
	{
		fp.addKeyDownHandler(new KeyDownHandler()
		{
			@Override
			public void onKeyDown(KeyDownEvent event)
			{
				int key = event.getNativeKeyCode();
				switch (key)
				{
				case 38:
					createMove(Direction.UP);
					event.preventDefault();
					break;
				case 40:
					createMove(Direction.DOWN);
					event.preventDefault();
					break;
				case 39:
					createMove(Direction.RIGHT);
					event.preventDefault();
					break;
				case 37:
					createMove(Direction.LEFT);
					event.preventDefault();
					break;
				}
			}
		});
		
		gameHeader.setGameHeaderHandler(new GameHeaderHandler() 
		{			
			@Override
			public void setFocusIfAnimationChange() 
			{
				fp.setFocus(true);
			}
			
			@Override
			public void initBoard() 
			{
				initialSettings();
				fp.setFocus(true);
				if(gameHeader.isAnimation())
				{
					start();
				}
				else
				{
					doMove();
				}
			}
		});
		
		aboutWindow.setAboutCloseHandler(new AboutCloseHandler() 
		{
			@Override
			public void setFocusOnClose() 
			{
				fp.setFocus(true);				
			}
		});
		
		about.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent oevent) 
			{
		        int left = canvas.getAbsoluteLeft();
		        int top =  canvas.getAbsoluteTop();

		        aboutWindow.setPopupPosition(left, top);
		        aboutWindow.setAnimationEnabled(true);
		        aboutWindow.setGlassEnabled(true);
				aboutWindow.show();
			}		    
		});
		
	}
	
	private boolean isNumeric(String num)
	{
		for(int i=0; i<num.length(); i++)
		{
			if(!Character.isDigit(num.charAt(i)))
			{
				return false;
			}
		}
		
		return true;
	}
	
	private String createStringFromBoardValues()
	{
		sbBoard.setLength(0);
		for (int i=0; i<4; i++)
		{
			for(int j=0; j<4; j++)
			{
				sbBoard.append(gameCards[i][j].getValue()).append(";");
			}
		}
		
		return sbBoard.toString();
	}
	
	enum Direction
	{
		UP(0, 1), DOWN(3, -1), LEFT(0, 1), RIGHT(3, -1);
		
		private int dMax;
		private int dOff;
		
		Direction(int dMax, int dOff)
		{
			this.dMax = dMax;
			this.dOff = dOff;
		}

		protected int getdMax()
		{
			return dMax;
		}

		protected int getdOff()
		{
			return dOff;
		}		
	}
	
	enum AnimationUpdateType
	{
		UPDATE, FADEOUTUPDATE, NEWCARDUPDATE;
	}
	
	public interface IHeaderWidget extends IsWidget
	{
		public void setScore(int score);
		public void setBestScore(int bestScore);
		public void setGameHeaderHandler(GameHeaderHandler gameHeaderHandler);
		public boolean isAnimation();
	}
	
	public interface IAboutWidget
	{
		public void setAboutCloseHandler(AboutCloseHandler aboutCloseHandler);
	}
}
