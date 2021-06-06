package fxgames.ttt;

import fxgames.Coord;

import java.io.Serializable;
import java.util.*;

public class TicTacToe implements Serializable {
    private String[][] state = new String[3][3];
    public String winner;
    public boolean playerOneIsX;
    public String playerOneName;
    public int playerOneWins;
    public String playerTwoName;
    public int playerTwoWins;
    public int ties;
    public String turn;

    public int vx;
    public int vy;
    public int vxd;
    public int vyd;

    public TicTacToe() {
        resetGame();
    }

    public TicTacToe(TicTacToe game) {
        for (var y = 0; y < 3; y++) {
            for (var x = 0; x < 3; x++) {
                if (game.state[y][x] != null) {
                    this.state[y][x] = game.state[y][x];
                }
            }
        }
        this.turn = game.turn;
        this.winner = game.winner;
        playerOneIsX = game.playerOneIsX;
        playerOneName = "";
        playerTwoName = "";
    }

    public void newGame() {
        state = new String[3][3];
        winner = "";
        turn = "X"; //X always goes first
    }

    public void resetGame() {
        newGame();
        playerOneIsX = true;
        playerOneName = "";
        playerOneWins = 0;
        playerTwoName = "";
        playerTwoWins = 0;
        ties = 0;
    }

    private String bstr(int x, int y, int xd, int yd) {
        return state[x][y] + state[x + xd][y + yd] + state[x + xd + xd][y + yd + yd];
    }

    public String check(int x, int y, int xd, int yd) {
        if (winner.equals("")) {
            String s = bstr(x, y, xd, yd);
            vx = x;
            vy = y;
            vxd = xd;
            vyd = yd;
            if (s.equals("OOO") || s.equals("XXX")) {
                winner = s.substring(0, 1);
            }
        }
        return winner;
    }

    public void checkVictoryCondition() {
        check(0, 0, 0, 1);
        check(1, 0, 0, 1);
        check(2, 0, 0, 1);
        check(0, 0, 1, 0);
        check(0, 1, 1, 0);
        check(0, 2, 1, 0);
        check(0, 0, 1, 1);
        check(2, 0, -1, 1);
    }

    public void checkGameState() {
        checkVictoryCondition();
        if (!winner.equals("")) {
            if (winner.equals("X") && playerOneIsX) {
                playerOneWins++;
            } else if (winner.equals("O")) {
                playerTwoWins++;
            }
        } else if (empties().size() == 0) {
            winner = "-";
            ties++;
        }
    }

    public int scoreForMove(Coord move, boolean maximize, int level) {
        var g = new TicTacToe(this);
        g.addPiece(turn, move.x, move.y);
        if (!g.winner.equals("")) {
            if (maximize) return 10 - level;
            else return -11 + level;
        } else {
            List<Coord> e = g.empties();
            HashMap<Coord, Integer> m = new HashMap<Coord, Integer>();
            for (Coord coord : e) {
                m.put(coord, g.scoreForMove(coord, !maximize, level + 1));
            }
            int bestValue = maximize ? 1000000 : -1000000;
            for (Map.Entry<Coord, Integer> entry : m.entrySet()) {
                if ((!maximize && entry.getValue() > bestValue) || (maximize && entry.getValue() < bestValue)) {
                    bestValue = entry.getValue();
                }
            }
            return bestValue;
        }
    }


    public Coord minMax() {
        List<Coord> e = empties();
        HashMap<Coord, Integer> m = new HashMap<Coord, Integer>();
        for (Coord coord : e) {
            m.put(coord, scoreForMove(coord, true, 0));
        }
        int bestValue = -1000000;
        Coord bestCoord = new Coord(-1, -1);
        for (Map.Entry<Coord, Integer> entry : m.entrySet()) {
            if (entry.getValue() > bestValue) {
                bestValue = entry.getValue();
                bestCoord = entry.getKey();
            }
        }
        return bestCoord;
    }

    public void automove() {
        if (!winner.equals("")) return;

        String p = ((playerOneIsX && turn.equals("X")) || ((!playerOneIsX && turn.equals("O")))) ? playerOneName : playerTwoName;

        if (p.equals("Rando")) {
            var l = empties();
            var c = l.get((new Random()).nextInt(l.size()));
            addPiece(turn, c.x, c.y);
        } else if (p.equals("MinMax")) {
            var coord = minMax();
            addPiece(turn, coord.x, coord.y);
        }
    }

    public List<Coord> empties() {
        var l = new ArrayList<Coord>();
        for (var y = 0; y < 3; y++) {
            for (var x = 0; x < 3; x++) {
                if (state[y][x] == null) {
                    l.add(new Coord(x, y));
                }
            }
        }
        return l;
    }

    public void addPiece(String piece, int x, int y) {
        state[y][x] = piece;
        checkGameState();
        if (turn.equals("X")) {
            turn = "O";
        } else {
            turn = "X";
        }
        automove();
    }

    public void togglePlayerX() {
        playerOneIsX = !playerOneIsX;
    }

    public String get(int x, int y) {
        return state[y][x];
    }
}
