package dev.latvian.kubejs.server;

import dev.latvian.kubejs.util.CommandSender;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class CommandProperties
{
	@FunctionalInterface
	public interface ExecuteFunction
	{
		void execute(CommandSender sender, List<String> args);
	}

	@FunctionalInterface
	public interface UsernameFunction
	{
		boolean isUsername(List<String> args, int index);
	}

	public final transient Consumer<CommandBase> callback;
	public final String name;
	public final List<String> aliases;
	public ExecuteFunction execute;
	public UsernameFunction username;

	public CommandProperties(Consumer<CommandBase> c, String n)
	{
		callback = c;
		name = n;
		aliases = new ArrayList<>();
		execute = null;
		username = null;
	}

	public void register()
	{
		callback.accept(new Cmd(this));
	}

	private static class Cmd extends CommandBase
	{
		private final CommandProperties properties;

		private Cmd(CommandProperties p)
		{
			properties = p;
		}

		@Override
		public String getName()
		{
			return properties.name;
		}

		@Override
		public List<String> getAliases()
		{
			return properties.aliases;
		}

		@Override
		public String getUsage(ICommandSender sender)
		{
			return "commands." + properties.name + ".usage";
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index)
		{
			return properties.username != null && properties.username.isUsername(Arrays.asList(args), index);
		}

		@Override
		public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
		{
			if (args.length == 0)
			{
				return Collections.emptyList();
			}
			else if (isUsernameIndex(args, args.length - 1))
			{
				return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			}

			return super.getTabCompletions(server, sender, args, pos);
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			if (properties.execute == null)
			{
				throw new CommandNotFoundException();
			}

			properties.execute.execute(new CommandSender(ServerJS.instance, sender), Arrays.asList(args));
		}
	}
}