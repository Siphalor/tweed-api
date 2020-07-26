package de.siphalor.tweed.client;

import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.text.Text;

public class CustomNoticeScreen extends NoticeScreen {
	private final Runnable openHandler;

	public CustomNoticeScreen(Runnable openHandler, Runnable closeHandler, Text title, Text notice) {
		super(closeHandler, title, notice);
		this.openHandler = openHandler;
	}

	public CustomNoticeScreen(Runnable openHandler, Runnable closeHandler, Text title, Text notice, Text text) {
		super(closeHandler, title, notice, text);
		this.openHandler = openHandler;
	}

	@Override
	protected void init() {
		openHandler.run();
		super.init();
	}
}
