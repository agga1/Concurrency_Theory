defmodule TW5 do
  @moduledoc """
  Documentation for `TW5`.
  """
  @doc """
  main
  """
  def main() do
    [alphabet, wordWithName , actions] = IOfunc.parseInput "actions.txt"
    [wordName, word] = wordWithName
    dSet = getDependenceSet(actions)
    IOfunc.displaySet(dSet, "D")
    indSet =getIndependenceSet(dSet, alphabet)
    IOfunc.displaySet(indSet, "I")
    foataClasses = getFoataFromSet(dSet, word)
    IOfunc.displayFoata(foataClasses, wordName)
    letterIds = markLetters(word, 1)
    edges = createGraph(dSet, letterIds)
    foatafromGraph = getFoataFromGraph(edges, letterIds)
    IOfunc.displayFoata(foatafromGraph, wordName)
    IOfunc.saveAsDotFile(edges, letterIds)
#     generate png representation of a graph using standard Graphviz library
    System.cmd("dot", [ "-Tpng", "haiku.dot", "-O"])
  end

  @doc """
  finding dependence set - creating a list of all (sorted) pairs, and converting created list
  to MapSet - for O(1) complexity of membership check
  """
  def getDependenceSet(actions) do
    getDependenceSetLoop(actions, []) |> MapSet.new()
  end

  def getDependenceSetLoop([], acc) do acc end
  def getDependenceSetLoop([action | actions], acc) do
    [name, _modified, _refs] = action
    new_deps = actions
               |> Enum.filter(fn(a) -> dependent?(a, action) end )
               |> Enum.flat_map(fn([tmpName, _modified, _refs]) -> [{name, tmpName}, {tmpName, name}] end)
    getDependenceSetLoop(actions, acc ++ [{name, name} | new_deps])
  end

  # check if 2 actions are dependent
  def dependent?([_name, modified, refs], [_name2, modified2, refs2]) do
    MapSet.member?(refs, modified2) or MapSet.member?(refs2, modified)
    or modified == modified2
  end

  def getIndependenceSet(dSet, alphabet) do
    squaredSet = getSquareSet(alphabet, []) |> MapSet.new()
    MapSet.difference(squaredSet, dSet)
  end

  def getSquareSet([], acc) do acc end
  def getSquareSet([el | tail], acc) do
    pairs = tail
            |> Enum.flat_map(fn(e) -> [{el, e}, {e, el}] end)
    getSquareSet(tail, acc ++ [{el, el}|pairs])
  end

  def getFoataFromSet(dSet, word) do
    getLetterClassnrPairs(dSet, word, []) |> toFnF()
  end
  
  def getLetterClassnrPairs(_, [], processed_actions) do processed_actions end
  def getLetterClassnrPairs(deps, [letter | letters], processed_actions) do
    highestNr = processed_actions
          |> Enum.filter(fn({tmpLetter, _nr}) -> isDependent(deps, tmpLetter, letter) end)
          |> Enum.map(fn({_tmpLetter, nr})-> nr end)
          |> Enum.max(&>=/2, fn -> 0 end)
    getLetterClassnrPairs(deps, letters, [{letter, highestNr+1} | processed_actions])
  end

  # check if two actions are dependent
  def isDependent(deps, action1, action2) do
    MapSet.member?(deps, {action1, action2})
  end

  # change representation: e.g. from pairs (a, 2) (c, 1) (b, 2) to [[bc][a]]
  def toFnF(letter_class) do
    classN = letter_class |> Enum.reduce(0, fn {_letter, nr}, acc -> max(nr, acc) end)
    1..classN |> Enum.map(fn(nr) ->
      letter_class |> Enum.filter(fn {_letter, tmpNr} -> tmpNr==nr end) |> Enum.map(&elem(&1, 0))
      end)
  end

  # assign id to each letter to distinguish between productions (word aabc -> {a,1}, {a,2} {b,3} {c,4})
  def markLetters([], _) do [] end
  def markLetters([letter|letters], nr) do
    [{letter, nr}| markLetters(letters, nr+1)]
  end

  @doc """
  --GRAPH CREATION--
  Create Diekert's graph from dependency set 'deps' and word as returned from 'markLetters'
  returns graph as set of edges
  """
  def createGraph(deps, letterIds) do
    findAllEdges(deps, letterIds, [])
      |> Enum.sort( fn({f1, t1}, {f2, t2})-> # sort edges for better complexity of removing redundant edges
        if t1 == t2 do f1>=f2 else t1<=t2 end end )
      |> removeRedundant([])
  end

  # find all edges of dependency graph, analyzing letter by letter
  def findAllEdges(_, [], edges) do edges end
  def findAllEdges(deps, [letterId | letterIds], edges) do
    {letter, id} = letterId
    newEdges = letterIds
               |> Enum.filter(fn{tLetter, _} -> isDependent(deps, tLetter, letter) end)
               |> Enum.map(fn{_, tId} -> {id, tId} end)
    findAllEdges(deps, letterIds, edges ++ newEdges)
  end

  # returns a set of edges, where all edges are irreplacable by a subset of any other edges
  def removeRedundant([], accepted) do accepted end
  def removeRedundant([edge | edges], accepted) do
    if isReplacable?(accepted, edge) do
      removeRedundant(edges, accepted) # reject edge
    else
      removeRedundant(edges, [edge | accepted]) # accept edge
    end
  end
  # check recursively all combinations of already accepted edges, if they can replace current edge
  def isReplacable?(accepted, {from, to}) do
      Enum.any?(accepted, &(&1 == {from, to})) or
      accepted |> Enum.filter(fn{_, t} -> t == to end)
               |> Enum.any?(fn{f, _} -> isReplacable?(accepted, {from, f}) end)
  end

  @doc """
  --FOATA CLASSES FROM GRAPH--
  In each step take all vertices (represented as tuple {letter, id} ) that have no incoming edges,
  and mark them as new class.
  """
  def getFoataFromGraph(_edges, []) do [] end
  def getFoataFromGraph(edges, lettersIds) do
    # split vertices into 2 groups depending on whether they have any input edges
    division = lettersIds |> Enum.group_by(fn{_letter, id} -> hasInputs?(edges, id) end)
    newClass= Map.get(division, false, [])
    remaingingIds = Map.get(division, true, [])
    newEdges =  deleteEdgesFromVertices(newClass, edges)
    [ newClass |> Enum.map(&elem(&1, 0)) |  getFoataFromGraph(newEdges, remaingingIds)]
  end

  # check whether vertex with id 'id' has any incoming edges
  def hasInputs?(edges, id) do
    edges |> Enum.any?(fn{_, to} -> to == id end)
  end

  # delete all edges from vertices that are already processed
  def deleteEdgesFromVertices([], edges) do edges end
  def deleteEdgesFromVertices([{_letter, id}| vertices], edges) do
    deleteEdgesFromVertices(vertices, edges |> Enum.filter(fn{from, _}-> from != id end))
  end

end
