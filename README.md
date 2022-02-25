![Banner](https://raw.githubusercontent.com/track/sdk/main/.github/banner.png)

# Analyse (Spigot Plugin)

This is the official spigot adapter for Analyse - the Minecraft Server Analytics platform that tracks community performance.

## Installation
1. Download this plugin (Latest from Releases).
2. Head to Analyse Dashboard, and copy the server command.
3. Start your server, and run the setup command.

You're ready to go!

## Custom Integration
This project utilises our official [SDK](https://github.com/track/sdk), which is an official wrapper around our [API](https://docs.analyse.net/page/api-endpoints).

## Compiling
1. Clone this repo (`git clone git@github.com:track/plugin.git`).
1. Clone our sdk (`git clone git@github.com:track/sdk.git`).
1. Install our sdk using `./gradlew pushToMavenLocal`.
2. Make any needed changes.
3. Then use `./gradlew shadowjar` to build.