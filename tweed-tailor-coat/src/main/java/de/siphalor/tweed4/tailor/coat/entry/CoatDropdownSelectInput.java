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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Function;

import static net.minecraft.client.gui.DrawableHelper.fill;

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
		button = new ButtonWidget(0,0, 20, 20, "", button_ -> {
			expanded = !expanded;
			scroll = 0;
			if (parent != null) {
				parent.entryHeightChanged(this);
			}
		});

		setValue(value);
	}

	@Override
	public int getHeight() {
		if (expanded) {
			return 20 + 5 + getOptionsVisibleLength() * 14;
		}
		return 20 + 2;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public void setValue(V value) {
		this.value = value;
		if (value == null) {
			button.setMessage(I18n.translate("tweed4.cloth.dropdown.empty"));
		} else {
			button.setMessage(valueConverter.apply(value).asFormattedString());
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
	public void setFocused(boolean focused) {
		if (button.isFocused() != focused) {
			button.active = true;
			button.changeFocus(true);
		}
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		button.x = x;
		button.y = y;
		button.setWidth(width);
		button.render(mouseX, mouseY, tickDelta);

		if (expanded) {
			int length = getOptionsVisibleLength();
			int top = button.y + 20;
			int right = button.x + button.getWidth();
			int height = length * 14;

			int mousePos = -1;
			if (mouseX >= button.x && mouseX < right) {
				mousePos = (mouseY - top - 3) / 14 + scroll;
			}

			fill(button.x, top, button.x + button.getWidth(), top + height, 0xff000000);
			if (options.length > length) {
				int l = options.length - 1;
				fill(right - 3, top + scroll * height / l, right, top + (MAX_VISIBLE_LINES + scroll) * height / l, 0xffbbbbbb);
			}

			for (int i = scroll; i < scroll + length; i++) {
				MinecraftClient.getInstance().textRenderer.draw(valueConverter.apply(options[i]).asFormattedString(), button.x + 3F, top + (i - scroll) * 14 + 3,
						mousePos == i ? 0xffffffff : 0xffbbbbbb
				);
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {

	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= button.x && mouseX < button.x + button.getWidth()
				&& mouseY >= button.y && mouseY < button.y + getHeight();
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (
				mouseY >= button.y + 20 && mouseY < button.y + getHeight()
						&& mouseX >= button.x && mouseX < button.x + button.getWidth()
		) {
			int newScroll = scroll - (int) amount;
			scroll = MathHelper.clamp(newScroll, 0, Math.max(0, options.length - MAX_VISIBLE_LINES - 1));
			return newScroll == scroll;
		}
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (mouseY >= button.y) {
			if (expanded && mouseX >= button.x && mouseX < button.x + button.getWidth()) {
				int pos = (int) ((mouseY - (button.y + 20 + 3)) / 14);
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
