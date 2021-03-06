package dev.latvian.kubejs.script.data;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.server.ServerEventJS;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import net.minecraft.resources.ResourceLocation;

/**
 * @author LatvianModder
 */
public class DataPackEventJS extends ServerEventJS
{
	private final VirtualKubeJSDataPack virtualDataPack;

	public DataPackEventJS(VirtualKubeJSDataPack d)
	{
		virtualDataPack = d;
	}

	public void add(ResourceLocation id, String content)
	{
		virtualDataPack.addData(id, content);
	}

	public void addJson(ResourceLocation id, Object json)
	{
		JsonElement j = MapJS.json(json);

		if (j == null)
		{
			j = ListJS.json(json);
		}

		if (j != null)
		{
			add(id, JsonUtilsJS.toString(j));
		}
	}
}