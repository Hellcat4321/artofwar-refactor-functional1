package com.mygdx.game.model.maps;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.mygdx.game.ProjectVariables;
import com.mygdx.game.model.gameobjects.GameObject;
import com.mygdx.game.model.maps.*;
import com.mygdx.game.model.players.Player;

import static com.mygdx.game.ProjectVariables.tileHeight;
import static com.mygdx.game.ProjectVariables.tileWidth;

public class MapToRendererTransformator {
    private final HexagonalTiledMapRenderer renderer;
    private TiledMap tiledMap;
    private Map map;

    public MapToRendererTransformator(Map map) {
        this.map = map;
        loadNewTiledMap();
        renderer = new HexagonalTiledMapRenderer(tiledMap);
    }

    private void loadNewTiledMap() {
        tiledMap = new TiledMap();
        createMapLayer();
        createBorders();
        createGameObjectsLayer();
    }

    private void createMapLayer() {
        TiledMapTileLayer generalMapLayer = new TiledMapTileLayer(map.getWidth(), map.getHeight(), tileWidth, tileHeight);
        for (int i = 0; i < map.getWidth(); ++i) {
            for (int j = 0; j < map.getHeight(); ++j) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(map.getMapCreator().getCells()[i][j].getType().tile());
                generalMapLayer.setCell(i, j, cell);
            }
        }
        tiledMap.getLayers().add(generalMapLayer);
    }

    private void createGameObjectsLayer() {
        TiledMapTileLayer gameObjectsLayer = new TiledMapTileLayer(map.getWidth(), map.getHeight(), tileWidth, tileHeight);
        for (int i = 0; i < map.getWidth(); ++i) {
            for (int j = 0; j < map.getHeight(); ++j) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                GameObject obj = map.getMapCreator().getCells()[i][j].getGameObject();
                gameObjectsLayer.setCell(i, j, cell);
                if (obj == null) continue;
                cell.setTile(obj.getTile());
            }
        }
        tiledMap.getLayers().add(gameObjectsLayer);
    }

    private void createBorders() {
        TiledMapTileLayer[] borders = new TiledMapTileLayer[6];
        for (int i = 0; i < 6; ++i) {
            borders[i] = new TiledMapTileLayer(map.getWidth(), map.getHeight(), tileWidth, tileHeight);
            tiledMap.getLayers().add(borders[i]);
        }
        for (int i = 0; i < map.getWidth(); ++i) {
            for (int j = 0; j < map.getHeight(); ++j) {
                for (int k = 0; k < 6; ++k) {
                    TiledMapTileLayer.Cell tiledMapCell = new TiledMapTileLayer.Cell();
                    borders[k].setCell(i, j, tiledMapCell);
                    Border.flipCell(tiledMapCell, k);
                }
                setBorders(i, j);
            }
        }
    }

    public void updateBorders(int x, int y) {
        setBorders(x, y);
        int[][] nb = MapCreator.getNeighbours(x);
        for (int i = 0; i < 6; ++i) {
            MapCell cell = map.getCell(x + nb[i][0], y + nb[i][1]);
            if (cell == null) continue;
            setBorders(cell.x, cell.y);
        }
    }

    private void setBorders(int x, int y) {
        MapCell curCell = map.getCell(x, y);
        int nb[][] = MapCreator.getNeighbours(x);
        if (curCell.getType() == CellType.WATER) return;
        if (curCell.getOwner() == Player.NOBODY) return;
        for (int k = 0; k < 6; ++k) {
            MapCell cell = map.getCell(x + nb[k][0], y + nb[k][1]);
            TiledMapTileLayer.Cell cell1 = ((TiledMapTileLayer) tiledMap.getLayers().get(k + 1)).getCell(x, y);
            if (cell == null || cell.getOwner() != curCell.getOwner()) {
                cell1.setTile(curCell.getOwner().border.getTile(k));
            } else cell1.setTile(null);
        }
    }

    public void createSelectedArea(int[][] area) {
        TiledMapTileLayer black = new TiledMapTileLayer(map.getWidth(), map.getHeight(), tileWidth, tileHeight);
        black.setName("selected");
        for (int i = 0; i < map.getWidth(); ++i) {
            for (int j = 0; j < map.getHeight(); ++j) {
                if (area[i][j] != -1) continue;
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(ProjectVariables.blackTile);
                black.setCell(i, j, cell);
            }
        }
        black.setOpacity(0.7f);
        tiledMap.getLayers().add(black);
    }

    public void clearSelectedArea() {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("selected");
        if (layer != null) tiledMap.getLayers().remove(layer);

    }

    public void update(int x, int y) {
        if (x < 0 || x >= map.getWidth() || y < 0 || y >= map.getHeight()) return;
        TiledMapTileLayer gameObjectsLayer = (TiledMapTileLayer) tiledMap.getLayers().get(7);
        MapCell mapCell = map.getCell(x, y);
        TiledMapTileLayer.Cell cell = gameObjectsLayer.getCell(x, y);
        if (mapCell.getGameObject() == null) {
            cell.setTile(null);
            return;
        }
        gameObjectsLayer.getCell(x, y).setTile(map.getCell(x, y).getGameObject().getTile());
        updateBorders(x,y);
    }

    public HexagonalTiledMapRenderer getRenderer() {
        return renderer;
    }
}
