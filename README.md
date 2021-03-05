# Movecraft-Towny Integration
 
Home of the code for the following features:
 - Towny plugin integration

## Version support
The `main` branch is coded for Towny v0.93.1.0 for 1.10.2 and 1.11.2, and the `dev` branch is coded for Towny v0.96.7.0 for all other versions.

## Download

Devevlopment builds can be found on the [Releases page](https://github.com/TylerS1066/Movecraft-Towny) of this repository.  Stable builds can be found on [our SpigotMC page](TBD).

## Building
This plugin requires that the user setup and build their [Movecraft](https://github.com/APDevTeam/Movecraft) development environment, and then clone this into the same folder as your Movecraft development environment such that Movecraft-Towny and Movecraft are contained in the same folder.  This plugin also requires you to build the latest version of 1.13.2 using build tools.

```
java -jar BuildTools.jar --rev 1.13.2
```

Then, run the following to build Movecraft-Cannons through `maven`.
```
mvn clean install
```
Jars are located in `/target`.


## Support
[Github Issues](https://github.com/TylerS1066/Movecraft-Towny/issues)

[Discord](http://bit.ly/JoinAP-Dev)

The plugin is released here under the GNU General Public License V3. 
