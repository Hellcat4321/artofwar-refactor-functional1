package com.mygdx.game.model.gameobjects.buildings;

import static com.mygdx.game.ProjectVariables.*;
import static com.mygdx.game.ProjectVariables.BuildingSpec.*;

import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.mygdx.game.model.maps.Map;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.model.players.Player;

public class Capital extends Building{
    public Capital(
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
        return capitalMoneyPerTurn;
    }

    @Override
    public int getDefence() {
        return capitalDefence;
    }

    @Override
    public StaticTiledMapTile getTile() {
        return capitalPic;
    }

    @Override
    public int getCost() {
        return capitalCost;
    }
}
