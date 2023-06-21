import "./style.css";
import { fromEvent, interval, merge} from 'rxjs'; 
import { map, filter, scan} from 'rxjs/operators';

function main() {
  /**
   * Inside this function you will use the classes and functions from rx.js
   * to add visuals to the svg element in pong.html, animate them, and make them interactive.
   *
   * Study and complete the tasks in observable examples first to get ideas.
   *
   * Course Notes showing Asteroids in FRP: https://tgdwyer.github.io/asteroids/
   *
   * You will be marked on your functional programming style
   * as well as the functionality that you implement.
   *
   * Document your code!
   */

  /**
   * This is the view for your game to add and update your game elements.
   */
  const svg = document.querySelector("#svgCanvas") as SVGElement & HTMLElement;

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //Section 1, initialization of backbone for the game

  //Section 1.1 Typing declaration

  //Define State type, this is the game state which we will change according to user input and time ticks
  //Since we want to ensure pure functional codes, instead of updating each html element individually for every user input and time tick, we
  //will update it at the end when we pass the updated state to the updateView(), and contain the effectful code in there.
  type State = Readonly<{
    frog : Body
    planks : ReadonlyArray<Body>
    cars : ReadonlyArray<Body>
    turtles : ReadonlyArray<TurtleBody>
    crocodiles : ReadonlyArray<CrocoBody>
    pointBlock : ReadonlyArray<PointBody>
    frogDied : boolean
    points : number
    highScore : number
  }>

  //Define Body type, this is the main property bag that we will use to move each elements
  //tickX and tickY here describe how will this body move for every time ticks, very useful for updating self moving body every tick
  //id here is to store the id of the element so that we can get HTML element by id in the updateView() function, then we don't need to 
  //define many constant variable to hold our html element
  type Body = Readonly<{
    x : number;
    y : number;
    tickX : number;
    tickY : number;
    id : string;
  }>

  //Define TBody interface for turtle body, extends body as we want to use the properties from the Body type
  //notSafe here is used for checking whether the turtle is safe to step on or not, useful to allow us to identify its current "status", 
  //whether its submerged or not
  interface TBody extends Body {
    notSafe : boolean;
    emergeTime : number;
    submergeLogic : {mod : number, timeAfloat : number};
  }

  //Define CBody interface for crocodile body, extends body as we want to use the properties from the Body type
  //deathZone here describe border of the mouth, an x - coordinate value, mouthOnLeft here will tells us that whether the mouth is at the left side or right side
  //2 of this properties will be used to check whether the frog is "eaten", if the frog is currently on the mouth
  interface CBody extends Body {
    deathZone : number
    mouthOnLeft : boolean
  }

  //Define PBody interface for point body, extends body as we want to use the x, y properties from the Body type
  //pointsObtained here describe whether the frog already collected the point for this point body in current life, useful to stop user to 
  //farm the same point body over and over again as we can don't give the user points based on this property
  //
  //pointToTake here describe how many point this point body will give, technically we don't really need this but for extension purposes,
  //we might as well do it, also allow us to place the point body in places that are hard to reach, and give them more point value, spicing up the gameplay
  interface PBody extends Body {
    pointsObtained : boolean
    pointsToTake : number
  }

  type TurtleBody = Readonly<TBody>
  type CrocoBody = Readonly<CBody>
  type PointBody = Readonly<PBody>

  //This key type here will enforce that we only accept arrow keys from user
  type Key = 'ArrowLeft' | 'ArrowRight' | 'ArrowUp' | 'ArrowDown'

  //This CONSTANT variable will help us to make the code more maintainbale as we will use this constant values to set up our game stage
  const CONSTANT = {MOVE_VALUE : 75, 
                    SPEED_INCREASE : 5, 
                    RIVER_ZONE : {upperBound : 75 , lowerBound : 300},
                    FROG_ID : "froggu",
                    PLANK_ID : "plank",
                    CAR_A_ID : "carA",
                    CAR_B_ID : "carB",
                    CROCO_ID : "croco",
                    TURTLE_ID : "turtle",
                    POINT_A_ID : "pointA",
                    POINT_B_ID : "pointB",
                    POINT_C_ID : "pointC",
                    POINT_D_ID : "pointD",
                    INITIAL_FROG_POS : {x : 300,  y : 525},
                    PLANK_OBJ_POS : {x : 300, y : 225},
                    CAR_OBJ_A_POS : {x : 300, y : 375},
                    CAR_OBJ_B_POS : {x : 300, y : 450},
                    CROCO_OBJ_POS : {x : 300, y : 150}, 
                    TURTLE_OBJ_POS : {x : 300, y : 75},
                    POINT_A_POS : {x : 75, y : 0},
                    POINT_B_POS : {x : 225, y : 0},
                    POINT_C_POS : {x : 375, y : 0},
                    POINT_D_POS : {x : 525, y : 0},
                  }
  
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //Section 2 Handling of all the events


  //Class declaration, for recognizing which event am i looking at
  // This will be useful when i want to update the game state, since i need to do different operation on the game state depends on what events the observable pass in
  class Move {constructor(public readonly x : number, public readonly y : number){}}
  class Tick {constructor(public readonly elapsedTime : number){}}

  //All the event stream goes here
  //gameClock here will be used to generate Tick Object to represent time tick, useful for self moving body, and update of frog status
  const gameClock = interval(10).pipe(map(elapsed=>new Tick(elapsed))),

  //this function here will help us to map observable into Move Object, which is what we will use to move the frog
  keyObservable = <T>(k:Key, result:()=>T)=>
      fromEvent<KeyboardEvent>(document, 'keydown')
      .pipe(filter(({code})=>code === k),
            filter(({repeat})=>!repeat),
            map(result)),
  
  //This for all the movement observables, that will map into Move object
  moveLeft = keyObservable('ArrowLeft', () => new Move(-CONSTANT.MOVE_VALUE, 0)),
  moveRight = keyObservable('ArrowRight', () => new Move(CONSTANT.MOVE_VALUE, 0)),
  moveUp = keyObservable('ArrowUp', () => new Move(0, -CONSTANT.MOVE_VALUE)),
  moveDown = keyObservable('ArrowDown', () => new Move(0, CONSTANT.MOVE_VALUE))

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //Section 3 Initialization of every HTML elements, and Body which we will use to store the "changes", and then process it at the end

  // This function will help in creating a new object of type Body.
  function createBody(newX: number, newY : number, newTickX : number, 
    newTickY : number , newId : string) : Body{
    return {x : newX, y : newY, tickX: newTickX, tickY: newTickY, id : newId}
  }

  //This function will help in creation of new object of type TurtleBody, for turtle
  const createTbody = (newBody : Body) => (safe : boolean, newSubmergeLogic : {mod:number, timeAfloat : number }) => <TurtleBody>{
    ...newBody, notSafe : safe, emergeTime: newSubmergeLogic.timeAfloat, submergeLogic : newSubmergeLogic
  }

  //This function will help in creation of new object of type CrocoBody, for Crocodile
  const createCbody = (newBody : Body) => (newDeathZone : number, mouthLeft : boolean) => <CrocoBody>{
    ...newBody, deathZone : newDeathZone, mouthOnLeft : mouthLeft
  }

  //This function will help in creation of new object of type PointBody, for the point blocks where player score
  const createPbody = (newBody : Body) => (newPoints : number) => <PointBody>{
    ...newBody, pointsObtained : false, pointsToTake : newPoints
  }

  //This function is used to set the attribute of the html element
  //will be very useful for setting many attributes in one go
  //"i" parameter here is interesting since we need to specify that we are passing object bag with key-value pairs, of type string, both key and value
  //if we just state i is of type Object the IDE will scold me since i do not specify what is inside the bag
  function attr(e: Element, i : {[key : string] : string}): void {
    for(const k in i) e.setAttribute(k,i[k])
  }
  
  /////// This section is for setting up all the Body, and appending them the newly created html element to the svg element
  // set the frog
  const frogObj = document.createElementNS(svg.namespaceURI, "rect");
  attr(frogObj, {"width" : "75", "height": "75", "style": "fill: rgb(0,0,255)",
                 "transform":`translate(${CONSTANT.INITIAL_FROG_POS.x},${CONSTANT.INITIAL_FROG_POS.y})`,
                 "id": CONSTANT.FROG_ID})
  const frogBody = createBody(CONSTANT.INITIAL_FROG_POS.x,CONSTANT.INITIAL_FROG_POS.y,0,0,CONSTANT.FROG_ID)
  
  //set the planks
  const plankObj = document.createElementNS(svg.namespaceURI, "rect");
  attr(plankObj, {"width" : "200", "height" : "75", "style" : "fill: rgb(139,69,19)",
                  "transform" : `translate(${CONSTANT.PLANK_OBJ_POS.x},${CONSTANT.PLANK_OBJ_POS.y})`,
                  "id" : CONSTANT.PLANK_ID})
  const plankBody = createBody(CONSTANT.PLANK_OBJ_POS.x,CONSTANT.PLANK_OBJ_POS.y,-2,0,CONSTANT.PLANK_ID)

  //set the cars
  const carObjA = document.createElementNS(svg.namespaceURI, "rect");
  attr(carObjA, {"width" : "200", "height" : "75", "style" : "fill: rgb(238,232,170)",
                 "transform" : `translate(${CONSTANT.CAR_OBJ_A_POS.x}, ${CONSTANT.CAR_OBJ_A_POS.y})`,
                 "id" : CONSTANT.CAR_A_ID})
  const carBodyA = createBody(CONSTANT.CAR_OBJ_A_POS.x,CONSTANT.CAR_OBJ_A_POS.y,-1,0,CONSTANT.CAR_A_ID)

  const carObjB = document.createElementNS(svg.namespaceURI, "rect");
  attr(carObjB, {"width" : "200", "height" : "75", "style" : "fill: rgb(238,232,170)",
                 "transform" : `translate(${CONSTANT.CAR_OBJ_B_POS.x}, ${CONSTANT.CAR_OBJ_B_POS.y})`,
                 "id" : CONSTANT.CAR_B_ID})
  const carBodyB = createBody(CONSTANT.CAR_OBJ_B_POS.x,CONSTANT.CAR_OBJ_B_POS.y,1,0,CONSTANT.CAR_B_ID)

  //set the turtles
  const turtleObj = document.createElementNS(svg.namespaceURI, "rect");
  attr(turtleObj, {"width" : "500", "height" : "75", "style" : "fill: rgb(135,206,235)",
                   "transform" : `translate(${CONSTANT.TURTLE_OBJ_POS.x},${CONSTANT.TURTLE_OBJ_POS.y})`,
                   "id" : CONSTANT.TURTLE_ID})
  const turtleBody = createTbody(createBody(CONSTANT.TURTLE_OBJ_POS.x,CONSTANT.TURTLE_OBJ_POS.y,3,0,CONSTANT.TURTLE_ID))(false, {mod :1500, timeAfloat : 600})

  //set the crocodiles
  const crocodileObj = document.createElementNS(svg.namespaceURI, "rect");
  attr(crocodileObj, {"width" : "400", "height" : "75", "style" : "fill: rgb(0,100,0)",
                      "transform" : `translate(${CONSTANT.CROCO_OBJ_POS.x},${CONSTANT.CROCO_OBJ_POS.y})`,
                      "id" : CONSTANT.CROCO_ID})
  const crocodileBody = createCbody(createBody(CONSTANT.CROCO_OBJ_POS.x,CONSTANT.CROCO_OBJ_POS.y,1,0,CONSTANT.CROCO_ID))(30, false)

  //set the point body
  const pointObjA = document.createElementNS(svg.namespaceURI, "rect");
  attr(pointObjA, {"width" : "75", "height" : "75", "style" : "fill: rgb(241,196,15)",
                    "transform" : `translate(${CONSTANT.POINT_A_POS.x},${CONSTANT.POINT_A_POS.y})`,
                    "id" : CONSTANT.POINT_A_ID})
  const pointBodyA = createPbody(createBody(CONSTANT.POINT_A_POS.x,CONSTANT.POINT_A_POS.y,0,0,CONSTANT.POINT_A_ID))(10)

  const pointObjB = document.createElementNS(svg.namespaceURI, "rect");
  attr(pointObjB, {"width" : "75", "height" : "75", "style" : "fill: rgb(241,196,15)",
                    "transform" : `translate(${CONSTANT.POINT_B_POS.x},${CONSTANT.POINT_B_POS.y})`,
                    "id" : CONSTANT.POINT_B_ID})
  const pointBodyB = createPbody(createBody(CONSTANT.POINT_B_POS.x,CONSTANT.POINT_B_POS.y,0,0,CONSTANT.POINT_B_ID))(10)

  const pointObjC = document.createElementNS(svg.namespaceURI, "rect");
  attr(pointObjC, {"width" : "75", "height" : "75", "style" : "fill: rgb(241,196,15)",
                    "transform" : `translate(${CONSTANT.POINT_C_POS.x},${CONSTANT.POINT_C_POS.y})`,
                    "id" : CONSTANT.POINT_C_ID})
  const pointBodyC = createPbody(createBody(CONSTANT.POINT_C_POS.x,CONSTANT.POINT_C_POS.y,0,0,CONSTANT.POINT_C_ID))(10)

  const pointObjD = document.createElementNS(svg.namespaceURI, "rect");
  attr(pointObjD, {"width" : "75", "height" : "75", "style" : "fill: rgb(241,196,15)",
                    "transform" : `translate(${CONSTANT.POINT_D_POS.x},${CONSTANT.POINT_D_POS.y})`,
                    "id" : CONSTANT.POINT_D_ID})
  const pointBodyD = createPbody(createBody(CONSTANT.POINT_D_POS.x,CONSTANT.POINT_D_POS.y,0,0,CONSTANT.POINT_D_ID))(10)


  //append everything to svg
  svg.appendChild(plankObj)
  svg.append(carObjA)
  svg.appendChild(carObjB);
  svg.appendChild(turtleObj)
  svg.appendChild(crocodileObj)
  svg.appendChild(pointObjA)
  svg.appendChild(pointObjB)
  svg.appendChild(pointObjC)
  svg.appendChild(pointObjD)
  svg.appendChild(frogObj);

  //This is the initial state that we will use at the start of the game
  //Or when the frog died and the game restarts
  const INITIAL_STATE : State = {
    frog : frogBody,
    planks : [plankBody],
    cars : [carBodyA, carBodyB],
    turtles : [turtleBody],
    crocodiles : [crocodileBody],
    pointBlock : [pointBodyA, pointBodyB, pointBodyC, pointBodyD],
    frogDied : false,
    points : 0,
    highScore : 0
  }
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //Section 4 The main section to change the game state

  //This function will help us to wrap Body around the edge of svg element,
  //so that user will have better game experience, also it makes the game easier, and look nicer
  //to deal with wrapping Body horizontally, we will update the x coordinate if the Body is fully out of bound
  //this will create the "wrap around" effect.
  //
  //to deal with not allowing the Body to go out of bound vertically, we will change the y coordinate so that the Body is within the svg element,
  //this will not allow player to "cheese" the game by moving out of bound, avoiding all the obstacle and back in svg element at the opposite side
  function moveBody<T extends Body>(bodyToMove : T): T {
    const newX = bodyToMove.x > svg.clientWidth ? -getWidth(bodyToMove) : 
              bodyToMove.x < -getWidth(bodyToMove) ? svg.clientWidth : bodyToMove.x

    const newY = bodyToMove.y < 0 ? 0: bodyToMove.y >= svg.clientHeight ? 
                  svg.clientHeight - getHeight(bodyToMove) : bodyToMove.y

    return {...bodyToMove, x : newX, y : newY}
  }

  //This function will help us to get the width of the HTML element, useful for dealing with interaction between Body
  function getWidth(b : Body) : number {
    //assume that this object will be there
    return Number(document.getElementById(b.id)!.getAttribute('width'))
  }

  //This function will help us to get the height of the HTML element, useful for dealing with interaction between Body
  function getHeight(b : Body) : number {
    return Number(document.getElementById(b.id)!.getAttribute('height'))
  }

  //This reduceState function is the main function that will change the game state, before passing it to the updateView function to update the html elements
  // We use instanceof here to check whether the Observable is sending in input from users or input based on time.
  // Then we can update the state accordingly
  function reduceState(s:State, e: Move | Tick): State{
    return e instanceof Move ? {...s, 
                                frog : moveBody({...s.frog, x : s.frog.x + e.x, y : s.frog.y + e.y})} 
                                : tick(s, e)
  }


  //The function to take care of what each Body does every tick event
  function tick(s:State, t: Tick) : State{

    // This curried function will be checking whether an object is on top of another object, very useful to check whether to perform the logic for interaction between Body
    const checkObjOnObj = (a:Body) => (b : Body) : boolean => 
      a.x + getWidth(a) > b.x && a.x < b.x + getWidth(b) && a.y === b.y

    // This function is to check whether the frog is standing within the deathzone of crocodile
    function checkFrogEaten(a : Body, b : CrocoBody) : boolean {
      return b.mouthOnLeft ? a.x + getWidth(a) > b.x && a.x < b.x + b.deathZone && a.y === b.y : 
      a.x + getWidth(a) > b.x + getWidth(b) - b.deathZone && a.x < b.x + getWidth(b) && a.y === b.y
    }

    //This function is to check whether the Body is in the river, we will use this to check whether the frog body is in the river
    function checkBodyInRiver(a : Body) : boolean {
      return a.y >= 75  && a.y < 300 && a.x >= 0 && a.x < 600
    }

    //This function help us to allow frog to move along with self moving body every tick, if the frog is standing on it
    //To create the effect of moving together with the self moving Body, i just reuse the tickX and tickY from the Body that the frog is standing on currently
    //Then for each Body it will move differently, according to the direction and speed of the self moving body
    function bodyUpdateFrog(selectedBody : Body) : State{
      return {...s,
              frog : moveBody({...s.frog,
                      x : s.frog.x + selectedBody.tickX,
                      y : s.frog.y + selectedBody.tickY
              })
      }
    }

    //This function will help us to update the frog if the frog is stading on turtle currently
    //This function will call the bodyUpdateFrog() to deal with movement of the frog, then decide whether the frog died or not, if the turtle is currently submerged in river
    function turtleUpdateFrog(selectedTurtle : TurtleBody): State{
      return {...bodyUpdateFrog(selectedTurtle),  
              frogDied : selectedTurtle.notSafe
              }
    }

    //This function will help us to update the frog if the frog is stading on crocodile currently
    //This function will call the bodyUpdateFrog() to deal with movement of the frog, then decide whether the frog died or not, if the the frog is standing in the deathZone,
    //by calling the checkFrogEaten function
    function crocoUpdateFrog(selectedCroco : CrocoBody): State {
      return {...bodyUpdateFrog(selectedCroco),  
              frogDied : checkFrogEaten(s.frog,selectedCroco)
              }
    }

    //This function will help us to kill the frog if the frog is stading on top of it
    //not calling bodyUpdateFrog here since the frog is dead, either way, so no point moving the frog
    function carUpdateFrog(): State {
      return {...s,
              frogDied : true
            }
    }

    //This function here will help us to check whether the frog can collect the point from the point body, 
    //as well as to send the frog back to its initial position, regardless of whether the frog can obtain points or not
    //this will increase the diffculty of the game, because I want to make the player frustrated for landing on the wrong point body :>
    function pointUpdateFrog(selectedPoint : PointBody) : State {
      return {...s,
              frog : {...s.frog, x : CONSTANT.INITIAL_FROG_POS.x, y : CONSTANT.INITIAL_FROG_POS.y},
              points : !selectedPoint.pointsObtained ? s.points + selectedPoint.pointsToTake : s.points
      }
    }


    //This function will update the gameState depending on where the frog is currently
    //Used many ternary here since its easier to understand from developer persepctive
    //The checkBodyInRiver is called only when the frog is currently not standing on any of the self moving Body,
    //as that is the only scenario where the frog will die in the river.
    function updateFrog(selectedPlank : Body | null, 
                        selectedTurtle : TurtleBody | null, 
                        selectedCroco : CrocoBody | null,
                        selectedCar : Body | null, 
                        selectedPoint : PointBody | null) : State
    {
      return selectedPlank ? bodyUpdateFrog(selectedPlank) :
             selectedTurtle ? turtleUpdateFrog(selectedTurtle) :
             selectedCroco ? crocoUpdateFrog(selectedCroco) : 
             selectedCar ? carUpdateFrog() : 
             selectedPoint ?  pointUpdateFrog(selectedPoint) : {...s, frogDied : checkBodyInRiver(s.frog)}
    }

    //This function is used to get the last item from the array
    //Might look useless, but I want to avoid using any someArray[] like accesing calls, therefore i made this
    //Used to ensure that only one self moving Body update the frog at a time
    //Will return null if there is nothing in the array, useful for the ternary operator in the updateFrog function
    function getLastItem<T>(array : ReadonlyArray<T>) : T | null{
      return array.length > 0 ? array.reduce((a : T, b : T): T => b) : null
    }

    //This function here is obtained from the curried function checkObjOnObj,
    //This will be used for checking whether the frog is on top of another Body
    const checkFrogOnObj = checkObjOnObj(s.frog),

    //Here we will try to find which moving body has the frog on top of it.
    //Calling getLastItem to ensure that only one self moving Body is updating the frog
    selectedPlank = getLastItem(s.planks.filter(checkFrogOnObj)),
    selectedCar = getLastItem(s.cars.filter(checkFrogOnObj)),
    selectedTurtle = getLastItem(s.turtles.filter(checkFrogOnObj)),
    selectedCroco = getLastItem(s.crocodiles.filter(checkFrogOnObj)),
    selectedPoint = getLastItem(s.pointBlock.filter(checkFrogOnObj)),

    //here we will update the frog according to the nature of the moving object the frog is standing on
    updatedState = updateFrog(selectedPlank,selectedTurtle,selectedCroco, selectedCar, selectedPoint)

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Section 4.1, Dealing with what the self moving Body does every tick
    
    //This function will be useful in mapping each Body in the array according to the mapping function
    //The only reason i do this is i just want to make my code looks nice, and easily maintainable
    //Useful in dealing with the interaction of bodies every game tick
    function updateBody<T>(array : ReadonlyArray<T>, mappingFunc : (b: T) => T): ReadonlyArray<T>{
      return  array.map(mappingFunc)
    }

    //This function will update the x and y coordinate according to their tickX and tickY value
    //Using generic here so that it accepts all object of type Body, including types extending body
    function movementUpdate<T extends Body>(b : T) : T{
      return moveBody({...b, x : b.x + b.tickX, y : b.y + b.tickY})
    }

    //This function will update coordinate of the turtle bodies, 
    //as well as to change the notSafe status according to the emergeTime value,
    //the update of the emergeTime is done here as well, then all turtle will have different emergeTime
    function turtleUpdate(b : TurtleBody) : TurtleBody{
      return {...movementUpdate(b), notSafe : b.emergeTime <= 0, 
              emergeTime : t.elapsedTime % b.submergeLogic.mod === 0 ? b.submergeLogic.timeAfloat : b.emergeTime - 1}
    }

    //This function will update the pointBody and change the status of pointsObtained depending whether the frog already collected the point
    function pointUpdate(b : PointBody) : PointBody {
      return {...b, pointsObtained : b.pointsObtained || checkFrogOnObj(b)}
    }

    //This function will help us to update the speed of the self moving bodies,
    //I chose to use the constant for specifying speed increase here instead of doing it randomly because
    // 1) Random function are non-pure, and i don't want to risk creating a random function that i thought is pure, but it's not
    // 2) I can control the speed increase by changing the constant value, and if I really don't want players to have fun, i will increase the speed change accordingly :> 
    function increaseSpeed<T extends Body>(b : T) : T {
      return {...b,
              tickX : b.tickX > 0 ? b.tickX + CONSTANT.SPEED_INCREASE : b.tickX < 0 ? b.tickX - CONSTANT.SPEED_INCREASE : b.tickX,
              tickY : b.tickY > 0 ? b.tickY + CONSTANT.SPEED_INCREASE : b.tickY < 0 ? b.tickY - CONSTANT.SPEED_INCREASE : b.tickY
      }
    }

    //This function will reset the status of point block, so that player can collect points from the point block again
    function resetPoint(b : PointBody): PointBody {
      return {...b, pointsObtained : false}
    }

    //This function is to help us to check whether all the points of all point block were collected
    //Very useful when we want to decide whether to increase difficulty of the game or not
    function goalMet(b : ReadonlyArray<PointBody>) : boolean {
      //map to boolean, then reduce
      return b.map((x : PointBody) => x.pointsObtained)
      .reduce((a : boolean, b : boolean): boolean => a && b)
    }

    //return the updated state for frog, and update every other Body accordingly, with the submerge time logic
    //using ternary function here to deal with 3 scenario, to increase the readibility of the code
    // 1) frog died
    // 2) goal met
    // 3) nothing happen
    return updatedState.frogDied ? {...INITIAL_STATE, highScore : updatedState.points > updatedState.highScore ? updatedState.points : updatedState.highScore} 
          : goalMet(updateBody(s.pointBlock, pointUpdate)) ? 
          {...updatedState, 
            planks : updateBody(updateBody(s.planks, movementUpdate), increaseSpeed), 
            cars : updateBody(updateBody(s.cars, movementUpdate), increaseSpeed), turtles : updateBody(updateBody(s.turtles, turtleUpdate), increaseSpeed), 
            crocodiles : updateBody(updateBody(s.crocodiles, movementUpdate), increaseSpeed),
            pointBlock : updateBody(updateBody(s.pointBlock, pointUpdate), resetPoint),
          } :

          {...updatedState, 
            planks : updateBody(s.planks, movementUpdate), 
            cars : updateBody(s.cars, movementUpdate), turtles : updateBody(s.turtles, turtleUpdate), 
            crocodiles : updateBody(s.crocodiles, movementUpdate),
            pointBlock : updateBody(s.pointBlock, pointUpdate),
          }
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //Section 5 : The impure among the pure function


  //The impure function that change the attribute of the svg element
  function updateView(s:State) : void {

    //This function will deal with the update of the html element 
    function updateBodyView(b : Body) : void {
      const elemSelected = document.getElementById(b.id)!
      elemSelected.setAttribute('transform', `translate(${b.x},${b.y})`)
    }


    //This function will deal with the update of the turtle html element
    //Update the colour if the turtle submerged
    function updateTurtleView(t : TurtleBody) : void {
      updateBodyView(t)
      const turtleSelected = document.getElementById(t.id)!
      t.notSafe ? turtleSelected.setAttribute("style","fill: rgb(139,0,0);") : 
      turtleSelected.setAttribute("style","fill: rgb(135,206,235);")
    }

    //This function will deal with the update of the point html element
    // Update the colour if the point block was collected
    function updatePointBlockView(p : PointBody) : void {
      const pointSelected = document.getElementById(p.id)!
      p.pointsObtained ? pointSelected.setAttribute("style","fill: rgb(241,148,138);") :
      pointSelected.setAttribute("style","fill: rgb(241,196,15);")
    }

    //This section will deal with the impure update of the html element  
    updateBodyView(s.frog)
    s.planks.forEach(updateBodyView)
    s.cars.forEach(updateBodyView)
    s.crocodiles.forEach(updateBodyView)
    s.turtles.forEach(updateTurtleView)
    s.pointBlock.forEach(updatePointBlockView)

    //This section will deal with the scoring of game session
    document.getElementById("score")!.innerHTML = `Points Obtained : ${s.points}`
    document.getElementById("hScore")!.innerHTML = `High Score : ${s.highScore}`
  }

  //The subscription of the observable
  merge(gameClock,moveLeft,moveRight,moveUp,moveDown).pipe(scan(reduceState, INITIAL_STATE)).subscribe(updateView)
}

// The following simply runs your main function on window load.  Make sure to leave it in place.
if (typeof window !== "undefined") {
  window.onload = () => {
    main();
  };
}
