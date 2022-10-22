/*
 * Copyright 2021-2022 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.tweed5.tailor.screen;

import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.text.Text;

/**
 * A notice screen that additionally executes a callback when it's opened.
 */
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
