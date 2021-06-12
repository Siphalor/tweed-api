package de.siphalor.tweed4.tailor.coat.entryhandler;

import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import de.siphalor.tweed4.config.constraints.Constraint;
import de.siphalor.tweed4.config.entry.ValueConfigEntry;
import de.siphalor.tweed4.tailor.coat.CoatTailor;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public class SimpleConfigEntryHandler<V> implements ConfigEntryHandler<V> {
	private final ValueConfigEntry<V> configEntry;

	public SimpleConfigEntryHandler(ValueConfigEntry<V> configEntry) {
		this.configEntry = configEntry;
	}

	@Override
	public V getDefault() {
		return configEntry.getDefaultValue();
	}

	@Override
	public @NotNull Collection<Message> getMessages(V value) {
		Constraint.Result<V> result = configEntry.applyConstraints(value);
		return result.messages.stream().map(CoatTailor::convert).collect(Collectors.toList());
	}

	@Override
	public void save(V value) {

	}

	@Override
	public Text asText(V value) {
		return new LiteralText(configEntry.getValueSerializer().asString(value));
	}
}
