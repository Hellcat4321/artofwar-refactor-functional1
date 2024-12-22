package com.mygdx.game.model.gameobjects.buildings;

import static com.mygdx.game.ProjectVariables.*;
import static com.mygdx.game.ProjectVariables.BuildingSpec.*;

import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.mygdx.game.model.maps.Map;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.model.players.Player;

public class SuperTower extends Building{
    public SuperTower(
            Map Map,
            MapCell placement,
            Player owner
    ){
        super(
                Map,
                placement,
                owner
        );
    }

    @Override
    public int getMoneyPerTurn() {
        return superTowerMoneyPerTurn;
    }

    @Override
    public int getDefence() {
        return superTowerDefence;
    }

    @Override
    public StaticTiledMapTile getTile() {
        return superTowerPic;
    }

    @Override
    public int getCost() {
        return superTowerCost;
    }
}
