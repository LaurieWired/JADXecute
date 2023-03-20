package jadx.api.plugins.options.impl;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import jadx.api.plugins.options.OptionDescription;

public class JadxOptionDescription implements OptionDescription {

	private final String name;
	private final String desc;
	private final String defaultValue;
	private final List<String> values;

	public JadxOptionDescription(String name, String desc, @Nullable String defaultValue, List<String> values) {
		this.name = name;
		this.desc = desc;
		this.defaultValue = defaultValue;
		this.values = values;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String description() {
		return desc;
	}

	@Override
	public @Nullable String defaultValue() {
		return defaultValue;
	}

	@Override
	public List<String> values() {
		return values;
	}

	@Override
	public String toString() {
		return "OptionDescription{" + desc + ", values=" + values + '}';
	}
}
