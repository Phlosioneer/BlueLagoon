
# MQTT API Specification

For this game, all topics are prefixed with `BlueLagoon/`.


## Hosting a Game

New games will be published to `BlueLagoon/Games`. The published game will be a single 
post, with the form:

```json
{"message":"NewGame", "topic": string, "name": string}
```

The name will match the regex `[a-zA-Z0-9 ]+`. The topic will be the same 
as the gameName, except
that spaces are replaced with underscores.

When the game begins, the host will publish to `BlueLagoon/Games`:
```json
{"message":"GameStarted", "topic": string}
```

Any client may send the message:
```json
{"message":"ListGames"}
```
All clients hosting games will then re-send their NewGame messages or their GameStarted 
messages.

## Game Lobby

When a game is created, all communication for that game will happen on the topic
`BlueLagoon/Game/<topic>`

### Joining a Game

When a client joins a game, they will post a query with the form:

```json
{"message":"JoinRequest", "clientId": string, "color": string, "username": string}
```

The color is one of "red", "blue", "green", "yellow". The username can be any ascii
string without a newline (`\n`) character. (Usernames can have length 0).

The host will then reply with one of the following:
```json
// Successful join:
{"message":"JoinSuccess", "clientId": string}

// Color already taken:
{"message":"JoinColorTaken", "clientId": string}

// Name already taken:
{"message":"JoinNameTaken", "clientId": string}
```

Where clientId is the id given in the JoinRequest.

Whenever a player joins, the host will also send a separate message with a list of the
joined players:
```json
{"message":"Players", "hostColor": string, "players": [{"name": string, "color": string, "isPlayer": boolean, "clientId": string}, ...]}
```
The message sends information about each player in the game, including the host.
isPlayer is a boolean that is true if a human player has joined that seat, and false
if it is empty or if it has a computer player. color is the color for that player, as
a string. The name is that player's username. Players are listed in the order they joined.
If for computer players, clientId is left blank, but should still be sent.

### Maintaining a Connection

Finally, the host and each joined player will use the MQTT Last Will feature to notify the 
server. The last will must publish on the topic `LastWills` (note: not game specific) 
the following message:
```json
{"message":"LastWill", "clientId": string}
```

### Kicking Joined Players

If a ping message is sent, but the clientId does not match the host or any of the
joined players, then the following message will be posted to the game topic:
```json
{"message":"UnrecognizedPlayer", "clientId": string}
```
Where clientId is the id that does not match. This situation happens if a player
loses connection, but does not realize they lost connection. They will ping when
the connection returns, and this message will inform them that they have been
kicked from the game.

## Transmitting the Game State

### Turn Order

When the host begins the game, they will generate the map, and will generate a random 
player order. The host will send the player order with the message:
```json
{"message":"TurnOrder", "order": [string, ...]}
```
The order field lists the player's colors.

This will be followed by another Players message.

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
{"message":"PlayPiece", "color": string, "piece": string, "x": int, "y": int}
```
The piece names are "hut" or "piece". <!-- TODO: What to do if the move is invalid? 
-->

### Advancing the Turn

At the beginning of each player's turn, the host will send the message:
```json
{"message":"CurrentTurn", "color": string}
```
The color parameter is the player's color.

### Advancing the Round

When the first round is done, and when the game ends, the host will send the message:
```json
{"message":"PlayerScores", "isGameOver": boolean, "scores": [{"color": string, "score": int}, ...]}
```
If the first round is over, then isGameOver is false. Then the host will reset the map, distribute
resources, and then send the map again.

When the game is over, no further communication on the game's topics or subtopics is necessary after
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

### Handling Kicked Players

If a player is kicked, then the host may choose one of the following options:

#### Computer Plays
Allow a computer to play for that player until they return. The host will send the 
message:
```json
{"message":"KickedComputerStandin", "color": string, "newName": string}
```
color is the color of the kicked player. newName is a new username that the host may
choose. It can be an empty string. 

The next client that sends a JoinRequest for that color takes over that player. The 
host will reply with:
```json
{"message":"JoinTakeover", "clientId": string}
```

See *Taking Over for a Player* below for the next steps.

#### Pause the game until they return.
To choose this option, the host will send the message:

```json
{"message":"KickedGamePause", "color": string}
```
All game actions by the host will stop until the game resumes. Clients may still send 
actions, and both clients and hosts must continue to ping each other.

The host may then either choose to continue with either of the two other options. Or,
when a new player joins successfully, the host will send the message:

```json
{"message":"KickedGameResume", "color": string, "name": string}
```

See *Taking Over for a Player* below for the next steps.

#### Force the player to concede.
The host can completely remove a player from the game by sending:
```json
{"message":"KickedForcedConcede", "color": string}
```

Play will then continue as normal. No pieces will be removed from the board.

### Taking Over for a Player
In the event that a player is allowed to rejoin a game, then the host will send the
"Players" message again.

If the player rejoins before round 2, then the host will send the message:
```json
{"message":"NoPreviousRounds"}
```
Otherwise, the host will re-send the RoundOneScores message. Then the host will send
the map, and the turn order.
