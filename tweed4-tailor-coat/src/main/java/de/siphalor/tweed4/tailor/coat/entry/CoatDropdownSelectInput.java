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

package de.siphalor.tweed4.tailor.coat.entry;

import de.siphalor.coat.input.ConfigInput;
import de.siphalor.coat.input.InputChangeListener;
import de.siphalor.coat.list.entry.ConfigListConfigEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Function;

public class CoatDropdownSelectInput<V> implements ConfigInput<V> {
	private static final int MAX_VISIBLE_LINES = 5;

	private V value;
	private final V[] options;
	private final Function<V, Text> valueConverter;
	private final ButtonWidget button;
	private boolean expanded = false;
	private int scroll;
	private ConfigListConfigEntry<V> parent;
	private InputChangeListener<V> changeListener;

	public CoatDropdownSelectInput(V value, V[] options, Function<V, Text> valueConverter) {
		this.options = options;
		this.valueConverter = valueConverter;
		button = ButtonWidget.builder(Text.empty(), button_ -> {
			expanded = !expanded;
			scroll = 0;
			if (parent != null) {
				parent.entryHeightChanged(this);
			}
		}).size(20, 20).build();

		setValue(value);
	}

	@Override
	public int getHeight() {
		if (expanded) {
			return button.getHeight() + 5 + getOptionsVisibleLength() * 14;
		}
		return button.getHeight() + 2;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public void setValue(V value) {
		this.value = value;
		if (value == null) {
			button.setMessage(Text.translatable("tweed4.cloth.dropdown.empty"));
		} else {
			button.setMessage(valueConverter.apply(value));
		}
		if (changeListener != null) {
			changeListener.inputChanged(value);
		}
	}

	public void setParent(ConfigListConfigEntry<V> parent) {
		this.parent = parent;
	}

	@Override
	public void setChangeListener(InputChangeListener<V> changeListener) {
		this.changeListener = changeListener;
	}

	@Override
	public boolean isFocused() {
		return button.isFocused();
	}

	@Override
	public void setFocused(boolean focused) {
		if (button.isFocused() != focused) {
			button.active = true;
			button.setFocused(true);
		}
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(DrawContext drawContext, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		button.setX(x);
		button.setY(y);
		button.setWidth(width);
		button.render(drawContext, mouseX, mouseY, tickDelta);

		if (expanded) {
			int length = getOptionsVisibleLength();
			int top = button.getY() + button.getHeight();
			int right = button.getX() + button.getWidth();
			int height = length * 14;

			int mousePos = -1;
			if (mouseX >= button.getX() && mouseX < right) {
				mousePos = (mouseY - top - 3) / 14 + scroll;
			}

			drawContext.fill(button.getX(), top, button.getX() + button.getWidth(), top + height, 0xff000000);
			if (options.length > length) {
				int l = options.length - 1;
				drawContext.fill(right - 3, top + scroll * height / l, right, top + (MAX_VISIBLE_LINES + scroll) * height / l, 0xffbbbbbb);
			}

			for (int i = scroll; i < scroll + length; i++) {
				drawContext.drawText(
						MinecraftClient.getInstance().textRenderer,
						valueConverter.apply(options[i]),
						button.getX() + 3, top + (i - scroll) * 14 + 3,
						mousePos == i ? 0xffffffff : 0xffbbbbbb,
						false
				);
			}
		}
	}

	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {

	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= button.getX() && mouseX < button.getX() + button.getWidth()
				&& mouseY >= button.getY() && mouseY < button.getY() + getHeight();
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (
				mouseY >= button.getY() + button.getHeight() && mouseY < button.getY() + getHeight()
						&& mouseX >= button.getX() && mouseX < button.getX() + button.getWidth()
		) {
			int newScroll = scroll - (int) verticalAmount;
			scroll = MathHelper.clamp(newScroll, 0, Math.max(0, options.length - MAX_VISIBLE_LINES - 1));
			return newScroll == scroll;
		}
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (mouseY >= button.getY()) {
			if (expanded && mouseX >= button.getX() && mouseX < button.getX() + button.getWidth()) {
				int pos = (int) ((mouseY - (button.getY() + button.getHeight() + 3)) / 14);
				if (pos >= 0 && pos < MAX_VISIBLE_LINES) {
					pos += scroll;
					setValue(options[pos]);
					button.onPress();
					MinecraftClient.getInstance().getSoundManager().play(
							PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1F)
					);
					return true;
				}
			}
			return button.mouseClicked(mouseX, mouseY, mouseButton);
		} else {
			expanded = false;
		}
		return false;
	}

	public int getOptionsVisibleLength() {
		return Math.min(MAX_VISIBLE_LINES, options.length);
	}
}
