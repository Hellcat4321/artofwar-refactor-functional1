package com.mygdx.game.model.players;

import com.mygdx.game.ProjectVariables;
import com.mygdx.game.model.GamingProcess;
import com.mygdx.game.model.gameobjects.GameObject;
import com.mygdx.game.model.gameobjects.buildings.Building;
import com.mygdx.game.model.gameobjects.buildings.Capital;
import com.mygdx.game.model.gameobjects.buildings.Farm;
import com.mygdx.game.model.gameobjects.units.Unit;
import com.mygdx.game.model.maps.*;
import com.mygdx.game.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    private int id = -1;
    private GamingProcess gamingProcess;
    public static final Player NOBODY = new Player("", null);
    public final Border border;
    public final String name;
    private List<Building> buildings;
    private List<Unit> units;
    private List<Farm> farms;

    boolean done = false;
    private Capital capital;
    private int gold = 0;
    private int territory = 0;

    public Player(String name, Border border) {
        this.border = border;
        this.name = name;
        buildings = new ArrayList<>();
        units = new ArrayList<>();
        farms = new ArrayList<>();
    }

    public Player(Player player) {
        this.id = player.id;
        this.gamingProcess = player.gamingProcess;
        this.border = player.border;
        this.name = player.name;
        this.buildings = player.buildings;
        this.units = player.units;
        this.farms = player.farms;

        this.done = player.done;
        this.capital = player.capital;
        this.gold = player.gold;
        this.territory = player.territory;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFarmsNumber() {
        return farms.size();
    }

    public void removeGameObject(GameObject gameObject) {
        if (gameObject instanceof Unit) {
            units = ListUtils.removeObject(units, List.of((Unit) gameObject));
        }
        if (gameObject instanceof Building) {
            if (gameObject instanceof Capital) {
                this.capital = null;
            }
            if (gameObject instanceof Farm) {
                farms = ListUtils.removeObject(farms, List.of((Farm) gameObject));

            } else {
                buildings = ListUtils.removeObject(buildings, List.of((Building) gameObject));
            }
        }
    }

    public void addGameObject(GameObject gameObject) {
        gold -= gameObject.getCost();
        if (gameObject instanceof Unit) {
            units = ListUtils.addObject(units, (Unit) gameObject);
        }
        if (gameObject instanceof Building) {
            if (gameObject instanceof Capital) {
                capital = (Capital) gameObject;
            }
            if (gameObject instanceof Farm) {
                farms = ListUtils.addObject(farms, (Farm) gameObject);
            } else {
                buildings = ListUtils.addObject(buildings, (Building) gameObject);
            }
        }
    }

    public void armyWipe() {
        gold = 0;
    }

    public void refreshUnits() {
        units.forEach(unit -> unit.setMoved(false));
    }

    public boolean countIncome() {
        if (capital != null) {
            gold += getFarmsNumber() * ProjectVariables.BuildingSpec.farmMoneyPerTurn;
            gold += capital.getMoneyPerTurn();
            gold += territory;
        }
        for (Building building : buildings) {
            gold += building.getMoneyPerTurn();
        }
        for (Unit unit : units) {
            gold += unit.getMoneyPerTurn();
        }
        if (gold < 0) {
            armyWipe();
            gold = 0;
            return false;
        }
        return true;
    }

    public boolean isDone() {
        return done;
    }

    public void addTerritory() {
        ++territory;
    }

    public void removeTerritory() {
        --territory;
        if (territory == 0) done = true;
    }

    public Capital getCapital() {
        return capital;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public int getGold() {
        return gold;
    }

    public int getId() {
        return id;
    }

    public int getTerritories() {
        return territory;
    }

    public void setGamingProcess(GamingProcess gamingProcess) {
        this.gamingProcess = gamingProcess;
    }
}
