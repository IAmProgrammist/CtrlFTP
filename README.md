# CtrlFTP

Welcome to the repository CtrlFTP

Repository consists of two parts:

## Libraries

A CtrlFTP library consists of two parts

### Core

A `rchat.info.ctrlftp.core` package contains some basic classes to create your own FTP server. 
Here some of the basic functionality you can find there:

#### Commands
`ctrlftp-core` contains basic classes to create your own FTP server.
In CtrlFTP every command is created using @Command from package
`rchat.info.ctrlftp.core.annotations.Command`, for example:

```
public class ServiceService {
    @Command(name = "NOOP")
    public static Response noop() {
        return new Response(ResponseTypes.COMMAND_OK, "Glad to work with ya, human 'fella!");
    }
}
```

We created a FTP command NOOP that returns COMMAND_OK response with a message `Glad to work with ya, human 'fella!`.
Note that method should be static and return a Response class from package `rchat.info.ctrlftp.core.responses`.

#### Dependencies
Method `noop` seems pretty boring right now, it doesn't receives
any parameters, and we can't read user arguments. 

CtrlFTP for such cases provides a dependency system. 

There are multiple types of dependencies supported:
* `String` - a raw command is passed
* `Session` - a current session is passed (package `rchat.info.ctrlftp.core`)
* `Server` - a server instance is passed (package `rchat.info.ctrlftp.core`)
* `AbstractDependency` - a custom dependency class, we are going to talk about it later

A dependency is injected in a 
command by passing dependency as a parameter in a method in case of commands. For example:

```
public class ServiceService {
    @Command(name = "NOOP")
    public static Response noop(String command) {
        return new Response(ResponseTypes.COMMAND_OK, "Glad to work with ya, human 'fella!");
    }
}
```

Now we can achieve original raw command that came from a user in method noop.

It is also possible to write your own dependencies. Dependencies is a class that
inherits from a AbstractDependency class (package `rchat.info.ctrlftp.core.dependencies`)
and also annotated by a @Dependency (package `rchat.info.ctrlftp.core.dependency`). 

@Dependency accepts a level parameter. Now in CtrlFTP three levels are supported:

* `Global` - a dependency object is living for a server instance.
* `Session` - a dependency is living during a session. Every session has its own instance of this dependency.
* `Command` - a dependency is living during a command. Every command has its own instance of this dependency.

A dependency class should have a single constructor that, just like command, accepts dependency parameters.

But there are some limitations for dependency levels unlike command methods. 
`Session` and `Global` dependencies can't accept a 
`String` as a parameter. Only `Command` dependency can accept `String` in its constructor. Also 
higher-level dependencies can't accept dependency of a lower level (`Global` can't accept `Command`
dependency). All limitations can be expressed as a table:

|                      | Can accept String? | Can accept Session? | Can accept Server? | Can accept dependencies with level `Command`? | Can accept dependencies with level  `Session` ? | Can accept dependencies with level  `Global` ? |
|----------------------|--------------------|---------------------|--------------------|-----------------------------------------------|-------------------------------------------------|------------------------------------------------|
| `Command` dependency | +                  | +                   | +                  | +                                             | +                                               | +                                              |
| `Session` dependency | -                  | +                   | +                  | -                                             | +                                               | +                                              |
| `Global` dependency  | -                  | -                   | +                  | -                                             | -                                               | +                                              |

Dependencies can't also accept themselves and recursive dependencies for now.

Now, lets take a look at example dependency:
```
package rchat.info.ctrlftp.dependencies.deserializer;

import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;

/**
 * A dependency which deserializes and parses command arguments
 * @param <DeserializeClass> a result deserialize class
 */
@Dependency(level = DependencyLevel.COMMAND)
public abstract class BaseDeserializer<DeserializeClass> extends AbstractDependency {
    private final DeserializeClass deserializeData;

    public BaseDeserializer(String command) {
        this.deserializeData = deserialize(command);
    }

    /**
     * A deserialize method that parses command and returns DeserializeClass
     * @param command a raw command
     * @return deserialized data
     */
    protected abstract DeserializeClass deserialize(String command);

    /**
     * A method that returns args from command
     * @return deserialized args from command
     */
    public DeserializeClass getDeserializeData() {
        return deserializeData;
    }
}
```

Here a dependency has a level of `Command`, that means it will only exist during command execution. 
Its constructor accepts a `String` - a raw command from a user. Then this dependency deserializes 
raw command and saves result in its field. Later it can be accessed with a getDeserializeData. 
Lets complete our example code:

```
package rchat.info.ctrlftp.dependencies.deserializer;

/**
 * A dependency to parse single string after a command name
 */
public class SingleStringDeserializer extends BaseDeserializer<SingleStringDeserialized> {
    public SingleStringDeserializer(String command) {
        super(command);
    }

    @Override
    public SingleStringDeserialized deserialize(String command) {
        var firstSpaceIndex = command.indexOf(' ');

        return new SingleStringDeserialized(firstSpaceIndex == -1 ? "" : command.substring(firstSpaceIndex + 1));
    }
}
```

```
package rchat.info.ctrlftp.dependencies.deserializer;

public record SingleStringDeserialized(String arg) {}
```

```
@Command(name = "USER")
public static Response onUser(BasicAuthenticationDependency auth, SingleStringDeserializer arg) {
    auth.setLogin(arg.getDeserializeData().arg());

    var authResponse = auth.authenticate();
    return authResponse.cause();
}
```

In example method `onUser` we access deserialized data from class `SingleStringDeserializer` dependency.

#### Server startup

We created all of required dependencies and services, but how do we start server? To start a server
we need to create a new Server, like this:

```
package rchat.info.ctrlftp.examplebasic;

import rchat.info.ctrlftp.core.Server;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        var u = new Server(List.of("dependencies.xml"));
        u.mainLoop();
    }
}

```

A server will start and will listen for user input at port 21.

A Server constructor accepts a list of Strings. This list contains a resource name with 
list of services and dependencies to use. In this example we created a file at `src/main/resources/dependencies.xml`
which looks like this:

```
<?xml version="1.0" encoding="utf-8"?>
<config>
    <services>
        <service>path.to.class.with.commands.ServiceClass</service>
        ...
    </services>
    <dependencies>
        <dependency>path.to.dependency.class.DependencyClass</dependency>
        ...
    </dependencies>
</config>
```
Ax XML file should contain root `config` with nodes `services` and `dependencies`. 
Services list should contain pathes to a classes with methods and dependencies list
should contain a list to a dependencies.

### Dependencies

A `rchat.info.ctrlftp.dependencies` package contains some basic dependencies 
that allow to create basic functionality faster.

#### Authentication

A `BaseAuthenticationDependency` is created to check session authentication and logout
if needed. Method `authenticate` returns AuthenticationResult that contains 
user information if authentication and response that contains message for user. 

#### Deserializer

A `BaseDeserializer` is created to deserialize raw command input. Also it contains
realization for a single parameter `SingleStringDeserializer` after a command name.
Its functionality example is provided earlier.

#### File transfer

An `AcceptTransferDependency` is a class that allows FTP server to accept and send
data using data connections.

This dependency is capable of entering passive and active mode using `setPassive` method. 

Data send and transfer is asynchronous. It wont block called function. send and accept
methods are receiving `TransferEvent` and `AcceptEvent` accordingly to process results
of data transfering.

Dependency also accepts an instance of `BasePipeDependency`. The purpose of this class is to 
transform user input in program format (for example, a temporary file or a buffer of characters)
and backwards. This may be useful, for example, in commands `STRU` and `TYPE` when user input 
may be splitted in headers. 

## Examples

You can find examples at `examples` folder. 

* `example-basic` - contains a very (VERY) basic FTP server to look files on UNIX systems
* `example-dbauth` - a FTP server that stores user credentials in database connection