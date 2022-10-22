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

package de.siphalor.tweed5.tailor.cloth;

import com.google.common.collect.ImmutableList;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClothDropdownSelectEntry<V> extends TooltipListEntry<V> {
	private static final int MAX_VISIBLE_LINES = 5;
	private V value;
	private final V originalValue;
	private final Supplier<V> defaultValue;
	private final Consumer<V> saveConsumer;
	private final Function<V, Text> valueConverter;
	private final V[] options;
	private final ButtonWidget mainButton;
	private final ButtonWidget resetButton;
	private boolean optionsVisible = false;
	private int scrollOffset = 0;

	public ClothDropdownSelectEntry(Text fieldName, V originalValue, Text resetButtonText, Supplier<Optional<Text[]>> tooltipSupplier, boolean requiresRestart, Supplier<V> defaultValue, Consumer<V> saveConsumer, List<V> options, Function<V, Text> valueConverter) {
		super(fieldName, tooltipSupplier, requiresRestart);
		this.originalValue = originalValue;
		this.defaultValue = defaultValue;
		this.saveConsumer = saveConsumer;
		//noinspection unchecked
		this.options = (V[]) options.toArray();
		this.valueConverter = valueConverter;

		mainButton = new ButtonWidget(0, 0, 150, 20, resetButtonText, button -> {
			optionsVisible = !optionsVisible;
			scrollOffset = 0;
		});
		resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getWidth(resetButtonText) + 6, 20, resetButtonText, button -> {
			setValue(getDefaultValue().orElse(null));
		});

		setValue(originalValue);
	}

	public void setValue(V value) {
		this.value = value;
		if (value == null) {
			mainButton.setMessage(new TranslatableText("tweed4.cloth.dropdown.empty"));
		} else {
			mainButton.setMessage(valueConverter.apply(value));
		}
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public Optional<V> getDefaultValue() {
		return defaultValue == null ? Optional.empty() : Optional.ofNullable(defaultValue.get());
	}

	@Override
	public void save() {
		if (saveConsumer != null) {
			saveConsumer.accept(getValue());
		}
	}

	@Override
	public List<? extends Element> children() {
		return ImmutableList.of(mainButton, resetButton);
	}

	@Override
	public boolean isEdited() {
		return super.isEdited() || !Objects.equals(value, originalValue);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (
				mouseY >= mainButton.y + mainButton.getHeight() && mouseY < mainButton.y + mainButton.getHeight() + getOptionsVisibleLength() * 14
				&& mouseX >= mainButton.x && mouseX < mainButton.x + mainButton.getWidth()
		) {
			scrollOffset = MathHelper.clamp(scrollOffset - (int) amount, 0, Math.max(0, options.length - MAX_VISIBLE_LINES - 1));
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	public Element getFocused() {
		return null;
	}

	@Override
	public void setFocused(@Nullable Element focused) {

	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (mouseY >= mainButton.y && mouseY < mainButton.y + getItemHeight()) {
			if (optionsVisible && mouseX >= mainButton.x && mouseX < mainButton.x + mainButton.getWidth()) {
				int pos = (int) ((mouseY - getOptionsTop()) / 14);
				if (pos >= 0 && pos < MAX_VISIBLE_LINES) {
					pos += scrollOffset;
					setValue(options[pos]);
					optionsVisible = false;
					return true;
				}
			}
		} else {
			optionsVisible = false;
			return false;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean isDragging() {
		return false;
	}

	@Override
	public void setDragging(boolean dragging) {

	}

	public int getOptionsVisibleLength() {
		return Math.min(MAX_VISIBLE_LINES, options.length);
	}

	@Override
	public int getItemHeight() {
		return super.getItemHeight() + (optionsVisible ? getOptionsVisibleLength() * 14 + 5 : 0);
	}

	private int getOptionsTop() {
		return mainButton.y + mainButton.getHeight() + 3;
	}

	@Override
	public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
		super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
		Window window = MinecraftClient.getInstance().getWindow();

		mainButton.active = isEditable();
		resetButton.active = isEditable() && getDefaultValue().map(def -> !def.equals(value)).orElse(false);

		mainButton.y = y;
		resetButton.y = y;

		Text displayedFieldName = getDisplayedFieldName();
		if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
			MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName, window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName), y + 6, getPreferredTextColor());
			resetButton.x = x;
			mainButton.x = x + resetButton.getWidth() + 1;
		} else {
			MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName, x, y + 6, getPreferredTextColor());
			resetButton.x = x + entryWidth - resetButton.getWidth();
			mainButton.x = x + entryWidth - 150 + 1;
		}
		mainButton.setWidth(150 - resetButton.getWidth() - 4);

		mainButton.render(matrices, mouseX, mouseY, delta);
		resetButton.render(matrices, mouseX, mouseY, delta);

		if (optionsVisible) {
			int length = getOptionsVisibleLength();
			int top = mainButton.y + mainButton.getHeight();
			int right = mainButton.x + mainButton.getWidth();
			int height = length * 14;

			int mousePos = -1;
			if (mouseX >= mainButton.x && mouseX < right) {
				mousePos = (mouseY - top - 3) / 14 + scrollOffset;
			}

			fill(matrices, mainButton.x, top, mainButton.x + mainButton.getWidth(), top + height, 0xff000000);
			if (options.length > length) {
				int l = options.length - 1;
				fill(matrices, right - 3, top + scrollOffset * height / l, right, top + (MAX_VISIBLE_LINES + scrollOffset) * height / l, 0xffbbbbbb);
			}

			for (int i = scrollOffset; i < scrollOffset + length; i++) {
				MinecraftClient.getInstance().textRenderer.draw(matrices, valueConverter.apply(options[i]), mainButton.x + 3F, top + (i - scrollOffset) * 14 + 3,
						mousePos == i ? 0xffffffff : 0xffbbbbbb
				);
			}
		}
	}
}
