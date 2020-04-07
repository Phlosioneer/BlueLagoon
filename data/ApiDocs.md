
# MQTT API Specification

For this game, all topics are prefixed with `BlueLagoon/`.

All messages are idempotent (i.e. they may be sent or recieved multiple times without affecting the logic of this protocol).

# Hosting a Game

## New Games

New games will be published to `BlueLagoon/Games`. The published game will be a single 
post, with the form:

```json
{"message":"NewGame", "topic": string, "name": string, "startUTC": number, "clientId": string}
```

The `name` field will match the regex `[a-zA-Z0-9 ]+`. The `topic` field will be the same 
as the `name`, except that spaces are replaced with underscores. The `startUTC` field is the
unix time that the first `NewGame` message was sent. `clientId` is the name of the hosting
client.

#### Edge cases

If two games with the same name are posted, the game with the lowest `startUTC` field has priority.
When the host of the game with priority recieves a `NewGame` message with the same name, they must
re-transmit their original `NewGame` message.

If a game with the same name as a game-in-progress is posted, the game-in-progess has priority until
a `LastWill` message for the original host is posted. 

## Beginning the game

When the game begins, the host will publish to `BlueLagoon/Games`:

```json
{"message":"GameStarted", "topic": string, "startUTC": number}
```

## Listing current games

Any client may send the message:

```json
{"message":"ListGames"}
```

All clients hosting games will then re-send their `NewGame` messages or their `GameStarted` 
messages.

### Edge cases

If multiple `ListGames` messages are posted before a host can respond, the host only needs to send
one message to satisfy both requests.

# Game Lobby

When a game is created, all communication for that game will happen on the topic
`BlueLagoon/Game/<topic>`

## Joining a Game

When a client joins a game, they will post a query with the form:

```json
{"message":"JoinRequest", "clientId": string, "color": string, "username": string}
```

The `color` field is one of "red", "blue", "green", "yellow". The `username` can be any ascii
string without a newline (`\n`) character or a null (`\0`) character. (Usernames can have length 0).

The host will then reply with:

```json
{"message":"JoinResponse", "success": bool, "reason": string, "players": [Player], "clientId": string}
```

The `clientId` is the same as given in the `JoinRequest` message. `success` is true if the color and username were available, and
false otherwise. `players` contains a list of [Player](#the-player-object) objects, one for each successfully joined user. When `success`
is true, the `players` field will include the an entry for the client of this `JoinRequest`.

The `reason` field is one of:

// TODO: Reasons

## Listing Members and Adding Computer Players

Any client may send the message:

```json
{"message":"ListPlayers"}
```

The host will reply with the message:

```json
{"message":"AllPlayers", "players": [Player]}
```

The `players` field contains a list of [Player](#the-player-object) objects, one for each player.

When the host adds a new computer player, they should (but are not required to) send an `AllPlayers` message.

### Edge Cases

If multiple `ListPlayers` messages are sent before the host can reply, the host only needs to send
one message to satisfy both requests.

## Maintaining a Connection

The host and each joined player will use the MQTT Last Will feature to notify the 
server of disconnect. The last will must publish on the topic `LastWills` (note: not game specific) 
the following message:

```json
{"message":"LastWill", "clientId": string}
```

## Kicking Joined Players, Rejoining Games

The host may remove a player by sending the message:

```json
{"message":"Replace", "reason": string, "players": [Player]}
```

The `reason` field contains an optional message for the change. The `players` field contains an updated array
of the players in the game.


## Transmitting the Game State

### Turn Order

When the host begins the game, they will generate the map, and will generate a random player order. The turn order
is encoded inside the `Player` object, so the server will send an `AllPlayers` message at the start of the
game.

### The Map

The host sends the positions of all pieces with the message:

```json
{"message":"AllPieces", "pieces": [{"name": string, "x": int, "y": int, "color": string},...]}
```

Each entry of the pieces array contains the name of the piece, the x coordinate, and
the y coordinate. The pieces are: "wood", "ore", "gems", "bread", "gold", "piece", and 
"hut". For "piece" and "hut", there is an additional color field for the piece's color.

## Game Actions

### Placing a Piece

Whenever a player places a piece, they will send the message:

```json
{"message":"PlayPieceRequest", "color": string, "piece": string, "x": int, "y": int, "turn": int}
```

The `piece` field is either "hut" or "piece". The `turn` field is the current turn number, included to ensure
`PlayPieceRequest` messages are idempotent. 

```json
{"message":"PlayPieceResponse", "success": bool, "reason": string}
```

The `success` field is true if the piece was placed, and false if the move was invalid. The `reason` field is
provided when `success` is false, and is one of the following:

// TODO: List of error codes for piece placement.

### Advancing the Turn

At the beginning of each player's turn, the host will send the message:

```json
{"message":"CurrentTurn", "color": string}
```

The color parameter is the player's color.

### Advancing the Round

When the first round is done, and when the game ends, the host will send the message:

```json
{"message":"PlayerScores", "isGameOver": boolean, "players": [Player]}
```

If the first round is over, then isGameOver is false. Then the host will reset the map, distribute
resources, and then send the map again.

After the second round is done, `isGameOver` is true. No further communication on the game's topics or subtopics is necessary after
this message.

### Conceding

If a player concedes, they will send the message:

```json
{"message":"Conceded", "color": string}
```

Where color is the player's color. If the host concedes, they will randomly choose one of
the remaining players, and will send the message:

```json
{"message":"HostConcede", "oldColor": string, "newColor": string}
```

newColor is the color of the new host player. The old host then prints the map state, if
able, and an updated player turn order.

If all players other than the host concede, then the host wins.

## Handling Kicked Players

If a player is kicked, then the host may choose one of the following options:

#### Computer Plays

Allow a computer to play for that player until they return. The host will send the 
a `Replace` message.

See *Taking Over for a Player* below for the next steps.

#### Pause the game until they return.
To choose this option, the host will send the message:

```json
{"message":"GamePause", "pauseUTC": number}
```
The `pauseUTC` field is the unix time in the UTC timezone when the server sends the message.
It is used to ensure game pauses are idempotent.

All game actions by the host will stop until the game resumes. If a client sends a message
corresponding to a game action, the host will retransmit the `GamePause` message as
its reply.

The host may send any number of `Replace` messages while a game is paused.

When the host is ready to resume, they will send the message:

```json
{"message":"GameResume", "pauseUTC": number}
```
The `pauseUTC` field is the same as the corresponding `GameResume` message.

### Taking Over for a Player

Any client not currently playing the game can send a join request. The host may accept
or reject the attempt in the normal way. If a join request is accepted, the host must
send a corresponding `Replace` message.

## Intermediate Objects

### The Player Object

```
{
	"username": string,
	"clientId": string,
	"color": string,
	"score": number,
	"turn": number,
	"piecesLeft": number,
	"hutsLeft": number,
	"resources": {
		"wood": number,
		"gems": number,
		"bread": number,
		"rocks": number,
		"gold": number
	}
}
```

// TODO: Explanations of the fields, and their default values.
