# Gregtech 6 Mod Workspace

This is a pre-built workspace that is convenient to build on top of to make mods for [Gregtech 6](https://gregtech.overminddl1.com/com/gregoriust/gregtech/gregtech_1.7.10/index.html) for MC 1.7.10.

## Getting Started

This should work and has been tested with both Java 7 and Java 8, make sure the JDK is installed and operational before proceeding.

### To work on this template repository itself

Clone this repository via (as with all shell examples in this file, alter as necessary for your own operating system, the example here use `sh`):

```sh
git clone https://github.com/OvermindDL1/gt6-new-workspace.git gt6-new-workspace
cd !$
```

Or using your own fork if you wish to submit a PR.

### To use this template as the base for your own mod

Clone this repository via (as with all shell examples in this file, alter as necessary for your own operating system, the example here use `sh`), and then remove and rebuild the git repo to make your own history (replace the `mymod` with whatever you wish to name your mod in this entire readme):

```sh
git clone https://github.com/OvermindDL1/gt6-new-workspace.git mymod # Clone this repository to your own location
cd mymod # Change to the directory of your new mod workspace
rm -rf .git # Remove the template history
git init # Initialize your own git history
git add -A # Add all existing files to your git history as the new base
git commit -m 'Initial workspace creation from template' # And save it
```

Then you should open the `build.gradle` file in the text editor of your choice and set the variable lines of:

```groovy
def gt6_version = "6.05.34"
version = "0.0.0"
group = "com.yourname.modid" // http://maven.apache.org/guides/mini/guide-naming-conventions.html
archivesBaseName = "modid"
```

* Set the `gt6_version` with the GT6 version you wish to mod against.
* The `version` is the version of your mod.  It is always best to follow [Semantic Versioning](http://semver.org/) standards.  Basically the version number in semver is made of up 3 parts, like `1.2.3` is `<major>.<minor>.<patch>` where `major` should only be incremented when updating to the next major version forces a world re-creation (be sure to set minor and patch to 0 when major is incremented), and `minor` should update with feature additions (resetting patch to 0 when minor increments), and `patch` should increment when a bug fix is released but is otherwise feature identical.  Another popular style for the version is instead do `<major>.<minor>.<patch>-mc1.7.10` to encode the MC version as well, however as GT6 is 1.7.10 only this is not really necessary, but is still good practice.
* The `group` is the base Java namespace of your mod.
* The `achivesBaseName` is the base filename of your mod that is exported, it will automatically have the version and more attached.

At this point make a GT6 mod as normal, such as with the normal Forge workspace creation commands.  Run `setupDecompWorkspace` as below to perform the standard Forge workspace setup.  This **will** take quite a while as it has to decompile the original MC source.

```sh
./gradlew setupDecompWorkspace --refresh-dependencies
```

If you wish to also be able to run your mod as you test and program it then also then run:

```sh
./gradlew setupDevWorkspace
```

Then setup whatever IDE you wish to use.  Forge is built primarily with Eclipse in mind so if you use anything better like Intellij then that will require slightly more setup (documentation PR's welcome for other IDE's).  As it is built with Eclipse in mind then you can just run this command to have everything setup properly for Eclipse:

```sh
./gradlew eclipse
```

#### Building

To build your mod just run:

```sh
./gradlew build
```

If you wish to make a full release build then set the `ARTIFACT_VERSION` to a unique incremental ID before running build, such as via (great to put in a continuous building system!):

```sh
ARTIFACT_VERSION=1 ./gradlew build
```

Your library should be at `./build/libs` and you can distribute as you wish.

# GT6 Modding Documentation

There is no source for modding GT6 specific information other than in the GT6 API files themselves.  Look there to start, otherwise feel free to edit the Wiki in this template repo at:  https://github.com/OvermindDL1/gt6-new-workspace/wiki

PR's to this Repo itself are always welcome and encouraged!

## Basic setup

To setup your actual source files, after naming your mod in the `build.gradle` file as above then next you will want to perform these steps (please PR for better documentation!):

1. Change the namespace of your mod, renaming the folders as appropriately.
2. Rename the ExampleMod.java file to whatever you want your mod file to be named.
3. Inside the previously ExampleMod.java file rename anything named ExampleMod to your new name as appropriate
4. Also change the top `public static final` variables near the top as appropriate.
5. Change the namespace for the proxy's as appropriate (you do not need to rename the proxy files for note, just adjust their namespace package).
6. Uncomment out the `@Mod` annotation in the previous ExampleMod.java file, renaming as appropriate.
7. Look through the commented example code (which you can uncomment and run if you wish, it will just lack textures is all) to see example usage of the GT6 API.
