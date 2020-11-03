package de.siphalor.tweed.client.cloth;

import com.google.common.collect.ImmutableList;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

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

	public ClothDropdownSelectEntry(String fieldName, V originalValue, String resetButtonText, Supplier<Optional<String[]>> tooltipSupplier, boolean requiresRestart, Supplier<V> defaultValue, Consumer<V> saveConsumer, List<V> options, Function<V, Text> valueConverter) {
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
		resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getStringWidth(resetButtonText) + 6, 20, resetButtonText, button -> {
			setValue(getDefaultValue().orElse(null));
		});

		setValue(originalValue);
	}

	public void setValue(V value) {
		this.value = value;
		if (value == null) {
			mainButton.setMessage(I18n.translate("tweed.cloth.dropdown.empty"));
		} else {
			mainButton.setMessage(valueConverter.apply(value).asString());
		}
		if (getScreen() != null) {
			if (!Objects.equals(originalValue, value)) {
				getScreen().setEdited(true, isRequiresRestart());
			}
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

	public boolean isEdited() {
		return getConfigError().isPresent() || !Objects.equals(value, originalValue);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (
				mouseY >= mainButton.y + 20 && mouseY < mainButton.y + 20 + getOptionsVisibleLength() * 14
				&& mouseX >= mainButton.x && mouseX < mainButton.x + mainButton.getWidth()
		) {
			scrollOffset = MathHelper.clamp(scrollOffset - (int) amount, 0, Math.max(0, options.length - MAX_VISIBLE_LINES - 1));
			return true;
		}
		return false;
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

	public int getOptionsVisibleLength() {
		return Math.min(MAX_VISIBLE_LINES, options.length);
	}

	@Override
	public int getItemHeight() {
		return super.getItemHeight() + (optionsVisible ? getOptionsVisibleLength() * 14 + 5 : 0);
	}

	private int getOptionsTop() {
		return mainButton.y + 20 + 3;
	}

	@Override
	public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
		super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
		Window window = MinecraftClient.getInstance().window;

		mainButton.active = isEditable();
		resetButton.active = isEditable() && getDefaultValue().map(def -> !def.equals(value)).orElse(false);

		mainButton.y = y;
		resetButton.y = y;

		String displayedFieldName = getFieldName();
		if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
			MinecraftClient.getInstance().textRenderer.drawWithShadow(displayedFieldName, window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getStringWidth(displayedFieldName), y + 6, getPreferredTextColor());
			resetButton.x = x;
			mainButton.x = x + resetButton.getWidth() + 1;
		} else {
			MinecraftClient.getInstance().textRenderer.drawWithShadow(displayedFieldName, x, y + 6, getPreferredTextColor());
			resetButton.x = x + entryWidth - resetButton.getWidth();
			mainButton.x = x + entryWidth - 150 + 1;
		}
		mainButton.setWidth(150 - resetButton.getWidth() - 4);

		mainButton.render(mouseX, mouseY, delta);
		resetButton.render(mouseX, mouseY, delta);

		if (optionsVisible) {
			int length = getOptionsVisibleLength();
			int top = mainButton.y + 20;
			int right = mainButton.x + mainButton.getWidth();
			int height = length * 14;

			int mousePos = -1;
			if (mouseX >= mainButton.x && mouseX < right) {
				mousePos = (mouseY - top - 3) / 14 + scrollOffset;
			}

			fill(mainButton.x, top, mainButton.x + mainButton.getWidth(), top + height, 0xff000000);
			if (options.length > length) {
				int l = options.length - 1;
				fill(right - 3, top + scrollOffset * height / l, right, top + (MAX_VISIBLE_LINES + scrollOffset) * height / l, 0xffbbbbbb);
			}

			for (int i = scrollOffset; i < scrollOffset + length; i++) {
				MinecraftClient.getInstance().textRenderer.draw(valueConverter.apply(options[i]).asString(), mainButton.x + 3F, top + (i - scrollOffset) * 14 + 3,
						mousePos == i ? 0xffffffff : 0xffbbbbbb
				);
			}
		}
	}
}
