# valueIterationAgents.py
# -----------------------
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


# valueIterationAgents.py
# -----------------------
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

INFINITY=100000000.0
import mdp, util

from learningAgents import ValueEstimationAgent
import collections

class ValueIterationAgent(ValueEstimationAgent):
    """
        * Please read learningAgents.py before reading this.*

        A ValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs value iteration
        for a given number of iterations using the supplied
        discount factor.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 100):
        """
          Your value iteration agent should take an mdp on
          construction, run the indicated number of iterations
          and then act according to the resulting policy.

          Some useful mdp methods you will use:
              mdp.getStates()
              mdp.getPossibleActions(state)
              mdp.getTransitionStatesAndProbs(state, action)
              mdp.getReward(state, action, nextState)
              mdp.isTerminal(state)
        """
        self.mdp = mdp
        self.discount = discount
        self.iterations = iterations
        self.values = util.Counter() # A Counter is a dict with default 0
        self.runValueIteration()

    def runValueIteration(self):
        # Write value iteration code here
        "*** YOUR CODE HERE ***"
        k=0
        while k<self.iterations:
            k+=1
            values_k=dict()
            for state in self.mdp.getStates():
                Q_max=-INFINITY
                for action in self.mdp.getPossibleActions(state):
                    Q_s_a=self.computeQValueFromValues(state, action)
                    if Q_s_a>Q_max:
                        Q_max=Q_s_a
                if self.mdp.isTerminal(state):
                    Q_max=0
                values_k[state]=Q_max
            for state in values_k:
                self.values[state]=values_k[state]


    def getValue(self, state):
        """
          Return the value of the state (computed in __init__).
        """
        return self.values[state]


    def computeQValueFromValues(self, state, action):
        """
          Compute the Q-value of action in state from the
          value function stored in self.values.
        """
        "*** YOUR CODE HERE ***"
        transitionStatesAndProbs=self.mdp.getTransitionStatesAndProbs(state, action)
        toReturn= sum([prob*(self.mdp.getReward(state, action, nextState)+self.discount*self.values[nextState]) for nextState,prob in transitionStatesAndProbs])
        # print("state: ", state, "action: ", action, toReturn)
        return toReturn

    def computeActionFromValues(self, state):
        """
          The policy is the best action in the given state
          according to the values currently stored in self.values.

          You may break ties any way you see fit.  Note that if
          there are no legal actions, which is the case at the
          terminal state, you should return None.
        """
        "*** YOUR CODE HERE ***"
        bestAction=None
        maxVal=-INFINITY
        for action in self.mdp.getPossibleActions(state):
            newVal=self.computeQValueFromValues(state, action)
            if newVal>maxVal:
                bestAction=action
                maxVal=newVal
        return bestAction

    def getPolicy(self, state):
        return self.computeActionFromValues(state)

    def getAction(self, state):
        "Returns the policy at the state (no exploration)."
        return self.computeActionFromValues(state)

    def getQValue(self, state, action):
        return self.computeQValueFromValues(state, action)

class AsynchronousValueIterationAgent(ValueIterationAgent):
    """
        * Please read learningAgents.py before reading this.*

        An AsynchronousValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs cyclic value iteration
        for a given number of iterations using the supplied
        discount factor.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 1000):
        """
          Your cyclic value iteration agent should take an mdp on
          construction, run the indicated number of iterations,
          and then act according to the resulting policy. Each iteration
          updates the value of only one state, which cycles through
          the states list. If the chosen state is terminal, nothing
          happens in that iteration.

          Some useful mdp methods you will use:
              mdp.getStates()
              mdp.getPossibleActions(state)
              mdp.getTransitionStatesAndProbs(state, action)
              mdp.getReward(state)
              mdp.isTerminal(state)
        """
        ValueIterationAgent.__init__(self, mdp, discount, iterations)

    def runValueIteration(self):
        "*** YOUR CODE HERE ***"
        countStates=len(self.mdp.getStates())
        k=0
        while k<self.iterations:
            state=self.mdp.getStates()[k%countStates]
            Q_max=-INFINITY
            for action in self.mdp.getPossibleActions(state):
                Q_s_a=self.computeQValueFromValues(state, action)
                if Q_s_a>Q_max:
                    Q_max=Q_s_a
            if self.mdp.isTerminal(state):
                Q_max=0
            self.values[state]=Q_max
            k+=1

class PrioritizedSweepingValueIterationAgent(AsynchronousValueIterationAgent):
    """
        * Please read learningAgents.py before reading this.*

        A PrioritizedSweepingValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs prioritized sweeping value iteration
        for a given number of iterations using the supplied parameters.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 100, theta = 1e-5):
        """
          Your prioritized sweeping value iteration agent should take an mdp on
          construction, run the indicated number of iterations,
          and then act according to the resulting policy.
        """
        self.theta = theta
        self.predecessors = dict()
        ValueIterationAgent.__init__(self, mdp, discount, iterations)

    def runValueIteration(self):
        "*** YOUR CODE HERE ***"
        # Setting predessors for all states
        self.setPredecessors()
        queue=util.PriorityQueue()
        # pushing states to queue based on their priority
        for state in self.mdp.getStates():
            if self.mdp.isTerminal(state):
                continue
            Q_max=max([self.getQValue(state,action) for action in self.mdp.getPossibleActions(state)])
            diff=abs(Q_max-self.getValue(state))
            queue.update(state,-diff)
        # iterating k times, each time updating state and all predecessors
        k=0
        while k<self.iterations:
            if queue.isEmpty():
                return
            state=queue.pop()
            # update(state)
            if not self.mdp.isTerminal(state):
                Q_max=-INFINITY
                for action in self.mdp.getPossibleActions(state):
                    Q_s_a=self.getQValue(state, action)
                    Q_max=max(Q_s_a,Q_max)
                self.values[state]=Q_max
            # print(self.values[state])
            for pred in self.predecessors[state]:
                Q_max=-INFINITY
                for action in self.mdp.getPossibleActions(pred):
                    Q_max=max(self.getQValue(pred, action),Q_max)
                diff=abs(Q_max-self.values[pred])
                if diff > self.theta:
                    queue.update(pred,-diff)
            k+=1

    def setPredecessors(self):
        for state in self.mdp.getStates():
            for action in self.mdp.getPossibleActions(state):
                transitionStatesAndProbs=self.mdp.getTransitionStatesAndProbs(state, action)
                for nextState, prob in transitionStatesAndProbs:
                    if prob>0:
                        if nextState not in self.predecessors:
                            self.predecessors[nextState]=set()
                        self.predecessors[nextState].add(state)
























