/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.malisis.advert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.malisis.advert.gui.manager.AdvertManagerGui;
import net.malisis.core.MalisisCore;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

/**
 * Commands handler for {@link MalisisCore} mod.
 *
 * @author Ordinastie
 *
 */
public class MalisisAdvertCommand extends CommandBase
{
	/** List of parameters available for this {@link MalisisAdvertCommand}. */
	Set<String> parameters = new HashSet<>();

	/**
	 * Instantiates the command
	 */
	public MalisisAdvertCommand()
	{
		//parameters.add("delete");
	}

	/**
	 * Gets the command name.
	 *
	 * @return the command name
	 */
	@Override
	public String getCommandName()
	{
		return "malisisadvert";
	}

	/**
	 * Gets the command usage.
	 *
	 * @param sender the sender
	 * @return the command usage
	 */
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "malisisadvert.commands.usage";
	}

	/**
	 * Processes the command.
	 *
	 * @param sender the sender
	 * @param params the params
	 */
	@Override
	public void processCommand(ICommandSender sender, String[] params)
	{
		if (params.length == 0)
		{
			openManagerGui();
			return;
		}

		if (!parameters.contains(params[0]))
			throw new WrongUsageException("malisisadvert.commands.usage", new Object[0]);

		//		switch (params[0])
		//		{
		//				break;
		//		}

	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;//OP
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender, String[] params)
	{
		if (params.length == 1)
			return getListOfStringsFromIterableMatchingLastWord(params, parameters);
		//		else if (params.length == 2)
		//			return getListOfStringsFromIterableMatchingLastWord(params, MalisisCore.listModId());
		//		else
		return null;
	}

	private void openManagerGui()
	{
		new AdvertManagerGui().display(true);
	}

}
