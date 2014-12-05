package com.kalbomat.twozerofoureight.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.kalbomat.twozerofoureight.client.GwtTwoZeroFourEight.IAboutWidget;

public class AboutWindow extends PopupPanel implements IAboutWidget
{
	private AboutCloseHandler aboutCloseHandler;
	
	public AboutWindow()
	{
		super(false);
		
		setWidget(createWidget());
	}
	
	private VerticalPanel createWidget()
	{
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth(468 + "px");
		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		vp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		StringBuilder sb = new StringBuilder();
		sb.append("<b>HOW TO PLAY</b>").append("<br>");
		sb.append("Use arrow keys to merge the numbers.").append("<br>").append("<br>");
		
		sb.append("<b>ABOUT 2048</b>").append("<br>");
		sb.append("This popular and highly addictive game 2048 was created by Gabriele Cirulli (<a href='http://gabrielecirulli.com/' target='_blank'> gabrielecirulli.com </a>)").append("<br>").append("<br>");
		
		sb.append("<b>ABOUT THIS SITE</b>").append("<br>");
		sb.append("On this site yo can find GWT (Google Web Toolkit) implementation of the game 2048. It was mainly writen because of the fun and as a learning process of Html5 support in GWT.").append("<br>").append("<br>");
		
		sb.append("You can find source code <a href='https://github.com/kalbomat/gwt2048' target='_blank'> here </a>.").append("<br>").append("<br>");
		
		HTML h0 = new HTML(sb.toString());
		vp.add(h0);

		Button closeButton = new Button("Close");
		vp.add(closeButton);
		closeButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{				
				hide();
				aboutCloseHandler.setFocusOnClose();
			}
		});
		
		return vp;
	}
	
	public interface AboutCloseHandler
	{
		public void setFocusOnClose();
	}

	@Override
	public void setAboutCloseHandler(AboutCloseHandler aboutCloseHandler) 
	{		
		this.aboutCloseHandler = aboutCloseHandler;
	}
}
