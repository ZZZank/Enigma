package cuchaz.enigma.mcp.util;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author ZZZank
 */
public class ClosableHelper<T> implements AutoCloseable, Consumer<T> {
	private T value;
	private final Consumer<T> action;

	public ClosableHelper(Consumer<T> action) {
		this.action = Objects.requireNonNull(action, "action == null");
	}

	public void accept(T value) {
		this.value = value;
	}

	@Override
	public void close() {
		if (value != null) {
			action.accept(value);
		}
	}
}
