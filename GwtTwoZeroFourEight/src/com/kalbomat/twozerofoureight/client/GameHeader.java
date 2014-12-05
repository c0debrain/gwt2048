package com.kalbomat.twozerofoureight.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.kalbomat.twozerofoureight.client.GwtTwoZeroFourEight.IHeaderWidget;

public class GameHeader implements IHeaderWidget
{
	private HorizontalPanel hp = new HorizontalPanel();
	
	private Label lblScore;
	private Label lblBest;
	
	private CheckBox animation;
	private Button newGameBtn;
	private GameHeaderHandler gameHeaderHandler;
	
	public GameHeader()
	{
		newGameBtn = new Button("New game");
		newGameBtn.addClickHandler(new ClickHandler() 
		{
		    public void onClick(ClickEvent event) 
		    {
		    	gameHeaderHandler.initBoard();
		    }
		});
		lblScore = new Label("0");
		lblScore.addStyleName("scoreLbl");
		lblBest = new Label("0");
		lblBest.addStyleName("scoreLbl");
		animation = new CheckBox("Animation");
		animation.setValue(true);
		animation.addStyleName("scoreLbl");
		animation.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				gameHeaderHandler.setFocusIfAnimationChange();
			}			
		});
		
		hp.setWidth(GwtTwoZeroFourEight.GAME_WIDITH + "px");
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		Label name = new Label("2048");
		name.addStyleName("nameLable");
		
		Label description = new Label("implemented with GWT");
		description.addStyleName("descriptionLable");
		
		VerticalPanel vp1 = new VerticalPanel();
		vp1.setWidth(200 + "px");
		vp1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		vp1.add(name);
		vp1.add(description);
		hp.add(vp1);
		
		VerticalPanel vp2 = new VerticalPanel();
		vp2.setWidth(300 + "px");
		vp2.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.setWidth(300 + "px");
		hp2.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		hp2.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		VerticalPanel vp3 = new VerticalPanel();
		vp3.setWidth(150 + "px");
		vp3.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Label lblScoreName = new Label("SCORE");
		lblScoreName.addStyleName("scoreNameLbl");
		vp3.add(lblScoreName);
		vp3.add(lblScore);	
		hp2.add(vp3);
		
		VerticalPanel vp4 = new VerticalPanel();
		vp4.setWidth(150 + "px");
		vp4.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Label lblBesteName = new Label("BEST");
		lblBesteName.addStyleName("scoreNameLbl");
		vp4.add(lblBesteName);
		vp4.add(lblBest);
		hp2.add(vp4);
		vp2.add(hp2);
		
		HorizontalPanel hp3 = new HorizontalPanel();
		hp3.setWidth(300 + "px");
		hp3.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		hp3.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp3.add(newGameBtn);
		hp3.add(animation);
		
		vp2.add(hp3);		
		hp.add(vp2);
	}
	
	@Override
	public Widget asWidget() 
	{		
		return hp;
	}

	@Override
	public void setScore(int score) 
	{
		lblScore.setText(Integer.toString(score));
	}

	@Override
	public void setBestScore(int bestScore) 
	{
		lblBest.setText(Integer.toString(bestScore));
	}
	
	@Override
	public void setGameHeaderHandler(GameHeaderHandler gameHeaderHandler) 
	{
		this.gameHeaderHandler = gameHeaderHandler;
	}
	
	public interface GameHeaderHandler
	{
		public void initBoard();
		public void setFocusIfAnimationChange();
	}

	@Override
	public boolean isAnimation() 
	{
		return animation.getValue();
	}
}