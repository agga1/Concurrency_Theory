defmodule TW5 do
  @moduledoc """
  Documentation for `TW5`.
  """
  @doc """
  główna funkcja wywołująca po kolei funkcje generujące kolejne części rozwiązania.
  Forma wejścia:
  alfabet w formie 'A = { a, b, ... }'
  slowo w formie 'w = abc...'
  w kolejnych linijkach kolejne transakcje w formie '(a) x = x + 1 '
  !! bez nowej linii na końcu pliku !!
  Przykladowe poprawne wejscie w pliku example.txt
  """
  def main() do
    # konwertowanie wejścia do odpowiedniej reprezentacji: patrz moduł IOfunc
    [alphabet, word, actions] = IOfunc.parseInput "example.txt"
    dSet = getDependenceSet(actions)
    IOfunc.displaySet(dSet, "D")
    indSet =getIndependenceSet(dSet, alphabet)
    IOfunc.displaySet(indSet, "I")
    foataClasses = getFoataFromSet(dSet, word)
    IOfunc.displayFoata(foataClasses)
    letterIds = markLetters(word, 1)
    edges = createGraph(dSet, letterIds)
    foatafromGraph = getFoataFromGraph(edges, letterIds)
    IOfunc.displayFoata(foatafromGraph)

    IOfunc.saveAsDotFile(edges, letterIds)
#     generate png representation of a graph using standard Graphviz library (dot must be added to path)
    System.cmd("dot", [ "-Tpng", "graph.dot", "-O"])
  end

  @doc """
  wyznaczanie relacji zależności - dostając listę transakcji, rekurencyjnie znajduje
  wszystkie pary (a,b), takie że a i b są zależne. Wynik zwracam jako zbiór, dzieki czemu
  sprawdzenie czy zbiór zawiera daną parę ma złożoność O(1).
  Np. dla listy transakcji: [[a, x, {x,y}], [b, y, {y}], [c, z, {}]]
  dostaniemy: Mapset([{a,a}, {a,b}, {b,a}, {b,b}, {c,c}])

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
    getDependenceSetLoop(actions, acc ++ [{name, name} | new_deps]) # do zgromadzonych zależności dodajemy zależność od samego siebie
  end

  # funckja pomocnicza sprawdzająca czy 2 transakcje są od siebie zależne
  def dependent?([_name, modified, refs], [_name2, modified2, refs2]) do
    MapSet.member?(refs, modified2) or MapSet.member?(refs2, modified)
    or modified == modified2
  end

  @doc """
  wyznaczanie relacji niezależności - dostając relację zależności oraz alfabet,
  znajduje relacje niezależnośći jako różnicę iloczynu kartezjańskiego AxA ze zbiorem
  D (relacją zależności)
  """
  def getIndependenceSet(dSet, alphabet) do
    squaredSet = getSquareSet(alphabet, []) |> MapSet.new()
    MapSet.difference(squaredSet, dSet)
  end

  # funkcja pomocnicza - znajduje iloczyn kartezjański zbioru
  def getSquareSet([], acc) do acc end
  def getSquareSet([el | tail], acc) do
    pairs = tail
            |> Enum.flat_map(fn(e) -> [{el, e}, {e, el}] end)
    getSquareSet(tail, acc ++ [{el, el}|pairs])
  end

  @doc """
  wyznaczanie postaci normalnej Foaty na podstawie relacji zależności i otrzymanego słowa
  np.
  deps: MapSet([{a,a}, {a,b}, {b,b}, {c,c}])
  word: [a,c,b,a]
  wynik: [[ac],[b],[a]]
  """
  def getFoataFromSet(deps, word) do
    getLetterClassnrPairs(deps, word, []) |> toFnF()
  end

  # oblicza wynik przejściowy dla @getFoataFromSet. Każdej akcji przypisuje klasę Foaty
  # np. deps: MapSet([{a,a}, {a,b}, {b,b}, {c,c}])
  # słowo: [a,c,b,a]
  # wynik: [{a, 1}, {c, 1}, {b, 2}, {a,3}]
  def getLetterClassnrPairs(_, [], processed_actions) do processed_actions end
  def getLetterClassnrPairs(deps, [letter | letters], processed_actions) do
    highestNr = processed_actions
          |> Enum.filter(fn({tmpLetter, _nr}) -> isDependent?(deps, tmpLetter, letter) end)
          |> Enum.map(fn({_tmpLetter, nr})-> nr end)
          |> Enum.max(&>=/2, fn -> 0 end)
    getLetterClassnrPairs(deps, letters, [{letter, highestNr+1} | processed_actions])
  end

  # sprawdza czy dwie transakcje są zależne
  def isDependent?(deps, action1, action2) do
    MapSet.member?(deps, {action1, action2})
  end

  # zmien reprezentacje klas Foaty: z [{a, 1}, {c, 1}, {b, 2}, {a,3}] na [[ac],[b],[a]]
  def toFnF(letter_class) do
    classN = letter_class |> Enum.reduce(0, fn {_letter, nr}, acc -> max(nr, acc) end)
    1..classN |> Enum.map(fn(nr) ->
      letter_class |> Enum.filter(fn {_letter, tmpNr} -> tmpNr==nr end) |> Enum.map(&elem(&1, 0))
      end)
  end

  # mapowanie kolejnych akcji na liczby naturalne;
  # każdej akcji w slowie przypisz numer porządkowy (word: [a,c,b,a] -> [{a,1}, {c,2} {b,3} {a,4}])
  def markLetters([], _) do [] end
  def markLetters([letter|letters], nr) do
    [{letter, nr}| markLetters(letters, nr+1)]
  end

  @doc """
  --TWORZENIE GRAFU--
  Tworzenie grafu Diekerta jako listy krawędzi, na podstawie relacji zależności oraz słowa (w postaci
  zwroconej przez @markLetters).
  Np.:
  deps: MapSet([{a,a}, {a,b}, {b,b}, {c,c}])
  letterIds:  [{a,1}, {c,2} {b,3} {a,4}]
  wynik: [{1,3}, {1,4}, {3,4}]
  """
  def createGraph(deps, letterIds) do
    findAllEdges(deps, letterIds, [])
      |> Enum.sort( fn({f1, t1}, {f2, t2})-> # sort edges for better complexity of removing redundant edges
        if t1 == t2 do f1>=f2 else t1<=t2 end end )
      |> removeRedundant([])
  end

  # znajduje rekurencyjnie wszystkie krawędzie w grafie
  def findAllEdges(_, [], edges) do edges end
  def findAllEdges(deps, [letterId | letterIds], edges) do
    {letter, id} = letterId
    newEdges = letterIds
               |> Enum.filter(fn{tLetter, _} -> isDependent?(deps, tLetter, letter) end)
               |> Enum.map(fn{_, tId} -> {id, tId} end)
    findAllEdges(deps, letterIds, edges ++ newEdges)
  end

  # z listy krawędzi grafu usuwa te, które mogą być zastąpione przez pewną kombinację pozostałych krawędzi
  # zwraca accepted - listę krawędzi niezastępowalnych
  def removeRedundant([], accepted) do accepted end
  def removeRedundant([edge | edges], accepted) do
    if isReplacable?(accepted, edge) do
      removeRedundant(edges, accepted) # reject edge
    else
      removeRedundant(edges, [edge | accepted]) # accept edge
    end
  end
  # sprawdz rekurencyjnie wszystkie już zaakceptowane krawędzie, czy są w stanie zastąpić obecnie przeglądaną krawędź
  def isReplacable?(accepted, {from, to}) do
      Enum.any?(accepted, &(&1 == {from, to})) or
      accepted |> Enum.filter(fn{_, t} -> t == to end)
               |> Enum.any?(fn{f, _} -> isReplacable?(accepted, {from, f}) end)
  end

  @doc """
  --POSTAĆ NORMALNA FOATY NA PODSTAWIE GRAFU --
  w każdym etapie znajduje wszystkie wierzchołki, do których nie wchodzi żadna krawędź.
  Wierzchołki znalezione w i-tym etapie tworzą i-tą klasę Foaty.
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

  # sprawdź czy do wierzchołka o danym numerze wchodzą jakieś krawędzie
  def hasInputs?(edges, id) do
    edges |> Enum.any?(fn{_, to} -> to == id end)
  end

  # usuń z grafu wierzchołki, które zostały już przeanalizowane (czyli wszystkie krawędzie, które z nich prowadziły)
  def deleteEdgesFromVertices([], edges) do edges end
  def deleteEdgesFromVertices([{_letter, id}| vertices], edges) do
    deleteEdgesFromVertices(vertices, edges |> Enum.filter(fn{from, _}-> from != id end))
  end

end
