package battleship;

class Game {
    Game() {
        this.activePlayer = ActivePlayer.PLAYER_1;
    }

    private Player playerOneData;
    private Player playerTwoData;
    ActivePlayer activePlayer;

    public Player getActivePlayerData() {
        if (activePlayer == ActivePlayer.PLAYER_1) {
            return this.playerOneData;
        } else {
            return this.playerTwoData;
        }
    }

    public Player getOtherPlayerData() {
        if (activePlayer == ActivePlayer.PLAYER_1) {
            return this.playerTwoData;
        } else {
            return this.playerOneData;
        }
    }

    public void setActivePlayerData(Player playerData) {
        if (activePlayer == ActivePlayer.PLAYER_1) {
            this.playerOneData = playerData;
        } else {
            this.playerTwoData = playerData;
        }

    }

    @SuppressWarnings("unused")
    public void setOtherPlayerData(Player playerData) {
        if (activePlayer == ActivePlayer.PLAYER_1) {
            this.playerTwoData = playerData;
        } else {
            this.playerOneData = playerData;
        }
    }

    public void transitionPlayer() {
        switch (this.activePlayer) {
            case PLAYER_1:
                this.activePlayer = ActivePlayer.PLAYER_2;
                break;
            case PLAYER_2:
                this.activePlayer = ActivePlayer.PLAYER_1;
                break;
        }
    }

    enum ActivePlayer {
        PLAYER_1("Player 1"),
        PLAYER_2("Player 2");

        ActivePlayer(String string) {
            this.player = string;
        }

        private final String player;

        public String getPlayer() {
            return player;
        }
    }
}
