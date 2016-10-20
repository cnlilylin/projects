# # multiAgents.py
# # --------------
# # Licensing Information:  You are free to use or extend these projects for
# # educational purposes provided that (1) you do not distribute or publish
# # solutions, (2) you retain this notice, and (3) you provide clear
# # attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# # 
# # Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# # The core projects and autograders were primarily created by John DeNero
# # (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# # Student side autograding was added by Brad Miller, Nick Hay, and
# # Pieter Abbeel (pabbeel@cs.berkeley.edu).


# from util import manhattanDistance
# from game import Directions
# import random, util

# from game import Agent

# INFINITY=1000000000.0

# class ReflexAgent(Agent):
#     """
#       A reflex agent chooses an action at each choice point by examining
#       its alternatives via a state evaluation function.

#       The code below is provided as a guide.  You are welcome to change
#       it in any way you see fit, so long as you don't touch our method
#       headers.
#     """


#     def getAction(self, gameState):
#         """
#         You do not need to change this method, but you're welcome to.

#         getAction chooses among the best options according to the evaluation function.

#         Just like in the previous project, getAction takes a GameState and returns
#         some Directions.X for some X in the set {North, South, West, East, Stop}
#         """
#         # Collect legal moves and successor states
#         legalMoves = gameState.getLegalActions()

#         # Choose one of the best actions
#         scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
#         bestScore = max(scores)
#         bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
#         chosenIndex = random.choice(bestIndices) # Pick randomly among the best

#         "Add more of your code here if you want to"

#         return legalMoves[chosenIndex]

#     def evaluationFunction(self, currentGameState, action):
#         """
#         Design a better evaluation function here.

#         The evaluation function takes in the current and proposed successor
#         GameStates (pacman.py) and returns a number, where higher numbers are better.

#         The code below extracts some useful information from the state, like the
#         remaining food (newFood) and Pacman position after moving (newPos).
#         newScaredTimes holds the number of moves that each ghost will remain
#         scared because of Pacman having eaten a power pellet.

#         Print out these variables to see what you're getting, then combine them
#         to create a masterful evaluation function.
#         """
#         # Useful information you can extract from a GameState (pacman.py)
#         successorGameState = currentGameState.generatePacmanSuccessor(action)
#         newPos = successorGameState.getPacmanPosition()
#         newFood = successorGameState.getFood()
#         newGhostStates = successorGameState.getGhostStates()
#         newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

#         "*** YOUR CODE HERE ***"
#         if action == 'Stop':
#           return -9999
#         ghostAlarm = [manhattanDistance(ghost.getPosition(),newPos)<=0 for ghost in newGhostStates if ghost.scaredTimer <= 0]
#         if True in ghostAlarm :
#           return -999999
#         foodList = currentGameState.getFood().asList()
#         return currentGameState.getScore() - min([manhattanDistance(food,newPos) for food in foodList]) - len(foodList)*10

# def scoreEvaluationFunction(currentGameState):
#     """
#       This default evaluation function just returns the score of the state.
#       The score is the same one displayed in the Pacman GUI.

#       This evaluation function is meant for use with adversarial search agents
#       (not reflex agents).
#     """
#     return currentGameState.getScore()

# class MultiAgentSearchAgent(Agent):
#     """
#       This class provides some common elements to all of your
#       multi-agent searchers.  Any methods defined here will be available
#       to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

#       You *do not* need to make any changes here, but you can if you want to
#       add functionality to all your adversarial search agents.  Please do not
#       remove anything, however.

#       Note: this is an abstract class: one that should not be instantiated.  It's
#       only partially specified, and designed to be extended.  Agent (game.py)
#       is another abstract class.

#       EDIT: I added the following helper functions:
#         getMaxVal--returns the maximum value of the legal actions
#         getMinVal--returns the minimum values of the legal actions
#         getAverageVal--returns the average value of legal actions
#     """

#     def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
#         self.index = 0 # Pacman is always agent index 0
#         self.evaluationFunction = util.lookup(evalFn, globals())
#         self.depth = int(depth)

#     def getMaxVal(self, gameState, agentIndex, depth, alpha=None, beta=None):
#         val=-10000

#         actions=gameState.getLegalActions(agentIndex)
#         nextAgent=agentIndex+1
#         nextDepth=depth
#         if nextAgent==gameState.getNumAgents():
#             nextDepth+=1
#             nextAgent=0


#         for action in actions:
#             nextState=gameState.generateSuccessor(agentIndex, action)
#             nextVal=self.getVal(nextState,nextAgent, nextDepth, alpha,beta)
#             # print(action, nextVal)
#             val=max(val,nextVal)
#             if beta !=None and val>beta:
#                 return val
#             if alpha !=None:
#                 alpha=max(alpha,val)
#             # print("agent {} is getting max result {}. alpha={}, beta={}".format(agentIndex, val, alpha, beta))

#         return val

#     def getMinVal(self, gameState, agentIndex, depth, alpha=None, beta=None):
#         val=10000
#         actions=gameState.getLegalActions(agentIndex)

#         nextAgent=agentIndex+1
#         nextDepth=depth
#         if nextAgent==gameState.getNumAgents():
#             nextDepth+=1
#             nextAgent=0

#         for action in actions:
#             nextState=gameState.generateSuccessor(agentIndex, action)
#             nextVal=self.getVal(nextState,nextAgent, nextDepth, alpha,beta)
#             # print(action, nextVal)
#             val=min(val,nextVal)
#             if alpha !=None and val<alpha:
#                 return val
#             if beta !=None:
#                 beta=min(beta,val)
#             # print("agent {} is getting min result {}. alpha={}, beta={}".format(agentIndex, val, alpha, beta))
#         return val

#     def getAverageVal(self, gameState, agentIndex, depth):
#         actions=gameState.getLegalActions(agentIndex)

#         nextAgent=agentIndex+1
#         nextDepth=depth
#         if nextAgent==gameState.getNumAgents():
#             nextDepth+=1
#             nextAgent=0
#         valList=[]
#         for action in actions:
#             nextState=gameState.generateSuccessor(agentIndex, action)
#             nextVal=self.getVal(nextState,nextAgent, nextDepth)
#             # print(action, nextVal)
#             valList.append(nextVal)
#         return sum(valList) / float(len(valList))

#     def evaluate(self,gameState):
#         return self.evaluationFunction(gameState)


# class MinimaxAgent(MultiAgentSearchAgent):
#     """
#       Your minimax agent (question 2)
#     """

#     def getAction(self, gameState):
#         """
#           Returns the minimax action from the current gameState using self.depth
#           and self.evaluationFunction.

#           Here are some method calls that might be useful when implementing minimax.

#           gameState.getLegalActions(agentIndex):
#             Returns a list of legal actions for an agent
#             agentIndex=0 means Pacman, ghosts are >= 1

#           gameState.generateSuccessor(agentIndex, action):
#             Returns the successor game state after an agent takes an action

#           gameState.getNumAgents():
#             Returns the total number of agents in the game

#           gameState.isWin():
#             Returns whether or not the game state is a winning state

#           gameState.isLose():
#             Returns whether or not the game state is a losing state
#         """
#         "*** YOUR CODE HERE ***"
#         actions=gameState.getLegalActions(0)
#         bestVal,bestAction=-100000.0,actions[0]
#         for action in actions:
#             nextState=gameState.generateSuccessor(0, action)
#             nextVal=self.getVal(nextState, 1, 0)
#             if nextVal>bestVal:
#                 bestVal=nextVal
#                 bestAction=action

#         return bestAction

#     def getVal(self, gameState, agentIndex, depth,alpha=None,beta=None):
#         if depth==self.depth or gameState.isWin() or gameState.isLose():
#             # print("evaluating state at agent {} depth {} : {}".format(agentIndex, depth, evaluate(gameState)))
#             return self.evaluate(gameState)

#         if agentIndex%gameState.getNumAgents()==0: #is pacman
#             result=self.getMaxVal(gameState, agentIndex, depth)
#         else:
#             result= self.getMinVal(gameState, agentIndex, depth)

#         return result


# class AlphaBetaAgent(MultiAgentSearchAgent):
#     """
#       Your minimax agent with alpha-beta pruning (question 3)
#     """

#     def getAction(self, gameState):
#         """
#           Returns the minimax action using self.depth and self.evaluationFunction
#         """
#         "*** YOUR CODE HERE ***"

#         actions=gameState.getLegalActions(0)
#         bestVal,bestAction=-1000000.0,actions[0]
#         alpha,beta=-INFINITY, INFINITY
#         for action in actions:
#             nextState=gameState.generateSuccessor(0, action)
#             nextVal=self.getVal(nextState, 1, 0,alpha,beta)
#             if nextVal>bestVal:
#                 bestVal=nextVal
#                 bestAction=action
#             alpha=max(alpha,bestVal)
#         return bestAction

#     def getVal(self, gameState, agentIndex, depth,alpha=None,beta=None):
#         if depth==self.depth or gameState.isWin() or gameState.isLose():
#             # print("evaluating state at agent {} depth {} : {}".format(agentIndex, depth, evaluate(gameState)))
#             return self.evaluate(gameState)

#         if agentIndex%gameState.getNumAgents()==0: #is pacman
#             result=self.getMaxVal(gameState, agentIndex, depth,alpha,beta)
#         else:
#             result= self.getMinVal(gameState, agentIndex, depth,alpha,beta)

#         return result

# class ExpectimaxAgent(MultiAgentSearchAgent):
#     """
#       Your expectimax agent (question 4)
#     """

#     def getAction(self, gameState):
#         """
#           Returns the expectimax action using self.depth and self.evaluationFunction

#           All ghosts should be modeled as choosing uniformly at random from their
#           legal moves.
#         """
#         "*** YOUR CODE HERE ***"
#         actions=gameState.getLegalActions(0)
#         bestVal,bestAction=-100000.0,actions[0]
#         for action in actions:
#             nextState=gameState.generateSuccessor(0, action)
#             nextVal=self.getVal(nextState, 1, 0)
#             if nextVal>bestVal:
#                 bestVal=nextVal
#                 bestAction=action
#         return bestAction

#     def getVal(self, gameState, agentIndex, depth,alpha=None,beta=None):
#         if depth==self.depth or gameState.isWin() or gameState.isLose():
#             # print("evaluating state at agent {} depth {} : {}".format(agentIndex, depth, evaluate(gameState)))
#             return self.evaluate(gameState)
#         if agentIndex%gameState.getNumAgents()==0: #is pacman
#             result=self.getMaxVal(gameState, agentIndex, depth)
#         else:
#             result= self.getAverageVal(gameState, agentIndex, depth)
#         return result

# def betterEvaluationFunction(currentGameState):
#     """
#       Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
#       evaluation function (question 5).

#       DESCRIPTION: <write something here so we know what you did>
#     """
#     "*** YOUR CODE HERE ***"
#     # if currentGameState.isWin():
#     #     return INFINITY
#     # if currentGameState.isLose():
#     #     return -INFINITY
#     # newPos = currentGameState.getPacmanPosition()
#     # newFood = currentGameState.getFood()
#     # newGhostStates = currentGameState.getGhostStates()
#     # newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]
#     # numFood=0
#     # closestGhost=INFINITY
#     # closestFood=INFINITY
#     # for pos in newFood.asList():
#     #     if newFood[pos[0]][pos[1]]==True:
#     #         numFood+=1
#     #         closestFood=min(manhattanDistance(pos,newPos),closestFood)
#     # for g in range(len(newGhostStates)):
#     #   closestGhost=min(manhattanDistance(newGhostStates[g].getPosition(),newPos),closestGhost)
#     #   if newScaredTimes[g]==0:
#     #     if manhattanDistance(newGhostStates[g].getPosition(),newPos)<=0:
#     #         return -INFINITY
#     # if numFood==0:
#     #     return INFINITY
#     # score=currentGameState.getScore()
#     # foodFactor=-(numFood+1)-2*closestFood
#     # print(currentGameState.getCapsules())
#     # pelletFactor=-20*len(currentGameState.getCapsules())
#     # # print(score,foodFactor)
#     # return score+foodFactor+pelletFactor

#     PacPos = currentGameState.getPacmanPosition()
#     ghostStates = currentGameState.getGhostStates()
#     Caps = len(currentGameState.getCapsules())
#     foodPos = currentGameState.getFood().asList()
#     foodDist = [manhattanDistance(food,PacPos) for food in foodPos]
#     if(foodDist==[]):
#       foodDist=[0]
#     huntGhost = []
#     for ghost in ghostStates:
#       ghostDist = manhattanDistance(ghost.getPosition(),PacPos)
#       if(ghost.scaredTimer>0 and ghostDist==0):
#         huntGhost.append(1000)
#       elif (ghost.scaredTimer > ghostDist):
#         huntGhost.append(100.0/(ghostDist+1)*1.0)
#       else:
#         huntGhost.append(0)
#     return currentGameState.getScore() - min(foodDist) + max(huntGhost) - 10000*Caps

# # Abbreviation
# better = betterEvaluationFunction

# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


from util import manhattanDistance
from game import Directions
import random, util

from game import Agent

class ReflexAgent(Agent):
    """
      A reflex agent chooses an action at each choice point by examining
      its alternatives via a state evaluation function.

      The code below is provided as a guide.  You are welcome to change
      it in any way you see fit, so long as you don't touch our method
      headers.
    """


    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {North, South, West, East, Stop}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

        "*** YOUR CODE HERE ***"
        if action == 'Stop':
          return -9999
        ghostAlarm = [manhattanDistance(ghost.getPosition(),newPos)<=0 for ghost in newGhostStates if ghost.scaredTimer <= 0]
        if True in ghostAlarm :
          return -999999
        foodList = currentGameState.getFood().asList()
        return currentGameState.getScore() - min([manhattanDistance(food,newPos) for food in foodList]) - len(foodList)*10

def scoreEvaluationFunction(currentGameState):
    """
      This default evaluation function just returns the score of the state.
      The score is the same one displayed in the Pacman GUI.

      This evaluation function is meant for use with adversarial search agents
      (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
      This class provides some common elements to all of your
      multi-agent searchers.  Any methods defined here will be available
      to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

      You *do not* need to make any changes here, but you can if you want to
      add functionality to all your adversarial search agents.  Please do not
      remove anything, however.

      Note: this is an abstract class: one that should not be instantiated.  It's
      only partially specified, and designed to be extended.  Agent (game.py)
      is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
      Your minimax agent (question 2)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action from the current gameState using self.depth
          and self.evaluationFunction.

          Here are some method calls that might be useful when implementing minimax.

          gameState.getLegalActions(agentIndex):
            Returns a list of legal actions for an agent
            agentIndex=0 means Pacman, ghosts are >= 1

          gameState.generateSuccessor(agentIndex, action):
            Returns the successor game state after an agent takes an action

          gameState.getNumAgents():
            Returns the total number of agents in the game

          gameState.isWin():
            Returns whether or not the game state is a winning state

          gameState.isLose():
            Returns whether or not the game state is a losing state
        """
        "*** YOUR CODE HERE ***"
        n = gameState.getNumAgents()
        def minimaxTree(gameState,agentIndex,depth):
          if gameState.isWin() or gameState.isLose() or depth==self.depth:
            return self.evaluationFunction(gameState)
          scores = []
          actions = gameState.getLegalActions(agentIndex)
          depth += 1 if agentIndex==n-1 else 0
          for action in actions:
            score = (action,minimaxTree(gameState.generateSuccessor(agentIndex,action),(agentIndex+1)%n,depth))
            if type(score[1]) is tuple:
              score = (score[0],score[1][1])
            scores.append(score)
          if agentIndex==0:
            return max(scores,key=lambda x:x[1])
          return min(scores,key=lambda x:x[1])
        return minimaxTree(gameState,0,0)[0]



class AlphaBetaAgent(MultiAgentSearchAgent):
    """
      Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"
        n = gameState.getNumAgents()
        def minimaxTree(gameState,agentIndex,depth,ab):
          a,b=ab
          if gameState.isWin() or gameState.isLose() or depth==self.depth:
            return self.evaluationFunction(gameState)
          scores = []
          actions = gameState.getLegalActions(agentIndex)
          depth += 1 if agentIndex==n-1 else 0
          for action in actions:
            score = (action,minimaxTree(gameState.generateSuccessor(agentIndex,action),(agentIndex+1)%n,depth,(a,b)))
            if type(score[1]) is tuple:
              score = (score[0],score[1][1])            
            scores.append(score)
            if agentIndex==0:
              if score[1] > b:
                break
              a = max(a,score[1])
            else:
              if score[1] < a:
                break
              b = min(b,score[1])
          if agentIndex==0:
            return max(scores,key=lambda x:x[1])
          return min(scores,key=lambda x:x[1])
        return minimaxTree(gameState,0,0,(-float("inf"),float("inf")))[0]

class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState):
        """
          Returns the expectimax action using self.depth and self.evaluationFunction

          All ghosts should be modeled as choosing uniformly at random from their
          legal moves.
        """
        "*** YOUR CODE HERE ***"
        n = gameState.getNumAgents()
        def minimaxTree(gameState,agentIndex,depth):
          if gameState.isWin() or gameState.isLose() or depth==self.depth:
            return self.evaluationFunction(gameState)
          scores = []
          actions = gameState.getLegalActions(agentIndex)
          if "Stop" in actions:
            actions.remove("Stop")
          depth += 1 if agentIndex==n-1 else 0
          for action in actions:
            score = (action,minimaxTree(gameState.generateSuccessor(agentIndex,action),(agentIndex+1)%n,depth))
            if type(score[1]) is tuple:
              score = (score[0],score[1][1])
            scores.append(score)
          l = len(scores)*1.0
          if agentIndex==0:
            return max(scores,key=lambda x:x[1])
          return sum([1.0/len(scores)* movement[1] for movement in scores])
        return minimaxTree(gameState,0,0)[0]

def betterEvaluationFunction(currentGameState):
    """
      Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
      evaluation function (question 5).

      DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"  
    PacPos = currentGameState.getPacmanPosition()
    ghostStates = currentGameState.getGhostStates()
    Caps = len(currentGameState.getCapsules())
    foodPos = currentGameState.getFood().asList()
    foodDist = [manhattanDistance(food,PacPos) for food in foodPos]
    if(foodDist==[]):
      foodDist=[0]
    huntGhost = []
    for ghost in ghostStates:
      ghostDist = manhattanDistance(ghost.getPosition(),PacPos)
      if(ghost.scaredTimer>0 and ghostDist==0):
        huntGhost.append(1000)
      elif (ghost.scaredTimer > ghostDist):
        huntGhost.append(100.0/(ghostDist+1)*1.0)
      else:
        huntGhost.append(0)
    return currentGameState.getScore() - min(foodDist) + max(huntGhost) - 10000*Caps
# Abbreviation
better = betterEvaluationFunction

