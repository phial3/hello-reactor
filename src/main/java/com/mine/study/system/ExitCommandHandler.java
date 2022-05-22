package com.mine.study.system;

import io.airlift.airline.Command;

import java.util.concurrent.Callable;

/**
 * Only used to display help information
 * @since 2020/9/7
 * @author root
 */
@Command(name = "exit/quit", description = "Exit current session")
public abstract class ExitCommandHandler implements Callable<String> {
}