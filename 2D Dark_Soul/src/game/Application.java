package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.*;
import game.actors.Player;
import game.ground.*;

/**
 * The main class for the Jurassic World game.
 *
 */
public class Application {

	public static void main(String[] args) {

			World world = new World(new Display());
			FancyGroundFactory groundFactory = new FancyGroundFactory(new Dirt(), new Wall(), new Floor(),
					new Valley() , new Cemetery(), new Vendor(), new BurningGround());

			ArrayList<GameMap> gameMaps = new ArrayList<>();

			MapHandler maps = new MapHandler();
			GameMap gameMap = new GameMap(groundFactory, maps.getMapReference(0));
			world.addGameMap(gameMap);
			gameMaps.add(gameMap);

			Actor player = new Player("Unkindled (Player)", '@', 100, gameMap.at(38,12));
			world.addPlayer(player, gameMap.at(38, 12));

			maps.addMapElements(gameMap, 0);

			GameMap gameMap1 = new GameMap(groundFactory, maps.getMapReference(1));
			world.addGameMap(gameMap1);
			gameMaps.add(gameMap1);
//
			maps.addMapElements(gameMap1, 1);
//

			FogDoor here = new FogDoor("Profane Capital", gameMap.at(38, 25));
			gameMap.at(38, 25).setGround(here);

			FogDoor there = new FogDoor("Anor Londo", gameMap1.at(38, 0));
			gameMap1.at(38,0).setGround(there);

			here.setLink(there);
			there.setLink(here);

			world.run();


	}
}
