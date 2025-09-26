/*
 * MIT License
 *
 * Copyright (c) 2025 Kalculos and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.ib67.kiwi;

import lombok.Builder;
import org.jetbrains.annotations.ApiStatus;

import java.io.PrintStream;
import java.util.*;

@ApiStatus.Experimental
@ApiStatus.AvailableSince("1.1")
public class ArgOpts {
    private final Map<String, Object> args;
    private final List<String> nonArgOptions;
    private final List<OptionInfo> collectedOptionInfo = new ArrayList<>();
    private final String programName;
    private final String description;

    @Builder
    public ArgOpts(String programName, String description, String[] args) {
        Objects.requireNonNull(args, "args not present");
        this.programName = programName;
        this.description = description;
        this.args = parseArguments(args);
        this.nonArgOptions = collectNonArgOptions(args);
    }

    private Map<String, Object> parseArguments(String[] args) {
        Map<String, Object> argMap = new HashMap<>();
        String lastOpt = null;

        for (String current : args) {
            if (current.startsWith("-")) {
                handleOptionFlag(argMap, lastOpt);
                lastOpt = stripScoreLine(current);
                continue;
            }
            if (lastOpt != null) {
                handleOptionValue(argMap, lastOpt, current);
                lastOpt = null;
            }
        }
        if (lastOpt != null) {
            argMap.put(lastOpt, null);
        }
        return argMap;
    }

    private void handleOptionFlag(Map<String, Object> argMap, String lastOpt) {
        if (lastOpt != null && argMap.get(lastOpt) != null) {
            throw new IllegalArgumentException("Duplicate and conflict option: " + lastOpt);
        }
        argMap.put(lastOpt, null);
    }

    private void handleOptionValue(Map<String, Object> argMap, String key, String value) {
        Object oldValue = argMap.get(key);
        if (oldValue != null) {
            if (oldValue instanceof List) {
                ((List) oldValue).add(value);
            } else {
                List<Object> list = new ArrayList<>();
                list.add(oldValue);
                list.add(value);
                argMap.put(key, list);
            }
        } else if (argMap.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate and conflict option: " + key);
        } else {
            argMap.put(key, value);
        }
    }

    private List<String> collectNonArgOptions(String[] args) {
        List<String> nonArgOpts = new ArrayList<>();
        boolean lastWasOption = false;

        for (String arg : args) {
            if (arg.startsWith("-")) {
                lastWasOption = true;
                continue;
            }
            if (!lastWasOption) {
                nonArgOpts.add(arg);
            }
            lastWasOption = false;
        }
        return nonArgOpts;
    }

    public List<String> stringList(String key, List<String> def) {
        return stringList(key, "", def);
    }

    public List<String> stringList(String key, String description, List<String> defaultValue) {
        collectedOptionInfo.add(new OptionInfo(key, description, defaultValue));
        return args.containsKey(key) ? (List<String>) args.get(key) : defaultValue;
    }

    public List<String> nonArgOptions() {
        return new ArrayList<>(nonArgOptions);
    }

    public int integer(String key, int defaultValue) {
        return integer(key, "", defaultValue);
    }

    public int integer(String key, String description, int defaultValue) {
        collectedOptionInfo.add(new OptionInfo(key, description, defaultValue));
        return args.containsKey(key) ? Integer.parseInt(args.get(key).toString()) : defaultValue;
    }

    public String string(String key, String defaultValue) {
        return string(key, "", defaultValue);
    }

    public String string(String key, String description, String defaultValue) {
        collectedOptionInfo.add(new OptionInfo(key, description, defaultValue));
        return args.containsKey(key) ? args.get(key).toString() : defaultValue;
    }

    public boolean bool(String key, boolean def) {
        return bool(key, "", def);
    }

    public boolean bool(String key, String description, boolean defaultValue) {
        collectedOptionInfo.add(new OptionInfo(key, description, defaultValue));
        if (!args.containsKey(key)) return defaultValue;
        Object value = args.get(key);
        return value == null ? true : Boolean.parseBoolean(value.toString());
    }

    public void printHelp(PrintStream out) {
        if (programName != null) {
            out.println("Usage: " + programName + " [options...]");
        }
        if (description != null) {
            out.println("\n" + description);
        }
        out.println("\nOptions:");

        int maxNameLength = collectedOptionInfo.stream()
                .mapToInt(opt -> opt.name().length())
                .max()
                .orElse(0);

        for (OptionInfo option : collectedOptionInfo) {
            String prefix = " --" + option.name() + " ".repeat(maxNameLength - option.name().length() + 2);
            out.print(prefix);

            if (option.description() != null) {
                formatDescription(out, prefix, option.description(), option.defaultValue());
            } else {
                out.println();
            }
        }
    }

    private void formatDescription(PrintStream out, String prefix, String description, Object defaultValue) {
        int maxLineLength = 256;
        if (description.length() <= maxLineLength) {
            out.println(description + " (default: " + defaultValue + ")");
            return;
        }

        String padding = " ".repeat(prefix.length());
        String[] words = description.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (line.length() + word.length() > maxLineLength) {
                out.println(line);
                line.setLength(0);
                line.append(padding);
            }
            line.append(word).append(" ");
        }

        out.println(line.toString().trim());
        out.println(padding + "(default: " + defaultValue + ")");
    }

    public record OptionInfo(String name, String description, Object defaultValue) {}

    private static String stripScoreLine(String s) {
        if (s.startsWith("--")) {
            return s.substring(2);
        }
        if (s.startsWith("-")) {
            return s.substring(1);
        }
        return s;
    }
}

