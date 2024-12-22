package com.mygdx.game.model.maps;


import com.mygdx.game.model.gameobjects.GameObject;
import com.mygdx.game.model.players.Player;

public class MapCell {
    private CellType type;
    private double elevation;
    private double humidity;
    private int defence;
    public final int x, y;
    private Player owner;
    private GameObject gameObject;

    public MapCell(int x, int y) {
        this.type = CellType.UNDEFINED;
        this.elevation = -1;
        this.humidity = -1;
        this.x = x;
        this.y = y;
    }

    public MapCell(MapCell mapCell){
        this.type = mapCell.type;
        this.elevation = mapCell.elevation;
        this.humidity = mapCell.humidity;
        this.defence = mapCell.defence;
        this.x = mapCell.x;
        this.y = mapCell.y;
        this.owner = mapCell.owner;
        this.gameObject = mapCell.gameObject;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getElevation() {
        return elevation;
    }

    public CellType getType() {
        return type;
    }

    public Player getOwner() {
        return owner;
    }

    public int getDefence() {
        return defence;
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public void setGameObject(GameObject gameObject) {
        if (type == CellType.WATER) return;
        this.gameObject = gameObject;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public void setType(CellType type) {
        if (type == CellType.UNDEFINED) return;
        this.type = type;
    }

    public void setOwner(Player owner) {
        if (this.owner != null) this.owner.removeTerritory();
        this.owner = owner;
        this.owner.addTerritory();
    }
}
