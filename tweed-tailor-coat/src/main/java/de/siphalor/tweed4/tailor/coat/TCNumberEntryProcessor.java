package de.siphalor.tweed4.tailor.coat;

import de.siphalor.coat.input.TextConfigInput;
import de.siphalor.coat.list.ConfigListWidget;
import de.siphalor.tweed4.config.entry.ValueConfigEntry;
import de.siphalor.tweed4.tailor.coat.entryhandler.ConvertingConfigEntryHandler;
import net.minecraft.text.LiteralText;

import java.util.function.Function;

public class TCNumberEntryProcessor<N extends Number> implements TweedCoatEntryProcessor<N> {
	private final Function<String, N> parseFunction;

	public TCNumberEntryProcessor(Function<String, N> parseFunction) {
		this.parseFunction = parseFunction;
	}

	@Override
	public boolean process(ConfigListWidget parentWidget, ValueConfigEntry<N> configEntry, String path) {
		TextConfigInput textConfigInput = new TextConfigInput(configEntry.getValue().toString());
		parentWidget.addEntry(CoatTailor.convertSimpleConfigEntry(
				path, textConfigInput, new ConvertingConfigEntryHandler<>(
						configEntry, Object::toString, input -> CoatTailor.wrapExceptions(() -> parseFunction.apply(input))
				)
		));
		return true;
	}
}
