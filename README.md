<p align="center"><a href="https://analyse.net" target="_blank"><img src="https://analyse.net/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Flogo.8237c69e.svg&w=384&q=75" width="400"></a></p>

## About Analyse

This is the official plugin version of Analyse, for usage with tracking community performance for Minecraft Servers.

### Installation
1. Download this plugin (Latest from Releases).
2. Head to Analyse Dashboard, and copy the server command.
3. Start your server, and run the setup command.

You're ready to go!

---

### Custom Implementations
We welcome any custom community implementations, this plugin simply sends requests to our backend API and endpoints are documented below.

Base URL: `https://app.analyse.net/api/v1/`

_All requests require the `X-SERVER-TOKEN` header with the token provided._

---

**Server Information**

URL: `server`

Type: `GET`

---

**Player Sessions**

URL: `server/sessions`

Type: `POST`

Example Payload:

```json
{
  "name": "User",
  "uuid": "3234-2324-3232-32323",
  "joined_at": "2021-12-29 16:58:24.436228",
  "quit_at": "2021-12-29 19:58:24.436228",
  "ip_address": "017386cd32f983e735db582718f11ffbc9b1233b06f16383f13f6d23823da0e3",
  "country": "GB",
  "stats": [
    {
      "key": "player_kills",
      "value": "100"
    }
  ]
}
```

---

**Server Heartbeat**

URL: `server/heartbeat`

Type: `POST`

Example Payload:

```json
{
  "players": 300
}
```

---

### Compiling
1. Git clone this repository.
2. Run `./gradlew shadowjar` to build.