# Setup

### API key

For security reasons, the API is stored in `resources/IDS_PRIVATE` which is included in the
`.gitignore`. For testing purposes, you will have to create a new discord bot and populate
`resources/IDS_PRIVATE` in the format described below. Whitespace on either side of the semicolon
is ignored.

```
API_KEY : ABCDE.ABCDE
```

### Responses Manager Module

See [`response-manager/Readme.md`](response-manager/README.md) for an explanation on the setup
needed for the `ResponseManager` module. This can be ignored if you do not intend test or modify
the `ResponseManager`.

### Building

At the moment, the only build specification is in Intellij project files, there is a `pom.xml` for
Maven, but I am bad so there is no run or release goal, only a compile one. Feel free to fix this.

# Structure

### Modules

Discord bots are represented by a `Bot` object, which have very limited functionality until modules
are added to it. Modules represent a set of features that the bot will have. See the
[`IModule`](src/main/bot/modules/IModule.kt) interface for the most generic definition of a module.
When creating a new module, it is recommended that you extend `BotModule` or `ListenerModule` if
the module is based on receiving events from the JDA instance.

### Commands

Commands can be added to the bot by calling `GeneralCommandModule.addCommands()`. The general
command module instance can be obtained by `calling Bot.resolveDependency(GeneralCommandModule)`.
The `Command` data class is defined [here](src/main/bot/commands/Command.kt).

# Contributing

Contributors who have submitted accepted pull requests will be given the Developer role in
DotaPack.

It would be appreciated if commits followed the style followed these guidelines, but pull requests
may still be accepted even if they do not.

### Code Style

Code style is loosely defined, but it would be appreciated if it matched the existing code. The
only specification I will mention is that the maximum line length is `100`

### Documentation

Add Javadocs for all the classes and functions added that are not inherited from another class or
interface.

Add a `ModuleID` annotation for any new Modules.