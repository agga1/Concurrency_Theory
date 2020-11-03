defmodule TW5 do
  @moduledoc """
  Documentation for `TW5`.
  """

  @doc """
  Hello world.
  """
  def main() do
    [alphabet, word , actions] = parseInput "actions.txt"
    dSet = getDependenceSet(actions)
#    IO.puts("#{dSet}")
    indSet =getIndependenceSet(dSet, alphabet)
#    IO.puts("#{indSet}")
    foataClasses = foata(dSet, word)
#    IO.puts(foataClasses)
    letterIds = markLetters(word, 1)
    graph = createGraph(dSet, letterIds)
    foatafromGraph = getFoataFromGraph(graph, letterIds)
#    graphViz = toGraphvizGraph(graph, letterIds)
  end

  def parseInput(path) do
    {:ok, contents} = File.read(path)
    [alph_str, word_str | actions_str] = String.split(contents, "\r\n")

    alphabet = alph_str   |> parseAlphabet
    word =    word_str    |> String.split("", trim: true)
    actions = actions_str |> Enum.map(&parseAction/1)

    [alphabet, word , actions]
  end

  @doc """
  """
  def parseAlphabet(line) do
    line
      |> String.split("")
      |> Enum.filter(fn(x) ->
      String.match?(x, ~r/^[[:alnum:]]+$/)
              end)
  end

  def parseAction(line) do
    [l, r] = String.split(line, "=")
    [name, modified] = String.split(l, " ", trim: true)
    refs = MapSet.new(r
        |> String.split("", trim: true)
        |> Enum.filter(fn(x) -> String.match?(x, ~r/^[[:alpha:]]+$/) end) # filter non-letters
            )
#            IO.puts(MapSet.to_list(refs))
    [name, modified, refs]
  end



  def getDependenceSet(actions) do
    getDependenceSetLoop(actions, [])
    |> MapSet.new() # convert created list to MapSet for O(1) check if set contains x
  end

  def getDependenceSetLoop([], acc) do acc end
  def getDependenceSetLoop([action | actions], acc) do
    [name, _mod, _refs] = action
    new_deps = actions
               |> Enum.filter(fn(a) -> dependent(a, action) end )
               |> Enum.flat_map(fn([tmpName, _mod, _refs]) -> [{name, tmpName}, {tmpName, name}] end)
    getDependenceSetLoop(actions, acc ++ [{name, name} | new_deps])
  end

  def dependent([_name, modified, refs], [_name2, modified2, refs2]) do
    MapSet.member?(refs, modified2) or MapSet.member?(refs2, modified)
    or modified == modified2
  end

  def getIndependenceSet(dSet, alphabet) do
    squaredSet = getSquareSet(alphabet, []) |> MapSet.new()
    #    IO.puts("#{squaredSet}")
    MapSet.difference(squaredSet, dSet)
  end

  def getSquareSet([], acc) do acc end
  def getSquareSet([el | tail], acc) do
    pairs = tail
            |> Enum.flat_map(fn(e) -> [{el, e}, {e, el}] end)
    getSquareSet(tail, acc ++ [{el, el}|pairs])
  end

  def foata(dSet, word) do
    letter_class = getLetterClassnrPairs(dSet, word, [])
    toFnF(letter_class)
  end

  def isDependent(deps, action1, action2) do
    MapSet.member?(deps, {action1, action2})
  end
  
  def getLetterClassnrPairs(_, [], processed_actions) do
    processed_actions
  end
  
  def getLetterClassnrPairs(deps, [letter | letters], processed_actions) do
    # find min number from processed_actions filtered by dependency with letter
    # recurse for letter and updated processed actions
    highestNr = processed_actions
          |> Enum.filter(fn({tmpLetter, _nr}) -> isDependent(deps, tmpLetter, letter) end)
          |> Enum.map(fn({_tmpLetter, nr})-> nr end)
          |> Enum.max(&>=/2, fn -> 0 end)
    getLetterClassnrPairs(deps, letters, [{letter, highestNr+1} | processed_actions])
  end

  def toFnF(letter_class) do
    classN = letter_class |> Enum.reduce(0, fn {_letter, nr}, acc -> max(nr, acc) end)
    1..classN |> Enum.map(fn(nr) ->
      letter_class |> Enum.filter(fn {_letter, tmpNr} -> tmpNr==nr end) |> Enum.map(&elem(&1, 0))
      end)
  end

  # assign id to each letter to distinguish between productions
  def markLetters([], _) do [] end
  def markLetters([letter|letters], nr) do
    [{letter, nr}| markLetters(letters, nr+1)]
  end

  def createGraph(deps, letterIds) do
    allEdgesSet = findAllEdges(deps, letterIds, []) |> MapSet.new()
    n = length(letterIds)
    redundant = findAllRedundant(allEdgesSet, [], 0, n, 0) |> MapSet.new()
    edgeSet = MapSet.difference(allEdgesSet, redundant)
    edgeSet
  end

  def findAllEdges(_, [], edges) do edges end
  def findAllEdges(deps, [letterId | letterIds], edges) do
    # find all connections with future letters
    {letter, id} = letterId
    newEdges = letterIds
               |> Enum.filter(fn{tLetter, _} -> isDependent(deps, tLetter, letter) end)
               |> Enum.map(fn{_, tId} -> {id, tId} end)
    findAllEdges(deps, letterIds, edges ++ newEdges)
  end

  def findAllRedundant(edges, prefix, n, n, ones) do
    if ones>2 do
      path = binaryToPath(prefix, n)
      if pathExists?(edges, path) do
        [{List.first(path), List.last(path)}] else [] end
    else [] end
  end
  def findAllRedundant(edges, prefix, len, n, ones) do
    findAllRedundant(edges, [0|prefix], len+1, n, ones) ++ findAllRedundant(edges, [1|prefix], len+1, n, ones+1)
  end
  def binaryToPath(binary, n)do
    List.zip([binary, 1..n|> Enum.to_list()])
    |> Enum.filter(fn {bin, _id} -> bin==1 end)
    |> Enum.map(fn {_bin, id} -> id end)
  end
  def pathExists?(edges, []) do true end
  def pathExists?(edges, [nr]) do true end
  def pathExists?(edges, [nr1, nr2| rest]) do
    MapSet.member?(edges, {nr1, nr2}) and pathExists?(edges, [nr2|rest])
  end

  def getFoataFromGraph(edges, []) do [] end
  def getFoataFromGraph(edges, labelsIds) do
    division = labelsIds |> Enum.group_by(fn{letter, id} -> hasInputs(edges, id) end)
    newClass= Map.get(division, false, [])
    remaingingIds = Map.get(division, true, [])
    newEdges =  deleteEdgesFromVertex(newClass, edges)
    [ newClass |> Enum.map(&elem(&1, 0)) |  getFoataFromGraph(newEdges, remaingingIds)]
  end

  def hasInputs(edges, id) do
    1..(id-1) |> Enum.any?(&MapSet.member?(edges, {&1, id}))
  end

  def deleteEdgesFromVertex([], edges) do edges end
  def deleteEdgesFromVertex([{_letter, id}| vertices], edges) do
    deleteEdgesFromVertex(vertices, edges |> Enum.filter(fn{from, to}-> from != id end) |> MapSet.new())
  end

  # --------------------------------------
  def toGraphvizGraph(edges, letterIds) do
    edgesStr = edges |> Enum.map(fn {from, to} -> "#{from} -> #{to}" end) |> Enum.join("\n")
    labelsStr = letterIds |> Enum.map(fn {label, id} -> "#{id}[label=#{label}]" end) |> Enum.join("\n")
    str = ["digraph g{", edgesStr, labelsStr, "}"] |> Enum.join("\n")
    File.write("haiku.txt", str)
  end

end
