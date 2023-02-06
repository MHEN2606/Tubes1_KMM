package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;
    private GameObject worldCenter;
    private boolean burner = false;

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }


    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        Position centerPoint = new Position(0,0);
        worldCenter = new GameObject(null, null, null, null, centerPoint, null, null);
        playerAction.action = PlayerActions.FORWARD;
        playerAction.heading = new Random().nextInt(360);
        System.out.println("TIME TO COMPUTE AT TICK " + gameState.getWorld().getCurrentTick());
        if(!this.gameState.getGameObjects().isEmpty()){
            var foodList = gameState.getGameObjects().stream()
                .filter(food -> food.getGameObjectType() == ObjectTypes.FOOD)
                .sorted(Comparator.comparing(food -> getDistanceBetween(this.bot, food)))
                .collect(Collectors.toList());

            var gasList = gameState.getGameObjects().stream()
                .filter(obs -> obs.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                .sorted(Comparator.comparing(obs -> getDistanceBetween(this.bot, obs)))
                .collect(Collectors.toList());

            var astList = gameState.getGameObjects().stream()
                .filter(ast -> ast.getGameObjectType() == ObjectTypes.ASTEROID_FIELD)
                .sorted(Comparator.comparing(ast -> getDistanceBetween(this.bot, ast)))
                .collect(Collectors.toList());
            
            var enemyList = gameState.getPlayerGameObjects().stream()
                .filter(enemy -> enemy.getId() != this.bot.getId())
                .sorted(Comparator.comparing(enemy -> getDistanceBetween(this.bot, enemy)))
                .collect(Collectors.toList());

            var torpedoList = gameState.getGameObjects().stream()
                .filter(torpedo -> torpedo.getGameObjectType() == ObjectTypes.TORPEDO_SALVO && torpedo.currentHeading != this.bot.currentHeading)
                .sorted(Comparator.comparing(torpedo -> getDistanceBetween(this.bot, torpedo)))
                .collect(Collectors.toList());

            System.out.println("NEAREST ASTEROID " + getDistanceBetween(astList.get(0), bot));
            System.out.println("NEAREST GAS " +  getDistanceBetween(gasList.get(0), bot));
            System.out.println("NEAREST PLAYER " + getDistanceBetween(enemyList.get(0), bot));

            /* GREEDY BY POSITION */

            /* Default action, cari makan */
            playerAction.heading = getHeadingBetween(foodList.get(0));
            System.out.println("MAKAN");

            /* Kondisi jika ada player obstacle dekat dengan kita 
             * Putar setiap 30 derajat terhadap obstacle tsb lalu maju, berharap tidak nabrak
            */
            if(getDistanceBetween(this.bot, gasList.get(0)) < 30 || getDistanceBetween(this.bot, astList.get(0)) < 30 ) {
                System.out.println("MENGHINDAR DARI GAS CLOUD / ASTEROID");
                playerAction.heading = (playerAction.getHeading() + 30) % 360;
            }

            /* Kondisi kalau ada player lain dekat dengan kita */
            if(getDistanceBetween(this.bot, enemyList.get(0)) + (1.5 * this.bot.getSize()) < 100) {
                System.out.println("ADA PLAYER DEKAT KU");
                if(enemyList.get(0).getSize() < this.bot.getSize()) {
                    playerAction.heading = getHeadingBetween(enemyList.get(0));
                    if(this.bot.getSize() > 100 && getDistanceBetween(enemyList.get(0), bot) < 50){
                        playerAction.action = PlayerActions.FIRETORPEDOES;
                    }
                    System.out.println("SERANG");
                }else if(enemyList.get(0).getSize() >= this.bot.getSize()){
                    playerAction.heading = (getHeadingBetween(enemyList.get(0)) + 180) % 360;
                    System.out.println("KABUR");
                }
            }

            /* Kalau tersisa cmn dua player lagi */
            if(enemyList.size() == 1){
                System.out.println("Tinggal berdua nih!");
                if(enemyList.get(0).getSize() < this.bot.getSize()) {
                    playerAction.heading = getHeadingBetween(enemyList.get(0));
                    if(this.bot.getSize() > 100) {
                        playerAction.action = PlayerActions.FIRETORPEDOES;
                    }
                    System.out.println("SERANG");
                }else if(enemyList.get(0).getSize() >= this.bot.getSize()){
                    playerAction.heading = (enemyList.get(0).currentHeading);
                    if(getDistanceBetween(enemyList.get(0), bot) < 100){
                        playerAction.action = PlayerActions.STARTAFTERBURNER;
                        burner = true;
                    }else{
                        playerAction.action = PlayerActions.FORWARD;
                    }
                    System.out.println("KABUR");
                }
                if(burner = true){
                    burner = false;
                    playerAction.action = PlayerActions.STOPAFTERBURNER;
                }
            }
            
            /* Kondisi jika posisi dekat dengan edge of the world! 
                Cek apakah ada di edge atau tidak */
            if(getDistanceBetween(this.bot, worldCenter)  + (1.5 * this.bot.getSize()) >= gameState.getWorld().radius){
                playerAction.heading = getHeadingBetween(worldCenter);
                System.out.println("Di ujung peta. Kembali ke pusat!");
            }
        }

        this.playerAction = playerAction;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }


}
