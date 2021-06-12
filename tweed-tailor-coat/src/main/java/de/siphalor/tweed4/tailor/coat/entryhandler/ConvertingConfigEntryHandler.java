package de.siphalor.tweed4.tailor.coat.entryhandler;

import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import de.siphalor.tweed4.config.constraints.Constraint;
import de.siphalor.tweed4.config.entry.ValueConfigEntry;
import de.siphalor.tweed4.tailor.coat.CoatTailor;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConvertingConfigEntryHandler<V, C> implements ConfigEntryHandler<C> {
	private final ValueConfigEntry<V> configEntry;
	private final Function<V, C> converterTo;
	private final Function<C, Constraint.Result<V>> converterFrom;

	public ConvertingConfigEntryHandler(ValueConfigEntry<V> configEntry, Function<V, C> converterTo, Function<C, Constraint.Result<V>> converterFrom) {
		this.configEntry = configEntry;
		this.converterTo = converterTo;
		this.converterFrom = converterFrom;
	}

	@Override
	public C getDefault() {
		return converterTo.apply(configEntry.getDefaultValue());
	}

	@Override
	public @NotNull Collection<Message> getMessages(C value) {
		Constraint.Result<V> result = converterFrom.apply(value);
		List<Message> messages = result.messages.stream().map(CoatTailor::convert).collect(Collectors.toList());
		if (!result.ok) {
			return messages;
		}

		Constraint.Result<V> constraintResult = configEntry.applyConstraints(result.value);
		messages.addAll(constraintResult.messages.stream().map(CoatTailor::convert).collect(Collectors.toList()));

		return messages;
	}

	@Override
	public void save(C value) {

	}

	@Override
	public Text asText(C value) {
		Constraint.Result<V> conversionResult = converterFrom.apply(value);
		if (conversionResult.ok) {
			return new LiteralText(configEntry.getValueSerializer().asString(conversionResult.value));
		}
		return new LiteralText("I am Error.").formatted(Formatting.RED);
	}
}