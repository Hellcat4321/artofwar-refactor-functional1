package com.mygdx.game.model.gameobjects;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.mygdx.game.model.maps.CellType;
import com.mygdx.game.model.maps.Map;
import com.mygdx.game.model.maps.MapCreator;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.model.players.Player;

public abstract class GameObject {
    private final Map map;
    private MapCell placement;
    public final Player owner;

    public GameObject(
            Map map,
            MapCell placement,
            Player owner
    ) {
        this.map = map;
        this.placement = placement;
        this.owner = owner;
    }

    public Map getMap() {
        return map;
    }

    public MapCell getPlacement() {
        return placement;
    }

    public void setPlacement(MapCell placement) {
        if(placement.getType() == CellType.WATER) return;
        placement.setGameObject(this);
        this.placement = placement;
    }
    public abstract int getMoneyPerTurn();
    public abstract int getDefence();
    public abstract int getCost();
    public abstract TiledMapTile getTile();
}
