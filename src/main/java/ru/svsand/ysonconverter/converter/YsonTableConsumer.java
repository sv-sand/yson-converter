package ru.svsand.ysonconverter.converter;

import lombok.Getter;
import tech.ytsaurus.yson.YsonConsumer;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 07.06.2026
 */

public class YsonTableConsumer implements YsonConsumer {

	private String currentMapKey;

	@Getter
	private Map<String, Object> map;

	@Override
	public void onEntity() {}

	@Override
	public void onBeginList() {}

	@Override
	public void onListItem() {}

	@Override
	public void onEndList() {}

	@Override
	public void onBeginMap() {
		map = new HashMap<>();
	}

	@Override
	public void onKeyedItem(byte[] value, int offset, int length) {
		onKeyedItem(new String(value, offset, length, StandardCharsets.UTF_8));
	}

	@Override
	public void onKeyedItem(String key) {
		currentMapKey = key;
	}

	@Override
	public void onEndMap() {
		map = null;
	}

	@Override
	public void onBeginAttributes() {}

	@Override
	public void onEndAttributes() {}

	@Override
	public void onInteger(long value) {
		if (map != null)
			map.put(currentMapKey, value);
	}

	@Override
	public void onUnsignedInteger(long value) {
		if (map != null)
			map.put(currentMapKey, value);
	}

	@Override
	public void onBoolean(boolean value) {
		if (map != null)
			map.put(currentMapKey, value);
	}

	@Override
	public void onDouble(double value) {
		if (map != null)
			map.put(currentMapKey, value);
	}

	@Override
	public void onString(byte[] value, int offset, int length) {
		onString(new String(value, offset, length, StandardCharsets.UTF_8));
	}

	@Override
	public void onString(String value) {
		if (map != null)
			map.put(currentMapKey, value);
	}
}
