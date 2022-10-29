package de.siphalor.tweed5.reload;

import de.siphalor.tweed5.config.ConfigFile;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

@ParametersAreNonnullByDefault
public class ReloadContext {
	private final @NotNull ReloadEnvironment environment;
	private final @NotNull ReloadScope scope;
	private final boolean mainOrigin;
	/**
	 * The path to the file that triggered the reload, excluding the file name.
	 * If the reload was not triggered by a file, this is <code>null</code>.
	 */
	private final @Nullable String originPath;

	protected ReloadContext(ReloadEnvironment environment, ReloadScope scope, boolean mainOrigin, @Nullable String originPath) {
		this.environment = environment;
		this.scope = scope;
		this.mainOrigin = mainOrigin;
		this.originPath = originPath;
	}

	public static ReloadContext nonFile(ReloadEnvironment environment, ReloadScope scope, boolean mainOrigin) {
		return new ReloadContext(environment, scope, mainOrigin, null);
	}

	public static ReloadContext file(ReloadEnvironment environment, ReloadScope scope, boolean mainOrigin, @NotNull File file) {
		String dir = file.getParent();
		return new ReloadContext(environment, scope, mainOrigin, dir == null ? "" : dir);
	}

	public static ReloadContext file(ReloadEnvironment environment, ReloadScope scope, boolean mainOrigin, @NotNull ConfigFile file) {
		return file(environment, scope, mainOrigin, new File(file.getFileName()));
	}

	public @NotNull ReloadEnvironment getEnvironment() {
		return environment;
	}

	public @NotNull ReloadScope getScope() {
		return scope;
	}

	public boolean isMainOrigin() {
		return mainOrigin;
	}

	public @Nullable String getOriginPath() {
		return originPath;
	}

	public @NotNull ReloadContext withEnvironment(ReloadEnvironment environment) {
		return new ReloadContext(environment, scope, mainOrigin, originPath);
	}

	public @NotNull ReloadContext withScope(ReloadScope scope) {
		return new ReloadContext(environment, scope, mainOrigin, originPath);
	}

	public @NotNull ReloadContext withMainOrigin(boolean mainOrigin) {
		return new ReloadContext(environment, scope, mainOrigin, originPath);
	}

	public void write(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeString(environment.name());
		packetByteBuf.writeString(scope.name());
		packetByteBuf.writeBoolean(mainOrigin);
	}

	public static ReloadContext read(PacketByteBuf packetByteBuf) {
		return new ReloadContext(
				ReloadEnvironment.ENUM.valueOf(packetByteBuf.readString(Short.MAX_VALUE)),
				ReloadScope.ENUM.valueOf(packetByteBuf.readString(Short.MAX_VALUE)),
				packetByteBuf.readBoolean(),
				null
		);
	}
}
