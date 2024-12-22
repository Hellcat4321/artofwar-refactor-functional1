package com.mygdx.game.model;

import com.mygdx.game.db.GameDatabase;
import com.mygdx.game.model.gameobjects.GameObject;
import com.mygdx.game.model.gameobjects.buildings.Capital;
import com.mygdx.game.model.gameobjects.units.Unit;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.utils.TurnState;
import com.mygdx.game.model.maps.Map;
import com.mygdx.game.model.players.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


public class GamingProcess {
    private int gameId;
    private int round;
    private int currentPlayer;

    private final Map map;
    private GameDatabase gameDatabase;
    private List<Player> players;

    private GameObject gameObjectToPlace = null;
    private Unit unitToMove = null;

    public GamingProcess(Map map, GameDatabase gameDatabase) {
        this.map = map;
        this.currentPlayer = 0;
        this.round = 0;
        this.gameDatabase = gameDatabase;
    }


    public void setGameObjectSelection(GameObject gameObjectToPlace) {
        this.gameObjectToPlace = gameObjectToPlace;
    }

    public void setUnitSelection(Unit unit) {
        unitToMove = unit;
    }


    public Unit getUnitSelection() {
        return unitToMove;
    }

    public GameObject getGameObjectSelection() {
        return gameObjectToPlace;
    }


    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    private void nextRound() {
        players = players.stream().filter(player -> !player.isDone()).collect(Collectors.toList());
        ++round;
    }

    private void killGameObject(GameObject gameObject) {
        Player player = gameObject.owner;
        player.removeGameObject(gameObject);
        map.removeGameObject(gameObject);
    }

    public void wipePlayerArmy(Player player) {
        player.getUnits().forEach(map::removeGameObject);
        player.armyWipe();

        map.recountDefenceCoverage(players);
    }

    public void createCapitalArea(Player player, int x, int y) {
        Capital capital = new Capital(map, null, player);
        player.addGameObject(capital);

        map.setGameObject(capital, x, y);
        map.createCapitalArea(capital, x, y);
    }


    public void placeNewGameObjectOnCell(GameObject gameObject, int x, int y) {
        MapCell placeTo = map.getCell(x, y);

        Player owner = gameObject.owner;
        owner.addGameObject(gameObject);

        if (placeTo.getGameObject() != null) {
            killGameObject(placeTo.getGameObject());
        }

        map.setGameObject(gameObject, x, y);
        map.recountDefenceCoverage(players);
    }

    public void moveUnit(Unit unit, int x, int y) {
        MapCell moveTo = map.getCell(x, y);

        if (moveTo == null) return;
        if (!unit.canMove(moveTo)) return;

        if (moveTo.getGameObject() != null) {
            killGameObject(moveTo.getGameObject());
        }

        if (unit.getPlacement() != null) {
            map.removeGameObject(unit);
        }

        map.setGameObject(unit, x, y);
        map.recountDefenceCoverage(players);

        unit.setMoved(true);
    }

    private void nextPlayer() {
        if (isLast()) {
            nextRound();
            currentPlayer = 0;
            return;
        }
        currentPlayer = (++currentPlayer) % players.size();
    }

    public TurnState nextTurn() {
        nextPlayer();
        if (players.size() < 2) {
            insertGameIntoDB();
            return TurnState.FINISH;
        }
        if (round == 0) return TurnState.CAPITAL;
        Player player = getCurrentPlayer();
        if (player.isDone()) {
            insertTurnInfoIntoDB(player, round);
            return nextTurn();
        }

        if (!player.countIncome()) {
            wipePlayerArmy(player);
        }
        player.refreshUnits();

        insertTurnInfoIntoDB(player, round);

        if (player.getCapital() == null) {
            setGameObjectSelection(new Capital(map, null, player));
            return TurnState.CAPITAL;
        }

        return TurnState.OK;
    }

    private void insertTurnInfoIntoDB(Player player, int round) {
        try {
            gameDatabase.insertTurn(player.getId(), gameId, round, player.getGold(), player.getTerritories());
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void insertGameIntoDB() {
        try {
            gameDatabase.finishGame(gameId);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean isFirst() {
        return currentPlayer == 0;
    }

    public boolean isLast() {
        return currentPlayer == players.size() - 1;
    }

    public void setId(int id) {
        this.gameId = id;
    }

    public int getGameId() {
        return gameId;
    }

    public int getRound() {
        return round;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
        this.players.forEach(player -> player.setGamingProcess(this));
    }

    public Map getMap() {
        return map;
    }
}
